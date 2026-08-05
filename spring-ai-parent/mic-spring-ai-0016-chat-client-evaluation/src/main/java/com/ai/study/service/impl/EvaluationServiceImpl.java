package com.ai.study.service.impl;

import com.ai.study.advisor.AuditAdvisor;
import com.ai.study.advisor.ContentAuditAdvisor;
import com.ai.study.advisor.SensitiveWordAdvisor;
import com.ai.study.enums.EvaluationDimensionEnum;
import com.ai.study.model.ABTestResult;
import com.ai.study.model.EvaluationResult;
import com.ai.study.service.EvaluationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class EvaluationServiceImpl implements EvaluationService {

    @Qualifier("ollamaChatClient")
    private final ChatClient ollamaChatClient;

    @Qualifier("deepSeekChatClient")
    private final ChatClient deepSeekClient;

    @Value("${evaluation.auto-evaluate:true}")
    private boolean autoEvaluate;

    /** 审计日志 Advisor */
    private final AuditAdvisor auditAdvisor;

    /** 内容审核 Advisor */
    private final ContentAuditAdvisor contentAuditAdvisor;

    /** 敏感词过滤 Advisor */
    private final SensitiveWordAdvisor sensitiveWordAdvisor;

    private final Map<String, List<EvaluationResult>> evaluationHistory = new ConcurrentHashMap<>();
    private final AtomicInteger totalEvaluations = new AtomicInteger(0);

    public EvaluationServiceImpl(
            @Qualifier("ollamaChatClient") ChatClient ollamaChatClient,
            @Qualifier("deepSeekChatClient") ChatClient deepSeekClient,
            AuditAdvisor auditAdvisor,
            ContentAuditAdvisor contentAuditAdvisor,
            SensitiveWordAdvisor sensitiveWordAdvisor) {
        this.ollamaChatClient = ollamaChatClient;
        this.deepSeekClient = deepSeekClient;
        this.auditAdvisor = auditAdvisor;
        this.contentAuditAdvisor = contentAuditAdvisor;
        this.sensitiveWordAdvisor = sensitiveWordAdvisor;
    }

    /**
     * 获取带所有 Advisor 的 ChatClient
     */
    private ChatClient getChatClientWithAdvisors(ChatClient client) {
        List<Advisor> advisors = List.of(
                sensitiveWordAdvisor,
                contentAuditAdvisor,
                auditAdvisor
        );
        return client.mutate()
                .defaultAdvisors(advisors)
                .build();
    }

    // ================================================================
    // 1. 自动评估（带 Advisor）
    // ================================================================

    @Override
    public EvaluationResult autoEvaluate(String question, String answer, List<String> dimensions) {
        log.info("📊 自动评估：{}", question);

        if (!autoEvaluate) {
            return EvaluationResult.builder()
                    .question(question)
                    .answer(answer)
                    .comment("自动评估未启用")
                    .evaluationType("auto")
                    .evaluationTime(LocalDateTime.now())
                    .build();
        }

        String dimensionList = dimensions != null && !dimensions.isEmpty()
                ? String.join("、", dimensions)
                : "准确性、完整性、相关性、流畅性、有用性";

        String evalPrompt = String.format("""
                请对以下 AI 回答进行评估。
                
                问题：%s
                回答：%s
                
                评估维度：%s
                
                请按以下格式返回：
                1. 各维度评分（1-10分）：维度名=分数
                2. 综合评分（1-10分）
                3. 评估意见：...
                
                只返回评估结果，不要其他内容。
                """, question, answer, dimensionList);

        try {
            // ✅ 使用带 Advisor 的客户端
            ChatClient clientWithAdvisors = getChatClientWithAdvisors(ollamaChatClient);

            String evalResponse = clientWithAdvisors.prompt()
                    .system("你是一个专业的回答质量评估专家，请客观、公正地评估 AI 回答的质量。")
                    .user(evalPrompt)
                    .call()
                    .content();

            EvaluationResult result = parseEvaluation(evalResponse);
            result.setQuestion(question);
            result.setAnswer(answer);
            result.setEvaluationType("auto");
            result.setEvaluationTime(LocalDateTime.now());

            saveHistory(result);
            return result;

        } catch (Exception e) {
            log.error("自动评估失败：{}", e.getMessage());
            return EvaluationResult.builder()
                    .question(question)
                    .answer(answer)
                    .comment("评估失败：" + e.getMessage())
                    .evaluationType("auto")
                    .evaluationTime(LocalDateTime.now())
                    .build();
        }
    }

    /**
     * 解析评估结果
     */
    private EvaluationResult parseEvaluation(String response) {
        Map<String, Double> scores = new LinkedHashMap<>();
        Double overallScore = null;
        String comment = "";

        String[] lines = response.split("\n");
        for (String line : lines) {
            line = line.trim();

            if (line.contains("=") || line.contains("：") || line.contains(":")) {
                String[] parts = line.split("[=：:]");
                if (parts.length == 2) {
                    String key = parts[0].trim();
                    try {
                        double value = Double.parseDouble(parts[1].trim().replaceAll("[^0-9.]", ""));
                        if (key.contains("综合") || key.contains("总体")) {
                            overallScore = value;
                        } else {
                            scores.put(key, value);
                        }
                    } catch (NumberFormatException ignored) {
                    }
                }
            }

            if (line.contains("意见") || line.contains("评价") || line.contains("建议")) {
                comment = line.replaceAll("^.*?[：:]", "").trim();
            }
        }

        if (overallScore == null && !scores.isEmpty()) {
            overallScore = scores.values().stream()
                    .mapToDouble(Double::doubleValue)
                    .average()
                    .orElse(0.0);
        }

        return EvaluationResult.builder()
                .scores(scores)
                .overallScore(overallScore)
                .comment(comment)
                .build();
    }

    // ================================================================
    // 2. 模型对比（带 Advisor）- ✅ 两个模型都加 Advisor
    // ================================================================

    @Override
    public Map<String, EvaluationResult> compareModels(String question) {
        log.info("📊 对比模型：{}", question);

        Map<String, EvaluationResult> results = new LinkedHashMap<>();

        // ✅ Ollama：带 Advisor
        ChatClient ollamaWithAdvisors = getChatClientWithAdvisors(ollamaChatClient);

        log.info("   🤖 调用 Ollama...");
        long start1 = System.currentTimeMillis();
        String answer1 = ollamaWithAdvisors.prompt()
                .user(question)
                .call()
                .content();
        long time1 = System.currentTimeMillis() - start1;
        EvaluationResult result1 = autoEvaluate(question, answer1, null);
        result1.setModel("Ollama");
        result1.setResponseTime(time1);
        results.put("Ollama", result1);

        // ✅ DeepSeek：带 Advisor
        ChatClient deepSeekWithAdvisors = getChatClientWithAdvisors(deepSeekClient);

        log.info("   🤖 调用 DeepSeek...");
        long start2 = System.currentTimeMillis();
        String answer2 = deepSeekWithAdvisors.prompt()
                .user(question)
                .call()
                .content();
        long time2 = System.currentTimeMillis() - start2;
        EvaluationResult result2 = autoEvaluate(question, answer2, null);
        result2.setModel("DeepSeek");
        result2.setResponseTime(time2);
        results.put("DeepSeek", result2);

        return results;
    }

    // ================================================================
    // 3. 参数配置对比（带 Advisor）
    // ================================================================

    @Override
    public Map<String, EvaluationResult> compareConfigs(String question, List<Map<String, Object>> configs) {
        log.info("📊 对比配置：{}，配置数量：{}", question, configs.size());

        Map<String, EvaluationResult> results = new LinkedHashMap<>();

        ChatClient clientWithAdvisors = getChatClientWithAdvisors(ollamaChatClient);

        for (int i = 0; i < configs.size(); i++) {
            Map<String, Object> config = configs.get(i);
            String name = (String) config.getOrDefault("name", "配置" + (i + 1));

            log.info("   🔧 测试配置：{}", name);

            OpenAiChatOptions.Builder optionsBuilder = OpenAiChatOptions.builder();

            if (config.containsKey("temperature")) {
                optionsBuilder.temperature((Double) config.get("temperature"));
            }
            if (config.containsKey("maxTokens")) {
                optionsBuilder.maxTokens((Integer) config.get("maxTokens"));
            }
            if (config.containsKey("topP")) {
                optionsBuilder.topP((Double) config.get("topP"));
            }

            long start = System.currentTimeMillis();
            String answer = clientWithAdvisors.prompt()
                    .user(question)
                    .options(optionsBuilder.build())
                    .call()
                    .content();
            long time = System.currentTimeMillis() - start;

            EvaluationResult result = autoEvaluate(question, answer, null);
            result.setModel(name);
            result.setResponseTime(time);
            results.put(name, result);
        }

        return results;
    }

    // ================================================================
    // 4. A/B 测试（带 Advisor）
    // ================================================================

    @Override
    public ABTestResult abTest(String question) {
        log.info("📊 A/B 测试：{}", question);

        ChatClient clientWithAdvisors = getChatClientWithAdvisors(ollamaChatClient);

        // A组：默认配置
        long startA = System.currentTimeMillis();
        String answerA = clientWithAdvisors.prompt()
                .user(question)
                .call()
                .content();
        long timeA = System.currentTimeMillis() - startA;
        EvaluationResult resultA = autoEvaluate(question, answerA, null);
        resultA.setModel("A组-默认配置");
        resultA.setResponseTime(timeA);

        // B组：优化配置
        long startB = System.currentTimeMillis();
        String answerB = clientWithAdvisors.prompt()
                .user(question)
                .options(OpenAiChatOptions.builder()
                        .temperature(1.2)
                        .topP(0.95)
                        .maxTokens(2048)
                        .build())
                .call()
                .content();
        long timeB = System.currentTimeMillis() - startB;
        EvaluationResult resultB = autoEvaluate(question, answerB, null);
        resultB.setModel("B组-优化配置（temperature=1.2）");
        resultB.setResponseTime(timeB);

        ABTestResult.GroupResult groupA = ABTestResult.GroupResult.builder()
                .name("A组")
                .description("默认配置（temperature=0.7）")
                .results(List.of(resultA))
                .avgScore(resultA.getOverallScore())
                .avgResponseTime((double) timeA)
                .sampleCount(1)
                .build();

        ABTestResult.GroupResult groupB = ABTestResult.GroupResult.builder()
                .name("B组")
                .description("优化配置（temperature=1.2）")
                .results(List.of(resultB))
                .avgScore(resultB.getOverallScore())
                .avgResponseTime((double) timeB)
                .sampleCount(1)
                .build();

        String conclusion;
        String recommendation;
        if (resultA.getOverallScore() != null && resultB.getOverallScore() != null) {
            if (resultA.getOverallScore() > resultB.getOverallScore()) {
                conclusion = "A组（默认配置）表现更好";
                recommendation = "推荐使用默认配置";
            } else if (resultA.getOverallScore() < resultB.getOverallScore()) {
                conclusion = "B组（优化配置）表现更好";
                recommendation = "推荐使用优化配置（temperature=1.2）";
            } else {
                conclusion = "两组表现相近";
                recommendation = "可根据具体场景选择";
            }
        } else {
            conclusion = "数据不足，无法得出结论";
            recommendation = "建议增加样本量后重新测试";
        }

        return ABTestResult.builder()
                .groupA(groupA)
                .groupB(groupB)
                .conclusion(conclusion)
                .recommendation(recommendation)
                .build();
    }

    // ================================================================
    // 5. 人工评估
    // ================================================================

    @Override
    public EvaluationResult manualEvaluate(String question, String answer, Map<String, Double> scores, String evaluator) {
        log.info("📝 人工评估：{}，评估人：{}", question, evaluator);

        Double overallScore = scores.values().stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);

        return EvaluationResult.builder()
                .question(question)
                .answer(answer)
                .scores(scores)
                .overallScore(overallScore)
                .comment("人工评估")
                .evaluationType("manual")
                .evaluator(evaluator)
                .evaluationTime(LocalDateTime.now())
                .build();
    }

    // ================================================================
    // 6. 统计信息
    // ================================================================

    @Override
    public Map<String, Object> getEvaluationStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalEvaluations", totalEvaluations.get());
        stats.put("autoEvaluateEnabled", autoEvaluate);
        stats.put("historySize", evaluationHistory.size());
        stats.put("availableDimensions", EvaluationDimensionEnum.values());
        stats.put("advisors", List.of(
                "SensitiveWordAdvisor - 敏感词过滤",
                "ContentAuditAdvisor - 内容审核",
                "AuditAdvisor - 审计日志"
        ));
        return stats;
    }

    /**
     * 保存评估历史
     */
    private void saveHistory(EvaluationResult result) {
        String key = result.getQuestion() + "-" + result.getEvaluationTime();
        evaluationHistory.computeIfAbsent(key, k -> new ArrayList<>()).add(result);
        totalEvaluations.incrementAndGet();
    }
}
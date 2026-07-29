package com.ai.study.controller;

import com.ai.study.service.ModelMetricsService;
import com.ai.study.service.ModelRouterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 多模型聊天 Controller
 *
 * 功能说明：
 * 1. 手动选择模型对话
 * 2. 自动路由对话
 * 3. 模型对比
 * 4. 模型统计信息
 *
 * @author AI Study
 */
@RestController
@RequestMapping("/api/chat")
@Slf4j
public class ChatController {

    @Autowired
    private ModelRouterService routerService;

    @Autowired
    private ModelMetricsService metricsService;

    @Autowired
    @Qualifier("ollamaChatClient")
    private ChatClient ollamaClient;

    @Autowired
    @Qualifier("deepSeekChatClient")
    private ChatClient deepSeekClient;

    @Autowired
    @Qualifier("geminiChatClient")
    private ChatClient geminiClient;
    @Autowired
    @Qualifier("openRouterChatClient") // 👈 注入
    private  ChatClient openRouterClient;
    // ================================================================
    // 1. 手动选择模型
    // ================================================================

    /**
     * 使用指定模型对话
     *
     * 测试方式：GET /api/chat/select?model=ollama&message=你好
     *
     * @param model 模型代码：ollama / deepseek / gemini / auto
     * @param message 用户消息
     * @return AI 响应
     */
    @GetMapping("/select")
    public Map<String, Object> chatWithModel(
            @RequestParam(name = "model", defaultValue = "ollama") String model,
            @RequestParam(name = "message", defaultValue = "你好") String message) {

        log.info("💬 [收到请求] 指定模型参数: [{}] | 消息: [{}]", model, message);

        long startTime = System.currentTimeMillis();
        String modelName = model;
        String response = null;
        String error = null;

        try {
            ChatClient client;
            if ("auto".equalsIgnoreCase(model)) {
                client = routerService.route(message);
                modelName = getModelName(client);
                log.info("🔀 [自动路由] 路由决策成功 -> 选中模型: [{}]", modelName);
            } else {
                client = getClient(model);
                modelName = model;
                log.info("🎯 [手动选择] 映射模型: [{}]", modelName);
            }

            if (client == null) {
                throw new IllegalArgumentException("模型 [" + model + "] 不可用或未初始化");
            }

            // ================================================================
            // 💡 关键诊断日志：打印出当前持有的 Client 内存对象与类型
            // ================================================================
            log.info("🚀 [执行调用] 准备向大模型发起请求 -> 最终识别模型: [{}] | Client 实例: [{}]", modelName, client);

            response = client.prompt()
                    .user(message)
                    .call()
                    .content();

            long endTime = System.currentTimeMillis();
            metricsService.recordSuccess(modelName, endTime - startTime);

            log.info("✅ [调用成功] 模型 [{}] 响应完成，耗时: {}ms", modelName, (endTime - startTime));

            Map<String, Object> result = new HashMap<>();
            result.put("model", modelName);
            result.put("message", message);
            result.put("response", response);
            result.put("responseTime", endTime - startTime + "ms");
            result.put("availableModels", getAvailableModels());
            return result;

        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            metricsService.recordFailure(modelName);
            error = e.getMessage();

            log.error("❌ [调用异常] 模型 [{}] 调用失败: {}", modelName, e.getMessage(), e);

            Map<String, Object> result = new HashMap<>();
            result.put("error", "调用失败");
            result.put("model", modelName);
            result.put("message", e.getMessage());
            result.put("responseTime", endTime - startTime + "ms");
            return result;
        }
    }

    /**
     * 自动路由对话
     *
     * 测试方式：GET /api/chat/route?message=写一段Java代码
     *
     * @param message 用户消息
     * @return AI 响应（自动选择最优模型）
     */
    @GetMapping("/route")
    public Map<String, Object> chatWithRoute(
            @RequestParam(name = "message", defaultValue = "你好") String message) {

        log.info("🔀 使用自动路由对话：{}", message);

        long startTime = System.currentTimeMillis();

        try {
            // 1. 路由选择模型
            ChatClient client = routerService.route(message);

            // 2. 获取模型名称（用于统计）
            String modelName = getModelName(client);
            // ================================================================
            // ✅ 关键诊断：打印 client 的内存地址，与 select 中的对比
            // ================================================================
            log.info("🔍 [路由诊断] 获取到的 Client 实例: {}", System.identityHashCode(client));
            log.info("🔍 [路由诊断] 判断是否是 geminiClient: {}", client == geminiClient);
            log.info("🔍 [路由诊断] 判断是否是 ollamaClient: {}", client == ollamaClient);
            log.info("🔍 [路由诊断] 判断是否是 deepSeekClient: {}", client == deepSeekClient);
            // 3. 调用模型
            String response = client.prompt()
                    .user(message)
                    .call()
                    .content();

            long endTime = System.currentTimeMillis();
            metricsService.recordSuccess(modelName, endTime - startTime);

            Map<String, Object> result = new HashMap<>();
            result.put("model", modelName);
            result.put("routingReason", getRoutingReason(message));
            result.put("message", message);
            result.put("response", response);
            result.put("responseTime", endTime - startTime + "ms");
            return result;

        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            metricsService.recordFailure("unknown");

            Map<String, Object> result = new HashMap<>();
            result.put("error", "路由调用失败");
            result.put("message", e.getMessage());
            result.put("responseTime", endTime - startTime + "ms");
            return result;
        }
    }

    // ================================================================
    // 2. 模型对比
    // ================================================================

    /**
     * 对比所有模型
     *
     * 测试方式：GET /api/chat/compare-all?message=你好
     *
     * @param message 用户消息
     * @return 各模型响应对比
     */
    @GetMapping("/compare-all")
    public Map<String, Map<String, Object>> compareAllModels(
            @RequestParam(name = "message", defaultValue = "你好") String message) {

        log.info("📊 对比所有模型：{}", message);

        Map<String, Map<String, Object>> results = new HashMap<>();

        // 对比所有可用模型
        results.put("ollama", compareWithModel(ollamaClient, "Ollama（本地）", message));
        results.put("deepseek", compareWithModel(deepSeekClient, "DeepSeek", message));
        results.put("openrouter", compareWithModel(openRouterClient, "OpenRouter", message));

        if (geminiClient != null) {
            results.put("gemini", compareWithModel(geminiClient, "Gemini", message));
        }

        return results;
    }

    /**
     * 单个模型对比
     */
    private Map<String, Object> compareWithModel(ChatClient client, String modelName, String message) {
        Map<String, Object> result = new HashMap<>();
        result.put("model", modelName);

        if (client == null) {
            result.put("status", "❌ 未配置");
            result.put("response", "模型不可用");
            result.put("time", "N/A");
            return result;
        }

        try {
            long start = System.currentTimeMillis();
            String response = client.prompt()
                    .user(message)
                    .call()
                    .content();
            long end = System.currentTimeMillis();

            result.put("status", "✅ 成功");
            result.put("response", response);
            result.put("time", (end - start) + "ms");
            result.put("length", response.length());

        } catch (Exception e) {
            result.put("status", "❌ 失败");
            result.put("response", "调用失败: " + e.getMessage());
            result.put("time", "N/A");
        }

        return result;
    }

    // ================================================================
    // 3. 统计信息
    // ================================================================

    /**
     * 获取模型统计信息
     *
     * 测试方式：GET /api/chat/stats
     */
    @GetMapping("/stats")
    public Map<String, Object> getStats() {
        Map<String, Object> result = new HashMap<>();
        result.put("availableModels", getAvailableModels());

        var stats = metricsService.getAllStats();
        stats.forEach((model, stat) -> {
            Map<String, Object> statMap = new HashMap<>();
            statMap.put("totalCalls", stat.getTotalCalls().get());
            statMap.put("successCalls", stat.getSuccessCalls().get());
            statMap.put("failCalls", stat.getFailCalls().get());
            statMap.put("successRate", String.format("%.2f%%", stat.getSuccessRate()));
            statMap.put("avgResponseTime", String.format("%.2fms", stat.getAvgResponseTime()));
            statMap.put("minResponseTime", stat.getMinResponseTime() == Long.MAX_VALUE ? "N/A" : stat.getMinResponseTime() + "ms");
            statMap.put("maxResponseTime", stat.getMaxResponseTime() + "ms");
            result.put("stats_" + model, statMap);
        });

        return result;
    }

    /**
     * 重置统计信息
     *
     * 测试方式：POST /api/chat/stats/reset
     */
    @PostMapping("/stats/reset")
    public Map<String, Object> resetStats() {
        metricsService.resetStats();
        Map<String, Object> result = new HashMap<>();
        result.put("status", "success");
        result.put("message", "统计信息已重置");
        return result;
    }

    // ================================================================
    // 4. 健康检查
    // ================================================================

    /**
     * 健康检查
     *
     * 测试方式：GET /api/chat/health
     */
    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> status = new HashMap<>();
        status.put("status", "UP");
        status.put("module", "mic-spring-ai-0003-chat-client-models");

        // 检查各模型状态
        Map<String, String> models = new HashMap<>();
        models.put("ollama", ollamaClient != null ? "✅" : "❌");
        models.put("deepseek", deepSeekClient != null ? "✅" : "❌");
        models.put("gemini", geminiClient != null ? "✅" : "❌");
        models.put("openrouter", openRouterClient != null ? "✅" : "❌");
        status.put("models", models);

        // 检查可用性
        try {
            ollamaClient.prompt().user("ping").call().content();
            status.put("ollamaStatus", "✅ 可用");
        } catch (Exception e) {
            status.put("ollamaStatus", "❌ 不可用");
        }

        try {
            if (deepSeekClient != null) {
                deepSeekClient.prompt().user("ping").call().content();
                status.put("deepSeekStatus", "✅ 可用");
            } else {
                status.put("deepSeekStatus", "⚠️ 未配置");
            }
        } catch (Exception e) {
            status.put("deepSeekStatus", "❌ 不可用");
        }

        try {
            if (geminiClient != null) {
                geminiClient.prompt().user("ping").call().content();
                status.put("geminiStatus", "✅ 可用");
            } else {
                status.put("geminiStatus", "⚠️ 未配置");
            }
        } catch (Exception e) {
            status.put("geminiStatus", "❌ 不可用");
        }
        // 👈 新增 OpenRouter 健康检查
        try {
            if (openRouterClient != null) {
                openRouterClient.prompt().user("ping").call().content();
                status.put("openRouterStatus", "✅ 可用");
            } else {
                status.put("openRouterStatus", "⚠️ 未配置");
            }
        } catch (Exception e) {
            status.put("openRouterStatus", "❌ 不可用");
        }
        return status;
    }

    // ================================================================
    // 辅助方法
    // ================================================================

    /**
     * ✅ 简化：getClient 不再处理 auto，由上层统一处理
     */
    private ChatClient getClient(String model) {
        // ✅ 直接从 modelRegistry 获取，而不是使用字段
        Map<String, ChatClient> registry = routerService.getAvailableModels();
        ChatClient client = registry.get(model.toLowerCase());
        if (client == null) {
            log.warn("⚠️ 模型 {} 不在注册表中，可用模型：{}", model, registry.keySet());
        }
        return client;
    }

    /**
     * 获取可用模型列表
     */
    private Map<String, String> getAvailableModels() {
        Map<String, String> models = new HashMap<>();
        models.put("ollama", ollamaClient != null ? "可用" : "不可用");
        models.put("deepseek", deepSeekClient != null ? "可用" : "不可用");
        models.put("gemini", geminiClient != null ? "可用" : "不可用");
        models.put("openrouter", openRouterClient != null ? "可用" : "不可用");
        return models;
    }

    /**
     * 获取模型名称
     */
    private String getModelName(ChatClient client) {
        if (client == ollamaClient) return "ollama";
        if (client == deepSeekClient) return "deepseek";
        if (client == geminiClient) return "gemini";
        if (client == openRouterClient) return "openrouter";
        return "unknown";
    }

    /**
     * 获取路由原因
     */
    private String getRoutingReason(String message) {
        String lower = message.toLowerCase();
        if (lower.contains("代码") || lower.contains("编程") || lower.contains("java")) {
            return "检测到代码相关关键词 → 选择 DeepSeek";
        }
        if (lower.contains("写诗") || lower.contains("故事") || lower.contains("创意")) {
            return "检测到创意相关关键词 → 选择 Gemini";
        }
        if (lower.contains("翻译")) {
            return "检测到翻译关键词 → 选择 DeepSeek";
        }
        if (lower.contains("分析") || lower.contains("推理")) {
            return "检测到推理关键词 → 选择 Gemini";
        }
        return "简单问答 → 选择 Ollama（默认）";
    }
}
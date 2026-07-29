package com.ai.study;

import com.ai.study.service.ModelMetricsService;
import com.ai.study.service.ModelRouterService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.io.File;
import java.io.FileInputStream;
import java.util.Map;
import java.util.Properties;

/**
 * 多模型对比测试
 *
 * @author AI Study
 */
@SpringBootTest
@Slf4j
public class ModelComparisonTest {

    @DynamicPropertySource
    static void loadEnvProperties(DynamicPropertyRegistry registry) {
        try {
            Properties props = new Properties();
            String currentDir = System.getProperty("user.dir");

            File envFile = findEnvFile(new File(currentDir));
            if (envFile != null && envFile.exists()) {
                try (FileInputStream fis = new FileInputStream(envFile)) {
                    props.load(fis);
                    log.info("✅ .env 文件加载成功");
                }
            }

            props.forEach((key, value) -> {
                String strKey = key.toString();
                String strValue = value.toString();
                if (!strKey.startsWith("#") && !strValue.isEmpty()) {
                    registry.add(strKey, () -> strValue);
                }
            });

        } catch (Exception e) {
            log.warn("加载 .env 文件失败：{}", e.getMessage());
        }
    }

    private static File findEnvFile(File startDir) {
        File current = startDir;
        while (current != null) {
            File envFile = new File(current, ".env");
            if (envFile.exists()) {
                return envFile;
            }
            current = current.getParentFile();
        }
        return null;
    }

    // ================================================================
    // 注入
    // ================================================================

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

    // ================================================================
    // 测试方法
    // ================================================================

    /**
     * 测试功能点：验证所有模型是否正常注入
     */
    @Test
    void testModelInjection() {
        log.info("========== 测试模型注入 ==========");
        log.info("Ollama: {}", ollamaClient != null ? "✅" : "❌");
        log.info("DeepSeek: {}", deepSeekClient != null ? "✅" : "❌");
        log.info("Gemini: {}", geminiClient != null ? "✅" : "❌");
    }

    /**
     * 测试功能点：对比所有模型的响应
     */
    @Test
    void testModelComparison() {
        log.info("========== 对比所有模型 ==========");
        String message = "请用一句话介绍人工智能";

        // Ollama
        try {
            long start = System.currentTimeMillis();
            String response = ollamaClient.prompt().user(message).call().content();
            log.info("【Ollama（本地）】\n{} (耗时: {}ms)\n", response, System.currentTimeMillis() - start);
        } catch (Exception e) {
            log.error("Ollama 调用失败：{}", e.getMessage());
        }

        // DeepSeek
        try {
            long start = System.currentTimeMillis();
            String response = deepSeekClient.prompt().user(message).call().content();
            log.info("【DeepSeek】\n{} (耗时: {}ms)\n", response, System.currentTimeMillis() - start);
        } catch (Exception e) {
            log.error("DeepSeek 调用失败：{}", e.getMessage());
        }

        // Gemini
        if (geminiClient != null) {
            try {
                long start = System.currentTimeMillis();
                String response = geminiClient.prompt().user(message).call().content();
                log.info("【Gemini】\n{} (耗时: {}ms)\n", response, System.currentTimeMillis() - start);
            } catch (Exception e) {
                log.error("Gemini 调用失败：{}", e.getMessage());
            }
        }
    }

    /**
     * 测试功能点：测试不同场景下的路由决策
     */
    @Test
    void testRoutingScenarios() {
        log.info("========== 测试路由决策 ==========");

        String[] testMessages = {
                "写一段Java代码实现冒泡排序",      // 代码 → DeepSeek
                "写一首关于春天的诗",              // 创意 → Gemini
                "翻译这段文字成英文",              // 翻译 → DeepSeek
                "分析这个问题的逻辑结构",           // 推理 → Gemini
                "你好，请介绍一下自己"              // 简单 → Ollama
        };

        for (String message : testMessages) {
            log.info("\n--- 消息：{} ---", message);
            ChatClient client = routerService.route(message);
            String modelName = getModelName(client);
            log.info("路由结果：{}", modelName);

            try {
                long start = System.currentTimeMillis();
                String response = client.prompt().user(message).call().content();
                log.info("响应预览：{}... (耗时: {}ms)",
                        response.length() > 100 ? response.substring(0, 100) : response,
                        System.currentTimeMillis() - start);
            } catch (Exception e) {
                log.error("调用失败：{}", e.getMessage());
            }
        }
    }

    /**
     * 测试功能点：统计信息验证
     */
    @Test
    void testMetrics() {
        log.info("========== 测试统计信息 ==========");

        // 先调用几次产生数据
        for (int i = 0; i < 3; i++) {
            try {
                ollamaClient.prompt().user("hi").call().content();
                metricsService.recordSuccess("ollama", 100 + i * 50);
            } catch (Exception e) {
                metricsService.recordFailure("ollama");
            }
        }

        var stats = metricsService.getAllStats();
        stats.forEach((model, stat) -> {
            log.info("模型 [{}]:", model);
            log.info("  总调用: {}", stat.getTotalCalls().get());
            log.info("  成功: {}", stat.getSuccessCalls().get());
            log.info("  失败: {}", stat.getFailCalls().get());
            log.info("  成功率: {:.2f}%", stat.getSuccessRate());
            log.info("  平均耗时: {:.2f}ms", stat.getAvgResponseTime());
        });
    }

    /**
     * 测试功能点：模型可用性检查
     */
    @Test
    void testModelAvailability() {
        log.info("========== 测试模型可用性 ==========");

        log.info("Ollama 可用: {}", routerService.isModelAvailable("ollama"));
        log.info("DeepSeek 可用: {}", routerService.isModelAvailable("deepseek"));
        log.info("Gemini 可用: {}", routerService.isModelAvailable("gemini"));

        Map<String, ChatClient> available = routerService.getAvailableModels();
        log.info("可用模型: {}", available.keySet());
    }

    private String getModelName(ChatClient client) {
        if (client == ollamaClient) return "Ollama";
        if (client == deepSeekClient) return "DeepSeek";
        if (client == geminiClient) return "Gemini";
        return "Unknown";
    }
}

package com.ai.study;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

/**
 * 基础配置单元测试（默认使用 Ollama 本地模型）
 *
 * @author AI Study
 */
@SpringBootTest
@Slf4j
public class ChatClientConfigTest {

    // ==========================================
    // 注入不同的 ChatClient
    // ==========================================

    /**
     * 默认 ChatClient（Ollama 本地模型）
     * 使用 @Primary 标记
     */
    @Autowired
    private ChatClient defaultChatClient;

    /**
     * OpenAI 兼容接口 ChatClient（如 DeepSeek）
     */
    @Autowired
    @Qualifier("openAiCompatibleChatClient")
    private ChatClient openAiCompatibleChatClient;

    /**
     * Gemini ChatClient
     */
    @Autowired
    @Qualifier("geminiChatClient")
    private ChatClient geminiChatClient;

    // ==========================================
    // 测试方法
    // ==========================================

    /**
     * 测试功能点：验证所有 ChatClient Bean 是否正确注入
     */
    @Test
    void testBeanInjection() {
        log.info("========== 测试 Bean 注入 ==========");
        log.info("defaultChatClient (Ollama): {}", defaultChatClient != null ? "注入成功 ✅" : "注入失败 ❌");
        log.info("deepSeekChatClient: {}", openAiCompatibleChatClient != null ? "注入成功 ✅" : "注入失败 ❌");
        log.info("geminiChatClient: {}", geminiChatClient != null ? "注入成功 ✅" : "注入失败 ❌");
        log.info("=====================================");
    }

    /**
     * 测试功能点：测试默认模型（Ollama 本地）的基础对话
     *
     * 测试场景：发送简单问候，验证本地模型能否正常响应
     *
     * 注意：需要先启动 Ollama 服务并下载模型
     *
     * 预期结果：返回有意义的响应内容
     */
    @Test
    void testDefaultModelChat() {
        log.info("========== 测试默认模型（Ollama 本地）对话 ==========");
        String message = "你好，请简单介绍一下你自己";

        long startTime = System.currentTimeMillis();
        String response = defaultChatClient.prompt()
                .system("你是一个友好的AI助手")
                .user(message)
                .call()
                .content();
        long endTime = System.currentTimeMillis();

        log.info("用户消息：{}", message);
        log.info("AI 响应：{}", response);
        log.info("响应耗时：{}ms", endTime - startTime);
        log.info("===================================================");
    }

    /**
     * 测试功能点：测试 DeepSeek 模型的基础对话
     */
    @Test
    void testDeepSeekModelChat() {
        log.info("========== 测试 DeepSeek 对话 ==========");
        String message = "你好，请简单介绍一下你自己";

        try {
            long startTime = System.currentTimeMillis();
            String response = openAiCompatibleChatClient.prompt()
                    .system("你是一个友好的AI助手")
                    .user(message)
                    .call()
                    .content();
            long endTime = System.currentTimeMillis();

            log.info("用户消息：{}", message);
            log.info("AI 响应：{}", response);
            log.info("响应耗时：{}ms", endTime - startTime);
        } catch (Exception e) {
            log.error("DeepSeek 模型调用失败，请确认 DEEPSEEK_API_KEY 配置是否正确");
            log.error("错误信息：{}", e.getMessage());
        }
        log.info("=========================================");
    }

    /**
     * 测试功能点：测试 Google Gemini 的基础对话
     */
    @Test
    void testGeminiModelChat() {
        log.info("========== 测试 Google Gemini 对话 ==========");
        String message = "你好，请简单介绍一下你自己";

        try {
            long startTime = System.currentTimeMillis();
            String response = geminiChatClient.prompt()
                    .system("你是一个友好的AI助手")
                    .user(message)
                    .call()
                    .content();
            long endTime = System.currentTimeMillis();

            log.info("用户消息：{}", message);
            log.info("AI 响应：{}", response);
            log.info("响应耗时：{}ms", endTime - startTime);
        } catch (Exception e) {
            log.error("Gemini 模型调用失败，请确认 GEMINI_API_KEY 配置是否正确");
            log.error("错误信息：{}", e.getMessage());
        }
        log.info("===========================================");
    }

    /**
     * 测试功能点：对比所有模型的响应
     */
    @Test
    void testCompareAllModels() {
        log.info("========== 对比所有模型 ==========");
        String question = "请用一句话介绍中国的首都北京";

        Map<String, String> responses = new HashMap<>();

        // 1. Ollama（本地）
        try {
            long start = System.currentTimeMillis();
            String response = defaultChatClient.prompt().user(question).call().content();
            responses.put("Ollama（本地）", response + " (耗时: " + (System.currentTimeMillis() - start) + "ms)");
        } catch (Exception e) {
            responses.put("Ollama（本地）", "调用失败: " + e.getMessage());
        }

        // 2. DeepSeek
        try {
            long start = System.currentTimeMillis();
            String response = openAiCompatibleChatClient.prompt().user(question).call().content();
            responses.put("DeepSeek", response + " (耗时: " + (System.currentTimeMillis() - start) + "ms)");
        } catch (Exception e) {
            responses.put("DeepSeek", "调用失败: " + e.getMessage());
        }

        // 3. Gemini
        try {
            long start = System.currentTimeMillis();
            String response = geminiChatClient.prompt().user(question).call().content();
            responses.put("Gemini", response + " (耗时: " + (System.currentTimeMillis() - start) + "ms)");
        } catch (Exception e) {
            responses.put("Gemini", "调用失败: " + e.getMessage());
        }

        // 打印对比结果
        responses.forEach((model, response) -> {
            log.info("\n【{}】\n{}\n", model, response);
        });
    }
}

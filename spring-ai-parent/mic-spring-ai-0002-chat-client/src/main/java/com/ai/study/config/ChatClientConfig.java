package com.ai.study.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * ChatClient 配置类
 *
 * 功能说明：
 * 1. Ollama 本地模型为默认模型（@Primary）- 免费、隐私安全
 * 2. DeepSeek 通过 OpenAI 兼容接口接入 - 高性能云端模型
 * 3. Google Gemini 通过 Google AI Studio OpenAI 兼容接口接入 - 免费云端模型
 *
 * Gemini 配置说明：
 * - 使用 Google AI Studio 生成的 API Key（完全免费）
 * - 端点：https://generativelanguage.googleapis.com/v1beta/openai/
 * - 模型：gemini-2.0-flash 或 gemini-2.0-flash-exp
 * - 获取 Key：https://aistudio.google.com/app/apikey
 *
 * 当前版本：Spring AI 1.1.2
 *
 * @author AI Study
 */
@Configuration
@Slf4j
public class ChatClientConfig {

    // ================================================================
    // 配置属性注入
    // ================================================================

    @Value("${spring.ai.ollama.chat.options.model:qwen2.5-coder:14b}")
    private String ollamaModel;

    @Value("${spring.ai.openai.chat.options.model:deepseek-chat}")
    private String deepSeekModel;

    // Gemini 配置
    @Value("${gemini.api-key:}")
    private String geminiApiKey;

    @Value("${gemini.base-url:https://generativelanguage.googleapis.com/v1beta/openai/}")
    private String geminiBaseUrl;

    @Value("${gemini.chat.options.model:gemini-2.0-flash}")
    private String geminiModel;

    @Value("${gemini.chat.options.temperature:0.7}")
    private Double geminiTemperature;

    // ================================================================
    // Bean 定义
    // ================================================================

    /**
     * 默认 ChatClient - 使用 Ollama 本地模型
     *
     * @Primary 表示默认注入此 Bean
     */
    @Bean
    @Primary
    public ChatClient defaultChatClient(OllamaChatModel ollamaChatModel) {
        log.info("========================================");
        log.info("✅ 初始化默认 ChatClient：Ollama 本地模型");
        log.info("   📍 模型：{}", ollamaModel);
        log.info("   📍 类型：本地部署，免费使用");
        log.info("========================================");

        return ChatClient.builder(ollamaChatModel)
                .defaultSystem("你是一个友好的AI助手，请用中文回答问题。")
                .build();
    }

    /**
     * DeepSeek ChatClient（通过 OpenAI 兼容接口）
     */
    @Bean
    @Qualifier("deepSeekChatClient")
    public ChatClient deepSeekChatClient(OpenAiChatModel openAiChatModel) {
        log.info("========================================");
        log.info("✅ 初始化 DeepSeek ChatClient");
        log.info("   📍 模型：{}", deepSeekModel);
        log.info("   📍 类型：云端 API，需要 API Key");
        log.info("========================================");

        return ChatClient.builder(openAiChatModel)
                .defaultSystem("你是一个友好的AI助手，请用中文回答问题。")
                .build();
    }

    /**
     * Google Gemini ChatClient（通过 Google AI Studio OpenAI 兼容接口）
     *
     * 使用 OpenAiChatModel.builder() 创建，这是源码中正确的创建方式
     *
     * 获取 API Key：https://aistudio.google.com/app/apikey
     */
    @Bean
    @Qualifier("geminiChatClient")
    public ChatClient geminiChatClient() {
        log.info("========================================");
        log.info("✅ 初始化 Google Gemini ChatClient");

        // 检查 API Key 是否配置
        if (geminiApiKey == null || geminiApiKey.isEmpty() || geminiApiKey.startsWith("your-")) {
            log.warn("   ⚠️ Google Gemini API Key 未配置");
            log.warn("   📍 获取地址：https://aistudio.google.com/app/apikey");
            log.info("========================================");
            return null;
        }

        log.info("   📍 端点：{}", geminiBaseUrl);
        log.info("   📍 模型：{}", geminiModel);
        log.info("   📍 温度：{}", geminiTemperature);
        log.info("   📍 类型：Google AI Studio（免费）");
        log.info("========================================");

        try {
            // ================================================================
            // 使用 OpenAiApi.builder() 创建 OpenAiApi
            // ================================================================
            OpenAiApi openAiApi = OpenAiApi.builder()
                    .baseUrl(geminiBaseUrl)
                    .apiKey(geminiApiKey)
                    .build();

            // ================================================================
            // 创建 OpenAiChatOptions
            // ================================================================
            OpenAiChatOptions options = OpenAiChatOptions.builder()
                    .model(geminiModel)
                    .temperature(geminiTemperature)
                    .build();

            // ================================================================
            // 使用 OpenAiChatModel.builder() 创建模型实例
            // ================================================================
            OpenAiChatModel geminiModelInstance = OpenAiChatModel.builder()
                    .openAiApi(openAiApi)
                    .defaultOptions(options)
                    .build();

            return ChatClient.builder(geminiModelInstance)
                    .defaultSystem("你是一个友好的AI助手，请用中文回答问题。")
                    .build();

        } catch (Exception e) {
            log.error("❌ Google Gemini 初始化失败");
            log.error("   错误信息：{}", e.getMessage());
            log.error("   请检查 GEMINI_API_KEY 是否有效");
            log.info("========================================");
            return null;
        }
    }

    /**
     * 打印配置信息
     *
     * 注意：@Bean 方法不能返回 void，所以返回一个 String 或使用 @PostConstruct
     * 方式1：返回一个字符串（推荐）
     */
    @Bean
    public String printConfigInfo(
            @Qualifier("defaultChatClient") ChatClient defaultClient,
            @Qualifier("deepSeekChatClient") ChatClient deepSeekClient,
            @Qualifier("geminiChatClient") ChatClient geminiClient) {

        log.info("========================================");
        log.info("📋 ChatClient 配置完成");
        log.info("========================================");
        log.info("✅ 默认模型: Ollama（本地）");
        log.info("✅ 云端模型: DeepSeek");
        if (geminiClient != null) {
            log.info("✅ 云端模型: Google Gemini（免费）");
        } else {
            log.info("⚠️ 云端模型: Google Gemini（未配置）");
        }
        log.info("========================================");
        log.info("💡 使用说明：");
        log.info("   - 默认使用 Ollama 本地模型，无需 API Key");
        log.info("   - DeepSeek 需要 DEEPSEEK_API_KEY 环境变量");
        log.info("   - Gemini 需要 GEMINI_API_KEY 环境变量");
        log.info("   - Gemini 获取地址：https://aistudio.google.com/app/apikey");
        log.info("========================================");

        return "ChatClient 配置完成";
    }
}
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

import java.util.HashMap;
import java.util.Map;

/**
 * ChatClient 配置类
 *
 * 功能说明：
 * 1. 配置三个不同的模型：Ollama、DeepSeek、Gemini
 * 2. 使用 @Primary 标记默认模型
 * 3. 使用 @Qualifier 区分不同模型
 * 4. 提供模型注册表供路由使用
 *
 * @author AI Study
 */
@Configuration
@Slf4j
public class ChatClientConfig {

    // ================================================================
    // 配置属性注入
    // ================================================================

    @Value("${spring.ai.ollama.chat.options.model}")
    private String ollamaModel;

    @Value("${spring.ai.openai.chat.options.model}")
    private String deepSeekModel;

    @Value("${gemini.api-key}")
    private String geminiApiKey;

    @Value("${gemini.base-url}")
    private String geminiBaseUrl;

    @Value("${gemini.chat.options.model}")
    private String geminiModel;

    @Value("${gemini.chat.options.temperature}")
    private Double geminiTemperature;

    // ================================================================
    // OpenRouter 配置属性注入
    // ================================================================
    @Value("${openrouter.api-key}")
    private String openRouterApiKey;

    @Value("${openrouter.base-url}")
    private String openRouterBaseUrl;

    @Value("${openrouter.chat.options.model}")
    private String openRouterModel;

    @Value("${openrouter.chat.options.temperature}")
    private Double openRouterTemperature;
    // ================================================================
    // Bean 定义
    // ================================================================

    /**
     * Ollama 本地模型（默认）
     */
    @Bean
    @Primary
    @Qualifier("ollamaChatClient")
    public ChatClient ollamaChatClient(OllamaChatModel ollamaChatModel) {
        log.info("========================================");
        log.info("✅ 初始化 Ollama 本地模型");
        log.info("   📍 模型：{}", ollamaModel);
        log.info("   📍 特点：免费、隐私安全、可离线");
        log.info("========================================");

        return ChatClient.builder(ollamaChatModel)
                .defaultSystem("你是一个友好的AI助手，请用中文回答问题。")
                .build();
    }

    /**
     * DeepSeek 云端模型
     */
    @Bean
    @Qualifier("deepSeekChatClient")
    public ChatClient deepSeekChatClient(OpenAiChatModel openAiChatModel) {
        log.info("========================================");
        log.info("✅ 初始化 DeepSeek 云端模型");
        log.info("   📍 模型：{}", deepSeekModel);
        log.info("   📍 特点：性价比高、中文友好、代码能力强");
        log.info("========================================");

        return ChatClient.builder(openAiChatModel)
                .defaultSystem("你是一个友好的AI助手，请用中文回答问题。")
                .build();
    }

    /**
     * Google Gemini 云端模型（通过 OpenAI 兼容接口）
     */
    @Bean
    @Qualifier("geminiChatClient")
    public ChatClient geminiChatClient() {
        log.info("========================================");
        log.info("✅ 初始化 Google Gemini 云端模型");
        log.info("   📍 读取到的模型名称：{}", geminiModel);  // ✅ 添加这行日志
        if (geminiApiKey == null || geminiApiKey.isEmpty() || geminiApiKey.startsWith("your-")) {
            log.warn("   ⚠️ Google Gemini API Key 未配置，跳过初始化");
            log.info("   📍 获取地址：https://aistudio.google.com/app/apikey");
            log.info("========================================");

        }

        log.info("   📍 模型：{}", geminiModel);
        log.info("   📍 特点：免费、多模态能力、推理强");
        log.info("========================================");

        try {
            OpenAiApi geminiApi = OpenAiApi.builder()
                    .baseUrl(geminiBaseUrl)
                    .apiKey(geminiApiKey)
                    .build();

            OpenAiChatOptions options = OpenAiChatOptions.builder()
                    .model(geminiModel)
                    .temperature(geminiTemperature)
                    .build();

            OpenAiChatModel geminiModelInstance = OpenAiChatModel.builder()
                    .openAiApi(geminiApi)
                    .defaultOptions(options)
                    .build();

            return ChatClient.builder(geminiModelInstance)
                    .defaultSystem("你是一个友好的AI助手，请用中文回答问题。")
                    .build();

        } catch (Exception e) {
            log.error("❌ Google Gemini 初始化失败：{}", e.getMessage());
            return null;
        }
    }
    /**
     * OpenRouter 云端模型（通过 OpenAI 兼容接口）
     */
    @Bean
    @Qualifier("openRouterChatClient")
    public ChatClient openRouterChatClient() {
        log.info("========================================");
        log.info("✅ 初始化 OpenRouter 云端模型");

        if (openRouterApiKey == null || openRouterApiKey.isEmpty() || openRouterApiKey.startsWith("your-")) {
            log.warn("   ⚠️ OpenRouter API Key 未配置，跳过初始化");
            log.info("   📍 获取地址：https://openrouter.ai/keys");
            log.info("========================================");
            return null;
        }

        log.info("   📍 模型：{}", openRouterModel);
        log.info("   📍 特点：聚合平台、多模型选择、包含免费模型");
        log.info("========================================");

        try {
            OpenAiApi openRouterApi = OpenAiApi.builder()
                    .baseUrl(openRouterBaseUrl)
                    .apiKey(openRouterApiKey)
                    .completionsPath("/chat/completions") // ✅ 加上这行
                    .build();

            OpenAiChatOptions options = OpenAiChatOptions.builder()
                    .model(openRouterModel)
                    .temperature(openRouterTemperature)
                    .build();

            OpenAiChatModel openRouterModelInstance = OpenAiChatModel.builder()
                    .openAiApi(openRouterApi)
                    .defaultOptions(options)
                    .build();

            return ChatClient.builder(openRouterModelInstance)
                    .defaultSystem("你是一个友好的AI助手，请用中文回答问题。")
                    .build();

        } catch (Exception e) {
            log.error("❌ OpenRouter 初始化失败：{}", e.getMessage());
            return null;
        }
    }
    /**
     * 模型注册表 - 供路由服务使用
     */
    @Bean
    public Map<String, ChatClient> modelRegistry(
            @Qualifier("ollamaChatClient") ChatClient ollamaClient,
            @Qualifier("deepSeekChatClient") ChatClient deepSeekClient,
            @Qualifier("geminiChatClient") ChatClient geminiClient,
            @Qualifier("openRouterChatClient") ChatClient openRouterClient) {

        Map<String, ChatClient> registry = new HashMap<>();
        // ================================================================
        // 修复：只注册非 null 的模型
        // ================================================================
        if (ollamaClient != null) {
            registry.put("ollama", ollamaClient);
        }
        if (deepSeekClient != null) {
            registry.put("deepseek", deepSeekClient);
        }
        if (geminiClient != null) {
            registry.put("gemini", geminiClient);
        }
        if (openRouterClient != null) {
            registry.put("openrouter", openRouterClient);
        }
        // ================================================================
        // ✅ 打印每个 client 的内存地址
        // ================================================================
        log.info("🔍 [注册诊断] ollamaClient: {}", ollamaClient);
        log.info("🔍 [注册诊断] deepSeekClient: {}", deepSeekClient);
        log.info("🔍 [注册诊断] geminiClient: {}", geminiClient);
        log.info("🔍 [注册诊断] openrouter: {}", openRouterClient);
        log.info("🔍 [注册诊断] ollamaClient 内存地址: {}", System.identityHashCode(ollamaClient));
        log.info("🔍 [注册诊断] deepSeekClient 内存地址: {}", System.identityHashCode(deepSeekClient));
        log.info("🔍 [注册诊断] geminiClient 内存地址: {}", System.identityHashCode(geminiClient));
        log.info("🔍 [注册诊断] openrouter 内存地址: {}", System.identityHashCode(openRouterClient));
        log.info("📋 模型注册表：{}", registry.keySet());
        return registry;
    }

    /**
     * 打印配置信息
     *
     * 注意：@Bean 方法不能返回 void，所以返回一个 String 或使用 @PostConstruct
     * 方式1：返回一个字符串（推荐）
     */
    @Bean
    public String printConfigInfo(
            @Qualifier("ollamaChatClient") ChatClient ollamaChatClient,
            @Qualifier("deepSeekChatClient") ChatClient deepSeekClient,
            @Qualifier("geminiChatClient") ChatClient geminiClient,
            @Qualifier("openRouterChatClient") ChatClient openRouterClient
    ) {

        log.info("========================================");
        log.info("📋 多模型配置完成");
        log.info("========================================");
        log.info("✅ 模型1: Ollama（本地）- {}", ollamaModel);
        log.info("✅ 模型2: DeepSeek（云端）- {}", deepSeekModel);
        boolean geminiAvailable = geminiApiKey != null && !geminiApiKey.isEmpty() && !geminiApiKey.startsWith("your-");
        if (geminiAvailable) {
            log.info("✅ 模型3: Gemini（云端）- {}", geminiModel);
        } else {
            log.info("⚠️ 模型3: Gemini（未配置）");
        }
        log.info("✅ 模型4: openrouter（云端）- {}", openRouterModel);
        log.info("========================================");
        log.info("💡 默认模型: Ollama（本地）");
        log.info("========================================");

        return "ChatClient 配置完成";
    }
}
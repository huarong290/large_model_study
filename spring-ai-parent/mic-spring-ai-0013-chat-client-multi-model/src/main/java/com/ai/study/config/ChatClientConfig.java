package com.ai.study.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * ChatClient 配置类
 * 功能说明：
 * 1. 为不同模型创建独立的 ChatClient Bean
 * 2. 使用 @Primary 将本地 Ollama 设为默认 ChatClient
 * 3. 使用 @Qualifier 区分不同的 ChatClient 保留云端模型（DeepSeek、Gemini）作为备用
 *
 * @author AI Study
 */
@Configuration
@Slf4j
public class ChatClientConfig {

    @Value("${spring.ai.ollama.base-url:http://localhost:11434}")
    private String ollamaBaseUrl;

    @Value("${spring.ai.ollama.chat.options.model:llama3.2}")
    private String ollamaModel;

    @Value("${spring.ai.openai.base-url:https://api.deepseek.com}")
    private String openAiBaseUrl;

    @Value("${spring.ai.openai.chat.options.model:deepseek-chat}")
    private String openAiModel;

    @Value("${gemini.base-url:https://generativelanguage.googleapis.com/v1beta/openai/}")
    private String geminiBaseUrl;

    @Value("${gemini.chat.options.model:gemini-2.0-flash}")
    private String geminiModel;
    /**
     * 默认 ChatClient - 优先使用本地 Ollama 模型（@Primary）
     * 当其他类直接注入 ChatClient 且不指定 @Qualifier 时，默认使用此本地模型
     */
    @Bean
    @Primary
    public ChatClient defaultChatClient(OllamaChatModel ollamaChatModel) {
        log.info("初始化默认 ChatClient -> 本地模型 [Ollama] (地址: {}, 模型: {})...", ollamaBaseUrl, ollamaModel);
        return ChatClient.builder(ollamaChatModel)
                // 设置默认系统提示词
                .defaultSystem("你是一个友好的AI助手，请用中文回答问题。")
                // 设置默认温度
                .defaultOptions(ollamaChatModel.getDefaultOptions())
                .build();
    }
    /**
     * 云端备用模型 - 通用 OpenAI 兼容接口（目前对接 DeepSeek，后续可随时切换为 GPT-4o、Kimi、通义等）
     * @Qualifier("openAiCompatibleChatClient") 用于在注入时指定使用这个 Bean
     */
    @Bean
    @Qualifier("openAiCompatibleChatClient")
    public ChatClient openAiCompatibleChatClient(OpenAiChatModel openAiChatModel) {
        log.info("初始化云端备用 ChatClient -> [OpenAI 兼容接口] (地址: {}, 模型: {})...", openAiBaseUrl, openAiModel);
        return ChatClient.builder(openAiChatModel)
                .defaultSystem("你是一个友好的AI助手，请用中文回答问题。")
                // 设置默认温度
                .defaultOptions(openAiChatModel.getDefaultOptions())
                .build();
    }

    /**
     * 3. Google Gemini ChatClient：通过 Google AI Studio 的 OpenAI 兼容接口接入
     */
    @Bean(name = "geminiChatClient")
    public ChatClient geminiChatClient(
            @Value("${gemini.api-key}") String apiKey,
            @Value("${gemini.base-url}") String baseUrl,
            @Value("${gemini.chat.options.model}") String model,
            @Value("${gemini.chat.options.temperature}") double temperature) {
        if (apiKey == null || apiKey.trim().isEmpty() || apiKey.startsWith("your-")) {
            log.error("❌ Google Gemini API Key 未配置或为无效的占位符！");
        }
        log.info("初始化云端多模态 ChatClient -> [Google Gemini (OpenAI 兼容)] (模型: {})...", model);

        // 使用 OpenAiApi 构建器
        OpenAiApi openAiApi = OpenAiApi.builder()
                .baseUrl(baseUrl)
                .apiKey(apiKey)
                .build();

        // 使用 OpenAiChatOptions 设置模型参数
        OpenAiChatOptions options = OpenAiChatOptions.builder()
                .model(model)
                .temperature(temperature)
                .build();

        // 核心修改：使用 OpenAiChatModel.builder() 替代已移除的构造函数
        OpenAiChatModel geminiChatModel = OpenAiChatModel.builder()
                .openAiApi(openAiApi)
                .defaultOptions(options)
                .build();

        return ChatClient.builder(geminiChatModel).build();
    }


    /**
     * 容器启动完成后打印配置信息（使用 CommandLineRunner 替代返回 void 的 Bean）
     */
    @Bean
    public CommandLineRunner printModelInfo(
            @Qualifier("defaultChatClient") ChatClient defaultClient,
            @Qualifier("openAiCompatibleChatClient") ChatClient openAiClient,
            @Qualifier("geminiChatClient") ChatClient geminiChatClient) {
        return args -> {
            log.info("=================================================================");
            log.info("                 AI 模型路由与后端运维看板                         ");
            log.info("=================================================================");
            log.info(" 👉 [默认首选] 本地模型 (Primary):");
            log.info("    - 引擎类型: Ollama");
            log.info("    - 服务地址: {}", ollamaBaseUrl);
            log.info("    - 运行模型: {}", ollamaModel);
            log.info("-----------------------------------------------------------------");
            log.info(" 🛡️ [云端备用 1] OpenAI 兼容接口 (DeepSeek):");
            log.info("    - 接口网关: {}", openAiBaseUrl);
            log.info("    - 运行模型: {}", openAiModel);
            log.info("-----------------------------------------------------------------");
            log.info(" 🛡️ [云端备用 2] Google Gemini (AI Studio):");
            log.info("    - 接口网关: {}", geminiBaseUrl);
            log.info("    - 运行模型: {}", geminiModel);
            log.info("=================================================================");
        };

    }
}

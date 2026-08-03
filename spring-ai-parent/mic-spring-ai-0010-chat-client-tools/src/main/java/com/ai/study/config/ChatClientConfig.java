package com.ai.study.config;


import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
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
 * @author AI Study
 */
@Configuration
@Slf4j
public class ChatClientConfig {

    @Value("${spring.ai.ollama.chat.options.model}")
    private String ollamaModel;

    @Value("${spring.ai.openai.chat.options.model}")
    private String deepSeekModel;

    // ================================================================
    // Bean 定义
    // ================================================================

    @Bean
    @Primary
    @Qualifier("ollamaChatClient")
    public ChatClient ollamaChatClient(OllamaChatModel ollamaChatModel) {
        log.info("========================================");
        log.info("✅ 初始化 Ollama 本地模型");
        log.info("   📍 模型：{}", ollamaModel);
        log.info("========================================");

        return ChatClient.builder(ollamaChatModel)
                .defaultSystem("你是一个友好的AI助手，请用中文回答问题。")
                .build();
    }

    @Bean
    @Qualifier("deepSeekChatClient")
    public ChatClient deepSeekChatClient(OpenAiChatModel openAiChatModel) {
        log.info("========================================");
        log.info("✅ 初始化 DeepSeek 云端模型");
        log.info("   📍 模型：{}", deepSeekModel);
        log.info("========================================");

        return ChatClient.builder(openAiChatModel)
                .defaultSystem("你是一个友好的AI助手，请用中文回答问题。")
                .build();
    }

    /**
     * 模型注册表
     */
    @Bean
    public Map<String, ChatClient> modelRegistry(
            @Qualifier("ollamaChatClient") ChatClient ollamaClient,
            @Qualifier("deepSeekChatClient") ChatClient deepSeekClient) {

        Map<String, ChatClient> registry = new HashMap<>();
        if (ollamaClient != null) {
            registry.put("ollama", ollamaClient);
        }
        if (deepSeekClient != null) {
            registry.put("deepseek", deepSeekClient);
        }
        log.info("📋 模型注册表：{}", registry.keySet());
        return registry;
    }

    /**
     * 打印配置信息
     * 注意：@Bean 方法不能返回 void，所以返回一个 String 或使用 @PostConstruct
     * 方式1：返回一个字符串（推荐）
     */
    @Bean
    public String printConfigInfo(
            @Qualifier("ollamaChatClient") ChatClient ollamaChatClient,
            @Qualifier("deepSeekChatClient") ChatClient deepSeekClient) {

        log.info("========================================");
        log.info("📋 工具调用模块配置完成");
        log.info("========================================");
        log.info("✅ 模型1: Ollama（本地）- {}", ollamaModel);
        log.info("✅ 模型2: DeepSeek（云端）- {}", deepSeekModel);
        return "ChatClient 配置完成";
    }
}
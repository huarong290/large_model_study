/*
package com.ai.study.config;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
langchain4j-ollama-spring-boot-starter 引入依赖 就不需要手动注册bean
@Data
@ConfigurationProperties("langchain4j.ollama.chat-model")
@Configuration
public class OllamaConfig {

    private String baseUrl;

    private String modelName;

    @Bean("ollamaChatModel")
    public ChatModel chatModel() {
        return OllamaChatModel.builder()
                .baseUrl(baseUrl)
                .modelName(modelName)
                .temperature(0.7)
                .timeout(Duration.ofSeconds(60))
                .logRequests(true)
                .logResponses(true)
                .build();
    }
}
*/

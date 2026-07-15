package com.ai.study.config;

import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatMemoryConfig {


    @Bean("chatMemoryProvider")
    public ChatMemoryProvider chatMemoryProvider(){

        return chatId-> MessageWindowChatMemory.builder().id(chatId).maxMessages(20).build();
    }
}

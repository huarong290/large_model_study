package com.ai.study.config;

import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatMemoryConfig {

    @Bean(name ="windowChatMemoryProvider")
    public ChatMemoryProvider windowChatMemoryProvider(){
        return sessionId->
            MessageWindowChatMemory.builder()
                    .id(sessionId)
                    .maxMessages(10)
                    .build();
    }
}

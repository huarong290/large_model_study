package com.ai.study.config;

import com.ai.study.persist.MysqlChatMemoryStore;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LLConfig {

    @Autowired
    private MysqlChatMemoryStore mysqlChatMemoryStore;

    @Bean(name ="mysqlChatMemoryProvider")
    public ChatMemoryProvider mysqlChatMemoryProvider(){
        return sessionId->
                MessageWindowChatMemory.builder()
                        .id(sessionId)
                        .maxMessages(10)
                        .chatMemoryStore(mysqlChatMemoryStore)
                        .build();
    }
}


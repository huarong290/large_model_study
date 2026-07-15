package com.ai.study.config;

import com.ai.study.persist.MysqlChatMemoryStore;
import com.ai.study.persist.RedisChatMemoryStore;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LLConfig {

    @Autowired
    private MysqlChatMemoryStore mysqlChatMemoryStore;

//
//    @Bean(name ="mysqlChatMemoryProvider")
//    public ChatMemoryProvider mysqlChatMemoryProvider(){
//        return sessionId->
//                MessageWindowChatMemory.builder()
//                        .id(sessionId)
//                        .maxMessages(10)
//                        .chatMemoryStore(mysqlChatMemoryStore)
//                        .build();
//    }

    @Autowired
    private RedisChatMemoryStore redisChatMemoryStore ;
    @Bean(name ="redisChatMemoryProvider")
    public ChatMemoryProvider redisChatMemoryProvider(){
        return sessionId->
                MessageWindowChatMemory.builder()
                        .id(sessionId)
                        .maxMessages(10)
                        .chatMemoryStore(redisChatMemoryStore)
                        .build();
    }
}


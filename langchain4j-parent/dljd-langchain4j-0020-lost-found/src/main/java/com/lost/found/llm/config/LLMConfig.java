package com.lost.found.llm.config;

import com.lost.found.llm.store.RedisChatMemoryStore;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LLMConfig {

    @Autowired
    private RedisChatMemoryStore redisChatMemoryStore;

    @Bean("redisWindowChatMemoryProvider")
    public ChatMemoryProvider redisWindowChatMemoryProvider(){
        return chatId-> MessageWindowChatMemory.builder()
                .id(chatId)
                .maxMessages(28)
                .chatMemoryStore(redisChatMemoryStore)
                // 系统提示词 位于第一位
                .alwaysKeepSystemMessageFirst(true)
                .build();
    }
}

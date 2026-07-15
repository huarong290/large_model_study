package com.ai.study.service;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;
import dev.langchain4j.service.spring.AiServiceWiringMode;

@AiService(wiringMode = AiServiceWiringMode.EXPLICIT,
        chatModel = "openAiChatModel",
//        chatMemoryProvider = "mysqlChatMemoryProvider"
        chatMemoryProvider= "redisChatMemoryProvider"
)
public interface OpenAiAssistant {

    String ask(@MemoryId String sessionId, @UserMessage String userMessage);
}

package com.lost.found.llm.aiservice;

import com.lost.found.dto.FoundRegisterDTO;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;
import dev.langchain4j.service.spring.AiServiceWiringMode;

/**
 * 招领服务：招领管理角色
 */
@AiService(wiringMode = AiServiceWiringMode.EXPLICIT,
        chatModel = "openAiChatModel",
        chatMemoryProvider= "redisWindowChatMemoryProvider"

)
public interface FoundAssistant {

    @SystemMessage(fromResource = "/prompt/sys/FoundRegister.md")
    FoundRegisterDTO foundRegister(@MemoryId String sessionId, @UserMessage String userMessage);
}

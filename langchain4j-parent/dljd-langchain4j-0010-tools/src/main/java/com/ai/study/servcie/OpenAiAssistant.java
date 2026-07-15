package com.ai.study.servcie;

import com.ai.study.dto.UserInfoDTO;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;
import dev.langchain4j.service.spring.AiServiceWiringMode;

@AiService(wiringMode = AiServiceWiringMode.EXPLICIT,
        chatModel = "openAiChatModel",
        tools = {"calculateTools"},
        chatMemoryProvider = "chatMemoryProvider"
)
public interface OpenAiAssistant {

    String ask(String question);

    String chat(@MemoryId String chatId,@UserMessage String question);

    UserInfoDTO query(String info);
}

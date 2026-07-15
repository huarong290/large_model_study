package com.lost.found.llm.aiservice;

import com.lost.found.dto.IntentionDTO;
import com.lost.found.dto.LostRegisterDTO;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;
import dev.langchain4j.service.spring.AiServiceWiringMode;

/**
 * 失物服务：失物管理角色
 */
@AiService(wiringMode = AiServiceWiringMode.EXPLICIT,
        chatModel = "openAiChatModel",
        chatMemoryProvider= "redisWindowChatMemoryProvider",
        tools = {"lostFoundTools"}
)
public interface LostAssistant {
    /**
     * 意图分析、失物登记、招领失物
     * @param sessionId 会话ID
     * @param userMessage 用户信息
     * @return 返回值
     */
    @SystemMessage(fromResource = "/prompt/sys/LostRegister.md")
    IntentionDTO intention(@MemoryId String sessionId, @UserMessage String userMessage);

    /**
     * 失物登记
     * @param sessionId sessionId 会话ID
     * @param userMessage userMessage 用户信息
     * @return 返回值
     */
    @SystemMessage(fromResource = "/prompt/sys/LostRegister.md")
    LostRegisterDTO lostRegister(@MemoryId String sessionId, @UserMessage String userMessage);

    /**
     * 失物查询功能
     * @param sessionId sessionId 会话ID
     * @param userMessage userMessage 用户信息
     * @return 返回值
     */
    @SystemMessage(fromResource = "/prompt/sys/LostMatch.md")
    String lostQuery(@MemoryId String sessionId, @UserMessage String userMessage);
}

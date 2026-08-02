package com.ai.study.service.impl;

import com.ai.study.manager.ChatMemoryManager;
import com.ai.study.service.MemoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 对话记忆服务实现类
 *
 * <p>核心机制：</p>
 * <ul>
 *   <li>使用 MessageChatMemoryAdvisor 自动管理对话历史</li>
 *   <li>使用滑动窗口控制消息数量（避免内存溢出）</li>
 *   <li>每个会话独立存储，互不影响</li>
 * </ul>
 *
 * @author AI Study
 * @since 1.0.0
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class MemoryServiceImpl implements MemoryService {

    /**
     * Ollama 本地模型客户端
     */
    @Qualifier("ollamaChatClient")
    private final ChatClient ollamaChatClient;

    /**
     * 对话记忆管理器
     */
    private final ChatMemoryManager memoryManager;

    /**
     * 每个会话保留的最大消息数（从配置文件读取）
     */
    @Value("${memory.max-messages:10}")
    private int maxMessages;

    @Override
    public String chatWithMemory(String sessionId, String message) {
        log.info("💬 [会话: {}] 用户消息：{}", sessionId, message);

        // 1. 获取或创建会话记忆（使用滑动窗口实现）
        ChatMemory chatMemory = memoryManager.getOrCreateMemory(sessionId);

        // 2. 创建带记忆的 Advisor
        // MessageChatMemoryAdvisor 会自动处理：
        // - 每次请求前加载历史消息
        // - 每次响应后保存新消息
        // - 控制消息数量（滑动窗口）
        //  使用 Builder 创建 MessageChatMemoryAdvisor
        // 注意：builder() 方法需要传入 ChatMemory 参数
        MessageChatMemoryAdvisor memoryAdvisor = MessageChatMemoryAdvisor.builder(chatMemory)
                .conversationId(sessionId)
                .build();


        // 3. 创建带记忆的 ChatClient
        // mutate() 方法会复制当前 ChatClient 并添加新的 Advisor
        ChatClient clientWithMemory = ollamaChatClient.mutate()
                .defaultAdvisors(memoryAdvisor)
                .build();

        // 4. 执行对话
        String response = clientWithMemory.prompt()
                .user(message)
                .call()
                .content();

        log.info("💬 [会话: {}] AI 响应：{}", sessionId,
                response.length() > 50 ? response.substring(0, 50) + "..." : response);

        return response;
    }

    @Override
    public String chatWithoutMemory(String message) {
        log.info("💬 [无记忆] 用户消息：{}", message);

        // 直接使用原始的 ChatClient（没有添加任何 Advisor）
        return ollamaChatClient.prompt()
                .user(message)
                .call()
                .content();
    }

    @Override
    public List<String> batchChatWithMemory(String sessionId, List<String> messages) {
        log.info("📦 [会话: {}] 批量对话，消息数量：{}", sessionId, messages.size());

        List<String> responses = new ArrayList<>();

        for (String message : messages) {
            String response = chatWithMemory(sessionId, message);
            responses.add(response);
        }

        log.info("📦 [会话: {}] 批量对话完成，共 {} 条响应", sessionId, responses.size());
        return responses;
    }

    @Override
    public List<Message> getConversationHistory(String sessionId) {
        log.info("📜 [会话: {}] 获取对话历史", sessionId);

        ChatMemory chatMemory = memoryManager.getMemory(sessionId);
        if (chatMemory == null) {
            log.info("📜 [会话: {}] 没有历史记录", sessionId);
            return new ArrayList<>();
        }

        return chatMemory.get(sessionId);
    }

    @Override
    public void clearMemory(String sessionId) {
        memoryManager.clearMemory(sessionId);
    }

    @Override
    public void clearAllMemory() {
        memoryManager.clearAllMemory();
    }

    @Override
    public int getActiveSessionCount() {
        return memoryManager.getActiveSessionCount();
    }

    @Override
    public boolean hasSession(String sessionId) {
        return memoryManager.hasSession(sessionId);
    }

    @Override
    public Map<String, Object> getSessionStatus() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("activeSessions", memoryManager.getActiveSessionCount());
        stats.put("maxMessagesPerSession", maxMessages);
        return stats;
    }
}
package com.ai.study.manager;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 对话记忆管理器
 * 功能说明：
 * 管理多个会话的 ChatMemory 实例
 * 每个会话通过 sessionId 进行隔离
 * 使用滑动窗口（MessageWindowChatMemory）自动淘汰旧消息
 * 记录会话的最后访问时间，支持超时清理
 * =
 * MessageWindowChatMemory 使用说明：
 * 需要 ChatMemoryRepository 来持久化消息
 * 使用 InMemoryChatMemoryRepository 作为内存存储
 * maxMessages 控制保留的最大消息数
 * SystemMessage 会被特殊保留（不会被淘汰）
 *
 *
 * @author AI Study
 * @since 1.0.0
 */
@Component
@Slf4j
public class ChatMemoryManager {

    /**
     * 每个会话保留的最大消息数
     * 超过此数量时，最旧的非 SystemMessage 消息会被淘汰
     */
    @Value("${memory.max-messages:10}")
    private int maxMessages;

    /**
     * 会话记忆存储 Map
     * key: sessionId -> value: ChatMemory
     *
     * 每个会话都有自己的 ChatMemory 实例，互不影响
     */
    private final Map<String, ChatMemory> memoryMap = new ConcurrentHashMap<>();

    /**
     * 会话最后访问时间 Map
     * key: sessionId -> value: 最后访问时间戳（毫秒）
     */
    private final Map<String, Long> lastAccessTime = new ConcurrentHashMap<>();

    /**
     * 获取或创建会话记忆
     *
     * <p>使用 MessageWindowChatMemory.Builder 创建实例：</p>
     * <ul>
     *   <li>chatMemoryRepository: InMemoryChatMemoryRepository（内存存储）</li>
     *   <li>maxMessages: 保留的最大消息数（从配置文件读取）</li>
     * </ul>
     *
     * @param sessionId 会话唯一标识
     * @return ChatMemory 实例
     */
    public ChatMemory getOrCreateMemory(String sessionId) {
        // 更新最后访问时间
        lastAccessTime.put(sessionId, System.currentTimeMillis());

        return memoryMap.computeIfAbsent(sessionId, id -> {
            log.info("🆕 创建新的会话记忆 [{}]，窗口大小：{}", id, maxMessages);

            // 使用 Builder，需要传入 ChatMemoryRepository
            return MessageWindowChatMemory.builder()
                    .chatMemoryRepository(new InMemoryChatMemoryRepository())
                    .maxMessages(maxMessages)
                    .build();
        });
    }

    /**
     * 获取会话记忆（不创建）
     *
     * @param sessionId 会话唯一标识
     * @return ChatMemory 实例，如果不存在则返回 null
     */
    public ChatMemory getMemory(String sessionId) {
        return memoryMap.get(sessionId);
    }

    /**
     * 清除指定会话的记忆
     *
     * @param sessionId 会话唯一标识
     */
    public void clearMemory(String sessionId) {
        ChatMemory removed = memoryMap.remove(sessionId);
        lastAccessTime.remove(sessionId);
        if (removed != null) {
            removed.clear(sessionId);
            log.info("🗑️ 清除会话记忆 [{}]", sessionId);
        }
    }

    /**
     * 清除所有会话的记忆
     */
    public void clearAllMemory() {
        int count = memoryMap.size();
        // 逐个清除每个会话的记忆
        for (Map.Entry<String, ChatMemory> entry : memoryMap.entrySet()) {
            entry.getValue().clear(entry.getKey());
        }
        memoryMap.clear();
        lastAccessTime.clear();
        log.info("🗑️ 清除所有会话记忆，共 {} 个会话", count);
    }

    /**
     * 获取当前活跃会话数量
     *
     * @return 活跃会话数
     */
    public int getActiveSessionCount() {
        return memoryMap.size();
    }

    /**
     * 获取会话的最后访问时间
     *
     * @param sessionId 会话唯一标识
     * @return 最后访问时间戳，如果不存在则返回 null
     */
    public Long getLastAccessTime(String sessionId) {
        return lastAccessTime.get(sessionId);
    }

    /**
     * 清理超时会话
     *
     * @param timeoutMinutes 超时时间（分钟），小于等于 0 表示不清理
     */
    public void cleanExpiredSessions(long timeoutMinutes) {
        if (timeoutMinutes <= 0) {
            return;
        }

        long now = System.currentTimeMillis();
        long timeoutMillis = timeoutMinutes * 60 * 1000;

        int expiredCount = 0;
        for (Map.Entry<String, Long> entry : lastAccessTime.entrySet()) {
            if (now - entry.getValue() > timeoutMillis) {
                String sessionId = entry.getKey();
                ChatMemory memory = memoryMap.get(sessionId);
                if (memory != null) {
                    memory.clear(sessionId);
                }
                memoryMap.remove(sessionId);
                expiredCount++;
            }
        }

        // 清理 lastAccessTime 中已删除的条目
        lastAccessTime.entrySet().removeIf(entry -> !memoryMap.containsKey(entry.getKey()));

        if (expiredCount > 0) {
            log.info("🧹 清理了 {} 个超时会话", expiredCount);
        }
    }

    /**
     * 检查指定会话是否存在
     *
     * @param sessionId 会话唯一标识
     * @return true 表示存在，false 表示不存在
     */
    public boolean hasSession(String sessionId) {
        return memoryMap.containsKey(sessionId);
    }
}
package com.ai.study.service;

import java.util.List;
import java.util.Map;

/**
 * 对话记忆服务接口
 * 功能说明：
 *   提供带记忆的对话能力（多轮对话保持上下文）
 *   提供无记忆的对话能力（用于对比测试）
 *   支持会话隔离（不同 sessionId 独立记忆）
 *   支持记忆清理和管理
 * </ul>
 *
 * @author AI Study
 * @since 1.0.0
 */
public interface MemoryService {

    /**
     * 带记忆的对话
     *
     * <p>使用 MessageChatMemoryAdvisor 自动管理对话历史：</p>
     * <ul>
     *   <li>自动保存用户消息和 AI 响应</li>
     *   <li>自动加载历史消息作为上下文</li>
     *   <li>支持滑动窗口控制消息数量</li>
     * </ul>
     *
     * @param sessionId 会话唯一标识（相同 ID 共享记忆）
     * @param message 用户消息
     * @return AI 响应内容
     */
    String chatWithMemory(String sessionId, String message);

    /**
     * 无记忆的对话（对比测试）
     *
     * <p>每次调用都是独立的，不保留任何上下文。</p>
     * <p>用于和带记忆的对话进行对比，展示记忆功能的价值。</p>
     *
     * @param message 用户消息
     * @return AI 响应内容
     */
    String chatWithoutMemory(String message);

    /**
     * 批量带记忆对话（多轮对话）
     *
     * <p>一次性发送多条消息，保持上下文连续性。</p>
     *
     * @param sessionId 会话唯一标识
     * @param messages 消息列表（按顺序发送）
     * @return AI 响应列表（每条消息对应一个响应）
     */
    List<String> batchChatWithMemory(String sessionId, List<String> messages);

    /**
     * 获取会话的对话历史
     *
     * @param sessionId 会话唯一标识
     * @return 历史消息列表
     */
    List<org.springframework.ai.chat.messages.Message> getConversationHistory(String sessionId);

    /**
     * 清除指定会话的记忆
     *
     * @param sessionId 会话唯一标识
     */
    void clearMemory(String sessionId);

    /**
     * 清除所有会话的记忆
     */
    void clearAllMemory();

    /**
     * 获取当前活跃会话数量
     *
     * @return 活跃会话数
     */
    int getActiveSessionCount();

    /**
     * 检查会话是否存在
     *
     * @param sessionId 会话唯一标识
     * @return true 表示存在，false 表示不存在
     */
    boolean hasSession(String sessionId);

    /**
     * 获取所有会话统计信息
     *
     * @return 会话统计信息 Map
     */
    Map<String, Object> getSessionStatus();
}
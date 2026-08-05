package com.ai.study.service;



import com.ai.study.model.ChatRequest;
import com.ai.study.model.ChatResponse;

import reactor.core.publisher.Flux;

import java.util.Map;

/**
 * 练习服务接口
 *
 * 定义聊天、流式对话、记忆管理和统计信息相关的操作。
 */
public interface PracticeService {

    /**
     * 普通聊天接口
     *
     * @param request 聊天请求参数
     * @return 聊天响应结果
     */
    ChatResponse chat(ChatRequest request);

    /**
     * 流式聊天接口
     *
     * @param request 聊天请求参数
     * @return Flux 流式输出的回答内容
     */
    Flux<String> chatStream(ChatRequest request);

    /**
     * 清除指定会话的记忆
     *
     * @param sessionId 会话唯一标识
     */
    void clearMemory(String sessionId);

    /**
     * 获取服务统计信息
     *
     * @return 包含统计数据的 Map
     */
    Map<String, Object> getStats();
}


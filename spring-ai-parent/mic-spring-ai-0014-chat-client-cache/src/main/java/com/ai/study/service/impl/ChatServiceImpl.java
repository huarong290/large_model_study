package com.ai.study.service.impl;

import com.ai.study.config.MultiLevelCacheConfig;
import com.ai.study.service.ChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ChatServiceImpl implements ChatService {

    private final ChatClient deepSeekChatClient;

    public ChatServiceImpl(@Qualifier("deepSeekChatClient") ChatClient deepSeekChatClient) {
        this.deepSeekChatClient = deepSeekChatClient;
    }

    /**
     * 🚀 多级缓存执行流程：
     * 1. @Cacheable 先看 Caffeine 是否包含 key=#message，有则直接返回。
     * 2. 未命中，再看 Redis 是否包含 key，有则返回并将结果回填到 Caffeine。
     * 3. 全部未命中，执行下方方法体调用 DeepSeek。
     * 4. DeepSeek 返回后，自动将结果同时写入 Caffeine 和 Redis。
     */
    @Override
    @Cacheable(value = MultiLevelCacheConfig.AI_RESPONSE_CACHE, key = "#message")
    public String chat(String message) {
        log.info("⏳【完全未命中】正在调用 DeepSeek 大模型 (耗时约 2~3秒)...");

        // 模拟网络延迟，方便你在控制台观察缓存效果
        try { Thread.sleep(2000); } catch (InterruptedException e) {}

        return deepSeekChatClient.prompt()
                .user(message)
                .call()
                .content();
    }
}

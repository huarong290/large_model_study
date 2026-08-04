package com.ai.study.service.impl;

import com.ai.study.exception.SecurityException;
import com.ai.study.service.SecurityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 安全对话服务实现类
 *
 * @author AI Study
 * @since 1.0.0
 */
@Service
@Slf4j
public class SecurityServiceImpl implements SecurityService {

    private final ChatClient chatClient;
    private final Set<String> sensitiveWordSet;

    /**
     * 请求数与拦截数统计
     */
    private final AtomicLong totalRequests = new AtomicLong(0);
    private final AtomicLong blockedRequests = new AtomicLong(0);

    public SecurityServiceImpl(@Qualifier("ollamaChatClient") ChatClient chatClient,
                               Set<String> sensitiveWordSet) {
        this.chatClient = chatClient;
        this.sensitiveWordSet = sensitiveWordSet;
    }

    @Override
    public String chatWithSecurity(String message) {
        totalRequests.incrementAndGet();

        // 1. 输入文本敏感词校验
        checkSensitiveWords(message);

        // 2. 调用大模型生成文本
        String response = chatClient.prompt()
                .user(message)
                .call()
                .content();

        // 3. 输出文本敏感词校验（防止模型生成违规内容）
        checkSensitiveWords(response);

        return response;
    }

    @Override
    public String chatWithoutSecurity(String message) {
        return chatClient.prompt()
                .user(message)
                .call()
                .content();
    }

    /**
     * 敏感词扫描核心逻辑
     *
     * @param text 待检测文本
     */
    private void checkSensitiveWords(String text) {
        if (text == null || text.isBlank()) {
            return;
        }
        for (String word : sensitiveWordSet) {
            if (text.contains(word)) {
                blockedRequests.incrementAndGet();
                log.warn("⚠️ 检测到敏感词，已触发安全拦截：{}", word);
                throw SecurityException.sensitiveWordDetected(word);
            }
        }
    }

    @Override
    public Map<String, Object> getSecurityStats() {
        return Map.of(
                "totalRequests", totalRequests.get(),
                "blockedRequests", blockedRequests.get(),
                "sensitiveWordsCount", sensitiveWordSet.size()
        );
    }
}
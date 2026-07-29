package com.ai.study.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.Duration;

/**
 * 流式响应服务
 *
 * @author AI Study
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class StreamService {

    @Qualifier("ollamaChatClient")
    private final ChatClient ollamaClient;

    /**
     * 流式响应
     */
    public Flux<String> streamChat(String message) {
        return ollamaClient.prompt()
                .user(message)
                .stream()
                .content()
                .doOnSubscribe(subscription -> log.info("🌊 开始流式响应"))
                .doOnNext(chunk -> log.debug("📦 发送块：{}", chunk.length()))
                .doOnComplete(() -> log.info("✅ 流式响应完成"))
                .doOnError(error -> log.error("❌ 流式响应错误：{}", error.getMessage()));
    }

    /**
     * 流式响应 - 带 System 消息
     */
    public Flux<String> streamChatWithSystem(String message, String system) {
        return ollamaClient.prompt()
                .system(system)
                .user(message)
                .stream()
                .content();
    }

    /**
     * 流式响应 - 带超时
     */
    public Flux<String> streamChatWithTimeout(String message, int timeoutSeconds) {
        return ollamaClient.prompt()
                .user(message)
                .stream()
                .content()
                .timeout(Duration.ofSeconds(timeoutSeconds));
    }

    /**
     * 同步响应（对比用）
     */
    public String syncChat(String message) {
        return ollamaClient.prompt()
                .user(message)
                .call()
                .content();
    }
}
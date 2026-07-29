package com.ai.study.controller;

import com.ai.study.service.StreamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * 流式响应 Controller
 *
 * 功能说明：
 * 1. 流式响应（SSE）
 * 2. 同步响应对比
 * 3. 流式 + 超时控制
 *
 * @author AI Study
 */
@RestController
@RequestMapping("/api/stream")
@Slf4j
@RequiredArgsConstructor
public class StreamController {

    private final StreamService streamService;

    @Qualifier("ollamaChatClient")
    private final ChatClient ollamaClient;

    // ================================================================
    // 1. 流式响应（SSE）
    // ================================================================

    /**
     * 流式响应 - 逐字返回
     *
     * 测试方式：GET /api/stream/chat?message=讲个故事
     *
     * 特点：
     * - 使用 text/event-stream 格式
     * - 实时逐字/逐块返回
     * - 用户体验更好
     */
    @GetMapping(value = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamChat(
            @RequestParam(name = "message", defaultValue = "请用中文讲一个简短的故事") String message) {

        log.info("🌊 [流式响应] 收到请求：{}", message);
        long startTime = System.currentTimeMillis();

        return ollamaClient.prompt()
                .user(message)
                .stream()
                .content()
                .doOnNext(chunk -> log.debug("📦 发送数据块：{}", chunk.length()))
                .doOnComplete(() -> {
                    long duration = System.currentTimeMillis() - startTime;
                    log.info("✅ [流式响应完成] 耗时：{}ms", duration);
                })
                .doOnError(error -> {
                    log.error("❌ [流式响应错误]：{}", error.getMessage());
                });
    }

    /**
     * 流式响应 - 带超时控制
     *
     * 测试方式：GET /api/stream/chat-timeout?message=讲个故事&timeout=10
     */
    @GetMapping(value = "/chat-timeout", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamChatWithTimeout(
            @RequestParam(name = "message", defaultValue = "讲个故事") String message,
            @RequestParam(name = "timeout", defaultValue = "30") int timeoutSeconds) {

        log.info("🌊 [流式响应-超时] 消息：{}，超时：{}s", message, timeoutSeconds);

        return ollamaClient.prompt()
                .user(message)
                .stream()
                .content()
                .timeout(Duration.ofSeconds(timeoutSeconds))
                .doOnError(error -> log.error("⏰ 流式响应超时：{}", error.getMessage()));
    }

    /**
     * 流式响应 - 带自定义 System 消息
     *
     * 测试方式：GET /api/stream/with-system?message=你好&system=你是一个编程专家
     */
    @GetMapping(value = "/with-system", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamChatWithSystem(
            @RequestParam(name = "message", defaultValue = "你好") String message,
            @RequestParam(name = "system", defaultValue = "你是一个友好的AI助手") String system) {

        log.info("🌊 [流式响应-System] System：{}，Message：{}", system, message);

        return ollamaClient.prompt()
                .system(system)
                .user(message)
                .stream()
                .content()
                .doOnComplete(() -> log.info("✅ 流式响应完成"));
    }

    // ================================================================
    // 2. 同步响应对比
    // ================================================================

    /**
     * 同步响应（对比测试）
     *
     * 测试方式：GET /api/stream/sync?message=讲个故事
     *
     * 特点：
     * - 等待完整结果后一次性返回
     * - 需要等待所有内容生成完毕
     * - 用户等待时间长
     */
    @GetMapping("/sync")
    public Map<String, Object> syncChat(
            @RequestParam(name = "message", defaultValue = "讲个故事") String message) {

        log.info("⏳ [同步响应] 收到请求：{}", message);
        long startTime = System.currentTimeMillis();

        String response = ollamaClient.prompt()
                .user(message)
                .call()
                .content();

        long duration = System.currentTimeMillis() - startTime;
        log.info("✅ [同步响应完成] 耗时：{}ms", duration);

        Map<String, Object> result = new HashMap<>();
        result.put("response", response);
        result.put("responseTime", duration + "ms");
        result.put("length", response.length());
        result.put("type", "sync");
        return result;
    }

    /**
     * 流式 vs 同步对比（同时返回两种方式）
     *
     * 测试方式：GET /api/stream/compare?message=讲个故事
     */
    @GetMapping("/compare")
    public Map<String, Object> compare(
            @RequestParam(name = "message", defaultValue = "讲个故事") String message) {

        log.info("📊 [对比测试] 消息：{}", message);

        // 同步测试
        long syncStart = System.currentTimeMillis();
        String syncResponse = ollamaClient.prompt()
                .user(message)
                .call()
                .content();
        long syncDuration = System.currentTimeMillis() - syncStart;

        Map<String, Object> result = new HashMap<>();
        result.put("sync", Map.of(
                "response", syncResponse,
                "time", syncDuration + "ms",
                "length", syncResponse.length()
        ));
        result.put("stream", Map.of(
                "note", "流式响应请访问 /api/stream/chat?message=" + message,
                "advantage", "逐字返回，用户体验更好"
        ));
        result.put("conclusion", "同步响应需要等待完整结果，流式响应可以实时看到生成过程");

        return result;
    }

    // ================================================================
    // 3. 高级流式功能
    // ================================================================

    /**
     * 流式响应 - 带进度指示
     *
     * 测试方式：GET /api/stream/with-progress?message=写一篇关于人工智能的文章
     *
     * 返回格式：{"type":"progress","data":"..."} 或 {"type":"content","data":"..."}
     */
    @GetMapping(value = "/with-progress", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamWithProgress(
            @RequestParam(name = "message", defaultValue = "写一篇关于人工智能的短文") String message) {

        log.info("🌊 [流式响应-进度] 消息：{}", message);

        // 先发送进度信息，再发送内容
        return Flux.concat(
                // 发送开始进度
                Flux.just("data: {\"type\":\"progress\",\"data\":\"开始生成...\"}\n\n"),
                // 发送内容
                ollamaClient.prompt()
                        .user(message)
                        .stream()
                        .content()
                        .map(chunk -> "data: {\"type\":\"content\",\"data\":\"" + chunk + "\"}\n\n"),
                // 发送完成进度
                Flux.just("data: {\"type\":\"progress\",\"data\":\"生成完成!\"}\n\n")
        );
    }

    /**
     * 流式响应 - 带统计信息
     *
     * 返回格式：在最后返回统计信息
     */
    @GetMapping(value = "/with-stats", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamWithStats(
            @RequestParam(name = "message", defaultValue = "讲个故事") String message) {

        log.info("🌊 [流式响应-统计] 消息：{}", message);
        long startTime = System.currentTimeMillis();

        final int[] chunkCount = {0};
        final int[] totalLength = {0};

        return ollamaClient.prompt()
                .user(message)
                .stream()
                .content()
                .doOnNext(chunk -> {
                    chunkCount[0]++;
                    totalLength[0] += chunk.length();
                })
                .map(chunk -> "data: " + chunk + "\n\n")
                .concatWith(Flux.just(
                        "data: \n\n",
                        "data: --- 统计信息 ---\n\n",
                        "data: 总块数: " + chunkCount[0] + "\n\n",
                        "data: 总字数: " + totalLength[0] + "\n\n",
                        "data: 耗时: " + (System.currentTimeMillis() - startTime) + "ms\n\n"
                ));
    }
}
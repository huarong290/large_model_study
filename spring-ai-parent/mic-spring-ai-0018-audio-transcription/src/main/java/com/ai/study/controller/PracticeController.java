package com.ai.study.controller;

import com.ai.study.model.ChatRequest;
import com.ai.study.model.ChatResponse;
import com.ai.study.service.PracticeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.Map;

/**
 * 智能客服 Controller
 *
 * <p>提供 REST API 接口，支持同步和流式对话。</p>
 *
 * @author AI Study
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/practice")
@Slf4j
@RequiredArgsConstructor
public class PracticeController {

    private final PracticeService practiceService;

    // ================================================================
    // 1. 同步对话
    // ================================================================

    /**
     * 同步对话
     *
     * <p>等待 AI 完整响应后一次性返回。</p>
     *
     * @param request 聊天请求
     * @return 聊天响应
     */
    @PostMapping("/chat")
    public ChatResponse chat(@RequestBody ChatRequest request) {
        log.info("📝 收到同步对话请求");
        return practiceService.chat(request);
    }

    // ================================================================
    // 2. 流式对话
    // ================================================================

    /**
     * 流式对话
     *
     * <p>逐字返回 AI 响应，提供更好的用户体验。</p>
     *
     * @param sessionId 会话 ID
     * @param message 用户消息
     * @return SSE 流式响应
     */
    @GetMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> chatStream(
            @RequestParam String sessionId,
            @RequestParam String message) {
        log.info("📝 收到流式对话请求：sessionId={}", sessionId);
        ChatRequest request = new ChatRequest();
        request.setSessionId(sessionId);
        request.setMessage(message);
        return practiceService.chatStream(request);
    }

    // ================================================================
    // 3. 记忆管理
    // ================================================================

    /**
     * 清除会话记忆
     *
     * @param sessionId 会话 ID
     * @return 操作结果
     */
    @DeleteMapping("/memory/{sessionId}")
    public Map<String, Object> clearMemory(@PathVariable String sessionId) {
        log.info("🗑️ 清除会话记忆：{}", sessionId);
        practiceService.clearMemory(sessionId);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "会话记忆已清除：" + sessionId);
        return result;
    }

    // ================================================================
    // 4. 系统统计
    // ================================================================

    /**
     * 获取系统统计信息
     *
     * @return 统计信息
     */
    @GetMapping("/stats")
    public Map<String, Object> getStats() {
        return practiceService.getStats();
    }

    // ================================================================
    // 5. 健康检查
    // ================================================================

    /**
     * 健康检查
     *
     * @return 服务状态
     */
    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> status = new HashMap<>();
        status.put("status", "UP");
        status.put("service", "智能客服系统");
        status.put("version", "1.0.0");
        status.put("timestamp", System.currentTimeMillis());
        return status;
    }
}
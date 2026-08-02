package com.ai.study.controller;

import com.ai.study.service.MemoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 对话记忆 Controller
 *
 * <p>功能说明：</p>
 * <ul>
 *   <li>提供带记忆的对话接口（多轮对话保持上下文）</li>
 *   <li>提供无记忆的对话接口（用于对比测试）</li>
 *   <li>提供批量对话接口</li>
 *   <li>提供会话管理接口（清除记忆、统计信息等）</li>
 *   <li>提供对比演示接口（直观展示记忆的价值）</li>
 * </ul>
 *
 * @author AI Study
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/memory")
@Slf4j
@RequiredArgsConstructor
public class MemoryController {

    /**
     * 注入接口，而非具体实现类（面向接口编程）
     */
    private final MemoryService memoryService;

    // ================================================================
    // 1. 带记忆的对话
    // ================================================================

    /**
     * 带记忆的对话
     *
     * @param sessionId 会话 ID（相同 ID 共享记忆）
     * @param message 用户消息
     * @return AI 响应
     */
    @GetMapping("/chat")
    public Map<String, Object> chatWithMemory(
            @RequestParam("sessionId") String sessionId,
            @RequestParam("message") String message) {

        log.info("📝 [会话: {}] 收到消息：{}", sessionId, message);

        try {
            String response = memoryService.chatWithMemory(sessionId, message);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("sessionId", sessionId);
            result.put("message", message);
            result.put("response", response);
            result.put("hasMemory", true);
            result.put("tip", "同一 sessionId 的对话会保持记忆");
            return result;

        } catch (Exception e) {
            log.error("❌ 对话失败：{}", e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            return error;
        }
    }

    // ================================================================
    // 2. 无记忆的对话
    // ================================================================

    /**
     * 无记忆的对话（对比测试）
     */
    @GetMapping("/no-memory")
    public Map<String, Object> chatWithoutMemory(
            @RequestParam("message") String message) {

        log.info("📝 [无记忆] 收到消息：{}", message);

        try {
            String response = memoryService.chatWithoutMemory(message);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", message);
            result.put("response", response);
            result.put("hasMemory", false);
            result.put("tip", "每次调用都是独立的，不保留上下文");
            return result;

        } catch (Exception e) {
            log.error("❌ 对话失败：{}", e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            return error;
        }
    }

    // ================================================================
    // 3. 批量对话
    // ================================================================

    /**
     * 批量对话（多轮对话）
     *
     * @param sessionId 会话 ID
     * @param messages 消息列表（JSON 数组）
     * @return 响应列表
     */
    @PostMapping("/batch")
    public Map<String, Object> batchChat(
            @RequestParam("sessionId") String sessionId,
            @RequestBody List<String> messages) {

        log.info("📦 [会话: {}] 批量对话，消息数量：{}", sessionId, messages.size());

        try {
            List<String> responses = memoryService.batchChatWithMemory(sessionId, messages);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("sessionId", sessionId);
            result.put("messages", messages);
            result.put("responses", responses);
            result.put("count", responses.size());
            return result;

        } catch (Exception e) {
            log.error("❌ 批量对话失败：{}", e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            return error;
        }
    }

    // ================================================================
    // 4. 会话管理
    // ================================================================

    /**
     * 获取会话历史
     */
    @GetMapping("/history")
    public Map<String, Object> getHistory(
            @RequestParam("sessionId") String sessionId) {

        log.info("📜 [会话: {}] 获取历史记录", sessionId);

        try {
            var history = memoryService.getConversationHistory(sessionId);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("sessionId", sessionId);
            result.put("history", history);
            result.put("count", history.size());
            return result;

        } catch (Exception e) {
            log.error("❌ 获取历史失败：{}", e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            return error;
        }
    }

    /**
     * 清除指定会话的记忆
     */
    @DeleteMapping("/clear")
    public Map<String, Object> clearMemory(
            @RequestParam("sessionId") String sessionId) {

        log.info("🗑️ 清除会话记忆：{}", sessionId);

        memoryService.clearMemory(sessionId);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("sessionId", sessionId);
        result.put("message", "会话记忆已清除");
        return result;
    }

    /**
     * 清除所有会话的记忆
     */
    @DeleteMapping("/clear-all")
    public Map<String, Object> clearAllMemory() {

        log.info("🗑️ 清除所有会话记忆");

        memoryService.clearAllMemory();

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "所有会话记忆已清除");
        return result;
    }

    // ================================================================
    // 5. 统计信息
    // ================================================================

    /**
     * 获取会话统计信息
     */
    @GetMapping("/status")
    public Map<String, Object> getStatus() {

        Map<String, Object> stats = memoryService.getSessionStatus();
        stats.put("status", "UP");
        stats.put("tip", "活跃会话数：当前正在维护记忆的会话数量");
        return stats;
    }

    // ================================================================
    // 6. 对比演示
    // ================================================================

    /**
     * 对比测试：有记忆 vs 无记忆
     */
    @GetMapping("/compare")
    public Map<String, Object> compare(
            @RequestParam("sessionId") String sessionId,
            @RequestParam("name") String name,
            @RequestParam(value = "question", defaultValue = "我叫什么名字？") String question) {

        log.info("📊 对比测试：sessionId={}, name={}, question={}", sessionId, name, question);

        Map<String, Object> result = new HashMap<>();

        // 1. 先告诉 AI 名字（带记忆）
        String tellResult = memoryService.chatWithMemory(sessionId, "我叫" + name);
        log.info("✅ 已告诉 AI：我叫{}", name);

        // 2. 有记忆的问
        String withMemory = memoryService.chatWithMemory(sessionId, question);
        log.info("✅ 有记忆的响应：{}", withMemory);

        // 3. 清除记忆
        memoryService.clearMemory(sessionId);
        log.info("🗑️ 已清除记忆");

        // 4. 无记忆的问
        String withoutMemory = memoryService.chatWithoutMemory(question);
        log.info("❌ 无记忆的响应：{}", withoutMemory);

        result.put("step1", "已告诉 AI 名字：" + name);
        result.put("step2", "有记忆的响应：" + withMemory);
        result.put("step3", "已清除记忆");
        result.put("step4", "无记忆的响应：" + withoutMemory);
        result.put("conclusion", "有记忆的 AI 记得用户名字，无记忆的 AI 不记得");
        result.put("tip", "记忆功能让 AI 能够保持对话上下文，实现真正的多轮对话");

        return result;
    }
}
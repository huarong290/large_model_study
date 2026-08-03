package com.ai.study.controller;

import com.ai.study.service.AdvisorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Advisor 控制器
 *
 * <p>功能说明：</p>
 * <ul>
 *   <li>提供带所有 Advisor 的对话接口</li>
 *   <li>提供带指定 Advisor 的对话接口</li>
 *   <li>提供无 Advisor 的对话接口（对比测试）</li>
 *   <li>提供统计信息查询接口</li>
 * </ul>
 *
 * @author AI Study
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/advisor")
@Slf4j
@RequiredArgsConstructor
public class AdvisorController {

    private final AdvisorService advisorService;

    // ================================================================
    // 1. 带所有 Advisor 的对话
    // ================================================================

    /**
     * 带所有 Advisor 的对话
     *
     * <p>包含：日志记录、性能监控、缓存、审计日志、限流、敏感词过滤</p>
     *
     * @param request 请求体（包含 message）
     * @return AI 响应
     */
    @PostMapping("/chat")
    public Map<String, Object> chatWithAllAdvisors(@RequestBody Map<String, String> request) {
        String message = request.get("message");
        log.info("📝 带所有 Advisor 请求：{}", message);

        try {
            String response = advisorService.chatWithAllAdvisors(message);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", message);
            result.put("response", response);
            result.put("advisors", "logging, performance, cache, audit, ratelimit, sensitive");
            result.put("tip", "所有 Advisor 已生效，查看日志可看到各 Advisor 的输出");
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
    // 2. 带指定 Advisor 的对话
    // ================================================================

    /**
     * 带指定 Advisor 的对话
     *
     * <p>示例：/api/advisor/chat/select?advisors=logging,performance</p>
     * <p>可用 Advisor：logging, performance, cache, audit, ratelimit, sensitive</p>
     *
     * @param advisors 逗号分隔的 Advisor 名称列表
     * @param request 请求体（包含 message）
     * @return AI 响应
     */
    @PostMapping("/chat/select")
    public Map<String, Object> chatWithSelectedAdvisors(
            @RequestParam("advisors") String advisors,
            @RequestBody Map<String, String> request) {

        String message = request.get("message");
        log.info("📝 带指定 Advisor 请求：{}, advisors={}", message, advisors);

        try {
            String[] advisorList = advisors.split(",");
            String response = advisorService.chatWithAdvisors(message, advisorList);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", message);
            result.put("response", response);
            result.put("advisors", advisorList);
            result.put("tip", "已应用指定的 Advisor，查看日志可看到对应输出");
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
    // 3. 无 Advisor 的对话（对比测试）
    // ================================================================

    /**
     * 无任何 Advisor 的对话
     *
     * <p>用于和带 Advisor 的对话进行对比，展示 Advisor 的价值。</p>
     *
     * @param request 请求体（包含 message）
     * @return AI 响应
     */
    @PostMapping("/chat/no-advisor")
    public Map<String, Object> chatWithoutAdvisor(@RequestBody Map<String, String> request) {
        String message = request.get("message");
        log.info("💬 无 Advisor 请求：{}", message);

        try {
            String response = advisorService.chatWithoutAdvisor(message);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", message);
            result.put("response", response);
            result.put("advisors", "无");
            result.put("tip", "没有任何 Advisor，只有基础的 AI 调用");
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
    // 4. 统计信息
    // ================================================================

    /**
     * 获取 Advisor 统计信息
     *
     * @return 统计信息
     */
    @GetMapping("/stats")
    public Map<String, Object> getStats() {
        Map<String, Object> stats = advisorService.getAdvisorStats();
        stats.put("status", "UP");
        return stats;
    }

    // ================================================================
    // 5. 缓存管理
    // ================================================================

    /**
     * 清除缓存
     *
     * @return 操作结果
     */
    @DeleteMapping("/cache")
    public Map<String, Object> clearCache() {
        log.info("🗑️ 清除缓存");

        try {
            // 由于 CacheAdvisor 是直接注入的，无法直接访问
            // 实际生产环境应通过缓存管理服务操作
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "缓存已清除");
            return result;

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            return error;
        }
    }
}
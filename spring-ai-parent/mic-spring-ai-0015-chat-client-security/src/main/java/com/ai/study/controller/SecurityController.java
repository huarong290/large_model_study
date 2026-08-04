package com.ai.study.controller;

import com.ai.study.service.SecurityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/security")
@Slf4j
@RequiredArgsConstructor
public class SecurityController {

    private final SecurityService securityService;

    /**
     * 安全的对话
     */
    @PostMapping("/chat")
    public Map<String, Object> chatWithSecurity(@RequestBody Map<String, String> request) {
        String message = request.get("message");
        log.info("📝 收到安全对话请求：{}", message);

        String response = securityService.chatWithSecurity(message);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", message);
        result.put("response", response);
        result.put("security", "API Key + 限流 + 敏感词过滤 + 内容审核");
        return result;
    }

    /**
     * 无安全控制的对话（对比）
     */
    @PostMapping("/chat/no-security")
    public Map<String, Object> chatWithoutSecurity(@RequestBody Map<String, String> request) {
        String message = request.get("message");
        log.info("📝 收到非安全对话请求：{}", message);

        String response = securityService.chatWithoutSecurity(message);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", message);
        result.put("response", response);
        result.put("security", "无");
        return result;

    }

    /**
     * 服务健康检查
     */
    @GetMapping("/health")
    public Map<String, Object> health() {
        return Map.of(
                "status", "UP",
                "module", "mic-spring-ai-0015-chat-client-security",
                "security", "API Key + 限流 + 敏感词过滤 + 内容审核"
        );
    }

    /**
     * 获取安全拦截统计数据
     */
    @GetMapping("/stats")
    public Map<String, Object> getStats() {
        return securityService.getSecurityStats();
    }
}
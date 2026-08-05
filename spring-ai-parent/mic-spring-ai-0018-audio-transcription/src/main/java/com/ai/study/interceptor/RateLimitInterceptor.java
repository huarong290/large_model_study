package com.ai.study.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 基于 IP + API Key 的每分钟限流拦截器
 *
 * <p>功能说明：</p>
 * <ul>
 *   <li>限制每个客户端每分钟的请求数</li>
 *   <li>防止 API 滥用</li>
 *   <li>支持滑动窗口重置</li>
 * </ul>
 *
 * @author AI Study
 * @since 1.0.0
 */
@Slf4j
public class RateLimitInterceptor implements HandlerInterceptor {

    /**
     * 是否启用限流
     */
    private final boolean enabled;

    /**
     * 每分钟最大请求数
     */
    private final int maxRequestsPerMinute;

    /**
     * 客户端请求计数 Map（Key: IP:API_KEY）
     * key: 客户端标识（IP + API Key）
     * value: 请求计数信息
     */
    private final ConcurrentHashMap<String, ClientRequestInfo> requestCounts = new ConcurrentHashMap<>();

    public RateLimitInterceptor(boolean enabled, int maxRequestsPerMinute) {
        this.enabled = enabled;
        this.maxRequestsPerMinute = maxRequestsPerMinute;
        log.info("🚫 RateLimitInterceptor 初始化，启用状态：{}，最大请求数/分钟：{}", enabled, maxRequestsPerMinute);
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 如果未启用，直接放行
        if (!enabled) {
            return true;
        }

        // 放行健康检查接口
        String path = request.getRequestURI();
        if (path.contains("/health") || path.contains("/stats")) {
            return true;
        }

        // 获取客户端标识（IP + API Key）
        String clientId = getClientId(request);

        // 检查限流
        if (!checkRateLimit(clientId)) {
            log.warn("🚫 请求被限流：clientId={}", clientId);
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json;charset=UTF-8");
            try {
                response.getWriter().write("{\"error\":\"请求过于频繁，请稍后再试\"}");
            } catch (Exception e) {
                log.error("写入响应失败：{}", e.getMessage());
            }
            return false;
        }

        return true;
    }

    /**
     * 获取客户端标识
     */
    private String getClientId(HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        String apiKey = (String) request.getAttribute("apiKey");
        return ip + ":" + (apiKey != null ? apiKey : "unknown");
    }

    /**
     * 检查限流
     */
    private boolean checkRateLimit(String clientId) {
        long currentMinute = System.currentTimeMillis() / 60000;
        ClientRequestInfo info = requestCounts.computeIfAbsent(
                clientId,
                k -> new ClientRequestInfo(currentMinute, new AtomicInteger(0))
        );

        // 跨分钟重置计数
        if (info.getMinute() != currentMinute) {
            info.setMinute(currentMinute);
            info.getCount().set(0);
        }

        // 检查是否超过限制
        int count = info.getCount().incrementAndGet();
        if (count > maxRequestsPerMinute) {
            return false;
        }

        log.debug("📊 请求计数：clientId={}, count={}/{}", clientId, count, maxRequestsPerMinute);
        return true;
    }

    /**
     * 客户端请求信息
     */
    private static class ClientRequestInfo {
        private long minute;
        private final AtomicInteger count;

        public ClientRequestInfo(long minute, AtomicInteger count) {
            this.minute = minute;
            this.count = count;
        }

        public long getMinute() { return minute; }
        public void setMinute(long minute) { this.minute = minute; }
        public AtomicInteger getCount() { return count; }
    }

    /**
     * 重置限流统计
     */
    public void resetRateLimit() {
        requestCounts.clear();
        log.info("🚫 限流统计已重置");
    }
}
package com.ai.study.advisor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisor;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisorChain;
import org.springframework.core.Ordered;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 限流 Advisor
 *
 * <p>功能说明：</p>
 * <ul>
 *   <li>限制每分钟最大请求数</li>
 *   <li>超过限制时拒绝请求并抛出异常</li>
 *   <li>防止 API 滥用和资源耗尽</li>
 *   <li>支持按客户端隔离限流</li>
 * </ul>
 *
 * <p>执行顺序：{@link Ordered#HIGHEST_PRECEDENCE} + 500</p>
 * <p>注意：当前为单机版，生产环境建议使用分布式限流（如 Redis + Lua）</p>
 *
 * @author AI Study
 * @since 1.0.0
 */
@Slf4j
@Component
public class RateLimitAdvisor implements CallAdvisor, StreamAdvisor {

    /** Advisor 执行顺序 */
    private static final int ORDER = Ordered.HIGHEST_PRECEDENCE + 500;

    /** Advisor 名称 */
    private static final String NAME = "ratelimit";

    /** 每分钟最大请求数 */
    private final int maxRequestsPerMinute;

    /** 客户端请求计数 Map */
    private final ConcurrentHashMap<String, RateLimitInfo> requestCounts = new ConcurrentHashMap<>();

    /**
     * 默认构造函数
     */
    public RateLimitAdvisor() {
        this(10);
    }

    /**
     * 带限流参数的构造函数
     *
     * @param maxRequestsPerMinute 每分钟最大请求数
     */
    public RateLimitAdvisor(int maxRequestsPerMinute) {
        this.maxRequestsPerMinute = maxRequestsPerMinute;
        log.info("✅ [RateLimitAdvisor] 初始化，最大请求数/分钟：{}", maxRequestsPerMinute);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public int getOrder() {
        return ORDER;
    }

    @Override
    public ChatClientResponse adviseCall(@NonNull ChatClientRequest request, @NonNull CallAdvisorChain chain) {
        // 获取客户端 ID（实际应从请求上下文中获取）
        String clientId = getClientId(request);

        // 检查限流
        if (!checkRateLimit(clientId)) {
            log.warn("🚫 [RateLimitAdvisor] 请求被限流：clientId={}", clientId);
            throw new RuntimeException("请求过于频繁，请稍后再试");
        }

        return chain.nextCall(request);
    }

    @Override
    public Flux<ChatClientResponse> adviseStream(@NonNull ChatClientRequest request, @NonNull StreamAdvisorChain chain) {
        String clientId = getClientId(request);

        if (!checkRateLimit(clientId)) {
            log.warn("🚫 [RateLimitAdvisor] 流式请求被限流：clientId={}", clientId);
            throw new RuntimeException("请求过于频繁，请稍后再试");
        }

        return chain.nextStream(request);
    }

    /**
     * 获取客户端 ID
     *
     * @param request 请求对象
     * @return 客户端标识
     */
    private String getClientId(ChatClientRequest request) {
        // 实际应从请求头（如 X-Client-Id）或上下文中获取
        return "default-client";
    }

    /**
     * 检查限流
     *
     * @param clientId 客户端 ID
     * @return true 表示允许请求，false 表示被限流
     */
    private boolean checkRateLimit(String clientId) {
        long currentMinute = System.currentTimeMillis() / 60000;
        RateLimitInfo info = requestCounts.computeIfAbsent(
                clientId,
                k -> new RateLimitInfo(currentMinute, new AtomicInteger(0))
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

        log.debug("📊 [RateLimitAdvisor] 请求计数：clientId={}, count={}/{}",
                clientId, count, maxRequestsPerMinute);
        return true;
    }

    /**
     * 限流信息内部类
     */
    private static class RateLimitInfo {
        private long minute;
        private final AtomicInteger count;

        public RateLimitInfo(long minute, AtomicInteger count) {
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
        log.info("🚫 [RateLimitAdvisor] 限流统计已重置");
    }
}
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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 性能监控 Advisor
 *
 * <p>功能说明：</p>
 * <ul>
 *   <li>统计 AI 调用总次数</li>
 *   <li>统计总耗时、平均耗时</li>
 *   <li>统计最小/最大响应时间</li>
 *   <li>帮助识别性能瓶颈</li>
 * </ul>
 *
 * <p>执行顺序：{@link Ordered#HIGHEST_PRECEDENCE} + 200</p>
 *
 * @author AI Study
 * @since 1.0.0
 */
@Slf4j
@Component
public class PerformanceAdvisor implements CallAdvisor, StreamAdvisor {

    /** Advisor 执行顺序 */
    private static final int ORDER = Ordered.HIGHEST_PRECEDENCE + 200;

    /** Advisor 名称 */
    private static final String NAME = "performance";

    /** 总调用次数 */
    private final AtomicLong totalCalls = new AtomicLong(0);

    /** 总耗时（毫秒） */
    private final AtomicLong totalDuration = new AtomicLong(0);

    /** 最小耗时（毫秒） */
    private volatile long minDuration = Long.MAX_VALUE;

    /** 最大耗时（毫秒） */
    private volatile long maxDuration = 0;

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
        long startTime = System.currentTimeMillis();

        try {
            ChatClientResponse response = chain.nextCall(request);
            long duration = System.currentTimeMillis() - startTime;
            recordStats(duration);
            return response;
        } catch (Exception e) {
            log.error("❌ [PerformanceAdvisor] 调用失败：{}", e.getMessage());
            throw e;
        }
    }

    @Override
    public Flux<ChatClientResponse> adviseStream(@NonNull ChatClientRequest request, @NonNull StreamAdvisorChain chain) {
        long startTime = System.currentTimeMillis();
        return chain.nextStream(request)
                .doOnComplete(() -> {
                    long duration = System.currentTimeMillis() - startTime;
                    recordStats(duration);
                })
                .doOnError(e -> {
                    log.error("❌ [PerformanceAdvisor] 流式失败：{}", e.getMessage());
                });
    }

    /**
     * 记录统计信息
     *
     * @param duration 耗时（毫秒）
     */
    private void recordStats(long duration) {
        totalCalls.incrementAndGet();
        totalDuration.addAndGet(duration);
        synchronized (this) {
            if (duration < minDuration) {
                minDuration = duration;
            }
            if (duration > maxDuration) {
                maxDuration = duration;
            }
        }
        log.debug("⏱️ [PerformanceAdvisor] 耗时：{}ms", duration);
    }

    /**
     * 获取性能统计信息
     *
     * @return 统计信息 Map
     */
    public Map<String, Object> getStats() {
        Map<String, Object> result = new HashMap<>();
        long calls = totalCalls.get();
        result.put("totalCalls", calls);
        result.put("totalDuration", totalDuration.get() + "ms");
        result.put("minDuration", minDuration == Long.MAX_VALUE ? "N/A" : minDuration + "ms");
        result.put("maxDuration", maxDuration + "ms");
        result.put("avgDuration", calls == 0 ? "0ms" : (totalDuration.get() / calls) + "ms");
        return result;
    }

    /**
     * 重置统计信息
     */
    public void resetStats() {
        totalCalls.set(0);
        totalDuration.set(0);
        minDuration = Long.MAX_VALUE;
        maxDuration = 0;
        log.info("📊 [PerformanceAdvisor] 统计已重置");
    }
}

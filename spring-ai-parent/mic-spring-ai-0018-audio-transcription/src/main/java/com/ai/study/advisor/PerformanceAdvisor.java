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

import java.util.concurrent.atomic.AtomicLong;

/**
 * 性能统计切面 Advisor
 * <p>
 * 同时实现 {@link CallAdvisor} 和 {@link StreamAdvisor}，
 * 用于拦截同步及流式 AI 请求，统计系统总调用次数与平均响应延迟。
 * </p>
 *
 * @author AI Assistant
 * @version 1.1
 */
@Slf4j
@Component
public class PerformanceAdvisor implements CallAdvisor, StreamAdvisor {

    /** 执行优先级 */
    private static final int ORDER = Ordered.HIGHEST_PRECEDENCE + 300;

    /** Advisor 名称标识 */
    private static final String NAME = "performance";

    /** 累计调用总次数 */
    private final AtomicLong totalCalls = new AtomicLong(0);

    /** 累计消耗总耗时 (毫秒) */
    private final AtomicLong totalDuration = new AtomicLong(0);

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public int getOrder() {
        return ORDER;
    }

    /**
     * 拦截同步对话调用并统计性能
     */
    @Override
    public ChatClientResponse adviseCall(@NonNull ChatClientRequest request, @NonNull CallAdvisorChain chain) {
        long start = System.currentTimeMillis();
        try {
            return chain.nextCall(request);
        } finally {
            long duration = System.currentTimeMillis() - start;
            totalCalls.incrementAndGet();
            totalDuration.addAndGet(duration);
            log.debug("⏱️ [Call] 请求处理耗时：{}ms", duration);
        }
    }

    /**
     * 拦截流式对话调用并通过 Reactive 钩子统计性能
     */
    @Override
    public Flux<ChatClientResponse> adviseStream(@NonNull ChatClientRequest request, @NonNull StreamAdvisorChain chain) {
        long start = System.currentTimeMillis();
        return chain.nextStream(request)
                .doOnComplete(() -> {
                    long duration = System.currentTimeMillis() - start;
                    totalCalls.incrementAndGet();
                    totalDuration.addAndGet(duration);
                    log.debug("⏱️ [Stream] 响应流处理完成，总耗时：{}ms", duration);
                })
                .doOnError(e -> {
                    long duration = System.currentTimeMillis() - start;
                    totalDuration.addAndGet(duration);
                    log.warn("⏱️ [Stream] 响应流发生异常，已记录耗时：{}ms", duration);
                });
    }

    /**
     * 计算并获取平均响应耗时 (毫秒)
     *
     * @return 平均耗时，若调用次数为 0 则返回 0
     */
    public double getAvgResponseTime() {
        long calls = totalCalls.get();
        return calls == 0 ? 0 : (double) totalDuration.get() / calls;
    }

    /**
     * 获取系统总调用次数
     *
     * @return 累计总次数
     */
    public long getTotalCalls() {
        return totalCalls.get();
    }
}
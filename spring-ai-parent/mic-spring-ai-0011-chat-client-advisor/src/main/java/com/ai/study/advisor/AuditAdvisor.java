package com.ai.study.advisor;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
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

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 审计日志 Advisor
 *
 * <p>功能说明：</p>
 * <ul>
 *   <li>生成 TraceId 用于全链路追踪</li>
 *   <li>记录每次调用的完整信息</li>
 *   <li>记录用户、时间、请求内容等</li>
 *   <li>满足合规和审计要求</li>
 * </ul>
 *
 * <p>执行顺序：{@link Ordered#HIGHEST_PRECEDENCE} + 400</p>
 *
 * @author AI Study
 * @since 1.0.0
 */
@Slf4j
@Component
public class AuditAdvisor implements CallAdvisor, StreamAdvisor {

    /** Advisor 执行顺序 */
    private static final int ORDER = Ordered.HIGHEST_PRECEDENCE + 400;

    /** Advisor 名称 */
    private static final String NAME = "audit";

    private static final String TRACE_ID = "traceId";

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
        // ✅ 从 MDC 获取 traceId（由拦截器在入口处生成）
        String traceId = MDC.get(TRACE_ID);
        if (traceId == null) {
            traceId = "N/A";
        }

        // 记录请求信息
        log.info("📋 [AuditAdvisor] ===== 审计日志开始 =====");
        log.info("📋 [AuditAdvisor] TraceId: {}", traceId);
        log.info("📋 [AuditAdvisor] 时间: {}", LocalDateTime.now());
        log.info("📋 [AuditAdvisor] 请求: {}", request.prompt().getInstructions());

        long startTime = System.currentTimeMillis();

        try {
            // 执行调用链
            ChatClientResponse response = chain.nextCall(request);
            long duration = System.currentTimeMillis() - startTime;

            // 记录响应信息
            if (response != null && response.chatResponse() != null) {
                log.info("📋 [AuditAdvisor] 响应: {}",
                        response.chatResponse().getResult().getOutput().getText());
            }
            log.info("📋 [AuditAdvisor] 耗时: {}ms", duration);
            log.info("📋 [AuditAdvisor] ===== 审计日志结束 =====");

            return response;

        } catch (Exception e) {
            log.error("📋 [AuditAdvisor] 调用失败: {}", e.getMessage());
            log.info("📋 [AuditAdvisor] ===== 审计日志结束（异常） =====");
            throw e;
        }
    }

    @Override
    public Flux<ChatClientResponse> adviseStream(@NonNull ChatClientRequest request, @NonNull StreamAdvisorChain chain) {
        String traceId = UUID.randomUUID().toString();

        log.info("📋 [AuditAdvisor] ===== 流式审计开始 =====");
        log.info("📋 [AuditAdvisor] TraceId: {}", traceId);
        log.info("📋 [AuditAdvisor] 时间: {}", LocalDateTime.now());

        return chain.nextStream(request)
                .doOnComplete(() -> {
                    log.info("📋 [AuditAdvisor] ===== 流式审计结束 =====");
                })
                .doOnError(e -> {
                    log.error("📋 [AuditAdvisor] 流式调用失败: {}", e.getMessage());
                });
    }
}
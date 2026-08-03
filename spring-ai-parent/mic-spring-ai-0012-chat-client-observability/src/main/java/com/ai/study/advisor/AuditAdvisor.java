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

import java.time.LocalDateTime;

/**
 * 审计日志 Advisor
 *
 * <p>功能说明：</p>
 * <ul>
 *   <li>记录每次调用的完整信息</li>
 *   <li>记录用户、时间、请求内容等</li>
 *   <li>满足合规和审计要求</li>
 * </ul>
 *
 * <p>⚠️ 重要提示：不再主动生成 TraceId，而是完全依赖 Micrometer 框架自动注入到 MDC 中。</p>
 *
 * @author AI Study
 * @since 1.0.0
 */
@Slf4j
@Component
public class AuditAdvisor implements CallAdvisor, StreamAdvisor {

    private static final int ORDER = Ordered.HIGHEST_PRECEDENCE + 400;
    private static final String NAME = "audit";

    // 官方 Micrometer 可能会使用 "traceId" 或 "X-B3-TraceId"，Logback 配置里已经兼容了
    // 我们这里直接通过 %X 打印即可，Java 代码中不做任何主动拦截和生成

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
        // 记录请求信息
        log.info("📋 [AuditAdvisor] ===== 审计日志开始 =====");
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
            log.error("📋 [AuditAdvisor] 调用失败: {}", e.getMessage(), e);
            log.info("📋 [AuditAdvisor] ===== 审计日志结束（异常） =====");
            throw e;
        }
    }

    @Override
    public Flux<ChatClientResponse> adviseStream(@NonNull ChatClientRequest request, @NonNull StreamAdvisorChain chain) {
        // 完全交给框架，不在 Java 层手动生成 TraceId
        log.info("📋 [AuditAdvisor] ===== 流式审计开始 =====");
        log.info("📋 [AuditAdvisor] 时间: {}", LocalDateTime.now());

        return chain.nextStream(request)
                .doOnComplete(() -> {
                    log.info("📋 [AuditAdvisor] ===== 流式审计结束 =====");
                })
                .doOnError(e -> {
                    log.error("📋 [AuditAdvisor] 流式调用失败: {}", e.getMessage(), e);
                });
    }
}
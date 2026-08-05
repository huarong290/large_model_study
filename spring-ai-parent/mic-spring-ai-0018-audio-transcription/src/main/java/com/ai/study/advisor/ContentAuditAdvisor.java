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
import java.util.UUID;

/**
 * 内容审核 Advisor
 *
 * <p>功能说明：</p>
 * <ul>
 *   <li>记录所有请求和响应的完整内容</li>
 *   <li>生成审计 TraceId</li>
 *   <li>记录敏感操作日志</li>
 * </ul>
 *
 * @author AI Study
 * @since 1.0.0
 */
@Slf4j
@Component
public class ContentAuditAdvisor implements CallAdvisor, StreamAdvisor {

    private static final int ORDER = Ordered.HIGHEST_PRECEDENCE + 200;
    private static final String NAME = "content-audit";

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
        String auditId = UUID.randomUUID().toString();

        log.info("📋 [ContentAudit] ===== 内容审核开始 =====");
        log.info("📋 [ContentAudit] AuditId: {}", auditId);
        log.info("📋 [ContentAudit] 时间: {}", LocalDateTime.now());
        log.info("📋 [ContentAudit] 请求内容: {}", request.prompt().getInstructions());

        long startTime = System.currentTimeMillis();

        try {
            ChatClientResponse response = chain.nextCall(request);
            long duration = System.currentTimeMillis() - startTime;

            if (response != null && response.chatResponse() != null) {
                String content = response.chatResponse().getResult().getOutput().getText();
                log.info("📋 [ContentAudit] 响应内容: {}",
                        content.length() > 500 ? content.substring(0, 500) + "..." : content);
                log.info("📋 [ContentAudit] 响应长度: {} 字符", content.length());
            }
            log.info("📋 [ContentAudit] 耗时: {}ms", duration);
            log.info("📋 [ContentAudit] ===== 内容审核结束 =====");

            return response;

        } catch (Exception e) {
            log.error("📋 [ContentAudit] 调用失败: {}", e.getMessage());
            log.info("📋 [ContentAudit] ===== 内容审核结束（异常） =====");
            throw e;
        }
    }

    @Override
    public Flux<ChatClientResponse> adviseStream(@NonNull ChatClientRequest request, @NonNull StreamAdvisorChain chain) {
        String auditId = UUID.randomUUID().toString();

        log.info("📋 [ContentAudit] ===== 流式内容审核开始 =====");
        log.info("📋 [ContentAudit] AuditId: {}", auditId);

        return chain.nextStream(request)
                .doOnComplete(() -> {
                    log.info("📋 [ContentAudit] ===== 流式内容审核结束 =====");
                })
                .doOnError(e -> {
                    log.error("📋 [ContentAudit] 流式调用失败: {}", e.getMessage());
                });
    }
}
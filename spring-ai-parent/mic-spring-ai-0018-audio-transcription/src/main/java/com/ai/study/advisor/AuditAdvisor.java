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

/**
 * 审计日志切面 Advisor
 * <p>
 * 位于责任链的最外层 (High Precedence)，负责统一记录请求的发起时间、用户提示词及拦截状态，
 * 确保即使后续环节触发拦截（如敏感词校验失败），系统依然留有全量的审计日志。
 * </p>
 *
 * @author AI Assistant
 * @version 1.1
 */
@Slf4j
@Component
public class AuditAdvisor implements CallAdvisor, StreamAdvisor {

    /** 提高优先级至外层（数字越小越先执行），确保全局请求留痕 */
    private static final int ORDER = Ordered.HIGHEST_PRECEDENCE + 50;

    /** Advisor 名称标识 */
    private static final String NAME = "audit";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public int getOrder() {
        return ORDER;
    }

    /**
     * 拦截同步请求审计
     */
    @Override
    public ChatClientResponse adviseCall(@NonNull ChatClientRequest request, @NonNull CallAdvisorChain chain) {
        log.info("🔍 [Audit] 收到对话请求，Prompt 内容：{}", request.prompt().getInstructions());
        try {
            ChatClientResponse response = chain.nextCall(request);
            log.info("🔍 [Audit] 对话响应成功");
            return response;
        } catch (Exception e) {
            log.warn("🔍 [Audit] 对话请求处理异常或被后续切面拦截：{}", e.getMessage());
            throw e;
        }
    }

    /**
     * 拦截流式请求审计
     */
    @Override
    public Flux<ChatClientResponse> adviseStream(@NonNull ChatClientRequest request, @NonNull StreamAdvisorChain chain) {
        log.info("🔍 [Audit] 收到流式请求，Prompt 内容：{}", request.context());
        return chain.nextStream(request)
                .doOnError(e -> log.warn("🔍 [Audit] 流式传输异常或被拦截：{}", e.getMessage()));
    }
}
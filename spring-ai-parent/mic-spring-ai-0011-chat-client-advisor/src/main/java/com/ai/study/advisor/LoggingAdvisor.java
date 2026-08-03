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
 * 日志记录 Advisor
 *
 * <p>功能说明：</p>
 * <ul>
 *   <li>在 AI 调用前记录请求消息内容</li>
 *   <li>在 AI 调用后记录响应内容</li>
 *   <li>记录完整的调用耗时</li>
 *   <li>便于问题排查和审计</li>
 * </ul>
 *
 * <p>执行顺序：{@link Ordered#HIGHEST_PRECEDENCE} + 100</p>
 * <p>作为第一个执行的 Advisor，记录最原始的请求信息</p>
 *
 * @author AI Study
 * @since 1.0.0
 */
@Slf4j
@Component
public class LoggingAdvisor implements CallAdvisor, StreamAdvisor {

    /** Advisor 执行顺序（数字越小越先执行） */
    private static final int ORDER = Ordered.HIGHEST_PRECEDENCE + 100;

    /** Advisor 唯一标识名称 */
    private static final String NAME = "logging";

    /**
     * 获取 Advisor 名称
     *
     * @return Advisor 名称
     */
    @Override
    public String getName() {
        return NAME;
    }

    /**
     * 获取执行顺序
     *
     * @return 顺序值
     */
    @Override
    public int getOrder() {
        return ORDER;
    }

    /**
     * 同步调用拦截
     *
     * <p>在 AI 调用前后插入日志记录逻辑</p>
     *
     * @param request 聊天请求
     * @param chain 调用链
     * @return 聊天响应
     */
    @Override
    public ChatClientResponse adviseCall(@NonNull ChatClientRequest request, @NonNull CallAdvisorChain chain) {
        long startTime = System.currentTimeMillis();

        // before: 请求前日志
        log.info("========================================");
        log.info("📝 [LoggingAdvisor] 请求消息：{}", request.prompt().getInstructions());
        log.info("========================================");

        try {
            // 执行后续调用链
            ChatClientResponse response = chain.nextCall(request);

            // after: 响应后日志
            long duration = System.currentTimeMillis() - startTime;
            if (response != null && response.chatResponse() != null) {
                String content = response.chatResponse().getResult().getOutput().getText();
                log.info("========================================");
                log.info("✅ [LoggingAdvisor] 响应：{}",
                        content != null && content.length() > 200 ? content.substring(0, 200) + "..." : content);
                log.info("⏱️ [LoggingAdvisor] 耗时：{}ms", duration);
                log.info("========================================");
            }
            return response;

        } catch (Exception e) {
            log.error("❌ [LoggingAdvisor] 调用失败：{}", e.getMessage());
            throw e;
        }
    }

    /**
     * 流式调用拦截
     *
     * @param request 聊天请求
     * @param chain 流式调用链
     * @return 流式响应
     */
    @Override
    public Flux<ChatClientResponse> adviseStream(@NonNull ChatClientRequest request, @NonNull StreamAdvisorChain chain) {
        long startTime = System.currentTimeMillis();

        log.info("========================================");
        log.info("📝 [LoggingAdvisor] 开始流式处理");
        log.info("请求消息：{}", request.prompt().getInstructions());
        log.info("========================================");

        return chain.nextStream(request)
                .doOnComplete(() -> {
                    long duration = System.currentTimeMillis() - startTime;
                    log.info("✅ [LoggingAdvisor] 流式完成，耗时：{}ms", duration);
                    log.info("========================================");
                })
                .doOnError(e -> {
                    log.error("❌ [LoggingAdvisor] 流式失败：{}", e.getMessage());
                });
    }
}
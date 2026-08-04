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
import java.time.format.DateTimeFormatter;

/**
 * 统一 AI 对话审计与日志拦截器
 *
 * <p>核心功能：</p>
 * <ul>
 *   <li><b>请求记录</b>：拦截并记录发起调用的时间、Prompt 文本与模型指令</li>
 *   <li><b>响应统计</b>：计算单次 AI 调用完整耗时，捕获并记录模型返回结果</li>
 *   <li><b>安全审计</b>：监控异常中断与错误原因，支持系统操作留痕与合规审查</li>
 *   <li><b>链路追踪</b>：日志天然兼容 Spring Cloud / Micrometer 的 MDC TraceId 自动注入</li>
 * </ul>
 *
 * @author AI Study
 * @since 1.0.0
 */
@Slf4j
@Component
public class AuditAdvisor implements CallAdvisor, StreamAdvisor {

    /** Advisor 执行优先级：放在最外层（数字越小越先执行），优先捕获最原始的请求与最终的异常 */
    private static final int ORDER = Ordered.HIGHEST_PRECEDENCE + 100;

    /** Advisor 注册唯一标识 */
    private static final String NAME = "audit";

    /** 时间格式化器 */
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    /** 响应内容在日志中的最大打印长度（超过部分截断，防止大日志打爆控制台） */
    private static final int MAX_RESPONSE_LOG_LENGTH = 300;

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public int getOrder() {
        return ORDER;
    }

    // ================================================================
    // 1. 同步 Blocking 调用审计拦截
    // ================================================================

    @Override
    public ChatClientResponse adviseCall(@NonNull ChatClientRequest request, @NonNull CallAdvisorChain chain) {
        long startTime = System.currentTimeMillis();
        String timestamp = LocalDateTime.now().format(DATE_FORMATTER);
        // 提取每条 Message 的内容进行拼接（推荐，日志更清晰）
        String promptText = request.prompt().getInstructions().stream()
                .map(org.springframework.ai.chat.messages.Message::getText)
                .collect(java.util.stream.Collectors.joining("\n"));

        log.info("========================================");
        log.info("📋 [Audit Log] 同步 AI 调用开始");
        log.info("⏰ 触发时间: {}", timestamp);
        log.info("📝 请求内容: {}", promptText);
        log.info("========================================");

        try {
            // 执行后续 Advisor 链及大模型调用
            ChatClientResponse response = chain.nextCall(request);
            long duration = System.currentTimeMillis() - startTime;

            // 解析模型返回内容
            String responseText = extractResponseText(response);

            log.info("========================================");
            log.info("✅ [Audit Log] 同步 AI 调用完成");
            log.info("⏱️ 执行耗时: {}ms", duration);
            log.info("📤 响应摘要: {}", formatContentForLog(responseText));
            log.info("========================================");

            return response;

        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("========================================");
            log.error("❌ [Audit Log] 同步 AI 调用异常");
            log.error("⏱️ 失败耗时: {}ms", duration);
            log.error("⚠️ 异常信息: {}", e.getMessage(), e);
            log.error("========================================");
            throw e;
        }
    }

    // ================================================================
    // 2. 流式 Streaming 调用审计拦截
    // ================================================================

    @Override
    public Flux<ChatClientResponse> adviseStream(@NonNull ChatClientRequest request, @NonNull StreamAdvisorChain chain) {
        long startTime = System.currentTimeMillis();
        String timestamp = LocalDateTime.now().format(DATE_FORMATTER);
        // 提取每条 Message 的内容进行拼接（推荐，日志更清晰）
        String promptText = request.prompt().getInstructions().stream()
                .map(org.springframework.ai.chat.messages.Message::getText)
                .collect(java.util.stream.Collectors.joining("\n"));

        log.info("========================================");
        log.info("📋 [Audit Log] 流式 AI 响应开始");
        log.info("⏰ 触发时间: {}", timestamp);
        log.info("📝 请求内容: {}", promptText);
        log.info("========================================");

        return chain.nextStream(request)
                .doOnComplete(() -> {
                    long duration = System.currentTimeMillis() - startTime;
                    log.info("========================================");
                    log.info("✅ [Audit Log] 流式 AI 响应传输完成");
                    log.info("⏱️ 总计耗时: {}ms", duration);
                    log.info("========================================");
                })
                .doOnError(e -> {
                    long duration = System.currentTimeMillis() - startTime;
                    log.error("========================================");
                    log.error("❌ [Audit Log] 流式 AI 传输中断");
                    log.error("⏱️ 异常耗时: {}ms", duration);
                    log.error("⚠️ 中断原因: {}", e.getMessage(), e);
                    log.error("========================================");
                });
    }

    // ================================================================
    // 内部辅助方法
    // ================================================================

    /**
     * 安全提取 ChatClientResponse 中的文本
     */
    private String extractResponseText(ChatClientResponse response) {
        if (response != null && response.chatResponse() != null
                && response.chatResponse().getResult() != null
                && response.chatResponse().getResult().getOutput() != null) {
            return response.chatResponse().getResult().getOutput().getText();
        }
        return "[无文本内容返回]";
    }

    /**
     * 针对过长响应文本进行格式化与截断展示
     */
    private String formatContentForLog(String content) {
        if (content == null) {
            return "";
        }
        if (content.length() > MAX_RESPONSE_LOG_LENGTH) {
            return content.substring(0, MAX_RESPONSE_LOG_LENGTH) + String.format(" ... [共 %d 字，部分省略]", content.length());
        }
        return content;
    }
}

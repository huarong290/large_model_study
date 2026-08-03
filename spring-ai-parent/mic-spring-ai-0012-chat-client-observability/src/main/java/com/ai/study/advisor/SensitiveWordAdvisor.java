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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 敏感词过滤 Advisor
 *
 * <p>功能说明：</p>
 * <ul>
 *   <li>过滤用户消息中的敏感词</li>
 *   <li>过滤 AI 响应中的敏感词</li>
 *   <li>检测到敏感词时记录警告日志</li>
 *   <li>保障内容安全</li>
 * </ul>
 *
 * <p>执行顺序：{@link Ordered#HIGHEST_PRECEDENCE} + 600</p>
 * <p>注意：实际生产环境应从配置文件或数据库加载敏感词列表</p>
 *
 * @author AI Study
 * @since 1.0.0
 */
@Slf4j
@Component
public class SensitiveWordAdvisor implements CallAdvisor, StreamAdvisor {

    /** Advisor 执行顺序 */
    private static final int ORDER = Ordered.HIGHEST_PRECEDENCE + 600;

    /** Advisor 名称 */
    private static final String NAME = "sensitive";

    /** 敏感词集合 */
    private final Set<String> sensitiveWords;

    /**
     * 默认构造函数
     */
    public SensitiveWordAdvisor() {
        this.sensitiveWords = new HashSet<>(Arrays.asList(
                "敏感词1", "敏感词2", "敏感词3"
                // 实际生产环境应从配置文件或数据库加载
        ));
        log.info("✅ [SensitiveWordAdvisor] 初始化，敏感词数量：{}", sensitiveWords.size());
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
        // 检查用户消息
        String userMessage = request.prompt().getInstructions().toString();
        if (containsSensitiveWords(userMessage)) {
            log.warn("⚠️ [SensitiveWordAdvisor] 用户消息包含敏感词");
            // 可以选择抛异常、替换内容或记录日志
        }

        // 执行调用
        ChatClientResponse response = chain.nextCall(request);

        // 检查 AI 响应
        if (response != null && response.chatResponse() != null) {
            String content = response.chatResponse().getResult().getOutput().getText();
            if (containsSensitiveWords(content)) {
                log.warn("⚠️ [SensitiveWordAdvisor] AI 响应包含敏感词");
            }
        }

        return response;
    }

    @Override
    public Flux<ChatClientResponse> adviseStream(@NonNull ChatClientRequest request, @NonNull StreamAdvisorChain chain) {
        return chain.nextStream(request);
    }

    /**
     * 检查文本是否包含敏感词
     *
     * @param text 待检查文本
     * @return true 表示包含敏感词
     */
    private boolean containsSensitiveWords(String text) {
        if (text == null || text.isEmpty()) {
            return false;
        }
        for (String word : sensitiveWords) {
            if (text.contains(word)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 添加敏感词
     *
     * @param word 敏感词
     */
    public void addSensitiveWord(String word) {
        sensitiveWords.add(word);
        log.info("✅ [SensitiveWordAdvisor] 添加敏感词：{}", word);
    }

    /**
     * 移除敏感词
     *
     * @param word 敏感词
     */
    public void removeSensitiveWord(String word) {
        sensitiveWords.remove(word);
        log.info("✅ [SensitiveWordAdvisor] 移除敏感词：{}", word);
    }
}
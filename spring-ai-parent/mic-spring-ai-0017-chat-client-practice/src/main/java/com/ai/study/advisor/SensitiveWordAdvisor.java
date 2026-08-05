package com.ai.study.advisor;

import com.ai.study.exception.SecurityException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisor;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisorChain;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.Set;

/**
 * 敏感词过滤 Advisor
 *
 * <p>功能说明：</p>
 * <ul>
 *   <li>过滤用户消息中的敏感词</li>
 *   <li>过滤 AI 响应中的敏感词</li>
 *   <li>检测到敏感词时抛出异常或记录日志</li>
 * </ul>
 *
 * @author AI Study
 * @since 1.0.0
 */
@Slf4j
@Component
public class SensitiveWordAdvisor implements CallAdvisor, StreamAdvisor {

    private static final int ORDER = Ordered.HIGHEST_PRECEDENCE + 100;
    private static final String NAME = "sensitive-word";

    /**
     * 是否启用敏感词过滤
     */
    @Value("${security.sensitive-word.enabled:true}")
    private boolean enabled;

    /**
     * 是否拦截包含敏感词的响应
     */
    @Value("${security.content-audit.block-sensitive-response:true}")
    private boolean blockSensitiveResponse;

    /**
     * 敏感词集合
     */
    private final Set<String> sensitiveWords;

    public SensitiveWordAdvisor(@Qualifier("sensitiveWordSet") Set<String> sensitiveWords) {
        this.sensitiveWords = sensitiveWords;
        log.info("✅ SensitiveWordAdvisor 初始化，敏感词数量：{}", sensitiveWords.size());
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
        // 1. 检查用户消息
        String userMessage = request.prompt().getInstructions().toString();
        String detectedWord = containsSensitiveWords(userMessage);
        if (detectedWord != null) {
            log.warn("⚠️ [SensitiveWordAdvisor] 用户消息包含敏感词：{}", detectedWord);
            throw SecurityException.sensitiveWordDetected(detectedWord);
        }

        // 2. 执行调用
        ChatClientResponse response = chain.nextCall(request);

        // 3. 检查 AI 响应
        if (response != null && response.chatResponse() != null) {
            String content = response.chatResponse().getResult().getOutput().getText();
            String detectedInResponse = containsSensitiveWords(content);
            if (detectedInResponse != null) {
                log.warn("⚠️ [SensitiveWordAdvisor] AI 响应包含敏感词：{}", detectedInResponse);
                if (blockSensitiveResponse) {
                    throw SecurityException.sensitiveWordDetected(detectedInResponse);
                }
            }
        }

        return response;
    }

    @Override
    public Flux<ChatClientResponse> adviseStream(@NonNull ChatClientRequest request, @NonNull StreamAdvisorChain chain) {
        String userMessage = request.prompt().getInstructions().toString();
        String detectedWord = containsSensitiveWords(userMessage);
        if (detectedWord != null) {
            log.warn("⚠️ [SensitiveWordAdvisor] 用户消息包含敏感词：{}", detectedWord);
            throw SecurityException.sensitiveWordDetected(detectedWord);
        }

        return chain.nextStream(request);
    }

    /**
     * 检查文本是否包含敏感词
     *
     * @param text 待检查文本
     * @return 匹配到的敏感词，如果没有则返回 null
     */
    private String containsSensitiveWords(String text) {
        if (text == null || text.isEmpty()) {
            return null;
        }
        for (String word : sensitiveWords) {
            if (text.contains(word)) {
                return word;
            }
        }
        return null;
    }
}

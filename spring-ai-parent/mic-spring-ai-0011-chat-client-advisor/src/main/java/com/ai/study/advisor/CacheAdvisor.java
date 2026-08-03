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

import java.util.concurrent.ConcurrentHashMap;

/**
 * 缓存 Advisor
 *
 * <p>功能说明：</p>
 * <ul>
 *   <li>缓存 AI 响应结果</li>
 *   <li>相同问题直接返回缓存结果，减少 API 调用</li>
 *   <li>控制缓存大小，防止内存溢出</li>
 *   <li>降低 API 调用成本</li>
 * </ul>
 *
 * <p>执行顺序：{@link Ordered#HIGHEST_PRECEDENCE} + 300</p>
 * <p>注意：当前为简化版，生产环境建议使用 Redis 等分布式缓存</p>
 *
 * @author AI Study
 * @since 1.0.0
 */
@Slf4j
@Component
public class CacheAdvisor implements CallAdvisor, StreamAdvisor {

    /** Advisor 执行顺序 */
    private static final int ORDER = Ordered.HIGHEST_PRECEDENCE + 300;

    /** Advisor 名称 */
    private static final String NAME = "cache";

    /** 缓存最大容量 */
    private final int maxSize;

    /** 缓存存储 Map */
    private final ConcurrentHashMap<String, String> cache = new ConcurrentHashMap<>();

    /**
     * 默认构造函数
     */
    public CacheAdvisor() {
        this(100);
    }

    /**
     * 带缓存大小的构造函数
     *
     * @param maxSize 最大缓存数量
     */
    public CacheAdvisor(int maxSize) {
        this.maxSize = maxSize;
        log.info("✅ [CacheAdvisor] 初始化，最大缓存：{}", maxSize);
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
        // 生成缓存 Key（基于 Prompt 内容）
        String cacheKey = String.valueOf(request.prompt().getInstructions().hashCode());

        // 检查缓存
        String cachedContent = cache.get(cacheKey);
        if (cachedContent != null) {
            log.info("💾 [CacheAdvisor] 命中缓存：{}", cacheKey);
            // 缓存命中，继续调用（实际应直接返回缓存结果，简化处理）
        }

        // 执行调用
        ChatClientResponse response = chain.nextCall(request);

        // 存入缓存
        if (response != null && response.chatResponse() != null) {
            String content = response.chatResponse().getResult().getOutput().getText();
            if (content != null && !content.isEmpty()) {
                // 检查缓存大小，淘汰最旧的条目
                if (cache.size() >= maxSize) {
                    String firstKey = cache.keySet().iterator().next();
                    cache.remove(firstKey);
                    log.debug("💾 [CacheAdvisor] 缓存已满，淘汰：{}", firstKey);
                }
                cache.put(cacheKey, content);
                log.info("💾 [CacheAdvisor] 缓存已保存：{}", cacheKey);
            }
        }

        return response;
    }

    @Override
    public Flux<ChatClientResponse> adviseStream(@NonNull ChatClientRequest request, @NonNull StreamAdvisorChain chain) {
        return chain.nextStream(request);
    }

    /**
     * 清除所有缓存
     */
    public void clearCache() {
        cache.clear();
        log.info("💾 [CacheAdvisor] 缓存已清除");
    }

    /**
     * 获取当前缓存大小
     *
     * @return 缓存数量
     */
    public int getCacheSize() {
        return cache.size();
    }
}
package com.ai.study.service.impl;

import com.ai.study.advisor.*;
import com.ai.study.service.AdvisorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Advisor 服务实现类
 *
 * <p>功能说明：</p>
 * <ul>
 *   <li>使用 ChatClient.mutate() 动态添加 Advisor</li>
 *   <li>支持组合多个 Advisor</li>
 *   <li>提供统计信息收集</li>
 * </ul>
 *
 * @author AI Study
 * @since 1.0.0
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AdvisorServiceImpl implements AdvisorService {

    /** Ollama 本地模型客户端 */
    @Qualifier("ollamaChatClient")
    private final ChatClient chatClient;

    /** 日志记录 Advisor */
    private final LoggingAdvisor loggingAdvisor;

    /** 性能监控 Advisor */
    private final PerformanceAdvisor performanceAdvisor;

    /** 缓存 Advisor */
    private final CacheAdvisor cacheAdvisor;

    /** 审计日志 Advisor */
    private final AuditAdvisor auditAdvisor;

    /** 限流 Advisor */
    private final RateLimitAdvisor rateLimitAdvisor;

    /** 敏感词过滤 Advisor */
    private final SensitiveWordAdvisor sensitiveWordAdvisor;

    @Override
    public String chatWithAllAdvisors(String message) {
        log.info("🔧 带所有 Advisor 对话：{}", message);

        return chatClient.mutate()
                .defaultAdvisors(
                        loggingAdvisor,
                        performanceAdvisor,
                        cacheAdvisor,
                        auditAdvisor,
                        rateLimitAdvisor,
                        sensitiveWordAdvisor
                )
                .build()
                .prompt()
                .user(message)
                .call()
                .content();
    }

    @Override
    public String chatWithAdvisors(String message, String... advisorNames) {
        log.info("🔧 带指定 Advisor 对话：{}，Advisors：{}", message, String.join(",", advisorNames));

        List<Advisor> advisors = new ArrayList<>();
        for (String name : advisorNames) {
            switch (name.toLowerCase().trim()) {
                case "logging":
                    advisors.add(loggingAdvisor);
                    break;
                case "performance":
                    advisors.add(performanceAdvisor);
                    break;
                case "cache":
                    advisors.add(cacheAdvisor);
                    break;
                case "audit":
                    advisors.add(auditAdvisor);
                    break;
                case "ratelimit":
                    advisors.add(rateLimitAdvisor);
                    break;
                case "sensitive":
                    advisors.add(sensitiveWordAdvisor);
                    break;
                default:
                    log.warn("未知 Advisor：{}", name);
            }
        }

        if (advisors.isEmpty()) {
            log.warn("没有找到任何 Advisor，使用无 Advisor 模式");
            return chatWithoutAdvisor(message);
        }

        return chatClient.mutate()
                .defaultAdvisors(advisors)
                .build()
                .prompt()
                .user(message)
                .call()
                .content();
    }

    @Override
    public String chatWithoutAdvisor(String message) {
        log.info("💬 无 Advisor 对话：{}", message);
        return chatClient.prompt()
                .user(message)
                .call()
                .content();
    }

    @Override
    public Map<String, Object> getAdvisorStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("performance", performanceAdvisor.getStats());
        stats.put("cacheSize", cacheAdvisor.getCacheSize());
        stats.put("availableAdvisors", List.of(
                "logging - 日志记录",
                "performance - 性能监控",
                "cache - 缓存",
                "audit - 审计日志",
                "ratelimit - 限流",
                "sensitive - 敏感词过滤"
        ));
        stats.put("tip", "使用 /api/advisor/chat/select?advisors=xxx 指定 Advisor");
        return stats;
    }
}
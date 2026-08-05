package com.ai.study.config;

import com.ai.study.filter.ApiKeyFilter;
import com.ai.study.interceptor.RateLimitInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 安全配置类
 *
 * @author AI Study
 * @since 1.0.0
 */
@Configuration
@Slf4j
public class SecurityConfig implements WebMvcConfigurer {

    // ================================================================
    // API Key 配置
    // ================================================================
    @Value("${security.api-key.enabled:true}")
    private boolean apiKeyEnabled;

    @Value("${security.api-key.valid-keys:admin-key-123}")
    private String[] validKeys;

    @Value("${security.api-key.path-patterns:/api/practice/*}")
    private List<String> apiKeyPathPatterns;

    // ✅ 新增：白名单路径
    @Value("${security.whitelist.paths:/health,/actuator/**,/no-security}")
    private List<String> whitelistPaths;

    // ================================================================
    // 限流配置
    // ================================================================
    @Value("${security.rate-limit.enabled:true}")
    private boolean rateLimitEnabled;

    @Value("${security.rate-limit.max-requests-per-minute:10}")
    private int maxRequestsPerMinute;

    @Value("${security.rate-limit.path-patterns:/api/practice/**}")
    private List<String> rateLimitPathPatterns;

    // ================================================================
    // 敏感词配置
    // ================================================================
    @Value("${security.sensitive-word.words:}")
    private String[] sensitiveWords;

    // ================================================================
    // 1. API Key 过滤器
    // ================================================================

    @Bean
    public FilterRegistrationBean<ApiKeyFilter> apiKeyFilter() {
        FilterRegistrationBean<ApiKeyFilter> registrationBean = new FilterRegistrationBean<>();
        // ✅ 传入白名单路径
        registrationBean.setFilter(new ApiKeyFilter(apiKeyEnabled,
                new HashSet<>(Arrays.asList(validKeys)),
                whitelistPaths));
        // ✅ 从配置文件读取路径
        if (apiKeyPathPatterns != null && !apiKeyPathPatterns.isEmpty()) {
            registrationBean.addUrlPatterns(apiKeyPathPatterns.toArray(new String[0]));
        } else {
            registrationBean.addUrlPatterns("/api/practice/*");
        }

        registrationBean.setOrder(1);
        log.info("🔑 API Key 过滤器已配置");
        log.info("   🔹 启用状态：{}", apiKeyEnabled);
        log.info("   🔹 有效 Key 数：{}", validKeys.length);
        log.info("   🔹 拦截路径：{}", apiKeyPathPatterns);
        log.info("   🔹 白名单路径：{}", whitelistPaths);
        return registrationBean;
    }

    // ================================================================
    // 2. 限流拦截器
    // ================================================================

    @Bean
    public RateLimitInterceptor rateLimitInterceptor() {
        log.info("🚫 限流拦截器已配置");
        log.info("   🔹 启用状态：{}", rateLimitEnabled);
        log.info("   🔹 阈值：{} 次/分钟", maxRequestsPerMinute);
        log.info("   🔹 拦截路径：{}", rateLimitPathPatterns);
        return new RateLimitInterceptor(rateLimitEnabled, maxRequestsPerMinute);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        if (rateLimitEnabled && rateLimitPathPatterns != null && !rateLimitPathPatterns.isEmpty()) {
            // ✅ 从配置文件读取路径
            registry.addInterceptor(rateLimitInterceptor())
                    .addPathPatterns(rateLimitPathPatterns);
        }
    }

    // ================================================================
    // 3. 敏感词配置
    // ================================================================

    @Bean
    public Set<String> sensitiveWordSet() {
        Set<String> words = new HashSet<>(Arrays.asList(sensitiveWords));
        // 默认敏感词（仅当配置为空时使用）
        if (words.isEmpty()) {
            words.addAll(Arrays.asList(
                    "暴力", "色情", "赌博", "毒品"
            ));
        }
        log.info("📝 敏感词过滤已配置，共 {} 个词", words.size());
        return words;
    }
}
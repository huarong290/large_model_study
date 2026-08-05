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

    @Value("${security.api-key.enabled:true}")
    private boolean apiKeyEnabled;

    @Value("${security.api-key.valid-keys:admin-key-123}")
    private String[] validKeys;

    @Value("${security.rate-limit.enabled:true}")
    private boolean rateLimitEnabled;

    @Value("${security.rate-limit.max-requests-per-minute:10}")
    private int maxRequestsPerMinute;

    @Value("${security.sensitive-word.enabled:true}")
    private boolean sensitiveWordEnabled;

    @Value("${security.sensitive-word.words:}")
    private String[] sensitiveWords;

    // ================================================================
    // 1. API Key 过滤器
    // ================================================================

    @Bean
    public FilterRegistrationBean<ApiKeyFilter> apiKeyFilter() {
        FilterRegistrationBean<ApiKeyFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new ApiKeyFilter(apiKeyEnabled, new HashSet<>(Arrays.asList(validKeys))));
        registrationBean.addUrlPatterns("/api/security/*");
        registrationBean.setOrder(1);
        log.info("🔑 API Key 过滤器已配置，启用状态：{}", apiKeyEnabled);
        return registrationBean;
    }

    // ================================================================
    // 2. 限流拦截器
    // ================================================================

    @Bean
    public RateLimitInterceptor rateLimitInterceptor() {
        log.info("🚫 限流拦截器已配置,阈值：{} 次/分钟", maxRequestsPerMinute);
        return new RateLimitInterceptor(rateLimitEnabled, maxRequestsPerMinute);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        if (rateLimitEnabled) {
            registry.addInterceptor(rateLimitInterceptor())
                    .addPathPatterns("/api/security/**");
        }
    }

    // ================================================================
    // 3. 敏感词配置
    // ================================================================

    @Bean
    public Set<String> sensitiveWordSet() {
        Set<String> words = new HashSet<>(Arrays.asList(sensitiveWords));
        // 默认敏感词
        words.addAll(Arrays.asList(
                "敏感词1", "敏感词2", "敏感词3",
                "暴力", "色情", "赌博", "毒品"
        ));
        log.info("📝 敏感词过滤已配置，共 {} 个词", words.size());
        return words;
    }
}
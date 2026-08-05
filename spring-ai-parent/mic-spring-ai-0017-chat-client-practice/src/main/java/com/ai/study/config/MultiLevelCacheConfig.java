package com.ai.study.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.support.CompositeCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.List;

@Configuration
@EnableCaching
@Slf4j
@ConditionalOnProperty(name = "practice.cache-enabled", havingValue = "true", matchIfMissing = true) // 👈 1. 联动 YAML 配置开关
public class MultiLevelCacheConfig {

    public static final String AI_RESPONSE_CACHE = "practice";

    /**
     * 1. L1 一级缓存：Caffeine 本地内存
     * 用于存放最热门的问答，响应速度纳秒级
     */
    @Bean
    public CaffeineCacheManager caffeineCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager(AI_RESPONSE_CACHE);
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .initialCapacity(100)
                .maximumSize(1000)
                .expireAfterWrite(Duration.ofMinutes(30)) // 30分钟不访问即淘汰
                .recordStats()
        );
        log.info("✅ L1 缓存已初始化：Caffeine（最大1000条，过期30分钟）");
        return cacheManager;
    }

    /**
     * 2. L2 二级缓存：Redis 分布式集群
     * 用于存放全量缓存数据。配置 StringRedisSerializer 保证存入的是明文，绝不报序列化错误。
     */
    @Bean
    public RedisCacheManager redisCacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofHours(24))
                .disableCachingNullValues()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()));

        RedisCacheManager manager = RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(config)
                .build();

        log.info("✅ L2 缓存已初始化：Redis（过期24小时）");
        return manager;
    }

    /**
     * 3. 组合缓存管理器 (核心)
     * 顺序决定机制：先查 Caffeine (L1)，没命中再查 Redis (L2)。
     * 如果是第一次查，两级都没命中，则触发实际业务方法。
     */
    @Bean
    @Primary
    public CacheManager compositeCacheManager(
            CaffeineCacheManager caffeineCacheManager,
            RedisCacheManager redisCacheManager) {

        CompositeCacheManager compositeCacheManager = new CompositeCacheManager();
        compositeCacheManager.setCacheManagers(List.of(
                caffeineCacheManager,
                redisCacheManager
        ));
        // 如果两级都没命中，返回 NoOp 缓存，允许执行底层业务逻辑（即调大模型）
        compositeCacheManager.setFallbackToNoOpCache(true);

        log.info("✅ 多级缓存已启用：L1(Caffeine) + L2(Redis)");
        return compositeCacheManager;
    }
}
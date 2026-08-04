package com.ai.study;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

@SpringBootApplication
@Slf4j
public class MicSpringAi0014Application {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(MicSpringAi0014Application.class);
        Environment env = app.run(args).getEnvironment();

        log.info("========================================");
        log.info("🚀 Redis(Caffeine+L2) 多级缓存模块启动成功！");
        log.info("📡 服务端口：{}", env.getProperty("server.port"));
        log.info("📡 Redis 模式：Sentinel(哨兵)");
        log.info("========================================");
    }
}
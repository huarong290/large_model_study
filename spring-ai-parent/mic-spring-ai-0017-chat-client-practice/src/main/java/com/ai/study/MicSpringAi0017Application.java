package com.ai.study;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.core.env.Environment;

@SpringBootApplication
@EnableCaching
@Slf4j
public class MicSpringAi0017Application {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(MicSpringAi0017Application.class);
        Environment env = app.run(args).getEnvironment();

        log.info("========================================");
        log.info("🚀 智能客服系统启动成功！");
        log.info("📡 服务端口：{}", env.getProperty("server.port"));
        log.info("========================================");
        log.info("💡 测试说明：");
        log.info("   POST /api/practice/chat 智能对话");
        log.info("   POST /api/practice/chat/stream 流式对话");
        log.info("   GET /api/practice/stats 系统统计");
        log.info("========================================");
        log.info("🔑 API Key：practice-key-123");
        log.info("========================================");
    }
}
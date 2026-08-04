package com.ai.study;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

@SpringBootApplication
@Slf4j
public class MicSpringAi0015Application {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(MicSpringAi0015Application.class);
        Environment env = app.run(args).getEnvironment();

        log.info("========================================");
        log.info("🚀 安全控制模块启动成功！");
        log.info("📡 服务端口：{}", env.getProperty("server.port"));
        log.info("========================================");
        log.info("💡 测试说明：");
        log.info("   POST /api/security/chat 安全对话");
        log.info("   GET /api/security/health 健康检查");
        log.info("========================================");
        log.info("🔑 测试 API Key：admin-key-123");
        log.info("========================================");
    }
}
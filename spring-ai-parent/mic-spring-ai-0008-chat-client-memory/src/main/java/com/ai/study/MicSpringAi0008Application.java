package com.ai.study;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

@SpringBootApplication
@Slf4j
public class MicSpringAi0008Application {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(MicSpringAi0008Application.class);
        Environment env = app.run(args).getEnvironment();

        log.info("========================================");
        log.info("🚀 对话记忆模块启动成功！");
        log.info("📡 服务端口：{}", env.getProperty("server.port"));
        log.info("========================================");
        log.info("💡 测试说明：");
        log.info("   GET /api/memory/chat?sessionId=xxx&message=xxx");
        log.info("   同一个 sessionId 会保持对话记忆");
        log.info("   不同 sessionId 的对话互不影响");
        log.info("========================================");
    }
}
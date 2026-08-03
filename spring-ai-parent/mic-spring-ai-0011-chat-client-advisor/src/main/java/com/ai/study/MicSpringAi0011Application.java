package com.ai.study;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

/**
 * 自定义 Advisor 模块启动类
 *
 * @author AI Study
 * @since 1.0.0
 */
@SpringBootApplication
@Slf4j
public class MicSpringAi0011Application {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(MicSpringAi0011Application.class);
        Environment env = app.run(args).getEnvironment();

        log.info("========================================");
        log.info("🚀 自定义 Advisor 模块启动成功！");
        log.info("📡 服务端口：{}", env.getProperty("server.port"));
        log.info("========================================");
        log.info("💡 测试说明：");
        log.info("   POST /api/advisor/chat 带 Advisor 对话");
        log.info("   GET /api/advisor/stats 查看统计");
        log.info("========================================");
    }
}
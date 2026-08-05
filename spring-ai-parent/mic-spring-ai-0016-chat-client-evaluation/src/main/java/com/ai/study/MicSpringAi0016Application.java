package com.ai.study;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

@SpringBootApplication
@Slf4j
public class MicSpringAi0016Application {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(MicSpringAi0016Application.class);
        Environment env = app.run(args).getEnvironment();

        log.info("========================================");
        log.info("🚀 效果评估模块启动成功！");
        log.info("📡 服务端口：{}", env.getProperty("server.port"));
        log.info("========================================");
        log.info("💡 测试说明：");
        log.info("   POST /api/evaluation/score 自动评分");
        log.info("   POST /api/evaluation/compare 模型对比");
        log.info("   POST /api/evaluation/ab-test A/B测试");
        log.info("========================================");
    }
}
package com.ai.study;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

@SpringBootApplication
@Slf4j
public class MicSpringAi0009Application {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(MicSpringAi0009Application.class);
        Environment env = app.run(args).getEnvironment();

        log.info("========================================");
        log.info("🚀 RAG 检索增强生成模块启动成功！");
        log.info("📡 服务端口：{}", env.getProperty("server.port"));
        log.info("========================================");
        log.info("💡 测试说明：");
        log.info("   POST /api/rag/build 构建知识库");
        log.info("   POST /api/rag/ask 基于知识库问答");
        log.info("========================================");
    }
}
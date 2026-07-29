package com.ai.study;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

/**
 * 模型参数配置模块启动类
 *
 * 功能说明：
 * 1. 演示 Temperature、MaxTokens、TopP 等参数的作用
 * 2. 提供不同场景的参数预设
 * 3. 对比不同参数配置的响应效果
 *
 * @author AI Study
 */
@Slf4j
@SpringBootApplication
public class MicSpringAI0004Application {

    public static void main(String[] args){
        SpringApplication app = new SpringApplication(MicSpringAI0004Application.class);
        Environment env = app.run(args).getEnvironment();

        log.info("========================================");
        log.info("🚀 模型参数配置模块启动成功！");
        log.info("📡 服务端口：{}", env.getProperty("server.port"));
        log.info("🔄 默认模型：Ollama（本地）");
        log.info("🌐 访问地址：http://localhost:{}", env.getProperty("server.port"));
        log.info("========================================");
    }
}

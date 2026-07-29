package com.ai.study;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

/**
 * 流式响应模块启动类
 *
 * 功能说明：
 * 1. 演示流式响应与同步响应的区别
 * 2. 实现 SSE 实时推送
 * 3. 展示流式响应的用户体验优势
 *
 * @author AI Study
 */
@SpringBootApplication
@Slf4j
public class MicSpringAi0005Application {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(MicSpringAi0005Application.class);
        Environment env = app.run(args).getEnvironment();

        log.info("========================================");
        log.info("🚀 流式响应模块启动成功！");
        log.info("📡 服务端口：{}", env.getProperty("server.port"));
        log.info("🔄 默认模型：Ollama（本地）");
        log.info("🌐 访问地址：http://localhost:{}", env.getProperty("server.port"));
        log.info("📄 测试页面：http://localhost:{}/index.html", env.getProperty("server.port"));
        log.info("========================================");
    }
}

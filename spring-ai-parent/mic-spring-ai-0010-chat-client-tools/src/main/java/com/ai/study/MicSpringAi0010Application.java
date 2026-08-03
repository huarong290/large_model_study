package com.ai.study;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

/**
 * 工具调用模块启动类
 *
 * <p>功能说明：</p>
 * <ul>
 *   <li>演示 AI 工具调用（Function Calling）能力</li>
 *   <li>支持多工具协同工作</li>
 *   <li>展示工具自动调用机制</li>
 * </ul>
 *
 * @author AI Study
 * @since 1.0.0
 */
@SpringBootApplication
@Slf4j
public class MicSpringAi0010Application {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(MicSpringAi0010Application.class);
        Environment env = app.run(args).getEnvironment();

        log.info("========================================");
        log.info("🚀 工具调用模块启动成功！");
        log.info("📡 服务端口：{}", env.getProperty("server.port"));
        log.info("========================================");
        log.info("💡 测试说明：");
        log.info("   POST /api/tools/chat 带工具对话");
        log.info("   POST /api/tools/compare 对比有无工具效果");
        log.info("========================================");
    }
}
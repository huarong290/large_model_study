package com.ai.study;



import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

/**
 * 多模型路由模块启动类
 *
 * <p>功能说明：</p>
 * <ul>
 *   <li>提供基于内容关键词的动态多模型路由（Ollama 本地 vs DeepSeek 云端）</li>
 *   <li>展示 Spring AI 多 ChatClient 注册与 @Qualifier 注入</li>
 *   <li>演示标准接口（Service）与实现类（Impl）的分离架构</li>
 * </ul>
 *
 * @author AI Study
 * @since 1.0.0
 */
@SpringBootApplication
@Slf4j
public class MicSpringAi0013Application {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(MicSpringAi0013Application.class);
        Environment env = app.run(args).getEnvironment();

        log.info("========================================");
        log.info("🚀 多模型路由模块启动成功！");
        log.info("📡 服务端口：{}", env.getProperty("server.port"));
        log.info("========================================");
        log.info("💡 测试说明：");
        log.info("   🔄 动态路由：基于关键词自动切换模型");
        log.info("   🟢 本地模型：Ollama (llama3.2:3b)");
        log.info("   🔵 云端模型：DeepSeek (deepseek-chat)");
        log.info("========================================");
        log.info("📝 API 测试：");
        log.info("   curl -X POST http://localhost:{}/api/router/chat \\", env.getProperty("server.port"));
        log.info("   -H \"Content-Type: application/json\" \\");
        log.info("   -d '{\"message\": \"你好\"}'");
        log.info("========================================");
    }
}
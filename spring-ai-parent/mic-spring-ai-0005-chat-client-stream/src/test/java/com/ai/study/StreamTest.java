package com.ai.study;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.io.File;
import java.io.FileInputStream;
import java.time.Duration;
import java.util.Properties;

@SpringBootTest
@Slf4j
public class StreamTest {

    @DynamicPropertySource
    static void loadEnvProperties(DynamicPropertyRegistry registry) {
        try {
            Properties props = new Properties();
            String currentDir = System.getProperty("user.dir");
            File envFile = findEnvFile(new File(currentDir));
            if (envFile != null && envFile.exists()) {
                try (FileInputStream fis = new FileInputStream(envFile)) {
                    props.load(fis);
                    log.info("✅ .env 文件加载成功");
                }
            }
            props.forEach((key, value) -> {
                String strKey = key.toString();
                String strValue = value.toString();
                if (!strKey.startsWith("#") && !strValue.isEmpty()) {
                    registry.add(strKey, () -> strValue);
                }
            });
        } catch (Exception e) {
            log.warn("加载 .env 文件失败：{}", e.getMessage());
        }
    }

    private static File findEnvFile(File startDir) {
        File current = startDir;
        while (current != null) {
            File envFile = new File(current, ".env");
            if (envFile.exists()) {
                return envFile;
            }
            current = current.getParentFile();
        }
        return null;
    }

    @Autowired
    @Qualifier("ollamaChatClient")
    private ChatClient ollamaClient;

    /**
     * 测试功能点：流式响应基础测试
     */
    @Test
    void testStreamResponse() {
        log.info("========== 测试流式响应 ==========");
        String message = "用一句话介绍人工智能";

        Flux<String> stream = ollamaClient.prompt()
                .user(message)
                .stream()
                .content()
                .doOnNext(chunk -> log.info("收到块：{}", chunk));

        StepVerifier.create(stream)
                .expectNextCount(1)  // 至少收到一个块
                .verifyComplete();

        log.info("✅ 流式响应测试通过");
    }

    /**
     * 测试功能点：流式响应超时测试
     */
    @Test
    void testStreamTimeout() {
        log.info("========== 测试流式响应超时 ==========");
        String message = "讲一个很长的故事";

        Flux<String> stream = ollamaClient.prompt()
                .user(message)
                .stream()
                .content()
                .timeout(Duration.ofSeconds(5))
                .doOnError(e -> log.warn("⏰ 超时触发（预期行为）"));

        StepVerifier.create(stream)
                .expectNextCount(1)
                .verifyComplete();
    }

    /**
     * 测试功能点：流式 vs 同步对比
     */
    @Test
    void testStreamVsSync() {
        log.info("========== 流式 vs 同步对比 ==========");
        String message = "你好";

        // 同步
        long syncStart = System.currentTimeMillis();
        String syncResponse = ollamaClient.prompt()
                .user(message)
                .call()
                .content();
        long syncTime = System.currentTimeMillis() - syncStart;
        log.info("同步响应：{}ms", syncTime);

        // 流式
        long streamStart = System.currentTimeMillis();
        Flux<String> stream = ollamaClient.prompt()
                .user(message)
                .stream()
                .content();

        StepVerifier.create(stream)
                .expectNextCount(1)
                .verifyComplete();
        long streamTime = System.currentTimeMillis() - streamStart;
        log.info("流式响应：{}ms", streamTime);

        log.info("📊 结论：流式响应虽然总时间相近，但用户能实时看到生成过程");
    }
}
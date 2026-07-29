package com.ai.study;

import com.ai.study.enums.PresetMode;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

/**
 * 参数配置单元测试
 *
 * @author AI Study
 */
@SpringBootTest
@Slf4j
public class OptionsTest {

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

    // ================================================================
    // 测试方法
    // ================================================================

    /**
     * 测试功能点：对比不同温度的响应
     *
     * 测试场景：使用相同的消息，不同温度
     *
     * 预期结果：温度越高，回答越有创造性
     */
    @Test
    void testTemperatureComparison() {
        log.info("========== 测试不同温度 ==========");
        String message = "用一句话形容春天";

        double[] temperatures = {0.0, 0.5, 1.0, 1.5};

        for (double temp : temperatures) {
            log.info("--- 温度: {} ---", temp);
            String response = ollamaClient.prompt()
                    .user(message)
                    .options(OpenAiChatOptions.builder()
                            .temperature(temp)
                            .maxTokens(100)
                            .build())
                    .call()
                    .content();
            log.info("响应: {}\n", response);
        }
    }

    /**
     * 测试功能点：对比不同 MaxTokens 的响应
     *
     * 测试场景：使用相同的消息，不同 MaxTokens
     *
     * 预期结果：MaxTokens 越大，输出越长
     */
    @Test
    void testMaxTokensComparison() {
        log.info("========== 测试不同 MaxTokens ==========");
        String message = "介绍一下人工智能的发展历程";

        int[] tokenLimits = {50, 200, 500};

        for (int limit : tokenLimits) {
            log.info("--- MaxTokens: {} ---", limit);
            String response = ollamaClient.prompt()
                    .user(message)
                    .options(OpenAiChatOptions.builder()
                            .temperature(0.7)
                            .maxTokens(limit)
                            .build())
                    .call()
                    .content();
            log.info("响应长度: {} 字符", response.length());
            log.info("响应预览: {}\n", response.length() > 100 ? response.substring(0, 100) + "..." : response);
        }
    }

    /**
     * 测试功能点：对比所有预设模式
     *
     * 测试场景：使用相同的消息，所有预设模式
     *
     * 预期结果：不同模式输出风格不同
     */
    @Test
    void testAllPresets() {
        log.info("========== 测试所有预设模式 ==========");
        String message = "写一首关于春天的诗";

        for (PresetMode preset : PresetMode.values()) {
            log.info("--- {} ---", preset.getName());
            String response = ollamaClient.prompt()
                    .user(message)
                    .options(OpenAiChatOptions.builder()
                            .temperature(preset.getTemperature())
                            .topP(preset.getTopP())
                            .maxTokens(preset.getMaxTokens())
                            .build())
                    .call()
                    .content();
            log.info("响应: {}\n", response);
        }
    }

    /**
     * 测试功能点：极低温度 vs 极高温度
     *
     * 测试场景：对比温度 0.0 和 2.0 的极端差异
     *
     * 预期结果：0.0 非常确定，2.0 非常随机
     */
    @Test
    void testExtremeTemperatures() {
        log.info("========== 测试极端温度 ==========");
        String message = "1 + 1 等于多少？";

        // 极低温度
        log.info("--- 温度 0.0（极低） ---");
        String lowTemp = ollamaClient.prompt()
                .user(message)
                .options(OpenAiChatOptions.builder()
                        .temperature(0.0)
                        .build())
                .call()
                .content();
        log.info("响应: {}", lowTemp);

        // 极高温度
        log.info("--- 温度 2.0（极高） ---");
        String highTemp = ollamaClient.prompt()
                .user(message)
                .options(OpenAiChatOptions.builder()
                        .temperature(2.0)
                        .build())
                .call()
                .content();
        log.info("响应: {}", highTemp);

        log.info("💡 结论：温度越低回答越确定，温度越高回答越随机");
    }
}

package com.ai.study;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 基础配置单元测试
 *
 * 功能说明：
 * 1. 测试不同模型的注入是否成功
 * 2. 测试不同模型的基本对话功能
 * 3. 对比不同模型的响应时间和质量
 *
 * @author AI Study
 */
@SpringBootTest
@Slf4j
public class ChatClientConfigTest {

    // ================================================================
    // 动态加载 .env 文件
    // ================================================================

    /**
     * 在测试启动前加载 .env 文件中的环境变量
     * 向上查找父目录的 .env 文件（因为文件在 spring-ai-parent 下）
     */
    @DynamicPropertySource
    static void loadEnvProperties(DynamicPropertyRegistry registry) {
        try {
            Properties props = new Properties();

            // ================================================================
            // 向上查找 .env 文件
            // ================================================================
            String currentDir = System.getProperty("user.dir");
            log.info("当前工作目录：{}", currentDir);

            // 从当前目录开始，逐级向上查找 .env 文件
            File envFile = findEnvFile(new File(currentDir));

            if (envFile != null && envFile.exists()) {
                log.info("✅ 找到 .env 文件：{}", envFile.getAbsolutePath());

                try (FileInputStream fis = new FileInputStream(envFile)) {
                    props.load(fis);
                    log.info("✅ .env 文件加载成功，共 {} 个配置项", props.size());

                    // 打印加载的配置项（隐藏敏感信息）
                    props.forEach((key, value) -> {
                        String strKey = key.toString();
                        String strValue = value.toString();
                        if (strKey.contains("API_KEY") && strValue.length() > 8) {
                            String masked = strValue.substring(0, 4) + "****" + strValue.substring(strValue.length() - 4);
                            log.info("   {} = {}", strKey, masked);
                        } else {
                            log.info("   {} = {}", strKey, strValue);
                        }
                    });
                }
            } else {
                log.warn("⚠️ .env 文件未找到，使用系统环境变量或默认值");
                log.warn("   查找路径：{}", currentDir);
                log.warn("   请确认 .env 文件存在于 spring-ai-parent 目录下");
            }

            // 将 .env 中的属性注册到 Spring 环境
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

    /**
     * 从当前目录开始，逐级向上查找 .env 文件
     *
     * @param startDir 起始目录
     * @return .env 文件，如果找不到则返回 null
     */
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

    // ================================================================
    // 注入不同的 ChatClient
    // ================================================================

    /**
     * 默认 ChatClient（Ollama 本地模型）
     * 使用 @Primary 标记，直接注入
     */
    @Autowired
    private ChatClient defaultChatClient;

    /**
     * DeepSeek ChatClient
     */
    @Autowired
    @Qualifier("deepSeekChatClient")
    private ChatClient deepSeekChatClient;

    /**
     * Gemini ChatClient
     */
    @Autowired
    @Qualifier("geminiChatClient")
    private ChatClient geminiChatClient;

    // ================================================================
    // 测试方法
    // ================================================================

    /**
     * 测试功能点：验证所有 ChatClient Bean 是否正确注入
     *
     * 预期结果：Ollama 和 DeepSeek 注入成功，Gemini 可能为 null
     */
    @Test
    void testBeanInjection() {
        log.info("========== 测试 Bean 注入 ==========");
        log.info("defaultChatClient (Ollama): {}", defaultChatClient != null ? "✅ 注入成功" : "❌ 注入失败");
        log.info("deepSeekChatClient: {}", deepSeekChatClient != null ? "✅ 注入成功" : "❌ 注入失败");
        log.info("geminiChatClient: {}", geminiChatClient != null ? "✅ 注入成功" : "⚠️ 未配置");
        log.info("=====================================");
    }

    /**
     * 测试功能点：测试默认模型（Ollama 本地）的基础对话
     *
     * 测试场景：发送简单问候，验证本地模型能否正常响应
     *
     * 注意：需要先启动 Ollama 服务并下载模型
     *
     * 预期结果：返回有意义的响应内容
     */
    @Test
    void testDefaultModelChat() {
        log.info("========== 测试默认模型（Ollama 本地）对话 ==========");
        String message = "你好，请简单介绍一下你自己";

        long startTime = System.currentTimeMillis();
        String response = defaultChatClient.prompt()
                .system("你是一个友好的AI助手")
                .user(message)
                .call()
                .content();
        long endTime = System.currentTimeMillis();

        log.info("用户消息：{}", message);
        log.info("AI 响应：{}", response);
        log.info("响应耗时：{}ms", endTime - startTime);
        log.info("====================================================");
    }

    /**
     * 测试功能点：测试 DeepSeek 模型的基础对话
     *
     * 注意：需要配置 DEEPSEEK_API_KEY 环境变量
     */
    @Test
    void testDeepSeekModelChat() {
        log.info("========== 测试 DeepSeek 对话 ==========");
        String message = "你好，请简单介绍一下你自己";

        try {
            long startTime = System.currentTimeMillis();
            String response = deepSeekChatClient.prompt()
                    .system("你是一个友好的AI助手")
                    .user(message)
                    .call()
                    .content();
            long endTime = System.currentTimeMillis();

            log.info("用户消息：{}", message);
            log.info("AI 响应：{}", response);
            log.info("响应耗时：{}ms", endTime - startTime);
        } catch (Exception e) {
            log.error("❌ DeepSeek 模型调用失败");
            log.error("   请检查 DEEPSEEK_API_KEY 环境变量是否正确");
            log.error("   错误信息：{}", e.getMessage());
        }
        log.info("=========================================");
    }

    /**
     * 测试功能点：测试 Google Gemini 的基础对话
     *
     * 注意：需要配置 GEMINI_API_KEY 环境变量
     * 获取地址：https://aistudio.google.com/app/apikey
     */
    @Test
    void testGeminiModelChat() {
        log.info("========== 测试 Google Gemini 对话 ==========");
        String message = "你好，请简单介绍一下你自己";

        if (geminiChatClient == null) {
            log.warn("⚠️ Gemini 未配置，跳过测试");
            log.warn("   请设置 GEMINI_API_KEY 环境变量");
            log.warn("   获取地址：https://aistudio.google.com/app/apikey");
            return;
        }

        try {
            long startTime = System.currentTimeMillis();
            String response = geminiChatClient.prompt()
                    .system("你是一个友好的AI助手")
                    .user(message)
                    .call()
                    .content();
            long endTime = System.currentTimeMillis();

            log.info("用户消息：{}", message);
            log.info("AI 响应：{}", response);
            log.info("响应耗时：{}ms", endTime - startTime);
        } catch (Exception e) {
            log.error("❌ Gemini 模型调用失败");
            log.error("   请检查 GEMINI_API_KEY 环境变量是否正确");
            log.error("   错误信息：{}", e.getMessage());
        }
        log.info("===========================================");
    }

    /**
     * 测试功能点：对比所有模型的响应
     *
     * 测试场景：使用相同的问题，对比不同模型的回答
     *
     * 预期结果：记录不同模型的响应时间和回答内容
     */
    @Test
    void testCompareAllModels() {
        log.info("========== 对比所有模型 ==========");
        String question = "请用一句话介绍中国的首都北京";

        Map<String, String> responses = new HashMap<>();

        // 1. Ollama（本地）
        try {
            long start = System.currentTimeMillis();
            String response = defaultChatClient.prompt().user(question).call().content();
            responses.put("Ollama（本地）", response + " (耗时: " + (System.currentTimeMillis() - start) + "ms)");
        } catch (Exception e) {
            responses.put("Ollama（本地）", "调用失败: " + e.getMessage());
        }

        // 2. DeepSeek
        try {
            long start = System.currentTimeMillis();
            String response = deepSeekChatClient.prompt().user(question).call().content();
            responses.put("DeepSeek", response + " (耗时: " + (System.currentTimeMillis() - start) + "ms)");
        } catch (Exception e) {
            responses.put("DeepSeek", "调用失败: " + e.getMessage());
        }

        // 3. Gemini
        if (geminiChatClient != null) {
            try {
                long start = System.currentTimeMillis();
                String response = geminiChatClient.prompt().user(question).call().content();
                responses.put("Gemini", response + " (耗时: " + (System.currentTimeMillis() - start) + "ms)");
            } catch (Exception e) {
                responses.put("Gemini", "调用失败: " + e.getMessage());
            }
        } else {
            responses.put("Gemini", "未配置");
        }

        // 打印对比结果
        responses.forEach((model, response) -> {
            log.info("\n【{}】\n{}\n", model, response);
        });
    }

    /**
     * 测试功能点：测试带 System 消息的调用
     */
    @Test
    void testWithSystemMessage() {
        log.info("========== 测试带 System 消息调用 ==========");
        String message = "什么是人工智能？";
        String system = "你是一个编程专家，请用技术角度回答";

        String response = defaultChatClient.prompt()
                .system(system)
                .user(message)
                .call()
                .content();

        log.info("System：{}", system);
        log.info("用户消息：{}", message);
        log.info("AI 响应：{}", response);
        log.info("============================================");
    }

    /**
     * 测试功能点：测试不同角色的 System 提示词
     */
    @Test
    void testDifferentRoles() {
        log.info("========== 测试不同角色 System 提示词 ==========");
        String message = "你好，请介绍一下自己";

        Map<String, String> roles = new HashMap<>();
        roles.put("教师", "你是一位有20年经验的优秀教师");
        roles.put("诗人", "你是一位著名的浪漫主义诗人");
        roles.put("科学家", "你是一位诺贝尔奖获得者科学家");

        roles.forEach((role, systemPrompt) -> {
            log.info("--- 角色：{} ---", role);
            String response = defaultChatClient.prompt()
                    .system(systemPrompt)
                    .user(message)
                    .call()
                    .content();
            log.info("响应：{}\n", response);
        });
    }
}
package com.ai.study;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * ChatClient 基础功能单元测试
 *
 * 功能说明：
 * 1. 测试不同的调用方式
 * 2. 测试 System 和 User 消息的组合
 * 3. 测试不同角色的响应效果
 *
 * @author AI Study
 */
@SpringBootTest
@Slf4j
public class ChatClientCoreTest {

    // ================================================================
    // 动态加载 .env 文件（从父目录查找）
    // ================================================================

    @DynamicPropertySource
    static void loadEnvProperties(DynamicPropertyRegistry registry) {
        try {
            Properties props = new Properties();
            String currentDir = System.getProperty("user.dir");

            // 向上查找 .env 文件
            File envFile = findEnvFile(new File(currentDir));

            if (envFile != null && envFile.exists()) {
                log.info("✅ 找到 .env 文件：{}", envFile.getAbsolutePath());
                try (FileInputStream fis = new FileInputStream(envFile)) {
                    props.load(fis);
                    log.info("✅ .env 文件加载成功");
                }
            } else {
                log.warn("⚠️ .env 文件未找到，使用系统环境变量");
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

    // ================================================================
    // 注入 ChatClient
    // ================================================================

    @Autowired
    private ChatClient defaultChatClient;

    @Autowired
    @Qualifier("deepSeekChatClient")
    private ChatClient deepSeekChatClient;

    @Autowired
    @Qualifier("geminiChatClient")
    private ChatClient geminiChatClient;

    // ================================================================
    // 测试方法
    // ================================================================

    /**
     * 测试功能点：最简单的调用方式
     *
     * 测试场景：只提供用户消息，不设置 System
     *
     * 预期结果：AI 以默认方式回答
     */
    @Test
    void testSimplePrompt() {
        log.info("========== 测试简单调用 ==========");
        String message = "你好，请介绍一下人工智能";

        String response = defaultChatClient.prompt()
                .user(message)
                .call()
                .content();

        log.info("用户消息：{}", message);
        log.info("AI 响应：{}", response);
        log.info("==================================");
    }

    /**
     * 测试功能点：带 System 消息的调用
     *
     * 测试场景：设置 System 为"编程专家"，提问编程相关问题
     *
     * 预期结果：AI 以编程专家的身份回答
     */
    @Test
    void testWithSystemMessage() {
        log.info("========== 测试带 System 消息 ==========");
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
        log.info("======================================");
    }

    /**
     * 测试功能点：使用 Message 对象
     *
     * 测试场景：分别创建 UserMessage 和 SystemMessage 对象，
     * 使用 Prompt 类包装多个 Message
     *
     * 预期结果：AI 以"地球国家地理专家"身份回答问题
     */
    @Test
    void testWithMessageObject() {
        log.info("========== 测试 Message 对象 ==========");
        String userMsg = "介绍下中国的地理面积";
        String systemMsg = "你是一个地球国家地理专家，能很好的回复用户的各种地理问题";

        UserMessage userMessage = new UserMessage(userMsg);
        SystemMessage systemMessage = new SystemMessage(systemMsg);
        Prompt prompt = new Prompt(Arrays.asList(systemMessage, userMessage));

        String response = defaultChatClient.prompt(prompt)
                .call()
                .content();

        log.info("用户消息：{}", userMsg);
        log.info("AI 响应：{}", response);
        log.info("======================================");
    }

    /**
     * 测试功能点：对比 System 消息的位置
     *
     * 测试场景：对比 System 在 user() 之前和之后的效果
     *
     * 预期结果：System 在 user() 之前效果更好
     */
    @Test
    void testSystemPositionComparison() {
        log.info("========== 测试 System 消息位置对比 ==========");
        String question = "介绍下中国的地理面积";

        // 方式1：System 在 user() 之前（推荐）
        log.info("--- 方式1：System 在 user() 之前 ---");
        String response1 = defaultChatClient.prompt()
                .system("你是一个地球国家地理专家")
                .user(question)
                .call()
                .content();
        log.info("响应1：{}\n", response1);

        // 方式2：System 指令拼接到 user 消息中
        log.info("--- 方式2：System 拼接到 user 消息中 ---");
        String userWithSystem = "你是一个地球国家地理专家，" + question;
        String response2 = defaultChatClient.prompt()
                .user(userWithSystem)
                .call()
                .content();
        log.info("响应2：{}\n", response2);
    }

    /**
     * 测试功能点：不同角色的 System 提示词
     *
     * 测试场景：使用教师、诗人、科学家三种角色
     *
     * 预期结果：不同角色回答风格不同
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

    /**
     * 测试功能点：多轮对话（无记忆）
     *
     * 测试场景：两轮独立对话，验证无上下文记忆
     *
     * 预期结果：第二轮无法记住第一轮的内容
     */
    @Test
    void testMultiTurnWithoutMemory() {
        log.info("========== 测试多轮对话（无记忆） ==========");

        // 第一轮：告诉 AI 名字
        String response1 = defaultChatClient.prompt()
                .user("我叫张三")
                .call()
                .content();
        log.info("第一轮用户：我叫张三");
        log.info("第一轮 AI：{}\n", response1);

        // 第二轮：询问名字（AI 应该不记得）
        String response2 = defaultChatClient.prompt()
                .user("我叫什么名字？")
                .call()
                .content();
        log.info("第二轮用户：我叫什么名字？");
        log.info("第二轮 AI：{}", response2);
        log.info("============================================");
        log.info("💡 注意：因为没有记忆功能，AI 不记得第一轮的内容");
        log.info("   后续模块会学习 ChatMemory 解决这个问题");
    }

    /**
     * 测试功能点：获取完整响应（含元数据）
     *
     * 测试场景：使用 chatResponse() 获取完整响应对象
     *
     * 预期结果：包含响应内容和元数据
     */
    @Test
    void testFullResponse() {
        log.info("========== 测试获取完整响应 ==========");
        String message = "你好";

        var chatResponse = defaultChatClient.prompt()
                .system("你是一个友好的AI助手")
                .user(message)
                .call()
                .chatResponse();

        String content = chatResponse.getResult().getOutput().getText();
        log.info("响应内容：{}", content);

        if (chatResponse.getMetadata() != null) {
            log.info("模型名称：{}", chatResponse.getMetadata().getModel());
            if (chatResponse.getMetadata().getUsage() != null) {
                log.info("Token 使用量：{}", chatResponse.getMetadata().getUsage().getTotalTokens());
            }
        }
        log.info("======================================");
    }
}
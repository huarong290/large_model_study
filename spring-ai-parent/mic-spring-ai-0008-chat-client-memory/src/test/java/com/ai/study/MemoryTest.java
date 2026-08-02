package com.ai.study;

import com.ai.study.service.MemoryService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Properties;

@SpringBootTest
@Slf4j
public class MemoryTest {

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
    private MemoryService memoryService;

    @Test
    void testMemoryWithSameSession() {
        String sessionId = "test-user-001";

        log.info("========== 测试：同一会话保持记忆 ==========");

        String response1 = memoryService.chatWithMemory(sessionId, "我叫张三");
        log.info("第一轮响应：{}", response1);

        String response2 = memoryService.chatWithMemory(sessionId, "我叫什么名字？");
        log.info("第二轮响应：{}", response2);

        log.info("💡 验证：AI 是否记得用户名字？");
    }

    @Test
    void testMemoryWithDifferentSessions() {
        String session1 = "user-001";
        String session2 = "user-002";

        log.info("========== 测试：不同会话独立 ==========");

        memoryService.chatWithMemory(session1, "我叫张三");
        memoryService.chatWithMemory(session2, "我叫李四");

        String response1 = memoryService.chatWithMemory(session1, "我叫什么名字？");
        String response2 = memoryService.chatWithMemory(session2, "我叫什么名字？");

        log.info("用户1 响应：{}", response1);
        log.info("用户2 响应：{}", response2);
    }

    @Test
    void testCompareWithAndWithoutMemory() {
        log.info("========== 测试：有记忆 vs 无记忆 ==========");

        String sessionId = "compare-test";

        memoryService.chatWithMemory(sessionId, "我叫王五");

        String withMemory = memoryService.chatWithMemory(sessionId, "我叫什么名字？");
        log.info("有记忆的响应：{}", withMemory);

        memoryService.clearMemory(sessionId);

        String withoutMemory = memoryService.chatWithoutMemory("我叫什么名字？");
        log.info("无记忆的响应：{}", withoutMemory);
    }

    @Test
    void testBatchChat() {
        log.info("========== 测试：批量对话 ==========");
        String sessionId = "batch-test";

        List<String> messages = List.of(
                "我叫张三",
                "我住在北京",
                "我的职业是程序员",
                "请总结一下我的信息"
        );

        List<String> responses = memoryService.batchChatWithMemory(sessionId, messages);
        for (int i = 0; i < responses.size(); i++) {
            log.info("第{}轮响应：{}", i + 1, responses.get(i));
        }
    }

    @Test
    void testGetHistory() {
        log.info("========== 测试：获取历史记录 ==========");
        String sessionId = "history-test";

        memoryService.chatWithMemory(sessionId, "我叫张三");
        memoryService.chatWithMemory(sessionId, "我住在北京");

        var history = memoryService.getConversationHistory(sessionId);
        log.info("历史消息数量：{}", history.size());
        history.forEach(msg -> {
            log.info("消息类型：{}，内容：{}", msg.getMessageType(), msg.getText());
        });
    }
}
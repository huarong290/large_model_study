package com.ai.study;

import com.ai.study.service.AdvisorService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

/**
 * Advisor 单元测试
 *
 * <p>测试内容：</p>
 * <ul>
 *   <li>测试带所有 Advisor 的对话</li>
 *   <li>测试带指定 Advisor 的对话</li>
 *   <li>测试无 Advisor 的对话</li>
 *   <li>测试统计信息</li>
 * </ul>
 *
 * @author AI Study
 * @since 1.0.0
 */
@SpringBootTest
@Slf4j
public class AdvisorTest {

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
    private AdvisorService advisorService;

    /**
     * 测试功能点：带所有 Advisor 的对话
     *
     * <p>预期：日志记录、性能监控、缓存、审计、限流、敏感词过滤全部生效</p>
     */
    @Test
    void testChatWithAllAdvisors() {
        log.info("========== 测试：带所有 Advisor 的对话 ==========");
        String response = advisorService.chatWithAllAdvisors("你好，请介绍一下自己");
        log.info("响应：{}", response);
        log.info("💡 查看控制台日志，可以看到所有 Advisor 的输出");
    }

    /**
     * 测试功能点：带指定 Advisor 的对话
     */
    @Test
    void testChatWithSelectedAdvisors() {
        log.info("========== 测试：带指定 Advisor 的对话 ==========");
        log.info("--- 只有日志和性能监控 ---");
        String response1 = advisorService.chatWithAdvisors("你好", "logging", "performance");
        log.info("响应：{}", response1);

        log.info("--- 只有审计日志 ---");
        String response2 = advisorService.chatWithAdvisors("介绍一下Spring AI", "audit");
        log.info("响应：{}", response2);
    }

    /**
     * 测试功能点：无 Advisor 的对话（对比测试）
     */
    @Test
    void testChatWithoutAdvisor() {
        log.info("========== 测试：无 Advisor 的对话 ==========");
        String response = advisorService.chatWithoutAdvisor("你好");
        log.info("响应：{}", response);
        log.info("💡 对比可以看到，没有 Advisor 的日志输出更少");
    }

    /**
     * 测试功能点：获取统计信息
     */
    @Test
    void testGetStats() {
        log.info("========== 测试：获取统计信息 ==========");
        var stats = advisorService.getAdvisorStats();
        log.info("统计信息：{}", stats);
    }
}
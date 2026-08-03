package com.ai.study;

import com.ai.study.service.ToolsService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.io.File;
import java.io.FileInputStream;
import java.util.Map;
import java.util.Properties;

@SpringBootTest
@Slf4j
public class ToolsTest {

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
    private ToolsService toolsService;

    /**
     * 测试功能点：天气工具调用
     */
    @Test
    void testWeatherTool() {
        log.info("========== 测试：天气工具 ==========");
        String response = toolsService.chatWithTools("北京今天天气怎么样？");
        log.info("响应：{}", response);
    }

    /**
     * 测试功能点：计算器工具调用
     */
    @Test
    void testCalculatorTool() {
        log.info("========== 测试：计算器工具 ==========");
        String response = toolsService.chatWithTools("计算 3.14 * 2 的平方");
        log.info("响应：{}", response);
    }

    /**
     * 测试功能点：时间工具调用
     */
    @Test
    void testTimeTool() {
        log.info("========== 测试：时间工具 ==========");
        String response = toolsService.chatWithTools("现在几点了？");
        log.info("响应：{}", response);
    }

    /**
     * 测试功能点：搜索工具调用
     */
    @Test
    void testSearchTool() {
        log.info("========== 测试：搜索工具 ==========");
        String response = toolsService.chatWithTools("介绍一下 Spring AI");
        log.info("响应：{}", response);
    }

    /**
     * 测试功能点：多工具协同
     */
    @Test
    void testMultipleTools() {
        log.info("========== 测试：多工具协同 ==========");
        String response = toolsService.chatWithTools("北京今天天气怎么样？然后计算 100 + 200");
        log.info("响应：{}", response);
    }

    /**
     * 测试功能点：有工具 vs 无工具对比
     */
    @Test
    void testCompare() {
        log.info("========== 测试：有工具 vs 无工具 ==========");
        String message = "北京今天天气怎么样？";
        Map<String, String> result = toolsService.compare(message);
        log.info("无工具回答：{}", result.get("withoutTools"));
        log.info("有工具回答：{}", result.get("withTools"));
        log.info("💡 结论：有工具的 AI 可以获取实时数据，无工具的 AI 只能基于训练数据猜测");
    }
}
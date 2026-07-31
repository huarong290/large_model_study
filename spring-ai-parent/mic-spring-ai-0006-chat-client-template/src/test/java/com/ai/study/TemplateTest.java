package com.ai.study;

import com.ai.study.service.TemplateService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@SpringBootTest
@Slf4j
public class TemplateTest {

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
    private TemplateService templateService;

    /**
     * 测试功能点：命名占位符模板
     */
    @Test
    void testNamedTemplate() {
        log.info("========== 测试命名占位符模板 ==========");

        Map<String, Object> params = new HashMap<>();
        params.put("name", "小智");
        params.put("age", "25");
        params.put("city", "北京");
        params.put("hobby", "编程、阅读");

        String response = templateService.chatWithNamedTemplate("introduce", params);
        log.info("自我介绍模板响应：\n{}", response);
    }

    /**
     * 测试功能点：位置占位符模板
     */
    @Test
    void testPositionalTemplate() {
        log.info("========== 测试位置占位符模板 ==========");

        String response = templateService.chatWithPositionalTemplate(
                "translate", "Hello, world!", "中文", "正式");
        log.info("翻译模板响应：\n{}", response);
    }

    /**
     * 测试功能点：批量测试所有模板
     */
    @Test
    void testBatchTemplates() {
        log.info("========== 批量测试所有模板 ==========");

        Map<String, String> results = templateService.batchTest();
        results.forEach((id, response) -> {
            log.info("--- 模板 [{}] 响应 ---\n{}\n", id,
                    response.length() > 200 ? response.substring(0, 200) + "..." : response);
        });
    }

    /**
     * 测试功能点：模板列表
     */
    @Test
    void testTemplateList() {
        log.info("========== 测试模板列表 ==========");

        var templates = templateService.getAllTemplates();
        templates.forEach(t -> {
            log.info("ID: {}, 名称: {}, 参数: {}", t.getId(), t.getName(), t.getParams());
        });
    }
}
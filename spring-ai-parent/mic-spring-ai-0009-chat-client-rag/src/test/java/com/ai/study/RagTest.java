package com.ai.study;

import com.ai.study.service.RagService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

@SpringBootTest
@Slf4j
public class RagTest {

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
    private RagService ragService;

    /**
     * 测试功能点：构建知识库
     */
    @Test
    void testBuildKnowledgeBase() {
        log.info("========== 测试：构建知识库 ==========");

        String text = """
                Spring AI 是一个用于构建 AI 应用程序的框架。
                它提供了统一的 API 来访问各种 AI 模型。
                支持的主要模型包括：OpenAI、Ollama、DeepSeek、Google Gemini。
                Spring AI 的核心功能包括：聊天、嵌入、图像生成、音频处理等。
                RAG（检索增强生成）是 Spring AI 的重要功能之一。
                通过 RAG，可以基于私有数据构建智能问答系统。
                """;

        var result = ragService.buildKnowledgeBase(text, "test", "Spring AI 简介");
        log.info("构建结果：{}", result);
    }

    /**
     * 测试功能点：RAG 问答
     */
    @Test
    void testAsk() {
        log.info("========== 测试：RAG 问答 ==========");

        // 1. 构建知识库
        String knowledge = """
                Spring AI 是一个用于构建 AI 应用程序的框架。
                它提供了统一的 API 来访问各种 AI 模型。
                RAG（检索增强生成）是 Spring AI 的重要功能。
                Spring AI 支持向量数据库集成，包括 PGVector、Redis、Milvus 等。
                """;
        ragService.buildKnowledgeBase(knowledge, "test", "Spring AI 功能");

        // 2. 问答
        String answer = ragService.ask("Spring AI 支持哪些向量数据库？");
        log.info("问题：Spring AI 支持哪些向量数据库？");
        log.info("回答：{}", answer);
    }

    /**
     * 测试功能点：有 RAG vs 无 RAG 对比
     */
    @Test
    void testCompare() {
        log.info("========== 测试：有 RAG vs 无 RAG ==========");

        // 1. 构建知识库
        String knowledge = """
                Spring AI 的 RAG 功能支持以下向量数据库：
                1. PGVector - PostgreSQL 向量扩展
                2. Redis - Redis 向量存储
                3. Milvus - 专用向量数据库
                """;
        ragService.buildKnowledgeBase(knowledge, "test", "向量数据库");

        // 2. 对比测试
        var result = ragService.compare("Spring AI RAG 支持哪些向量数据库？");
        log.info("无 RAG 回答：{}", result.get("withoutRAG"));
        log.info("有 RAG 回答：{}", result.get("withRAG"));
        log.info("💡 结论：有 RAG 的回答基于私有数据，更准确");
    }

    /**
     * 测试功能点：状态检查
     */
    @Test
    void testStatus() {
        log.info("========== 测试：状态检查 ==========");

        var stats = ragService.getStats();
        log.info("状态：{}", stats);
    }
}
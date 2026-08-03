package com.ai.study.tools;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 搜索工具（模拟）
 *
 * <p>功能说明：</p>
 * <ul>
 *   <li>模拟搜索引擎</li>
 *   <li>基于关键词匹配</li>
 *   <li>演示工具的多参数使用</li>
 * </ul>
 *
 * @author AI Study
 * @since 1.0.0
 */
@Component
@Slf4j
public class SearchTool {

    /**
     * 模拟知识库
     */
    private static final Map<String, String> KNOWLEDGE_BASE = new HashMap<>();

    static {
        KNOWLEDGE_BASE.put("Spring AI", "Spring AI 是 Spring 官方推出的 AI 应用开发框架，提供统一的 API 接入多种 AI 模型。");
        KNOWLEDGE_BASE.put("RAG", "RAG（检索增强生成）是一种结合检索和生成的 AI 技术，可以提高回答的准确性和相关性。");
        KNOWLEDGE_BASE.put("Function Calling", "Function Calling 是 AI 模型调用外部工具的能力，让 AI 能够执行实际操作。");
        KNOWLEDGE_BASE.put("Java", "Java 是一种面向对象的编程语言，具有跨平台、高性能、安全性好等特点。");
        KNOWLEDGE_BASE.put("Spring Boot", "Spring Boot 是 Spring 框架的扩展，用于简化 Spring 应用的初始搭建和开发过程。");
        KNOWLEDGE_BASE.put("人工智能", "人工智能（AI）是计算机科学的一个分支，致力于创建能够执行通常需要人类智能的任务的系统。");
        KNOWLEDGE_BASE.put("机器学习", "机器学习是人工智能的核心领域，通过算法让计算机从数据中学习和改进。");
        KNOWLEDGE_BASE.put("深度学习", "深度学习是基于神经网络的机器学习方法，在图像识别、自然语言处理等领域表现优异。");
    }

    /**
     * 搜索信息
     *
     * @param query 搜索关键词
     * @param maxResults 最大结果数
     * @return 搜索结果
     */
    @Tool(name = "searchKnowledge", description = "搜索知识库中的信息，返回相关的知识点")
    public String search(
            @ToolParam(description = "搜索关键词") String query,
            @ToolParam(description = "最大返回结果数量，默认为3", required = false) Integer maxResults) {

        log.info("🔍 调用搜索工具：query={}, maxResults={}", query, maxResults);

        if (query == null || query.trim().isEmpty()) {
            return "请提供搜索关键词";
        }

        int limit = maxResults != null ? maxResults : 3;
        StringBuilder result = new StringBuilder();

        int count = 0;
        for (Map.Entry<String, String> entry : KNOWLEDGE_BASE.entrySet()) {
            if (entry.getKey().contains(query) || entry.getValue().contains(query)) {
                result.append("【").append(entry.getKey()).append("】\n");
                result.append(entry.getValue()).append("\n\n");
                count++;
                if (count >= limit) {
                    break;
                }
            }
        }

        if (count == 0) {
            return String.format("未找到与 '%s' 相关的信息", query);
        }

        return String.format("找到 %d 条相关信息：\n\n%s", count, result.toString());
    }
}
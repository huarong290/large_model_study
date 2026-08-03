package com.ai.study.service;

import java.util.Map;

/**
 * RAG 服务接口
 *
 * <p>功能说明：</p>
 * <ul>
 *   <li>构建知识库</li>
 *   <li>检索增强生成（RAG）</li>
 *   <li>基于私有数据的智能问答</li>
 *   <li>有 RAG vs 无 RAG 对比测试</li>
 * </ul>
 *
 * <p>核心流程：</p>
 * <ol>
 *   <li>用户提问</li>
 *   <li>向量检索相关文档</li>
 *   <li>构建增强 Prompt</li>
 *   <li>AI 生成回答</li>
 * </ol>
 *
 * @author AI Study
 * @since 1.0.0
 */
public interface RagService {

    /**
     * 构建知识库（从文本）
     *
     * @param text 文档文本
     * @param source 来源
     * @param title 标题
     * @return 构建结果
     */
    Map<String, Object> buildKnowledgeBase(String text, String source, String title);

    /**
     * 构建知识库（从文件）
     *
     * @param filePath 文件路径
     * @return 构建结果
     */
    Map<String, Object> buildKnowledgeBaseFromFile(String filePath);

    /**
     * RAG 问答
     *
     * @param question 用户问题
     * @return AI 回答
     */
    String ask(String question);

    /**
     * RAG 问答（带 System 消息）
     *
     * @param question 用户问题
     * @param system System 消息
     * @return AI 回答
     */
    String askWithSystem(String question, String system);

    /**
     * 对比测试：有 RAG vs 无 RAG
     *
     * @param question 用户问题
     * @return 对比结果
     */
    Map<String, String> compare(String question);

    /**
     * 获取知识库统计信息
     *
     * @return 统计信息
     */
    Map<String, Object> getStats();
}

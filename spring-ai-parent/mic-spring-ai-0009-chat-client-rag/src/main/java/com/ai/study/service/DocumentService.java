package com.ai.study.service;

import com.ai.study.model.Document;

import java.util.List;

/**
 * 文档管理服务接口
 *
 * <p>功能说明：</p>
 * <ul>
 *   <li>加载文档（从文本或文件）</li>
 *   <li>文档分块处理</li>
 *   <li>文档存储和检索</li>
 *   <li>向量检索（基于关键词匹配模拟）</li>
 * </ul>
 *
 * @author AI Study
 * @since 1.0.0
 */
public interface DocumentService {

    /**
     * 加载文档（从文本）
     *
     * @param text 文档文本
     * @param source 来源
     * @param title 标题
     * @return 分块后的文档列表
     */
    List<Document> loadDocument(String text, String source, String title);

    /**
     * 加载文档（从文件）
     *
     * @param filePath 文件路径
     * @return 分块后的文档列表
     */
    List<Document> loadDocumentFromFile(String filePath);

    /**
     * 根据文档 ID 获取文档内容
     *
     * @param docId 文档 ID
     * @return 文档内容
     */
    String getContent(String docId);

    /**
     * 根据文档 ID 获取文档
     *
     * @param docId 文档 ID
     * @return 文档对象
     */
    Document getDocument(String docId);

    /**
     * 获取所有文档
     *
     * @return 文档列表
     */
    List<Document> getAllDocuments();

    /**
     * 获取文档数量
     *
     * @return 文档数量
     */
    int getDocumentCount();

    /**
     * 清除所有文档
     */
    void clearAllDocuments();

    /**
     * 向量检索（模拟语义检索）
     *
     * @param query 查询文本
     * @param topK 返回结果数量
     * @return 相关文档列表
     */
    List<Document> search(String query, int topK);
}

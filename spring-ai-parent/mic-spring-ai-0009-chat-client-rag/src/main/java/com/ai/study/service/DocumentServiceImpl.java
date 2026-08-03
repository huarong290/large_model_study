package com.ai.study.service.impl;

import cn.hutool.core.io.FileUtil;
import com.ai.study.model.Document;
import com.ai.study.service.DocumentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 文档管理服务实现类
 *
 * <p>功能说明：</p>
 * <ul>
 *   <li>使用 ConcurrentHashMap 实现内存存储</li>
 *   <li>支持基于句子边界的智能分块</li>
 *   <li>提供关键词匹配的模拟向量检索</li>
 * </ul>
 *
 * @author AI Study
 * @since 1.0.0
 */
@Service
@Slf4j
public class DocumentServiceImpl implements DocumentService {

    /**
     * 文档存储 Map
     * key: 文档 ID -> value: Document 对象
     */
    private final Map<String, Document> documentStore = new ConcurrentHashMap<>();

    /**
     * 文档内容存储 Map（用于快速检索）
     * key: 文档 ID -> value: 文档内容
     */
    private final Map<String, String> contentStore = new ConcurrentHashMap<>();

    /**
     * 文档分块大小（字符数）
     */
    @Value("${rag.chunk-size:500}")
    private int chunkSize;

    /**
     * 分块重叠大小（字符数）
     */
    @Value("${rag.chunk-overlap:50}")
    private int chunkOverlap;

    @Override
    public List<Document> loadDocument(String text, String source, String title) {
        log.info("📄 加载文档：{}", title);

        // 文本分块
        List<String> chunks = splitText(text);
        List<Document> documents = new ArrayList<>();

        for (int i = 0; i < chunks.size(); i++) {
            String id = UUID.randomUUID().toString();
            Document doc = new Document();
            doc.setId(id);
            doc.setContent(chunks.get(i));
            doc.setSource(source);
            doc.setTitle(title + " (块" + (i + 1) + "/" + chunks.size() + ")");
            doc.setCategory("knowledge");
            doc.setChunkIndex(i + 1);
            doc.setTotalChunks(chunks.size());
            doc.setCreateTime(System.currentTimeMillis());
            doc.setKeywords(extractKeywords(chunks.get(i)));

            documents.add(doc);
            documentStore.put(id, doc);
            contentStore.put(id, chunks.get(i));
        }

        log.info("📄 文档加载完成，共 {} 个块", documents.size());
        return documents;
    }

    @Override
    public List<Document> loadDocumentFromFile(String filePath) {
        log.info("📄 从文件加载文档：{}", filePath);

        String content = FileUtil.readString(filePath, StandardCharsets.UTF_8);
        String fileName = FileUtil.getName(filePath);
        String title = fileName.substring(0, fileName.lastIndexOf('.'));

        return loadDocument(content, filePath, title);
    }

    /**
     * 文本分块
     *
     * <p>分块策略：</p>
     * <ul>
     *   <li>按 chunkSize 分割文本</li>
     *   <li>在句子边界（。！？；\n）处分割，保持语义完整</li>
     *   <li>chunkOverlap 保证相邻块之间有重叠，避免信息丢失</li>
     * </ul>
     *
     * @param text 原始文本
     * @return 分块后的文本列表
     */
    private List<String> splitText(String text) {
        List<String> chunks = new ArrayList<>();

        if (text.length() <= chunkSize) {
            chunks.add(text);
            return chunks;
        }

        int start = 0;
        while (start < text.length()) {
            int end = Math.min(start + chunkSize, text.length());

            // 尽量在句子边界分割
            if (end < text.length()) {
                int sentenceEnd = findSentenceBoundary(text, end);
                if (sentenceEnd > start && sentenceEnd - start <= chunkSize + 50) {
                    end = sentenceEnd;
                }
            }

            chunks.add(text.substring(start, end));
            start = end - chunkOverlap;
            if (start < 0) start = 0;

            // 防止死循环
            if (start >= end) {
                break;
            }
        }

        return chunks;
    }

    /**
     * 查找句子边界
     *
     * @param text 文本
     * @param position 起始位置
     * @return 句子结束位置
     */
    private int findSentenceBoundary(String text, int position) {
        String punctuation = "。！？；\n";
        for (int i = position; i < Math.min(position + 50, text.length()); i++) {
            char c = text.charAt(i);
            if (punctuation.indexOf(c) != -1) {
                return i + 1;
            }
        }
        return position;
    }

    /**
     * 提取关键词（简单实现）
     *
     * <p>实际生产环境可使用 NLP 工具或 AI 提取关键词</p>
     *
     * @param text 文本内容
     * @return 关键词列表
     */
    private List<String> extractKeywords(String text) {
        // 简单实现：提取常见关键词
        // 实际生产环境可以用 NLP 工具
        return new ArrayList<>();
    }

    @Override
    public String getContent(String docId) {
        return contentStore.get(docId);
    }

    @Override
    public Document getDocument(String docId) {
        return documentStore.get(docId);
    }

    @Override
    public List<Document> getAllDocuments() {
        return new ArrayList<>(documentStore.values());
    }

    @Override
    public int getDocumentCount() {
        return documentStore.size();
    }

    @Override
    public void clearAllDocuments() {
        documentStore.clear();
        contentStore.clear();
        log.info("🗑️ 已清除所有文档");
    }

    @Override
    public List<Document> search(String query, int topK) {
        log.info("🔍 检索文档：{}", query);

        if (documentStore.isEmpty()) {
            log.warn("⚠️ 文档库为空，请先构建知识库");
            return new ArrayList<>();
        }

        // 关键词匹配（模拟语义检索）
        String[] keywords = query.split("\\s+");
        Map<String, Integer> scoreMap = new HashMap<>();

        for (Document doc : documentStore.values()) {
            String content = contentStore.get(doc.getId());
            if (content == null) continue;

            int score = 0;
            for (String keyword : keywords) {
                if (content.contains(keyword)) {
                    score++;
                }
            }

            if (score > 0) {
                scoreMap.put(doc.getId(), score);
            }
        }

        // 按匹配度降序排序
        List<String> sortedIds = scoreMap.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .map(Map.Entry::getKey)
                .toList();

        // 取 topK 个
        List<String> topIds = sortedIds.stream()
                .limit(topK)
                .toList();

        List<Document> results = new ArrayList<>();
        for (String id : topIds) {
            Document doc = documentStore.get(id);
            if (doc != null) {
                results.add(doc);
            }
        }

        log.info("🔍 检索完成，找到 {} 个相关文档", results.size());
        return results;
    }
}
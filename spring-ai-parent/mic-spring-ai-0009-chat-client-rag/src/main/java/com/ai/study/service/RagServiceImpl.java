package com.ai.study.service;

import com.ai.study.model.Document;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * RAG 服务实现类
 *
 * <p>功能说明：</p>
 * <ul>
 *   <li>实现完整的 RAG（检索增强生成）流程</li>
 *   <li>支持从文本或文件构建知识库</li>
 *   <li>基于知识库进行智能问答</li>
 *   <li>提供有/无 RAG 的对比测试</li>
 * </ul>
 *
 * <p>RAG 工作流程：</p>
 * <ol>
 *   <li>用户提问</li>
 *   <li>向量检索（模拟）相关文档</li>
 *   <li>将文档内容构建为上下文</li>
 *   <li>构建增强 Prompt（文档 + 问题）</li>
 *   <li>AI 基于文档生成回答</li>
 * </ol>
 *
 * @author AI Study
 * @since 1.0.0
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class RagServiceImpl implements RagService {

    /**
     * 默认模型客户端（Ollama 本地模型）
     */
    @Qualifier("ollamaChatClient")
    private final ChatClient chatClient;

    /**
     * 文档管理服务
     */
    private final DocumentService documentService;

    /**
     * 检索返回的文档数量（Top-K）
     */
    @Value("${rag.top-k:3}")
    private int topK;

    @Override
    public Map<String, Object> buildKnowledgeBase(String text, String source, String title) {
        log.info("📚 构建知识库：{}", title);

        // 清空旧文档
        documentService.clearAllDocuments();

        // 加载新文档
        List<Document> documents = documentService.loadDocument(text, source, title);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "知识库构建完成");
        result.put("documentCount", documents.size());
        result.put("title", title);
        result.put("source", source);
        return result;
    }

    @Override
    public Map<String, Object> buildKnowledgeBaseFromFile(String filePath) {
        log.info("📚 从文件构建知识库：{}", filePath);

        documentService.clearAllDocuments();
        List<Document> documents = documentService.loadDocumentFromFile(filePath);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "知识库构建完成");
        result.put("documentCount", documents.size());
        result.put("filePath", filePath);
        return result;
    }

    @Override
    public String ask(String question) {
        log.info("❓ RAG 问答：{}", question);

        // 1. 检索相关文档
        List<Document> relevantDocs = documentService.search(question, topK);

        if (relevantDocs.isEmpty()) {
            log.warn("⚠️ 未找到相关文档，使用常规回答");
            return chatClient.prompt()
                    .user(question)
                    .call()
                    .content();
        }

        // 2. 构建上下文
        StringBuilder context = new StringBuilder();
        for (int i = 0; i < relevantDocs.size(); i++) {
            context.append("【文档").append(i + 1).append("】\n");
            context.append(relevantDocs.get(i).getContent());
            context.append("\n\n");
        }

        log.info("📚 检索到 {} 个相关文档", relevantDocs.size());

        // 3. 构建增强 Prompt
        String enhancedPrompt = String.format("""
                请基于以下文档内容回答问题。
                
                === 文档内容 ===
                %s
                
                === 问题 ===
                %s
                
                === 要求 ===
                1. 如果文档中有相关信息，请基于文档回答
                2. 如果文档中没有相关信息，请说明"文档中没有相关信息"
                3. 回答要准确、简洁
                
                回答：
                """, context.toString().trim(), question);

        // 4. 生成回答
        return chatClient.prompt()
                .user(enhancedPrompt)
                .call()
                .content();
    }

    @Override
    public String askWithSystem(String question, String system) {
        log.info("❓ RAG 问答（带System）：{}", question);

        List<Document> relevantDocs = documentService.search(question, topK);

        if (relevantDocs.isEmpty()) {
            return chatClient.prompt()
                    .system(system)
                    .user(question)
                    .call()
                    .content();
        }

        StringBuilder context = new StringBuilder();
        for (int i = 0; i < relevantDocs.size(); i++) {
            context.append("【文档").append(i + 1).append("】\n");
            context.append(relevantDocs.get(i).getContent());
            context.append("\n\n");
        }

        String enhancedPrompt = String.format("""
                请基于以下文档内容回答问题。
                
                === 文档内容 ===
                %s
                
                === 问题 ===
                %s
                
                请用中文回答。
                """, context.toString().trim(), question);

        return chatClient.prompt()
                .system(system)
                .user(enhancedPrompt)
                .call()
                .content();
    }

    @Override
    public Map<String, String> compare(String question) {
        log.info("📊 对比测试：{}", question);

        Map<String, String> result = new HashMap<>();

        // 1. 无 RAG（直接回答）
        String withoutRag = chatClient.prompt()
                .user(question)
                .call()
                .content();
        result.put("withoutRAG", withoutRag);

        // 2. 有 RAG（检索增强）
        String withRag = ask(question);
        result.put("withRAG", withRag);

        return result;
    }

    @Override
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("documentCount", documentService.getDocumentCount());
        stats.put("topK", topK);
        stats.put("chunkSize", documentService.getAllDocuments().isEmpty() ? 0 :
                documentService.getAllDocuments().get(0).getTotalChunks());
        return stats;
    }
}
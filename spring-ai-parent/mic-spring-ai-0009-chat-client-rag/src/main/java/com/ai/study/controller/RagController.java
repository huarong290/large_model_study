package com.ai.study.controller;

import com.ai.study.model.BuildKnowledgeRequest;
import com.ai.study.model.RagRequest;
import com.ai.study.service.RagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * RAG 控制器
 *
 * <p>功能说明：</p>
 * <ul>
 *   <li>提供知识库构建接口</li>
 *   <li>提供 RAG 问答接口</li>
 *   <li>提供有/无 RAG 对比接口</li>
 *   <li>提供状态查询接口</li>
 * </ul>
 *
 * @author AI Study
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/rag")
@Slf4j
@RequiredArgsConstructor
public class RagController {

    private final RagService ragService;

    // ================================================================
    // 1. 构建知识库
    // ================================================================

    /**
     * 构建知识库
     *
     * <p>将提供的文本内容加载到知识库中，用于后续的 RAG 问答。</p>
     * <p>注意：每次构建会清空之前的知识库。</p>
     *
     * @param request 构建请求（包含文本、标题、来源）
     * @return 构建结果
     */
    @PostMapping("/build")
    public Map<String, Object> buildKnowledgeBase(@RequestBody BuildKnowledgeRequest request) {
        String text = request.getText();
        String source = request.getSource() != null ? request.getSource() : "user-input";
        String title = request.getTitle() != null ? request.getTitle() : "知识文档";

        log.info("📚 构建知识库请求：{}", title);

        try {
            return ragService.buildKnowledgeBase(text, source, title);
        } catch (Exception e) {
            log.error("❌ 构建知识库失败：{}", e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            return error;
        }
    }

    // ================================================================
    // 2. RAG 问答
    // ================================================================

    /**
     * RAG 问答
     *
     * <p>基于已构建的知识库回答问题。</p>
     * <p>流程：检索相关文档 → 构建增强 Prompt → AI 生成回答</p>
     *
     * @param request 问答请求（包含问题）
     * @return 问答结果
     */
    @PostMapping("/ask")
    public Map<String, Object> ask(@RequestBody RagRequest request) {
        String question = request.getQuestion();

        log.info("❓ RAG 问答请求：{}", question);

        try {
            String answer = ragService.ask(question);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("question", question);
            result.put("answer", answer);
            return result;

        } catch (Exception e) {
            log.error("❌ RAG 问答失败：{}", e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            return error;
        }
    }

    // ================================================================
    // 3. 对比测试
    // ================================================================

    /**
     * 对比测试：有 RAG vs 无 RAG
     *
     * <p>用同一个问题分别测试有 RAG 和无 RAG 的回答效果。</p>
     * <p>用于直观展示 RAG 的价值。</p>
     *
     * @param request 对比请求（包含问题）
     * @return 对比结果
     */
    @PostMapping("/compare")
    public Map<String, Object> compare(@RequestBody RagRequest request) {
        String question = request.getQuestion();

        log.info("📊 对比测试请求：{}", question);

        try {
            Map<String, String> result = ragService.compare(question);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("question", question);
            response.put("withoutRAG", result.get("withoutRAG"));
            response.put("withRAG", result.get("withRAG"));
            response.put("conclusion", "有 RAG 的回答基于私有数据，更准确、更相关");
            return response;

        } catch (Exception e) {
            log.error("❌ 对比测试失败：{}", e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            return error;
        }
    }

    // ================================================================
    // 4. 状态检查
    // ================================================================

    /**
     * 获取知识库状态
     *
     * <p>返回当前知识库的文档数量等信息。</p>
     *
     * @return 状态信息
     */
    @GetMapping("/status")
    public Map<String, Object> status() {
        Map<String, Object> stats = ragService.getStats();
        stats.put("status", "UP");
        stats.put("tip", "文档数量 > 0 表示知识库已构建");
        return stats;
    }
}
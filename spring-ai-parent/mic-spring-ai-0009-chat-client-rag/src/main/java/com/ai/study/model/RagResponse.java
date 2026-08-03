package com.ai.study.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * RAG 响应模型
 *
 * <p>用于返回 RAG 问答结果，包含回答和相关文档信息。</p>
 *
 * @author AI Study
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "RAG 响应模型")
public class RagResponse {

    /**
     * 是否成功
     */
    @Schema(description = "是否成功", example = "true")
    private boolean success;

    /**
     * 用户问题
     */
    @Schema(description = "用户问题", example = "Spring AI 是什么？")
    private String question;

    /**
     * AI 回答
     */
    @Schema(description = "AI 回答", example = "Spring AI 是一个用于构建 AI 应用程序的框架...")
    private String answer;

    /**
     * 检索到的相关文档列表
     */
    @Schema(description = "检索到的相关文档列表")
    private List<Document> relevantDocuments;

    /**
     * 检索到的文档数量
     */
    @Schema(description = "检索到的文档数量", example = "3")
    private Integer documentCount;

    /**
     * 错误信息（如有）
     */
    @Schema(description = "错误信息", example = "知识库为空，请先构建知识库")
    private String error;

    /**
     * 响应时间戳
     */
    @Schema(description = "响应时间戳", example = "1700000000000")
    private Long timestamp;
}
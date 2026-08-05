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
 * <p>RAG 问答的返回结果。</p>
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
    @Schema(description = "用户问题", example = "Spring AI 支持哪些向量数据库？")
    private String question;

    /**
     * AI 回答
     */
    @Schema(description = "AI 回答",
            example = "Spring AI 支持 PGVector、Redis、Milvus 等向量数据库。")
    private String answer;

    /**
     * 检索到的相关文档
     */
    @Schema(description = "检索到的相关文档")
    private List<Document> relevantDocuments;

    /**
     * 检索到的文档数量
     */
    @Schema(description = "检索到的文档数量", example = "3")
    private Integer documentCount;

    /**
     * 错误信息
     */
    @Schema(description = "错误信息",
            example = "知识库为空，请先构建知识库")
    private String error;

    /**
     * 响应时间戳
     */
    @Schema(description = "响应时间戳", example = "1700000000000")
    private Long timestamp;
}
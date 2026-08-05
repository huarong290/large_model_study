package com.ai.study.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * RAG 问答请求模型
 *
 * <p>用于向 RAG 系统提问。</p>
 *
 * @author AI Study
 * @since 1.0.0
 */
@Data
@Schema(description = "RAG 问答请求模型")
public class RagRequest {

    /**
     * 用户问题
     */
    @Schema(description = "用户问题",
            example = "Spring AI 支持哪些向量数据库？",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String question;
}
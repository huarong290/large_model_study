package com.ai.study.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * RAG 请求模型
 *
 * <p>用于接收构建知识库和问答的请求参数。</p>
 *
 * @author AI Study
 * @since 1.0.0
 */
@Data
@Schema(description = "RAG 请求模型")
public class RagRequest {

    /**
     * 文档文本内容
     */
    @Schema(description = "文档文本内容", required = true, example = "Spring AI 是一个用于构建 AI 应用程序的框架...")
    private String text;

    /**
     * 用户问题
     */
    @Schema(description = "用户问题", required = true, example = "Spring AI 是什么？")
    private String question;

    /**
     * 文档来源
     */
    @Schema(description = "文档来源", example = "用户输入")
    private String source;

    /**
     * 文档标题
     */
    @Schema(description = "文档标题", example = "Spring AI 介绍")
    private String title;
}
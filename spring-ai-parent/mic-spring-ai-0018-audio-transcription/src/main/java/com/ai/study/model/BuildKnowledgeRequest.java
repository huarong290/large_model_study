package com.ai.study.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 构建知识库请求模型
 * 用于向知识库添加文档。
 */
@Data
@Schema(description = "构建知识库请求模型")
public class BuildKnowledgeRequest {

    /**
     * 文档文本内容
     */
    @Schema(description = "文档文本内容", example = "Spring AI 是一个用于构建 AI 应用程序的框架...", requiredMode = Schema.RequiredMode.REQUIRED)
    private String text;

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


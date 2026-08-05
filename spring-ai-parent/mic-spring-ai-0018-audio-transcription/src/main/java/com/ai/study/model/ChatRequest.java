package com.ai.study.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 聊天请求模型
 * 用于接收客户端发送的聊天请求参数。
 */
@Data
@Schema(description = "聊天请求模型")
public class ChatRequest {

    /**
     * 会话唯一标识
     * 相同 ID 共享对话历史，若为空系统自动生成。
     */
    @Schema(description = "会话唯一标识", example = "user-001")
    private String sessionId;

    /**
     * 用户消息内容
     * 用户输入的问题或指令。
     */
    @Schema(description = "用户消息内容", example = "你好，请介绍一下自己", requiredMode = Schema.RequiredMode.REQUIRED)
    private String message;

    /**
     * 使用的模型
     * 可选：ollama / deepseek / auto
     */
    @Schema(description = "使用的模型", example = "auto", defaultValue = "ollama", allowableValues = {"ollama", "deepseek", "auto"})
    private String model;

    /**
     * 是否启用流式响应
     * true：逐字返回；false：完整结果。
     */
    @Schema(description = "是否启用流式响应", example = "false", defaultValue = "false")
    private Boolean stream;
}


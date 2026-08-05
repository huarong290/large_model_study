package com.ai.study.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 聊天响应模型
 *
 * 用于返回 AI 对话结果及相关信息。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "聊天响应模型")
public class ChatResponse {

    /**
     * 是否成功
     **/
    @Schema(description = "是否成功", example = "true")
    private boolean success;

    /**
     * 会话唯一标识
     * */
    @Schema(description = "会话唯一标识", example = "user-001")
    private String sessionId;

    /**
     * 用户消息
     * */
    @Schema(description = "用户消息", example = "你好，请介绍一下自己")
    private String message;

    /**
     * AI 回答内容
     * */
    @Schema(description = "AI 回答内容", example = "你好！我是智能客服助手...")
    private String answer;

    /**
     * 使用的模型名称
     * */
    @Schema(description = "使用的模型名称", example = "Ollama")
    private String model;

    /**
     * 响应耗时（毫秒）
     * */
    @Schema(description = "响应耗时（毫秒）", example = "1523")
    private Long responseTime;

    /**
     * 是否命中缓存
     * */
    @Schema(description = "是否命中缓存", example = "false")
    private boolean fromCache;

    /**
     * 是否使用对话记忆
     * */
    @Schema(description = "是否使用对话记忆", example = "true")
    private boolean fromMemory;

    /**
     * 额外的元数据
     * */
    @Schema(description = "额外的元数据")
    private Map<String, Object> metadata;

    /**
     * 响应时间戳
     * */
    @Schema(description = "响应时间戳", example = "2024-01-15T10:30:00")
    private LocalDateTime timestamp;
}


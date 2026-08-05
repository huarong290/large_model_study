package com.ai.study.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 评估请求模型
 * 用于请求 AI 回答的质量评估。
 */
@Data
@Schema(description = "评估请求模型")
public class EvaluationRequest {

    /** 用户问题 */
    @Schema(description = "用户问题",
            example = "什么是人工智能？",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String question;

    /** AI 回答 */
    @Schema(description = "AI 回答",
            example = "人工智能是计算机科学的一个分支...",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String answer;

    /** 参考回答（可选） */
    @Schema(description = "参考回答（可选）",
            example = "人工智能是...")
    private String referenceAnswer;

    /** 评估维度列表 */
    @Schema(description = "评估维度列表",
            example = "[\"accuracy\", \"completeness\", \"relevance\"]")
    private List<String> dimensions;

    /** 评估人 */
    @Schema(description = "评估人",
            example = "admin")
    private String evaluator;

    /** 备注 */
    @Schema(description = "备注",
            example = "测试场景下的评估")
    private String remark;
}

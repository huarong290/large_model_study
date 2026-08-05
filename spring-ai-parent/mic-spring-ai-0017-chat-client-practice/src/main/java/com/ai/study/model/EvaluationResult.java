package com.ai.study.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 评估结果模型
 *
 * <p>AI 回答质量评估的完整结果。</p>
 *
 * @author AI Study
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "评估结果模型")
public class EvaluationResult {

    /**
     * 用户问题
     */
    @Schema(description = "用户问题", example = "什么是人工智能？")
    private String question;

    /**
     * AI 回答
     */
    @Schema(description = "AI 回答", example = "人工智能是...")
    private String answer;

    /**
     * 各维度评分
     *
     * <p>key：维度名称，value：分数（1-10）</p>
     * <p>示例：{"准确性": 8.5, "完整性": 7.0, "相关性": 9.0}</p>
     */
    @Schema(description = "各维度评分",
            example = "{\"准确性\": 8.5, \"完整性\": 7.0, \"相关性\": 9.0}")
    private Map<String, Double> scores;

    /**
     * 综合评分
     *
     * <p>所有维度的加权平均分（1-10）。</p>
     */
    @Schema(description = "综合评分（1-10）", example = "8.2")
    private Double overallScore;

    /**
     * 评估意见
     *
     * <p>评估人员的文字评价或建议。</p>
     */
    @Schema(description = "评估意见",
            example = "回答准确，但可以补充更多细节")
    private String comment;

    /**
     * 评估方式
     *
     * <p>auto：自动评估（AI 评分）</p>
     * <p>manual：人工评估</p>
     */
    @Schema(description = "评估方式：auto / manual",
            example = "auto")
    private String evaluationType;

    /**
     * 评估时间
     */
    @Schema(description = "评估时间", example = "2024-01-15T10:30:00")
    private LocalDateTime evaluationTime;

    /**
     * 评估人（人工评估时）
     */
    @Schema(description = "评估人", example = "admin")
    private String evaluator;

    /**
     * 使用的模型
     */
    @Schema(description = "使用的模型", example = "Ollama")
    private String model;

    /**
     * 响应时间（毫秒）
     */
    @Schema(description = "响应时间（毫秒）", example = "1523")
    private Long responseTime;

    /**
     * Token 使用量
     */
    @Schema(description = "Token 使用量", example = "256")
    private Integer tokenUsage;
}
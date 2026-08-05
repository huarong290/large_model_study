package com.ai.study.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 评估结果模型
 *
 * @author AI Study
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationResult {

    /**
     * 用户问题
     */
    private String question;

    /**
     * AI 回答
     */
    private String answer;

    /**
     * 各维度评分
     */
    private Map<String, Double> scores;

    /**
     * 综合评分
     */
    private Double overallScore;

    /**
     * 评估意见
     */
    private String comment;

    /**
     * 评估方式：auto / manual
     */
    private String evaluationType;

    /**
     * 评估时间
     */
    private LocalDateTime evaluationTime;

    /**
     * 评估人（人工评估时）
     */
    private String evaluator;

    /**
     * 使用的模型
     */
    private String model;

    /**
     * 响应时间
     */
    private Long responseTime;

    /**
     * Token 使用量
     */
    private Integer tokenUsage;
}
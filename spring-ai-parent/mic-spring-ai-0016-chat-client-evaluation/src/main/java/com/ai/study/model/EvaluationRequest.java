package com.ai.study.model;

import lombok.Data;
import java.util.List;

/**
 * 评估请求模型
 *
 * @author AI Study
 * @since 1.0.0
 */
@Data
public class EvaluationRequest {

    /**
     * 用户问题
     */
    private String question;

    /**
     * AI 回答
     */
    private String answer;

    /**
     * 参考回答（可选，用于对比）
     */
    private String referenceAnswer;

    /**
     * 评估维度列表
     */
    private List<String> dimensions;

    /**
     * 评估人（人工评估时使用）
     */
    private String evaluator;

    /**
     * 备注
     */
    private String remark;
}
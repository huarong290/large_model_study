package com.ai.study.service;

import com.ai.study.model.ABTestResult;
import com.ai.study.model.EvaluationResult;

import java.util.List;
import java.util.Map;

/**
 * 评估服务接口
 *
 * @author AI Study
 * @since 1.0.0
 */
public interface EvaluationService {

    /**
     * 自动评估回答质量
     *
     * @param question 问题
     * @param answer 回答
     * @param dimensions 评估维度
     * @return 评估结果
     */
    EvaluationResult autoEvaluate(String question, String answer, List<String> dimensions);

    /**
     * 对比不同模型的回答
     *
     * @param question 问题
     * @return 对比结果
     */
    Map<String, EvaluationResult> compareModels(String question);

    /**
     * 对比不同参数配置的回答
     *
     * @param question 问题
     * @param configs 配置列表
     * @return 对比结果
     */
    Map<String, EvaluationResult> compareConfigs(String question, List<Map<String, Object>> configs);

    /**
     * A/B 测试
     *
     * @param question 问题
     * @return A/B 测试结果
     */
    ABTestResult abTest(String question);

    /**
     * 人工评估
     *
     * @param question 问题
     * @param answer 回答
     * @param scores 评分
     * @param evaluator 评估人
     * @return 评估结果
     */
    EvaluationResult manualEvaluate(String question, String answer, Map<String, Double> scores, String evaluator);

    /**
     * 获取评估统计
     *
     * @return 统计信息
     */
    Map<String, Object> getEvaluationStats();
}
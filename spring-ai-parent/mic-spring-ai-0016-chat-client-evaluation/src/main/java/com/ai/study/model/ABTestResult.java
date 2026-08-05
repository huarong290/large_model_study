package com.ai.study.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * A/B 测试结果模型
 *
 * @author AI Study
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ABTestResult {

    /**
     * A组配置
     */
    private GroupResult groupA;

    /**
     * B组配置
     */
    private GroupResult groupB;

    /**
     * 结论
     */
    private String conclusion;

    /**
     * 推荐方案
     */
    private String recommendation;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GroupResult {
        private String name;
        private String description;
        private List<EvaluationResult> results;
        private Double avgScore;
        private Double avgResponseTime;
        private int sampleCount;
    }
}

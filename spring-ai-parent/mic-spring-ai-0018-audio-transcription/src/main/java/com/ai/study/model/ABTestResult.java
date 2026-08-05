package com.ai.study.model;

import com.ai.study.model.EvaluationResult;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * A/B 测试结果模型
 *
 * <p>用于对比不同配置或模型的测试结果。</p>
 *
 * @author AI Study
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "A/B 测试结果模型")
public class ABTestResult {

    /**
     * A组结果
     */
    @Schema(description = "A组结果")
    private GroupResult groupA;

    /**
     * B组结果
     */
    @Schema(description = "B组结果")
    private GroupResult groupB;

    /**
     * 测试结论
     *
     * <p>对两组结果的对比分析。</p>
     */
    @Schema(description = "测试结论",
            example = "A组（默认配置）表现更好")
    private String conclusion;

    /**
     * 推荐方案
     *
     * <p>基于测试结果给出的建议。</p>
     */
    @Schema(description = "推荐方案",
            example = "推荐使用默认配置")
    private String recommendation;

    /**
     * 分组结果内部类
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "分组结果")
    public static class GroupResult {

        /**
         * 分组名称
         */
        @Schema(description = "分组名称", example = "A组")
        private String name;

        /**
         * 分组描述
         */
        @Schema(description = "分组描述",
                example = "默认配置（temperature=0.7）")
        private String description;

        /**
         * 评估结果列表
         */
        @Schema(description = "评估结果列表")
        private List<EvaluationResult> results;

        /**
         * 平均得分
         */
        @Schema(description = "平均得分", example = "8.5")
        private Double avgScore;

        /**
         * 平均响应时间（毫秒）
         */
        @Schema(description = "平均响应时间（毫秒）", example = "1500.0")
        private Double avgResponseTime;

        /**
         * 样本数量
         */
        @Schema(description = "样本数量", example = "10")
        private int sampleCount;
    }
}
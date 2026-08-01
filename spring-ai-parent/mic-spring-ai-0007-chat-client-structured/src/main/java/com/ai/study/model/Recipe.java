package com.ai.study.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 菜谱信息模型
 *
 * <p>用于接收 AI 生成的菜谱数据，包含食材、步骤、难度等烹饪信息。</p>
 *
 * @author AI Study
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "菜谱信息模型")
public class Recipe implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 菜品名称
     */
    @Schema(description = "菜品名称", example = "宫保鸡丁")
    private String name;

    /**
     * 所属菜系
     */
    @Schema(description = "所属菜系", example = "川菜")
    private String cuisine;

    /**
     * 准备时间（分钟）
     */
    @Schema(description = "准备时间（分钟）", example = "15")
    private Integer prepTime;

    /**
     * 烹饪时间（分钟）
     */
    @Schema(description = "烹饪时间（分钟）", example = "20")
    private Integer cookTime;

    /**
     * 总耗时（分钟）
     */
    @Schema(description = "总耗时（分钟）", example = "35")
    private Integer totalTime;

    /**
     * 所需食材列表
     */
    @Schema(description = "所需食材列表", example = "[\"鸡胸肉\", \"花生\", \"干辣椒\"]")
    private List<String> ingredients;

    /**
     * 烹饪步骤列表
     */
    @Schema(description = "烹饪步骤列表", example = "[\"将鸡肉切丁\", \"调制酱汁\", \"翻炒鸡肉\"]")
    private List<String> steps;

    /**
     * 烹饪小贴士列表
     */
    @Schema(description = "烹饪小贴士", example = "[\"鸡肉提前腌制更入味\", \"火候要适中\"]")
    private List<String> tips;

    /**
     * 难度等级
     */
    @Schema(description = "难度等级", example = "中等", allowableValues = {"简单", "中等", "困难"})
    private String difficulty;

    /**
     * 菜品简介
     */
    @Schema(description = "菜品简介", example = "宫保鸡丁是川菜经典菜品，以麻辣鲜香著称...")
    private String description;
}
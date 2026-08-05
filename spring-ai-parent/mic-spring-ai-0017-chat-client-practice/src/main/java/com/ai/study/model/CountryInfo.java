package com.ai.study.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 国家信息模型
 *
 * <p>用于接收 AI 生成的结构化国家数据。</p>
 *
 * @author AI Study
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "国家信息模型")
public class CountryInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 国家名称
     */
    @Schema(description = "国家名称", example = "中国")
    private String name;

    /**
     * 首都城市
     */
    @Schema(description = "首都城市", example = "北京")
    private String capital;

    /**
     * 国土面积
     */
    @Schema(description = "国土面积（平方公里）", example = "960万")
    private String area;

    /**
     * 人口数量
     */
    @Schema(description = "人口数量", example = "14.1亿")
    private String population;

    /**
     * 官方语言
     */
    @Schema(description = "官方语言", example = "中文")
    private String language;

    /**
     * 货币单位
     */
    @Schema(description = "货币单位", example = "人民币")
    private String currency;

    /**
     * 时区
     */
    @Schema(description = "时区", example = "UTC+8")
    private String timezone;

    /**
     * 主要城市列表
     */
    @Schema(description = "主要城市列表", example = "[\"北京\", \"上海\", \"广州\"]")
    private List<String> majorCities;

    /**
     * 国家简介
     */
    @Schema(description = "国家简介",
            example = "中国是一个拥有悠久历史和灿烂文化的文明古国...")
    private String description;
}
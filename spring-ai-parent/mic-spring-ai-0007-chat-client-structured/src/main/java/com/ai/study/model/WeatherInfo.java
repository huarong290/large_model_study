package com.ai.study.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 天气信息模型
 *
 * <p>用于接收 AI 生成的天气数据，包含温度、湿度、风力等气象信息。</p>
 *
 * @author AI Study
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "天气信息模型")
public class WeatherInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 城市名称
     */
    @Schema(description = "城市名称", example = "北京")
    private String city;

    /**
     * 日期
     */
    @Schema(description = "日期", example = "2024-01-15")
    private String date;

    /**
     * 当前温度（摄氏度）
     */
    @Schema(description = "当前温度（摄氏度）", example = "25.5")
    private Double temperature;

    /**
     * 天气状况描述
     */
    @Schema(description = "天气状况", example = "晴朗")
    private String weather;

    /**
     * 湿度百分比
     */
    @Schema(description = "湿度百分比", example = "65")
    private Integer humidity;

    /**
     * 风速（公里/小时）
     */
    @Schema(description = "风速（公里/小时）", example = "15")
    private Integer windSpeed;

    /**
     * 风向
     */
    @Schema(description = "风向", example = "东南风")
    private String windDirection;

    /**
     * 日出时间
     */
    @Schema(description = "日出时间", example = "06:30")
    private String sunrise;

    /**
     * 日落时间
     */
    @Schema(description = "日落时间", example = "18:00")
    private String sunset;

    /**
     * 生活小贴士列表
     */
    @Schema(description = "生活小贴士", example = "[\"注意防晒\", \"适合户外活动\"]")
    private List<String> tips;
}
package com.ai.study.records;

import org.springframework.ai.tool.annotation.ToolParam;
/**
 * 天气查询工具请求参数
 *
 * @author AI Study
 * @since 1.0.0
 */
public record WeatherRequestRecord(
        @ToolParam(description = "城市名称，如：北京、上海、广州")
        String city
) {
}
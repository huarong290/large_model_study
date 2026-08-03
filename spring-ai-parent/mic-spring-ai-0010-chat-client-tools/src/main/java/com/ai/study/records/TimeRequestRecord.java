package com.ai.study.records;

import org.springframework.ai.tool.annotation.ToolParam;
/**
 * 时间工具请求参数
 *
 * @author AI Study
 * @since 1.0.0
 */
public record TimeRequestRecord(
        @ToolParam(description = "城市名称，用于确定时区，如：北京、纽约、伦敦", required = false)
        String city,

        @ToolParam(description = "时间格式，如：yyyy-MM-dd HH:mm:ss", required = false)
        String format
) {
}
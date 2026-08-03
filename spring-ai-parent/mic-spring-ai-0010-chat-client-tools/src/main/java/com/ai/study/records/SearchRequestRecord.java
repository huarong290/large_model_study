package com.ai.study.records;

import org.springframework.ai.tool.annotation.ToolParam;

/**
 * 搜索工具请求参数
 *
 * @author AI Study
 * @since 1.0.0
 */
public record SearchRequestRecord(
        @ToolParam(description = "搜索关键词")
        String query,

        @ToolParam(description = "最大返回结果数量，默认为3", required = false)
        Integer maxResults
) {
}
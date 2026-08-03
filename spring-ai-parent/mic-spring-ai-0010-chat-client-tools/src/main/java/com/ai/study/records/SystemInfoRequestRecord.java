package com.ai.study.records;

import org.springframework.ai.tool.annotation.ToolParam;

/**
 * 系统信息工具请求参数
 *
 * @author AI Study
 * @since 1.0.0
 */
public record SystemInfoRequestRecord(
        @ToolParam(description = "信息类型：all（全部）、memory（内存）、os（操作系统）、jvm（JVM）", required = false)
        String type
) {
}
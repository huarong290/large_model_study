package com.ai.study.records;

import org.springframework.ai.tool.annotation.ToolParam;
/**
 * 计算器工具请求参数
 *
 * @author AI Study
 * @since 1.0.0
 */
public record CalculatorRequestRecord(
        @ToolParam(description = "数学表达式，如：2 + 3 * 4 或 (1 + 2) * 3")
        String expression
) {
}
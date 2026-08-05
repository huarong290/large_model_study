package com.ai.study.tools;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.stereotype.Component;

/**
 * 数学计算器工具类 (Spring AI Tool)
 * <p>
 * 为 AI 模型提供准确的数学表达式求值能力，解决大语言模型直接进行复杂算术计算容易出错的问题。
 * 内部采用 Spring 表达式语言 (SpEL) 执行计算，兼顾安全与效率。
 * </p>
 *
 * @author AI Assistant
 * @version 1.0
 */
@Component
@Slf4j
public class CalculatorTool {

    /**
     * Spring EL 表达式解析器，替代已废弃的 Nashorn JavaScript 引擎
     */
    private final ExpressionParser parser = new SpelExpressionParser();

    /**
     * 执行数学表达式计算
     *
     * @param expression 待计算的数学算式，例如 "2 + 3 * (4 - 1)"
     * @return 计算结果的字符串形式，包含完整算式与最终结果
     */
    @Tool(name = "calculate", description = "执行数学计算，支持加减乘除和括号运算")
    public String calculate(
            @ToolParam(description = "数学表达式，如：2 + 3 * 4 或 (1 + 2) * 3") String expression) {

        log.info("🧮 调用计算器工具：expression={}", expression);

        // 校验输入参数合法性
        if (expression == null || expression.trim().isEmpty()) {
            return "请提供有效的数学表达式";
        }

        try {
            // 使用 SpEL 解析并计算算式结果
            Object result = parser.parseExpression(expression).getValue();
            return String.format("%s = %s", expression, result);

        } catch (Exception e) {
            log.error("数学算式解析异常：{}", e.getMessage());
            return String.format("计算失败：%s，请检查表达式格式", e.getMessage());
        }
    }
}
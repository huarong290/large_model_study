package com.ai.study.tools;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * 计算器工具
 *
 * <p>功能说明：</p>
 * <ul>
 *   <li>执行数学表达式计算</li>
 *   <li>支持加减乘除等基本运算</li>
 *   <li>演示工具的参数和返回值类型</li>
 * </ul>
 *
 * @author AI Study
 * @since 1.0.0
 */
@Component
@Slf4j
public class CalculatorTool {

    /**
     * 执行数学计算
     *
     * @param expression 数学表达式，如 "2 + 3 * 4"
     * @return 计算结果
     */
    @Tool(name = "calculate", description = "执行数学计算，支持加减乘除和括号运算")
    public String calculate(
            @ToolParam(description = "数学表达式，如：2 + 3 * 4 或 (1 + 2) * 3") String expression) {

        log.info("🧮 调用计算器工具：expression={}", expression);

        if (expression == null || expression.trim().isEmpty()) {
            return "请提供有效的数学表达式";
        }

        try {
            // 使用 JavaScript 引擎计算（简单场景）
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine engine = manager.getEngineByName("JavaScript");
            Object result = engine.eval(expression);

            return String.format("%s = %s", expression, result);

        } catch (ScriptException e) {
            log.error("计算失败：{}", e.getMessage());
            return String.format("计算失败：%s，请检查表达式格式", e.getMessage());
        } catch (Exception e) {
            log.error("计算异常：{}", e.getMessage());
            return "计算异常，请检查表达式是否正确";
        }
    }
}
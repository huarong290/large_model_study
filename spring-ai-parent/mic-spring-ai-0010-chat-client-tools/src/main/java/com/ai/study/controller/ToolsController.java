package com.ai.study.controller;

import com.ai.study.service.ToolsService;
import com.ai.study.tools.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 工具调用 Controller
 *
 * <p>功能说明：</p>
 * <ul>
 *   <li>提供带工具调用的对话接口</li>
 *   <li>提供对比测试接口</li>
 *   <li>提供单工具测试接口</li>
 * </ul>
 *
 * @author AI Study
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/tools")
@Slf4j
@RequiredArgsConstructor
public class ToolsController {

    private final ToolsService toolsService;
    private final WeatherTool weatherTool;
    private final CalculatorTool calculatorTool;
    private final TimeTool timeTool;
    private final SearchTool searchTool;
    private final SystemTool systemTool;

    // ================================================================
    // 1. 带工具调用的对话
    // ================================================================

    /**
     * 带工具调用的对话
     *
     * <p>AI 会自动判断是否需要调用工具。</p>
     * <p>示例：</p>
     * <ul>
     *   <li>"北京今天天气怎么样？" → 调用 WeatherTool</li>
     *   <li>"计算 3.14 * 2 的平方" → 调用 CalculatorTool</li>
     *   <li>"现在几点了？" → 调用 TimeTool</li>
     * </ul>
     *
     * @param request 请求（包含 message）
     * @return AI 响应
     */
    @PostMapping("/chat")
    public Map<String, Object> chatWithTools(@RequestBody Map<String, String> request) {
        String message = request.get("message");

        log.info("🔧 带工具调用请求：{}", message);

        try {
            String response = toolsService.chatWithTools(message);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", message);
            result.put("response", response);
            result.put("hasTools", true);
            result.put("tip", "AI 可以调用天气、计算器、时间、搜索、系统等工具");
            return result;

        } catch (Exception e) {
            log.error("❌ 工具调用失败：{}", e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            return error;
        }
    }

    // ================================================================
    // 2. 对比测试
    // ================================================================

    /**
     * 对比测试：有工具 vs 无工具
     *
     * <p>用同一个问题分别测试有工具和无工具的回答效果。</p>
     *
     * @param request 请求（包含 message）
     * @return 对比结果
     */
    @PostMapping("/compare")
    public Map<String, Object> compare(@RequestBody Map<String, String> request) {
        String message = request.get("message");

        log.info("📊 对比测试请求：{}", message);

        try {
            Map<String, String> result = toolsService.compare(message);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", message);
            response.put("withoutTools", result.get("withoutTools"));
            response.put("withTools", result.get("withTools"));
            response.put("conclusion", "有工具的 AI 可以执行实际操作，无工具的 AI 只能基于训练数据回答");
            return response;

        } catch (Exception e) {
            log.error("❌ 对比测试失败：{}", e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            return error;
        }
    }

    // ================================================================
    // 3. 单工具测试
    // ================================================================

    /**
     * 天气查询（单工具测试）
     *
     * @param request 请求（包含 city）
     * @return 天气信息
     */
    @PostMapping("/weather")
    public Map<String, Object> testWeather(@RequestBody Map<String, String> request) {
        String city = request.get("city");
        log.info("🌤️ 天气查询：{}", city);

        try {
            String result = weatherTool.getWeather(city);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("city", city);
            response.put("result", result);
            return response;

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            return error;
        }
    }

    /**
     * 计算器（单工具测试）
     *
     * @param request 请求（包含 expression）
     * @return 计算结果
     */
    @PostMapping("/calculate")
    public Map<String, Object> testCalculate(@RequestBody Map<String, String> request) {
        String expression = request.get("expression");
        log.info("🧮 计算：{}", expression);

        try {
            String result = calculatorTool.calculate(expression);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("expression", expression);
            response.put("result", result);
            return response;

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            return error;
        }
    }

    /**
     * 时间查询（单工具测试）
     *
     * @param request 请求（包含 city, format）
     * @return 当前时间
     */
    @PostMapping("/time")
    public Map<String, Object> testTime(@RequestBody Map<String, String> request) {
        String city = request.get("city");
        String format = request.get("format");
        log.info("🕐 时间查询：city={}, format={}", city, format);

        try {
            String result = timeTool.getCurrentTime(city, format);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("city", city);
            response.put("format", format);
            response.put("result", result);
            return response;

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            return error;
        }
    }

    /**
     * 搜索（单工具测试）
     *
     * @param request 请求（包含 query, maxResults）
     * @return 搜索结果
     */
    @PostMapping("/search")
    public Map<String, Object> testSearch(@RequestBody Map<String, String> request) {
        String query = request.get("query");
        String maxResultsStr = request.get("maxResults");
        log.info("🔍 搜索：query={}, maxResults={}", query, maxResultsStr);

        try {
            Integer maxResults = maxResultsStr != null ? Integer.parseInt(maxResultsStr) : null;
            String result = searchTool.search(query, maxResults);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("query", query);
            response.put("maxResults", maxResults);
            response.put("result", result);
            return response;

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            return error;
        }
    }

    /**
     * 系统信息（单工具测试）
     *
     * @param request 请求（包含 type）
     * @return 系统信息
     */
    @PostMapping("/system")
    public Map<String, Object> testSystem(@RequestBody Map<String, String> request) {
        String type = request.get("type");
        log.info("🖥️ 系统信息：type={}", type);

        try {
            String result = systemTool.getSystemInfo(type);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("type", type);
            response.put("result", result);
            return response;

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            return error;
        }
    }

    // ================================================================
    // 4. 工具信息
    // ================================================================

    /**
     * 获取可用工具列表
     *
     * @return 工具列表
     */
    @GetMapping("/list")
    public Map<String, Object> getToolsList() {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("tools", toolsService.getAvailableTools());
        return result;
    }
}
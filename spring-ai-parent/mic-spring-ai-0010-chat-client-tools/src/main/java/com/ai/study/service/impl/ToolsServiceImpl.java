package com.ai.study.service.impl;

import com.ai.study.service.ToolsService;
import com.ai.study.tools.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 工具调用服务实现类
 *
 * <p>功能说明：</p>
 * <ul>
 *   <li>实现带工具调用的对话能力</li>
 *   <li>使用 .tools() 方法注册工具</li>
 *   <li>支持多工具协同工作</li>
 * </ul>
 *
 * @author AI Study
 * @since 1.0.0
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ToolsServiceImpl implements ToolsService {

    /**
     * 默认模型客户端（Ollama 本地模型）
     */
    @Qualifier("ollamaChatClient")
    private final ChatClient chatClient;

    /**
     * 天气工具
     */
    private final WeatherTool weatherTool;

    /**
     * 计算器工具
     */
    private final CalculatorTool calculatorTool;

    /**
     * 时间工具
     */
    private final TimeTool timeTool;

    /**
     * 搜索工具
     */
    private final SearchTool searchTool;

    /**
     * 系统工具
     */
    private final SystemTool systemTool;

    @Override
    public String chatWithTools(String message) {
        log.info("🔧 带工具调用对话：{}", message);

        return chatClient.prompt()
                .user(message)
                .tools(weatherTool, calculatorTool, timeTool, searchTool, systemTool)
                .call()
                .content();
    }

    @Override
    public String chatWithSpecificTools(String message, Object... tools) {
        log.info("🔧 指定工具对话：{}", message);

        return chatClient.prompt()
                .user(message)
                .tools(tools)
                .call()
                .content();
    }

    @Override
    public String chatWithoutTools(String message) {
        log.info("💬 无工具对话：{}", message);

        return chatClient.prompt()
                .user(message)
                .call()
                .content();
    }

    @Override
    public Map<String, String> compare(String message) {
        log.info("📊 对比测试：{}", message);

        return Map.of(
                "withoutTools", chatWithoutTools(message),
                "withTools", chatWithTools(message)
        );
    }

    /**
     * 动态获取可用工具列表（让大模型自己读取并返回）
     */
    @Override
    public String getAvailableTools() {
        log.info("🔍 获取并验证大模型当前加载的工具列表与参数名");

        // 构造探测 Prompt，要求大模型读取并输出工具的 JSON Schema 信息
        String probePrompt = """
                作为一个AI，请查阅你当前上下文中注册的所有工具(Function/Tools)。
                请务必严谨地读取底层工具定义，并按以下格式列出所有可用工具：
                
                - **工具名称** (name)
                - **工具描述** (description)
                - **参数列表** (非常重要：必须极其精准地写出你接收到的**参数名**，比如到底是 arg0 还是 city？类型是什么？是否有必填要求？)
                
                注意：不要调用这些工具，仅仅以 Markdown 列表的形式将它们的定义元数据打印出来即可。
                """;

        try {
            return chatClient.prompt()
                    .user(probePrompt)
                    // 把所有工具都挂载上，让大模型看到它们
                    .tools(weatherTool, calculatorTool, timeTool, searchTool, systemTool)
                    .call()
                    .content();
        } catch (Exception e) {
            log.error("❌ 获取工具列表失败", e);
            return "获取工具列表失败：" + e.getMessage();
        }
    }
}
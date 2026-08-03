package com.ai.study.service;

import java.util.Map;

/**
 * 工具调用服务接口
 *
 * <p>功能说明：</p>
 * <ul>
 *   <li>提供带工具调用的对话能力</li>
 *   <li>提供无工具的对话能力（对比测试）</li>
 *   <li>支持多工具协同</li>
 * </ul>
 *
 * @author AI Study
 * @since 1.0.0
 */
public interface ToolsService {

    /**
     * 带工具调用的对话（自动判断使用哪些工具）
     *
     * <p>AI 会根据用户消息自动判断是否需要调用工具，以及调用哪个工具。</p>
     *
     * @param message 用户消息
     * @return AI 响应
     */
    String chatWithTools(String message);

    /**
     * 带工具调用的对话（指定工具）
     *
     * <p>手动指定可用的工具列表，AI 只能使用这些工具。</p>
     *
     * @param message 用户消息
     * @param tools 指定的工具数组
     * @return AI 响应
     */
    String chatWithSpecificTools(String message, Object... tools);

    /**
     * 无工具的对话（对比测试）
     *
     * <p>不提供任何工具，AI 只能基于训练数据回答。</p>
     *
     * @param message 用户消息
     * @return AI 响应
     */
    String chatWithoutTools(String message);

    /**
     * 对比测试：有工具 vs 无工具
     *
     * <p>用同一个问题分别测试有工具和无工具的回答效果。</p>
     *
     * @param message 用户消息
     * @return 对比结果
     */
    Map<String, String> compare(String message);

    /**
     * 获取可用工具列表
     *
     * @return 工具名称列表
     */
    String getAvailableTools();
}

package com.ai.study.service;

import java.util.Map;

/**
 * Advisor 服务接口
 *
 * <p>功能说明：</p>
 * <ul>
 *   <li>提供带各种 Advisor 的对话能力</li>
 *   <li>支持组合多个 Advisor</li>
 *   <li>提供统计信息查询</li>
 * </ul>
 *
 * @author AI Study
 * @since 1.0.0
 */
public interface AdvisorService {

    /**
     * 带所有 Advisor 的对话
     *
     * <p>包含：日志、性能、缓存、审计、限流、敏感词过滤</p>
     *
     * @param message 用户消息
     * @return AI 响应
     */
    String chatWithAllAdvisors(String message);

    /**
     * 带指定 Advisor 的对话
     *
     * @param message 用户消息
     * @param advisorNames Advisor 名称列表（如：logging, performance）
     * @return AI 响应
     */
    String chatWithAdvisors(String message, String... advisorNames);

    /**
     * 不带任何 Advisor 的对话（对比测试）
     *
     * @param message 用户消息
     * @return AI 响应
     */
    String chatWithoutAdvisor(String message);

    /**
     * 获取所有 Advisor 的统计信息
     *
     * @return 统计信息 Map
     */
    Map<String, Object> getAdvisorStats();
}
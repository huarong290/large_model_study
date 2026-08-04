package com.ai.study.service;

/**
 * 模型路由服务接口
 *
 * <p>功能说明：</p>
 * <ul>
 *   <li>定义多模型路由的核心业务方法</li>
 *   <li>支持根据用户输入动态路由到不同的 AI 模型</li>
 * </ul>
 *
 * @author AI Study
 * @since 1.0.0
 */
public interface ModelRouterService {

    /**
     * 根据用户消息动态选择模型并返回 AI 回复
     *
     * @param message 用户输入的提问内容
     * @return 模型生成的响应文本
     */
    String routeAndChat(String message);
}
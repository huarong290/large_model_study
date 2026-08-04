package com.ai.study.service;

import java.util.Map;

/**
 * 安全服务接口
 *
 * @author AI Study
 * @since 1.0.0
 */
public interface SecurityService {

    /**
     *
     *  安全对话（包含所有安全控制 输入/输出双向敏感词过滤）
     * @param message 用户消息
     * @return AI 响应
     */
    String chatWithSecurity(String message);

    /**
     * 不带安全控制的对话（对比测试）
     *
     * @param message 用户消息
     * @return AI 响应
     */
    String chatWithoutSecurity(String message);

    /**
     * 获取安全统计信息
     *
     * @return 统计信息 Key-Value 映射
     */
    Map<String, Object> getSecurityStats();
}


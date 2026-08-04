package com.ai.study.service.impl;


import com.ai.study.service.ModelRouterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 模型路由服务实现类
 *
 * <p>功能说明：</p>
 * <ul>
 *   <li>实现多模型路由的核心业务逻辑</li>
 *   <li>通过关键词匹配判断复杂/简单问题，动态选择 DeepSeek 或 Ollama</li>
 * </ul>
 *
 * @author AI Study
 * @since 1.0.0
 */
@Service
@Slf4j
public class ModelRouterServiceImpl implements ModelRouterService {

    private final ChatClient ollamaChatClient;
    private final ChatClient deepSeekChatClient;

    // 通过 @Qualifier 区分注入两个 ChatClient Bean
    public ModelRouterServiceImpl(@Qualifier("ollamaChatClient") ChatClient ollamaChatClient,
                           @Qualifier("deepSeekChatClient") ChatClient deepSeekChatClient) {
        this.ollamaChatClient = ollamaChatClient;
        this.deepSeekChatClient = deepSeekChatClient;
    }

    @Override
    public String routeAndChat(String message) {
        // 简单的路由规则：如果包含特定关键词，走 DeepSeek
        List<String> complexKeywords = List.of("写个", "代码", "实现", "分析", "翻译", "算法");
        boolean isComplex = complexKeywords.stream().anyMatch(message::contains);

        ChatClient client = isComplex ? deepSeekChatClient : ollamaChatClient;
        String modelName = isComplex ? "DeepSeek(云端)" : "Ollama(本地)";

        // 打印路由决策到控制台，方便调试测试
        log.info("========== 路由决策 ==========");
        log.info("当前问题={}" , message);
        log.info("路由至:={} " , modelName);
        log.info("==============================");

        // 执行 AI 调用
        return client.prompt()
                .user(message)
                .call()
                .content();
    }
}
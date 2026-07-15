package com.ai.study.service;

import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.spring.AiService;
import dev.langchain4j.service.spring.AiServiceWiringMode;
import reactor.core.publisher.Flux;

@AiService(wiringMode = AiServiceWiringMode.EXPLICIT, chatModel = "ollamaChatModel" ,streamingChatModel = "ollamaStreamingChatModel")
public interface OllamaAssistant {
    /**普通对话（非流式）
     * LLM 交互方法
     *
     * @param question 参数用户的提问
     * @return LLM的输出返回
     */
    String ask(String question);



    /**
     * 流式对话（响应式）
     * 返回 Flux<String> 实现流式输出
     * @param question 参数用户的提问
     * @return LLM的输出返回
     */
    @SystemMessage("你是一个有用的AI助手")
    Flux<String> streamChat(String question);
}

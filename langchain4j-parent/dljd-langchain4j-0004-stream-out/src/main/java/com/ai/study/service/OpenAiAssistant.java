package com.ai.study.service;


import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;
import dev.langchain4j.service.spring.AiServiceWiringMode;
import reactor.core.publisher.Flux;

@AiService(wiringMode = AiServiceWiringMode.EXPLICIT,chatModel = "openAiChatModel",streamingChatModel = "openAiStreamingChatModel")
public interface OpenAiAssistant {

    /**
     * 普通对话（阻塞式）
     */
    String ask(String userMessage);

    /**
     * 流式对话（非阻塞，返回 Flux）
     */
    @SystemMessage("你是一个有用的AI助手")
    Flux<String> streamChat(@UserMessage String userMessage);
}

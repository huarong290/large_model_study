package com.ai.study.service;

import dev.langchain4j.service.spring.AiService;
import dev.langchain4j.service.spring.AiServiceWiringMode;

@AiService(wiringMode = AiServiceWiringMode.EXPLICIT, chatModel = "ollamaChatModel")
public interface OllamaAssistant {
    /**
     * LLM 交互方法
     *
     * @param question 参数用户的提问
     * @retur LLM的输出返回
     */
    String ask(String question);

    //定义其他方法
}

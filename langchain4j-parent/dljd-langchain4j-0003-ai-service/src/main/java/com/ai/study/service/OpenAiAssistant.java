package com.ai.study.service;

import dev.langchain4j.service.spring.AiService;
import dev.langchain4j.service.spring.AiServiceWiringMode;

@AiService(wiringMode = AiServiceWiringMode.EXPLICIT,chatModel = "openAiChatModel")
public interface OpenAiAssistant {

    String ask(String userMessage);
}

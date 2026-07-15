package com.ai.study.service;


import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
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
     * 普通对话（阻塞式）
     */
//    @SystemMessage("你是邻居30多岁大姐,以生活化的语气回答")
    @SystemMessage(fromResource = "SystemRule.md")
    Flux<String> chat(String userMessage);
    /**
     * 普通对话（响应式）单个参数
     */
    @UserMessage(value = "翻译为英文:{{it}}")
    Flux<String> askFlux(String userMessage);
    /**
     * 流式对话（非阻塞，返回 Flux）单个参数
     */
    @SystemMessage("你是一个有用的AI助手")
    Flux<String> streamChat(@UserMessage String userMessage);

    /**
     * 普通对话（响应式）多个参数
     */
    @UserMessage(value = "将{{text}}翻译为英文:{{lang}}")
    Flux<String> translate(@V("text")String srcText,@V("lang") String targetLang);


    /**
     *
     * @param srcText 需要翻译的内容
     * @param targetLang 目标语言
     * @return 大模型的响应
     */
    @UserMessage(fromResource = "userPrompt.txt")
    Flux<String> translateFromResource(@V("text")String srcText,@V("lang") String targetLang);

    /**
     *
     * @param userMessage 用户提示词
     * @param roleName 角色
     * @return 大模型的响应
     */
    @SystemMessage("你是{{roleName}}助手")
    Flux<String> mixPrompt(@UserMessage String userMessage,@V("roleName") String roleName);
}

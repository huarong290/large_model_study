package com.ai.study.service;

import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.service.AiServices;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TestService {

    @Autowired
    private OllamaChatModel ollamaChatModel;
    /**
     * Assistant 接口上没有@AiService 通过代理对象
     */
    @Test
    void test01(){
        //创建AI Service 接口的代理对象
        OllamaAssistant ollamaAssistant =  AiServices.create(OllamaAssistant.class,ollamaChatModel);

        // 调用代理对象的方法
        String answer = ollamaAssistant.ask("你是谁");
        System.out.println("test01LLM应答结果="+answer);

        //建造模式
        OllamaAssistant buildOllamaAssistant = AiServices.builder(OllamaAssistant.class).chatModel(ollamaChatModel).build();
    }

    @Autowired
    private OllamaAssistant ollamaAssistant;

    /**
     * Assistant 接口上增加了@AiService
     */
    @Test
    void test02(){
        // 调用方法
        String answer = ollamaAssistant.ask("你是谁");
        System.out.println("test02LLM应答结果="+answer);
    }
    @Autowired
    private OpenAiAssistant openAiAssistant;
    /**
     * openAiAssistant 接口上增加了@AiService
     */
    @Test
    void test03(){
        // 调用方法
        String answer = openAiAssistant.ask("你是谁");
        System.out.println("test03LLM应答结果="+answer);
    }
}

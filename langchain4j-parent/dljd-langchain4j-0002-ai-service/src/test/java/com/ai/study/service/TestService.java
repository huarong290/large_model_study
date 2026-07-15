package com.ai.study.service;

import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.spring.AiService;
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
        Assistant assistant =  AiServices.create(Assistant.class,ollamaChatModel);

        // 调用代理对象的方法
        String answer = assistant.ask("你是谁");
        System.out.println("test01LLM应答结果="+answer);

        //建造模式
        Assistant buildAssistant = AiServices.builder(Assistant.class).chatModel(ollamaChatModel).build();
    }

    @Autowired
    private Assistant assistant;

    /**
     * Assistant 接口上增加了@AiService
     */
    @Test
    void test02(){
        // 调用代理对象的方法
        String answer = assistant.ask("你是谁");
        System.out.println("test02LLM应答结果="+answer);
    }
}

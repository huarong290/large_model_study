package com.ai.study.controller;

import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModelName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication // 必须用这个注解启动测试容器
public class LLMTest {

    // 【纯 Java 方式】无须 Spring 容器介入
    @Test
    public void helloWorld(){
        OpenAiChatModel model = OpenAiChatModel.builder()
                .baseUrl("http://langchain4j.dev/demo/openai/v1")
                .apiKey("demo")
                .modelName(OpenAiChatModelName.GPT_4_O_MINI)
                .build();
        String answer = model.chat("你是谁");
        System.out.println("纯 Java 响应: " + answer);
    }


}
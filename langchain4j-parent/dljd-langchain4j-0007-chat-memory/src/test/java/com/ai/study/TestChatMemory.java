package com.ai.study;

import com.ai.study.service.Assistant;
import com.ai.study.service.OpenAiAssistant;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.memory.chat.TokenWindowChatMemory;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModelName;
import dev.langchain4j.model.openai.OpenAiTokenCountEstimator;
import dev.langchain4j.service.AiServices;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.List;

@SpringBootTest
public class TestChatMemory {
    @Test
    void test01(){

        OpenAiChatModel model = OpenAiChatModel
                .builder()
                .baseUrl("https://api.deepseek.com")
                .apiKey("sk-d9acf81847274fcfb8cbfe0f20051ffd")
                .modelName("deepseek-v4-flash")
                .build()
                ;
        // 第一次对话
        UserMessage userMessage1 = UserMessage.from("北京是一线城市吗");
        ChatResponse response1 = model.chat(userMessage1);
        AiMessage aiMessage1= response1.aiMessage();
        String text1= aiMessage1.text();
        System.out.println("LLM第一次应答="+text1);
        //第二次对话
        UserMessage userMessage2 = UserMessage.from("上海是吗");
        ChatResponse response2 = model.chat(userMessage2);
        AiMessage aiMessage2= response2.aiMessage();
        String text2= aiMessage2.text();
        System.out.println("LLM第二次应答="+text2);
    }

    @Test
    void test02(){

        OpenAiChatModel model = OpenAiChatModel
                .builder()
                .baseUrl("https://api.deepseek.com")
                .apiKey("sk-d9acf81847274fcfb8cbfe0f20051ffd")
                .modelName("deepseek-v4-flash")
                .build()
                ;
        // 第一次对话
        UserMessage userMessage1 = UserMessage.from("北京是一线城市吗");
        ChatResponse response1 = model.chat(userMessage1);
        AiMessage aiMessage1= response1.aiMessage();
        String text1= aiMessage1.text();
        System.out.println("LLM第一次应答="+text1);
        //第二次对话
        UserMessage userMessage2 = UserMessage.from("上海是吗");
        ChatResponse response2 = model.chat(List.of(userMessage1,aiMessage1,userMessage2));
        AiMessage aiMessage2= response2.aiMessage();
        String text2= aiMessage2.text();
        System.out.println("LLM第二次应答="+text2);
    }
    @Autowired
    private OpenAiChatModel openAiChatModel;

    @Test
    void test03(){
        //创建ChatMemory
        ChatMemory messageWindowChatMemory = MessageWindowChatMemory.builder()
                .maxMessages(10)
                .build();

        // 创建Ai Service代理对象
        Assistant assistant = AiServices.builder(Assistant.class)
                .chatMemory(messageWindowChatMemory)
                .chatModel(openAiChatModel).build();
        //通过代理对象进行对话
        String res1 =assistant.ask("杭州是一线城市吗");
        System.out.println("res1="+res1);

        String res2 =assistant.ask("上海是吗");
        System.out.println("res2="+res2);
    }

    @Test
    void test04(){
        // 1. 直接创建 OpenAiTokenCountEstimator（不需要注入）
// 直接传入模型名称字符串
        OpenAiTokenCountEstimator openAiTokenCountEstimator = new OpenAiTokenCountEstimator(OpenAiChatModelName.GPT_4_O);
        // 2. 创建 ChatMemory
        ChatMemory tokenWindowChatMemory = TokenWindowChatMemory.builder()
                .maxTokens(3200,openAiTokenCountEstimator)
                .build();
        //token 窗口：设置为模型上下文的70% 80% 如(4000 token上下文设置为3200)
        // 3. 创建 AI Service 代理
        Assistant assistant =AiServices.builder(Assistant.class)
                .chatMemory(tokenWindowChatMemory)
                .chatModel(openAiChatModel).build();

        // 4. 通过代理对象进行测试对话
        String res1 =assistant.ask("杭州是一线城市吗");
        System.out.println("res1="+res1);

        String res2 =assistant.ask("上海是吗");
        System.out.println("res2="+res2);

        String res3 =assistant.ask("广州呢");
        System.out.println("res3="+res3);
    }

    @Autowired
    private OpenAiAssistant openAiAssistant;

    /**
     * Assistant 接口上增加了@AiService
     */
    @Test
    void test05(){
        // 调用代理对象的方法
        String answer1 = openAiAssistant.ask("user001","上海是一线城市吗");
        System.out.println("上海是一线城市吗 test05LLM应答,结果="+answer1);
        String answer2 = openAiAssistant.ask("user002","武汉是一线城市吗");
        System.out.println("武汉是一线城市吗test05LLM应答结果="+answer2);
    }
}

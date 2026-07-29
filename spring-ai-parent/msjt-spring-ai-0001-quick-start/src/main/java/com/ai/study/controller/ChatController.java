package com.ai.study.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.ai.deepseek.DeepSeekChatOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/chat")
@Slf4j
public class ChatController {
    //与deepseek 聊天的模型
    @Autowired
    private DeepSeekChatModel deepSeekChatModel;

    @GetMapping("/generate")
    public String generate(@RequestParam(value = "message",defaultValue = "你是谁") String message){
        log.info("message={}",message);
        // 与模型直接对话，调用chatmodel.call方法
        String result = deepSeekChatModel.call(message);
        log.info("返回内容={}",result);
        return result;
    }
    @GetMapping("/generateStream")
    public Flux<ChatResponse> generateStream(@RequestParam(value = "message",defaultValue = "你是谁") String message){
        log.info("message={}",message);
        // 与模型直接对话，流式返回容
        Prompt prompt = new Prompt(new UserMessage(message));
        Flux<ChatResponse> streamResult = deepSeekChatModel.stream(prompt);
        log.info("返回内容={}",streamResult);
        return streamResult;
    }

    @GetMapping("/generateStream2")
    public Flux<String> generateStream2(@RequestParam(value = "message",defaultValue = "你是谁") String message){
        log.info("message={}",message);
        // 与模型直接对话，流式返回容
        Prompt prompt = new Prompt(new UserMessage(message));
        Flux<ChatResponse> streamResult = deepSeekChatModel.stream(prompt);
//        Flux<String> result=streamResult.map(new Function<ChatResponse,String>(){
//            @Override
//            public String apply(ChatResponse chatResponse) {
//                return chatResponse.getResult().getOutput().getText();
//            }
//        });
        Flux<String> result=streamResult.map(chatResponse -> chatResponse.getResult().getOutput().getText());

        log.info("返回内容={}",result);
        return result;
    }

    @GetMapping("/runtimeOption")
    public String runtimeOption(@RequestParam(value = "message",defaultValue = "你是谁") String message,@RequestParam(value = "temp",required = false) Double temp){
        log.info("收到message={}，temp={}",message,temp);
        // 与模型直接对话，流式返回容
        Prompt prompt ;
        if(temp !=null){
            log.info("使用传入的temp...");
            DeepSeekChatOptions deepSeekChatOptions = DeepSeekChatOptions.builder().temperature(temp).build();
            prompt = new Prompt(message,deepSeekChatOptions);
        }else{
            log.info("使用默认的temp...");
            prompt = new Prompt(new UserMessage(message));
        }
        ChatResponse response = deepSeekChatModel.call(prompt);

        return response.getResult().getOutput().getText();
    }

}

package com.ai.study.config;


import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatClientConfig {

//    @Bean
//    public ChatClient chatClient(OpenAiChatModel openAiChatModel){
//
//        return ChatClient.create(openAiChatModel);
//    }


//    @Bean
//    public ChatClient chatClient(ChatClient.Builder builder){
//
//        return builder.build();
//    }

    /**
     *
     * @param builder
     * @return
     */
    @Bean
    public ChatClient chatClient(ChatClient.Builder builder){

        return builder.defaultOptions(ChatOptions.builder()
                .temperature(0.1)
                .topK(5)
                .topP(0.9)
                .build()).build();
    }
}

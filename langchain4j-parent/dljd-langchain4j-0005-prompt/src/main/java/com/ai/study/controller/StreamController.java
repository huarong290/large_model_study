package com.ai.study.controller;

import com.ai.study.service.OllamaAssistant;
import com.ai.study.service.OpenAiAssistant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class StreamController {

    @Autowired
    private OpenAiAssistant openAiAssistant;

    @Autowired
    private OllamaAssistant ollamaAssistant;

    @GetMapping(value = "/streamOpenAiChat")
    public Flux<String> streamOpenAiChat(@RequestParam("message") String message){

        return openAiAssistant.streamChat(message);
    }

    @GetMapping(value = "/streamOllamaChat")
    public Flux<String> streamOllamaChat(@RequestParam("message") String message){

        return ollamaAssistant.streamChat(message);
    }
}

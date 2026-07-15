package com.ai.study.controller;

import com.ai.study.service.OpenAiAssistant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class MessTypeController {
    @Autowired
    private OpenAiAssistant openAiAssistant;

    @GetMapping(value = "/askFlux")
    public Flux<String> askFlux(@RequestParam("message") String message){
        return  openAiAssistant.askFlux(message);
    }

    @GetMapping(value = "/chat")
    public Flux<String> chat(@RequestParam("message") String message){
        return  openAiAssistant.chat(message);
    }
    @GetMapping(value = "/translate")
    public Flux<String> translate(@RequestParam("text") String text,@RequestParam("lang") String lang){
        return  openAiAssistant.translate(text,lang);
    }

    @GetMapping(value = "/translateFromResource")
    public Flux<String> translateFromResource(@RequestParam("text") String text,@RequestParam("lang") String lang){
        return  openAiAssistant.translateFromResource(text,lang);
    }

    @GetMapping(value = "/mixPrompt")
    public Flux<String> mixPrompt(@RequestParam("message") String message,@RequestParam("roleName") String roleName){
        return  openAiAssistant.mixPrompt(message,roleName);
    }
}

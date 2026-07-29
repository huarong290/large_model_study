package com.ai.study.controller;

import com.ai.study.service.RagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rag")
public class RagController {
    @Autowired
    private RagService ragService;

    @GetMapping("ask")
    public String ask(@RequestParam("question") String question){

    String result = ragService.answer(question);
    return  result;
    }
}

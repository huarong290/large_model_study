package com.ai.study.controller;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

public class EmbeddingController {
    @Autowired
    private EmbeddingModel embeddingModel;

    // 对用户传入的文本进行 向量话，测试embedding
    @GetMapping("embedding")
    public Map embed(@RequestParam(value = "message",defaultValue = "给我讲个笑话") String message){

        float[] embed = embeddingModel.embed(message);

        return Map.of("message",message,"vector", embed);
    }
}

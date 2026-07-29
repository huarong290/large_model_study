package com.ai.study.controller;

import com.ai.study.service.EmbeddingService;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/embed")
public class EmbeddingController {
    @Autowired
    private EmbeddingModel embeddingModel;
    @Autowired
    private EmbeddingService embeddingService;

    // 对用户传入的文本进行 向量话，测试embedding
    @GetMapping("embedding")
    public Map embed(@RequestParam(value = "message", defaultValue = "给我讲个笑话") String message) {

        float[] embed = embeddingModel.embed(message);

        return Map.of("message", message, "vector", embed);
    }

    @GetMapping("similarity")
    public String similarity(@RequestParam(value = "query") String query) {
        // 调用service
        String result = embeddingService.queryBastMatch(query);
        return result;
    }
}

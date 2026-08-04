package com.ai.study.controller;

import com.ai.study.service.ModelRouterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/router")
public class RouterController {

    @Autowired
    private ModelRouterService modelRouterService; // ✅ 这里注入的是接口，不是实现类

    @PostMapping("/chat")
    public String chat(@RequestBody Map<String, String> request) {
        String message = request.get("message");
        return modelRouterService.routeAndChat(message);
    }
}
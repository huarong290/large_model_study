package com.lost.found.controller;

import com.lost.found.servive.LostFoundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LostFoundController {

    @Autowired
    private LostFoundService lostFoundService;

    @GetMapping("/help")
    public String help(@RequestParam(name="sessionId",defaultValue = "user_0001") String sessionId,@RequestParam("userMessage") String userMessage){

        String result = lostFoundService.doWithLostFound(sessionId,userMessage);

        return result;
    }
}

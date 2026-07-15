package com.ao.study.controller;

import com.ao.study.pojo.ProductInfo;
import com.ao.study.pojo.WritePrompt;
import com.ao.study.service.ProductAssistant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductController {
    @Autowired
    private ProductAssistant productAssistant;

    @GetMapping("/getProductInfo")
    public ProductInfo getProductInfo(){

        return productAssistant.chat("华为FreeBuds SE 2");
    }
    @GetMapping("/write")
    public String write(){
        WritePrompt writePrompt = new WritePrompt();
        writePrompt.setLang("中文");
        writePrompt.setStyle("幽默");
        writePrompt.setTopic("小学生写作业");
        writePrompt.setLength(200);
        return productAssistant.write(writePrompt);
    }

}

package com.ao.study.service;

import com.ao.study.pojo.ProductInfo;
import com.ao.study.pojo.WritePrompt;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import dev.langchain4j.service.spring.AiService;
import dev.langchain4j.service.spring.AiServiceWiringMode;

@AiService(wiringMode = AiServiceWiringMode.EXPLICIT,chatModel = "openAiChatModel")
public interface ProductAssistant {
    @SystemMessage("你是一个专业的商品分析师,能够根据商品名称生成详细的评价。")
    @UserMessage("请为以下商品生成评价信息:{{productName}}")
    ProductInfo chat(@V("productName") String productName);

    @SystemMessage("你是一个经验丰富的作家")
    String write(WritePrompt writePrompt);
}

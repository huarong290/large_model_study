package com.ai.study.log;

import dev.langchain4j.model.chat.listener.ChatModelErrorContext;
import dev.langchain4j.model.chat.listener.ChatModelListener;
import dev.langchain4j.model.chat.listener.ChatModelRequestContext;
import dev.langchain4j.model.chat.listener.ChatModelResponseContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
@Slf4j
public class LangChain4jLoggingListenter implements ChatModelListener {
    @Override
    public void onRequest(ChatModelRequestContext requestContext) {
        log.info("===========[BEGIN 百炼大模型请求开始]===========");
        Map<String, Object> logMap = new HashMap<>();
        logMap.put("traceId", UUID.randomUUID().toString());
        logMap.put("type", "REQUEST");

        // 提取请求的消息内容
        logMap.put("messages", requestContext.chatRequest().messages());
        logMap.put("parameters", requestContext.chatRequest().parameters());
        try {
            // 存储traceId 以便在响应式关联
            requestContext.attributes().put("traceId", logMap.get("traceId"));
            log.info(logMap.toString());
        } catch (Exception e) {
            log.error("Failed to log request", e);
        }
        log.info("===========[END 百炼大模型请求完成]===========");
    }

    @Override
    public void onResponse(ChatModelResponseContext responseContext) {

        log.info("===========[BEGIN 百炼大模型响应开始]===========");
        String traceId =(String) responseContext.attributes().get("traceId");
        Map<String, Object> logMap = new HashMap<>();
        logMap.put("traceId", UUID.randomUUID().toString());
        logMap.put("type", "RESPONSE");

        // 提取响应的文本内容，token使用情况
        logMap.put("responseId", responseContext.chatResponse().metadata().id());
        logMap.put("content", responseContext.chatResponse().aiMessage().text());
        logMap.put("tokenUsage", responseContext.chatResponse().metadata().tokenUsage());

        try {
            log.info(logMap.toString());
        } catch (Exception e) {
            log.error("Failed to log request", e);
        }
        log.info("===========[END 百炼大模型响应完成]===========");
    }

    @Override
    public void onError(ChatModelErrorContext errorContext) {
        log.error("===========[START 百炼大模型调用失败]===========");
        log.error("Error Message:{}",errorContext.error().getMessage(),errorContext.error());
        log.error("===========[END 百炼大模型调用失败]===========");
    }
}

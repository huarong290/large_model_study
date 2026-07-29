package com.ai.study.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * ChatController - 测试不同 AI 模型的对话接口
 *
 * 功能说明：
 * 1. 默认使用 Ollama 本地模型
 * 2. 支持 DeepSeek 和 Google Gemini
 * 3. 提供模型对比功能
 *
 * @author AI Study
 */
@RestController
@RequestMapping("/api/chat")
@Slf4j
@RequiredArgsConstructor
public class ChatController {

    private final ChatClient defaultChatClient;

    @Qualifier("deepSeekChatClient")
    private final ChatClient deepSeekChatClient;

    @Qualifier("geminiChatClient")
    private final ChatClient geminiChatClient;

    /**
     * 健康检查
     *
     * 测试方式：GET /api/chat/health
     */
    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> status = new HashMap<>();
        status.put("status", "UP");
        status.put("defaultModel", "Ollama（本地）");
        status.put("deepSeek", deepSeekChatClient != null ? "✅ 已配置" : "❌ 未配置");
        status.put("gemini", geminiChatClient != null ? "✅ 已配置" : "❌ 未配置");
        return status;
    }

    /**
     * 使用默认模型（Ollama）对话
     *
     * 测试方式：GET /api/chat/default?message=你好
     */
    @GetMapping("/default")
    public Map<String, Object> chatWithDefault(
            @RequestParam(name = "message", defaultValue = "你好") String message) {
        log.info("💬 使用默认模型（Ollama 本地）对话：{}", message);

        try {
            long startTime = System.currentTimeMillis();
            String response = defaultChatClient.prompt()
                    .user(message)
                    .call()
                    .content();
            long endTime = System.currentTimeMillis();

            Map<String, Object> result = new HashMap<>();
            result.put("model", "Ollama（本地）");
            result.put("message", message);
            result.put("response", response);
            result.put("responseTime", endTime - startTime + "ms");
            return result;
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Ollama 调用失败");
            error.put("message", e.getMessage());
            error.put("hint", "请确认 Ollama 服务是否正常运行");
            return error;
        }
    }

    /**
     * 使用 DeepSeek 对话
     *
     * 测试方式：GET /api/chat/deepseek?message=你好
     */
    @GetMapping("/deepseek")
    public Map<String, Object> chatWithDeepSeek(
            @RequestParam(name = "message", defaultValue = "你好") String message) {
        log.info("💬 使用 DeepSeek 对话：{}", message);

        if (deepSeekChatClient == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "DeepSeek 未配置");
            error.put("hint", "请配置 DEEPSEEK_API_KEY 环境变量");
            return error;
        }

        try {
            long startTime = System.currentTimeMillis();
            String response = deepSeekChatClient.prompt()
                    .user(message)
                    .call()
                    .content();
            long endTime = System.currentTimeMillis();

            Map<String, Object> result = new HashMap<>();
            result.put("model", "DeepSeek");
            result.put("message", message);
            result.put("response", response);
            result.put("responseTime", endTime - startTime + "ms");
            return result;
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "DeepSeek 调用失败");
            error.put("message", e.getMessage());
            return error;
        }
    }

    /**
     * 使用 Gemini 对话
     *
     * 测试方式：GET /api/chat/gemini?message=你好
     */
    @GetMapping("/gemini")
    public Map<String, Object> chatWithGemini(
            @RequestParam(name = "message", defaultValue = "你好") String message) {
        log.info("💬 使用 Google Gemini 对话：{}", message);

        if (geminiChatClient == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Gemini 未配置");
            error.put("hint", "请配置 GEMINI_API_KEY 环境变量");
            error.put("获取地址", "https://aistudio.google.com/app/apikey");
            return error;
        }

        try {
            long startTime = System.currentTimeMillis();
            String response = geminiChatClient.prompt()
                    .user(message)
                    .call()
                    .content();
            long endTime = System.currentTimeMillis();

            Map<String, Object> result = new HashMap<>();
            result.put("model", "Google Gemini");
            result.put("message", message);
            result.put("response", response);
            result.put("responseTime", endTime - startTime + "ms");
            return result;
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Gemini 调用失败");
            error.put("message", e.getMessage());
            return error;
        }
    }

    /**
     * 对比所有模型
     *
     * 测试方式：GET /api/chat/compare?message=你好
     */
    @GetMapping("/compare")
    public Map<String, Map<String, Object>> compareModels(
            @RequestParam(name = "message", defaultValue = "你好") String message) {
        log.info("📊 对比所有模型：{}", message);

        Map<String, Map<String, Object>> results = new HashMap<>();

        // 1. Ollama（本地）
        try {
            long start = System.currentTimeMillis();
            String ollamaResponse = defaultChatClient.prompt()
                    .user(message)
                    .call()
                    .content();
            Map<String, Object> ollamaResult = new HashMap<>();
            ollamaResult.put("response", ollamaResponse);
            ollamaResult.put("time", System.currentTimeMillis() - start + "ms");
            results.put("Ollama（本地）", ollamaResult);
        } catch (Exception e) {
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("response", "调用失败: " + e.getMessage());
            errorResult.put("time", "N/A");
            results.put("Ollama（本地）", errorResult);
        }

        // 2. DeepSeek
        if (deepSeekChatClient != null) {
            try {
                long start = System.currentTimeMillis();
                String deepseekResponse = deepSeekChatClient.prompt()
                        .user(message)
                        .call()
                        .content();
                Map<String, Object> deepseekResult = new HashMap<>();
                deepseekResult.put("response", deepseekResponse);
                deepseekResult.put("time", System.currentTimeMillis() - start + "ms");
                results.put("DeepSeek", deepseekResult);
            } catch (Exception e) {
                Map<String, Object> errorResult = new HashMap<>();
                errorResult.put("response", "调用失败: " + e.getMessage());
                errorResult.put("time", "N/A");
                results.put("DeepSeek", errorResult);
            }
        } else {
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("response", "未配置");
            errorResult.put("time", "N/A");
            results.put("DeepSeek", errorResult);
        }

        // 3. Gemini
        if (geminiChatClient != null) {
            try {
                long start = System.currentTimeMillis();
                String geminiResponse = geminiChatClient.prompt()
                        .user(message)
                        .call()
                        .content();
                Map<String, Object> geminiResult = new HashMap<>();
                geminiResult.put("response", geminiResponse);
                geminiResult.put("time", System.currentTimeMillis() - start + "ms");
                results.put("Gemini", geminiResult);
            } catch (Exception e) {
                Map<String, Object> errorResult = new HashMap<>();
                errorResult.put("response", "调用失败: " + e.getMessage());
                errorResult.put("time", "N/A");
                results.put("Gemini", errorResult);
            }
        } else {
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("response", "未配置");
            errorResult.put("time", "N/A");
            results.put("Gemini", errorResult);
        }

        return results;
    }

    /**
     * 带 System 消息的调用
     *
     * 测试方式：GET /api/chat/with-system?message=你好&system=你是一个编程专家
     */
    @GetMapping("/with-system")
    public Map<String, Object> chatWithSystem(
            @RequestParam(name = "message", defaultValue = "你好") String message,
            @RequestParam(name = "system", defaultValue = "你是一个友好的AI助手") String system) {

        log.info("💬 带 System 消息调用，System：{}，Message：{}", system, message);

        try {
            long startTime = System.currentTimeMillis();
            String response = defaultChatClient.prompt()
                    .system(system)
                    .user(message)
                    .call()
                    .content();
            long endTime = System.currentTimeMillis();

            Map<String, Object> result = new HashMap<>();
            result.put("model", "Ollama（本地）");
            result.put("system", system);
            result.put("message", message);
            result.put("response", response);
            result.put("responseTime", endTime - startTime + "ms");
            return result;
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "调用失败");
            error.put("message", e.getMessage());
            return error;
        }
    }

    /**
     * 使用默认模型（Ollama）进行流式对话
     *
     * 测试方式：GET /api/chat/stream?message=讲个故事
     */
    @GetMapping(value = "/stream", produces = "text/event-stream;charset=UTF-8")
    public Object chatStream(
            @RequestParam(name = "message", defaultValue = "请用中文讲一个简短的故事") String message) {
        log.info("💬 使用流式响应（Ollama），消息：{}", message);

        try {
            return defaultChatClient.prompt()
                    .user(message)
                    .stream()
                    .content();
        } catch (Exception e) {
            log.error("流式响应失败：{}", e.getMessage());
            return Map.of("error", "流式响应失败", "message", e.getMessage());
        }
    }
}
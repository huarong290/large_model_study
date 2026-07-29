package com.ai.study.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ChatClient 核心 API 演示 Controller
 *
 * 模块名称：mic-spring-ai-0002-chat-client
 *
 * 功能说明：
 * 1. 演示 ChatClient 的核心 API：prompt() / call() / content()
 * 2. 演示 System 和 User 消息的使用
 * 3. 演示不同的调用方式（链式调用、Message 对象、Prompt 对象）
 * 4. 演示不同角色的 System 提示词效果
 * 5. 演示多模型对比（Ollama / DeepSeek / Gemini）
 *
 * ┌─────────────────────────────────────────────────────────────────────┐
 * │ 接口分类说明：                                                      │
 * │                                                                   │
 * │ 📌 通用接口（所有模块共用）：                                       │
 * │    - /health      健康检查                                         │
 * │    - /default     默认模型对话                                      │
 * │    - /deepseek    DeepSeek 对话                                    │
 * │    - /gemini      Gemini 对话                                      │
 * │    - /compare     多模型对比                                        │
 * │    - /with-system 带 System 消息的调用                             │
 * │                                                                   │
 * │ 🎯 本模块特有接口（ChatClient 基础功能演示）：                      │
 * │    - /simple      最简单的调用方式（只有 user 消息）                │
 * │    - /messages    使用 Message 对象构建 Prompt                     │
 * │    - /role        不同角色的 System 提示词测试                     │
 * │    - /stream      流式响应演示                                     │
 * └─────────────────────────────────────────────────────────────────────┘
 *
 * @author AI Study
 */
@RestController
@RequestMapping("/api/chat")
@Slf4j
@RequiredArgsConstructor
public class ChatController {

    // ================================================================
    // 注入不同的 ChatClient
    // ================================================================

    /**
     * 默认 ChatClient（Ollama 本地模型）
     * 使用 @Primary 标记，直接注入
     */
    private final ChatClient defaultChatClient;

    /**
     * DeepSeek ChatClient
     * 使用 @Qualifier 指定
     */
    @Qualifier("deepSeekChatClient")
    private final ChatClient deepSeekChatClient;

    /**
     * Gemini ChatClient
     * 使用 @Qualifier 指定
     */
    @Qualifier("geminiChatClient")
    private final ChatClient geminiChatClient;

    // ================================================================
    // 📌 通用接口（所有模块共用）
    // ================================================================

    /**
     * 【通用接口】健康检查
     *
     * 检查所有模型配置状态和可用性
     *
     * 测试方式：GET /api/chat/health
     *
     * @return 各模型状态
     */
    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> status = new HashMap<>();
        status.put("status", "UP");
        status.put("module", "mic-spring-ai-0002-chat-client");
        status.put("defaultModel", "Ollama（本地）");
        status.put("deepSeek", deepSeekChatClient != null ? "✅ 已配置" : "❌ 未配置");
        status.put("gemini", geminiChatClient != null ? "✅ 已配置" : "❌ 未配置");

        // 检查各模型是否可用
        try {
            defaultChatClient.prompt().user("ping").call().content();
            status.put("ollamaStatus", "✅ 可用");
        } catch (Exception e) {
            status.put("ollamaStatus", "❌ 不可用");
        }

        try {
            if (deepSeekChatClient != null) {
                deepSeekChatClient.prompt().user("ping").call().content();
                status.put("deepSeekStatus", "✅ 可用");
            } else {
                status.put("deepSeekStatus", "⚠️ 未配置");
            }
        } catch (Exception e) {
            status.put("deepSeekStatus", "❌ 不可用");
        }

        try {
            if (geminiChatClient != null) {
                geminiChatClient.prompt().user("ping").call().content();
                status.put("geminiStatus", "✅ 可用");
            } else {
                status.put("geminiStatus", "⚠️ 未配置");
            }
        } catch (Exception e) {
            status.put("geminiStatus", "❌ 不可用");
        }

        return status;
    }

    /**
     * 【通用接口】使用默认模型（Ollama）对话
     *
     * 测试方式：GET /api/chat/default?message=你好
     *
     * @param message 用户消息
     * @return AI 响应
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
     * 【通用接口】使用 DeepSeek 对话
     *
     * 测试方式：GET /api/chat/deepseek?message=你好
     *
     * @param message 用户消息
     * @return AI 响应
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
     * 【通用接口】使用 Gemini 对话
     *
     * 测试方式：GET /api/chat/gemini?message=你好
     *
     * @param message 用户消息
     * @return AI 响应
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
     * 【通用接口】对比所有模型
     *
     * 测试方式：GET /api/chat/compare?message=你好
     *
     * @param message 用户消息
     * @return 各模型响应对比
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
     * 【通用接口】带 System 消息的调用
     *
     * System 消息用于设定 AI 的角色和行为
     *
     * 测试方式：GET /api/chat/with-system?message=你好&system=你是一个编程专家
     *
     * @param message 用户消息
     * @param system 系统提示词（设定 AI 角色）
     * @return AI 响应
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

    // ================================================================
    // 🎯 本模块特有接口（ChatClient 基础功能演示）
    // ================================================================

    /**
     * 【本模块特有】最简单的调用方式：只有用户消息
     *
     * 调用链：prompt() -> user() -> call() -> content()
     *
     * 这是 ChatClient 最基础的用法，演示了完整的调用链
     *
     * 测试方式：GET /api/chat/simple?message=你好
     *
     * @param message 用户消息
     * @return AI 响应
     */
    @GetMapping("/simple")
    public Map<String, Object> simpleChat(@RequestParam(name = "message", defaultValue = "你好") String message) {
        log.info("====== 【本模块特有】基础调用：只有用户消息 ======");
        log.info("调用链：prompt() -> user() -> call() -> content()");

        long startTime = System.currentTimeMillis();

        // ================================================================
        // 核心调用链演示：prompt().user().call().content()
        // ================================================================
        String response = defaultChatClient.prompt()        // 1. 开始构建请求
                .user(message)                              // 2. 设置用户消息
                .call()                                     // 3. 执行调用（同步）
                .content();                                 // 4. 获取响应内容

        long endTime = System.currentTimeMillis();

        Map<String, Object> result = new HashMap<>();
        result.put("model", "Ollama（本地）");
        result.put("message", message);
        result.put("response", response);
        result.put("responseTime", endTime - startTime + "ms");
        result.put("callChain", "prompt() -> user() -> call() -> content()");
        return result;
    }

    /**
     * 【本模块特有】使用 Message 对象构建 Prompt
     *
     * 演示两种方式：
     * 1. 链式调用（推荐）：system() + user()
     * 2. Message 对象方式：UserMessage + SystemMessage + Prompt
     *
     * 测试方式：POST /api/chat/messages
     *
     * 请求体示例：
     * {
     *   "user": "你好",
     *   "system": "你是一个友好的AI助手"
     * }
     *
     * @param request 包含 user 和 system 的请求体
     * @return 两种方式的响应对比
     */
    @PostMapping("/messages")
    public Map<String, Object> chatWithMessages(@RequestBody Map<String, String> request) {
        String userMsg = request.getOrDefault("user", "你好");
        String systemMsg = request.getOrDefault("system", "你是一个友好的AI助手");

        log.info("====== 【本模块特有】使用 Message 对象 ======");
        log.info("System: {}", systemMsg);
        log.info("User: {}", userMsg);

        long startTime = System.currentTimeMillis();

        // ================================================================
        // 方式1：链式调用（推荐）
        // ================================================================
        log.info("--- 方式1：链式调用 .system().user() ---");
        String response1 = defaultChatClient.prompt()
                .system(systemMsg)
                .user(userMsg)
                .call()
                .content();

        long endTime1 = System.currentTimeMillis();

        // ================================================================
        // 方式2：使用 Message 对象
        // ================================================================
        log.info("--- 方式2：Message 对象 + Prompt ---");
        UserMessage userMessage = new UserMessage(userMsg);
        SystemMessage systemMessage = new SystemMessage(systemMsg);
        Prompt prompt = new Prompt(List.of(systemMessage, userMessage));

        String response2 = defaultChatClient.prompt(prompt)
                .call()
                .content();

        long endTime2 = System.currentTimeMillis();

        Map<String, Object> result = new HashMap<>();
        result.put("model", "Ollama（本地）");
        result.put("chainResponse", response1);
        result.put("chainTime", endTime1 - startTime + "ms");
        result.put("promptResponse", response2);
        result.put("promptTime", endTime2 - endTime1 + "ms");
        result.put("note", "两种方式结果相同，链式调用更简洁推荐使用");
        return result;
    }

    /**
     * 【本模块特有】使用不同的 System 角色进行对话
     *
     * 演示不同角色设定对 AI 回答风格的影响
     *
     * 测试方式：GET /api/chat/role?role=teacher&message=你好
     *
     * 可选角色及说明：
     * ┌──────────┬────────────────────────────────────────────────┐
     * │ role     │ 说明                                          │
     * ├──────────┼────────────────────────────────────────────────┤
     * │ teacher  │ 教师：用通俗易懂的方式讲解                    │
     * │ doctor   │ 医生：专业、准确、有温度                      │
     * │ lawyer   │ 律师：严谨、专业、依法依规                    │
     * │ poet     │ 诗人：富有诗意、优美动人                      │
     * │ engineer │ 工程师：技术性强、实用性好                    │
     * └──────────┴────────────────────────────────────────────────┘
     *
     * @param role 角色类型
     * @param message 用户消息
     * @return AI 响应
     */
    @GetMapping("/role")
    public Map<String, Object> chatWithRole(
            @RequestParam(name = "role", defaultValue = "teacher") String role,
            @RequestParam(name = "message", defaultValue = "你好") String message) {

        // ================================================================
        // 不同角色的 System 提示词
        // ================================================================
        Map<String, String> rolePrompts = new HashMap<>();
        rolePrompts.put("teacher", "你是一位有20年教学经验的优秀教师，擅长用通俗易懂的方式讲解知识");
        rolePrompts.put("doctor", "你是一位经验丰富的医生，回答要专业、准确、有温度");
        rolePrompts.put("lawyer", "你是一位资深律师，回答要严谨、专业、依法依规");
        rolePrompts.put("poet", "你是一位著名的诗人，回答要富有诗意、优美动人");
        rolePrompts.put("engineer", "你是一位资深软件工程师，回答要技术性强、实用性好");
        rolePrompts.put("default", "你是一个友好的AI助手");

        String systemPrompt = rolePrompts.getOrDefault(role, rolePrompts.get("default"));
        log.info("====== 【本模块特有】使用角色：{} ======", role);
        log.info("System: {}", systemPrompt);
        log.info("User: {}", message);

        long startTime = System.currentTimeMillis();

        String response = defaultChatClient.prompt()
                .system(systemPrompt)
                .user(message)
                .call()
                .content();

        long endTime = System.currentTimeMillis();

        Map<String, Object> result = new HashMap<>();
        result.put("model", "Ollama（本地）");
        result.put("role", role);
        result.put("systemPrompt", systemPrompt);
        result.put("message", message);
        result.put("response", response);
        result.put("responseTime", endTime - startTime + "ms");
        result.put("availableRoles", List.of("teacher", "doctor", "lawyer", "poet", "engineer"));
        return result;
    }

    /**
     * 【本模块特有】流式响应演示
     *
     * 使用 Server-Sent Events (SSE) 实现逐字输出
     * 适合聊天应用场景，提升用户体验
     *
     * 测试方式：GET /api/chat/stream?message=讲个故事
     *
     * @param message 用户消息
     * @return Flux 流式响应
     */
    @GetMapping(value = "/stream", produces = "text/event-stream;charset=UTF-8")
    public Object chatStream(
            @RequestParam(name = "message", defaultValue = "请用中文讲一个简短的故事") String message) {
        log.info("====== 【本模块特有】流式响应 ======");
        log.info("消息：{}", message);

        try {
            // ================================================================
            // 使用 stream() 代替 call() 实现流式响应
            // ================================================================
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
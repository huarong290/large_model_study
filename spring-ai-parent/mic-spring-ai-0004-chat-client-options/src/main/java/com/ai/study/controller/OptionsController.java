package com.ai.study.controller;

import com.ai.study.enums.PresetMode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 参数配置 Controller
 *
 * 功能说明：
 * 1. 演示不同参数配置的效果
 * 2. 提供预设参数模式
 * 3. 支持自定义参数
 *
 * @author AI Study
 */
@RestController
@RequestMapping("/api/options")
@Slf4j
@RequiredArgsConstructor
public class OptionsController {

    @Qualifier("ollamaChatClient")
    private final ChatClient ollamaClient;

    // ================================================================
    // 1. 预设模式测试
    // ================================================================

    /**
     * 使用预设参数模式进行对话
     *
     * 测试方式：GET /api/options/preset?mode=creative&message=写一首关于春天的诗
     *
     * 可选模式：
     * - creative: 创意模式（高温度）
     * - balanced: 平衡模式（默认）
     * - precise: 精准模式（低温度）
     * - concise: 简洁模式（短输出）
     * - verbose: 详细模式（长输出）
     */
    @GetMapping("/preset")
    public Map<String, Object> chatWithPreset(
            @RequestParam(name = "mode", defaultValue = "balanced") String mode,
            @RequestParam(name = "message", defaultValue = "你好") String message) {

        PresetMode preset = PresetMode.fromCode(mode);
        log.info("🎯 使用预设模式：{} - {}", preset.getName(), preset.getDescription());
        log.info("💬 消息：{}", message);

        long startTime = System.currentTimeMillis();

        try {
            // 构建参数
            OpenAiChatOptions options = OpenAiChatOptions.builder()
                    .temperature(preset.getTemperature())
                    .topP(preset.getTopP())
                    .maxTokens(preset.getMaxTokens())
                    .build();

            String response = ollamaClient.prompt()
                    .user(message)
                    .options(options)
                    .call()
                    .content();

            long endTime = System.currentTimeMillis();

            Map<String, Object> result = new HashMap<>();
            result.put("mode", preset.getName());
            result.put("temperature", preset.getTemperature());
            result.put("topP", preset.getTopP());
            result.put("maxTokens", preset.getMaxTokens());
            result.put("description", preset.getDescription());
            result.put("message", message);
            result.put("response", response);
            result.put("responseTime", endTime - startTime + "ms");
            result.put("responseLength", response.length());
            return result;

        } catch (Exception e) {
            log.error("❌ 调用失败：{}", e.getMessage());
            Map<String, Object> result = new HashMap<>();
            result.put("error", "调用失败");
            result.put("message", e.getMessage());
            return result;
        }
    }

    // ================================================================
    // 2. 自定义参数测试
    // ================================================================

    /**
     * 自定义参数对话
     *
     * 测试方式：GET /api/options/custom?message=你好&temperature=0.1&maxTokens=100&topP=0.5
     *
     * @param message 用户消息
     * @param temperature 温度 (0.0-2.0)
     * @param maxTokens 最大输出 Token
     * @param topP 核采样 (0.0-1.0)
     */
    @GetMapping("/custom")
    public Map<String, Object> chatWithCustomOptions(
            @RequestParam(name = "message", defaultValue = "你好") String message,
            @RequestParam(name = "temperature", defaultValue = "0.7") Double temperature,
            @RequestParam(name = "maxTokens", defaultValue = "1024") Integer maxTokens,
            @RequestParam(name = "topP", defaultValue = "0.9") Double topP) {

        log.info("🎯 自定义参数：temperature={}, maxTokens={}, topP={}", temperature, maxTokens, topP);
        log.info("💬 消息：{}", message);

        long startTime = System.currentTimeMillis();

        try {
            OpenAiChatOptions options = OpenAiChatOptions.builder()
                    .temperature(temperature)
                    .topP(topP)
                    .maxTokens(maxTokens)
                    .build();

            String response = ollamaClient.prompt()
                    .user(message)
                    .options(options)
                    .call()
                    .content();

            long endTime = System.currentTimeMillis();

            Map<String, Object> result = new HashMap<>();
            result.put("temperature", temperature);
            result.put("topP", topP);
            result.put("maxTokens", maxTokens);
            result.put("message", message);
            result.put("response", response);
            result.put("responseTime", endTime - startTime + "ms");
            result.put("responseLength", response.length());
            return result;

        } catch (Exception e) {
            log.error("❌ 调用失败：{}", e.getMessage());
            Map<String, Object> result = new HashMap<>();
            result.put("error", "调用失败");
            result.put("message", e.getMessage());
            return result;
        }
    }

    // ================================================================
    // 3. 参数对比测试
    // ================================================================

    /**
     * 对比不同温度的效果
     *
     * 测试方式：GET /api/options/compare-temperature?message=写一首关于春天的诗
     */
    @GetMapping("/compare-temperature")
    public Map<String, Object> compareTemperature(
            @RequestParam(name = "message", defaultValue = "写一首关于春天的诗") String message) {

        log.info("📊 对比不同温度的效果：{}", message);

        Map<String, Object> results = new HashMap<>();

        // 测试不同温度
        double[] temperatures = {0.0, 0.3, 0.7, 1.2, 1.8};

        for (double temp : temperatures) {
            try {
                long start = System.currentTimeMillis();
                OpenAiChatOptions options = OpenAiChatOptions.builder()
                        .temperature(temp)
                        .maxTokens(512)
                        .build();

                String response = ollamaClient.prompt()
                        .user(message)
                        .options(options)
                        .call()
                        .content();
                long end = System.currentTimeMillis();

                Map<String, Object> tempResult = new HashMap<>();
                tempResult.put("response", response);
                tempResult.put("time", (end - start) + "ms");
                tempResult.put("length", response.length());
                results.put("temperature=" + temp, tempResult);

            } catch (Exception e) {
                Map<String, Object> errorResult = new HashMap<>();
                errorResult.put("error", e.getMessage());
                results.put("temperature=" + temp, errorResult);
            }
        }

        return results;
    }

    /**
     * 对比不同 MaxTokens 的效果
     *
     * 测试方式：GET /api/options/compare-tokens?message=详细介绍人工智能
     */
    @GetMapping("/compare-tokens")
    public Map<String, Object> compareMaxTokens(
            @RequestParam(name = "message", defaultValue = "详细介绍人工智能") String message) {

        log.info("📊 对比不同 MaxTokens 的效果：{}", message);

        Map<String, Object> results = new HashMap<>();

        int[] tokenLimits = {50, 200, 500, 1000, 2000};

        for (int limit : tokenLimits) {
            try {
                long start = System.currentTimeMillis();
                OpenAiChatOptions options = OpenAiChatOptions.builder()
                        .temperature(0.7)
                        .maxTokens(limit)
                        .build();

                String response = ollamaClient.prompt()
                        .user(message)
                        .options(options)
                        .call()
                        .content();
                long end = System.currentTimeMillis();

                Map<String, Object> tokenResult = new HashMap<>();
                tokenResult.put("response", response);
                tokenResult.put("time", (end - start) + "ms");
                tokenResult.put("length", response.length());
                results.put("maxTokens=" + limit, tokenResult);

            } catch (Exception e) {
                Map<String, Object> errorResult = new HashMap<>();
                errorResult.put("error", e.getMessage());
                results.put("maxTokens=" + limit, errorResult);
            }
        }

        return results;
    }

    // ================================================================
    // 4. 参数信息
    // ================================================================

    /**
     * 获取所有预设模式信息
     *
     * 测试方式：GET /api/options/presets
     */
    @GetMapping("/presets")
    public Map<String, Object> getPresets() {
        Map<String, Object> result = new HashMap<>();

        Map<String, Map<String, Object>> presets = new HashMap<>();
        for (PresetMode preset : PresetMode.values()) {
            Map<String, Object> info = new HashMap<>();
            info.put("name", preset.getName());
            info.put("temperature", preset.getTemperature());
            info.put("topP", preset.getTopP());
            info.put("maxTokens", preset.getMaxTokens());
            info.put("description", preset.getDescription());
            presets.put(preset.getCode(), info);
        }

        result.put("presets", presets);
        result.put("default", "balanced");
        result.put("tip", "使用 /api/options/preset?mode={mode}&message=你的消息 进行测试");
        return result;
    }
}
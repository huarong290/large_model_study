package com.ai.study.enums;

import lombok.Getter;

/**
 * 模型类型枚举
 *
 * @author AI Study
 */
@Getter
public enum ModelType {

    OLLAMA("ollama", "Ollama 本地模型", "免费，数据隐私安全，可离线"),
    DEEPSEEK("deepseek", "DeepSeek 云端模型", "性价比高，中文友好，代码能力强"),
    GEMINI("gemini", "Google Gemini 云端模型", "免费，多模态能力，推理强"),
    OPENROUTER("openrouter", "OpenRouter 云端模型", "聚合平台，多模型选择，支持免费开源模型"),
    AUTO("auto", "自动路由", "根据消息类型自动选择模型");

    private final String code;
    private final String name;
    private final String description;

    ModelType(String code, String name, String description) {
        this.code = code;
        this.name = name;
        this.description = description;
    }

    public static ModelType fromCode(String code) {
        for (ModelType type : values()) {
            if (type.code.equalsIgnoreCase(code)) {
                return type;
            }
        }
        return OLLAMA;
    }
}
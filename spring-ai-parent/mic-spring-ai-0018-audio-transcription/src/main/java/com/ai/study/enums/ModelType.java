package com.ai.study.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ModelType {
    OLLAMA("ollama", "本地模型"),
    DEEPSEEK("deepseek", "云端模型"),
    AUTO("auto", "自动路由");

    private final String code;
    private final String name;
}

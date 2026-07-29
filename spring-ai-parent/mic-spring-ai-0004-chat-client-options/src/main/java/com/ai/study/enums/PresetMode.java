package com.ai.study.enums;

import lombok.Getter;

/**
 * 参数预设模式
 *
 * @author AI Study
 */
@Getter
public enum PresetMode {

    CREATIVE("creative", "创意模式", 1.2, 0.95, 1024, "高温度、高多样性，适合创意写作"),
    BALANCED("balanced", "平衡模式", 0.7, 0.9, 1024, "中庸之道，适合日常对话"),
    PRECISE("precise", "精准模式", 0.1, 0.5, 2048, "低温度，适合精确问答、代码生成"),
    CONCISE("concise", "简洁模式", 0.5, 0.8, 512, "输出短小精悍，适合摘要总结"),
    VERBOSE("verbose", "详细模式", 0.8, 0.95, 4096, "长输出，适合详细解释");

    private final String code;
    private final String name;
    private final Double temperature;
    private final Double topP;
    private final Integer maxTokens;
    private final String description;

    PresetMode(String code, String name, Double temperature, Double topP, Integer maxTokens, String description) {
        this.code = code;
        this.name = name;
        this.temperature = temperature;
        this.topP = topP;
        this.maxTokens = maxTokens;
        this.description = description;
    }

    public static PresetMode fromCode(String code) {
        for (PresetMode mode : values()) {
            if (mode.code.equalsIgnoreCase(code)) {
                return mode;
            }
        }
        return BALANCED;
    }
}

package com.ai.study.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 评估维度枚举
 *
 * @author AI Study
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum EvaluationDimensionEnum {

    ACCURACY("accuracy", "准确性", "回答是否准确无误"),
    COMPLETENESS("completeness", "完整性", "回答是否全面完整"),
    RELEVANCE("relevance", "相关性", "回答是否切题相关"),
    FLUENCY("fluency", "流畅性", "回答是否自然流畅"),
    USEFULNESS("usefulness", "有用性", "回答是否解决问题");

    private final String code;
    private final String name;
    private final String description;
}

package com.ai.study.model;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 人物信息 Record
 *
 * <p>使用 Java Record 作为数据传输对象（DTO），比传统 Java Bean 更简洁。</p>
 *
 * @param name       人物姓名
 * @param age        年龄
 * @param city       所在城市
 * @param occupation 职业
 *
 * @author AI Study
 * @since 1.0.0
 */
@Schema(description = "人物信息（Record）")
public record PersonRecord(

        @Schema(description = "人物姓名", example = "张三")
        String name,

        @Schema(description = "年龄", example = "25")
        Integer age,

        @Schema(description = "所在城市", example = "北京")
        String city,

        @Schema(description = "职业", example = "软件工程师")
        String occupation

) {}
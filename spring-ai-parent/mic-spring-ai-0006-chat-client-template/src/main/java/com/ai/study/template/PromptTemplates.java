package com.ai.study.template;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * Prompt 模板定义
 *
 * @author AI Study
 */
@Component
@Getter
public class PromptTemplates {

    // ================================================================
    // 命名占位符模板 {name}
    // ================================================================

    /**
     * 自我介绍模板
     * 占位符：{name}, {age}, {city}, {hobby}
     */
    public static final String INTRODUCE_SELF = """
            请帮我做一个自我介绍。
            姓名：{name}
            年龄：{age}
            城市：{city}
            爱好：{hobby}
            
            请用第一人称，以友好、热情的语气介绍自己。
            字数控制在100字左右。
            """;

    /**
     * 角色扮演模板
     * 占位符：{role}, {scenario}, {tone}
     */
    public static final String ROLE_PLAY = """
            你是一个{role}。
            现在的情景是：{scenario}
            请用{tone}的语气来回应。
            
            要求：
            1. 符合角色身份
            2. 回应要自然、生动
            3. 不要脱离角色设定
            """;

    /**
     * 知识问答模板
     * 占位符：{topic}, {level}, {detail}
     */
    public static final String KNOWLEDGE_QA = """
            请用{level}的水平解释{topic}。
            如果可能，请包含以下细节：{detail}
            
            要求：
            1. 语言通俗易懂
            2. 结构清晰有条理
            3. 适当举例说明
            """;

    // ================================================================
    // 位置占位符模板 {0}, {1}, {2}
    // ================================================================

    /**
     * 翻译模板
     * 位置占位符：{0}=文本, {1}=目标语言, {2}=风格
     */
    public static final String TRANSLATE = """
            请将以下文本翻译成{1}：
            
            {0}
            
            翻译要求：
            1. 保持原意
            2. 风格：{2}
            3. 如果有多义词，选择最合适的
            """;

    /**
     * 总结模板
     * 位置占位符：{0}=文本, {1}=字数限制, {2}=重点
     */
    public static final String SUMMARIZE = """
            请总结以下文本，字数控制在{1}字以内：
            
            {0}
            
            重点要突出：{2}
            """;

    /**
     * 代码生成模板
     * 位置占位符：{0}=语言, {1}=功能, {2}=要求
     */
    public static final String CODE_GENERATE = """
            请用{0}编写一个{1}的程序。
            
            要求：{2}
            
            代码要求：
            1. 包含必要的注释
            2. 考虑边界情况
            3. 代码风格规范
            """;

    // ================================================================
    // 混合占位符模板
    // ================================================================

    /**
     * 混合模板（位置 + 命名）
     */
    public static final String MIXED = """
            请根据以下信息回答问题：
            
            主题：{topic}
            问题：{0}
            上下文：{1}
            
            回答要求：
            1. 基于提供的上下文
            2. 如果信息不足，请说明
            3. 语言：{language}
            """;

    // ================================================================
    // 预定义模板列表
    // ================================================================

    public static final List<TemplateInfo> TEMPLATE_LIST = Arrays.asList(
            new TemplateInfo("introduce", "自我介绍", INTRODUCE_SELF,
                    Arrays.asList("name", "age", "city", "hobby")),
            new TemplateInfo("role_play", "角色扮演", ROLE_PLAY,
                    Arrays.asList("role", "scenario", "tone")),
            new TemplateInfo("knowledge", "知识问答", KNOWLEDGE_QA,
                    Arrays.asList("topic", "level", "detail")),
            new TemplateInfo("translate", "翻译", TRANSLATE,
                    Arrays.asList("0", "1", "2")),
            new TemplateInfo("summarize", "总结", SUMMARIZE,
                    Arrays.asList("0", "1", "2")),
            new TemplateInfo("code", "代码生成", CODE_GENERATE,
                    Arrays.asList("0", "1", "2")),
            new TemplateInfo("mixed", "混合模板", MIXED,
                    Arrays.asList("topic", "0", "1", "language"))
    );

    /**
     * 模板信息
     */
    @Getter
    public static class TemplateInfo {
        private final String id;
        private final String name;
        private final String template;
        private final List<String> params;

        public TemplateInfo(String id, String name, String template, List<String> params) {
            this.id = id;
            this.name = name;
            this.template = template;
            this.params = params;
        }
    }
}
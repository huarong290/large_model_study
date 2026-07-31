package com.ai.study.service;

import com.ai.study.template.PromptTemplates;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class TemplateService {

    @Qualifier("ollamaChatClient")
    private final ChatClient chatClient;

    // ================================================================
    // 1. 命名占位符模板
    // ================================================================

    public String renderNamedTemplate(String template, Map<String, Object> params) {
        log.info("📝 渲染命名占位符模板，参数：{}", params.keySet());
        PromptTemplate promptTemplate = new PromptTemplate(template);
        return promptTemplate.render(params);
    }

    public String chatWithNamedTemplate(String templateId, Map<String, Object> params) {
        log.info("💬 使用命名模板 [{}] 对话", templateId);

        String template = getTemplateById(templateId);
        if (template == null) {
            throw new IllegalArgumentException("模板不存在：" + templateId);
        }

        String prompt = renderNamedTemplate(template, params);
        log.info("📝 渲染后的 Prompt：\n{}", prompt);

        return chatClient.prompt()
                .user(prompt)
                .call()
                .content();
    }

    // ================================================================
    // 2. 位置占位符模板（支持两种调用方式）
    // ================================================================

    /**
     * 使用位置占位符模板（可变参数方式）
     */
    public String renderPositionalTemplate(String template, Object... params) {
        log.info("📝 渲染位置占位符模板，参数数量：{}", params.length);
        PromptTemplate promptTemplate = new PromptTemplate(template);

        Map<String, Object> paramMap = new HashMap<>();
        for (int i = 0; i < params.length; i++) {
            paramMap.put(String.valueOf(i), params[i]);
        }

        return promptTemplate.render(paramMap);
    }

    /**
     * ✅ 使用位置占位符模板对话（可变参数方式）
     * 用于：batchTest() 和直接调用
     */
    public String chatWithPositionalTemplate(String templateId, Object... params) {
        log.info("💬 使用位置模板 [{}] 对话（可变参数），参数数量：{}", templateId, params.length);

        String template = getTemplateById(templateId);
        if (template == null) {
            throw new IllegalArgumentException("模板不存在：" + templateId);
        }

        String prompt = renderPositionalTemplate(template, params);
        log.info("📝 渲染后的 Prompt：\n{}", prompt);

        return chatClient.prompt()
                .user(prompt)
                .call()
                .content();
    }

    /**
     * ✅ 使用位置占位符模板对话（List 方式）
     * 用于：Controller 中传入 List 参数
     */
    public String chatWithPositionalTemplateFromList(String templateId, List<Object> params) {
        log.info("💬 使用位置模板 [{}] 对话（List），参数数量：{}", templateId, params.size());

        String template = getTemplateById(templateId);
        if (template == null) {
            throw new IllegalArgumentException("模板不存在：" + templateId);
        }

        // 将 List 转为数组
        String prompt = renderPositionalTemplate(template, params.toArray());
        log.info("📝 渲染后的 Prompt：\n{}", prompt);

        return chatClient.prompt()
                .user(prompt)
                .call()
                .content();
    }

    // ================================================================
    // 3. 混合模板
    // ================================================================

    public String renderMixedTemplate(String template, Map<String, Object> params) {
        log.info("📝 渲染混合占位符模板");
        PromptTemplate promptTemplate = new PromptTemplate(template);
        return promptTemplate.render(params);
    }

    // ================================================================
    // 4. 模板管理
    // ================================================================

    public List<PromptTemplates.TemplateInfo> getAllTemplates() {
        return PromptTemplates.TEMPLATE_LIST;
    }

    public String getTemplateById(String id) {
        return PromptTemplates.TEMPLATE_LIST.stream()
                .filter(t -> t.getId().equals(id))
                .map(PromptTemplates.TemplateInfo::getTemplate)
                .findFirst()
                .orElse(null);
    }

    public PromptTemplates.TemplateInfo getTemplateInfo(String id) {
        return PromptTemplates.TEMPLATE_LIST.stream()
                .filter(t -> t.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    // ================================================================
    // 5. 批量测试
    // ================================================================

    public Map<String, String> batchTest() {
        log.info("📊 批量测试模板");

        Map<String, String> results = new HashMap<>();

        // 1. 自我介绍模板（命名占位符）
        Map<String, Object> introParams = new HashMap<>();
        introParams.put("name", "小智");
        introParams.put("age", "25");
        introParams.put("city", "北京");
        introParams.put("hobby", "编程、阅读");
        results.put("introduce", chatWithNamedTemplate("introduce", introParams));

        // 2. 翻译模板（可变参数方式）
        results.put("translate", chatWithPositionalTemplate("translate",
                "Hello, world!", "中文", "正式"));

        // 3. 总结模板（可变参数方式）
        String longText = "人工智能（AI）是计算机科学的一个分支，致力于创建能够执行通常需要人类智能的任务的系统。";
        results.put("summarize", chatWithPositionalTemplate("summarize",
                longText, "30", "核心定义"));

        // 4. 代码生成模板（可变参数方式）
        results.put("code", chatWithPositionalTemplate("code",
                "Java", "冒泡排序", "代码规范、注释完整"));

        // 5. 知识问答模板（命名占位符）
        Map<String, Object> knowledgeParams = new HashMap<>();
        knowledgeParams.put("topic", "Spring AI");
        knowledgeParams.put("level", "初学者");
        knowledgeParams.put("detail", "基本概念和使用方法");
        results.put("knowledge", chatWithNamedTemplate("knowledge", knowledgeParams));

        log.info("✅ 批量测试完成，共 {} 个模板", results.size());
        return results;
    }
}
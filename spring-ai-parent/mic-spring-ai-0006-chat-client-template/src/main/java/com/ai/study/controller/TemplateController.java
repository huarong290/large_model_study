package com.ai.study.controller;

import com.ai.study.service.TemplateService;
import com.ai.study.template.PromptTemplates;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/template")
@Slf4j
@RequiredArgsConstructor
public class TemplateController {

    private final TemplateService templateService;

    // ================================================================
    // 1. 模板列表
    // ================================================================

    @GetMapping("/list")
    public Map<String, Object> listTemplates() {
        List<PromptTemplates.TemplateInfo> templates = templateService.getAllTemplates();

        Map<String, Object> result = new HashMap<>();
        result.put("count", templates.size());
        result.put("templates", templates);
        result.put("usage", Map.of(
                "named", "使用命名占位符 {name}，调用 POST /api/template/named/{id}",
                "positional", "使用位置占位符 {0}，调用 POST /api/template/positional/{id}"
        ));
        return result;
    }

    @GetMapping("/{id}")
    public Map<String, Object> getTemplate(@PathVariable("id") String id) {
        PromptTemplates.TemplateInfo info = templateService.getTemplateInfo(id);
        if (info == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "模板不存在：" + id);
            return error;
        }

        Map<String, Object> result = new HashMap<>();
        result.put("id", info.getId());
        result.put("name", info.getName());
        result.put("template", info.getTemplate());
        result.put("params", info.getParams());
        return result;
    }

    // ================================================================
    // 2. 命名占位符模板（POST）
    // ================================================================

    @PostMapping("/named/{id}")
    public Map<String, Object> chatWithNamedTemplate(
            @PathVariable("id") String id,
            @RequestBody Map<String, Object> params) {

        log.info("📝 命名模板 [{}] 参数：{}", id, params.keySet());

        try {
            String response = templateService.chatWithNamedTemplate(id, params);

            Map<String, Object> result = new HashMap<>();
            result.put("templateId", id);
            result.put("params", params);
            result.put("response", response);
            return result;

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return error;
        }
    }

    // ================================================================
    // 3. 位置占位符模板（POST）
    // ================================================================

    @PostMapping("/positional/{id}")
    public Map<String, Object> chatWithPositionalTemplate(
            @PathVariable("id") String id,
            @RequestBody Map<String, Object> request) {

        List<Object> params = (List<Object>) request.get("params");
        if (params == null || params.isEmpty()) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "请提供 params 数组");
            return error;
        }

        log.info("📝 位置模板 [{}] 参数数量：{}", id, params.size());

        try {
            // 调用 chatWithPositionalTemplateFromList 而不是 chatWithPositionalTemplate
            String response = templateService.chatWithPositionalTemplateFromList(id, params);
            Map<String, Object> result = new HashMap<>();
            result.put("templateId", id);
            result.put("params", params);
            result.put("response", response);
            return result;

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return error;
        }
    }

    // ================================================================
    // 4. 快捷接口（GET）
    // ================================================================

    /**
     * 通用位置模板调用
     */
    private Map<String, Object> callPositionalTemplate(String id, Object... params) {
        try {
            String response = templateService.chatWithPositionalTemplate(id, params);

            Map<String, Object> result = new HashMap<>();
            result.put("templateId", id);
            result.put("params", List.of(params));
            result.put("response", response);
            return result;

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return error;
        }
    }

    @GetMapping("/introduce")
    public Map<String, Object> introduce(
            @RequestParam(value = "name", defaultValue = "小智") String name,
            @RequestParam(value = "age", defaultValue = "25") String age,
            @RequestParam(value = "city", defaultValue = "北京") String city,
            @RequestParam(value = "hobby", defaultValue = "编程、阅读") String hobby) {

        Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        params.put("age", age);
        params.put("city", city);
        params.put("hobby", hobby);

        try {
            String response = templateService.chatWithNamedTemplate("introduce", params);

            Map<String, Object> result = new HashMap<>();
            result.put("templateId", "introduce");
            result.put("params", params);
            result.put("response", response);
            return result;

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return error;
        }
    }

    @GetMapping("/role-play")
    public Map<String, Object> rolePlay(
            @RequestParam(value = "role", defaultValue = "程序员") String role,
            @RequestParam(value = "scenario", defaultValue = "面试") String scenario,
            @RequestParam(value = "tone", defaultValue = "自信专业") String tone) {

        Map<String, Object> params = new HashMap<>();
        params.put("role", role);
        params.put("scenario", scenario);
        params.put("tone", tone);

        try {
            String response = templateService.chatWithNamedTemplate("role_play", params);

            Map<String, Object> result = new HashMap<>();
            result.put("templateId", "role_play");
            result.put("params", params);
            result.put("response", response);
            return result;

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return error;
        }
    }

    @GetMapping("/knowledge")
    public Map<String, Object> knowledge(
            @RequestParam(value = "topic", defaultValue = "Spring AI") String topic,
            @RequestParam(value = "level", defaultValue = "初学者") String level,
            @RequestParam(value = "detail", defaultValue = "基本概念和使用方法") String detail) {

        Map<String, Object> params = new HashMap<>();
        params.put("topic", topic);
        params.put("level", level);
        params.put("detail", detail);

        try {
            String response = templateService.chatWithNamedTemplate("knowledge", params);

            Map<String, Object> result = new HashMap<>();
            result.put("templateId", "knowledge");
            result.put("params", params);
            result.put("response", response);
            return result;

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return error;
        }
    }

    @GetMapping("/translate")
    public Map<String, Object> translate(
            @RequestParam(value = "text", defaultValue = "Hello, world!") String text,
            @RequestParam(value = "target", defaultValue = "中文") String target,
            @RequestParam(value = "style", defaultValue = "正式") String style) {

        return callPositionalTemplate("translate", text, target, style);
    }

    @GetMapping("/summarize")
    public Map<String, Object> summarize(
            @RequestParam(value = "text") String text,
            @RequestParam(value = "limit", defaultValue = "100") String limit,
            @RequestParam(value = "focus", defaultValue = "核心内容") String focus) {

        return callPositionalTemplate("summarize", text, limit, focus);
    }

    @GetMapping("/code")
    public Map<String, Object> generateCode(
            @RequestParam(value = "language", defaultValue = "Java") String language,
            @RequestParam(value = "function", defaultValue = "冒泡排序") String function,
            @RequestParam(value = "requirement", defaultValue = "代码规范、注释完整") String requirement) {

        return callPositionalTemplate("code", language, function, requirement);
    }

    // ================================================================
    // 5. 批量测试
    // ================================================================

    @GetMapping("/batch-test")
    public Map<String, String> batchTest() {
        log.info("📊 执行批量模板测试");
        return templateService.batchTest();
    }
}
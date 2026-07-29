package com.ai.study.service;

import com.ai.study.enums.ModelType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
@Slf4j
public class ModelRouterService {

    @Qualifier("modelRegistry")
    private final Map<String, ChatClient> modelRegistry;

    @Value("${model.router.default:ollama}")
    private String defaultModel;

    // ================================================================
    // 使用 Set 存储关键词，查找效率 O(1)
    // ================================================================

    private static final Set<String> CODE_KEYWORDS = new HashSet<>(Arrays.asList(
            "代码", "编程", "java", "python", "写代码",
            "debug", "bug", "算法", "数据结构", "开发", "程序"
    ));

    private static final Set<String> CREATIVE_KEYWORDS = new HashSet<>(Arrays.asList(
            "写诗", "诗", "诗歌", "故事", "创意", "创作",
            "歌词", "剧本", "艺术", "设计", "散文", "小说",
            "文学", "童话", "寓言"
    ));

    private static final Set<String> TRANSLATE_KEYWORDS = new HashSet<>(Arrays.asList(
            "翻译", "译", "英文", "中英文"
    ));

    private static final Set<String> REASONING_KEYWORDS = new HashSet<>(Arrays.asList(
            "分析", "推理", "逻辑", "论证", "证明", "为什么"
    ));
    // 👈 新增：OpenRouter 关键词（可根据你的实际需求调整，比如复杂综合任务）
    private static final Set<String> OPENROUTER_KEYWORDS = new HashSet<>(Arrays.asList(
            "综合", "高级", "cloud", "openrouter", "评测"
    ));
    private static final Set<String> SIMPLE_KEYWORDS = new HashSet<>(Arrays.asList(
            "你好", "hi", "hello", "介绍", "是什么", "什么是"
    ));

    public ModelRouterService(@Qualifier("modelRegistry") Map<String, ChatClient> modelRegistry) {
        this.modelRegistry = modelRegistry;
        log.info(" 模型路由服务初始化，注册模型：{}", modelRegistry.keySet());
    }

    public ChatClient route(String message) {
        ModelType modelType = selectModel(message);
        ChatClient client = modelRegistry.get(modelType.getCode());
        // ================================================================
        // ✅ 关键诊断：打印从 registry 获取的 client
        // ================================================================
        log.info("🔍 [路由诊断] 选择的模型类型: {}", modelType.getName());
        log.info("🔍 [路由诊断] 从 registry 获取的 client: {}", client);
        log.info("🔍 [路由诊断] registry 内容: {}", modelRegistry.keySet());
        if (client == null) {
            log.warn("⚠️ 模型 {} 不可用，降级到默认模型", modelType.getName());
            client = modelRegistry.get(defaultModel);
        }

        log.info("🔀 路由决策：消息类型={} → 选择模型={}",
                modelType.getName(),
                client != null ? "可用" : "不可用");
        return client;
    }

    /**
     *  使用 Set 匹配，效率更高
     */
    private ModelType selectModel(String message) {
        String lowerMsg = message.toLowerCase();

        // 按优先级匹配
        if (containsAny(lowerMsg, CODE_KEYWORDS)) {
            log.debug("   🔍 匹配到代码关键词 → DeepSeek");
            return ModelType.DEEPSEEK;
        }

        if (containsAny(lowerMsg, CREATIVE_KEYWORDS)) {
            log.debug("   🔍 匹配到创意关键词 → Gemini");
            return ModelType.GEMINI;
        }

        if (containsAny(lowerMsg, TRANSLATE_KEYWORDS)) {
            log.debug("   🔍 匹配到翻译关键词 → DeepSeek");
            return ModelType.DEEPSEEK;
        }

        if (containsAny(lowerMsg, REASONING_KEYWORDS)) {
            log.debug("   🔍 匹配到推理关键词 → Gemini");
            return ModelType.GEMINI;
        }
        // OpenRouter 路由分支
        if (containsAny(lowerMsg, OPENROUTER_KEYWORDS)) {
            log.debug("   🔍 匹配到 OpenRouter 关键词 → OpenRouter");
            return ModelType.OPENROUTER;
        }
        if (containsAny(lowerMsg, SIMPLE_KEYWORDS)) {
            log.debug("   🔍 匹配到简单问答关键词 → Ollama");
            return ModelType.OLLAMA;
        }

        log.debug("   🔍 无特殊匹配 → 使用默认模型 Ollama");
        return ModelType.OLLAMA;
    }

    /**
     * Set 查找，时间复杂度 O(1)
     */
    private boolean containsAny(String message, Set<String> keywords) {
        for (String keyword : keywords) {
            if (message.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    public Map<String, ChatClient> getAvailableModels() {
        return modelRegistry;
    }

    public boolean isModelAvailable(String modelCode) {
        return modelRegistry.containsKey(modelCode) && modelRegistry.get(modelCode) != null;
    }
}
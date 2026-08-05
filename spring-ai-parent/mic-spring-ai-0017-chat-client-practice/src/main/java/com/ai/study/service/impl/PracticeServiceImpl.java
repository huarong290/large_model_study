package com.ai.study.service.impl;

import com.ai.study.advisor.AuditAdvisor;
import com.ai.study.advisor.ContentAuditAdvisor;
import com.ai.study.advisor.PerformanceAdvisor;
import com.ai.study.advisor.SensitiveWordAdvisor;
import com.ai.study.config.MultiLevelCacheConfig;
import com.ai.study.enums.ModelType;
import com.ai.study.manager.ChatMemoryManager;
import com.ai.study.model.ChatRequest;
import com.ai.study.model.ChatResponse;
import com.ai.study.service.PracticeService;
import com.ai.study.tools.CalculatorTool;
import com.ai.study.tools.WeatherTool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 智能对话业务服务实现类
 * <p>
 * 负责整合双大模型路由、多级缓存拦截、上下文记忆管理、Function Calling 工具调用以及 Advisor 责任链。
 * </p>
 *
 * @author AI Assistant
 * @version 1.1
 */
@Service
@Slf4j
public class PracticeServiceImpl implements PracticeService {

    /** 本地 Ollama 模型客户端 */
    private final ChatClient ollamaClient;

    /** 云端 DeepSeek 模型客户端 */
    private final ChatClient deepSeekClient;

    /** 会话历史记忆管理器 */
    private final ChatMemoryManager memoryManager;

    /** Function Calling 工具：天气查询 */
    private final WeatherTool weatherTool;

    /** Function Calling 工具：数学计算 */
    private final CalculatorTool calculatorTool;

    /** 责任链组件：审计日志 */
    private final AuditAdvisor auditAdvisor;

    /** 责任链组件：内容审核 */
    private final ContentAuditAdvisor contentAuditAdvisor;

    /** 责任链组件：敏感词过滤 */
    private final SensitiveWordAdvisor sensitiveWordAdvisor;

    /** 责任链组件：性能监测 */
    private final PerformanceAdvisor performanceAdvisor;

    /** 默认选用的模型标识 */
    @Value("${practice.default-model:ollama}")
    private String defaultModel;

    /** 是否开启对话记忆功能 */
    @Value("${practice.memory-enabled:true}")
    private boolean memoryEnabled;

    /** 单个会话保存的最大历史消息条数 */
    @Value("${practice.max-messages:10}")
    private int maxMessages;

    /**
     * 构造函数注入所需依赖
     */
    public PracticeServiceImpl(
            @Qualifier("ollamaChatClient") ChatClient ollamaClient,
            @Qualifier("deepSeekChatClient") ChatClient deepSeekClient,
            ChatMemoryManager memoryManager,
            WeatherTool weatherTool,
            CalculatorTool calculatorTool,
            AuditAdvisor auditAdvisor,
            ContentAuditAdvisor contentAuditAdvisor,
            SensitiveWordAdvisor sensitiveWordAdvisor,
            PerformanceAdvisor performanceAdvisor) {
        this.ollamaClient = ollamaClient;
        this.deepSeekClient = deepSeekClient;
        this.memoryManager = memoryManager;
        this.weatherTool = weatherTool;
        this.calculatorTool = calculatorTool;
        this.auditAdvisor = auditAdvisor;
        this.contentAuditAdvisor = contentAuditAdvisor;
        this.sensitiveWordAdvisor = sensitiveWordAdvisor;
        this.performanceAdvisor = performanceAdvisor;
    }

    /**
     * 同步 Blocking 对话接口
     *
     * @param request 对话请求参数包 (包含 sessionId、模型类型、提问内容等)
     * @return 封装好的对话响应结构体
     */
    @Override
    @Cacheable(value = MultiLevelCacheConfig.AI_RESPONSE_CACHE, key = "#request.sessionId + '_' + #request.message")
    public ChatResponse chat(ChatRequest request) {
        log.info("💬 接收对话请求：sessionId={}, message={}", request.getSessionId(), request.getMessage());

        long startTime = System.currentTimeMillis();
        // 若没有传入 sessionId，自动生成唯一标识
        String sessionId = (request.getSessionId() != null && !request.getSessionId().isEmpty())
                ? request.getSessionId()
                : UUID.randomUUID().toString();

        try {
            // 装配包含指定 Advisor 和 Memory 的 ChatClient
            ChatClient client = buildConfiguredClient(request.getModel(), sessionId);

            // 发起模型调用
            String answer = client.prompt()
                    .user(request.getMessage())
                    .tools(weatherTool, calculatorTool)
                    .call()
                    .content();

            long duration = System.currentTimeMillis() - startTime;

            return ChatResponse.builder()
                    .success(true)
                    .sessionId(sessionId)
                    .message(request.getMessage())
                    .answer(answer)
                    .model(getModelName(selectModel(request.getModel())))
                    .responseTime(duration)
                    .fromCache(false)
                    .fromMemory(memoryEnabled)
                    .timestamp(LocalDateTime.now())
                    .build();

        } catch (Exception e) {
            log.error("同步对话发生异常：{}", e.getMessage(), e);
            return ChatResponse.builder()
                    .success(false)
                    .sessionId(sessionId)
                    .message(request.getMessage())
                    .answer("抱歉，系统处理您的请求时出错了：" + e.getMessage())
                    .responseTime(System.currentTimeMillis() - startTime)
                    .timestamp(LocalDateTime.now())
                    .build();
        }
    }

    /**
     * 响应式流式对话接口 (SSE / Reactive Stream)
     *
     * @param request 对话请求参数包
     * @return 包含文本增量片段的 Flux 流
     */
    @Override
    public Flux<String> chatStream(ChatRequest request) {
        log.info("🌊 接收流式对话请求：sessionId={}, message={}", request.getSessionId(), request.getMessage());

        String sessionId = (request.getSessionId() != null && !request.getSessionId().isEmpty())
                ? request.getSessionId()
                : UUID.randomUUID().toString();

        ChatClient client = buildConfiguredClient(request.getModel(), sessionId);

        return client.prompt()
                .user(request.getMessage())
                .tools(weatherTool, calculatorTool)
                .stream()
                .content();
    }

    /**
     * 清理指定会话的上下文记忆
     *
     * @param sessionId 会话ID
     */
    @Override
    public void clearMemory(String sessionId) {
        log.info("🧹 清理会话上下文历史：sessionId={}", sessionId);
        memoryManager.clearMemory(sessionId);
    }

    /**
     * 获取当前对话系统的运行状态指标
     *
     * @return 状态统计 Map 数据
     */
    @Override
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("activeSessions", memoryManager.getActiveSessionCount());
        stats.put("totalCalls", performanceAdvisor.getTotalCalls());
        stats.put("avgResponseTime", String.format("%.2fms", performanceAdvisor.getAvgResponseTime()));
        stats.put("defaultModel", defaultModel);
        stats.put("memoryEnabled", memoryEnabled);
        stats.put("status", "UP");
        return stats;
    }

    /**
     * 构建经过配置（挂载 Advisor 责任链与 ChatMemory 记忆机制）的 ChatClient 实例
     *
     * @param modelCode 模型标识代码
     * @param sessionId 会话ID
     * @return 装配完毕的 ChatClient
     */
    private ChatClient buildConfiguredClient(String modelCode, String sessionId) {
        ChatClient baseClient = selectModel(modelCode);
        ChatClient.Builder builder = baseClient.mutate();

        // ✅ 优化：调整 Advisor 执行顺序（从外到内）
        // 1. 性能监控（最外层）- 统计整体耗时
        // 2. 审计日志 - 记录请求/响应
        // 3. 敏感词过滤（最内层）- 检查内容
        // 4. 内容审核（如果需要在敏感词之后）
        builder.defaultAdvisors(
                performanceAdvisor,     // 1. 最外层：性能监控
                auditAdvisor,           // 2. 审计日志
                sensitiveWordAdvisor,   // 3. 敏感词过滤
                contentAuditAdvisor     // 4. 内容审核
        );
        // 如果开启了会话历史记忆，通过 Builder API 构建 MessageChatMemoryAdvisor
        if (memoryEnabled && sessionId != null) {
            ChatMemory memory = memoryManager.getOrCreateMemory(sessionId);

            MessageChatMemoryAdvisor memoryAdvisor = MessageChatMemoryAdvisor.builder(memory)
                    .conversationId(sessionId)
                    .build();

            builder.defaultAdvisors(memoryAdvisor);
        }

        return builder.build();
    }

    /**
     * 根据传入的代码路由选择目标大模型 Client
     *
     * @param model 模型代码标识 (ollama / deepseek / auto)
     * @return 对应的 ChatClient 对象
     */
    private ChatClient selectModel(String model) {
        if (model == null || model.isEmpty() || ModelType.AUTO.getCode().equals(model)) {
            model = defaultModel;
        }
        if (ModelType.DEEPSEEK.getCode().equals(model)) {
            return deepSeekClient;
        }
        return ollamaClient;
    }

    /**
     * 获取客户端实例相对应的展示名称
     *
     * @param client ChatClient 实例
     * @return 模型展示名称
     */
    private String getModelName(ChatClient client) {
        if (client == ollamaClient) return "Ollama";
        if (client == deepSeekClient) return "DeepSeek";
        return "Unknown";
    }
}
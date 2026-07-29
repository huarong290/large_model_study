//package com.ai.study;
//
//import lombok.extern.slf4j.Slf4j;
//import org.junit.jupiter.api.Test;
//import org.springframework.ai.chat.client.ChatClient;
//import org.springframework.ai.chat.messages.Message;
//import org.springframework.ai.chat.messages.SystemMessage;
//import org.springframework.ai.chat.messages.UserMessage;
//import org.springframework.ai.chat.model.ChatResponse;
//import org.springframework.ai.chat.prompt.Prompt;
//import org.springframework.ai.openai.OpenAiChatOptions;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import reactor.core.publisher.Flux;
//
//import java.util.Arrays;
//
//@SpringBootTest
//@Slf4j
//public class ChatClientCoreFuncTest {
//
//    @Autowired
//    private ChatClient chatClient;
//    @Autowired
//    private ChatClient.Builder chatClientBuilder;
//    /**
//     * 测试功能点：链式调用 - 分别设置 System 和 User 消息
//     *
//     * 测试场景：
//     * 1. 使用链式调用 .system() 和 .user() 分别设置系统提示和用户问题
//     * 2. 验证 ChatClient 的流式 API 调用方式
//     * 3. System Message 在 User Message 之前设置，确保系统角色优先
//     *
//     * 预期结果：AI 以"地球国家地理专家"的身份，详细介绍中国的地理面积
//     */
//    @Test
//    void test_0001() {
//        String content1 = chatClient.prompt()
//                .user("介绍下中国的地理面积")
//                .system("你是一个地球国家地理专家，能很好的回复用户的各种地理问题")
//                .call()
//                .content();
//        log.info("test_0001 中 content1={}", content1);
//    }
//
//    /**
//     * 测试功能点：链式调用 - 将 System 指令放在 User 消息内容中
//     *
//     * 测试场景：
//     * 1. 只有 User 消息，但 System 角色指令被拼接在 User 消息中
//     * 2. 对比 test_0001 和 test_0002 的响应差异
//     * 3. 验证不同消息设置方式对 AI 响应质量的影响
//     *
//     * 注意：此方式 System 角色的指令可能被 AI 理解为用户消息的一部分，
//     * 可能导致响应不如 test_0001 中显式设置 System 消息那样精准
//     *
//     * 预期结果：AI 仍然能回答问题，但可能不如 test_0001 那样明确以专家身份回答
//     */
//    @Test
//    void test_0002() {
//        String content2 = chatClient.prompt()
//                .user("你是一个地球国家地理专家，能很好的回复用户的各种地理问题,介绍下中国的地理面积")
//                .call()
//                .content();
//        log.info("test_0002 中 content2={}", content2);
//    }
//
//    /**
//     * 测试功能点：使用 Prompt 对象 - 显式创建 Message 对象并构建 Prompt
//     *
//     * 测试场景：
//     * 1. 分别创建 UserMessage 和 SystemMessage 对象
//     * 2. 使用 Prompt 类包装多个 Message
//     * 3. 通过 chatClient.prompt(prompt) 传入完整的 Prompt 对象
//     *
//     * 功能优势：
//     * - 更面向对象的方式，便于复用 Message 对象
//     * - 可以灵活组合多个 SystemMessage、UserMessage 和 AssistantMessage
//     * - 适合复杂对话场景（多轮对话、多角色对话）
//     *
//     * 预期结果：与 test_0001 类似，AI 以"地球国家地理专家"身份回答问题
//     */
//    @Test
//    void test_0003() {
//        Message userMessage = new UserMessage("介绍下中国的地理面积");
//        Message systemMessage = new SystemMessage("你是一个地球国家地理专家，能很好的回复用户的各种地理问题");
//        Prompt prompt = new Prompt(Arrays.asList(userMessage, systemMessage));
//        String content3 = chatClient.prompt(prompt).call().content();
//        log.info("test_0003 中 content3={}", content3);
//    }
//
//    @Test
//    void test_0004() {
//        Message userMessage = new UserMessage("介绍下中国的地理面积");
//        Message systemMessage = new SystemMessage("你是一个地球国家地理专家，能很好的回复用户的各种地理问题");
//        Prompt prompt = new Prompt(Arrays.asList(userMessage, systemMessage));
//        ChatResponse chatResponse = chatClient.prompt(prompt).call().chatResponse();
//        log.info("test_0004 中 chatResponse={}", chatResponse);
//    }
//    /**
//     * 测试功能点：流式响应（Stream）
//     *
//     * 测试场景：
//     * 1. 使用 .stream() 代替 .call()
//     * 2. 通过 Flux 流式接收 AI 响应（逐字或逐句返回）
//     * 3. 适合需要实时显示回复内容的场景（如聊天应用）
//     */
//    @Test
//    void test_0005_stream_response() {
//        // 核心API使用
//        Flux<String> content = chatClient.prompt().user("介绍下中国的地理面积")
//                .system("你是一个地球国家地理专家，能很好的回复用户的各种地理问题")
//                .stream().content();
//        //方式2:收集为字符串
//        String result = content.reduce("",String::concat).block();
//        log.info("test_0005_stream_response 中 result={}", result);
//    }
//
//
//
//
//
//
//
//    /**
//     * 测试功能点：使用 Builder 配置默认参数
//     *
//     * 配置内容：
//     * 1. 默认系统消息：设定 AI 的角色
//     * 2. 默认温度：控制回答的创造性
//     * 3. 默认参数：通过 params 传递默认值
//     * 4. 默认 Advisor：添加切面功能（日志、审计等）
//     */
//    @Test
//    void test_default_configuration() {
//        // 创建自定义配置的 ChatClient
//        ChatClient customChatClient = chatClientBuilder
//                .defaultSystem("你是一个专业的中国地理专家，回答要详细、准确")
//                // 使用 defaultOptions 设置温度等参数
//                .defaultOptions(
//                        OpenAiChatOptions.builder()
//                                .temperature(0.3)  // 设置默认温度
//                                .model("deepseek-chat")   // 设置默认模型
//                                .maxTokens(2048)   // 设置最大输出
//                                .build()
//                )
//                .build();
//
//        // 使用自定义配置的 ChatClient
//        String response = customChatClient.prompt()
//                .user("介绍下中国的四大高原")
//                .call()
//                .content();
//
//        log.info("默认配置测试 - 四大高原：{}", response);
//    }
//
//    /**
//     * 测试功能点：参数化请求（带变量）
//     *
//     * 测试场景：
//     * 1. 使用 .params() 方法传入动态参数
//     * 2. 在 User 消息中使用占位符 {0}, {1} 等
//     * 3. 适合需要动态构建 prompt 的场景
//     *
//     * 示例：
//     * chatClient.prompt()
//     *     .user("介绍下{0}的地理面积")
//     *     .params("中国")
//     *     .call()
//     *     .content();
//  */
//    @Test
//    void test_parameterized_prompt() {
//        // 待实现
//    }
//
//
//    /*
//    /**
//     * 测试功能点：多轮对话
//     *
//     * 测试场景：
//     * 1. 保持上下文的多轮对话
//     * 2. 验证 AI 是否能记住之前的对话内容
//     * 3. 使用 ChatMemory 或 ConversationContext 维护状态
//     *
//     * 示例：
//     * 第一轮：用户问"中国面积有多大？"
//     * 第二轮：用户问"那人口呢？"（AI 应该能知道是在问中国的人口）
//     *
//    @Test
//    void test_multi_turn_conversation() {
//        // 待实现
//    }
//    */
//
//    /*
//    /**
//     * 测试功能点：结构化输出
//     *
//     * 测试场景：
//     * 1. 要求 AI 以 JSON、XML 等特定格式输出
//     * 2. 解析返回结果到 Java 对象
//     * 3. 使用 .entity() 方法自动反序列化
//     *
//     * 示例：
//     * chatClient.prompt()
//     *     .user("请以JSON格式返回中国的地理信息")
//     *     .call()
//     *     .entity(GeoInfo.class);
//     *
//    @Test
//    void test_structured_output() {
//        // 待实现
//    }
//    */
//
//    /*
//    /**
//     * 测试功能点：系统角色持久化
//     *
//     * 测试场景：
//     * 1. 在 ChatClient 构建时设置默认 System Message
//     * 2. 所有后续请求自动携带该系统提示
//     * 3. 适合固定角色场景（如客服机器人、专家系统）
//     *
//     * 配置方式：
//     * ChatClient.builder(chatModel)
//     *     .defaultSystem("你是一位专业的Java技术顾问")
//     *     .build();
//     *
//    @Test
//    void test_default_system_message() {
//        // 待实现
//    }
//    */
//
//    /*
//    /**
//     * 测试功能点：异常处理
//     *
//     * 测试场景：
//     * 1. 测试无效的 API Key
//     * 2. 测试网络超时
//     * 3. 测试超出 token 限制
//     * 4. 验证异常信息的可读性
//     *
//    @Test
//    void test_exception_handling() {
//        // 待实现
//    }
//    */
//
//    /*
//    /**
//     * 测试功能点：响应元数据
//     *
//     * 测试场景：
//     * 1. 获取响应中的元数据（token 使用量、模型信息等）
//     * 2. 用于监控成本和性能
//     *
//     * 示例：
//     * ChatResponse response = chatClient.prompt()
//     *     .user("你好")
//     *     .call()
//     *     .chatResponse();
//     * log.info("Token使用：{}", response.getMetadata().getUsage());
//     *
//    @Test
//    void test_response_metadata() {
//        // 待实现
//    }
//    */
//}
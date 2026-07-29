//package com.ai.study;
//
//import com.ai.study.model.CountryInfo;
//import lombok.extern.slf4j.Slf4j;
//import org.junit.jupiter.api.Test;
//import org.springframework.ai.chat.client.ChatClient;
//import org.springframework.ai.chat.messages.UserMessage;
//import org.springframework.ai.chat.prompt.PromptTemplate;
//import org.springframework.ai.openai.OpenAiChatOptions;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@SpringBootTest
//@Slf4j
//public class ChatClientParameterizedTest {
//
//    @Autowired
//    private ChatClient chatClient;
//
//    @Autowired
//    private ChatClient.Builder chatClientBuilder;
//
//    /**
//     * 测试功能点：使用 PromptTemplate 实现参数化
//     *
//     * 场景：使用 PromptTemplate 的 render 方法替换占位符
//     */
//    @Test
//    void test_parameterized_with_prompt_template() {
//        // 方式1：使用 PromptTemplate
//        String template = "介绍下{country}的地理面积和{population}";
//        Map<String, Object> params = new HashMap<>();
//        params.put("country", "中国");
//        params.put("population", "人口数量");
//
//        PromptTemplate promptTemplate = new PromptTemplate(template);
//        String renderedPrompt = promptTemplate.render(params);
//
//        String response = chatClient.prompt()
//                .user(renderedPrompt)
//                .call()
//                .content();
//
//        log.info("PromptTemplate 参数测试：\n{}", response);
//    }
//
//    /**
//     * 测试功能点：使用 System 和 User 消息 + 字符串格式化
//     *
//     * 场景：使用 String.format 或直接字符串拼接
//     */
//    @Test
//    void test_parameterized_with_format() {
//        String country = "中国";
//        String topic = "地理面积";
//        String detail = "包括陆地面积、海域面积和边境线长度";
//
//        // 方式2：使用 String.format
//        String userMessage = String.format("请详细介绍%s的%s，%s", country, topic, detail);
//
//        String response = chatClient.prompt()
//                .system("你是一个专业的地理专家")
//                .user(userMessage)
//                .call()
//                .content();
//
//        log.info("格式化参数测试：\n{}", response);
//    }
//
//    /**
//     * 测试功能点：使用 StringBuilder 动态构建
//     */
//    @Test
//    void test_parameterized_with_stringbuilder() {
//        Map<String, String> params = new HashMap<>();
//        params.put("role", "地理专家");
//        params.put("country", "中国");
//        params.put("aspect", "地理特征");
//
//        StringBuilder systemMsg = new StringBuilder("你是一个专业的")
//                .append(params.get("role"))
//                .append("，擅长回答关于")
//                .append(params.get("country"))
//                .append("的各种问题");
//
//        StringBuilder userMsg = new StringBuilder("请详细介绍")
//                .append(params.get("country"))
//                .append("的")
//                .append(params.get("aspect"))
//                .append("，包括地形、气候、水系等方面");
//
//        String response = chatClient.prompt()
//                .system(systemMsg.toString())
//                .user(userMsg.toString())
//                .call()
//                .content();
//
//        log.info("StringBuilder 参数测试：\n{}", response);
//    }
//
//    /**
//     * 测试功能点：批量参数化测试
//     */
//    @Test
//    void test_batch_parameterized() {
//        // 准备多组参数
//        Object[][] testCases = {
//                {"中国", "四大发明"},
//                {"美国", "科技发展"},
//                {"日本", "动漫文化"},
//                {"法国", "美食文化"}
//        };
//
//        for (Object[] testCase : testCases) {
//            String country = (String) testCase[0];
//            String aspect = (String) testCase[1];
//
//            String userMessage = String.format("请介绍%s的%s", country, aspect);
//
//            String response = chatClient.prompt()
//                    .user(userMessage)
//                    .call()
//                    .content();
//
//            log.info("【{}的{}】\n{}\n", country, aspect, response);
//        }
//    }
//
//    /**
//     * 测试功能点：使用对象转字符串
//     */
//    @Test
//    void test_parameterized_with_object() {
//        CountryInfo china = new CountryInfo("中国", "960万平方公里", "14.1亿", "56个民族");
//
//        String userMessage = String.format(
//                "请介绍%s的基本信息：\n面积：%s\n人口：%s\n民族：%s",
//                china.getName(),
//                china.getArea(),
//                china.getPopulation(),
//                china.getEthnicGroups()
//        );
//
//        String response = chatClient.prompt()
//                .system("你是一个专业的地理专家")
//                .user(userMessage)
//                .call()
//                .content();
//
//        log.info("对象参数测试：\n{}", response);
//    }
//
//    /**
//     * 测试功能点：动态构建 Prompt
//     */
//    @Test
//    void test_dynamic_parameters() {
//        String country = "中国";
//        boolean includeHistory = true;
//        boolean includeCulture = false;
//
//        StringBuilder userMessage = new StringBuilder("请介绍").append(country);
//
//        if (includeHistory) {
//            userMessage.append("的历史");
//        }
//
//        if (includeCulture) {
//            userMessage.append("和文化");
//        }
//
//        userMessage.append("的基本情况");
//
//        String response = chatClient.prompt()
//                .user(userMessage.toString())
//                .call()
//                .content();
//
//        log.info("动态参数测试：\n{}", response);
//    }
//
//    /**
//     * 测试功能点：参数化 + 配置选项结合
//     */
//    @Test
//    void test_parameterized_with_options() {
//        String language = "英文";
//        String country = "China";
//        String topic = "春节";
//
//        String userMessage = String.format("用%s写一篇关于%s的%s的短文", language, country, topic);
//
//        String response = chatClient.prompt()
//                .user(userMessage)
//                .options(
//                        OpenAiChatOptions.builder()
//                                .temperature(0.8)
//                                .maxTokens(1024)
//                                .build()
//                )
//                .call()
//                .content();
//
//        log.info("参数+配置测试：\n{}", response);
//    }
//
//    /**
//     * 测试功能点：使用 Message 对象
//     */
//    @Test
//    void test_parameterized_with_message() {
//        String country = "中国";
//        String aspect = "地理特征";
//
//        String content = String.format("请详细介绍%s的%s", country, aspect);
//        UserMessage userMessage = new UserMessage(content);
//
//        String response = chatClient.prompt()
//                .user(userMessage.getText())
//                .call()
//                .content();
//
//        log.info("Message 对象参数测试：\n{}", response);
//    }
//
//    /**
//     * 测试功能点：使用模板方法模式
//     */
//    @Test
//    void test_parameterized_with_template_method() {
//        String response = askQuestion("中国", "地理面积");
//        log.info("模板方法测试：\n{}", response);
//    }
//
//    /**
//     * 封装提问方法
//     */
//    private String askQuestion(String country, String topic) {
//        String userMessage = String.format("请详细介绍一下%s的%s", country, topic);
//        return chatClient.prompt()
//                .system("你是一个专业的地理专家，回答要详细准确")
//                .user(userMessage)
//                .call()
//                .content();
//    }
//}
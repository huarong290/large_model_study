package com.ai.study;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
public class QuickStartTest {
    @Autowired
    private ChatModel chatModel;
    @Test
    void test_0001(){
        log.info("---{}",chatModel.call("您好啊,能介绍下自己吗"));
    }

    /**
     * temperature
     * 较低值(0.0-0.3)：响应更确定、更集中。更适合事实类问题、分类问题或一致性至关重要的任务
     * 中等值(0.0-0.3)：在确定性和创造性之间取得平衡。适合于一般用例
     * 值越高(0.0-0.3)：回复更具创意、多样性，且可能带来惊喜。更适合创意写作，头脑风暴或剩菜多样化的选项
     */
    @Test
    void test_0002(){
        log.info("---{}",chatModel.call("讲一个关于猫的故事？"));
    }

    /**
     * MaxTokens:该maxToken参数限制了模型在响应中可以生成的标记的数量
     * 低值(5-25)：适用于单个单词、短语或分类标签
     * 中等值(1000)：用于段落或简短解释
     * 中等值(1000)：适用于长篇内容、故事或复杂的解释
     */
    @Test
    void test_0003(){
        log.info("---{}",chatModel.call("讲一个关于猫的故事？"));
    }
}

package com.ai.study;

import com.ai.study.service.RagService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
@Slf4j
public class RagServiceTest {

    @Autowired
    private RagService ragService;

    // ========================================== //
    // 测试用例 1：具体意象查询                   //
    // ========================================== //
    @Test
    void testQueryBySpecificImage() {
        String question = "梅花代表什么含义？";
        String result = ragService.answer(question);
        log.info("问题: {}", question);
        log.info("回答: {}", result);
        // 预期：文档中"植物类"的"梅：高洁、坚强、孤傲"
    }

    // ========================================== //
    // 测试用例 2：多个意象查询                   //
    // ========================================== //
    @Test
    void testQueryByMultiImages() {
        String question = "柳和月分别代表什么？";
        String result = ragService.answer(question);
        log.info("问题: {}", question);
        log.info("回答: {}", result);
        // 预期：找到"柳"和"月"的条目
    }

    // ========================================== //
    // 测试用例 3：组合意象查询                   //
    // ========================================== //
    @Test
    void testQueryByCombination() {
        String question = "月+酒组合代表什么意境？";
        String result = ragService.answer(question);
        log.info("问题: {}", question);
        log.info("回答: {}", result);
        // 预期：找到"常用意象组合"中的条目
    }

    // ========================================== //
    // 测试用例 4：非精确匹配查询                 //
    // ========================================== //
    @Test
    void testQueryByVagueMatch() {
        String question = "古代诗人常用哪些意象表达离别？";
        String result = ragService.answer(question);
        log.info("问题: {}", question);
        log.info("回答: {}", result);
        // 预期：返回相关的描述
    }

    // ========================================== //
    // 测试用例 5：知识库不存在的内容              //
    // ========================================== //
    @Test
    void testQueryUnknownContent() {
        String question = "香蕉代表什么含义？";
        String result = ragService.answer(question);
        log.info("问题: {}", question);
        log.info("回答: {}", result);
        // 预期：基于知识库内容回答，可能没有直接答案
    }

    // ========================================== //
    // 测试用例 6：测试 Top2 逻辑是否正确         //
    // ========================================== //
    @Test
    void testTop2Logic() {
        // 这个方法测试我们的 Top2 查找算法是否正确工作
        log.info("========== 测试 Top2 逻辑 ==========");
        // 会在日志中打印 Top1 和 Top2 的索引和相似度
        ragService.answer("梅花代表什么含义");
        // 观察日志中的 Top1: {}, 相似度: {} 和 Top2: {}, 相似度: {}
    }

    // ========================================== //
    // 测试用例 7：空查询处理                    //
    // ========================================== //
    @Test
    void testEmptyQuestion() {
        try {
            String result = ragService.answer("");
            log.info("空查询回答: {}", result);
        } catch (Exception e) {
            log.error("空查询异常: {}", e.getMessage());
        }
    }
}
package com.ai.study;

import com.ai.study.service.OpenAiAssistant;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.TimeUnit;

@SpringBootTest
@Slf4j
public class StoreTest {

    @Autowired
    private OpenAiAssistant openAiAssistant;
    @Test
    void test01() throws InterruptedException {

        String answer1 = openAiAssistant.ask("user_000001","我是桑椹果实");
        log.info("answer1={}",answer1);
        String answer2 = openAiAssistant.ask("user_000001","你知道我是谁吗");
        log.info("answer2={}",answer2);
        String answer3 = openAiAssistant.ask("user_000003","你知道我是谁吗");
        log.info("answer3={}",answer3);
        log.info("test01========");
        TimeUnit.MINUTES.sleep(1);
    }
}

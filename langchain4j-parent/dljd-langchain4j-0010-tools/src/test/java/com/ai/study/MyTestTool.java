package com.ai.study;

import com.ai.study.dto.UserInfoDTO;
import com.ai.study.servcie.OpenAiAssistant;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
public class MyTestTool {

    @Autowired
    private OpenAiAssistant openAiAssistant;
    @Test
    void test01(){

        String answer1 = openAiAssistant.ask("求100和200的和");
        log.info("test01 中answer1={}",answer1);
    }

    @Test
    void test02(){

        String answer2 = openAiAssistant.ask("计算4和5的乘积");
        log.info("test02 中answer2={}",answer2);
    }

    @Test
    void test03(){

        String answer3 = openAiAssistant.chat("user111","查询订单号是A100订单信息");
        log.info("test03 answer3={}",answer3);
    }


    @Test
    void test04(){

        UserInfoDTO userInfoDTO = openAiAssistant.query("我想了解李白诗人的信息");
        log.info("test04 userInfoDTO={}",userInfoDTO);
    }
}

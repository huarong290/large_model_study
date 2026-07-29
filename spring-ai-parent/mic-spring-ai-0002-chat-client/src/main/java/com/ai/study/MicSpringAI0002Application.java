package com.ai.study;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
/**
 * ChatClient 基础使用模块启动类
 *
 * 功能说明：
 * 1. 演示 ChatClient 的核心 API：prompt() / call() / content()
 * 2. 演示 System 和 User 消息的使用
 * 3. 演示同步调用
 *
 * @author AI Study
 */
@SpringBootApplication
public class MicSpringAI0002Application {

    public static void main(String[] args){
        SpringApplication.run(MicSpringAI0002Application.class,args);
    }
}
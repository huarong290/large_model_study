package com.ai.study;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


/**
 * Spring AI 基础配置模块启动类
 * 功能说明：
 * 1. 演示多种 AI 模型的配置方式
 * 2. 默认使用 Ollama 本地模型
 * 3. 支持 DeepSeek 和 Google Gemini（通过 OpenAI 兼容接口）
 * 支持的模型：
 * - Ollama（本地，默认）
 * - DeepSeek（云端）
 * - Google Gemini（云端，通过 AI Studio API Key）
 *
 * @author AI Study
 */
@SpringBootApplication
public class MicSpringAI0001Application {

    public static void main(String[] args){
        SpringApplication.run(MicSpringAI0001Application.class,args);
    }
}
package com.ai.study.service;


public interface Assistant {
    /**
     * LLM 交互方法
     *
     * @param question 参数用户的提问
     * @retur LLM的输出返回
     */
    String ask(String question);

    //定义其他方法
}

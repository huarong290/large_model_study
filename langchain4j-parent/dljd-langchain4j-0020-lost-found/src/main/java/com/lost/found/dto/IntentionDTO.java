package com.lost.found.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import dev.langchain4j.model.output.structured.Description;
import lombok.Data;

/**
 * 存储失物管理用户问题意图分析信息
 */
@Data
@Description("存储失物管理用户问题意图分析信息")
@JsonIgnoreProperties(ignoreUnknown = true)
public class IntentionDTO {
    /**
     * 用户输入问题：LLM提供信息转为intention
     */
    @Description("意图分析：1.失物登记 2.招领登记(找到失物登记) 3.失物查询 4.其他")
    private Integer intention;
    /**
     * 大模型对用户的输出内容
     */
    @Description("大模型对用户的输出内容")
    private String response;
    /**
     * 失物人联系手机号
     */
    @Description("失物人联系手机号")
    private String phone;
}

package com.lost.found.dto;

import dev.langchain4j.model.output.structured.Description;
import lombok.Data;

/**
 *
 */
@Data
public class MatchResultDTO {
    @Description("失物人联系手机号")
    private String phone;
    @Description("失物名称")
    private String lostName;
    @Description("失物和招领物品匹配分数0-1之间，靠近是匹配，0是不匹配")
    private Double matchScore;
    @Description("失物和招领物品匹配度原因")
    private Double matchReason;
    @Description("大模型返回输出内容")
    private String response;
}

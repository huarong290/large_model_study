package com.lost.found.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import dev.langchain4j.model.output.structured.Description;
import lombok.Data;

/**
 * 失物登记
 */
@Data
@Description("存储失登记信息")
@JsonIgnoreProperties(ignoreUnknown = true)
public class LostRegisterDTO {

    /**
     *
     */
    @Description("大模型对用户的输出")
    private String response;

    @Description("用户姓名")
    private String userName;

    @Description("用户手机号")
    private String phone;

    @Description("失物名称，比如华为耳机")
    private String lostName;

    @Description("丢失时间，比如1月5日上午10点30分左右")
    private String lostDateTime;

    @Description("丢失地点，比如10号楼2层")
    private String lostLocation;

    @Description("失物特征，比如黑色，皮面，手掌大小")
    private String lostDescription;

    @Description("是否完成登记")
    private Boolean completeFlag;

    @Description("数据库主键ID")
    private Long id ;
}

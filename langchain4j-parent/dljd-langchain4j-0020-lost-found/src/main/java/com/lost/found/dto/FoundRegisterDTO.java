package com.lost.found.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import dev.langchain4j.model.output.structured.Description;
import lombok.Data;

/**
 * 招领登记
 */
@Data
@Description("招领登记信息")
@JsonIgnoreProperties(ignoreUnknown = true)
public class FoundRegisterDTO {
    /**
     *
     */
    @Description("大模型对用户的输出")
    private String response;

    /**
     * 拾得人姓名
     */
    @Description("拾得人姓名")
    private String finderName;

    /**
     * 拾得人手机号
     */
    @Description("拾得人手机号")
    private String finderPhone;

    /**
     * 拾得物品名称
     */
    @Description("失物名称")
    private String findName;

    /**
     * 拾得时间
     */
    @Description("拾得时间")
    private String findDateTime;

    /**
     * 拾得地址
     */
    @Description("拾得地址")
    private String findLocation;

    /**
     * 物品特征描述
     */
    @Description("物品特征描述")
    private String findDescription;

    @Description("是否完成登记")
    private Boolean completeFlag;

    @Description("数据库主键ID")
    private Long id ;
}

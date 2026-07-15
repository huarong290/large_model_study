package com.ai.study.dto;

import dev.langchain4j.model.output.structured.Description;
import lombok.Data;

@Data
@Description("用户信息类，存储用户名称、职业信息")
public class UserInfoDTO {
    @Description("用户姓名")
    private String name;
    @Description("用户职业，工作或特长")
    private String profession;
    @Description("对此用户的评价")
    private String evaluate;
}

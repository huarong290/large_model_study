package com.ao.study.pojo;

import dev.langchain4j.model.output.structured.Description;
import lombok.Data;

@Data
@Description("这个是一个介绍商品的对象")
public class ProductInfo {

    @Description("从1到10分，商品评分，1最低，10最高")
    private String rate;

    @Description("商品的评价说明")
    private String evaluation;
}

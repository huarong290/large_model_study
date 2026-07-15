package com.lost.found.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 招领登记表
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("found_item")
public class FoundItem implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 拾得人姓名
     */
    @TableField("finder_name")
    private String finderName;

    /**
     * 拾得人手机号
     */
    @TableField("finder_phone")
    private String finderPhone;

    /**
     * 失物名称
     */
    @TableField("find_name")
    private String findName;

    /**
     * 丢失时间
     */
    @TableField("find_date_time")
    private String findDateTime;

    /**
     * 丢失地址
     */
    @TableField("find_location")
    private String findLocation;

    /**
     * 物品特征
     */
    @TableField("find_description")
    private String findDescription;

    /**
     * 状态：0-登记 1-已找到 2-已归还
     */
    @TableField("status")
    private Integer status;

    /**
     * 删除标识
     */
    @TableField("delete_flag")
    @TableLogic
    private Long deleteFlag;

    /**
     * 创建人
     */
    @TableField("create_by")
    private String createBy;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新人
     */
    @TableField("update_by")
    private String updateBy;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
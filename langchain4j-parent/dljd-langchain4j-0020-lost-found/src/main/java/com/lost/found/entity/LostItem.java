package com.lost.found.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 失物登记表
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("lost_item")
public class LostItem implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 失主姓名
     */
    @TableField("user_name")
    private String userName;

    /**
     * 失主手机号
     */
    @TableField("phone")
    private String phone;

    /**
     * 失物名称
     */
    @TableField("lost_name")
    private String lostName;

    /**
     * 丢失时间
     */
    @TableField("lost_date_time")
    private String lostDateTime;

    /**
     * 丢失地址
     */
    @TableField("lost_location")
    private String lostLocation;

    /**
     * 失物特征
     */
    @TableField("lost_description")
    private String lostDescription;

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
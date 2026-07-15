package com.lost.found.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 对话历史表
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("chat_his_msg")
public class ChatHisMsg implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 会话ID
     */
    @TableField("chat_id")
    private String chatId;

    /**
     * 消息内容
     */
    @TableField("message")
    private String message;

    /**
     * User-用户消息 AI-LLM 返回消息
     */
    @TableField("role_type")
    private String roleType;

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
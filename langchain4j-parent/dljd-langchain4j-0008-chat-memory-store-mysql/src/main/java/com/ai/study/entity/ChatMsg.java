package com.ai.study.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("chat_msg")
public class ChatMsg {

    /**
     * 主键ID（自增）
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 会话ID
     */
    @TableField("chat_id")
    private String chatId;

    /**
     * 消息类型：USER / AI
     */
    @TableField("message_type")
    private String messageType;

    /**
     * 消息内容
     */
    @TableField("content")
    private String content;

    /**
     * 删除标识：0-未删除，1-已删除
     */
    @TableField("delete_flag")
    @TableLogic
    private Integer deleteFlag;

    /**
     * 创建人
     */
    @TableField("create_by")
    private String createBy;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", insertStrategy = FieldStrategy.NEVER)
    private LocalDateTime createTime;

    /**
     * 更新人
     */
    @TableField("update_by")
    private String updateBy;

    /**
     * 更新时间（自动填充）
     */
// 插入和更新时都忽略，由数据库自动维护
    @TableField(value = "update_time", insertStrategy = FieldStrategy.NEVER, updateStrategy = FieldStrategy.NEVER)
    private LocalDateTime updateTime;
}
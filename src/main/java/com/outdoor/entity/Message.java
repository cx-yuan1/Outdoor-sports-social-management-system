package com.outdoor.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 消息通知实体类
 */
@Data
@TableName("message")
public class Message implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 消息ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 接收用户ID */
    private Long userId;

    /** 发送者ID，系统消息为空 */
    private Long senderId;

    /** 类型：1-系统通知，2-活动通知，3-互动消息，4-私信 */
    private Integer type;

    /** 标题 */
    private String title;

    /** 内容 */
    private String content;

    /** 关联目标ID */
    private Long targetId;

    /** 目标类型：activity/moment/comment */
    private String targetType;

    /** 是否已读：0-未读，1-已读 */
    private Integer isRead;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    /** 发送者昵称（非数据库字段） */
    @TableField(exist = false)
    private String senderName;

    /** 发送者头像（非数据库字段） */
    @TableField(exist = false)
    private String senderAvatar;
}

package com.outdoor.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 评论实体类
 */
@Data
@TableName("comment")
public class Comment implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 评论ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 动态ID */
    private Long momentId;

    /** 评论用户ID */
    private Long userId;

    /** 评论内容 */
    private String content;

    /** 父评论ID，0表示一级评论 */
    private Long parentId;

    /** 回复用户ID */
    private Long replyUserId;

    /** 状态：0-隐藏，1-正常 */
    private Integer status;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    /** 是否删除 */
    @TableLogic
    private Integer deleted;

    /** 用户昵称（非数据库字段） */
    @TableField(exist = false)
    private String userName;

    /** 用户头像（非数据库字段） */
    @TableField(exist = false)
    private String userAvatar;

    /** 回复用户昵称（非数据库字段） */
    @TableField(exist = false)
    private String replyUserName;

    /** 子评论列表（非数据库字段） */
    @TableField(exist = false)
    private List<Comment> children;
}

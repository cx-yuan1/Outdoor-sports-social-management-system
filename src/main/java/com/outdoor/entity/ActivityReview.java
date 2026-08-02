package com.outdoor.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 活动评价实体类
 */
@Data
@TableName("activity_review")
public class ActivityReview implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 评价ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 活动ID */
    private Long activityId;

    /** 用户ID */
    private Long userId;

    /** 评分：1-5 */
    private Integer rating;

    /** 评价内容 */
    private String content;

    /** 评价图片 */
    private String images;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    /** 用户昵称（非数据库字段） */
    @TableField(exist = false)
    private String userName;

    /** 用户头像（非数据库字段） */
    @TableField(exist = false)
    private String userAvatar;

    /** 活动标题（非数据库字段） */
    @TableField(exist = false)
    private String activityTitle;
}

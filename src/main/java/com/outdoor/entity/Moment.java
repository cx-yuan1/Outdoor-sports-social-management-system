package com.outdoor.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 动态实体类
 */
@Data
@TableName("moment")
public class Moment implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 动态ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 用户ID */
    private Long userId;

    /** 动态内容 */
    private String content;

    /** 图片URL，多个用逗号分隔 */
    private String images;

    /** 位置 */
    private String location;

    /** 关联活动ID */
    private Long activityId;

    /** 点赞数 */
    private Integer likeCount;

    /** 评论数 */
    private Integer commentCount;

    /** 状态：0-待审核，1-已通过，2-已拒绝 */
    private Integer status;

    /** 审核备注 */
    private String auditRemark;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    /** 是否删除：0-未删除，1-已删除 */
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private Integer deleted = 0;

    /** 用户昵称（非数据库字段） */
    @TableField(exist = false)
    private String userName;

    /** 用户头像（非数据库字段） */
    @TableField(exist = false)
    private String userAvatar;

    /** 图片列表（非数据库字段） */
    @TableField(exist = false)
    private List<String> imageList;

    /** 是否已点赞（非数据库字段） */
    @TableField(exist = false)
    private Boolean isLiked;

    /** 关联活动标题（非数据库字段） */
    @TableField(exist = false)
    private String activityTitle;
}

package com.outdoor.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 活动报名实体类
 */
@Data
@TableName("activity_signup")
public class ActivitySignup implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 报名ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 活动ID */
    private Long activityId;

    /** 用户ID */
    private Long userId;

    /** 报名备注 */
    private String remark;

    /** 状态：0-待审核，1-已通过，2-已拒绝，3-已取消 */
    private Integer status;

    /** 审核备注 */
    private String auditRemark;

    /** 报名时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;

    /** 用户昵称（非数据库字段） */
    @TableField(exist = false)
    private String userNickname;

    /** 用户头像（非数据库字段） */
    @TableField(exist = false)
    private String userAvatar;

    /** 用户手机（非数据库字段） */
    @TableField(exist = false)
    private String contactPhone;

    /** 活动标题（非数据库字段） */
    @TableField(exist = false)
    private String activityTitle;
}

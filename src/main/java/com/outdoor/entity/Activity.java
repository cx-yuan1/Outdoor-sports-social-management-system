package com.outdoor.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 活动实体类
 */
@Data
@TableName("activity")
public class Activity implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 活动ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 活动标题 */
    private String title;

    /** 封面图片 */
    private String coverImage;

    /** 分类ID */
    private Long categoryId;

    /** 发起者ID */
    private Long organizerId;

    /** 活动描述 */
    private String description;

    /** 活动地点 */
    private String location;

    /** 开始时间 */
    private LocalDateTime startTime;

    /** 结束时间 */
    private LocalDateTime endTime;

    /** 报名截止时间 */
    private LocalDateTime signupDeadline;

    /** 最大参与人数，0表示不限 */
    private Integer maxParticipants;

    /** 当前报名人数 */
    private Integer currentParticipants;

    /** 费用 */
    private BigDecimal fee;

    /** 联系电话 */
    private String contactPhone;

    /** 参与要求 */
    private String requirements;

    /** 状态：0-待审核，1-已通过，2-已拒绝，3-进行中，4-已结束，5-已取消 */
    private Integer status;

    /** 审核备注 */
    private String auditRemark;

    /** 浏览次数 */
    private Integer viewCount;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;

    /** 是否删除 */
    @TableLogic
    private Integer deleted;

    /** 分类名称（非数据库字段） */
    @TableField(exist = false)
    private String categoryName;

    /** 发起者昵称（非数据库字段） */
    @TableField(exist = false)
    private String organizerName;

    /** 发起者头像（非数据库字段） */
    @TableField(exist = false)
    private String organizerAvatar;

    /** 是否已报名（非数据库字段） */
    @TableField(exist = false)
    private Boolean isSignedUp;

    /** 报名状态（非数据库字段） */
    @TableField(exist = false)
    private Integer signupStatus;
}

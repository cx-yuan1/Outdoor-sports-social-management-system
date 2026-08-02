package com.outdoor.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 活动分类实体类
 */
@Data
@TableName("activity_category")
public class ActivityCategory implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 分类ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 分类名称 */
    private String name;

    /** 分类图标 */
    private String icon;

    /** 排序 */
    private Integer sort;

    /** 状态：0-禁用，1-正常 */
    private Integer status;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    /** 是否删除 */
    @TableLogic
    private Integer deleted;

    /** 活动数量（非数据库字段） */
    @TableField(exist = false)
    private Integer activityCount;
}

package com.outdoor.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 关注实体类
 */
@Data
@TableName("follow")
public class Follow implements Serializable {

    private static final long serialVersionUID = 1L;

    /** ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 用户ID */
    private Long userId;

    /** 被关注用户ID */
    private Long followUserId;

    /** 关注时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;
}

package com.outdoor.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 系统公告实体类
 */
@Data
@TableName("notice")
public class Notice implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 公告ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 标题 */
    private String title;

    /** 内容 */
    private String content;

    /** 类型：1-普通公告，2-重要公告 */
    private Integer type;

    /** 状态：0-下架，1-发布 */
    private Integer status;

    /** 发布者ID */
    private Long publisherId;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    /** 是否删除 */
    @TableLogic
    private Integer deleted;

    /** 发布者昵称（非数据库字段） */
    @TableField(exist = false)
    private String publisherName;
}

package com.outdoor.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.outdoor.entity.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 评论Mapper接口
 */
@Mapper
public interface CommentMapper extends BaseMapper<Comment> {

    /**
     * 查询动态的评论列表（带用户信息）
     * @param momentId 动态ID
     * @return 评论列表
     */
    List<Comment> selectCommentList(@Param("momentId") Long momentId);
}

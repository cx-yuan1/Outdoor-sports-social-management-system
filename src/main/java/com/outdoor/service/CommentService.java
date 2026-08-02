package com.outdoor.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.outdoor.entity.Comment;

import java.util.List;

/**
 * 评论服务接口
 */
public interface CommentService extends IService<Comment> {

    /**
     * 发表评论
     * @param comment 评论信息
     * @return 是否成功
     */
    boolean publish(Comment comment);

    /**
     * 获取动态的评论列表（树形结构）
     * @param momentId 动态ID
     * @return 评论列表
     */
    List<Comment> listByMoment(Long momentId);

    /**
     * 删除评论
     * @param id 评论ID
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean deleteComment(Long id, Long userId);
}

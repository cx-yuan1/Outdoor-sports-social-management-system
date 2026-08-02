package com.outdoor.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.outdoor.entity.Comment;
import com.outdoor.entity.Moment;
import com.outdoor.mapper.CommentMapper;
import com.outdoor.mapper.MomentMapper;
import com.outdoor.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 评论服务实现类
 */
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    @Autowired
    private MomentMapper momentMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean publish(Comment comment) {
        comment.setStatus(1);
        boolean result = save(comment);
        if (result) {
            // 增加动态评论数
            Moment moment = momentMapper.selectById(comment.getMomentId());
            if (moment != null) {
                moment.setCommentCount(moment.getCommentCount() + 1);
                momentMapper.updateById(moment);
            }
        }
        return result;
    }

    @Override
    public List<Comment> listByMoment(Long momentId) {
        List<Comment> allComments = baseMapper.selectCommentList(momentId);
        
        // 构建树形结构
        Map<Long, List<Comment>> childrenMap = allComments.stream()
                .filter(c -> c.getParentId() != null && c.getParentId() > 0)
                .collect(Collectors.groupingBy(Comment::getParentId));
        
        List<Comment> rootComments = new ArrayList<>();
        for (Comment comment : allComments) {
            if (comment.getParentId() == null || comment.getParentId() == 0) {
                comment.setChildren(childrenMap.getOrDefault(comment.getId(), new ArrayList<>()));
                rootComments.add(comment);
            }
        }
        return rootComments;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteComment(Long id, Long userId) {
        Comment comment = getById(id);
        if (comment == null || !comment.getUserId().equals(userId)) {
            return false;
        }
        boolean result = removeById(id);
        if (result) {
            // 减少动态评论数
            Moment moment = momentMapper.selectById(comment.getMomentId());
            if (moment != null && moment.getCommentCount() > 0) {
                moment.setCommentCount(moment.getCommentCount() - 1);
                momentMapper.updateById(moment);
            }
        }
        return result;
    }
}

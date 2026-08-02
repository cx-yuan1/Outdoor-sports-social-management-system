package com.outdoor.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.outdoor.common.Constants;
import com.outdoor.entity.LikeRecord;
import com.outdoor.entity.Moment;
import com.outdoor.mapper.LikeRecordMapper;
import com.outdoor.mapper.MomentMapper;
import com.outdoor.service.MomentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

/**
 * 动态服务实现类
 */
@Service
public class MomentServiceImpl extends ServiceImpl<MomentMapper, Moment> implements MomentService {

    @Autowired
    private LikeRecordMapper likeRecordMapper;

    @Override
    public boolean publish(Moment moment) {
        moment.setStatus(Constants.MomentStatus.PENDING);
        moment.setLikeCount(0);
        moment.setCommentCount(0);
        return save(moment);
    }

    @Override
    public IPage<Moment> pageList(Integer page, Integer size, Long userId, Integer status, Long currentUserId) {
        Page<Moment> pageParam = new Page<>(page, size);
        IPage<Moment> result = baseMapper.selectMomentPage(pageParam, userId, status);
        
        // 处理图片列表和点赞状态
        for (Moment moment : result.getRecords()) {
            processImages(moment);
            if (currentUserId != null) {
                LikeRecord like = likeRecordMapper.selectByUserAndTarget(currentUserId, moment.getId(), Constants.LikeTargetType.MOMENT);
                moment.setIsLiked(like != null);
            } else {
                moment.setIsLiked(false);
            }
        }
        return result;
    }

    @Override
    public IPage<Moment> pageFollowMoments(Integer page, Integer size, Long userId) {
        Page<Moment> pageParam = new Page<>(page, size);
        IPage<Moment> result = baseMapper.selectFollowMomentPage(pageParam, userId);
        
        for (Moment moment : result.getRecords()) {
            processImages(moment);
            LikeRecord like = likeRecordMapper.selectByUserAndTarget(userId, moment.getId(), Constants.LikeTargetType.MOMENT);
            moment.setIsLiked(like != null);
        }
        return result;
    }

    @Override
    public boolean audit(Long id, Integer status, String remark) {
        Moment moment = new Moment();
        moment.setId(id);
        moment.setStatus(status);
        moment.setAuditRemark(remark);
        return updateById(moment);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean toggleLike(Long momentId, Long userId) {
        LikeRecord existLike = likeRecordMapper.selectByUserAndTarget(userId, momentId, Constants.LikeTargetType.MOMENT);
        
        if (existLike != null) {
            // 取消点赞
            likeRecordMapper.deleteById(existLike.getId());
            // 减少点赞数
            Moment moment = getById(momentId);
            if (moment != null && moment.getLikeCount() > 0) {
                moment.setLikeCount(moment.getLikeCount() - 1);
                updateById(moment);
            }
            return false;
        } else {
            // 点赞
            LikeRecord like = new LikeRecord();
            like.setUserId(userId);
            like.setTargetId(momentId);
            like.setTargetType(Constants.LikeTargetType.MOMENT);
            likeRecordMapper.insert(like);
            // 增加点赞数
            Moment moment = getById(momentId);
            if (moment != null) {
                moment.setLikeCount(moment.getLikeCount() + 1);
                updateById(moment);
            }
            return true;
        }
    }

    @Override
    public int countByUser(Long userId) {
        LambdaQueryWrapper<Moment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Moment::getUserId, userId)
               .eq(Moment::getStatus, Constants.MomentStatus.APPROVED);
        return Math.toIntExact(count(wrapper));
    }

    /**
     * 处理图片字符串转列表
     */
    private void processImages(Moment moment) {
        if (StrUtil.isNotBlank(moment.getImages())) {
            List<String> imageList = Arrays.asList(moment.getImages().split(","));
            moment.setImageList(imageList);
        }
    }
}

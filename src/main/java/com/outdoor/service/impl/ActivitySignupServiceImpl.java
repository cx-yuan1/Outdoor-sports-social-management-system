package com.outdoor.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.outdoor.common.Constants;
import com.outdoor.entity.Activity;
import com.outdoor.entity.ActivitySignup;
import com.outdoor.mapper.ActivityMapper;
import com.outdoor.mapper.ActivitySignupMapper;
import com.outdoor.service.ActivitySignupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 活动报名服务实现类
 */
@Service
public class ActivitySignupServiceImpl extends ServiceImpl<ActivitySignupMapper, ActivitySignup> implements ActivitySignupService {

    @Autowired
    private ActivityMapper activityMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String signup(Long activityId, Long userId, String remark) {
        // 检查活动是否存在
        Activity activity = activityMapper.selectById(activityId);
        if (activity == null) {
            return "活动不存在";
        }
        // 检查是否是自己发起的活动
        if (activity.getOrganizerId().equals(userId)) {
            return "不能报名自己发起的活动";
        }
        // 检查活动状态
        if (activity.getStatus() != Constants.ActivityStatus.APPROVED) {
            return "活动未开放报名";
        }
        // 检查报名截止时间
        if (activity.getSignupDeadline() != null && LocalDateTime.now().isAfter(activity.getSignupDeadline())) {
            return "报名已截止";
        }
        // 检查人数限制
        if (activity.getMaxParticipants() > 0 && activity.getCurrentParticipants() >= activity.getMaxParticipants()) {
            return "报名人数已满";
        }
        // 检查是否已报名
        ActivitySignup existSignup = baseMapper.selectByActivityAndUser(activityId, userId);
        if (existSignup != null) {
            if (existSignup.getStatus() == Constants.SignupStatus.CANCELLED) {
                // 重新报名
                existSignup.setStatus(Constants.SignupStatus.PENDING);
                existSignup.setRemark(remark);
                updateById(existSignup);
                return null;
            }
            return "您已报名该活动";
        }
        // 创建报名记录
        ActivitySignup signup = new ActivitySignup();
        signup.setActivityId(activityId);
        signup.setUserId(userId);
        signup.setRemark(remark);
        signup.setStatus(Constants.SignupStatus.PENDING);
        save(signup);
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cancelSignup(Long activityId, Long userId) {
        ActivitySignup signup = baseMapper.selectByActivityAndUser(activityId, userId);
        if (signup == null) {
            return false;
        }
        // 只有待审核和已通过状态可以取消
        if (signup.getStatus() != Constants.SignupStatus.PENDING 
            && signup.getStatus() != Constants.SignupStatus.APPROVED) {
            return false;
        }
        // 如果已通过，需要减少报名人数
        if (signup.getStatus() == Constants.SignupStatus.APPROVED) {
            activityMapper.updateParticipantCount(activityId, -1);
        }
        signup.setStatus(Constants.SignupStatus.CANCELLED);
        return updateById(signup);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean audit(Long id, Integer status, String remark) {
        ActivitySignup signup = getById(id);
        if (signup == null) {
            return false;
        }
        // 如果审核通过，增加报名人数
        if (status == Constants.SignupStatus.APPROVED && signup.getStatus() == Constants.SignupStatus.PENDING) {
            activityMapper.updateParticipantCount(signup.getActivityId(), 1);
        }
        signup.setStatus(status);
        signup.setAuditRemark(remark);
        return updateById(signup);
    }

    @Override
    public IPage<ActivitySignup> pageByActivity(Integer page, Integer size, Long activityId, Integer status) {
        Page<ActivitySignup> pageParam = new Page<>(page, size);
        return baseMapper.selectSignupPage(pageParam, activityId, status);
    }

    @Override
    public IPage<ActivitySignup> pageByUser(Integer page, Integer size, Long userId, Integer status) {
        Page<ActivitySignup> pageParam = new Page<>(page, size);
        return baseMapper.selectUserSignupPage(pageParam, userId, status);
    }

    @Override
    public IPage<Activity> pageUserActivities(Integer page, Integer size, Long userId) {
        Page<Activity> pageParam = new Page<>(page, size);
        return baseMapper.selectUserActivities(pageParam, userId);
    }
}

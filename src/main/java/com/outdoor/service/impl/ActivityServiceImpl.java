package com.outdoor.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.outdoor.common.Constants;
import com.outdoor.entity.Activity;
import com.outdoor.entity.ActivitySignup;
import com.outdoor.mapper.ActivityMapper;
import com.outdoor.mapper.ActivitySignupMapper;
import com.outdoor.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 活动服务实现类
 */
@Service
public class ActivityServiceImpl extends ServiceImpl<ActivityMapper, Activity> implements ActivityService {

    @Autowired
    private ActivitySignupMapper signupMapper;

    @Override
    public IPage<Activity> pageList(Integer page, Integer size, Map<String, Object> params) {
        Page<Activity> pageParam = new Page<>(page, size);
        return baseMapper.selectActivityPage(pageParam, params);
    }

    @Override
    public Activity getDetail(Long id, Long userId) {
        Activity activity = baseMapper.selectActivityDetail(id);
        if (activity == null) {
            return null;
        }
        // 增加浏览次数
        baseMapper.incrementViewCount(id);
        
        // 查询用户是否已报名
        if (userId != null) {
            ActivitySignup signup = signupMapper.selectByActivityAndUser(id, userId);
            if (signup != null) {
                activity.setIsSignedUp(true);
                activity.setSignupStatus(signup.getStatus());
            } else {
                activity.setIsSignedUp(false);
            }
        }
        return activity;
    }

    @Override
    public boolean publish(Activity activity) {
        // 设置初始状态为待审核
        activity.setStatus(Constants.ActivityStatus.PENDING);
        activity.setCurrentParticipants(0);
        activity.setViewCount(0);
        return save(activity);
    }

    @Override
    public boolean audit(Long id, Integer status, String remark) {
        Activity activity = new Activity();
        activity.setId(id);
        activity.setStatus(status);
        activity.setAuditRemark(remark);
        return updateById(activity);
    }

    @Override
    public boolean cancel(Long id, Long organizerId) {
        Activity activity = getById(id);
        if (activity == null || !activity.getOrganizerId().equals(organizerId)) {
            return false;
        }
        // 只有待审核、已通过状态的活动可以取消
        if (activity.getStatus() != Constants.ActivityStatus.PENDING 
            && activity.getStatus() != Constants.ActivityStatus.APPROVED) {
            return false;
        }
        Activity update = new Activity();
        update.setId(id);
        update.setStatus(Constants.ActivityStatus.CANCELLED);
        return updateById(update);
    }

    @Override
    public boolean finish(Long id, Long organizerId) {
        Activity activity = getById(id);
        if (activity == null || !activity.getOrganizerId().equals(organizerId)) {
            return false;
        }
        Activity update = new Activity();
        update.setId(id);
        update.setStatus(Constants.ActivityStatus.FINISHED);
        return updateById(update);
    }

    @Override
    public long countByStatus(Integer status, Long organizerId) {
        LambdaQueryWrapper<Activity> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(Activity::getStatus, status);
        }
        if (organizerId != null) {
            wrapper.eq(Activity::getOrganizerId, organizerId);
        }
        return count(wrapper);
    }
}

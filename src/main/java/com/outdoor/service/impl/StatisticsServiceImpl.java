package com.outdoor.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.outdoor.common.Constants;
import com.outdoor.entity.*;
import com.outdoor.mapper.*;
import com.outdoor.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 统计服务实现类
 */
@Service
public class StatisticsServiceImpl implements StatisticsService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ActivityMapper activityMapper;

    @Autowired
    private ActivityCategoryMapper categoryMapper;

    @Autowired
    private MomentMapper momentMapper;

    @Override
    public Map<String, Object> getOverview() {
        Map<String, Object> result = new HashMap<>();
        
        // 用户总数
        LambdaQueryWrapper<User> userWrapper = new LambdaQueryWrapper<>();
        userWrapper.eq(User::getDeleted, 0);
        result.put("userCount", userMapper.selectCount(userWrapper));
        
        // 活动总数
        LambdaQueryWrapper<Activity> activityWrapper = new LambdaQueryWrapper<>();
        activityWrapper.eq(Activity::getDeleted, 0);
        result.put("activityCount", activityMapper.selectCount(activityWrapper));
        
        // 动态总数
        LambdaQueryWrapper<Moment> momentWrapper = new LambdaQueryWrapper<>();
        momentWrapper.eq(Moment::getDeleted, 0);
        result.put("momentCount", momentMapper.selectCount(momentWrapper));
        
        // 待审核活动数
        LambdaQueryWrapper<Activity> pendingWrapper = new LambdaQueryWrapper<>();
        pendingWrapper.eq(Activity::getStatus, Constants.ActivityStatus.PENDING)
                      .eq(Activity::getDeleted, 0);
        result.put("pendingActivityCount", activityMapper.selectCount(pendingWrapper));
        
        // 今日新增用户
        LambdaQueryWrapper<User> todayUserWrapper = new LambdaQueryWrapper<>();
        todayUserWrapper.ge(User::getCreatedTime, LocalDate.now().atStartOfDay())
                        .eq(User::getDeleted, 0);
        result.put("todayUserCount", userMapper.selectCount(todayUserWrapper));
        
        // 今日新增活动
        LambdaQueryWrapper<Activity> todayActivityWrapper = new LambdaQueryWrapper<>();
        todayActivityWrapper.ge(Activity::getCreatedTime, LocalDate.now().atStartOfDay())
                            .eq(Activity::getDeleted, 0);
        result.put("todayActivityCount", activityMapper.selectCount(todayActivityWrapper));
        
        return result;
    }

    @Override
    public List<Map<String, Object>> getUserTrend() {
        List<Map<String, Object>> result = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");
        
        // 最近7天
        for (int i = 6; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            LocalDateTime startTime = date.atStartOfDay();
            LocalDateTime endTime = date.plusDays(1).atStartOfDay();
            
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            wrapper.ge(User::getCreatedTime, startTime)
                   .lt(User::getCreatedTime, endTime)
                   .eq(User::getDeleted, 0);
            
            Map<String, Object> item = new HashMap<>();
            item.put("date", date.format(formatter));
            item.put("count", userMapper.selectCount(wrapper));
            result.add(item);
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> getActivityCategoryStats() {
        List<Map<String, Object>> result = new ArrayList<>();
        
        // 获取所有分类
        LambdaQueryWrapper<ActivityCategory> categoryWrapper = new LambdaQueryWrapper<>();
        categoryWrapper.eq(ActivityCategory::getDeleted, 0)
                       .eq(ActivityCategory::getStatus, 1);
        List<ActivityCategory> categories = categoryMapper.selectList(categoryWrapper);
        
        for (ActivityCategory category : categories) {
            LambdaQueryWrapper<Activity> activityWrapper = new LambdaQueryWrapper<>();
            activityWrapper.eq(Activity::getCategoryId, category.getId())
                           .eq(Activity::getDeleted, 0);
            long count = activityMapper.selectCount(activityWrapper);
            
            Map<String, Object> item = new HashMap<>();
            item.put("name", category.getName());
            item.put("value", count);
            result.add(item);
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> getActivityStatusStats() {
        List<Map<String, Object>> result = new ArrayList<>();
        
        String[] statusNames = {"待审核", "已通过", "已拒绝", "进行中", "已结束", "已取消"};
        
        for (int i = 0; i < statusNames.length; i++) {
            LambdaQueryWrapper<Activity> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Activity::getStatus, i)
                   .eq(Activity::getDeleted, 0);
            
            Map<String, Object> item = new HashMap<>();
            item.put("name", statusNames[i]);
            item.put("value", activityMapper.selectCount(wrapper));
            result.add(item);
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> getRecentActivities(Integer limit) {
        List<Map<String, Object>> result = new ArrayList<>();
        
        LambdaQueryWrapper<Activity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Activity::getDeleted, 0)
               .orderByDesc(Activity::getCreatedTime)
               .last("LIMIT " + limit);
        
        List<Activity> activities = activityMapper.selectList(wrapper);
        for (Activity activity : activities) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", activity.getId());
            item.put("title", activity.getTitle());
            item.put("status", activity.getStatus());
            item.put("createdTime", activity.getCreatedTime());
            result.add(item);
        }
        return result;
    }
}

package com.outdoor.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.outdoor.entity.Activity;
import com.outdoor.entity.ActivityCategory;
import com.outdoor.mapper.ActivityCategoryMapper;
import com.outdoor.mapper.ActivityMapper;
import com.outdoor.service.ActivityCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 活动分类服务实现类
 */
@Service
public class ActivityCategoryServiceImpl extends ServiceImpl<ActivityCategoryMapper, ActivityCategory> implements ActivityCategoryService {

    @Autowired
    private ActivityMapper activityMapper;

    @Override
    public List<ActivityCategory> listEnabled() {
        LambdaQueryWrapper<ActivityCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ActivityCategory::getStatus, 1)
               .orderByAsc(ActivityCategory::getSort);
        return list(wrapper);
    }

    @Override
    public String checkCanDelete(Long id) {
        // 检查是否有关联的活动
        LambdaQueryWrapper<Activity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Activity::getCategoryId, id);
        long count = activityMapper.selectCount(wrapper);
        if (count > 0) {
            return "该分类下存在" + count + "个活动，无法删除。请先将这些活动移至其他分类或删除后再试。";
        }
        return null;
    }
}

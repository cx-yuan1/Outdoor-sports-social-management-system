package com.outdoor.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.outdoor.entity.ActivityCategory;

import java.util.List;

/**
 * 活动分类服务接口
 */
public interface ActivityCategoryService extends IService<ActivityCategory> {

    /**
     * 获取所有启用的分类
     * @return 分类列表
     */
    List<ActivityCategory> listEnabled();

    /**
     * 检查分类是否可以删除
     * @param id 分类ID
     * @return 错误信息，null表示可以删除
     */
    String checkCanDelete(Long id);
}

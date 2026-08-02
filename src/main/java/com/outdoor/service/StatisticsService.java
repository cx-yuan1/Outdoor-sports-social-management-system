package com.outdoor.service;

import java.util.List;
import java.util.Map;

/**
 * 统计服务接口
 */
public interface StatisticsService {

    /**
     * 获取概览数据
     * @return 概览数据
     */
    Map<String, Object> getOverview();

    /**
     * 获取用户注册趋势（最近7天）
     * @return 趋势数据
     */
    List<Map<String, Object>> getUserTrend();

    /**
     * 获取活动分类统计
     * @return 分类统计
     */
    List<Map<String, Object>> getActivityCategoryStats();

    /**
     * 获取活动状态统计
     * @return 状态统计
     */
    List<Map<String, Object>> getActivityStatusStats();

    /**
     * 获取最近活动列表
     * @param limit 数量
     * @return 活动列表
     */
    List<Map<String, Object>> getRecentActivities(Integer limit);
}

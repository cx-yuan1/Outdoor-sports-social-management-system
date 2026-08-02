package com.outdoor.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.outdoor.entity.ActivitySignup;

/**
 * 活动报名服务接口
 */
public interface ActivitySignupService extends IService<ActivitySignup> {

    /**
     * 报名活动
     * @param activityId 活动ID
     * @param userId 用户ID
     * @param remark 报名备注
     * @return 结果信息
     */
    String signup(Long activityId, Long userId, String remark);

    /**
     * 取消报名
     * @param activityId 活动ID
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean cancelSignup(Long activityId, Long userId);

    /**
     * 审核报名
     * @param id 报名ID
     * @param status 审核状态
     * @param remark 审核备注
     * @return 是否成功
     */
    boolean audit(Long id, Integer status, String remark);

    /**
     * 分页查询活动的报名列表
     * @param page 页码
     * @param size 每页数量
     * @param activityId 活动ID
     * @param status 状态
     * @return 报名列表
     */
    IPage<ActivitySignup> pageByActivity(Integer page, Integer size, Long activityId, Integer status);

    /**
     * 分页查询用户的报名记录
     * @param page 页码
     * @param size 每页数量
     * @param userId 用户ID
     * @param status 状态
     * @return 报名列表
     */
    IPage<ActivitySignup> pageByUser(Integer page, Integer size, Long userId, Integer status);

    /**
     * 分页查询用户参与的活动列表
     * @param page 页码
     * @param size 每页数量
     * @param userId 用户ID
     * @return 活动列表
     */
    IPage<com.outdoor.entity.Activity> pageUserActivities(Integer page, Integer size, Long userId);
}

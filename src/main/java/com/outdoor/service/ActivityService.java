package com.outdoor.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.outdoor.entity.Activity;

import java.util.Map;

/**
 * 活动服务接口
 */
public interface ActivityService extends IService<Activity> {

    /**
     * 分页查询活动列表
     * @param page 页码
     * @param size 每页数量
     * @param params 查询参数
     * @return 活动列表
     */
    IPage<Activity> pageList(Integer page, Integer size, Map<String, Object> params);

    /**
     * 获取活动详情
     * @param id 活动ID
     * @param userId 当前用户ID
     * @return 活动详情
     */
    Activity getDetail(Long id, Long userId);

    /**
     * 发布活动
     * @param activity 活动信息
     * @return 是否成功
     */
    boolean publish(Activity activity);

    /**
     * 审核活动
     * @param id 活动ID
     * @param status 审核状态
     * @param remark 审核备注
     * @return 是否成功
     */
    boolean audit(Long id, Integer status, String remark);

    /**
     * 取消活动
     * @param id 活动ID
     * @param organizerId 发起者ID
     * @return 是否成功
     */
    boolean cancel(Long id, Long organizerId);

    /**
     * 结束活动
     * @param id 活动ID
     * @param organizerId 发起者ID
     * @return 是否成功
     */
    boolean finish(Long id, Long organizerId);

    /**
     * 统计活动数量
     * @param status 状态
     * @param organizerId 发起者ID
     * @return 数量
     */
    long countByStatus(Integer status, Long organizerId);
}

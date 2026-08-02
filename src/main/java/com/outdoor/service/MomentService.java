package com.outdoor.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.outdoor.entity.Moment;

/**
 * 动态服务接口
 */
public interface MomentService extends IService<Moment> {

    /**
     * 发布动态
     * @param moment 动态信息
     * @return 是否成功
     */
    boolean publish(Moment moment);

    /**
     * 分页查询动态列表
     * @param page 页码
     * @param size 每页数量
     * @param userId 用户ID（可选）
     * @param status 状态
     * @param currentUserId 当前登录用户ID
     * @return 动态列表
     */
    IPage<Moment> pageList(Integer page, Integer size, Long userId, Integer status, Long currentUserId);

    /**
     * 查询关注用户的动态
     * @param page 页码
     * @param size 每页数量
     * @param userId 当前用户ID
     * @return 动态列表
     */
    IPage<Moment> pageFollowMoments(Integer page, Integer size, Long userId);

    /**
     * 审核动态
     * @param id 动态ID
     * @param status 审核状态
     * @param remark 审核备注
     * @return 是否成功
     */
    boolean audit(Long id, Integer status, String remark);

    /**
     * 点赞/取消点赞
     * @param momentId 动态ID
     * @param userId 用户ID
     * @return true-点赞成功，false-取消点赞成功
     */
    boolean toggleLike(Long momentId, Long userId);

    /**
     * 统计用户动态数量
     * @param userId 用户ID
     * @return 动态数量
     */
    int countByUser(Long userId);
}

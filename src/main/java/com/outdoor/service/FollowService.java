package com.outdoor.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.outdoor.entity.Follow;
import com.outdoor.entity.User;

/**
 * 关注服务接口
 */
public interface FollowService extends IService<Follow> {

    /**
     * 关注用户
     * @param userId 用户ID
     * @param followUserId 被关注用户ID
     * @return 是否成功
     */
    boolean follow(Long userId, Long followUserId);

    /**
     * 取消关注
     * @param userId 用户ID
     * @param followUserId 被关注用户ID
     * @return 是否成功
     */
    boolean unfollow(Long userId, Long followUserId);

    /**
     * 关注/取消关注
     * @param userId 用户ID
     * @param followUserId 被关注用户ID
     * @return true-关注成功，false-取消关注成功
     */
    boolean toggleFollow(Long userId, Long followUserId);

    /**
     * 查询是否已关注
     * @param userId 用户ID
     * @param followUserId 被关注用户ID
     * @return 是否已关注
     */
    boolean isFollowed(Long userId, Long followUserId);

    /**
     * 统计关注数
     * @param userId 用户ID
     * @return 关注数
     */
    int countFollowing(Long userId);

    /**
     * 统计粉丝数
     * @param userId 用户ID
     * @return 粉丝数
     */
    int countFollowers(Long userId);

    /**
     * 获取关注列表
     * @param page 页码
     * @param size 每页数量
     * @param userId 用户ID
     * @return 关注用户列表
     */
    IPage<User> pageFollows(Integer page, Integer size, Long userId);

    /**
     * 获取粉丝列表
     * @param page 页码
     * @param size 每页数量
     * @param userId 用户ID
     * @return 粉丝用户列表
     */
    IPage<User> pageFans(Integer page, Integer size, Long userId);
}

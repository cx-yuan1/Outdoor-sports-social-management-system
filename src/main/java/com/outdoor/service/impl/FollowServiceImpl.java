package com.outdoor.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.outdoor.entity.Follow;
import com.outdoor.entity.User;
import com.outdoor.mapper.FollowMapper;
import com.outdoor.service.FollowService;
import org.springframework.stereotype.Service;

/**
 * 关注服务实现类
 */
@Service
public class FollowServiceImpl extends ServiceImpl<FollowMapper, Follow> implements FollowService {

    @Override
    public boolean follow(Long userId, Long followUserId) {
        // 不能关注自己
        if (userId.equals(followUserId)) {
            return false;
        }
        // 检查是否已关注
        Follow existFollow = baseMapper.selectByUserAndFollow(userId, followUserId);
        if (existFollow != null) {
            return true; // 已关注
        }
        // 关注
        Follow follow = new Follow();
        follow.setUserId(userId);
        follow.setFollowUserId(followUserId);
        return save(follow);
    }

    @Override
    public boolean unfollow(Long userId, Long followUserId) {
        Follow existFollow = baseMapper.selectByUserAndFollow(userId, followUserId);
        if (existFollow != null) {
            return removeById(existFollow.getId());
        }
        return true;
    }

    @Override
    public boolean toggleFollow(Long userId, Long followUserId) {
        // 不能关注自己
        if (userId.equals(followUserId)) {
            return false;
        }
        
        Follow existFollow = baseMapper.selectByUserAndFollow(userId, followUserId);
        if (existFollow != null) {
            // 取消关注
            removeById(existFollow.getId());
            return false;
        } else {
            // 关注
            Follow follow = new Follow();
            follow.setUserId(userId);
            follow.setFollowUserId(followUserId);
            save(follow);
            return true;
        }
    }

    @Override
    public boolean isFollowed(Long userId, Long followUserId) {
        Follow follow = baseMapper.selectByUserAndFollow(userId, followUserId);
        return follow != null;
    }

    @Override
    public int countFollowing(Long userId) {
        return Math.toIntExact(count(new LambdaQueryWrapper<Follow>().eq(Follow::getUserId, userId)));
    }

    @Override
    public int countFollowers(Long userId) {
        return Math.toIntExact(count(new LambdaQueryWrapper<Follow>().eq(Follow::getFollowUserId, userId)));
    }

    @Override
    public IPage<User> pageFollows(Integer page, Integer size, Long userId) {
        Page<User> pageParam = new Page<>(page, size);
        return baseMapper.selectFollowList(pageParam, userId);
    }

    @Override
    public IPage<User> pageFans(Integer page, Integer size, Long userId) {
        Page<User> pageParam = new Page<>(page, size);
        return baseMapper.selectFansList(pageParam, userId);
    }
}

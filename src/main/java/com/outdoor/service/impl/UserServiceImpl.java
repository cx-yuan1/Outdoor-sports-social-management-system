package com.outdoor.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.outdoor.common.Constants;
import com.outdoor.entity.Follow;
import com.outdoor.entity.User;
import com.outdoor.mapper.FollowMapper;
import com.outdoor.mapper.UserMapper;
import com.outdoor.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private FollowMapper followMapper;

    @Override
    public User login(String username, String password) {
        User user = baseMapper.selectByUsername(username);
        if (user == null) {
            return null;
        }
        // 简单密码验证（不加密）
        if (!password.equals(user.getPassword())) {
            return null;
        }
        // 检查用户状态
        if (user.getStatus() != Constants.UserStatus.NORMAL) {
            return null;
        }
        return user;
    }

    @Override
    public boolean register(User user) {
        // 检查用户名是否已存在
        User existUser = baseMapper.selectByUsername(user.getUsername());
        if (existUser != null) {
            return false;
        }
        // 检查手机号是否已存在
        if (StrUtil.isNotBlank(user.getPhone())) {
            User phoneUser = baseMapper.selectByPhone(user.getPhone());
            if (phoneUser != null) {
                return false;
            }
        }
        // 设置默认值
        user.setRole(Constants.Role.USER);
        user.setStatus(Constants.UserStatus.NORMAL);
        return save(user);
    }

    @Override
    public User getByUsername(String username) {
        return baseMapper.selectByUsername(username);
    }

    @Override
    public IPage<User> pageList(Integer page, Integer size, String keyword, Integer role, Integer status) {
        Page<User> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        
        // 关键词搜索
        if (StrUtil.isNotBlank(keyword)) {
            wrapper.and(w -> w.like(User::getUsername, keyword)
                    .or().like(User::getNickname, keyword)
                    .or().like(User::getPhone, keyword));
        }
        // 角色筛选
        if (role != null) {
            wrapper.eq(User::getRole, role);
        }
        // 状态筛选
        if (status != null) {
            wrapper.eq(User::getStatus, status);
        }
        wrapper.orderByDesc(User::getCreatedTime);
        
        return page(pageParam, wrapper);
    }

    @Override
    public boolean updateStatus(Long id, Integer status) {
        User user = new User();
        user.setId(id);
        user.setStatus(status);
        return updateById(user);
    }

    @Override
    public User getUserDetail(Long id, Long currentUserId) {
        User user = getById(id);
        if (user == null) {
            return null;
        }
        // 查询粉丝数和关注数
        user.setFansCount(baseMapper.countFans(id));
        user.setFollowCount(baseMapper.countFollows(id));
        
        // 查询是否已关注
        if (currentUserId != null && !currentUserId.equals(id)) {
            Follow follow = followMapper.selectByUserAndFollow(currentUserId, id);
            user.setIsFollowed(follow != null);
        } else {
            user.setIsFollowed(false);
        }
        
        // 清除敏感信息
        user.setPassword(null);
        return user;
    }
}

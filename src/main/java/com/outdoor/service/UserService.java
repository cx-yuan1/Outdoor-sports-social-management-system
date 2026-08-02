package com.outdoor.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.outdoor.entity.User;

/**
 * 用户服务接口
 */
public interface UserService extends IService<User> {

    /**
     * 用户登录
     * @param username 用户名
     * @param password 密码
     * @return 用户信息
     */
    User login(String username, String password);

    /**
     * 用户注册
     * @param user 用户信息
     * @return 是否成功
     */
    boolean register(User user);

    /**
     * 根据用户名查询用户
     * @param username 用户名
     * @return 用户信息
     */
    User getByUsername(String username);

    /**
     * 分页查询用户列表
     * @param page 页码
     * @param size 每页数量
     * @param keyword 关键词
     * @param role 角色
     * @param status 状态
     * @return 用户列表
     */
    IPage<User> pageList(Integer page, Integer size, String keyword, Integer role, Integer status);

    /**
     * 更新用户状态
     * @param id 用户ID
     * @param status 状态
     * @return 是否成功
     */
    boolean updateStatus(Long id, Integer status);

    /**
     * 获取用户详情（包含粉丝数、关注数）
     * @param id 用户ID
     * @param currentUserId 当前登录用户ID
     * @return 用户信息
     */
    User getUserDetail(Long id, Long currentUserId);
}

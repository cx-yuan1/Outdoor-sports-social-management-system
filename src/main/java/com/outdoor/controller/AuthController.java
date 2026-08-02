    package com.outdoor.controller;

import com.outdoor.common.Constants;
import com.outdoor.common.Result;
import com.outdoor.entity.User;
import com.outdoor.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/api")
public class AuthController {

    @Autowired
    private UserService userService;

    /**
     * 用户登录
     * @param params 登录参数（username, password, loginRole）
     * @param session HTTP会话
     * @return 登录结果
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, Object> params, HttpSession session) {
        String username = params.get("username") != null ? params.get("username").toString() : null;
        String password = params.get("password") != null ? params.get("password").toString() : null;
        // 获取选择的登录身份：1-普通用户，2-活动组织者，3-管理员
        Integer loginRole = params.get("loginRole") != null ? 
                Integer.valueOf(params.get("loginRole").toString()) : Constants.Role.ADMIN;
        
        if (username == null || username.trim().isEmpty()) {
            return Result.error("请输入用户名");
        }
        if (password == null || password.trim().isEmpty()) {
            return Result.error("请输入密码");
        }
        
        User user = userService.login(username, password);
        if (user == null) {
            return Result.error("用户名或密码错误，或账号已被禁用");
        }
        
        // 验证登录身份与用户角色是否匹配
        if (loginRole == Constants.Role.ADMIN && user.getRole() != Constants.Role.ADMIN) {
            return Result.error("该账号不是管理员，请选择其他身份登录");
        }
        if (loginRole == Constants.Role.ORGANIZER && user.getRole() != Constants.Role.ORGANIZER) {
            return Result.error("该账号不是活动组织者，请选择其他身份登录");
        }
        if (loginRole == Constants.Role.USER && user.getRole() != Constants.Role.USER) {
            String roleName = user.getRole() == Constants.Role.ADMIN ? "管理员" : "组织者";
            return Result.error("该账号是" + roleName + "，请选择对应身份登录");
        }
        
        // 保存到Session
        session.setAttribute(Constants.SESSION_USER, user);
        
        // 返回用户信息（不包含密码）
        user.setPassword(null);
        Map<String, Object> data = new HashMap<>();
        data.put("user", user);
        
        return Result.success("登录成功", data);
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<Void> register(@RequestBody User user) {
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            return Result.error("请输入用户名");
        }
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            return Result.error("请输入密码");
        }
        if (user.getUsername().length() < 3 || user.getUsername().length() > 20) {
            return Result.error("用户名长度应为3-20个字符");
        }
        if (user.getPassword().length() < 6) {
            return Result.error("密码长度不能少于6位");
        }
        
        // 设置默认昵称
        if (user.getNickname() == null || user.getNickname().trim().isEmpty()) {
            user.setNickname(user.getUsername());
        }
        
        boolean success = userService.register(user);
        if (!success) {
            return Result.error("用户名或手机号已存在");
        }
        return Result.success();
    }

    /**
     * 退出登录
     */
    @PostMapping("/logout")
    public Result<Void> logout(HttpSession session) {
        session.removeAttribute(Constants.SESSION_USER);
        return Result.success();
    }

    /**
     * 获取当前登录用户信息
     * 未登录时返回成功但数据为null，不强制跳转登录页
     */
    @GetMapping("/current-user")
    public Result<User> getCurrentUser(HttpSession session) {
        User user = (User) session.getAttribute(Constants.SESSION_USER);
        if (user == null) {
            // 未登录返回成功但数据为null，前端根据data判断是否显示登录按钮
            return Result.success(null);
        }
        // 重新查询最新信息
        user = userService.getById(user.getId());
        if (user != null) {
            user.setPassword(null);
        }
        return Result.success(user);
    }
}

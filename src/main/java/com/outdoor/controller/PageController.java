package com.outdoor.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 页面跳转控制器
 */
@Controller
public class PageController {

    // ==================== 前台页面 ====================

    /**
     * 首页
     */
    @GetMapping({"/", "/index"})
    public String index() {
        return "front/index";
    }

    /**
     * 登录页
     */
    @GetMapping("/login")
    public String login() {
        return "front/login";
    }

    /**
     * 注册页
     */
    @GetMapping("/register")
    public String register() {
        return "front/register";
    }

    /**
     * 活动列表页
     */
    @GetMapping("/activities")
    public String activities() {
        return "front/activities";
    }

    /**
     * 活动详情页
     */
    @GetMapping("/activity/{id}")
    public String activityDetail(@PathVariable Long id) {
        return "front/activity-detail";
    }

    /**
     * 动态广场页
     */
    @GetMapping("/moments")
    public String moments() {
        return "front/moments";
    }

    /**
     * 用户主页
     */
    @GetMapping("/profile/{id}")
    public String userProfile(@PathVariable Long id) {
        return "front/profile";
    }

    /**
     * 公告列表页
     */
    @GetMapping("/notices")
    public String notices() {
        return "front/notices";
    }

    /**
     * 公告详情页
     */
    @GetMapping("/notice/{id}")
    public String noticeDetail(@PathVariable Long id) {
        return "front/notice-detail";
    }

    // ==================== 用户中心页面 ====================

    /**
     * 个人中心
     */
    @GetMapping("/user/center")
    public String userCenter() {
        return "front/user/center";
    }

    /**
     * 我的报名
     */
    @GetMapping("/user/signups")
    public String userSignups() {
        return "front/user/signups";
    }

    /**
     * 我的动态
     */
    @GetMapping("/user/moments")
    public String userMoments() {
        return "front/user/moments";
    }

    /**
     * 我的消息
     */
    @GetMapping("/user/messages")
    public String userMessages() {
        return "front/user/messages";
    }

    // ==================== 活动发起者页面 ====================

    /**
     * 发起者中心
     */
    @GetMapping("/organizer/center")
    public String organizerCenter() {
        return "front/organizer/center";
    }

    /**
     * 发布活动
     */
    @GetMapping("/organizer/publish")
    public String organizerPublish() {
        return "front/organizer/publish";
    }

    /**
     * 我的活动
     */
    @GetMapping("/organizer/activities")
    public String organizerActivities() {
        return "front/organizer/activities";
    }

    /**
     * 活动管理
     */
    @GetMapping("/organizer/activity/{id}")
    public String organizerActivityManage(@PathVariable Long id) {
        return "front/organizer/activity-manage";
    }

    // ==================== 管理后台页面 ====================

    /**
     * 管理后台首页
     */
    @GetMapping("/admin")
    public String admin() {
        return "admin/index";
    }

    /**
     * 用户管理
     */
    @GetMapping("/admin/users")
    public String adminUsers() {
        return "admin/users";
    }

    /**
     * 活动管理
     */
    @GetMapping("/admin/activities")
    public String adminActivities() {
        return "admin/activities";
    }

    /**
     * 分类管理
     */
    @GetMapping("/admin/categories")
    public String adminCategories() {
        return "admin/categories";
    }

    /**
     * 动态管理
     */
    @GetMapping("/admin/moments")
    public String adminMoments() {
        return "admin/moments";
    }

    /**
     * 轮播图管理
     */
    @GetMapping("/admin/banners")
    public String adminBanners() {
        return "admin/banners";
    }

    /**
     * 公告管理
     */
    @GetMapping("/admin/notices")
    public String adminNotices() {
        return "admin/notices";
    }
}

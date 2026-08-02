package com.outdoor.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.outdoor.common.Constants;
import com.outdoor.common.Result;
import com.outdoor.entity.*;
import com.outdoor.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

/**
 * 管理员控制器
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private ActivityService activityService;

    @Autowired
    private ActivityCategoryService categoryService;

    @Autowired
    private MomentService momentService;

    @Autowired
    private BannerService bannerService;

    @Autowired
    private NoticeService noticeService;

    @Autowired
    private StatisticsService statisticsService;

    @Autowired
    private MessageService messageService;

    // ==================== 统计数据 ====================

    /**
     * 获取概览数据
     */
    @GetMapping("/statistics/overview")
    public Result<Map<String, Object>> getOverview() {
        return Result.success(statisticsService.getOverview());
    }

    /**
     * 获取用户注册趋势
     */
    @GetMapping("/statistics/user-trend")
    public Result<List<Map<String, Object>>> getUserTrend() {
        return Result.success(statisticsService.getUserTrend());
    }

    /**
     * 获取活动分类统计
     */
    @GetMapping("/statistics/activity-category")
    public Result<List<Map<String, Object>>> getActivityCategoryStats() {
        return Result.success(statisticsService.getActivityCategoryStats());
    }

    /**
     * 获取活动状态统计
     */
    @GetMapping("/statistics/activity-status")
    public Result<List<Map<String, Object>>> getActivityStatusStats() {
        return Result.success(statisticsService.getActivityStatusStats());
    }

    // ==================== 用户管理 ====================

    /**
     * 用户列表
     */
    @GetMapping("/users")
    public Result<IPage<User>> userList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer role,
            @RequestParam(required = false) Integer status) {
        return Result.success(userService.pageList(page, size, keyword, role, status));
    }

    /**
     * 更新用户状态
     */
    @PutMapping("/user/{id}/status")
    public Result<Void> updateUserStatus(@PathVariable Long id, @RequestBody Map<String, Integer> params) {
        Integer status = params.get("status");
        boolean success = userService.updateStatus(id, status);
        return success ? Result.success() : Result.error("操作失败");
    }

    /**
     * 管理员添加用户
     * @param user 用户信息
     * @return 添加结果
     */
    @PostMapping("/user")
    public Result<Void> addUser(@RequestBody User user) {
        // 参数验证
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
        // 只允许添加普通用户和活动发起者
        if (user.getRole() == null || (user.getRole() != Constants.Role.USER && user.getRole() != Constants.Role.ORGANIZER)) {
            return Result.error("角色类型不正确");
        }
        
        // 设置默认昵称
        if (user.getNickname() == null || user.getNickname().trim().isEmpty()) {
            user.setNickname(user.getUsername());
        }
        
        // 设置默认状态为正常
        user.setStatus(Constants.UserStatus.NORMAL);
        
        boolean success = userService.register(user);
        if (!success) {
            return Result.error("用户名或手机号已存在");
        }
        return Result.success();
    }

    /**
     * 更新用户角色
     */
    @PutMapping("/user/{id}/role")
    public Result<Void> updateUserRole(@PathVariable Long id, @RequestBody Map<String, Integer> params) {
        Integer role = params.get("role");
        User user = new User();
        user.setId(id);
        user.setRole(role);
        boolean success = userService.updateById(user);
        return success ? Result.success() : Result.error("操作失败");
    }

    // ==================== 活动管理 ====================

    /**
     * 活动列表
     */
    @GetMapping("/activities")
    public Result<IPage<Activity>> activityList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String keyword) {
        
        java.util.Map<String, Object> params = new java.util.HashMap<>();
        if (status != null) {
            params.put("status", status);
        }
        if (categoryId != null) {
            params.put("categoryId", categoryId);
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            params.put("keyword", keyword.trim());
        }
        
        return Result.success(activityService.pageList(page, size, params));
    }

    /**
     * 审核活动
     */
    @PutMapping("/activity/{id}/audit")
    public Result<Void> auditActivity(@PathVariable Long id, @RequestBody Map<String, Object> params, HttpSession session) {
        Integer status = Integer.valueOf(params.get("status").toString());
        String remark = params.get("remark") != null ? params.get("remark").toString() : "";
        
        boolean success = activityService.audit(id, status, remark);
        
        // 发送通知
        if (success) {
            Activity activity = activityService.getById(id);
            if (activity != null) {
                Message message = new Message();
                message.setUserId(activity.getOrganizerId());
                message.setType(Constants.MessageType.SYSTEM);
                message.setTargetId(id);
                message.setTargetType("activity");
                
                if (status == Constants.ActivityStatus.APPROVED) {
                    message.setTitle("活动审核通过");
                    message.setContent("您发布的活动「" + activity.getTitle() + "」已通过审核");
                } else if (status == Constants.ActivityStatus.REJECTED) {
                    message.setTitle("活动审核未通过");
                    message.setContent("您发布的活动「" + activity.getTitle() + "」未通过审核" + (remark.isEmpty() ? "" : "，原因：" + remark));
                }
                messageService.send(message);
            }
        }
        
        return success ? Result.success() : Result.error("操作失败");
    }

    /**
     * 删除活动
     */
    @DeleteMapping("/activity/{id}")
    public Result<Void> deleteActivity(@PathVariable Long id) {
        boolean success = activityService.removeById(id);
        return success ? Result.success() : Result.error("删除失败");
    }

    // ==================== 分类管理 ====================

    /**
     * 分类列表
     */
    @GetMapping("/categories")
    public Result<List<ActivityCategory>> categoryList() {
        return Result.success(categoryService.list());
    }

    /**
     * 添加分类
     */
    @PostMapping("/category")
    public Result<Void> addCategory(@RequestBody ActivityCategory category) {
        boolean success = categoryService.save(category);
        return success ? Result.success() : Result.error("添加失败");
    }

    /**
     * 更新分类
     */
    @PutMapping("/category/{id}")
    public Result<Void> updateCategory(@PathVariable Long id, @RequestBody ActivityCategory category) {
        category.setId(id);
        boolean success = categoryService.updateById(category);
        return success ? Result.success() : Result.error("更新失败");
    }

    /**
     * 删除分类
     */
    @DeleteMapping("/category/{id}")
    public Result<Void> deleteCategory(@PathVariable Long id) {
        // 检查是否可以删除
        String error = categoryService.checkCanDelete(id);
        if (error != null) {
            return Result.error(error);
        }
        boolean success = categoryService.removeById(id);
        return success ? Result.success() : Result.error("删除失败");
    }

    // ==================== 动态管理 ====================

    /**
     * 动态列表
     */
    @GetMapping("/moments")
    public Result<IPage<Moment>> momentList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer status) {
        return Result.success(momentService.pageList(page, size, null, status, null));
    }

    /**
     * 审核动态
     */
    @PutMapping("/moment/{id}/audit")
    public Result<Void> auditMoment(@PathVariable Long id, @RequestBody Map<String, Object> params) {
        Integer status = Integer.valueOf(params.get("status").toString());
        String remark = params.get("remark") != null ? params.get("remark").toString() : "";
        
        boolean success = momentService.audit(id, status, remark);
        return success ? Result.success() : Result.error("操作失败");
    }

    /**
     * 删除动态
     */
    @DeleteMapping("/moment/{id}")
    public Result<Void> deleteMoment(@PathVariable Long id) {
        boolean success = momentService.removeById(id);
        return success ? Result.success() : Result.error("删除失败");
    }

    // ==================== 轮播图管理 ====================

    /**
     * 轮播图列表
     */
    @GetMapping("/banners")
    public Result<List<Banner>> bannerList() {
        return Result.success(bannerService.list());
    }

    /**
     * 添加轮播图
     */
    @PostMapping("/banner")
    public Result<Void> addBanner(@RequestBody Banner banner) {
        boolean success = bannerService.save(banner);
        return success ? Result.success() : Result.error("添加失败");
    }

    /**
     * 更新轮播图
     */
    @PutMapping("/banner/{id}")
    public Result<Void> updateBanner(@PathVariable Long id, @RequestBody Banner banner) {
        banner.setId(id);
        boolean success = bannerService.updateById(banner);
        return success ? Result.success() : Result.error("更新失败");
    }

    /**
     * 删除轮播图
     */
    @DeleteMapping("/banner/{id}")
    public Result<Void> deleteBanner(@PathVariable Long id) {
        boolean success = bannerService.removeById(id);
        return success ? Result.success() : Result.error("删除失败");
    }

    // ==================== 公告管理 ====================

    /**
     * 公告列表
     */
    @GetMapping("/notices")
    public Result<IPage<Notice>> noticeList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer status) {
        return Result.success(noticeService.pageList(page, size, status));
    }

    /**
     * 添加公告
     */
    @PostMapping("/notice")
    public Result<Void> addNotice(@RequestBody Notice notice, HttpSession session) {
        User user = (User) session.getAttribute(Constants.SESSION_USER);
        notice.setPublisherId(user.getId());
        boolean success = noticeService.save(notice);
        return success ? Result.success() : Result.error("添加失败");
    }

    /**
     * 更新公告
     */
    @PutMapping("/notice/{id}")
    public Result<Void> updateNotice(@PathVariable Long id, @RequestBody Notice notice) {
        notice.setId(id);
        boolean success = noticeService.updateById(notice);
        return success ? Result.success() : Result.error("更新失败");
    }

    /**
     * 删除公告
     */
    @DeleteMapping("/notice/{id}")
    public Result<Void> deleteNotice(@PathVariable Long id) {
        boolean success = noticeService.removeById(id);
        return success ? Result.success() : Result.error("删除失败");
    }
}

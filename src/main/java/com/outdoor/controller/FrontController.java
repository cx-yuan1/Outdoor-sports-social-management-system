package com.outdoor.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.outdoor.common.Constants;
import com.outdoor.common.Result;
import com.outdoor.entity.*;
import com.outdoor.mapper.ActivityReviewMapper;
import com.outdoor.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 前台公开接口控制器
 */
@RestController
@RequestMapping("/api/front")
public class FrontController {

    @Autowired
    private BannerService bannerService;

    @Autowired
    private NoticeService noticeService;

    @Autowired
    private ActivityCategoryService categoryService;

    @Autowired
    private ActivityService activityService;

    @Autowired
    private MomentService momentService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserService userService;

    @Autowired
    private FollowService followService;

    @Autowired
    private ActivitySignupService signupService;

    @Autowired
    private ActivityReviewMapper activityReviewMapper;

    /**
     * 获取首页数据
     */
    @GetMapping("/home")
    public Result<Map<String, Object>> getHomeData() {
        Map<String, Object> data = new HashMap<>();
        
        // 轮播图
        data.put("banners", bannerService.listEnabled());
        
        // 公告
        data.put("notices", noticeService.listPublished(5));
        
        // 活动分类
        data.put("categories", categoryService.listEnabled());
        
        // 热门活动（已通过审核的）
        Map<String, Object> params = new HashMap<>();
        params.put("status", Constants.ActivityStatus.APPROVED);
        data.put("hotActivities", activityService.pageList(1, 6, params).getRecords());
        
        return Result.success(data);
    }

    /**
     * 获取轮播图列表
     */
    @GetMapping("/banners")
    public Result<List<Banner>> getBanners() {
        return Result.success(bannerService.listEnabled());
    }

    /**
     * 获取公告列表
     */
    @GetMapping("/notices")
    public Result<List<Notice>> getNotices(@RequestParam(defaultValue = "10") Integer limit) {
        return Result.success(noticeService.listPublished(limit));
    }

    /**
     * 获取公告详情
     */
    @GetMapping("/notice/{id}")
    public Result<Notice> getNoticeDetail(@PathVariable Long id) {
        return Result.success(noticeService.getById(id));
    }

    /**
     * 获取活动分类列表
     */
    @GetMapping("/categories")
    public Result<List<ActivityCategory>> getCategories() {
        return Result.success(categoryService.listEnabled());
    }

    /**
     * 分页查询活动列表
     */
    @GetMapping("/activities")
    public Result<IPage<Activity>> getActivities(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String keyword) {
        
        Map<String, Object> params = new HashMap<>();
        params.put("status", Constants.ActivityStatus.APPROVED);
        if (categoryId != null) {
            params.put("categoryId", categoryId);
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            params.put("keyword", keyword.trim());
        }
        
        return Result.success(activityService.pageList(page, size, params));
    }

    /**
     * 获取活动详情
     */
    @GetMapping("/activity/{id}")
    public Result<Activity> getActivityDetail(@PathVariable Long id, HttpSession session) {
        User user = (User) session.getAttribute(Constants.SESSION_USER);
        Long userId = user != null ? user.getId() : null;
        Activity activity = activityService.getDetail(id, userId);
        if (activity == null) {
            return Result.error("活动不存在");
        }
        return Result.success(activity);
    }

    /**
     * 分页查询动态列表
     */
    @GetMapping("/moments")
    public Result<IPage<Moment>> getMoments(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long userId,
            HttpSession session) {
        
        User currentUser = (User) session.getAttribute(Constants.SESSION_USER);
        Long currentUserId = currentUser != null ? currentUser.getId() : null;
        
        return Result.success(momentService.pageList(page, size, userId, Constants.MomentStatus.APPROVED, currentUserId));
    }

    /**
     * 获取动态评论列表
     */
    @GetMapping("/moment/{id}/comments")
    public Result<List<Comment>> getMomentComments(@PathVariable Long id) {
        return Result.success(commentService.listByMoment(id));
    }

    /**
     * 获取用户主页信息
     */
    @GetMapping("/user/{id}")
    public Result<User> getUserProfile(@PathVariable Long id, HttpSession session) {
        User currentUser = (User) session.getAttribute(Constants.SESSION_USER);
        Long currentUserId = currentUser != null ? currentUser.getId() : null;
        
        User user = userService.getUserDetail(id, currentUserId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        return Result.success(user);
    }

    /**
     * 获取用户主页信息（带统计数据）
     */
    @GetMapping("/user/{id}/profile")
    public Result<Map<String, Object>> getUserProfileDetail(@PathVariable Long id, HttpSession session) {
        User currentUser = (User) session.getAttribute(Constants.SESSION_USER);
        Long currentUserId = currentUser != null ? currentUser.getId() : null;
        
        User user = userService.getById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        // 隐藏敏感信息
        user.setPassword(null);
        user.setPhone(null);
        
        Map<String, Object> data = new HashMap<>();
        data.put("id", user.getId());
        data.put("nickname", user.getNickname());
        data.put("avatar", user.getAvatar());
        data.put("bio", user.getIntro());
        data.put("gender", user.getGender());
        data.put("momentCount", momentService.countByUser(id));
        data.put("followingCount", followService.countFollowing(id));
        data.put("followersCount", followService.countFollowers(id));
        
        // 是否已关注
        if (currentUserId != null && !currentUserId.equals(id)) {
            data.put("isFollowed", followService.isFollowed(currentUserId, id));
        } else {
            data.put("isFollowed", false);
        }
        
        return Result.success(data);
    }

    /**
     * 获取用户的动态列表
     */
    @GetMapping("/user/{id}/moments")
    public Result<IPage<Moment>> getUserMoments(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            HttpSession session) {
        User currentUser = (User) session.getAttribute(Constants.SESSION_USER);
        Long currentUserId = currentUser != null ? currentUser.getId() : null;
        
        return Result.success(momentService.pageList(page, size, id, Constants.MomentStatus.APPROVED, currentUserId));
    }

    /**
     * 获取用户参与的活动列表
     */
    @GetMapping("/user/{id}/activities")
    public Result<IPage<Activity>> getUserActivities(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(signupService.pageUserActivities(page, size, id));
    }

    /**
     * 获取活动评价列表
     * @param id 活动ID
     * @param page 页码
     * @param size 每页数量
     * @return 评价列表
     */
    @GetMapping("/activity/{id}/reviews")
    public Result<IPage<ActivityReview>> getActivityReviews(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<ActivityReview> pageParam = new Page<>(page, size);
        return Result.success(activityReviewMapper.selectReviewPage(pageParam, id));
    }
}

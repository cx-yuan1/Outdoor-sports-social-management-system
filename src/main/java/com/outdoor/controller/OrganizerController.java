package com.outdoor.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.outdoor.common.Constants;
import com.outdoor.common.Result;
import com.outdoor.entity.Activity;
import com.outdoor.entity.ActivitySignup;
import com.outdoor.entity.User;
import com.outdoor.service.ActivityService;
import com.outdoor.service.ActivitySignupService;
import com.outdoor.service.MessageService;
import com.outdoor.entity.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * 活动发起者控制器
 */
@RestController
@RequestMapping("/api/organizer")
public class OrganizerController {

    @Autowired
    private ActivityService activityService;

    @Autowired
    private ActivitySignupService signupService;

    @Autowired
    private MessageService messageService;

    /**
     * 发布活动
     */
    @PostMapping("/activity")
    public Result<Void> publishActivity(@RequestBody Activity activity, HttpSession session) {
        User user = (User) session.getAttribute(Constants.SESSION_USER);
        activity.setOrganizerId(user.getId());
        
        boolean success = activityService.publish(activity);
        return success ? Result.success() : Result.error("发布失败");
    }

    /**
     * 更新活动
     */
    @PutMapping("/activity/{id}")
    public Result<Void> updateActivity(@PathVariable Long id, @RequestBody Activity activity, HttpSession session) {
        User user = (User) session.getAttribute(Constants.SESSION_USER);
        
        // 检查权限
        Activity existActivity = activityService.getById(id);
        if (existActivity == null || !existActivity.getOrganizerId().equals(user.getId())) {
            return Result.error("无权修改");
        }
        
        activity.setId(id);
        // 不允许修改敏感字段
        activity.setOrganizerId(null);
        activity.setStatus(null);
        activity.setCurrentParticipants(null);
        
        boolean success = activityService.updateById(activity);
        return success ? Result.success() : Result.error("更新失败");
    }

    /**
     * 取消活动
     */
    @PutMapping("/activity/{id}/cancel")
    public Result<Void> cancelActivity(@PathVariable Long id, HttpSession session) {
        User user = (User) session.getAttribute(Constants.SESSION_USER);
        boolean success = activityService.cancel(id, user.getId());
        return success ? Result.success() : Result.error("取消失败，活动状态不允许取消");
    }

    /**
     * 结束活动
     */
    @PutMapping("/activity/{id}/finish")
    public Result<Void> finishActivity(@PathVariable Long id, HttpSession session) {
        User user = (User) session.getAttribute(Constants.SESSION_USER);
        boolean success = activityService.finish(id, user.getId());
        return success ? Result.success() : Result.error("操作失败");
    }

    /**
     * 结束活动（别名）
     */
    @PostMapping("/activity/{id}/end")
    public Result<Void> endActivity(@PathVariable Long id, HttpSession session) {
        User user = (User) session.getAttribute(Constants.SESSION_USER);
        boolean success = activityService.finish(id, user.getId());
        return success ? Result.success() : Result.error("操作失败");
    }

    /**
     * 删除活动
     */
    @DeleteMapping("/activity/{id}")
    public Result<Void> deleteActivity(@PathVariable Long id, HttpSession session) {
        User user = (User) session.getAttribute(Constants.SESSION_USER);
        
        // 检查权限
        Activity activity = activityService.getById(id);
        if (activity == null) {
            return Result.error("活动不存在");
        }
        if (!activity.getOrganizerId().equals(user.getId())) {
            return Result.error("无权删除");
        }
        // 只有待审核状态的活动可以删除
        if (activity.getStatus() != Constants.ActivityStatus.PENDING) {
            return Result.error("只有待审核的活动可以删除");
        }
        
        boolean success = activityService.removeById(id);
        return success ? Result.success() : Result.error("删除失败");
    }

    /**
     * 我发起的活动列表
     */
    @GetMapping("/activities")
    public Result<IPage<Activity>> myActivities(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer status,
            HttpSession session) {
        User user = (User) session.getAttribute(Constants.SESSION_USER);
        
        Map<String, Object> params = new HashMap<>();
        params.put("organizerId", user.getId());
        if (status != null) {
            params.put("status", status);
        }
        
        return Result.success(activityService.pageList(page, size, params));
    }

    /**
     * 获取活动详情
     */
    @GetMapping("/activity/{id}")
    public Result<Activity> getActivity(@PathVariable Long id, HttpSession session) {
        User user = (User) session.getAttribute(Constants.SESSION_USER);
        Activity activity = activityService.getDetail(id, user.getId());
        
        if (activity == null) {
            return Result.error("活动不存在");
        }
        if (!activity.getOrganizerId().equals(user.getId())) {
            return Result.error("无权查看");
        }
        
        return Result.success(activity);
    }

    /**
     * 获取活动报名列表
     */
    @GetMapping("/activity/{id}/signups")
    public Result<IPage<ActivitySignup>> getSignups(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer status,
            HttpSession session) {
        User user = (User) session.getAttribute(Constants.SESSION_USER);
        
        // 检查权限
        Activity activity = activityService.getById(id);
        if (activity == null || !activity.getOrganizerId().equals(user.getId())) {
            return Result.error("无权查看");
        }
        
        return Result.success(signupService.pageByActivity(page, size, id, status));
    }

    /**
     * 审核报名
     */
    @PutMapping("/signup/{id}/audit")
    public Result<Void> auditSignup(
            @PathVariable Long id,
            @RequestBody Map<String, Object> params,
            HttpSession session) {
        User user = (User) session.getAttribute(Constants.SESSION_USER);
        
        // 检查权限
        ActivitySignup signup = signupService.getById(id);
        if (signup == null) {
            return Result.error("报名记录不存在");
        }
        Activity activity = activityService.getById(signup.getActivityId());
        if (activity == null || !activity.getOrganizerId().equals(user.getId())) {
            return Result.error("无权操作");
        }
        
        Integer status = Integer.valueOf(params.get("status").toString());
        String remark = params.get("remark") != null ? params.get("remark").toString() : "";
        
        boolean success = signupService.audit(id, status, remark);
        
        // 发送通知消息
        if (success) {
            Message message = new Message();
            message.setUserId(signup.getUserId());
            message.setSenderId(user.getId());
            message.setType(Constants.MessageType.ACTIVITY);
            message.setTargetId(activity.getId());
            message.setTargetType("activity");
            
            if (status == Constants.SignupStatus.APPROVED) {
                message.setTitle("报名审核通过");
                message.setContent("您报名的活动「" + activity.getTitle() + "」已通过审核");
            } else if (status == Constants.SignupStatus.REJECTED) {
                message.setTitle("报名审核未通过");
                message.setContent("您报名的活动「" + activity.getTitle() + "」未通过审核" + (remark.isEmpty() ? "" : "，原因：" + remark));
            }
            messageService.send(message);
        }
        
        return success ? Result.success() : Result.error("操作失败");
    }

    /**
     * 向参与者发送通知
     */
    @PostMapping("/activity/{id}/notify")
    public Result<Void> sendNotify(
            @PathVariable Long id,
            @RequestBody Map<String, String> params,
            HttpSession session) {
        User user = (User) session.getAttribute(Constants.SESSION_USER);
        
        // 检查权限
        Activity activity = activityService.getById(id);
        if (activity == null || !activity.getOrganizerId().equals(user.getId())) {
            return Result.error("无权操作");
        }
        
        String content = params.get("content");
        if (content == null || content.trim().isEmpty()) {
            return Result.error("请输入通知内容");
        }
        
        // 获取所有已通过审核的报名用户
        IPage<ActivitySignup> signups = signupService.pageByActivity(1, 1000, id, Constants.SignupStatus.APPROVED);
        
        for (ActivitySignup signup : signups.getRecords()) {
            Message message = new Message();
            message.setUserId(signup.getUserId());
            message.setSenderId(user.getId());
            message.setType(Constants.MessageType.ACTIVITY);
            message.setTitle("活动通知");
            message.setContent("活动「" + activity.getTitle() + "」发起者通知：" + content);
            message.setTargetId(id);
            message.setTargetType("activity");
            messageService.send(message);
        }
        
        return Result.success();
    }

    /**
     * 统计数据
     */
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getStatistics(HttpSession session) {
        User user = (User) session.getAttribute(Constants.SESSION_USER);
        
        Map<String, Object> data = new HashMap<>();
        data.put("totalCount", activityService.countByStatus(null, user.getId()));
        data.put("pendingCount", activityService.countByStatus(Constants.ActivityStatus.PENDING, user.getId()));
        data.put("approvedCount", activityService.countByStatus(Constants.ActivityStatus.APPROVED, user.getId()));
        data.put("finishedCount", activityService.countByStatus(Constants.ActivityStatus.FINISHED, user.getId()));
        
        return Result.success(data);
    }
}

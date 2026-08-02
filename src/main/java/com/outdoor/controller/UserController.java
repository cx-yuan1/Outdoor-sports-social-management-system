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

/**
 * 用户端控制器（需要登录）
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private ActivitySignupService signupService;

    @Autowired
    private MomentService momentService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private FollowService followService;

    @Autowired
    private MessageService messageService;

    /**
     * 更新个人资料
     */
    @PutMapping("/profile")
    public Result<Void> updateProfile(@RequestBody User user, HttpSession session) {
        User currentUser = (User) session.getAttribute(Constants.SESSION_USER);
        user.setId(currentUser.getId());
        // 不允许修改敏感字段
        user.setUsername(null);
        user.setPassword(null);
        user.setRole(null);
        user.setStatus(null);
        
        boolean success = userService.updateById(user);
        if (success) {
            // 更新Session中的用户信息
            User updatedUser = userService.getById(currentUser.getId());
            session.setAttribute(Constants.SESSION_USER, updatedUser);
        }
        return success ? Result.success() : Result.error("更新失败");
    }

    /**
     * 修改密码
     */
    @PutMapping("/password")
    public Result<Void> updatePassword(@RequestBody java.util.Map<String, String> params, HttpSession session) {
        User currentUser = (User) session.getAttribute(Constants.SESSION_USER);
        String oldPassword = params.get("oldPassword");
        String newPassword = params.get("newPassword");
        
        if (oldPassword == null || newPassword == null) {
            return Result.error("请输入密码");
        }
        
        // 验证旧密码
        User user = userService.getById(currentUser.getId());
        if (!oldPassword.equals(user.getPassword())) {
            return Result.error("原密码错误");
        }
        
        if (newPassword.length() < 6) {
            return Result.error("新密码长度不能少于6位");
        }
        
        user.setPassword(newPassword);
        return userService.updateById(user) ? Result.success() : Result.error("修改失败");
    }

    /**
     * 报名活动
     */
    @PostMapping("/signup")
    public Result<Void> signup(@RequestBody java.util.Map<String, Object> params, HttpSession session) {
        User user = (User) session.getAttribute(Constants.SESSION_USER);
        Long activityId = Long.valueOf(params.get("activityId").toString());
        String remark = params.get("remark") != null ? params.get("remark").toString() : "";
        
        String error = signupService.signup(activityId, user.getId(), remark);
        if (error != null) {
            return Result.error(error);
        }
        return Result.success();
    }

    /**
     * 取消报名
     */
    @DeleteMapping("/signup/{activityId}")
    public Result<Void> cancelSignup(@PathVariable Long activityId, HttpSession session) {
        User user = (User) session.getAttribute(Constants.SESSION_USER);
        boolean success = signupService.cancelSignup(activityId, user.getId());
        return success ? Result.success() : Result.error("取消失败");
    }

    /**
     * 我的报名列表
     */
    @GetMapping("/signups")
    public Result<IPage<ActivitySignup>> mySignups(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer status,
            HttpSession session) {
        User user = (User) session.getAttribute(Constants.SESSION_USER);
        return Result.success(signupService.pageByUser(page, size, user.getId(), status));
    }

    /**
     * 发布动态
     */
    @PostMapping("/moment")
    public Result<Void> publishMoment(@RequestBody Moment moment, HttpSession session) {
        User user = (User) session.getAttribute(Constants.SESSION_USER);
        moment.setUserId(user.getId());
        
        boolean success = momentService.publish(moment);
        return success ? Result.success() : Result.error("发布失败");
    }

    /**
     * 删除动态
     */
    @DeleteMapping("/moment/{id}")
    public Result<Void> deleteMoment(@PathVariable Long id, HttpSession session) {
        User user = (User) session.getAttribute(Constants.SESSION_USER);
        Moment moment = momentService.getById(id);
        if (moment == null || !moment.getUserId().equals(user.getId())) {
            return Result.error("无权删除");
        }
        return momentService.removeById(id) ? Result.success() : Result.error("删除失败");
    }

    /**
     * 我的动态列表
     */
    @GetMapping("/moments")
    public Result<IPage<Moment>> myMoments(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            HttpSession session) {
        User user = (User) session.getAttribute(Constants.SESSION_USER);
        return Result.success(momentService.pageList(page, size, user.getId(), null, user.getId()));
    }

    /**
     * 关注用户的动态
     */
    @GetMapping("/follow-moments")
    public Result<IPage<Moment>> followMoments(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            HttpSession session) {
        User user = (User) session.getAttribute(Constants.SESSION_USER);
        return Result.success(momentService.pageFollowMoments(page, size, user.getId()));
    }

    /**
     * 点赞/取消点赞动态
     */
    @PostMapping("/moment/{id}/like")
    public Result<Boolean> toggleLike(@PathVariable Long id, HttpSession session) {
        User user = (User) session.getAttribute(Constants.SESSION_USER);
        boolean isLiked = momentService.toggleLike(id, user.getId());
        return Result.success(isLiked);
    }

    /**
     * 发表评论
     */
    @PostMapping("/comment")
    public Result<Void> publishComment(@RequestBody Comment comment, HttpSession session) {
        User user = (User) session.getAttribute(Constants.SESSION_USER);
        comment.setUserId(user.getId());
        
        boolean success = commentService.publish(comment);
        return success ? Result.success() : Result.error("评论失败");
    }

    /**
     * 删除评论
     */
    @DeleteMapping("/comment/{id}")
    public Result<Void> deleteComment(@PathVariable Long id, HttpSession session) {
        User user = (User) session.getAttribute(Constants.SESSION_USER);
        boolean success = commentService.deleteComment(id, user.getId());
        return success ? Result.success() : Result.error("删除失败");
    }

    /**
     * 关注用户
     */
    @PostMapping("/follow/{userId}")
    public Result<Void> follow(@PathVariable Long userId, HttpSession session) {
        User user = (User) session.getAttribute(Constants.SESSION_USER);
        if (user.getId().equals(userId)) {
            return Result.error("不能关注自己");
        }
        boolean success = followService.follow(user.getId(), userId);
        return success ? Result.success() : Result.error("关注失败");
    }

    /**
     * 取消关注用户
     */
    @DeleteMapping("/follow/{userId}")
    public Result<Void> unfollow(@PathVariable Long userId, HttpSession session) {
        User user = (User) session.getAttribute(Constants.SESSION_USER);
        boolean success = followService.unfollow(user.getId(), userId);
        return success ? Result.success() : Result.error("取消关注失败");
    }

    /**
     * 关注/粉丝统计
     */
    @GetMapping("/follow-stats")
    public Result<java.util.Map<String, Object>> followStats(HttpSession session) {
        User user = (User) session.getAttribute(Constants.SESSION_USER);
        java.util.Map<String, Object> stats = new java.util.HashMap<>();
        stats.put("followingCount", followService.countFollowing(user.getId()));
        stats.put("followersCount", followService.countFollowers(user.getId()));
        return Result.success(stats);
    }

    /**
     * 我的关注列表
     */
    @GetMapping("/following")
    public Result<IPage<User>> myFollowing(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            HttpSession session) {
        User user = (User) session.getAttribute(Constants.SESSION_USER);
        return Result.success(followService.pageFollows(page, size, user.getId()));
    }

    /**
     * 我的粉丝列表
     */
    @GetMapping("/followers")
    public Result<IPage<User>> myFollowers(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            HttpSession session) {
        User user = (User) session.getAttribute(Constants.SESSION_USER);
        return Result.success(followService.pageFans(page, size, user.getId()));
    }

    /**
     * 获取会话列表（私信）
     */
    @GetMapping("/conversations")
    public Result<IPage<java.util.Map<String, Object>>> conversations(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            HttpSession session) {
        User user = (User) session.getAttribute(Constants.SESSION_USER);
        return Result.success(messageService.pageConversations(page, size, user.getId()));
    }

    /**
     * 获取与某用户的聊天记录
     */
    @GetMapping("/chat/{userId}")
    public Result<List<Message>> chatHistory(@PathVariable Long userId, HttpSession session) {
        User user = (User) session.getAttribute(Constants.SESSION_USER);
        List<Message> messages = messageService.getChatHistory(user.getId(), userId);
        // 标记为已读
        messageService.markChatRead(user.getId(), userId);
        return Result.success(messages);
    }

    /**
     * 发送私信
     */
    @PostMapping("/message")
    public Result<Void> sendMessage(@RequestBody java.util.Map<String, Object> params, HttpSession session) {
        User user = (User) session.getAttribute(Constants.SESSION_USER);
        Long receiverId = Long.valueOf(params.get("receiverId").toString());
        String content = params.get("content").toString();
        
        if (content == null || content.trim().isEmpty()) {
            return Result.error("消息内容不能为空");
        }
        
        Message message = new Message();
        message.setSenderId(user.getId());
        message.setUserId(receiverId);
        message.setType(Constants.MessageType.CHAT);
        message.setContent(content);
        
        boolean success = messageService.send(message);
        return success ? Result.success() : Result.error("发送失败");
    }

    /**
     * 我的消息列表
     */
    @GetMapping("/messages")
    public Result<IPage<Message>> myMessages(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer type,
            HttpSession session) {
        User user = (User) session.getAttribute(Constants.SESSION_USER);
        return Result.success(messageService.pageByUser(page, size, user.getId(), type));
    }

    /**
     * 未读消息数
     */
    @GetMapping("/messages/unread-count")
    public Result<Integer> unreadCount(HttpSession session) {
        User user = (User) session.getAttribute(Constants.SESSION_USER);
        return Result.success(messageService.countUnread(user.getId()));
    }

    /**
     * 标记消息已读
     */
    @PutMapping("/message/{id}/read")
    public Result<Void> markRead(@PathVariable Long id, HttpSession session) {
        User user = (User) session.getAttribute(Constants.SESSION_USER);
        return messageService.markRead(id, user.getId()) ? Result.success() : Result.error("操作失败");
    }

    /**
     * 标记所有消息已读
     */
    @PutMapping("/messages/read-all")
    public Result<Void> markAllRead(HttpSession session) {
        User user = (User) session.getAttribute(Constants.SESSION_USER);
        return messageService.markAllRead(user.getId()) ? Result.success() : Result.error("操作失败");
    }
}

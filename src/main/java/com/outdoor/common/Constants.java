package com.outdoor.common;

/**
 * 系统常量类
 */
public class Constants {

    /** Session中用户信息的key */
    public static final String SESSION_USER = "loginUser";

    /** 用户角色 */
    public static class Role {
        /** 普通用户 */
        public static final int USER = 1;
        /** 活动发起者 */
        public static final int ORGANIZER = 2;
        /** 管理员 */
        public static final int ADMIN = 3;
    }

    /** 用户状态 */
    public static class UserStatus {
        /** 禁用 */
        public static final int DISABLED = 0;
        /** 正常 */
        public static final int NORMAL = 1;
    }

    /** 活动状态 */
    public static class ActivityStatus {
        /** 待审核 */
        public static final int PENDING = 0;
        /** 已通过 */
        public static final int APPROVED = 1;
        /** 已拒绝 */
        public static final int REJECTED = 2;
        /** 进行中 */
        public static final int ONGOING = 3;
        /** 已结束 */
        public static final int FINISHED = 4;
        /** 已取消 */
        public static final int CANCELLED = 5;
    }

    /** 报名状态 */
    public static class SignupStatus {
        /** 待审核 */
        public static final int PENDING = 0;
        /** 已通过 */
        public static final int APPROVED = 1;
        /** 已拒绝 */
        public static final int REJECTED = 2;
        /** 已取消 */
        public static final int CANCELLED = 3;
    }

    /** 动态状态 */
    public static class MomentStatus {
        /** 待审核 */
        public static final int PENDING = 0;
        /** 已通过 */
        public static final int APPROVED = 1;
        /** 已拒绝 */
        public static final int REJECTED = 2;
    }

    /** 消息类型 */
    public static class MessageType {
        /** 私信 */
        public static final int CHAT = 1;
        /** 系统通知 */
        public static final int SYSTEM = 2;
        /** 活动通知 */
        public static final int ACTIVITY = 3;
        /** 互动消息 */
        public static final int INTERACTION = 4;
    }

    /** 点赞目标类型 */
    public static class LikeTargetType {
        /** 动态 */
        public static final int MOMENT = 1;
        /** 评论 */
        public static final int COMMENT = 2;
    }
}

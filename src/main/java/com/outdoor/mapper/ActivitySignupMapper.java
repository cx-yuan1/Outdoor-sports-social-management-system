package com.outdoor.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.outdoor.entity.Activity;
import com.outdoor.entity.ActivitySignup;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 活动报名Mapper接口
 */
@Mapper
public interface ActivitySignupMapper extends BaseMapper<ActivitySignup> {

    /**
     * 分页查询报名列表（带用户信息）
     * @param page 分页参数
     * @param activityId 活动ID
     * @param status 状态
     * @return 报名列表
     */
    IPage<ActivitySignup> selectSignupPage(Page<ActivitySignup> page, 
                                           @Param("activityId") Long activityId,
                                           @Param("status") Integer status);

    /**
     * 查询用户的报名记录（带活动信息）
     * @param page 分页参数
     * @param userId 用户ID
     * @param status 状态
     * @return 报名列表
     */
    IPage<ActivitySignup> selectUserSignupPage(Page<ActivitySignup> page,
                                               @Param("userId") Long userId,
                                               @Param("status") Integer status);

    /**
     * 查询用户参与的活动列表（已通过审核的报名）
     * @param page 分页参数
     * @param userId 用户ID
     * @return 活动列表
     */
    IPage<Activity> selectUserActivities(Page<Activity> page, @Param("userId") Long userId);

    /**
     * 查询用户是否已报名某活动
     * @param activityId 活动ID
     * @param userId 用户ID
     * @return 报名记录
     */
    @Select("SELECT * FROM activity_signup WHERE activity_id = #{activityId} AND user_id = #{userId}")
    ActivitySignup selectByActivityAndUser(@Param("activityId") Long activityId, @Param("userId") Long userId);
}

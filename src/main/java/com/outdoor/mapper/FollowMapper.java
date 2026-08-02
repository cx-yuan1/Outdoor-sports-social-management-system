package com.outdoor.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.outdoor.entity.Follow;
import com.outdoor.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 关注Mapper接口
 */
@Mapper
public interface FollowMapper extends BaseMapper<Follow> {

    /**
     * 查询是否已关注
     * @param userId 用户ID
     * @param followUserId 被关注用户ID
     * @return 关注记录
     */
    @Select("SELECT * FROM follow WHERE user_id = #{userId} AND follow_user_id = #{followUserId}")
    Follow selectByUserAndFollow(@Param("userId") Long userId, @Param("followUserId") Long followUserId);

    /**
     * 查询用户的关注列表
     * @param page 分页参数
     * @param userId 用户ID
     * @return 关注用户列表
     */
    IPage<User> selectFollowList(Page<User> page, @Param("userId") Long userId);

    /**
     * 查询用户的粉丝列表
     * @param page 分页参数
     * @param userId 用户ID
     * @return 粉丝用户列表
     */
    IPage<User> selectFansList(Page<User> page, @Param("userId") Long userId);
}

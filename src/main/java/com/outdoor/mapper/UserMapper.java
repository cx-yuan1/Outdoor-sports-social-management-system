package com.outdoor.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.outdoor.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 用户Mapper接口
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 根据用户名查询用户
     * @param username 用户名
     * @return 用户信息
     */
    @Select("SELECT * FROM sys_user WHERE username = #{username} AND deleted = 0")
    User selectByUsername(@Param("username") String username);

    /**
     * 根据手机号查询用户
     * @param phone 手机号
     * @return 用户信息
     */
    @Select("SELECT * FROM sys_user WHERE phone = #{phone} AND deleted = 0")
    User selectByPhone(@Param("phone") String phone);

    /**
     * 统计用户粉丝数
     * @param userId 用户ID
     * @return 粉丝数
     */
    @Select("SELECT COUNT(*) FROM follow WHERE follow_user_id = #{userId}")
    Integer countFans(@Param("userId") Long userId);

    /**
     * 统计用户关注数
     * @param userId 用户ID
     * @return 关注数
     */
    @Select("SELECT COUNT(*) FROM follow WHERE user_id = #{userId}")
    Integer countFollows(@Param("userId") Long userId);
}

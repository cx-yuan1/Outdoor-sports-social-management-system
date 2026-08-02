package com.outdoor.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.outdoor.entity.Moment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 动态Mapper接口
 */
@Mapper
public interface MomentMapper extends BaseMapper<Moment> {

    /**
     * 分页查询动态列表（带用户信息）
     * @param page 分页参数
     * @param userId 用户ID（可选，查询指定用户的动态）
     * @param status 状态
     * @return 动态列表
     */
    IPage<Moment> selectMomentPage(Page<Moment> page, 
                                   @Param("userId") Long userId,
                                   @Param("status") Integer status);

    /**
     * 查询关注用户的动态
     * @param page 分页参数
     * @param userId 当前用户ID
     * @return 动态列表
     */
    IPage<Moment> selectFollowMomentPage(Page<Moment> page, @Param("userId") Long userId);
}

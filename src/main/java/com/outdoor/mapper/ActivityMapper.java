package com.outdoor.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.outdoor.entity.Activity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * 活动Mapper接口
 */
@Mapper
public interface ActivityMapper extends BaseMapper<Activity> {

    /**
     * 分页查询活动列表（带关联信息）
     * @param page 分页参数
     * @param params 查询参数
     * @return 活动列表
     */
    IPage<Activity> selectActivityPage(Page<Activity> page, @Param("params") Map<String, Object> params);

    /**
     * 查询活动详情（带关联信息）
     * @param id 活动ID
     * @return 活动详情
     */
    Activity selectActivityDetail(@Param("id") Long id);

    /**
     * 增加浏览次数
     * @param id 活动ID
     */
    void incrementViewCount(@Param("id") Long id);

    /**
     * 更新报名人数
     * @param id 活动ID
     * @param count 变化数量（正数增加，负数减少）
     */
    void updateParticipantCount(@Param("id") Long id, @Param("count") Integer count);
}

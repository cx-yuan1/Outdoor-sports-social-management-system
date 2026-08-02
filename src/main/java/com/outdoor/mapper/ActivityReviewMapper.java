package com.outdoor.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.outdoor.entity.ActivityReview;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 活动评价Mapper接口
 */
@Mapper
public interface ActivityReviewMapper extends BaseMapper<ActivityReview> {

    /**
     * 分页查询活动评价（带用户信息）
     * @param page 分页参数
     * @param activityId 活动ID
     * @return 评价列表
     */
    IPage<ActivityReview> selectReviewPage(Page<ActivityReview> page, @Param("activityId") Long activityId);

    /**
     * 计算活动平均评分
     * @param activityId 活动ID
     * @return 平均评分
     */
    @Select("SELECT AVG(rating) FROM activity_review WHERE activity_id = #{activityId}")
    Double selectAvgRating(@Param("activityId") Long activityId);
}

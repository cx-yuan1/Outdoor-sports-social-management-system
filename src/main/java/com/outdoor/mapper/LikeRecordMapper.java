package com.outdoor.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.outdoor.entity.LikeRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 点赞记录Mapper接口
 */
@Mapper
public interface LikeRecordMapper extends BaseMapper<LikeRecord> {

    /**
     * 查询是否已点赞
     * @param userId 用户ID
     * @param targetId 目标ID
     * @param targetType 目标类型
     * @return 点赞记录
     */
    @Select("SELECT * FROM like_record WHERE user_id = #{userId} AND target_id = #{targetId} AND target_type = #{targetType}")
    LikeRecord selectByUserAndTarget(@Param("userId") Long userId, 
                                     @Param("targetId") Long targetId, 
                                     @Param("targetType") Integer targetType);
}

package com.outdoor.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.outdoor.entity.Notice;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统公告Mapper接口
 */
@Mapper
public interface NoticeMapper extends BaseMapper<Notice> {
}

package com.outdoor.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.outdoor.entity.Notice;
import com.outdoor.mapper.NoticeMapper;
import com.outdoor.service.NoticeService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 系统公告服务实现类
 */
@Service
public class NoticeServiceImpl extends ServiceImpl<NoticeMapper, Notice> implements NoticeService {

    @Override
    public List<Notice> listPublished(Integer limit) {
        LambdaQueryWrapper<Notice> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Notice::getStatus, 1)
               .orderByDesc(Notice::getType)
               .orderByDesc(Notice::getCreatedTime);
        if (limit != null && limit > 0) {
            wrapper.last("LIMIT " + limit);
        }
        return list(wrapper);
    }

    @Override
    public IPage<Notice> pageList(Integer page, Integer size, Integer status) {
        Page<Notice> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Notice> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(Notice::getStatus, status);
        }
        wrapper.orderByDesc(Notice::getCreatedTime);
        return page(pageParam, wrapper);
    }
}

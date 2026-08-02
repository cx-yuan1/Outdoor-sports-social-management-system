package com.outdoor.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.outdoor.entity.Notice;

import java.util.List;

/**
 * 系统公告服务接口
 */
public interface NoticeService extends IService<Notice> {

    /**
     * 获取已发布的公告列表
     * @param limit 数量限制
     * @return 公告列表
     */
    List<Notice> listPublished(Integer limit);

    /**
     * 分页查询公告
     * @param page 页码
     * @param size 每页数量
     * @param status 状态
     * @return 公告列表
     */
    IPage<Notice> pageList(Integer page, Integer size, Integer status);
}

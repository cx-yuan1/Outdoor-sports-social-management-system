package com.outdoor.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.outdoor.entity.Banner;

import java.util.List;

/**
 * 轮播图服务接口
 */
public interface BannerService extends IService<Banner> {

    /**
     * 获取启用的轮播图列表
     * @return 轮播图列表
     */
    List<Banner> listEnabled();
}

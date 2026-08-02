package com.outdoor.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.outdoor.entity.Banner;
import com.outdoor.mapper.BannerMapper;
import com.outdoor.service.BannerService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 轮播图服务实现类
 */
@Service
public class BannerServiceImpl extends ServiceImpl<BannerMapper, Banner> implements BannerService {

    @Override
    public List<Banner> listEnabled() {
        LambdaQueryWrapper<Banner> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Banner::getStatus, 1)
               .orderByAsc(Banner::getSort);
        return list(wrapper);
    }
}

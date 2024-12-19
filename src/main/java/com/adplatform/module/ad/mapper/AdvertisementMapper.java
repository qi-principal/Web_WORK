package com.adplatform.module.ad.mapper;

import com.adplatform.module.ad.entity.Advertisement;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 广告数据访问接口
 *
 * @author andrew
 * @date 2023-12-19
 */
@Mapper
public interface AdvertisementMapper extends BaseMapper<Advertisement> {
} 
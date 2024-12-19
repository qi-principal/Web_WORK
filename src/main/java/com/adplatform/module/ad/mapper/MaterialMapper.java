package com.adplatform.module.ad.mapper;

import com.adplatform.module.ad.entity.Material;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 广告素材数据访问接口
 *
 * @author andrew
 * @date 2023-12-19
 */
@Mapper
public interface MaterialMapper extends BaseMapper<Material> {
    
    // 分页查询方法
    Page<Material> selectPage(Material query, Pageable pageable);
} 
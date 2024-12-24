package com.adplatform.module.ad.mapper;

import com.adplatform.module.ad.entity.Material;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;

/**
 * 广告素材数据访问接口
 *
 * @author andrew
 * @date 2023-12-19
 */
@Mapper
public interface MaterialMapper extends BaseMapper<Material> {
    
    /**
     * 分页查询方法
     *
     * @param page 分页参数
     * @param query 查询条件
     * @return 分页结果
     */
    IPage<Material> selectPage(Page<Material> page, Material query);
} 
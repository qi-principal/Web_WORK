package com.adplatform.module.website.mapper;

import com.adplatform.module.website.entity.Website;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface WebsiteMapper extends BaseMapper<Website> {
    @Select("SELECT name FROM website WHERE id=#{adSpaceId}")
    String getAdTitleById(Long adSpaceId);
}
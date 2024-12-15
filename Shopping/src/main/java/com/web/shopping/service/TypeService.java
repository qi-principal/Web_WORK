package com.web.shopping.service;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.web.shopping.entity.Type;
import com.web.shopping.mapper.TypeMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 分类信息表业务处理
 **/
@Service
public class TypeService {

    @Resource
    private TypeMapper typeMapper;

    /**
     * 新增
     */
    public void add(Type type) {
        typeMapper.insert(type);
    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        typeMapper.deleteById(id);
    }

    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            typeMapper.deleteById(id);
        }
    }

    /**
     * 修改
     */
    public void updateById(Type type) {
        typeMapper.updateById(type);
    }

    /**
     * 根据ID查询
     */
    public Type selectById(Integer id) {
        return typeMapper.selectById(id);
    }

    /**
     * 查询所有
     */
    public List<Type> selectAll(Type type) {
        return typeMapper.selectAll(type);
    }

    /**
     * 分页查询
     */
    public PageInfo<Type> selectPage(Type type, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Type> list = typeMapper.selectAll(type);
        return PageInfo.of(list);
    }

}
package com.web.shopping.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.web.shopping.entity.User;
import com.web.shopping.mapper.UserMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 商家业务处理
 **/
@Service
public class UserService {

    @Resource
    private UserMapper userMapper;

    /**
     * 新增
     */
    public void add(User user) {
        userMapper.insert(user);
    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        userMapper.deleteById(id);
    }

    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            userMapper.deleteById(id);
        }
    }

    /**
     * 修改
     */
    public void updateById(User user) {
        userMapper.updateById(user);
    }

    /**
     * 根据ID查询
     */
    public User selectById(Integer id) {
        return userMapper.selectById(id);
    }

    /**
     * 查询所有
     */
    public List<User> selectAll(User user) {
        return userMapper.selectAll(user);
    }

    /**
     * 分页查询
     */
    public PageInfo<User> selectPage(User user, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<User> list = userMapper.selectAll(user);
        return PageInfo.of(list);
    }

}
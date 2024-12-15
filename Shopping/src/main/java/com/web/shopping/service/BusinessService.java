package com.web.shopping.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.web.shopping.entity.Business;
import com.web.shopping.mapper.BusinessMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;


import java.util.List;

/**
 * 商家业务处理
 **/
@Service
public class BusinessService {

    @Resource
    private BusinessMapper businessMapper;

    /**
     * 新增
     */
    public void add(Business business) {

        businessMapper.insert(business);
    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        businessMapper.deleteById(id);
    }

    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            businessMapper.deleteById(id);
        }
    }

    /**
     * 修改
     */
    public void updateById(Business business) {
        businessMapper.updateById(business);
    }

    /**
     * 根据ID查询
     */
    public Business selectById(Integer id) {
        return businessMapper.selectById(id);
    }

    /**
     * 查询所有
     */
    public List<Business> selectAll(Business business) {
        return businessMapper.selectAll(business);
    }

    /**
     * 分页查询
     */
    public PageInfo<Business> selectPage(Business business, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Business> list = businessMapper.selectAll(business);
        return PageInfo.of(list);
    }

    /**
     * 登录
     */
//    public Account login(Account account) {
//        Account dbBusiness = businessMapper.selectByUsername(account.getUsername());
//        if (ObjectUtil.isNull(dbBusiness)) {
//            throw new CustomException(ResultCodeEnum.USER_NOT_EXIST_ERROR);
//        }
//        if (!account.getPassword().equals(dbBusiness.getPassword())) {
//            throw new CustomException(ResultCodeEnum.USER_ACCOUNT_ERROR);
//        }
//        // 生成token
//        String tokenData = dbBusiness.getId() + "-" + RoleEnum.BUSINESS.name();
//        String token = TokenUtils.createToken(tokenData, dbBusiness.getPassword());
//        dbBusiness.setToken(token);
//        return dbBusiness;
//    }
//
//    /**
//     * 注册
//     */
//    public void register(Account account) {
//        Business business = new Business();
//        BeanUtils.copyProperties(account, business);
//        add(business);
//    }
//
//    /**
//     * 修改密码
//     */
//    public void updatePassword(Account account) {
//        Business dbBusiness = businessMapper.selectByUsername(account.getUsername());
//        if (ObjectUtil.isNull(dbBusiness)) {
//            throw new CustomException(ResultCodeEnum.USER_NOT_EXIST_ERROR);
//        }
//        if (!account.getPassword().equals(dbBusiness.getPassword())) {
//            throw new CustomException(ResultCodeEnum.PARAM_PASSWORD_ERROR);
//        }
//        dbBusiness.setPassword(account.getNewPassword());
//        businessMapper.updateById(dbBusiness);
//    }

}
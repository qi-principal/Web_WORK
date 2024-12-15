package com.web.shopping.service;

import cn.hutool.core.date.DateUtil;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.web.shopping.common.enums.ResultCodeEnum;
import com.web.shopping.entity.Cart;
import com.web.shopping.entity.Goods;
import com.web.shopping.entity.Orders;
import com.web.shopping.exception.CustomException;
import com.web.shopping.mapper.CartMapper;
import com.web.shopping.mapper.GoodsMapper;
import com.web.shopping.mapper.OrdersMapper;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 收藏业务处理
 **/
@Service
public class OrdersService {

    @Resource
    private OrdersMapper ordersMapper;
    @Resource
    private CartMapper cartMapper;
    @Resource
    private GoodsMapper goodsMapper;

    /**
     * 新增
     */
    public void add(Orders orders) {
        orders.setOrderId(DateUtil.format(new Date(), "yyyyMMddHHmmss"));
        for (Cart cart : orders.getCartData()) {
            Orders dbOrders = new Orders();
            BeanUtils.copyProperties(orders, dbOrders);
            dbOrders.setGoodsId(cart.getGoodsId());
            dbOrders.setBusinessId(cart.getBusinessId());
            dbOrders.setNum(cart.getNum());
            dbOrders.setPrice(cart.getNum() * cart.getGoodsPrice());
            ordersMapper.insert(dbOrders);

            // 把购物车里对应的商品删掉
            cartMapper.deleteById(cart.getId());
            // 检查库存是否足够
            Goods goods = goodsMapper.selectById(cart.getGoodsId());
            if (goods == null || goods.getStock() < cart.getNum()) {
                throw new CustomException(ResultCodeEnum.GOODS_STOCK_ERROR);
            }
            // 把商品销量增加一下,库存减一下
            goods.setCount(goods.getCount() + cart.getNum());
            goods.setStock(goods.getStock() - cart.getNum());
            goodsMapper.updateById(goods);


        }
    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        ordersMapper.deleteById(id);
    }

    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            ordersMapper.deleteById(id);
        }
    }

    /**
     * 修改
     */
    public void updateById(Orders orders) {
        ordersMapper.updateById(orders);
    }

    /**
     * 根据ID查询
     */
    public Orders selectById(Integer id) {
        return ordersMapper.selectById(id);
    }

    /**
     * 查询所有
     */
    public List<Orders> selectAll(Orders orders) {
        return ordersMapper.selectAll(orders);
    }

    /**
     * 分页查询
     */
    public PageInfo<Orders> selectPage(Orders orders, Integer pageNum, Integer pageSize) {

        PageHelper.startPage(pageNum, pageSize);
        List<Orders> list = ordersMapper.selectAll(orders);
        return PageInfo.of(list);
    }
    public List<Orders> getAllOrdersWithStock(Orders orders) {
        return ordersMapper.selectAll(orders);
    }

    public Orders getOrderWithStockById(int orderId) {
        return ordersMapper.selectById(orderId);
    }
}
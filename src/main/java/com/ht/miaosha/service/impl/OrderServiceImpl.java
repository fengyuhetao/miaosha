package com.ht.miaosha.service.impl;

import com.ht.miaosha.dao.OrderDao;
import com.ht.miaosha.entity.MiaoshaGoods;
import com.ht.miaosha.entity.MiaoshaOrder;
import com.ht.miaosha.entity.MiaoshaUser;
import com.ht.miaosha.entity.OrderInfo;
import com.ht.miaosha.enumerate.OrderChannel;
import com.ht.miaosha.enumerate.OrderStatus;
import com.ht.miaosha.service.OrderService;
import com.ht.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;

/**
 * Created by hetao on 2019/1/5.
 */
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    OrderDao orderDao;

    @Override
    public MiaoshaOrder getMiaoshaOrderByUserIdAndGoodsId(long userId, long goodsId) {
        return orderDao.getMiaoshaOrderByUserIdAndGoodsId(userId, goodsId);
    }

    @Transactional
    @Override
    public OrderInfo  createOrder(MiaoshaUser user, GoodsVo goodsVo) {
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setCreateDate(new Date());
        orderInfo.setDeliveryAddrId(0L);
        orderInfo.setGoodsCount(1);
        orderInfo.setGoodsId(goodsVo.getId());
        orderInfo.setGoodsName(goodsVo.getGoodsName());
        orderInfo.setGoodsPrice(goodsVo.getMiaoshaPrice());
        orderInfo.setOrderChannel(OrderChannel.ANDROID.getCode());
        orderInfo.setStatus(OrderStatus.NEW_WITHOUT_PAY.getCode());
        orderInfo.setUserId(user.getId());

        long orderId = orderDao.insert(orderInfo);
        MiaoshaOrder miaoshaOrder = new MiaoshaOrder();
        miaoshaOrder.setGoodsId(goodsVo.getId());
        miaoshaOrder.setOrderId(orderId);
        miaoshaOrder.setUserId(user.getId());
        orderDao.insertMiaoshaOrder(miaoshaOrder);

        orderInfo.setId(orderId);
        return orderInfo;
    }
}

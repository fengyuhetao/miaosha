package com.ht.miaosha.service.impl;

import com.ht.miaosha.dao.GoodsDao;
import com.ht.miaosha.dao.OrderDao;
import com.ht.miaosha.entity.Goods;
import com.ht.miaosha.entity.MiaoshaUser;
import com.ht.miaosha.entity.OrderInfo;
import com.ht.miaosha.service.GoodsService;
import com.ht.miaosha.service.MiaoshaService;
import com.ht.miaosha.service.OrderService;
import com.ht.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

/**
 * Created by hetao on 2019/1/5.
 */
@Service
public class MiaoshaServiceImpl implements MiaoshaService {
    @Autowired
    OrderDao orderDao;

    /**
     * 这里注意，提倡在自己的service引入对应的dao，如果需要引入其他dao，则选择引入其他dao对应的service
     * 所以这里不引入GoodsDao,而选择引入GoodsService
     */

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Transactional
    @Override
    public OrderInfo miaosha(MiaoshaUser user, GoodsVo goodsVo) {

        goodsService.reduceStock(goodsVo);

//        order_info miaosha_order
        OrderInfo orderInfo = orderService.createOrder(user, goodsVo);
        return null;
    }
}

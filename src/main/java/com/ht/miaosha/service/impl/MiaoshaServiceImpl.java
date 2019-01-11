package com.ht.miaosha.service.impl;

import com.ht.miaosha.dao.OrderDao;
import com.ht.miaosha.entity.MiaoshaOrder;
import com.ht.miaosha.entity.MiaoshaUser;
import com.ht.miaosha.entity.OrderInfo;
import com.ht.miaosha.redis.MiaoshaKey;
import com.ht.miaosha.redis.RedisService;
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

    @Autowired
    RedisService redisService;

    @Transactional
    @Override
    public OrderInfo miaosha(MiaoshaUser user, GoodsVo goodsVo) {

        boolean isSuccess = goodsService.reduceStock(goodsVo);

        if(!isSuccess) {
            setGoodsOver(goodsVo.getId());
            return null;
        }

//        order_info miaosha_order
        return orderService.createOrder(user, goodsVo);
    }

    @Override
    public long getMiaoshaResult(long userId, long goodsId) {
        MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdAndGoodsId(userId, goodsId);
        if(order != null) {         // 秒杀成功
            return order.getOrderId();
        } else {
            boolean isOver = getGoodsOver(goodsId);
            if(isOver) {             // 秒杀结束
                return -1;
            } else {                 // 排队中
                return 0;
            }
        }
    }

    private void setGoodsOver(Long goodsId) {
        redisService.set(MiaoshaKey.isGoodsOver, ""+goodsId, true);
    }

    private boolean getGoodsOver(Long goodsId) {
        return redisService.exists(MiaoshaKey.isGoodsOver, ""+goodsId);
    }
}

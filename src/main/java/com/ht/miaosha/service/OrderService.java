package com.ht.miaosha.service;

import com.ht.miaosha.entity.MiaoshaOrder;
import com.ht.miaosha.entity.MiaoshaUser;
import com.ht.miaosha.entity.OrderInfo;
import com.ht.miaosha.vo.GoodsVo;

/**
 * Created by hetao on 2019/1/5.
 */
public interface OrderService {

    MiaoshaOrder getMiaoshaOrderByUserIdAndGoodsId(long id, long goodsId);

    OrderInfo createOrder(MiaoshaUser user, GoodsVo goodsVo);
}

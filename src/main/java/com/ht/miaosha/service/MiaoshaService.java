package com.ht.miaosha.service;

import com.ht.miaosha.entity.MiaoshaUser;
import com.ht.miaosha.entity.OrderInfo;
import com.ht.miaosha.vo.GoodsVo;

/**
 * Created by hetao on 2019/1/5.
 */
public interface MiaoshaService {
    OrderInfo miaosha(MiaoshaUser user, GoodsVo goodsVo);
}

package com.ht.miaosha.service;

import com.ht.miaosha.entity.MiaoshaUser;
import com.ht.miaosha.entity.OrderInfo;
import com.ht.miaosha.vo.GoodsVo;

/**
 * Created by hetao on 2019/1/5.
 */
public interface MiaoshaService {
    OrderInfo miaosha(MiaoshaUser user, GoodsVo goodsVo);

    long getMiaoshaResult(long userId, long goodsId);

    boolean checkPath(MiaoshaUser user, long goodsId, String path);

    String createMiaoshaPath(MiaoshaUser user, long goodsId);
}

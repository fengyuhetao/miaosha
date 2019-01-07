package com.ht.miaosha.vo;

import com.ht.miaosha.entity.MiaoshaUser;
import lombok.Data;

/**
 * Created by hetao on 2019/1/7.
 */
@Data
public class GoodsDetailVo {
    private int miaoshaStatus;

    private int remainSeconds;

    private GoodsVo goods;

    private MiaoshaUser user;
}

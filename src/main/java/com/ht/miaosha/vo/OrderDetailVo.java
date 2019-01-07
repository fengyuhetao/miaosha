package com.ht.miaosha.vo;

import com.ht.miaosha.entity.OrderInfo;
import lombok.Data;

/**
 * Created by hetao on 2019/1/7.
 */
@Data
public class OrderDetailVo {
    private GoodsVo goods;

    private OrderInfo orderInfo;
}

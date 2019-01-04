package com.ht.miaosha.entity;

import lombok.Data;

/**
 * Created by hetao on 2019/1/4.
 */
@Data
public class MiaoshaOrder {
    private Long id;

    private Long userId;

    private Long orderId;

    private Long goodsId;
}

package com.ht.miaosha.entity;

import lombok.Data;

import java.util.Date;

/**
 * Created by hetao on 2019/1/4.
 */
@Data
public class MiaoshaGoods {
    private Long id;

    private Long goodsId;

    private Integer stockCount;

    private Date startDate;

    private Date endDate;

}

package com.ht.miaosha.entity;

import com.ht.miaosha.enumerate.OrderChannel;
import com.ht.miaosha.enumerate.OrderStatus;
import lombok.Data;

import java.util.Date;

/**
 * Created by hetao on 2019/1/4.
 */
@Data
public class OrderInfo {
    private Long id;

    private Integer userId;

    private Long goodsId;

    private Long deliveryAddrId;

    private String goodsName;

    private Integer goodsCount;

    private Double goodsPrice;

    private OrderChannel orderChannel;

    private OrderStatus status;

    private Date createDate;

    private Date payDate;

}

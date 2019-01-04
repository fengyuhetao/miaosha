package com.ht.miaosha.entity;

import lombok.Data;

import javax.persistence.Entity;

/**
 * Created by hetao on 2019/1/4.
 */
@Data
public class Goods {
    private Long id;

    private String goodsName;

    private String goodsTitle;

    private String goodsImg;

    private String goodsDetail;

    private Double goodsPrice;

    private Integer goodsStock;
}

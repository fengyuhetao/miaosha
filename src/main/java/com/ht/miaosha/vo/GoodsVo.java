package com.ht.miaosha.vo;

import com.ht.miaosha.entity.Goods;
import lombok.Data;

import java.util.Date;

/**
 * Created by hetao on 2019/1/4.
 */
@Data
public class GoodsVo extends Goods {

    private Integer stockCount;

    private Date startDate;

    private Date endDate;

    private Double miaoshaPrice;
}

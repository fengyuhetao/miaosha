package com.ht.miaosha.rabbitmq;

import com.ht.miaosha.entity.MiaoshaUser;
import lombok.Data;

/**
 * Created by hetao on 2019/1/11.
 */
@Data
public class MiaoshaMessage {
    private MiaoshaUser user;

    private long goodsId;
}

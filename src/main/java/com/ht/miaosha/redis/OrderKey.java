package com.ht.miaosha.redis;

/**
 * Created by hetao on 2018/12/21.
 */
public class OrderKey extends BasePrefix {


    private OrderKey(String prefix) {
        super(prefix);
    }

    public static OrderKey getMiaoshaOrderByUidGid = new OrderKey("moug");


}

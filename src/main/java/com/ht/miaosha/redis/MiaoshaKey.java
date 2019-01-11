package com.ht.miaosha.redis;

/**
 * Created by hetao on 2018/12/21.
 */
public class MiaoshaKey extends BasePrefix {

    public static final int TOKEN_EXPIRE = 3600 * 24 * 2;

    private MiaoshaKey(String prefix) {
        super(prefix);
    }

    public static MiaoshaKey isGoodsOver = new MiaoshaKey("go");
}

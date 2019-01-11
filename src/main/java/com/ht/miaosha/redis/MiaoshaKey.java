package com.ht.miaosha.redis;

/**
 * Created by hetao on 2018/12/21.
 */
public class MiaoshaKey extends BasePrefix {

    public static final int TOKEN_EXPIRE = 3600 * 24 * 2;

    private MiaoshaKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static MiaoshaKey isGoodsOver = new MiaoshaKey(0,"go");

    public static MiaoshaKey getMiaoshaPath = new MiaoshaKey(60, "mp");
}

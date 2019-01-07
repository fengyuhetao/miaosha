package com.ht.miaosha.redis;

/**
 * Created by hetao on 2018/12/21.
 */
public class MiaoshaUserKey extends BasePrefix {

    public static final int TOKEN_EXPIRE = 3600 * 24 * 2;

    private MiaoshaUserKey(String prefix) {
        super(TOKEN_EXPIRE, prefix);
    }

    public static MiaoshaUserKey token = new MiaoshaUserKey("tk");

    public static MiaoshaUserKey getById = new MiaoshaUserKey("id");
}

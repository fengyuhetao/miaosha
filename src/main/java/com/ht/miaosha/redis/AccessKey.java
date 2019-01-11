package com.ht.miaosha.redis;

/**
 * Created by hetao on 2018/12/21.
 */
public class AccessKey extends BasePrefix {


    private AccessKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static AccessKey access = new AccessKey(5, "ac");

    public static AccessKey withExpire(int expireSeconds) {
        return new AccessKey(expireSeconds, "access");
    }
}

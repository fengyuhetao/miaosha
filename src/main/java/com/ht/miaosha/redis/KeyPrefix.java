package com.ht.miaosha.redis;

/**
 * Created by hetao on 2018/12/21.
 */
public interface KeyPrefix {
    public int expireSeconds();
    public String getKeyPrefix();
}

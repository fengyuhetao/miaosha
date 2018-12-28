package com.ht.miaosha.redis;

/**
 * Created by hetao on 2018/12/21.
 */
public abstract
class BasePrefix implements KeyPrefix{

    private int expireSconds;

    private String keyPrefix;

    public BasePrefix(String prefix) {
        this(0, prefix);
    }

    public BasePrefix(int expireSconds, String prefix) {
        this.expireSconds = expireSconds;
        this.keyPrefix = prefix;
    }

    /**
     * 默认0 代表永不过期
     * @return
     */
    @Override
    public int expireSeconds() {
        return expireSconds;
    }

    @Override
    public String getKeyPrefix() {
        String className = getClass().getSimpleName();
        return className + ":" + keyPrefix;
    }
}

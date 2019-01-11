package com.ht.miaosha.access;

import com.ht.miaosha.entity.MiaoshaUser;

/**
 * Created by hetao on 2019/1/11.
 */
public class UserContext {
    private static ThreadLocal<MiaoshaUser> userThreadLocal = new ThreadLocal<>();

    public static void setUser(MiaoshaUser user) {
        userThreadLocal.set(user);
    }

    public static MiaoshaUser getUser() {
        return userThreadLocal.get();
    }
}

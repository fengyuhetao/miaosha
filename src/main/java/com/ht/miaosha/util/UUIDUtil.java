package com.ht.miaosha.util;

import java.util.UUID;

/**
 * Created by hetao on 2018/12/28.
 */
public class UUIDUtil {
    public static String uuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}

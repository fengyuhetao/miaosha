package com.ht.miaosha.enumerate;

import lombok.Data;

/**
 * Created by hetao on 2019/1/4.
 */
public enum OrderChannel {
    PC(0, "pc"),
    ANDROID(1, "android"),
    IOS(2, "ios");

    private Integer code;
    private String message;

    OrderChannel(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}

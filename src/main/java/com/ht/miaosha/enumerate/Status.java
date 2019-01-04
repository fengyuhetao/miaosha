package com.ht.miaosha.enumerate;

import lombok.Data;

/**
 * Created by hetao on 2019/1/4.
 */
public enum  Status {
    NEW_WITHOUT_PAY(0, "新建未支付"),
    PAY_COMPLETE(1, "已支付"),
    SEND_GOOD(2, "已发货"),
    GET_GOOD(3, "已收货"),
    BACK_GOOD(4, "已退货"),
    ORDER_COMPLETE(5, "已完成");

    private Integer code;

    private String message;

    Status(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }
}

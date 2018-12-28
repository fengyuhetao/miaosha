package com.ht.miaosha.entity;

import lombok.Data;

import java.util.Date;

/**
 * Created by hetao on 2018/12/27.
 */
@Data
public class MiaoshaUser  {
    private Long id;

    private String nickname;

    private String password;

    private String salt;

    private String head;

    private Date registerDate;

    private Date lastLoginDate;

    private Integer loginCount;
}

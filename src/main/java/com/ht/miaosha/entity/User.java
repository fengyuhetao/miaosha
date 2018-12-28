package com.ht.miaosha.entity;

import lombok.Data;

/**
 * Created by hetao on 2018/12/18.
 */
@Data
public class User {
    private int id;
    private String name;

    public User(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public User() {
    }
}

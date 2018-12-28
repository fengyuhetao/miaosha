package com.ht.miaosha.service;

import com.ht.miaosha.entity.User;

/**
 * Created by hetao on 2018/12/18.
 */
public interface UserService {
    public User getById(int id);

    public boolean tx();
}

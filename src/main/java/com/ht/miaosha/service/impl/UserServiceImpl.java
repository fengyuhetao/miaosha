package com.ht.miaosha.service.impl;

import com.ht.miaosha.dao.UserDao;
import com.ht.miaosha.entity.User;
import com.ht.miaosha.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

/**
 * Created by hetao on 2018/12/18.
 */
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserDao userDao;

    @Override
    public User getById(int id) {
        return userDao.getById(id);
    }

    /** 该注解需要表采用MyIsam **/
    @Override
    @Transactional
    public boolean tx() {
        User user = new User();
        user.setId(6);
        user.setName("TEST");
        userDao.insert(user);

        user.setId(4);
        user.setName("ahha");
        userDao.insert(user);
        return true;
    }
}

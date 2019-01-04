package com.ht.miaosha.service;

import com.ht.miaosha.entity.MiaoshaUser;
import com.ht.miaosha.vo.LoginVo;

import javax.servlet.http.HttpServletResponse;

/**
 * Created by hetao on 2018/12/27.
 */
public interface MiaoshaUserService {
    String COOKIE_NAME_TOKEN = "token";

    MiaoshaUser getById(long id);

    boolean login(LoginVo loginVo, HttpServletResponse response);

    MiaoshaUser getByToken(HttpServletResponse response, String token);
}

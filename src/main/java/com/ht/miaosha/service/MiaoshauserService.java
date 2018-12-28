package com.ht.miaosha.service;

import com.ht.miaosha.entity.MiaoshaUser;
import com.ht.miaosha.result.CodeMsg;
import com.ht.miaosha.vo.LoginVo;

import javax.servlet.http.HttpServletResponse;

/**
 * Created by hetao on 2018/12/27.
 */
public interface MiaoshauserService {
    public MiaoshaUser getById(long id);

    boolean login(LoginVo loginVo, HttpServletResponse response);

    MiaoshaUser getByToken(HttpServletResponse response, String token);
}

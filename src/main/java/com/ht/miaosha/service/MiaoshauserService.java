package com.ht.miaosha.service;

import com.ht.miaosha.entity.MiaoshaUser;
import com.ht.miaosha.result.CodeMsg;
import com.ht.miaosha.vo.LoginVo;

/**
 * Created by hetao on 2018/12/27.
 */
public interface MiaoshauserService {
    public MiaoshaUser getById(long id);

    CodeMsg login(LoginVo loginVo);
}

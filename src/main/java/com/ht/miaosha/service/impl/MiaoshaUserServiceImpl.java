package com.ht.miaosha.service.impl;

import com.ht.miaosha.dao.MiaoshaUserDao;
import com.ht.miaosha.entity.MiaoshaUser;
import com.ht.miaosha.exception.GlobalException;
import com.ht.miaosha.result.CodeMsg;
import com.ht.miaosha.service.MiaoshauserService;
import com.ht.miaosha.util.MD5Util;
import com.ht.miaosha.vo.LoginVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by hetao on 2018/12/27.
 */
@Service
public class MiaoshaUserServiceImpl implements MiaoshauserService {

    @Autowired
    MiaoshaUserDao miaoshaUserDao;

    @Override
    public MiaoshaUser getById(long id) {
        return miaoshaUserDao.getById(id);
    }

    @Override
    public boolean login(LoginVo loginVo) {
        if(loginVo == null) {
            throw new GlobalException(CodeMsg.SERVER_ERROR);
        }

//        判断手机号是否存在
        MiaoshaUser user = getById(Long.parseLong(loginVo.getMobile()));
        if(user == null) {
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        }

        //验证密码
        String calcPass = MD5Util.formPassToDBPass(loginVo.getPassword(), user.getSalt());
        if(!calcPass.equals(user.getPassword())) {
            throw new GlobalException(CodeMsg.PASSWORD_ERROR);
        }

        return true;
    }
}

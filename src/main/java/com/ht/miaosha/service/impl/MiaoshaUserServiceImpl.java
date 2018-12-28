package com.ht.miaosha.service.impl;

import com.ht.miaosha.dao.MiaoshaUserDao;
import com.ht.miaosha.entity.MiaoshaUser;
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
    public CodeMsg login(LoginVo loginVo) {
        if(loginVo == null) {
            return CodeMsg.SERVER_ERROR;
        }

        String mobile = loginVo.getMobile();
        String formPass = loginVo.getPassword();
//        判断手机号是否存在
        MiaoshaUser user = getById(Long.parseLong(mobile));
        if(user == null) {
            return CodeMsg.MOBILE_NOT_EXIST;
        }

        //验证密码
        String dbPass = user.getPassword();
        String saltDB = user.getSalt();
        String calcPass = MD5Util.formPassToDBPass(formPass, saltDB);
        if(!calcPass.equals(dbPass)) {
            return CodeMsg.PASSWORD_ERROR;
        }

        return CodeMsg.SUCCESS;
    }
}

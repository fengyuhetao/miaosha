package com.ht.miaosha.service.impl;

import com.ht.miaosha.dao.MiaoshaUserDao;
import com.ht.miaosha.entity.MiaoshaUser;
import com.ht.miaosha.exception.GlobalException;
import com.ht.miaosha.redis.MiaoshaUserKey;
import com.ht.miaosha.redis.RedisService;
import com.ht.miaosha.result.CodeMsg;
import com.ht.miaosha.service.MiaoshaUserService;
import com.ht.miaosha.util.MD5Util;
import com.ht.miaosha.util.UUIDUtil;
import com.ht.miaosha.vo.LoginVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by hetao on 2018/12/27.
 */
@Service
public class MiaoshaUserServiceImpl implements MiaoshaUserService {

    @Autowired
    MiaoshaUserDao miaoshaUserDao;

    @Autowired
    RedisService redisService;

    @Override
    public MiaoshaUser getById(long id) {
//        取缓存
        MiaoshaUser user = redisService.get(MiaoshaUserKey.getById, ""+id, MiaoshaUser.class);
        if(user != null) {
            return user;
        }

//        取数据库
//        对象缓存，粒度最细
        user = miaoshaUserDao.getById(id);
        if(user != null) {
            redisService.set(MiaoshaUserKey.getById, ""+id, user);
        }

        return user;
    }

    public boolean updatePassword(String token, long id, String passwordNew) {
//        取user
        MiaoshaUser user = getById(id);
        if(user == null) {
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        }

//        更新数据库
        MiaoshaUser toBeUpdate = new MiaoshaUser();
        toBeUpdate.setPassword(MD5Util.formPassToDBPass(passwordNew, user.getSalt()));
        miaoshaUserDao.update(toBeUpdate);

//        处理缓存
        redisService.delete(MiaoshaUserKey.getById, ""+id);
        user.setPassword(toBeUpdate.getPassword());
        redisService.set(MiaoshaUserKey.token, token, user);
        return true;
    }

    @Override
    public boolean login(LoginVo loginVo, HttpServletResponse response) {
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

        String token = UUIDUtil.uuid();

//        生成cookie
        addCookie(response, user, token);

        return true;
    }

    @Override
    public MiaoshaUser getByToken(HttpServletResponse response, String token) {
        if(StringUtils.isEmpty(token)) {
            return null;
        }

        MiaoshaUser user = redisService.get(MiaoshaUserKey.token, token, MiaoshaUser.class);

        // 延长有效期

        if(user != null) {
            addCookie(response, user, token);
        }

        return user;
    }

    private void addCookie(HttpServletResponse response, MiaoshaUser user, String token) {
        redisService.set(MiaoshaUserKey.token, token, user);
        Cookie cookie = new Cookie(COOKIE_NAME_TOKEN, token);
        cookie.setMaxAge(MiaoshaUserKey.token.expireSeconds());
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}

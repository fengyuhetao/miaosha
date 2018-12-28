package com.ht.miaosha.service.impl;

import com.ht.miaosha.dao.MiaoshaUserDao;
import com.ht.miaosha.entity.MiaoshaUser;
import com.ht.miaosha.exception.GlobalException;
import com.ht.miaosha.redis.MiaoshaUserKey;
import com.ht.miaosha.redis.RedisService;
import com.ht.miaosha.result.CodeMsg;
import com.ht.miaosha.service.MiaoshauserService;
import com.ht.miaosha.util.MD5Util;
import com.ht.miaosha.util.UUIDUtil;
import com.ht.miaosha.vo.LoginVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.CookieHandler;

/**
 * Created by hetao on 2018/12/27.
 */
@Service
public class MiaoshaUserServiceImpl implements MiaoshauserService {
    public static final String COOKIE_NAME_TOKEN = "token";

    @Autowired
    MiaoshaUserDao miaoshaUserDao;

    @Autowired
    RedisService redisService;

    @Override
    public MiaoshaUser getById(long id) {
        return miaoshaUserDao.getById(id);
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

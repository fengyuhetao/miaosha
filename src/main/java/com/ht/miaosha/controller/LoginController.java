package com.ht.miaosha.controller;

import com.ht.miaosha.result.CodeMsg;
import com.ht.miaosha.result.Result;
import com.ht.miaosha.service.MiaoshauserService;
import com.ht.miaosha.util.ValidatorUtil;
import com.ht.miaosha.vo.LoginVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.util.StringUtils;

/**
 * Created by hetao on 2018/12/27.
 */
@Controller
public class LoginController {

    private static Logger log = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    public MiaoshauserService miaoshauserService;

    @GetMapping("/to_login")
    public String toLogin() {
        return "login";
    }

    @PostMapping("do_login")
    @ResponseBody
    public Result doLogin(LoginVo loginVo) {
        log.info(loginVo.toString());
        // 参数校验
        String passInput = loginVo.getPassword();
        String mobile = loginVo.getMobile();
        if(StringUtils.isEmpty(passInput)) {
            return Result.error(CodeMsg.PASSWORD_EMPTY);
        }

        if(StringUtils.isEmpty(mobile)) {
            return Result.error(CodeMsg.MOBILE_EMPTY);
        }

        if(!ValidatorUtil.isMobile(mobile)) {
            return Result.error(CodeMsg.MOBILE_ERROR);
        }

        log.info(miaoshauserService.getById(Long.parseLong("13263138306")).toString());
        CodeMsg result = miaoshauserService.login(loginVo);
        if(result.getCode() == 0) {
            return Result.success(CodeMsg.SUCCESS);
        } else {
            return Result.error(result);
        }
    }

}

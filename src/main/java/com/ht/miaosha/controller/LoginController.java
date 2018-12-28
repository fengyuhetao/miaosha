package com.ht.miaosha.controller;

import com.ht.miaosha.result.CodeMsg;
import com.ht.miaosha.result.Result;
import com.ht.miaosha.service.MiaoshauserService;
import com.ht.miaosha.vo.LoginVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

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
    public Result doLogin(HttpServletResponse response, @Valid LoginVo loginVo) {
        log.info(miaoshauserService.getById(Long.parseLong("13263138306")).toString());
        miaoshauserService.login(loginVo, response);
        return Result.success(true);
    }

}

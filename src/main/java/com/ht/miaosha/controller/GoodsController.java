package com.ht.miaosha.controller;

import com.ht.miaosha.entity.MiaoshaUser;
import com.ht.miaosha.entity.User;
import com.ht.miaosha.service.impl.MiaoshaUserServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpServletResponse;

/**
 * Created by hetao on 2018/12/28.
 */
@Controller
@RequestMapping("goods")
public class GoodsController {
    private static final Logger log = LoggerFactory.getLogger(GoodsController.class);

    @Autowired
    MiaoshaUserServiceImpl userService;

    @GetMapping("/to_list")
    public String toList(Model model, MiaoshaUser user) {
        log.info(user.toString());
        model.addAttribute("user", user);
        return "goods_list";
    }
}

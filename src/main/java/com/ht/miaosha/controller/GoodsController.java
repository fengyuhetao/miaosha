package com.ht.miaosha.controller;

import com.ht.miaosha.entity.MiaoshaUser;
import com.ht.miaosha.service.GoodsService;
import com.ht.miaosha.service.MiaoshaUserService;
import com.ht.miaosha.vo.GoodsVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by hetao on 2018/12/28.
 */
@Controller
@RequestMapping("goods")
public class GoodsController {
    private static final Logger log = LoggerFactory.getLogger(GoodsController.class);

    @Autowired
    MiaoshaUserService userService;

    @Autowired
    GoodsService goodsService;

    @GetMapping("/to_list")
    public String toList(Model model, MiaoshaUser user) {
        log.info(user.toString());
        model.addAttribute("user", user);
        List<GoodsVo> goodsVoList = goodsService.getGoodsList();
        model.addAttribute("goodsList", goodsVoList);
        return "goods_list";
    }

    @GetMapping("/to_detail/{goodsId}")
    public String toDetail(Model model, MiaoshaUser user, @PathVariable("goodsId")long goodsId) {
        model.addAttribute("user", user);
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);

        long startAt = goods.getStartDate().getTime();
        long endAt = goods.getEndDate().getTime();
        long now = System.currentTimeMillis();

        int miaoshaStatus = 0;

        int remainSeconds = 0;

        // 秒杀尚未开始
        if(now < startAt) {
            miaoshaStatus = 0;
            remainSeconds = (int)(startAt - now) / 1000;
        } else if(now > endAt) {
//            秒杀已经结束
            miaoshaStatus = 2;
            remainSeconds = -1;
        } else {
//            秒杀进行中
            miaoshaStatus = 1;
            remainSeconds = 0;
        }

        model.addAttribute("miaoshaStatus", miaoshaStatus);
        model.addAttribute("remainSeconds", remainSeconds);
        model.addAttribute("goods", goods);

        return "goods_detail";
    }


}

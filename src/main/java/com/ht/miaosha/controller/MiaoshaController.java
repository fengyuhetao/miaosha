package com.ht.miaosha.controller;

import com.ht.miaosha.entity.MiaoshaOrder;
import com.ht.miaosha.entity.MiaoshaUser;
import com.ht.miaosha.entity.OrderInfo;
import com.ht.miaosha.result.CodeMsg;
import com.ht.miaosha.service.GoodsService;
import com.ht.miaosha.service.MiaoshaService;
import com.ht.miaosha.service.OrderService;
import com.ht.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created by hetao on 2019/1/5.
 */
@RequestMapping("/miaosha")
public class MiaoshaController {
    @Autowired
    private GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    MiaoshaService miaoshaService;

    @RequestMapping("/do_miaosha")
    public String list(Model model, MiaoshaUser user, @RequestParam("goodsId")long goodsId) {
        model.addAttribute("user", user);

        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);

        int stock = goods.getStockCount();

        if(stock <= 0) {
            model.addAttribute("errmsg", CodeMsg.MIAOSHA_OVER);
            return "miaosha_fail";
        }

//        判断是否秒杀到了
        MiaoshaOrder miaoshaOrder = orderService.getMiaoshaOrderByUserIdAndGoodsId(user.getId(), goodsId);

        if(miaoshaOrder != null) {
            model.addAttribute("errmsg", CodeMsg.REPEAT_MIAOSHA);
            return "miaosha_fail";
        }

//        减库存，下订单，写入秒杀订单          事务
        OrderInfo orderInfo = miaoshaService.miaosha(user, goods);
        model.addAttribute("orderInfo", orderInfo);
        model.addAttribute("goods", goods);

        return "order_detail";
    }
}

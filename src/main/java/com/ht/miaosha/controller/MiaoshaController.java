package com.ht.miaosha.controller;

import com.ht.miaosha.entity.MiaoshaOrder;
import com.ht.miaosha.entity.MiaoshaUser;
import com.ht.miaosha.entity.OrderInfo;
import com.ht.miaosha.result.CodeMsg;
import com.ht.miaosha.result.Result;
import com.ht.miaosha.service.GoodsService;
import com.ht.miaosha.service.MiaoshaService;
import com.ht.miaosha.service.OrderService;
import com.ht.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created by hetao on 2019/1/5.
 */
@Controller
@RequestMapping("/miaosha")
public class MiaoshaController {
    @Autowired
    private GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    MiaoshaService miaoshaService;

    @PostMapping("/do_miaosha")
    public String doMiaosha(Model model, MiaoshaUser user, @RequestParam("goodsId")long goodsId) {
        model.addAttribute("user", user);

//TODO        判断库存                 这里容易出现并发的问题
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);

        int stock = goods.getStockCount();

        if(stock <= 0) {
            model.addAttribute("errmsg", CodeMsg.MIAOSHA_OVER.getMsg());
            return "miaosha_fail";
        }

//TODO        判断是否秒杀到了              这里也可能出现并发的问题
        MiaoshaOrder miaoshaOrder = orderService.getMiaoshaOrderByUserIdAndGoodsId(user.getId(), goodsId);

        if(miaoshaOrder != null) {
            model.addAttribute("errmsg", CodeMsg.REPEAT_MIAOSHA.getMsg());
            return "miaosha_fail";
        }

//        减库存，下订单，写入秒杀订单          事务
        OrderInfo orderInfo = miaoshaService.miaosha(user, goods);
        model.addAttribute("orderInfo", orderInfo);
        model.addAttribute("goods", goods);

        return "order_detail";
    }

    @PostMapping("/api/do_miaosha")
    public Result doApiMiaosha(MiaoshaUser user, @RequestParam("goodsId")long goodsId) {
        if(user == null) {
            return Result.error(CodeMsg.MOBILE_NOT_EXIST);
        }

        //TODO        解决商品超卖的问题
        //同一个用户能够秒杀多个商品: 可以通过设置索引，添加userid+goodsid 唯一索引
        //商品库存变为负值: 可以在执行sql语句的时候，设置要求 库存>0 的条件
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);

        int stock = goods.getStockCount();

        if(stock <= 0) {
            return Result.error(CodeMsg.MIAOSHA_OVER);
        }

//TODO        判断是否秒杀到了              这里也可能出现并发的问题
        MiaoshaOrder miaoshaOrder = orderService.getMiaoshaOrderByUserIdAndGoodsId(user.getId(), goodsId);

        if(miaoshaOrder != null) {
            return Result.error(CodeMsg.MIAOSHA_OVER);
        }

//        减库存，下订单，写入秒杀订单          事务
        OrderInfo orderInfo = miaoshaService.miaosha(user, goods);

        return Result.success(orderInfo);
    }
}

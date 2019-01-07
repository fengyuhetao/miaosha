package com.ht.miaosha.controller;

import com.ht.miaosha.entity.MiaoshaUser;
import com.ht.miaosha.entity.OrderInfo;
import com.ht.miaosha.redis.GoodsKey;
import com.ht.miaosha.redis.RedisService;
import com.ht.miaosha.result.CodeMsg;
import com.ht.miaosha.result.Result;
import com.ht.miaosha.service.GoodsService;
import com.ht.miaosha.service.MiaoshaUserService;
import com.ht.miaosha.service.OrderService;
import com.ht.miaosha.vo.GoodsDetailVo;
import com.ht.miaosha.vo.GoodsVo;
import com.ht.miaosha.vo.OrderDetailVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Created by hetao on 2018/12/28.
 */
@Controller
@RequestMapping("order")
public class OrderController {
    private static final Logger log = LoggerFactory.getLogger(GoodsController.class);

    @Autowired
    MiaoshaUserService userService;

    @Autowired
    GoodsService goodsService;

    @Autowired
    RedisService redisService;

    @Autowired
    OrderService orderService;

    @Autowired
    ThymeleafViewResolver thymeleafViewResolver;

    /**
     * @param user
     * @param goodsId
     * @return
     */
    @GetMapping(value = "/api/detail")
    @ResponseBody
    public Result<OrderDetailVo> info(MiaoshaUser user, @RequestParam("orderId")long orderId) {
        if(user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }

        OrderInfo orderInfo = orderService.getMiaoshaOrderById(orderId);

        if(orderInfo == null) {
            return Result.error(CodeMsg.ORDER_NOT_EXIST);
        }

        long goodsId = orderInfo.getGoodsId();
        GoodsVo goodsVo = goodsService.getGoodsVoByGoodsId(goodsId);

        OrderDetailVo orderDetailVo = new OrderDetailVo();
        orderDetailVo.setGoods(goodsVo);
        orderDetailVo.setOrderInfo(orderInfo);
        return Result.success(orderDetailVo);
    }
}

package com.ht.miaosha.controller;

import com.ht.miaosha.entity.MiaoshaOrder;
import com.ht.miaosha.entity.MiaoshaUser;
import com.ht.miaosha.entity.OrderInfo;
import com.ht.miaosha.rabbitmq.MQSender;
import com.ht.miaosha.rabbitmq.MiaoshaMessage;
import com.ht.miaosha.redis.GoodsKey;
import com.ht.miaosha.redis.RedisService;
import com.ht.miaosha.result.CodeMsg;
import com.ht.miaosha.result.Result;
import com.ht.miaosha.service.GoodsService;
import com.ht.miaosha.service.MiaoshaService;
import com.ht.miaosha.service.OrderService;
import com.ht.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by hetao on 2019/1/5.
 */
@Controller
@RequestMapping("/miaosha")
public class MiaoshaController implements InitializingBean {
    @Autowired
    private GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    MiaoshaService miaoshaService;

    @Autowired
    RedisService redisService;

    @Autowired
    MQSender mqSender;

    /**
     * 系统初始化
     *
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> goodsVoList = goodsService.getGoodsList();
        if (goodsVoList == null) {
            return;
        }

        for (GoodsVo goodsVo : goodsVoList) {
            redisService.set(GoodsKey.getMiaoshaGoodsStock, "" + goodsVo.getId(), goodsVo.getGoodsStock());
        }
    }

    @PostMapping("/do_miaosha")
    public String doMiaosha(Model model, MiaoshaUser user, @RequestParam("goodsId") long goodsId) {
        model.addAttribute("user", user);

        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);

        int stock = goods.getStockCount();

        if (stock <= 0) {
            model.addAttribute("errmsg", CodeMsg.MIAOSHA_OVER.getMsg());
            return "miaosha_fail";
        }

//        判断是否秒杀到了              这里也可能出现并发的问题
        MiaoshaOrder miaoshaOrder = orderService.getMiaoshaOrderByUserIdAndGoodsId(user.getId(), goodsId);

        if (miaoshaOrder != null) {
            model.addAttribute("errmsg", CodeMsg.REPEAT_MIAOSHA.getMsg());
            return "miaosha_fail";
        }

//        减库存，下订单，写入秒杀订单          事务
        OrderInfo orderInfo = miaoshaService.miaosha(user, goods);
        model.addAttribute("orderInfo", orderInfo);
        model.addAttribute("goods", goods);

        return "order_detail";
    }

    /**
     * 前后端分离版本
     *
     * @param user
     * @param goodsId
     * @return
     */
    @PostMapping("/api/do_miaosha")
    public Result doApiMiaosha(MiaoshaUser user, @RequestParam("goodsId") long goodsId) {
        if (user == null) {
            return Result.error(CodeMsg.MOBILE_NOT_EXIST);
        }

        //同一个用户能够秒杀多个商品: 可以通过设置索引，添加userid+goodsid 唯一索引
        //商品库存变为负值: 可以在执行sql语句的时候，设置要求 库存>0 的条件
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);

        int stock = goods.getStockCount();

        if (stock <= 0) {
            return Result.error(CodeMsg.MIAOSHA_OVER);
        }

//        判断是否秒杀到了
        MiaoshaOrder miaoshaOrder = orderService.getMiaoshaOrderByUserIdAndGoodsId(user.getId(), goodsId);

        if (miaoshaOrder != null) {
            return Result.error(CodeMsg.REPEAT_MIAOSHA);
        }

//        减库存，下订单，写入秒杀订单          事务
        OrderInfo orderInfo = miaoshaService.miaosha(user, goods);

        return Result.success(orderInfo);
    }

    /**
     * 前后端分离
     * 使用rabbit改进
     *
     * @param user
     * @param goodsId
     * @return
     */
    @PostMapping("/api/rabbit/do_miaosha")
    @ResponseBody
    public Result<Integer> doApiRabbitMiaosha(MiaoshaUser user, @RequestParam("goodsId") long goodsId) {
        if (user == null) {
            return Result.error(CodeMsg.MOBILE_NOT_EXIST);
        }

//        判断是否秒杀到了
        MiaoshaOrder miaoshaOrder = orderService.getMiaoshaOrderByUserIdAndGoodsId(user.getId(), goodsId);

        if (miaoshaOrder != null) {
            return Result.error(CodeMsg.REPEAT_MIAOSHA);
        }

        //        redis中预减库存
        long stock = redisService.decr(GoodsKey.getMiaoshaGoodsStock, "" + goodsId);
        if (stock < 0) {
            return Result.error(CodeMsg.MIAOSHA_OVER);
        }

        //        入队
        MiaoshaMessage miaoshaMessage = new MiaoshaMessage();
        miaoshaMessage.setUser(user);
        miaoshaMessage.setGoodsId(goodsId);
        mqSender.sendMiaoshaMessage(miaoshaMessage);

        return Result.success(0);             // 排队中
    }

    /**
     * 秒杀成功返回orderId
     * 秒杀失败 -1
     * 排队中: 0
     * @param user
     * @param goodsId
     * @return
     */
    @PostMapping("/api/rabbit/result")
    @ResponseBody
    public Result<Long> getApiRabbitMiaoshaResult(MiaoshaUser user, @RequestParam("goodsId") long goodsId) {
        if (user == null) {
            return Result.error(CodeMsg.MOBILE_NOT_EXIST);
        }

        long result = miaoshaService.getMiaoshaResult(user.getId(), goodsId);
        return Result.success(result);
    }
}

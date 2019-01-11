package com.ht.miaosha.controller;

import com.ht.miaosha.entity.MiaoshaOrder;
import com.ht.miaosha.entity.MiaoshaUser;
import com.ht.miaosha.entity.OrderInfo;
import com.ht.miaosha.rabbitmq.MQSender;
import com.ht.miaosha.rabbitmq.MiaoshaMessage;
import com.ht.miaosha.redis.GoodsKey;
import com.ht.miaosha.redis.MiaoshaKey;
import com.ht.miaosha.redis.RedisService;
import com.ht.miaosha.result.CodeMsg;
import com.ht.miaosha.result.Result;
import com.ht.miaosha.service.GoodsService;
import com.ht.miaosha.service.MiaoshaService;
import com.ht.miaosha.service.OrderService;
import com.ht.miaosha.util.MD5Util;
import com.ht.miaosha.util.UUIDUtil;
import com.ht.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private Map<Long, Boolean> localOverMap = new HashMap<>();

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
            localOverMap.put(goodsVo.getId(), false);
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
     * QPS: 1306
     * 5000 * 10
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
     * QPS: 2114
     * 5000 * 10
     * @param user
     * @param goodsId
     * @return
     */
    @PostMapping("/api/{path}/rabbit/do_miaosha")
    @ResponseBody
    public Result<Integer> doApiRabbitMiaosha(MiaoshaUser user, @RequestParam("goodsId") long goodsId, @PathVariable("path") String path) {
//TODO        存在问题，如果rabbit出现问题或者延迟过高，第一个用户连续点击13次秒杀，redis中库存就变为负值
//        内存标记，减少redis查询量
        boolean over = localOverMap.get(goodsId);
        if(over) {
            return Result.error(CodeMsg.MIAOSHA_OVER);
        }

//        验证path
        boolean check = miaoshaService.checkPath(user, goodsId, path);
        if(!check) {
            return Result.error(CodeMsg.REQUEST_ILLEGAL);
        }

//        redis中预减库存
        long stock = redisService.decr(GoodsKey.getMiaoshaGoodsStock, "" + goodsId);
        if (stock < 0) {
            localOverMap.put(goodsId, true);
            return Result.error(CodeMsg.MIAOSHA_OVER);
        }

//        判断是否秒杀到了
        MiaoshaOrder miaoshaOrder = orderService.getMiaoshaOrderByUserIdAndGoodsId(user.getId(), goodsId);

        if (miaoshaOrder != null) {
            return Result.error(CodeMsg.REPEAT_MIAOSHA);
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

    @GetMapping("/api/path")
    @ResponseBody
    public Result<String> getMiaoshaPath(MiaoshaUser user,
                                         @RequestParam("goodsId") long goodsId,
                                         @RequestParam("verifyCode") int verifyCode) {
        if(goodsId <= 0) {
            return null;
        }
        boolean check = miaoshaService.checkVerifyCode(user, goodsId, verifyCode);
        if(!check) {
            return Result.error(CodeMsg.REQUEST_ILLEGAL);
        }

        String path = miaoshaService.createMiaoshaPath(user, goodsId);


        return Result.success(path);
    }


    @GetMapping("/verifyCode")
    @ResponseBody
    public Result<String> getVerifyCode(HttpServletResponse response, MiaoshaUser user, @RequestParam("goodsId") long goodsId) {
        if(goodsId <= 0) {
            return null;
        }
        BufferedImage image = miaoshaService.createVerifyCode(user, goodsId);
        OutputStream outputStream = null;
        try {
            outputStream = response.getOutputStream();
            ImageIO.write(image, "JPEG", outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(CodeMsg.MIAOSHA_FAIL);
        }
        return null;
    }
}

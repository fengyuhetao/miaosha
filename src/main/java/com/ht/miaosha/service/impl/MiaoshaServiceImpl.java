package com.ht.miaosha.service.impl;

import com.ht.miaosha.dao.OrderDao;
import com.ht.miaosha.entity.MiaoshaOrder;
import com.ht.miaosha.entity.MiaoshaUser;
import com.ht.miaosha.entity.OrderInfo;
import com.ht.miaosha.redis.MiaoshaKey;
import com.ht.miaosha.redis.RedisService;
import com.ht.miaosha.service.GoodsService;
import com.ht.miaosha.service.MiaoshaService;
import com.ht.miaosha.service.OrderService;
import com.ht.miaosha.util.MD5Util;
import com.ht.miaosha.util.UUIDUtil;
import com.ht.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.transaction.Transactional;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * Created by hetao on 2019/1/5.
 */
@Service
public class MiaoshaServiceImpl implements MiaoshaService {
    @Autowired
    OrderDao orderDao;

    /**
     * 这里注意，提倡在自己的service引入对应的dao，如果需要引入其他dao，则选择引入其他dao对应的service
     * 所以这里不引入GoodsDao,而选择引入GoodsService
     */

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    RedisService redisService;

    @Transactional
    @Override
    public OrderInfo miaosha(MiaoshaUser user, GoodsVo goodsVo) {

        boolean isSuccess = goodsService.reduceStock(goodsVo);

        if(!isSuccess) {
            setGoodsOver(goodsVo.getId());
            return null;
        }

//        order_info miaosha_order
        return orderService.createOrder(user, goodsVo);
    }

    @Override
    public long getMiaoshaResult(long userId, long goodsId) {
        MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdAndGoodsId(userId, goodsId);
        if(order != null) {         // 秒杀成功
            return order.getOrderId();
        } else {
            boolean isOver = getGoodsOver(goodsId);
            if(isOver) {             // 秒杀结束
                return -1;
            } else {                 // 排队中
                return 0;
            }
        }
    }

    @Override
    public boolean checkPath(MiaoshaUser user, long goodsId, String path) {
        String pathOld = redisService.get(MiaoshaKey.getMiaoshaPath, "" + user.getId() + "_" + goodsId, String.class);
        return path.equals(pathOld);
    }

    @Override
    public String createMiaoshaPath(MiaoshaUser user, long goodsId) {
        String str = MD5Util.md5(UUIDUtil.uuid() + "123456");
        redisService.set(MiaoshaKey.getMiaoshaPath, ""+ user.getId() + "_" + goodsId, str);
        return str;
    }

    @Override
    public BufferedImage createVerifyCode(MiaoshaUser user, long goodsId) {
        int width = 80;
        int height = 32;
        //create the image
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        // set the background color
        g.setColor(new Color(0xDCDCDC));
        g.fillRect(0, 0, width, height);
        // draw the border
        g.setColor(Color.black);
        g.drawRect(0, 0, width - 1, height - 1);
        // create a random instance to generate the codes
        Random rdm = new Random();
        // make some confusion
        for (int i = 0; i < 50; i++) {
            int x = rdm.nextInt(width);
            int y = rdm.nextInt(height);
            g.drawOval(x, y, 0, 0);
        }
        // generate a random code
        String verifyCode = generateVerifyCode(rdm);
        g.setColor(new Color(0, 100, 0));
        g.setFont(new Font("Candara", Font.BOLD, 24));
        g.drawString(verifyCode, 8, 24);
        g.dispose();
        //把验证码存到redis中
        int rnd = calc(verifyCode);
        redisService.set(MiaoshaKey.getMiaoshaVerifyCode, user.getId()+","+goodsId, rnd);
        //输出图片
        return image;
    }

    private static int calc(String exp) {
        try {
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine engine = manager.getEngineByName("JavaScript");
            return (Integer)engine.eval(exp);
        }catch(Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    private static char[] ops = new char[] {'+', '-', '*'};
    /**
     * + - *
     * */
    private String generateVerifyCode(Random rdm) {
        int num1 = rdm.nextInt(10);
        int num2 = rdm.nextInt(10);
        int num3 = rdm.nextInt(10);
        char op1 = ops[rdm.nextInt(3)];
        char op2 = ops[rdm.nextInt(3)];
        return ""+ num1 + op1 + num2 + op2 + num3;
    }

    public static void main(String [] args) {
        System.out.println(calc("1+3-8"));
    }

    @Override
    public boolean checkVerifyCode(MiaoshaUser user, long goodsId, int verifyCode) {
        Integer codeOld = redisService.get(MiaoshaKey.getMiaoshaVerifyCode, user.getId() + "," + goodsId, Integer.class);
        if(codeOld == null || codeOld - verifyCode != 0 ) {
            return false;
        }
        redisService.delete(MiaoshaKey.getMiaoshaVerifyCode, user.getId() + "," + goodsId);
        return true;
    }

    private void setGoodsOver(Long goodsId) {
        redisService.set(MiaoshaKey.isGoodsOver, ""+goodsId, true);
    }

    private boolean getGoodsOver(Long goodsId) {
        return redisService.exists(MiaoshaKey.isGoodsOver, ""+goodsId);
    }
}

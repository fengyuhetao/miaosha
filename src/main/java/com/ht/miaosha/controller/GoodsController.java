package com.ht.miaosha.controller;

import com.ht.miaosha.entity.MiaoshaUser;
import com.ht.miaosha.redis.GoodsKey;
import com.ht.miaosha.redis.RedisService;
import com.ht.miaosha.result.Result;
import com.ht.miaosha.service.GoodsService;
import com.ht.miaosha.service.MiaoshaUserService;
import com.ht.miaosha.vo.GoodsDetailVo;
import com.ht.miaosha.vo.GoodsVo;
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
@RequestMapping("goods")
public class GoodsController {
    private static final Logger log = LoggerFactory.getLogger(GoodsController.class);

    @Autowired
    MiaoshaUserService userService;

    @Autowired
    GoodsService goodsService;

    @Autowired
    RedisService redisService;

    @Autowired
    ThymeleafViewResolver thymeleafViewResolver;

    /**
     * 未优化
     * QPS 167
     * 1000 * 10
     * <p>
     * 该功能在真实环境中往往会采用分页的形式展示
     * 往往只会缓存前几页
     * 针对该功能的缓存时间往往比较短
     * </p>
     * 添加页面缓存
     * QPS 743
     * 1000 * 10
     * @param model
     * @param user
     * @return
     */
    @GetMapping(value = "/to_list", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String toList(HttpServletRequest request, HttpServletResponse response, Model model, MiaoshaUser user) {
        log.info(user.toString());
        model.addAttribute("user", user);

//        取缓存
        String html = redisService.get(GoodsKey.getGoodsList, "", String.class);
        if(!StringUtils.isEmpty(html)) {
            return html;
        }

        List<GoodsVo> goodsVoList = goodsService.getGoodsList();
        model.addAttribute("goodsList", goodsVoList);
//        return "goods_list";

//        手动渲染
        WebContext context = new WebContext(request, response, request.getServletContext(),
                request.getLocale(), model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goods_list", context);
        if(!StringUtils.isEmpty(html)) {
//            缓存页面
            redisService.set(GoodsKey.getGoodsList, "", html);
        }

        return html;

    }

    /**
     * 别忘了设置编码UTF-8，不然会出问题。。
     * QPS 295
     * 1000 * 10
     *
     * 添加缓存后
     * QPS 813
     * 1000 * 10
     * @param request
     * @param response
     * @param model
     * @param user
     * @param goodsId
     * @return
     */
    @GetMapping(value = "/to_detail/{goodsId}", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String toDetail(HttpServletRequest request, HttpServletResponse response, Model model, MiaoshaUser user, @PathVariable("goodsId")long goodsId) {
        model.addAttribute("user", user);

        //        取缓存
        String html = redisService.get(GoodsKey.getGoodsDetail, String.valueOf(goodsId), String.class);
        if(!StringUtils.isEmpty(html)) {
            return html;
        }

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

//        return "goods_detail";

        //        手动渲染
        WebContext context = new WebContext(request, response, request.getServletContext(),
                request.getLocale(), model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goods_detail", context);
        if(!StringUtils.isEmpty(html)) {
//            缓存页面
            redisService.set(GoodsKey.getGoodsDetail, String.valueOf(goodsId), html);
        }

        return html;
    }

    /**
     * @param user
     * @param goodsId
     * @return
     */
    @GetMapping(value = "/api/to_detail/{goodsId}")
    @ResponseBody
    public Result<GoodsDetailVo> toDetailApi(HttpServletRequest request, MiaoshaUser user, @PathVariable("goodsId")long goodsId) {
        System.out.println(request.getRequestURI());
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

        GoodsDetailVo goodsDetailVo = new GoodsDetailVo();
        goodsDetailVo.setUser(user);
        goodsDetailVo.setMiaoshaStatus(miaoshaStatus);
        goodsDetailVo.setRemainSeconds(remainSeconds);
        goodsDetailVo.setGoods(goods);
        return Result.success(goodsDetailVo);
    }
}

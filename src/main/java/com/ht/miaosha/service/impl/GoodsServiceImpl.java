package com.ht.miaosha.service.impl;

import com.ht.miaosha.dao.GoodsDao;
import com.ht.miaosha.entity.Goods;
import com.ht.miaosha.entity.MiaoshaGoods;
import com.ht.miaosha.service.GoodsService;
import com.ht.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by hetao on 2019/1/4.
 */
@Service
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    GoodsDao goodsDao;

    @Override
    public List<GoodsVo> getGoodsList() {
        return goodsDao.getGoodsVoList();
    }

    @Override
    public GoodsVo getGoodsVoByGoodsId(long goodsId) {
        return goodsDao.getGoodsVoByGoodsId(goodsId);
    }

    @Override
    public boolean reduceStock(GoodsVo good) {
        //        减少库存
        MiaoshaGoods g = new MiaoshaGoods();
        g.setGoodsId(good.getId());
        g.setStockCount(good.getGoodsStock() - 1);
        int ret = goodsDao.reduceStock(g);
        return ret > 0;
    }
}

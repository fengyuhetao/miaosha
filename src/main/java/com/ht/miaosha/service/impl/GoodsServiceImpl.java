package com.ht.miaosha.service.impl;

import com.ht.miaosha.dao.GoodsDao;
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
}

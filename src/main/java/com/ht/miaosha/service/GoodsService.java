package com.ht.miaosha.service;

import com.ht.miaosha.vo.GoodsVo;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by hetao on 2019/1/4.
 */
public interface GoodsService {
    List<GoodsVo> getGoodsList();
}

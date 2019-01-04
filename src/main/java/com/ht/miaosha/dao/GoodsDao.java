package com.ht.miaosha.dao;

import com.ht.miaosha.vo.GoodsVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Created by hetao on 2019/1/4.
 */
@Mapper
public interface GoodsDao {
    String main_tableName = "miaosha_goods";

    String sub_tableName = "goods";

    @Select("select t2.*, t1.stock_count, t1.start_date, t1.end_date, t1.miaosha_price from " + main_tableName + " t1 left join "
            + sub_tableName + " t2 on t1.goods_id = t2.id")
    List<GoodsVo> getGoodsVoList();
}

package com.ht.miaosha.dao;

import com.ht.miaosha.entity.MiaoshaOrder;

import com.ht.miaosha.entity.OrderInfo;
import org.apache.ibatis.annotations.*;


/**
 * Created by hetao on 2019/1/5.
 */
@Mapper
public interface OrderDao {

    @Select("select * from miaosha_order where user_id = #{userId} and goods_id = #{goodsId}")
    MiaoshaOrder getMiaoshaOrderByUserIdAndGoodsId(@Param("userId") long userId, @Param("goodsId") long goodsId);

    @Insert("insert into order_info(user_id, goods_id, goods_name, goods_count, goods_price, goods_channel, status, create_date) " +
            "values(#{userId}, #{goodsId}, #{goodsName}, #{goodsCount}, #{goodsPrice}, #{goodsChannel}, #{status}, #{createDate}) ")
    @SelectKey(keyColumn = "id", keyProperty = "id", resultType = long.class, before = false, statement = "select last_insert_id()")
    long insert(OrderInfo orderInfo);

    @Insert("insert into miaosha_order(user_id, goods_id, order_id) values(#{userId}, #{goodsId}, #{goodsId)")
    void insertMiaoshaOrder(MiaoshaOrder miaoshaOrder);
}

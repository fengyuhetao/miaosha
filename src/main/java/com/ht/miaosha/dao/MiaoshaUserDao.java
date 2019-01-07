package com.ht.miaosha.dao;

import com.ht.miaosha.entity.MiaoshaUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * Created by hetao on 2018/12/27.
 */
@Mapper
public interface MiaoshaUserDao {
    String tableName = "miaoshauser";

    @Select("select * from " + tableName + " where id = #{id}")
    MiaoshaUser getById(@Param("id") long id);

    @Update("update miaosha_user set password = #{password} where id = #{id}")
    void update(MiaoshaUser toBeUpdate);
}

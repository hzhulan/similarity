package com.hzhu.dao;

import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @Description TODO
 * @Date 2019/8/27 13:50
 * @Created by CZB
 */
public interface CarMapper {

    @Select("select name from car_model")
    List<String> getModelName();
}

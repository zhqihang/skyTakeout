package com.sky.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author: qihang
 * @CreateDate: 2023/11/16/ 9:12
 * @Project: sky-take-out
 * @Description: ......
 */

@Mapper
public interface SetmealDishMapper {

    /**
     * 根据菜品id查询对应的套餐id
     * @return
     * @param dishIds
     */
    List<Long> getSetmealIdsByDishIds(List<Long> dishIds);

}

package com.sky.service;

import com.sky.dto.DishDTO;

/**
 * @author: qihang
 * @CreateDate: 2023/11/15/ 20:05
 * @Project: sky-take-out
 * @Description:
 */


public interface DishService {

    /**
     * 新增菜品和对应的口味数据
     * @param dishDTO
     */
    public void saveWithFlavor(DishDTO dishDTO);

}

package com.sky.service.impl;

import com.sky.dto.DishDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author: qihang
 * @CreateDate: 2023/11/15/ 20:06
 * @Project: sky-take-out
 * @Description: ......
 */

@Service
@Slf4j
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private DishFlavorMapper dishFlavorMapper;


    /**
     * 新增菜品及对应口味
     * @param dishDTO
     */
    @Transactional //事务注解 保证数据一致性
    public void saveWithFlavor(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        // 向菜品表插入1条数据
        dishMapper.insert(dish);


        // 获取insert语句生成的主键值
        Long id = dish.getId(); //需要再mapper设置主键返回 才能get到

        List<DishFlavor> flavors = dishDTO.getFlavors(); //口味集合
        if (flavors != null && flavors.size() > 0){
            // 遍历设置口味所属菜品的id
            flavors.forEach(dishFlavor -> dishFlavor.setDishId(id));
            // 向口味表插入n条数据(sql批量)
            dishFlavorMapper.insertBatch(flavors); //此时dishId还拿不到
        }

    }


}

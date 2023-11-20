package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.entity.Setmeal;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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

    @Autowired
    private SetmealDishMapper setmealDishMapper;

    @Autowired
    private SetmealMapper setmealMapper;


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
            dishFlavorMapper.insertBatch(flavors); //这里的dishId 需要在mapper中设置主键返回
        }

    }

    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(),dishPageQueryDTO.getPageSize());

        // vo: 进行页面展示数据的封装
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);
        return new PageResult(page.getTotal(),page.getResult());
    }

    /**
     * 批量删除菜品
     * @param ids
     */
    @Transactional
    public void deleteBatch(List<Long> ids) {
        //判断当前菜品是否能够删除--是否存在起售中的菜品
        for (Long id : ids) {
            Dish dish = dishMapper.getById(id);
            if (dish.getStatus() == StatusConstant.ENABLE){
                //当前菜品处于起售中
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }
        //判断当前菜品是否能够删除--是否被套餐关联
        List<Long> setmealIds = setmealDishMapper.getSetmealIdsByDishIds(ids);
        if (setmealIds != null && setmealIds.size() >0){
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }
        /*
        //删除菜品表中的菜品数据
        for (Long id : ids) {
            dishMapper.deleteById(id);
            //删除菜品关联的口味数据
            dishFlavorMapper.deleteByDishId(id);
        }
         */

        //优化删除逻辑: 批量删除
        //sql: delete from dish where id in (?, ?, ?)
        dishMapper.deleteByIds(ids);
        //sql: delete from dish_flavor where dish_id in (?, ?, ?)
        dishFlavorMapper.deleteByDishIds(ids);
    }

    /**
     * 根据id查询菜品和对应的口味数据
     * @param id
     * @return
     */
    public DishVO getByIdWithFlavor(Long id) {
        //根据id查菜品数据
        Dish dish = dishMapper.getById(id);

        //根据菜品id查口味数据
        List<DishFlavor> dishFlavors = dishFlavorMapper.getByDishId(id); //一个菜品可能对应多个口味

        //将查询到的数据封装到VO返回前端
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish,dishVO);
        dishVO.setFlavors(dishFlavors);
        return dishVO;
    }


    /**
     * 修改菜品基本信息和口味信息
     * @param dishDTO
     */
    public void updateWithFlavor(DishDTO dishDTO) {
        //修改菜品表基本信息
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        dishMapper.update(dish);

        //修改口味的场景比较复杂,这里考虑: 修改 = 清空(删除) + 重新插入
        //删除原有的口味数据
        dishFlavorMapper.deleteByDishId(dishDTO.getId());
        //重新插入口味数据
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && flavors.size() > 0){
            // 遍历设置口味所属菜品的id
            flavors.forEach(dishFlavor -> dishFlavor.setDishId(dishDTO.getId()));
            // 向口味表插入n条数据(sql批量)
            dishFlavorMapper.insertBatch(flavors);
        }
    }

    /**
     * 修改菜品起售状态
     * @param id
     * @param status
     */
    public void updateStatus(Long id, Integer status) {
        //获取菜品
        // Dish dish = dishMapper.getById(id);
        //     //修改菜品状态
        //     dish.setStatus(status);
        //     dishMapper.update(dish);
        // if (status == StatusConstant.DISABLE){//起售-->停售
        //     //判断当前菜品是否被套餐关联
        //     List<Setmeal> setmeals = setmealDishMapper.getSetmealIdsByDishId(id);
        //     if (setmeals != null && setmeals.size() >0){
        //         //套餐同时停售
        //         setmeals.forEach(setmeal -> setmeal.setStatus(status));
        //         setmealMapper.update(setmeal);
        //     }
        // }

        //建造者模式 耦合低
        Dish dish = Dish.builder()
                .id(id)
                .status(status)
                .build();
        dishMapper.update(dish);

        if (status == StatusConstant.DISABLE) {
            // 如果是停售操作，还需要将包含当前菜品的套餐也停售
            List<Long> dishIds = new ArrayList<>();
            dishIds.add(id);
            // select setmeal_id from setmeal_dish where dish_id in (?,?,?)
            List<Long> setmealIds = setmealDishMapper.getSetmealIdsByDishIds(dishIds);
            if (setmealIds != null && setmealIds.size() > 0) {
                for (Long setmealId : setmealIds) {
                    Setmeal setmeal = Setmeal.builder()
                            .id(setmealId)
                            .status(StatusConstant.DISABLE)
                            .build();
                    setmealMapper.update(setmeal);
                }
            }
        }
    }

    /**
     * 根据分类id查询菜品
     * @param categoryId
     * @return
     */
    public List<Dish> getList(Long categoryId) {
        Dish dish = Dish.builder()
                .categoryId(categoryId)
                .status(StatusConstant.ENABLE)
                .build();
        List<Dish> dishList = dishMapper.getList(dish);
        return dishList;
    }

}

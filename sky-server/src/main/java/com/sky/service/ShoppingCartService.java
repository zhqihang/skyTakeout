package com.sky.service;

import com.sky.dto.ShoppingCartDTO;

/**
 * @author: qihang
 * @CreateDate: 2023/11/23/ 10:19
 * @Project: sky-take-out
 * @Description: ......
 */
public interface ShoppingCartService {

    /**
     * 添加购物车
     * @param shoppingCartDTO
     */
    void addShoppingCart(ShoppingCartDTO shoppingCartDTO);
}

package com.sky.dto;

import lombok.Data;
import java.io.Serializable;

@Data
public class ShoppingCartDTO implements Serializable {

    private Long dishId; //菜品
    private Long setmealId; //套餐
    private String dishFlavor; //口味

}

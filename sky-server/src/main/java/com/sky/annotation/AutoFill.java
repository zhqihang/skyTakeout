package com.sky.annotation;

import com.sky.enumeration.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author: qihang
 * @CreateDate: 2023/11/15/ 15:09
 * @Project: sky-take-out
 * @Description: 自定义注解, 用于标识某个方法需要进行功能字段的自动填充处理
 */


@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoFill {

    //指定数据库操作类型: UPDATE INSERT
    OperationType value();

}

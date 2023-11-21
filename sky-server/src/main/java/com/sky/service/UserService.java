package com.sky.service;

import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;

/**
 * @author: qihang
 * @CreateDate: 2023/11/21/ 16:09
 * @Project: sky-take-out
 * @Description: 用户相关接口
 */

public interface UserService {

    /**
     * 微信登录
     * @param userLoginDTO
     * @return
     */
    User wxLogin(UserLoginDTO userLoginDTO);


}

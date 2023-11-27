package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * @author: qihang
 * @CreateDate: 2023/11/21/ 16:45
 * @Project: sky-take-out
 * @Description: ......
 */
@Mapper
public interface UserMapper {

    /**
     * 根据openid查询用户
     * @param openid
     * @return
     */
    @Select("select * from user where openid = #{openid}")
    User getByOpenid(String openid);


    /**
     * 写入用户
     * @param user
     */
    void insert(User user);

    @Select("select * from user where id = #{id}")
    User getById(Long userId);
}

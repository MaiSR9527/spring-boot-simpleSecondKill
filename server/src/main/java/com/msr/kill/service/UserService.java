package com.msr.kill.service;

import com.msr.kill.entity.User;

/**
 * @Description:
 * @Author: maishuren
 * @Date: 2019/9/5
 */
public interface UserService {


    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

}

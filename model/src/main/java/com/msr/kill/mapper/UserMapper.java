package com.msr.kill.mapper;

import com.msr.kill.entity.User;
import org.apache.ibatis.annotations.Param;

/**
 * @Description:
 * @Author: maishuren
 * @Date: 2019/9/5
 */
public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    User selectByUserName(@Param("userName") String userName);

    User selectByUserNamePsd(@Param("userName") String userName, @Param("password") String password);
}
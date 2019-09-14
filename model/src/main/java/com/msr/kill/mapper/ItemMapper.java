package com.msr.kill.mapper;

import com.msr.kill.entity.Item;

/**
 * @Description:
 * @Author: maishuren
 * @Date: 2019/9/5
 */
public interface ItemMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Item record);

    int insertSelective(Item record);

    Item selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Item record);

    int updateByPrimaryKey(Item record);
}
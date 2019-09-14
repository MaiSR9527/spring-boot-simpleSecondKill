package com.msr.kill.service;

import com.msr.kill.entity.Item;
import com.msr.kill.entity.ItemKill;

import java.util.List;

/**
 * @Description:
 * @Author: maishuren
 * @Date: 2019/9/5
 */
public interface ItemService {


    int deleteByPrimaryKey(Integer id);

    int insert(Item record);

    int insertSelective(Item record);

    Item selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Item record);

    int updateByPrimaryKey(Item record);

}

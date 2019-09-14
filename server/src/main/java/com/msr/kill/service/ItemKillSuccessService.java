package com.msr.kill.service;

import com.msr.kill.entity.ItemKillSuccess;

/**
 * @Description:
 * @Author: maishuren
 * @Date: 2019/9/5
 */
public interface ItemKillSuccessService {


    int deleteByPrimaryKey(String code);

    int insert(ItemKillSuccess record);

    int insertSelective(ItemKillSuccess record);

    ItemKillSuccess selectByPrimaryKey(String code);

    int updateByPrimaryKeySelective(ItemKillSuccess record);

    int updateByPrimaryKey(ItemKillSuccess record);


}

package com.msr.kill.service;

import com.msr.kill.entity.ItemKill;

import java.util.List;

/**
 * @Description:
 * @Author: maishuren
 * @Date: 2019/9/5
 */
public interface ItemKillService {


    int deleteByPrimaryKey(Integer id);

    int insert(ItemKill record);

    int insertSelective(ItemKill record);

    ItemKill selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ItemKill record);

    int updateByPrimaryKey(ItemKill record);

    List<ItemKill> getKillItems();

    ItemKill getKillById(Integer id);

    Boolean killItem(Integer killId, Integer userId) throws Exception;

    Boolean killItemV2(Integer killId, Integer userId) throws Exception;

    Boolean killItemV3(Integer killId, Integer userId) throws Exception;

    Boolean killItemV4(Integer killId, Integer userId) throws Exception;

    Boolean killItemV5(Integer killId, Integer userId) throws Exception;
}

package com.msr.kill.mapper;

import com.msr.kill.entity.ItemKill;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description:
 * @Author: maishuren
 * @Date: 2019/9/5
 */
public interface ItemKillMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ItemKill record);

    int insertSelective(ItemKill record);

    ItemKill selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ItemKill record);

    int updateByPrimaryKey(ItemKill record);

    List<ItemKill> selectAll();

    ItemKill selectById(@Param("id") Integer id);

    int updateKillItem(@Param("killId") Integer killId);



    ItemKill selectByIdV2(@Param("id") Integer id);

    int updateKillItemV2(@Param("killId") Integer killId);
}
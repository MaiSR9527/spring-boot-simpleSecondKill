package com.msr.kill.service.impl;

import com.msr.kill.entity.ItemKillSuccess;
import com.msr.kill.mapper.ItemKillSuccessMapper;
import com.msr.kill.service.ItemKillSuccessService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Description:
 * @Author: maishuren
 * @Date: 2019/9/5
 */
@Service
public class ItemKillSuccessServiceImpl implements ItemKillSuccessService {

    @Resource
    private ItemKillSuccessMapper itemKillSuccessMapper;

    @Override
    public int deleteByPrimaryKey(String code) {
        return itemKillSuccessMapper.deleteByPrimaryKey(code);
    }

    @Override
    public int insert(ItemKillSuccess record) {
        return itemKillSuccessMapper.insert(record);
    }

    @Override
    public int insertSelective(ItemKillSuccess record) {
        return itemKillSuccessMapper.insertSelective(record);
    }

    @Override
    public ItemKillSuccess selectByPrimaryKey(String code) {
        return itemKillSuccessMapper.selectByPrimaryKey(code);
    }

    @Override
    public int updateByPrimaryKeySelective(ItemKillSuccess record) {
        return itemKillSuccessMapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public int updateByPrimaryKey(ItemKillSuccess record) {
        return itemKillSuccessMapper.updateByPrimaryKey(record);
    }

}

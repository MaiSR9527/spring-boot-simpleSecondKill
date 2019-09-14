package com.msr.kill.service.impl;

import com.msr.kill.entity.Item;
import com.msr.kill.entity.ItemKill;
import com.msr.kill.mapper.ItemMapper;
import com.msr.kill.service.ItemService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Description:
 * @Author: maishuren
 * @Date: 2019/9/5
 */
@Service
public class ItemServiceImpl implements ItemService {

    @Resource
    private ItemMapper itemMapper;

    @Override
    public int deleteByPrimaryKey(Integer id) {
        return itemMapper.deleteByPrimaryKey(id);
    }

    @Override
    public int insert(Item record) {
        return itemMapper.insert(record);
    }

    @Override
    public int insertSelective(Item record) {
        return itemMapper.insertSelective(record);
    }

    @Override
    public Item selectByPrimaryKey(Integer id) {
        return itemMapper.selectByPrimaryKey(id);
    }

    @Override
    public int updateByPrimaryKeySelective(Item record) {
        return itemMapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public int updateByPrimaryKey(Item record) {
        return itemMapper.updateByPrimaryKey(record);
    }

}

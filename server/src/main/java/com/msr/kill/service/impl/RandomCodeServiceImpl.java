package com.msr.kill.service.impl;

import com.msr.kill.entity.RandomCode;
import com.msr.kill.mapper.RandomCodeMapper;
import com.msr.kill.service.RandomCodeService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Description:
 * @Author: maishuren
 * @Date: 2019/9/5
 */
@Service
public class RandomCodeServiceImpl implements RandomCodeService {

    @Resource
    private RandomCodeMapper randomCodeMapper;

    @Override
    public int deleteByPrimaryKey(Integer id) {
        return randomCodeMapper.deleteByPrimaryKey(id);
    }

    @Override
    public int insert(RandomCode record) {
        return randomCodeMapper.insert(record);
    }

    @Override
    public int insertSelective(RandomCode record) {
        return randomCodeMapper.insertSelective(record);
    }

    @Override
    public RandomCode selectByPrimaryKey(Integer id) {
        return randomCodeMapper.selectByPrimaryKey(id);
    }

    @Override
    public int updateByPrimaryKeySelective(RandomCode record) {
        return randomCodeMapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public int updateByPrimaryKey(RandomCode record) {
        return randomCodeMapper.updateByPrimaryKey(record);
    }

}

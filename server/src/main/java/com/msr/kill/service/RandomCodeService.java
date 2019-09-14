package com.msr.kill.service;

import com.msr.kill.entity.RandomCode;

/**
 * @Description:
 * @Author: maishuren
 * @Date: 2019/9/5
 */
public interface RandomCodeService {


    int deleteByPrimaryKey(Integer id);

    int insert(RandomCode record);

    int insertSelective(RandomCode record);

    RandomCode selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(RandomCode record);

    int updateByPrimaryKey(RandomCode record);

}

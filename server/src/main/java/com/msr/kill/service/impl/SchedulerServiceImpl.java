package com.msr.kill.service.impl;

import com.msr.kill.entity.ItemKillSuccess;
import com.msr.kill.mapper.ItemKillSuccessMapper;
import com.msr.kill.service.SchedulerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description:
 * @Author: maishuren
 * @Date: 2019/9/14 15:41
 */
@Service
@Slf4j
public class SchedulerServiceImpl implements SchedulerService {

    @Autowired
    private ItemKillSuccessMapper itemKillSuccessMapper;

    @Autowired
    private Environment env;


    /**
     * 定时获取status=0的订单并判断是否超过TTL，然后进行失效
     * Scheduled(cron = "0/10 * * * * ?")
     */
    @Override
    @Scheduled(cron = "0 0/30 * * * ?")
    public void schedulerExpireOrders() {
        log.info("v1的定时任务----");
        try {
            List<ItemKillSuccess> list = itemKillSuccessMapper.selectExpireOrders();
            if (list != null && !list.isEmpty()) {
                //java8的写法
                list.stream().forEach(i -> {
                    if (i != null && i.getDiffTime() > env.getProperty("scheduler.expire.orders.time", Integer.class)) {
                        itemKillSuccessMapper.expireOrder(i.getCode());
                    }
                });
            }

            /*for (ItemKillSuccess entity:list){
            }*/ //非java8的写法
        } catch (Exception e) {
            log.error("定时获取status=0的订单并判断是否超过TTL，然后进行失效-发生异常：", e.fillInStackTrace());
        }
    }
}

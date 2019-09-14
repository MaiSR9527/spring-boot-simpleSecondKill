package com.msr.kill.service.impl;

import com.msr.kill.entity.ItemKill;
import com.msr.kill.entity.ItemKillSuccess;
import com.msr.kill.enums.SysConstant;
import com.msr.kill.mapper.ItemKillMapper;
import com.msr.kill.mapper.ItemKillSuccessMapper;
import com.msr.kill.service.ItemKillService;
import com.msr.kill.service.RabbitSenderService;
import com.msr.kill.utils.RandomIdUtil;
import com.msr.kill.utils.SnowFlake;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.joda.time.DateTime;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Description:
 * @Author: maishuren
 * @Date: 2019/9/5
 */
@Service
@Slf4j
public class ItemKillServiceImpl implements ItemKillService {

    private static final String PATH_PREFIX = "/kill/zkLock/";
    private final StringRedisTemplate stringRedisTemplate;
    private final RedissonClient redissonClient;
    private final CuratorFramework curatorFramework;
    private final RabbitSenderService rabbitSenderService;
    @Resource
    private ItemKillMapper itemKillMapper;
    @Resource
    private ItemKillSuccessMapper itemKillSuccessMapper;
    /**
     * 雪花算法生成分布式唯一id
     */
    private SnowFlake snowFlake = new SnowFlake(2, 3);

    public ItemKillServiceImpl(StringRedisTemplate stringRedisTemplate, RedissonClient redissonClient, CuratorFramework curatorFramework, RabbitSenderService rabbitSenderService) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.redissonClient = redissonClient;
        this.curatorFramework = curatorFramework;
        this.rabbitSenderService = rabbitSenderService;
    }

    @Override
    public int deleteByPrimaryKey(Integer id) {
        return itemKillMapper.deleteByPrimaryKey(id);
    }

    @Override
    public int insert(ItemKill record) {
        return itemKillMapper.insert(record);
    }

    @Override
    public int insertSelective(ItemKill record) {
        return itemKillMapper.insertSelective(record);
    }

    @Override
    public ItemKill selectByPrimaryKey(Integer id) {
        return itemKillMapper.selectByPrimaryKey(id);
    }

    @Override
    public int updateByPrimaryKeySelective(ItemKill record) {
        return itemKillMapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public int updateByPrimaryKey(ItemKill record) {
        return itemKillMapper.updateByPrimaryKey(record);
    }

    @Override
    public List<ItemKill> getKillItems() {
        return itemKillMapper.selectAll();
    }

    @Override
    public ItemKill getKillById(Integer id) {
        ItemKill itemKill = itemKillMapper.selectById(id);
        if (itemKill == null) {
            throw new RuntimeException("获取的待秒杀商品不存在");
        }
        return itemKill;
    }

    @Override
    public Boolean killItem(Integer killId, Integer userId) throws Exception {
        boolean result = false;

        //TODO:判断当前用户是否已经抢购过当前商品
        if (itemKillSuccessMapper.countByKillUserId(killId, userId) <= 0) {
            //TODO:查询待秒杀商品详情
            ItemKill itemKill = itemKillMapper.selectById(killId);

            //TODO:判断是否可以被秒杀canKill=1?
            if (itemKill != null && 1 == itemKill.getCanKill()) {
                //TODO:扣减库存-减一
                int res = itemKillMapper.updateKillItem(killId);

                //TODO:扣减是否成功?是-生成秒杀成功的订单，同时通知用户秒杀成功的消息
                if (res > 0) {
                    commonRecordKillSuccessInfo(itemKill, userId);
                    result = true;
                }
            }
        } else {
            throw new Exception("您已经抢购过该商品了!");
        }
        return result;
    }

    /**
     * 商品秒杀核心业务逻辑的处理-mysql的优化
     *
     * @param killId 秒杀商品id
     * @param userId 用户id
     * @return 返回结果
     * @throws Exception 抛出异常
     */
    @Override
    public Boolean killItemV2(Integer killId, Integer userId) throws Exception {
        Boolean result = false;

        //TODO:判断当前用户是否已经抢购过当前商品
        if (itemKillSuccessMapper.countByKillUserId(killId, userId) <= 0) {
            //TODO:A.查询待秒杀商品详情
            ItemKill itemKill = itemKillMapper.selectByIdV2(killId);

            //TODO:判断是否可以被秒杀canKill=1?
            if (itemKill != null && 1 == itemKill.getCanKill() && itemKill.getTotal() > 0) {
                //TODO:B.扣减库存-减一
                int res = itemKillMapper.updateKillItemV2(killId);

                //TODO:扣减是否成功?是-生成秒杀成功的订单，同时通知用户秒杀成功的消息
                if (res > 0) {
                    commonRecordKillSuccessInfo(itemKill, userId);

                    result = true;
                }
            }
        } else {
            throw new Exception("您已经抢购过该商品了!");
        }
        return result;
    }

    /**
     * 商品秒杀核心业务逻辑的处理-redis的分布式锁
     *
     * @param killId 秒杀商品id
     * @param userId 用户id
     * @return 返回结果
     * @throws Exception 抛出异常
     */
    @Override
    public Boolean killItemV3(Integer killId, Integer userId) throws Exception {
        Boolean result = false;

        if (itemKillSuccessMapper.countByKillUserId(killId, userId) <= 0) {

            //TODO:借助Redis的原子操作实现分布式锁-对共享操作-资源进行控制
            ValueOperations valueOperations = stringRedisTemplate.opsForValue();
            final String key = new StringBuffer().append(killId).append(userId).append("-RedisLock").toString();
            final String value = RandomIdUtil.generateOrderCode();
            //luna脚本提供“分布式锁服务”，就可以写在一起
            Boolean cacheRes = valueOperations.setIfAbsent(key, value);
            //TODO:redis部署节点宕机了
            if (cacheRes) {
                stringRedisTemplate.expire(key, 30, TimeUnit.SECONDS);

                try {
                    ItemKill itemKill = itemKillMapper.selectByIdV2(killId);
                    if (itemKill != null && 1 == itemKill.getCanKill() && itemKill.getTotal() > 0) {
                        int res = itemKillMapper.updateKillItemV2(killId);
                        if (res > 0) {
                            commonRecordKillSuccessInfo(itemKill, userId);

                            result = true;
                        }
                    }
                } catch (Exception e) {
                    throw new Exception("还没到抢购日期、已过了抢购时间或已被抢购完毕！");
                } finally {
                    if (value.equals(valueOperations.get(key).toString())) {
                        stringRedisTemplate.delete(key);
                    }
                }
            }
        } else {
            throw new Exception("Redis-您已经抢购过该商品了!");
        }
        return result;
    }

    /**
     * 商品秒杀核心业务逻辑的处理-redisson的分布式锁
     *
     * @param killId 秒杀商品id
     * @param userId 用户id
     * @return 返回结果
     * @throws Exception 抛出异常
     */
    @Override
    public Boolean killItemV4(Integer killId, Integer userId) throws Exception {
        Boolean result = false;

        final String lockKey = new StringBuffer().append(killId).append(userId).append("-RedissonLock").toString();
        RLock lock = redissonClient.getLock(lockKey);

        try {
            Boolean cacheRes = lock.tryLock(30, 10, TimeUnit.SECONDS);
            if (cacheRes) {
                //TODO:核心业务逻辑的处理
                if (itemKillSuccessMapper.countByKillUserId(killId, userId) <= 0) {
                    ItemKill itemKill = itemKillMapper.selectByIdV2(killId);
                    if (itemKill != null && 1 == itemKill.getCanKill() && itemKill.getTotal() > 0) {
                        int res = itemKillMapper.updateKillItemV2(killId);
                        if (res > 0) {
                            commonRecordKillSuccessInfo(itemKill, userId);

                            result = true;
                        }
                    }
                } else {
                    throw new Exception("redisson-您已经抢购过该商品了!");
                }
            }
        } finally {
            lock.unlock();
        }
        return result;
    }

    /**
     * 商品秒杀核心业务逻辑的处理-基于ZooKeeper的分布式锁
     *
     * @param killId 秒杀商品id
     * @param userId 用户id
     * @return 返回结果
     * @throws Exception 抛出异常
     */
    @Override
    public Boolean killItemV5(Integer killId, Integer userId) throws Exception {
        Boolean result = false;

        InterProcessMutex mutex = new InterProcessMutex(curatorFramework, PATH_PREFIX + killId + userId + "-lock");
        try {
            if (mutex.acquire(10L, TimeUnit.SECONDS)) {

                //TODO:核心业务逻辑
                if (itemKillSuccessMapper.countByKillUserId(killId, userId) <= 0) {
                    ItemKill itemKill = itemKillMapper.selectByIdV2(killId);
                    if (itemKill != null && 1 == itemKill.getCanKill() && itemKill.getTotal() > 0) {
                        int res = itemKillMapper.updateKillItemV2(killId);
                        if (res > 0) {
                            commonRecordKillSuccessInfo(itemKill, userId);
                            result = true;
                        }
                    }
                } else {
                    throw new Exception("zookeeper-您已经抢购过该商品了!");
                }
            }
        } catch (Exception e) {
            throw new Exception("还没到抢购日期、已过了抢购时间或已被抢购完毕！");
        } finally {
            mutex.release();
        }
        return result;
    }

    /**
     * 通用的方法-记录用户秒杀成功后生成的订单-并进行异步邮件消息的通知
     *
     * @param kill   秒杀的商品
     * @param userId 用户id
     */
    private void commonRecordKillSuccessInfo(ItemKill kill, Integer userId) {
        //TODO:记录抢购成功后生成的秒杀订单记录

        ItemKillSuccess entity = new ItemKillSuccess();
        String orderNo = String.valueOf(snowFlake.nextId());

        //entity.setCode(RandomUtil.generateOrderCode());   //传统时间戳+N位随机数
        //雪花算法
        entity.setCode(orderNo);
        entity.setItemId(kill.getItemId());
        entity.setKillId(kill.getId());
        entity.setUserId(userId.toString());
        entity.setStatus(SysConstant.OrderStatus.SuccessNotPayed.getCode().byteValue());
        entity.setCreateTime(DateTime.now().toDate());
        //TODO:学以致用，举一反三 -> 仿照单例模式的双重检验锁写法
        if (itemKillSuccessMapper.countByKillUserId(kill.getId(), userId) <= 0) {
            int res = itemKillSuccessMapper.insertSelective(entity);

            if (res > 0) {
                //TODO:进行异步邮件消息的通知=rabbitmq+mail
                rabbitSenderService.sendKillSuccessEmailMsg(orderNo);

                //TODO:入死信队列，用于 “失效” 超过指定的TTL时间时仍然未支付的订单
                rabbitSenderService.sendKillSuccessOrderExpireMsg(orderNo);
            }
        }
    }

}

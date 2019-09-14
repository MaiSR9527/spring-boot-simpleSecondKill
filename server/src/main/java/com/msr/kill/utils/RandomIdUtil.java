package com.msr.kill.utils;

import org.joda.time.DateTime;

import java.text.SimpleDateFormat;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @Description: 随机数生成util
 * @Author: maishuren
 * @Date: 2019/9/6 15:43
 */

public class RandomIdUtil {

    private static final SimpleDateFormat DATE_FORMAT_ONE = new SimpleDateFormat("yyyyMMddHHmmssSS");

    private static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();

    /**
     * 生成订单编号-方式一
     *
     * @return
     */
    public static String generateOrderCode() {
        //TODO:时间戳+N为随机数流水号
        return DATE_FORMAT_ONE.format(DateTime.now().toDate()) + generateNumber(4);
    }

    /**
     * N为随机数流水号
     *
     * @param num 位数
     * @return 返回
     */
    private static String generateNumber(final int num) {
        StringBuffer sb = new StringBuffer();
        for (int i = 1; i <= num; i++) {
            sb.append(RANDOM.nextInt(9));
        }
        return sb.toString();
    }
}

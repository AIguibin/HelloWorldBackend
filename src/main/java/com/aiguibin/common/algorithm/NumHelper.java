package com.aiguibin.common.algorithm;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.math.BigDecimal;
import java.util.*;

/**
 * @author AIguibin
 * Date time 2019年04月11日 14:33:56
 */
public class NumHelper {

    private static final Log log = LogFactory.getLog(NumHelper.class);

    /**
     * 素数：大于等于1且只能被1和它本身整除的整数
     * 求两个整数之间素数
     */
    public static List<Integer> getPrimeNumbers(int start, int ender) {
        List<Integer> primes = new ArrayList<>();
        Set<Integer> set = new HashSet<>();
        if (start < 1) {
            if (log.isInfoEnabled()) {
                log.info(String.format("开始数字不大于1,请检查开始数字：%d", start));
            }
        }
        if (start - ender > 0) {
            if (log.isInfoEnabled()) {
                log.info(String.format("开始与结束的数字参数输入颠倒, start：%d,ender：%d", start, ender));
            }
            int temp = start;
            start = ender;
            ender = temp;
            if (log.isInfoEnabled()) {
                log.info(String.format("容错处理，参数对调, start：%d,ender：%d", start, ender));
            }
        }
        for (int i = start; i <= ender; i++) {
            if (i == 1 || i == 2) {
                primes.add(i);
            } else {
                //每一个i去与每一个j做运算
                for (int j = 2; j < i; j++) {
                    if (i % j == 0) {
                        break;
                    } else {
                        set.add(i);
                    }
                }
            }
        }
        primes.addAll(set);
        return primes;
    }

    /**
     * 抢红包随机金额简单算法
     *
     * @param amount 红包总金额
     * @param number 红包总个数
     * @return 获取的随机金额
     * @author AIguibin
     * Date time 2019/4/11 14:45
     */
    public static double redEnvelopeAmount(double amount, int number) {
        Random random = new Random();
        if (number <= 1) {
            switch (number) {
                case 0:
                    return 0;
                case 1:
                    return amount;
                default:
                    break;
            }
        }else {
            if (amount/number<0.01) {
                return 0;
            }else if (amount/number==0.01) {
                return 0.01;
            }
            //每人最少领取金额=总金额/总个数
            BigDecimal decimalLeast=new BigDecimal(random.nextDouble()*amount/number);
            double least=decimalLeast.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            if (least==0) {
                least=0.01;
            }
            //减去每人最少可得金额,把剩下的钱随机,否则不够分.
            amount=amount-number*least;
            BigDecimal decimalAmount=new BigDecimal(random.nextDouble()*amount);
            amount=decimalAmount.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            if (amount==0) {
                amount=0.01;
            }
        }
        return amount;
    }
}
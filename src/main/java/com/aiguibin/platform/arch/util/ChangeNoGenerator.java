package com.aiguibin.platform.arch.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 变更单号生成器
 */
public class ChangeNoGenerator {
    
    /**
     * 日期格式
     */
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");
    
    /**
     * 序列号
     */
    private static final AtomicInteger SEQUENCE = new AtomicInteger(1);
    
    /**
     * 生成变更单号
     * 格式：CHG_DICT_YYYYMMDD_0001
     */
    public synchronized String generateChangeNo() {
        String dateStr = DATE_FORMAT.format(new Date());
        int seq = SEQUENCE.getAndIncrement();
        
        // 重置序列号（每天）
        if (seq >= 9999) {
            SEQUENCE.set(1);
        }
        
        return String.format("CHG_DICT_%s_%04d", dateStr, seq);
    }
}
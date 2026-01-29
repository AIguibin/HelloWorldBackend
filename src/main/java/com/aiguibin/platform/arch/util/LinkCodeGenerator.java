package com.aiguibin.platform.arch.util;

import org.springframework.stereotype.Component;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 链路编号生成器
 * 格式：LN-CREDIT-{系统简写}-{日期YYYYMMDD}-{3位序列号}
 * 示例：LN-CREDIT-PAY-20260126-001
 */
@Component
public class LinkCodeGenerator {
    
    /**
     * 日期格式
     */
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");
    
    /**
     * 序列号（每天重置）
     */
    private static final AtomicInteger SEQUENCE = new AtomicInteger(1);
    
    /**
     * 当前日期（用于判断是否需要重置序列号）
     */
    private static String currentDate = DATE_FORMAT.format(new Date());
    
    /**
     * 生成链路编号
     * 格式：LN-CREDIT-{系统简写}-{日期YYYYMMDD}-{3位序列号}
     * 
     * @param systemAbbr 系统简写（如：PAY-支付系统，CBS-核心银行系统）
     * @return 链路编号
     */
    public synchronized String generateLinkCode(String systemAbbr) {
        String today = DATE_FORMAT.format(new Date());
        
        // 如果日期变化，重置序列号
        if (!today.equals(currentDate)) {
            SEQUENCE.set(1);
            currentDate = today;
        }
        
        int seq = SEQUENCE.getAndIncrement();
        
        // 重置序列号（每天最多999条）
        if (seq >= 1000) {
            SEQUENCE.set(1);
            seq = 1;
        }
        
        return String.format("LN-CREDIT-%s-%s-%03d", 
            systemAbbr != null && !systemAbbr.isEmpty() ? systemAbbr : "SYS", 
            today, 
            seq);
    }
    
    /**
     * 生成链路编号（使用默认系统简写）
     * 格式：LN-CREDIT-SYS-{日期YYYYMMDD}-{3位序列号}
     * 
     * @return 链路编号
     */
    public synchronized String generateLinkCode() {
        return generateLinkCode("SYS");
    }
}

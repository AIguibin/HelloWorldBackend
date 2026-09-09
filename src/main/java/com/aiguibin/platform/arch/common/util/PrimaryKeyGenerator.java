package com.aiguibin.platform.arch.common.util;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 64 位有序主键生成器（公司建表规范 V4.0）.
 *
 * <p>结构：时间前缀(17 位，UTC yyyyMMddHHmmssSSS) + 大写去横线 UUID(32 位)
 * + 机器标识(4 位十六进制) + 序列号(11 位，毫秒内自增)。
 *
 * <p>使用约定：实体主键声明为 {@code @TableId(type = IdType.INPUT)} 的 String，
 * 插入前调用 {@link #nextId()} 由应用层赋值，禁止依赖数据库自增。
 * 多节点部署时机器标识必须唯一，通过环境变量 {@code ARCH_MACHINE_ID} 注入
 * （4 位十六进制，非法值将导致启动失败）。
 */
public final class PrimaryKeyGenerator {

    /** UTC 毫秒时间前缀格式（17 位）. */
    private static final DateTimeFormatter UTC_MILLIS =
            DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS").withZone(ZoneOffset.UTC);

    /** 序列号上限（11 位十进制）. */
    private static final long MAX_SEQUENCE = 999_999_999_99L;

    private static final String MACHINE_ID_ENV = "ARCH_MACHINE_ID";
    private static final String DEFAULT_MACHINE_ID = "0000";

    private static final String MACHINE_ID = resolveMachineId();

    private static long lastMillis = -1L;
    private static long sequence = 0L;

    private PrimaryKeyGenerator() {
    }

    /**
     * 生成 64 位主键（线程安全；同一毫秒单机最多 10^11 个）.
     * 时间前缀保证全局趋势递增，适配 InnoDB 聚簇索引顺序插入.
     */
    public static synchronized String nextId() {
        long millis = System.currentTimeMillis();
        if (millis < lastMillis) {
            // 时钟回拨：沿用上一毫秒时间戳，保证主键仍趋势递增
            millis = lastMillis;
        }
        if (millis == lastMillis) {
            if (++sequence > MAX_SEQUENCE) {
                // 当前毫秒序列耗尽：自旋等待进入下一毫秒
                do {
                    millis = System.currentTimeMillis();
                    if (millis < lastMillis) {
                        millis = lastMillis;
                    }
                } while (millis == lastMillis);
                sequence = 1;
            }
        } else {
            sequence = 1;
        }
        lastMillis = millis;

        return UTC_MILLIS.format(Instant.ofEpochMilli(millis))
                + UUID.randomUUID().toString().replace("-", "").toUpperCase()
                + MACHINE_ID
                + String.format("%011d", sequence);
    }

    private static String resolveMachineId() {
        String id = System.getenv().getOrDefault(MACHINE_ID_ENV,
                System.getProperty("arch.machine-id", DEFAULT_MACHINE_ID));
        if (!id.matches("[0-9A-Fa-f]{4}")) {
            throw new IllegalStateException(MACHINE_ID_ENV + " 必须为 4 位十六进制（如 A1C0），当前值非法");
        }
        return id.toUpperCase();
    }
}

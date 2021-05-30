package com.aiguibin.common.character;

import com.aiguibin.common.constant.DateConstant;
import com.aiguibin.common.constant.RegexConstant;
import com.aiguibin.common.constant.SpaceConstant;
import com.aiguibin.common.enumer.Radix;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.regex.Matcher;

/**
 * 描述： 字符串工具类
 *
 * @author AIguibin Date time 2018/7/25 11:23
 */
public class StringHelper {
    private static final Log logger = LogFactory.getLog(StringHelper.class);

    /**
     * 默认的构造函数
     */
    private StringHelper() {
        super();
    }

    /**
     * @param str 字符串
     * @return 字符串为isNull
     */
    public static boolean isNull(String str) {
        if (str == null) {
            return true;
        } else {
            if ("null".equals(str.trim().toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param str 字符串
     * @return 字符串isNotNull
     */
    public static boolean isNotNull(String str) {
        if (isNull(str)) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * @param str 字符串
     * @return 字符串isKong
     */
    public static boolean isKong(String str) {
        if (isNotNull(str)) {
            str = str.trim();
            if (str == "" || "".equals(str) || str.length() <= 0) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    /**
     * @param str 字符串
     * @return 字符串isNotKong
     */
    public static boolean isNotKong(String str) {
        if (isKong(str)) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * @param str 字符串
     * @return 字符串isEmpty
     */
    public static boolean isEmpty(String str) {
        if (isNull(str) || isKong(str)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @param str 字符串
     * @return 字符串isNotEmpty
     */
    public static boolean isNotEmpty(String str) {
        if (isEmpty(str)) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 任意类型转成字符串
     *
     * @param obj 任意类型
     * @return 字符串或空串
     */
    public static String forString(Object obj) {
        if (obj == null) {
            return "";
        } else {
            if ("null".equals(obj.toString().trim().toLowerCase())) {
                return "";
            }
        }
        return obj.toString();
    }

    /**
     * 判断单个字符是否为汉字
     *
     * @param c 判断字符
     * @return 是否为汉字
     */
    public static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A) {
            return true;
        }
        return false;
    }

    /**
     * 判断字符串是否为汉字 只要出现汉字 就返回true
     *
     * @param str 判断字符串
     * @return 是否为汉字
     */
    public static boolean isChinese(String str) {
        boolean flg = false;
        if (isNotEmpty(str)) {
            char[] ch = str.toCharArray();
            for (int i = 0; i < ch.length; i++) {
                char c = ch[i];
                if (isChinese(c)) {
                    flg = true;
                    break;
                }
            }
        }
        return flg;
    }

    /**
     * 将字符串转换为boolean值，如果不是合法的boolean字符串，则使用默认值。
     *
     * @param str          字符串
     * @param defaultValue 默认值
     * @return boolean值
     */
    public static boolean string2Boolean(String str, boolean defaultValue) {
        if ("true".equalsIgnoreCase(str)) {
            return true;
        } else if ("false".equalsIgnoreCase(str)) {
            return false;
        } else {
            return defaultValue;
        }
    }

    /**
     * 将字符串转换为int值，如果不是合法的数值，则使用默认值。
     *
     * @param str          字符串
     * @param radix        数制，支持二进制、八进制、十进制、十六进制
     * @param defaultValue 默认int值
     * @return int值
     */
    public static int string2Int(String str, Radix radix, int defaultValue) {
        try {
            if (!StringHelper.isEmpty(str)) {
                String check = str.toLowerCase();
                if (check.startsWith("0b") || (check.startsWith("08") && radix == Radix.Octonary)
                        || check.startsWith("0x")) {
                    str = str.substring(2);
                }
                return Integer.parseInt(str, radix.getRadix());
            }
        } catch (NumberFormatException ex) {
            if (logger.isWarnEnabled()) {
                logger.warn(String.format("Parse %s to int fail, radix: %s, default: %d.", str, radix.name(),
                        defaultValue));
            }
        }
        return defaultValue;
    }

    /**
     * 将字符串转换为long值，如果不是合法的数值，则使用默认值。
     *
     * @param str          字符串
     * @param radix        数制，支持二进制、八进制、十进制、十六进制
     * @param defaultValue 默认long值
     * @return long值
     */
    public static long string2Long(String str, Radix radix, long defaultValue) {
        try {
            if (!StringHelper.isEmpty(str)) {
                String check = str.toLowerCase();
                if (check.startsWith("0b") || (check.startsWith("08") && radix == Radix.Octonary) || check.startsWith("0x")) {
                    str = str.substring(2);
                }
                return Long.parseLong(str, radix.getRadix());
            }
        } catch (NumberFormatException ex) {
            if (logger.isWarnEnabled()) {
                logger.warn(String.format("Parse %s to long fail, radix: %s, default: %d.", str, radix.name(),
                        defaultValue));
            }
        }
        return defaultValue;
    }

    /**
     * 将字符串转换为float值，如果不是合法的数值，则使用默认值。
     *
     * @param str          字符串
     * @param defaultValue 默认float值
     * @return float值
     */
    public static float string2Float(String str, float defaultValue) {
        try {
            if (!StringHelper.isEmpty(str)) {
                return Float.parseFloat(str);
            }
        } catch (NumberFormatException ex) {
            if (logger.isWarnEnabled()) {
                logger.warn(String.format("Parse %s to float fail, default: %f.", str, defaultValue));
            }
        }
        return defaultValue;
    }

    /**
     * 将字符串转换为double值，如果不是合法的数值，则使用默认值。
     *
     * @param str          字符串
     * @param defaultValue 默认double值
     * @return double值
     */
    public static double string2Double(String str, double defaultValue) {
        try {
            if (!StringHelper.isEmpty(str)) {
                return Double.parseDouble(str);
            }
        } catch (NumberFormatException ex) {
            if (logger.isWarnEnabled()) {
                logger.warn(String.format("Parse %s to double fail, default: %f.", str, defaultValue));
            }
        }
        return defaultValue;
    }

    /**
     * 将字符串转换为空间大小，如果不是合法的空间大小字符串，则使用默认值。
     *
     * @param size         空间大小字符串
     * @param defaultValue 默认值
     * @return 空间大小
     */
    public static long string2Size(String size, long defaultValue) {
        if (StringHelper.isEmpty(size)) {
            return defaultValue;
        }
        final Matcher matcher = RegexConstant.SPACE_PATTERN.matcher(size);
        if (matcher.matches()) {
            try {
                final double value = NumberFormat.getNumberInstance(Locale.getDefault()).parse(matcher.group(1)).doubleValue();
                final String units = matcher.group(3);
                if (units.isEmpty()) {
                    return (long) value;
                } else if (units.equalsIgnoreCase("K")) {
                    return (long) (value * SpaceConstant.KB);
                } else if (units.equalsIgnoreCase("M")) {
                    return (long) (value * SpaceConstant.MB);
                } else if (units.equalsIgnoreCase("G")) {
                    return (long) (value * SpaceConstant.GB);
                } else if (units.equalsIgnoreCase("T")) {
                    return (long) (value * SpaceConstant.TB);
                } else if (units.equalsIgnoreCase("P")) {
                    return (long) (value * SpaceConstant.PB);
                }
            } catch (final Exception e) {
                if (logger.isWarnEnabled()) {
                    logger.warn(String.format("Parse %s to int fail, default: %d.", size, defaultValue));
                }
            }
        }
        return defaultValue;
    }

    /**
     * 将字符串转换为时间周期值，单位为毫秒，如果不是合法的时间周期字符串，则使用默认值。
     *
     * @param timePeriod   时间周期字符串
     * @param defaultValue 默认值
     * @return 时间周期，单位毫秒
     */
    public static long string2TimePeriod(String timePeriod, long defaultValue) {
        if (StringHelper.isEmpty(timePeriod)) {
            return defaultValue;
        }
        final Matcher matcher = RegexConstant.TIME_PERIOD_PATTERN.matcher(timePeriod);
        if (matcher.matches()) {
            try {
                final double value = NumberFormat.getNumberInstance(Locale.getDefault()).parse(matcher.group(1))
                        .doubleValue();
                final String units = matcher.group(3);
                if (units.isEmpty()) {
                    return (long) value;
                } else if (units.equalsIgnoreCase("SEC")) {
                    return (long) (value * DateConstant.SEC);
                } else if (units.equalsIgnoreCase("MIN")) {
                    return (long) (value * DateConstant.MIN);
                } else if (units.equalsIgnoreCase("HOUR")) {
                    return (long) (value * DateConstant.HOUR);
                } else if (units.equalsIgnoreCase("DAY")) {
                    return (long) (value * DateConstant.DAY);
                } else if (units.equalsIgnoreCase("WEEK")) {
                    return (long) (value * DateConstant.WEEK);
                } else if (units.equalsIgnoreCase("MON")) {
                    return (long) (value * DateConstant.MON);
                } else if (units.equalsIgnoreCase("QUAR")) {
                    return (long) (value * DateConstant.QUAR);
                } else if (units.equalsIgnoreCase("YEAR")) {
                    return (long) (value * DateConstant.YEAR);
                }
            } catch (final Exception e) {
                if (logger.isWarnEnabled()) {
                    logger.warn(String.format("Parse %s to time period fail, default: %d.", timePeriod, defaultValue));
                }
            }
        }
        return defaultValue;
    }
}

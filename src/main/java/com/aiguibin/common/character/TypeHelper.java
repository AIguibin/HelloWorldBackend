package com.aiguibin.common.character;

import com.aiguibin.common.constant.RegexConstant;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

/**
 * 描述：
 *
 * @author AIguibin Date time 2018/8/20 16:27
 */
public class TypeHelper {
    /**
     * 默认的构造函数
     */
    private TypeHelper() {
        super();
    }

    /**
     * 将字节数组转换为int数值
     *
     * @param bytes 字节数组，超出4字节以上的数据将被忽略
     * @return int型数值
     */
    public static int byteArray2Int(byte[] bytes) {
        if (bytes == null) {
            return 0;
        }
        int i = 0;
        for (int index = 0; index < Math.min(bytes.length, 4); index++) {
            i <<= 8;
            i |= (bytes[index] & 0xff);
        }
        return i;
    }

    /**
     * 将字节数组转换为long型数值
     *
     * @param bytes 字节数组，超出8字节以上的数据将被忽略
     * @return long型数值
     */
    public static long byteArray2Long(byte[] bytes) {
        if (bytes == null) {
            return 0;
        }
        long l = 0;
        for (int index = 0; index < Math.min(bytes.length, 8); index++) {
            l <<= 8;
            l |= (bytes[index] & 0xff);
        }
        return l;
    }

    /**
     * 将int型数值转换为字节数组
     *
     * @param i 数值
     * @return 长度为4的字节数组
     */
    public static byte[] int2ByteArray(int i) {
        byte[] bytes = new byte[4];
        for (int index = 0; index < 4; index++) {
            bytes[index] = (byte) ((i >> (32 - (index + 1) * 8)) & 0xff);
        }
        return bytes;
    }

    /**
     * 将long型数值转换为字节数组
     *
     * @param l 数值
     * @return 长度为8的字节数组
     */
    public static byte[] long2ByteArray(long l) {
        byte[] bytes = new byte[8];
        for (int index = 0; index < 8; index++) {
            bytes[index] = (byte) ((l >> (64 - (index + 1) * 8)) & 0xff);
        }
        return bytes;
    }

    /**
     * 将字节数组转换为IPv4格式的字符串。
     *
     * @param bytes 字节数组
     * @return IPv4格式的字符串
     */
    public static String byteArray2Ipv4(byte[] bytes) {
        if (bytes == null || bytes.length <= 0) {
            return "NA";
        }
        StringBuilder sb = new StringBuilder(String.valueOf(byte2IpInt(bytes[0])));
        for (int index = 1; index < bytes.length; index++) {
            sb.append(".");
            sb.append(byte2IpInt(bytes[index]));
        }
        return sb.toString();
    }

    /**
     * 将字节数组转换为IPv6
     *
     * @param bytes 字节数组
     * @return IPv6字符串
     */
    public static String byteArray2Ipv6(byte[] bytes) {
        if (bytes == null || bytes.length != 16) {
            return "NA";
        }
        BigInteger big = new BigInteger(bytes), ff = BigInteger.valueOf(0xffff);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            sb.insert(0, ":");
            sb.insert(0, big.and(ff).toString(16));
            big = big.shiftRight(16);
        }
        String str = sb.substring(0, sb.length() - 1);
        return str.replaceFirst("(^|:)(0+(:|$)){2,8}", "::");
    }

    /**
     * 将字节数组转换为IP地址，支持IPv4和IPv6，字节数组长度小于等于4的，转换为IPv4，否则转换为IPv6
     *
     * @param bytes 字节数组
     * @return IP地址
     */
    public static String byteArray2Ip(byte[] bytes) {
        if (bytes == null || bytes.length <= 0) {
            return "NA";
        }
        if (bytes.length > 4) {
            return byteArray2Ipv6(bytes);
        } else {
            return byteArray2Ipv4(bytes);
        }
    }

    private static int byte2IpInt(byte b) {
        return b >= 0 ? b : b + 256;
    }

    /**
     * 将一行CSV数据转换为列表
     *
     * @param line 一行CSV数据
     * @return 列表数据
     */
    public static List<String> csv2List(String line) {
        List<String> list = new ArrayList<>();
        if (!StringHelper.isEmpty(line)) {
            String[] segs = line.split(RegexConstant.regex, -1);
            list.addAll(Arrays.asList(segs));
        }
        return list;
    }

    /**
     * 将IPv4地址转换为字节数组
     *
     * @param ipv4 IP地址
     * @return 字节数组
     */
    public static byte[] Ip2byteArrayV4(String ipv4) {
        if (StringHelper.isEmpty(ipv4)) {
            return new byte[]{};
        }
        String[] splits = ipv4.split("\\.");
        int len = Math.min(splits.length, 4);
        byte[] values = new byte[len];
        for (int index = 0; index < len; index++) {
            values[index] = Integer.valueOf(splits[index], 10).byteValue();
        }
        return values;
    }

    private static BigInteger ipv6toInt(String ipv6) {
        int compressIndex = ipv6.indexOf("::");
        if (compressIndex != -1) {
            String part1s = ipv6.substring(0, compressIndex);
            String part2s = ipv6.substring(compressIndex + 1);
            BigInteger part1 = ipv6toInt(part1s);
            BigInteger part2 = ipv6toInt(part2s);
            int part1hasDot = 0;
            char ch[] = part1s.toCharArray();
            for (char c : ch) {
                if (c == ':') {
                    part1hasDot++;
                }
            }
            // ipv6 has most 7 dot
            return part1.shiftLeft(16 * (7 - part1hasDot)).add(part2);
        }
        String[] str = ipv6.split(":");
        BigInteger big = BigInteger.ZERO;
        for (int i = 0; i < str.length; i++) {
            //::1
            if (str[i].isEmpty()) {
                str[i] = "0";
            }
            big = big.add(BigInteger.valueOf(Long.valueOf(str[i], 16))
                    .shiftLeft(16 * (str.length - i - 1)));
        }
        return big;
    }

    /**
     * 将IPv6地址转换为字节数组
     *
     * @param ipv6 IP地址
     * @return 字节数组
     */
    public static byte[] Ip2byteArrayV6(String ipv6) {
        if (StringHelper.isEmpty(ipv6)) {
            return new byte[]{};
        }
        byte[] values = ipv6toInt(ipv6).toByteArray();
        byte[] tar = new byte[16];
        if (values.length > 16) {
            System.arraycopy(values, values.length - 16, tar, 0, 16);
            return tar;
        } else if (values.length < 16) {
            System.arraycopy(values, 0, tar, 16 - values.length, values.length);
            return tar;
        } else {
            return values;
        }
    }

    /**
     * 将IP地址转换为字节数组，能自动分辨IPv4和IPv6
     *
     * @param ip IP地址
     * @return 字节数组
     */
    public static byte[] Ip2byteArray(String ip) {
        if (StringHelper.isEmpty(ip)) {
            return new byte[]{};
        }
        if (ip.contains(":")) {
            return Ip2byteArrayV6(ip);
        } else {
            return Ip2byteArrayV4(ip);
        }
    }

    /**
     * 将字节数组转换为十六进制的字符串，一个字节转换为两位长度的十六进制。
     *
     * @param byteArray 待转换的字节数组
     * @return 转换后的十六进制字符串，如果输入的字节数组为null或者长度为0， 则返回长度为0的字符串。
     */
    public static String byteArray2HexString(byte[] byteArray) {
        if (byteArray == null || byteArray.length <= 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (byte b : byteArray) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    /**
     * 将整数数组转换为十六进制的字符串，一个整数转换为四位长度的十六进制。
     *
     * @param intArray 待转换的整数数组
     * @return 转换后的十六进制字符串，如果输入的整数数组为null或者长度为0， 则返回长度为0的字符串。
     */
    public static String intArray2HexString(int[] intArray) {
        if (intArray == null || intArray.length <= 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i : intArray) {
            sb.append(String.format("%04x", i));
        }
        return sb.toString();
    }

    /**
     * 将长整数数组转换为十六进制的字符串，一个长整数转换为八位长度的十六进制。
     *
     * @param longArray 待转换的长整数数组
     * @return 转换后的十六进制字符串，如果输入的长整数数组为null或者长度为0， 则返回长度为0的字符串。
     */
    public static String longArray2HexString(int[] longArray) {
        if (longArray == null || longArray.length <= 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int l : longArray) {
            sb.append(String.format("%08x", l));
        }
        return sb.toString();
    }

    /**
     * 采用Base64算法进行编码，将字节数据转换为字符串。
     *
     * @param value 待编码的字节数据
     * @return 编码后的字符串
     */
    public static String byteArray2Base64(byte[] value) {
        return Base64.getEncoder().encodeToString(value);
    }

    /**
     * 采用Base64算法进行解码，将字符串转换为字节数据
     *
     * @param base64Str Base64编码的字符串
     * @return 解码后的字节数据
     */
    public static byte[] base642ByteArray(String base64Str) {
        return Base64.getDecoder().decode(base64Str);
    }

    /**
     * 将一个字节编码为长度为2的十六进制标示的字符串
     *
     * @param b 字节
     * @return 十六进制编码的字符串
     */
    public static String byte2HexString(byte b) {
        String hex = Integer.toHexString(b);
        int length = hex.length();
        switch (length) {
            case 0:
                return "00";
            case 1:
                return String.format("0%s", hex);
            case 2:
                return hex;
            default:
                return hex.substring(length - 2);
        }
    }

    /**
     * 将十六进制表示的字符串解码为字节数组，长度为2的字符串表示一个字节。
     *
     * @param input 十六进制字符串
     * @return 字节数组
     * @throws NumberFormatException 转换过程发生的异常
     */
    public static byte[] hexString2ByteArray(String input) throws NumberFormatException {
        if (StringHelper.isEmpty(input)) {
            return null;
        }
        int length = input.length();
        if (length % 2 != 0) {
            throw new NumberFormatException(
                    String.format("The Hex String['%s']'s length is not a even number.", input));
        }
        byte[] bs = new byte[length / 2];
        for (int index = 0; index < length; index += 2) {
            bs[index / 2] = Byte.valueOf(input.substring(index, 2), 16);
        }
        return bs;
    }
}

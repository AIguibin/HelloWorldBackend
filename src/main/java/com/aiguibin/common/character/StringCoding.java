package com.aiguibin.common.character;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;

/**
 * 描述： 字符串编码转换
 *
 * @author AIguibin Date time 2018/7/30 17:37
 */
public class StringCoding {
    /**
     * String 转 Unicode
     * @param strVal 字符串
     * @return 编码为Unicode字符串
     */
    public static String string2Unicode(String strVal) {
        StringBuffer unicode = new StringBuffer();
        for (int i = 0; i < strVal.length(); i++) {
            // 取出每一个字符
            char c = strVal.charAt(i);
            // 转换为encode
            unicode.append("\\u" + Integer.toHexString(c));
        }
        return unicode.toString();
    }

    /**
     * String 转 Unicode
     * @param unicode 字符串
     * @return 解码为String字符串
     */
    public static String unicode2String(String unicode) {
        StringBuffer sb = new StringBuffer();
        String[] hex = unicode.split("\\\\u");
        for (int i = 1; i < hex.length; i++) {
            // 转换出每一个代码点
            int data = Integer.parseInt(hex[i], 16);
            // 追加成string
            sb.append((char) data);
        }
        return sb.toString();
    }

    /**
     * ISO-8859-1转 GB2312
     *
     * @param text
     * @return
     */
    public static final String ISO2GB(String text) {
        String result = "";
        try {
            result = new String(text.getBytes("ISO-8859-1"), "GB2312");
        } catch (UnsupportedEncodingException ex) {
            result = ex.toString();
        }
        return result;
    }

    /**
     * 转换编码 GB2312到ISO-8859-1
     *
     * @param text
     * @return
     */
    public static final String GB2ISO(String text) {
        String result = "";
        try {
            result = new String(text.getBytes("GB2312"), "ISO-8859-1");
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
        }
        return result;
    }

    public static String GBK2UTF8(String strVal) {
        try {
            if (strVal == null) {
                return "";
            } else {
                strVal = new String(strVal.getBytes("GBK"), "UTF-8");
                return strVal;
            }
        } catch (Exception exp) {
            return "";
        }
    }

    /**
     * 字符串从GBK编码转换为Unicode编码
     *
     * @param text
     * @return
     */
    public static String GBK2Unicode(String text) {
        String result = "";
        int input;
        StringReader isr;
        try {
            isr = new StringReader(new String(text.getBytes(), "GBK"));
        } catch (UnsupportedEncodingException e) {
            return "-1";
        }
        try {
            while ((input = isr.read()) != -1) {
                result = result + "&#x" + Integer.toHexString(input) + ";";
            }
        } catch (IOException e) {
            return "-2";
        }
        isr.close();
        return result;
    }

    /**
     *
     * @param strVal
     * @return UTF-8
     */
    public static String ISO2UTF8(String strVal) {
        try {
            if (strVal == null) {
                return "";
            } else {
                strVal = new String(strVal.getBytes("ISO-8859-1"), "UTF-8");
                return strVal;
            }
        } catch (Exception exp) {
            return "";
        }
    }

    /**
     *
     * @param strVal
     * @return Return:String Description:
     */
    public static String UTF82ISO(String strVal) {
        try {
            if (strVal == null) {
                return "";
            } else {
                strVal = new String(strVal.getBytes("UTF-8"), "ISO-8859-1");
                return strVal;
            }
        } catch (Exception exp) {
            return "";
        }
    }

    /**
     * 把字符串转换为UTF8859编码
     *
     * @param source 需要进行转换的字符串
     */
    public static final String GBKto8859(String source) {
        String temp = null;
        if (source == null || source.equals("")) {
            return source;
        }
        try {
            temp = new String(source.getBytes("GBK"), "8859_1");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Convert code Error");
        }
        return temp;
    }

    /**
     * 把字符串转换为GBK编码
     *
     * @param source 需要进行转换的字符串
     */
    public static final String toGBK(String source) {
        String temp = null;
        if (source == null || source.equals("")) {
            return source;
        }
        try {
            temp = new String(source.getBytes("8859_1"), "GBK");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Convert code Error");
        }
        return temp;
    }

    /**
     * 把字符串转换为gb2312编码
     *
     * @param source 需要进行转换的字符串
     */
    public static final String toGb2312(String source) {
        String temp = null;
        if (source == null || source.equals("")) {
            return source;
        }
        try {
            temp = new String(source.getBytes("8859_1"), "GB2312");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("转换字符串为gb2312编码出错");
        }
        return temp;
    }

    /**
     * 把中文字符串，转换为unicode字符串
     *
     * @param source 需要进行转换的字符串
     * @return 转换后的unicode字符串
     */
    public static String chineseToUnicode(String source) {
        if (source == null || source.trim().length() == 0) {
            return source;
        }
        String unicode = null;
        String temp = null;
        for (int i = 0; i < source.length(); i++) {
            temp = "\\u" + Integer.toHexString((int) source.charAt(i));
            unicode = unicode == null ? temp : unicode + temp;
        }
        return unicode;
    }

    /**
     * 在将数据存入数据库前转换
     *
     * @param strVal 要转换的字符串
     * @return 从“ISO8859_1”到“GBK”得到的字符串
     * @since 1.0
     */
    public static String toChinese(String strVal) {
        try {
            if (strVal == null) {
                return "";
            } else {
                strVal = strVal.trim();
                strVal = new String(strVal.getBytes("ISO8859_1"), "GBK");
                return strVal;
            }
        } catch (Exception exp) {
            return "";
        }
    }

    /**
     * Utf8URL编码
     *
     * @param
     * @return
     */
    public static final String Utf8URLencode(String text) {
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c >= 0 && c <= 255) {
                result.append(c);
            } else {
                byte[] b = new byte[0];
                try {
                    b = Character.toString(c).getBytes("UTF-8");
                } catch (Exception ex) {
                }
                for (int j = 0; j < b.length; j++) {
                    int k = b[j];
                    if (k < 0){
                        k += 256;
                    }
                    result.append("%" + Integer.toHexString(k).toUpperCase());
                }
            }
        }
        return result.toString();
    }

    /**
     * Utf8URL解码
     *
     * @param text
     * @return
     */
    public static final String Utf8URLdecode(String text) {
        String result = "";
        int p = 0;
        if (text != null && text.length() > 0) {
            text = text.toLowerCase();
            p = text.indexOf("%e");
            if (p == -1){
                return text;
            }
            while (p != -1) {
                result += text.substring(0, p);
                text = text.substring(p, text.length());
                if (text == "" || text.length() < 9){
                    return result;
                }
                result += CodeToWord(text.substring(0, 9));
                text = text.substring(9, text.length());
                p = text.indexOf("%e");
            }
        }
        return result + text;
    }

    /**
     * Utf8URL编码
     *
     * @param source
     * @return
     */
    public static String urlEncodeUTF8(String source) {
        String result = source;
        try {
            result = java.net.URLEncoder.encode(source, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * utf8URL编码转字符
     *
     * @param text
     * @return
     */
    private static final String CodeToWord(String text) {
        String result;
        if (Utf8codeCheck(text)) {
            byte[] code = new byte[3];
            code[0] = (byte) (Integer.parseInt(text.substring(1, 3), 16) - 256);
            code[1] = (byte) (Integer.parseInt(text.substring(4, 6), 16) - 256);
            code[2] = (byte) (Integer.parseInt(text.substring(7, 9), 16) - 256);
            try {
                result = new String(code, "UTF-8");
            } catch (UnsupportedEncodingException ex) {
                result = null;
            }
        } else {
            result = text;
        }
        return result;
    }

    /**
     * 编码是否有效
     *
     * @param text
     * @return
     */
    private static final boolean Utf8codeCheck(String text) {
        String sign = "";
        if (text.startsWith("%e")){
            for (int i = 0, p = 0; p != -1; i++) {
                p = text.indexOf("%", p);
                if (p != -1){
                    p++;
                    sign += p;
                }
            }
        }
        return sign.equals("147-1");
    }

    /**
     * 判断是否Utf8Url编码
     *
     * @param text
     * @return
     */
    public static final boolean isUtf8Url(String text) {
        text = text.toLowerCase();
        int p = text.indexOf("%");
        if (p != -1 && text.length() - p > 9) {
            text = text.substring(p, p + 9);
        }
        return Utf8codeCheck(text);
    }

    /**
     * GBKStr2UTF8Bytes
     *
     * @param gbkStr
     * @return
     * @throws Throwable
     */
    public static String getUTF8BytesFromGBKString(String gbkStr) throws Throwable {
        int n = gbkStr.length();
        byte[] utfBytes = new byte[3 * n];
        int k = 0;
        for (int i = 0; i < n; i++) {
            int m = gbkStr.charAt(i);
            if (m < 128 && m >= 0) {
                utfBytes[k++] = (byte) m;
                continue;
            }
            utfBytes[k++] = (byte) (0xe0 | (m >> 12));
            utfBytes[k++] = (byte) (0x80 | ((m >> 6) & 0x3f));
            utfBytes[k++] = (byte) (0x80 | (m & 0x3f));
        }
        if (k < utfBytes.length) {
            byte[] tmp = new byte[k];
            System.arraycopy(utfBytes, 0, tmp, 0, k);
            return new String(tmp, "UTF-8");
        }
        return new String(utfBytes, "UTF-8");
    }
}

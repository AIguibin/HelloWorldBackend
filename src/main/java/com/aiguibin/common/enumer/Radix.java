package com.aiguibin.common.enumer;

public enum  Radix {
    /**
     * 二进制
     */
    Binary(2),
    /**
     * 八进制
     */
    Octonary(8),
    /**
     * 十进制
     */
    Decimal(10),
    /**
     * 十六进制
     */
    Hexadecimal(16);
    int radix;

    Radix(int radix) {
        this.radix = radix;
    }

    public int getRadix() {
        return radix;
    }
}

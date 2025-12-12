package com.aiguibin.platform.arch.util;

/**
 * 密码哈希生成工具类.
 * 用于生成和验证密码哈希值.
 */
public final class GeneratePasswordHash {

    /**
     * 私有构造函数，防止实例化.
     */
    private GeneratePasswordHash() {
        // 工具类不允许实例化
    }

    /**
     * 十六进制掩码常量.
     */
    private static final int HEX_MASK = 0xFF;

    /**
     * 十六进制偏移常量.
     */
    private static final int HEX_OFFSET = 0x100;

    /**
     * 十六进制字符串长度常量.
     */
    private static final int HEX_STRING_LENGTH = 3;

    /**
     * 主方法，用于测试密码哈希生成.
     * @param args 命令行参数.
     */
    public static void main(final String[] args) {
        String password = "123456";
        String hashedPassword = hashPassword(password);
        System.out.println("Password: " + password);
        System.out.println("Hashed Password: " + hashedPassword);

        // 验证密码
        boolean isValid = verifyPassword(password, hashedPassword);
        System.out.println("Password valid: " + isValid);
    }

    /**
     * 生成密码哈希值.
     * @param password 明文密码.
     * @return 哈希后的密码.
     */
    public static String hashPassword(final String password) {
        // 简化实现，实际项目中应使用更安全的哈希算法
        // 这里使用简单的MD5哈希（仅用于演示，生产环境请使用BCrypt或Argon2）
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(password.getBytes());
            StringBuffer sb = new StringBuffer();
            for (byte b : array) {
                sb.append(Integer.toHexString((b & HEX_MASK) | HEX_OFFSET)
                        .substring(1, HEX_STRING_LENGTH));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
            // 如果MD5算法不可用，返回明文密码（仅用于演示）
            return password;
        }
    }

    /**
     * 验证密码.
     * @param password 明文密码.
     * @param hashedPassword 哈希后的密码.
     * @return 是否匹配.
     */
    public static boolean verifyPassword(
            final String password,
            final String hashedPassword) {
        return hashPassword(password).equals(hashedPassword);
    }
}

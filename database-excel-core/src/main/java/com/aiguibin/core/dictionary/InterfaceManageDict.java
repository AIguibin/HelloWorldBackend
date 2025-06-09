package com.aiguibin.core.dictionary;

import java.util.HashMap;
import java.util.Map;

public class InterfaceManageDict {

    private static final Map<String, InterfaceInfo> MAPPING = new HashMap<>();

    static {
        // 初始化微服务中心标识与接口管理信息的映射关系
        // 格式：服务中心标识 -> (服务中心中文, 负责人英文, 负责人中文)
        MAPPING.put("TestCenter", new InterfaceInfo("测试中心", "xuxuan", "徐玄"));
    }

    /**
     * 根据服务中心标识获取接口管理信息
     * @param centerCode 服务中心标识（如TestCenter）
     * @return InterfaceInfo对象，未找到时返回空对象
     */
    public static InterfaceInfo getInterfaceInfo(String centerCode) {
        return MAPPING.getOrDefault(centerCode, new InterfaceInfo("", "", ""));
    }

    public static class InterfaceInfo {
        private final String centerCnName;   // 服务中心中文名
        private final String superintendentEnName;  // 负责人英文名
        private final String superintendentCnName;  // 负责人中文名

        public InterfaceInfo(String centerCnName, String superintendentEnName, String superintendentCnName) {
            this.centerCnName = centerCnName;
            this.superintendentEnName = superintendentEnName;
            this.superintendentCnName = superintendentCnName;
        }

        public String getCenterCnName() {
            return centerCnName;
        }

        public String getSuperintendentEnName() {
            return superintendentEnName;
        }

        public String getSuperintendentCnName() {
            return superintendentCnName;
        }

        @Override
        public String toString() {
            return "服务中心: " + centerCnName +
                    ", 负责人: " + superintendentCnName +
                    " (" + superintendentEnName + ")";
        }
    }
}
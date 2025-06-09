package com.aiguibin.core.dictionary;

import java.util.HashMap;
import java.util.Map;

public class InterfaceManageDict {

    private static final Map<String, InterfaceInfo> MAPPING = new HashMap<>();

    static {
        // 初始化微服务中心标识与接口管理信息的映射关系
        // 格式：服务中心标识 -> (服务中心中文, 负责人英文, 负责人中文)
        MAPPING.put("creditStatisticsControlApi", new InterfaceInfo("授信管理", "chenyuan", "陈园"));
        MAPPING.put("custCorporat", new InterfaceInfo("客户管理", "jiangchanghao", "姜昌号"));
        MAPPING.put("edoc", new InterfaceInfo("电子文档", "wanxujiang", "万旭江"));
        MAPPING.put("ipcFcnScnInf", new InterfaceInfo("天元运行态", "lifuqiang", "李富强"));
        MAPPING.put("channel", new InterfaceInfo("渠道服务", "fengshuai", "冯帅"));
        MAPPING.put("createToken", new InterfaceInfo("认证服务", "dingyaqiang", "丁亚强"));
        MAPPING.put("createTokenByVerificationCode", new InterfaceInfo("认证服务", "dingyaqiang", "丁亚强"));
        MAPPING.put("microDef", new InterfaceInfo("未识别服务", "xuxuan", "刘贵斌"));
        MAPPING.put("passwdCheck", new InterfaceInfo("公共服务", "zhaotao", "赵涛"));
        MAPPING.put("risk-asset-mgt", new InterfaceInfo("风控管理", "zhukai", "朱凯"));
        MAPPING.put("sendVerificationCode", new InterfaceInfo("公共服务", "zhaotao", "赵涛"));
        MAPPING.put("tansun-tcp-businessrating", new InterfaceInfo("评级管理", "panhui", "潘辉"));
        MAPPING.put("tansun-tcp-collateral", new InterfaceInfo("押品服务", "guoxi", "郭西"));
        MAPPING.put("tansun-tcp-common", new InterfaceInfo("渠道服务", "fengshuai", "冯帅"));
        MAPPING.put("tansun-tcp-contract-loan", new InterfaceInfo("合同放还款", "yuanshibo", "袁仕波"));
        MAPPING.put("tansun-tcp-corporate-boot", new InterfaceInfo("对公能力中心", "chenyuan", "陈园"));
        MAPPING.put("tansun-tcp-creditcontrol", new InterfaceInfo("额度管理", "liqichao", "李启超"));
        MAPPING.put("tansun-tcp-custmanage", new InterfaceInfo("客户管理", "jiangbinxin", "江斌鑫"));
        MAPPING.put("tansun-tcp-docmanage", new InterfaceInfo("档案管理", "xujiafeng", "徐佳峰"));
        MAPPING.put("tansun-tcp-edoc", new InterfaceInfo("电子文档", "wanxujiang", "万旭江"));
        MAPPING.put("tansun-tcp-hierarchy", new InterfaceInfo("分层分域分级", "zhaotao", "赵涛"));
        MAPPING.put("tansun-tcp-ipc", new InterfaceInfo("天元平台", "lifuqiang", "李富强"));
        MAPPING.put("tansun-tcp-message", new InterfaceInfo("消息中心", "zhaotao", "赵涛"));
        MAPPING.put("tansun-tcp-pd", new InterfaceInfo("产品中心", "zhaotao", "赵涛"));
        MAPPING.put("tansun-tcp-postloan", new InterfaceInfo("贷后管理", "chenjianwei", "陈建伟"));
        MAPPING.put("tansun-tcp-retail-boot", new InterfaceInfo("零售能力中心", "chenyaun", "陈园"));
        MAPPING.put("tansun-tcp-statistics", new InterfaceInfo("统计查询", "chenyuan", "陈园"));
        MAPPING.put("tansun-tcp-sys", new InterfaceInfo("系统管理", "zhaotao", "赵涛"));
        MAPPING.put("tansun-tcp-system-boot", new InterfaceInfo("系统管理", "zhaotao", "赵涛"));
        MAPPING.put("tansun-tcp-usecredit", new InterfaceInfo("用信管理", "yuanshibo", "袁仕波"));
        MAPPING.put("tansun-tcp-user", new InterfaceInfo("公共服务", "zhaotao", "赵涛"));
        MAPPING.put("tansun-tcp-workflow", new InterfaceInfo("工作流", "lifuqiang", "李富强"));
        MAPPING.put("tcp-ecms-statistics", new InterfaceInfo("统计查询", "yuanshibo", "袁仕波"));
        MAPPING.put("tencentMaps", new InterfaceInfo("未识别服务", "xuxuan", "徐玄"));
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
package com.aiguibin.core.dictionary;

import java.util.HashMap;
import java.util.Map;


public class superintendentDict {

    private static final Map<String, superintendentInfo> MAPPING = new HashMap<>();

    static {
        // 初始化微服务中心与负责人的映射关系
        MAPPING.put("测试中心", new superintendentInfo("xuxuan", "徐玄"));
        MAPPING.put("产品中心", new superintendentInfo("zhaotao", "赵涛"));
        MAPPING.put("贷后中心", new superintendentInfo("chenjianwei", "陈建伟"));
        MAPPING.put("档案中心", new superintendentInfo("zhaotao", "赵涛"));
        MAPPING.put("电子文档", new superintendentInfo("wanxujiang", "万旭江"));
        MAPPING.put("对公能力中心", new superintendentInfo("zhouxin", "周鑫"));
        MAPPING.put("对公能力中心-用信", new superintendentInfo("yuanshibo", "袁仕波"));
        MAPPING.put("额度子系统", new superintendentInfo("wangqiang", "王强"));
        MAPPING.put("放款中心", new superintendentInfo("yuanshibo", "袁仕波"));
        MAPPING.put("分域分层分级中心", new superintendentInfo("zhaotao", "赵涛"));
        MAPPING.put("风险资产管理子系统", new superintendentInfo("zhukai", "朱凯"));
        MAPPING.put("合同放还款中心", new superintendentInfo("yuanshibo", "袁仕波"));
        MAPPING.put("合同中心", new superintendentInfo("yuanshibo", "袁仕波"));
        MAPPING.put("监控中心", new superintendentInfo("xuxuan", "徐玄"));
        MAPPING.put("决策中心", new superintendentInfo("xuxuan", "徐玄"));
        MAPPING.put("客户中心", new superintendentInfo("jiangchanghao", "姜昌号"));
        MAPPING.put("利率中心", new superintendentInfo("zhaotao", "赵涛"));
        MAPPING.put("零售能力中心", new superintendentInfo("zhouxin", "周鑫"));
        MAPPING.put("零售能力中心-用信", new superintendentInfo("yuanshibo", "袁仕波"));
        MAPPING.put("流程引擎", new superintendentInfo("lifuqiang", "李富强"));
        MAPPING.put("模型中心", new superintendentInfo("xuxuan", "徐玄"));
        MAPPING.put("批处理作业中心", new superintendentInfo("zhoulei", "周磊"));
        MAPPING.put("数据中心", new superintendentInfo("xuxuan", "徐玄"));
        MAPPING.put("天元网关", new superintendentInfo("hebing", "何兵"));
        MAPPING.put("天元运行态", new superintendentInfo("lifuqiang", "李富强"));
        MAPPING.put("外数中心", new superintendentInfo("jiangchanghao", "姜昌号"));
        MAPPING.put("系统管理中心", new superintendentInfo("zhaotao", "赵涛"));
        MAPPING.put("消息中心", new superintendentInfo("zhaotao", "赵涛"));
        MAPPING.put("押品中心", new superintendentInfo("guoxi", "郭西"));
        MAPPING.put("评级中心", new superintendentInfo("panhui", "潘辉"));
        MAPPING.put("用信中心", new superintendentInfo("yuanshibo", "袁仕波"));
        MAPPING.put("执行器相关表", new superintendentInfo("xuxuan", "徐玄"));
        MAPPING.put("智能问答中心", new superintendentInfo("zhaotao", "赵涛"));
        MAPPING.put("注册中心", new superintendentInfo("hebing", "何兵"));
        MAPPING.put("综合服务中心", new superintendentInfo("fengshuai", "冯帅"));
        // 添加更多映射关系...
    }

    public static superintendentInfo getSuperintendent(String center) {
        return MAPPING.getOrDefault(center, new superintendentInfo("", ""));
    }

    public static class superintendentInfo {
        private final String enName;
        private final String cnName;

        public superintendentInfo(String enName, String cnName) {
            this.enName = enName;
            this.cnName = cnName;
        }

        public String getEnName() {
            return enName;
        }

        public String getCnName() {
            return cnName;
        }
    }
}
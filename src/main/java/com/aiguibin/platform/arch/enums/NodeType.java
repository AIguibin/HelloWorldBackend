package com.aiguibin.platform.arch.enums;

/**
 * 节点类型枚举
 * 定义审批节点的流转类型
 */
public enum NodeType {
    /**
     * 串行节点：节点按顺序执行，前一节点完成后下一节点才能开始
     */
    SERIAL("串行", "节点按顺序执行，前一节点完成后下一节点才能开始"),
    
    /**
     * 并行节点：多个节点同时执行，所有节点完成后才能进入下一阶段
     */
    PARALLEL("并行", "多个节点同时执行，所有节点完成后才能进入下一阶段");
    
    private final String nodeTypeName;
    private final String description;
    
    /**
     * 构造函数
     * @param nodeTypeName 节点类型名称
     * @param description 节点类型描述
     */
    NodeType(String nodeTypeName, String description) {
        this.nodeTypeName = nodeTypeName;
        this.description = description;
    }
    
    /**
     * 获取节点类型名称
     * @return 节点类型名称
     */
    public String getNodeTypeName() {
        return nodeTypeName;
    }
    
    /**
     * 获取节点类型描述
     * @return 节点类型描述
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * 根据节点类型名称获取枚举值
     * @param nodeTypeName 节点类型名称
     * @return 枚举值
     */
    public static NodeType fromNodeTypeName(String nodeTypeName) {
        for (NodeType nodeType : values()) {
            if (nodeType.nodeTypeName.equals(nodeTypeName)) {
                return nodeType;
            }
        }
        return null;
    }
}

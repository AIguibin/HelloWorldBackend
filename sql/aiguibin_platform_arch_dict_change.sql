-- ----------------------------
-- 字典类型变更主表
-- ----------------------------
DROP TABLE IF EXISTS `biz_ddct_type_change`;
CREATE TABLE `biz_ddct_type_change` (
  `uuid` varchar(32) NOT NULL COMMENT 'UUID，32位随机字符串',
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '自增ID，仅做序号',
  `change_no` varchar(32) NOT NULL COMMENT '变更单号，如CHG_DICT_20260125_0001',
  `dct_tp_id` varchar(64) DEFAULT NULL COMMENT '关联字典类型ID',
  `change_type` varchar(20) NOT NULL COMMENT '变更类型：ADD-新增，MOD-修改，DEL-删除',
  `old_dct_tp` varchar(100) DEFAULT NULL COMMENT '原始字典类型编码',
  `new_dct_tp` varchar(100) DEFAULT NULL COMMENT '新字典类型编码',
  `old_dct_tp_nm` varchar(450) DEFAULT NULL COMMENT '原始字典类型名称',
  `new_dct_tp_nm` varchar(450) DEFAULT NULL COMMENT '新字典类型名称',
  `apply_user_num` varchar(20) NOT NULL COMMENT '申请人用户编号',
  `apply_user_name` varchar(50) NOT NULL COMMENT '申请人姓名',
  `apply_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
  `change_reason` text COMMENT '变更原因',
  `change_impact` text COMMENT '变更影响分析',
  `approve_status` varchar(20) NOT NULL DEFAULT 'PENDING' COMMENT '审批状态：DRAFT-草稿，PENDING-待审批，APPROVED-已通过，REJECTED-已拒绝，CANCELED-已取消',
  `execute_status` varchar(20) NOT NULL DEFAULT 'PENDING' COMMENT '执行状态：PENDING-待执行，EXECUTING-执行中，SUCCESS-成功，FAILED-失败',
  `approver_num` varchar(20) DEFAULT NULL COMMENT '审批人用户编号',
  `approver_name` varchar(50) DEFAULT NULL COMMENT '审批人姓名',
  `approve_time` datetime DEFAULT NULL COMMENT '审批时间',
  `approve_remark` text COMMENT '审批备注',
  `execute_user_num` varchar(20) DEFAULT NULL COMMENT '执行人用户编号',
  `execute_user_name` varchar(50) DEFAULT NULL COMMENT '执行人姓名',
  `execute_time` datetime DEFAULT NULL COMMENT '执行时间',
  `execute_result` text COMMENT '执行结果',
  `flow_id` varchar(32) DEFAULT NULL COMMENT '关联流程ID',
  `current_node_id` varchar(32) DEFAULT NULL COMMENT '当前节点ID',
  `approval_instance_id` varchar(32) DEFAULT NULL COMMENT '审批实例ID',
  `created_by` varchar(20) NOT NULL COMMENT '创建人用户编号',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` varchar(20) DEFAULT NULL COMMENT '更新人用户编号',
  `updated_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint DEFAULT '0' COMMENT '是否删除：0-否，1-是',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_uuid` (`uuid`),
  UNIQUE KEY `uk_change_no` (`change_no`),
  KEY `idx_dct_tp_id` (`dct_tp_id`),
  KEY `idx_change_type` (`change_type`),
  KEY `idx_apply_user_num` (`apply_user_num`),
  KEY `idx_apply_time` (`apply_time`),
  KEY `idx_approve_status` (`approve_status`),
  KEY `idx_execute_status` (`execute_status`),
  KEY `idx_is_deleted` (`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='字典类型变更主表';

-- ----------------------------
-- 字典项变更明细表
-- ----------------------------
DROP TABLE IF EXISTS `biz_ddct_item_change`;
CREATE TABLE `biz_ddct_item_change` (
  `uuid` varchar(32) NOT NULL COMMENT 'UUID，32位随机字符串',
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '自增ID，仅做序号',
  `change_id` varchar(32) NOT NULL COMMENT '关联变更主表UUID',
  `change_no` varchar(32) NOT NULL COMMENT '关联变更单号',
  `item_order` int DEFAULT '0' COMMENT '字典项顺序',
  `dict_id` varchar(64) DEFAULT NULL COMMENT '关联字典项ID（修改/删除时使用）',
  `change_operation` varchar(20) NOT NULL COMMENT '操作类型：ADD-新增，MOD-修改，DEL-删除',
  `old_dct_seq` int DEFAULT NULL COMMENT '原始字典排序',
  `new_dct_seq` int DEFAULT NULL COMMENT '新字典排序',
  `old_dct_grp` varchar(32) DEFAULT NULL COMMENT '原始字典组',
  `new_dct_grp` varchar(32) DEFAULT NULL COMMENT '新字典组',
  `old_dct_key` varchar(100) DEFAULT NULL COMMENT '原始字典键',
  `new_dct_key` varchar(100) DEFAULT NULL COMMENT '新字典键',
  `old_dct_val_nm` varchar(450) DEFAULT NULL COMMENT '原始字典值名称',
  `new_dct_val_nm` varchar(450) DEFAULT NULL COMMENT '新字典值名称',
  `old_dct_tp_nm` varchar(450) DEFAULT NULL COMMENT '原始字典类型名称',
  `new_dct_tp_nm` varchar(450) DEFAULT NULL COMMENT '新字典类型名称',
  `old_dct_val` varchar(500) DEFAULT NULL COMMENT '原始字典值',
  `new_dct_val` varchar(500) DEFAULT NULL COMMENT '新字典值',
  `old_dct_tp` varchar(100) DEFAULT NULL COMMENT '原始字典类型',
  `new_dct_tp` varchar(100) DEFAULT NULL COMMENT '新字典类型',
  `old_dct_dsc` varchar(300) DEFAULT NULL COMMENT '原始字典描述',
  `new_dct_dsc` varchar(300) DEFAULT NULL COMMENT '新字典描述',
  `old_stcd` varchar(1) DEFAULT NULL COMMENT '原始状态代码',
  `new_stcd` varchar(1) DEFAULT NULL COMMENT '新状态代码',
  `execute_status` varchar(20) NOT NULL DEFAULT 'PENDING' COMMENT '执行状态：PENDING-待执行，SUCCESS-成功，FAILED-失败',
  `execute_result` text COMMENT '执行结果',
  `created_by` varchar(20) NOT NULL COMMENT '创建人用户编号',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` varchar(20) DEFAULT NULL COMMENT '更新人用户编号',
  `updated_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint DEFAULT '0' COMMENT '是否删除：0-否，1-是',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_uuid` (`uuid`),
  KEY `idx_change_id` (`change_id`),
  KEY `idx_change_no` (`change_no`),
  KEY `idx_dict_id` (`dict_id`),
  KEY `idx_change_operation` (`change_operation`),
  KEY `idx_item_order` (`item_order`),
  KEY `idx_execute_status` (`execute_status`),
  KEY `idx_is_deleted` (`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='字典项变更明细表';

-- ----------------------------
-- 业务类型注册表 - 添加数据字典变更业务类型
-- ----------------------------
INSERT INTO `biz_business_type` (`uuid`, `type_code`, `type_name`, `main_table_name`, `id_field_name`, `code_field_name`, `status_field_name`, `title_field_name`, `is_active`, `description`, `created_by`, `created_time`, `updated_by`, `updated_time`, `is_deleted`)
VALUES (REPLACE(UUID(), '-', ''), 'DICT_CHANGE', '数据字典变更', 'biz_ddct_type_change', 'uuid', 'change_no', 'approve_status', 'change_no', 1, '数据字典变更业务类型', 'system', NOW(), 'system', NOW(), 0)
ON DUPLICATE KEY UPDATE `is_active` = 1, `updated_by` = 'system', `updated_time` = NOW();

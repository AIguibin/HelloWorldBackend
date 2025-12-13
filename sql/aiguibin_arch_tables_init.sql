-- ----------------------------
-- Chat2DB export data , export time: 2025-12-14 03:55:53
-- ----------------------------
SET FOREIGN_KEY_CHECKS=0;
-- ----------------------------
-- Table structure for table biz_approval_flow
-- ----------------------------
DROP TABLE IF EXISTS `biz_approval_flow`;
CREATE TABLE `biz_approval_flow` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '自增ID，仅做序号',
  `flow_id` varchar(32) NOT NULL COMMENT '流程ID，如FLOW_CHG_001',
  `flow_name` varchar(100) NOT NULL COMMENT '流程名称',
  `business_type` varchar(32) NOT NULL COMMENT '业务类型：CHANGE_RECORD-变更记录，RELEASE-发版记录，DB_CHANGE-数据库变更，CONFIG_CHANGE-配置变更，RESOURCE_APPLY-资源申请',
  `description` text COMMENT '流程描述',
  `is_active` tinyint DEFAULT '1' COMMENT '是否激活：0-否，1-是',
  `is_default` tinyint DEFAULT '0' COMMENT '是否默认流程：0-否，1-是',
  `created_by` varchar(20) NOT NULL COMMENT '创建人用户编号',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` varchar(20) DEFAULT NULL COMMENT '更新人用户编号',
  `updated_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint DEFAULT '0' COMMENT '是否删除：0-否，1-是',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_flow_id` (`flow_id`),
  UNIQUE KEY `uk_flow_business_type` (`business_type`,`is_default`) COMMENT '每种业务类型只能有一个默认流程',
  KEY `idx_business_type` (`business_type`),
  KEY `idx_is_active` (`is_active`),
  KEY `idx_is_deleted` (`is_deleted`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='审批流程定义表';

-- ----------------------------
-- Records of biz_approval_flow
-- ----------------------------
INSERT INTO `biz_approval_flow` (`id`,`flow_id`,`flow_name`,`business_type`,`description`,`is_active`,`is_default`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('1','FLOW_CHG_001','变更记录审批流程','CHANGE_RECORD','变更记录的标准审批流程',1,1,'administrator','2025-12-12 22:49:24',NULL,NULL,0);
-- ----------------------------
-- Table structure for table biz_approval_log
-- ----------------------------
DROP TABLE IF EXISTS `biz_approval_log`;
CREATE TABLE `biz_approval_log` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '自增ID，仅做序号',
  `log_id` varchar(32) NOT NULL COMMENT '日志ID，如LOG_20241212_0001',
  `task_id` varchar(32) NOT NULL COMMENT '关联任务ID',
  `flow_id` varchar(32) NOT NULL COMMENT '所属流程ID',
  `node_id` varchar(32) NOT NULL COMMENT '所属节点ID',
  `business_type` varchar(32) NOT NULL COMMENT '业务类型',
  `business_id` bigint unsigned NOT NULL COMMENT '业务数据ID',
  `business_code` varchar(50) NOT NULL COMMENT '业务数据编码',
  `operation_type` varchar(32) NOT NULL COMMENT '操作类型：SUBMIT-提交，APPROVE-通过，REJECT-拒绝，TRANSFER-转办，CANCEL-取消',
  `operator_num` varchar(20) NOT NULL COMMENT '操作人用户编号',
  `operator_name` varchar(50) NOT NULL COMMENT '操作人姓名',
  `operation_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  `operation_remark` text COMMENT '操作备注',
  `before_status` varchar(32) DEFAULT NULL COMMENT '操作前状态',
  `after_status` varchar(32) DEFAULT NULL COMMENT '操作后状态',
  `created_by` varchar(20) NOT NULL COMMENT '创建人用户编号',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `is_deleted` tinyint DEFAULT '0' COMMENT '是否删除：0-否，1-是',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_log_id` (`log_id`),
  KEY `idx_task_id` (`task_id`),
  KEY `idx_flow_id` (`flow_id`),
  KEY `idx_node_id` (`node_id`),
  KEY `idx_business_type` (`business_type`),
  KEY `idx_business_id` (`business_id`),
  KEY `idx_operation_type` (`operation_type`),
  KEY `idx_operator_num` (`operator_num`),
  KEY `idx_operation_time` (`operation_time`),
  KEY `idx_is_deleted` (`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='审批流转记录表';

-- ----------------------------
-- Records of biz_approval_log
-- ----------------------------
-- ----------------------------
-- Table structure for table biz_approval_node
-- ----------------------------
DROP TABLE IF EXISTS `biz_approval_node`;
CREATE TABLE `biz_approval_node` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '自增ID，仅做序号',
  `node_id` varchar(32) NOT NULL COMMENT '节点ID，如NODE_TECH_REVIEW',
  `flow_id` varchar(32) NOT NULL COMMENT '所属流程ID',
  `node_name` varchar(100) NOT NULL COMMENT '节点名称',
  `node_order` int DEFAULT '1' COMMENT '节点顺序',
  `node_type` varchar(32) NOT NULL COMMENT '节点类型：START-开始节点，NORMAL-普通节点，END-结束节点',
  `approver_num` varchar(20) NOT NULL COMMENT '审批人用户编号',
  `approver_name` varchar(50) NOT NULL COMMENT '审批人姓名',
  `is_active` tinyint DEFAULT '1' COMMENT '是否激活：0-否，1-是',
  `created_by` varchar(20) NOT NULL COMMENT '创建人用户编号',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` varchar(20) DEFAULT NULL COMMENT '更新人用户编号',
  `updated_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint DEFAULT '0' COMMENT '是否删除：0-否，1-是',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_node_id` (`node_id`),
  KEY `idx_flow_id` (`flow_id`),
  KEY `idx_node_order` (`node_order`),
  KEY `idx_approver_num` (`approver_num`),
  KEY `idx_is_active` (`is_active`),
  KEY `idx_is_deleted` (`is_deleted`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='审批节点定义表';

-- ----------------------------
-- Records of biz_approval_node
-- ----------------------------
INSERT INTO `biz_approval_node` (`id`,`node_id`,`flow_id`,`node_name`,`node_order`,`node_type`,`approver_num`,`approver_name`,`is_active`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('1','NODE_TECH_REVIEW','FLOW_CHG_001','技术评审','1','NORMAL','90004','赵六',1,'administrator','2025-12-12 22:49:24',NULL,NULL,0);
INSERT INTO `biz_approval_node` (`id`,`node_id`,`flow_id`,`node_name`,`node_order`,`node_type`,`approver_num`,`approver_name`,`is_active`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('2','NODE_MANAGER_APPROVE','FLOW_CHG_001','经理审批','2','NORMAL','90001','张三',1,'administrator','2025-12-12 22:49:24',NULL,NULL,0);
INSERT INTO `biz_approval_node` (`id`,`node_id`,`flow_id`,`node_name`,`node_order`,`node_type`,`approver_num`,`approver_name`,`is_active`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('3','NODE_FINAL_APPROVE','FLOW_CHG_001','最终审批','3','END','administrator','超级管理员',1,'administrator','2025-12-12 22:49:24',NULL,NULL,0);
-- ----------------------------
-- Table structure for table biz_approval_task
-- ----------------------------
DROP TABLE IF EXISTS `biz_approval_task`;
CREATE TABLE `biz_approval_task` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '自增ID，仅做序号',
  `task_id` varchar(32) NOT NULL COMMENT '任务ID，如TASK_20241212_0001',
  `flow_id` varchar(32) NOT NULL COMMENT '所属流程ID',
  `node_id` varchar(32) NOT NULL COMMENT '所属节点ID',
  `business_type` varchar(32) NOT NULL COMMENT '业务类型',
  `business_id` bigint unsigned NOT NULL COMMENT '业务数据ID',
  `business_code` varchar(50) NOT NULL COMMENT '业务数据编码',
  `approver_num` varchar(20) NOT NULL COMMENT '审批人用户编号',
  `approver_name` varchar(50) NOT NULL COMMENT '审批人姓名',
  `task_status` varchar(32) NOT NULL DEFAULT 'PENDING' COMMENT '任务状态：PENDING-待审批，APPROVED-已通过，REJECTED-已拒绝，CANCELED-已取消',
  `current_status` varchar(32) NOT NULL COMMENT '当前业务状态',
  `assign_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '分配时间',
  `approval_time` datetime DEFAULT NULL COMMENT '审批时间',
  `approval_remark` text COMMENT '审批备注',
  `created_by` varchar(20) NOT NULL COMMENT '创建人用户编号',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` varchar(20) DEFAULT NULL COMMENT '更新人用户编号',
  `updated_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint DEFAULT '0' COMMENT '是否删除：0-否，1-是',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_task_id` (`task_id`),
  KEY `idx_flow_id` (`flow_id`),
  KEY `idx_node_id` (`node_id`),
  KEY `idx_business_type` (`business_type`),
  KEY `idx_business_id` (`business_id`),
  KEY `idx_approver_num` (`approver_num`),
  KEY `idx_task_status` (`task_status`),
  KEY `idx_assign_time` (`assign_time`),
  KEY `idx_approval_time` (`approval_time`),
  KEY `idx_is_deleted` (`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='待办任务表';

-- ----------------------------
-- Records of biz_approval_task
-- ----------------------------
-- ----------------------------
-- Table structure for table biz_business_type
-- ----------------------------
DROP TABLE IF EXISTS `biz_business_type`;
CREATE TABLE `biz_business_type` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '自增ID，仅做序号',
  `type_code` varchar(32) NOT NULL COMMENT '业务类型编码，如CHANGE_RECORD',
  `type_name` varchar(100) NOT NULL COMMENT '业务类型名称，如变更记录',
  `main_table_name` varchar(50) NOT NULL COMMENT '主表名，如biz_change_record',
  `id_field_name` varchar(32) NOT NULL DEFAULT 'id' COMMENT '主键字段名',
  `code_field_name` varchar(32) NOT NULL COMMENT '编码字段名，如record_code',
  `status_field_name` varchar(32) NOT NULL DEFAULT 'current_status' COMMENT '状态字段名',
  `title_field_name` varchar(32) NOT NULL COMMENT '标题字段名，用于待办显示',
  `is_active` tinyint DEFAULT '1' COMMENT '是否激活：0-否，1-是',
  `description` text COMMENT '业务类型描述',
  `created_by` varchar(20) NOT NULL COMMENT '创建人用户编号',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` varchar(20) DEFAULT NULL COMMENT '更新人用户编号',
  `updated_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint DEFAULT '0' COMMENT '是否删除：0-否，1-是',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_type_code` (`type_code`),
  UNIQUE KEY `uk_main_table_name` (`main_table_name`),
  KEY `idx_is_active` (`is_active`),
  KEY `idx_is_deleted` (`is_deleted`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='业务类型注册表';

-- ----------------------------
-- Records of biz_business_type
-- ----------------------------
INSERT INTO `biz_business_type` (`id`,`type_code`,`type_name`,`main_table_name`,`id_field_name`,`code_field_name`,`status_field_name`,`title_field_name`,`is_active`,`description`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('1','CHANGE_RECORD','变更记录','biz_change_record','id','record_code','current_status','change_desc',1,'变更记录业务类型','administrator','2025-12-12 22:49:24',NULL,NULL,0);
-- ----------------------------
-- Table structure for table biz_change_history
-- ----------------------------
DROP TABLE IF EXISTS `biz_change_history`;
CREATE TABLE `biz_change_history` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '历史记录ID',
  `record_id` bigint unsigned NOT NULL COMMENT '关联的主记录ID',
  `record_code` varchar(20) NOT NULL COMMENT '变更记录编码',
  `operation_type` varchar(20) NOT NULL COMMENT '操作类型：CREATE/UPDATE/DELETE/APPROVE/REJECT',
  `operation_user_num` varchar(20) NOT NULL COMMENT '操作用户编号',
  `operation_user_name` varchar(50) NOT NULL COMMENT '操作用户姓名',
  `operation_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  `operation_description` varchar(500) DEFAULT NULL COMMENT '操作描述',
  `current_status` varchar(50) DEFAULT NULL COMMENT '操作时的当前状态',
  `release_date` date DEFAULT NULL COMMENT '发布日期',
  `defect_number` varchar(100) DEFAULT NULL COMMENT '缺陷编号',
  `group_name` varchar(100) DEFAULT NULL COMMENT '组别',
  `developer_num` varchar(20) DEFAULT NULL COMMENT '开发负责人用户编号',
  `developer_name` varchar(50) DEFAULT NULL COMMENT '开发负责人姓名',
  `branch_name` varchar(100) DEFAULT NULL COMMENT '分支名称',
  `service_name` varchar(100) DEFAULT NULL COMMENT '服务名称',
  `problem_description` text COMMENT '问题描述',
  `impact_analysis` text COMMENT '问题影响分析',
  `solution` text COMMENT '解决方案',
  `involve_external_system` tinyint DEFAULT NULL COMMENT '是否涉及外围系统',
  `cross_service` tinyint DEFAULT NULL COMMENT '是否跨服务',
  `code_list` text COMMENT '代码清单',
  `remark` text COMMENT '备注说明',
  `version` varchar(50) DEFAULT NULL COMMENT '版本号',
  `change_desc` text COMMENT '变更描述',
  `develop_type` varchar(32) DEFAULT NULL COMMENT '开发类别',
  `org_code` varchar(10) DEFAULT NULL COMMENT '所属机构编码',
  `dept_code` varchar(15) DEFAULT NULL COMMENT '所属部门编码',
  `approver_num` varchar(20) DEFAULT NULL COMMENT '审批人用户编号',
  `approver_name` varchar(50) DEFAULT NULL COMMENT '审批人姓名',
  `approval_time` datetime DEFAULT NULL COMMENT '审批时间',
  `approval_remark` text COMMENT '审批备注',
  `approval_task_id` varchar(32) DEFAULT NULL COMMENT '关联审批任务ID',
  `approval_log_id` varchar(32) DEFAULT NULL COMMENT '关联审批日志ID',
  `approval_operation_type` varchar(32) DEFAULT NULL COMMENT '审批操作类型',
  PRIMARY KEY (`id`),
  KEY `idx_record_id` (`record_id`),
  KEY `idx_record_code` (`record_code`),
  KEY `idx_operation_time` (`operation_time`),
  KEY `idx_operation_user_num` (`operation_user_num`),
  KEY `idx_operation_type` (`operation_type`),
  KEY `idx_current_status` (`current_status`),
  KEY `idx_approval_task_id` (`approval_task_id`),
  KEY `idx_approval_log_id` (`approval_log_id`),
  KEY `idx_approval_operation_type` (`approval_operation_type`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='变更历史表';

-- ----------------------------
-- Records of biz_change_history
-- ----------------------------
INSERT INTO `biz_change_history` (`id`,`record_id`,`record_code`,`operation_type`,`operation_user_num`,`operation_user_name`,`operation_time`,`operation_description`,`current_status`,`release_date`,`defect_number`,`group_name`,`developer_num`,`developer_name`,`branch_name`,`service_name`,`problem_description`,`impact_analysis`,`solution`,`involve_external_system`,`cross_service`,`code_list`,`remark`,`version`,`change_desc`,`develop_type`,`org_code`,`dept_code`,`approver_num`,`approver_name`,`approval_time`,`approval_remark`,`approval_task_id`,`approval_log_id`,`approval_operation_type`)  VALUES ('1','1','CHG202501010001','CREATE','90001','张三','2025-01-10 09:30:00','创建变更记录','PENDING_APPROVAL','2025-01-15','DEF-2025-001','前端组','90001','张三','feature/user-auth','user-service','用户登录认证存在安全漏洞','可能导致用户信息泄露，影响所有使用该服务的用户','升级认证算法，增加二次验证',1,0,'UserController.java, AuthService.java, security-config.xml','需要同步更新前端认证逻辑','v2.5.1','用户认证安全升级','FRONTEND','01000','01000D001',NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `biz_change_history` (`id`,`record_id`,`record_code`,`operation_type`,`operation_user_num`,`operation_user_name`,`operation_time`,`operation_description`,`current_status`,`release_date`,`defect_number`,`group_name`,`developer_num`,`developer_name`,`branch_name`,`service_name`,`problem_description`,`impact_analysis`,`solution`,`involve_external_system`,`cross_service`,`code_list`,`remark`,`version`,`change_desc`,`develop_type`,`org_code`,`dept_code`,`approver_num`,`approver_name`,`approval_time`,`approval_remark`,`approval_task_id`,`approval_log_id`,`approval_operation_type`)  VALUES ('2','1','CHG202501010001','UPDATE','90001','张三','2025-01-11 10:15:00','更新解决方案','PENDING_REVIEW','2025-01-15','DEF-2025-001','前端组','90001','张三','feature/user-auth','user-service','用户登录认证存在安全漏洞','可能导致用户信息泄露，影响所有使用该服务的用户','升级认证算法，增加二次验证和日志审计',1,0,'UserController.java, AuthService.java, security-config.xml, AuditInterceptor.java','需要同步更新前端认证逻辑','v2.5.1','用户认证安全升级','FRONTEND','01000','01000D001',NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `biz_change_history` (`id`,`record_id`,`record_code`,`operation_type`,`operation_user_num`,`operation_user_name`,`operation_time`,`operation_description`,`current_status`,`release_date`,`defect_number`,`group_name`,`developer_num`,`developer_name`,`branch_name`,`service_name`,`problem_description`,`impact_analysis`,`solution`,`involve_external_system`,`cross_service`,`code_list`,`remark`,`version`,`change_desc`,`develop_type`,`org_code`,`dept_code`,`approver_num`,`approver_name`,`approval_time`,`approval_remark`,`approval_task_id`,`approval_log_id`,`approval_operation_type`)  VALUES ('3','1','CHG202501010001','APPROVE','90004','赵六','2025-01-13 14:20:00','审批通过','MERGED','2025-01-15','DEF-2025-001','前端组','90001','张三','feature/user-auth','user-service','用户登录认证存在安全漏洞','可能导致用户信息泄露，影响所有使用该服务的用户','升级认证算法，增加二次验证和日志审计',1,0,'UserController.java, AuthService.java, security-config.xml, AuditInterceptor.java','需要同步更新前端认证逻辑','v2.5.1','用户认证安全升级','FRONTEND','01000','01000D001',NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `biz_change_history` (`id`,`record_id`,`record_code`,`operation_type`,`operation_user_num`,`operation_user_name`,`operation_time`,`operation_description`,`current_status`,`release_date`,`defect_number`,`group_name`,`developer_num`,`developer_name`,`branch_name`,`service_name`,`problem_description`,`impact_analysis`,`solution`,`involve_external_system`,`cross_service`,`code_list`,`remark`,`version`,`change_desc`,`develop_type`,`org_code`,`dept_code`,`approver_num`,`approver_name`,`approval_time`,`approval_remark`,`approval_task_id`,`approval_log_id`,`approval_operation_type`)  VALUES ('4','2','CHG202501010002','CREATE','90002','李四','2025-01-12 11:15:00','创建变更记录','PENDING_APPROVAL',NULL,'DEF-2025-002','后端组','90002','李四','fix/order-bug','order-service','订单状态更新不及时','导致用户看到的订单状态不准确，可能引发投诉','优化数据库事务处理，增加状态同步机制',0,1,'OrderService.java, OrderRepository.java, OrderStatusSyncJob.java','需要测试并发场景下的状态同步','v1.3.2','订单状态同步优化','BACKEND','02000','02000D001',NULL,NULL,NULL,NULL,NULL,NULL,NULL);
-- ----------------------------
-- Table structure for table biz_change_record
-- ----------------------------
DROP TABLE IF EXISTS `biz_change_record`;
CREATE TABLE `biz_change_record` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '序号',
  `record_code` varchar(20) NOT NULL COMMENT '变更记录编码，CHG+年月日+4位序列',
  `current_status` varchar(50) DEFAULT 'PENDING_APPROVAL' COMMENT '当前状态（关联字典）',
  `release_date` date DEFAULT NULL COMMENT '发布日期',
  `defect_number` varchar(100) DEFAULT NULL COMMENT '缺陷编号',
  `group_name` varchar(100) DEFAULT NULL COMMENT '组别',
  `developer_num` varchar(20) DEFAULT NULL COMMENT '开发负责人用户编号',
  `developer_name` varchar(50) DEFAULT NULL COMMENT '开发负责人姓名',
  `branch_name` varchar(100) DEFAULT NULL COMMENT '分支名称',
  `service_name` varchar(100) DEFAULT NULL COMMENT '服务名称',
  `problem_description` text COMMENT '问题描述',
  `impact_analysis` text COMMENT '问题影响分析',
  `solution` text COMMENT '解决方案',
  `involve_external_system` tinyint DEFAULT '0' COMMENT '是否涉及外围系统：0-否，1-是',
  `cross_service` tinyint DEFAULT '0' COMMENT '是否跨服务：0-否，1-是',
  `code_list` text COMMENT '代码清单',
  `remark` text COMMENT '备注说明',
  `version` varchar(50) DEFAULT NULL COMMENT '版本号',
  `change_desc` text COMMENT '变更描述',
  `develop_type` varchar(32) DEFAULT NULL COMMENT '开发类别（关联字典）',
  `org_code` varchar(10) DEFAULT NULL COMMENT '所属机构编码',
  `dept_code` varchar(15) DEFAULT NULL COMMENT '所属部门编码',
  `approver_num` varchar(20) DEFAULT NULL COMMENT '审批人用户编号',
  `approver_name` varchar(50) DEFAULT NULL COMMENT '审批人姓名',
  `approval_time` datetime DEFAULT NULL COMMENT '审批时间',
  `approval_remark` text COMMENT '审批备注',
  `created_by` varchar(20) NOT NULL COMMENT '创建人用户编号',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` varchar(20) DEFAULT NULL COMMENT '更新人用户编号',
  `updated_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint DEFAULT '0' COMMENT '是否删除：0-否，1-是',
  `flow_id` varchar(32) DEFAULT NULL COMMENT '关联流程ID',
  `current_node_id` varchar(32) DEFAULT NULL COMMENT '当前节点ID',
  `approval_instance_id` varchar(32) DEFAULT NULL COMMENT '审批实例ID',
  `approval_status` varchar(32) DEFAULT 'DRAFT' COMMENT '审批状态：DRAFT-草稿，PENDING-待审批，APPROVED-已通过，REJECTED-已拒绝，CANCELED-已取消',
  `submit_time` datetime DEFAULT NULL COMMENT '提交审批时间',
  `reject_reason` text COMMENT '拒绝原因',
  `reject_node_id` varchar(32) DEFAULT NULL COMMENT '拒绝节点ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_record_code` (`record_code`),
  KEY `idx_release_date` (`release_date`),
  KEY `idx_defect_number` (`defect_number`),
  KEY `idx_service_name` (`service_name`),
  KEY `idx_developer_num` (`developer_num`),
  KEY `idx_group_name` (`group_name`),
  KEY `idx_current_status` (`current_status`),
  KEY `idx_org_code` (`org_code`),
  KEY `idx_dept_code` (`dept_code`),
  KEY `idx_approver_num` (`approver_num`),
  KEY `idx_flow_id` (`flow_id`),
  KEY `idx_current_node_id` (`current_node_id`),
  KEY `idx_approval_instance_id` (`approval_instance_id`),
  KEY `idx_approval_status` (`approval_status`),
  KEY `idx_submit_time` (`submit_time`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='变更记录表';

-- ----------------------------
-- Records of biz_change_record
-- ----------------------------
INSERT INTO `biz_change_record` (`id`,`record_code`,`current_status`,`release_date`,`defect_number`,`group_name`,`developer_num`,`developer_name`,`branch_name`,`service_name`,`problem_description`,`impact_analysis`,`solution`,`involve_external_system`,`cross_service`,`code_list`,`remark`,`version`,`change_desc`,`develop_type`,`org_code`,`dept_code`,`approver_num`,`approver_name`,`approval_time`,`approval_remark`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`,`flow_id`,`current_node_id`,`approval_instance_id`,`approval_status`,`submit_time`,`reject_reason`,`reject_node_id`)  VALUES ('1','CHG202501010001','RELEASED','2025-01-15','DEF-2025-001','前端组','90001','张三','feature/user-auth','user-service','用户登录认证存在安全漏洞','可能导致用户信息泄露，影响所有使用该服务的用户','升级认证算法，增加二次验证',1,0,'UserController.java, AuthService.java, security-config.xml','需要同步更新前端认证逻辑','v2.5.1','用户认证安全升级','FRONTEND','01000','01000D001',NULL,NULL,NULL,NULL,'90001','2025-01-10 09:30:00','90001','2025-01-15 14:20:00',0,NULL,NULL,NULL,'DRAFT',NULL,NULL,NULL);
INSERT INTO `biz_change_record` (`id`,`record_code`,`current_status`,`release_date`,`defect_number`,`group_name`,`developer_num`,`developer_name`,`branch_name`,`service_name`,`problem_description`,`impact_analysis`,`solution`,`involve_external_system`,`cross_service`,`code_list`,`remark`,`version`,`change_desc`,`develop_type`,`org_code`,`dept_code`,`approver_num`,`approver_name`,`approval_time`,`approval_remark`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`,`flow_id`,`current_node_id`,`approval_instance_id`,`approval_status`,`submit_time`,`reject_reason`,`reject_node_id`)  VALUES ('2','CHG202501010002','PENDING_APPROVAL',NULL,'DEF-2025-002','后端组','90002','李四','fix/order-bug','order-service','订单状态更新不及时','导致用户看到的订单状态不准确，可能引发投诉','优化数据库事务处理，增加状态同步机制',0,1,'OrderService.java, OrderRepository.java, OrderStatusSyncJob.java','需要测试并发场景下的状态同步','v1.3.2','订单状态同步优化','BACKEND','02000','02000D001',NULL,NULL,NULL,NULL,'90002','2025-01-12 11:15:00','90002','2025-01-12 11:15:00',0,NULL,NULL,NULL,'DRAFT',NULL,NULL,NULL);
INSERT INTO `biz_change_record` (`id`,`record_code`,`current_status`,`release_date`,`defect_number`,`group_name`,`developer_num`,`developer_name`,`branch_name`,`service_name`,`problem_description`,`impact_analysis`,`solution`,`involve_external_system`,`cross_service`,`code_list`,`remark`,`version`,`change_desc`,`develop_type`,`org_code`,`dept_code`,`approver_num`,`approver_name`,`approval_time`,`approval_remark`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`,`flow_id`,`current_node_id`,`approval_instance_id`,`approval_status`,`submit_time`,`reject_reason`,`reject_node_id`)  VALUES ('3','CHG202501010003','MERGED',NULL,'DEF-2025-003','脚本组','90004','赵六','feature/data-migration','data-service','数据迁移脚本执行效率低','大数据量迁移耗时过长，影响业务连续性','优化SQL查询，增加分批处理机制',0,0,'migration_v1.sql, migration_v2.sql, DataMigrationUtil.java','需要在测试环境充分验证后再上线','v3.0.0','数据迁移性能优化','SCRIPT','01001','01001D001',NULL,NULL,NULL,NULL,'90004','2025-01-14 14:45:00','90004','2025-01-16 10:30:00',0,NULL,NULL,NULL,'DRAFT',NULL,NULL,NULL);
-- ----------------------------
-- Table structure for table sys_data_rule
-- ----------------------------
DROP TABLE IF EXISTS `sys_data_rule`;
CREATE TABLE `sys_data_rule` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '自增ID，仅做序号',
  `rule_code` varchar(10) NOT NULL COMMENT '规则编码，10位',
  `rule_name` varchar(100) NOT NULL COMMENT '规则名称',
  `rule_type` tinyint NOT NULL COMMENT '规则类型：1-预定义，2-自定义SQL，3-组合规则',
  `perm_code` varchar(5) DEFAULT NULL COMMENT '关联的权限编码',
  `entity_type` varchar(50) NOT NULL COMMENT '业务实体类型（如：sys_user, sys_order）',
  `scope_type` tinyint DEFAULT NULL COMMENT '预定义范围：1-全部，2-本机构，3-本部门，4-本人',
  `include_children` tinyint DEFAULT '1' COMMENT '是否包含下级：0-否，1-是（针对机构、部门）',
  `custom_sql` text COMMENT '自定义SQL条件（WHERE子句内容）',
  `rule_expression` json DEFAULT NULL COMMENT '组合规则表达式（JSON格式）',
  `rule_priority` tinyint DEFAULT '1' COMMENT '规则优先级：1-高，2-中，3-低',
  `is_global` tinyint DEFAULT '0' COMMENT '是否全局规则：0-否，1-是',
  `status` tinyint DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
  `description` varchar(500) DEFAULT NULL COMMENT '规则描述',
  `created_by` varchar(20) NOT NULL COMMENT '创建人用户编号',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` varchar(20) DEFAULT NULL COMMENT '更新人用户编号',
  `updated_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint DEFAULT '0' COMMENT '是否删除：0-否，1-是',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_rule_code` (`rule_code`),
  KEY `idx_perm_code` (`perm_code`),
  KEY `idx_entity_type` (`entity_type`),
  KEY `idx_rule_type` (`rule_type`),
  KEY `idx_status` (`status`),
  KEY `idx_is_global` (`is_global`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='数据权限规则表';

-- ----------------------------
-- Records of sys_data_rule
-- ----------------------------
INSERT INTO `sys_data_rule` (`id`,`rule_code`,`rule_name`,`rule_type`,`perm_code`,`entity_type`,`scope_type`,`include_children`,`custom_sql`,`rule_expression`,`rule_priority`,`is_global`,`status`,`description`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('1','DR00000001','超级管理员数据规则',1,'P0012','biz_change_record',1,1,NULL,NULL,1,1,1,'超级管理员可以查看所有变更数据','administrator','2025-12-12 22:49:24',NULL,NULL,0);
INSERT INTO `sys_data_rule` (`id`,`rule_code`,`rule_name`,`rule_type`,`perm_code`,`entity_type`,`scope_type`,`include_children`,`custom_sql`,`rule_expression`,`rule_priority`,`is_global`,`status`,`description`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('2','DR00000002','架构师数据规则',1,'P0013','biz_change_record',2,1,NULL,NULL,2,0,1,'架构师可以查看本机构及下属机构变更数据','administrator','2025-12-12 22:49:24',NULL,NULL,0);
INSERT INTO `sys_data_rule` (`id`,`rule_code`,`rule_name`,`rule_type`,`perm_code`,`entity_type`,`scope_type`,`include_children`,`custom_sql`,`rule_expression`,`rule_priority`,`is_global`,`status`,`description`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('3','DR00000003','开发工程师数据规则',1,'P0014','biz_change_record',4,0,NULL,NULL,3,0,1,'开发工程师只能查看自己的变更数据','administrator','2025-12-12 22:49:24',NULL,NULL,0);
-- ----------------------------
-- Table structure for table sys_dept
-- ----------------------------
DROP TABLE IF EXISTS `sys_dept`;
CREATE TABLE `sys_dept` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '自增ID，仅做序号',
  `dept_code` varchar(15) NOT NULL COMMENT '部门编码，15位：机构编码(10位)+D+4位序列',
  `dept_name` varchar(100) NOT NULL COMMENT '部门名称',
  `org_code` varchar(10) NOT NULL COMMENT '所属机构编码',
  `manager_num` varchar(20) DEFAULT NULL COMMENT '部门负责人用户编号',
  `sort_order` int DEFAULT '100' COMMENT '排序号',
  `status` tinyint DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
  `description` varchar(500) DEFAULT NULL COMMENT '部门描述',
  `created_by` varchar(20) NOT NULL COMMENT '创建人用户编号',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` varchar(20) DEFAULT NULL COMMENT '更新人用户编号',
  `updated_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint DEFAULT '0' COMMENT '是否删除：0-否，1-是',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_dept_code` (`dept_code`),
  KEY `idx_org_code` (`org_code`),
  KEY `idx_manager_num` (`manager_num`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='部门信息表';

-- ----------------------------
-- Records of sys_dept
-- ----------------------------
INSERT INTO `sys_dept` (`id`,`dept_code`,`dept_name`,`org_code`,`manager_num`,`sort_order`,`status`,`description`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('1','99999D001','研发中心','99999','administrator','1',1,'超级机构研发中心','administrator','2025-12-12 22:49:24',NULL,NULL,0);
INSERT INTO `sys_dept` (`id`,`dept_code`,`dept_name`,`org_code`,`manager_num`,`sort_order`,`status`,`description`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('2','01000D001','总部研发部','01000','90001','1',1,'总部研发部门','administrator','2025-12-12 22:49:24',NULL,NULL,0);
INSERT INTO `sys_dept` (`id`,`dept_code`,`dept_name`,`org_code`,`manager_num`,`sort_order`,`status`,`description`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('3','01000D002','总部产品部','01000','90003','2',1,'总部产品部门','administrator','2025-12-12 22:49:24',NULL,NULL,0);
INSERT INTO `sys_dept` (`id`,`dept_code`,`dept_name`,`org_code`,`manager_num`,`sort_order`,`status`,`description`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('4','01001D001','技术研发部','01001','90004','1',1,'技术研发部门','administrator','2025-12-12 22:49:24',NULL,NULL,0);
INSERT INTO `sys_dept` (`id`,`dept_code`,`dept_name`,`org_code`,`manager_num`,`sort_order`,`status`,`description`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('5','02000D001','北京研发部','02000','90002','1',1,'北京研发部门','administrator','2025-12-12 22:49:24',NULL,NULL,0);
-- ----------------------------
-- Table structure for table sys_dict_item
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_item`;
CREATE TABLE `sys_dict_item` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `dict_type_code` varchar(50) NOT NULL COMMENT '字典类型编码',
  `dict_value` varchar(50) NOT NULL COMMENT '字典值',
  `dict_label` varchar(100) NOT NULL COMMENT '字典标签',
  `group_code` varchar(50) DEFAULT NULL COMMENT '分组编码',
  `group_name` varchar(100) DEFAULT NULL COMMENT '分组名称',
  `sort_order` int DEFAULT '100' COMMENT '排序',
  `status` tinyint DEFAULT '1' COMMENT '状态：1-启用 0-禁用',
  `created_by` varchar(20) NOT NULL COMMENT '创建人用户编号',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` varchar(20) DEFAULT NULL COMMENT '更新人用户编号',
  `updated_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint DEFAULT '0' COMMENT '是否删除：0-否，1-是',
  PRIMARY KEY (`id`),
  KEY `idx_dict_type_code` (`dict_type_code`),
  KEY `idx_group_code` (`group_code`),
  KEY `idx_dict_value` (`dict_value`),
  KEY `idx_status` (`status`),
  KEY `idx_is_deleted` (`is_deleted`)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='字典项表';

-- ----------------------------
-- Records of sys_dict_item
-- ----------------------------
INSERT INTO `sys_dict_item` (`id`,`dict_type_code`,`dict_value`,`dict_label`,`group_code`,`group_name`,`sort_order`,`status`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('1','CURRENT_STATUS','PENDING_APPROVAL','待审批',NULL,NULL,'1',1,'administrator','2025-12-12 22:49:24',NULL,NULL,0);
INSERT INTO `sys_dict_item` (`id`,`dict_type_code`,`dict_value`,`dict_label`,`group_code`,`group_name`,`sort_order`,`status`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('2','CURRENT_STATUS','PENDING_REVIEW','待评审',NULL,NULL,'2',1,'administrator','2025-12-12 22:49:24',NULL,NULL,0);
INSERT INTO `sys_dict_item` (`id`,`dict_type_code`,`dict_value`,`dict_label`,`group_code`,`group_name`,`sort_order`,`status`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('3','CURRENT_STATUS','PENDING_MERGE','待合版',NULL,NULL,'3',1,'administrator','2025-12-12 22:49:24',NULL,NULL,0);
INSERT INTO `sys_dict_item` (`id`,`dict_type_code`,`dict_value`,`dict_label`,`group_code`,`group_name`,`sort_order`,`status`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('4','CURRENT_STATUS','MERGED','已合版',NULL,NULL,'4',1,'administrator','2025-12-12 22:49:24',NULL,NULL,0);
INSERT INTO `sys_dict_item` (`id`,`dict_type_code`,`dict_value`,`dict_label`,`group_code`,`group_name`,`sort_order`,`status`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('5','CURRENT_STATUS','PENDING_RELEASE','待发布',NULL,NULL,'5',1,'administrator','2025-12-12 22:49:24',NULL,NULL,0);
INSERT INTO `sys_dict_item` (`id`,`dict_type_code`,`dict_value`,`dict_label`,`group_code`,`group_name`,`sort_order`,`status`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('6','CURRENT_STATUS','RELEASED','已发布',NULL,NULL,'6',1,'administrator','2025-12-12 22:49:24',NULL,NULL,0);
INSERT INTO `sys_dict_item` (`id`,`dict_type_code`,`dict_value`,`dict_label`,`group_code`,`group_name`,`sort_order`,`status`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('7','CURRENT_STATUS','REJECTED','已驳回',NULL,NULL,'7',1,'administrator','2025-12-12 22:49:24',NULL,NULL,0);
INSERT INTO `sys_dict_item` (`id`,`dict_type_code`,`dict_value`,`dict_label`,`group_code`,`group_name`,`sort_order`,`status`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('8','CURRENT_STATUS','CANCELLED','已取消',NULL,NULL,'8',1,'administrator','2025-12-12 22:49:24',NULL,NULL,0);
INSERT INTO `sys_dict_item` (`id`,`dict_type_code`,`dict_value`,`dict_label`,`group_code`,`group_name`,`sort_order`,`status`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('9','DEVELOP_TYPE','FRONTEND','前端',NULL,NULL,'1',1,'administrator','2025-12-12 22:49:24',NULL,NULL,0);
INSERT INTO `sys_dict_item` (`id`,`dict_type_code`,`dict_value`,`dict_label`,`group_code`,`group_name`,`sort_order`,`status`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('10','DEVELOP_TYPE','BACKEND','后端',NULL,NULL,'2',1,'administrator','2025-12-12 22:49:24',NULL,NULL,0);
INSERT INTO `sys_dict_item` (`id`,`dict_type_code`,`dict_value`,`dict_label`,`group_code`,`group_name`,`sort_order`,`status`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('11','DEVELOP_TYPE','SCRIPT','脚本',NULL,NULL,'3',1,'administrator','2025-12-12 22:49:24',NULL,NULL,0);
INSERT INTO `sys_dict_item` (`id`,`dict_type_code`,`dict_value`,`dict_label`,`group_code`,`group_name`,`sort_order`,`status`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('12','DEVELOP_TYPE','CONFIG','配置',NULL,NULL,'4',1,'administrator','2025-12-12 22:49:24',NULL,NULL,0);
INSERT INTO `sys_dict_item` (`id`,`dict_type_code`,`dict_value`,`dict_label`,`group_code`,`group_name`,`sort_order`,`status`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('13','DEVELOP_TYPE','DATABASE','数据库',NULL,NULL,'5',1,'administrator','2025-12-12 22:49:24',NULL,NULL,0);
INSERT INTO `sys_dict_item` (`id`,`dict_type_code`,`dict_value`,`dict_label`,`group_code`,`group_name`,`sort_order`,`status`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('14','DEVELOP_TYPE','OTHER','其他',NULL,NULL,'6',1,'administrator','2025-12-12 22:49:24',NULL,NULL,0);
INSERT INTO `sys_dict_item` (`id`,`dict_type_code`,`dict_value`,`dict_label`,`group_code`,`group_name`,`sort_order`,`status`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('15','YES_NO','1','是',NULL,NULL,'1',1,'administrator','2025-12-12 22:49:24',NULL,NULL,0);
INSERT INTO `sys_dict_item` (`id`,`dict_type_code`,`dict_value`,`dict_label`,`group_code`,`group_name`,`sort_order`,`status`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('16','YES_NO','0','否',NULL,NULL,'2',1,'administrator','2025-12-12 22:49:24',NULL,NULL,0);
INSERT INTO `sys_dict_item` (`id`,`dict_type_code`,`dict_value`,`dict_label`,`group_code`,`group_name`,`sort_order`,`status`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('17','OPERATION_TYPE','CREATE','创建',NULL,NULL,'1',1,'administrator','2025-12-12 22:49:24',NULL,NULL,0);
INSERT INTO `sys_dict_item` (`id`,`dict_type_code`,`dict_value`,`dict_label`,`group_code`,`group_name`,`sort_order`,`status`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('18','OPERATION_TYPE','UPDATE','更新',NULL,NULL,'2',1,'administrator','2025-12-12 22:49:24',NULL,NULL,0);
INSERT INTO `sys_dict_item` (`id`,`dict_type_code`,`dict_value`,`dict_label`,`group_code`,`group_name`,`sort_order`,`status`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('19','OPERATION_TYPE','DELETE','删除',NULL,NULL,'3',1,'administrator','2025-12-12 22:49:24',NULL,NULL,0);
INSERT INTO `sys_dict_item` (`id`,`dict_type_code`,`dict_value`,`dict_label`,`group_code`,`group_name`,`sort_order`,`status`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('20','OPERATION_TYPE','APPROVE','审批',NULL,NULL,'4',1,'administrator','2025-12-12 22:49:24',NULL,NULL,0);
INSERT INTO `sys_dict_item` (`id`,`dict_type_code`,`dict_value`,`dict_label`,`group_code`,`group_name`,`sort_order`,`status`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('21','OPERATION_TYPE','REJECT','驳回',NULL,NULL,'5',1,'administrator','2025-12-12 22:49:24',NULL,NULL,0);
INSERT INTO `sys_dict_item` (`id`,`dict_type_code`,`dict_value`,`dict_label`,`group_code`,`group_name`,`sort_order`,`status`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('22','OPERATION_TYPE','LOGIN','登录',NULL,NULL,'6',1,'administrator','2025-12-12 22:49:24',NULL,NULL,0);
INSERT INTO `sys_dict_item` (`id`,`dict_type_code`,`dict_value`,`dict_label`,`group_code`,`group_name`,`sort_order`,`status`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('23','OPERATION_TYPE','LOGOUT','登出',NULL,NULL,'7',1,'administrator','2025-12-12 22:49:24',NULL,NULL,0);
INSERT INTO `sys_dict_item` (`id`,`dict_type_code`,`dict_value`,`dict_label`,`group_code`,`group_name`,`sort_order`,`status`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('24','OPERATION_TYPE','EXPORT','导出',NULL,NULL,'8',1,'administrator','2025-12-12 22:49:24',NULL,NULL,0);
-- ----------------------------
-- Table structure for table sys_dict_type
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_type`;
CREATE TABLE `sys_dict_type` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `dict_type_code` varchar(50) NOT NULL COMMENT '字典类型编码',
  `dict_type_name` varchar(100) NOT NULL COMMENT '字典类型名称',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `sort_order` int DEFAULT '100' COMMENT '排序',
  `status` tinyint DEFAULT '1' COMMENT '状态：1-启用 0-禁用',
  `created_by` varchar(20) NOT NULL COMMENT '创建人用户编号',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` varchar(20) DEFAULT NULL COMMENT '更新人用户编号',
  `updated_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint DEFAULT '0' COMMENT '是否删除：0-否，1-是',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_dict_type_code` (`dict_type_code`),
  KEY `idx_status` (`status`),
  KEY `idx_is_deleted` (`is_deleted`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='字典类型表';

-- ----------------------------
-- Records of sys_dict_type
-- ----------------------------
INSERT INTO `sys_dict_type` (`id`,`dict_type_code`,`dict_type_name`,`description`,`sort_order`,`status`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('1','CURRENT_STATUS','当前状态','变更记录的当前状态','1',1,'administrator','2025-12-12 22:49:24',NULL,NULL,0);
INSERT INTO `sys_dict_type` (`id`,`dict_type_code`,`dict_type_name`,`description`,`sort_order`,`status`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('2','DEVELOP_TYPE','开发类别','开发任务的类别','2',1,'administrator','2025-12-12 22:49:24',NULL,NULL,0);
INSERT INTO `sys_dict_type` (`id`,`dict_type_code`,`dict_type_name`,`description`,`sort_order`,`status`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('3','YES_NO','是否选项','通用的是/否选项','3',1,'administrator','2025-12-12 22:49:24',NULL,NULL,0);
INSERT INTO `sys_dict_type` (`id`,`dict_type_code`,`dict_type_name`,`description`,`sort_order`,`status`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('4','OPERATION_TYPE','操作类型','系统操作类型','4',1,'administrator','2025-12-12 22:49:24',NULL,NULL,0);
-- ----------------------------
-- Table structure for table sys_field_permission
-- ----------------------------
DROP TABLE IF EXISTS `sys_field_permission`;
CREATE TABLE `sys_field_permission` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '自增ID，仅做序号',
  `field_code` varchar(15) NOT NULL COMMENT '字段编码，15位',
  `entity_type` varchar(50) NOT NULL COMMENT '业务实体类型',
  `field_name` varchar(100) NOT NULL COMMENT '字段名称（数据库字段名）',
  `field_alias` varchar(100) NOT NULL COMMENT '字段别名（显示名称）',
  `role_code` varchar(5) NOT NULL COMMENT '角色编码',
  `perm_type` tinyint NOT NULL COMMENT '权限类型：1-可见，2-可编辑，3-必填，4-隐藏，5-只读',
  `condition_expression` json DEFAULT NULL COMMENT '条件表达式（JSON格式，满足条件时生效）',
  `sort_order` int DEFAULT '100' COMMENT '排序号',
  `status` tinyint DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
  `description` varchar(500) DEFAULT NULL COMMENT '字段权限描述',
  `created_by` varchar(20) NOT NULL COMMENT '创建人用户编号',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` varchar(20) DEFAULT NULL COMMENT '更新人用户编号',
  `updated_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint DEFAULT '0' COMMENT '是否删除：0-否，1-是',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_field_perm` (`entity_type`,`field_name`,`role_code`,`perm_type`),
  KEY `idx_field_code` (`field_code`),
  KEY `idx_entity_type` (`entity_type`),
  KEY `idx_role_code` (`role_code`),
  KEY `idx_perm_type` (`perm_type`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='字段权限表';

-- ----------------------------
-- Records of sys_field_permission
-- ----------------------------
-- ----------------------------
-- Table structure for table sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '自增ID，仅做序号',
  `menu_code` varchar(20) NOT NULL COMMENT '菜单编码，20位',
  `menu_name` varchar(100) NOT NULL COMMENT '菜单名称',
  `menu_type` tinyint NOT NULL COMMENT '类型：1-目录，2-页面，3-按钮',
  `parent_menu_code` varchar(20) DEFAULT NULL COMMENT '父菜单编码',
  `icon` varchar(100) DEFAULT NULL COMMENT '图标',
  `path` varchar(200) DEFAULT NULL COMMENT '路由路径（前端使用）',
  `component` varchar(200) DEFAULT NULL COMMENT '组件路径（前端使用）',
  `url` varchar(500) DEFAULT NULL COMMENT '访问URL（后端API路径）',
  `http_method` varchar(10) DEFAULT NULL COMMENT 'HTTP方法：GET,POST,PUT,DELETE等',
  `is_external` tinyint DEFAULT '0' COMMENT '是否外部链接：0-否，1-是',
  `is_cache` tinyint DEFAULT '1' COMMENT '是否缓存：0-否，1-是',
  `is_visible` tinyint DEFAULT '1' COMMENT '是否显示：0-否，1-是',
  `permission_key` varchar(100) DEFAULT NULL COMMENT '权限标识（如：user:view）',
  `sort_order` int DEFAULT '100' COMMENT '排序号',
  `status` tinyint DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
  `description` varchar(500) DEFAULT NULL COMMENT '菜单描述',
  `created_by` varchar(20) NOT NULL COMMENT '创建人用户编号',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` varchar(20) DEFAULT NULL COMMENT '更新人用户编号',
  `updated_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint DEFAULT '0' COMMENT '是否删除：0-否，1-是',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_menu_code` (`menu_code`),
  KEY `idx_parent_menu_code` (`parent_menu_code`),
  KEY `idx_menu_type` (`menu_type`),
  KEY `idx_permission_key` (`permission_key`),
  KEY `idx_path` (`path`(100)),
  KEY `idx_url` (`url`(100)),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='菜单/页面/按钮表';

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
INSERT INTO `sys_menu` (`id`,`menu_code`,`menu_name`,`menu_type`,`parent_menu_code`,`icon`,`path`,`component`,`url`,`http_method`,`is_external`,`is_cache`,`is_visible`,`permission_key`,`sort_order`,`status`,`description`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('1','M0000001','首页',1,NULL,'el-icon-s-home','/',NULL,NULL,NULL,0,1,1,NULL,'1',1,'系统首页目录','administrator','2025-12-12 22:49:24',NULL,NULL,0);
INSERT INTO `sys_menu` (`id`,`menu_code`,`menu_name`,`menu_type`,`parent_menu_code`,`icon`,`path`,`component`,`url`,`http_method`,`is_external`,`is_cache`,`is_visible`,`permission_key`,`sort_order`,`status`,`description`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('2','P0000001','待办事项',2,'M0000001',NULL,'/approval-todos','views/ApprovalTodoList',NULL,NULL,0,1,1,'approval:todo:list','1',1,'待办任务列表','administrator','2025-12-12 22:49:24',NULL,'2025-12-13 01:14:27',0);
INSERT INTO `sys_menu` (`id`,`menu_code`,`menu_name`,`menu_type`,`parent_menu_code`,`icon`,`path`,`component`,`url`,`http_method`,`is_external`,`is_cache`,`is_visible`,`permission_key`,`sort_order`,`status`,`description`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('3','M0000002','变更管理',1,NULL,'el-icon-edit-outline','/change-records',NULL,NULL,NULL,0,1,1,NULL,'2',1,'变更管理目录','administrator','2025-12-12 22:49:24',NULL,NULL,0);
INSERT INTO `sys_menu` (`id`,`menu_code`,`menu_name`,`menu_type`,`parent_menu_code`,`icon`,`path`,`component`,`url`,`http_method`,`is_external`,`is_cache`,`is_visible`,`permission_key`,`sort_order`,`status`,`description`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('4','P0000002','变更记录',2,'M0000002',NULL,'/change-records','views/ChangeRecordList',NULL,NULL,0,1,1,'change:record:list','1',1,'变更记录列表页面','administrator','2025-12-12 22:49:24',NULL,'2025-12-13 01:07:20',0);
INSERT INTO `sys_menu` (`id`,`menu_code`,`menu_name`,`menu_type`,`parent_menu_code`,`icon`,`path`,`component`,`url`,`http_method`,`is_external`,`is_cache`,`is_visible`,`permission_key`,`sort_order`,`status`,`description`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('5','B0000001','新增变更',3,'P0000002',NULL,NULL,NULL,'/api/change-records','POST',0,1,1,'change:record:create','1',1,'新增变更记录按钮','administrator','2025-12-12 22:49:24',NULL,NULL,0);
INSERT INTO `sys_menu` (`id`,`menu_code`,`menu_name`,`menu_type`,`parent_menu_code`,`icon`,`path`,`component`,`url`,`http_method`,`is_external`,`is_cache`,`is_visible`,`permission_key`,`sort_order`,`status`,`description`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('6','B0000002','编辑变更',3,'P0000002',NULL,NULL,NULL,'/api/change-records/*','PUT',0,1,1,'change:record:edit','2',1,'编辑变更记录按钮','administrator','2025-12-12 22:49:24',NULL,NULL,0);
INSERT INTO `sys_menu` (`id`,`menu_code`,`menu_name`,`menu_type`,`parent_menu_code`,`icon`,`path`,`component`,`url`,`http_method`,`is_external`,`is_cache`,`is_visible`,`permission_key`,`sort_order`,`status`,`description`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('7','B0000003','删除变更',3,'P0000002',NULL,NULL,NULL,'/api/change-records/*','DELETE',0,1,1,'change:record:delete','3',1,'删除变更记录按钮','administrator','2025-12-12 22:49:24',NULL,NULL,0);
INSERT INTO `sys_menu` (`id`,`menu_code`,`menu_name`,`menu_type`,`parent_menu_code`,`icon`,`path`,`component`,`url`,`http_method`,`is_external`,`is_cache`,`is_visible`,`permission_key`,`sort_order`,`status`,`description`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('8','B0000004','导出变更',3,'P0000002',NULL,NULL,NULL,'/api/change-records/export','GET',0,1,1,'change:record:export','4',1,'导出变更记录按钮','administrator','2025-12-12 22:49:24',NULL,NULL,0);
INSERT INTO `sys_menu` (`id`,`menu_code`,`menu_name`,`menu_type`,`parent_menu_code`,`icon`,`path`,`component`,`url`,`http_method`,`is_external`,`is_cache`,`is_visible`,`permission_key`,`sort_order`,`status`,`description`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('9','M0000003','系统设置',1,NULL,'el-icon-setting','/system-settings',NULL,NULL,NULL,0,1,1,NULL,'5',1,'系统设置目录','administrator','2025-12-12 22:49:24',NULL,NULL,0);
INSERT INTO `sys_menu` (`id`,`menu_code`,`menu_name`,`menu_type`,`parent_menu_code`,`icon`,`path`,`component`,`url`,`http_method`,`is_external`,`is_cache`,`is_visible`,`permission_key`,`sort_order`,`status`,`description`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('10','P0000003','用户管理',2,'M0000003',NULL,'/system-settings/users','views/UserManagement',NULL,NULL,0,1,1,'user:management','1',1,'用户管理页面','administrator','2025-12-12 22:49:24',NULL,NULL,0);
INSERT INTO `sys_menu` (`id`,`menu_code`,`menu_name`,`menu_type`,`parent_menu_code`,`icon`,`path`,`component`,`url`,`http_method`,`is_external`,`is_cache`,`is_visible`,`permission_key`,`sort_order`,`status`,`description`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('11','P0000004','角色管理',2,'M0000003',NULL,'/system-settings/roles','views/RoleManagement',NULL,NULL,0,1,1,'role:management','2',1,'角色管理页面','administrator','2025-12-12 22:49:24',NULL,NULL,0);
INSERT INTO `sys_menu` (`id`,`menu_code`,`menu_name`,`menu_type`,`parent_menu_code`,`icon`,`path`,`component`,`url`,`http_method`,`is_external`,`is_cache`,`is_visible`,`permission_key`,`sort_order`,`status`,`description`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('12','P0000005','菜单管理',2,'M0000003',NULL,'/system-settings/menus','views/MenuManagement',NULL,NULL,0,1,1,'menu:management','3',1,'菜单管理页面','administrator','2025-12-12 22:49:24',NULL,NULL,0);
INSERT INTO `sys_menu` (`id`,`menu_code`,`menu_name`,`menu_type`,`parent_menu_code`,`icon`,`path`,`component`,`url`,`http_method`,`is_external`,`is_cache`,`is_visible`,`permission_key`,`sort_order`,`status`,`description`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('13','P0000006','字典管理',2,'M0000003',NULL,'/system-settings/dicts','views/DictManagement',NULL,NULL,0,1,1,'dict:management','4',1,'字典管理页面','administrator','2025-12-12 22:49:24',NULL,NULL,0);
INSERT INTO `sys_menu` (`id`,`menu_code`,`menu_name`,`menu_type`,`parent_menu_code`,`icon`,`path`,`component`,`url`,`http_method`,`is_external`,`is_cache`,`is_visible`,`permission_key`,`sort_order`,`status`,`description`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('14','P0000007','操作日志',2,'M0000003',NULL,'/system-settings/logs','views/OperationLogs',NULL,NULL,0,1,1,'log:operation','5',1,'操作日志页面','administrator','2025-12-12 22:49:24',NULL,NULL,0);
-- ----------------------------
-- Table structure for table sys_operation_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_operation_log`;
CREATE TABLE `sys_operation_log` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `operator_num` varchar(20) NOT NULL COMMENT '操作人用户编号',
  `operator_name` varchar(50) NOT NULL COMMENT '操作人姓名',
  `operation_type` varchar(20) NOT NULL COMMENT '操作类型',
  `object_type` varchar(50) NOT NULL COMMENT '对象类型',
  `module` varchar(100) DEFAULT NULL COMMENT '模块名称',
  `object_id` bigint unsigned NOT NULL COMMENT '对象ID',
  `object_code` varchar(50) DEFAULT NULL COMMENT '对象编码',
  `result` varchar(20) NOT NULL COMMENT '结果：SUCCESS/FAIL',
  `message` varchar(500) DEFAULT NULL COMMENT '结果说明',
  `operation_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  `page_path` varchar(200) DEFAULT NULL COMMENT '页面路径',
  `button_name` varchar(100) DEFAULT NULL COMMENT '按钮名称',
  `ip_address` varchar(50) DEFAULT NULL COMMENT '操作IP地址',
  `user_agent` varchar(500) DEFAULT NULL COMMENT '用户代理',
  `request_params` text COMMENT '请求参数',
  `response_data` text COMMENT '响应数据',
  `duration_ms` int DEFAULT NULL COMMENT '操作耗时（毫秒）',
  PRIMARY KEY (`id`),
  KEY `idx_object` (`object_type`,`object_id`),
  KEY `idx_operator_time` (`operator_num`,`operation_time`),
  KEY `idx_object_code` (`object_code`),
  KEY `idx_operation_type` (`operation_type`),
  KEY `idx_result` (`result`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统操作日志表';

-- ----------------------------
-- Records of sys_operation_log
-- ----------------------------
INSERT INTO `sys_operation_log` (`id`,`operator_num`,`operator_name`,`operation_type`,`object_type`,`module`,`object_id`,`object_code`,`result`,`message`,`operation_time`,`page_path`,`button_name`,`ip_address`,`user_agent`,`request_params`,`response_data`,`duration_ms`)  VALUES ('1','90001','张三','LOGIN','USER','认证模块','0',NULL,'SUCCESS','用户登录成功','2025-01-10 09:25:00','/login','登录按钮','192.168.1.100','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36',NULL,NULL,NULL);
INSERT INTO `sys_operation_log` (`id`,`operator_num`,`operator_name`,`operation_type`,`object_type`,`module`,`object_id`,`object_code`,`result`,`message`,`operation_time`,`page_path`,`button_name`,`ip_address`,`user_agent`,`request_params`,`response_data`,`duration_ms`)  VALUES ('2','90001','张三','CREATE','CHANGE_RECORD','变更管理','1','CHG202501010001','SUCCESS','创建变更记录成功','2025-01-10 09:30:00','/change-records','新增变更','192.168.1.100','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36',NULL,NULL,NULL);
INSERT INTO `sys_operation_log` (`id`,`operator_num`,`operator_name`,`operation_type`,`object_type`,`module`,`object_id`,`object_code`,`result`,`message`,`operation_time`,`page_path`,`button_name`,`ip_address`,`user_agent`,`request_params`,`response_data`,`duration_ms`)  VALUES ('3','90004','赵六','LOGIN','USER','认证模块','0',NULL,'SUCCESS','用户登录成功','2025-01-13 14:15:00','/login','登录按钮','192.168.1.101','Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36',NULL,NULL,NULL);
INSERT INTO `sys_operation_log` (`id`,`operator_num`,`operator_name`,`operation_type`,`object_type`,`module`,`object_id`,`object_code`,`result`,`message`,`operation_time`,`page_path`,`button_name`,`ip_address`,`user_agent`,`request_params`,`response_data`,`duration_ms`)  VALUES ('4','90004','赵六','APPROVE','CHANGE_RECORD','变更管理','1','CHG202501010001','SUCCESS','审批变更记录成功','2025-01-13 14:20:00','/change-records','审批通过','192.168.1.101','Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36',NULL,NULL,NULL);
INSERT INTO `sys_operation_log` (`id`,`operator_num`,`operator_name`,`operation_type`,`object_type`,`module`,`object_id`,`object_code`,`result`,`message`,`operation_time`,`page_path`,`button_name`,`ip_address`,`user_agent`,`request_params`,`response_data`,`duration_ms`)  VALUES ('5','90002','李四','LOGIN','USER','认证模块','0',NULL,'SUCCESS','用户登录成功','2025-01-12 11:10:00','/login','登录按钮','192.168.1.102','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36',NULL,NULL,NULL);
INSERT INTO `sys_operation_log` (`id`,`operator_num`,`operator_name`,`operation_type`,`object_type`,`module`,`object_id`,`object_code`,`result`,`message`,`operation_time`,`page_path`,`button_name`,`ip_address`,`user_agent`,`request_params`,`response_data`,`duration_ms`)  VALUES ('6','90002','李四','CREATE','CHANGE_RECORD','变更管理','2','CHG202501010002','SUCCESS','创建变更记录成功','2025-01-12 11:15:00','/change-records','新增变更','192.168.1.102','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36',NULL,NULL,NULL);
-- ----------------------------
-- Table structure for table sys_org
-- ----------------------------
DROP TABLE IF EXISTS `sys_org`;
CREATE TABLE `sys_org` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '自增ID，仅做序号',
  `org_code` varchar(10) NOT NULL COMMENT '机构编码，10位定长层级编码',
  `org_name` varchar(100) NOT NULL COMMENT '机构名称',
  `parent_org_code` varchar(10) DEFAULT NULL COMMENT '上级机构编码',
  `level` tinyint NOT NULL COMMENT '机构层级：1-5级',
  `sort_order` int DEFAULT '100' COMMENT '排序号',
  `status` tinyint DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
  `description` varchar(500) DEFAULT NULL COMMENT '机构描述',
  `created_by` varchar(20) NOT NULL COMMENT '创建人用户编号',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` varchar(20) DEFAULT NULL COMMENT '更新人用户编号',
  `updated_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint DEFAULT '0' COMMENT '是否删除：0-否，1-是',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_org_code` (`org_code`),
  KEY `idx_parent_org_code` (`parent_org_code`),
  KEY `idx_level` (`level`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='机构信息表';

-- ----------------------------
-- Records of sys_org
-- ----------------------------
INSERT INTO `sys_org` (`id`,`org_code`,`org_name`,`parent_org_code`,`level`,`sort_order`,`status`,`description`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('1','99999','超级机构',NULL,1,'1',1,'系统超级管理员所属机构','SYSTEM','2025-12-12 22:49:24',NULL,NULL,0);
INSERT INTO `sys_org` (`id`,`org_code`,`org_name`,`parent_org_code`,`level`,`sort_order`,`status`,`description`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('2','01000','总部',NULL,1,'2',1,'公司总部','administrator','2025-12-12 22:49:24',NULL,NULL,0);
INSERT INTO `sys_org` (`id`,`org_code`,`org_name`,`parent_org_code`,`level`,`sort_order`,`status`,`description`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('3','01001','技术中心','01000',2,'1',1,'技术研发中心','administrator','2025-12-12 22:49:24',NULL,NULL,0);
INSERT INTO `sys_org` (`id`,`org_code`,`org_name`,`parent_org_code`,`level`,`sort_order`,`status`,`description`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('4','01002','产品中心','01000',2,'2',1,'产品管理部门','administrator','2025-12-12 22:49:24',NULL,NULL,0);
INSERT INTO `sys_org` (`id`,`org_code`,`org_name`,`parent_org_code`,`level`,`sort_order`,`status`,`description`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('5','02000','北京分公司',NULL,1,'3',1,'北京分公司','administrator','2025-12-12 22:49:24',NULL,NULL,0);
-- ----------------------------
-- Table structure for table sys_permission
-- ----------------------------
DROP TABLE IF EXISTS `sys_permission`;
CREATE TABLE `sys_permission` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '自增ID，仅做序号',
  `perm_code` varchar(5) NOT NULL COMMENT '权限编码，5位：P+4位数字',
  `perm_name` varchar(100) NOT NULL COMMENT '权限名称',
  `perm_key` varchar(100) NOT NULL COMMENT '权限标识（如：user:create）',
  `perm_type` tinyint NOT NULL COMMENT '权限类型：1-菜单，2-操作，3-接口，4-数据，5-字段，6-时间，7-业务',
  `menu_code` varchar(20) DEFAULT NULL COMMENT '关联的菜单编码（当perm_type=1,2时）',
  `api_path` varchar(500) DEFAULT NULL COMMENT '接口路径（当perm_type=3时）',
  `entity_type` varchar(50) DEFAULT NULL COMMENT '业务实体类型（如：user,order，当perm_type=4,5时）',
  `entity_field` varchar(100) DEFAULT NULL COMMENT '业务实体字段（当perm_type=5时）',
  `rule_type` tinyint DEFAULT NULL COMMENT '规则类型：1-预定义，2-自定义SQL',
  `scope_type` tinyint DEFAULT NULL COMMENT '预定义范围：1-全部，2-本机构，3-本部门，4-本人',
  `custom_rule` text COMMENT '自定义规则（JSON或SQL片段）',
  `is_default` tinyint DEFAULT '0' COMMENT '是否默认权限：0-否，1-是',
  `sort_order` int DEFAULT '100' COMMENT '排序号',
  `status` tinyint DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
  `description` varchar(500) DEFAULT NULL COMMENT '权限描述',
  `created_by` varchar(20) NOT NULL COMMENT '创建人用户编号',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` varchar(20) DEFAULT NULL COMMENT '更新人用户编号',
  `updated_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint DEFAULT '0' COMMENT '是否删除：0-否，1-是',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_perm_code` (`perm_code`),
  UNIQUE KEY `uk_perm_key` (`perm_key`),
  KEY `idx_perm_type` (`perm_type`),
  KEY `idx_menu_code` (`menu_code`),
  KEY `idx_entity_type` (`entity_type`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='统一权限表';

-- ----------------------------
-- Records of sys_permission
-- ----------------------------
INSERT INTO `sys_permission` (`id`,`perm_code`,`perm_name`,`perm_key`,`perm_type`,`menu_code`,`api_path`,`entity_type`,`entity_field`,`rule_type`,`scope_type`,`custom_rule`,`is_default`,`sort_order`,`status`,`description`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('1','P0001','待办任务列表','approval:todo:list',1,'P0000001','/api/approval-todos',NULL,NULL,NULL,NULL,NULL,1,'1',1,'访问待办任务列表的权限','administrator','2025-12-12 22:49:24',NULL,'2025-12-13 01:44:21',0);
INSERT INTO `sys_permission` (`id`,`perm_code`,`perm_name`,`perm_key`,`perm_type`,`menu_code`,`api_path`,`entity_type`,`entity_field`,`rule_type`,`scope_type`,`custom_rule`,`is_default`,`sort_order`,`status`,`description`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('2','P0002','变更记录列表','change:record:list',1,'P0000002','/api/change-records',NULL,NULL,NULL,NULL,NULL,1,'2',1,'查看变更记录列表的权限','administrator','2025-12-12 22:49:24',NULL,'2025-12-13 01:42:31',0);
INSERT INTO `sys_permission` (`id`,`perm_code`,`perm_name`,`perm_key`,`perm_type`,`menu_code`,`api_path`,`entity_type`,`entity_field`,`rule_type`,`scope_type`,`custom_rule`,`is_default`,`sort_order`,`status`,`description`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('3','P0003','变更记录新增','change:record:create',2,NULL,'/api/change-records',NULL,NULL,NULL,NULL,NULL,1,'3',1,'新增变更记录的权限','administrator','2025-12-12 22:49:24',NULL,NULL,0);
INSERT INTO `sys_permission` (`id`,`perm_code`,`perm_name`,`perm_key`,`perm_type`,`menu_code`,`api_path`,`entity_type`,`entity_field`,`rule_type`,`scope_type`,`custom_rule`,`is_default`,`sort_order`,`status`,`description`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('4','P0004','变更记录编辑','change:record:edit',2,NULL,'/api/change-records/*',NULL,NULL,NULL,NULL,NULL,1,'4',1,'编辑变更记录的权限','administrator','2025-12-12 22:49:24',NULL,NULL,0);
INSERT INTO `sys_permission` (`id`,`perm_code`,`perm_name`,`perm_key`,`perm_type`,`menu_code`,`api_path`,`entity_type`,`entity_field`,`rule_type`,`scope_type`,`custom_rule`,`is_default`,`sort_order`,`status`,`description`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('5','P0005','变更记录删除','change:record:delete',2,NULL,'/api/change-records/*',NULL,NULL,NULL,NULL,NULL,1,'5',1,'删除变更记录的权限','administrator','2025-12-12 22:49:24',NULL,NULL,0);
INSERT INTO `sys_permission` (`id`,`perm_code`,`perm_name`,`perm_key`,`perm_type`,`menu_code`,`api_path`,`entity_type`,`entity_field`,`rule_type`,`scope_type`,`custom_rule`,`is_default`,`sort_order`,`status`,`description`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('6','P0006','变更记录导出','change:record:export',2,NULL,'/api/change-records/export',NULL,NULL,NULL,NULL,NULL,1,'6',1,'导出变更记录的权限','administrator','2025-12-12 22:49:24',NULL,NULL,0);
INSERT INTO `sys_permission` (`id`,`perm_code`,`perm_name`,`perm_key`,`perm_type`,`menu_code`,`api_path`,`entity_type`,`entity_field`,`rule_type`,`scope_type`,`custom_rule`,`is_default`,`sort_order`,`status`,`description`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('7','P0007','用户管理','user:management',1,'P0000003','/api/users',NULL,NULL,NULL,NULL,NULL,1,'7',1,'用户管理页面访问权限','administrator','2025-12-12 22:49:24',NULL,NULL,0);
INSERT INTO `sys_permission` (`id`,`perm_code`,`perm_name`,`perm_key`,`perm_type`,`menu_code`,`api_path`,`entity_type`,`entity_field`,`rule_type`,`scope_type`,`custom_rule`,`is_default`,`sort_order`,`status`,`description`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('8','P0008','角色管理','role:management',1,'P0000004','/api/roles',NULL,NULL,NULL,NULL,NULL,1,'8',1,'角色管理页面访问权限','administrator','2025-12-12 22:49:24',NULL,NULL,0);
INSERT INTO `sys_permission` (`id`,`perm_code`,`perm_name`,`perm_key`,`perm_type`,`menu_code`,`api_path`,`entity_type`,`entity_field`,`rule_type`,`scope_type`,`custom_rule`,`is_default`,`sort_order`,`status`,`description`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('9','P0009','菜单管理','menu:management',1,'P0000005','/api/menus',NULL,NULL,NULL,NULL,NULL,1,'9',1,'菜单管理页面访问权限','administrator','2025-12-12 22:49:24',NULL,NULL,0);
INSERT INTO `sys_permission` (`id`,`perm_code`,`perm_name`,`perm_key`,`perm_type`,`menu_code`,`api_path`,`entity_type`,`entity_field`,`rule_type`,`scope_type`,`custom_rule`,`is_default`,`sort_order`,`status`,`description`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('10','P0010','字典管理','dict:management',1,'P0000006','/api/dicts',NULL,NULL,NULL,NULL,NULL,1,'10',1,'字典管理页面访问权限','administrator','2025-12-12 22:49:24',NULL,NULL,0);
INSERT INTO `sys_permission` (`id`,`perm_code`,`perm_name`,`perm_key`,`perm_type`,`menu_code`,`api_path`,`entity_type`,`entity_field`,`rule_type`,`scope_type`,`custom_rule`,`is_default`,`sort_order`,`status`,`description`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('11','P0011','操作日志查看','log:operation',1,'P0000007','/api/operation-logs',NULL,NULL,NULL,NULL,NULL,1,'11',1,'查看操作日志的权限','administrator','2025-12-12 22:49:24',NULL,NULL,0);
INSERT INTO `sys_permission` (`id`,`perm_code`,`perm_name`,`perm_key`,`perm_type`,`menu_code`,`api_path`,`entity_type`,`entity_field`,`rule_type`,`scope_type`,`custom_rule`,`is_default`,`sort_order`,`status`,`description`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('12','P0012','查看全部变更数据','change:data:all',4,NULL,NULL,'biz_change_record',NULL,1,1,NULL,0,'12',1,'查看全部变更数据的权限','administrator','2025-12-12 22:49:24',NULL,NULL,0);
INSERT INTO `sys_permission` (`id`,`perm_code`,`perm_name`,`perm_key`,`perm_type`,`menu_code`,`api_path`,`entity_type`,`entity_field`,`rule_type`,`scope_type`,`custom_rule`,`is_default`,`sort_order`,`status`,`description`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('13','P0013','查看本机构变更数据','change:data:org',4,NULL,NULL,'biz_change_record',NULL,1,2,NULL,0,'13',1,'查看本机构变更数据的权限','administrator','2025-12-12 22:49:24',NULL,NULL,0);
INSERT INTO `sys_permission` (`id`,`perm_code`,`perm_name`,`perm_key`,`perm_type`,`menu_code`,`api_path`,`entity_type`,`entity_field`,`rule_type`,`scope_type`,`custom_rule`,`is_default`,`sort_order`,`status`,`description`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('14','P0014','查看本人变更数据','change:data:self',4,NULL,NULL,'biz_change_record',NULL,1,4,NULL,0,'14',1,'查看本人变更数据的权限','administrator','2025-12-12 22:49:24',NULL,NULL,0);
-- ----------------------------
-- Table structure for table sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '自增ID，仅做序号',
  `role_code` varchar(5) NOT NULL COMMENT '角色编码，5位：R+4位数字',
  `role_name` varchar(50) NOT NULL COMMENT '角色名称',
  `role_type` tinyint DEFAULT '1' COMMENT '角色类型：1-系统角色，2-业务角色，3-自定义角色',
  `data_scope_type` tinyint DEFAULT '4' COMMENT '默认数据范围：1-全部，2-本机构，3-本部门，4-本人，5-自定义',
  `sort_order` int DEFAULT '100' COMMENT '排序号',
  `status` tinyint DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
  `description` varchar(500) DEFAULT NULL COMMENT '角色描述',
  `created_by` varchar(20) NOT NULL COMMENT '创建人用户编号',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` varchar(20) DEFAULT NULL COMMENT '更新人用户编号',
  `updated_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint DEFAULT '0' COMMENT '是否删除：0-否，1-是',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_code` (`role_code`),
  UNIQUE KEY `uk_role_name` (`role_name`),
  KEY `idx_role_type` (`role_type`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色信息表';

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` (`id`,`role_code`,`role_name`,`role_type`,`data_scope_type`,`sort_order`,`status`,`description`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('1','R0001','超级管理员',1,1,'1',1,'系统超级管理员，拥有所有权限','administrator','2025-12-12 22:49:24',NULL,NULL,0);
INSERT INTO `sys_role` (`id`,`role_code`,`role_name`,`role_type`,`data_scope_type`,`sort_order`,`status`,`description`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('2','R0002','架构师',2,2,'2',1,'项目架构师，负责技术架构和项目管理','administrator','2025-12-12 22:49:24',NULL,NULL,0);
INSERT INTO `sys_role` (`id`,`role_code`,`role_name`,`role_type`,`data_scope_type`,`sort_order`,`status`,`description`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('3','R0003','开发工程师',2,4,'3',1,'开发工程师，负责具体开发工作','administrator','2025-12-12 22:49:24',NULL,NULL,0);
INSERT INTO `sys_role` (`id`,`role_code`,`role_name`,`role_type`,`data_scope_type`,`sort_order`,`status`,`description`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('4','R0004','产品经理',2,3,'4',1,'产品经理，负责产品管理','administrator','2025-12-12 22:49:24',NULL,NULL,0);
INSERT INTO `sys_role` (`id`,`role_code`,`role_name`,`role_type`,`data_scope_type`,`sort_order`,`status`,`description`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('5','R0005','测试工程师',2,4,'5',1,'测试工程师，负责测试工作','administrator','2025-12-12 22:49:24',NULL,NULL,0);
-- ----------------------------
-- Table structure for table sys_role_org
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_org`;
CREATE TABLE `sys_role_org` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '自增ID，仅做序号',
  `role_code` varchar(5) NOT NULL COMMENT '角色编码',
  `org_code` varchar(10) NOT NULL COMMENT '机构编码',
  `org_range_type` tinyint DEFAULT '1' COMMENT '机构范围类型：1-本机构，2-包含下级机构',
  `perm_type` tinyint DEFAULT '1' COMMENT '权限类型：1-管理，2-查看，3-操作',
  `effective_start` datetime DEFAULT NULL COMMENT '生效开始时间',
  `effective_end` datetime DEFAULT NULL COMMENT '生效结束时间',
  `status` tinyint DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
  `description` varchar(500) DEFAULT NULL COMMENT '描述',
  `created_by` varchar(20) NOT NULL COMMENT '创建人用户编号',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` varchar(20) DEFAULT NULL COMMENT '更新人用户编号',
  `updated_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint DEFAULT '0' COMMENT '是否删除：0-否，1-是',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_org` (`role_code`,`org_code`,`perm_type`),
  KEY `idx_role_code` (`role_code`),
  KEY `idx_org_code` (`org_code`),
  KEY `idx_org_range_type` (`org_range_type`),
  KEY `idx_status` (`status`),
  KEY `idx_effective_time` (`effective_start`,`effective_end`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色机构范围表';

-- ----------------------------
-- Records of sys_role_org
-- ----------------------------
-- ----------------------------
-- Table structure for table sys_role_permission
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_permission`;
CREATE TABLE `sys_role_permission` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '自增ID，仅做序号',
  `role_code` varchar(5) NOT NULL COMMENT '角色编码',
  `perm_code` varchar(5) NOT NULL COMMENT '权限编码',
  `auth_type` tinyint DEFAULT '1' COMMENT '授权类型：1-允许，2-拒绝',
  `effective_start` datetime DEFAULT NULL COMMENT '生效开始时间',
  `effective_end` datetime DEFAULT NULL COMMENT '生效结束时间',
  `priority` int DEFAULT '100' COMMENT '优先级（数字越小优先级越高）',
  `status` tinyint DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
  `description` varchar(500) DEFAULT NULL COMMENT '描述',
  `created_by` varchar(20) NOT NULL COMMENT '创建人用户编号',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` varchar(20) DEFAULT NULL COMMENT '更新人用户编号',
  `updated_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint DEFAULT '0' COMMENT '是否删除：0-否，1-是',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_perm` (`role_code`,`perm_code`),
  KEY `idx_role_code` (`role_code`),
  KEY `idx_perm_code` (`perm_code`),
  KEY `idx_auth_type` (`auth_type`),
  KEY `idx_status` (`status`),
  KEY `idx_effective_time` (`effective_start`,`effective_end`)
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色权限关联表';

-- ----------------------------
-- Records of sys_role_permission
-- ----------------------------
INSERT INTO `sys_role_permission` (`id`,`role_code`,`perm_code`,`auth_type`,`effective_start`,`effective_end`,`priority`,`status`,`description`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('1','R0001','P0001',1,'2000-01-01 00:00:00','2099-12-31 00:00:00','100',1,'超级管理员权限','administrator','2025-12-12 22:49:24',NULL,NULL,0);
INSERT INTO `sys_role_permission` (`id`,`role_code`,`perm_code`,`auth_type`,`effective_start`,`effective_end`,`priority`,`status`,`description`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('2','R0001','P0002',1,'2000-01-01 00:00:00','2099-12-31 00:00:00','100',1,'超级管理员权限','administrator','2025-12-12 22:49:24',NULL,NULL,0);
INSERT INTO `sys_role_permission` (`id`,`role_code`,`perm_code`,`auth_type`,`effective_start`,`effective_end`,`priority`,`status`,`description`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('3','R0001','P0003',1,'2000-01-01 00:00:00','2099-12-31 00:00:00','100',1,'超级管理员权限','administrator','2025-12-12 22:49:24',NULL,NULL,0);
INSERT INTO `sys_role_permission` (`id`,`role_code`,`perm_code`,`auth_type`,`effective_start`,`effective_end`,`priority`,`status`,`description`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('4','R0001','P0004',1,'2000-01-01 00:00:00','2099-12-31 00:00:00','100',1,'超级管理员权限','administrator','2025-12-12 22:49:24',NULL,NULL,0);
INSERT INTO `sys_role_permission` (`id`,`role_code`,`perm_code`,`auth_type`,`effective_start`,`effective_end`,`priority`,`status`,`description`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('5','R0001','P0005',1,'2000-01-01 00:00:00','2099-12-31 00:00:00','100',1,'超级管理员权限','administrator','2025-12-12 22:49:24',NULL,NULL,0);
INSERT INTO `sys_role_permission` (`id`,`role_code`,`perm_code`,`auth_type`,`effective_start`,`effective_end`,`priority`,`status`,`description`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('6','R0001','P0006',1,'2000-01-01 00:00:00','2099-12-31 00:00:00','100',1,'超级管理员权限','administrator','2025-12-12 22:49:24',NULL,NULL,0);
INSERT INTO `sys_role_permission` (`id`,`role_code`,`perm_code`,`auth_type`,`effective_start`,`effective_end`,`priority`,`status`,`description`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('7','R0001','P0007',1,'2000-01-01 00:00:00','2099-12-31 00:00:00','100',1,'超级管理员权限','administrator','2025-12-12 22:49:24',NULL,NULL,0);
INSERT INTO `sys_role_permission` (`id`,`role_code`,`perm_code`,`auth_type`,`effective_start`,`effective_end`,`priority`,`status`,`description`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('8','R0001','P0008',1,'2000-01-01 00:00:00','2099-12-31 00:00:00','100',1,'超级管理员权限','administrator','2025-12-12 22:49:24',NULL,NULL,0);
INSERT INTO `sys_role_permission` (`id`,`role_code`,`perm_code`,`auth_type`,`effective_start`,`effective_end`,`priority`,`status`,`description`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('9','R0001','P0009',1,'2000-01-01 00:00:00','2099-12-31 00:00:00','100',1,'超级管理员权限','administrator','2025-12-12 22:49:24',NULL,NULL,0);
INSERT INTO `sys_role_permission` (`id`,`role_code`,`perm_code`,`auth_type`,`effective_start`,`effective_end`,`priority`,`status`,`description`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('10','R0001','P0010',1,'2000-01-01 00:00:00','2099-12-31 00:00:00','100',1,'超级管理员权限','administrator','2025-12-12 22:49:24',NULL,NULL,0);
INSERT INTO `sys_role_permission` (`id`,`role_code`,`perm_code`,`auth_type`,`effective_start`,`effective_end`,`priority`,`status`,`description`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('11','R0001','P0011',1,'2000-01-01 00:00:00','2099-12-31 00:00:00','100',1,'超级管理员权限','administrator','2025-12-12 22:49:24',NULL,NULL,0);
INSERT INTO `sys_role_permission` (`id`,`role_code`,`perm_code`,`auth_type`,`effective_start`,`effective_end`,`priority`,`status`,`description`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('12','R0001','P0012',1,'2000-01-01 00:00:00','2099-12-31 00:00:00','100',1,'超级管理员权限','administrator','2025-12-12 22:49:24',NULL,NULL,0);
INSERT INTO `sys_role_permission` (`id`,`role_code`,`perm_code`,`auth_type`,`effective_start`,`effective_end`,`priority`,`status`,`description`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('13','R0001','P0013',1,'2000-01-01 00:00:00','2099-12-31 00:00:00','100',1,'超级管理员权限','administrator','2025-12-12 22:49:24',NULL,NULL,0);
INSERT INTO `sys_role_permission` (`id`,`role_code`,`perm_code`,`auth_type`,`effective_start`,`effective_end`,`priority`,`status`,`description`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('14','R0001','P0014',1,'2000-01-01 00:00:00','2099-12-31 00:00:00','100',1,'超级管理员权限','administrator','2025-12-12 22:49:24',NULL,NULL,0);
INSERT INTO `sys_role_permission` (`id`,`role_code`,`perm_code`,`auth_type`,`effective_start`,`effective_end`,`priority`,`status`,`description`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('16','R0002','P0001',1,'2024-01-01 00:00:00','2026-12-31 00:00:00','100',1,'架构师首页权限','administrator','2025-12-12 22:49:24',NULL,NULL,0);
INSERT INTO `sys_role_permission` (`id`,`role_code`,`perm_code`,`auth_type`,`effective_start`,`effective_end`,`priority`,`status`,`description`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('17','R0002','P0002',1,'2024-01-01 00:00:00','2026-12-31 00:00:00','100',1,'架构师变更列表权限','administrator','2025-12-12 22:49:24',NULL,NULL,0);
INSERT INTO `sys_role_permission` (`id`,`role_code`,`perm_code`,`auth_type`,`effective_start`,`effective_end`,`priority`,`status`,`description`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('18','R0002','P0003',1,'2024-01-01 00:00:00','2026-12-31 00:00:00','100',1,'架构师新增变更权限','administrator','2025-12-12 22:49:24',NULL,NULL,0);
INSERT INTO `sys_role_permission` (`id`,`role_code`,`perm_code`,`auth_type`,`effective_start`,`effective_end`,`priority`,`status`,`description`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('19','R0002','P0004',1,'2024-01-01 00:00:00','2026-12-31 00:00:00','100',1,'架构师编辑变更权限','administrator','2025-12-12 22:49:24',NULL,NULL,0);
INSERT INTO `sys_role_permission` (`id`,`role_code`,`perm_code`,`auth_type`,`effective_start`,`effective_end`,`priority`,`status`,`description`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('20','R0002','P0005',1,'2024-01-01 00:00:00','2026-12-31 00:00:00','100',1,'架构师删除变更权限','administrator','2025-12-12 22:49:24',NULL,NULL,0);
INSERT INTO `sys_role_permission` (`id`,`role_code`,`perm_code`,`auth_type`,`effective_start`,`effective_end`,`priority`,`status`,`description`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('21','R0002','P0006',1,'2024-01-01 00:00:00','2026-12-31 00:00:00','100',1,'架构师导出变更权限','administrator','2025-12-12 22:49:24',NULL,NULL,0);
INSERT INTO `sys_role_permission` (`id`,`role_code`,`perm_code`,`auth_type`,`effective_start`,`effective_end`,`priority`,`status`,`description`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('22','R0002','P0013',1,'2024-01-01 00:00:00','2026-12-31 00:00:00','100',1,'架构师查看本机构数据权限','administrator','2025-12-12 22:49:24',NULL,NULL,0);
INSERT INTO `sys_role_permission` (`id`,`role_code`,`perm_code`,`auth_type`,`effective_start`,`effective_end`,`priority`,`status`,`description`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('23','R0003','P0001',1,'2024-01-01 00:00:00','2026-12-31 00:00:00','100',1,'开发工程师首页权限','administrator','2025-12-12 22:49:24',NULL,NULL,0);
INSERT INTO `sys_role_permission` (`id`,`role_code`,`perm_code`,`auth_type`,`effective_start`,`effective_end`,`priority`,`status`,`description`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('24','R0003','P0002',1,'2024-01-01 00:00:00','2026-12-31 00:00:00','100',1,'开发工程师变更列表权限','administrator','2025-12-12 22:49:24',NULL,NULL,0);
INSERT INTO `sys_role_permission` (`id`,`role_code`,`perm_code`,`auth_type`,`effective_start`,`effective_end`,`priority`,`status`,`description`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('25','R0003','P0003',1,'2024-01-01 00:00:00','2026-12-31 00:00:00','100',1,'开发工程师新增变更权限','administrator','2025-12-12 22:49:24',NULL,NULL,0);
INSERT INTO `sys_role_permission` (`id`,`role_code`,`perm_code`,`auth_type`,`effective_start`,`effective_end`,`priority`,`status`,`description`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('26','R0003','P0004',1,'2024-01-01 00:00:00','2026-12-31 00:00:00','100',1,'开发工程师编辑变更权限','administrator','2025-12-12 22:49:24',NULL,NULL,0);
INSERT INTO `sys_role_permission` (`id`,`role_code`,`perm_code`,`auth_type`,`effective_start`,`effective_end`,`priority`,`status`,`description`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('27','R0003','P0014',1,'2024-01-01 00:00:00','2026-12-31 00:00:00','100',1,'开发工程师查看本人数据权限','administrator','2025-12-12 22:49:24',NULL,NULL,0);
-- ----------------------------
-- Table structure for table sys_time_permission
-- ----------------------------
DROP TABLE IF EXISTS `sys_time_permission`;
CREATE TABLE `sys_time_permission` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '自增ID，仅做序号',
  `time_code` varchar(10) NOT NULL COMMENT '时间权限编码，10位',
  `perm_code` varchar(5) DEFAULT NULL COMMENT '关联的权限编码',
  `user_num` varchar(20) DEFAULT NULL COMMENT '用户编号（为空表示应用于所有用户）',
  `role_code` varchar(5) DEFAULT NULL COMMENT '角色编号（为空表示应用于所有角色）',
  `allowed_days` varchar(20) DEFAULT NULL COMMENT '允许访问的星期（如：1,2,3,4,5,6,7）',
  `start_time` time DEFAULT NULL COMMENT '每天开始时间',
  `end_time` time DEFAULT NULL COMMENT '每天结束时间',
  `effective_start` date DEFAULT NULL COMMENT '生效开始日期',
  `effective_end` date DEFAULT NULL COMMENT '生效结束日期',
  `timezone` varchar(50) DEFAULT 'Asia/Shanghai' COMMENT '时区',
  `is_recurring` tinyint DEFAULT '1' COMMENT '是否循环：0-否，1-是',
  `holiday_excluded` tinyint DEFAULT '0' COMMENT '是否排除节假日：0-否，1-是',
  `status` tinyint DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
  `description` varchar(500) DEFAULT NULL COMMENT '时间权限描述',
  `created_by` varchar(20) NOT NULL COMMENT '创建人用户编号',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` varchar(20) DEFAULT NULL COMMENT '更新人用户编号',
  `updated_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint DEFAULT '0' COMMENT '是否删除：0-否，1-是',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_time_code` (`time_code`),
  KEY `idx_perm_code` (`perm_code`),
  KEY `idx_user_num` (`user_num`),
  KEY `idx_role_code` (`role_code`),
  KEY `idx_effective_date` (`effective_start`,`effective_end`),
  KEY `idx_allowed_days` (`allowed_days`(10)),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='时间权限表';

-- ----------------------------
-- Records of sys_time_permission
-- ----------------------------
-- ----------------------------
-- Table structure for table sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '自增ID，仅做序号',
  `user_num` varchar(20) NOT NULL COMMENT '用户编号，20位',
  `user_name` varchar(50) NOT NULL COMMENT '用户姓名',
  `nickname` varchar(50) DEFAULT NULL COMMENT '用户昵称',
  `gender` tinyint DEFAULT '0' COMMENT '性别：0-未知，1-男，2-女',
  `password` varchar(128) NOT NULL COMMENT '密码（加密存储）',
  `salt` varchar(32) DEFAULT NULL COMMENT '密码盐值',
  `org_code` varchar(10) DEFAULT NULL COMMENT '主机构编码',
  `dept_code` varchar(15) DEFAULT NULL COMMENT '主部门编码',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `avatar` varchar(500) DEFAULT NULL COMMENT '头像URL',
  `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
  `last_login_ip` varchar(50) DEFAULT NULL COMMENT '最后登录IP',
  `login_count` int DEFAULT '0' COMMENT '登录次数',
  `status` tinyint DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
  `is_locked` tinyint DEFAULT '0' COMMENT '是否锁定：0-否，1-是',
  `lock_time` datetime DEFAULT NULL COMMENT '锁定时间',
  `lock_reason` varchar(200) DEFAULT NULL COMMENT '锁定原因',
  `is_special` tinyint DEFAULT '0' COMMENT '是否特殊用户：0-否，1-是（如AIguibin）',
  `pwd_expire_time` datetime DEFAULT NULL COMMENT '密码过期时间',
  `pwd_modified_time` datetime DEFAULT NULL COMMENT '密码最后修改时间',
  `created_by` varchar(20) NOT NULL COMMENT '创建人用户编号',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` varchar(20) DEFAULT NULL COMMENT '更新人用户编号',
  `updated_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint DEFAULT '0' COMMENT '是否删除：0-否，1-是',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_num` (`user_num`),
  UNIQUE KEY `uk_user_name` (`user_name`),
  KEY `idx_org_code` (`org_code`),
  KEY `idx_dept_code` (`dept_code`),
  KEY `idx_status` (`status`),
  KEY `idx_is_locked` (`is_locked`),
  KEY `idx_phone` (`phone`),
  KEY `idx_email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户信息表';

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` (`id`,`user_num`,`user_name`,`nickname`,`gender`,`password`,`salt`,`org_code`,`dept_code`,`email`,`phone`,`avatar`,`last_login_time`,`last_login_ip`,`login_count`,`status`,`is_locked`,`lock_time`,`lock_reason`,`is_special`,`pwd_expire_time`,`pwd_modified_time`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('1','administrator','超级管理员','超级管理员',1,'$2a$10$rlzysPIW259zbtSjoGj.zeEJG8yzRW5.21Ti.amypjcAKAPOvIjze','abcdefghijklmnopqrstuvwx','99999','99999D001','administrator@system.com','13888888888',NULL,NULL,NULL,'0',1,0,NULL,NULL,1,'2099-12-31 23:59:59','2025-12-12 22:49:24','SYSTEM','2025-12-12 22:49:24',NULL,'2025-12-12 23:45:02',0);
INSERT INTO `sys_user` (`id`,`user_num`,`user_name`,`nickname`,`gender`,`password`,`salt`,`org_code`,`dept_code`,`email`,`phone`,`avatar`,`last_login_time`,`last_login_ip`,`login_count`,`status`,`is_locked`,`lock_time`,`lock_reason`,`is_special`,`pwd_expire_time`,`pwd_modified_time`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('2','90001','张三','张三',1,'$2a$10$abcdefghijklmnopqrstuvwxyz0123456789abcdefghijklmnopq','abcdefghijklmnopqrstuvwx','01000','01000D001','zhangsan@example.com','13800138001',NULL,NULL,NULL,'0',1,0,NULL,NULL,0,'2026-12-31 23:59:59','2025-12-12 22:49:24','administrator','2025-12-12 22:49:24',NULL,NULL,0);
INSERT INTO `sys_user` (`id`,`user_num`,`user_name`,`nickname`,`gender`,`password`,`salt`,`org_code`,`dept_code`,`email`,`phone`,`avatar`,`last_login_time`,`last_login_ip`,`login_count`,`status`,`is_locked`,`lock_time`,`lock_reason`,`is_special`,`pwd_expire_time`,`pwd_modified_time`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('3','90002','李四','李四',2,'$2a$10$abcdefghijklmnopqrstuvwxyz0123456789abcdefghijklmnopq','abcdefghijklmnopqrstuvwx','02000','02000D001','lisi@example.com','13800138002',NULL,NULL,NULL,'0',1,0,NULL,NULL,0,'2026-12-31 23:59:59','2025-12-12 22:49:24','administrator','2025-12-12 22:49:24',NULL,NULL,0);
INSERT INTO `sys_user` (`id`,`user_num`,`user_name`,`nickname`,`gender`,`password`,`salt`,`org_code`,`dept_code`,`email`,`phone`,`avatar`,`last_login_time`,`last_login_ip`,`login_count`,`status`,`is_locked`,`lock_time`,`lock_reason`,`is_special`,`pwd_expire_time`,`pwd_modified_time`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('4','90003','王五','王五',1,'$2a$10$abcdefghijklmnopqrstuvwxyz0123456789abcdefghijklmnopq','abcdefghijklmnopqrstuvwx','01000','01000D002','wangwu@example.com','13800138003',NULL,NULL,NULL,'0',1,0,NULL,NULL,0,'2026-12-31 23:59:59','2025-12-12 22:49:24','administrator','2025-12-12 22:49:24',NULL,NULL,0);
INSERT INTO `sys_user` (`id`,`user_num`,`user_name`,`nickname`,`gender`,`password`,`salt`,`org_code`,`dept_code`,`email`,`phone`,`avatar`,`last_login_time`,`last_login_ip`,`login_count`,`status`,`is_locked`,`lock_time`,`lock_reason`,`is_special`,`pwd_expire_time`,`pwd_modified_time`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('5','90004','赵六','赵六',2,'$2a$10$abcdefghijklmnopqrstuvwxyz0123456789abcdefghijklmnopq','abcdefghijklmnopqrstuvwx','01001','01001D001','zhaoliu@example.com','13800138004',NULL,NULL,NULL,'0',1,0,NULL,NULL,0,'2026-12-31 23:59:59','2025-12-12 22:49:24','administrator','2025-12-12 22:49:24',NULL,NULL,0);
INSERT INTO `sys_user` (`id`,`user_num`,`user_name`,`nickname`,`gender`,`password`,`salt`,`org_code`,`dept_code`,`email`,`phone`,`avatar`,`last_login_time`,`last_login_ip`,`login_count`,`status`,`is_locked`,`lock_time`,`lock_reason`,`is_special`,`pwd_expire_time`,`pwd_modified_time`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('6','liuguibin','刘贵斌','guibin',1,'$2a$10$abcdefghijklmnopqrstuvwxyz0123456789abcdefghijklmnopq','abcdefghijklmnopqrstuvwx','01000','01000D001','liuguibin@example.com','13800138005',NULL,NULL,NULL,'0',1,0,NULL,NULL,0,'2026-12-31 23:59:59','2025-12-12 22:49:24','administrator','2025-12-12 22:49:24',NULL,NULL,0);
-- ----------------------------
-- Table structure for table sys_user_dept
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_dept`;
CREATE TABLE `sys_user_dept` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '自增ID，仅做序号',
  `user_num` varchar(20) NOT NULL COMMENT '用户编号',
  `dept_code` varchar(15) NOT NULL COMMENT '部门编码',
  `is_primary` tinyint DEFAULT '0' COMMENT '是否主部门：0-否，1-是',
  `position` varchar(100) DEFAULT NULL COMMENT '用户在部门中的职位',
  `effective_start` datetime DEFAULT NULL COMMENT '生效开始时间',
  `effective_end` datetime DEFAULT NULL COMMENT '生效结束时间',
  `status` tinyint DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
  `description` varchar(500) DEFAULT NULL COMMENT '描述',
  `created_by` varchar(20) NOT NULL COMMENT '创建人用户编号',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` varchar(20) DEFAULT NULL COMMENT '更新人用户编号',
  `updated_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint DEFAULT '0' COMMENT '是否删除：0-否，1-是',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_dept` (`user_num`,`dept_code`),
  KEY `idx_user_num` (`user_num`),
  KEY `idx_dept_code` (`dept_code`),
  KEY `idx_is_primary` (`is_primary`),
  KEY `idx_status` (`status`),
  KEY `idx_effective_time` (`effective_start`,`effective_end`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户部门扩展表（支持用户多部门）';

-- ----------------------------
-- Records of sys_user_dept
-- ----------------------------
INSERT INTO `sys_user_dept` (`id`,`user_num`,`dept_code`,`is_primary`,`position`,`effective_start`,`effective_end`,`status`,`description`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('1','administrator','99999D001',1,'超级管理员','2000-01-01 00:00:00','2099-12-31 00:00:00',1,'超级管理员主部门','SYSTEM','2025-12-12 22:49:24',NULL,'2025-12-13 01:47:19',0);
INSERT INTO `sys_user_dept` (`id`,`user_num`,`dept_code`,`is_primary`,`position`,`effective_start`,`effective_end`,`status`,`description`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('2','90001','01000D001',1,'研发工程师','2024-01-01 00:00:00','2026-12-31 00:00:00',1,'总部研发部主部门','administrator','2025-12-12 22:49:24',NULL,NULL,0);
INSERT INTO `sys_user_dept` (`id`,`user_num`,`dept_code`,`is_primary`,`position`,`effective_start`,`effective_end`,`status`,`description`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('3','90002','02000D001',1,'研发工程师','2024-01-01 00:00:00','2026-12-31 00:00:00',1,'北京研发部主部门','administrator','2025-12-12 22:49:24',NULL,NULL,0);
INSERT INTO `sys_user_dept` (`id`,`user_num`,`dept_code`,`is_primary`,`position`,`effective_start`,`effective_end`,`status`,`description`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('4','90003','01000D002',1,'产品经理','2024-01-01 00:00:00','2026-12-31 00:00:00',1,'总部产品部主部门','administrator','2025-12-12 22:49:24',NULL,NULL,0);
INSERT INTO `sys_user_dept` (`id`,`user_num`,`dept_code`,`is_primary`,`position`,`effective_start`,`effective_end`,`status`,`description`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('5','90004','01001D001',1,'架构师','2024-01-01 00:00:00','2026-12-31 00:00:00',1,'技术研发部主部门','administrator','2025-12-12 22:49:24',NULL,NULL,0);
INSERT INTO `sys_user_dept` (`id`,`user_num`,`dept_code`,`is_primary`,`position`,`effective_start`,`effective_end`,`status`,`description`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('6','liuguibin','01000D001',1,'研发工程师','2024-01-01 00:00:00','2026-12-31 00:00:00',1,'总部研发部主部门','administrator','2025-12-12 22:49:24',NULL,NULL,0);
-- ----------------------------
-- Table structure for table sys_user_org
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_org`;
CREATE TABLE `sys_user_org` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '自增ID，仅做序号',
  `user_num` varchar(20) NOT NULL COMMENT '用户编号',
  `org_code` varchar(10) NOT NULL COMMENT '机构编码',
  `is_primary` tinyint DEFAULT '0' COMMENT '是否主机构：0-否，1-是',
  `position` varchar(100) DEFAULT NULL COMMENT '用户在机构中的职位',
  `effective_start` datetime DEFAULT NULL COMMENT '生效开始时间',
  `effective_end` datetime DEFAULT NULL COMMENT '生效结束时间',
  `status` tinyint DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
  `description` varchar(500) DEFAULT NULL COMMENT '描述',
  `created_by` varchar(20) NOT NULL COMMENT '创建人用户编号',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` varchar(20) DEFAULT NULL COMMENT '更新人用户编号',
  `updated_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint DEFAULT '0' COMMENT '是否删除：0-否，1-是',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_org` (`user_num`,`org_code`),
  KEY `idx_user_num` (`user_num`),
  KEY `idx_org_code` (`org_code`),
  KEY `idx_is_primary` (`is_primary`),
  KEY `idx_status` (`status`),
  KEY `idx_effective_time` (`effective_start`,`effective_end`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户机构扩展表（支持用户多机构）';

-- ----------------------------
-- Records of sys_user_org
-- ----------------------------
INSERT INTO `sys_user_org` (`id`,`user_num`,`org_code`,`is_primary`,`position`,`effective_start`,`effective_end`,`status`,`description`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('1','administrator','99999',1,'超级管理员','2000-01-01 00:00:00','2099-12-31 00:00:00',1,'超级管理员主机构','SYSTEM','2025-12-12 22:49:24',NULL,'2025-12-13 01:47:25',0);
INSERT INTO `sys_user_org` (`id`,`user_num`,`org_code`,`is_primary`,`position`,`effective_start`,`effective_end`,`status`,`description`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('2','90001','01000',1,'研发工程师','2024-01-01 00:00:00','2026-12-31 00:00:00',1,'总部主机构','administrator','2025-12-12 22:49:24',NULL,NULL,0);
INSERT INTO `sys_user_org` (`id`,`user_num`,`org_code`,`is_primary`,`position`,`effective_start`,`effective_end`,`status`,`description`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('3','90002','02000',1,'研发工程师','2024-01-01 00:00:00','2026-12-31 00:00:00',1,'北京分公司主机构','administrator','2025-12-12 22:49:24',NULL,NULL,0);
INSERT INTO `sys_user_org` (`id`,`user_num`,`org_code`,`is_primary`,`position`,`effective_start`,`effective_end`,`status`,`description`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('4','90003','01000',1,'产品经理','2024-01-01 00:00:00','2026-12-31 00:00:00',1,'总部产品部主机构','administrator','2025-12-12 22:49:24',NULL,NULL,0);
INSERT INTO `sys_user_org` (`id`,`user_num`,`org_code`,`is_primary`,`position`,`effective_start`,`effective_end`,`status`,`description`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('5','90004','01001',1,'架构师','2024-01-01 00:00:00','2026-12-31 00:00:00',1,'技术中心主机构','administrator','2025-12-12 22:49:24',NULL,NULL,0);
INSERT INTO `sys_user_org` (`id`,`user_num`,`org_code`,`is_primary`,`position`,`effective_start`,`effective_end`,`status`,`description`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('6','liuguibin','01000',1,'研发工程师','2024-01-01 00:00:00','2026-12-31 00:00:00',1,'总部主机构','administrator','2025-12-12 22:49:24',NULL,NULL,0);
-- ----------------------------
-- Table structure for table sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '自增ID，仅做序号',
  `user_num` varchar(20) NOT NULL COMMENT '用户编号',
  `role_code` varchar(5) NOT NULL COMMENT '角色编码',
  `is_primary` tinyint DEFAULT '0' COMMENT '是否主角色：0-否，1-是（一个用户只有一个主角色）',
  `effective_start` datetime DEFAULT NULL COMMENT '生效开始时间',
  `effective_end` datetime DEFAULT NULL COMMENT '生效结束时间',
  `status` tinyint DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
  `description` varchar(500) DEFAULT NULL COMMENT '描述',
  `created_by` varchar(20) NOT NULL COMMENT '创建人用户编号',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` varchar(20) DEFAULT NULL COMMENT '更新人用户编号',
  `updated_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint DEFAULT '0' COMMENT '是否删除：0-否，1-是',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_role` (`user_num`,`role_code`),
  KEY `idx_user_num` (`user_num`),
  KEY `idx_role_code` (`role_code`),
  KEY `idx_is_primary` (`is_primary`),
  KEY `idx_status` (`status`),
  KEY `idx_effective_time` (`effective_start`,`effective_end`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户角色关联表';

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
INSERT INTO `sys_user_role` (`id`,`user_num`,`role_code`,`is_primary`,`effective_start`,`effective_end`,`status`,`description`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('1','administrator','R0001',1,'2000-01-01 00:00:00','2099-12-31 00:00:00',1,'超级管理员作为超级管理员','SYSTEM','2025-12-12 22:49:24',NULL,'2025-12-14 03:08:13',0);
INSERT INTO `sys_user_role` (`id`,`user_num`,`role_code`,`is_primary`,`effective_start`,`effective_end`,`status`,`description`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('2','90001','R0003',1,'2024-01-01 00:00:00','2026-12-31 00:00:00',1,'张三作为开发工程师','administrator','2025-12-12 22:49:24',NULL,NULL,0);
INSERT INTO `sys_user_role` (`id`,`user_num`,`role_code`,`is_primary`,`effective_start`,`effective_end`,`status`,`description`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('3','90002','R0003',1,'2024-01-01 00:00:00','2026-12-31 00:00:00',1,'李四作为开发工程师','administrator','2025-12-12 22:49:24',NULL,NULL,0);
INSERT INTO `sys_user_role` (`id`,`user_num`,`role_code`,`is_primary`,`effective_start`,`effective_end`,`status`,`description`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('4','90003','R0004',1,'2024-01-01 00:00:00','2026-12-31 00:00:00',1,'王五作为产品经理','administrator','2025-12-12 22:49:24',NULL,NULL,0);
INSERT INTO `sys_user_role` (`id`,`user_num`,`role_code`,`is_primary`,`effective_start`,`effective_end`,`status`,`description`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('5','90004','R0002',1,'2024-01-01 00:00:00','2026-12-31 00:00:00',1,'赵六作为架构师','administrator','2025-12-12 22:49:24',NULL,NULL,0);
INSERT INTO `sys_user_role` (`id`,`user_num`,`role_code`,`is_primary`,`effective_start`,`effective_end`,`status`,`description`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES ('6','liuguibin','R0003',1,'2024-01-01 00:00:00','2026-12-31 00:00:00',1,'刘贵斌作为开发工程师','administrator','2025-12-12 22:49:24',NULL,NULL,0);
SET FOREIGN_KEY_CHECKS=1;

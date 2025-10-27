CREATE TABLE IF NOT EXISTS `system_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `usernumb` varchar(50) NOT NULL COMMENT '用户编号',
  `username` varchar(50) NOT NULL COMMENT '用户姓名',
  `password` varchar(100) NOT NULL COMMENT '密码',
  `role` varchar(20) NOT NULL DEFAULT 'USER' COMMENT '角色(USER/ADMIN)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_usernumb` (`usernumb`)
);

CREATE TABLE IF NOT EXISTS `code_script_change_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '序号',
  `release_date` date DEFAULT NULL COMMENT '发版日期',
  `defect_number` varchar(100) DEFAULT NULL COMMENT '缺陷编号',
  `group_name` varchar(100) DEFAULT NULL COMMENT '组别',
  `developer` varchar(50) DEFAULT NULL COMMENT '开发负责人',
  `branch_name` varchar(100) DEFAULT NULL COMMENT '分支名称',
  `service_name` varchar(100) DEFAULT NULL COMMENT '服务名称',
  `problem_description` text COMMENT '问题描述',
  `impact_analysis` text COMMENT '问题影响分析',
  `solution` text COMMENT '解决方案',
  `involve_external_system` tinyint(1) DEFAULT '0' COMMENT '是否涉及外围系统(0-否，1-是)',
  `cross_service` tinyint(1) DEFAULT '0' COMMENT '是否跨服务(0-否，1-是)',
  `code_list` text COMMENT '代码清单',
  `remark` text COMMENT '备注说明',
  `version` varchar(50) NOT NULL COMMENT '版本号',
  `change_desc` text COMMENT '变更描述',
  `current_status` varchar(50) DEFAULT '待审批' COMMENT '当前状态：待审批/待评审/待合版/已合版',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_user` varchar(50) DEFAULT NULL COMMENT '创建人',
  `update_user` varchar(50) DEFAULT NULL COMMENT '更新人',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除标志(0-未删除，1-已删除)',
  PRIMARY KEY (`id`),
  KEY `idx_release_date` (`release_date`),
  KEY `idx_defect_number` (`defect_number`),
  KEY `idx_service_name` (`service_name`),
  KEY `idx_developer` (`developer`),
  KEY `idx_group_name` (`group_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='代码脚本变更记录表';

-- 历史记录表：代码脚本变更历史表
CREATE TABLE IF NOT EXISTS `code_script_change_history` (
  `history_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '历史记录ID',
  `record_id` bigint(20) NOT NULL COMMENT '关联的主记录ID',
  `operation_type` varchar(20) NOT NULL COMMENT '操作类型(CREATE/UPDATE/DELETE)',
  `operation_user` varchar(50) NOT NULL COMMENT '操作用户',
  `operation_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  `operation_description` varchar(200) DEFAULT NULL COMMENT '操作描述',

  `release_date` date DEFAULT NULL COMMENT '发版日期',
  `defect_number` varchar(100) DEFAULT NULL COMMENT '缺陷编号',
  `group_name` varchar(100) DEFAULT NULL COMMENT '组别',
  `developer` varchar(50) DEFAULT NULL COMMENT '开发负责人',
  `branch_name` varchar(100) DEFAULT NULL COMMENT '分支名称',
  `service_name` varchar(100) DEFAULT NULL COMMENT '服务名称',
  `problem_description` text COMMENT '问题描述',
  `impact_analysis` text COMMENT '问题影响分析',
  `solution` text COMMENT '解决方案',
  `involve_external_system` tinyint(1) DEFAULT '0' COMMENT '是否涉及外围系统',
  `cross_service` tinyint(1) DEFAULT '0' COMMENT '是否跨服务',
  `code_list` text COMMENT '代码清单',
  `remark` text COMMENT '备注说明',
  `version` varchar(50) NOT NULL COMMENT '版本号',
  `change_desc` text COMMENT '变更描述',
  `current_status` varchar(50) DEFAULT '待审批' COMMENT '当前状态：待审批/待评审/待合版/已合版',
  `create_time` datetime COMMENT '原始创建时间',
  `update_time` datetime COMMENT '原始更新时间',
  `create_user` varchar(50) DEFAULT NULL COMMENT '原始创建人',
  `update_user` varchar(50) DEFAULT NULL COMMENT '原始更新人',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除标志',

  PRIMARY KEY (`history_id`),
  KEY `idx_record_id` (`record_id`),
  KEY `idx_operation_time` (`operation_time`),
  KEY `idx_operation_user` (`operation_user`),
  KEY `idx_operation_type` (`operation_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='代码脚本变更历史表';

-- legacy tables version/version_history removed

-- 操作日志表：记录所有变更操作便于追溯
CREATE TABLE IF NOT EXISTS `operation_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `operator` varchar(50) NOT NULL COMMENT '操作人',
  `operation_type` varchar(20) NOT NULL COMMENT '操作类型',
  `object_type` varchar(50) NOT NULL COMMENT '对象类型',
  `object_id` bigint(20) NOT NULL COMMENT '对象ID',
  `result` varchar(20) NOT NULL COMMENT '结果(OK/FAIL)',
  `message` varchar(200) DEFAULT NULL COMMENT '结果说明',
  `operation_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  PRIMARY KEY (`id`),
  KEY `idx_object` (`object_type`, `object_id`),
  KEY `idx_operator_time` (`operator`, `operation_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

-- 确保 system_user 表存在角色字段（兼容 MySQL 5.7/8.0）
SET @role_exists := (
  SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'system_user'
    AND COLUMN_NAME = 'role'
);
SET @add_role_sql := IF(@role_exists = 0,
  'ALTER TABLE `system_user` ADD COLUMN `role` varchar(20) NOT NULL DEFAULT ''USER'' COMMENT ''角色(USER/ADMIN)''',
  'SELECT 1'
);
PREPARE add_role_stmt FROM @add_role_sql; EXECUTE add_role_stmt; DEALLOCATE PREPARE add_role_stmt;

-- 回填管理员角色（仅当存在 role 字段时执行）
SET @update_admin_sql := IF(@role_exists = 0,
  'SELECT 1',
  'UPDATE `system_user` SET `role` = ''ADMIN'' WHERE (`usernumb` = ''admin'' OR `username` = ''admin'')'
);
PREPARE update_admin_stmt FROM @update_admin_sql; EXECUTE update_admin_stmt; DEALLOCATE PREPARE update_admin_stmt;

-- 兼容已存在库：为主表和历史表补充 current_status 字段（如缺失）
SET @rec_current_status_exists := (
  SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'code_script_change_record'
    AND COLUMN_NAME = 'current_status'
);
SET @his_current_status_exists := (
  SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'code_script_change_history'
    AND COLUMN_NAME = 'current_status'
);
SET @add_rec_current_status_sql := IF(@rec_current_status_exists = 0,
  'ALTER TABLE `code_script_change_record` ADD COLUMN `current_status` varchar(50) DEFAULT ''待审批'' COMMENT ''当前状态：待审批/待评审/待合版/已合版'' AFTER `change_desc`',
  'SELECT 1'
);
SET @add_his_current_status_sql := IF(@his_current_status_exists = 0,
  'ALTER TABLE `code_script_change_history` ADD COLUMN `current_status` varchar(50) DEFAULT ''待审批'' COMMENT ''当前状态：待审批/待评审/待合版/已合版'' AFTER `change_desc`',
  'SELECT 1'
);
-- 执行主表 current_status 兼容补充
PREPARE add_rec_current_status_stmt FROM @add_rec_current_status_sql; EXECUTE add_rec_current_status_stmt; DEALLOCATE PREPARE add_rec_current_status_stmt;
PREPARE add_his_current_status_stmt FROM @add_his_current_status_sql; EXECUTE add_his_current_status_stmt; DEALLOCATE PREPARE add_his_current_status_stmt;

-- 兼容迁移：移除旧 is_released 字段（如存在）
SET @rec_is_released_exists := (
  SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'code_script_change_record'
    AND COLUMN_NAME = 'is_released'
);
SET @his_is_released_exists := (
  SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'code_script_change_history'
    AND COLUMN_NAME = 'is_released'
);
SET @drop_rec_is_released_sql := IF(@rec_is_released_exists = 1,
  'ALTER TABLE `code_script_change_record` DROP COLUMN `is_released`',
  'SELECT 1'
);
SET @drop_his_is_released_sql := IF(@his_is_released_exists = 1,
  'ALTER TABLE `code_script_change_history` DROP COLUMN `is_released`',
  'SELECT 1'
);
PREPARE drop_rec_is_released_stmt FROM @drop_rec_is_released_sql; EXECUTE drop_rec_is_released_stmt; DEALLOCATE PREPARE drop_rec_is_released_stmt;
PREPARE drop_his_is_released_stmt FROM @drop_his_is_released_sql; EXECUTE drop_his_is_released_stmt; DEALLOCATE PREPARE drop_his_is_released_stmt;
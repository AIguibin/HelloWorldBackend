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
  `develop_type` varchar(50) DEFAULT NULL COMMENT '开发类别',
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
  `develop_type` varchar(50) DEFAULT NULL COMMENT '开发类别',
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

-- 添加开发类别字段（如缺失）
SET @rec_develop_type_exists := (
  SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'code_script_change_record'
    AND COLUMN_NAME = 'develop_type'
);
SET @his_develop_type_exists := (
  SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'code_script_change_history'
    AND COLUMN_NAME = 'develop_type'
);
SET @add_rec_develop_type_sql := IF(@rec_develop_type_exists = 0,
  'ALTER TABLE `code_script_change_record` ADD COLUMN `develop_type` varchar(50) DEFAULT NULL COMMENT ''开发类别'' AFTER `developer`',
  'SELECT 1'
);
SET @add_his_develop_type_sql := IF(@his_develop_type_exists = 0,
  'ALTER TABLE `code_script_change_history` ADD COLUMN `develop_type` varchar(50) DEFAULT NULL COMMENT ''开发类别'' AFTER `developer`',
  'SELECT 1'
);
PREPARE add_rec_develop_type_stmt FROM @add_rec_develop_type_sql; EXECUTE add_rec_develop_type_stmt; DEALLOCATE PREPARE add_rec_develop_type_stmt;
PREPARE add_his_develop_type_stmt FROM @add_his_develop_type_sql; EXECUTE add_his_develop_type_stmt; DEALLOCATE PREPARE add_his_develop_type_stmt;

-- ============================================
-- 权限管理系统核心表结构
-- ============================================

-- 机构表
CREATE TABLE IF NOT EXISTS `sys_organization` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `parent_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '父机构ID',
  `org_code` varchar(100) NOT NULL COMMENT '机构编码',
  `org_name` varchar(200) NOT NULL COMMENT '机构名称',
  `org_type` tinyint(4) NOT NULL COMMENT '机构类型 1:集团 2:公司 3:部门 4:小组',
  `leader_id` bigint(20) DEFAULT NULL COMMENT '负责人ID',
  `sort_order` int(11) DEFAULT 0 COMMENT '排序号',
  `status` tinyint(4) DEFAULT 1 COMMENT '状态 0:停用 1:启用',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_org_code` (`org_code`),
  KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='机构表';

-- 扩展用户表（添加机构ID字段）
SET @user_org_id_exists := (
  SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'system_user'
    AND COLUMN_NAME = 'org_id'
);
SET @add_user_org_id_sql := IF(@user_org_id_exists = 0,
  'ALTER TABLE `system_user` ADD COLUMN `org_id` bigint(20) DEFAULT NULL COMMENT ''所属机构ID'' AFTER `role`',
  'SELECT 1'
);
PREPARE add_user_org_id_stmt FROM @add_user_org_id_sql; EXECUTE add_user_org_id_stmt; DEALLOCATE PREPARE add_user_org_id_stmt;

-- 角色表
CREATE TABLE IF NOT EXISTS `sys_role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `role_code` varchar(100) NOT NULL COMMENT '角色编码',
  `role_name` varchar(200) NOT NULL COMMENT '角色名称',
  `role_type` tinyint(4) NOT NULL COMMENT '角色类型 1:系统角色 2:业务角色',
  `data_scope_type` tinyint(4) DEFAULT 1 COMMENT '数据权限范围 1:全部 2:本机构 3:本部门 4:本人 5:自定义',
  `description` varchar(500) DEFAULT NULL COMMENT '描述',
  `status` tinyint(4) DEFAULT 1 COMMENT '状态 0:停用 1:启用',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_code` (`role_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 菜单表
CREATE TABLE IF NOT EXISTS `sys_menu` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `parent_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '父菜单ID',
  `menu_type` tinyint(4) NOT NULL COMMENT '菜单类型 1:目录 2:菜单 3:按钮 4:接口',
  `menu_name` varchar(100) NOT NULL COMMENT '菜单名称',
  `menu_code` varchar(100) NOT NULL COMMENT '菜单编码',
  `path` varchar(200) DEFAULT NULL COMMENT '路由路径',
  `component` varchar(200) DEFAULT NULL COMMENT '组件路径',
  `icon` varchar(100) DEFAULT NULL COMMENT '图标',
  `perms` varchar(100) DEFAULT NULL COMMENT '权限标识',
  `sort_order` int(11) DEFAULT 0 COMMENT '排序号',
  `is_visible` tinyint(4) DEFAULT 1 COMMENT '是否显示 0:隐藏 1:显示',
  `status` tinyint(4) DEFAULT 1 COMMENT '状态 0:停用 1:启用',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_menu_code` (`menu_code`),
  KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单表';

-- 用户角色关系表
CREATE TABLE IF NOT EXISTS `sys_user_role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_role` (`user_id`, `role_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关系表';

-- 角色菜单关系表
CREATE TABLE IF NOT EXISTS `sys_role_menu` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  `menu_id` bigint(20) NOT NULL COMMENT '菜单ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_menu` (`role_id`, `menu_id`),
  KEY `idx_role_id` (`role_id`),
  KEY `idx_menu_id` (`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色菜单关系表';

-- 数据权限规则表
CREATE TABLE IF NOT EXISTS `sys_data_permission` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  `rule_name` varchar(100) NOT NULL COMMENT '规则名称',
  `table_name` varchar(100) NOT NULL COMMENT '表名',
  `condition_type` tinyint(4) NOT NULL COMMENT '条件类型 1:等于 2:包含 3:范围',
  `condition_field` varchar(100) NOT NULL COMMENT '条件字段',
  `condition_value` varchar(500) DEFAULT NULL COMMENT '条件值',
  `description` varchar(500) DEFAULT NULL COMMENT '描述',
  `status` tinyint(4) DEFAULT 1 COMMENT '状态 0:停用 1:启用',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_role_id` (`role_id`),
  KEY `idx_table_name` (`table_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据权限规则表';

-- 操作日志表（扩展原有operation_log表）
SET @op_log_module_exists := (
  SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'operation_log'
    AND COLUMN_NAME = 'module'
);
SET @add_op_log_module_sql := IF(@op_log_module_exists = 0,
  'ALTER TABLE `operation_log` ADD COLUMN `module` varchar(100) DEFAULT NULL COMMENT ''模块名称'' AFTER `object_type`',
  'SELECT 1'
);
PREPARE add_op_log_module_stmt FROM @add_op_log_module_sql; EXECUTE add_op_log_module_stmt; DEALLOCATE PREPARE add_op_log_module_stmt;

-- 初始化菜单数据
INSERT INTO `sys_menu` (`id`, `parent_id`, `menu_type`, `menu_name`, `menu_code`, `path`, `component`, `icon`, `perms`, `sort_order`, `is_visible`, `status`) VALUES
(1, 0, 1, '登记管理', 'register_manage', '/register', NULL, 'el-icon-document', NULL, 1, 1, 1),
(2, 1, 2, '变更登记管理', 'change_record_manage', '/change-records', 'views/ChangeRecordList', 'el-icon-edit', 'change:record:list', 1, 1, 1),
(3, 2, 3, '新增', 'change_record_create', NULL, NULL, NULL, 'change:record:create', 1, 1, 1),
(4, 2, 3, '编辑', 'change_record_edit', NULL, NULL, NULL, 'change:record:edit', 2, 1, 1),
(5, 2, 3, '删除', 'change_record_delete', NULL, NULL, NULL, 'change:record:delete', 3, 1, 1),
(6, 2, 3, '导出', 'change_record_export', NULL, NULL, NULL, 'change:record:export', 4, 1, 1),
(7, 2, 2, '变更记录详情', 'change_record_detail', '/change-records/:id', 'views/ChangeRecordDetail', NULL, 'change:record:detail', 1, 0, 1),
(8, 2, 2, '变更记录历史', 'change_record_history', '/change-records/:id/history', 'views/ChangeRecordHistory', NULL, 'change:record:history', 2, 0, 1),
(9, 0, 2, '修改密码', 'change_password', '/change-password', 'views/ChangePassword', 'el-icon-lock', 'user:password:change', 99, 1, 1)
ON DUPLICATE KEY UPDATE `menu_name` = VALUES(`menu_name`), `path` = VALUES(`path`), `component` = VALUES(`component`);

-- 初始化角色数据（超级管理员）
INSERT INTO `sys_role` (`id`, `role_code`, `role_name`, `role_type`, `data_scope_type`, `description`, `status`) VALUES
(1, 'SUPER_ADMIN', '超级管理员', 1, 1, '系统超级管理员，拥有所有权限', 1)
ON DUPLICATE KEY UPDATE `role_name` = VALUES(`role_name`);

-- 为超级管理员角色分配所有菜单权限
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
SELECT 1, id FROM `sys_menu` WHERE `status` = 1
ON DUPLICATE KEY UPDATE `role_id` = VALUES(`role_id`);

-- 为admin用户分配超级管理员角色
INSERT INTO `sys_user_role` (`user_id`, `role_id`)
SELECT u.id, 1 FROM `system_user` u WHERE (u.usernumb = 'admin' OR u.username = 'admin') AND NOT EXISTS (
  SELECT 1 FROM `sys_user_role` ur WHERE ur.user_id = u.id AND ur.role_id = 1
);
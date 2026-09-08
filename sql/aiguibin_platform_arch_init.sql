-- ----------------------------
-- Chat2DB export data , export time: 2026-01-25 20:42:18
-- ----------------------------
SET FOREIGN_KEY_CHECKS=0;
-- ----------------------------
-- Table structure for table sys_dept
-- ----------------------------
DROP TABLE IF EXISTS `sys_dept`;
CREATE TABLE `sys_dept` (
  `uuid` varchar(32) NOT NULL COMMENT 'UUID，32位随机字符串',
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
  UNIQUE KEY `uk_uuid` (`uuid`),
  UNIQUE KEY `uk_dept_code` (`dept_code`),
  KEY `idx_org_code` (`org_code`),
  KEY `idx_manager_num` (`manager_num`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='部门信息表';

-- ----------------------------
-- Table structure for table sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu` (
  `uuid` varchar(32) NOT NULL COMMENT 'UUID，32位随机字符串',
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `menu_code` varchar(20) NOT NULL COMMENT '菜单编码，M/P/C/B+17位，类型+17位数字',
  `menu_name` varchar(100) NOT NULL COMMENT '菜单名称',
  `menu_type` varchar(1) NOT NULL COMMENT '类型：M-目录，P-页面，C-组件，B-按钮',
  `parent_menu_code` varchar(20) DEFAULT NULL COMMENT '父菜单编码',
  `icon` varchar(100) DEFAULT NULL COMMENT '图标',
  `path` varchar(200) DEFAULT NULL COMMENT '前端路由路径',
  `component` varchar(200) DEFAULT NULL COMMENT '前端组件路径',
  `resource_key` varchar(100) NOT NULL COMMENT '资源标识（如：system:user:list）',
  `resource_type` varchar(50) DEFAULT 'MENU' COMMENT '资源类型：MENU-菜单，BUTTON-按钮，PAGE-页面',
  `is_external` tinyint DEFAULT '0' COMMENT '是否外部链接：0-否，1-是',
  `is_cache` tinyint DEFAULT '1' COMMENT '是否缓存：0-否，1-是',
  `is_visible` tinyint DEFAULT '1' COMMENT '是否显示：0-否，1-是',
  `sort_order` int DEFAULT '100' COMMENT '排序号',
  `status` tinyint DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
  `description` varchar(500) DEFAULT NULL COMMENT '菜单描述',
  `created_by` varchar(20) NOT NULL COMMENT '创建人用户编号',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` varchar(20) DEFAULT NULL COMMENT '更新人用户编号',
  `updated_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint DEFAULT '0' COMMENT '是否删除：0-否，1-是',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_uuid` (`uuid`),
  UNIQUE KEY `uk_menu_code` (`menu_code`),
  UNIQUE KEY `uk_resource_key` (`resource_key`),
  KEY `idx_parent_menu_code` (`parent_menu_code`),
  KEY `idx_menu_type` (`menu_type`),
  KEY `idx_resource_type` (`resource_type`),
  KEY `idx_path` (`path`(100)),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=1171 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='菜单/按钮资源表';

-- ----------------------------
-- Table structure for table sys_org
-- ----------------------------
DROP TABLE IF EXISTS `sys_org`;
CREATE TABLE `sys_org` (
  `uuid` varchar(32) NOT NULL COMMENT 'UUID，32位随机字符串',
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '自增ID，仅做序号',
  `org_code` varchar(10) NOT NULL COMMENT '机构编码，JG+8位数字',
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
  UNIQUE KEY `uk_uuid` (`uuid`),
  UNIQUE KEY `uk_org_code` (`org_code`),
  KEY `idx_parent_org_code` (`parent_org_code`),
  KEY `idx_level` (`level`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='机构信息表';

-- ----------------------------
-- Table structure for table sys_permission
-- ----------------------------
DROP TABLE IF EXISTS `sys_permission`;
CREATE TABLE `sys_permission` (
  `uuid` varchar(32) NOT NULL COMMENT 'UUID，32位随机字符串',
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `perm_code` varchar(20) NOT NULL COMMENT '权限编码，PERM+16位数字',
  `perm_name` varchar(100) NOT NULL COMMENT '权限名称',
  `perm_key` varchar(100) NOT NULL COMMENT '权限标识（唯一业务标识）',
  `perm_type` tinyint NOT NULL COMMENT '权限类型：1-访问控制，2-数据范围，3-字段控制，4-时间控制，5-业务规则',
  `action_type` varchar(50) NOT NULL COMMENT '操作类型：VIEW-查看，CREATE-新增，UPDATE-修改，DELETE-删除，EXECUTE-执行',
  `effect_type` tinyint DEFAULT '1' COMMENT '生效类型：1-允许，2-禁止',
  `condition_expression` json DEFAULT NULL COMMENT '条件表达式（JSON格式）',
  `sort_order` int DEFAULT '100' COMMENT '排序号',
  `status` tinyint DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
  `description` varchar(500) DEFAULT NULL COMMENT '权限描述',
  `created_by` varchar(20) NOT NULL COMMENT '创建人用户编号',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` varchar(20) DEFAULT NULL COMMENT '更新人用户编号',
  `updated_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint DEFAULT '0' COMMENT '是否删除：0-否，1-是',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_uuid` (`uuid`),
  UNIQUE KEY `uk_perm_code` (`perm_code`),
  UNIQUE KEY `uk_perm_key` (`perm_key`),
  KEY `idx_perm_type` (`perm_type`),
  KEY `idx_action_type` (`action_type`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='统一权限定义表';

-- ----------------------------
-- Table structure for table sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
  `uuid` varchar(32) NOT NULL COMMENT 'UUID，32位随机字符串',
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '自增ID，仅做序号',
  `role_code` varchar(8) NOT NULL COMMENT '角色编码，RL+6位数字',
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
  UNIQUE KEY `uk_uuid` (`uuid`),
  UNIQUE KEY `uk_role_code` (`role_code`),
  UNIQUE KEY `uk_role_name` (`role_name`),
  KEY `idx_role_type` (`role_type`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色信息表';

-- ----------------------------
-- Table structure for table sys_role_permission
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_permission`;
CREATE TABLE `sys_role_permission` (
  `uuid` varchar(32) NOT NULL COMMENT 'UUID，32位随机字符串',
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `role_code` varchar(8) NOT NULL COMMENT '角色编码',
  `perm_code` varchar(20) NOT NULL COMMENT '权限编码',
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
  UNIQUE KEY `uk_uuid` (`uuid`),
  UNIQUE KEY `uk_role_perm` (`role_code`,`perm_code`),
  KEY `idx_role_code` (`role_code`),
  KEY `idx_perm_code` (`perm_code`),
  KEY `idx_auth_type` (`auth_type`),
  KEY `idx_status` (`status`),
  KEY `idx_effective_time` (`effective_start`,`effective_end`)
) ENGINE=InnoDB AUTO_INCREMENT=41 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色权限关联表';

-- ----------------------------
-- Table structure for table sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `uuid` varchar(32) NOT NULL COMMENT 'UUID，32位随机字符串',
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
  UNIQUE KEY `uk_uuid` (`uuid`),
  UNIQUE KEY `uk_user_num` (`user_num`),
  UNIQUE KEY `uk_user_name` (`user_name`),
  KEY `idx_org_code` (`org_code`),
  KEY `idx_dept_code` (`dept_code`),
  KEY `idx_status` (`status`),
  KEY `idx_is_locked` (`is_locked`),
  KEY `idx_phone` (`phone`),
  KEY `idx_email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户信息表';

-- ----------------------------
-- Table structure for table sys_user_dept
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_dept`;
CREATE TABLE `sys_user_dept` (
  `uuid` varchar(32) NOT NULL COMMENT 'UUID，32位随机字符串',
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '自增ID，仅做序号',
  `user_num` varchar(20) NOT NULL COMMENT '用户编号',
  `dept_code` varchar(15) NOT NULL COMMENT '部门编码',
  `dept_name` varchar(100) DEFAULT NULL COMMENT '部门名称',
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
  UNIQUE KEY `uk_uuid` (`uuid`),
  UNIQUE KEY `uk_user_dept` (`user_num`,`dept_code`),
  KEY `idx_user_num` (`user_num`),
  KEY `idx_dept_code` (`dept_code`),
  KEY `idx_is_primary` (`is_primary`),
  KEY `idx_status` (`status`),
  KEY `idx_effective_time` (`effective_start`,`effective_end`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户部门扩展表（支持用户多部门）';

-- ----------------------------
-- Table structure for table sys_user_org
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_org`;
CREATE TABLE `sys_user_org` (
  `uuid` varchar(32) NOT NULL COMMENT 'UUID，32位随机字符串',
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '自增ID，仅做序号',
  `user_num` varchar(20) NOT NULL COMMENT '用户编号',
  `org_code` varchar(10) NOT NULL COMMENT '机构编码',
  `org_name` varchar(100) DEFAULT NULL COMMENT '机构名称',
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
  UNIQUE KEY `uk_uuid` (`uuid`),
  UNIQUE KEY `uk_user_org` (`user_num`,`org_code`),
  KEY `idx_user_num` (`user_num`),
  KEY `idx_org_code` (`org_code`),
  KEY `idx_is_primary` (`is_primary`),
  KEY `idx_status` (`status`),
  KEY `idx_effective_time` (`effective_start`,`effective_end`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户机构扩展表（支持用户多机构）';

-- ----------------------------
-- Table structure for table sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role` (
  `uuid` varchar(32) NOT NULL COMMENT 'UUID，32位随机字符串',
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '自增ID，仅做序号',
  `user_num` varchar(20) NOT NULL COMMENT '用户编号',
  `role_code` varchar(8) NOT NULL COMMENT '角色编码',
  `role_name` varchar(50) DEFAULT NULL COMMENT '角色名称',
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
  UNIQUE KEY `uk_uuid` (`uuid`),
  UNIQUE KEY `uk_user_role` (`user_num`,`role_code`),
  KEY `idx_user_num` (`user_num`),
  KEY `idx_role_code` (`role_code`),
  KEY `idx_is_primary` (`is_primary`),
  KEY `idx_status` (`status`),
  KEY `idx_effective_time` (`effective_start`,`effective_end`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户角色关联表';

SET FOREIGN_KEY_CHECKS=1;

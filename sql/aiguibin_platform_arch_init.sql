-- ----------------------------
-- 1. 机构信息表
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='机构信息表';

-- ----------------------------
-- 2. 部门信息表
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='部门信息表';

-- ----------------------------
-- 3. 用户信息表
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户信息表';

-- ----------------------------
-- 4. 用户机构扩展表（支持用户多机构）
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
  UNIQUE KEY `uk_user_org` (`user_num`, `org_code`),
  KEY `idx_user_num` (`user_num`),
  KEY `idx_org_code` (`org_code`),
  KEY `idx_is_primary` (`is_primary`),
  KEY `idx_status` (`status`),
  KEY `idx_effective_time` (`effective_start`, `effective_end`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户机构扩展表（支持用户多机构）';

-- ----------------------------
-- 5. 用户部门扩展表（支持用户多部门）
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
  UNIQUE KEY `uk_user_dept` (`user_num`, `dept_code`),
  KEY `idx_user_num` (`user_num`),
  KEY `idx_dept_code` (`dept_code`),
  KEY `idx_is_primary` (`is_primary`),
  KEY `idx_status` (`status`),
  KEY `idx_effective_time` (`effective_start`, `effective_end`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户部门扩展表（支持用户多部门）';

-- ----------------------------
-- 6. 角色信息表
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色信息表';

-- ----------------------------
-- 7. 用户角色关联表
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
  UNIQUE KEY `uk_user_role` (`user_num`, `role_code`),
  KEY `idx_user_num` (`user_num`),
  KEY `idx_role_code` (`role_code`),
  KEY `idx_is_primary` (`is_primary`),
  KEY `idx_status` (`status`),
  KEY `idx_effective_time` (`effective_start`, `effective_end`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户角色关联表';

-- ----------------------------
-- 8. 角色机构范围表
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_org`;
CREATE TABLE `sys_role_org` (
  `uuid` varchar(32) NOT NULL COMMENT 'UUID，32位随机字符串',
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '自增ID，仅做序号',
  `role_code` varchar(8) NOT NULL COMMENT '角色编码',
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
  UNIQUE KEY `uk_uuid` (`uuid`),
  UNIQUE KEY `uk_role_org` (`role_code`, `org_code`, `perm_type`),
  KEY `idx_role_code` (`role_code`),
  KEY `idx_org_code` (`org_code`),
  KEY `idx_org_range_type` (`org_range_type`),
  KEY `idx_status` (`status`),
  KEY `idx_effective_time` (`effective_start`, `effective_end`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色机构范围表';

-- ----------------------------
-- 9. 菜单/按钮资源定义表（去权限化）
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='菜单/按钮资源表';

-- ----------------------------
-- 10. API资源定义表
-- ----------------------------
DROP TABLE IF EXISTS `sys_api_resource`;
CREATE TABLE `sys_api_resource` (
  `uuid` varchar(32) NOT NULL COMMENT 'UUID，32位随机字符串',
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `api_code` varchar(32) NOT NULL COMMENT 'API编码，API+29位数字',
  `api_name` varchar(100) NOT NULL COMMENT 'API名称',
  `api_path` varchar(500) NOT NULL COMMENT 'API路径（支持Ant风格）',
  `http_method` varchar(10) NOT NULL COMMENT 'HTTP方法',
  `resource_key` varchar(100) NOT NULL COMMENT '资源标识',
  `service_name` varchar(100) DEFAULT NULL COMMENT '所属服务名称',
  `module_name` varchar(100) DEFAULT NULL COMMENT '模块名称',
  `rate_limit` int DEFAULT NULL COMMENT '限流次数/秒',
  `need_auth` tinyint DEFAULT '1' COMMENT '是否需要认证：0-否，1-是',
  `log_enabled` tinyint DEFAULT '1' COMMENT '是否记录日志：0-否，1-是',
  `sort_order` int DEFAULT '100' COMMENT '排序号',
  `status` tinyint DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
  `description` varchar(500) DEFAULT NULL COMMENT 'API描述',
  `created_by` varchar(20) NOT NULL COMMENT '创建人用户编号',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` varchar(20) DEFAULT NULL COMMENT '更新人用户编号',
  `updated_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint DEFAULT '0' COMMENT '是否删除：0-否，1-是',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_uuid` (`uuid`),
  UNIQUE KEY `uk_api_code` (`api_code`),
  UNIQUE KEY `uk_api_method` (`api_path`, `http_method`),
  UNIQUE KEY `uk_resource_key` (`resource_key`),
  KEY `idx_service_name` (`service_name`),
  KEY `idx_module_name` (`module_name`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='API资源定义表';

-- ----------------------------
-- 11. 统一权限定义表（优化后）
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='统一权限定义表';

-- ----------------------------
-- 12. 权限-资源关联表（多对多，扩展后）
-- ----------------------------
DROP TABLE IF EXISTS `sys_perm_resource`;
CREATE TABLE `sys_perm_resource` (
  `uuid` varchar(32) NOT NULL COMMENT 'UUID，32位随机字符串',
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `perm_code` varchar(20) NOT NULL COMMENT '权限编码',
  `resource_type` varchar(50) NOT NULL COMMENT '资源类型：MENU-菜单，API-接口，DATA-数据实体，FIELD-数据字段，TIME-时间规则，BUSINESS-业务规则',
  `resource_key` varchar(100) NOT NULL COMMENT '资源标识',
  `resource_sub_type` varchar(50) DEFAULT NULL COMMENT '资源子类型（如：BUTTON-按钮，PAGE-页面）',
  `relation_type` tinyint DEFAULT '1' COMMENT '关联类型：1-主关联，2-条件关联，3-扩展关联',
  `condition_expression` json DEFAULT NULL COMMENT '关联条件（JSON格式）',
  `status` tinyint DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
  `description` varchar(500) DEFAULT NULL COMMENT '关联描述',
  `created_by` varchar(20) NOT NULL COMMENT '创建人用户编号',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` varchar(20) DEFAULT NULL COMMENT '更新人用户编号',
  `updated_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint DEFAULT '0' COMMENT '是否删除：0-否，1-是',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_uuid` (`uuid`),
  UNIQUE KEY `uk_perm_resource` (`perm_code`, `resource_type`, `resource_key`),
  KEY `idx_perm_code` (`perm_code`),
  KEY `idx_resource_type_key` (`resource_type`, `resource_key`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='权限-资源关联表（多对多）';

-- ----------------------------
-- 13. 角色权限关联表
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
  UNIQUE KEY `uk_role_perm` (`role_code`, `perm_code`),
  KEY `idx_role_code` (`role_code`),
  KEY `idx_perm_code` (`perm_code`),
  KEY `idx_auth_type` (`auth_type`),
  KEY `idx_status` (`status`),
  KEY `idx_effective_time` (`effective_start`, `effective_end`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色权限关联表';

-- ----------------------------
-- 14. 数据权限子表（修正命名）
-- ----------------------------
DROP TABLE IF EXISTS `sys_data_permission`;
CREATE TABLE `sys_data_permission` (
  `uuid` varchar(32) NOT NULL COMMENT 'UUID，32位随机字符串',
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `perm_code` varchar(20) NOT NULL COMMENT '关联权限编码',
  `data_name` varchar(100) NOT NULL COMMENT '数据权限名称',
  `entity_type` varchar(50) NOT NULL COMMENT '业务实体类型（如：biz_change_record）',
  `scope_type` tinyint NOT NULL COMMENT '范围类型：1-全部，2-本机构，3-本部门，4-本人，5-自定义',
  `include_children` tinyint DEFAULT '1' COMMENT '是否包含下级：0-否，1-是（针对机构、部门）',
  `rule_type` tinyint NOT NULL COMMENT '规则类型：1-预定义规则，2-自定义SQL，3-组合规则',
  `custom_sql` text COMMENT '自定义SQL条件（WHERE子句内容）',
  `rule_expression` json DEFAULT NULL COMMENT '组合规则表达式（JSON格式）',
  `rule_priority` tinyint DEFAULT '1' COMMENT '规则优先级：1-高，2-中，3-低',
  `condition_fields` json DEFAULT NULL COMMENT '条件字段配置',
  `is_global` tinyint DEFAULT '0' COMMENT '是否全局规则：0-否，1-是',
  `status` tinyint DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
  `description` varchar(500) DEFAULT NULL COMMENT '规则描述',
  `created_by` varchar(20) NOT NULL COMMENT '创建人用户编号',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` varchar(20) DEFAULT NULL COMMENT '更新人用户编号',
  `updated_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint DEFAULT '0' COMMENT '是否删除：0-否，1-是',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_uuid` (`uuid`),
  UNIQUE KEY `uk_perm_code` (`perm_code`),
  KEY `idx_entity_type` (`entity_type`),
  KEY `idx_scope_type` (`scope_type`),
  KEY `idx_rule_type` (`rule_type`),
  KEY `idx_status` (`status`),
  KEY `idx_is_global` (`is_global`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='数据权限子表';

-- ----------------------------
-- 15. 字段权限子表
-- ----------------------------
DROP TABLE IF EXISTS `sys_field_permission`;
CREATE TABLE `sys_field_permission` (
  `uuid` varchar(32) NOT NULL COMMENT 'UUID，32位随机字符串',
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `perm_code` varchar(20) NOT NULL COMMENT '关联权限编码',
  `field_code` varchar(15) NOT NULL COMMENT '字段编码，15位',
  `entity_type` varchar(50) NOT NULL COMMENT '业务实体类型',
  `field_name` varchar(100) NOT NULL COMMENT '字段名称（数据库字段名）',
  `field_alias` varchar(100) NOT NULL COMMENT '字段别名（显示名称）',
  `field_type` tinyint NOT NULL COMMENT '字段类型：1-可见，2-可编辑，3-必填，4-隐藏，5-只读',
  `condition_expression` json DEFAULT NULL COMMENT '条件表达式（JSON格式，满足条件时生效）',
  `default_value` varchar(500) DEFAULT NULL COMMENT '字段默认值',
  `validation_rules` json DEFAULT NULL COMMENT '验证规则',
  `ui_config` json DEFAULT NULL COMMENT 'UI配置（如组件类型、样式等）',
  `sort_order` int DEFAULT '100' COMMENT '排序号',
  `status` tinyint DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
  `description` varchar(500) DEFAULT NULL COMMENT '字段权限描述',
  `created_by` varchar(20) NOT NULL COMMENT '创建人用户编号',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` varchar(20) DEFAULT NULL COMMENT '更新人用户编号',
  `updated_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint DEFAULT '0' COMMENT '是否删除：0-否，1-是',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_uuid` (`uuid`),
  UNIQUE KEY `uk_field_perm` (`perm_code`, `entity_type`, `field_name`),
  UNIQUE KEY `uk_field_code` (`field_code`),
  KEY `idx_entity_type` (`entity_type`),
  KEY `idx_field_type` (`field_type`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='字段权限子表';

-- ----------------------------
-- 16. 时间权限子表
-- ----------------------------
DROP TABLE IF EXISTS `sys_time_permission`;
CREATE TABLE `sys_time_permission` (
  `uuid` varchar(32) NOT NULL COMMENT 'UUID，32位随机字符串',
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `perm_code` varchar(20) NOT NULL COMMENT '关联权限编码',
  `time_name` varchar(100) NOT NULL COMMENT '时间权限名称',
  `time_type` tinyint NOT NULL COMMENT '时间类型：1-工作日，2-节假日，3-特定日期，4-时间范围',
  `allowed_days` varchar(20) DEFAULT NULL COMMENT '允许访问的星期（1-7，逗号分隔）',
  `start_date` date DEFAULT NULL COMMENT '开始日期',
  `end_date` date DEFAULT NULL COMMENT '结束日期',
  `start_time` time DEFAULT NULL COMMENT '每天开始时间',
  `end_time` time DEFAULT NULL COMMENT '每天结束时间',
  `specific_dates` json DEFAULT NULL COMMENT '特定日期列表（JSON数组）',
  `exclude_dates` json DEFAULT NULL COMMENT '排除日期列表（JSON数组）',
  `timezone` varchar(50) DEFAULT 'Asia/Shanghai' COMMENT '时区',
  `is_recurring` tinyint DEFAULT '1' COMMENT '是否循环：0-否，1-是',
  `holiday_excluded` tinyint DEFAULT '0' COMMENT '是否排除节假日：0-否，1-是',
  `action_type` tinyint DEFAULT '1' COMMENT '动作类型：1-允许，2-禁止',
  `status` tinyint DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
  `description` varchar(500) DEFAULT NULL COMMENT '时间权限描述',
  `created_by` varchar(20) NOT NULL COMMENT '创建人用户编号',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` varchar(20) DEFAULT NULL COMMENT '更新人用户编号',
  `updated_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint DEFAULT '0' COMMENT '是否删除：0-否，1-是',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_uuid` (`uuid`),
  UNIQUE KEY `uk_perm_code` (`perm_code`),
  KEY `idx_time_type` (`time_type`),
  KEY `idx_date_range` (`start_date`, `end_date`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='时间权限子表';

-- ----------------------------
-- 17. 业务权限子表（修正命名）
-- ----------------------------
DROP TABLE IF EXISTS `sys_biz_permission`;
CREATE TABLE `sys_biz_permission` (
  `uuid` varchar(32) NOT NULL COMMENT 'UUID，32位随机字符串',
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `perm_code` varchar(20) NOT NULL COMMENT '关联权限编码',
  `business_name` varchar(100) NOT NULL COMMENT '业务权限名称',
  `business_type` varchar(50) NOT NULL COMMENT '业务类型',
  `business_rule` json NOT NULL COMMENT '业务规则（JSON格式）',
  `rule_engine` varchar(50) DEFAULT 'DROOLS' COMMENT '规则引擎：DROOLS/EASY_RULES/SPEL',
  `rule_priority` tinyint DEFAULT '1' COMMENT '规则优先级',
  `condition_expression` text COMMENT '条件表达式',
  `action_expression` text COMMENT '动作表达式',
  `effect_type` tinyint DEFAULT '1' COMMENT '生效类型：1-允许，2-禁止，3-限制',
  `limit_config` json DEFAULT NULL COMMENT '限制配置',
  `audit_enabled` tinyint DEFAULT '1' COMMENT '是否审计：0-否，1-是',
  `status` tinyint DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
  `description` varchar(500) DEFAULT NULL COMMENT '业务权限描述',
  `created_by` varchar(20) NOT NULL COMMENT '创建人用户编号',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` varchar(20) DEFAULT NULL COMMENT '更新人用户编号',
  `updated_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint DEFAULT '0' COMMENT '是否删除：0-否，1-是',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_uuid` (`uuid`),
  UNIQUE KEY `uk_perm_code` (`perm_code`),
  KEY `idx_business_type` (`business_type`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='业务权限子表';

-- ----------------------------
-- 18. 字典类型表
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_type`;
CREATE TABLE `sys_dict_type` (
  `uuid` varchar(32) NOT NULL COMMENT 'UUID，32位随机字符串',
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
  UNIQUE KEY `uk_uuid` (`uuid`),
  UNIQUE KEY `uk_dict_type_code` (`dict_type_code`),
  KEY `idx_status` (`status`),
  KEY `idx_is_deleted` (`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='字典类型表';

-- ----------------------------
-- 19. 字典项表
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_item`;
CREATE TABLE `sys_dict_item` (
  `uuid` varchar(32) NOT NULL COMMENT 'UUID，32位随机字符串',
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
  UNIQUE KEY `uk_uuid` (`uuid`),
  KEY `idx_dict_type_code` (`dict_type_code`),
  KEY `idx_group_code` (`group_code`),
  KEY `idx_dict_value` (`dict_value`),
  KEY `idx_status` (`status`),
  KEY `idx_is_deleted` (`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='字典项表';

-- ----------------------------
-- 20. 系统操作日志表
-- ----------------------------
DROP TABLE IF EXISTS `sys_operation_log`;
CREATE TABLE `sys_operation_log` (
  `uuid` varchar(32) NOT NULL COMMENT 'UUID，32位随机字符串',
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
  UNIQUE KEY `uk_uuid` (`uuid`),
  KEY `idx_object` (`object_type`, `object_id`),
  KEY `idx_operator_time` (`operator_num`, `operation_time`),
  KEY `idx_object_code` (`object_code`),
  KEY `idx_operation_type` (`operation_type`),
  KEY `idx_result` (`result`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统操作日志表';

-- ----------------------------
-- 21. 业务类型注册表
-- ----------------------------
DROP TABLE IF EXISTS `biz_business_type`;
CREATE TABLE `biz_business_type` (
  `uuid` varchar(32) NOT NULL COMMENT 'UUID，32位随机字符串',
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
  UNIQUE KEY `uk_uuid` (`uuid`),
  UNIQUE KEY `uk_type_code` (`type_code`),
  UNIQUE KEY `uk_main_table_name` (`main_table_name`),
  KEY `idx_is_active` (`is_active`),
  KEY `idx_is_deleted` (`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='业务类型注册表';

-- ----------------------------
-- 22. 审批流程定义表
-- ----------------------------
DROP TABLE IF EXISTS `biz_approval_flow`;
CREATE TABLE `biz_approval_flow` (
  `uuid` varchar(32) NOT NULL COMMENT 'UUID，32位随机字符串',
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
  UNIQUE KEY `uk_uuid` (`uuid`),
  UNIQUE KEY `uk_flow_id` (`flow_id`),
  UNIQUE KEY `uk_flow_business_type` (`business_type`,`is_default`) COMMENT '每种业务类型只能有一个默认流程',
  KEY `idx_business_type` (`business_type`),
  KEY `idx_is_active` (`is_active`),
  KEY `idx_is_deleted` (`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='审批流程定义表';

-- ----------------------------
-- 23. 审批节点定义表
-- ----------------------------
DROP TABLE IF EXISTS `biz_approval_node`;
CREATE TABLE `biz_approval_node` (
  `uuid` varchar(32) NOT NULL COMMENT 'UUID，32位随机字符串',
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
  UNIQUE KEY `uk_uuid` (`uuid`),
  UNIQUE KEY `uk_node_id` (`node_id`),
  KEY `idx_flow_id` (`flow_id`),
  KEY `idx_node_order` (`node_order`),
  KEY `idx_approver_num` (`approver_num`),
  KEY `idx_is_active` (`is_active`),
  KEY `idx_is_deleted` (`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='审批节点定义表';

-- ----------------------------
-- 24. 待办任务表
-- ----------------------------
DROP TABLE IF EXISTS `biz_approval_task`;
CREATE TABLE `biz_approval_task` (
  `uuid` varchar(32) NOT NULL COMMENT 'UUID，32位随机字符串',
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
  UNIQUE KEY `uk_uuid` (`uuid`),
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
-- 25. 审批流转记录表
-- ----------------------------
DROP TABLE IF EXISTS `biz_approval_log`;
CREATE TABLE `biz_approval_log` (
  `uuid` varchar(32) NOT NULL COMMENT 'UUID，32位随机字符串',
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
  UNIQUE KEY `uk_uuid` (`uuid`),
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
-- 26. 变更记录表
-- ----------------------------
DROP TABLE IF EXISTS `biz_change_record`;
CREATE TABLE `biz_change_record` (
  `uuid` varchar(32) NOT NULL COMMENT 'UUID，32位随机字符串',
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
  UNIQUE KEY `uk_uuid` (`uuid`),
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='变更记录表';

-- ----------------------------
-- 27. 变更历史表
-- ----------------------------
DROP TABLE IF EXISTS `biz_change_history`;
CREATE TABLE `biz_change_history` (
  `uuid` varchar(32) NOT NULL COMMENT 'UUID，32位随机字符串',
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
  UNIQUE KEY `uk_uuid` (`uuid`),
  KEY `idx_record_id` (`record_id`),
  KEY `idx_record_code` (`record_code`),
  KEY `idx_operation_time` (`operation_time`),
  KEY `idx_operation_user_num` (`operation_user_num`),
  KEY `idx_operation_type` (`operation_type`),
  KEY `idx_current_status` (`current_status`),
  KEY `idx_approval_task_id` (`approval_task_id`),
  KEY `idx_approval_log_id` (`approval_log_id`),
  KEY `idx_approval_operation_type` (`approval_operation_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='变更历史表';

-- ----------------------------
-- 28. 权限统一查询视图（优化后）
-- ----------------------------
-- CREATE OR REPLACE VIEW `v_user_full_permissions` AS
-- SELECT 
--     -- 用户和角色信息
--     u.user_num, u.user_name, r.role_code, r.role_name,
    
--     -- 权限信息
--     p.perm_code, p.perm_name, p.perm_key, 
--     p.perm_type, p.action_type, p.effect_type,
    
--     -- 资源信息
--     pr.resource_type, pr.resource_key, pr.resource_sub_type,
--     pr.relation_type, pr.condition_expression as resource_condition,
    
--     -- 菜单资源详情
--     CASE WHEN pr.resource_type = 'MENU' THEN m.menu_name END as menu_name,
--     CASE WHEN pr.resource_type = 'MENU' THEN m.path END as menu_path,
    
--     -- API资源详情
--     CASE WHEN pr.resource_type = 'API' THEN ar.api_name END as api_name,
--     CASE WHEN pr.resource_type = 'API' THEN ar.api_path END as api_path,
--     CASE WHEN pr.resource_type = 'API' THEN ar.http_method END as http_method,
    
--     -- 数据权限详情
--     dp.entity_type as data_entity, dp.scope_type as data_scope,
    
--     -- 字段权限详情
--     fp.field_name, fp.field_alias, fp.field_type as field_permission_type,
    
--     -- 时间权限详情
--     tp.time_type, tp.start_time, tp.end_time,
    
--     -- 业务权限详情
--     bp.business_type, bp.business_rule,
    
--     -- 授权信息
--     rp.auth_type, rp.effective_start, rp.effective_end
    
-- FROM sys_user u
-- JOIN sys_user_role ur ON u.user_num = ur.user_num AND ur.status = 1 AND ur.is_deleted = 0
-- JOIN sys_role r ON ur.role_code = r.role_code AND r.status = 1 AND r.is_deleted = 0
-- JOIN sys_role_permission rp ON r.role_code = rp.role_code AND rp.status = 1 AND rp.is_deleted = 0
-- JOIN sys_permission p ON rp.perm_code = p.perm_code AND p.status = 1 AND p.is_deleted = 0
-- LEFT JOIN sys_perm_resource pr ON p.perm_code = pr.perm_code AND pr.status = 1 AND pr.is_deleted = 0
-- LEFT JOIN sys_menu m ON pr.resource_type = 'MENU' AND pr.resource_key = m.resource_key AND m.status = 1 AND m.is_deleted = 0
-- LEFT JOIN sys_api_resource ar ON pr.resource_type = 'API' AND pr.resource_key = ar.resource_key AND ar.status = 1 AND ar.is_deleted = 0
-- LEFT JOIN sys_data_permission dp ON p.perm_type = 2 AND p.perm_code = dp.perm_code AND dp.status = 1 AND dp.is_deleted = 0
-- LEFT JOIN sys_field_permission fp ON p.perm_type = 3 AND p.perm_code = fp.perm_code AND fp.status = 1 AND fp.is_deleted = 0
-- LEFT JOIN sys_time_permission tp ON p.perm_type = 4 AND p.perm_code = tp.perm_code AND tp.status = 1 AND tp.is_deleted = 0
-- LEFT JOIN sys_biz_permission bp ON p.perm_type = 5 AND p.perm_code = bp.perm_code AND bp.status = 1 AND bp.is_deleted = 0
-- WHERE u.status = 1 AND u.is_deleted = 0;


SELECT 'SYS系统初始化数据插入完成！' AS '初始化结果';
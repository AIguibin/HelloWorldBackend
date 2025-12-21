## 一、基础信息表（5张）
### 1. 机构表 (sys_org)
```sql
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
```

### 2. 部门表 (sys_dept)
```sql
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
```

### 3. 用户表 (sys_user)
```sql
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
```

### 4. 角色表 (sys_role)
```sql
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
```

### 5. 菜单表 (sys_menu)
```sql
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
```

## 二、权限核心表（4张）
### 6. 权限表 (sys_permission)
```sql
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
```

### 7. 角色权限关联表 (sys_role_permission)
```sql
CREATE TABLE sys_role_permission (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增ID，仅做序号',
    role_code VARCHAR(4) NOT NULL COMMENT '角色编码',
    perm_code VARCHAR(5) NOT NULL COMMENT '权限编码',
    auth_type TINYINT DEFAULT 1 COMMENT '授权类型：1-允许，2-拒绝',
    effective_start DATETIME COMMENT '生效开始时间',
    effective_end DATETIME COMMENT '生效结束时间',
    priority INT DEFAULT 100 COMMENT '优先级（数字越小优先级越高）',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    description VARCHAR(500) COMMENT '描述',
    created_by VARCHAR(20) NOT NULL COMMENT '创建人用户编号',
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by VARCHAR(20) COMMENT '更新人用户编号',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除：0-否，1-是',
    PRIMARY KEY (id),
    UNIQUE KEY uk_role_perm (role_code, perm_code),
    KEY idx_role_code (role_code),
    KEY idx_perm_code (perm_code),
    KEY idx_auth_type (auth_type),
    KEY idx_status (status),
    KEY idx_effective_time (effective_start, effective_end)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限关联表';
```

### 8. 用户角色关联表 (sys_user_role)
```sql
CREATE TABLE `sys_user_role` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '自增ID，仅做序号',
  `user_num` varchar(20) NOT NULL COMMENT '用户编号',
  `role_code` varchar(5) NOT NULL COMMENT '角色编码',
  `role_name` varchar(255) DEFAULT NULL,
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
```

### 9. 数据权限规则表 (sys_data_rule)
```sql
CREATE TABLE sys_data_rule (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增ID，仅做序号',
    rule_code VARCHAR(10) NOT NULL COMMENT '规则编码，10位',
    rule_name VARCHAR(100) NOT NULL COMMENT '规则名称',
    rule_type TINYINT NOT NULL COMMENT '规则类型：1-预定义，2-自定义SQL，3-组合规则',
    perm_code VARCHAR(5) COMMENT '关联的权限编码',
    entity_type VARCHAR(50) NOT NULL COMMENT '业务实体类型（如：sys_user, sys_order）',
    scope_type TINYINT COMMENT '预定义范围：1-全部，2-本机构，3-本部门，4-本人',
    include_children TINYINT DEFAULT 1 COMMENT '是否包含下级：0-否，1-是（针对机构、部门）',
    custom_sql TEXT COMMENT '自定义SQL条件（WHERE子句内容）',
    rule_expression JSON COMMENT '组合规则表达式（JSON格式）',
    rule_priority TINYINT DEFAULT 1 COMMENT '规则优先级：1-高，2-中，3-低',
    is_global TINYINT DEFAULT 0 COMMENT '是否全局规则：0-否，1-是',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    description VARCHAR(500) COMMENT '规则描述',
    created_by VARCHAR(20) NOT NULL COMMENT '创建人用户编号',
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by VARCHAR(20) COMMENT '更新人用户编号',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除：0-否，1-是',
    PRIMARY KEY (id),
    UNIQUE KEY uk_rule_code (rule_code),
    KEY idx_perm_code (perm_code),
    KEY idx_entity_type (entity_type),
    KEY idx_rule_type (rule_type),
    KEY idx_status (status),
    KEY idx_is_global (is_global)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据权限规则表';
```

## 三、关联扩展表（5张）
### 10. 用户机构扩展表 (sys_user_org)
```sql
CREATE TABLE `sys_user_org` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '自增ID，仅做序号',
  `user_num` varchar(20) NOT NULL COMMENT '用户编号',
  `org_code` varchar(10) NOT NULL COMMENT '机构编码',
  `org_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '机构名称',
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
```

### 11. 用户部门扩展表 (sys_user_dept)
```sql
CREATE TABLE `sys_user_dept` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '自增ID，仅做序号',
  `user_num` varchar(20) NOT NULL COMMENT '用户编号',
  `dept_code` varchar(15) NOT NULL COMMENT '部门编码',
  `dept_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '部门名称',
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
```

### 12. 字段权限表 (sys_field_permission)
```sql
CREATE TABLE sys_field_permission (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增ID，仅做序号',
    field_code VARCHAR(15) NOT NULL COMMENT '字段编码，15位',
    entity_type VARCHAR(50) NOT NULL COMMENT '业务实体类型',
    field_name VARCHAR(100) NOT NULL COMMENT '字段名称（数据库字段名）',
    field_alias VARCHAR(100) NOT NULL COMMENT '字段别名（显示名称）',
    role_code VARCHAR(4) NOT NULL COMMENT '角色编码',
    perm_type TINYINT NOT NULL COMMENT '权限类型：1-可见，2-可编辑，3-必填，4-隐藏，5-只读',
    condition_expression JSON COMMENT '条件表达式（JSON格式，满足条件时生效）',
    sort_order INT DEFAULT 100 COMMENT '排序号',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    description VARCHAR(500) COMMENT '字段权限描述',
    created_by VARCHAR(20) NOT NULL COMMENT '创建人用户编号',
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by VARCHAR(20) COMMENT '更新人用户编号',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除：0-否，1-是',
    PRIMARY KEY (id),
    UNIQUE KEY uk_field_perm (entity_type, field_name, role_code, perm_type),
    KEY idx_field_code (field_code),
    KEY idx_entity_type (entity_type),
    KEY idx_role_code (role_code),
    KEY idx_perm_type (perm_type),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='字段权限表';
```

### 13. 角色机构范围表 (sys_role_org)
```sql
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
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色机构范围表';
```

### 14. 时间权限表 (sys_time_permission)
```sql
CREATE TABLE sys_time_permission (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增ID，仅做序号',
    time_code VARCHAR(10) NOT NULL COMMENT '时间权限编码，10位',
    perm_code VARCHAR(5) COMMENT '关联的权限编码',
    user_num VARCHAR(5) COMMENT '用户编号（为空表示应用于所有用户）',
    role_code VARCHAR(4) COMMENT '角色编号（为空表示应用于所有角色）',
    allowed_days VARCHAR(20) COMMENT '允许访问的星期（如：1,2,3,4,5,6,7）',
    start_time TIME COMMENT '每天开始时间',
    end_time TIME COMMENT '每天结束时间',
    effective_start DATE COMMENT '生效开始日期',
    effective_end DATE COMMENT '生效结束日期',
    timezone VARCHAR(50) DEFAULT 'Asia/Shanghai' COMMENT '时区',
    is_recurring TINYINT DEFAULT 1 COMMENT '是否循环：0-否，1-是',
    holiday_excluded TINYINT DEFAULT 0 COMMENT '是否排除节假日：0-否，1-是',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    description VARCHAR(500) COMMENT '时间权限描述',
    created_by VARCHAR(20) NOT NULL COMMENT '创建人用户编号',
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by VARCHAR(20) COMMENT '更新人用户编号',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除：0-否，1-是',
    PRIMARY KEY (id),
    UNIQUE KEY uk_time_code (time_code),
    KEY idx_perm_code (perm_code),
    KEY idx_user_num (user_num),
    KEY idx_role_code (role_code),
    KEY idx_effective_date (effective_start, effective_end),
    KEY idx_allowed_days (allowed_days(10)),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='时间权限表';
```

## 四、表关系总结
### 1. 核心关系链
```plain
用户(sys_user) 
  → 用户角色(sys_user_role) → 角色(sys_role)
    → 角色权限(sys_role_permission) → 权限(sys_permission)
```

### 2. 权限扩展关系
```plain
权限(sys_permission)
  ├── 菜单权限 → 关联 sys_menu.menu_code
  ├── 数据权限 → 关联 sys_data_rule.rule_code
  ├── 字段权限 → 关联 sys_field_permission.field_code
  └── 时间权限 → 关联 sys_time_permission.time_code
```

### 3. 组织架构关系
```plain
机构(sys_org)
  ├── 部门(sys_dept) → 所属机构 (dept.org_code = org.org_code)
  ├── 用户(sys_user) → 主机构/主部门 (user.org_code/user.dept_code)
  ├── 用户机构扩展(sys_user_org) → 用户多机构
  ├── 用户部门扩展(sys_user_dept) → 用户多部门
  └── 角色机构范围(sys_role_org) → 角色可管理机构
```


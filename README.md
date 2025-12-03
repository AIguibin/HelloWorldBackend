# aiguibin-platform-arch

前后端一体化的架构管理系统（Spring Boot 2.7 + MyBatis-Plus + MySQL，前端 Vue 2 + Element UI，支持 RBAC 与 CSRF）。

## 项目结构
```
aiguibin-platform-arch/
├── src/main/java/com/aiguibin/online/table/      # Java 源码
│   ├── controller/                               # REST 控制器
│   ├── entity/                                   # 实体类
│   ├── mapper/                                   # MyBatis-Plus Mapper
│   ├── service/                                  # 业务层
│   ├── dto/                                      # 数据传输对象
│   ├── config/                                   # 配置类（分页、拦截器、异常处理、密码加密等）
│   └── SpringbootStarterApplication.java         # 启动类
├── src/main/resources/
│   ├── static/                                   # Vue 构建后的静态资源
│   ├── templates/                                # 预留模板目录
│   ├── application.yml                           # Spring Boot 配置
│   └── mapper/                                   # XML 映射（MyBatis-Plus无需也可运行）
└── src/main/webapp/                              # Vue 前端源码
    ├── public/
    ├── src/
    │   ├── components/
    │   ├── views/
    │   ├── router/
    │   ├── api/
    │   ├── utils/
    │   └── store/（可选）
    ├── package.json
    └── vue.config.js
```

## 数据库准备
- MySQL 建库：`aiguibin_arch_tables`
- 表结构：

```sql
-- ============================================
-- 权限管理及业务系统建表语句
-- 使用统一的字符集和排序规则
-- ============================================

-- ----------------------------
-- 1. 机构信息表
-- ----------------------------
CREATE TABLE sys_org (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增ID，仅做序号',
    org_code VARCHAR(10) NOT NULL COMMENT '机构编码，10位定长层级编码',
    org_name VARCHAR(100) NOT NULL COMMENT '机构名称',
    parent_org_code VARCHAR(10) COMMENT '上级机构编码',
    level TINYINT NOT NULL COMMENT '机构层级：1-5级',
    sort_order INT DEFAULT 100 COMMENT '排序号',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    description VARCHAR(500) COMMENT '机构描述',
    created_by VARCHAR(20) NOT NULL COMMENT '创建人用户编号',
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by VARCHAR(20) COMMENT '更新人用户编号',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除：0-否，1-是',
    PRIMARY KEY (id),
    UNIQUE KEY uk_org_code (org_code),
    KEY idx_parent_org_code (parent_org_code),
    KEY idx_level (level),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='机构信息表';

-- ----------------------------
-- 2. 部门信息表
-- ----------------------------
CREATE TABLE sys_dept (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增ID，仅做序号',
    dept_code VARCHAR(15) NOT NULL COMMENT '部门编码，15位：机构编码(10位)+D+4位序列',
    dept_name VARCHAR(100) NOT NULL COMMENT '部门名称',
    org_code VARCHAR(10) NOT NULL COMMENT '所属机构编码',
    manager_num VARCHAR(20) COMMENT '部门负责人用户编号',
    sort_order INT DEFAULT 100 COMMENT '排序号',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    description VARCHAR(500) COMMENT '部门描述',
    created_by VARCHAR(20) NOT NULL COMMENT '创建人用户编号',
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by VARCHAR(20) COMMENT '更新人用户编号',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除：0-否，1-是',
    PRIMARY KEY (id),
    UNIQUE KEY uk_dept_code (dept_code),
    KEY idx_org_code (org_code),
    KEY idx_manager_num (manager_num),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='部门信息表';

-- ----------------------------
-- 3. 用户信息表
-- ----------------------------
CREATE TABLE sys_user (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增ID，仅做序号',
    user_num VARCHAR(20) NOT NULL COMMENT '用户编号，5位，9开头',
    user_name VARCHAR(50) NOT NULL COMMENT '用户姓名',
    nickname VARCHAR(50) COMMENT '用户昵称',
    gender TINYINT DEFAULT 0 COMMENT '性别：0-未知，1-男，2-女',
    password VARCHAR(128) NOT NULL COMMENT '密码（加密存储）',
    salt VARCHAR(32) COMMENT '密码盐值',
    org_code VARCHAR(10) COMMENT '主机构编码',
    dept_code VARCHAR(15) COMMENT '主部门编码',
    email VARCHAR(100) COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '手机号',
    avatar VARCHAR(500) COMMENT '头像URL',
    last_login_time DATETIME COMMENT '最后登录时间',
    last_login_ip VARCHAR(50) COMMENT '最后登录IP',
    login_count INT DEFAULT 0 COMMENT '登录次数',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    is_locked TINYINT DEFAULT 0 COMMENT '是否锁定：0-否，1-是',
    lock_time DATETIME COMMENT '锁定时间',
    lock_reason VARCHAR(200) COMMENT '锁定原因',
    is_special TINYINT DEFAULT 0 COMMENT '是否特殊用户：0-否，1-是（如AIguibin）',
    pwd_expire_time DATETIME COMMENT '密码过期时间',
    pwd_modified_time DATETIME COMMENT '密码最后修改时间',
    created_by VARCHAR(20) NOT NULL COMMENT '创建人用户编号',
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by VARCHAR(20) COMMENT '更新人用户编号',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除：0-否，1-是',
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_num (user_num),
    UNIQUE KEY uk_user_name (user_name),
    KEY idx_org_code (org_code),
    KEY idx_dept_code (dept_code),
    KEY idx_status (status),
    KEY idx_is_locked (is_locked),
    KEY idx_phone (phone),
    KEY idx_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户信息表';

-- ----------------------------
-- 4. 角色信息表
-- ----------------------------
CREATE TABLE sys_role (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增ID，仅做序号',
    role_code VARCHAR(5) NOT NULL COMMENT '角色编码，5位：R+4位数字',
    role_name VARCHAR(50) NOT NULL COMMENT '角色名称',
    role_type TINYINT DEFAULT 1 COMMENT '角色类型：1-系统角色，2-业务角色，3-自定义角色',
    data_scope_type TINYINT DEFAULT 4 COMMENT '默认数据范围：1-全部，2-本机构，3-本部门，4-本人，5-自定义',
    sort_order INT DEFAULT 100 COMMENT '排序号',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    description VARCHAR(500) COMMENT '角色描述',
    created_by VARCHAR(20) NOT NULL COMMENT '创建人用户编号',
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by VARCHAR(20) COMMENT '更新人用户编号',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除：0-否，1-是',
    PRIMARY KEY (id),
    UNIQUE KEY uk_role_code (role_code),
    UNIQUE KEY uk_role_name (role_name),
    KEY idx_role_type (role_type),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色信息表';

-- ----------------------------
-- 5. 菜单/页面/按钮表
-- ----------------------------
CREATE TABLE sys_menu (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增ID，仅做序号',
    menu_code VARCHAR(20) NOT NULL COMMENT '菜单编码，20位',
    menu_name VARCHAR(100) NOT NULL COMMENT '菜单名称',
    menu_type TINYINT NOT NULL COMMENT '类型：1-目录，2-页面，3-按钮',
    parent_menu_code VARCHAR(20) COMMENT '父菜单编码',
    icon VARCHAR(100) COMMENT '图标',
    path VARCHAR(200) COMMENT '路由路径（前端使用）',
    component VARCHAR(200) COMMENT '组件路径（前端使用）',
    url VARCHAR(500) COMMENT '访问URL（后端API路径）',
    http_method VARCHAR(10) COMMENT 'HTTP方法：GET,POST,PUT,DELETE等',
    is_external TINYINT DEFAULT 0 COMMENT '是否外部链接：0-否，1-是',
    is_cache TINYINT DEFAULT 1 COMMENT '是否缓存：0-否，1-是',
    is_visible TINYINT DEFAULT 1 COMMENT '是否显示：0-否，1-是',
    permission_key VARCHAR(100) COMMENT '权限标识（如：user:view）',
    sort_order INT DEFAULT 100 COMMENT '排序号',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    description VARCHAR(500) COMMENT '菜单描述',
    created_by VARCHAR(20) NOT NULL COMMENT '创建人用户编号',
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by VARCHAR(20) COMMENT '更新人用户编号',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除：0-否，1-是',
    PRIMARY KEY (id),
    UNIQUE KEY uk_menu_code (menu_code),
    KEY idx_parent_menu_code (parent_menu_code),
    KEY idx_menu_type (menu_type),
    KEY idx_permission_key (permission_key),
    KEY idx_path (path(100)),
    KEY idx_url (url(100)),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='菜单/页面/按钮表';

-- ----------------------------
-- 6. 统一权限表
-- ----------------------------
CREATE TABLE sys_permission (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增ID，仅做序号',
    perm_code VARCHAR(5) NOT NULL COMMENT '权限编码，5位：P+4位数字',
    perm_name VARCHAR(100) NOT NULL COMMENT '权限名称',
    perm_key VARCHAR(100) NOT NULL COMMENT '权限标识（如：user:create）',
    perm_type TINYINT NOT NULL COMMENT '权限类型：1-菜单，2-操作，3-接口，4-数据，5-字段，6-时间，7-业务',
    menu_code VARCHAR(20) COMMENT '关联的菜单编码（当perm_type=1,2时）',
    api_path VARCHAR(500) COMMENT '接口路径（当perm_type=3时）',
    entity_type VARCHAR(50) COMMENT '业务实体类型（如：user,order，当perm_type=4,5时）',
    entity_field VARCHAR(100) COMMENT '业务实体字段（当perm_type=5时）',
    rule_type TINYINT COMMENT '规则类型：1-预定义，2-自定义SQL',
    scope_type TINYINT COMMENT '预定义范围：1-全部，2-本机构，3-本部门，4-本人',
    custom_rule TEXT COMMENT '自定义规则（JSON或SQL片段）',
    is_default TINYINT DEFAULT 0 COMMENT '是否默认权限：0-否，1-是',
    sort_order INT DEFAULT 100 COMMENT '排序号',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    description VARCHAR(500) COMMENT '权限描述',
    created_by VARCHAR(20) NOT NULL COMMENT '创建人用户编号',
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by VARCHAR(20) COMMENT '更新人用户编号',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除：0-否，1-是',
    PRIMARY KEY (id),
    UNIQUE KEY uk_perm_code (perm_code),
    UNIQUE KEY uk_perm_key (perm_key),
    KEY idx_perm_type (perm_type),
    KEY idx_menu_code (menu_code),
    KEY idx_entity_type (entity_type),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='统一权限表';

-- ----------------------------
-- 7. 角色权限关联表
-- ----------------------------
CREATE TABLE sys_role_permission (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增ID，仅做序号',
    role_code VARCHAR(5) NOT NULL COMMENT '角色编码',
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色权限关联表';

-- ----------------------------
-- 8. 用户角色关联表
-- ----------------------------
CREATE TABLE sys_user_role (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增ID，仅做序号',
    user_num VARCHAR(20) NOT NULL COMMENT '用户编号',
    role_code VARCHAR(5) NOT NULL COMMENT '角色编码',
    is_primary TINYINT DEFAULT 0 COMMENT '是否主角色：0-否，1-是（一个用户只有一个主角色）',
    effective_start DATETIME COMMENT '生效开始时间',
    effective_end DATETIME COMMENT '生效结束时间',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    description VARCHAR(500) COMMENT '描述',
    created_by VARCHAR(20) NOT NULL COMMENT '创建人用户编号',
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by VARCHAR(20) COMMENT '更新人用户编号',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除：0-否，1-是',
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_role (user_num, role_code),
    KEY idx_user_num (user_num),
    KEY idx_role_code (role_code),
    KEY idx_is_primary (is_primary),
    KEY idx_status (status),
    KEY idx_effective_time (effective_start, effective_end)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户角色关联表';

-- ----------------------------
-- 9. 数据权限规则表
-- ----------------------------
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='数据权限规则表';

-- ----------------------------
-- 10. 用户机构扩展表
-- ----------------------------
CREATE TABLE sys_user_org (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增ID，仅做序号',
    user_num VARCHAR(20) NOT NULL COMMENT '用户编号',
    org_code VARCHAR(10) NOT NULL COMMENT '机构编码',
    is_primary TINYINT DEFAULT 0 COMMENT '是否主机构：0-否，1-是',
    position VARCHAR(100) COMMENT '用户在机构中的职位',
    effective_start DATETIME COMMENT '生效开始时间',
    effective_end DATETIME COMMENT '生效结束时间',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    description VARCHAR(500) COMMENT '描述',
    created_by VARCHAR(20) NOT NULL COMMENT '创建人用户编号',
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by VARCHAR(20) COMMENT '更新人用户编号',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除：0-否，1-是',
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_org (user_num, org_code),
    KEY idx_user_num (user_num),
    KEY idx_org_code (org_code),
    KEY idx_is_primary (is_primary),
    KEY idx_status (status),
    KEY idx_effective_time (effective_start, effective_end)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户机构扩展表（支持用户多机构）';

-- ----------------------------
-- 11. 用户部门扩展表
-- ----------------------------
CREATE TABLE sys_user_dept (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增ID，仅做序号',
    user_num VARCHAR(20) NOT NULL COMMENT '用户编号',
    dept_code VARCHAR(15) NOT NULL COMMENT '部门编码',
    is_primary TINYINT DEFAULT 0 COMMENT '是否主部门：0-否，1-是',
    position VARCHAR(100) COMMENT '用户在部门中的职位',
    effective_start DATETIME COMMENT '生效开始时间',
    effective_end DATETIME COMMENT '生效结束时间',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    description VARCHAR(500) COMMENT '描述',
    created_by VARCHAR(20) NOT NULL COMMENT '创建人用户编号',
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by VARCHAR(20) COMMENT '更新人用户编号',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除：0-否，1-是',
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_dept (user_num, dept_code),
    KEY idx_user_num (user_num),
    KEY idx_dept_code (dept_code),
    KEY idx_is_primary (is_primary),
    KEY idx_status (status),
    KEY idx_effective_time (effective_start, effective_end)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户部门扩展表（支持用户多部门）';

-- ----------------------------
-- 12. 字段权限表
-- ----------------------------
CREATE TABLE sys_field_permission (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增ID，仅做序号',
    field_code VARCHAR(15) NOT NULL COMMENT '字段编码，15位',
    entity_type VARCHAR(50) NOT NULL COMMENT '业务实体类型',
    field_name VARCHAR(100) NOT NULL COMMENT '字段名称（数据库字段名）',
    field_alias VARCHAR(100) NOT NULL COMMENT '字段别名（显示名称）',
    role_code VARCHAR(5) NOT NULL COMMENT '角色编码',
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='字段权限表';

-- ----------------------------
-- 13. 角色机构范围表
-- ----------------------------
CREATE TABLE sys_role_org (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增ID，仅做序号',
    role_code VARCHAR(5) NOT NULL COMMENT '角色编码',
    org_code VARCHAR(10) NOT NULL COMMENT '机构编码',
    org_range_type TINYINT DEFAULT 1 COMMENT '机构范围类型：1-本机构，2-包含下级机构',
    perm_type TINYINT DEFAULT 1 COMMENT '权限类型：1-管理，2-查看，3-操作',
    effective_start DATETIME COMMENT '生效开始时间',
    effective_end DATETIME COMMENT '生效结束时间',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    description VARCHAR(500) COMMENT '描述',
    created_by VARCHAR(20) NOT NULL COMMENT '创建人用户编号',
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by VARCHAR(20) COMMENT '更新人用户编号',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除：0-否，1-是',
    PRIMARY KEY (id),
    UNIQUE KEY uk_role_org (role_code, org_code, perm_type),
    KEY idx_role_code (role_code),
    KEY idx_org_code (org_code),
    KEY idx_org_range_type (org_range_type),
    KEY idx_status (status),
    KEY idx_effective_time (effective_start, effective_end)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色机构范围表';

-- ----------------------------
-- 14. 时间权限表
-- ----------------------------
CREATE TABLE sys_time_permission (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增ID，仅做序号',
    time_code VARCHAR(10) NOT NULL COMMENT '时间权限编码，10位',
    perm_code VARCHAR(5) COMMENT '关联的权限编码',
    user_num VARCHAR(20) COMMENT '用户编号（为空表示应用于所有用户）',
    role_code VARCHAR(5) COMMENT '角色编号（为空表示应用于所有角色）',
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='时间权限表';

-- ----------------------------
-- 15. 字典类型表
-- ----------------------------
CREATE TABLE sys_dict_type (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    dict_type_code VARCHAR(50) NOT NULL COMMENT '字典类型编码',
    dict_type_name VARCHAR(100) NOT NULL COMMENT '字典类型名称',
    description VARCHAR(255) COMMENT '描述',
    sort_order INT DEFAULT 100 COMMENT '排序',
    status TINYINT DEFAULT 1 COMMENT '状态：1-启用 0-禁用',
    created_by VARCHAR(20) NOT NULL COMMENT '创建人用户编号',
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by VARCHAR(20) COMMENT '更新人用户编号',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除：0-否，1-是',
    PRIMARY KEY (id),
    UNIQUE KEY uk_dict_type_code (dict_type_code),
    KEY idx_status (status),
    KEY idx_is_deleted (is_deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='字典类型表';

-- ----------------------------
-- 16. 字典项表
-- ----------------------------
CREATE TABLE sys_dict_item (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    dict_type_code VARCHAR(50) NOT NULL COMMENT '字典类型编码',
    dict_value VARCHAR(50) NOT NULL COMMENT '字典值',
    dict_label VARCHAR(100) NOT NULL COMMENT '字典标签',
    group_code VARCHAR(50) COMMENT '分组编码',
    group_name VARCHAR(100) COMMENT '分组名称',
    sort_order INT DEFAULT 100 COMMENT '排序',
    status TINYINT DEFAULT 1 COMMENT '状态：1-启用 0-禁用',
    created_by VARCHAR(20) NOT NULL COMMENT '创建人用户编号',
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by VARCHAR(20) COMMENT '更新人用户编号',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除：0-否，1-是',
    PRIMARY KEY (id),
    KEY idx_dict_type_code (dict_type_code),
    KEY idx_group_code (group_code),
    KEY idx_dict_value (dict_value),
    KEY idx_status (status),
    KEY idx_is_deleted (is_deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='字典项表';

-- ----------------------------
-- 17. 变更记录表
-- ----------------------------
CREATE TABLE biz_change_record (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '序号',
    record_code VARCHAR(20) NOT NULL COMMENT '变更记录编码，CHG+年月日+4位序列',
    current_status VARCHAR(50) DEFAULT 'PENDING_APPROVAL' COMMENT '当前状态（关联字典）',
    release_date DATE COMMENT '发布日期',
    defect_number VARCHAR(100) COMMENT '缺陷编号',
    group_name VARCHAR(100) COMMENT '组别',
    developer_num VARCHAR(5) COMMENT '开发负责人用户编号',
    developer_name VARCHAR(50) COMMENT '开发负责人姓名',
    branch_name VARCHAR(100) COMMENT '分支名称',
    service_name VARCHAR(100) COMMENT '服务名称',
    problem_description TEXT COMMENT '问题描述',
    impact_analysis TEXT COMMENT '问题影响分析',
    solution TEXT COMMENT '解决方案',
    involve_external_system TINYINT DEFAULT 0 COMMENT '是否涉及外围系统：0-否，1-是',
    cross_service TINYINT DEFAULT 0 COMMENT '是否跨服务：0-否，1-是',
    code_list TEXT COMMENT '代码清单',
    remark TEXT COMMENT '备注说明',
    version VARCHAR(50) COMMENT '版本号',
    change_desc TEXT COMMENT '变更描述',
    develop_type VARCHAR(32) COMMENT '开发类别（关联字典）',
    org_code VARCHAR(10) COMMENT '所属机构编码',
    dept_code VARCHAR(15) COMMENT '所属部门编码',
    approver_num VARCHAR(5) COMMENT '审批人用户编号',
    approver_name VARCHAR(50) COMMENT '审批人姓名',
    approval_time DATETIME COMMENT '审批时间',
    approval_remark TEXT COMMENT '审批备注',
    created_by VARCHAR(20) NOT NULL COMMENT '创建人用户编号',
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by VARCHAR(20) COMMENT '更新人用户编号',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除：0-否，1-是',
    PRIMARY KEY (id),
    UNIQUE KEY uk_record_code (record_code),
    KEY idx_release_date (release_date),
    KEY idx_defect_number (defect_number),
    KEY idx_service_name (service_name),
    KEY idx_developer_num (developer_num),
    KEY idx_group_name (group_name),
    KEY idx_current_status (current_status),
    KEY idx_org_code (org_code),
    KEY idx_dept_code (dept_code),
    KEY idx_approver_num (approver_num)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='变更记录表';

-- ----------------------------
-- 18. 变更历史表
-- ----------------------------
CREATE TABLE biz_change_history (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '历史记录ID',
    record_id BIGINT UNSIGNED NOT NULL COMMENT '关联的主记录ID',
    record_code VARCHAR(20) NOT NULL COMMENT '变更记录编码',
    operation_type VARCHAR(20) NOT NULL COMMENT '操作类型：CREATE/UPDATE/DELETE/APPROVE/REJECT',
    operation_user_num VARCHAR(20) NOT NULL COMMENT '操作用户编号',
    operation_user_name VARCHAR(50) NOT NULL COMMENT '操作用户姓名',
    operation_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    operation_description VARCHAR(500) COMMENT '操作描述',
    current_status VARCHAR(50) COMMENT '操作时的当前状态',
    release_date DATE COMMENT '发布日期',
    defect_number VARCHAR(100) COMMENT '缺陷编号',
    group_name VARCHAR(100) COMMENT '组别',
    developer_num VARCHAR(5) COMMENT '开发负责人用户编号',
    developer_name VARCHAR(50) COMMENT '开发负责人姓名',
    branch_name VARCHAR(100) COMMENT '分支名称',
    service_name VARCHAR(100) COMMENT '服务名称',
    problem_description TEXT COMMENT '问题描述',
    impact_analysis TEXT COMMENT '问题影响分析',
    solution TEXT COMMENT '解决方案',
    involve_external_system TINYINT COMMENT '是否涉及外围系统',
    cross_service TINYINT COMMENT '是否跨服务',
    code_list TEXT COMMENT '代码清单',
    remark TEXT COMMENT '备注说明',
    version VARCHAR(50) COMMENT '版本号',
    change_desc TEXT COMMENT '变更描述',
    develop_type VARCHAR(32) COMMENT '开发类别',
    org_code VARCHAR(10) COMMENT '所属机构编码',
    dept_code VARCHAR(15) COMMENT '所属部门编码',
    approver_num VARCHAR(5) COMMENT '审批人用户编号',
    approver_name VARCHAR(50) COMMENT '审批人姓名',
    approval_time DATETIME COMMENT '审批时间',
    approval_remark TEXT COMMENT '审批备注',
    PRIMARY KEY (id),
    KEY idx_record_id (record_id),
    KEY idx_record_code (record_code),
    KEY idx_operation_time (operation_time),
    KEY idx_operation_user_num (operation_user_num),
    KEY idx_operation_type (operation_type),
    KEY idx_current_status (current_status),
    CONSTRAINT fk_history_record FOREIGN KEY (record_id) REFERENCES biz_change_record (id) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='变更历史表';

-- ----------------------------
-- 19. 系统操作日志表
-- ----------------------------
CREATE TABLE sys_operation_log (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '日志ID',
    operator_num VARCHAR(5) NOT NULL COMMENT '操作人用户编号',
    operator_name VARCHAR(50) NOT NULL COMMENT '操作人姓名',
    operation_type VARCHAR(20) NOT NULL COMMENT '操作类型',
    object_type VARCHAR(50) NOT NULL COMMENT '对象类型',
    module VARCHAR(100) COMMENT '模块名称',
    object_id BIGINT UNSIGNED NOT NULL COMMENT '对象ID',
    object_code VARCHAR(50) COMMENT '对象编码',
    result VARCHAR(20) NOT NULL COMMENT '结果：SUCCESS/FAIL',
    message VARCHAR(500) COMMENT '结果说明',
    operation_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    page_path VARCHAR(200) COMMENT '页面路径',
    button_name VARCHAR(100) COMMENT '按钮名称',
    ip_address VARCHAR(50) COMMENT '操作IP地址',
    user_agent VARCHAR(500) COMMENT '用户代理',
    request_params TEXT COMMENT '请求参数',
    response_data TEXT COMMENT '响应数据',
    duration_ms INT COMMENT '操作耗时（毫秒）',
    PRIMARY KEY (id),
    KEY idx_object (object_type, object_id),
    KEY idx_operator_time (operator_num, operation_time),
    KEY idx_object_code (object_code),
    KEY idx_operation_type (operation_type),
    KEY idx_result (result)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统操作日志表';

-- ============================================
-- 19张表创建完成
-- ============================================


-- ============================================
-- 初始化数据
-- ============================================

-- 1. 初始化特殊机构（超级机构）
TRUNCATE TABLE sys_org;
INSERT INTO sys_org (org_code, org_name, parent_org_code, level, sort_order, status, description, created_by, created_time, updated_by, updated_time, is_deleted) VALUES
('99999', '超级机构', NULL, 1, 1, 1, '系统超级管理员所属机构', 'SYSTEM', NOW(), NULL, NULL, 0),
('01000', '总部', NULL, 1, 2, 1, '公司总部', 'administrator', NOW(), NULL, NULL, 0),
('01001', '技术中心', '01000', 2, 1, 1, '技术研发中心', 'administrator', NOW(), NULL, NULL, 0),
('01002', '产品中心', '01000', 2, 2, 1, '产品管理部门', 'administrator', NOW(), NULL, NULL, 0),
('02000', '北京分公司', NULL, 1, 3, 1, '北京分公司', 'administrator', NOW(), NULL, NULL, 0);

-- 2. 初始化部门
TRUNCATE TABLE sys_dept;
INSERT INTO sys_dept (dept_code, dept_name, org_code, manager_num, sort_order, status, description, created_by, created_time, updated_by, updated_time, is_deleted) VALUES
('99999D001', '研发中心', '99999', 'administrator', 1, 1, '超级机构研发中心', 'administrator', NOW(), NULL, NULL, 0),
('01000D001', '总部研发部', '01000', '90001', 1, 1, '总部研发部门', 'administrator', NOW(), NULL, NULL, 0),
('01000D002', '总部产品部', '01000', '90003', 2, 1, '总部产品部门', 'administrator', NOW(), NULL, NULL, 0),
('01001D001', '技术研发部', '01001', '90004', 1, 1, '技术研发部门', 'administrator', NOW(), NULL, NULL, 0),
('02000D001', '北京研发部', '02000', '90002', 1, 1, '北京研发部门', 'administrator', NOW(), NULL, NULL, 0);

-- 3. 初始化特殊用户 超级管理员（administrator）
TRUNCATE TABLE sys_user;
INSERT INTO sys_user (user_num, user_name, nickname, gender, password, salt, org_code, dept_code, email, phone, avatar, 
                      last_login_time, last_login_ip, login_count, status, is_locked, lock_time, lock_reason, 
                      is_special, pwd_expire_time, pwd_modified_time, created_by, created_time, updated_by, updated_time, is_deleted) VALUES
('administrator', '超级管理员', '系统拥有者', 1, 
 '$2a$10$rlzysPIW259zbtSjoGj.zeEJG8yzRW5.21Ti.amypjcAKAPOvIjze', 
 'abcdefghijklmnopqrstuvwx', '99999', '99999D001', 'administrator@system.com', '13888888888', 
 NULL, NULL, NULL, 0, 1, 0, NULL, NULL, 1, '2099-12-31 23:59:59', NOW(), 'SYSTEM', NOW(), NULL, NULL, 0);

-- 4. 初始化其他示例用户
INSERT INTO sys_user (user_num, user_name, nickname, gender, password, salt, org_code, dept_code, email, phone, avatar, 
                      last_login_time, last_login_ip, login_count, status, is_locked, lock_time, lock_reason, 
                      is_special, pwd_expire_time, pwd_modified_time, created_by, created_time, updated_by, updated_time, is_deleted) VALUES
('90001', '张三', '张三', 1, 
 '$2a$10$abcdefghijklmnopqrstuvwxyz0123456789abcdefghijklmnopq', 
 'abcdefghijklmnopqrstuvwx', '01000', '01000D001', 'zhangsan@example.com', '13800138001', 
 NULL, NULL, NULL, 0, 1, 0, NULL, NULL, 0, '2026-12-31 23:59:59', NOW(), 'administrator', NOW(), NULL, NULL, 0),
('90002', '李四', '李四', 2, 
 '$2a$10$abcdefghijklmnopqrstuvwxyz0123456789abcdefghijklmnopq', 
 'abcdefghijklmnopqrstuvwx', '02000', '02000D001', 'lisi@example.com', '13800138002', 
 NULL, NULL, NULL, 0, 1, 0, NULL, NULL, 0, '2026-12-31 23:59:59', NOW(), 'administrator', NOW(), NULL, NULL, 0),
('90003', '王五', '王五', 1, 
 '$2a$10$abcdefghijklmnopqrstuvwxyz0123456789abcdefghijklmnopq', 
 'abcdefghijklmnopqrstuvwx', '01000', '01000D002', 'wangwu@example.com', '13800138003', 
 NULL, NULL, NULL, 0, 1, 0, NULL, NULL, 0, '2026-12-31 23:59:59', NOW(), 'administrator', NOW(), NULL, NULL, 0),
('90004', '赵六', '赵六', 2, 
 '$2a$10$abcdefghijklmnopqrstuvwxyz0123456789abcdefghijklmnopq', 
 'abcdefghijklmnopqrstuvwx', '01001', '01001D001', 'zhaoliu@example.com', '13800138004', 
 NULL, NULL, NULL, 0, 1, 0, NULL, NULL, 0, '2026-12-31 23:59:59', NOW(), 'administrator', NOW(), NULL, NULL, 0);

-- 5. 初始化用户机构扩展表
TRUNCATE TABLE sys_user_org;
INSERT INTO sys_user_org (user_num, org_code, is_primary, position, effective_start, effective_end, status, description, created_by, created_time, updated_by, updated_time, is_deleted) VALUES
('administrator', '99999', 1, '系统拥有者', '2000-01-01', '2099-12-31', 1, '超级管理员主机构', 'SYSTEM', NOW(), NULL, NULL, 0),
('90001', '01000', 1, '研发工程师', '2024-01-01', '2026-12-31', 1, '总部主机构', 'administrator', NOW(), NULL, NULL, 0),
('90002', '02000', 1, '研发工程师', '2024-01-01', '2026-12-31', 1, '北京分公司主机构', 'administrator', NOW(), NULL, NULL, 0),
('90003', '01000', 1, '产品经理', '2024-01-01', '2026-12-31', 1, '总部产品部主机构', 'administrator', NOW(), NULL, NULL, 0),
('90004', '01001', 1, '架构师', '2024-01-01', '2026-12-31', 1, '技术中心主机构', 'administrator', NOW(), NULL, NULL, 0);

-- 6. 初始化用户部门扩展表
TRUNCATE TABLE sys_user_dept;
INSERT INTO sys_user_dept (user_num, dept_code, is_primary, position, effective_start, effective_end, status, description, created_by, created_time, updated_by, updated_time, is_deleted) VALUES
('administrator', '99999D001', 1, '系统拥有者', '2000-01-01', '2099-12-31', 1, '超级管理员主部门', 'SYSTEM', NOW(), NULL, NULL, 0),
('90001', '01000D001', 1, '研发工程师', '2024-01-01', '2026-12-31', 1, '总部研发部主部门', 'administrator', NOW(), NULL, NULL, 0),
('90002', '02000D001', 1, '研发工程师', '2024-01-01', '2026-12-31', 1, '北京研发部主部门', 'administrator', NOW(), NULL, NULL, 0),
('90003', '01000D002', 1, '产品经理', '2024-01-01', '2026-12-31', 1, '总部产品部主部门', 'administrator', NOW(), NULL, NULL, 0),
('90004', '01001D001', 1, '架构师', '2024-01-01', '2026-12-31', 1, '技术研发部主部门', 'administrator', NOW(), NULL, NULL, 0);

-- 7. 初始化角色
TRUNCATE TABLE sys_role;
INSERT INTO sys_role (role_code, role_name, role_type, data_scope_type, sort_order, status, description, created_by, created_time, updated_by, updated_time, is_deleted) VALUES
('R0001', '超级管理员', 1, 1, 1, 1, '系统超级管理员，拥有所有权限', 'administrator', NOW(), NULL, NULL, 0),
('R0002', '架构师', 2, 2, 2, 1, '项目架构师，负责技术架构和项目管理', 'administrator', NOW(), NULL, NULL, 0),
('R0003', '开发工程师', 2, 4, 3, 1, '开发工程师，负责具体开发工作', 'administrator', NOW(), NULL, NULL, 0),
('R0004', '产品经理', 2, 3, 4, 1, '产品经理，负责产品管理', 'administrator', NOW(), NULL, NULL, 0),
('R0005', '测试工程师', 2, 4, 5, 1, '测试工程师，负责测试工作', 'administrator', NOW(), NULL, NULL, 0);

-- 8. 初始化用户角色关联
TRUNCATE TABLE sys_user_role;
INSERT INTO sys_user_role (user_num, role_code, is_primary, effective_start, effective_end, status, description, created_by, created_time, updated_by, updated_time, is_deleted) VALUES
('administrator', 'R0001', 1, '2000-01-01', '2099-12-31', 1, '超级管理员作为超级管理员', 'SYSTEM', NOW(), NULL, NULL, 0),
('90001', 'R0003', 1, '2024-01-01', '2026-12-31', 1, '张三作为开发工程师', 'administrator', NOW(), NULL, NULL, 0),
('90002', 'R0003', 1, '2024-01-01', '2026-12-31', 1, '李四作为开发工程师', 'administrator', NOW(), NULL, NULL, 0),
('90003', 'R0004', 1, '2024-01-01', '2026-12-31', 1, '王五作为产品经理', 'administrator', NOW(), NULL, NULL, 0),
('90004', 'R0002', 1, '2024-01-01', '2026-12-31', 1, '赵六作为架构师', 'administrator', NOW(), NULL, NULL, 0);

-- 9. 初始化菜单（简化版本，只包含核心功能）
TRUNCATE TABLE sys_menu;
INSERT INTO sys_menu (menu_code, menu_name, menu_type, parent_menu_code, icon, path, component, url, http_method, is_external, is_cache, is_visible, permission_key, sort_order, status, description, created_by, created_time, updated_by, updated_time, is_deleted) VALUES
-- 首页
('M0000001', '首页', 1, NULL, 'el-icon-s-home', '/', NULL, NULL, NULL, 0, 1, 1, NULL, 1, 1, '系统首页目录', 'administrator', NOW(), NULL, NULL, 0),
('P0000001', '首页概览', 2, 'M0000001', NULL, '/', 'views/Home', NULL, NULL, 0, 1, 1, 'home:overview', 1, 1, '首页概览页面', 'administrator', NOW(), NULL, NULL, 0),

-- 变更管理
('M0000002', '变更管理', 1, NULL, 'el-icon-edit-outline', '/change-records', NULL, NULL, NULL, 0, 1, 1, NULL, 2, 1, '变更管理目录', 'administrator', NOW(), NULL, NULL, 0),
('P0000002', '变更记录管理', 2, 'M0000002', NULL, '/change-records', 'views/ChangeRecordList', NULL, NULL, 0, 1, 1, 'change:record:list', 1, 1, '变更记录列表页面', 'administrator', NOW(), NULL, NULL, 0),
('B0000001', '新增变更', 3, 'P0000002', NULL, NULL, NULL, '/api/change-records', 'POST', 0, 1, 1, 'change:record:create', 1, 1, '新增变更记录按钮', 'administrator', NOW(), NULL, NULL, 0),
('B0000002', '编辑变更', 3, 'P0000002', NULL, NULL, NULL, '/api/change-records/*', 'PUT', 0, 1, 1, 'change:record:edit', 2, 1, '编辑变更记录按钮', 'administrator', NOW(), NULL, NULL, 0),
('B0000003', '删除变更', 3, 'P0000002', NULL, NULL, NULL, '/api/change-records/*', 'DELETE', 0, 1, 1, 'change:record:delete', 3, 1, '删除变更记录按钮', 'administrator', NOW(), NULL, NULL, 0),
('B0000004', '导出变更', 3, 'P0000002', NULL, NULL, NULL, '/api/change-records/export', 'GET', 0, 1, 1, 'change:record:export', 4, 1, '导出变更记录按钮', 'administrator', NOW(), NULL, NULL, 0),

-- 系统设置
('M0000003', '系统设置', 1, NULL, 'el-icon-setting', '/system-settings', NULL, NULL, NULL, 0, 1, 1, NULL, 5, 1, '系统设置目录', 'administrator', NOW(), NULL, NULL, 0),
('P0000003', '用户管理', 2, 'M0000003', NULL, '/system-settings/users', 'views/UserManagement', NULL, NULL, 0, 1, 1, 'user:management', 1, 1, '用户管理页面', 'administrator', NOW(), NULL, NULL, 0),
('P0000004', '角色管理', 2, 'M0000003', NULL, '/system-settings/roles', 'views/RoleManagement', NULL, NULL, 0, 1, 1, 'role:management', 2, 1, '角色管理页面', 'administrator', NOW(), NULL, NULL, 0),
('P0000005', '菜单管理', 2, 'M0000003', NULL, '/system-settings/menus', 'views/MenuManagement', NULL, NULL, 0, 1, 1, 'menu:management', 3, 1, '菜单管理页面', 'administrator', NOW(), NULL, NULL, 0);
```

## 构建与运行
- 构建：`mvn -DskipTests clean package`
- 运行：`java -jar target/aiguibin-platform-arch.jar`
- 访问：`http://localhost:8080`

## 认证与安全
- 登录：`POST /api/login`，入参：`{ username, password }`
- 返回：`token`（Bearer）、`csrfToken`（CSRF防护）、`username`、`chineseName: ""`
- 所有变更类接口（POST/PUT/PATCH/DELETE）必须在请求头携带：
  - `Authorization: Bearer <token>`
  - `X-CSRF-Token: <csrfToken>`
- 存储安全：密码采用 BCrypt 加密；传输安全：生产环境启用 HTTPS。

## 用户功能
- 用户管理接口（需 `Authorization` + `X-CSRF-Token`）：
  - `GET /api/users?page=1&size=10&usernumb=&username=` 列表
  - `GET /api/users/{id}` 详情
  - `POST /api/users` 新增（初始密码强制为 `666666`）
  - `PUT /api/users/{id}` 更新（支持改口令）
  - `DELETE /api/users/{id}` 删除
- 修改密码：
  - `POST /api/users/change-password`
  - 入参：`{ currentPassword, newPassword, confirmPassword }`
  - 校验：当前密码验证；新密码强度（至少6位且包含字母和数字）；一致性校验；CSRF校验
  - 成功返回：`"密码修改成功"`；失败返回相应错误信息


   权限分类体系
用户权限通常分为三大类：功能权限、数据权限和字段权限。
权限体系
├── 功能权限 (能做什么)
├── 数据权限 (能看到什么数据)
└── 字段权限 (能看到什么字段信息)
 功能权限
2.1 菜单权限
控制用户能够访问的系统菜单和页面。
权限粒度：
● 目录权限：能否看到一级菜单目录
● 页面权限：能否访问具体功能页面
● 子菜单权限：能否看到页面内的子菜单
示例：
├── 系统管理 (目录)
│   ├── 用户管理 (页面)
│   ├── 角色管理 (页面)
│   └── 菜单管理 (页面)
├── 业务管理 (目录)
│   ├── 订单管理 (页面)
│   └── 客户管理 (页面)
└── 报表管理 (目录)
    ├── 销售报表 (页面)
    └── 统计报表 (页面)
2.2 操作权限
控制用户在页面内的具体操作能力。
权限类型：
● 按钮权限：页面内按钮的显示和操作权限
● 链接权限：页面内链接的访问权限
● 操作权限：特定的功能操作
常见操作权限示例：
-- 用户管理模块操作权限
user:create     -- 创建用户
user:update     -- 修改用户
user:delete     -- 删除用户
user:view       -- 查看用户
user:export     -- 导出用户
user:reset-pwd  -- 重置密码
user:assign-role -- 分配角色

-- 订单管理模块操作权限
order:create    -- 创建订单
order:edit      -- 编辑订单
order:cancel    -- 取消订单
order:approve   -- 审核订单
order:reject    -- 驳回订单
order:export    -- 导出订单
2.3 接口权限
控制后端API接口的访问权限。
权限类型：
● HTTP方法权限：GET、POST、PUT、DELETE等
● API路径权限：具体的接口路径访问权限
● 接口参数权限：接口参数的访问限制
示例：
// 接口权限配置
@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @GetMapping
    @PreAuthorize("hasPermission('user:list')")
    public List<User> getUsers() { ... }
    
    @PostMapping
    @PreAuthorize("hasPermission('user:create')")
    public User createUser(@RequestBody User user) { ... }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasPermission('user:update')")
    public User updateUser(@PathVariable Long id, @RequestBody User user) { ... }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasPermission('user:delete')")
    public void deleteUser(@PathVariable Long id) { ... }
    
    @GetMapping("/change-password")
    @PreAuthorize("hasPermission('user:change-password')")
    public void changePassword(@RequestBody PasswordChangeRequest request) { ... }
}
3. 数据权限
3.1 数据范围权限
控制用户能够访问的数据范围。
权限级别：
● 全部数据权限：可以访问所有数据
● 本机构数据权限：只能访问自己所在机构及下属机构的数据
● 本部门数据权限：只能访问自己所在部门及下属部门的数据
● 本人数据权限：只能访问自己创建的数据
● 自定义数据权限：按照自定义规则访问数据
数据权限实现示例：
-- 数据权限规则表
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='数据权限规则表';
3.2 数据操作权限
控制用户对数据的操作能力。
权限类型：
● 查询权限：能否查询数据
● 新增权限：能否新增数据
● 修改权限：能否修改数据
● 删除权限：能否删除数据
● 导入权限：能否导入数据
● 导出权限：能否导出数据
3.3 数据字段权限
控制用户能够看到和操作的数据字段。
权限类型：
● 字段可见性：某些字段对特定用户隐藏
● 字段可编辑性：某些字段对特定用户只读
● 字段必填控制：某些字段对特定用户必填
字段权限示例：
// 字段权限控制 - 使用 sys_user 表字段
public class User {
    // 公开可见字段
    private String userNum;       // 用户编号
    private String userName;      // 用户姓名
    private String nickname;      // 用户昵称
    private String deptCode;      // 主部门编码
    private String email;         // 邮箱
    private String phone;         // 手机号
    
    // 仅管理员可见字段
    @JsonView(Views.AdminOnly.class)
    private String password;      // 密码（加密存储）
    
    @JsonView(Views.AdminOnly.class)
    private Integer loginCount;   // 登录次数
    
    @JsonView(Views.AdminOnly.class)
    private LocalDateTime lastLoginTime; // 最后登录时间
    
    @JsonView(Views.AdminOnly.class)
    private String lastLoginIp;   // 最后登录IP
    
    // 仅HR和管理员可见字段
    @JsonView(Views.HRAndAdmin.class)
    private LocalDateTime pwdExpireTime; // 密码过期时间
    
    @JsonView(Views.HRAndAdmin.class)
    private LocalDateTime pwdModifiedTime; // 密码最后修改时间
}
4. 组织架构权限
4.1 机构权限
基于组织机构的权限控制。
权限维度：
● 机构范围：能够管理的机构范围
● 机构级别：能够管理的机构层级
● 机构类型：能够管理的机构类型
4.2 部门权限
基于部门的权限控制。
权限类型：
● 本部门权限：只能管理本部门
● 跨部门权限：可以管理多个部门
● 下级部门权限：可以管理下级部门
5. 时间维度权限
5.1 时间范围权限
控制用户权限的有效时间范围。
权限类型：
● 永久权限：长期有效
● 临时权限：指定时间段内有效
● 定时权限：特定时间点生效
5.2 访问时段权限
控制用户在什么时间段可以访问系统。
示例：
-- 时间权限表
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='时间权限表';
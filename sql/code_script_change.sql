-- ============================================
-- 权限管理及业务系统建表语句（共19张表）
-- 使用统一的字符集和排序规则
-- ============================================

-- ----------------------------
-- 1. 机构信息表
-- ----------------------------
CREATE TABLE sys_org
(
    id              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增ID，仅做序号',
    org_code        VARCHAR(10)  NOT NULL COMMENT '机构编码，10位定长层级编码',
    org_name        VARCHAR(100) NOT NULL COMMENT '机构名称',
    parent_org_code VARCHAR(10) COMMENT '上级机构编码',
    level           TINYINT      NOT NULL COMMENT '机构层级：1-5级',
    sort_order      INT                   DEFAULT 100 COMMENT '排序号',
    status          TINYINT               DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    description     VARCHAR(500) COMMENT '机构描述',
    created_by      VARCHAR(20)  NOT NULL COMMENT '创建人用户编号',
    created_time    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by      VARCHAR(20) COMMENT '更新人用户编号',
    updated_time    DATETIME              DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted      TINYINT               DEFAULT 0 COMMENT '是否删除：0-否，1-是',
    PRIMARY KEY (id),
    UNIQUE KEY uk_org_code (org_code),
    KEY             idx_parent_org_code (parent_org_code),
    KEY             idx_level (level),
    KEY             idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='机构信息表';

-- ----------------------------
-- 2. 部门信息表
-- ----------------------------
CREATE TABLE sys_dept
(
    id           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增ID，仅做序号',
    dept_code    VARCHAR(14)  NOT NULL COMMENT '部门编码，14位：机构编码(10位)+D+3位序列',
    dept_name    VARCHAR(100) NOT NULL COMMENT '部门名称',
    org_code     VARCHAR(10)  NOT NULL COMMENT '所属机构编码',
    manager_num  VARCHAR(5) COMMENT '部门负责人用户编号',
    sort_order   INT                   DEFAULT 100 COMMENT '排序号',
    status       TINYINT               DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    description  VARCHAR(500) COMMENT '部门描述',
    created_by   VARCHAR(20)  NOT NULL COMMENT '创建人用户编号',
    created_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by   VARCHAR(20) COMMENT '更新人用户编号',
    updated_time DATETIME              DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted   TINYINT               DEFAULT 0 COMMENT '是否删除：0-否，1-是',
    PRIMARY KEY (id),
    UNIQUE KEY uk_dept_code (dept_code),
    KEY          idx_org_code (org_code),
    KEY          idx_manager_num (manager_num),
    KEY          idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='部门信息表';

-- ----------------------------
-- 3. 用户信息表
-- ----------------------------
CREATE TABLE sys_user
(
    id                BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增ID，仅做序号',
    user_num          VARCHAR(5)   NOT NULL COMMENT '用户编号，5位，9开头',
    user_name         VARCHAR(50)  NOT NULL COMMENT '用户姓名',
    nickname          VARCHAR(50) COMMENT '用户昵称',
    gender            TINYINT               DEFAULT 0 COMMENT '性别：0-未知，1-男，2-女',
    password          VARCHAR(128) NOT NULL COMMENT '密码（加密存储）',
    salt              VARCHAR(32) COMMENT '密码盐值',
    org_code          VARCHAR(10) COMMENT '主机构编码',
    dept_code         VARCHAR(14) COMMENT '主部门编码',
    email             VARCHAR(100) COMMENT '邮箱',
    phone             VARCHAR(20) COMMENT '手机号',
    avatar            VARCHAR(500) COMMENT '头像URL',
    last_login_time   DATETIME COMMENT '最后登录时间',
    last_login_ip     VARCHAR(50) COMMENT '最后登录IP',
    login_count       INT                   DEFAULT 0 COMMENT '登录次数',
    status            TINYINT               DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    is_locked         TINYINT               DEFAULT 0 COMMENT '是否锁定：0-否，1-是',
    lock_time         DATETIME COMMENT '锁定时间',
    lock_reason       VARCHAR(200) COMMENT '锁定原因',
    is_special        TINYINT               DEFAULT 0 COMMENT '是否特殊用户：0-否，1-是（如AIguibin）',
    pwd_expire_time   DATETIME COMMENT '密码过期时间',
    pwd_modified_time DATETIME COMMENT '密码最后修改时间',
    created_by        VARCHAR(20)  NOT NULL COMMENT '创建人用户编号',
    created_time      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by        VARCHAR(20) COMMENT '更新人用户编号',
    updated_time      DATETIME              DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted        TINYINT               DEFAULT 0 COMMENT '是否删除：0-否，1-是',
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_num (user_num),
    UNIQUE KEY uk_user_name (user_name),
    KEY               idx_org_code (org_code),
    KEY               idx_dept_code (dept_code),
    KEY               idx_status (status),
    KEY               idx_is_locked (is_locked),
    KEY               idx_phone (phone),
    KEY               idx_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户信息表';

-- ----------------------------
-- 4. 角色信息表
-- ----------------------------
CREATE TABLE sys_role
(
    id              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增ID，仅做序号',
    role_code       VARCHAR(4)  NOT NULL COMMENT '角色编码，4位：R+3位数字',
    role_name       VARCHAR(50) NOT NULL COMMENT '角色名称',
    role_type       TINYINT              DEFAULT 1 COMMENT '角色类型：1-系统角色，2-业务角色，3-自定义角色',
    data_scope_type TINYINT              DEFAULT 4 COMMENT '默认数据范围：1-全部，2-本机构，3-本部门，4-本人，5-自定义',
    sort_order      INT                  DEFAULT 100 COMMENT '排序号',
    status          TINYINT              DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    description     VARCHAR(500) COMMENT '角色描述',
    created_by      VARCHAR(20) NOT NULL COMMENT '创建人用户编号',
    created_time    DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by      VARCHAR(20) COMMENT '更新人用户编号',
    updated_time    DATETIME             DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted      TINYINT              DEFAULT 0 COMMENT '是否删除：0-否，1-是',
    PRIMARY KEY (id),
    UNIQUE KEY uk_role_code (role_code),
    UNIQUE KEY uk_role_name (role_name),
    KEY             idx_role_type (role_type),
    KEY             idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色信息表';

-- ----------------------------
-- 5. 菜单/页面/按钮表
-- ----------------------------
CREATE TABLE sys_menu
(
    id               BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增ID，仅做序号',
    menu_code        VARCHAR(20)  NOT NULL COMMENT '菜单编码，20位',
    menu_name        VARCHAR(100) NOT NULL COMMENT '菜单名称',
    menu_type        TINYINT      NOT NULL COMMENT '类型：1-目录，2-页面，3-按钮',
    parent_menu_code VARCHAR(20) COMMENT '父菜单编码',
    icon             VARCHAR(100) COMMENT '图标',
    path             VARCHAR(200) COMMENT '路由路径（前端使用）',
    component        VARCHAR(200) COMMENT '组件路径（前端使用）',
    url              VARCHAR(500) COMMENT '访问URL（后端API路径）',
    http_method      VARCHAR(10) COMMENT 'HTTP方法：GET,POST,PUT,DELETE等',
    is_external      TINYINT               DEFAULT 0 COMMENT '是否外部链接：0-否，1-是',
    is_cache         TINYINT               DEFAULT 1 COMMENT '是否缓存：0-否，1-是',
    is_visible       TINYINT               DEFAULT 1 COMMENT '是否显示：0-否，1-是',
    permission_key   VARCHAR(100) COMMENT '权限标识（如：user:view）',
    sort_order       INT                   DEFAULT 100 COMMENT '排序号',
    status           TINYINT               DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    description      VARCHAR(500) COMMENT '菜单描述',
    created_by       VARCHAR(20)  NOT NULL COMMENT '创建人用户编号',
    created_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by       VARCHAR(20) COMMENT '更新人用户编号',
    updated_time     DATETIME              DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted       TINYINT               DEFAULT 0 COMMENT '是否删除：0-否，1-是',
    PRIMARY KEY (id),
    UNIQUE KEY uk_menu_code (menu_code),
    KEY              idx_parent_menu_code (parent_menu_code),
    KEY              idx_menu_type (menu_type),
    KEY              idx_permission_key (permission_key),
    KEY              idx_path (path(100)),
    KEY              idx_url (url(100)),
    KEY              idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='菜单/页面/按钮表';

-- ----------------------------
-- 6. 统一权限表
-- ----------------------------
CREATE TABLE sys_permission
(
    id           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增ID，仅做序号',
    perm_code    VARCHAR(5)   NOT NULL COMMENT '权限编码，5位：P+4位数字',
    perm_name    VARCHAR(100) NOT NULL COMMENT '权限名称',
    perm_key     VARCHAR(100) NOT NULL COMMENT '权限标识（如：user:create）',
    perm_type    TINYINT      NOT NULL COMMENT '权限类型：1-菜单，2-操作，3-接口，4-数据，5-字段，6-时间，7-业务',
    menu_code    VARCHAR(20) COMMENT '关联的菜单编码（当perm_type=1,2时）',
    api_path     VARCHAR(500) COMMENT '接口路径（当perm_type=3时）',
    entity_type  VARCHAR(50) COMMENT '业务实体类型（如：user,order，当perm_type=4,5时）',
    entity_field VARCHAR(100) COMMENT '业务实体字段（当perm_type=5时）',
    rule_type    TINYINT COMMENT '规则类型：1-预定义，2-自定义SQL',
    scope_type   TINYINT COMMENT '预定义范围：1-全部，2-本机构，3-本部门，4-本人',
    custom_rule  TEXT COMMENT '自定义规则（JSON或SQL片段）',
    is_default   TINYINT               DEFAULT 0 COMMENT '是否默认权限：0-否，1-是',
    sort_order   INT                   DEFAULT 100 COMMENT '排序号',
    status       TINYINT               DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    description  VARCHAR(500) COMMENT '权限描述',
    created_by   VARCHAR(20)  NOT NULL COMMENT '创建人用户编号',
    created_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by   VARCHAR(20) COMMENT '更新人用户编号',
    updated_time DATETIME              DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted   TINYINT               DEFAULT 0 COMMENT '是否删除：0-否，1-是',
    PRIMARY KEY (id),
    UNIQUE KEY uk_perm_code (perm_code),
    UNIQUE KEY uk_perm_key (perm_key),
    KEY          idx_perm_type (perm_type),
    KEY          idx_menu_code (menu_code),
    KEY          idx_entity_type (entity_type),
    KEY          idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='统一权限表';

-- ----------------------------
-- 7. 角色权限关联表
-- ----------------------------
CREATE TABLE sys_role_permission
(
    id              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增ID，仅做序号',
    role_code       VARCHAR(4)  NOT NULL COMMENT '角色编码',
    perm_code       VARCHAR(5)  NOT NULL COMMENT '权限编码',
    auth_type       TINYINT              DEFAULT 1 COMMENT '授权类型：1-允许，2-拒绝',
    effective_start DATETIME COMMENT '生效开始时间',
    effective_end   DATETIME COMMENT '生效结束时间',
    priority        INT                  DEFAULT 100 COMMENT '优先级（数字越小优先级越高）',
    status          TINYINT              DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    description     VARCHAR(500) COMMENT '描述',
    created_by      VARCHAR(20) NOT NULL COMMENT '创建人用户编号',
    created_time    DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by      VARCHAR(20) COMMENT '更新人用户编号',
    updated_time    DATETIME             DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted      TINYINT              DEFAULT 0 COMMENT '是否删除：0-否，1-是',
    PRIMARY KEY (id),
    UNIQUE KEY uk_role_perm (role_code, perm_code),
    KEY             idx_role_code (role_code),
    KEY             idx_perm_code (perm_code),
    KEY             idx_auth_type (auth_type),
    KEY             idx_status (status),
    KEY             idx_effective_time (effective_start, effective_end)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色权限关联表';

-- ----------------------------
-- 8. 用户角色关联表
-- ----------------------------
CREATE TABLE sys_user_role
(
    id              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增ID，仅做序号',
    user_num        VARCHAR(5)  NOT NULL COMMENT '用户编号',
    role_code       VARCHAR(4)  NOT NULL COMMENT '角色编码',
    is_primary      TINYINT              DEFAULT 0 COMMENT '是否主角色：0-否，1-是（一个用户只有一个主角色）',
    effective_start DATETIME COMMENT '生效开始时间',
    effective_end   DATETIME COMMENT '生效结束时间',
    status          TINYINT              DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    description     VARCHAR(500) COMMENT '描述',
    created_by      VARCHAR(20) NOT NULL COMMENT '创建人用户编号',
    created_time    DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by      VARCHAR(20) COMMENT '更新人用户编号',
    updated_time    DATETIME             DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted      TINYINT              DEFAULT 0 COMMENT '是否删除：0-否，1-是',
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_role (user_num, role_code),
    KEY             idx_user_num (user_num),
    KEY             idx_role_code (role_code),
    KEY             idx_is_primary (is_primary),
    KEY             idx_status (status),
    KEY             idx_effective_time (effective_start, effective_end)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户角色关联表';

-- ----------------------------
-- 9. 数据权限规则表
-- ----------------------------
CREATE TABLE sys_data_rule
(
    id               BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增ID，仅做序号',
    rule_code        VARCHAR(10)  NOT NULL COMMENT '规则编码，10位',
    rule_name        VARCHAR(100) NOT NULL COMMENT '规则名称',
    rule_type        TINYINT      NOT NULL COMMENT '规则类型：1-预定义，2-自定义SQL，3-组合规则',
    perm_code        VARCHAR(5) COMMENT '关联的权限编码',
    entity_type      VARCHAR(50)  NOT NULL COMMENT '业务实体类型（如：sys_user, sys_order）',
    scope_type       TINYINT COMMENT '预定义范围：1-全部，2-本机构，3-本部门，4-本人',
    include_children TINYINT               DEFAULT 1 COMMENT '是否包含下级：0-否，1-是（针对机构、部门）',
    custom_sql       TEXT COMMENT '自定义SQL条件（WHERE子句内容）',
    rule_expression  JSON COMMENT '组合规则表达式（JSON格式）',
    rule_priority    TINYINT               DEFAULT 1 COMMENT '规则优先级：1-高，2-中，3-低',
    is_global        TINYINT               DEFAULT 0 COMMENT '是否全局规则：0-否，1-是',
    status           TINYINT               DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    description      VARCHAR(500) COMMENT '规则描述',
    created_by       VARCHAR(20)  NOT NULL COMMENT '创建人用户编号',
    created_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by       VARCHAR(20) COMMENT '更新人用户编号',
    updated_time     DATETIME              DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted       TINYINT               DEFAULT 0 COMMENT '是否删除：0-否，1-是',
    PRIMARY KEY (id),
    UNIQUE KEY uk_rule_code (rule_code),
    KEY              idx_perm_code (perm_code),
    KEY              idx_entity_type (entity_type),
    KEY              idx_rule_type (rule_type),
    KEY              idx_status (status),
    KEY              idx_is_global (is_global)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='数据权限规则表';

-- ----------------------------
-- 10. 用户机构扩展表
-- ----------------------------
CREATE TABLE sys_user_org
(
    id              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增ID，仅做序号',
    user_num        VARCHAR(5)  NOT NULL COMMENT '用户编号',
    org_code        VARCHAR(10) NOT NULL COMMENT '机构编码',
    is_primary      TINYINT              DEFAULT 0 COMMENT '是否主机构：0-否，1-是',
    position        VARCHAR(100) COMMENT '用户在机构中的职位',
    effective_start DATETIME COMMENT '生效开始时间',
    effective_end   DATETIME COMMENT '生效结束时间',
    status          TINYINT              DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    description     VARCHAR(500) COMMENT '描述',
    created_by      VARCHAR(20) NOT NULL COMMENT '创建人用户编号',
    created_time    DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by      VARCHAR(20) COMMENT '更新人用户编号',
    updated_time    DATETIME             DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted      TINYINT              DEFAULT 0 COMMENT '是否删除：0-否，1-是',
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_org (user_num, org_code),
    KEY             idx_user_num (user_num),
    KEY             idx_org_code (org_code),
    KEY             idx_is_primary (is_primary),
    KEY             idx_status (status),
    KEY             idx_effective_time (effective_start, effective_end)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户机构扩展表（支持用户多机构）';

-- ----------------------------
-- 11. 用户部门扩展表
-- ----------------------------
CREATE TABLE sys_user_dept
(
    id              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增ID，仅做序号',
    user_num        VARCHAR(5)  NOT NULL COMMENT '用户编号',
    dept_code       VARCHAR(14) NOT NULL COMMENT '部门编码',
    is_primary      TINYINT              DEFAULT 0 COMMENT '是否主部门：0-否，1-是',
    position        VARCHAR(100) COMMENT '用户在部门中的职位',
    effective_start DATETIME COMMENT '生效开始时间',
    effective_end   DATETIME COMMENT '生效结束时间',
    status          TINYINT              DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    description     VARCHAR(500) COMMENT '描述',
    created_by      VARCHAR(20) NOT NULL COMMENT '创建人用户编号',
    created_time    DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by      VARCHAR(20) COMMENT '更新人用户编号',
    updated_time    DATETIME             DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted      TINYINT              DEFAULT 0 COMMENT '是否删除：0-否，1-是',
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_dept (user_num, dept_code),
    KEY             idx_user_num (user_num),
    KEY             idx_dept_code (dept_code),
    KEY             idx_is_primary (is_primary),
    KEY             idx_status (status),
    KEY             idx_effective_time (effective_start, effective_end)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户部门扩展表（支持用户多部门）';

-- ----------------------------
-- 12. 字段权限表
-- ----------------------------
CREATE TABLE sys_field_permission
(
    id                   BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增ID，仅做序号',
    field_code           VARCHAR(15)  NOT NULL COMMENT '字段编码，15位',
    entity_type          VARCHAR(50)  NOT NULL COMMENT '业务实体类型',
    field_name           VARCHAR(100) NOT NULL COMMENT '字段名称（数据库字段名）',
    field_alias          VARCHAR(100) NOT NULL COMMENT '字段别名（显示名称）',
    role_code            VARCHAR(4)   NOT NULL COMMENT '角色编码',
    perm_type            TINYINT      NOT NULL COMMENT '权限类型：1-可见，2-可编辑，3-必填，4-隐藏，5-只读',
    condition_expression JSON COMMENT '条件表达式（JSON格式，满足条件时生效）',
    sort_order           INT                   DEFAULT 100 COMMENT '排序号',
    status               TINYINT               DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    description          VARCHAR(500) COMMENT '字段权限描述',
    created_by           VARCHAR(20)  NOT NULL COMMENT '创建人用户编号',
    created_time         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by           VARCHAR(20) COMMENT '更新人用户编号',
    updated_time         DATETIME              DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted           TINYINT               DEFAULT 0 COMMENT '是否删除：0-否，1-是',
    PRIMARY KEY (id),
    UNIQUE KEY uk_field_perm (entity_type, field_name, role_code, perm_type),
    KEY                  idx_field_code (field_code),
    KEY                  idx_entity_type (entity_type),
    KEY                  idx_role_code (role_code),
    KEY                  idx_perm_type (perm_type),
    KEY                  idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='字段权限表';

-- ----------------------------
-- 13. 角色机构范围表
-- ----------------------------
CREATE TABLE sys_role_org
(
    id              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增ID，仅做序号',
    role_code       VARCHAR(4)  NOT NULL COMMENT '角色编码',
    org_code        VARCHAR(10) NOT NULL COMMENT '机构编码',
    org_range_type  TINYINT              DEFAULT 1 COMMENT '机构范围类型：1-本机构，2-包含下级机构',
    perm_type       TINYINT              DEFAULT 1 COMMENT '权限类型：1-管理，2-查看，3-操作',
    effective_start DATETIME COMMENT '生效开始时间',
    effective_end   DATETIME COMMENT '生效结束时间',
    status          TINYINT              DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    description     VARCHAR(500) COMMENT '描述',
    created_by      VARCHAR(20) NOT NULL COMMENT '创建人用户编号',
    created_time    DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by      VARCHAR(20) COMMENT '更新人用户编号',
    updated_time    DATETIME             DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted      TINYINT              DEFAULT 0 COMMENT '是否删除：0-否，1-是',
    PRIMARY KEY (id),
    UNIQUE KEY uk_role_org (role_code, org_code, perm_type),
    KEY             idx_role_code (role_code),
    KEY             idx_org_code (org_code),
    KEY             idx_org_range_type (org_range_type),
    KEY             idx_status (status),
    KEY             idx_effective_time (effective_start, effective_end)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色机构范围表';

-- ----------------------------
-- 14. 时间权限表
-- ----------------------------
CREATE TABLE sys_time_permission
(
    id               BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增ID，仅做序号',
    time_code        VARCHAR(10) NOT NULL COMMENT '时间权限编码，10位',
    perm_code        VARCHAR(5) COMMENT '关联的权限编码',
    user_num         VARCHAR(5) COMMENT '用户编号（为空表示应用于所有用户）',
    role_code        VARCHAR(4) COMMENT '角色编号（为空表示应用于所有角色）',
    allowed_days     VARCHAR(20) COMMENT '允许访问的星期（如：1,2,3,4,5,6,7）',
    start_time       TIME COMMENT '每天开始时间',
    end_time         TIME COMMENT '每天结束时间',
    effective_start  DATE COMMENT '生效开始日期',
    effective_end    DATE COMMENT '生效结束日期',
    timezone         VARCHAR(50)          DEFAULT 'Asia/Shanghai' COMMENT '时区',
    is_recurring     TINYINT              DEFAULT 1 COMMENT '是否循环：0-否，1-是',
    holiday_excluded TINYINT              DEFAULT 0 COMMENT '是否排除节假日：0-否，1-是',
    status           TINYINT              DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    description      VARCHAR(500) COMMENT '时间权限描述',
    created_by       VARCHAR(20) NOT NULL COMMENT '创建人用户编号',
    created_time     DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by       VARCHAR(20) COMMENT '更新人用户编号',
    updated_time     DATETIME             DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted       TINYINT              DEFAULT 0 COMMENT '是否删除：0-否，1-是',
    PRIMARY KEY (id),
    UNIQUE KEY uk_time_code (time_code),
    KEY              idx_perm_code (perm_code),
    KEY              idx_user_num (user_num),
    KEY              idx_role_code (role_code),
    KEY              idx_effective_date (effective_start, effective_end),
    KEY              idx_allowed_days (allowed_days(10)),
    KEY              idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='时间权限表';

-- ----------------------------
-- 15. 字典类型表
-- ----------------------------
CREATE TABLE sys_dict_type
(
    id             BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    dict_type_code VARCHAR(50)  NOT NULL COMMENT '字典类型编码',
    dict_type_name VARCHAR(100) NOT NULL COMMENT '字典类型名称',
    description    VARCHAR(255) COMMENT '描述',
    sort_order     INT                   DEFAULT 100 COMMENT '排序',
    status         TINYINT               DEFAULT 1 COMMENT '状态：1-启用 0-禁用',
    created_by     VARCHAR(20)  NOT NULL COMMENT '创建人用户编号',
    created_time   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by     VARCHAR(20) COMMENT '更新人用户编号',
    updated_time   DATETIME              DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted     TINYINT               DEFAULT 0 COMMENT '是否删除：0-否，1-是',
    PRIMARY KEY (id),
    UNIQUE KEY uk_dict_type_code (dict_type_code),
    KEY            idx_status (status),
    KEY            idx_is_deleted (is_deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='字典类型表';

-- ----------------------------
-- 16. 字典项表
-- ----------------------------
CREATE TABLE sys_dict_item
(
    id             BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    dict_type_code VARCHAR(50)  NOT NULL COMMENT '字典类型编码',
    dict_value     VARCHAR(50)  NOT NULL COMMENT '字典值',
    dict_label     VARCHAR(100) NOT NULL COMMENT '字典标签',
    group_code     VARCHAR(50) COMMENT '分组编码',
    group_name     VARCHAR(100) COMMENT '分组名称',
    sort_order     INT                   DEFAULT 100 COMMENT '排序',
    status         TINYINT               DEFAULT 1 COMMENT '状态：1-启用 0-禁用',
    created_by     VARCHAR(20)  NOT NULL COMMENT '创建人用户编号',
    created_time   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by     VARCHAR(20) COMMENT '更新人用户编号',
    updated_time   DATETIME              DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted     TINYINT               DEFAULT 0 COMMENT '是否删除：0-否，1-是',
    PRIMARY KEY (id),
    KEY            idx_dict_type_code (dict_type_code),
    KEY            idx_group_code (group_code),
    KEY            idx_dict_value (dict_value),
    KEY            idx_status (status),
    KEY            idx_is_deleted (is_deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='字典项表';

-- ----------------------------
-- 17. 变更记录表
-- ----------------------------
CREATE TABLE biz_change_record
(
    id                      BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '序号',
    record_code             VARCHAR(20) NOT NULL COMMENT '变更记录编码，CHG+年月日+4位序列',
    current_status          VARCHAR(50)          DEFAULT 'PENDING_APPROVAL' COMMENT '当前状态（关联字典）',
    release_date            DATE COMMENT '发布日期',
    defect_number           VARCHAR(100) COMMENT '缺陷编号',
    group_name              VARCHAR(100) COMMENT '组别',
    developer_num           VARCHAR(5) COMMENT '开发负责人用户编号',
    developer_name          VARCHAR(50) COMMENT '开发负责人姓名',
    branch_name             VARCHAR(100) COMMENT '分支名称',
    service_name            VARCHAR(100) COMMENT '服务名称',
    problem_description     TEXT COMMENT '问题描述',
    impact_analysis         TEXT COMMENT '问题影响分析',
    solution                TEXT COMMENT '解决方案',
    involve_external_system TINYINT              DEFAULT 0 COMMENT '是否涉及外围系统：0-否，1-是',
    cross_service           TINYINT              DEFAULT 0 COMMENT '是否跨服务：0-否，1-是',
    code_list               TEXT COMMENT '代码清单',
    remark                  TEXT COMMENT '备注说明',
    version                 VARCHAR(50) COMMENT '版本号',
    change_desc             TEXT COMMENT '变更描述',
    develop_type            VARCHAR(32) COMMENT '开发类别（关联字典）',
    org_code                VARCHAR(10) COMMENT '所属机构编码',
    dept_code               VARCHAR(14) COMMENT '所属部门编码',
    approver_num            VARCHAR(5) COMMENT '审批人用户编号',
    approver_name           VARCHAR(50) COMMENT '审批人姓名',
    approval_time           DATETIME COMMENT '审批时间',
    approval_remark         TEXT COMMENT '审批备注',
    created_by              VARCHAR(20) NOT NULL COMMENT '创建人用户编号',
    created_time            DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by              VARCHAR(20) COMMENT '更新人用户编号',
    updated_time            DATETIME             DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted              TINYINT              DEFAULT 0 COMMENT '是否删除：0-否，1-是',
    PRIMARY KEY (id),
    UNIQUE KEY uk_record_code (record_code),
    KEY                     idx_release_date (release_date),
    KEY                     idx_defect_number (defect_number),
    KEY                     idx_service_name (service_name),
    KEY                     idx_developer_num (developer_num),
    KEY                     idx_group_name (group_name),
    KEY                     idx_current_status (current_status),
    KEY                     idx_org_code (org_code),
    KEY                     idx_dept_code (dept_code),
    KEY                     idx_approver_num (approver_num)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='变更记录表';

-- ----------------------------
-- 18. 变更历史表
-- ----------------------------
CREATE TABLE biz_change_history
(
    id                      BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '历史记录ID',
    record_id               BIGINT UNSIGNED NOT NULL COMMENT '关联的主记录ID',
    record_code             VARCHAR(20) NOT NULL COMMENT '变更记录编码',
    operation_type          VARCHAR(20) NOT NULL COMMENT '操作类型：CREATE/UPDATE/DELETE/APPROVE/REJECT',
    operation_user_num      VARCHAR(5)  NOT NULL COMMENT '操作用户编号',
    operation_user_name     VARCHAR(50) NOT NULL COMMENT '操作用户姓名',
    operation_time          DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    operation_description   VARCHAR(500) COMMENT '操作描述',
    current_status          VARCHAR(50) COMMENT '操作时的当前状态',
    release_date            DATE COMMENT '发布日期',
    defect_number           VARCHAR(100) COMMENT '缺陷编号',
    group_name              VARCHAR(100) COMMENT '组别',
    developer_num           VARCHAR(5) COMMENT '开发负责人用户编号',
    developer_name          VARCHAR(50) COMMENT '开发负责人姓名',
    branch_name             VARCHAR(100) COMMENT '分支名称',
    service_name            VARCHAR(100) COMMENT '服务名称',
    problem_description     TEXT COMMENT '问题描述',
    impact_analysis         TEXT COMMENT '问题影响分析',
    solution                TEXT COMMENT '解决方案',
    involve_external_system TINYINT COMMENT '是否涉及外围系统',
    cross_service           TINYINT COMMENT '是否跨服务',
    code_list               TEXT COMMENT '代码清单',
    remark                  TEXT COMMENT '备注说明',
    version                 VARCHAR(50) COMMENT '版本号',
    change_desc             TEXT COMMENT '变更描述',
    develop_type            VARCHAR(32) COMMENT '开发类别',
    org_code                VARCHAR(10) COMMENT '所属机构编码',
    dept_code               VARCHAR(14) COMMENT '所属部门编码',
    approver_num            VARCHAR(5) COMMENT '审批人用户编号',
    approver_name           VARCHAR(50) COMMENT '审批人姓名',
    approval_time           DATETIME COMMENT '审批时间',
    approval_remark         TEXT COMMENT '审批备注',
    PRIMARY KEY (id),
    KEY                     idx_record_id (record_id),
    KEY                     idx_record_code (record_code),
    KEY                     idx_operation_time (operation_time),
    KEY                     idx_operation_user_num (operation_user_num),
    KEY                     idx_operation_type (operation_type),
    KEY                     idx_current_status (current_status),
    CONSTRAINT fk_history_record FOREIGN KEY (record_id) REFERENCES biz_change_record (id) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='变更历史表';

-- ----------------------------
-- 19. 系统操作日志表
-- ----------------------------
CREATE TABLE sys_operation_log
(
    id             BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '日志ID',
    operator_num   VARCHAR(5)  NOT NULL COMMENT '操作人用户编号',
    operator_name  VARCHAR(50) NOT NULL COMMENT '操作人姓名',
    operation_type VARCHAR(20) NOT NULL COMMENT '操作类型',
    object_type    VARCHAR(50) NOT NULL COMMENT '对象类型',
    module         VARCHAR(100) COMMENT '模块名称',
    object_id      BIGINT UNSIGNED NOT NULL COMMENT '对象ID',
    object_code    VARCHAR(50) COMMENT '对象编码',
    result         VARCHAR(20) NOT NULL COMMENT '结果：SUCCESS/FAIL',
    message        VARCHAR(500) COMMENT '结果说明',
    operation_time DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    page_path      VARCHAR(200) COMMENT '页面路径',
    button_name    VARCHAR(100) COMMENT '按钮名称',
    ip_address     VARCHAR(50) COMMENT '操作IP地址',
    user_agent     VARCHAR(500) COMMENT '用户代理',
    request_params TEXT COMMENT '请求参数',
    response_data  TEXT COMMENT '响应数据',
    duration_ms    INT COMMENT '操作耗时（毫秒）',
    PRIMARY KEY (id),
    KEY            idx_object (object_type, object_id),
    KEY            idx_operator_time (operator_num, operation_time),
    KEY            idx_object_code (object_code),
    KEY            idx_operation_type (operation_type),
    KEY            idx_result (result)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统操作日志表';

-- ============================================
-- 19张表创建完成
-- ============================================


-- ============================================
-- 初始化数据
-- ============================================

-- 1. 初始化特殊机构（超级机构）
INSERT INTO sys_org (org_code, org_name, parent_org_code, level, sort_order, status, description, created_by,
                     created_time, updated_by, updated_time, is_deleted)
VALUES ('99999', '超级机构', NULL, 1, 1, 1, '系统超级管理员所属机构', 'SYSTEM', NOW(), NULL, NULL, 0),
       ('01000', '总部', NULL, 1, 2, 1, '公司总部', '99999', NOW(), NULL, NULL, 0),
       ('01001', '技术中心', '01000', 2, 1, 1, '技术研发中心', '99999', NOW(), NULL, NULL, 0),
       ('01002', '产品中心', '01000', 2, 2, 1, '产品管理部门', '99999', NOW(), NULL, NULL, 0),
       ('02000', '北京分公司', NULL, 1, 3, 1, '北京分公司', '99999', NOW(), NULL, NULL, 0);

-- 2. 初始化部门
INSERT INTO sys_dept (dept_code, dept_name, org_code, manager_num, sort_order, status, description, created_by,
                      created_time, updated_by, updated_time, is_deleted)
VALUES ('99999D001', '研发中心', '99999', '99999', 1, 1, '超级机构研发中心', '99999', NOW(), NULL, NULL, 0),
       ('01000D001', '总部研发部', '01000', '90001', 1, 1, '总部研发部门', '99999', NOW(), NULL, NULL, 0),
       ('01000D002', '总部产品部', '01000', '90003', 2, 1, '总部产品部门', '99999', NOW(), NULL, NULL, 0),
       ('01001D001', '技术研发部', '01001', '90004', 1, 1, '技术研发部门', '99999', NOW(), NULL, NULL, 0),
       ('02000D001', '北京研发部', '02000', '90002', 1, 1, '北京研发部门', '99999', NOW(), NULL, NULL, 0);

-- 3. 初始化特殊用户 AIguibin（超级管理员）
INSERT INTO sys_user (user_num, user_name, nickname, gender, password, salt, org_code, dept_code, email, phone, avatar,
                      last_login_time, last_login_ip, login_count, status, is_locked, lock_time, lock_reason,
                      is_special, pwd_expire_time, pwd_modified_time, created_by, created_time, updated_by,
                      updated_time, is_deleted)
VALUES ('99999', 'AIguibin', '系统拥有者', 1,
        '$2a$10$abcdefghijklmnopqrstuvwxyz0123456789abcdefghijklmnopq',
        'abcdefghijklmnopqrstuvwx', '99999', '99999D001', 'aiguibin@system.com', '13888888888',
        NULL, NULL, NULL, 0, 1, 0, NULL, NULL, 1, '2099-12-31 23:59:59', NOW(), 'SYSTEM', NOW(), NULL, NULL, 0);

-- 4. 初始化其他示例用户
INSERT INTO sys_user (user_num, user_name, nickname, gender, password, salt, org_code, dept_code, email, phone, avatar,
                      last_login_time, last_login_ip, login_count, status, is_locked, lock_time, lock_reason,
                      is_special, pwd_expire_time, pwd_modified_time, created_by, created_time, updated_by,
                      updated_time, is_deleted)
VALUES ('90001', '张三', '张三', 1,
        '$2a$10$abcdefghijklmnopqrstuvwxyz0123456789abcdefghijklmnopq',
        'abcdefghijklmnopqrstuvwx', '01000', '01000D001', 'zhangsan@example.com', '13800138001',
        NULL, NULL, NULL, 0, 1, 0, NULL, NULL, 0, '2026-12-31 23:59:59', NOW(), '99999', NOW(), NULL, NULL, 0),
       ('90002', '李四', '李四', 2,
        '$2a$10$abcdefghijklmnopqrstuvwxyz0123456789abcdefghijklmnopq',
        'abcdefghijklmnopqrstuvwx', '02000', '02000D001', 'lisi@example.com', '13800138002',
        NULL, NULL, NULL, 0, 1, 0, NULL, NULL, 0, '2026-12-31 23:59:59', NOW(), '99999', NOW(), NULL, NULL, 0),
       ('90003', '王五', '王五', 1,
        '$2a$10$abcdefghijklmnopqrstuvwxyz0123456789abcdefghijklmnopq',
        'abcdefghijklmnopqrstuvwx', '01000', '01000D002', 'wangwu@example.com', '13800138003',
        NULL, NULL, NULL, 0, 1, 0, NULL, NULL, 0, '2026-12-31 23:59:59', NOW(), '99999', NOW(), NULL, NULL, 0),
       ('90004', '赵六', '赵六', 2,
        '$2a$10$abcdefghijklmnopqrstuvwxyz0123456789abcdefghijklmnopq',
        'abcdefghijklmnopqrstuvwx', '01001', '01001D001', 'zhaoliu@example.com', '13800138004',
        NULL, NULL, NULL, 0, 1, 0, NULL, NULL, 0, '2026-12-31 23:59:59', NOW(), '99999', NOW(), NULL, NULL, 0);

-- 5. 初始化用户机构扩展表
INSERT INTO sys_user_org (user_num, org_code, is_primary, position, effective_start, effective_end, status, description,
                          created_by, created_time, updated_by, updated_time, is_deleted)
VALUES ('99999', '99999', 1, '系统拥有者', '2000-01-01', '2099-12-31', 1, '超级管理员主机构', 'SYSTEM', NOW(), NULL,
        NULL, 0),
       ('90001', '01000', 1, '研发工程师', '2024-01-01', '2026-12-31', 1, '总部主机构', '99999', NOW(), NULL, NULL, 0),
       ('90002', '02000', 1, '研发工程师', '2024-01-01', '2026-12-31', 1, '北京分公司主机构', '99999', NOW(), NULL,
        NULL, 0),
       ('90003', '01000', 1, '产品经理', '2024-01-01', '2026-12-31', 1, '总部产品部主机构', '99999', NOW(), NULL, NULL,
        0),
       ('90004', '01001', 1, '架构师', '2024-01-01', '2026-12-31', 1, '技术中心主机构', '99999', NOW(), NULL, NULL, 0);

-- 6. 初始化用户部门扩展表
INSERT INTO sys_user_dept (user_num, dept_code, is_primary, position, effective_start, effective_end, status,
                           description, created_by, created_time, updated_by, updated_time, is_deleted)
VALUES ('99999', '99999D001', 1, '系统拥有者', '2000-01-01', '2099-12-31', 1, '超级管理员主部门', 'SYSTEM', NOW(), NULL,
        NULL, 0),
       ('90001', '01000D001', 1, '研发工程师', '2024-01-01', '2026-12-31', 1, '总部研发部主部门', '99999', NOW(), NULL,
        NULL, 0),
       ('90002', '02000D001', 1, '研发工程师', '2024-01-01', '2026-12-31', 1, '北京研发部主部门', '99999', NOW(), NULL,
        NULL, 0),
       ('90003', '01000D002', 1, '产品经理', '2024-01-01', '2026-12-31', 1, '总部产品部主部门', '99999', NOW(), NULL,
        NULL, 0),
       ('90004', '01001D001', 1, '架构师', '2024-01-01', '2026-12-31', 1, '技术研发部主部门', '99999', NOW(), NULL,
        NULL, 0);

-- 7. 初始化角色
INSERT INTO sys_role (role_code, role_name, role_type, data_scope_type, sort_order, status, description, created_by,
                      created_time, updated_by, updated_time, is_deleted)
VALUES ('R001', '超级管理员', 1, 1, 1, 1, '系统超级管理员，拥有所有权限', '99999', NOW(), NULL, NULL, 0),
       ('R002', '架构师', 2, 2, 2, 1, '项目架构师，负责技术架构和项目管理', '99999', NOW(), NULL, NULL, 0),
       ('R003', '开发工程师', 2, 4, 3, 1, '开发工程师，负责具体开发工作', '99999', NOW(), NULL, NULL, 0),
       ('R004', '产品经理', 2, 3, 4, 1, '产品经理，负责产品管理', '99999', NOW(), NULL, NULL, 0),
       ('R005', '测试工程师', 2, 4, 5, 1, '测试工程师，负责测试工作', '99999', NOW(), NULL, NULL, 0);

-- 8. 初始化用户角色关联
INSERT INTO sys_user_role (user_num, role_code, is_primary, effective_start, effective_end, status, description,
                           created_by, created_time, updated_by, updated_time, is_deleted)
VALUES ('99999', 'R001', 1, '2000-01-01', '2099-12-31', 1, 'AIguibin作为超级管理员', 'SYSTEM', NOW(), NULL, NULL, 0),
       ('90001', 'R003', 1, '2024-01-01', '2026-12-31', 1, '张三作为开发工程师', '99999', NOW(), NULL, NULL, 0),
       ('90002', 'R003', 1, '2024-01-01', '2026-12-31', 1, '李四作为开发工程师', '99999', NOW(), NULL, NULL, 0),
       ('90003', 'R004', 1, '2024-01-01', '2026-12-31', 1, '王五作为产品经理', '99999', NOW(), NULL, NULL, 0),
       ('90004', 'R002', 1, '2024-01-01', '2026-12-31', 1, '赵六作为架构师', '99999', NOW(), NULL, NULL, 0);

-- 9. 初始化菜单（简化版本，只包含核心功能）
INSERT INTO sys_menu (menu_code, menu_name, menu_type, parent_menu_code, icon, path, component, url, http_method,
                      is_external, is_cache, is_visible, permission_key, sort_order, status, description, created_by,
                      created_time, updated_by, updated_time, is_deleted)
VALUES
-- 首页
('M0000001', '首页', 1, NULL, 'el-icon-s-home', '/', NULL, NULL, NULL, 0, 1, 1, NULL, 1, 1, '系统首页目录', '99999',
 NOW(), NULL, NULL, 0),
('P0000001', '首页概览', 2, 'M0000001', NULL, '/', 'views/Home', NULL, NULL, 0, 1, 1, 'home:overview', 1, 1,
 '首页概览页面', '99999', NOW(), NULL, NULL, 0),

-- 变更管理
('M0000002', '变更管理', 1, NULL, 'el-icon-edit-outline', '/change-records', NULL, NULL, NULL, 0, 1, 1, NULL, 2, 1,
 '变更管理目录', '99999', NOW(), NULL, NULL, 0),
('P0000002', '变更记录管理', 2, 'M0000002', NULL, '/change-records', 'views/ChangeRecordList', NULL, NULL, 0, 1, 1,
 'change:record:list', 1, 1, '变更记录列表页面', '99999', NOW(), NULL, NULL, 0),
('B0000001', '新增变更', 3, 'P0000002', NULL, NULL, NULL, '/api/change-records', 'POST', 0, 1, 1,
 'change:record:create', 1, 1, '新增变更记录按钮', '99999', NOW(), NULL, NULL, 0),
('B0000002', '编辑变更', 3, 'P0000002', NULL, NULL, NULL, '/api/change-records/*', 'PUT', 0, 1, 1, 'change:record:edit',
 2, 1, '编辑变更记录按钮', '99999', NOW(), NULL, NULL, 0),
('B0000003', '删除变更', 3, 'P0000002', NULL, NULL, NULL, '/api/change-records/*', 'DELETE', 0, 1, 1,
 'change:record:delete', 3, 1, '删除变更记录按钮', '99999', NOW(), NULL, NULL, 0),
('B0000004', '导出变更', 3, 'P0000002', NULL, NULL, NULL, '/api/change-records/export', 'GET', 0, 1, 1,
 'change:record:export', 4, 1, '导出变更记录按钮', '99999', NOW(), NULL, NULL, 0),

-- 系统设置
('M0000003', '系统设置', 1, NULL, 'el-icon-setting', '/system-settings', NULL, NULL, NULL, 0, 1, 1, NULL, 5, 1,
 '系统设置目录', '99999', NOW(), NULL, NULL, 0),
('P0000003', '用户管理', 2, 'M0000003', NULL, '/system-settings/users', 'views/UserManagement', NULL, NULL, 0, 1, 1,
 'user:management', 1, 1, '用户管理页面', '99999', NOW(), NULL, NULL, 0),
('P0000004', '角色管理', 2, 'M0000003', NULL, '/system-settings/roles', 'views/RoleManagement', NULL, NULL, 0, 1, 1,
 'role:management', 2, 1, '角色管理页面', '99999', NOW(), NULL, NULL, 0),
('P0000005', '菜单管理', 2, 'M0000003', NULL, '/system-settings/menus', 'views/MenuManagement', NULL, NULL, 0, 1, 1,
 'menu:management', 3, 1, '菜单管理页面', '99999', NOW(), NULL, NULL, 0),
('P0000006', '字典管理', 2, 'M0000003', NULL, '/system-settings/dicts', 'views/DictManagement', NULL, NULL, 0, 1, 1,
 'dict:management', 4, 1, '字典管理页面', '99999', NOW(), NULL, NULL, 0),
('P0000007', '操作日志', 2, 'M0000003', NULL, '/system-settings/logs', 'views/OperationLogs', NULL, NULL, 0, 1, 1,
 'log:operation', 5, 1, '操作日志页面', '99999', NOW(), NULL, NULL, 0);

-- 10. 初始化权限（核心功能权限）
INSERT INTO sys_permission (perm_code, perm_name, perm_key, perm_type, menu_code, api_path, entity_type, rule_type,
                            scope_type, custom_rule, is_default, sort_order, status, description, created_by,
                            created_time, updated_by, updated_time, is_deleted)
VALUES
-- 首页权限
('P0001', '首页概览访问', 'home:overview', 1, 'P0000001', '/api/home/overview', NULL, NULL, NULL, NULL, 1, 1, 1,
 '访问首页概览的权限', '99999', NOW(), NULL, NULL, 0),

-- 变更管理权限
('P0002', '变更记录列表查看', 'change:record:list', 1, 'P0000002', '/api/change-records', NULL, NULL, NULL, NULL, 1, 2,
 1, '查看变更记录列表的权限', '99999', NOW(), NULL, NULL, 0),
('P0003', '变更记录新增', 'change:record:create', 2, NULL, '/api/change-records', NULL, NULL, NULL, NULL, 1, 3, 1,
 '新增变更记录的权限', '99999', NOW(), NULL, NULL, 0),
('P0004', '变更记录编辑', 'change:record:edit', 2, NULL, '/api/change-records/*', NULL, NULL, NULL, NULL, 1, 4, 1,
 '编辑变更记录的权限', '99999', NOW(), NULL, NULL, 0),
('P0005', '变更记录删除', 'change:record:delete', 2, NULL, '/api/change-records/*', NULL, NULL, NULL, NULL, 1, 5, 1,
 '删除变更记录的权限', '99999', NOW(), NULL, NULL, 0),
('P0006', '变更记录导出', 'change:record:export', 2, NULL, '/api/change-records/export', NULL, NULL, NULL, NULL, 1, 6,
 1, '导出变更记录的权限', '99999', NOW(), NULL, NULL, 0),

-- 系统管理权限
('P0007', '用户管理', 'user:management', 1, 'P0000003', '/api/users', NULL, NULL, NULL, NULL, 1, 7, 1,
 '用户管理页面访问权限', '99999', NOW(), NULL, NULL, 0),
('P0008', '角色管理', 'role:management', 1, 'P0000004', '/api/roles', NULL, NULL, NULL, NULL, 1, 8, 1,
 '角色管理页面访问权限', '99999', NOW(), NULL, NULL, 0),
('P0009', '菜单管理', 'menu:management', 1, 'P0000005', '/api/menus', NULL, NULL, NULL, NULL, 1, 9, 1,
 '菜单管理页面访问权限', '99999', NOW(), NULL, NULL, 0),
('P0010', '字典管理', 'dict:management', 1, 'P0000006', '/api/dicts', NULL, NULL, NULL, NULL, 1, 10, 1,
 '字典管理页面访问权限', '99999', NOW(), NULL, NULL, 0),
('P0011', '操作日志查看', 'log:operation', 1, 'P0000007', '/api/operation-logs', NULL, NULL, NULL, NULL, 1, 11, 1,
 '查看操作日志的权限', '99999', NOW(), NULL, NULL, 0),

-- 数据权限示例
('P0012', '查看全部变更数据', 'change:data:all', 4, NULL, NULL, 'biz_change_record', 1, 1, NULL, 0, 12, 1,
 '查看全部变更数据的权限', '99999', NOW(), NULL, NULL, 0),
('P0013', '查看本机构变更数据', 'change:data:org', 4, NULL, NULL, 'biz_change_record', 1, 2, NULL, 0, 13, 1,
 '查看本机构变更数据的权限', '99999', NOW(), NULL, NULL, 0),
('P0014', '查看本人变更数据', 'change:data:self', 4, NULL, NULL, 'biz_change_record', 1, 4, NULL, 0, 14, 1,
 '查看本人变更数据的权限', '99999', NOW(), NULL, NULL, 0);

-- 11. 初始化角色权限关联（超级管理员拥有所有权限）
INSERT INTO sys_role_permission (role_code, perm_code, auth_type, effective_start, effective_end, priority, status,
                                 description, created_by, created_time, updated_by, updated_time, is_deleted)
SELECT 'R001',
       perm_code,
       1,
       '2000-01-01',
       '2099-12-31',
       100,
       1,
       '超级管理员权限',
       '99999',
       NOW(),
       NULL,
       NULL,
       0
FROM sys_permission;

-- 12. 为架构师角色分配部分权限
INSERT INTO sys_role_permission (role_code, perm_code, auth_type, effective_start, effective_end, priority, status,
                                 description, created_by, created_time, updated_by, updated_time, is_deleted)
VALUES ('R002', 'P0001', 1, '2024-01-01', '2026-12-31', 100, 1, '架构师首页权限', '99999', NOW(), NULL, NULL, 0),
       ('R002', 'P0002', 1, '2024-01-01', '2026-12-31', 100, 1, '架构师变更列表权限', '99999', NOW(), NULL, NULL, 0),
       ('R002', 'P0003', 1, '2024-01-01', '2026-12-31', 100, 1, '架构师新增变更权限', '99999', NOW(), NULL, NULL, 0),
       ('R002', 'P0004', 1, '2024-01-01', '2026-12-31', 100, 1, '架构师编辑变更权限', '99999', NOW(), NULL, NULL, 0),
       ('R002', 'P0005', 1, '2024-01-01', '2026-12-31', 100, 1, '架构师删除变更权限', '99999', NOW(), NULL, NULL, 0),
       ('R002', 'P0006', 1, '2024-01-01', '2026-12-31', 100, 1, '架构师导出变更权限', '99999', NOW(), NULL, NULL, 0),
       ('R002', 'P0013', 1, '2024-01-01', '2026-12-31', 100, 1, '架构师查看本机构数据权限', '99999', NOW(), NULL, NULL,
        0);

-- 13. 为开发工程师角色分配基础权限
INSERT INTO sys_role_permission (role_code, perm_code, auth_type, effective_start, effective_end, priority, status,
                                 description, created_by, created_time, updated_by, updated_time, is_deleted)
VALUES ('R003', 'P0001', 1, '2024-01-01', '2026-12-31', 100, 1, '开发工程师首页权限', '99999', NOW(), NULL, NULL, 0),
       ('R003', 'P0002', 1, '2024-01-01', '2026-12-31', 100, 1, '开发工程师变更列表权限', '99999', NOW(), NULL, NULL,
        0),
       ('R003', 'P0003', 1, '2024-01-01', '2026-12-31', 100, 1, '开发工程师新增变更权限', '99999', NOW(), NULL, NULL,
        0),
       ('R003', 'P0004', 1, '2024-01-01', '2026-12-31', 100, 1, '开发工程师编辑变更权限', '99999', NOW(), NULL, NULL,
        0),
       ('R003', 'P0014', 1, '2024-01-01', '2026-12-31', 100, 1, '开发工程师查看本人数据权限', '99999', NOW(), NULL,
        NULL, 0);

-- 14. 初始化数据权限规则
INSERT INTO sys_data_rule (rule_code, rule_name, rule_type, perm_code, entity_type, scope_type, include_children,
                           custom_sql, rule_expression, rule_priority, is_global, status, description, created_by,
                           created_time, updated_by, updated_time, is_deleted)
VALUES ('DR00000001', '超级管理员数据规则', 1, 'P0012', 'biz_change_record', 1, 1, NULL, NULL, 1, 1, 1,
        '超级管理员可以查看所有变更数据', '99999', NOW(), NULL, NULL, 0),
       ('DR00000002', '架构师数据规则', 1, 'P0013', 'biz_change_record', 2, 1, NULL, NULL, 2, 0, 1,
        '架构师可以查看本机构及下属机构变更数据', '99999', NOW(), NULL, NULL, 0),
       ('DR00000003', '开发工程师数据规则', 1, 'P0014', 'biz_change_record', 4, 0, NULL, NULL, 3, 0, 1,
        '开发工程师只能查看自己的变更数据', '99999', NOW(), NULL, NULL, 0);

-- 15. 初始化字典类型
INSERT INTO sys_dict_type (dict_type_code, dict_type_name, description, sort_order, status, created_by, created_time,
                           updated_by, updated_time, is_deleted)
VALUES ('CURRENT_STATUS', '当前状态', '变更记录的当前状态', 1, 1, '99999', NOW(), NULL, NULL, 0),
       ('DEVELOP_TYPE', '开发类别', '开发任务的类别', 2, 1, '99999', NOW(), NULL, NULL, 0),
       ('YES_NO', '是否选项', '通用的是/否选项', 3, 1, '99999', NOW(), NULL, NULL, 0),
       ('OPERATION_TYPE', '操作类型', '系统操作类型', 4, 1, '99999', NOW(), NULL, NULL, 0);

-- 16. 初始化字典项
INSERT INTO sys_dict_item (dict_type_code, dict_value, dict_label, group_code, group_name, sort_order, status,
                           created_by, created_time, updated_by, updated_time, is_deleted)
VALUES
-- 当前状态
('CURRENT_STATUS', 'PENDING_APPROVAL', '待审批', NULL, NULL, 1, 1, '99999', NOW(), NULL, NULL, 0),
('CURRENT_STATUS', 'PENDING_REVIEW', '待评审', NULL, NULL, 2, 1, '99999', NOW(), NULL, NULL, 0),
('CURRENT_STATUS', 'PENDING_MERGE', '待合版', NULL, NULL, 3, 1, '99999', NOW(), NULL, NULL, 0),
('CURRENT_STATUS', 'MERGED', '已合版', NULL, NULL, 4, 1, '99999', NOW(), NULL, NULL, 0),
('CURRENT_STATUS', 'PENDING_RELEASE', '待发布', NULL, NULL, 5, 1, '99999', NOW(), NULL, NULL, 0),
('CURRENT_STATUS', 'RELEASED', '已发布', NULL, NULL, 6, 1, '99999', NOW(), NULL, NULL, 0),
('CURRENT_STATUS', 'REJECTED', '已驳回', NULL, NULL, 7, 1, '99999', NOW(), NULL, NULL, 0),
('CURRENT_STATUS', 'CANCELLED', '已取消', NULL, NULL, 8, 1, '99999', NOW(), NULL, NULL, 0),
-- 开发类别
('DEVELOP_TYPE', 'FRONTEND', '前端', NULL, NULL, 1, 1, '99999', NOW(), NULL, NULL, 0),
('DEVELOP_TYPE', 'BACKEND', '后端', NULL, NULL, 2, 1, '99999', NOW(), NULL, NULL, 0),
('DEVELOP_TYPE', 'SCRIPT', '脚本', NULL, NULL, 3, 1, '99999', NOW(), NULL, NULL, 0),
('DEVELOP_TYPE', 'CONFIG', '配置', NULL, NULL, 4, 1, '99999', NOW(), NULL, NULL, 0),
('DEVELOP_TYPE', 'DATABASE', '数据库', NULL, NULL, 5, 1, '99999', NOW(), NULL, NULL, 0),
('DEVELOP_TYPE', 'OTHER', '其他', NULL, NULL, 6, 1, '99999', NOW(), NULL, NULL, 0),
-- 是否选项
('YES_NO', '1', '是', NULL, NULL, 1, 1, '99999', NOW(), NULL, NULL, 0),
('YES_NO', '0', '否', NULL, NULL, 2, 1, '99999', NOW(), NULL, NULL, 0),
-- 操作类型
('OPERATION_TYPE', 'CREATE', '创建', NULL, NULL, 1, 1, '99999', NOW(), NULL, NULL, 0),
('OPERATION_TYPE', 'UPDATE', '更新', NULL, NULL, 2, 1, '99999', NOW(), NULL, NULL, 0),
('OPERATION_TYPE', 'DELETE', '删除', NULL, NULL, 3, 1, '99999', NOW(), NULL, NULL, 0),
('OPERATION_TYPE', 'APPROVE', '审批', NULL, NULL, 4, 1, '99999', NOW(), NULL, NULL, 0),
('OPERATION_TYPE', 'REJECT', '驳回', NULL, NULL, 5, 1, '99999', NOW(), NULL, NULL, 0),
('OPERATION_TYPE', 'LOGIN', '登录', NULL, NULL, 6, 1, '99999', NOW(), NULL, NULL, 0),
('OPERATION_TYPE', 'LOGOUT', '登出', NULL, NULL, 7, 1, '99999', NOW(), NULL, NULL, 0),
('OPERATION_TYPE', 'EXPORT', '导出', NULL, NULL, 8, 1, '99999', NOW(), NULL, NULL, 0);

-- 17. 初始化变更记录示例数据
INSERT INTO biz_change_record (record_code, current_status, release_date, defect_number, group_name,
                               developer_num, developer_name, branch_name, service_name, problem_description,
                               impact_analysis, solution, involve_external_system, cross_service, code_list,
                               remark, version, change_desc, develop_type, org_code, dept_code, created_by,
                               created_time, updated_by, updated_time, is_deleted)
VALUES ('CHG202501010001', 'RELEASED', '2025-01-15', 'DEF-2025-001', '前端组',
        '90001', '张三', 'feature/user-auth', 'user-service', '用户登录认证存在安全漏洞',
        '可能导致用户信息泄露，影响所有使用该服务的用户', '升级认证算法，增加二次验证', 1, 0,
        'UserController.java, AuthService.java, security-config.xml',
        '需要同步更新前端认证逻辑', 'v2.5.1', '用户认证安全升级', 'FRONTEND', '01000', '01000D001',
        '90001', '2025-01-10 09:30:00', '90001', '2025-01-15 14:20:00', 0),
       ('CHG202501010002', 'PENDING_APPROVAL', NULL, 'DEF-2025-002', '后端组',
        '90002', '李四', 'fix/order-bug', 'order-service', '订单状态更新不及时',
        '导致用户看到的订单状态不准确，可能引发投诉', '优化数据库事务处理，增加状态同步机制', 0, 1,
        'OrderService.java, OrderRepository.java, OrderStatusSyncJob.java',
        '需要测试并发场景下的状态同步', 'v1.3.2', '订单状态同步优化', 'BACKEND', '02000', '02000D001',
        '90002', '2025-01-12 11:15:00', '90002', '2025-01-12 11:15:00', 0),
       ('CHG202501010003', 'MERGED', NULL, 'DEF-2025-003', '脚本组',
        '90004', '赵六', 'feature/data-migration', 'data-service', '数据迁移脚本执行效率低',
        '大数据量迁移耗时过长，影响业务连续性', '优化SQL查询，增加分批处理机制', 0, 0,
        'migration_v1.sql, migration_v2.sql, DataMigrationUtil.java',
        '需要在测试环境充分验证后再上线', 'v3.0.0', '数据迁移性能优化', 'SCRIPT', '01001', '01001D001',
        '90004', '2025-01-14 14:45:00', '90004', '2025-01-16 10:30:00', 0);

-- 18. 初始化变更历史示例数据
INSERT INTO biz_change_history (record_id, record_code, operation_type, operation_user_num, operation_user_name,
                                operation_time, operation_description, current_status, release_date, defect_number,
                                group_name, developer_num, developer_name, branch_name, service_name,
                                problem_description,
                                impact_analysis, solution, involve_external_system, cross_service, code_list, remark,
                                version, change_desc, develop_type, org_code, dept_code)
VALUES (1, 'CHG202501010001', 'CREATE', '90001', '张三', '2025-01-10 09:30:00', '创建变更记录', 'PENDING_APPROVAL',
        '2025-01-15', 'DEF-2025-001', '前端组', '90001', '张三', 'feature/user-auth', 'user-service',
        '用户登录认证存在安全漏洞', '可能导致用户信息泄露，影响所有使用该服务的用户', '升级认证算法，增加二次验证',
        1, 0, 'UserController.java, AuthService.java, security-config.xml', '需要同步更新前端认证逻辑',
        'v2.5.1', '用户认证安全升级', 'FRONTEND', '01000', '01000D001'),
       (1, 'CHG202501010001', 'UPDATE', '90001', '张三', '2025-01-11 10:15:00', '更新解决方案', 'PENDING_REVIEW',
        '2025-01-15', 'DEF-2025-001', '前端组', '90001', '张三', 'feature/user-auth', 'user-service',
        '用户登录认证存在安全漏洞', '可能导致用户信息泄露，影响所有使用该服务的用户',
        '升级认证算法，增加二次验证和日志审计',
        1, 0, 'UserController.java, AuthService.java, security-config.xml, AuditInterceptor.java',
        '需要同步更新前端认证逻辑',
        'v2.5.1', '用户认证安全升级', 'FRONTEND', '01000', '01000D001'),
       (1, 'CHG202501010001', 'APPROVE', '90004', '赵六', '2025-01-13 14:20:00', '审批通过', 'MERGED',
        '2025-01-15', 'DEF-2025-001', '前端组', '90001', '张三', 'feature/user-auth', 'user-service',
        '用户登录认证存在安全漏洞', '可能导致用户信息泄露，影响所有使用该服务的用户',
        '升级认证算法，增加二次验证和日志审计',
        1, 0, 'UserController.java, AuthService.java, security-config.xml, AuditInterceptor.java',
        '需要同步更新前端认证逻辑',
        'v2.5.1', '用户认证安全升级', 'FRONTEND', '01000', '01000D001'),
       (2, 'CHG202501010002', 'CREATE', '90002', '李四', '2025-01-12 11:15:00', '创建变更记录', 'PENDING_APPROVAL',
        NULL, 'DEF-2025-002', '后端组', '90002', '李四', 'fix/order-bug', 'order-service',
        '订单状态更新不及时', '导致用户看到的订单状态不准确，可能引发投诉', '优化数据库事务处理，增加状态同步机制',
        0, 1, 'OrderService.java, OrderRepository.java, OrderStatusSyncJob.java', '需要测试并发场景下的状态同步',
        'v1.3.2', '订单状态同步优化', 'BACKEND', '02000', '02000D001');

-- 19. 初始化操作日志示例数据
INSERT INTO sys_operation_log (operator_num, operator_name, operation_type, object_type, module,
                               object_id, object_code, result, message, operation_time, page_path,
                               button_name, ip_address, user_agent)
VALUES ('90001', '张三', 'LOGIN', 'USER', '认证模块', 0, NULL, 'SUCCESS', '用户登录成功',
        '2025-01-10 09:25:00', '/login', '登录按钮', '192.168.1.100',
        'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36'),
       ('90001', '张三', 'CREATE', 'CHANGE_RECORD', '变更管理', 1, 'CHG202501010001', 'SUCCESS', '创建变更记录成功',
        '2025-01-10 09:30:00', '/change-records', '新增变更', '192.168.1.100',
        'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36'),
       ('90004', '赵六', 'LOGIN', 'USER', '认证模块', 0, NULL, 'SUCCESS', '用户登录成功',
        '2025-01-13 14:15:00', '/login', '登录按钮', '192.168.1.101',
        'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36'),
       ('90004', '赵六', 'APPROVE', 'CHANGE_RECORD', '变更管理', 1, 'CHG202501010001', 'SUCCESS', '审批变更记录成功',
        '2025-01-13 14:20:00', '/change-records', '审批通过', '192.168.1.101',
        'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36'),
       ('90002', '李四', 'LOGIN', 'USER', '认证模块', 0, NULL, 'SUCCESS', '用户登录成功',
        '2025-01-12 11:10:00', '/login', '登录按钮', '192.168.1.102',
        'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36'),
       ('90002', '李四', 'CREATE', 'CHANGE_RECORD', '变更管理', 2, 'CHG202501010002', 'SUCCESS', '创建变更记录成功',
        '2025-01-12 11:15:00', '/change-records', '新增变更', '192.168.1.102',
        'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36');

-- ============================================
-- 初始化数据完成
-- 总计19张表，包含完整的权限管理和业务功能
-- ============================================
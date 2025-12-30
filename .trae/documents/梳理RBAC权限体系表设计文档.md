# 系统权限管理表关系梳理文档

## 一、项目概述

### 1.1 业务背景

+ **业务领域**：企业级权限管理系统
+ **核心功能**：用户管理、组织架构管理、角色权限控制
+ **梳理目的**：理清用户、组织、角色之间的多对多关系，支持复杂的权限分配场景

### 1.2 文档说明

+ **适用对象**：开发人员、DBA、系统管理员
+ **文档版本**：v1.0
+ **最后更新**：2024年1月
+ **设计特点**：支持用户多机构/多部门、角色多数据范围、时效性权限控制

---

## 二、核心实体清单

### 2.1 实体列表

| 序号  | 实体名称   | 表名            | 业务含义      | 重要性   | 数据量级 |
| --- | ------ | ------------- | --------- | ----- | ---- |
| 1   | 用户     | sys_user      | 系统使用者账户   | ★★★★★ | 万级   |
| 2   | 机构     | sys_org       | 组织架构的顶级单位 | ★★★★★ | 百级   |
| 3   | 部门     | sys_dept      | 机构下的二级单位  | ★★★★☆ | 千级   |
| 4   | 角色     | sys_role      | 权限集合      | ★★★★★ | 百级   |
| 5   | 用户机构关联 | sys_user_org  | 用户与机构的关系  | ★★★★☆ | 万级   |
| 6   | 用户部门关联 | sys_user_dept | 用户与部门的关系  | ★★★★☆ | 万级   |
| 7   | 用户角色关联 | sys_user_role | 用户与角色的关系  | ★★★★★ | 万级   |
| 8   | 角色机构范围 | sys_role_org  | 角色的数据权限范围 | ★★★☆☆ | 千级   |

### 2.2 实体详细定义

#### 实体1：用户（sys_user）

**表结构：**

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
  `is_deleted` tinyint DEFAULT '0' COMMENT '是否删除：0-否，1-是'
);
```

**关键字段说明：**

| 字段名       | 数据类型        | 必填  | 默认值  | 说明        | 示例          |
| --------- | ----------- | --- | ---- | --------- | ----------- |
| user_num  | varchar(20) | 是   | -    | 用户编号，业务主键 | U2024010001 |
| user_name | varchar(50) | 是   | -    | 用户真实姓名，唯一 | 张三          |
| org_code  | varchar(10) | 否   | NULL | 主机构编码     | ORG001      |
| dept_code | varchar(15) | 否   | NULL | 主部门编码     | DEPT001     |
| status    | tinyint     | 否   | 1    | 账户状态      | 1(启用)       |
| is_locked | tinyint     | 否   | 0    | 是否被锁定     | 0(未锁定)      |

**业务规则：**

1. **唯一性规则**：`user_num`和`user_name`都必须全局唯一
2. **密码安全**：密码使用盐值加密存储，支持密码过期策略
3. **账户状态**：禁用(0)优先级高于锁定(1)，被禁用的账户即使未锁定也无法登录
4. **主归属**：`org_code`和`dept_code`表示用户的主要归属，必须与`sys_user_org`和`sys_user_dept`中的主记录一致
5. **审计追踪**：所有操作记录操作人(`created_by`/`updated_by`)和时间

---

#### 实体2：机构（sys_org）

**表结构：**

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
  `is_deleted` tinyint DEFAULT '0' COMMENT '是否删除：0-否，1-是'
);
```

**关键字段说明：**

| 字段名             | 数据类型         | 必填  | 默认值  | 说明        | 示例              |
| --------------- | ------------ | --- | ---- | --------- | --------------- |
| org_code        | varchar(10)  | 是   | -    | 机构编码，层级编码 | 0101(表示第一级第一机构) |
| org_name        | varchar(100) | 是   | -    | 机构名称      | 总公司             |
| parent_org_code | varchar(10)  | 否   | NULL | 上级机构编码    | 0100(上级机构)      |
| level           | tinyint      | 是   | -    | 层级深度      | 1(一级机构)         |
| sort_order      | int          | 否   | 100  | 同级排序      | 10(显示顺序)        |

**业务规则：**

1. **编码规则**：采用定长层级编码，如"0101"表示第一级第一个机构的下级机构
2. **树形结构**：通过`parent_org_code`实现无限级树形结构
3. **层级限制**：最多支持5级机构（`level`字段值1-5）
4. **启用约束**：禁用父机构时，子机构也应被禁用（业务逻辑控制）

---

#### 实体3：部门（sys_dept）

**表结构：**

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
  `is_deleted` tinyint DEFAULT '0' COMMENT '是否删除：0-否，1-是'
);
```

**关键字段说明：**

| 字段名         | 数据类型         | 必填  | 默认值  | 说明    | 示例          |
| ----------- | ------------ | --- | ---- | ----- | ----------- |
| dept_code   | varchar(15)  | 是   | -    | 部门编码  | ORG001D0001 |
| dept_name   | varchar(100) | 是   | -    | 部门名称  | 技术部         |
| org_code    | varchar(10)  | 是   | -    | 所属机构  | ORG001      |
| manager_num | varchar(20)  | 否   | NULL | 负责人编号 | U2024010001 |
| status      | tinyint      | 否   | 1    | 部门状态  | 1(启用)       |

**业务规则：**

1. **编码规则**：`机构编码(10位) + 'D' + 4位序列号`，确保全局唯一
2. **机构归属**：每个部门必须属于一个有效的机构
3. **负责人约束**：`manager_num`必须是有效的用户编号，且该用户属于本部门
4. **级联影响**：机构被禁用时，其下所有部门也应被禁用

---

#### 实体4：角色（sys_role）

**表结构：**

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
  `is_deleted` tinyint DEFAULT '0' COMMENT '是否删除：0-否，1-是'
);
```

**关键字段说明：**

| 字段名             | 数据类型        | 必填  | 默认值 | 说明     | 示例      |
| --------------- | ----------- | --- | --- | ------ | ------- |
| role_code       | varchar(5)  | 是   | -   | 角色编码   | R0001   |
| role_name       | varchar(50) | 是   | -   | 角色名称   | 系统管理员   |
| role_type       | tinyint     | 否   | 1   | 角色类型   | 1(系统角色) |
| data_scope_type | tinyint     | 否   | 4   | 默认数据范围 | 4(本人数据) |

**业务规则：**

1. **编码规则**：固定5位，格式"R" + 4位数字，如R0001
2. **类型区分**：
   - 系统角色(1)：系统内置，不可删除
   - 业务角色(2)：按业务需求预定义
   - 自定义角色(3)：用户自定义创建
3. **数据范围**：
   - 1-全部：无数据限制
   - 2-本机构：只能操作本机构数据
   - 3-本部门：只能操作本部门数据
   - 4-本人：只能操作自己的数据
   - 5-自定义：通过`sys_role_org`表详细定义

---

#### 实体5：用户机构关联（sys_user_org）

**表结构：**

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
  `is_deleted` tinyint DEFAULT '0' COMMENT '是否删除：0-否，1-是'
);
```

**关键字段说明：**

| 字段名             | 数据类型        | 必填  | 默认值  | 说明    | 示例                  |
| --------------- | ----------- | --- | ---- | ----- | ------------------- |
| user_num        | varchar(20) | 是   | -    | 用户编号  | U2024010001         |
| org_code        | varchar(10) | 是   | -    | 机构编码  | ORG001              |
| is_primary      | tinyint     | 否   | 0    | 是否主机构 | 1(是主机构)             |
| effective_start | datetime    | 否   | NULL | 生效开始  | 2024-01-01 00:00:00 |
| effective_end   | datetime    | 否   | NULL | 生效结束  | 2024-12-31 23:59:59 |

**业务规则：**

1. **唯一约束**：同一个用户在同一机构只能有一条有效记录
2. **主机构规则**：每个用户有且仅有一个主机构（`is_primary=1`）
3. **时效性**：支持设置有效时间段，过期自动失效
4. **数据冗余**：冗余`org_name`字段，减少关联查询
5. **状态同步**：当机构被禁用时，所有相关用户机构关联应标记为禁用

---

#### 实体6：用户部门关联（sys_user_dept）

**表结构：**

```sql
CREATE TABLE `sys_user_dept` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '自增ID，仅做序号',
  `user_num` varchar(20) NOT NULL COMMENT '用户编号',
  `dept_code` varchar(15) NOT NULL COMMENT '部门编码',
  `dept_name` varchar(100)  DEFAULT NULL COMMENT '部门名称',
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
  `is_deleted` tinyint DEFAULT '0' COMMENT '是否删除：0-否，1-是'
);
```

**关键字段说明：**

| 字段名             | 数据类型         | 必填  | 默认值  | 说明    | 示例          |
| --------------- | ------------ | --- | ---- | ----- | ----------- |
| user_num        | varchar(20)  | 是   | -    | 用户编号  | U2024010001 |
| dept_code       | varchar(15)  | 是   | -    | 部门编码  | DEPT001     |
| is_primary      | tinyint      | 否   | 0    | 是否主部门 | 1(是主部门)     |
| position        | varchar(100) | 否   | NULL | 职位名称  | 部门经理        |
| effective_start | datetime     | 否   | NULL | 生效开始  | 2024-01-01  |

**业务规则：**

1. **部门归属验证**：用户加入的部门必须属于用户已关联的机构
2. **主部门唯一**：每个用户在每个机构下有且仅有一个主部门
3. **一致性检查**：用户的主部门必须属于用户的主机构
4. **负责人关联**：当用户是部门负责人时，必须存在于该部门的用户部门关联中

---

#### 实体7：用户角色关联（sys_user_role）

**表结构：**

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
  `is_deleted` tinyint DEFAULT '0' COMMENT '是否删除：0-否，1-是'
);
```

**关键字段说明：**

| 字段名        | 数据类型         | 必填  | 默认值  | 说明    | 示例          |
| ---------- | ------------ | --- | ---- | ----- | ----------- |
| user_num   | varchar(20)  | 是   | -    | 用户编号  | U2024010001 |
| role_code  | varchar(5)   | 是   | -    | 角色编码  | R0001       |
| role_name  | varchar(255) | 否   | NULL | 角色名称  | 系统管理员       |
| is_primary | tinyint      | 否   | 0    | 是否主角色 | 1(是主角色)     |

**业务规则：**

1. **主角色唯一**：每个用户有且仅有一个主角色（`is_primary=1`）
2. **时效性控制**：支持角色分配的有效期管理
3. **角色状态同步**：当角色被禁用时，所有用户角色关联应标记为禁用
4. **冗余设计**：冗余`role_name`字段，方便直接显示

---

#### 实体8：角色机构范围（sys_role_org）

**表结构：**

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
  `is_deleted` tinyint DEFAULT '0' COMMENT '是否删除：0-否，1-是'
);
```

**关键字段说明：**

| 字段名            | 数据类型        | 必填  | 默认值 | 说明   | 示例       |
| -------------- | ----------- | --- | --- | ---- | -------- |
| role_code      | varchar(5)  | 是   | -   | 角色编码 | R0001    |
| org_code       | varchar(10) | 是   | -   | 机构编码 | ORG001   |
| org_range_type | tinyint     | 否   | 1   | 范围类型 | 2(含下级机构) |
| perm_type      | tinyint     | 否   | 1   | 权限类型 | 1(管理权限)  |

**业务规则：**

1. **唯一组合**：同一角色对同一机构的同一权限类型只能有一条记录
2. **范围类型**：
   - 1-本机构：仅当前机构
   - 2-包含下级机构：当前机构及其所有子机构
3. **权限类型**：
   - 1-管理权限：增删改查全部权限
   - 2-查看权限：仅数据查看
   - 3-操作权限：特定业务操作权限
4. **数据范围计算**：当角色`data_scope_type=5(自定义)`时，使用此表数据范围

---

## 三、表关系总览

### 3.1 关系图

![](https://cdn.nlark.com/yuque/__mermaid_v3/4aa743b3e474ae4654c143314f5bc7f8.svg)

### 3.2 关系类型统计

| 关系类型 | 数量  | 占比    | 典型示例              |
| ---- | --- | ----- | ----------------- |
| 一对多  | 3   | 37.5% | 机构→部门、机构→用户机构关联   |
| 多对多  | 5   | 62.5% | 用户↔机构、用户↔角色、角色↔机构 |
| 自关联  | 1   | 12.5% | 机构层级关系            |

---

## 四、表关系详述

### 4.1 层级关系（一对多）

#### 关系1：机构 → 部门

+ **关系描述**：一个机构包含多个部门
+ **关联字段**：`sys_org.org_code` = `sys_dept.org_code`
+ **业务场景**：组织架构管理，按机构查看部门列表
+ **查询示例**：

```sql
-- 查询某个机构下的所有部门
SELECT d.* 
FROM sys_dept d
WHERE d.org_code = 'ORG001'
  AND d.status = 1
  AND d.is_deleted = 0
ORDER BY d.sort_order;
```

#### 关系2：机构 → 子机构（自关联）

+ **关系描述**：机构层级树状结构
+ **关联字段**：`parent_org_code` → `org_code`
+ **业务场景**：机构树形展示，权限逐级继承
+ **查询示例**：

```sql
-- 查询机构及其所有子机构（递归）
WITH RECURSIVE org_tree AS (
    SELECT org_code, org_name, parent_org_code, level
    FROM sys_org
    WHERE org_code = 'ORG001'  -- 根机构
      AND status = 1
      AND is_deleted = 0
    UNION ALL
    SELECT o.org_code, o.org_name, o.parent_org_code, o.level
    FROM sys_org o
    INNER JOIN org_tree ot ON o.parent_org_code = ot.org_code
    WHERE o.status = 1 AND o.is_deleted = 0
)
SELECT * FROM org_tree;
```

### 4.2 多对多关系（通过中间表）

#### 关系1：用户 ↔ 机构（sys_user_org）

+ **中间表**：`sys_user_org`
+ **关联字段**：
  - `sys_user.user_num` = `sys_user_org.user_num`
  - `sys_org.org_code` = `sys_user_org.org_code`
+ **业务特点**：
  - 支持用户属于多个机构
  - 标识主机构（`is_primary=1`）
  - 支持时效性控制（`effective_start/end`）
+ **查询示例**：

```sql
-- 查询用户的所有机构（包含主机构标识）
SELECT uo.*, o.org_name
FROM sys_user_org uo
JOIN sys_org o ON uo.org_code = o.org_code
WHERE uo.user_num = 'USER001'
  AND uo.status = 1
  AND uo.is_deleted = 0
  AND (uo.effective_end IS NULL OR uo.effective_end > NOW())
ORDER BY uo.is_primary DESC;
```

#### 关系2：用户 ↔ 部门（sys_user_dept）

+ **中间表**：`sys_user_dept`
+ **业务特点**：
  - 支持用户属于多个部门
  - 部门必须在用户所属的机构下
  - 记录用户在部门的职位信息
+ **数据完整性规则**：

```sql
-- 用户部门的机构必须与用户机构匹配
SELECT COUNT(*) 
FROM sys_user_dept ud
JOIN sys_dept d ON ud.dept_code = d.dept_code
JOIN sys_user_org uo ON ud.user_num = uo.user_num
WHERE ud.user_num = 'USER001'
  AND d.org_code != uo.org_code;
-- 结果应为0
```

#### 关系3：用户 ↔ 角色（sys_user_role）

+ **中间表**：`sys_user_role`
+ **业务特点**：
  - 一个用户可以有多个角色
  - 一个角色可以分配给多个用户
  - 标识主角色（用于默认数据范围）
  - 支持角色有效期控制
+ **权限计算逻辑**：

```sql
-- 获取用户当前有效的所有角色
SELECT r.*, ur.is_primary
FROM sys_user_role ur
JOIN sys_role r ON ur.role_code = r.role_code
WHERE ur.user_num = 'USER001'
  AND ur.status = 1
  AND r.status = 1
  AND ur.is_deleted = 0
  AND r.is_deleted = 0
  AND (ur.effective_end IS NULL OR ur.effective_end > NOW())
ORDER BY ur.is_primary DESC, r.sort_order;
```

#### 关系4：角色 ↔ 机构数据范围（sys_role_org）

+ **中间表**：`sys_role_org`
+ **业务特点**：
  - 定义角色可以操作哪些机构的数据
  - 支持范围类型：本机构、包含下级机构
  - 支持不同的权限类型：管理、查看、操作
+ **数据范围计算示例**：

```sql
-- 查询角色R001可以管理的所有机构
SELECT 
    ro.org_code,
    ro.org_range_type,
    CASE 
        WHEN ro.org_range_type = 1 THEN ro.org_code  -- 仅本机构
        WHEN ro.org_range_type = 2 THEN (           -- 包含下级机构
            SELECT GROUP_CONCAT(org_code)
            FROM sys_org
            WHERE org_code = ro.org_code 
               OR parent_org_code = ro.org_code
        )
    END as accessible_orgs
FROM sys_role_org ro
WHERE ro.role_code = 'R001'
  AND ro.perm_type = 1  -- 管理权限
  AND ro.status = 1
  AND (ro.effective_end IS NULL OR ro.effective_end > NOW());
```

### 4.3 一对一关系

#### 关系1：用户 ↔ 主机构/主部门

+ **关系描述**：用户在`sys_user`表中记录主机构和主部门
+ **关联字段**：
  - `sys_user.org_code` = `sys_org.org_code`
  - `sys_user.dept_code` = `sys_dept.dept_code`
+ **业务规则**：
  - 主机构必须在`sys_user_org`中存在且`is_primary=1`
  - 主部门必须在`sys_user_dept`中存在且`is_primary=1`
  - 主部门必须属于主机构

---

## 五、业务场景映射

### 5.1 核心业务流程

#### 流程1：用户登录与权限加载

![](https://cdn.nlark.com/yuque/__mermaid_v3/ba81e87aa475b6c2b5f59a1c90de7178.svg)

**涉及表操作序列：**

```sql
-- 1. 验证用户基础信息
SELECT * FROM sys_user 
WHERE user_num = 'USER001' 
  AND status = 1 
  AND is_locked = 0;

-- 2. 加载用户机构信息
SELECT org_code, is_primary, position 
FROM sys_user_org 
WHERE user_num = 'USER001' 
  AND status = 1 
  AND (effective_end IS NULL OR effective_end > NOW());

-- 3. 加载用户角色信息（包含角色类型和数据范围）
SELECT ur.*, r.role_type, r.data_scope_type 
FROM sys_user_role ur
JOIN sys_role r ON ur.role_code = r.role_code
WHERE ur.user_num = 'USER001'
  AND ur.status = 1 
  AND r.status = 1
  AND (ur.effective_end IS NULL OR ur.effective_end > NOW());

-- 4. 加载角色的机构数据范围
SELECT * FROM sys_role_org 
WHERE role_code IN (用户角色列表)
  AND status = 1
  AND (effective_end IS NULL OR effective_end > NOW());
```

#### 流程2：数据权限过滤（查询用户列表）

**业务需求**：用户只能看到自己权限范围内的用户数据

**权限判断逻辑：**

```sql
-- 根据用户角色计算可访问的机构范围
WITH user_accessible_orgs AS (
    -- 用户直接关联的机构
    SELECT DISTINCT org_code 
    FROM sys_user_org 
    WHERE user_num = '当前用户'
      AND status = 1

    UNION

    -- 通过角色数据范围获得的机构
    SELECT DISTINCT 
        CASE 
            WHEN ro.org_range_type = 1 THEN ro.org_code
            WHEN ro.org_range_type = 2 THEN (
                SELECT org_code FROM sys_org 
                WHERE org_code = ro.org_code OR parent_org_code = ro.org_code
            )
        END as org_code
    FROM sys_user_role ur
    JOIN sys_role_org ro ON ur.role_code = ro.role_code
    WHERE ur.user_num = '当前用户'
      AND ur.status = 1
      AND ro.status = 1
      AND ro.perm_type IN (1, 2)  -- 管理或查看权限
      AND (ro.effective_end IS NULL OR ro.effective_end > NOW())
)
-- 查询可访问的用户
SELECT DISTINCT u.* 
FROM sys_user u
WHERE EXISTS (
    SELECT 1 FROM sys_user_org uo
    WHERE uo.user_num = u.user_num
      AND uo.org_code IN (SELECT org_code FROM user_accessible_orgs)
      AND uo.status = 1
);
```

### 5.2 常用查询场景

| 场景       | 查询目的       | 涉及表         | 复杂度 | 使用频率 |
| -------- | ---------- | ----------- | --- | ---- |
| 用户登录验证   | 验证身份和状态    | sys_user    | 简单  | 极高   |
| 获取用户完整信息 | 展示用户详情     | 用户+机构+部门+角色 | 复杂  | 高    |
| 机构树形展示   | 展示组织架构     | sys_org（递归） | 中等  | 中    |
| 权限校验     | 判断用户是否有某权限 | 用户角色+角色权限   | 中等  | 高    |
| 数据范围过滤   | 查询可见数据     | 多表关联        | 复杂  | 高    |

---

## 六、数据字典

### 6.1 状态码枚举

#### 通用状态

```markdown
| 值 | 名称 | 说明 |
|----|------|------|
| 0 | 禁用 | 记录不可用 |
| 1 | 启用 | 记录正常可用 |
```

#### 用户状态（sys_user.status）

```markdown
| 值 | 名称 | 说明 | 允许操作 |
|----|------|------|----------|
| 0 | 禁用 | 账户被管理员禁用 | 无 |
| 1 | 启用 | 正常使用状态 | 所有 |
```

#### 角色类型（sys_role.role_type）

```markdown
| 值 | 名称 | 说明 | 示例 |
|----|------|------|------|
| 1 | 系统角色 | 系统内置角色 | 超级管理员 |
| 2 | 业务角色 | 业务相关角色 | 财务主管 |
| 3 | 自定义角色 | 用户自定义角色 | 项目组长 |
```

#### 数据范围类型（sys_role.data_scope_type）

```markdown
| 值 | 名称 | 说明 | 计算逻辑 |
|----|------|------|----------|
| 1 | 全部数据 | 无限制 | 不过滤 |
| 2 | 本机构 | 仅所在机构 | org_code = 用户机构 |
| 3 | 本部门 | 仅所在部门 | dept_code = 用户部门 |
| 4 | 本人 | 仅自己的数据 | user_num = 当前用户 |
| 5 | 自定义 | 按sys_role_org配置 | 动态计算 |
```

#### 机构范围类型（sys_role_org.org_range_type）

```markdown
| 值 | 名称 | 说明 |
|----|------|------|
| 1 | 本机构 | 仅指定的机构 |
| 2 | 包含下级机构 | 指定机构及其所有子机构 |
```

#### 权限类型（sys_role_org.perm_type）

```markdown
| 值 | 名称 | 说明 |
|----|------|------|
| 1 | 管理权限 | 增删改查全部权限 |
| 2 | 查看权限 | 仅查看权限 |
| 3 | 操作权限 | 特定操作权限 |
```

### 6.2 关键字段约束

| 字段            | 表名           | 约束类型    | 约束条件                | 业务意义   |
| ------------- | ------------ | ------- | ------------------- | ------ |
| user_num      | sys_user     | 唯一索引    | 不能重复                | 用户唯一标识 |
| org_code      | sys_user_org | 外键+唯一组合 | 用户+机构唯一             | 防止重复关联 |
| is_primary    | sys_user_org | 业务约束    | 每个用户只能有一个主机构        | 确定主归属  |
| effective_end | 所有中间表        | 业务约束    | 必须大于effective_start | 时间有效性  |

---

## 七、性能与优化

### 7.1 索引策略分析

| 表名           | 索引字段                             | 索引类型 | 使用场景     | 建议  |
| ------------ | -------------------------------- | ---- | -------- | --- |
| sys_user     | user_num                         | 唯一索引 | 用户登录、查询  | 已优化 |
| sys_user     | org_code, dept_code              | 普通索引 | 按组织查询用户  | 已优化 |
| sys_user_org | (user_num, org_code)             | 唯一索引 | 用户机构关系查询 | 已优化 |
| sys_user_org | effective_start, effective_end   | 联合索引 | 查询有效关系   | 已优化 |
| sys_role_org | (role_code, org_code, perm_type) | 唯一索引 | 角色权限查询   | 已优化 |

### 7.2 查询优化建议

#### 建议1：权限数据缓存

```java
// 用户登录时将权限信息缓存，避免频繁查询
@Cacheable(value = "userPermissions", key = "#userNum")
public UserPermissions loadPermissions(String userNum) {
    // 一次性查询所有权限相关数据
    // 缓存有效期：30分钟
}
```

#### 建议2：大数据量分页优化

```sql
-- 传统分页（大数据量性能差）
SELECT * FROM sys_user LIMIT 100000, 20;

-- 优化分页（使用覆盖索引）
SELECT u.* FROM sys_user u
JOIN (
    SELECT id FROM sys_user 
    WHERE status = 1
    ORDER BY created_time DESC
    LIMIT 100000, 20
) AS tmp ON u.id = tmp.id;
```

#### 建议3：定期清理过期数据

```sql
-- 清理过期的用户机构关系
UPDATE sys_user_org 
SET status = 0 
WHERE effective_end < NOW() 
  AND status = 1;

-- 建立归档表，转移历史数据
INSERT INTO sys_user_org_history 
SELECT * FROM sys_user_org 
WHERE effective_end < DATE_SUB(NOW(), INTERVAL 1 YEAR);
DELETE FROM sys_user_org 
WHERE effective_end < DATE_SUB(NOW(), INTERVAL 1 YEAR);
```

### 7.3 数据一致性维护

#### 触发器示例：维护主机构一致性

```sql
-- 确保sys_user.org_code与sys_user_org.is_primary一致
DELIMITER //
CREATE TRIGGER trg_sync_primary_org
AFTER UPDATE ON sys_user_org
FOR EACH ROW
BEGIN
    IF NEW.is_primary = 1 AND OLD.is_primary = 0 THEN
        -- 更新用户表的主机构
        UPDATE sys_user 
        SET org_code = NEW.org_code,
            updated_by = NEW.updated_by,
            updated_time = NOW()
        WHERE user_num = NEW.user_num;

        -- 将其他机构设为非主机构
        UPDATE sys_user_org
        SET is_primary = 0,
            updated_by = NEW.updated_by,
            updated_time = NOW()
        WHERE user_num = NEW.user_num
          AND org_code != NEW.org_code
          AND is_primary = 1;
    END IF;
END//
DELIMITER ;
```

---

## 八、设计原则与规范

### 8.1 命名规范

+ **表名**：`sys_`前缀表示系统表，`业务模块_实体名`，如`sys_user_org`
+ **字段名**：
  - 编码字段：`xxx_code`，如`user_num`、`org_code`
  - 状态字段：`status`、`is_xxx`
  - 时间字段：`xxx_time`，如`created_time`、`effective_start`
+ **索引名**：
  - 唯一索引：`uk_字段名`，如`uk_user_num`
  - 普通索引：`idx_字段名`，如`idx_org_code`

### 8.2 设计原则

1. **业务主键原则**：使用有意义的业务编码作为主键（如`user_num`）
2. **软删除原则**：使用`is_deleted`标记删除，保留历史数据
3. **时效性原则**：关联关系支持有效期控制
4. **审计追踪原则**：记录`created_by`、`updated_by`等操作人信息
5. **数据冗余原则**：中间表冗余名称字段（如`org_name`），减少关联查询

### 8.3 扩展性考虑

1. **多租户支持**：可通过添加`tenant_id`字段支持多租户
2. **国际化支持**：机构/部门名称可扩展多语言字段
3. **权限分级**：已支持机构、部门、个人三级数据范围
4. **角色继承**：可通过扩展`parent_role_code`支持角色继承

---

## 九、常见问题解答（FAQ）

### Q1：如何查询用户的所有有效权限？

```sql
-- 包括机构、部门、角色和数据范围
SELECT 
    u.user_num,
    u.user_name,
    -- 用户机构
    GROUP_CONCAT(DISTINCT CONCAT(uo.org_code, '(', 
        CASE uo.is_primary WHEN 1 THEN '主' ELSE '辅' END, ')')
    ) as user_orgs,
    -- 用户部门
    GROUP_CONCAT(DISTINCT CONCAT(ud.dept_code, '(', 
        CASE ud.is_primary WHEN 1 THEN '主' ELSE '辅' END, ')')
    ) as user_depts,
    -- 用户角色
    GROUP_CONCAT(DISTINCT ur.role_code) as user_roles,
    -- 角色数据范围
    GROUP_CONCAT(DISTINCT CONCAT(ro.org_code, 
        CASE ro.org_range_type WHEN 2 THEN '(含下级)' ELSE '' END)
    ) as role_org_scopes
FROM sys_user u
LEFT JOIN sys_user_org uo ON u.user_num = uo.user_num 
    AND uo.status = 1 AND (uo.effective_end IS NULL OR uo.effective_end > NOW())
LEFT JOIN sys_user_dept ud ON u.user_num = ud.user_num 
    AND ud.status = 1 AND (ud.effective_end IS NULL OR ud.effective_end > NOW())
LEFT JOIN sys_user_role ur ON u.user_num = ur.user_num 
    AND ur.status = 1 AND (ur.effective_end IS NULL OR ur.effective_end > NOW())
LEFT JOIN sys_role_org ro ON ur.role_code = ro.role_code 
    AND ro.status = 1 AND (ro.effective_end IS NULL OR ro.effective_end > NOW())
WHERE u.user_num = 'USER001'
GROUP BY u.user_num, u.user_name;
```

### Q2：新员工入职，如何分配权限？

**操作步骤：**

1. 创建用户记录（`sys_user`）
2. 分配主机构（`sys_user_org`，`is_primary=1`）
3. 分配主部门（`sys_user_dept`，`is_primary=1`）
4. 分配角色（`sys_user_role`）
5. 设置有效期（如试用期3个月）

**SQL示例：**

```sql
-- 1. 创建用户
INSERT INTO sys_user (user_num, user_name, password, org_code, dept_code, created_by)
VALUES ('NEW001', '新员工', '加密密码', 'ORG001', 'DEPT001', 'ADMIN001');

-- 2. 分配主机构（有效期3个月）
INSERT INTO sys_user_org (user_num, org_code, is_primary, effective_start, effective_end, created_by)
VALUES ('NEW001', 'ORG001', 1, NOW(), DATE_ADD(NOW(), INTERVAL 3 MONTH), 'ADMIN001');

-- 3. 分配角色
INSERT INTO sys_user_role (user_num, role_code, is_primary, effective_start, effective_end, created_by)
VALUES ('NEW001', 'R002', 1, NOW(), DATE_ADD(NOW(), INTERVAL 3 MONTH), 'ADMIN001');
```

### Q3：用户调岗如何处理？

**场景**：用户从A部门调到B部门

**处理方案：**

```sql
-- 1. 原部门关系结束
UPDATE sys_user_dept 
SET effective_end = NOW(),
    status = 0,
    updated_by = 'ADMIN001',
    updated_time = NOW()
WHERE user_num = 'USER001'
  AND dept_code = 'DEPT001'
  AND status = 1
  AND (effective_end IS NULL OR effective_end > NOW());

-- 2. 新部门关系开始
INSERT INTO sys_user_dept (user_num, dept_code, is_primary, effective_start, created_by)
VALUES ('USER001', 'DEPT002', 1, NOW(), 'ADMIN001');

-- 3. 更新用户表主部门
UPDATE sys_user 
SET dept_code = 'DEPT002',
    updated_by = 'ADMIN001',
    updated_time = NOW()
WHERE user_num = 'USER001';
```

---

## 

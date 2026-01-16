# 修改AuthServiceImpl.java的checkOrgAccess方法执行计划

## 1. 核心需求

* 按照数据格式文档修改`checkOrgAccess`方法的返回结构

* 补充`menuPermissions`字段

* 保持代码结构的稳定性，只修改必要部分

## 2. 执行步骤

### 2.1 分析当前返回结构与预期结构的差异

当前返回结构包含：

* `user`：用户基本信息

* `currentOrg`：当前机构

* `currentDept`：当前部门

* `availableOrgs`：可用机构列表

* `availableDepts`：可用部门列表

* `permissions`：统一权限结构（包含菜单、按钮、数据、字段、时间权限等）

* `authorization`：简化授权信息

预期返回结构需要：

* `user`：用户基本信息（保留）

* `currentOrgDepts`：当前机构部门信息（新增，整合当前机构和部门）

* `allPermissions`：所有权限（保留）

* `userPermissions`：直接授权（新增，当前为空列表）

* `rolePermissions`：角色权限（新增）

* `pagePermissions`：页面权限（新增）

* `dataPermissions`：数据权限（保留，调整格式）

* `fieldPermissions`：字段权限（保留）

* `apiPermissions`：API权限（新增）

* `bizPermissions`：业务权限（新增）

* `timePermissions`：时间权限（保留）

### 2.2 实现menuPermissions字段

1. **添加menuPermissions字段到返回结构**
2. **实现从sys\_menu表获取菜单权限的逻辑**
3. **结合用户角色关联的权限进行过滤**
4. **按照预期格式组装菜单权限数据**

### 2.3 调整返回结构

1. **保留user字段**
2. **新增currentOrgDepts字段，整合当前机构和部门信息**
3. **保留allPermissions字段**
4. **新增userPermissions字段（当前为空列表）**
5. **新增rolePermissions字段**
6. **新增pagePermissions字段**
7. **调整dataPermissions字段格式**
8. **保留fieldPermissions字段**
9. **新增apiPermissions字段**
10. **新增bizPermissions字段（当前为空列表）**
11. **保留timePermissions字段**
12. **移除冗余字段：currentOrg、currentDept、availableOrgs、availableDepts、permissions嵌套结构、authorization**

### 2.4 具体代码修改

1. **修改checkOrgAccess方法的返回结构**

   * 调整result Map的构建逻辑

   * 按照预期格式组装每个字段

2. **实现menuPermissions获取逻辑**

   * 从sys\_menu表获取菜单数据

   * 结合用户角色关联的权限过滤

   * 组装菜单权限数据

3. **实现currentOrgDepts组装逻辑**

   * 整合orgInfo和userDeptsInOrg数据

   * 按照预期格式组装

4. **调整其他权限字段的组装逻辑**

   * rolePermissions

   * pagePermissions

   * dataPermissions

   * apiPermissions

   * bizPermissions

## 3. 预期效果

修改后的`checkOrgAccess`方法将返回符合数据格式文档要求的JSON结构，删除无用代码，重构新方法包含所有必要的字段，特别是补充了`menuPermissions`字段，并且结构清晰，易于前端使用。

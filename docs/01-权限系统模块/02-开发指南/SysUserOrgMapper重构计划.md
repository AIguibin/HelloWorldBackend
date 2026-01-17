# SysUserOrgMapper重构计划

## 问题分析

1. **方法重复问题**：

   * `selectOrgsByUserNum` 与 `selectAccessibleOrgsByUserNum` 方法功能完全相同，返回机构基本信息列表

   * `selectExtOrgCodesByUserNum` 与 `selectAccessibleOrgCodesByUserNum` 方法功能完全相同，返回机构编码列表

2. **缺少必要方法**：

   * 缺少直接返回SysUserOrg实体对象的查询方法

   * 缺少sys\_user\_org与sys\_org表关联查询的方法

## 重构方案

### 1. 移除重复方法

* 删除 `selectOrgsByUserNum` 方法（与 `selectAccessibleOrgsByUserNum` 重复）

* 删除 `selectExtOrgCodesByUserNum` 方法（与 `selectAccessibleOrgCodesByUserNum` 重复）

### 2. 新增查询方法

#### 方法1：查询sys\_user\_org实体列表

```java
/**
 * 通过用户编号查询sys_user_org表记录列表
 * @param userNum 用户编号
 * @return SysUserOrg实体对象集合
 */
@Select("SELECT * FROM sys_user_org WHERE user_num = #{userNum} AND status = 1 AND is_deleted = 0")
List<SysUserOrg> selectUserOrgEntitiesByUserNum(@Param("userNum") String userNum);
```

#### 方法2：查询用户所属机构详细信息（关联sys\_org表）

```java
/**
 * 通过用户编号查询用户所属机构详细信息（关联sys_org表）
 * 主要返回sys_org的信息以及sys_user_org的position(用户在机构中的职位)
 * @param userNum 用户编号
 * @return 包含机构详细信息和用户职位的结果集
 */
@Select("SELECT uo.org_code, uo.user_num, uo.position, uo.is_primary, uo.effective_start, uo.effective_end, " +
        "o.org_name, o.parent_org_code, o.org_level, o.org_sort_order, o.org_description " +
        "FROM sys_user_org uo " +
        "LEFT JOIN sys_org o ON uo.org_code = o.org_code " +
        "WHERE uo.user_num = #{userNum} AND uo.status = 1 AND uo.is_deleted = 0 AND o.is_deleted = 0")
List<Map<String, Object>> selectUserOrgDetailsByUserNum(@Param("userNum") String userNum);
```

### 3. 优化现有方法

* 确保所有保留方法命名规范，具有明确业务含义

* 为所有方法添加完整JavaDoc注释

## 预期效果

1. 消除方法重复，提高代码可维护性
2. 提供直接返回实体对象的查询方法，方便上层业务使用
3. 实现关联查询，获取完整的机构详细信息和用户职位
4. 方法命名规范，注释完整，便于理解和使用

## 执行步骤

1. 删除重复方法
2. 添加两个新查询方法
3. 检查SQL语句正确性
4. 添加完整JavaDoc注释
5. 编译验证重构结果


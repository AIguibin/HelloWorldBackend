-- ============================================
-- SYS系统初始化数据脚本
-- 创建人：系统初始化
-- ============================================

-- 禁用外键检查，便于按顺序插入
SET FOREIGN_KEY_CHECKS = 0;

-- ============================================
-- 1. 机构信息表初始化
-- ============================================
INSERT INTO `sys_org` (`uuid`, `id`, `org_code`, `org_name`, `parent_org_code`, `level`, `sort_order`, `status`, `description`, `created_by`, `created_time`, `updated_by`, `updated_time`, `is_deleted`) VALUES
-- 顶级机构
('4a3b2c1d5e6f7a8b9c0d1e2f3a4b5c6d', 1, 'JG00000001', '系统管理中心', NULL, 1, 10, 1, '负责整个系统的管理与维护', 'system', NOW(), 'system', NOW(), 0),
('5b4c3d2e1f0a9b8c7d6e5f4a3b2c1d0e', 2, 'JG00000002', '产品中心', NULL, 1, 20, 1, '负责产品规划与设计', 'system', NOW(), 'system', NOW(), 0),
('6c5d4e3f2a1b0c9d8e7f6a5b4c3d2e1f', 3, 'JG00000003', '需求中心', NULL, 1, 30, 1, '负责需求收集与分析', 'system', NOW(), 'system', NOW(), 0),
('7d6e5f4a3b2c1d0e9f8a7b6c5d4e3f2a', 4, 'JG00000004', '开发中心', NULL, 1, 40, 1, '负责系统开发与实现', 'system', NOW(), 'system', NOW(), 0),
('8e7f6a5b4c3d2e1f0a9b8c7d6e5f4a3b', 5, 'JG00000005', '测试中心', NULL, 1, 50, 1, '负责系统测试与质量保障', 'system', NOW(), 'system', NOW(), 0);

-- ============================================
-- 2. 部门信息表初始化
-- ============================================
INSERT INTO `sys_dept` (`uuid`, `id`, `dept_code`, `dept_name`, `org_code`, `manager_num`, `sort_order`, `status`, `description`, `created_by`, `created_time`, `updated_by`, `updated_time`, `is_deleted`) VALUES
-- 系统管理中心的部门
('9f8a7b6c5d4e3f2a1b0c9d8e7f6a5b4c', 1, 'JG00000001D0001', '研发中心', 'JG00000001', 'aiguibin', 10, 1, '系统管理中心下属研发部门', 'system', NOW(), 'system', NOW(), 0),
('0a9b8c7d6e5f4a3b2c1d0e9f8a7b6c5d', 2, 'JG00000001D0002', '管理办公室', 'JG00000001', 'admin', 20, 1, '系统管理中心下属管理部门', 'system', NOW(), 'system', NOW(), 0),
-- 产品中心的部门
('1b0c9d8e7f6a5b4c3d2e1f0a9b8c7d6e', 3, 'JG00000002D0001', '产品部', 'JG00000002', 'zhangsan', 10, 1, '产品中心下属产品部门', 'system', NOW(), 'system', NOW(), 0),
-- 开发中心的部门
('2c1d0e9f8a7b6c5d4e3f2a1b0c9d8e7f', 4, 'JG00000004D0001', '研发部', 'JG00000004', 'wangwu', 10, 1, '开发中心下属研发部门', 'system', NOW(), 'system', NOW(), 0),
-- 测试中心的部门
('3d2e1f0a9b8c7d6e5f4a3b2c1d0e9f8a', 5, 'JG00000005D0001', '测试部', 'JG00000005', 'zhaoliu', 10, 1, '测试中心下属测试部门', 'system', NOW(), 'system', NOW(), 0);

-- ============================================
-- 3. 用户信息表初始化
-- ============================================
-- 注意：密码采用BCrypt加密，示例密码为'123456'的加密值，实际使用时请替换
INSERT INTO `sys_user` (`uuid`, `id`, `user_num`, `user_name`, `nickname`, `gender`, `password`, `salt`, `org_code`, `dept_code`, `email`, `phone`, `avatar`, `last_login_time`, `last_login_ip`, `login_count`, `status`, `is_locked`, `lock_time`, `lock_reason`, `is_special`, `pwd_expire_time`, `pwd_modified_time`, `created_by`, `created_time`, `updated_by`, `updated_time`, `is_deleted`) VALUES
-- 超级管理员
('4e3f2a1b0c9d8e7f6a5b4c3d2e1f0a9b', 1, 'aiguibin', '超级管理员', 'AI桂斌', 1, '$2a$10$EZZYkHpzQNQh/CrJpQ6j6uK7M3xVvV8xYp5zLbM9nXqJkLmNpOqRrSsTtUuVv', 'salt123', 'JG00000001', 'JG00000001D0001', 'aiguibin@company.com', '13800138000', '/avatar/admin.png', NOW(), '127.0.0.1', 1, 1, 0, NULL, NULL, 1, DATE_ADD(NOW(), INTERVAL 90 DAY), NOW(), 'system', NOW(), 'system', NOW(), 0),
-- 系统管理员
('5f4a3b2c1d0e9f8a7b6c5d4e3f2a1b0c', 2, 'system', '系统管理员', '系统管理员', 0, '$2a$10$EZZYkHpzQNQh/CrJpQ6j6uK7M3xVvV8xYp5zLbM9nXqJkLmNpOqRrSsTtUuVv', 'salt456', 'JG00000001', 'JG00000001D0001', 'system@company.com', '13800138001', '/avatar/system.png', NULL, NULL, 0, 1, 0, NULL, NULL, 0, DATE_ADD(NOW(), INTERVAL 90 DAY), NOW(), 'system', NOW(), 'system', NOW(), 0),
-- 管理负责人
('6a5b4c3d2e1f0a9b8c7d6e5f4a3b2c1d', 3, 'admin', '管理负责人', '管理员', 1, '$2a$10$EZZYkHpzQNQh/CrJpQ6j6uK7M3xVvV8xYp5zLbM9nXqJkLmNpOqRrSsTtUuVv', 'salt789', 'JG00000001', 'JG00000001D0002', 'admin@company.com', '13800138002', '/avatar/manager.png', NULL, NULL, 0, 1, 0, NULL, NULL, 0, DATE_ADD(NOW(), INTERVAL 90 DAY), NOW(), 'system', NOW(), 'system', NOW(), 0),
-- 产品中心 - 张三
('7b6c5d4e3f2a1b0c9d8e7f6a5b4c3d2e', 4, 'zhangsan', '张三', '张三', 1, '$2a$10$EZZYkHpzQNQh/CrJpQ6j6uK7M3xVvV8xYp5zLbM9nXqJkLmNpOqRrSsTtUuVv', 'salt101', 'JG00000002', 'JG00000002D0001', 'zhangsan@company.com', '13800138003', '/avatar/user1.png', NULL, NULL, 0, 1, 0, NULL, NULL, 0, DATE_ADD(NOW(), INTERVAL 90 DAY), NOW(), 'system', NOW(), 'system', NOW(), 0),
-- 需求中心 - 李四
('8c7d6e5f4a3b2c1d0e9f8a7b6c5d4e3f', 5, 'lisi', '李四', '李四', 1, '$2a$10$EZZYkHpzQNQh/CrJpQ6j6uK7M3xVvV8xYp5zLbM9nXqJkLmNpOqRrSsTtUuVv', 'salt102', 'JG00000003', NULL, 'lisi@company.com', '13800138004', '/avatar/user2.png', NULL, NULL, 0, 1, 0, NULL, NULL, 0, DATE_ADD(NOW(), INTERVAL 90 DAY), NOW(), 'system', NOW(), 'system', NOW(), 0),
-- 开发中心 - 王五
('9d8e7f6a5b4c3d2e1f0a9b8c7d6e5f4a', 6, 'wangwu', '王五', '王五', 1, '$2a$10$EZZYkHpzQNQh/CrJpQ6j6uK7M3xVvV8xYp5zLbM9nXqJkLmNpOqRrSsTtUuVv', 'salt103', 'JG00000004', 'JG00000004D0001', 'wangwu@company.com', '13800138005', '/avatar/user3.png', NULL, NULL, 0, 1, 0, NULL, NULL, 0, DATE_ADD(NOW(), INTERVAL 90 DAY), NOW(), 'system', NOW(), 'system', NOW(), 0),
-- 测试中心 - 赵六
('0e9f8a7b6c5d4e3f2a1b0c9d8e7f6a5b', 7, 'zhaoliu', '赵六', '赵六', 1, '$2a$10$EZZYkHpzQNQh/CrJpQ6j6uK7M3xVvV8xYp5zLbM9nXqJkLmNpOqRrSsTtUuVv', 'salt104', 'JG00000005', 'JG00000005D0001', 'zhaoliu@company.com', '13800138006', '/avatar/user4.png', NULL, NULL, 0, 1, 0, NULL, NULL, 0, DATE_ADD(NOW(), INTERVAL 90 DAY), NOW(), 'system', NOW(), 'system', NOW(), 0);

-- ============================================
-- 4. 用户机构扩展表初始化
-- ============================================
INSERT INTO `sys_user_org` (`uuid`, `id`, `user_num`, `org_code`, `org_name`, `is_primary`, `position`, `effective_start`, `effective_end`, `status`, `description`, `created_by`, `created_time`, `updated_by`, `updated_time`, `is_deleted`) VALUES
-- 超级管理员 - 系统管理中心（主机构）
('1f0a9b8c7d6e5f4a3b2c1d0e9f8a7b6c', 1, 'aiguibin', 'JG00000001', '系统管理中心', 1, '超级管理员', NOW(), DATE_ADD(NOW(), INTERVAL 3650 DAY), 1, '超级管理员主机构', 'system', NOW(), 'system', NOW(), 0),
-- 系统管理员 - 系统管理中心（主机构）
('2a1b0c9d8e7f6a5b4c3d2e1f0a9b8c7d', 2, 'system', 'JG00000001', '系统管理中心', 1, '系统管理员', NOW(), DATE_ADD(NOW(), INTERVAL 3650 DAY), 1, '系统管理员主机构', 'system', NOW(), 'system', NOW(), 0),
-- 管理负责人 - 系统管理中心（主机构）
('3b2c1d0e9f8a7b6c5d4e3f2a1b0c9d8e', 3, 'admin', 'JG00000001', '系统管理中心', 1, '管理负责人', NOW(), DATE_ADD(NOW(), INTERVAL 3650 DAY), 1, '管理负责人主机构', 'system', NOW(), 'system', NOW(), 0),
-- 张三 - 产品中心（主机构）
('4c3d2e1f0a9b8c7d6e5f4a3b2c1d0e9f', 4, 'zhangsan', 'JG00000002', '产品中心', 1, '产品经理', NOW(), DATE_ADD(NOW(), INTERVAL 3650 DAY), 1, '产品经理主机构', 'system', NOW(), 'system', NOW(), 0),
-- 李四 - 需求中心（主机构）
('5d4e3f2a1b0c9d8e7f6a5b4c3d2e1f0a', 5, 'lisi', 'JG00000003', '需求中心', 1, '需求经理', NOW(), DATE_ADD(NOW(), INTERVAL 3650 DAY), 1, '需求经理主机构', 'system', NOW(), 'system', NOW(), 0),
-- 王五 - 开发中心（主机构）
('6e5f4a3b2c1d0e9f8a7b6c5d4e3f2a1b', 6, 'wangwu', 'JG00000004', '开发中心', 1, '高级开发', NOW(), DATE_ADD(NOW(), INTERVAL 3650 DAY), 1, '开发人员主机构', 'system', NOW(), 'system', NOW(), 0),
-- 赵六 - 测试中心（主机构）
('7f6a5b4c3d2e1f0a9b8c7d6e5f4a3b2c', 7, 'zhaoliu', 'JG00000005', '测试中心', 1, '测试经理', NOW(), DATE_ADD(NOW(), INTERVAL 3650 DAY), 1, '测试人员主机构', 'system', NOW(), 'system', NOW(), 0);

-- ============================================
-- 5. 用户部门扩展表初始化
-- ============================================
INSERT INTO `sys_user_dept` (`uuid`, `id`, `user_num`, `dept_code`, `dept_name`, `is_primary`, `position`, `effective_start`, `effective_end`, `status`, `description`, `created_by`, `created_time`, `updated_by`, `updated_time`, `is_deleted`) VALUES
-- 超级管理员 - 研发中心（主部门）
('8a7b6c5d4e3f2a1b0c9d8e7f6a5b4c3d', 1, 'aiguibin', 'JG00000001D0001', '研发中心', 1, '超级管理员', NOW(), DATE_ADD(NOW(), INTERVAL 3650 DAY), 1, '超级管理员主部门', 'system', NOW(), 'system', NOW(), 0),
-- 系统管理员 - 研发中心（主部门）
('9b8c7d6e5f4a3b2c1d0e9f8a7b6c5d4e', 2, 'system', 'JG00000001D0001', '研发中心', 1, '系统管理员', NOW(), DATE_ADD(NOW(), INTERVAL 3650 DAY), 1, '系统管理员主部门', 'system', NOW(), 'system', NOW(), 0),
-- 管理负责人 - 管理办公室（主部门）
('0c9d8e7f6a5b4c3d2e1f0a9b8c7d6e5f', 3, 'admin', 'JG00000001D0002', '管理办公室', 1, '管理负责人', NOW(), DATE_ADD(NOW(), INTERVAL 3650 DAY), 1, '管理负责人主部门', 'system', NOW(), 'system', NOW(), 0),
-- 张三 - 产品部（主部门）
('1d0e9f8a7b6c5d4e3f2a1b0c9d8e7f6a', 4, 'zhangsan', 'JG00000002D0001', '产品部', 1, '产品经理', NOW(), DATE_ADD(NOW(), INTERVAL 3650 DAY), 1, '产品经理主部门', 'system', NOW(), 'system', NOW(), 0),
-- 李四 - 需求中心无部门，暂不分配
-- 王五 - 研发部（主部门）
('2e1f0a9b8c7d6e5f4a3b2c1d0e9f8a7b', 5, 'wangwu', 'JG00000004D0001', '研发部', 1, '高级开发', NOW(), DATE_ADD(NOW(), INTERVAL 3650 DAY), 1, '开发人员主部门', 'system', NOW(), 'system', NOW(), 0),
-- 赵六 - 测试部（主部门）
('3f2a1b0c9d8e7f6a5b4c3d2e1f0a9b8c', 6, 'zhaoliu', 'JG00000005D0001', '测试部', 1, '测试经理', NOW(), DATE_ADD(NOW(), INTERVAL 3650 DAY), 1, '测试人员主部门', 'system', NOW(), 'system', NOW(), 0);

-- ============================================
-- 6. 角色信息表初始化
-- ============================================
INSERT INTO `sys_role` (`uuid`, `id`, `role_code`, `role_name`, `role_type`, `data_scope_type`, `sort_order`, `status`, `description`, `created_by`, `created_time`, `updated_by`, `updated_time`, `is_deleted`) VALUES
-- 系统角色
('4a3b2c1d5e6f7a8b9c0d1e2f3a4b5c6d', 1, 'RL000001', '超级管理员', 1, 1, 10, 1, '拥有系统所有权限，包括系统管理、用户管理、角色管理、权限分配等', 'system', NOW(), 'system', NOW(), 0),
('5b4c3d2e1f0a9b8c7d6e5f4a3b2c1d0e', 2, 'RL000002', '系统管理员', 1, 1, 20, 1, '负责系统日常维护和管理，包括用户管理、角色管理、权限配置等', 'system', NOW(), 'system', NOW(), 0),
('6c5d4e3f2a1b0c9d8e7f6a5b4c3d2e1f', 3, 'RL000003', '管理负责人', 1, 2, 30, 1, '负责本机构的管理工作，包括人员管理、资源分配等', 'system', NOW(), 'system', NOW(), 0),
-- 业务角色
('7d6e5f4a3b2c1d0e9f8a7b6c5d4e3f2a', 4, 'RL000004', '产品经理', 2, 3, 40, 1, '负责产品规划、需求分析、产品设计等工作', 'system', NOW(), 'system', NOW(), 0),
('8e7f6a5b4c3d2e1f0a9b8c7d6e5f4a3b', 5, 'RL000005', '需求经理', 2, 3, 50, 1, '负责需求收集、分析、整理和管理', 'system', NOW(), 'system', NOW(), 0),
('9f8a7b6c5d4e3f2a1b0c9d8e7f6a5b4c', 6, 'RL000006', '测试经理', 2, 3, 60, 1, '负责测试计划、测试执行、质量保障等工作', 'system', NOW(), 'system', NOW(), 0),
('0a9b8c7d6e5f4a3b2c1d0e9f8a7b6c5d', 7, 'RL000007', '项目总监', 2, 2, 70, 1, '负责项目整体规划、资源协调、进度控制等', 'system', NOW(), 'system', NOW(), 0),
('1b0c9d8e7f6a5b4c3d2e1f0a9b8c7d6e', 8, 'RL000008', '项目经理', 2, 3, 80, 1, '负责具体项目的执行和管理', 'system', NOW(), 'system', NOW(), 0),
('2c1d0e9f8a7b6c5d4e3f2a1b0c9d8e7f', 9, 'RL000009', '部门经理', 2, 3, 90, 1, '负责部门日常管理和团队建设', 'system', NOW(), 'system', NOW(), 0),
('3d2e1f0a9b8c7d6e5f4a3b2c1d0e9f8a', 10, 'RL000010', '项目大组长', 2, 3, 100, 1, '负责项目组的管理和技术指导', 'system', NOW(), 'system', NOW(), 0),
('4e3f2a1b0c9d8e7f6a5b4c3d2e1f0a9b', 11, 'RL000011', '项目小组长', 2, 3, 110, 1, '负责项目小组的具体工作安排', 'system', NOW(), 'system', NOW(), 0),
('5f4a3b2c1d0e9f8a7b6c5d4e3f2a1b0c', 12, 'RL000012', '架构师', 2, 3, 120, 1, '负责系统架构设计和技术选型', 'system', NOW(), 'system', NOW(), 0),
('6a5b4c3d2e1f0a9b8c7d6e5f4a3b2c1d', 13, 'RL000013', '高级开发', 2, 4, 130, 1, '负责核心功能开发和代码审核', 'system', NOW(), 'system', NOW(), 0),
('7b6c5d4e3f2a1b0c9d8e7f6a5b4c3d2e', 14, 'RL000014', '高级需求', 2, 4, 140, 1, '负责复杂需求分析和方案设计', 'system', NOW(), 'system', NOW(), 0),
('8c7d6e5f4a3b2c1d0e9f8a7b6c5d4e3f', 15, 'RL000015', '高级测试', 2, 4, 150, 1, '负责复杂测试场景设计和执行', 'system', NOW(), 'system', NOW(), 0),
('9d8e7f6a5b4c3d2e1f0a9b8c7d6e5f4a', 16, 'RL000016', '版本管理', 2, 4, 160, 1, '负责版本发布和配置管理', 'system', NOW(), 'system', NOW(), 0),
('0e9f8a7b6c5d4e3f2a1b0c9d8e7f6a5b', 17, 'RL000017', '正式访客', 3, 4, 170, 1, '正式环境只读访问权限', 'system', NOW(), 'system', NOW(), 0);

-- ============================================
-- 7. 用户角色关联表初始化
-- ============================================
INSERT INTO `sys_user_role` (`uuid`, `id`, `user_num`, `role_code`, `role_name`, `is_primary`, `effective_start`, `effective_end`, `status`, `description`, `created_by`, `created_time`, `updated_by`, `updated_time`, `is_deleted`) VALUES
-- 超级管理员 - 超级管理员角色（主角色）
('1f0a9b8c7d6e5f4a3b2c1d0e9f8a7b6c', 1, 'aiguibin', 'RL000001', '超级管理员', 1, NOW(), DATE_ADD(NOW(), INTERVAL 3650 DAY), 1, '超级管理员主角色', 'system', NOW(), 'system', NOW(), 0),
-- 系统管理员 - 系统管理员角色（主角色）
('2a1b0c9d8e7f6a5b4c3d2e1f0a9b8c7d', 2, 'system', 'RL000002', '系统管理员', 1, NOW(), DATE_ADD(NOW(), INTERVAL 3650 DAY), 1, '系统管理员主角色', 'system', NOW(), 'system', NOW(), 0),
-- 管理负责人 - 管理负责人角色（主角色）
('3b2c1d0e9f8a7b6c5d4e3f2a1b0c9d8e', 3, 'admin', 'RL000003', '管理负责人', 1, NOW(), DATE_ADD(NOW(), INTERVAL 3650 DAY), 1, '管理负责人主角色', 'system', NOW(), 'system', NOW(), 0),
-- 张三 - 产品经理角色（主角色）
('4c3d2e1f0a9b8c7d6e5f4a3b2c1d0e9f', 4, 'zhangsan', 'RL000004', '产品经理', 1, NOW(), DATE_ADD(NOW(), INTERVAL 3650 DAY), 1, '产品经理主角色', 'system', NOW(), 'system', NOW(), 0),
-- 李四 - 需求经理角色（主角色）
('5d4e3f2a1b0c9d8e7f6a5b4c3d2e1f0a', 5, 'lisi', 'RL000005', '需求经理', 1, NOW(), DATE_ADD(NOW(), INTERVAL 3650 DAY), 1, '需求经理主角色', 'system', NOW(), 'system', NOW(), 0),
-- 王五 - 高级开发角色（主角色）
('6e5f4a3b2c1d0e9f8a7b6c5d4e3f2a1b', 6, 'wangwu', 'RL000013', '高级开发', 1, NOW(), DATE_ADD(NOW(), INTERVAL 3650 DAY), 1, '高级开发主角色', 'system', NOW(), 'system', NOW(), 0),
-- 赵六 - 测试经理角色（主角色）
('7f6a5b4c3d2e1f0a9b8c7d6e5f4a3b2c', 7, 'zhaoliu', 'RL000006', '测试经理', 1, NOW(), DATE_ADD(NOW(), INTERVAL 3650 DAY), 1, '测试经理主角色', 'system', NOW(), 'system', NOW(), 0);

-- ============================================
-- 8. 菜单资源定义表初始化
-- ============================================
-- 注意：先插入父菜单，再插入子菜单，menu_type: M-目录，P-页面，C-组件，B-按钮
INSERT INTO `sys_menu` (`uuid`, `id`, `menu_code`, `menu_name`, `menu_type`, `parent_menu_code`, `icon`, `path`, `component`, `resource_key`, `resource_type`, `is_external`, `is_cache`, `is_visible`, `sort_order`, `status`, `description`, `created_by`, `created_time`, `updated_by`, `updated_time`, `is_deleted`) VALUES
-- 一级菜单：首页
('4a3b2c1d5e6f7a8b9c0d1e2f3a4b5c6d', 1, 'M00000000000000001', '首页', 'P', NULL, 'HomeOutlined', '/dashboard', '/dashboard/index', 'dashboard:index', 'PAGE', 0, 1, 1, 100, 1, '系统首页，展示工作台和统计信息', 'system', NOW(), 'system', NOW(), 0),

-- 一级菜单：我的工作（目录）
('5b4c3d2e1f0a9b8c7d6e5f4a3b2c1d0e', 2, 'M00000000000000002', '我的工作', 'M', NULL, 'DesktopOutlined', '/mywork', NULL, 'mywork:index', 'MENU', 0, 1, 1, 200, 1, '个人工作台，包含待办事项和申请管理', 'system', NOW(), 'system', NOW(), 0),
-- 二级菜单：待办事项
('6c5d4e3f2a1b0c9d8e7f6a5b4c3d2e1f', 3, 'P00000000000000001', '待办事项', 'P', 'M00000000000000002', 'ScheduleOutlined', '/mywork/todo', '/mywork/todo/index', 'mywork:todo:list', 'PAGE', 0, 1, 1, 210, 1, '个人待办任务列表', 'system', NOW(), 'system', NOW(), 0),
-- 二级菜单：变更申请
('7d6e5f4a3b2c1d0e9f8a7b6c5d4e3f2a', 4, 'P00000000000000002', '变更申请', 'P', 'M00000000000000002', 'FormOutlined', '/mywork/change', '/mywork/change/index', 'mywork:change:list', 'PAGE', 0, 1, 1, 220, 1, '变更申请管理', 'system', NOW(), 'system', NOW(), 0),
-- 二级菜单：部署申请
('8e7f6a5b4c3d2e1f0a9b8c7d6e5f4a3b', 5, 'P00000000000000003', '部署申请', 'P', 'M00000000000000002', 'CloudUploadOutlined', '/mywork/deploy', '/mywork/deploy/index', 'mywork:deploy:list', 'PAGE', 0, 1, 1, 230, 1, '部署申请管理', 'system', NOW(), 'system', NOW(), 0),

-- 一级菜单：环境管理（目录）
('9f8a7b6c5d4e3f2a1b0c9d8e7f6a5b4c', 6, 'M00000000000000003', '环境管理', 'M', NULL, 'EnvironmentOutlined', '/environment', NULL, 'environment:index', 'MENU', 0, 1, 1, 300, 1, '系统环境管理', 'system', NOW(), 'system', NOW(), 0),
-- 二级菜单：服务器管理
('0a9b8c7d6e5f4a3b2c1d0e9f8a7b6c5d', 7, 'P00000000000000004', '服务器管理', 'P', 'M00000000000000003', 'ServerOutlined', '/environment/server', '/environment/server/index', 'environment:server:list', 'PAGE', 0, 1, 1, 310, 1, '服务器资源管理', 'system', NOW(), 'system', NOW(), 0),
-- 二级菜单：数据库管理
('1b0c9d8e7f6a5b4c3d2e1f0a9b8c7d6e', 8, 'P00000000000000005', '数据库管理', 'P', 'M00000000000000003', 'DatabaseOutlined', '/environment/database', '/environment/database/index', 'environment:database:list', 'PAGE', 0, 1, 1, 320, 1, '数据库实例管理', 'system', NOW(), 'system', NOW(), 0),
-- 二级菜单：关联系统管理
('2c1d0e9f8a7b6c5d4e3f2a1b0c9d8e7f', 9, 'P00000000000000006', '关联系统管理', 'P', 'M00000000000000003', 'LinkOutlined', '/environment/system', '/environment/system/index', 'environment:system:list', 'PAGE', 0, 1, 1, 330, 1, '关联系统配置管理', 'system', NOW(), 'system', NOW(), 0),

-- 一级菜单：配置管理
('3d2e1f0a9b8c7d6e5f4a3b2c1d0e9f8a', 10, 'M00000000000000004', '配置管理', 'P', NULL, 'SettingOutlined', '/config', '/config/index', 'config:index', 'PAGE', 0, 1, 1, 400, 1, '系统配置管理', 'system', NOW(), 'system', NOW(), 0),

-- 一级菜单：接口管理（目录）
('4e3f2a1b0c9d8e7f6a5b4c3d2e1f0a9b', 11, 'M00000000000000005', '接口管理', 'M', NULL, 'ApiOutlined', '/api', NULL, 'api:index', 'MENU', 0, 1, 1, 500, 1, '接口服务管理', 'system', NOW(), 'system', NOW(), 0),
-- 二级菜单：服务方接口
('5f4a3b2c1d0e9f8a7b6c5d4e3f2a1b0c', 12, 'P00000000000000007', '服务方接口', 'P', 'M00000000000000005', 'ApiFilled', '/api/provider', '/api/provider/index', 'api:provider:list', 'PAGE', 0, 1, 1, 510, 1, '服务方接口管理', 'system', NOW(), 'system', NOW(), 0),
-- 二级菜单：消费方接口
('6a5b4c3d2e1f0a9b8c7d6e5f4a3b2c1d', 13, 'P00000000000000008', '消费方接口', 'P', 'M00000000000000005', 'ApiTwoTone', '/api/consumer', '/api/consumer/index', 'api:consumer:list', 'PAGE', 0, 1, 1, 520, 1, '消费方接口管理', 'system', NOW(), 'system', NOW(), 0),

-- 一级菜单：基础数据（目录）
('7b6c5d4e3f2a1b0c9d8e7f6a5b4c3d2e', 14, 'M00000000000000006', '基础数据', 'M', NULL, 'DatabaseOutlined', '/basic', NULL, 'basic:index', 'MENU', 0, 1, 1, 600, 1, '基础数据管理', 'system', NOW(), 'system', NOW(), 0),
-- 二级菜单：数据库表
('8c7d6e5f4a3b2c1d0e9f8a7b6c5d4e3f', 15, 'P00000000000000009', '数据库表', 'P', 'M00000000000000006', 'TableOutlined', '/basic/table', '/basic/table/index', 'basic:table:list', 'PAGE', 0, 1, 1, 610, 1, '数据库表结构管理', 'system', NOW(), 'system', NOW(), 0),
-- 二级菜单：数据表字段
('9d8e7f6a5b4c3d2e1f0a9b8c7d6e5f4a', 16, 'P00000000000000010', '数据表字段', 'P', 'M00000000000000006', 'ColumnHeightOutlined', '/basic/column', '/basic/column/index', 'basic:column:list', 'PAGE', 0, 1, 1, 620, 1, '数据表字段管理', 'system', NOW(), 'system', NOW(), 0),
-- 二级菜单：数据表索引
('0e9f8a7b6c5d4e3f2a1b0c9d8e7f6a5b', 17, 'P00000000000000011', '数据表索引', 'P', 'M00000000000000006', 'OrderedListOutlined', '/basic/index', '/basic/index/index', 'basic:index:list', 'PAGE', 0, 1, 1, 630, 1, '数据表索引管理', 'system', NOW(), 'system', NOW(), 0),
-- 二级菜单：数据表分区
('1f0a9b8c7d6e5f4a3b2c1d0e9f8a7b6c', 18, 'P00000000000000012', '数据表分区', 'P', 'M00000000000000006', 'PartitionOutlined', '/basic/partition', '/basic/partition/index', 'basic:partition:list', 'PAGE', 0, 1, 1, 640, 1, '数据表分区管理', 'system', NOW(), 'system', NOW(), 0),

-- 一级菜单：开发规范（目录）
('2a1b0c9d8e7f6a5b4c3d2e1f0a9b8c7d', 19, 'M00000000000000007', '开发规范', 'M', NULL, 'CodeOutlined', '/standard', NULL, 'standard:index', 'MENU', 0, 1, 1, 700, 1, '开发规范和标准', 'system', NOW(), 'system', NOW(), 0),
-- 二级菜单：脚本编写规范
('3b2c1d0e9f8a7b6c5d4e3f2a1b0c9d8e', 20, 'P00000000000000013', '脚本编写规范', 'P', 'M00000000000000007', 'FileTextOutlined', '/standard/script', '/standard/script/index', 'standard:script:list', 'PAGE', 0, 1, 1, 710, 1, 'SQL脚本编写规范', 'system', NOW(), 'system', NOW(), 0),
-- 二级菜单：代码开发规范
('4c3d2e1f0a9b8c7d6e5f4a3b2c1d0e9f', 21, 'P00000000000000014', '代码开发规范', 'P', 'M00000000000000007', 'CodeSandboxOutlined', '/standard/code', '/standard/code/index', 'standard:code:list', 'PAGE', 0, 1, 1, 720, 1, '代码开发规范', 'system', NOW(), 'system', NOW(), 0),
-- 二级菜单：版本管理规范
('5d4e3f2a1b0c9d8e7f6a5b4c3d2e1f0a', 22, 'P00000000000000015', '版本管理规范', 'P', 'M00000000000000007', 'GitlabOutlined', '/standard/version', '/standard/version/index', 'standard:version:list', 'PAGE', 0, 1, 1, 730, 1, '版本管理规范', 'system', NOW(), 'system', NOW(), 0),

-- 一级菜单：效能手册（目录）
('6e5f4a3b2c1d0e9f8a7b6c5d4e3f2a1b', 23, 'M00000000000000008', '效能手册', 'M', NULL, 'BookOutlined', '/efficiency', NULL, 'efficiency:index', 'MENU', 0, 1, 1, 800, 1, '开发效能提升手册', 'system', NOW(), 'system', NOW(), 0),
-- 二级菜单：避坑指南
('7f6a5b4c3d2e1f0a9b8c7d6e5f4a3b2c', 24, 'P00000000000000016', '避坑指南', 'P', 'M00000000000000008', 'WarningOutlined', '/efficiency/guide', '/efficiency/guide/index', 'efficiency:guide:list', 'PAGE', 0, 1, 1, 810, 1, '开发过程中常见问题避坑指南', 'system', NOW(), 'system', NOW(), 0),
-- 二级菜单：公共组件之多线程
('8a7b6c5d4e3f2a1b0c9d8e7f6a5b4c3d', 25, 'P00000000000000017', '公共组件之多线程', 'P', 'M00000000000000008', 'ClusterOutlined', '/efficiency/thread', '/efficiency/thread/index', 'efficiency:thread:list', 'PAGE', 0, 1, 1, 820, 1, '多线程组件使用指南', 'system', NOW(), 'system', NOW(), 0),
-- 二级菜单：公共组件之影像组件
('9b8c7d6e5f4a3b2c1d0e9f8a7b6c5d4e', 26, 'P00000000000000018', '公共组件之影像组件', 'P', 'M00000000000000008', 'PictureOutlined', '/efficiency/image', '/efficiency/image/index', 'efficiency:image:list', 'PAGE', 0, 1, 1, 830, 1, '影像组件使用指南', 'system', NOW(), 'system', NOW(), 0);

-- ============================================
-- 9. API资源定义表初始化（关键API示例）
-- ============================================
INSERT INTO `sys_api_resource` (`uuid`, `id`, `api_code`, `api_name`, `api_path`, `http_method`, `resource_key`, `service_name`, `module_name`, `rate_limit`, `need_auth`, `log_enabled`, `sort_order`, `status`, `description`, `created_by`, `created_time`, `updated_by`, `updated_time`, `is_deleted`) VALUES
-- 用户认证相关API
('4a3b2c1d5e6f7a8b9c0d1e2f3a4b5c6d', 1, 'API00000000000000000000000000001', '用户登录', '/api/auth/login', 'POST', 'auth:login', 'system-service', 'auth', 10, 0, 1, 100, 1, '用户登录接口', 'system', NOW(), 'system', NOW(), 0),
('5b4c3d2e1f0a9b8c7d6e5f4a3b2c1d0e', 2, 'API00000000000000000000000000002', '获取用户信息', '/api/auth/userinfo', 'GET', 'auth:userinfo', 'system-service', 'auth', 100, 1, 1, 110, 1, '获取当前登录用户信息', 'system', NOW(), 'system', NOW(), 0),
('6c5d4e3f2a1b0c9d8e7f6a5b4c3d2e1f', 3, 'API00000000000000000000000000003', '用户登出', '/api/auth/logout', 'POST', 'auth:logout', 'system-service', 'auth', 100, 1, 1, 120, 1, '用户登出接口', 'system', NOW(), 'system', NOW(), 0),

-- 用户管理API
('7d6e5f4a3b2c1d0e9f8a7b6c5d4e3f2a', 4, 'API00000000000000000000000000004', '查询用户列表', '/api/user/list', 'GET', 'user:list', 'system-service', 'user', 100, 1, 1, 200, 1, '分页查询用户列表', 'system', NOW(), 'system', NOW(), 0),
('8e7f6a5b4c3d2e1f0a9b8c7d6e5f4a3b', 5, 'API00000000000000000000000000005', '获取用户详情', '/api/user/detail/{id}', 'GET', 'user:detail', 'system-service', 'user', 100, 1, 1, 210, 1, '根据ID获取用户详情', 'system', NOW(), 'system', NOW(), 0),
('9f8a7b6c5d4e3f2a1b0c9d8e7f6a5b4c', 6, 'API00000000000000000000000000006', '新增用户', '/api/user/create', 'POST', 'user:create', 'system-service', 'user', 50, 1, 1, 220, 1, '新增用户', 'system', NOW(), 'system', NOW(), 0),
('0a9b8c7d6e5f4a3b2c1d0e9f8a7b6c5d', 7, 'API00000000000000000000000000007', '更新用户', '/api/user/update', 'PUT', 'user:update', 'system-service', 'user', 50, 1, 1, 230, 1, '更新用户信息', 'system', NOW(), 'system', NOW(), 0),
('1b0c9d8e7f6a5b4c3d2e1f0a9b8c7d6e', 8, 'API00000000000000000000000000008', '删除用户', '/api/user/delete/{id}', 'DELETE', 'user:delete', 'system-service', 'user', 20, 1, 1, 240, 1, '删除用户', 'system', NOW(), 'system', NOW(), 0),

-- 角色管理API
('2c1d0e9f8a7b6c5d4e3f2a1b0c9d8e7f', 9, 'API00000000000000000000000000009', '查询角色列表', '/api/role/list', 'GET', 'role:list', 'system-service', 'role', 100, 1, 1, 300, 1, '分页查询角色列表', 'system', NOW(), 'system', NOW(), 0),
('3d2e1f0a9b8c7d6e5f4a3b2c1d0e9f8a', 10, 'API00000000000000000000000000010', '获取角色详情', '/api/role/detail/{code}', 'GET', 'role:detail', 'system-service', 'role', 100, 1, 1, 310, 1, '根据编码获取角色详情', 'system', NOW(), 'system', NOW(), 0),

-- 菜单管理API
('4e3f2a1b0c9d8e7f6a5b4c3d2e1f0a9b', 11, 'API00000000000000000000000000011', '查询菜单树', '/api/menu/tree', 'GET', 'menu:tree', 'system-service', 'menu', 100, 1, 1, 400, 1, '查询菜单树形结构', 'system', NOW(), 'system', NOW(), 0),
('5f4a3b2c1d0e9f8a7b6c5d4e3f2a1b0c', 12, 'API00000000000000000000000000012', '获取用户菜单', '/api/menu/user', 'GET', 'menu:user', 'system-service', 'menu', 100, 1, 1, 410, 1, '获取当前用户的菜单权限', 'system', NOW(), 'system', NOW(), 0),

-- 待办任务API
('6a5b4c3d2e1f0a9b8c7d6e5f4a3b2c1d', 13, 'API00000000000000000000000000013', '查询待办任务', '/api/task/todo', 'GET', 'task:todo', 'workflow-service', 'task', 100, 1, 1, 500, 1, '查询当前用户的待办任务', 'system', NOW(), 'system', NOW(), 0),
('7b6c5d4e3f2a1b0c9d8e7f6a5b4c3d2e', 14, 'API00000000000000000000000000014', '审批任务', '/api/task/approve', 'POST', 'task:approve', 'workflow-service', 'task', 50, 1, 1, 510, 1, '审批待办任务', 'system', NOW(), 'system', NOW(), 0),

-- 变更记录API
('8c7d6e5f4a3b2c1d0e9f8a7b6c5d4e3f', 15, 'API00000000000000000000000000015', '查询变更记录', '/api/change/list', 'GET', 'change:list', 'business-service', 'change', 100, 1, 1, 600, 1, '分页查询变更记录', 'system', NOW(), 'system', NOW(), 0),
('9d8e7f6a5b4c3d2e1f0a9b8c7d6e5f4a', 16, 'API00000000000000000000000000016', '提交变更', '/api/change/submit', 'POST', 'change:submit', 'business-service', 'change', 30, 1, 1, 610, 1, '提交变更申请', 'system', NOW(), 'system', NOW(), 0);

-- ============================================
-- 10. 权限定义表初始化（关键权限示例）
-- ============================================
INSERT INTO `sys_permission` (`uuid`, `id`, `perm_code`, `perm_name`, `perm_key`, `perm_type`, `action_type`, `effect_type`, `condition_expression`, `sort_order`, `status`, `description`, `created_by`, `created_time`, `updated_by`, `updated_time`, `is_deleted`) VALUES
-- 访问控制类权限（perm_type = 1）
-- 用户管理权限
('4a3b2c1d5e6f7a8b9c0d1e2f3a4b5c6d', 1, 'PERM00000000000001', '查看用户列表', 'system:user:list', 1, 'VIEW', 1, NULL, 100, 1, '查看用户列表权限', 'system', NOW(), 'system', NOW(), 0),
('5b4c3d2e1f0a9b8c7d6e5f4a3b2c1d0e', 2, 'PERM00000000000002', '新增用户', 'system:user:create', 1, 'CREATE', 1, NULL, 110, 1, '新增用户权限', 'system', NOW(), 'system', NOW(), 0),
('6c5d4e3f2a1b0c9d8e7f6a5b4c3d2e1f', 3, 'PERM00000000000003', '编辑用户', 'system:user:update', 1, 'UPDATE', 1, NULL, 120, 1, '编辑用户权限', 'system', NOW(), 'system', NOW(), 0),
('7d6e5f4a3b2c1d0e9f8a7b6c5d4e3f2a', 4, 'PERM00000000000004', '删除用户', 'system:user:delete', 1, 'DELETE', 1, NULL, 130, 1, '删除用户权限', 'system', NOW(), 'system', NOW(), 0),

-- 角色管理权限
('8e7f6a5b4c3d2e1f0a9b8c7d6e5f4a3b', 5, 'PERM00000000000005', '查看角色列表', 'system:role:list', 1, 'VIEW', 1, NULL, 200, 1, '查看角色列表权限', 'system', NOW(), 'system', NOW(), 0),
('9f8a7b6c5d4e3f2a1b0c9d8e7f6a5b4c', 6, 'PERM00000000000006', '编辑角色', 'system:role:update', 1, 'UPDATE', 1, NULL, 210, 1, '编辑角色权限', 'system', NOW(), 'system', NOW(), 0),

-- 菜单管理权限
('0a9b8c7d6e5f4a3b2c1d0e9f8a7b6c5d', 7, 'PERM00000000000007', '查看菜单树', 'system:menu:tree', 1, 'VIEW', 1, NULL, 300, 1, '查看菜单树权限', 'system', NOW(), 'system', NOW(), 0),

-- 待办事项权限
('1b0c9d8e7f6a5b4c3d2e1f0a9b8c7d6e', 8, 'PERM00000000000008', '查看待办事项', 'workflow:task:todo', 1, 'VIEW', 1, NULL, 400, 1, '查看待办事项权限', 'system', NOW(), 'system', NOW(), 0),
('2c1d0e9f8a7b6c5d4e3f2a1b0c9d8e7f', 9, 'PERM00000000000009', '审批任务', 'workflow:task:approve', 1, 'EXECUTE', 1, NULL, 410, 1, '审批任务权限', 'system', NOW(), 'system', NOW(), 0),

-- 变更记录权限
('3d2e1f0a9b8c7d6e5f4a3b2c1d0e9f8a', 10, 'PERM00000000000010', '查看变更记录', 'business:change:list', 1, 'VIEW', 1, NULL, 500, 1, '查看变更记录权限', 'system', NOW(), 'system', NOW(), 0),
('4e3f2a1b0c9d8e7f6a5b4c3d2e1f0a9b', 11, 'PERM00000000000011', '提交变更', 'business:change:submit', 1, 'CREATE', 1, NULL, 510, 1, '提交变更权限', 'system', NOW(), 'system', NOW(), 0),

-- 数据范围类权限（perm_type = 2）
('5f4a3b2c1d0e9f8a7b6c5d4e3f2a1b0c', 12, 'PERM00000000000012', '查看本机构用户数据', 'system:user:data:org', 2, 'VIEW', 1, NULL, 600, 1, '查看本机构用户数据权限', 'system', NOW(), 'system', NOW(), 0),
('6a5b4c3d2e1f0a9b8c7d6e5f4a3b2c1d', 13, 'PERM00000000000013', '查看本部门用户数据', 'system:user:data:dept', 2, 'VIEW', 1, NULL, 610, 1, '查看本部门用户数据权限', 'system', NOW(), 'system', NOW(), 0),

-- 字段控制类权限（perm_type = 3）
('7b6c5d4e3f2a1b0c9d8e7f6a5b4c3d2e', 14, 'PERM00000000000014', '查看用户敏感信息', 'system:user:field:sensitive', 3, 'VIEW', 1, NULL, 700, 1, '查看用户敏感信息权限', 'system', NOW(), 'system', NOW(), 0);

-- ============================================
-- 11. 权限-资源关联表初始化
-- ============================================
-- 将权限与菜单、API资源关联
INSERT INTO `sys_perm_resource` (`uuid`, `id`, `perm_code`, `resource_type`, `resource_key`, `resource_sub_type`, `relation_type`, `condition_expression`, `status`, `description`, `created_by`, `created_time`, `updated_by`, `updated_time`, `is_deleted`) VALUES
-- 用户管理权限关联
-- 查看用户列表权限 关联 菜单和API
('4a3b2c1d5e6f7a8b9c0d1e2f3a4b5c6d', 1, 'PERM00000000000001', 'MENU', 'system:user:management', NULL, 1, NULL, 1, '查看用户列表菜单权限', 'system', NOW(), 'system', NOW(), 0),
('5b4c3d2e1f0a9b8c7d6e5f4a3b2c1d0e', 2, 'PERM00000000000001', 'API', 'user:list', NULL, 1, NULL, 1, '查看用户列表API权限', 'system', NOW(), 'system', NOW(), 0),

-- 新增用户权限关联API
('6c5d4e3f2a1b0c9d8e7f6a5b4c3d2e1f', 3, 'PERM00000000000002', 'API', 'user:create', NULL, 1, NULL, 1, '新增用户API权限', 'system', NOW(), 'system', NOW(), 0),

-- 编辑用户权限关联API
('7d6e5f4a3b2c1d0e9f8a7b6c5d4e3f2a', 4, 'PERM00000000000003', 'API', 'user:update', NULL, 1, NULL, 1, '编辑用户API权限', 'system', NOW(), 'system', NOW(), 0),

-- 删除用户权限关联API
('8e7f6a5b4c3d2e1f0a9b8c7d6e5f4a3b', 5, 'PERM00000000000004', 'API', 'user:delete', NULL, 1, NULL, 1, '删除用户API权限', 'system', NOW(), 'system', NOW(), 0),

-- 角色管理权限关联
('9f8a7b6c5d4e3f2a1b0c9d8e7f6a5b4c', 6, 'PERM00000000000005', 'API', 'role:list', NULL, 1, NULL, 1, '查看角色列表API权限', 'system', NOW(), 'system', NOW(), 0),
('0a9b8c7d6e5f4a3b2c1d0e9f8a7b6c5d', 7, 'PERM00000000000006', 'API', 'role:update', NULL, 1, NULL, 1, '编辑角色API权限', 'system', NOW(), 'system', NOW(), 0),

-- 菜单管理权限关联
('1b0c9d8e7f6a5b4c3d2e1f0a9b8c7d6e', 8, 'PERM00000000000007', 'API', 'menu:tree', NULL, 1, NULL, 1, '查看菜单树API权限', 'system', NOW(), 'system', NOW(), 0),

-- 待办事项权限关联
('2c1d0e9f8a7b6c5d4e3f2a1b0c9d8e7f', 9, 'PERM00000000000008', 'MENU', 'mywork:todo:list', NULL, 1, NULL, 1, '查看待办事项菜单权限', 'system', NOW(), 'system', NOW(), 0),
('3d2e1f0a9b8c7d6e5f4a3b2c1d0e9f8a', 10, 'PERM00000000000008', 'API', 'task:todo', NULL, 1, NULL, 1, '查看待办事项API权限', 'system', NOW(), 'system', NOW(), 0),
('4e3f2a1b0c9d8e7f6a5b4c3d2e1f0a9b', 11, 'PERM00000000000009', 'API', 'task:approve', NULL, 1, NULL, 1, '审批任务API权限', 'system', NOW(), 'system', NOW(), 0),

-- 变更记录权限关联
('5f4a3b2c1d0e9f8a7b6c5d4e3f2a1b0c', 12, 'PERM00000000000010', 'MENU', 'mywork:change:list', NULL, 1, NULL, 1, '查看变更记录菜单权限', 'system', NOW(), 'system', NOW(), 0),
('6a5b4c3d2e1f0a9b8c7d6e5f4a3b2c1d', 13, 'PERM00000000000010', 'API', 'change:list', NULL, 1, NULL, 1, '查看变更记录API权限', 'system', NOW(), 'system', NOW(), 0),
('7b6c5d4e3f2a1b0c9d8e7f6a5b4c3d2e', 14, 'PERM00000000000011', 'API', 'change:submit', NULL, 1, NULL, 1, '提交变更API权限', 'system', NOW(), 'system', NOW(), 0),

-- 数据范围权限关联数据实体
('8c7d6e5f4a3b2c1d0e9f8a7b6c5d4e3f', 15, 'PERM00000000000012', 'DATA', 'sys_user', NULL, 1, NULL, 1, '用户数据实体权限', 'system', NOW(), 'system', NOW(), 0),
('9d8e7f6a5b4c3d2e1f0a9b8c7d6e5f4a', 16, 'PERM00000000000013', 'DATA', 'sys_user', NULL, 1, NULL, 1, '用户数据实体权限', 'system', NOW(), 'system', NOW(), 0),

-- 字段控制权限关联字段
('0e9f8a7b6c5d4e3f2a1b0c9d8e7f6a5b', 17, 'PERM00000000000014', 'FIELD', 'sys_user.salary', 'SALARY', 1, NULL, 1, '用户薪资字段权限', 'system', NOW(), 'system', NOW(), 0),
('1f0a9b8c7d6e5f4a3b2c1d0e9f8a7b6c', 18, 'PERM00000000000014', 'FIELD', 'sys_user.phone', 'PHONE', 1, NULL, 1, '用户电话字段权限', 'system', NOW(), 'system', NOW(), 0),
('2a1b0c9d8e7f6a5b4c3d2e1f0a9b8c7d', 19, 'PERM00000000000014', 'FIELD', 'sys_user.email', 'EMAIL', 1, NULL, 1, '用户邮箱字段权限', 'system', NOW(), 'system', NOW(), 0);

-- ============================================
-- 12. 角色权限关联表初始化
-- ============================================
-- 为不同角色分配权限
INSERT INTO `sys_role_permission` (`uuid`, `id`, `role_code`, `perm_code`, `auth_type`, `effective_start`, `effective_end`, `priority`, `status`, `description`, `created_by`, `created_time`, `updated_by`, `updated_time`, `is_deleted`) VALUES
-- 超级管理员：拥有所有权限
('4a3b2c1d5e6f7a8b9c0d1e2f3a4b5c6d', 1, 'RL000001', 'PERM00000000000001', 1, NOW(), DATE_ADD(NOW(), INTERVAL 3650 DAY), 1, 1, '超级管理员-查看用户列表', 'system', NOW(), 'system', NOW(), 0),
('5b4c3d2e1f0a9b8c7d6e5f4a3b2c1d0e', 2, 'RL000001', 'PERM00000000000002', 1, NOW(), DATE_ADD(NOW(), INTERVAL 3650 DAY), 1, 1, '超级管理员-新增用户', 'system', NOW(), 'system', NOW(), 0),
('6c5d4e3f2a1b0c9d8e7f6a5b4c3d2e1f', 3, 'RL000001', 'PERM00000000000003', 1, NOW(), DATE_ADD(NOW(), INTERVAL 3650 DAY), 1, 1, '超级管理员-编辑用户', 'system', NOW(), 'system', NOW(), 0),
('7d6e5f4a3b2c1d0e9f8a7b6c5d4e3f2a', 4, 'RL000001', 'PERM00000000000004', 1, NOW(), DATE_ADD(NOW(), INTERVAL 3650 DAY), 1, 1, '超级管理员-删除用户', 'system', NOW(), 'system', NOW(), 0),
('8e7f6a5b4c3d2e1f0a9b8c7d6e5f4a3b', 5, 'RL000001', 'PERM00000000000005', 1, NOW(), DATE_ADD(NOW(), INTERVAL 3650 DAY), 1, 1, '超级管理员-查看角色列表', 'system', NOW(), 'system', NOW(), 0),
('9f8a7b6c5d4e3f2a1b0c9d8e7f6a5b4c', 6, 'RL000001', 'PERM00000000000006', 1, NOW(), DATE_ADD(NOW(), INTERVAL 3650 DAY), 1, 1, '超级管理员-编辑角色', 'system', NOW(), 'system', NOW(), 0),
('0a9b8c7d6e5f4a3b2c1d0e9f8a7b6c5d', 7, 'RL000001', 'PERM00000000000007', 1, NOW(), DATE_ADD(NOW(), INTERVAL 3650 DAY), 1, 1, '超级管理员-查看菜单树', 'system', NOW(), 'system', NOW(), 0),
('1b0c9d8e7f6a5b4c3d2e1f0a9b8c7d6e', 8, 'RL000001', 'PERM00000000000008', 1, NOW(), DATE_ADD(NOW(), INTERVAL 3650 DAY), 1, 1, '超级管理员-查看待办事项', 'system', NOW(), 'system', NOW(), 0),
('2c1d0e9f8a7b6c5d4e3f2a1b0c9d8e7f', 9, 'RL000001', 'PERM00000000000009', 1, NOW(), DATE_ADD(NOW(), INTERVAL 3650 DAY), 1, 1, '超级管理员-审批任务', 'system', NOW(), 'system', NOW(), 0),
('3d2e1f0a9b8c7d6e5f4a3b2c1d0e9f8a', 10, 'RL000001', 'PERM00000000000010', 1, NOW(), DATE_ADD(NOW(), INTERVAL 3650 DAY), 1, 1, '超级管理员-查看变更记录', 'system', NOW(), 'system', NOW(), 0),
('4e3f2a1b0c9d8e7f6a5b4c3d2e1f0a9b', 11, 'RL000001', 'PERM00000000000011', 1, NOW(), DATE_ADD(NOW(), INTERVAL 3650 DAY), 1, 1, '超级管理员-提交变更', 'system', NOW(), 'system', NOW(), 0),
('5f4a3b2c1d0e9f8a7b6c5d4e3f2a1b0c', 12, 'RL000001', 'PERM00000000000012', 1, NOW(), DATE_ADD(NOW(), INTERVAL 3650 DAY), 1, 1, '超级管理员-查看本机构用户数据', 'system', NOW(), 'system', NOW(), 0),
('6a5b4c3d2e1f0a9b8c7d6e5f4a3b2c1d', 13, 'RL000001', 'PERM00000000000013', 1, NOW(), DATE_ADD(NOW(), INTERVAL 3650 DAY), 1, 1, '超级管理员-查看本部门用户数据', 'system', NOW(), 'system', NOW(), 0),
('7b6c5d4e3f2a1b0c9d8e7f6a5b4c3d2e', 14, 'RL000001', 'PERM00000000000014', 1, NOW(), DATE_ADD(NOW(), INTERVAL 3650 DAY), 1, 1, '超级管理员-查看用户敏感信息', 'system', NOW(), 'system', NOW(), 0),

-- 系统管理员：拥有管理权限，但不能查看敏感信息
('8c7d6e5f4a3b2c1d0e9f8a7b6c5d4e3f', 15, 'RL000002', 'PERM00000000000001', 1, NOW(), DATE_ADD(NOW(), INTERVAL 3650 DAY), 10, 1, '系统管理员-查看用户列表', 'system', NOW(), 'system', NOW(), 0),
('9d8e7f6a5b4c3d2e1f0a9b8c7d6e5f4a', 16, 'RL000002', 'PERM00000000000002', 1, NOW(), DATE_ADD(NOW(), INTERVAL 3650 DAY), 10, 1, '系统管理员-新增用户', 'system', NOW(), 'system', NOW(), 0),
('0e9f8a7b6c5d4e3f2a1b0c9d8e7f6a5b', 17, 'RL000002', 'PERM00000000000003', 1, NOW(), DATE_ADD(NOW(), INTERVAL 3650 DAY), 10, 1, '系统管理员-编辑用户', 'system', NOW(), 'system', NOW(), 0),
('1f0a9b8c7d6e5f4a3b2c1d0e9f8a7b6c', 18, 'RL000002', 'PERM00000000000004', 1, NOW(), DATE_ADD(NOW(), INTERVAL 3650 DAY), 10, 1, '系统管理员-删除用户', 'system', NOW(), 'system', NOW(), 0),
('2a1b0c9d8e7f6a5b4c3d2e1f0a9b8c7d', 19, 'RL000002', 'PERM00000000000005', 1, NOW(), DATE_ADD(NOW(), INTERVAL 3650 DAY), 10, 1, '系统管理员-查看角色列表', 'system', NOW(), 'system', NOW(), 0),
('3b2c1d0e9f8a7b6c5d4e3f2a1b0c9d8e', 20, 'RL000002', 'PERM00000000000006', 1, NOW(), DATE_ADD(NOW(), INTERVAL 3650 DAY), 10, 1, '系统管理员-编辑角色', 'system', NOW(), 'system', NOW(), 0),
('4c3d2e1f0a9b8c7d6e5f4a3b2c1d0e9f', 21, 'RL000002', 'PERM00000000000007', 1, NOW(), DATE_ADD(NOW(), INTERVAL 3650 DAY), 10, 1, '系统管理员-查看菜单树', 'system', NOW(), 'system', NOW(), 0),
('5d4e3f2a1b0c9d8e7f6a5b4c3d2e1f0a', 22, 'RL000002', 'PERM00000000000008', 1, NOW(), DATE_ADD(NOW(), INTERVAL 3650 DAY), 10, 1, '系统管理员-查看待办事项', 'system', NOW(), 'system', NOW(), 0),
('6e5f4a3b2c1d0e9f8a7b6c5d4e3f2a1b', 23, 'RL000002', 'PERM00000000000009', 1, NOW(), DATE_ADD(NOW(), INTERVAL 3650 DAY), 10, 1, '系统管理员-审批任务', 'system', NOW(), 'system', NOW(), 0),
-- 系统管理员不能查看用户敏感信息（通过不分配该权限实现）

-- 管理负责人：管理本机构数据
('7f6a5b4c3d2e1f0a9b8c7d6e5f4a3b2c', 24, 'RL000003', 'PERM00000000000001', 1, NOW(), DATE_ADD(NOW(), INTERVAL 3650 DAY), 20, 1, '管理负责人-查看用户列表', 'system', NOW(), 'system', NOW(), 0),
('8a7b6c5d4e3f2a1b0c9d8e7f6a5b4c3d', 25, 'RL000003', 'PERM00000000000012', 1, NOW(), DATE_ADD(NOW(), INTERVAL 3650 DAY), 20, 1, '管理负责人-查看本机构用户数据', 'system', NOW(), 'system', NOW(), 0),
('9b8c7d6e5f4a3b2c1d0e9f8a7b6c5d4e', 26, 'RL000003', 'PERM00000000000008', 1, NOW(), DATE_ADD(NOW(), INTERVAL 3650 DAY), 20, 1, '管理负责人-查看待办事项', 'system', NOW(), 'system', NOW(), 0),
('0c9d8e7f6a5b4c3d2e1f0a9b8c7d6e5f', 27, 'RL000003', 'PERM00000000000009', 1, NOW(), DATE_ADD(NOW(), INTERVAL 3650 DAY), 20, 1, '管理负责人-审批任务', 'system', NOW(), 'system', NOW(), 0),

-- 产品经理：查看变更记录，提交变更
('1d0e9f8a7b6c5d4e3f2a1b0c9d8e7f6a', 28, 'RL000004', 'PERM00000000000010', 1, NOW(), DATE_ADD(NOW(), INTERVAL 3650 DAY), 30, 1, '产品经理-查看变更记录', 'system', NOW(), 'system', NOW(), 0),
('2e1f0a9b8c7d6e5f4a3b2c1d0e9f8a7b', 29, 'RL000004', 'PERM00000000000011', 1, NOW(), DATE_ADD(NOW(), INTERVAL 3650 DAY), 30, 1, '产品经理-提交变更', 'system', NOW(), 'system', NOW(), 0),
('3f2a1b0c9d8e7f6a5b4c3d2e1f0a9b8c', 30, 'RL000004', 'PERM00000000000008', 1, NOW(), DATE_ADD(NOW(), INTERVAL 3650 DAY), 30, 1, '产品经理-查看待办事项', 'system', NOW(), 'system', NOW(), 0),

-- 高级开发：查看待办，提交变更
('4a3b2c1d5e6f7a8b9c0d1e2f3a4b5c6e', 31, 'RL000013', 'PERM00000000000008', 1, NOW(), DATE_ADD(NOW(), INTERVAL 3650 DAY), 40, 1, '高级开发-查看待办事项', 'system', NOW(), 'system', NOW(), 0),
('5b4c3d2e1f0a9b8c7d6e5f4a3b2c1d0f', 32, 'RL000013', 'PERM00000000000010', 1, NOW(), DATE_ADD(NOW(), INTERVAL 3650 DAY), 40, 1, '高级开发-查看变更记录', 'system', NOW(), 'system', NOW(), 0),
('6c5d4e3f2a1b0c9d8e7f6a5b4c3d2e1g', 33, 'RL000013', 'PERM00000000000011', 1, NOW(), DATE_ADD(NOW(), INTERVAL 3650 DAY), 40, 1, '高级开发-提交变更', 'system', NOW(), 'system', NOW(), 0),

-- 测试经理：查看待办，审批测试相关任务
('7d6e5f4a3b2c1d0e9f8a7b6c5d4e3f2b', 34, 'RL000006', 'PERM00000000000008', 1, NOW(), DATE_ADD(NOW(), INTERVAL 3650 DAY), 50, 1, '测试经理-查看待办事项', 'system', NOW(), 'system', NOW(), 0),
('8e7f6a5b4c3d2e1f0a9b8c7d6e5f4a3c', 35, 'RL000006', 'PERM00000000000009', 1, NOW(), DATE_ADD(NOW(), INTERVAL 3650 DAY), 50, 1, '测试经理-审批任务', 'system', NOW(), 'system', NOW(), 0),
('9f8a7b6c5d4e3f2a1b0c9d8e7f6a5b4d', 36, 'RL000006', 'PERM00000000000010', 1, NOW(), DATE_ADD(NOW(), INTERVAL 3650 DAY), 50, 1, '测试经理-查看变更记录', 'system', NOW(), 'system', NOW(), 0);

-- ============================================
-- 13. 数据权限子表初始化（示例）
-- ============================================
INSERT INTO `sys_data_permission` (`uuid`, `id`, `perm_code`, `data_name`, `entity_type`, `scope_type`, `include_children`, `rule_type`, `custom_sql`, `rule_expression`, `rule_priority`, `condition_fields`, `is_global`, `status`, `description`, `created_by`, `created_time`, `updated_by`, `updated_time`, `is_deleted`) VALUES
-- 查看本机构用户数据权限规则
('4a3b2c1d5e6f7a8b9c0d1e2f3a4b5c6d', 1, 'PERM00000000000012', '本机构用户数据范围', 'sys_user', 2, 1, 1, NULL, '{"type": "ORG", "includeChildren": true}', 1, '["org_code"]', 0, 1, '只能查看本机构及下级机构的用户数据', 'system', NOW(), 'system', NOW(), 0),
-- 查看本部门用户数据权限规则
('5b4c3d2e1f0a9b8c7d6e5f4a3b2c1d0e', 2, 'PERM00000000000013', '本部门用户数据范围', 'sys_user', 3, 1, 1, NULL, '{"type": "DEPT", "includeChildren": true}', 1, '["dept_code"]', 0, 1, '只能查看本部门及下级部门的用户数据', 'system', NOW(), 'system', NOW(), 0);

-- ============================================
-- 14. 字段权限子表初始化（示例）
-- ============================================
INSERT INTO `sys_field_permission` (`uuid`, `id`, `perm_code`, `field_code`, `entity_type`, `field_name`, `field_alias`, `field_type`, `condition_expression`, `default_value`, `validation_rules`, `ui_config`, `sort_order`, `status`, `description`, `created_by`, `created_time`, `updated_by`, `updated_time`, `is_deleted`) VALUES
-- 薪资字段权限（只有特定角色可见）
('4a3b2c1d5e6f7a8b9c0d1e2f3a4b5c6d', 1, 'PERM00000000000014', 'FIELD000000001', 'sys_user', 'salary', '薪资', 4, '{"condition": "user.role in [\"RL000001\", \"RL000002\", \"HR_MANAGER\"]"}', NULL, NULL, '{"component": "InputNumber", "precision": 2}', 100, 1, '薪资字段权限控制', 'system', NOW(), 'system', NOW(), 0),
-- 电话字段权限
('5b4c3d2e1f0a9b8c7d6e5f4a3b2c1d0e', 2, 'PERM00000000000014', 'FIELD000000002', 'sys_user', 'phone', '手机号', 1, NULL, NULL, '{"pattern": "^1[3-9]\\\\d{9}$", "message": "手机号格式不正确"}', '{"component": "Input", "maxLength": 11}', 110, 1, '电话字段权限控制', 'system', NOW(), 'system', NOW(), 0),
-- 邮箱字段权限
('6c5d4e3f2a1b0c9d8e7f6a5b4c3d2e1f', 3, 'PERM00000000000014', 'FIELD000000003', 'sys_user', 'email', '邮箱', 1, NULL, NULL, '{"type": "email", "message": "邮箱格式不正确"}', '{"component": "Input", "maxLength": 100}', 120, 1, '邮箱字段权限控制', 'system', NOW(), 'system', NOW(), 0);

-- ============================================
-- 15. 字典类型表初始化（基础字典）
-- ============================================
INSERT INTO `sys_dict_type` (`uuid`, `id`, `dict_type_code`, `dict_type_name`, `description`, `sort_order`, `status`, `created_by`, `created_time`, `updated_by`, `updated_time`, `is_deleted`) VALUES
('4a3b2c1d5e6f7a8b9c0d1e2f3a4b5c6d', 1, 'USER_STATUS', '用户状态', '系统用户状态', 100, 1, 'system', NOW(), 'system', NOW(), 0),
('5b4c3d2e1f0a9b8c7d6e5f4a3b2c1d0e', 2, 'GENDER', '性别', '用户性别', 110, 1, 'system', NOW(), 'system', NOW(), 0),
('6c5d4e3f2a1b0c9d8e7f6a5b4c3d2e1f', 3, 'CHANGE_STATUS', '变更状态', '变更记录状态', 120, 1, 'system', NOW(), 'system', NOW(), 0),
('7d6e5f4a3b2c1d0e9f8a7b6c5d4e3f2a', 4, 'TASK_STATUS', '任务状态', '待办任务状态', 130, 1, 'system', NOW(), 'system', NOW(), 0),
('8e7f6a5b4c3d2e1f0a9b8c7d6e5f4a3b', 5, 'ORG_LEVEL', '机构层级', '机构层级分类', 140, 1, 'system', NOW(), 'system', NOW(), 0);

-- ============================================
-- 16. 字典项表初始化
-- ============================================
INSERT INTO `sys_dict_item` (`uuid`, `id`, `dict_type_code`, `dict_value`, `dict_label`, `group_code`, `group_name`, `sort_order`, `status`, `created_by`, `created_time`, `updated_by`, `updated_time`, `is_deleted`) VALUES
-- 用户状态
('4a3b2c1d5e6f7a8b9c0d1e2f3a4b5c6d', 1, 'USER_STATUS', '0', '禁用', NULL, NULL, 100, 1, 'system', NOW(), 'system', NOW(), 0),
('5b4c3d2e1f0a9b8c7d6e5f4a3b2c1d0e', 2, 'USER_STATUS', '1', '启用', NULL, NULL, 110, 1, 'system', NOW(), 'system', NOW(), 0),
('6c5d4e3f2a1b0c9d8e7f6a5b4c3d2e1f', 3, 'USER_STATUS', '2', '锁定', NULL, NULL, 120, 1, 'system', NOW(), 'system', NOW(), 0),

-- 性别
('7d6e5f4a3b2c1d0e9f8a7b6c5d4e3f2a', 4, 'GENDER', '0', '未知', NULL, NULL, 100, 1, 'system', NOW(), 'system', NOW(), 0),
('8e7f6a5b4c3d2e1f0a9b8c7d6e5f4a3b', 5, 'GENDER', '1', '男', NULL, NULL, 110, 1, 'system', NOW(), 'system', NOW(), 0),
('9f8a7b6c5d4e3f2a1b0c9d8e7f6a5b4c', 6, 'GENDER', '2', '女', NULL, NULL, 120, 1, 'system', NOW(), 'system', NOW(), 0),

-- 变更状态
('0a9b8c7d6e5f4a3b2c1d0e9f8a7b6c5d', 7, 'CHANGE_STATUS', 'DRAFT', '草稿', NULL, NULL, 100, 1, 'system', NOW(), 'system', NOW(), 0),
('1b0c9d8e7f6a5b4c3d2e1f0a9b8c7d6e', 8, 'CHANGE_STATUS', 'PENDING_APPROVAL', '待审批', NULL, NULL, 110, 1, 'system', NOW(), 'system', NOW(), 0),
('2c1d0e9f8a7b6c5d4e3f2a1b0c9d8e7f', 9, 'CHANGE_STATUS', 'APPROVED', '已通过', NULL, NULL, 120, 1, 'system', NOW(), 'system', NOW(), 0),
('3d2e1f0a9b8c7d6e5f4a3b2c1d0e9f8a', 10, 'CHANGE_STATUS', 'REJECTED', '已拒绝', NULL, NULL, 130, 1, 'system', NOW(), 'system', NOW(), 0),
('4e3f2a1b0c9d8e7f6a5b4c3d2e1f0a9b', 11, 'CHANGE_STATUS', 'IMPLEMENTING', '实施中', NULL, NULL, 140, 1, 'system', NOW(), 'system', NOW(), 0),
('5f4a3b2c1d0e9f8a7b6c5d4e3f2a1b0c', 12, 'CHANGE_STATUS', 'COMPLETED', '已完成', NULL, NULL, 150, 1, 'system', NOW(), 'system', NOW(), 0),

-- 任务状态
('6a5b4c3d2e1f0a9b8c7d6e5f4a3b2c1d', 13, 'TASK_STATUS', 'PENDING', '待审批', NULL, NULL, 100, 1, 'system', NOW(), 'system', NOW(), 0),
('7b6c5d4e3f2a1b0c9d8e7f6a5b4c3d2e', 14, 'TASK_STATUS', 'APPROVED', '已通过', NULL, NULL, 110, 1, 'system', NOW(), 'system', NOW(), 0),
('8c7d6e5f4a3b2c1d0e9f8a7b6c5d4e3f', 15, 'TASK_STATUS', 'REJECTED', '已拒绝', NULL, NULL, 120, 1, 'system', NOW(), 'system', NOW(), 0),
('9d8e7f6a5b4c3d2e1f0a9b8c7d6e5f4a', 16, 'TASK_STATUS', 'CANCELED', '已取消', NULL, NULL, 130, 1, 'system', NOW(), 'system', NOW(), 0),

-- 机构层级
('0e9f8a7b6c5d4e3f2a1b0c9d8e7f6a5b', 17, 'ORG_LEVEL', '1', '一级机构', NULL, NULL, 100, 1, 'system', NOW(), 'system', NOW(), 0),
('1f0a9b8c7d6e5f4a3b2c1d0e9f8a7b6c', 18, 'ORG_LEVEL', '2', '二级机构', NULL, NULL, 110, 1, 'system', NOW(), 'system', NOW(), 0),
('2a1b0c9d8e7f6a5b4c3d2e1f0a9b8c7d', 19, 'ORG_LEVEL', '3', '三级机构', NULL, NULL, 120, 1, 'system', NOW(), 'system', NOW(), 0),
('3b2c1d0e9f8a7b6c5d4e3f2a1b0c9d8e', 20, 'ORG_LEVEL', '4', '四级机构', NULL, NULL, 130, 1, 'system', NOW(), 'system', NOW(), 0),
('4c3d2e1f0a9b8c7d6e5f4a3b2c1d0e9f', 21, 'ORG_LEVEL', '5', '五级机构', NULL, NULL, 140, 1, 'system', NOW(), 'system', NOW(), 0);

-- ============================================
-- 17. 系统操作日志表（暂无初始数据）
-- ============================================

-- ============================================
-- sys_role_org 表数据初始化
-- 超级管理员 (RL000001) 拥有全部机构权限
-- ============================================

INSERT INTO `sys_role_org` (`uuid`, `id`, `role_code`, `org_code`, `org_range_type`, `perm_type`, `effective_start`, `effective_end`, `status`, `description`, `created_by`, `created_time`, `updated_by`, `updated_time`, `is_deleted`) VALUES
-- 系统管理中心 (包含下级机构，管理权限)
('4a3b2c1d5e6f7a8b9c0d1e2f3a4b5c6d', 1, 'RL000001', 'JG00000001', 2, 1, NOW(), DATE_ADD(NOW(), INTERVAL 3650 DAY), 1, '超级管理员-系统管理中心-管理权限', 'system', NOW(), 'system', NOW(), 0),
('5b4c3d2e1f0a9b8c7d6e5f4a3b2c1d0e', 2, 'RL000001', 'JG00000001', 2, 2, NOW(), DATE_ADD(NOW(), INTERVAL 3650 DAY), 1, '超级管理员-系统管理中心-查看权限', 'system', NOW(), 'system', NOW(), 0),
('6c5d4e3f2a1b0c9d8e7f6a5b4c3d2e1f', 3, 'RL000001', 'JG00000001', 2, 3, NOW(), DATE_ADD(NOW(), INTERVAL 3650 DAY), 1, '超级管理员-系统管理中心-操作权限', 'system', NOW(), 'system', NOW(), 0),

-- 产品中心 (包含下级机构，管理权限)
('7d6e5f4a3b2c1d0e9f8a7b6c5d4e3f2a', 4, 'RL000001', 'JG00000002', 2, 1, NOW(), DATE_ADD(NOW(), INTERVAL 3650 DAY), 1, '超级管理员-产品中心-管理权限', 'system', NOW(), 'system', NOW(), 0),
('8e7f6a5b4c3d2e1f0a9b8c7d6e5f4a3b', 5, 'RL000001', 'JG00000002', 2, 2, NOW(), DATE_ADD(NOW(), INTERVAL 3650 DAY), 1, '超级管理员-产品中心-查看权限', 'system', NOW(), 'system', NOW(), 0),
('9f8a7b6c5d4e3f2a1b0c9d8e7f6a5b4c', 6, 'RL000001', 'JG00000002', 2, 3, NOW(), DATE_ADD(NOW(), INTERVAL 3650 DAY), 1, '超级管理员-产品中心-操作权限', 'system', NOW(), 'system', NOW(), 0),

-- 需求中心 (包含下级机构，管理权限)
('0a9b8c7d6e5f4a3b2c1d0e9f8a7b6c5d', 7, 'RL000001', 'JG00000003', 2, 1, NOW(), DATE_ADD(NOW(), INTERVAL 3650 DAY), 1, '超级管理员-需求中心-管理权限', 'system', NOW(), 'system', NOW(), 0),
('1b0c9d8e7f6a5b4c3d2e1f0a9b8c7d6e', 8, 'RL000001', 'JG00000003', 2, 2, NOW(), DATE_ADD(NOW(), INTERVAL 3650 DAY), 1, '超级管理员-需求中心-查看权限', 'system', NOW(), 'system', NOW(), 0),
('2c1d0e9f8a7b6c5d4e3f2a1b0c9d8e7f', 9, 'RL000001', 'JG00000003', 2, 3, NOW(), DATE_ADD(NOW(), INTERVAL 3650 DAY), 1, '超级管理员-需求中心-操作权限', 'system', NOW(), 'system', NOW(), 0),

-- 开发中心 (包含下级机构，管理权限)
('3d2e1f0a9b8c7d6e5f4a3b2c1d0e9f8a', 10, 'RL000001', 'JG00000004', 2, 1, NOW(), DATE_ADD(NOW(), INTERVAL 3650 DAY), 1, '超级管理员-开发中心-管理权限', 'system', NOW(), 'system', NOW(), 0),
('4e3f2a1b0c9d8e7f6a5b4c3d2e1f0a9b', 11, 'RL000001', 'JG00000004', 2, 2, NOW(), DATE_ADD(NOW(), INTERVAL 3650 DAY), 1, '超级管理员-开发中心-查看权限', 'system', NOW(), 'system', NOW(), 0),
('5f4a3b2c1d0e9f8a7b6c5d4e3f2a1b0c', 12, 'RL000001', 'JG00000004', 2, 3, NOW(), DATE_ADD(NOW(), INTERVAL 3650 DAY), 1, '超级管理员-开发中心-操作权限', 'system', NOW(), 'system', NOW(), 0),

-- 测试中心 (包含下级机构，管理权限)
('6a5b4c3d2e1f0a9b8c7d6e5f4a3b2c1d', 13, 'RL000001', 'JG00000005', 2, 1, NOW(), DATE_ADD(NOW(), INTERVAL 3650 DAY), 1, '超级管理员-测试中心-管理权限', 'system', NOW(), 'system', NOW(), 0),
('7b6c5d4e3f2a1b0c9d8e7f6a5b4c3d2e', 14, 'RL000001', 'JG00000005', 2, 2, NOW(), DATE_ADD(NOW(), INTERVAL 3650 DAY), 1, '超级管理员-测试中心-查看权限', 'system', NOW(), 'system', NOW(), 0),
('8c7d6e5f4a3b2c1d0e9f8a7b6c5d4e3f', 15, 'RL000001', 'JG00000005', 2, 3, NOW(), DATE_ADD(NOW(), INTERVAL 3650 DAY), 1, '超级管理员-测试中心-操作权限', 'system', NOW(), 'system', NOW(), 0);

-- ============================================
-- 为其他关键角色配置机构权限
-- ============================================

-- 系统管理员 (RL000002)：拥有系统管理中心的管理权限
INSERT INTO `sys_role_org` (`uuid`, `id`, `role_code`, `org_code`, `org_range_type`, `perm_type`, `effective_start`, `effective_end`, `status`, `description`, `created_by`, `created_time`, `updated_by`, `updated_time`, `is_deleted`) VALUES
-- 系统管理中心 (包含下级机构，管理权限)
('9d8e7f6a5b4c3d2e1f0a9b8c7d6e5f4a', 16, 'RL000002', 'JG00000001', 2, 1, NOW(), DATE_ADD(NOW(), INTERVAL 3650 DAY), 1, '系统管理员-系统管理中心-管理权限', 'system', NOW(), 'system', NOW(), 0),
('0e9f8a7b6c5d4e3f2a1b0c9d8e7f6a5b', 17, 'RL000002', 'JG00000001', 2, 2, NOW(), DATE_ADD(NOW(), INTERVAL 3650 DAY), 1, '系统管理员-系统管理中心-查看权限', 'system', NOW(), 'system', NOW(), 0),
('1f0a9b8c7d6e5f4a3b2c1d0e9f8a7b6c', 18, 'RL000002', 'JG00000001', 2, 3, NOW(), DATE_ADD(NOW(), INTERVAL 3650 DAY), 1, '系统管理员-系统管理中心-操作权限', 'system', NOW(), 'system', NOW(), 0),

-- 其他机构只有查看权限
('2a1b0c9d8e7f6a5b4c3d2e1f0a9b8c7d', 19, 'RL000002', 'JG00000002', 1, 2, NOW(), DATE_ADD(NOW(), INTERVAL 3650 DAY), 1, '系统管理员-产品中心-查看权限', 'system', NOW(), 'system', NOW(), 0),
('3b2c1d0e9f8a7b6c5d4e3f2a1b0c9d8e', 20, 'RL000002', 'JG00000003', 1, 2, NOW(), DATE_ADD(NOW(), INTERVAL 3650 DAY), 1, '系统管理员-需求中心-查看权限', 'system', NOW(), 'system', NOW(), 0),
('4c3d2e1f0a9b8c7d6e5f4a3b2c1d0e9f', 21, 'RL000002', 'JG00000004', 1, 2, NOW(), DATE_ADD(NOW(), INTERVAL 3650 DAY), 1, '系统管理员-开发中心-查看权限', 'system', NOW(), 'system', NOW(), 0),
('5d4e3f2a1b0c9d8e7f6a5b4c3d2e1f0a', 22, 'RL000002', 'JG00000005', 1, 2, NOW(), DATE_ADD(NOW(), INTERVAL 3650 DAY), 1, '系统管理员-测试中心-查看权限', 'system', NOW(), 'system', NOW(), 0);

-- 管理负责人 (RL000003)：只管理自己所在的机构（系统管理中心）
INSERT INTO `sys_role_org` (`uuid`, `id`, `role_code`, `org_code`, `org_range_type`, `perm_type`, `effective_start`, `effective_end`, `status`, `description`, `created_by`, `created_time`, `updated_by`, `updated_time`, `is_deleted`) VALUES
-- 系统管理中心 (不包含下级机构，管理权限)
('6e5f4a3b2c1d0e9f8a7b6c5d4e3f2a1b', 23, 'RL000003', 'JG00000001', 1, 1, NOW(), DATE_ADD(NOW(), INTERVAL 3650 DAY), 1, '管理负责人-系统管理中心-管理权限', 'system', NOW(), 'system', NOW(), 0),
('7f6a5b4c3d2e1f0a9b8c7d6e5f4a3b2c', 24, 'RL000003', 'JG00000001', 1, 2, NOW(), DATE_ADD(NOW(), INTERVAL 3650 DAY), 1, '管理负责人-系统管理中心-查看权限', 'system', NOW(), 'system', NOW(), 0),
('8a7b6c5d4e3f2a1b0c9d8e7f6a5b4c3d', 25, 'RL000003', 'JG00000001', 1, 3, NOW(), DATE_ADD(NOW(), INTERVAL 3650 DAY), 1, '管理负责人-系统管理中心-操作权限', 'system', NOW(), 'system', NOW(), 0);

-- 部门经理 (RL000009)：管理本部门所在机构
INSERT INTO `sys_role_org` (`uuid`, `id`, `role_code`, `org_code`, `org_range_type`, `perm_type`, `effective_start`, `effective_end`, `status`, `description`, `created_by`, `created_time`, `updated_by`, `updated_time`, `is_deleted`) VALUES
-- 各机构的部门经理只能管理自己的机构
('9b8c7d6e5f4a3b2c1d0e9f8a7b6c5d4e', 26, 'RL000009', 'JG00000002', 1, 1, NOW(), DATE_ADD(NOW(), INTERVAL 3650 DAY), 1, '部门经理-产品中心-管理权限', 'system', NOW(), 'system', NOW(), 0),
('0c9d8e7f6a5b4c3d2e1f0a9b8c7d6e5f', 27, 'RL000009', 'JG00000004', 1, 1, NOW(), DATE_ADD(NOW(), INTERVAL 3650 DAY), 1, '部门经理-开发中心-管理权限', 'system', NOW(), 'system', NOW(), 0),
('1d0e9f8a7b6c5d4e3f2a1b0c9d8e7f6a', 28, 'RL000009', 'JG00000005', 1, 1, NOW(), DATE_ADD(NOW(), INTERVAL 3650 DAY), 1, '部门经理-测试中心-管理权限', 'system', NOW(), 'system', NOW(), 0);

-- 正式访客 (RL000017)：只有查看权限
INSERT INTO `sys_role_org` (`uuid`, `id`, `role_code`, `org_code`, `org_range_type`, `perm_type`, `effective_start`, `effective_end`, `status`, `description`, `created_by`, `created_time`, `updated_by`, `updated_time`, `is_deleted`) VALUES
-- 所有机构只有查看权限
('2e1f0a9b8c7d6e5f4a3b2c1d0e9f8a7b', 29, 'RL000017', 'JG00000001', 1, 2, NOW(), DATE_ADD(NOW(), INTERVAL 3650 DAY), 1, '正式访客-系统管理中心-查看权限', 'system', NOW(), 'system', NOW(), 0),
('3f2a1b0c9d8e7f6a5b4c3d2e1f0a9b8c', 30, 'RL000017', 'JG00000002', 1, 2, NOW(), DATE_ADD(NOW(), INTERVAL 3650 DAY), 1, '正式访客-产品中心-查看权限', 'system', NOW(), 'system', NOW(), 0),
('4a3b2c1d5e6f7a8b9c0d1e2f3a4b5c6e', 31, 'RL000017', 'JG00000003', 1, 2, NOW(), DATE_ADD(NOW(), INTERVAL 3650 DAY), 1, '正式访客-需求中心-查看权限', 'system', NOW(), 'system', NOW(), 0),
('5b4c3d2e1f0a9b8c7d6e5f4a3b2c1d0f', 32, 'RL000017', 'JG00000004', 1, 2, NOW(), DATE_ADD(NOW(), INTERVAL 3650 DAY), 1, '正式访客-开发中心-查看权限', 'system', NOW(), 'system', NOW(), 0),
('6c5d4e3f2a1b0c9d8e7f6a5b4c3d2e1g', 33, 'RL000017', 'JG00000005', 1, 2, NOW(), DATE_ADD(NOW(), INTERVAL 3650 DAY), 1, '正式访客-测试中心-查看权限', 'system', NOW(), 'system', NOW(), 0);

-- ============================================
-- 权限配置说明
-- ============================================

/*
sys_role_org 表字段说明：
1. org_range_type（机构范围类型）：
   - 1: 本机构（只包含指定机构）
   - 2: 包含下级机构（包含指定机构及其所有下级机构）

2. perm_type（权限类型）：
   - 1: 管理（增删改查等所有操作）
   - 2: 查看（只能查看，不能修改）
   - 3: 操作（特定操作权限，如审批、提交等）

权限设计原则：
1. 超级管理员：拥有所有机构的全部权限（管理、查看、操作），包含下级机构
2. 系统管理员：系统管理中心有管理权限，其他机构只有查看权限
3. 管理负责人：只有系统管理中心的管理权限，不包含下级机构
4. 部门经理：只管理自己所在的机构
5. 正式访客：所有机构只有查看权限

权限生效规则：
- 当用户拥有某个角色的机构权限时，才能在该机构范围内执行相应操作
- 权限按角色叠加，取最高权限
- 有效期为10年（3650天），可根据实际需求调整
*/

-- ============================================
-- 数据验证查询
-- ============================================

-- 查询超级管理员的所有机构权限
SELECT 
    r.role_name,
    ro.org_code,
    o.org_name,
    CASE ro.org_range_type 
        WHEN 1 THEN '本机构' 
        WHEN 2 THEN '包含下级机构' 
    END as 机构范围,
    CASE ro.perm_type 
        WHEN 1 THEN '管理' 
        WHEN 2 THEN '查看' 
        WHEN 3 THEN '操作' 
    END as 权限类型,
    ro.effective_start as 生效开始,
    ro.effective_end as 生效结束
FROM sys_role_org ro
JOIN sys_role r ON ro.role_code = r.role_code
JOIN sys_org o ON ro.org_code = o.org_code
WHERE ro.role_code = 'RL000001' 
    AND ro.status = 1 
    AND ro.is_deleted = 0
ORDER BY ro.org_code, ro.perm_type;

-- 查询各角色的机构权限汇总
SELECT 
    r.role_code,
    r.role_name,
    COUNT(DISTINCT ro.org_code) as 机构数量,
    GROUP_CONCAT(DISTINCT 
        CASE ro.perm_type 
            WHEN 1 THEN '管理' 
            WHEN 2 THEN '查看' 
            WHEN 3 THEN '操作' 
        END 
        ORDER BY ro.perm_type
    ) as 权限类型
FROM sys_role_org ro
JOIN sys_role r ON ro.role_code = r.role_code
WHERE ro.status = 1 AND ro.is_deleted = 0
GROUP BY r.role_code, r.role_name
ORDER BY r.sort_order;

-- ============================================
-- 使用场景示例
-- ============================================

/*
场景1：超级管理员创建新用户
- 权限检查：用户aiguibin拥有RL000001角色
- 机构权限：在所有机构上都有管理权限（perm_type=1）
- 操作结果：可以在任何机构下创建用户

场景2：系统管理员查看产品中心的用户
- 权限检查：用户system拥有RL000002角色
- 机构权限：在JG00000002（产品中心）只有查看权限（perm_type=2）
- 操作结果：可以查看产品中心的用户，但不能修改

场景3：管理负责人审批本机构申请
- 权限检查：用户admin拥有RL000003角色
- 机构权限：在JG00000001（系统管理中心）有操作权限（perm_type=3）
- 操作结果：可以审批系统管理中心的申请

场景4：正式访客查看所有机构信息
- 权限检查：访客用户拥有RL000017角色
- 机构权限：在所有机构上都有查看权限（perm_type=2）
- 操作结果：可以查看所有机构的基础信息，但不能进行任何修改
*/


-- ============================================
-- sys_field_permission 表数据补充
-- 限制 current_status 字段只能由 RL000016 角色编辑
-- ============================================

-- 1. 首先在 sys_permission 表中创建一个新的字段控制权限
INSERT INTO `sys_permission` (`uuid`, `id`, `perm_code`, `perm_name`, `perm_key`, `perm_type`, `action_type`, `effect_type`, `condition_expression`, `sort_order`, `status`, `description`, `created_by`, `created_time`, `updated_by`, `updated_time`, `is_deleted`) VALUES
('8d7e6f5a4b3c2d1e0f9a8b7c6d5e4f3a2', 15, 'PERM00000000000015', '编辑变更状态字段', 'business:change:field:current_status:edit', 3, 'UPDATE', 1, NULL, 800, 1, '编辑变更记录当前状态字段权限', 'system', NOW(), 'system', NOW(), 0);

-- 2. 为变更记录表的 current_status 字段创建字段权限控制
INSERT INTO `sys_field_permission` (`uuid`, `id`, `perm_code`, `field_code`, `entity_type`, `field_name`, `field_alias`, `field_type`, `condition_expression`, `default_value`, `validation_rules`, `ui_config`, `sort_order`, `status`, `description`, `created_by`, `created_time`, `updated_by`, `updated_time`, `is_deleted`) VALUES
-- current_status 字段权限配置：默认只读，RL000016 角色可编辑
('9e8f7a6b5c4d3e2f1a0b9c8d7e6f5a4b3', 4, 'PERM00000000000015', 'FIELD000000004', 'biz_change_record', 'current_status', '当前状态', 5, 
-- 条件表达式：只有 RL000016 角色可以编辑，其他角色只读
'{
  "rules": [
    {
      "condition": "user.roles contains \"RL000016\"",
      "field_type": 2
    },
    {
      "condition": "true",
      "field_type": 5
    }
  ],
  "default": 5
}', 
-- 默认值：待审批状态
'PENDING_APPROVAL', 
-- 验证规则：只能从指定状态中选择
'{
  "required": true,
  "enum": [
    {"value": "DRAFT", "label": "草稿"},
    {"value": "PENDING_APPROVAL", "label": "待审批"},
    {"value": "APPROVED", "label": "已通过"},
    {"value": "REJECTED", "label": "已拒绝"},
    {"value": "IMPLEMENTING", "label": "实施中"},
    {"value": "COMPLETED", "label": "已完成"}
  ]
}', 
-- UI配置：下拉选择组件
'{
  "component": "Select",
  "options": [
    {"value": "DRAFT", "label": "草稿", "disabled": false},
    {"value": "PENDING_APPROVAL", "label": "待审批", "disabled": false},
    {"value": "APPROVED", "label": "已通过", "disabled": false},
    {"value": "REJECTED", "label": "已拒绝", "disabled": false},
    {"value": "IMPLEMENTING", "label": "实施中", "disabled": false},
    {"value": "COMPLETED", "label": "已完成", "disabled": true}
  ],
  "allowClear": false,
  "placeholder": "请选择状态"
}', 
200, 1, '变更记录当前状态字段权限控制，只有版本管理角色可以编辑', 'system', NOW(), 'system', NOW(), 0),

-- 3. 同时为其他相关字段设置合理的字段权限（可选，确保数据完整性）
-- 3.1 release_date 字段：版本管理可编辑，其他角色只读
('0f9a8b7c6d5e4f3a2b1c0d9e8f7a6b5c4', 5, 'PERM00000000000015', 'FIELD000000005', 'biz_change_record', 'release_date', '发布日期', 5, 
'{
  "rules": [
    {
      "condition": "user.roles contains \"RL000016\"",
      "field_type": 2
    },
    {
      "condition": "true",
      "field_type": 5
    }
  ],
  "default": 5
}', 
NULL, 
'{"required": false, "type": "date"}', 
'{"component": "DatePicker", "format": "YYYY-MM-DD", "placeholder": "请选择发布日期"}', 
210, 1, '发布日期字段权限控制', 'system', NOW(), 'system', NOW(), 0),

-- 3.2 version 字段：版本管理可编辑，其他角色只读
('1a0b9c8d7e6f5a4b3c2d1e0f9a8b7c6d5', 6, 'PERM00000000000015', 'FIELD000000006', 'biz_change_record', 'version', '版本号', 5, 
'{
  "rules": [
    {
      "condition": "user.roles contains \"RL000016\"",
      "field_type": 2
    },
    {
      "condition": "true",
      "field_type": 5
    }
  ],
  "default": 5
}', 
NULL, 
'{"required": false, "pattern": "^v\\d+\\.\\d+\\.\\d+$", "message": "版本号格式应为 vX.Y.Z"}', 
'{"component": "Input", "placeholder": "请输入版本号，如：v1.0.0"}', 
220, 1, '版本号字段权限控制', 'system', NOW(), 'system', NOW(), 0);

-- 4. 在 sys_perm_resource 表中关联字段资源
INSERT INTO `sys_perm_resource` (`uuid`, `id`, `perm_code`, `resource_type`, `resource_key`, `resource_sub_type`, `relation_type`, `condition_expression`, `status`, `description`, `created_by`, `created_time`, `updated_by`, `updated_time`, `is_deleted`) VALUES
-- 关联 current_status 字段
('2b1c0d9e8f7a6b5c4d3e2f1a0b9c8d7e6', 20, 'PERM00000000000015', 'FIELD', 'biz_change_record.current_status', 'STATUS', 1, NULL, 1, '变更记录状态字段权限关联', 'system', NOW(), 'system', NOW(), 0),
-- 关联 release_date 字段
('3c2d1e0f9a8b7c6d5e4f3a2b1c0d9e8f7', 21, 'PERM00000000000015', 'FIELD', 'biz_change_record.release_date', 'DATE', 1, NULL, 1, '发布日期字段权限关联', 'system', NOW(), 'system', NOW(), 0),
-- 关联 version 字段
('4d3e2f1a0b9c8d7e6f5a4b3c2d1e0f9a8', 22, 'PERM00000000000015', 'FIELD', 'biz_change_record.version', 'VERSION', 1, NULL, 1, '版本号字段权限关联', 'system', NOW(), 'system', NOW(), 0);

-- 5. 在 sys_role_permission 表中为 RL000016 角色分配该权限
INSERT INTO `sys_role_permission` (`uuid`, `id`, `role_code`, `perm_code`, `auth_type`, `effective_start`, `effective_end`, `priority`, `status`, `description`, `created_by`, `created_time`, `updated_by`, `updated_time`, `is_deleted`) VALUES
-- RL000016 版本管理角色拥有编辑权限
('5e4f3a2b1c0d9e8f7a6b5c4d3e2f1a0b9', 37, 'RL000016', 'PERM00000000000015', 1, NOW(), DATE_ADD(NOW(), INTERVAL 3650 DAY), 10, 1, '版本管理-编辑变更状态字段权限', 'system', NOW(), 'system', NOW(), 0),

-- 6. 为其他相关角色设置禁止编辑这些字段（可选，确保安全性）
-- 6.1 超级管理员可以编辑（如果需要）
('6f5a4b3c2d1e0f9a8b7c6d5e4f3a2b1c0', 38, 'RL000001', 'PERM00000000000015', 1, NOW(), DATE_ADD(NOW(), INTERVAL 3650 DAY), 1, 1, '超级管理员-编辑变更状态字段权限', 'system', NOW(), 'system', NOW(), 0),

-- 6.2 系统管理员禁止编辑（拒绝权限）
('7a6b5c4d3e2f1a0b9c8d7e6f5a4b3c2d1', 39, 'RL000002', 'PERM00000000000015', 2, NOW(), DATE_ADD(NOW(), INTERVAL 3650 DAY), 20, 1, '系统管理员-禁止编辑变更状态字段', 'system', NOW(), 'system', NOW(), 0),

-- 6.3 普通开发角色禁止编辑（拒绝权限）
('8b7c6d5e4f3a2b1c0d9e8f7a6b5c4d3e2', 40, 'RL000013', 'PERM00000000000015', 2, NOW(), DATE_ADD(NOW(), INTERVAL 3650 DAY), 30, 1, '高级开发-禁止编辑变更状态字段', 'system', NOW(), 'system', NOW(), 0);

-- ============================================
-- 数据验证和查询
-- ============================================

-- 1. 查询创建的字段权限
SELECT 
    fp.field_code,
    fp.entity_type,
    fp.field_name,
    fp.field_alias,
    CASE fp.field_type 
        WHEN 1 THEN '可见' 
        WHEN 2 THEN '可编辑' 
        WHEN 3 THEN '必填' 
        WHEN 4 THEN '隐藏' 
        WHEN 5 THEN '只读' 
    END as 字段权限类型,
    fp.condition_expression->>'$.rules[0].condition' as 编辑条件,
    fp.validation_rules->>'$.enum' as 允许的状态值,
    fp.ui_config->>'$.component' as UI组件
FROM sys_field_permission fp
WHERE fp.perm_code = 'PERM00000000000015'
    AND fp.status = 1
    AND fp.is_deleted = 0
ORDER BY fp.sort_order;

-- 2. 查询哪些角色拥有编辑权限
SELECT 
    r.role_code,
    r.role_name,
    rp.auth_type,
    CASE rp.auth_type 
        WHEN 1 THEN '允许' 
        WHEN 2 THEN '拒绝' 
    END as 授权类型,
    rp.description as 权限描述
FROM sys_role_permission rp
JOIN sys_role r ON rp.role_code = r.role_code
WHERE rp.perm_code = 'PERM00000000000015'
    AND rp.status = 1
    AND rp.is_deleted = 0
ORDER BY rp.auth_type, rp.priority;

-- 3. 查询完整的权限关联关系
SELECT 
    p.perm_code,
    p.perm_name,
    p.perm_key,
    r.role_code,
    r.role_name,
    pr.resource_type,
    pr.resource_key,
    fp.field_name,
    fp.field_alias,
    CASE fp.field_type 
        WHEN 1 THEN '可见' 
        WHEN 2 THEN '可编辑' 
        WHEN 3 THEN '必填' 
        WHEN 4 THEN '隐藏' 
        WHEN 5 THEN '只读' 
    END as 字段权限
FROM sys_permission p
JOIN sys_role_permission rp ON p.perm_code = rp.perm_code
JOIN sys_role r ON rp.role_code = r.role_code
LEFT JOIN sys_perm_resource pr ON p.perm_code = pr.perm_code
LEFT JOIN sys_field_permission fp ON p.perm_code = fp.perm_code
WHERE p.perm_key = 'business:change:field:current_status:edit'
    AND p.status = 1
    AND p.is_deleted = 0
ORDER BY r.role_code, fp.field_name;

-- ============================================
-- 使用场景说明
-- ============================================

/*
权限控制逻辑说明：

1. 字段权限类型 (field_type) 说明：
   - 1: 可见
   - 2: 可编辑
   - 3: 必填
   - 4: 隐藏
   - 5: 只读

2. 条件表达式 (condition_expression) 说明：
   这是一个JSON结构，包含多个规则：
   {
     "rules": [
       {
         "condition": "user.roles contains \"RL000016\"",
         "field_type": 2  // 如果用户拥有RL000016角色，字段可编辑
       },
       {
         "condition": "true",  // 默认条件，总是匹配
         "field_type": 5       // 其他用户字段只读
       }
     ],
     "default": 5  // 默认权限类型
   }

3. 权限检查流程：
   当用户访问变更记录编辑界面时：
   1. 系统查询用户的所有角色
   2. 检查用户是否有 PERM00000000000015 权限
   3. 如果有，检查权限的 auth_type：
      - auth_type=1：允许
      - auth_type=2：拒绝（覆盖其他允许权限）
   4. 根据字段权限的条件表达式计算最终权限
   5. 前端根据计算结果渲染字段状态

4. 实际效果：
   - RL000016（版本管理）角色：可以编辑 current_status、release_date、version 字段
   - RL000001（超级管理员）角色：可以编辑这些字段（如果需要）
   - RL000002（系统管理员）角色：不能编辑这些字段（auth_type=2）
   - RL000013（高级开发）角色：不能编辑这些字段（auth_type=2）
   - 其他角色：字段只读（条件表达式默认规则）

5. 验证规则：
   - current_status 字段：只能从预定义的6个状态值中选择
   - release_date 字段：必须是有效日期
   - version 字段：必须符合 vX.Y.Z 格式

这样设计确保了只有版本管理角色（RL000016）能够修改变更记录的关键状态信息，
其他角色即使有编辑权限，也会被条件表达式或拒绝权限覆盖。
*/

-- ============================================
-- 权限检查函数示例（业务逻辑层）
-- ============================================

/*
示例Java代码：

public class ChangeRecordService {
    
    @Autowired
    private PermissionService permissionService;
    
    @Autowired
    private FieldPermissionService fieldPermissionService;
    
    /**
     * 检查用户是否可以编辑变更记录的某个字段
     */
    public boolean canEditField(User user, String entityType, String fieldName) {
        // 1. 检查用户是否有编辑权限
        if (!permissionService.hasPermission(user, "business:change:field:current_status:edit")) {
            return false;
        }
        
        // 2. 检查字段权限规则
        FieldPermission fieldPermission = fieldPermissionService.getFieldPermission(
            entityType, fieldName, user.getUserNum()
        );
        
        // 3. 根据条件表达式计算最终权限
        return fieldPermissionService.evaluatePermission(fieldPermission, user);
    }
    
    /**
     * 根据用户权限过滤字段
     */
    public ChangeRecordDTO filterFieldsByPermission(ChangeRecord record, User user) {
        ChangeRecordDTO dto = convertToDTO(record);
        
        // 获取用户对变更记录所有字段的权限
        Map<String, FieldPermission> fieldPermissions = fieldPermissionService
            .getFieldPermissionsForEntity("biz_change_record", user.getUserNum());
        
        // 根据权限设置字段可编辑性
        for (FieldPermission fp : fieldPermissions.values()) {
            switch (fp.getCalculatedPermission()) {
                case 2: // 可编辑
                    setFieldEditable(dto, fp.getFieldName());
                    break;
                case 5: // 只读
                    setFieldReadOnly(dto, fp.getFieldName());
                    break;
                case 4: // 隐藏
                    hideField(dto, fp.getFieldName());
                    break;
            }
        }
        
        return dto;
    }
}
*/

-- ============================================
-- 前端权限检查示例（Vue.js）
-- ============================================

/*
示例Vue代码：

<template>
  <el-form :model="changeRecord" :rules="rules" ref="form">
    <!-- 当前状态字段 -->
    <el-form-item label="当前状态" prop="current_status">
      <el-select 
        v-model="changeRecord.current_status"
        :disabled="!canEditField('current_status')"
        placeholder="请选择状态">
        <el-option 
          v-for="option in statusOptions"
          :key="option.value"
          :label="option.label"
          :value="option.value"
          :disabled="option.disabled">
        </el-option>
      </el-select>
    </el-form-item>
    
    <!-- 发布日期字段 -->
    <el-form-item label="发布日期" prop="release_date">
      <el-date-picker
        v-model="changeRecord.release_date"
        :disabled="!canEditField('release_date')"
        type="date"
        placeholder="选择发布日期"
        value-format="yyyy-MM-dd">
      </el-date-picker>
    </el-form-item>
    
    <!-- 版本号字段 -->
    <el-form-item label="版本号" prop="version">
      <el-input
        v-model="changeRecord.version"
        :disabled="!canEditField('version')"
        placeholder="请输入版本号，如：v1.0.0">
      </el-input>
    </el-form-item>
  </el-form>
</template>

<script>
export default {
  data() {
    return {
      changeRecord: {},
      statusOptions: [
        { value: 'DRAFT', label: '草稿' },
        { value: 'PENDING_APPROVAL', label: '待审批' },
        { value: 'APPROVED', label: '已通过' },
        { value: 'REJECTED', label: '已拒绝' },
        { value: 'IMPLEMENTING', label: '实施中' },
        { value: 'COMPLETED', label: '已完成', disabled: true }
      ],
      fieldPermissions: {}
    }
  },
  created() {
    // 加载字段权限
    this.loadFieldPermissions();
  },
  methods: {
    async loadFieldPermissions() {
      // 调用API获取用户对变更记录字段的权限
      const response = await this.$api.getFieldPermissions('biz_change_record');
      if (response.success) {
        this.fieldPermissions = response.data;
      }
    },
    canEditField(fieldName) {
      // 检查字段是否可编辑
      const permission = this.fieldPermissions[fieldName];
      if (!permission) {
        return false;
      }
      return permission.field_type === 2; // 2表示可编辑
    }
  }
}
</script>
*/

-- ============================================
-- 为 RL000010（项目大组长）和 RL000011（项目小组长）角色
-- 添加编辑 current_status 字段的权限
-- ============================================

-- 1. 首先检查这两个角色是否已经存在
SELECT role_code, role_name FROM sys_role 
WHERE role_code IN ('RL000010', 'RL000011') 
AND status = 1 AND is_deleted = 0;

-- 2. 更新 sys_field_permission 表中的条件表达式
--    将 RL000010 和 RL000011 添加到可编辑角色的条件中
UPDATE sys_field_permission 
SET condition_expression = 
'{
  "rules": [
    {
      "condition": "user.roles contains \"RL000016\" or user.roles contains \"RL000010\" or user.roles contains \"RL000011\"",
      "field_type": 2
    },
    {
      "condition": "true",
      "field_type": 5
    }
  ],
  "default": 5
}'
WHERE field_name = 'current_status' 
AND entity_type = 'biz_change_record'
AND perm_code = 'PERM00000000000015';

-- 3. 为 RL000010（项目大组长）角色分配权限
-- 检查是否已有权限分配
SELECT * FROM sys_role_permission 
WHERE role_code = 'RL000010' 
AND perm_code = 'PERM00000000000015';

-- 如果没有，则添加允许权限
INSERT INTO sys_role_permission (`uuid`, `id`, `role_code`, `perm_code`, `auth_type`, `effective_start`, `effective_end`, `priority`, `status`, `description`, `created_by`, `created_time`, `updated_by`, `updated_time`, `is_deleted`)
SELECT 
    UUID() as uuid,
    (SELECT MAX(id) + 1 FROM sys_role_permission) as id,
    'RL000010' as role_code,
    'PERM00000000000015' as perm_code,
    1 as auth_type,
    NOW() as effective_start,
    DATE_ADD(NOW(), INTERVAL 3650 DAY) as effective_end,
    15 as priority,  -- 优先级设置，介于超级管理员(1)和版本管理(10)之间
    1 as status,
    '项目大组长-编辑变更状态字段权限' as description,
    'system' as created_by,
    NOW() as created_time,
    'system' as updated_by,
    NOW() as updated_time,
    0 as is_deleted
FROM dual
WHERE NOT EXISTS (
    SELECT 1 FROM sys_role_permission 
    WHERE role_code = 'RL000010' 
    AND perm_code = 'PERM00000000000015'
);

-- 4. 为 RL000011（项目小组长）角色分配权限
-- 检查是否已有权限分配
SELECT * FROM sys_role_permission 
WHERE role_code = 'RL000011' 
AND perm_code = 'PERM00000000000015';

-- 如果没有，则添加允许权限
INSERT INTO sys_role_permission (`uuid`, `id`, `role_code`, `perm_code`, `auth_type`, `effective_start`, `effective_end`, `priority`, `status`, `description`, `created_by`, `created_time`, `updated_by`, `updated_time`, `is_deleted`)
SELECT 
    UUID() as uuid,
    (SELECT MAX(id) + 1 FROM sys_role_permission) as id,
    'RL000011' as role_code,
    'PERM00000000000015' as perm_code,
    1 as auth_type,
    NOW() as effective_start,
    DATE_ADD(NOW(), INTERVAL 3650 DAY) as effective_end,
    20 as priority,  -- 优先级设置，低于项目大组长(15)
    1 as status,
    '项目小组长-编辑变更状态字段权限' as description,
    'system' as created_by,
    NOW() as created_time,
    'system' as updated_by,
    NOW() as updated_time,
    0 as is_deleted
FROM dual
WHERE NOT EXISTS (
    SELECT 1 FROM sys_role_permission 
    WHERE role_code = 'RL000011' 
    AND perm_code = 'PERM00000000000015'
);

-- ============================================
-- 如果需要，为这些角色添加其他相关字段的编辑权限
-- ============================================

-- 5. 更新 release_date 字段的条件表达式
UPDATE sys_field_permission 
SET condition_expression = 
'{
  "rules": [
    {
      "condition": "user.roles contains \"RL000016\" or user.roles contains \"RL000010\" or user.roles contains \"RL000011\"",
      "field_type": 2
    },
    {
      "condition": "true",
      "field_type": 5
    }
  ],
  "default": 5
}'
WHERE field_name = 'release_date' 
AND entity_type = 'biz_change_record'
AND perm_code = 'PERM00000000000015';

-- 6. 更新 version 字段的条件表达式
UPDATE sys_field_permission 
SET condition_expression = 
'{
  "rules": [
    {
      "condition": "user.roles contains \"RL000016\" or user.roles contains \"RL000010\" or user.roles contains \"RL000011\"",
      "field_type": 2
    },
    {
      "condition": "true",
      "field_type": 5
    }
  ],
  "default": 5
}'
WHERE field_name = 'version' 
AND entity_type = 'biz_change_record'
AND perm_code = 'PERM00000000000015';

-- ============================================
-- 数据验证和查询
-- ============================================

-- 7. 验证更新后的条件表达式
SELECT 
    field_name,
    field_alias,
    condition_expression->>'$.rules[0].condition' as 可编辑条件,
    condition_expression->>'$.rules[1].condition' as 默认条件,
    condition_expression->>'$.default' as 默认权限类型
FROM sys_field_permission 
WHERE perm_code = 'PERM00000000000015'
ORDER BY sort_order;

-- 8. 查询所有可以编辑这些字段的角色
SELECT 
    r.role_code,
    r.role_name,
    rp.auth_type,
    CASE rp.auth_type 
        WHEN 1 THEN '允许' 
        WHEN 2 THEN '拒绝' 
    END as 授权类型,
    rp.priority as 优先级,
    rp.description as 权限描述
FROM sys_role_permission rp
JOIN sys_role r ON rp.role_code = r.role_code
WHERE rp.perm_code = 'PERM00000000000015'
    AND rp.status = 1
    AND rp.is_deleted = 0
ORDER BY rp.priority, rp.role_code;

-- 9. 查询所有角色及其对这些字段的权限状态
SELECT 
    r.role_code,
    r.role_name,
    CASE 
        WHEN EXISTS (
            SELECT 1 FROM sys_role_permission rp2 
            WHERE rp2.role_code = r.role_code 
            AND rp2.perm_code = 'PERM00000000000015'
            AND rp2.auth_type = 1
            AND rp2.status = 1
        ) THEN '允许编辑'
        WHEN EXISTS (
            SELECT 1 FROM sys_role_permission rp2 
            WHERE rp2.role_code = r.role_code 
            AND rp2.perm_code = 'PERM00000000000015'
            AND rp2.auth_type = 2
            AND rp2.status = 1
        ) THEN '拒绝编辑'
        ELSE '无权限配置'
    END as 权限状态
FROM sys_role r
WHERE r.status = 1 AND r.is_deleted = 0
ORDER BY r.sort_order;

-- -- 10. 创建视图查看详细的字段权限配置
-- CREATE OR REPLACE VIEW v_field_permission_detail AS
-- SELECT 
--     r.role_code,
--     r.role_name,
--     p.perm_name,
--     p.perm_key,
--     fp.entity_type,
--     fp.field_name,
--     fp.field_alias,
--     CASE 
--         WHEN rp.auth_type = 1 AND (
--             fp.condition_expression->>'$.rules[0].condition' LIKE CONCAT('%', r.role_code, '%') OR
--             r.role_code IN ('RL000001', 'RL000016', 'RL000010', 'RL000011')
--         ) THEN '可编辑'
--         WHEN rp.auth_type = 2 THEN '拒绝编辑'
--         ELSE '只读'
--     END as 实际权限,
--     fp.validation_rules->>'$.enum' as 允许的状态值,
--     fp.ui_config->>'$.component' as UI组件
-- FROM sys_role r
-- CROSS JOIN sys_field_permission fp
-- LEFT JOIN sys_role_permission rp ON r.role_code = rp.role_code 
--     AND rp.perm_code = fp.perm_code
-- LEFT JOIN sys_permission p ON fp.perm_code = p.perm_code
-- WHERE fp.entity_type = 'biz_change_record'
--     AND fp.field_name IN ('current_status', 'release_date', 'version')
--     AND r.status = 1
--     AND fp.status = 1
--     AND (rp.status = 1 OR rp.status IS NULL)
-- ORDER BY r.sort_order, fp.field_name;

-- -- 11. 使用视图查询
-- SELECT * FROM v_field_permission_detail 
-- WHERE role_code IN ('RL000001', 'RL000002', 'RL000010', 'RL000011', 'RL000013', 'RL000016')
-- ORDER BY role_code, field_name;

-- ============================================
-- 权限检查逻辑优化
-- ============================================

-- 12. 创建存储过程或函数来检查字段权限（可选）
/*
DELIMITER //

CREATE FUNCTION check_field_permission(
    p_user_num VARCHAR(20),
    p_entity_type VARCHAR(50),
    p_field_name VARCHAR(100)
) RETURNS INT
BEGIN
    DECLARE v_field_type INT DEFAULT 5; -- 默认只读
    
    -- 获取用户的角色列表
    SET @user_roles = (
        SELECT GROUP_CONCAT(DISTINCT ur.role_code) 
        FROM sys_user_role ur 
        WHERE ur.user_num = p_user_num 
        AND ur.status = 1
    );
    
    -- 查询字段权限配置
    SELECT 
        CASE 
            WHEN LOCATE('RL000016', @user_roles) > 0 
                OR LOCATE('RL000010', @user_roles) > 0 
                OR LOCATE('RL000011', @user_roles) > 0 THEN 2 -- 可编辑
            ELSE 5 -- 只读
        END INTO v_field_type
    FROM sys_field_permission fp
    WHERE fp.entity_type = p_entity_type
        AND fp.field_name = p_field_name
        AND fp.status = 1
    LIMIT 1;
    
    RETURN IFNULL(v_field_type, 5);
END//

DELIMITER ;
*/

-- ============================================
-- 更新后的权限配置总结
-- ============================================

/*
更新后的权限配置：

1. 可以编辑 current_status、release_date、version 字段的角色：
   - RL000001（超级管理员）：优先级 1，拥有所有权限
   - RL000016（版本管理）：优先级 10，主要管理角色
   - RL000010（项目大组长）：优先级 15，项目层面管理
   - RL000011（项目小组长）：优先级 20，具体项目执行

2. 被拒绝编辑的角色：
   - RL000002（系统管理员）：明确拒绝
   - RL000013（高级开发）：明确拒绝

3. 其他角色：
   - 默认只读权限（通过条件表达式控制）

权限检查流程：
1. 用户访问变更记录编辑页面
2. 系统查询用户的角色列表
3. 检查字段权限的条件表达式：
   - 如果用户角色包含 RL000016、RL000010 或 RL000011，字段可编辑
   - 否则，字段只读
4. 同时检查角色权限分配表中的拒绝权限（auth_type=2）：
   - 如果用户角色被分配了拒绝权限，即使条件表达式满足也拒绝编辑

实际应用场景：
- 版本管理员（RL000016）：负责整体版本管理和状态控制
- 项目大组长（RL000010）：负责项目层面的进度和状态管理
- 项目小组长（RL000011）：负责具体项目任务的执行和状态更新
- 超级管理员（RL000001）：系统管理，需要时可以介入

这种设计实现了分层级的权限管理：
1. 版本管理员：全局状态控制
2. 项目大组长：项目级状态控制
3. 项目小组长：任务级状态更新
4. 普通开发人员：只能查看状态，不能修改
*/

-- ============================================
-- 清理和优化
-- ============================================

-- 删除临时视图（如果需要）
-- DROP VIEW IF EXISTS v_field_permission_detail;

-- 查看当前所有相关的权限配置
SELECT 
    '角色数量' as 类别, COUNT(*) as 数量 FROM sys_role WHERE status = 1 AND is_deleted = 0
UNION ALL
SELECT 
    '拥有编辑权限的角色', COUNT(DISTINCT rp.role_code) 
FROM sys_role_permission rp
WHERE rp.perm_code = 'PERM00000000000015' 
    AND rp.auth_type = 1 
    AND rp.status = 1
UNION ALL
SELECT 
    '被拒绝编辑的角色', COUNT(DISTINCT rp.role_code) 
FROM sys_role_permission rp
WHERE rp.perm_code = 'PERM00000000000015' 
    AND rp.auth_type = 2 
    AND rp.status = 1;

-- 启用外键检查
SET FOREIGN_KEY_CHECKS = 1;

-- ============================================
-- 初始化完成
-- ============================================
SELECT 'SYS系统初始化数据插入完成！' AS '初始化结果';
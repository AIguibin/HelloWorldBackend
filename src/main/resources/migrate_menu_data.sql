-- 菜单数据迁移脚本
-- 基于MainLayout.vue中的topMenuList模拟数据生成

-- 先清空现有菜单数据（保留ID=9的修改密码菜单）
DELETE FROM `sys_menu` WHERE `id` NOT IN (9);
DELETE FROM `sys_role_menu` WHERE `menu_id` NOT IN (9);

-- 插入主菜单数据
INSERT INTO `sys_menu` (`id`, `parent_id`, `menu_type`, `menu_name`, `menu_code`, `path`, `component`, `icon`, `perms`, `sort_order`, `is_visible`, `status`)
VALUES
-- 首页
(101, 0, 1, '首页', 'home', '/', NULL, 'el-icon-s-home', NULL, 1, 1, 1),
(102, 101, 2, '首页概览', 'home_overview', '/', 'views/Home', NULL, 'home:overview', 1, 1, 1),
-- 变更管理
(201, 0, 1, '变更管理', 'change_management', '/change-records', NULL, 'el-icon-edit-outline', NULL, 2, 1, 1),
(202, 201, 2, '变更记录管理', 'change_record_manage', '/change-records', 'views/ChangeRecordList', NULL, 'change:record:list', 1, 1, 1),
(203, 202, 3, '新增', 'change_record_create', NULL, NULL, NULL, 'change:record:create', 1, 1, 1),
(204, 202, 3, '编辑', 'change_record_edit', NULL, NULL, NULL, 'change:record:edit', 2, 1, 1),
(205, 202, 3, '删除', 'change_record_delete', NULL, NULL, NULL, 'change:record:delete', 3, 1, 1),
(206, 202, 3, '导出', 'change_record_export', NULL, NULL, NULL, 'change:record:export', 4, 1, 1),
(207, 202, 2, '变更记录详情', 'change_record_detail', '/change-records/:id', 'views/ChangeRecordDetail', NULL, 'change:record:detail', 1, 0, 1),
(208, 202, 2, '变更记录历史', 'change_record_history', '/change-records/:id/history', 'views/ChangeRecordHistory', NULL, 'change:record:history', 2, 0, 1),
-- 版本管理
(301, 0, 1, '版本管理', 'version_management', '/versions', NULL, 'el-icon-document-checked', NULL, 3, 1, 1),
(302, 301, 2, '版本列表', 'version_list', '/versions', 'views/VersionList', NULL, 'version:list', 1, 1, 1),
(303, 301, 2, '版本详情', 'version_detail', '/versions/:id', 'views/VersionDetail', NULL, 'version:detail', 2, 0, 1),
-- 开发标准
(401, 0, 1, '开发标准', 'development_standards', '/development-standards', NULL, 'el-icon-finished', NULL, 4, 1, 1),
(402, 401, 2, '标准文档', 'standard_documents', '/development-standards', 'views/DevelopmentStandards', NULL, 'standard:documents', 1, 1, 1),
-- 系统设置
(501, 0, 1, '系统设置', 'system_settings', '/system-settings', NULL, 'el-icon-setting', NULL, 5, 1, 1),
(502, 501, 2, '用户管理', 'user_management', '/system-settings/users', 'views/UserManagement', NULL, 'user:management', 1, 1, 1),
(503, 501, 2, '角色管理', 'role_management', '/system-settings/roles', 'views/RoleManagement', NULL, 'role:management', 2, 1, 1),
(504, 501, 2, '菜单管理', 'menu_management', '/system-settings/menus', 'views/MenuManagement', NULL, 'menu:management', 3, 1, 1),
(505, 501, 2, '字典管理', 'dict_management', '/system-settings/dicts', 'views/DictManagement', NULL, 'dict:management', 4, 1, 1),
(506, 501, 2, '操作日志', 'operation_logs', '/system-settings/logs', 'views/OperationLogs', NULL, 'log:operation', 5, 1, 1);

-- 为超级管理员角色分配所有新菜单权限
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
SELECT 1, id FROM `sys_menu`;

-- 验证插入结果
SELECT '菜单数据迁移完成' AS `status`;
SELECT COUNT(*) AS `menu_count` FROM `sys_menu`;
SELECT COUNT(*) AS `role_menu_count` FROM `sys_role_menu`;
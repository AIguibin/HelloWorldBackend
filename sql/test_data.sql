-- 1. 添加测试用户（使用英雄联盟英雄名称）
INSERT INTO sys_user (`uuid`,`id`,`user_num`,`user_name`,`nickname`,`gender`,`password`,`salt`,`org_code`,`dept_code`,`email`,`phone`,`avatar`,`last_login_time`,`last_login_ip`,`login_count`,`status`,`is_locked`,`lock_time`,`lock_reason`,`is_special`,`pwd_expire_time`,`pwd_modified_time`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES 
(REPLACE(UUID(), '-', ''), 8, 'yasuo', '亚索', '疾风剑豪', 1, '$2a$10$abcdefghijklmnopqrstuvwxyz0123456789abcdefghijklmnopq','abcdefghijklmnopqrstuvwx','JG00000004','JG00000004D0001','yasuo@company.com','13800138007','/avatar/user5.png',NULL,NULL,'0',1,0,NULL,NULL,0,'2026-03-20 07:55:44','2025-12-20 07:55:44','system','2025-12-20 07:55:44','system','2025-12-20 15:20:10',0),
(REPLACE(UUID(), '-', ''), 9, 'leeSin', '李青', '盲僧', 1, '$2a$10$abcdefghijklmnopqrstuvwxyz0123456789abcdefghijklmnopq','abcdefghijklmnopqrstuvwx','JG00000004','JG00000004D0001','leeSin@company.com','13800138008','/avatar/user6.png',NULL,NULL,'0',1,0,NULL,NULL,0,'2026-03-20 07:55:44','2025-12-20 07:55:44','system','2025-12-20 07:55:44','system','2025-12-20 15:20:10',0),
(REPLACE(UUID(), '-', ''), 10, 'ryze', '瑞兹', '流浪法师', 1, '$2a$10$abcdefghijklmnopqrstuvwxyz0123456789abcdefghijklmnopq','abcdefghijklmnopqrstuvwx','JG00000004','JG00000004D0001','ryze@company.com','13800138009','/avatar/user7.png',NULL,NULL,'0',1,0,NULL,NULL,0,'2026-03-20 07:55:44','2025-12-20 07:55:44','system','2025-12-20 07:55:44','system','2025-12-20 15:20:10',0),
(REPLACE(UUID(), '-', ''), 11, 'karma', '卡尔玛', '天启者', 2, '$2a$10$abcdefghijklmnopqrstuvwxyz0123456789abcdefghijklmnopq','abcdefghijklmnopqrstuvwx','JG00000004','JG00000004D0001','karma@company.com','13800138010','/avatar/user8.png',NULL,NULL,'0',1,0,NULL,NULL,0,'2026-03-20 07:55:44','2025-12-20 07:55:44','system','2025-12-20 07:55:44','system','2025-12-20 15:20:10',0),
(REPLACE(UUID(), '-', ''), 12, 'sivir', '希维尔', '战争女神', 2, '$2a$10$abcdefghijklmnopqrstuvwxyz0123456789abcdefghijklmnopq','abcdefghijklmnopqrstuvwx','JG00000004','JG00000001D0001','sivir@company.com','13800138011','/avatar/user9.png',NULL,NULL,'0',1,0,NULL,NULL,0,'2026-03-20 07:55:44','2025-12-20 07:55:44','system','2025-12-20 07:55:44','system','2025-12-20 15:20:10',0),
(REPLACE(UUID(), '-', ''), 13, 'gwen', '格温', '痛苦之拥', 2, '$2a$10$abcdefghijklmnopqrstuvwxyz0123456789abcdefghijklmnopq','abcdefghijklmnopqrstuvwx','JG00000004','JG00000001D0001','gwen@company.com','13800138012','/avatar/user10.png',NULL,NULL,'0',1,0,NULL,NULL,0,'2026-03-20 07:55:44','2025-12-20 07:55:44','system','2025-12-20 07:55:44','system','2025-12-20 15:20:10',0);

-- 2. 添加用户角色关联
INSERT INTO sys_user_role (`uuid`,`id`,`user_num`,`role_code`,`role_name`,`is_primary`,`effective_start`,`effective_end`,`status`,`description`,`created_by`,`created_time`,`updated_by`,`updated_time`,`is_deleted`)  VALUES 
(REPLACE(UUID(), '-', ''), 8, 'yasuo', 'RL000013', '高级开发', 1, '2025-12-20 07:55:44', '2035-12-18 07:55:44', 1, '高级开发主角色', 'system', '2025-12-20 07:55:44', 'system', '2025-12-20 07:55:44', 0),
(REPLACE(UUID(), '-', ''), 9, 'leeSin', 'RL000011', '项目小组长', 1, '2025-12-20 07:55:44', '2035-12-18 07:55:44', 1, '项目小组长主角色', 'system', '2025-12-20 07:55:44', 'system', '2025-12-20 07:55:44', 0),
(REPLACE(UUID(), '-', ''), 10, 'ryze', 'RL000010', '项目大组长', 1, '2025-12-20 07:55:44', '2035-12-18 07:55:44', 1, '项目大组长主角色', 'system', '2025-12-20 07:55:44', 'system', '2025-12-20 07:55:44', 0),
(REPLACE(UUID(), '-', ''), 11, 'karma', 'RL000012', '架构师', 1, '2025-12-20 07:55:44', '2035-12-18 07:55:44', 1, '架构师主角色', 'system', '2025-12-20 07:55:44', 'system', '2025-12-20 07:55:44', 0),
(REPLACE(UUID(), '-', ''), 12, 'sivir', 'RL000016', '版本管理', 1, '2025-12-20 07:55:44', '2035-12-18 07:55:44', 1, '版本管理主角色', 'system', '2025-12-20 07:55:44', 'system', '2025-12-20 07:55:44', 0),
(REPLACE(UUID(), '-', ''), 13, 'gwen', 'RL000018', '运维管理', 1, '2025-12-20 07:55:44', '2035-12-18 07:55:44', 1, '运维管理主角色', 'system', '2025-12-20 07:55:44', 'system', '2025-12-20 07:55:44', 0);

-- 3. 添加审批流程
INSERT INTO biz_approval_flow (`uuid`, `flow_id`, `flow_name`, `business_type`, `is_active`, `is_default`, `created_by`, `created_time`, `updated_by`, `updated_time`, `is_deleted`) VALUES
(REPLACE(UUID(), '-', ''), 'FLOW_CHG_DEFAULT', '变更记录默认流程', 'CHANGE_RECORD', 1, 1, 'system', '2025-12-20 07:55:44', 'system', '2025-12-20 07:55:44', 0),
(REPLACE(UUID(), '-', ''), 'FLOW_RELEASE_DEFAULT', '发版记录默认流程', 'RELEASE', 1, 1, 'system', '2025-12-20 07:55:44', 'system', '2025-12-20 07:55:44', 0);

-- 4. 添加审批节点（6节点流程）
INSERT INTO biz_approval_node (`uuid`, `node_id`, `flow_id`, `node_name`, `node_order`, `node_type`, `approver_num`, `approver_name`, `is_active`, `created_by`, `created_time`, `updated_by`, `updated_time`, `is_deleted`) VALUES
-- 变更记录流程节点
(REPLACE(UUID(), '-', ''), 'NODE_DEV_APPLY', 'FLOW_CHG_DEFAULT', '开发申请', 1, 'START', 'yasuo', '亚索', 1, 'system', '2025-12-20 07:55:44', 'system', '2025-12-20 07:55:44', 0),
(REPLACE(UUID(), '-', ''), 'NODE_TEAM_LEAD', 'FLOW_CHG_DEFAULT', '小组长审核', 2, 'NORMAL', 'leeSin', '李青', 1, 'system', '2025-12-20 07:55:44', 'system', '2025-12-20 07:55:44', 0),
(REPLACE(UUID(), '-', ''), 'NODE_GROUP_LEAD', 'FLOW_CHG_DEFAULT', '大组长审核', 3, 'NORMAL', 'ryze', '瑞兹', 1, 'system', '2025-12-20 07:55:44', 'system', '2025-12-20 07:55:44', 0),
(REPLACE(UUID(), '-', ''), 'NODE_ARCH_REVIEW', 'FLOW_CHG_DEFAULT', '技术架构评审', 4, 'NORMAL', 'karma', '卡尔玛', 1, 'system', '2025-12-20 07:55:44', 'system', '2025-12-20 07:55:44', 0),
(REPLACE(UUID(), '-', ''), 'NODE_VERSION_CONFIRM', 'FLOW_CHG_DEFAULT', '版本管理员确认', 5, 'NORMAL', 'sivir', '希维尔', 1, 'system', '2025-12-20 07:55:44', 'system', '2025-12-20 07:55:44', 0),
(REPLACE(UUID(), '-', ''), 'NODE_END', 'FLOW_CHG_DEFAULT', '流程结束', 6, 'END', 'admin', '系统管理员', 1, 'system', '2025-12-20 07:55:44', 'system', '2025-12-20 07:55:44', 0),
-- 发版记录流程节点
(REPLACE(UUID(), '-', ''), 'NODE_RELEASE_APPLY', 'FLOW_RELEASE_DEFAULT', '发版申请', 1, 'START', 'yasuo', '亚索', 1, 'system', '2025-12-20 07:55:44', 'system', '2025-12-20 07:55:44', 0),
(REPLACE(UUID(), '-', ''), 'NODE_RELEASE_TL', 'FLOW_RELEASE_DEFAULT', '小组长审核', 2, 'NORMAL', 'leeSin', '李青', 1, 'system', '2025-12-20 07:55:44', 'system', '2025-12-20 07:55:44', 0),
(REPLACE(UUID(), '-', ''), 'NODE_RELEASE_GL', 'FLOW_RELEASE_DEFAULT', '大组长审核', 3, 'NORMAL', 'ryze', '瑞兹', 1, 'system', '2025-12-20 07:55:44', 'system', '2025-12-20 07:55:44', 0),
(REPLACE(UUID(), '-', ''), 'NODE_RELEASE_ARCH', 'FLOW_RELEASE_DEFAULT', '技术架构评审', 4, 'NORMAL', 'karma', '卡尔玛', 1, 'system', '2025-12-20 07:55:44', 'system', '2025-12-20 07:55:44', 0),
(REPLACE(UUID(), '-', ''), 'NODE_RELEASE_OPS', 'FLOW_RELEASE_DEFAULT', '运维管理员确认', 5, 'NORMAL', 'gwen', '格温', 1, 'system', '2025-12-20 07:55:44', 'system', '2025-12-20 07:55:44', 0),
(REPLACE(UUID(), '-', ''), 'NODE_RELEASE_END', 'FLOW_RELEASE_DEFAULT', '流程结束', 6, 'END', 'admin', '系统管理员', 1, 'system', '2025-12-20 07:55:44', 'system', '2025-12-20 07:55:44', 0);

-- 5. 添加变更记录（不同状态）
INSERT INTO biz_change_record (`uuid`, `record_code`, `current_status`, `release_date`, `defect_number`, `group_name`, `service_name`, `developer_num`, `developer_name`, `source_branch`, `target_branch`, `problem_description`, `solution_description`, `flow_id`, `current_node_id`, `approval_status`, `created_by`, `created_time`, `updated_by`, `updated_time`, `is_deleted`) VALUES
-- 草稿状态
(REPLACE(UUID(), '-', ''), 'CHG202601050001', '01', '2026-01-10', 'DEF001', '模块A', '服务A', 'yasuo', '亚索', 'feature/change1', 'develop', '问题描述1', '解决方案1', 'FLOW_CHG_DEFAULT', 'NODE_DEV_APPLY', 'DRAFT', 'yasuo', '2025-12-20 07:55:44', 'system', '2025-12-20 07:55:44', 0),
-- 已提请状态
(REPLACE(UUID(), '-', ''), 'CHG202601050002', '02', '2026-01-10', 'DEF002', '模块B', '服务B', 'yasuo', '亚索', 'feature/change2', 'develop', '问题描述2', '解决方案2', 'FLOW_CHG_DEFAULT', 'NODE_TEAM_LEAD', 'PENDING', 'yasuo', '2025-12-20 07:55:44', 'system', '2025-12-20 07:55:44', 0),
-- 审批中状态（小组长已通过，大组长待审批）
(REPLACE(UUID(), '-', ''), 'CHG202601050003', '03', '2026-01-10', 'DEF003', '模块C', '服务C', 'yasuo', '亚索', 'feature/change3', 'develop', '问题描述3', '解决方案3', 'FLOW_CHG_DEFAULT', 'NODE_GROUP_LEAD', 'PENDING', 'yasuo', '2025-12-20 07:55:44', 'system', '2025-12-20 07:55:44', 0),
-- 审批中状态（大组长已通过，架构师待审批）
(REPLACE(UUID(), '-', ''), 'CHG202601050004', '03', '2026-01-10', 'DEF004', '模块D', '服务D', 'yasuo', '亚索', 'feature/change4', 'develop', '问题描述4', '解决方案4', 'FLOW_CHG_DEFAULT', 'NODE_ARCH_REVIEW', 'PENDING', 'yasuo', '2025-12-20 07:55:44', 'system', '2025-12-20 07:55:44', 0),
-- 审批中状态（架构师已通过，版本管理员待确认）
(REPLACE(UUID(), '-', ''), 'CHG202601050005', '03', '2026-01-10', 'DEF005', '模块E', '服务E', 'yasuo', '亚索', 'feature/change5', 'develop', '问题描述5', '解决方案5', 'FLOW_CHG_DEFAULT', 'NODE_VERSION_CONFIRM', 'PENDING', 'yasuo', '2025-12-20 07:55:44', 'system', '2025-12-20 07:55:44', 0),
-- 已通过状态
(REPLACE(UUID(), '-', ''), 'CHG202601050006', '04', '2026-01-10', 'DEF006', '模块F', '服务F', 'yasuo', '亚索', 'feature/change6', 'develop', '问题描述6', '解决方案6', 'FLOW_CHG_DEFAULT', 'NODE_END', 'APPROVED', 'yasuo', '2025-12-20 07:55:44', 'system', '2025-12-20 07:55:44', 0),
-- 已拒绝状态
(REPLACE(UUID(), '-', ''), 'CHG202601050007', '01', '2026-01-10', 'DEF007', '模块G', '服务G', 'yasuo', '亚索', 'feature/change7', 'develop', '问题描述7', '解决方案7', 'FLOW_CHG_DEFAULT', 'NODE_DEV_APPLY', 'REJECTED', 'yasuo', '2025-12-20 07:55:44', 'system', '2025-12-20 07:55:44', 0);

-- 6. 添加审批任务
INSERT INTO biz_approval_task (`uuid`, `task_id`, `flow_id`, `node_id`, `business_type`, `business_id`, `business_code`, `approver_num`, `approver_name`, `task_status`, `current_status`, `created_by`, `created_time`, `updated_by`, `updated_time`, `is_deleted`) VALUES
-- 待审批任务
(REPLACE(UUID(), '-', ''), 'TASK202601050001', 'FLOW_CHG_DEFAULT', 'NODE_TEAM_LEAD', 'CHANGE_RECORD', 2, 'CHG202601050002', 'leeSin', '李青', 'PENDING', '02', 'system', '2025-12-20 07:55:44', 'system', '2025-12-20 07:55:44', 0),
(REPLACE(UUID(), '-', ''), 'TASK202601050002', 'FLOW_CHG_DEFAULT', 'NODE_GROUP_LEAD', 'CHANGE_RECORD', 3, 'CHG202601050003', 'ryze', '瑞兹', 'PENDING', '03', 'system', '2025-12-20 07:55:44', 'system', '2025-12-20 07:55:44', 0),
(REPLACE(UUID(), '-', ''), 'TASK202601050003', 'FLOW_CHG_DEFAULT', 'NODE_ARCH_REVIEW', 'CHANGE_RECORD', 4, 'CHG202601050004', 'karma', '卡尔玛', 'PENDING', '03', 'system', '2025-12-20 07:55:44', 'system', '2025-12-20 07:55:44', 0),
(REPLACE(UUID(), '-', ''), 'TASK202601050004', 'FLOW_CHG_DEFAULT', 'NODE_VERSION_CONFIRM', 'CHANGE_RECORD', 5, 'CHG202601050005', 'sivir', '希维尔', 'PENDING', '03', 'system', '2025-12-20 07:55:44', 'system', '2025-12-20 07:55:44', 0),
-- 已通过任务
(REPLACE(UUID(), '-', ''), 'TASK202601050005', 'FLOW_CHG_DEFAULT', 'NODE_TEAM_LEAD', 'CHANGE_RECORD', 6, 'CHG202601050006', 'leeSin', '李青', 'APPROVED', '04', 'system', '2025-12-20 07:55:44', 'system', '2025-12-20 07:55:44', 0),
(REPLACE(UUID(), '-', ''), 'TASK202601050006', 'FLOW_CHG_DEFAULT', 'NODE_GROUP_LEAD', 'CHANGE_RECORD', 6, 'CHG202601050006', 'ryze', '瑞兹', 'APPROVED', '04', 'system', '2025-12-20 07:55:44', 'system', '2025-12-20 07:55:44', 0),
(REPLACE(UUID(), '-', ''), 'TASK202601050007', 'FLOW_CHG_DEFAULT', 'NODE_ARCH_REVIEW', 'CHANGE_RECORD', 6, 'CHG202601050006', 'karma', '卡尔玛', 'APPROVED', '04', 'system', '2025-12-20 07:55:44', 'system', '2025-12-20 07:55:44', 0),
(REPLACE(UUID(), '-', ''), 'TASK202601050008', 'FLOW_CHG_DEFAULT', 'NODE_VERSION_CONFIRM', 'CHANGE_RECORD', 6, 'CHG202601050006', 'sivir', '希维尔', 'APPROVED', '04', 'system', '2025-12-20 07:55:44', 'system', '2025-12-20 07:55:44', 0),
-- 已拒绝任务
(REPLACE(UUID(), '-', ''), 'TASK202601050009', 'FLOW_CHG_DEFAULT', 'NODE_TEAM_LEAD', 'CHANGE_RECORD', 7, 'CHG202601050007', 'leeSin', '李青', 'REJECTED', '01', 'system', '2025-12-20 07:55:44', 'system', '2025-12-20 07:55:44', 0);

-- 7. 添加审批日志
INSERT INTO biz_approval_log (`uuid`, `log_id`, `task_id`, `flow_id`, `node_id`, `business_type`, `business_id`, `business_code`, `operation_type`, `operator_num`, `operator_name`, `before_status`, `after_status`, `created_by`, `created_time`, `updated_by`, `updated_time`, `is_deleted`) VALUES
-- 任务创建日志
(REPLACE(UUID(), '-', ''), 'LOG202601050001', 'TASK202601050001', 'FLOW_CHG_DEFAULT', 'NODE_TEAM_LEAD', 'CHANGE_RECORD', 2, 'CHG202601050002', 'SUBMIT', 'yasuo', '亚索', '01', '02', 'system', '2025-12-20 07:55:44', 'system', '2025-12-20 07:55:44', 0),
-- 审批通过日志
(REPLACE(UUID(), '-', ''), 'LOG202601050002', 'TASK202601050005', 'FLOW_CHG_DEFAULT', 'NODE_TEAM_LEAD', 'CHANGE_RECORD', 6, 'CHG202601050006', 'APPROVE', 'leeSin', '李青', '02', '03', 'system', '2025-12-20 07:55:44', 'system', '2025-12-20 07:55:44', 0),
(REPLACE(UUID(), '-', ''), 'LOG202601050003', 'TASK202601050006', 'FLOW_CHG_DEFAULT', 'NODE_GROUP_LEAD', 'CHANGE_RECORD', 6, 'CHG202601050006', 'APPROVE', 'ryze', '瑞兹', '03', '03', 'system', '2025-12-20 07:55:44', 'system', '2025-12-20 07:55:44', 0),
(REPLACE(UUID(), '-', ''), 'LOG202601050004', 'TASK202601050007', 'FLOW_CHG_DEFAULT', 'NODE_ARCH_REVIEW', 'CHANGE_RECORD', 6, 'CHG202601050006', 'APPROVE', 'karma', '卡尔玛', '03', '03', 'system', '2025-12-20 07:55:44', 'system', '2025-12-20 07:55:44', 0),
(REPLACE(UUID(), '-', ''), 'LOG202601050005', 'TASK202601050008', 'FLOW_CHG_DEFAULT', 'NODE_VERSION_CONFIRM', 'CHANGE_RECORD', 6, 'CHG202601050006', 'APPROVE', 'sivir', '希维尔', '03', '04', 'system', '2025-12-20 07:55:44', 'system', '2025-12-20 07:55:44', 0),
-- 审批拒绝日志
(REPLACE(UUID(), '-', ''), 'LOG202601050006', 'TASK202601050009', 'FLOW_CHG_DEFAULT', 'NODE_TEAM_LEAD', 'CHANGE_RECORD', 7, 'CHG202601050007', 'REJECT', 'leeSin', '李青', '02', '01', 'system', '2025-12-20 07:55:44', 'system', '2025-12-20 07:55:44', 0);

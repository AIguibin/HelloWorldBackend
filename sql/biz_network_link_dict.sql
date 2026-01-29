-- ----------------------------
-- 信贷系统外部链接网络管理清单 - 字典数据初始化
-- ----------------------------

-- 1. 创建字典类型：网络环境
INSERT INTO `sys_dict_type` (`uuid`, `dict_type_code`, `dict_type_name`, `description`, `sort_order`, `status`, `created_by`, `created_time`, `is_deleted`)
VALUES 
(REPLACE(UUID(), '-', ''), 'NET_ENV', '网络环境', '信贷系统外部链接网络管理清单-环境类型', 100, 1, 'system', NOW(), 0)
ON DUPLICATE KEY UPDATE `dict_type_name` = '网络环境', `description` = '信贷系统外部链接网络管理清单-环境类型';

-- 2. 创建字典类型：链路状态
INSERT INTO `sys_dict_type` (`uuid`, `dict_type_code`, `dict_type_name`, `description`, `sort_order`, `status`, `created_by`, `created_time`, `is_deleted`)
VALUES 
(REPLACE(UUID(), '-', ''), 'NET_STATUS', '链路状态', '信贷系统外部链接网络管理清单-链路状态', 101, 1, 'system', NOW(), 0)
ON DUPLICATE KEY UPDATE `dict_type_name` = '链路状态', `description` = '信贷系统外部链接网络管理清单-链路状态';

-- 3. 创建字典类型：连接协议
INSERT INTO `sys_dict_type` (`uuid`, `dict_type_code`, `dict_type_name`, `description`, `sort_order`, `status`, `created_by`, `created_time`, `is_deleted`)
VALUES 
(REPLACE(UUID(), '-', ''), 'NET_PROTOCOL', '连接协议', '信贷系统外部链接网络管理清单-连接协议', 102, 1, 'system', NOW(), 0)
ON DUPLICATE KEY UPDATE `dict_type_name` = '连接协议', `description` = '信贷系统外部链接网络管理清单-连接协议';

-- 4. 创建字典项：网络环境
INSERT INTO `sys_dict_item` (`uuid`, `dict_type_code`, `dict_value`, `dict_label`, `sort_order`, `status`, `created_by`, `created_time`, `is_deleted`)
VALUES 
(REPLACE(UUID(), '-', ''), 'NET_ENV', 'master', '生产环境', 10, 1, 'system', NOW(), 0),
(REPLACE(UUID(), '-', ''), 'NET_ENV', 'prod', '演练环境', 20, 1, 'system', NOW(), 0),
(REPLACE(UUID(), '-', ''), 'NET_ENV', 'uat', 'UAT测试', 30, 1, 'system', NOW(), 0),
(REPLACE(UUID(), '-', ''), 'NET_ENV', 'sit', 'SIT测试', 40, 1, 'system', NOW(), 0),
(REPLACE(UUID(), '-', ''), 'NET_ENV', 'dev', '开发环境', 50, 1, 'system', NOW(), 0)
ON DUPLICATE KEY UPDATE `dict_label` = VALUES(`dict_label`), `sort_order` = VALUES(`sort_order`);

-- 5. 创建字典项：链路状态
INSERT INTO `sys_dict_item` (`uuid`, `dict_type_code`, `dict_value`, `dict_label`, `sort_order`, `status`, `created_by`, `created_time`, `is_deleted`)
VALUES 
(REPLACE(UUID(), '-', ''), 'NET_STATUS', 'active', '启用中', 10, 1, 'system', NOW(), 0),
(REPLACE(UUID(), '-', ''), 'NET_STATUS', 'testing', '测试中', 20, 1, 'system', NOW(), 0),
(REPLACE(UUID(), '-', ''), 'NET_STATUS', 'disabled', '已停用', 30, 1, 'system', NOW(), 0)
ON DUPLICATE KEY UPDATE `dict_label` = VALUES(`dict_label`), `sort_order` = VALUES(`sort_order`);

-- 6. 创建字典项：连接协议
INSERT INTO `sys_dict_item` (`uuid`, `dict_type_code`, `dict_value`, `dict_label`, `sort_order`, `status`, `created_by`, `created_time`, `is_deleted`)
VALUES 
(REPLACE(UUID(), '-', ''), 'NET_PROTOCOL', 'HTTP', 'HTTP', 10, 1, 'system', NOW(), 0),
(REPLACE(UUID(), '-', ''), 'NET_PROTOCOL', 'HTTPS', 'HTTPS', 20, 1, 'system', NOW(), 0),
(REPLACE(UUID(), '-', ''), 'NET_PROTOCOL', 'TCP', 'TCP', 30, 1, 'system', NOW(), 0),
(REPLACE(UUID(), '-', ''), 'NET_PROTOCOL', 'SFTP', 'SFTP', 40, 1, 'system', NOW(), 0),
(REPLACE(UUID(), '-', ''), 'NET_PROTOCOL', 'MQ', 'MQ', 50, 1, 'system', NOW(), 0)
ON DUPLICATE KEY UPDATE `dict_label` = VALUES(`dict_label`), `sort_order` = VALUES(`sort_order`);

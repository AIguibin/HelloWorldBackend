-- 字典类型表
CREATE TABLE `dict_type` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `dict_type_code` VARCHAR(50) NOT NULL COMMENT '字典类型编码',
  `dict_type_name` VARCHAR(100) NOT NULL COMMENT '字典类型名称',
  `description` VARCHAR(255) DEFAULT NULL COMMENT '描述',
  `sort` INT DEFAULT 0 COMMENT '排序',
  `is_enabled` TINYINT DEFAULT 1 COMMENT '是否启用(1:启用 0:禁用)',
  `create_user` VARCHAR(50) DEFAULT NULL COMMENT '创建人',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_user` VARCHAR(50) DEFAULT NULL COMMENT '更新人',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除(1:已删除 0:未删除)',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_dict_type_code` (`dict_type_code`),
  KEY `idx_is_enabled` (`is_enabled`),
  KEY `idx_is_deleted` (`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='字典类型表';

-- 字典项表
CREATE TABLE `dict_item` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `dict_type_id` INT NOT NULL COMMENT '字典类型ID',
  `dict_type_code` VARCHAR(50) NOT NULL COMMENT '字典类型编码',
  `dict_value` VARCHAR(50) NOT NULL COMMENT '字典值',
  `dict_label` VARCHAR(100) NOT NULL COMMENT '字典标签',
  `group_code` VARCHAR(50) DEFAULT NULL COMMENT '分组编码',
  `group_name` VARCHAR(100) DEFAULT NULL COMMENT '分组名称',
  `sort` INT DEFAULT 0 COMMENT '排序',
  `is_enabled` TINYINT DEFAULT 1 COMMENT '是否启用(1:启用 0:禁用)',
  `create_user` VARCHAR(50) DEFAULT NULL COMMENT '创建人',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_user` VARCHAR(50) DEFAULT NULL COMMENT '更新人',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除(1:已删除 0:未删除)',
  PRIMARY KEY (`id`),
  KEY `idx_dict_type_id` (`dict_type_id`),
  KEY `idx_dict_type_code` (`dict_type_code`),
  KEY `idx_group_code` (`group_code`),
  KEY `idx_dict_value` (`dict_value`),
  KEY `idx_is_enabled` (`is_enabled`),
  KEY `idx_is_deleted` (`is_deleted`),
  CONSTRAINT `fk_dict_item_dict_type` FOREIGN KEY (`dict_type_id`) REFERENCES `dict_type` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='字典项表';

-- 初始化字典数据
INSERT INTO `dict_type` (`dict_type_code`, `dict_type_name`, `description`, `sort`, `is_enabled`) VALUES
('CURRENT_STATUS', '当前状态', '变更记录的当前状态', 1, 1),
('DEVELOP_TYPE', '开发类别', '开发任务的类别', 2, 1),
('YES_NO', '是否选项', '通用的是/否选项', 3, 1);

INSERT INTO `dict_item` (`dict_type_id`, `dict_type_code`, `dict_value`, `dict_label`, `group_code`, `group_name`, `sort`, `is_enabled`) VALUES
-- 当前状态
(1, 'CURRENT_STATUS', 'PENDING_APPROVAL', '待审批', NULL, NULL, 1, 1),
(1, 'CURRENT_STATUS', 'PENDING_REVIEW', '待评审', NULL, NULL, 2, 1),
(1, 'CURRENT_STATUS', 'PENDING_MERGE', '待合版', NULL, NULL, 3, 1),
(1, 'CURRENT_STATUS', 'MERGED', '已合版', NULL, NULL, 4, 1),
-- 开发类别
(2, 'DEVELOP_TYPE', 'FRONTEND', '前端', NULL, NULL, 1, 1),
(2, 'DEVELOP_TYPE', 'BACKEND', '后端', NULL, NULL, 2, 1),
(2, 'DEVELOP_TYPE', 'SCRIPT', '脚本', NULL, NULL, 3, 1),
(2, 'DEVELOP_TYPE', 'CONFIG', '配置', NULL, NULL, 4, 1),
-- 是否选项
(3, 'YES_NO', '1', '是', NULL, NULL, 1, 1),
(3, 'YES_NO', '0', '否', NULL, NULL, 2, 1);
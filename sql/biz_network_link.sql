-- ----------------------------
-- Table structure for biz_network_link
-- ----------------------------
DROP TABLE IF EXISTS `biz_network_link`;
CREATE TABLE `biz_network_link` (
  `uuid` varchar(32) NOT NULL COMMENT 'UUID，32位随机字符串',
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '自增ID，仅做序号',
  `link_code` varchar(64) NOT NULL COMMENT '链路编号，唯一',
  `link_name` varchar(256) NOT NULL COMMENT '链路名称',
  `source_system` varchar(128) NOT NULL COMMENT '源系统名称',
  `source_env` varchar(16) NOT NULL COMMENT '源环境：prod-生产，uat-UAT测试，sit-SIT测试，dev-开发环境',
  `source_ip` varchar(64) NOT NULL COMMENT '源IP地址',
  `target_system` varchar(128) NOT NULL COMMENT '目标系统名称',
  `target_env` varchar(16) NOT NULL COMMENT '目标环境：prod-生产，uat-UAT测试，sit-SIT测试，dev-开发环境',
  `target_host` varchar(255) NOT NULL COMMENT '目标主机（IP地址或域名）',
  `target_port` int NOT NULL COMMENT '目标端口号（1-65535）',
  `protocol` varchar(32) NOT NULL COMMENT '连接协议：HTTP/HTTPS/TCP/SFTP/MQ等',
  `auth_method` varchar(512) DEFAULT NULL COMMENT '认证方式',
  `scenario` varchar(512) DEFAULT NULL COMMENT '使用场景',
  `description` varchar(1024) DEFAULT NULL COMMENT '链路描述',
  `owner` varchar(64) DEFAULT NULL COMMENT '负责人',
  `status` varchar(16) NOT NULL DEFAULT 'active' COMMENT '链路状态：active-启用中，testing-测试中，disabled-已停用',
  `monitoring_level` varchar(32) DEFAULT NULL COMMENT '监控等级：P0-核心，P1-重要，P2-一般',
  `created_by` varchar(20) NOT NULL COMMENT '创建人用户编号',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` varchar(20) DEFAULT NULL COMMENT '更新人用户编号',
  `updated_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint DEFAULT '0' COMMENT '是否删除：0-否，1-是',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_uuid` (`uuid`),
  UNIQUE KEY `uk_link_code` (`link_code`),
  KEY `idx_source_env` (`source_env`),
  KEY `idx_target_env` (`target_env`),
  KEY `idx_status` (`status`),
  KEY `idx_protocol` (`protocol`),
  KEY `idx_updated_time` (`updated_time`),
  KEY `idx_is_deleted` (`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='信贷系统外部链接网络管理清单表';

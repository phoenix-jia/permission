SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for saas_privileges
-- ----------------------------
DROP TABLE IF EXISTS `v2_saas_privileges`;
CREATE TABLE `v2_saas_privileges` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  `deleted_at` datetime DEFAULT NULL,
  `resource` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `operation` enum('create','retrieve','update','delete','review') COLLATE utf8_unicode_ci NOT NULL,
  `resource_zh` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `resource_operation_index` (`resource`, `operation`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- ----------------------------
-- Table structure for saas_role_privileges
-- ----------------------------
DROP TABLE IF EXISTS `v2_saas_role_privileges`;
CREATE TABLE `v2_saas_role_privileges` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  `deleted_at` datetime DEFAULT NULL,
  `saas_privilege_id` int(11) NOT NULL,
  `saas_role_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `saas_privilege_id_index`(`saas_privilege_id`),
  INDEX `saas_role_id_index`(`saas_role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- ----------------------------
-- Table structure for saas_roles
-- ----------------------------
DROP TABLE IF EXISTS `v2_saas_roles`;
CREATE TABLE `v2_saas_roles` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  `deleted_at` datetime DEFAULT NULL,
  `name` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- ----------------------------
-- Table structure for saas_user_comm_alarms
-- ----------------------------
DROP TABLE IF EXISTS `v2_saas_user_comm_alarms`;
CREATE TABLE `v2_saas_user_comm_alarms` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  `deleted_at` datetime DEFAULT NULL,
  `saas_user_id` int(11) NOT NULL,
  `comm_alarm_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `saas_user_id_index` (`saas_user_id`),
  INDEX `comm_alarm_id_index` (`comm_alarm_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- ----------------------------
-- Table structure for saas_user_comms
-- ----------------------------
DROP TABLE IF EXISTS `v2_saas_user_comms`;
CREATE TABLE `v2_saas_user_comms` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  `deleted_at` datetime DEFAULT NULL,
  `comm_id` int(11) NOT NULL,
  `saas_user_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `saas_user_id_index` (`saas_user_id`),
  INDEX `comm_id_index` (`comm_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- ----------------------------
-- Table structure for saas_user_privileges
-- ----------------------------
DROP TABLE IF EXISTS `v2_saas_user_privileges`;
CREATE TABLE `v2_saas_user_privileges` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  `deleted_at` datetime DEFAULT NULL,
  `saas_privilege_id` int(11) NOT NULL,
  `saas_user_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `saas_privilege_id_index` (`saas_privilege_id`),
  INDEX `saas_user_id_index` (`saas_user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- ----------------------------
-- Table structure for saas_user_roles
-- ----------------------------
DROP TABLE IF EXISTS `v2_saas_user_roles`;
CREATE TABLE `v2_saas_user_roles` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  `deleted_at` datetime DEFAULT NULL,
  `saas_role_id` int(11) NOT NULL,
  `saas_user_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `saas_role_id_index` (`saas_role_id`),
  INDEX `saas_user_id_index` (`saas_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- ----------------------------
-- Table structure for saas_users
-- ----------------------------
DROP TABLE IF EXISTS `v2_saas_users`;
CREATE TABLE `v2_saas_users` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  `deleted_at` datetime DEFAULT NULL,
  `username` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `password` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `name` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '姓名',
  `phone` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '联系电话',
  `position` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '职位类型',
  `platform` tinyint(4) DEFAULT NULL COMMENT '平台:0-全部,1-PC,2-小程序',
  `expire_at` datetime DEFAULT NULL COMMENT '过期时间',
  `resident_id` int(11) DEFAULT NULL,
  `area_level` varchar(10) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '行政区划级别',
  `home_page_type` varchar(10) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `username_index` (`username`),
  INDEX `resident_id_index` (`resident_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

DROP TABLE IF EXISTS `v2_login_records`;
CREATE TABLE `v2_login_records` (
 `id` int(11) NOT NULL AUTO_INCREMENT,
 `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
 `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
 `deleted_at` datetime DEFAULT NULL,
 `username` varchar(20) COLLATE utf8_unicode_ci NOT NULL,
 `client_ip` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
 `client_address` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
 `project_code` char(5) COLLATE utf8_unicode_ci DEFAULT NULL,
 PRIMARY KEY (`id`),
 INDEX `username_index` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=596 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;


SET FOREIGN_KEY_CHECKS = 1;

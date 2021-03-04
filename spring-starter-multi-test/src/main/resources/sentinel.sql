/*
 Navicat Premium Data Transfer

 Source Server         : 测试
 Source Server Type    : MySQL
 Source Server Version : 50727
 Source Host           : localhost:3306
 Source Schema         : sentinel

 Target Server Type    : MySQL
 Target Server Version : 50727
 File Encoding         : 65001

 Date: 04/03/2021 20:03:16
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for sentinel_rule
-- ----------------------------
DROP TABLE IF EXISTS `sentinel_rule`;
CREATE TABLE `sentinel_rule` (
  `id` bigint(20) NOT NULL COMMENT '主键',
  `rule_type` varchar(12) COLLATE utf8_unicode_ci NOT NULL COMMENT '限流规则类型',
  `rule_content` varchar(512) COLLATE utf8_unicode_ci NOT NULL COMMENT '限流内容',
  `data_id` varchar(64) COLLATE utf8_unicode_ci NOT NULL COMMENT 'diamond data id',
  `group_id` varchar(16) COLLATE utf8_unicode_ci NOT NULL DEFAULT 'sentinel' COMMENT 'groupId',
  `diamond_status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否成功写入diamond',
  `app_name` varchar(64) COLLATE utf8_unicode_ci NOT NULL COMMENT '项目名字',
  `add_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间',
  `upd_time` datetime NOT NULL DEFAULT '2008-05-12 00:00:00' ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- ----------------------------
-- Records of sentinel_rule
-- ----------------------------
BEGIN;
INSERT INTO `sentinel_rule` VALUES (396610584576, 'flow', '{\"app\":\"sentinel-dashboard\",\"clusterConfig\":{\"fallbackToLocalWhenFail\":true,\"sampleCount\":10,\"strategy\":0,\"thresholdType\":0,\"windowIntervalMs\":1000},\"clusterMode\":false,\"controlBehavior\":0,\"count\":140.0,\"gmtCreate\":1609313294792,\"gmtModified\":1609313569680,\"grade\":1,\"id\":396610584576,\"ip\":\"192.168.63.127\",\"limitApp\":\"default\",\"port\":8719,\"resource\":\"/registry/machine\",\"strategy\":0}', 'sentinel-dashboard-flow', 'sentinel', 0, 'sentinel-dashboard', '2020-12-30 15:28:14', '2020-12-30 15:32:49');
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;

/*
 Navicat Premium Data Transfer

 Source Server         : 测试
 Source Server Type    : MySQL
 Source Server Version : 50727
 Source Host           : localhost:3306
 Source Schema         : api_document

 Target Server Type    : MySQL
 Target Server Version : 50727
 File Encoding         : 65001

 Date: 04/03/2021 19:14:09
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for api_detail
-- ----------------------------
DROP TABLE IF EXISTS `api_detail`;
CREATE TABLE `api_detail` (
  `apiId` int(11) NOT NULL COMMENT 'yapi api id',
  `projectId` int(11) NOT NULL COMMENT 'yapi项目ID',
  `catId` int(11) NOT NULL COMMENT 'API分类ID',
  `projectName` varchar(32) COLLATE utf8_unicode_ci NOT NULL COMMENT '项目名字',
  `catName` varchar(64) COLLATE utf8_unicode_ci NOT NULL COMMENT 'API分类名字',
  `apiDesc` varchar(512) COLLATE utf8_unicode_ci NOT NULL COMMENT 'API分类描述',
  `title` varchar(64) COLLATE utf8_unicode_ci NOT NULL DEFAULT '' COMMENT '商品标题',
  `path` varchar(64) COLLATE utf8_unicode_ci NOT NULL COMMENT 'http请求路径',
  `method` varchar(12) COLLATE utf8_unicode_ci NOT NULL COMMENT 'api请求方法',
  `upTime` datetime NOT NULL DEFAULT '2020-01-01 00:00:00' ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `addTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间',
  `version` varchar(32) COLLATE utf8_unicode_ci NOT NULL COMMENT '版本',
  `status` varchar(16) COLLATE utf8_unicode_ci NOT NULL COMMENT 'api状态(上线/下线)',
  `fullinfo` varchar(4096) COLLATE utf8_unicode_ci NOT NULL COMMENT 'API fullinfo信息',
  PRIMARY KEY (`apiId`,`projectId`,`catId`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- ----------------------------
-- Table structure for offline_api_detail
-- ----------------------------
DROP TABLE IF EXISTS `offline_api_detail`;
CREATE TABLE `offline_api_detail` (
  `apiId` int(11) NOT NULL COMMENT 'yapi apiID',
  `projectId` int(11) NOT NULL COMMENT 'yapi项目ID',
  `catId` int(11) NOT NULL COMMENT 'API分类ID',
  `projectName` varchar(32) COLLATE utf8_unicode_ci NOT NULL COMMENT '项目名字',
  `catName` varchar(64) COLLATE utf8_unicode_ci NOT NULL COMMENT 'API分类名字',
  `apiDesc` varchar(512) COLLATE utf8_unicode_ci NOT NULL COMMENT 'API分类描述',
  `title` varchar(64) COLLATE utf8_unicode_ci NOT NULL COMMENT 'API标题',
  `path` varchar(64) COLLATE utf8_unicode_ci NOT NULL COMMENT 'http请求路径',
  `method` varchar(12) COLLATE utf8_unicode_ci NOT NULL COMMENT 'api请求方法',
  `upTime` datetime NOT NULL DEFAULT '2020-01-01 00:00:00' ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `addTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间',
  `version` varchar(32) COLLATE utf8_unicode_ci NOT NULL COMMENT '版本',
  `status` varchar(16) COLLATE utf8_unicode_ci NOT NULL COMMENT 'api状态(上线/下线)',
  `fullinfo` varchar(4096) COLLATE utf8_unicode_ci NOT NULL COMMENT 'API fullinfo信息',
  PRIMARY KEY (`apiId`,`projectId`,`catId`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- ----------------------------
-- Records of offline_api_detail
-- ----------------------------
BEGIN;
INSERT INTO `offline_api_detail` VALUES (18279, 254, 2874, '测试', '商品API', '<p>搜索产品信息\n     \n      <p>\n      这部分主要是用于生成订单/处理订单/业务昆虫罗哦家\n      <p>\n      天猫商家发布商品时，查询关联产品信息时使用，非商品查询接口。商品查询接口：taobao.item.seller.get\n      两种方式查看一个产品详细信息: 传入product_id来查询；传入cid和props来查询</p>', '搜索产品信息', '/v1/test5', 'GET', '2020-01-01 00:00:00', '2021-02-02 22:47:10', '2021-02-03_144710', 'offline', '{\"query_path\":{\"path\":\"/v1/test5\",\"params\":[]},\"res_body_type\":\"json\",\"req_request\":\"Test5\",\"type\":\"static\",\"title\":\"搜索产品信息\",\"path\":\"/v1/test5\",\"catid\":\"2874\",\"req_body_is_json_schema\":false,\"__v\":0,\"markdown\":\"搜索产品信息\\n     \\n      <p>\\n      这部分主要是用于生成订单/处理订单/业务昆虫罗哦家\\n      <p>\\n      天猫商家发布商品时，查询关联产品信息时使用，非商品查询接口。商品查询接口：taobao.item.seller.get\\n      两种方式查看一个产品详细信息: 传入product_id来查询；传入cid和props来查询\",\"req_headers\":[],\"edit_uid\":0,\"tag\":[],\"req_query\":[{\"name\":\"first\",\"subType\":\"null\",\"type\":\"integer\",\"value\":\"0\",\"required\":1,\"example\":\"0\",\"desc\":\"第一个int类型的参数 \"},{\"name\":\"second\",\"subType\":\"null\",\"type\":\"long\",\"value\":\"0\",\"required\":0,\"example\":\"0\",\"desc\":\"第二个long类型的参数 \"},{\"name\":\"name\",\"subType\":\"null\",\"type\":\"string\",\"required\":1,\"desc\":\"登陆账号\"},{\"name\":\"password\",\"subType\":\"null\",\"type\":\"string\",\"required\":1,\"desc\":\"登入密码\"}],\"res_body_is_json_schema\":true,\"res_body\":\"{\\\"type\\\":\\\"object\\\",\\\"properties\\\":{\\\"code\\\":{\\\"type\\\":\\\"integer\\\",\\\"description\\\":\\\"响应码 100成功 非100失败\\\",\\\"default\\\":\\\"100\\\"},\\\"message\\\":{\\\"type\\\":\\\"string\\\",\\\"description\\\":\\\"业务失败的message，例如:xx参数为空\\\"},\\\"data\\\":{\\\"type\\\":\\\"object\\\",\\\"properties\\\":{\\\"merge\\\":{\\\"type\\\":\\\"boolean\\\",\\\"description\\\":\\\"是否婚配\\\",\\\"default\\\":\\\"false\\\"},\\\"age\\\":{\\\"type\\\":\\\"integer\\\",\\\"description\\\":\\\"年纪\\\"},\\\"name\\\":{\\\"type\\\":\\\"string\\\",\\\"description\\\":\\\"姓名\\\"},\\\"detail\\\":{\\\"type\\\":\\\"object\\\",\\\"properties\\\":{\\\"home\\\":{\\\"type\\\":\\\"string\\\"},\\\"father\\\":{\\\"type\\\":\\\"string\\\"}},\\\"required\\\":[\\\"home\\\",\\\"father\\\"],\\\"description\\\":\\\"用户详情数据\\\"},\\\"details\\\":{\\\"type\\\":\\\"array\\\",\\\"items\\\":{\\\"type\\\":\\\"object\\\",\\\"properties\\\":{\\\"home\\\":{\\\"type\\\":\\\"string\\\"},\\\"father\\\":{\\\"type\\\":\\\"string\\\"}},\\\"required\\\":[\\\"home\\\",\\\"father\\\"]},\\\"description\\\":\\\"详情列表\\\"}},\\\"required\\\":[\\\"age\\\",\\\"name\\\",\\\"detail\\\",\\\"details\\\"],\\\"description\\\":\\\"响应数据\\\"}},\\\"required\\\":[\\\"code\\\"],\\\"$schema\\\":\\\"http://json-schema.org/draft-04/schema#\\\",\\\"description\\\":\\\"get请求的响应参数\\\"}\",\"method\":\"GET\",\"index\":0,\"switch_notice\":true,\"token\":\"cea77500ef16d04aa5585a9e7857b5de7e78a08da89371a6f20344e8e047804d\",\"up_time\":1612334829,\"api_opened\":false,\"add_time\":1612334829,\"status\":\"done\",\"desc\":\"<p>搜索产品信息\\n     \\n      <p>\\n      这部分主要是用于生成订单/处理订单/业务昆虫罗哦家\\n      <p>\\n      天猫商家发布商品时，查询关联产品信息时使用，非商品查询接口。商品查询接口：taobao.item.seller.get\\n      两种方式查看一个产品详细信息: 传入product_id来查询；传入cid和props来查询</p>\"}');
INSERT INTO `offline_api_detail` VALUES (18297, 254, 2874, '测试', '商品API', '<p>测试GET方法</p>', '测试GET方法', '/v1/test4', 'GET', '2020-01-01 00:00:00', '2021-02-02 22:47:09', '2021-02-03_144709', 'offline', '{\"query_path\":{\"path\":\"/v1/test4\",\"params\":[]},\"res_body_type\":\"json\",\"req_request\":\"Test4\",\"type\":\"static\",\"title\":\"测试GET方法\",\"path\":\"/v1/test4\",\"catid\":\"2874\",\"req_body_is_json_schema\":false,\"__v\":0,\"markdown\":\"测试GET方法\",\"req_headers\":[],\"edit_uid\":0,\"tag\":[],\"req_query\":[{\"name\":\"first\",\"subType\":\"null\",\"type\":\"integer\",\"value\":\"0\",\"required\":0,\"example\":\"0\",\"desc\":\"第一个int类型的参数 \"},{\"name\":\"second\",\"subType\":\"null\",\"type\":\"long\",\"value\":\"0\",\"required\":0,\"example\":\"0\",\"desc\":\"第二个long类型的参数 \"}],\"res_body_is_json_schema\":true,\"res_body\":\"{\\\"type\\\":\\\"object\\\",\\\"properties\\\":{\\\"code\\\":{\\\"type\\\":\\\"integer\\\",\\\"description\\\":\\\"响应码 100成功 非100失败\\\",\\\"default\\\":\\\"100\\\"},\\\"message\\\":{\\\"type\\\":\\\"string\\\",\\\"description\\\":\\\"业务失败的message，例如:xx参数为空\\\"},\\\"data\\\":{\\\"type\\\":\\\"object\\\",\\\"properties\\\":{\\\"merge\\\":{\\\"type\\\":\\\"boolean\\\",\\\"description\\\":\\\"是否婚配\\\",\\\"default\\\":\\\"false\\\"},\\\"age\\\":{\\\"type\\\":\\\"integer\\\",\\\"description\\\":\\\"年纪\\\"},\\\"name\\\":{\\\"type\\\":\\\"string\\\",\\\"description\\\":\\\"姓名\\\"},\\\"detail\\\":{\\\"type\\\":\\\"object\\\",\\\"properties\\\":{\\\"home\\\":{\\\"type\\\":\\\"string\\\"},\\\"father\\\":{\\\"type\\\":\\\"string\\\"}},\\\"required\\\":[\\\"home\\\",\\\"father\\\"],\\\"description\\\":\\\"用户详情数据\\\"},\\\"details\\\":{\\\"type\\\":\\\"array\\\",\\\"items\\\":{\\\"type\\\":\\\"object\\\",\\\"properties\\\":{\\\"home\\\":{\\\"type\\\":\\\"string\\\"},\\\"father\\\":{\\\"type\\\":\\\"string\\\"}},\\\"required\\\":[\\\"home\\\",\\\"father\\\"]},\\\"description\\\":\\\"详情列表\\\"}},\\\"required\\\":[\\\"age\\\",\\\"name\\\",\\\"detail\\\",\\\"details\\\"],\\\"description\\\":\\\"响应数据\\\"}},\\\"required\\\":[\\\"code\\\"],\\\"$schema\\\":\\\"http://json-schema.org/draft-04/schema#\\\",\\\"description\\\":\\\"get请求的响应参数\\\"}\",\"method\":\"GET\",\"index\":0,\"switch_notice\":true,\"token\":\"cea77500ef16d04aa5585a9e7857b5de7e78a08da89371a6f20344e8e047804d\",\"up_time\":1612334828,\"api_opened\":false,\"add_time\":1612334828,\"status\":\"done\",\"desc\":\"<p>测试GET方法</p>\"}');
INSERT INTO `offline_api_detail` VALUES (18324, 434, 2894, 'API文档信息', 'API展示接口', '<p>API树形接口</p>', 'API树形接口', '/api/front/projects', 'GET', '2020-01-01 00:00:00', '2021-02-02 23:08:38', '2021-02-03_150838', 'offline', '{\"query_path\":{\"path\":\"/api/front/projects\",\"params\":[]},\"res_body_type\":\"json\",\"req_request\":\"apiInfoRequest\",\"type\":\"static\",\"title\":\"API树形接口\",\"path\":\"/api/front/projects\",\"catid\":\"2894\",\"req_body_is_json_schema\":false,\"__v\":0,\"markdown\":\"API树形接口\",\"req_headers\":[],\"edit_uid\":0,\"tag\":[],\"req_query\":[{\"name\":\"method\",\"subType\":\"null\",\"type\":\"string\",\"required\":0,\"desc\":\"\"},{\"name\":\"type\",\"subType\":\"null\",\"type\":\"integer\",\"required\":0,\"desc\":\"0未上线的API/1已经上线的API\"}],\"res_body_is_json_schema\":true,\"res_body\":\"{\\\"type\\\":\\\"object\\\",\\\"properties\\\":{\\\"listApiDetails\\\":{\\\"type\\\":\\\"array\\\",\\\"items\\\":{\\\"type\\\":\\\"object\\\",\\\"properties\\\":{\\\"projectId\\\":{\\\"type\\\":\\\"integer\\\"},\\\"projectName\\\":{\\\"type\\\":\\\"string\\\"},\\\"categories\\\":{\\\"type\\\":\\\"array\\\",\\\"items\\\":{\\\"type\\\":\\\"object\\\",\\\"properties\\\":{\\\"catId\\\":{\\\"type\\\":\\\"integer\\\"},\\\"catName\\\":{\\\"type\\\":\\\"string\\\"},\\\"projectId\\\":{\\\"type\\\":\\\"integer\\\"},\\\"apiDetails\\\":{\\\"type\\\":\\\"array\\\",\\\"items\\\":{\\\"type\\\":\\\"object\\\",\\\"properties\\\":{\\\"projectId\\\":{\\\"type\\\":\\\"integer\\\"},\\\"apiId\\\":{\\\"type\\\":\\\"integer\\\"},\\\"title\\\":{\\\"type\\\":\\\"string\\\"}}}}}}}}}},\\\"errcode\\\":{\\\"type\\\":\\\"integer\\\",\\\"default\\\":\\\"0\\\"},\\\"errmsg\\\":{\\\"type\\\":\\\"string\\\",\\\"default\\\":\\\"成功\\\"}},\\\"$schema\\\":\\\"http://json-schema.org/draft-04/schema#\\\",\\\"description\\\":\\\"api响应信息\\\"}\",\"method\":\"GET\",\"index\":0,\"switch_notice\":true,\"token\":\"d67d3aaaecb81eb2f4522239491bcfa290a3ab89f03f7c8c8c1ba0bccb9ff803\",\"up_time\":1612336118,\"api_opened\":false,\"add_time\":1612336118,\"status\":\"done\",\"desc\":\"<p>API树形接口</p>\"}');
INSERT INTO `offline_api_detail` VALUES (18333, 434, 2894, 'API文档信息', 'API展示接口', '<p>API详细信息</p>', 'API详细信息', '/api/front/api/detail', 'GET', '2020-01-01 00:00:00', '2021-02-02 23:08:39', '2021-02-03_150839', 'offline', '{\"query_path\":{\"path\":\"/api/front/api/detail\",\"params\":[]},\"res_body_type\":\"json\",\"req_request\":\"apiDetailRequest\",\"type\":\"static\",\"title\":\"API详细信息\",\"path\":\"/api/front/api/detail\",\"catid\":\"2894\",\"req_body_is_json_schema\":false,\"__v\":0,\"markdown\":\"API详细信息\",\"req_headers\":[],\"edit_uid\":0,\"tag\":[],\"req_query\":[{\"name\":\"method\",\"subType\":\"null\",\"type\":\"string\",\"required\":0,\"desc\":\"执行方法\"},{\"name\":\"projectId\",\"subType\":\"null\",\"type\":\"integer\",\"required\":0,\"desc\":\"项目ID\"},{\"name\":\"apiId\",\"subType\":\"null\",\"type\":\"integer\",\"required\":0,\"desc\":\"0未上线的API/1已经上线的API\"},{\"name\":\"type\",\"subType\":\"null\",\"type\":\"integer\",\"required\":0,\"desc\":\"\"}],\"res_body_is_json_schema\":true,\"res_body\":\"{\\\"type\\\":\\\"object\\\",\\\"properties\\\":{\\\"title\\\":{\\\"type\\\":\\\"string\\\"},\\\"method\\\":{\\\"type\\\":\\\"string\\\"},\\\"url\\\":{\\\"type\\\":\\\"string\\\"},\\\"catDesc\\\":{\\\"type\\\":\\\"string\\\"},\\\"contentType\\\":{\\\"type\\\":\\\"string\\\"},\\\"req\\\":{\\\"type\\\":\\\"object\\\",\\\"properties\\\":{\\\"modelName\\\":{\\\"type\\\":\\\"string\\\"},\\\"modelVars\\\":{\\\"type\\\":\\\"array\\\",\\\"items\\\":{\\\"type\\\":\\\"object\\\",\\\"properties\\\":{\\\"modelType\\\":{\\\"type\\\":\\\"string\\\"}}}}}},\\\"resp\\\":{\\\"type\\\":\\\"object\\\",\\\"properties\\\":{\\\"modelName\\\":{\\\"type\\\":\\\"string\\\"},\\\"modelVars\\\":{\\\"type\\\":\\\"array\\\",\\\"items\\\":{\\\"type\\\":\\\"object\\\",\\\"properties\\\":{\\\"modelType\\\":{\\\"type\\\":\\\"string\\\"}}}}}},\\\"errcode\\\":{\\\"type\\\":\\\"integer\\\",\\\"default\\\":\\\"0\\\"},\\\"errmsg\\\":{\\\"type\\\":\\\"string\\\",\\\"default\\\":\\\"成功\\\"}},\\\"$schema\\\":\\\"http://json-schema.org/draft-04/schema#\\\",\\\"description\\\":\\\"API详情信息\\\"}\",\"method\":\"GET\",\"index\":0,\"switch_notice\":true,\"token\":\"d67d3aaaecb81eb2f4522239491bcfa290a3ab89f03f7c8c8c1ba0bccb9ff803\",\"up_time\":1612336118,\"api_opened\":false,\"add_time\":1612336118,\"status\":\"done\",\"desc\":\"<p>API详细信息</p>\"}');
INSERT INTO `offline_api_detail` VALUES (19440, 254, 3026, '测试', '演示商品API', '<p>商品详情\n      <p>\n      根据商品ID获取商品详细信息</p>', '商品详情', '/item/detail', 'GET', '2020-01-01 00:00:00', '2021-02-21 21:31:25', '2021-02-22', 'offline', '{\"query_path\":{\"path\":\"/item/detail\",\"params\":[]},\"res_body_type\":\"json\",\"req_request\":\"detailRequest\",\"type\":\"static\",\"title\":\"商品详情\",\"path\":\"/item/detail\",\"catid\":\"3026\",\"req_body_is_json_schema\":false,\"__v\":0,\"markdown\":\"商品详情\\n      <p>\\n      根据商品ID获取商品详细信息\",\"req_headers\":[],\"edit_uid\":0,\"tag\":[],\"req_query\":[{\"name\":\"itemId\",\"subType\":\"null\",\"type\":\"integer\",\"value\":\"0\",\"required\":1,\"example\":\"0\",\"desc\":\"商品ID \"}],\"res_body_is_json_schema\":true,\"res_body\":\"{\\\"type\\\":\\\"object\\\",\\\"properties\\\":{\\\"code\\\":{\\\"type\\\":\\\"integer\\\",\\\"description\\\":\\\"响应码 100成功非100失败\\\"},\\\"message\\\":{\\\"type\\\":\\\"string\\\",\\\"description\\\":\\\"失败消息\\\"},\\\"itemDetailInfo\\\":{\\\"type\\\":\\\"object\\\",\\\"properties\\\":{\\\"color\\\":{\\\"type\\\":\\\"string\\\",\\\"description\\\":\\\"颜色\\\"},\\\"size\\\":{\\\"type\\\":\\\"string\\\",\\\"description\\\":\\\"尺寸\\\"}},\\\"required\\\":[\\\"color\\\",\\\"size\\\"],\\\"description\\\":\\\"商品详情\\\"}},\\\"required\\\":[\\\"itemDetailInfo\\\"],\\\"$schema\\\":\\\"http://json-schema.org/draft-04/schema#\\\",\\\"description\\\":\\\"商品详细信息\\\"}\",\"method\":\"GET\",\"index\":0,\"switch_notice\":true,\"token\":\"cea77500ef16d04aa5585a9e7857b5de7e78a08da89371a6f20344e8e047804d\",\"up_time\":1613971885,\"api_opened\":false,\"add_time\":1613971885,\"status\":\"done\",\"desc\":\"<p>商品详情\\n      <p>\\n      根据商品ID获取商品详细信息</p>\"}');
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;

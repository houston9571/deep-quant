/*
 Navicat Premium Data Transfer

 Source Server         : 127.0.0.1
 Source Server Type    : MySQL
 Source Server Version : 80408
 Source Host           : 127.0.0.1:3306
 Source Schema         : quant

 Target Server Type    : MySQL
 Target Server Version : 80408
 File Encoding         : 65001

 Date: 14/02/2026 04:26:07
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for board_delay
-- ----------------------------
DROP TABLE IF EXISTS `board_delay`;
CREATE TABLE `board_delay` (
  `id` int NOT NULL AUTO_INCREMENT,
  `market` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `code` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `trade_date` date NOT NULL,
  `stock_num` int DEFAULT '0',
  `latest_price` decimal(10,2) DEFAULT NULL,
  `change_rate` decimal(10,2) DEFAULT NULL,
  `change_price` decimal(10,2) DEFAULT NULL,
  `accum_amount` bigint DEFAULT NULL,
  `turnover_rate` decimal(10,2) DEFAULT NULL,
  `market_cap` bigint DEFAULT NULL,
  `up_num` int DEFAULT NULL,
  `down_num` int DEFAULT NULL COMMENT '市盈率(动)',
  `super_large_in` bigint DEFAULT NULL,
  `super_large_out` bigint DEFAULT NULL,
  `super_large_net_in` bigint DEFAULT NULL,
  `super_large_net_ratio` decimal(10,2) DEFAULT NULL,
  `large_in` bigint DEFAULT NULL,
  `large_out` bigint DEFAULT NULL,
  `large_net_in` bigint DEFAULT NULL,
  `large_net_ratio` decimal(10,0) DEFAULT NULL,
  `medium_in` bigint DEFAULT NULL,
  `medium_out` bigint DEFAULT NULL,
  `medium_net_in` bigint DEFAULT NULL,
  `medium_net_ratio` decimal(10,2) DEFAULT NULL,
  `small_in` bigint DEFAULT NULL,
  `small_out` bigint DEFAULT NULL,
  `small_net_in` bigint DEFAULT NULL,
  `small_net_ratio` decimal(10,2) DEFAULT NULL,
  `main_in` bigint DEFAULT NULL,
  `main_out` bigint DEFAULT NULL,
  `main_net_in` bigint DEFAULT NULL,
  `main_net_ratio` decimal(10,2) DEFAULT NULL,
  `retail_in` bigint DEFAULT NULL,
  `retail_out` bigint DEFAULT NULL,
  `retail_net_in` bigint DEFAULT NULL,
  `retail_net_ratio` decimal(10,2) DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `code_trans_inx` (`code`,`trade_date`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1807 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for board_info
-- ----------------------------
DROP TABLE IF EXISTS `board_info`;
CREATE TABLE `board_info` (
  `code` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `name` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `level` varchar(10) DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for board_stock
-- ----------------------------
DROP TABLE IF EXISTS `board_stock`;
CREATE TABLE `board_stock` (
  `id` int NOT NULL AUTO_INCREMENT,
  `code` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `bcode` varchar(10) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=31465 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for dragon_dept
-- ----------------------------
DROP TABLE IF EXISTS `dragon_dept`;
CREATE TABLE `dragon_dept` (
  `id` int NOT NULL AUTO_INCREMENT,
  `code` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `name_full` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `trade_date` date NOT NULL,
  `net_buy_amount` bigint DEFAULT NULL,
  `buy_amount` bigint DEFAULT NULL,
  `sell_amount` bigint DEFAULT NULL,
  `buy_stocks` json DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1908 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for dragon_stock
-- ----------------------------
DROP TABLE IF EXISTS `dragon_stock`;
CREATE TABLE `dragon_stock` (
  `id` int NOT NULL AUTO_INCREMENT,
  `code` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `name` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `trade_date` date NOT NULL,
  `close_price` decimal(10,2) DEFAULT NULL,
  `change_rate` decimal(10,2) DEFAULT NULL,
  `buy_amount` bigint DEFAULT NULL,
  `buy_amount_ratio` decimal(10,2) DEFAULT NULL,
  `sell_amount` bigint DEFAULT NULL,
  `sell_amount_ratio` decimal(10,2) DEFAULT NULL,
  `net_buy_amount` bigint DEFAULT NULL,
  `net_buy_amount_ratio` decimal(10,2) DEFAULT NULL,
  `deal_amount` bigint DEFAULT NULL,
  `deal_amount_ratio` decimal(10,2) DEFAULT NULL,
  `accum_amount` bigint DEFAULT NULL,
  `free_market_cap` bigint DEFAULT NULL,
  `explains` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `explanation` varchar(255) DEFAULT NULL,
  `d1_close_adjchrate` decimal(10,2) DEFAULT NULL,
  `d2_close_adjchrate` decimal(10,2) DEFAULT NULL,
  `d5_close_adjchrate` decimal(10,2) DEFAULT NULL,
  `d10_close_adjchrate` decimal(10,2) DEFAULT NULL,
  `security_type_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3122 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for dragon_stock_detail
-- ----------------------------
DROP TABLE IF EXISTS `dragon_stock_detail`;
CREATE TABLE `dragon_stock_detail` (
  `id` int NOT NULL AUTO_INCREMENT,
  `code` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `name` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `trade_date` date NOT NULL,
  `dept_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `close_price` decimal(10,2) DEFAULT NULL,
  `change_rate` decimal(10,2) DEFAULT NULL,
  `net_buy_amount` bigint DEFAULT NULL,
  `total_net_buy_ratio` decimal(10,2) DEFAULT NULL,
  `buy_amount` bigint DEFAULT NULL,
  `total_buy_ratio` decimal(10,2) DEFAULT NULL,
  `sell_amount` bigint DEFAULT NULL,
  `total_sell_ratio` decimal(10,2) DEFAULT NULL,
  `accum_volume` bigint DEFAULT NULL,
  `accum_amount` bigint DEFAULT NULL,
  `explanation` varchar(255) DEFAULT NULL,
  `trade_id` varchar(20) DEFAULT NULL,
  `dept_code_old` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=13878 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for fund_info
-- ----------------------------
DROP TABLE IF EXISTS `fund_info`;
CREATE TABLE `fund_info` (
  `code` varchar(10) NOT NULL,
  `name` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `type` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `pyjc` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `pyqc` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for org_dept
-- ----------------------------
DROP TABLE IF EXISTS `org_dept`;
CREATE TABLE `org_dept` (
  `code` varchar(20) NOT NULL,
  `name` varchar(60) DEFAULT NULL,
  `code_old` varchar(20) DEFAULT NULL,
  `remark` varchar(255) DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for org_partner
-- ----------------------------
DROP TABLE IF EXISTS `org_partner`;
CREATE TABLE `org_partner` (
  `code` varchar(20) NOT NULL,
  `name` varchar(60) DEFAULT NULL,
  `style` varchar(512) DEFAULT NULL,
  `level` varchar(255) DEFAULT NULL,
  `fund_size` varchar(50) DEFAULT NULL,
  `remark` varchar(255) DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`code`),
  KEY `name_idx` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for org_partner_dept
-- ----------------------------
DROP TABLE IF EXISTS `org_partner_dept`;
CREATE TABLE `org_partner_dept` (
  `id` int NOT NULL AUTO_INCREMENT,
  `partner_code` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `dept_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `dept_name` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for stock_delay
-- ----------------------------
DROP TABLE IF EXISTS `stock_delay`;
CREATE TABLE `stock_delay` (
  `id` int NOT NULL AUTO_INCREMENT,
  `code` varchar(10) NOT NULL,
  `name` varchar(10) NOT NULL,
  `trade_date` date NOT NULL,
  `latest_price` decimal(10,2) DEFAULT NULL,
  `highest_price` decimal(10,2) DEFAULT NULL,
  `lowest_price` decimal(10,2) DEFAULT NULL,
  `open_price` decimal(10,2) DEFAULT NULL,
  `close_price` decimal(10,2) DEFAULT NULL,
  `change_rate` decimal(10,2) DEFAULT NULL,
  `change_price` decimal(10,2) DEFAULT NULL,
  `change_range` decimal(10,2) DEFAULT NULL,
  `volume` bigint DEFAULT NULL,
  `volume_ratio` decimal(10,2) DEFAULT NULL,
  `accum_amount` bigint DEFAULT NULL,
  `turnover_rate` decimal(10,2) DEFAULT NULL,
  `buy_volume` bigint DEFAULT NULL,
  `sell_volume` bigint DEFAULT NULL,
  `limit_up` decimal(10,2) DEFAULT NULL,
  `limit_down` decimal(10,2) DEFAULT NULL,
  `market_cap` bigint DEFAULT NULL,
  `free_market_cap` bigint DEFAULT NULL,
  `pef` decimal(10,2) DEFAULT NULL COMMENT '市盈率(动)',
  `pet` decimal(10,2) DEFAULT NULL,
  `pettm` decimal(10,2) DEFAULT NULL,
  `pb` decimal(10,2) DEFAULT NULL COMMENT '市净率',
  `change_percent5` decimal(10,2) DEFAULT NULL,
  `change_percent60` decimal(10,2) DEFAULT NULL,
  `roe` decimal(10,2) DEFAULT NULL,
  `total_revenue` bigint DEFAULT NULL,
  `total_revenue_rate` decimal(10,2) DEFAULT NULL,
  `net_profit` bigint DEFAULT NULL,
  `net_profit_rate` decimal(10,2) DEFAULT NULL,
  `net_profit_pre` decimal(10,2) DEFAULT NULL,
  `gross_margin` decimal(10,2) DEFAULT NULL,
  `net_margin` decimal(10,2) DEFAULT NULL,
  `debt_ratio` decimal(10,2) DEFAULT NULL COMMENT '负债率',
  `super_large_in` bigint DEFAULT NULL,
  `super_large_out` bigint DEFAULT NULL,
  `super_large_net_in` bigint DEFAULT NULL,
  `super_large_net_ratio` decimal(10,2) DEFAULT NULL,
  `large_in` bigint DEFAULT NULL,
  `large_out` bigint DEFAULT NULL,
  `large_net_in` bigint DEFAULT NULL,
  `large_net_ratio` decimal(10,0) DEFAULT NULL,
  `medium_in` bigint DEFAULT NULL,
  `medium_out` bigint DEFAULT NULL,
  `medium_net_in` bigint DEFAULT NULL,
  `medium_net_ratio` decimal(10,2) DEFAULT NULL,
  `small_in` bigint DEFAULT NULL,
  `small_out` bigint DEFAULT NULL,
  `small_net_in` bigint DEFAULT NULL,
  `small_net_ratio` decimal(10,2) DEFAULT NULL,
  `main_in` bigint DEFAULT NULL,
  `main_out` bigint DEFAULT NULL,
  `main_net_in` bigint DEFAULT NULL,
  `main_net_ratio` decimal(10,2) DEFAULT NULL,
  `retail_in` bigint DEFAULT NULL,
  `retail_out` bigint DEFAULT NULL,
  `retail_net_in` bigint DEFAULT NULL,
  `retail_net_ratio` decimal(10,2) DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `code_trans_inx` (`code`,`trade_date`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=56686 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for stock_info
-- ----------------------------
DROP TABLE IF EXISTS `stock_info`;
CREATE TABLE `stock_info` (
  `code` varchar(8) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '股票代码',
  `name` varchar(10) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '股票名称',
  `market` varchar(4) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '上市交易所',
  `gsmc` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '公司全称',
  `ywmc` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '英文名称',
  `cym` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '曾用名',
  `qy` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '区域',
  `sshy` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '所属行业',
  `sszjhhy` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '所属证监会行业',
  `clrq` date DEFAULT NULL COMMENT '成立日期',
  `ssrq` date DEFAULT NULL COMMENT '上市日期',
  `frdb` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '法人代表',
  `dsz` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '董事长',
  `zczb` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '注册资本',
  `zcdz` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '注册地址',
  `gsjj` text CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci COMMENT '公司简介',
  `jyfw` text CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci COMMENT '经营范围',
  `fxl` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '发行数量',
  `mgfxj` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '发行价格',
  `mjzjje` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '募资资金净额',
  `sort` int DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for stock_realtime
-- ----------------------------
DROP TABLE IF EXISTS `stock_realtime`;
CREATE TABLE `stock_realtime` (
  `id` int NOT NULL AUTO_INCREMENT,
  `code` varchar(10) NOT NULL,
  `name` varchar(10) NOT NULL,
  `trade_date` datetime NOT NULL,
  `market_cap` bigint DEFAULT NULL,
  `free_market_cap` bigint DEFAULT NULL,
  `pef` decimal(10,2) DEFAULT NULL,
  `pet` decimal(10,2) DEFAULT NULL,
  `pettm` decimal(10,2) DEFAULT NULL,
  `pb` decimal(10,2) DEFAULT NULL,
  `turnover_rate` decimal(10,2) DEFAULT NULL,
  `change_amount` decimal(10,2) DEFAULT NULL,
  `change_rate` decimal(10,2) DEFAULT NULL,
  `change_range` decimal(10,2) DEFAULT NULL,
  `latest_price` decimal(10,2) DEFAULT NULL,
  `highest_price` decimal(10,2) DEFAULT NULL,
  `lowest_price` decimal(10,2) DEFAULT NULL,
  `open_price` decimal(10,2) DEFAULT NULL,
  `close_price` decimal(10,2) DEFAULT NULL,
  `volume` bigint DEFAULT NULL,
  `accum_amount` bigint DEFAULT NULL,
  `buy_volume` bigint DEFAULT NULL,
  `sell_volume` bigint DEFAULT NULL,
  `volume_ratio` decimal(6,2) DEFAULT NULL,
  `limit_up` decimal(10,2) DEFAULT NULL,
  `limit_down` decimal(10,2) DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `code_trans_inx` (`code`,`trade_date`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for trade_calendar
-- ----------------------------
DROP TABLE IF EXISTS `trade_calendar`;
CREATE TABLE `trade_calendar` (
  `date` date NOT NULL,
  `week` varchar(10) DEFAULT NULL,
  `is_trade` int DEFAULT NULL,
  `sh` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `sh_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `sh_index` decimal(10,2) DEFAULT NULL,
  `sh_rate` decimal(10,2) DEFAULT NULL,
  `sh_range` decimal(10,2) DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`date`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for trade_note
-- ----------------------------
DROP TABLE IF EXISTS `trade_note`;
CREATE TABLE `trade_note` (
  `id` int NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

SET FOREIGN_KEY_CHECKS = 1;

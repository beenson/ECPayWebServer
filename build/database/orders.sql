/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 100408
 Source Host           : localhost:3306
 Source Schema         : ecpay

 Target Server Type    : MySQL
 Target Server Version : 100408
 File Encoding         : 65001

 Date: 18/04/2021 01:45:39
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for orders
-- ----------------------------
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `userId` int(11) NOT NULL COMMENT '使用者編號',
  `price` int(10) NULL DEFAULT NULL COMMENT '總價',
  `status` int(10) NULL DEFAULT NULL COMMENT '訂單狀態',
  `createAt` datetime(0) NULL DEFAULT NULL COMMENT '建立時間',
  PRIMARY KEY (`id`, `userId`) USING BTREE,
  INDEX `userid`(`userId`) USING BTREE,
  INDEX `id`(`id`) USING BTREE,
  CONSTRAINT `userid` FOREIGN KEY (`userid`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;

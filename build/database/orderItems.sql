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

 Date: 18/04/2021 01:45:09
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for orderitems
-- ----------------------------
DROP TABLE IF EXISTS `orderitems`;
CREATE TABLE `orderitems`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `orderId` int(11) NOT NULL COMMENT '訂單編號',
  `productId` int(11) NOT NULL COMMENT '商品編號',
  `amount` int(11) NULL DEFAULT NULL COMMENT '購買數量',
  PRIMARY KEY (`id`, `orderId`, `productId`) USING BTREE,
  INDEX `orderid`(`orderId`) USING BTREE,
  INDEX `productId`(`productId`) USING BTREE,
  CONSTRAINT `orderid` FOREIGN KEY (`orderId`) REFERENCES `orders` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `productId` FOREIGN KEY (`productId`) REFERENCES `products` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;

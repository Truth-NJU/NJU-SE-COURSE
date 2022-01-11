/*
 Navicat Premium Data Transfer

 Source Server         : mysql
 Source Server Type    : MySQL
 Source Server Version : 80023
 Source Host           : localhost:3306
 Source Schema         : UserData

 Target Server Type    : MySQL
 Target Server Version : 80023
 File Encoding         : 65001

 Date: 08/12/2021 16:49:23
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `username` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  PRIMARY KEY (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of user
-- ----------------------------
BEGIN;
INSERT INTO `user` VALUES ('qwee', '$2a$10$js1WBEA/dey0R98VhljXuO4FhA0ulJltK.BJLLqR.2.IiRqIXCW8K');
INSERT INTO `user` VALUES ('Truth', '$2a$10$5GOpm.sR2M.HRJs1c1Q3kO8LbUWvL5lFA7vlkyjlKuQC/FfdmEKsq');
INSERT INTO `user` VALUES ('tzh', '$2a$10$oCVChLSZWGgSas5Ue3kYEe2SCiNhMGgwQFsHf568ZKv1UukvmPz4e');
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;

/*
SQLyog Ultimate v11.33 (64 bit)
MySQL - 8.0.13 : Database - voice_red_packet
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`voice_red_packet` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci */;

USE `voice_red_packet`;

/*Table structure for table `t_red_packet` */

DROP TABLE IF EXISTS `t_red_packet`;

CREATE TABLE `t_red_packet` (
  `id` int(12) NOT NULL AUTO_INCREMENT COMMENT '红包编号',
  `user_id` int(12) NOT NULL COMMENT '发红包的用户id',
  `amount` decimal(16,2) NOT NULL COMMENT '红包金额',
  `send_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '发红包日期',
  `total` int(12) NOT NULL COMMENT '红包总数',
  `unit_amount` decimal(12,0) NOT NULL COMMENT '单个红包的金额',
  `stock` int(12) NOT NULL COMMENT '红包剩余个数',
  `version` int(12) NOT NULL DEFAULT '0' COMMENT '版本（为后续扩展用）',
  `note` varchar(256) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

/*Data for the table `t_red_packet` */

insert  into `t_red_packet`(`id`,`user_id`,`amount`,`send_date`,`total`,`unit_amount`,`stock`,`version`,`note`) values (1,1,'200000.00','2018-12-20 00:27:23',20000,'10',20000,0,'20万元金额，2万个小红包，每个10元');

/*Table structure for table `t_user_red_packet` */

DROP TABLE IF EXISTS `t_user_red_packet`;

CREATE TABLE `t_user_red_packet` (
  `id` int(12) NOT NULL AUTO_INCREMENT COMMENT '用户抢到的红包id',
  `red_packet_id` int(12) NOT NULL COMMENT '红包id',
  `user_id` int(12) NOT NULL COMMENT '抢红包用户的id',
  `amount` decimal(16,2) NOT NULL COMMENT '抢到的红包金额',
  `grab_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '抢红包时间',
  `note` varchar(256) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

/*Data for the table `t_user_red_packet` */

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

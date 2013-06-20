-- --------------------------------------------------------
-- Host                          :127.0.0.1
-- Server version                :5.5.24-log - MySQL Community Server (GPL)
-- Server OS                     :Win64
-- HeidiSQL Version              :7.0.0.4249
-- Created                       :2013-05-04 18:06:37
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;

-- Dumping database structure for gloredb
DROP DATABASE IF EXISTS `gloredb`;
CREATE DATABASE IF NOT EXISTS `gloredb` /*!40100 DEFAULT CHARACTER SET latin1 */;
USE `gloredb`;


-- Dumping structure for table gloredb.gtask
DROP TABLE IF EXISTS `gtask`;
CREATE TABLE IF NOT EXISTS `gtask` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `NAME` varchar(100) NOT NULL,
  `DESCRIPTION` varchar(500) DEFAULT NULL,
  `EXP_DATE` datetime DEFAULT NULL,
  `PARAMETERS` varchar(200) DEFAULT NULL,
  `OWNER_EMAIL` varchar(50) NOT NULL,
  `Target` text,
  `property` varchar(200) DEFAULT NULL,
  `Taskstatus` int(1) NOT NULL DEFAULT '0',
  `NumClient` int(11) DEFAULT '0',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `NAME` (`NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.


-- Dumping structure for table gloredb.registeduser
DROP TABLE IF EXISTS `registeduser`;
CREATE TABLE IF NOT EXISTS `registeduser` (
  `name` varchar(30) DEFAULT NULL,
  `email` varchar(30) DEFAULT NULL,
  `password` varchar(30) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.


-- Dumping structure for table gloredb.tempresult
DROP TABLE IF EXISTS `tempresult`;
CREATE TABLE IF NOT EXISTS `tempresult` (
  `taskname` varchar(100) DEFAULT NULL,
  `iteration` int(2) DEFAULT NULL,
  `cov` text,
  `stepFinish` int(1) DEFAULT '0',
  `taskfinish` int(1) DEFAULT '0',
  `type` int(1) DEFAULT '0',
  `participant` varchar(200) DEFAULT NULL,
  `beta` text,
  KEY `taskname` (`taskname`),
  CONSTRAINT `tempresult_ibfk_1` FOREIGN KEY (`taskname`) REFERENCES `gtask` (`NAME`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.


-- Dumping structure for table gloredb.user
DROP TABLE IF EXISTS `user`;
CREATE TABLE IF NOT EXISTS `user` (
  `EMAIL` varchar(50) DEFAULT NULL,
  `Dimension` int(10) DEFAULT NULL,
  `Records` int(10) DEFAULT NULL,
  `Weight` text,
  `PartialSum` text,
  `name` varchar(20) DEFAULT NULL,
  `password` varchar(30) DEFAULT NULL,
  `ready` int(1) DEFAULT '0',
  `datapath` varchar(100) DEFAULT NULL,
  `task_id` int(11) DEFAULT NULL,
  KEY `task_id` (`task_id`),
  CONSTRAINT `user_ibfk_1` FOREIGN KEY (`task_id`) REFERENCES `gtask` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;

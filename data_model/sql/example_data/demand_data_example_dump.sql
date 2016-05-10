-- MySQL dump 10.13  Distrib 5.5.31, for debian-linux-gnu (x86_64)
--
-- Host: localhost    Database: adserver
-- ------------------------------------------------------
-- Server version	5.5.31-0ubuntu0.12.10.1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `account`
--

DROP TABLE IF EXISTS `account`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `account` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `guid` varchar(100) NOT NULL,
  `type_id` smallint(6) NOT NULL,
  `name` varchar(100) NOT NULL,
  `registration_id` int(10) unsigned NOT NULL,
  `modified_by` int(10) unsigned NOT NULL,
  `created_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `last_modified` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`id`),
  UNIQUE KEY `guid` (`guid`),
  KEY `fk_account_acctype` (`type_id`),
  KEY `fk_account_registration` (`registration_id`),
  KEY `fk_account_modified_by` (`modified_by`),
  CONSTRAINT `fk_account_acctype` FOREIGN KEY (`type_id`) REFERENCES `account_type` (`id`),
  CONSTRAINT `fk_account_registration` FOREIGN KEY (`registration_id`) REFERENCES `registration_details` (`id`),
  CONSTRAINT `fk_account_modified_by` FOREIGN KEY (`modified_by`) REFERENCES `registration_details` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `account`
--

LOCK TABLES `account` WRITE;
/*!40000 ALTER TABLE `account` DISABLE KEYS */;
INSERT INTO `account` VALUES (1,'test_pub_guid',1,'test_pub',1,3,'2013-07-10 07:54:33','2013-07-10 07:54:33'),(2,'test_adv_guid',2,'test_adv',2,3,'2013-07-10 07:54:33','2013-07-10 07:54:33');
/*!40000 ALTER TABLE `account` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `account_budget`
--

DROP TABLE IF EXISTS `account_budget`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `account_budget` (
  `guid` varchar(100) NOT NULL,
  `total_budget` double DEFAULT NULL,
  `remaining_budget` double DEFAULT NULL,
  `burn` double DEFAULT NULL,
  `modified_by` int(10) unsigned NOT NULL,
  `created_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `last_modified` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  UNIQUE KEY `guid` (`guid`),
  KEY `fk_account_budget_modified_by` (`modified_by`),
  CONSTRAINT `fk_account_budget_guid` FOREIGN KEY (`guid`) REFERENCES `account` (`guid`),
  CONSTRAINT `fk_account_budget_modified_by` FOREIGN KEY (`modified_by`) REFERENCES `registration_details` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `account_budget`
--

LOCK TABLES `account_budget` WRITE;
/*!40000 ALTER TABLE `account_budget` DISABLE KEYS */;
INSERT INTO `account_budget` VALUES ('test_adv_guid',3000,3000,0,3,'2013-07-10 07:54:34','2013-07-10 07:54:34');
/*!40000 ALTER TABLE `account_budget` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `account_budget_snapshot`
--

DROP TABLE IF EXISTS `account_budget_snapshot`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `account_budget_snapshot` (
  `guid` varchar(100) NOT NULL,
  `total_budget` double DEFAULT NULL,
  `remaining_budget` double DEFAULT NULL,
  `burn` double DEFAULT NULL,
  `modified_by` int(11) NOT NULL,
  `snapshot_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `last_modified` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `created_on` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  KEY `fk_budget_snapshot_account` (`guid`),
  CONSTRAINT `fk_budget_snapshot_account` FOREIGN KEY (`guid`) REFERENCES `account_budget` (`guid`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `account_budget_snapshot`
--

LOCK TABLES `account_budget_snapshot` WRITE;
/*!40000 ALTER TABLE `account_budget_snapshot` DISABLE KEYS */;
/*!40000 ALTER TABLE `account_budget_snapshot` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `account_type`
--

DROP TABLE IF EXISTS `account_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `account_type` (
  `id` smallint(6) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `description` varchar(200) NOT NULL,
  `modified_by` varchar(50) NOT NULL,
  `created_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `last_modified` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `is_deprecated` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `account_type`
--

LOCK TABLES `account_type` WRITE;
/*!40000 ALTER TABLE `account_type` DISABLE KEYS */;
INSERT INTO `account_type` VALUES (1,'direct-pub','direct-pub','developer','2013-07-10 07:54:33','2013-07-10 07:54:33',0),(2,'direct-adv','direct-adv','developer','2013-07-10 07:54:33','2013-07-10 07:54:33',0);
/*!40000 ALTER TABLE `account_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ad`
--

DROP TABLE IF EXISTS `ad`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ad` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `guid` varchar(100) NOT NULL,
  `name` varchar(100) NOT NULL,
  `creative_id` int(10) unsigned NOT NULL,
  `creative_guid` varchar(100) NOT NULL,
  `landing_url` varchar(1024) DEFAULT NULL,
  `campaign_id` int(10) unsigned NOT NULL,
  `campaign_guid` varchar(100) NOT NULL,
  `status_id` smallint(6) NOT NULL,
  `marketplace_id` smallint(6) NOT NULL,
  `bid` double NOT NULL,
  `tracking_code` varchar(2000) DEFAULT NULL,
  `created_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `last_modified` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `modified_by` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `guid` (`guid`),
  KEY `fk_ad_creativeid` (`creative_id`),
  KEY `fk_ad_creativeguid` (`creative_guid`),
  KEY `fk_ad_campaignid` (`campaign_id`),
  KEY `fk_ad_campaignguid` (`campaign_guid`),
  KEY `fk_ad_status` (`status_id`),
  KEY `fk_ad_marketplace` (`marketplace_id`),
  KEY `fk_ad_modified_by` (`modified_by`),
  CONSTRAINT `fk_ad_creativeid` FOREIGN KEY (`creative_id`) REFERENCES `creative_container` (`id`),
  CONSTRAINT `fk_ad_creativeguid` FOREIGN KEY (`creative_guid`) REFERENCES `creative_container` (`guid`),
  CONSTRAINT `fk_ad_campaignid` FOREIGN KEY (`campaign_id`) REFERENCES `campaign` (`id`),
  CONSTRAINT `fk_ad_campaignguid` FOREIGN KEY (`campaign_guid`) REFERENCES `campaign` (`guid`),
  CONSTRAINT `fk_ad_status` FOREIGN KEY (`status_id`) REFERENCES `status` (`id`),
  CONSTRAINT `fk_ad_marketplace` FOREIGN KEY (`marketplace_id`) REFERENCES `marketplace` (`id`),
  CONSTRAINT `fk_ad_modified_by` FOREIGN KEY (`modified_by`) REFERENCES `registration_details` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ad`
--

LOCK TABLES `ad` WRITE;
/*!40000 ALTER TABLE `ad` DISABLE KEYS */;
INSERT INTO `ad` VALUES (1,'test_ad_guid','test_banner_ad',1,'test_creative_guid','http://kritter.in',1,'test_camp_guid',1,1,0.03,NULL,'2013-07-10 07:54:34','2013-07-10 07:54:34',3);
/*!40000 ALTER TABLE `ad` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `campaign`
--

DROP TABLE IF EXISTS `campaign`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `campaign` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `guid` varchar(100) NOT NULL,
  `name` varchar(100) NOT NULL,
  `account_id` varchar(100) NOT NULL,
  `objective_id` smallint(6) NOT NULL,
  `targeting_id` int(10) unsigned DEFAULT NULL,
  `bid` double NOT NULL,
  `landing_url` varchar(1024) NOT NULL,
  `status_id` smallint(6) NOT NULL,
  `created_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `start_date` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `end_date` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `modified_by` int(10) unsigned NOT NULL,
  `last_modified` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`id`),
  UNIQUE KEY `guid` (`guid`),
  KEY `fk_campaign_account` (`account_id`),
  KEY `fk_campaign_objective` (`objective_id`),
  KEY `fk_campaign_targeting` (`targeting_id`),
  KEY `fk_campaign_status` (`status_id`),
  KEY `fk_campaign_modified_by` (`modified_by`),
  CONSTRAINT `fk_campaign_account` FOREIGN KEY (`account_id`) REFERENCES `account` (`guid`),
  CONSTRAINT `fk_campaign_objective` FOREIGN KEY (`objective_id`) REFERENCES `campaign_objective` (`id`),
  CONSTRAINT `fk_campaign_targeting` FOREIGN KEY (`targeting_id`) REFERENCES `campaign_targeting` (`id`),
  CONSTRAINT `fk_campaign_status` FOREIGN KEY (`status_id`) REFERENCES `status` (`id`),
  CONSTRAINT `fk_campaign_modified_by` FOREIGN KEY (`modified_by`) REFERENCES `registration_details` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `campaign`
--

LOCK TABLES `campaign` WRITE;
/*!40000 ALTER TABLE `campaign` DISABLE KEYS */;
INSERT INTO `campaign` VALUES (1,'test_camp_guid','test_campaign','test_adv_guid',1,1,0.03,'http://kritter.in',11,'2013-07-10 07:54:34','2013-07-10 07:54:34','2013-07-10 07:55:34',3,'2013-07-10 07:54:34');
/*!40000 ALTER TABLE `campaign` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `campaign_budget`
--

DROP TABLE IF EXISTS `campaign_budget`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `campaign_budget` (
  `guid` varchar(100) NOT NULL,
  `account_guid` varchar(100) DEFAULT NULL,
  `total_budget` double DEFAULT NULL,
  `total_burn` double DEFAULT NULL,
  `total_remaining_budget` double DEFAULT NULL,
  `daily_budget` double DEFAULT NULL,
  `daily_burn` double DEFAULT NULL,
  `daily_remaining_budget` double DEFAULT NULL,
  `modified_by` int(10) unsigned NOT NULL,
  `created_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `last_modified` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  UNIQUE KEY `guid` (`guid`),
  KEY `fk_campaign_budget_account_guid` (`account_guid`),
  KEY `fk_campaign_budget_modified_by` (`modified_by`),
  CONSTRAINT `fk_campaign_budget_guid` FOREIGN KEY (`guid`) REFERENCES `campaign` (`guid`),
  CONSTRAINT `fk_campaign_budget_account_guid` FOREIGN KEY (`account_guid`) REFERENCES `account` (`guid`),
  CONSTRAINT `fk_campaign_budget_modified_by` FOREIGN KEY (`modified_by`) REFERENCES `registration_details` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `campaign_budget`
--

LOCK TABLES `campaign_budget` WRITE;
/*!40000 ALTER TABLE `campaign_budget` DISABLE KEYS */;
INSERT INTO `campaign_budget` VALUES ('test_camp_guid','test_adv_guid',3000,0,3000,300,0,300,3,'2013-07-10 07:54:34','2013-07-10 07:54:34');
/*!40000 ALTER TABLE `campaign_budget` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `campaign_budget_snapshot`
--

DROP TABLE IF EXISTS `campaign_budget_snapshot`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `campaign_budget_snapshot` (
  `guid` varchar(100) NOT NULL,
  `account_guid` varchar(100) DEFAULT NULL,
  `total_budget` double DEFAULT NULL,
  `total_burn` double DEFAULT NULL,
  `total_remaining_budget` double DEFAULT NULL,
  `daily_budget` double DEFAULT NULL,
  `daily_burn` double DEFAULT NULL,
  `daily_remaining_budget` double DEFAULT NULL,
  `modified_by` int(11) NOT NULL,
  `snapshot_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `last_modified` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `created_on` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  KEY `fk_budget_snapshot_campaign` (`guid`),
  CONSTRAINT `fk_budget_snapshot_campaign` FOREIGN KEY (`guid`) REFERENCES `campaign_budget` (`guid`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `campaign_budget_snapshot`
--

LOCK TABLES `campaign_budget_snapshot` WRITE;
/*!40000 ALTER TABLE `campaign_budget_snapshot` DISABLE KEYS */;
/*!40000 ALTER TABLE `campaign_budget_snapshot` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `campaign_objective`
--

DROP TABLE IF EXISTS `campaign_objective`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `campaign_objective` (
  `id` smallint(6) NOT NULL AUTO_INCREMENT,
  `description` varchar(300) NOT NULL,
  `min_bid` double NOT NULL,
  `rule_json` text NOT NULL,
  `modified_by` varchar(50) NOT NULL,
  `created_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `last_modified` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `is_deprecated` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `campaign_objective`
--

LOCK TABLES `campaign_objective` WRITE;
/*!40000 ALTER TABLE `campaign_objective` DISABLE KEYS */;
INSERT INTO `campaign_objective` VALUES (1,'traffic',0.03,'{}','developer','2013-07-10 07:54:33','2013-07-10 07:54:33',0);
/*!40000 ALTER TABLE `campaign_objective` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `campaign_targeting`
--

DROP TABLE IF EXISTS `campaign_targeting`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `campaign_targeting` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `brand_name_list` text,
  `model_list` text,
  `os_json` varchar(1000) DEFAULT NULL,
  `browser_json` varchar(1000) DEFAULT NULL,
  `country_list` text,
  `carrier_list` text,
  `state_list` text,
  `city_list` text,
  `zipcode_list` text,
  `modified_by` int(10) unsigned NOT NULL,
  `created_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `last_modified` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`id`),
  KEY `fk_campaign_targeting_modified_by` (`modified_by`),
  CONSTRAINT `fk_campaign_targeting_modified_by` FOREIGN KEY (`modified_by`) REFERENCES `registration_details` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `campaign_targeting`
--

LOCK TABLES `campaign_targeting` WRITE;
/*!40000 ALTER TABLE `campaign_targeting` DISABLE KEYS */;
INSERT INTO `campaign_targeting` VALUES (1,'{1}','{1}','{}','{}','{30}','{}','{}','{}','{}',3,'2013-07-10 07:54:34','2013-07-10 07:54:34');
/*!40000 ALTER TABLE `campaign_targeting` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `categories`
--

DROP TABLE IF EXISTS `categories`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `categories` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `code` varchar(50) NOT NULL,
  `value` varchar(100) NOT NULL,
  `description` varchar(200) NOT NULL,
  `modified_by` varchar(50) NOT NULL,
  `created_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `last_modified` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `is_visible_on_advertiser_ui` tinyint(1) DEFAULT '1',
  `is_visible_on_publisher_ui` tinyint(1) DEFAULT '1',
  `is_deprecated` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `code` (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `categories`
--

LOCK TABLES `categories` WRITE;
/*!40000 ALTER TABLE `categories` DISABLE KEYS */;
INSERT INTO `categories` VALUES (1,'IAB-1','sport and entertainment','not correct category','developer','2013-07-10 07:54:34','2013-07-10 07:54:34',1,1,0);
/*!40000 ALTER TABLE `categories` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `creative_container`
--

DROP TABLE IF EXISTS `creative_container`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `creative_container` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `guid` varchar(100) NOT NULL,
  `account_id` varchar(100) NOT NULL,
  `label` varchar(100) NOT NULL,
  `format_id` smallint(6) NOT NULL,
  `text` varchar(140) DEFAULT NULL,
  `banner_uri` varchar(1024) DEFAULT NULL,
  `slot_id` smallint(6) NOT NULL,
  `html_content` varchar(2000) DEFAULT NULL,
  `modified_by` int(10) unsigned NOT NULL,
  `created_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `last_modified` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`id`),
  UNIQUE KEY `guid` (`guid`),
  UNIQUE KEY `account_id` (`account_id`),
  KEY `fk_creative_format` (`format_id`),
  KEY `fk_creative_slot` (`slot_id`),
  KEY `fk_creative_modified_by` (`modified_by`),
  CONSTRAINT `fk_creative_account` FOREIGN KEY (`account_id`) REFERENCES `account` (`guid`),
  CONSTRAINT `fk_creative_format` FOREIGN KEY (`format_id`) REFERENCES `creative_formats` (`id`),
  CONSTRAINT `fk_creative_slot` FOREIGN KEY (`slot_id`) REFERENCES `creative_slots` (`id`),
  CONSTRAINT `fk_creative_modified_by` FOREIGN KEY (`modified_by`) REFERENCES `registration_details` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `creative_container`
--

LOCK TABLES `creative_container` WRITE;
/*!40000 ALTER TABLE `creative_container` DISABLE KEYS */;
INSERT INTO `creative_container` VALUES (1,'test_creative_guid','test_adv_guid','testbannerad',1,'test banner','banner.jpg',1,NULL,3,'2013-07-10 07:54:33','2013-07-10 07:54:33');
/*!40000 ALTER TABLE `creative_container` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `creative_formats`
--

DROP TABLE IF EXISTS `creative_formats`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `creative_formats` (
  `id` smallint(6) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `description` varchar(300) NOT NULL,
  `modified_by` varchar(50) NOT NULL,
  `created_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `last_modified` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `is_deprecated` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `creative_formats`
--

LOCK TABLES `creative_formats` WRITE;
/*!40000 ALTER TABLE `creative_formats` DISABLE KEYS */;
INSERT INTO `creative_formats` VALUES (1,'banner','banner','developer','2013-07-10 07:54:33','2013-07-10 07:54:33',0);
/*!40000 ALTER TABLE `creative_formats` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `creative_slots`
--

DROP TABLE IF EXISTS `creative_slots`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `creative_slots` (
  `id` smallint(6) NOT NULL AUTO_INCREMENT,
  `width` smallint(6) NOT NULL,
  `height` smallint(6) NOT NULL,
  `modified_by` varchar(50) NOT NULL,
  `created_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `last_modified` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `is_deprecated` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `creative_slots`
--

LOCK TABLES `creative_slots` WRITE;
/*!40000 ALTER TABLE `creative_slots` DISABLE KEYS */;
INSERT INTO `creative_slots` VALUES (1,300,250,'developer','2013-07-10 07:54:33','2013-07-10 07:54:33',0);
/*!40000 ALTER TABLE `creative_slots` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `marketplace`
--

DROP TABLE IF EXISTS `marketplace`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `marketplace` (
  `id` smallint(6) NOT NULL AUTO_INCREMENT,
  `pricing` varchar(50) NOT NULL,
  `description` varchar(300) NOT NULL,
  `modified_by` varchar(50) NOT NULL,
  `created_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `last_modified` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `is_deprecated` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `pricing` (`pricing`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `marketplace`
--

LOCK TABLES `marketplace` WRITE;
/*!40000 ALTER TABLE `marketplace` DISABLE KEYS */;
INSERT INTO `marketplace` VALUES (1,'cpm','costperimpression','developer','2013-07-10 07:54:33','2013-07-10 07:54:33',0);
/*!40000 ALTER TABLE `marketplace` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `registration_details`
--

DROP TABLE IF EXISTS `registration_details`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `registration_details` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `username` varchar(100) NOT NULL,
  `password` varchar(100) NOT NULL,
  `email` varchar(200) NOT NULL,
  `address` varchar(200) NOT NULL,
  `country` varchar(50) NOT NULL,
  `city` varchar(100) NOT NULL,
  `created_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `last_modified` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `registration_details`
--

LOCK TABLES `registration_details` WRITE;
/*!40000 ALTER TABLE `registration_details` DISABLE KEYS */;
INSERT INTO `registration_details` VALUES (1,'test_pub','testpub','test','test@test','addr','india','bangalore','2013-07-10 07:54:33','2013-07-10 07:54:33'),(2,'test_adv','testadv','test','test@test','addr','india','bangalore','2013-07-10 07:54:33','2013-07-10 07:54:33'),(3,'developer','testdeveloper','test','test@test','addr','india','bangalore','2013-07-10 07:54:33','2013-07-10 07:54:33');
/*!40000 ALTER TABLE `registration_details` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `site`
--

DROP TABLE IF EXISTS `site`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `site` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `guid` varchar(100) NOT NULL,
  `name` varchar(100) NOT NULL,
  `pub_id` varchar(100) NOT NULL,
  `site_url` varchar(1024) NOT NULL,
  `app_id` varchar(100) DEFAULT NULL,
  `app_store_id` smallint(6) DEFAULT NULL,
  `categories_list` text,
  `site_platform_id` smallint(5) unsigned NOT NULL,
  `status_id` smallint(6) NOT NULL,
  `created_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `last_modified` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `modified_by` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `guid` (`guid`),
  KEY `fk_site_account` (`pub_id`),
  KEY `fk_site_platform` (`site_platform_id`),
  KEY `fk_site_status` (`status_id`),
  KEY `fk_site_modified_by` (`modified_by`),
  CONSTRAINT `fk_site_account` FOREIGN KEY (`pub_id`) REFERENCES `account` (`guid`),
  CONSTRAINT `fk_site_platform` FOREIGN KEY (`site_platform_id`) REFERENCES `site_platform` (`id`),
  CONSTRAINT `fk_site_status` FOREIGN KEY (`status_id`) REFERENCES `status` (`id`),
  CONSTRAINT `fk_site_modified_by` FOREIGN KEY (`modified_by`) REFERENCES `registration_details` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `site`
--

LOCK TABLES `site` WRITE;
/*!40000 ALTER TABLE `site` DISABLE KEYS */;
INSERT INTO `site` VALUES (1,'test_site_guid','test_site','test_pub_guid','http://kritter.in',NULL,NULL,'{1}',1,21,'2013-07-10 07:54:34','2013-07-10 07:54:34',3);
/*!40000 ALTER TABLE `site` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `site_platform`
--

DROP TABLE IF EXISTS `site_platform`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `site_platform` (
  `id` smallint(5) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `description` varchar(200) NOT NULL,
  `modified_by` varchar(50) NOT NULL,
  `created_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `last_modified` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `site_platform`
--

LOCK TABLES `site_platform` WRITE;
/*!40000 ALTER TABLE `site_platform` DISABLE KEYS */;
INSERT INTO `site_platform` VALUES (1,'wap','mobile website','developer','2013-07-10 07:54:34','2013-07-10 07:54:34');
/*!40000 ALTER TABLE `site_platform` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `status`
--

DROP TABLE IF EXISTS `status`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `status` (
  `id` smallint(6) NOT NULL,
  `name` varchar(50) NOT NULL,
  `description` varchar(300) NOT NULL,
  `modified_by` varchar(50) NOT NULL,
  `created_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `last_modified` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `is_deprecated` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `status`
--

LOCK TABLES `status` WRITE;
/*!40000 ALTER TABLE `status` DISABLE KEYS */;
INSERT INTO `status` VALUES (1,'ad-active','ad is active','admin','2013-07-10 07:54:18','2013-07-10 07:54:18',0),(11,'campaign-active','campaign is active','admin','2013-07-10 07:54:18','2013-07-10 07:54:18',0),(21,'site-active','site is active','admin','2013-07-10 07:54:18','2013-07-10 07:54:18',0);
/*!40000 ALTER TABLE `status` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2013-07-10 13:25:41

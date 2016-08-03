CREATE TABLE IF NOT EXISTS `first_level` (
  `processing_time` datetime DEFAULT NULL,
  `impression_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `inventorySource` varchar(32) DEFAULT NULL, /* AdservingRequestResponse inventory_source  */
  `event_type` varchar(32) DEFAULT NULL, /* PostImpressionEvent event */
  `siteId` int(11) DEFAULT NULL,
  `deviceId` mediumtext,
  `deviceManufacturerId` int(11) DEFAULT NULL,
  `deviceModelId` int(11) DEFAULT NULL,
  `deviceOsId` int(11) DEFAULT NULL,
  `countryId` int(11) DEFAULT NULL,
  `countryCarrierId` int(11) DEFAULT NULL,
  `countryRegionId` int(11) DEFAULT NULL,
  `exchangeId` int(11) DEFAULT NULL,
  `creativeId` int(11) DEFAULT NULL,
  `adId` int(11) DEFAULT NULL,
  `campaignId` int(11) DEFAULT NULL,
  `advertiserId` varchar(128) DEFAULT NULL,
  `bidderModelId` int(11) DEFAULT NULL,
  `nofillReason` varchar(128) DEFAULT NULL,
  `total_request` int(11) DEFAULT '0',
  `total_impression` int(11) DEFAULT '0',
  `total_bidValue` double DEFAULT '0',
  `total_click` int(11) DEFAULT '0',
  `total_win` int(11) DEFAULT '0',
  `total_win_bidValue` double DEFAULT '0',
  `total_csc` int(11) DEFAULT '0',
  `total_event_type` int(11) DEFAULT '0',
  `demandCharges` double DEFAULT '0',
  `supplyCost` double DEFAULT '0',
  `earning` double DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE first_level ADD INDEX first_level_siteId(siteId);
ALTER TABLE first_level ADD INDEX first_level_deviceManufacturerId(deviceManufacturerId);
ALTER TABLE first_level ADD INDEX first_level_deviceModelId(deviceModelId);
ALTER TABLE first_level ADD INDEX first_level_deviceOsId(deviceOsId);
ALTER TABLE first_level ADD INDEX first_level_countryId(countryId);
ALTER TABLE first_level ADD INDEX first_level_countryCarrierId(countryCarrierId);
ALTER TABLE first_level ADD INDEX first_level_adId(adId);
ALTER TABLE first_level ADD INDEX first_level_campaignId(campaignId);


CREATE TABLE IF NOT EXISTS `first_level_daily` (
  `processing_time` datetime DEFAULT NULL,
  `impression_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `inventorySource` varchar(32) DEFAULT NULL, /* AdservingRequestResponse inventory_source  */
  `event_type` varchar(32) DEFAULT NULL, /* PostImpressionEvent event */
  `siteId` int(11) DEFAULT NULL,
  `deviceId` mediumtext,
  `deviceManufacturerId` int(11) DEFAULT NULL,
  `deviceModelId` int(11) DEFAULT NULL,
  `deviceOsId` int(11) DEFAULT NULL,
  `countryId` int(11) DEFAULT NULL,
  `countryCarrierId` int(11) DEFAULT NULL,
  `countryRegionId` int(11) DEFAULT NULL,
  `exchangeId` int(11) DEFAULT NULL,
  `creativeId` int(11) DEFAULT NULL,
  `adId` int(11) DEFAULT NULL,
  `campaignId` int(11) DEFAULT NULL,
  `advertiserId` varchar(128) DEFAULT NULL,
  `bidderModelId` int(11) DEFAULT NULL,
  `nofillReason` varchar(128) DEFAULT NULL,
  `total_request` int(11) DEFAULT '0',
  `total_impression` int(11) DEFAULT '0',
  `total_bidValue` double DEFAULT '0',
  `total_click` int(11) DEFAULT '0',
  `total_win` int(11) DEFAULT '0',
  `total_win_bidValue` double DEFAULT '0',
  `total_csc` int(11) DEFAULT '0',
  `total_event_type` int(11) DEFAULT '0',
  `demandCharges` double DEFAULT '0',
  `supplyCost` double DEFAULT '0',
  `earning` double DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE first_level_daily ADD INDEX first_level_daily_siteId(siteId);
ALTER TABLE first_level_daily ADD INDEX first_level_daily_deviceManufacturerId(deviceManufacturerId);
ALTER TABLE first_level_daily ADD INDEX first_level_daily_deviceModelId(deviceModelId);
ALTER TABLE first_level_daily ADD INDEX first_level_daily_deviceOsId(deviceOsId);
ALTER TABLE first_level_daily ADD INDEX first_level_daily_countryId(countryId);
ALTER TABLE first_level_daily ADD INDEX first_level_daily_countryCarrierId(countryCarrierId);
ALTER TABLE first_level_daily ADD INDEX first_level_daily_adId(adId);
ALTER TABLE first_level_daily ADD INDEX first_level_daily_campaignId(campaignId);


CREATE TABLE IF NOT EXISTS `first_level_monthly` (
  `processing_time` datetime DEFAULT NULL,
  `impression_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `inventorySource` varchar(32) DEFAULT NULL, /* AdservingRequestResponse inventory_source  */
  `event_type` varchar(32) DEFAULT NULL, /* PostImpressionEvent event */
  `siteId` int(11) DEFAULT NULL,
  `deviceId` mediumtext,
  `deviceManufacturerId` int(11) DEFAULT NULL,
  `deviceModelId` int(11) DEFAULT NULL,
  `deviceOsId` int(11) DEFAULT NULL,
  `countryId` int(11) DEFAULT NULL,
  `countryCarrierId` int(11) DEFAULT NULL,
  `countryRegionId` int(11) DEFAULT NULL,
  `exchangeId` int(11) DEFAULT NULL,
  `creativeId` int(11) DEFAULT NULL,
  `adId` int(11) DEFAULT NULL,
  `campaignId` int(11) DEFAULT NULL,
  `advertiserId` varchar(128) DEFAULT NULL,
  `bidderModelId` int(11) DEFAULT NULL,
  `nofillReason` varchar(128) DEFAULT NULL,
  `total_request` int(11) DEFAULT '0',
  `total_impression` int(11) DEFAULT '0',
  `total_bidValue` double DEFAULT '0',
  `total_click` int(11) DEFAULT '0',
  `total_win` int(11) DEFAULT '0',
  `total_win_bidValue` double DEFAULT '0',
  `total_csc` int(11) DEFAULT '0',
  `total_event_type` int(11) DEFAULT '0',
  `demandCharges` double DEFAULT '0',
  `supplyCost` double DEFAULT '0',
  `earning` double DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
ALTER TABLE first_level_monthly ADD INDEX first_level_monthly_siteId(siteId);
ALTER TABLE first_level_monthly ADD INDEX first_level_monthly_deviceManufacturerId(deviceManufacturerId);
ALTER TABLE first_level_monthly ADD INDEX first_level_monthly_deviceModelId(deviceModelId);
ALTER TABLE first_level_monthly ADD INDEX first_level_monthly_deviceOsId(deviceOsId);
ALTER TABLE first_level_monthly ADD INDEX first_level_monthly_countryId(countryId);
ALTER TABLE first_level_monthly ADD INDEX first_level_monthly_countryCarrierId(countryCarrierId);
ALTER TABLE first_level_monthly ADD INDEX first_level_monthly_adId(adId);
ALTER TABLE first_level_monthly ADD INDEX first_level_monthly_campaignId(campaignId);



CREATE TABLE IF NOT EXISTS `first_level_yearly` (
  `processing_time` datetime DEFAULT NULL,
  `impression_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `inventorySource` varchar(32) DEFAULT NULL, /* AdservingRequestResponse inventory_source  */
  `event_type` varchar(32) DEFAULT NULL, /* PostImpressionEvent event */
  `siteId` int(11) DEFAULT NULL,
  `deviceId` mediumtext,
  `deviceManufacturerId` int(11) DEFAULT NULL,
  `deviceModelId` int(11) DEFAULT NULL,
  `deviceOsId` int(11) DEFAULT NULL,
  `countryId` int(11) DEFAULT NULL,
  `countryCarrierId` int(11) DEFAULT NULL,
  `countryRegionId` int(11) DEFAULT NULL,
  `exchangeId` int(11) DEFAULT NULL,
  `creativeId` int(11) DEFAULT NULL,
  `adId` int(11) DEFAULT NULL,
  `campaignId` int(11) DEFAULT NULL,
  `advertiserId` varchar(128) DEFAULT NULL,
  `bidderModelId` int(11) DEFAULT NULL,
  `nofillReason` varchar(128) DEFAULT NULL,
  `total_request` int(11) DEFAULT '0',
  `total_impression` int(11) DEFAULT '0',
  `total_bidValue` double DEFAULT '0',
  `total_click` int(11) DEFAULT '0',
  `total_win` int(11) DEFAULT '0',
  `total_win_bidValue` double DEFAULT '0',
  `total_csc` int(11) DEFAULT '0',
  `total_event_type` int(11) DEFAULT '0',
  `demandCharges` double DEFAULT '0',
  `supplyCost` double DEFAULT '0',
  `earning` double DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
ALTER TABLE first_level_yearly ADD INDEX first_level_yearly_siteId(siteId);
ALTER TABLE first_level_yearly ADD INDEX first_level_yearly_deviceManufacturerId(deviceManufacturerId);
ALTER TABLE first_level_yearly ADD INDEX first_level_yearly_deviceModelId(deviceModelId);
ALTER TABLE first_level_yearly ADD INDEX first_level_yearly_deviceOsId(deviceOsId);
ALTER TABLE first_level_yearly ADD INDEX first_level_yearly_countryId(countryId);
ALTER TABLE first_level_yearly ADD INDEX first_level_yearly_countryCarrierId(countryCarrierId);
ALTER TABLE first_level_yearly ADD INDEX first_level_yearly_adId(adId);
ALTER TABLE first_level_yearly ADD INDEX first_level_yearly_campaignId(campaignId);

ALTER TABLE first_level CHANGE impression_time  impression_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE first_level_daily CHANGE impression_time  impression_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE first_level_monthly CHANGE impression_time  impression_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE first_level_yearly CHANGE impression_time  impression_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE first_level ADD COLUMN conversion INTEGER default 0 after earning;
ALTER TABLE first_level_daily ADD COLUMN conversion INTEGER default 0 after earning;
ALTER TABLE first_level_monthly ADD COLUMN conversion INTEGER default 0 after earning;
ALTER TABLE first_level_yearly ADD COLUMN conversion INTEGER default 0 after earning;

ALTER TABLE first_level ADD INDEX first_level_impression_time(impression_time);
ALTER TABLE first_level_daily ADD INDEX first_level_daily_impression_time(impression_time);
ALTER TABLE first_level_monthly ADD INDEX first_level_monthly_impression_time(impression_time);
ALTER TABLE first_level_yearly ADD INDEX first_level_yearly_impression_time(impression_time);

ALTER TABLE first_level ADD COLUMN bidprice_to_exchange double default 0.0 after conversion;
ALTER TABLE first_level_daily ADD COLUMN bidprice_to_exchange double default 0.0 after conversion;
ALTER TABLE first_level_monthly ADD COLUMN bidprice_to_exchange double default 0.0 after conversion;
ALTER TABLE first_level_yearly ADD COLUMN bidprice_to_exchange double default 0.0 after conversion;

ALTER TABLE first_level ADD COLUMN browserId INTEGER default 6 after bidprice_to_exchange;
ALTER TABLE first_level_daily ADD COLUMN browserId INTEGER default 6 after bidprice_to_exchange;
ALTER TABLE first_level_monthly ADD COLUMN browserId INTEGER default 6 after bidprice_to_exchange;
ALTER TABLE first_level_yearly ADD COLUMN browserId INTEGER default 6 after bidprice_to_exchange;

ALTER TABLE first_level ADD INDEX first_level_browserId(browserId);
ALTER TABLE first_level_daily ADD INDEX first_level_daily_browserId(browserId);
ALTER TABLE first_level_monthly ADD INDEX first_level_monthly_browserId(browserId);
ALTER TABLE first_level_yearly ADD INDEX first_level_yearly_browserId(browserId);

ALTER TABLE first_level ADD COLUMN cpa_goal double default 0.0 after browserId;
ALTER TABLE first_level_daily ADD COLUMN cpa_goal double default 0.0 after browserId;
ALTER TABLE first_level_monthly ADD COLUMN cpa_goal double default 0.0 after browserId;
ALTER TABLE first_level_yearly ADD COLUMN cpa_goal double default 0.0 after browserId;

ALTER TABLE first_level ADD COLUMN exchangepayout double default 0.0 after cpa_goal;
ALTER TABLE first_level_daily ADD COLUMN exchangepayout double default 0.0 after cpa_goal;
ALTER TABLE first_level_monthly ADD COLUMN exchangepayout double default 0.0 after cpa_goal;
ALTER TABLE first_level_yearly ADD COLUMN exchangepayout double default 0.0 after cpa_goal;

ALTER TABLE first_level ADD COLUMN exchangerevenue double default 0.0 after exchangepayout;
ALTER TABLE first_level_daily ADD COLUMN exchangerevenue double default 0.0 after exchangepayout;
ALTER TABLE first_level_monthly ADD COLUMN exchangerevenue double default 0.0 after exchangepayout;
ALTER TABLE first_level_yearly ADD COLUMN exchangerevenue double default 0.0 after exchangepayout;

ALTER TABLE first_level ADD COLUMN networkpayout double default 0.0 after exchangerevenue;
ALTER TABLE first_level_daily ADD COLUMN networkpayout double default 0.0 after exchangerevenue;
ALTER TABLE first_level_monthly ADD COLUMN networkpayout double default 0.0 after exchangerevenue;
ALTER TABLE first_level_yearly ADD COLUMN networkpayout double default 0.0 after exchangerevenue;

ALTER TABLE first_level ADD COLUMN networkrevenue double default 0.0 after networkpayout;
ALTER TABLE first_level_daily ADD COLUMN networkrevenue double default 0.0 after networkpayout;
ALTER TABLE first_level_monthly ADD COLUMN networkrevenue double default 0.0 after networkpayout;
ALTER TABLE first_level_yearly ADD COLUMN networkrevenue double default 0.0 after networkpayout;

CREATE TABLE IF NOT EXISTS `dashboard_total_metric` (
  `processing_time` datetime DEFAULT NULL,
  `impression_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ,
  `total_request` int(11) DEFAULT '0',
  `total_impression` int(11) DEFAULT '0',
  `total_csc` int(11) DEFAULT '0',
  `total_win` int(11) DEFAULT '0',
  `demandCharges` double DEFAULT '0',
  `supplyCost` double DEFAULT '0',
  `total_click` int(11) DEFAULT '0',
  `conversion` double DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS `dashboard_country` (
  `processing_time` datetime DEFAULT NULL,
  `impression_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ,
  `countryId` int(11) DEFAULT NULL, 
  `total_request` int(11) DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `dashboard_manufacturer` (
  `processing_time` datetime DEFAULT NULL,
  `impression_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP , 
  `deviceManufacturerId` int(11) DEFAULT NULL,
  `countryId` int(11) DEFAULT NULL,
  `total_request` int(11) DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `dashboard_os` (
  `processing_time` datetime DEFAULT NULL,
  `impression_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ,
  `deviceOsId` int(11) DEFAULT NULL,
  `total_request` int(11) DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `dashboard_browser` (
  `processing_time` datetime DEFAULT NULL,
  `impression_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ,
  `browserId` int(11) DEFAULT NULL,
  `total_request` int(11) DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `dashboard_site` (
  `processing_time` datetime DEFAULT NULL,
  `impression_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ,
  `siteId` int(11) DEFAULT NULL,
  `supplyCost` double DEFAULT '0',
  `exchangepayout` double DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `dashboard_campaign` (
  `processing_time` datetime DEFAULT NULL,
  `impression_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ,
  `campaignId` int(11) DEFAULT NULL,
  `demandCharges` double DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

alter table dashboard_manufacturer drop column countryId;
alter table dashboard_site add column total_impression int default 0 after exchangepayout;
alter table dashboard_site add column total_win int default 0 after total_impression;

ALTER TABLE first_level ADD COLUMN billedclicks double default 0.0 after networkrevenue;
ALTER TABLE first_level_daily ADD COLUMN billedclicks double default 0.0 after networkrevenue;
ALTER TABLE first_level_monthly ADD COLUMN billedclicks double default 0.0 after networkrevenue;
ALTER TABLE first_level_yearly ADD COLUMN billedclicks double default 0.0 after networkrevenue;

ALTER TABLE first_level ADD COLUMN billedcsc double default 0.0 after billedclicks;
ALTER TABLE first_level_daily ADD COLUMN billedcsc double default 0.0 after billedclicks;
ALTER TABLE first_level_monthly ADD COLUMN billedcsc double default 0.0 after billedclicks;
ALTER TABLE first_level_yearly ADD COLUMN billedcsc double default 0.0 after billedclicks;

ALTER TABLE first_level ADD COLUMN supply_source_type int default 2 after billedcsc;
ALTER TABLE first_level_daily ADD COLUMN supply_source_type int default 2 after billedcsc;
ALTER TABLE first_level_monthly ADD COLUMN supply_source_type int default 2 after billedcsc;
ALTER TABLE first_level_yearly ADD COLUMN supply_source_type int default 2 after billedcsc;


ALTER TABLE first_level ADD COLUMN ext_site int default -1 after supply_source_type;
ALTER TABLE first_level_daily ADD COLUMN ext_site int default -1 after supply_source_type;
ALTER TABLE first_level_monthly ADD COLUMN ext_site int default -1 after supply_source_type;
ALTER TABLE first_level_yearly ADD COLUMN ext_site int default -1 after supply_source_type;

ALTER TABLE first_level ADD INDEX first_level_ext_site(ext_site);
ALTER TABLE first_level_daily ADD INDEX first_level_daily_ext_site(ext_site);
ALTER TABLE first_level_monthly ADD INDEX first_level_monthly_ext_site(ext_site);
ALTER TABLE first_level_yearly ADD INDEX first_level_yearly_ext_site(ext_site);

ALTER TABLE first_level ADD COLUMN connection_type int default -1 after ext_site;
ALTER TABLE first_level_daily ADD COLUMN connection_type int default -1 after ext_site;
ALTER TABLE first_level_monthly ADD COLUMN connection_type int default -1 after ext_site;
ALTER TABLE first_level_yearly ADD COLUMN connection_type int default -1 after ext_site;

 CREATE TABLE IF NOT EXISTS `first_level_ext_site` (
  `processing_time` datetime DEFAULT NULL,
  `impression_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `exchangeId` int(11) DEFAULT NULL,
  `siteId` int(11) DEFAULT NULL,
  `ext_site` int(11) DEFAULT '-1',
  `adId` int(11) DEFAULT NULL,
  `campaignId` int(11) DEFAULT NULL,
  `total_request` int(11) DEFAULT '0',
  `total_impression` int(11) DEFAULT '0',
  `total_bidValue` double DEFAULT '0',
  `total_click` int(11) DEFAULT '0',
  `total_win` int(11) DEFAULT '0',
  `total_win_bidValue` double DEFAULT '0',
  `total_csc` int(11) DEFAULT '0',
  `demandCharges` double DEFAULT '0',
  `supplyCost` double DEFAULT '0',
  `earning` double DEFAULT '0',
  `conversion` int(11) DEFAULT '0',
  `bidprice_to_exchange` double DEFAULT '0',
  `cpa_goal` double DEFAULT '0',
  `exchangepayout` double DEFAULT '0',
  `exchangerevenue` double DEFAULT '0',
  `networkpayout` double DEFAULT '0',
  `networkrevenue` double DEFAULT '0',
  `billedclicks` double DEFAULT '0',
  `billedcsc` double DEFAULT '0',
  KEY `first_level_ext_site_exchangeId` (`exchangeId`),
  KEY `first_level_ext_site_adId` (`adId`),
  KEY `first_level_ext_site_campaignId` (`campaignId`),
  KEY `first_level_ext_site_impression_time` (`impression_time`),
  KEY `first_level_ext_site_ext_site` (`ext_site`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

 CREATE TABLE IF NOT EXISTS `first_level_ext_site_daily` (
  `processing_time` datetime DEFAULT NULL,
  `impression_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `exchangeId` int(11) DEFAULT NULL,
  `siteId` int(11) DEFAULT NULL,
  `ext_site` int(11) DEFAULT '-1',
  `adId` int(11) DEFAULT NULL,
  `campaignId` int(11) DEFAULT NULL,
  `total_request` int(11) DEFAULT '0',
  `total_impression` int(11) DEFAULT '0',
  `total_bidValue` double DEFAULT '0',
  `total_click` int(11) DEFAULT '0',
  `total_win` int(11) DEFAULT '0',
  `total_win_bidValue` double DEFAULT '0',
  `total_csc` int(11) DEFAULT '0',
  `demandCharges` double DEFAULT '0',
  `supplyCost` double DEFAULT '0',
  `earning` double DEFAULT '0',
  `conversion` int(11) DEFAULT '0',
  `bidprice_to_exchange` double DEFAULT '0',
  `cpa_goal` double DEFAULT '0',
  `exchangepayout` double DEFAULT '0',
  `exchangerevenue` double DEFAULT '0',
  `networkpayout` double DEFAULT '0',
  `networkrevenue` double DEFAULT '0',
  `billedclicks` double DEFAULT '0',
  `billedcsc` double DEFAULT '0',
  KEY `first_level_ext_site_daily_exchangeId` (`exchangeId`),
  KEY `first_level_ext_site_daily_adId` (`adId`),
  KEY `first_level_ext_site_daily_campaignId` (`campaignId`),
  KEY `first_level_ext_site_daily_impression_time` (`impression_time`),
  KEY `first_level_ext_site_daily_ext_site` (`ext_site`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS `first_level_ext_site_monthly` (
  `processing_time` datetime DEFAULT NULL,
  `impression_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `exchangeId` int(11) DEFAULT NULL,
  `siteId` int(11) DEFAULT NULL,
  `ext_site` int(11) DEFAULT '-1',
  `adId` int(11) DEFAULT NULL,
  `campaignId` int(11) DEFAULT NULL,
  `total_request` int(11) DEFAULT '0',
  `total_impression` int(11) DEFAULT '0',
  `total_bidValue` double DEFAULT '0',
  `total_click` int(11) DEFAULT '0',
  `total_win` int(11) DEFAULT '0',
  `total_win_bidValue` double DEFAULT '0',
  `total_csc` int(11) DEFAULT '0',
  `demandCharges` double DEFAULT '0',
  `supplyCost` double DEFAULT '0',
  `earning` double DEFAULT '0',
  `conversion` int(11) DEFAULT '0',
  `bidprice_to_exchange` double DEFAULT '0',
  `cpa_goal` double DEFAULT '0',
  `exchangepayout` double DEFAULT '0',
  `exchangerevenue` double DEFAULT '0',
  `networkpayout` double DEFAULT '0',
  `networkrevenue` double DEFAULT '0',
  `billedclicks` double DEFAULT '0',
  `billedcsc` double DEFAULT '0',
  KEY `first_level_ext_site_monthly_exchangeId` (`exchangeId`),
  KEY `first_level_ext_site_monthly_adId` (`adId`),
  KEY `first_level_ext_site_monthly_campaignId` (`campaignId`),
  KEY `first_level_ext_site_monthly_impression_time` (`impression_time`),
  KEY `first_level_ext_site_monthly_ext_site` (`ext_site`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS `first_level_ext_site_yearly` (
  `processing_time` datetime DEFAULT NULL,
  `impression_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `exchangeId` int(11) DEFAULT NULL,
  `siteId` int(11) DEFAULT NULL,
  `ext_site` int(11) DEFAULT '-1',
  `adId` int(11) DEFAULT NULL,
  `campaignId` int(11) DEFAULT NULL,
  `total_request` int(11) DEFAULT '0',
  `total_impression` int(11) DEFAULT '0',
  `total_bidValue` double DEFAULT '0',
  `total_click` int(11) DEFAULT '0',
  `total_win` int(11) DEFAULT '0',
  `total_win_bidValue` double DEFAULT '0',
  `total_csc` int(11) DEFAULT '0',
  `demandCharges` double DEFAULT '0',
  `supplyCost` double DEFAULT '0',
  `earning` double DEFAULT '0',
  `conversion` int(11) DEFAULT '0',
  `bidprice_to_exchange` double DEFAULT '0',
  `cpa_goal` double DEFAULT '0',
  `exchangepayout` double DEFAULT '0',
  `exchangerevenue` double DEFAULT '0',
  `networkpayout` double DEFAULT '0',
  `networkrevenue` double DEFAULT '0',
  `billedclicks` double DEFAULT '0',
  `billedcsc` double DEFAULT '0',
  KEY `first_level_ext_site_yearly_exchangeId` (`exchangeId`),
  KEY `first_level_ext_site_yearly_adId` (`adId`),
  KEY `first_level_ext_site_yearly_campaignId` (`campaignId`),
  KEY `first_level_ext_site_yearly_impression_time` (`impression_time`),
  KEY `first_level_ext_site_yearly_ext_site` (`ext_site`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `dashboard_site_v1` (
  `processing_time` datetime DEFAULT NULL,
  `impression_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ,
  `siteId` int(11) DEFAULT NULL,
  `total_request` int(11) DEFAULT '0',
  `total_impression` int(11) DEFAULT '0',
  `total_click` int(11) DEFAULT '0',
  `total_csc` int(11) DEFAULT '0',
  `total_win` int(11) DEFAULT '0',
  `supplyCost` double DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `dashboard_campaign_v1` (
  `processing_time` datetime DEFAULT NULL,
  `impression_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ,
  `campaignId` int(11) DEFAULT NULL,
  `total_impression` int(11) DEFAULT '0',
  `total_win` int(11) DEFAULT '0',
  `total_click` int(11) DEFAULT '0',
  `conversion` int(11) DEFAULT '0',
  `demandCharges` double DEFAULT '0',
  `total_csc` int(11) DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS `fraud_daily` (
  `processing_time` datetime DEFAULT NULL,
  `impression_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `siteId` int(11) DEFAULT NULL,
  `adId` int(11) DEFAULT '0',
  `event` varchar(64),
  `terminationReason` varchar(64),
  `count` int(11) DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE fraud_daily ADD INDEX fraud_daily_siteId(siteId);
ALTER TABLE fraud_daily ADD INDEX fraud_daily_adId(adId);

ALTER TABLE first_level_ext_site add column countryId int(11) DEFAULT NULL after billedcsc;
ALTER TABLE first_level_ext_site_daily add column countryId int(11) DEFAULT NULL after billedcsc;
ALTER TABLE first_level_ext_site_monthly add column countryId int(11) DEFAULT NULL after billedcsc;
ALTER TABLE first_level_ext_site change column countryId countryId int(11) DEFAULT -1 ;
ALTER TABLE first_level_ext_site_daily change column countryId countryId int(11) DEFAULT -1 ;
ALTER TABLE first_level_ext_site_monthly change column countryId countryId int(11) DEFAULT -1 ;


 CREATE TABLE IF NOT EXISTS `first_level_limited` (
  `processing_time` datetime DEFAULT NULL,
  `impression_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `pubId` int(11) DEFAULT NULL,
  `siteId` int(11) DEFAULT NULL,
  `deviceManufacturerId` int(11) DEFAULT NULL,
  `deviceOsId` int(11) DEFAULT NULL,
  `countryId` int(11) DEFAULT NULL,
  `countryCarrierId` int(11) DEFAULT NULL,
  `adId` int(11) DEFAULT NULL,
  `campaignId` int(11) DEFAULT NULL,
  `advId` int(11) DEFAULT NULL,
  `total_request` int(11) DEFAULT '0',
  `total_impression` int(11) DEFAULT '0',
  `total_bidValue` double DEFAULT '0',
  `total_click` int(11) DEFAULT '0',
  `total_win` int(11) DEFAULT '0',
  `total_win_bidValue` double DEFAULT '0',
  `total_csc` int(11) DEFAULT '0',
  `demandCharges` double DEFAULT '0',
  `supplyCost` double DEFAULT '0',
  `earning` double DEFAULT '0',
  `conversion` int(11) DEFAULT '0',
  `bidprice_to_exchange` double DEFAULT '0',
  `cpa_goal` double DEFAULT '0',
  `exchangepayout` double DEFAULT '0',
  `exchangerevenue` double DEFAULT '0',
  `networkpayout` double DEFAULT '0',
  `networkrevenue` double DEFAULT '0',
  `billedclicks` double DEFAULT '0',
  `billedcsc` double DEFAULT '0',
  KEY `first_level_limited_impression_time` (`impression_time`),
  KEY `first_level_limited_pubId` (`pubId`),
  KEY `first_level_limited_siteId` (`siteId`),
  KEY `first_level_limited_deviceManufacturerId` (`deviceManufacturerId`),
  KEY `first_level_limited_deviceOsId` (`deviceOsId`),
  KEY `first_level_limited_countryId` (`countryId`),
  KEY `first_level_limited_countryCarrierId` (`countryCarrierId`),
  KEY `first_level_limited_adId` (`adId`),
  KEY `first_level_limited_campaignId` (`campaignId`),
  KEY `first_level_limited_advId` (`advId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


 CREATE TABLE IF NOT EXISTS `first_level_limited_monthly` (
  `processing_time` datetime DEFAULT NULL,
  `impression_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `pubId` int(11) DEFAULT NULL,
  `siteId` int(11) DEFAULT NULL,
  `deviceManufacturerId` int(11) DEFAULT NULL,
  `deviceOsId` int(11) DEFAULT NULL,
  `countryId` int(11) DEFAULT NULL,
  `countryCarrierId` int(11) DEFAULT NULL,
  `adId` int(11) DEFAULT NULL,
  `campaignId` int(11) DEFAULT NULL,
  `advId` int(11) DEFAULT NULL,
  `total_request` int(11) DEFAULT '0',
  `total_impression` int(11) DEFAULT '0',
  `total_bidValue` double DEFAULT '0',
  `total_click` int(11) DEFAULT '0',
  `total_win` int(11) DEFAULT '0',
  `total_win_bidValue` double DEFAULT '0',
  `total_csc` int(11) DEFAULT '0',
  `demandCharges` double DEFAULT '0',
  `supplyCost` double DEFAULT '0',
  `earning` double DEFAULT '0',
  `conversion` int(11) DEFAULT '0',
  `bidprice_to_exchange` double DEFAULT '0',
  `cpa_goal` double DEFAULT '0',
  `exchangepayout` double DEFAULT '0',
  `exchangerevenue` double DEFAULT '0',
  `networkpayout` double DEFAULT '0',
  `networkrevenue` double DEFAULT '0',
  `billedclicks` double DEFAULT '0',
  `billedcsc` double DEFAULT '0',
  KEY `first_level_limited_monthly_impression_time` (`impression_time`),
  KEY `first_level_limited_monthly_pubId` (`pubId`),
  KEY `first_level_limited_monthly_siteId` (`siteId`),
  KEY `first_level_limited_monthly_deviceManufacturerId` (`deviceManufacturerId`),
  KEY `first_level_limited_monthly_deviceOsId` (`deviceOsId`),
  KEY `first_level_limited_monthly_countryId` (`countryId`),
  KEY `first_level_limited_monthly_countryCarrierId` (`countryCarrierId`),
  KEY `first_level_limited_monthly_adId` (`adId`),
  KEY `first_level_limited_monthly_campaignId` (`campaignId`),
  KEY `first_level_limited_monthly_advId` (`advId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


 CREATE TABLE IF NOT EXISTS `first_level_limited_daily` (
  `processing_time` datetime DEFAULT NULL,
  `impression_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `pubId` int(11) DEFAULT NULL,
  `siteId` int(11) DEFAULT NULL,
  `deviceManufacturerId` int(11) DEFAULT NULL,
  `deviceOsId` int(11) DEFAULT NULL,
  `countryId` int(11) DEFAULT NULL,
  `countryCarrierId` int(11) DEFAULT NULL,
  `adId` int(11) DEFAULT NULL,
  `campaignId` int(11) DEFAULT NULL,
  `advId` int(11) DEFAULT NULL,
  `total_request` int(11) DEFAULT '0',
  `total_impression` int(11) DEFAULT '0',
  `total_bidValue` double DEFAULT '0',
  `total_click` int(11) DEFAULT '0',
  `total_win` int(11) DEFAULT '0',
  `total_win_bidValue` double DEFAULT '0',
  `total_csc` int(11) DEFAULT '0',
  `demandCharges` double DEFAULT '0',
  `supplyCost` double DEFAULT '0',
  `earning` double DEFAULT '0',
  `conversion` int(11) DEFAULT '0',
  `bidprice_to_exchange` double DEFAULT '0',
  `cpa_goal` double DEFAULT '0',
  `exchangepayout` double DEFAULT '0',
  `exchangerevenue` double DEFAULT '0',
  `networkpayout` double DEFAULT '0',
  `networkrevenue` double DEFAULT '0',
  `billedclicks` double DEFAULT '0',
  `billedcsc` double DEFAULT '0',
  KEY `first_level_limited_daily_impression_time` (`impression_time`),
  KEY `first_level_limited_daily_pubId` (`pubId`),
  KEY `first_level_limited_daily_siteId` (`siteId`),
  KEY `first_level_limited_daily_deviceManufacturerId` (`deviceManufacturerId`),
  KEY `first_level_limited_daily_deviceOsId` (`deviceOsId`),
  KEY `first_level_limited_daily_countryId` (`countryId`),
  KEY `first_level_limited_daily_countryCarrierId` (`countryCarrierId`),
  KEY `first_level_limited_daily_adId` (`adId`),
  KEY `first_level_limited_daily_campaignId` (`campaignId`),
  KEY `first_level_limited_daily_advId` (`advId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



 CREATE TABLE IF NOT EXISTS `first_level_limited_yearly` (
  `processing_time` datetime DEFAULT NULL,
  `impression_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `pubId` int(11) DEFAULT NULL,
  `siteId` int(11) DEFAULT NULL,
  `deviceManufacturerId` int(11) DEFAULT NULL,
  `deviceOsId` int(11) DEFAULT NULL,
  `countryId` int(11) DEFAULT NULL,
  `countryCarrierId` int(11) DEFAULT NULL,
  `adId` int(11) DEFAULT NULL,
  `campaignId` int(11) DEFAULT NULL,
  `advId` int(11) DEFAULT NULL,
  `total_request` int(11) DEFAULT '0',
  `total_impression` int(11) DEFAULT '0',
  `total_bidValue` double DEFAULT '0',
  `total_click` int(11) DEFAULT '0',
  `total_win` int(11) DEFAULT '0',
  `total_win_bidValue` double DEFAULT '0',
  `total_csc` int(11) DEFAULT '0',
  `demandCharges` double DEFAULT '0',
  `supplyCost` double DEFAULT '0',
  `earning` double DEFAULT '0',
  `conversion` int(11) DEFAULT '0',
  `bidprice_to_exchange` double DEFAULT '0',
  `cpa_goal` double DEFAULT '0',
  `exchangepayout` double DEFAULT '0',
  `exchangerevenue` double DEFAULT '0',
  `networkpayout` double DEFAULT '0',
  `networkrevenue` double DEFAULT '0',
  `billedclicks` double DEFAULT '0',
  `billedcsc` double DEFAULT '0',
  KEY `first_level_limited_yearly_impression_time` (`impression_time`),
  KEY `first_level_limited_yearly_pubId` (`pubId`),
  KEY `first_level_limited_yearly_siteId` (`siteId`),
  KEY `first_level_limited_yearly_deviceManufacturerId` (`deviceManufacturerId`),
  KEY `first_level_limited_yearly_deviceOsId` (`deviceOsId`),
  KEY `first_level_limited_yearly_countryId` (`countryId`),
  KEY `first_level_limited_yearly_countryCarrierId` (`countryCarrierId`),
  KEY `first_level_limited_yearly_adId` (`adId`),
  KEY `first_level_limited_yearly_campaignId` (`campaignId`),
  KEY `first_level_limited_yearly_advId` (`advId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

 CREATE TABLE IF NOT EXISTS `tracking_hourly` (
  `processing_time` datetime DEFAULT NULL,
  `impression_time` timestamp NOT NULL,
  `pubId` int(11) DEFAULT NULL,
  `siteId` int(11) DEFAULT NULL,
  `extsiteId` int(11) DEFAULT NULL,
  `advId` int(11) DEFAULT NULL,
  `campaignId` int(11) DEFAULT NULL,
  `adId` int(11) DEFAULT NULL,
  `terminationReason` varchar(32) DEFAULT NULL,
  `tevent` varchar(32) DEFAULT NULL,
  `teventtype` varchar(32) DEFAULT NULL,
  `countryId` int(11) DEFAULT NULL,
  `countryCarrierId` int(11) DEFAULT NULL,
  `deviceManufacturerId` int(11) DEFAULT NULL,
  `deviceOsId` int(11) DEFAULT NULL,
  `total_event` int(11) DEFAULT '0',
  KEY `tracking_hourly_impression_time` (`impression_time`),
  KEY `tracking_hourly_pubId` (`pubId`),
  KEY `tracking_hourly_siteId` (`siteId`),
  KEY `tracking_hourly_extsiteId` (`extsiteId`),
  KEY `tracking_hourly_advId` (`advId`),
  KEY `tracking_hourly_campaignId` (`campaignId`),
  KEY `tracking_hourly_adId` (`adId`),
  KEY `tracking_hourly_terminationReason` (`terminationReason`),
  KEY `tracking_hourly_tevent` (`tevent`),
  KEY `tracking_hourly_teventtype` (`teventtype`),
  KEY `tracking_hourly_countryId` (`countryId`),
  KEY `tracking_hourly_countryCarrierId` (`countryCarrierId`),
  KEY `tracking_hourly_deviceManufacturerId` (`deviceManufacturerId`),
  KEY `tracking_hourly_deviceOsId` (`deviceOsId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

 CREATE TABLE IF NOT EXISTS `tracking_daily` (
  `processing_time` datetime DEFAULT NULL,
  `impression_time` timestamp NOT NULL,
  `pubId` int(11) DEFAULT NULL,
  `siteId` int(11) DEFAULT NULL,
  `extsiteId` int(11) DEFAULT NULL,
  `advId` int(11) DEFAULT NULL,
  `campaignId` int(11) DEFAULT NULL,
  `adId` int(11) DEFAULT NULL,
  `terminationReason` varchar(32) DEFAULT NULL,
  `tevent` varchar(32) DEFAULT NULL,
  `teventtype` varchar(32) DEFAULT NULL,
  `countryId` int(11) DEFAULT NULL,
  `countryCarrierId` int(11) DEFAULT NULL,
  `deviceManufacturerId` int(11) DEFAULT NULL,
  `deviceOsId` int(11) DEFAULT NULL,
  `total_event` int(11) DEFAULT '0',
  KEY `tracking_daily_impression_time` (`impression_time`),
  KEY `tracking_daily_pubId` (`pubId`),
  KEY `tracking_daily_siteId` (`siteId`),
  KEY `tracking_daily_extsiteId` (`extsiteId`),
  KEY `tracking_daily_advId` (`advId`),
  KEY `tracking_daily_campaignId` (`campaignId`),
  KEY `tracking_daily_adId` (`adId`),
  KEY `tracking_daily_terminationReason` (`terminationReason`),
  KEY `tracking_daily_tevent` (`tevent`),
  KEY `tracking_daily_teventtype` (`teventtype`),
  KEY `tracking_daily_countryId` (`countryId`),
  KEY `tracking_daily_countryCarrierId` (`countryCarrierId`),
  KEY `tracking_daily_deviceManufacturerId` (`deviceManufacturerId`),
  KEY `tracking_daily_deviceOsId` (`deviceOsId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

 CREATE TABLE IF NOT EXISTS `tracking_monthly` (
  `processing_time` datetime DEFAULT NULL,
  `impression_time` timestamp NOT NULL,
  `pubId` int(11) DEFAULT NULL,
  `siteId` int(11) DEFAULT NULL,
  `extsiteId` int(11) DEFAULT NULL,
  `advId` int(11) DEFAULT NULL,
  `campaignId` int(11) DEFAULT NULL,
  `adId` int(11) DEFAULT NULL,
  `terminationReason` varchar(32) DEFAULT NULL,
  `tevent` varchar(32) DEFAULT NULL,
  `teventtype` varchar(32) DEFAULT NULL,
  `countryId` int(11) DEFAULT NULL,
  `countryCarrierId` int(11) DEFAULT NULL,
  `deviceManufacturerId` int(11) DEFAULT NULL,
  `deviceOsId` int(11) DEFAULT NULL,
  `total_event` int(11) DEFAULT '0',
  KEY `tracking_monthly_impression_time` (`impression_time`),
  KEY `tracking_monthly_pubId` (`pubId`),
  KEY `tracking_monthly_siteId` (`siteId`),
  KEY `tracking_monthly_extsiteId` (`extsiteId`),
  KEY `tracking_monthly_advId` (`advId`),
  KEY `tracking_monthly_campaignId` (`campaignId`),
  KEY `tracking_monthly_adId` (`adId`),
  KEY `tracking_monthly_terminationReason` (`terminationReason`),
  KEY `tracking_monthly_tevent` (`tevent`),
  KEY `tracking_monthly_teventtype` (`teventtype`),
  KEY `tracking_monthly_countryId` (`countryId`),
  KEY `tracking_monthly_countryCarrierId` (`countryCarrierId`),
  KEY `tracking_monthly_deviceManufacturerId` (`deviceManufacturerId`),
  KEY `tracking_monthly_deviceOsId` (`deviceOsId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

 CREATE TABLE IF NOT EXISTS `uu_daily` (
  `processing_time` datetime DEFAULT NULL,
  `advId` int(11) DEFAULT NULL,
  `campaignId` int(11) DEFAULT NULL,
  `adId` int(11) DEFAULT NULL,
  `uu_count` int(11) DEFAULT '0',
  KEY `uu_daily_processing_time` (`processing_time`),
  KEY `uu_daily_advId` (`advId`),
  KEY `uu_daily_campaignId` (`campaignId`),
  KEY `uu_daily_adId` (`adId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `ad_no_fill_reason`
(
    ad_id INTEGER UNSIGNED NOT NULL, -- refers to id in ad table
    no_fill_reason_name VARCHAR(100) NOT NULL, -- refers to id in no_fill_reason table
    request_hour TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00',
    count INTEGER UNSIGNED NOT NULL DEFAULT 0,
    created_on TIMESTAMP NOT NULL DEFAULT now(),
    last_modified TIMESTAMP NOT NULL,
    CONSTRAINT `fk_ad_no_fill_reason_ad_id` FOREIGN KEY (`ad_id`) REFERENCES `ad` (`id`),   
    CONSTRAINT `uq_ad_id_no_fill_reason_hour` UNIQUE (ad_id, no_fill_reason_name, request_hour)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS `ad_no_fill_reason_daily`
(
    ad_id INTEGER UNSIGNED NOT NULL, -- refers to id in ad table
    no_fill_reason_name VARCHAR(100) NOT NULL, -- refers to id in no_fill_reason table
    request_day TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00',
    count INTEGER UNSIGNED NOT NULL DEFAULT 0,
    created_on TIMESTAMP NOT NULL DEFAULT now(),
    last_modified TIMESTAMP NOT NULL,
    CONSTRAINT `fk_ad_no_fill_reason_daily_ad_id` FOREIGN KEY (`ad_id`) REFERENCES `ad` (`id`),   
    CONSTRAINT `uq_ad_id_no_fill_reason_day` UNIQUE (ad_id, no_fill_reason_name, request_day)
) ENGINE=InnoDB;

alter table ad_no_fill_reason change column request_hour request_time timestamp NOT NULL DEFAULT '0000-00-00 00:00:00';
alter table ad_no_fill_reason_daily change column request_day request_time timestamp NOT NULL DEFAULT '0000-00-00 00:00:00';

alter table ad_no_fill_reason add column processing_time timestamp NOT NULL DEFAULT now() after request_time;
alter table ad_no_fill_reason_daily add column processing_time timestamp NOT NULL DEFAULT now() after request_time;

alter table ad_no_fill_reason drop foreign key `fk_ad_no_fill_reason_ad_id`;
alter table ad_no_fill_reason_daily drop foreign key `fk_ad_no_fill_reason_daily_ad_id`;

alter table ad_no_fill_reason drop index `uq_ad_id_no_fill_reason_hour`;
alter table ad_no_fill_reason_daily drop index `uq_ad_id_no_fill_reason_day`;

alter table ad_no_fill_reason add CONSTRAINT `fk_ad_no_fill_reason_ad_id` FOREIGN KEY (`ad_id`) REFERENCES `ad` (`id`);
alter table ad_no_fill_reason add constraint `uq_ad_id_no_fill_reason_hour` UNIQUE (ad_id, no_fill_reason_name, request_time, processing_time);

alter table ad_no_fill_reason_daily add CONSTRAINT `fk_ad_no_fill_reason_daily_ad_id` FOREIGN KEY (`ad_id`) REFERENCES `ad` (`id`);
alter table ad_no_fill_reason_daily add constraint `uq_ad_id_no_fill_reason_day` UNIQUE (ad_id, no_fill_reason_name, request_time, processing_time);

drop table ad_no_fill_reason;
CREATE TABLE `ad_no_fill_reason` (
  `ad_id` int(10) unsigned NOT NULL,
  `no_fill_reason_name` varchar(100) NOT NULL,
  `request_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `count` int(10) unsigned NOT NULL DEFAULT '0',
  `processing_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  KEY `fk_ad_no_fill_reason_ad_id` (`ad_id`),
  CONSTRAINT `fk_ad_no_fill_reason_ad_id` FOREIGN KEY (`ad_id`) REFERENCES `ad` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
ALTER TABLE ad_no_fill_reason ADD INDEX ad_no_fill_reason_ad_id(ad_id);
ALTER TABLE ad_no_fill_reason ADD INDEX ad_no_fill_reason_request_time(request_time);


drop table ad_no_fill_reason_daily;
CREATE TABLE `ad_no_fill_reason_daily` (
  `ad_id` int(10) unsigned NOT NULL,
  `no_fill_reason_name` varchar(100) NOT NULL,
  `request_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `count` int(10) unsigned NOT NULL DEFAULT '0',
  `processing_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  KEY `fk_ad_no_fill_reason_ad_id` (`ad_id`),
  CONSTRAINT `fk_ad_no_fill_reason_ad_daily_id` FOREIGN KEY (`ad_id`) REFERENCES `ad` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE ad_no_fill_reason_daily ADD INDEX ad_no_fill_reason_daily_ad_id(ad_id);
ALTER TABLE ad_no_fill_reason_daily ADD INDEX ad_no_fill_reason_daily_request_time(request_time);


CREATE TABLE `fast_path` (
  `processing_time` datetime DEFAULT NULL,
  `impression_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `pubId` int(11) DEFAULT NULL,
  `siteId` int(11) DEFAULT NULL,
  `ext_site` int(11) DEFAULT NULL,
  `advId` int(11) DEFAULT NULL,
  `campaignId` int(11) DEFAULT NULL,
  `adId` int(11) DEFAULT NULL,
  `total_request` BIGINT DEFAULT '0',
  `total_impression` BIGINT DEFAULT '0',
  `total_bidValue` double DEFAULT '0',
  `total_click` BIGINT DEFAULT '0',
  `total_win` BIGINT DEFAULT '0',
  `total_win_bidValue` double DEFAULT '0',
  `total_csc` BIGINT DEFAULT '0',
  `demandCharges` double DEFAULT '0',
  `supplyCost` double DEFAULT '0',
  `earning` double DEFAULT '0',
  `conversion` BIGINT DEFAULT '0',
  `bidprice_to_exchange` double DEFAULT '0',
  `cpa_goal` double DEFAULT '0',
  `exchangepayout` double DEFAULT '0',
  `exchangerevenue` double DEFAULT '0',
  `networkpayout` double DEFAULT '0',
  `networkrevenue` double DEFAULT '0',
  `billedclicks` double DEFAULT '0',
  `billedcsc` double DEFAULT '0',
  KEY `_impression_time` (`impression_time`),
  KEY `fast_path_pubId` (`pubId`),
  KEY `fast_path_siteId` (`siteId`),
  KEY `fast_path_ext_site` (`ext_site`),
  KEY `fast_path_adId` (`adId`),
  KEY `fast_path_campaignId` (`campaignId`),
  KEY `fast_path_advId` (`advId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

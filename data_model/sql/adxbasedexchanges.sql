
CREATE TABLE `adxbasedexchanges_metadata` (
  `internalid` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `pubIncId` int(11) NOT NULL,
  `advertiser_upload` boolean default false,
  `adposition_get` boolean default false,
  `banner_upload` boolean default false,
  `video_upload` boolean default false,
  `last_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`internalid`)
) ENGINE=InnoDB;

ALTER TABLE `adxbasedexchanges_metadata` ADD CONSTRAINT `uq_adxbasedexchanges_metadata_pubIncId` UNIQUE(`pubIncId`);


CREATE TABLE `adposition_get` (
  `internalid` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `pubIncId` int(11) NOT NULL,
  `adxbasedexhangesstatus` int(11) default 1,
  `message` text,
  `last_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`internalid`)
) ENGINE=InnoDB;


CREATE TABLE `banner_upload` (
  `internalid` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `pubIncId` int(11) NOT NULL,
  `adxbasedexhangesstatus` int(11) default 1,
  `advIncId` int(11) NOT NULL,
  `campaignId` int(11) NOT NULL,
  `adId` int(11) NOT NULL,
  `bannerId` int(11) NOT NULL,
  `message` text,
  `info` text,
  `last_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`internalid`)
) ENGINE=InnoDB;

alter table banner_upload add column campaignStatus int(11) not null after campaignId; 
alter table banner_upload add column adStatus int(11) not null after adId; 
alter table banner_upload add column creativeId int(11) not null after adStatus; 
alter table banner_upload add column creativeStatus int(11) not null after creativeId; 

CREATE TABLE `video_upload` (
  `internalid` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `pubIncId` int(11) NOT NULL,
  `adxbasedexhangesstatus` int(11) DEFAULT '1',
  `advIncId` int(11) NOT NULL,
  `campaignId` int(11) NOT NULL,
  `campaignStatus` int(11) NOT NULL,
  `adId` int(11) NOT NULL,
  `adStatus` int(11) NOT NULL,
  `creativeId` int(11) NOT NULL,
  `creativeStatus` int(11) NOT NULL,
  `videoInfoId` int(11) NOT NULL,
  `message` text,
  `info` text,
  `last_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`internalid`)
) ENGINE=InnoDB;


CREATE TABLE `material_upload_state` (
  `internalid` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `pubIncId` int(11) NOT NULL,
  `materialtype` int(11) NOT NULL,
  `last_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`internalid`)
) ENGINE=InnoDB;

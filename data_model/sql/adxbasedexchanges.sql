
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





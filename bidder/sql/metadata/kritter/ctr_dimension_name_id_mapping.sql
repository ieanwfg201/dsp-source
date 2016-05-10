CREATE TABLE `ctr_dimension_name_id_mapping` (
      `id` int(11) NOT NULL,
      `name` varchar(256) NOT NULL,
      `created_at` timestamp NULL DEFAULT NULL,
      `modified_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
      PRIMARY KEY (`id`),
      UNIQUE KEY `name` (`name`)
) DEFAULT CHARSET=latin1;

insert into ctr_dimension_name_id_mapping(id, name, created_at, modified_at) values (0, 'time', NOW(), NOW());
insert into ctr_dimension_name_id_mapping(id, name, created_at, modified_at) values (1, 'campaignid', NOW(), NOW());
insert into ctr_dimension_name_id_mapping(id, name, created_at, modified_at) values (2, 'adid', NOW(), NOW());
insert into ctr_dimension_name_id_mapping(id, name, created_at, modified_at) values (3, 'supply_source_type', NOW(), NOW());
insert into ctr_dimension_name_id_mapping(id, name, created_at, modified_at) values (4, 'selectedSiteCategoryId', NOW(), NOW());
insert into ctr_dimension_name_id_mapping(id, name, created_at, modified_at) values (5, 'country', NOW(), NOW());
insert into ctr_dimension_name_id_mapping(id, name, created_at, modified_at) values (6, 'carrier', NOW(), NOW());
insert into ctr_dimension_name_id_mapping(id, name, created_at, modified_at) values (7, 'countryRegionId', NOW(), NOW());
insert into ctr_dimension_name_id_mapping(id, name, created_at, modified_at) values (8, 'deviceManufacturerId', NOW(), NOW());
insert into ctr_dimension_name_id_mapping(id, name, created_at, modified_at) values (9, 'osid', NOW(), NOW());
insert into ctr_dimension_name_id_mapping(id, name, created_at, modified_at) values (10, 'exchangeid', NOW(), NOW());

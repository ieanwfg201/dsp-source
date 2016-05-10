CREATE TABLE `dimension_name_id_mapping` (
      `id` int(11) NOT NULL,
      `name` varchar(256) NOT NULL,
      `created_at` timestamp NULL DEFAULT NULL,
      `modified_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
      PRIMARY KEY (`id`),
      UNIQUE KEY `name` (`name`)
) DEFAULT CHARSET=latin1;

insert into dimension_name_id_mapping(id, name, created_at, modified_at) values (0, 'time', NOW(), NOW());
insert into dimension_name_id_mapping(id, name, created_at, modified_at) values (1, 'sitecategory', NOW(), NOW());
insert into dimension_name_id_mapping(id, name, created_at, modified_at) values (2, 'country', NOW(), NOW());
insert into dimension_name_id_mapping(id, name, created_at, modified_at) values (3, 'carrier', NOW(), NOW());
insert into dimension_name_id_mapping(id, name, created_at, modified_at) values (4, 'region', NOW(), NOW());
insert into dimension_name_id_mapping(id, name, created_at, modified_at) values (5, 'manufacturer', NOW(), NOW());
insert into dimension_name_id_mapping(id, name, created_at, modified_at) values (6, 'os', NOW(), NOW());
insert into dimension_name_id_mapping(id, name, created_at, modified_at) values (7, 'exchange', NOW(), NOW());

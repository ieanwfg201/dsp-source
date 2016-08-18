delete from connection_type_metadata;
insert into connection_type_metadata (id, name, created_on, last_modified) values
(0, "UNKNOWN", NOW(), NOW()),
(1, "Ethernet", NOW(), NOW()),
(2, "WIFI", NOW(), NOW()),
(3, "MobiledataUnknown", NOW(), NOW()),
(4, "MobiledataTwoG", NOW(), NOW()),
(5, "MobiledataThreeG", NOW(), NOW())
ON DUPLICATE KEY UPDATE id = VALUES(id);


INSERT INTO mma_exchangename_id_mapping (exchangename, exchangeid, exchangeguid) VALUES
('youku', '1', 'youku'),
('tencent', '2', 'tencent');

-- This table stores file id and name for custom ip ranges provided in CSV format
-- The full path for the online system is fetched as base_path(conf) plus the file_id.
CREATE TABLE custom_ip_range
(
id INTEGER AUTO_INCREMENT PRIMARY KEY NOT NULL,
file_id VARCHAR(100) UNIQUE NOT NULL,
file_name VARCHAR(100) UNIQUE NOT NULL,
file_description VARCHAR(200) NOT NULL,
account_id VARCHAR(100) NOT NULL,
modified_on TIMESTAMP NOT NULL,
CONSTRAINT fk_cstm_ip_range_account FOREIGN KEY(account_id) REFERENCES account(guid)
)ENGINE=INNODB;

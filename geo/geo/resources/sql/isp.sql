CREATE TABLE isp
(
id INTEGER AUTO_INCREMENT PRIMARY KEY NOT NULL,
country_id INTEGER NOT NULL,
isp_name VARCHAR(200) NOT NULL,
data_source_name VARCHAR(50) NOT NULL,
modified_on TIMESTAMP NOT NULL,
CONSTRAINT fk_country_isp FOREIGN KEY (country_id) REFERENCES country(id),
CONSTRAINT unique_isp_datasource UNIQUE(country_id,isp_name,data_source_name)
)ENGINE=INNODB;

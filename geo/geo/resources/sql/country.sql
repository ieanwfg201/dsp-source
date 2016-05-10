CREATE TABLE country
(
id INTEGER AUTO_INCREMENT PRIMARY KEY NOT NULL,
country_code VARCHAR(2) NOT NULL,
country_name VARCHAR(200) NOT NULL,
data_source_name VARCHAR(50) NOT NULL,
modified_on TIMESTAMP NOT NULL,
CONSTRAINT unique_country_datasource UNIQUE (country_code,country_name,data_source_name)
)ENGINE=INNODB;

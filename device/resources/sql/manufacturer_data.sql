-- This table contains mobile manufacturer names and corresponding ids for targeting purposes.

CREATE TABLE handset_manufacturer
(
 -- manufacturer id , will be used for targeting match.
  manufacturer_id INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
  
  -- manufacturer name, to show on user interface for targeting.
  manufacturer_name VARCHAR(100) UNIQUE NOT NULL,

  -- modified by
  modified_by VARCHAR(50) NOT NULL,

  -- modified on
  modified_on TIMESTAMP NOT NULL
);

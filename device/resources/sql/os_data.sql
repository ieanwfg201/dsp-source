-- This table contains mobile operating system names and corresponding ids for targeting purposes.

CREATE TABLE handset_os
(
 -- operating system id , will be used for targeting match.
  os_id INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
  
  -- operating system name, to show on user interface for targeting.
  os_name VARCHAR(100) UNIQUE NOT NULL,

  -- modified by
  modified_by VARCHAR(50) NOT NULL,

  -- modified on
  modified_on TIMESTAMP NOT NULL
);

-- This table contains mobile browser names and corresponding ids for targeting purposes.

CREATE TABLE handset_browser
(
 -- browser id , will be used for targeting match.
  browser_id INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
  
  -- browser name, to show on user interface for targeting.
  browser_name VARCHAR(100) UNIQUE NOT NULL,

  -- modified by
  modified_by VARCHAR(50) NOT NULL,

  -- modified on
  modified_on TIMESTAMP NOT NULL
);

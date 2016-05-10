-- This table contains handset model names and corresponding ids for targeting purposes.

CREATE TABLE handset_model
(
 -- model id , will be used for targeting match.
  model_id INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
 
  -- manufacturer id, corresponding manufacturer id for sanity.
  manufacturer_id INTEGER NOT NULL,

  -- model name, to show on user interface for targeting.
  model_name VARCHAR(100) NOT NULL,

  -- modified by
  modified_by VARCHAR(50) NOT NULL,

  -- modified on
  modified_on TIMESTAMP NOT NULL,

  CONSTRAINT fk_manufacturer FOREIGN KEY (manufacturer_id) REFERENCES handset_manufacturer(manufacturer_id),

  CONSTRAINT unique_manu_model UNIQUE (manufacturer_id,model_name)
);

-- This table contains handset data to be updated periodically using wurfl or edited manually.

CREATE TABLE handset_detection_data
(
  -- internal id to be used in url and reporting,data etc.
  -- internal id is a representation of a useragent.
  internal_id BIGINT AUTO_INCREMENT PRIMARY KEY,

  -- external id for matching the data after handset has been detected.
  external_id VARCHAR(200) NOT NULL,

  -- source of data where detection is done from, could be wurfl or 
  -- can be modified for some properties ,to be tagged 'manual' then.
  source VARCHAR(20) NOT NULL,

  -- version of this data row
  version INTEGER NOT NULL,

  -- manufacturer id, used for targeting.
  manufacturer_id INTEGER NOT NULL,
  
  -- model id used for targeting
  model_id INTEGER NOT NULL,

  -- marketing name
  marketing_name VARCHAR(200) NOT NULL,

  -- device os id used for targeting
  deviceOsId INTEGER NOT NULL,
  
  -- device os version
  device_os_version VARCHAR(20) NOT NULL,

  -- mobile browser id used for targeting
  device_browser_id INTEGER NOT NULL,

  -- mobile browser version
  device_browser_version VARCHAR(20) NOT NULL,
 
  -- device capability json. Other than necessary and primary properties
  -- above, additional capabilities are defined in this json.
  -- examples could be sms prefix,mail prefix,call prefix,
  -- other properties.
  device_capability_json TEXT,

  -- modified by
  modified_by VARCHAR(50) NOT NULL,

  -- modified_on timestamp
  modified_on TIMESTAMP NOT NULL,
 
  CONSTRAINT pk_handset UNIQUE (external_id,source,version),
  CONSTRAINT fk_device_os FOREIGN KEY (deviceOsId) REFERENCES handset_os(os_id),
  CONSTRAINT fk_browser FOREIGN KEY (device_browser_id) REFERENCES handset_browser(browser_id),
  CONSTRAINT fk_model FOREIGN KEY (model_id) REFERENCES handset_model(model_id)
);

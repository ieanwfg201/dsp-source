-- Assumptions: Moved to https://docs.google.com/a/kritter.in/document/d/1ZS0muvBJek47AiAG_N2sLzunRSBRp5lI4KDS7T1NX8o/edit?usp=sharing

-- Store all kinds of accounts that are allowed (direct publishers, direct advertisers, exchange, agencies, affiliates, partners and so on)
-- However primary the function would be of a publisher or advertiser, ie, creating supply source and its attributes or demand creation and management.
-- The use of account type other than above would also be in determining access level of the user.
-- TODO only keep advertiser and publisher in account_type,remove everything else.
CREATE TABLE IF NOT EXISTS account_type
(
    id SMALLINT PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL,
    description VARCHAR(200) NOT NULL,
    created_on TIMESTAMP NOT NULL DEFAULT now(),
    last_modified TIMESTAMP NOT NULL,
    is_deprecated boolean DEFAULT false
);

-- status-es for entities (Active, Paused, Expired, Rejected, Pending Approval, Archived)
CREATE TABLE IF NOT EXISTS status 
(
    id SMALLINT PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL,
    description VARCHAR(300) NOT NULL,
    created_on TIMESTAMP NOT NULL DEFAULT now(),
    last_modified TIMESTAMP NOT NULL,
    is_deprecated boolean DEFAULT false
);

-- account table.
CREATE TABLE IF NOT EXISTS account
(
    id INTEGER UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    guid VARCHAR(100) UNIQUE NOT NULL,
    status int DEFAULT 5,
    type_id SMALLINT NOT NULL, 
    name VARCHAR(100) NOT NULL,
    userid VARCHAR(100) UNIQUE NOT NULL, 
    password VARCHAR(100) NOT NULL,
    email VARCHAR(200) NOT NULL,
    address VARCHAR(200) NOT NULL,
    country VARCHAR(50) NOT NULL,
    city VARCHAR(100) NOT NULL,
    phone VARCHAR(100) NOT NULL,
    company_name VARCHAR(256) DEFAULT NULL,
    modified_by INTEGER UNSIGNED NOT NULL, 
    created_on TIMESTAMP NOT NULL DEFAULT now(),
    last_modified TIMESTAMP NOT NULL,
    payment_type SMALLINT NOT NULL DEFAULT 0,
    bank_transfer_beneficiary_name VARCHAR(256) DEFAULT NULL,
    bank_transfer_account_number VARCHAR(128) DEFAULT NULL,
    bank_transfer_bank_name  VARCHAR(128) DEFAULT NULL,
    bank_transfer_bank_add  TEXT DEFAULT NULL,
    bank_transfer_branch_number  VARCHAR(128) DEFAULT NULL,
    bank_transfer_vat_number  VARCHAR(128) DEFAULT NULL,
    wire_beneficiary_name VARCHAR(256) DEFAULT NULL,
    wire_account_number VARCHAR(128) DEFAULT NULL,
    wire_bank_name  VARCHAR(128) DEFAULT NULL,
    wire_transfer_bank_add  TEXT DEFAULT NULL,
    wire_swift_code  VARCHAR(128) DEFAULT NULL,
    wire_iban VARCHAR(128) DEFAULT NULL,
    paypal_id VARCHAR(256) DEFAULT NULL,
    CONSTRAINT fk_account_acctype FOREIGN KEY(type_id) REFERENCES account_type(id)
);

-- dimensions for creatives. should follow IAB standards.0 height and 0 width means variable size.
-- this includes all the possible IAB mobile and web ad unit sizes.
-- mobile : http://www.iab.net/guidelines/508676/508767/mobileguidelines
-- web: http://www.iab.net/guidelines/508676/508767/displayguidelines
CREATE TABLE IF NOT EXISTS creative_slots
(
    id SMALLINT PRIMARY KEY,
    width SMALLINT NOT NULL,
    height SMALLINT NOT NULL,
    description VARCHAR(500) NOT NULL,
    modified_by INTEGER UNSIGNED NOT NULL,
    created_on TIMESTAMP NOT NULL DEFAULT now(),
    last_modified TIMESTAMP NOT NULL,
    is_deprecated boolean DEFAULT false,
    CONSTRAINT fk_creative_slot_modified_by FOREIGN KEY(modified_by) REFERENCES account(id)
);

-- kinds of creatives (text, banner, third party banners, rich media, ad tags)
CREATE TABLE IF NOT EXISTS creative_formats
(
    id SMALLINT  PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL,
    description VARCHAR(300) NOT NULL,
    modified_by INTEGER UNSIGNED NOT NULL,
    created_on TIMESTAMP NOT NULL DEFAULT now(),
    last_modified TIMESTAMP NOT NULL,
    is_deprecated boolean DEFAULT false,
    CONSTRAINT fk_creative_format_modified_by FOREIGN KEY(modified_by) REFERENCES account(id)
);

CREATE TABLE IF NOT EXISTS creative_attributes
(
    id INTEGER UNIQUE NOT NULL,
    value VARCHAR(500) NOT NULL,
    modified_by INTEGER UNSIGNED NOT NULL,
    created_on TIMESTAMP NOT NULL DEFAULT now(),
    last_modified TIMESTAMP NOT NULL,
    CONSTRAINT fk_creative_attr_modified_by FOREIGN KEY(modified_by) REFERENCES account(id)
);

-- Above Banner only entry refer to non openrtb entry equal to that defined in com.kritter.constants.CreativeAttribute

CREATE TABLE IF NOT EXISTS creative_banner
(
    id INTEGER UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    guid VARCHAR(100) UNIQUE NOT NULL, 
    account_guid VARCHAR(100) NOT NULL, 
    slot_id SMALLINT NOT NULL, 
    resource_uri VARCHAR(100) UNIQUE NOT NULL,
    modified_by INTEGER UNSIGNED NOT NULL,
    created_on TIMESTAMP NOT NULL DEFAULT now(),
    last_modified TIMESTAMP NOT NULL,
    CONSTRAINT fk_creative_banner_account FOREIGN KEY(account_guid) REFERENCES account(guid),
    CONSTRAINT fk_creative_banner_modified_by FOREIGN KEY(modified_by) REFERENCES account(id),
    CONSTRAINT fk_creative_banner_slot FOREIGN KEY(slot_id) REFERENCES creative_slots(id)
);

-- actual creative content alongside account identifier
CREATE TABLE IF NOT EXISTS creative_container
(
    id INTEGER UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    guid VARCHAR(100) UNIQUE NOT NULL, 
    -- (creative has to belong to a single account. This field cannot be empty)
    account_guid VARCHAR(100) NOT NULL,  
    -- refers to guid in account table
    -- a name for the user to identify the creative by a name
    label VARCHAR(100) NOT NULL, 
    -- (type of creative : text, banner, third party banner,rich media and so on)
    format_id SMALLINT NOT NULL, 
    -- refers to id creative_formats table
    -- creative attributes as per the IAB standards.
    creative_attr TEXT NOT NULL, 
    -- [1,2] where 1,2 refers to id in creative_attributes table 
    -- (text of the ad in the case of text ad, alt text in case of banner ads)
    text VARCHAR(140), 
    -- This contains integer ids '[id1,id2,id3]' taken from creative_banner(id) table. 
    resource_uri_ids TEXT, 
    -- (html content in case of Ad tags(getting creative content from some external server),richmedia(javascript payload) and so on)
    -- in case of richmedia resource_uri would not be set if the payload is provided here and not by some external server.
    html_content TEXT, 
    modified_by INTEGER UNSIGNED NOT NULL,
    created_on TIMESTAMP NOT NULL DEFAULT now(),
    last_modified TIMESTAMP NOT NULL,
    CONSTRAINT fk_creative_account FOREIGN KEY(account_guid) REFERENCES account(guid),
    CONSTRAINT fk_creative_format FOREIGN KEY(format_id) REFERENCES creative_formats(id),
    CONSTRAINT fk_creative_modified_by FOREIGN KEY(modified_by) REFERENCES account(id)
);

alter table creative_container add column status_id smallint(6) NOT NULL DEFAULT 1 after last_modified;
alter table creative_container ADD CONSTRAINT fk_creative_container_status FOREIGN KEY (status_id) REFERENCES status(id);


-- marketplace details (primarily pricing),cpm or cpc or cpa.
CREATE TABLE IF NOT EXISTS marketplace
(
    id SMALLINT PRIMARY KEY,
    pricing VARCHAR(50) UNIQUE NOT NULL,
    description VARCHAR(300) NOT NULL,
    modified_by INTEGER UNSIGNED NOT NULL,
    created_on TIMESTAMP NOT NULL DEFAULT now(),
    last_modified TIMESTAMP NOT NULL,
    is_deprecated boolean default false,
    CONSTRAINT fk_marketplace_modified_by FOREIGN KEY(modified_by) REFERENCES account(id)
);

-- ARRAYs are values separated by commas stored as TEXT and converted at server end.
-- ARRAY format: [val1,val2,val3] where val can be integer/short.
CREATE TABLE IF NOT EXISTS targeting_profile
(
    guid VARCHAR(100) UNIQUE NOT NULL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    account_guid VARCHAR(100) NOT NULL, 
    -- refers to guid in account table
    -- used in visibility under the account while selecting for an ad, if made false not visible for selection.
    -- also used in adserving loading of adentity, if status not active then ad will not be loaded.
    status_id SMALLINT NOT NULL DEFAULT 1,
    brand_list TEXT DEFAULT NULL, 
    -- e.g [1,2] where 1,2 refers to manufacturer_id in handset_manufacturer table
    model_list TEXT DEFAULT NULL, 
    -- e.g [1,2] where 1,2 refers to model_id in handset_model table

    -- this would contain os and its version, in range or standalone, if version is 2.1 it means 2.1.* are targeted as well.
    -- format {"os_id":"1-2","os_id":"3-4"}
    os_json VARCHAR(1000) DEFAULT NULL,

    -- this would contain browsers and versions, in range or standalone.
    -- {"browser_id":"1-2","browser_id":"3-4"}
    browser_json VARCHAR(1000) DEFAULT NULL,

    country_json TEXT DEFAULT NULL, -- e.g {"1":[1,2],"3":[1,2]} where key is id from ui_targeting_country table and value is id set from country table.
    carrier_json TEXT DEFAULT NULL, -- e.g {"1":[1,2],"3":[1,2]} where key is id from ui_targeting_isp and value is id set from isp table.

    state_json   TEXT DEFAULT NULL, -- e.g {"1":[],"3":[]} where key is id from ui_targeting_state table.
    city_json    TEXT DEFAULT NULL, -- e.g {"1":[],"3":[]} where key is id from ui_targeting_city table.
    zipcode_file_id_set TEXT,

    -- site_list contains the list of sites to be included or excluded. is_site_list_excluded is false if the sites are to be included and true if excluded
    -- if the site_list is null or site_list size is 0, all the sites are included
    site_list TEXT DEFAULT NULL, -- e.g [1,2] where 1,2 refred to id in site table
    is_site_list_excluded boolean DEFAULT FALSE,

    -- iab categories from open rtb 2.1
    -- category_list contains the list of categories to be included or excluded. 
    -- is_category_list_excluded is false if the categories are to be included and true if excluded
    category_list TEXT DEFAULT NULL, -- e.g. [1,2] where 1,2 refers to id in content_categories table
    is_category_list_excluded boolean DEFAULT FALSE,
    custom_ip_file_id_set TEXT, -- format = ["file1","file2"] where file1,file2 refers to file_id column of custom_ip_range table.
    modified_by INTEGER UNSIGNED NOT NULL,
    created_on TIMESTAMP NOT NULL DEFAULT now(),
    last_modified TIMESTAMP NOT NULL,
    supply_source_type SMALLINT default 1, -- refers to APP_WAP|WAP|APP  from SupplySourceTypeEnum.java
    supply_source SMALLINT default 1, -- refers to NETWORK|EXCHANGE|EXCHANGE_NETWORK  from SupplySourceEnum.java
    CONSTRAINT fk_targeting_profile_accountguid FOREIGN KEY(account_guid) REFERENCES account(guid),
    CONSTRAINT fk_targeting_profile_modified_by FOREIGN KEY(modified_by) REFERENCES account(id),
    UNIQUE KEY targeting_profile_name_account_unique (name,account_guid)
);

-- hours_list will have short array [0,1,2] (possible values 0-23), null or empty would mean all day targeting.
alter table targeting_profile add column hours_list TEXT default NULL after supply_source;
-- midp targeting , 1= midp1,2 =midp2, 3= both  
alter table targeting_profile add column midp SMALLINT default 3 after hours_list;

-- campaign table.
CREATE TABLE IF NOT EXISTS campaign
(
    id INTEGER UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    guid VARCHAR(100) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    account_guid VARCHAR(100) NOT NULL,  -- refers to guid in account table
    status_id SMALLINT NOT NULL, -- refers to id in status table
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP NOT NULL,
    created_on TIMESTAMP NOT NULL,
    modified_by INTEGER UNSIGNED NOT NULL,
    last_modified TIMESTAMP NOT NULL,
    CONSTRAINT fk_campaign_account FOREIGN KEY(account_guid) REFERENCES account(guid),
    CONSTRAINT fk_campaign_status FOREIGN KEY(status_id) REFERENCES status(id),
    CONSTRAINT fk_campaign_modified_by FOREIGN KEY(modified_by) REFERENCES account(id)
);

-- line_item or ad
-- (creative_ids have to be unique across accounts)(a creative may correspond to more than one ad.)
-- ad corresponds to one targeting profile and one creative, if a request comes in for a different
-- slot size not conforming to this creative size then a separate ad should be created with same
-- targeting profile.
CREATE TABLE IF NOT EXISTS ad
(
    id INTEGER UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    guid VARCHAR(100) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    creative_id INTEGER UNSIGNED NOT NULL,  -- refers to id in creative_container 
    creative_guid VARCHAR(100) NOT NULL,  -- refers to guid in creative_container
    landing_url VARCHAR(1024), 
    targeting_guid VARCHAR(100) NOT NULL, -- refers to guid in targeting_profile table
    campaign_id INTEGER UNSIGNED NOT NULL,  -- refers to id in campaign table
    campaign_guid VARCHAR(100) NOT NULL,  -- refers to guid in campaign table
    categories_list TEXT NOT NULL, -- IAB categories this ad belongs to. e.g [1,2] where 1,2 refers to id in content_categories table
    hygiene_list TEXT NOT NULL, -- hygiene category list this ad belongs to. e.g. [1,2] where 1,2 refers to id in hygiene_categories table
    status_id SMALLINT NOT NULL, -- refers to id in status table
    marketplace_id SMALLINT NOT NULL,  -- refers to id in marketplace_id
    internal_max_bid DOUBLE PRECISION NOT NULL,
    advertiser_bid DOUBLE PRECISION NOT NULL,
    allocation_ids TEXT NULL, /*comma separated allocation ids from allocation_id table*/
    created_on TIMESTAMP NOT NULL,
    last_modified TIMESTAMP NOT NULL,
    modified_by INTEGER UNSIGNED NOT NULL,
    comment TEXT default NULL,
    CONSTRAINT fk_ad_creativeid FOREIGN KEY(creative_id) REFERENCES creative_container(id),
    CONSTRAINT fk_ad_creativeguid FOREIGN KEY(creative_guid) REFERENCES creative_container(guid),
    CONSTRAINT fk_targeting_profile FOREIGN KEY(targeting_guid) REFERENCES targeting_profile(guid),
    CONSTRAINT fk_ad_campaignid FOREIGN KEY(campaign_id) REFERENCES campaign(id),
    CONSTRAINT fk_ad_campaignguid FOREIGN KEY(campaign_guid) REFERENCES campaign(guid),
    CONSTRAINT fk_ad_status FOREIGN KEY(status_id) REFERENCES status(id),
    CONSTRAINT fk_ad_marketplace FOREIGN KEY(marketplace_id) REFERENCES marketplace(id),
    CONSTRAINT fk_ad_modified_by FOREIGN KEY(modified_by) REFERENCES account(id)
);

CREATE TABLE IF NOT EXISTS allocation_id
(
    id INT PRIMARY KEY,
    definition TEXT NULL,
    description TEXT NULL,
    fraction double NOT NULL DEFAULT 0.0,
    last_modified TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS content_categories
(
    id SMALLINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    -- if available from IAB put that otherwise generate some random.
    code VARCHAR(50) UNIQUE NOT NULL, 
    value VARCHAR(300) NOT NULL,
    modified_by INTEGER UNSIGNED NOT NULL,
    created_on TIMESTAMP NOT NULL DEFAULT now(),
    last_modified TIMESTAMP NOT NULL,
    is_visible_on_advertiser_ui boolean default true,
    is_visible_on_publisher_ui boolean default true,
    is_deprecated boolean default false,
    CONSTRAINT fk_categories_modified_by FOREIGN KEY(modified_by) REFERENCES account(id)
);

CREATE TABLE IF NOT EXISTS hygiene_categories
(
    id SMALLINT UNSIGNED PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL,
    description VARCHAR(200) NOT NULL,
    modified_by INTEGER UNSIGNED NOT NULL,
    created_on TIMESTAMP NOT NULL DEFAULT now(),
    last_modified TIMESTAMP NOT NULL,
    CONSTRAINT fk_hyg_categ_modified_by FOREIGN KEY(modified_by) REFERENCES account(id)
);

CREATE TABLE IF NOT EXISTS site_platform
(
    id SMALLINT UNSIGNED PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL,
    description VARCHAR(200) NOT NULL,
    modified_by INTEGER UNSIGNED NOT NULL,
    created_on TIMESTAMP NOT NULL DEFAULT now(),
    last_modified TIMESTAMP NOT NULL,
    CONSTRAINT fk_site_platform_modified_by FOREIGN KEY(modified_by) REFERENCES account(id)
);

CREATE TABLE IF NOT EXISTS app_store
(
    id SMALLINT UNSIGNED PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    description VARCHAR(100) NOT NULL,
    modified_by INTEGER UNSIGNED NOT NULL,
    created_on TIMESTAMP NOT NULL DEFAULT now(),
    last_modified TIMESTAMP NOT NULL,
    CONSTRAINT fk_app_store_modified_by FOREIGN KEY(modified_by) REFERENCES account(id)
);

CREATE TABLE IF NOT EXISTS site
(
    id INTEGER UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    guid VARCHAR(100) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    pub_id INTEGER UNSIGNED NOT NULL, -- id in account table
    pub_guid VARCHAR(100) NOT NULL, -- guid in account table
    site_url VARCHAR(1024) NOT NULL,
    app_id VARCHAR(100),
    app_store_id SMALLINT, -- refers to id in app_store table
    categories_list TEXT NOT NULL, -- categories this site belongs to. e.g. [1,2] where 1,2 refers id in content_categories table
    category_list_inc_exc TEXT, -- categories of ads this site want to include exclude. e.g. [1,2] where 1,2 refers id in content_categories table
    is_category_list_excluded boolean DEFAULT TRUE,
    hygiene_list TEXT NOT NULL, -- hygiene categories this site belongs to, e.g. [1,2] where 1,2 refers id in  hygiene_categories table
    opt_in_hygiene_list TEXT,   -- hygiene categories of any ad this site wants to opt in. e.g. [1,2] where 1,2 refers id in hygiene_categories table
    creative_attr_inc_exc TEXT, -- creative attributes this site wants to exclude/include. e.g. [1,2] where 1,2 refers id in creative_attributes table
    is_creative_attr_exc boolean DEFAULT TRUE,
    site_platform_id SMALLINT UNSIGNED NOT NULL, -- refers to id in site_platform table
    status_id SMALLINT NOT NULL, -- refers to id in status table
    created_on TIMESTAMP NOT NULL DEFAULT now(),
    last_modified TIMESTAMP NOT NULL,
    modified_by INTEGER UNSIGNED NOT NULL,
    billing_rules_json TEXT NULL, -- having format {"payout":payout_percentage:int} {"payout":50}
    url_exclusion  TEXT NULL, -- comma separated url list  for exclusion e.g ["a.com","b.com"] 
    allow_house_ads BOOLEAN default true,
    comments TEXT NULL,
    CONSTRAINT fk_site_account_incid FOREIGN KEY(pub_id) REFERENCES account(id),
    CONSTRAINT fk_site_account_guid FOREIGN KEY(pub_guid) REFERENCES account(guid),
    CONSTRAINT fk_site_platform FOREIGN KEY(site_platform_id) REFERENCES site_platform(id),
    CONSTRAINT fk_site_status FOREIGN KEY(status_id) REFERENCES status(id),
    CONSTRAINT fk_site_modified_by FOREIGN KEY(modified_by) REFERENCES account(id)
);

-- budget tables, figures in USD.Somebody creates IO, then assigns it to account so that balance+=amount_of_IO.
-- Then campaigns are assigned the total budget and daily limits along with targeting profile.
CREATE TABLE IF NOT EXISTS account_budget
(
    account_guid VARCHAR(100) UNIQUE NOT NULL, -- refrs to guid in account table
    internal_balance DOUBLE PRECISION,
    internal_burn DOUBLE PRECISION,
    adv_balance DOUBLE PRECISION,
    adv_burn DOUBLE PRECISION,
    modified_by INTEGER UNSIGNED NOT NULL,
    created_on TIMESTAMP NOT NULL DEFAULT now(),
    last_modified TIMESTAMP NOT NULL,
    CONSTRAINT fk_account_budget_guid FOREIGN KEY(account_guid) REFERENCES account(guid),
    CONSTRAINT fk_account_budget_modified_by FOREIGN KEY(modified_by) REFERENCES account(id)
);

CREATE TABLE IF NOT EXISTS insertion_order
(
    order_number VARCHAR(200) NOT NULL,
    account_guid VARCHAR(100) NOT NULL, -- refers to guid in account table
    name VARCHAR(300) NOT NULL,
    total_value DOUBLE PRECISION,
    modified_by INTEGER UNSIGNED NOT NULL,
    created_on TIMESTAMP NOT NULL DEFAULT now(),
    last_modified TIMESTAMP NOT NULL,
    status SMALLINT default 0, -- 0 for unapproved , 1 for rejected, 2 for approved and added to account
    comment TEXT NULL,
    CONSTRAINT fk_io_account_budget_guid FOREIGN KEY(account_guid) REFERENCES account(guid),
    CONSTRAINT fk_io_budget_modified_by FOREIGN KEY(modified_by) REFERENCES account(id),
    UNIQUE KEY io_name_account (order_number,account_guid)
);


CREATE TABLE IF NOT EXISTS campaign_budget
(
    campaign_id INTEGER UNSIGNED NOT NULL, -- refers to id in campaign table
    campaign_guid VARCHAR(100) NOT NULL, -- refers to guid in campaign table
    internal_total_budget DOUBLE PRECISION,
    adv_total_budget DOUBLE PRECISION,
    internal_total_burn DOUBLE PRECISION,
    adv_total_burn DOUBLE PRECISION,
    internal_daily_budget DOUBLE PRECISION,
    adv_daily_budget DOUBLE PRECISION,
    internal_daily_burn DOUBLE PRECISION,
    adv_daily_burn DOUBLE PRECISION,
    modified_by INTEGER UNSIGNED NOT NULL,
    created_on TIMESTAMP NOT NULL DEFAULT now(),
    last_modified TIMESTAMP NOT NULL,
    CONSTRAINT fk_campaign_budget_inc_id FOREIGN KEY(campaign_id) REFERENCES campaign(id),
    CONSTRAINT fk_campaign_budget_guid FOREIGN KEY(campaign_guid) REFERENCES campaign(guid),
    CONSTRAINT fk_campaign_budget_modified_by FOREIGN KEY(modified_by) REFERENCES account(id)
);

CREATE TABLE IF NOT EXISTS payout
(
    pub_id INTEGER UNSIGNED NOT NULL, -- refers to id in account table
    ad_id INTEGER UNSIGNED NOT NULL, -- refers to id in ad table
    payout DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    monthly_payout DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    yearly_payout DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    created_on TIMESTAMP NOT NULL DEFAULT now(),
    last_modified TIMESTAMP NOT NULL,
    CONSTRAINT fk_exch_payout_pub_inc FOREIGN KEY(pub_id) REFERENCES account(id),
    CONSTRAINT fk_exch_payout_ad_inc_id FOREIGN KEY(ad_id) REFERENCES ad(id),
    CONSTRAINT unique_pub_ad_exch_payout UNIQUE(pub_id,ad_id)
);

CREATE TABLE IF NOT EXISTS handset_browser
(
  -- browser id , will be used for targeting match.
  browser_id INTEGER AUTO_INCREMENT NOT NULL PRIMARY KEY,
  -- browser name, to show on user interface for targeting.
  browser_name VARCHAR(100) UNIQUE NOT NULL,
  -- this column contains all browser versions as found in handset_detection_data table
  browser_versions TEXT,
  -- modified by
  modified_by VARCHAR(50) NOT NULL,
  -- modified on
  modified_on TIMESTAMP NOT NULL
) CHARACTER SET UTF16;

CREATE TABLE IF NOT EXISTS handset_manufacturer
(
  -- manufacturer id , will be used for targeting match.
  manufacturer_id INTEGER AUTO_INCREMENT NOT NULL PRIMARY KEY,
  -- manufacturer name, to show on user interface for targeting.
  manufacturer_name VARCHAR(100) UNIQUE NOT NULL,
  -- modified by
  modified_by VARCHAR(50) NOT NULL,
  -- modified on
  modified_on TIMESTAMP NOT NULL
) CHARACTER SET UTF16;

CREATE TABLE IF NOT EXISTS handset_model
(
  -- model id , will be used for targeting match.
  model_id INTEGER AUTO_INCREMENT NOT NULL PRIMARY KEY,
  -- manufacturer id, corresponding manufacturer id for sanity.
  manufacturer_id INTEGER NOT NULL, -- refers to  manufacturer_id in handset_manufacturer table
  -- model name, to show on user interface for targeting.
  model_name VARCHAR(100) NOT NULL,
  -- modified by
  modified_by VARCHAR(50) NOT NULL,
  -- modified on
  modified_on TIMESTAMP NOT NULL,
  CONSTRAINT fk_manufacturer FOREIGN KEY (manufacturer_id) REFERENCES handset_manufacturer(manufacturer_id)
) CHARACTER SET UTF16;

CREATE TABLE IF NOT EXISTS handset_os
(
  -- operating system id , will be used for targeting match.
  os_id INTEGER AUTO_INCREMENT NOT NULL PRIMARY KEY,
  -- operating system name, to show on user interface for targeting.
  os_name VARCHAR(100) UNIQUE NOT NULL,
  -- this column contains all os versions as found in handset_detection_data table.
  os_versions TEXT,
  -- modified by
  modified_by VARCHAR(50) NOT NULL,
  -- modified on
  modified_on TIMESTAMP NOT NULL
) CHARACTER SET UTF16;

-- This table contains handset data to be updated periodically using wurfl or edited manually.

CREATE TABLE IF NOT EXISTS handset_detection_data
(
  -- internal id to be used in url and reporting,data etc.
  -- internal id is a representation of a useragent.
  internal_id BIGINT AUTO_INCREMENT NOT NULL PRIMARY KEY,
  -- external id for matching the data after handset has been detected.
  external_id VARCHAR(100) NOT NULL,
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
  device_os_id INTEGER NOT NULL,
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
  CONSTRAINT uk_handset_data UNIQUE (external_id,source,version),
  CONSTRAINT fk_device_os FOREIGN KEY (device_os_id) REFERENCES handset_os(os_id),
  CONSTRAINT fk_browser FOREIGN KEY (device_browser_id) REFERENCES handset_browser(browser_id),
  CONSTRAINT fk_manuf   FOREIGN KEY (manufacturer_id) REFERENCES handset_manufacturer(manufacturer_id),
  CONSTRAINT fk_model FOREIGN KEY (model_id) REFERENCES handset_model(model_id)
) CHARACTER SET UTF16;

alter table handset_detection_data add index handset_detection_data_device_os_id (device_os_id);
alter table handset_detection_data add index handset_detection_data_device_browser_id (device_browser_id);


CREATE TABLE IF NOT EXISTS third_party_publishers
(
third_party_pub_id INTEGER AUTO_INCREMENT PRIMARY KEY,
third_party_pub_name VARCHAR(100) UNIQUE NOT NULL,
third_party_pub_desc VARCHAR(500) NOT NULL,
status BOOLEAN DEFAULT FALSE,
last_modified TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
created_on TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS third_party_tracking_url
(
third_party_id INTEGER NOT NULL,
third_party_name VARCHAR(100) NOT NULL,
tracking_uri VARCHAR(100) NOT NULL,
tracking_uri_description VARCHAR(100) NOT NULL,
status BOOLEAN DEFAULT FALSE,
last_modified TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
created_on TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS url_alias
(
id INTEGER AUTO_INCREMENT PRIMARY KEY NOT NULL,
actual_url VARCHAR(1024) NOT NULL,
alias_url VARCHAR(100) UNIQUE NOT NULL, -- This is the unique string generated by UUID, base url is prepended to this.
status BOOLEAN DEFAULT FALSE,
last_modified TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
created_on TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS country
(
id INTEGER AUTO_INCREMENT PRIMARY KEY NOT NULL,
country_code VARCHAR(5) NOT NULL,
country_name VARCHAR(200) NOT NULL,
data_source_name VARCHAR(50) NOT NULL,
modified_on TIMESTAMP NOT NULL,
CONSTRAINT unique_country_datasource UNIQUE (country_code,country_name,data_source_name)
)ENGINE=INNODB;

-- This table stores country data as country_code/country_name along with set of ids as generated by different datasources.
CREATE TABLE IF NOT EXISTS ui_targeting_country
(
id INTEGER AUTO_INCREMENT PRIMARY KEY NOT NULL,
country_code VARCHAR(10) UNIQUE NOT NULL,
country_name VARCHAR(200) UNIQUE NOT NULL,
entity_id_set TEXT NOT NULL, -- [1,2] where 1,2 refers to id column in country table which corresponds ids from multiple data source
modified_on TIMESTAMP NOT NULL
)ENGINE=INNODB;

CREATE TABLE IF NOT EXISTS state
(
id INTEGER PRIMARY KEY NOT NULL,
country_id INTEGER NOT NULL,
state_name VARCHAR(200) NOT NULL,
modified_on TIMESTAMP NOT NULL,
CONSTRAINT unique_state UNIQUE(country_id,state_name),
CONSTRAINT fk_country_st FOREIGN KEY (country_id) REFERENCES country(id)
)ENGINE=INNODB;

CREATE TABLE IF NOT EXISTS city
(
id INTEGER PRIMARY KEY NOT NULL,
state_id INTEGER NOT NULL,
city_name VARCHAR(200) NOT NULL,
modified_on TIMESTAMP NOT NULL,
CONSTRAINT fk_state_city FOREIGN KEY (state_id) REFERENCES state(id),
CONSTRAINT unique_city UNIQUE(state_id,city_name)
)ENGINE=INNODB;

CREATE TABLE IF NOT EXISTS latlong
(
id INTEGER PRIMARY KEY NOT NULL,
city_id INTEGER NOT NULL,
latitude DOUBLE PRECISION NOT NULL,
longitude DOUBLE PRECISION NOT NULL,
modified_on TIMESTAMP NOT NULL,
CONSTRAINT fk_city_latlong FOREIGN KEY (city_id) REFERENCES city(id),
CONSTRAINT unique_latlong UNIQUE(city_id,latitude,longitude)
)ENGINE=INNODB;

CREATE TABLE IF NOT EXISTS zipcode
(
id INTEGER PRIMARY KEY NOT NULL,
city_id INTEGER NOT NULL,
zipcode VARCHAR(50) NOT NULL,
modified_on TIMESTAMP NOT NULL,
CONSTRAINT fk_city_zipcode FOREIGN KEY (city_id) REFERENCES city(id),
CONSTRAINT unique_zipcode UNIQUE(city_id,zipcode)
)ENGINE=INNODB;

CREATE TABLE IF NOT EXISTS isp
(
id INTEGER AUTO_INCREMENT PRIMARY KEY NOT NULL,
country_id INTEGER NOT NULL,
isp_name VARCHAR(200) NOT NULL,
data_source_name VARCHAR(50) NOT NULL,
modified_on TIMESTAMP NOT NULL,
CONSTRAINT fk_country_isp FOREIGN KEY (country_id) REFERENCES country(id),
CONSTRAINT unique_isp_datasource UNIQUE(country_id,isp_name,data_source_name)
)ENGINE=INNODB;

alter table isp add index isp_country_id (country_id);

-- This table stores ispname along with set of ids generated by different geo datasources for a given isp.
CREATE TABLE IF NOT EXISTS ui_targeting_isp
(
id INTEGER AUTO_INCREMENT PRIMARY KEY NOT NULL,
country_ui_id INTEGER NOT NULL,
isp_ui_name VARCHAR(200) NOT NULL,
entity_id_set TEXT NOT NULL, -- [1,2] where 1,2 refers to id column in isp table
modified_on TIMESTAMP NOT NULL
)ENGINE=INNODB;

alter table ui_targeting_isp add index ui_targeting_isp_isp_name (isp_ui_name);

-- This table stores file id and name for custom ip ranges provided in CSV format
-- The full path for the online system is fetched as base_path(conf) plus the file_id.
CREATE TABLE IF NOT EXISTS custom_ip_range
(
id INTEGER AUTO_INCREMENT PRIMARY KEY NOT NULL,
file_id VARCHAR(100) UNIQUE NOT NULL,
file_name VARCHAR(100) UNIQUE NOT NULL,
file_description VARCHAR(200) NOT NULL,
account_guid VARCHAR(100) NOT NULL,
modified_on TIMESTAMP NOT NULL,
CONSTRAINT fk_cstm_ip_range_account FOREIGN KEY(account_guid) REFERENCES account(guid)
)ENGINE=INNODB;

-- This table stores file id and name for custom zipcode ranges provided in CSV format
-- The full path for the online system is fetched as base_path(conf) plus the file_id.
CREATE TABLE IF NOT EXISTS custom_zipcode_range
(
id INTEGER AUTO_INCREMENT PRIMARY KEY NOT NULL,
file_id VARCHAR(100) UNIQUE NOT NULL,
file_name VARCHAR(100) UNIQUE NOT NULL,
file_description VARCHAR(200) NOT NULL,
account_guid VARCHAR(100) NOT NULL,
modified_on TIMESTAMP NOT NULL,
CONSTRAINT fk_custom_zipcode_range_account FOREIGN KEY(account_guid) REFERENCES account(guid)
)ENGINE=INNODB;

-- Table to store process state, used for managing processes across multiple nodes.
CREATE TABLE IF NOT EXISTS process_state
(
state_identifier VARCHAR(100) UNIQUE NOT NULL,
description VARCHAR(300) NOT NULL,
status BOOLEAN NOT NULL,
last_modified TIMESTAMP NOT NULL
);

-- Below table not used for now, may be used for showing up isp names 
-- on user interface, the isp_alias will map to ispnames in isp table
-- , so selecting an isp present in this table
-- will trigger selection of all possible ispnames which might be this
-- isp. Example: entry in this table => ('IN',Airtel,Bharti Airtel), ('IN',Airtel,Bharti Broadband),('IN',Airtel,XYZ), so
-- Airtel shows up on userinterface, and when targeting is stored the corresponding ids of third column value are stored
-- in targeting profile table.So [Bharti Airtel, Bharti Broadband and XYZ] would be present in isp table. 
CREATE TABLE IF NOT EXISTS isp_mappings
(
id INTEGER AUTO_INCREMENT PRIMARY KEY NOT NULL,
country_name VARCHAR(200) NOT NULL,
isp_ui_name VARCHAR(200) NOT NULL,
isp_name VARCHAR(200) NOT NULL,
modified_on TIMESTAMP NOT NULL
)ENGINE=INNODB;

CREATE TABLE IF NOT EXISTS formats_attributes_mapping
(
    creative_formats_id smallint(6) NOT NULL,
    creative_attributes_id integer NOT NULL,
    last_modified TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    modified_by INTEGER UNSIGNED NOT NULL,
    CONSTRAINT fk_fam_creative_formats_id FOREIGN KEY(creative_formats_id) REFERENCES creative_formats(id),
    CONSTRAINT fk_formats_attributes_mapping_creative_attributes_id FOREIGN KEY(creative_attributes_id) REFERENCES creative_attributes(id)
) ENGINE=INNODB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS ctr_models
(
id INTEGER AUTO_INCREMENT PRIMARY KEY NOT NULL,
data LONGTEXT,
last_modified TIMESTAMP NOT NULL
) ENGINE=INNODB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS bidder_models
(
id INTEGER AUTO_INCREMENT PRIMARY KEY NOT NULL,
data LONGTEXT,
last_modified TIMESTAMP NOT NULL
) ENGINE=INNODB DEFAULT CHARSET=utf8;


alter table account add column im varchar(256) default NULL after paypal_id;
alter table account add column comment TEXT default NULL after im;
alter table targeting_profile add column geo_targeting_type int default 0 after supply_source;
alter table account add column api_key TEXT default NULL after comment;
alter table content_categories add column tier int default 1 after is_deprecated;
update content_categories set tier=2 where code like 'IAB%-%';

create table IF NOT EXISTS saved_query
(
id INTEGER AUTO_INCREMENT PRIMARY KEY NOT NULL,
name VARCHAR(256) NOT NULL,
reporting_entity blob NOT NULL,
status_id SMALLINT NOT NULL,
account_guid VARCHAR(100) NOT NULL,
created_on TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
modified_on TIMESTAMP NOT NULL,
CONSTRAINT fk_saved_query_account_guid FOREIGN KEY(account_guid) REFERENCES account(guid)
)ENGINE=InnoDB;
alter table saved_query add column reporting_type smallint default 1 after modified_on;
alter table site add column floor double default 0 after comments;
alter table ad add column tracking_partner int default 0 after comment;
ALTER TABLE ad CHANGE created_on  created_on TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE campaign CHANGE start_date  start_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;
alter table site add column is_advertiser_excluded boolean default true after floor;
alter table site add column advertiser_json_array blob after is_advertiser_excluded;
alter table isp modify column isp_name VARCHAR(200) CHARACTER SET utf8 COLLATE utf8_bin;
alter table isp_mappings modify column isp_name VARCHAR(200) CHARACTER SET utf8 COLLATE utf8_bin;
CREATE TABLE IF NOT EXISTS csr_models
(
id INTEGER AUTO_INCREMENT PRIMARY KEY NOT NULL,
data LONGTEXT,
last_modified TIMESTAMP NOT NULL
) ENGINE=INNODB DEFAULT CHARSET=utf8;
alter table account add column  inventory_source int default 0 NULL after api_key; -- refers to inventory source in com.kritter.constants.INVENTORY_SOURCE
alter table targeting_profile add column pub_list TEXT DEFAULT NULL after geo_targeting_type;

CREATE TABLE IF NOT EXISTS mcc_mnc
(
mcc VARCHAR(10) NOT NULL,
mnc VARCHAR(10) NOT NULL,
country_code VARCHAR(5) NOT NULL,
country_name VARCHAR(100) NOT NULL,
network_name VARCHAR(200) NOT NULL,
last_modified TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
)ENGINE=INNODB;

alter table site add column campaign_json_array blob after advertiser_json_array;

alter table ad add column cpa_goal double default 0.0 after tracking_partner;

-- Following table contains zipcode available from different data sources mapped against lat-long for lookup
-- in online applications, accuracy concept is of geonames as described however can be utilized for other
-- datasources with default value as centroid which means data is accurate.
-- An online application can choose to use only a few selected datasources.
CREATE TABLE IF NOT EXISTS zipcode_lat_long
(
country_code VARCHAR(5) NOT NULL,
zip_code VARCHAR(50) NOT NULL,
latitude double NOT NULL,
longitude double NOT NULL,
-- accuracy of lat/lng from 1=estimated to 6=centroid
accuracy SMALLINT NOT NULL DEFAULT 6,
data_source_name VARCHAR(50) NOT NULL
)ENGINE=INNODB; 

alter table targeting_profile add column lat_long TEXT default NULL after midp;

alter table isp_mappings add column is_marked_for_deletion smallint default 0 after isp_name;

alter table account add column  billing_rules_json text after inventory_source; 

alter table insertion_order add column created_by INTEGER UNSIGNED NOT NULL default 1 after comment;

create table IF NOT EXISTS supply_source_type(
id int default 2 primary key not null,
name varchar(32) not null
)ENGINE=INNODB;

alter table insertion_order add column belongs_to INTEGER UNSIGNED NOT NULL default 1 after created_by;

CREATE TABLE IF NOT EXISTS reporting_hygiene_categories
(
    id VARCHAR(16) NOT NULL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL,
    description VARCHAR(200) NOT NULL,
    modified_by INTEGER UNSIGNED NOT NULL,
    created_on TIMESTAMP NOT NULL DEFAULT now(),
    last_modified TIMESTAMP NOT NULL,
    CONSTRAINT fk_ui_hyg_categ_modified_by FOREIGN KEY(modified_by) REFERENCES account(id)
);

-- External supply attributes to target, schema is:  {siteincid:[{internalid,externalid,name,domain}]}
-- {"1":[{"intid":1,"id":"123","name":"name","domain":"domain"},{"intid":2,"id":"456","name":"name2","domain":"domain2"},
-- {"intid":3,"id":"789","name":"name3","domain":"domain3"}],"2":[{"intid":4,"id":"456","name":"name2","domain":"domain2"}]}
alter table targeting_profile add column ext_supply_attributes TEXT default NULL after lat_long;

-- id 0 and -1 will have special meaning for ext_supply_attr table
CREATE TABLE IF NOT EXISTS ext_supply_attr
(
id INTEGER AUTO_INCREMENT PRIMARY KEY NOT NULL,
site_inc_id INTEGER NOT NULL,
ext_supply_id VARCHAR(300) NOT NULL,
ext_supply_name VARCHAR(300),
ext_supply_domain VARCHAR(2500),
last_modified TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
UNIQUE KEY ext_supply_pub_unique (site_inc_id,ext_supply_id) 
)ENGINE=INNODB CHARSET=latin1; 

drop table latlong;
drop table zipcode;
drop table city;
drop table state;

CREATE TABLE IF NOT EXISTS state
(
id INTEGER AUTO_INCREMENT PRIMARY KEY NOT NULL,
country_id INTEGER NOT NULL,
state_name VARCHAR(200) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
data_source_name VARCHAR(50) NOT NULL,
modified_on TIMESTAMP NOT NULL,
CONSTRAINT unique_state UNIQUE(country_id,state_name),
CONSTRAINT fk_country_st FOREIGN KEY (country_id) REFERENCES country(id)
)ENGINE=INNODB;

CREATE TABLE IF NOT EXISTS city
(
id INTEGER AUTO_INCREMENT PRIMARY KEY NOT NULL,
state_id INTEGER NOT NULL,
city_name VARCHAR(200) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
data_source_name VARCHAR(50) NOT NULL,
modified_on TIMESTAMP NOT NULL,
CONSTRAINT fk_state_city FOREIGN KEY (state_id) REFERENCES state(id),
CONSTRAINT unique_city UNIQUE(state_id,city_name)
)ENGINE=INNODB;

-- This table stores statename along with set of ids generated by different geo datasources for a given statename.
CREATE TABLE IF NOT EXISTS ui_targeting_state
(
id INTEGER AUTO_INCREMENT PRIMARY KEY NOT NULL,
country_ui_id INTEGER NOT NULL,
state_name VARCHAR(200) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
entity_id_set TEXT NOT NULL, -- [1,2] where 1,2 refers to id column in state table
modified_on TIMESTAMP NOT NULL
)ENGINE=INNODB;

-- This table stores cityname along with set of ids generated by different geo datasources for a given cityname.
CREATE TABLE IF NOT EXISTS ui_targeting_city
(
id INTEGER AUTO_INCREMENT PRIMARY KEY NOT NULL,
state_ui_id INTEGER NOT NULL,
city_name VARCHAR(200) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
entity_id_set TEXT NOT NULL, -- [1,2] where 1,2 refers to id column in city table
modified_on TIMESTAMP NOT NULL
)ENGINE=INNODB;

create table IF NOT EXISTS logevents
(
`processing_time` varchar(19) DEFAULT NULL,
`time` varchar(19) DEFAULT NULL,
`eventTime` varchar(19) DEFAULT NULL,
status varchar(32)DEFAULT NULL,
event varchar(40) DEFAULT NULL, 
campaignId int DEFAULT NULL,
adId int DEFAULT NULL,
marketplace_id smallint DEFAULT NULL,
exchangeId int DEFAULT NULL,
siteId int DEFAULT NULL,
ext_supply_attr_internal_id int DEFAULT NULL,
inventorySource int DEFAULT NULL, 
supply_source_type smallint DEFAULT NULL,
advertiser_bid double DEFAULT NULL, 
internal_max_bid double DEFAULT NULL,
bidprice_to_exchange double DEFAULT NULL,
cpa_goal double DEFAULT NULL,
countryId int DEFAULT NULL,
countryCarrierId int DEFAULT NULL,
deviceId long DEFAULT NULL,
deviceManufacturerId int DEFAULT NULL,
deviceModelId int DEFAULT NULL,
deviceOsId int DEFAULT NULL,
deviceBrowserId int DEFAULT NULL,
ipAddress text,
userAgent text,
xForwardedFor text,
urlExtraParameters text,
version int DEFAULT NULL,
requestId text,
srcRequestId text,
impressionId text, 
srcUrlHash text,
aliasUrlId text,
thirdPartyTrackingUrlEventId text,
thirdPartyId text,
thirdPartyUrlHash text,
auction_price double DEFAULT NULL,
auction_currency text,
auction_seat_id text,
auction_bid_id text,
auction_id text,
auction_imp_id text,
countryRegionId int DEFAULT NULL,
selectedSiteCategoryId smallint DEFAULT NULL,
urlVersion int DEFAULT NULL,
bidderModelId smallint DEFAULT NULL,
slotId smallint DEFAULT NULL,
buyerUid  text
)ENGINE=INNODB;

update ext_supply_attr set ext_supply_name = 'None' where id = -1;

alter table ext_supply_attr add column req int default 0  after last_modified;
alter table ext_supply_attr add column approved boolean default false after req;
alter table ext_supply_attr add column unapproved boolean default false after approved;
alter table ext_supply_attr add column supply_source_type SMALLINT default 1 after unapproved;
alter table ext_supply_attr add column ext_supply_url text after supply_source_type;

alter table ext_supply_attr MODIFY ext_supply_id VARCHAR(180) DEFAULT NULL;
alter table ext_supply_attr CONVERT TO CHARACTER SET utf8;

-- schema :  {"1":[3]} , map of pubid and sitelist, where 1 is publisher id and [3] is array of siteids.
alter table targeting_profile add column direct_supply_inc_exc TEXT default NULL after ext_supply_attributes;

-- {"1":{"3":[{"intid":5,"id":null,"name":null,"domain":null}]}} , map of pubid and value as ( map of siteid and list of extr attributes )  - exchange supply
alter table targeting_profile add column exchange_supply_inc_exc TEXT default NULL after direct_supply_inc_exc;

-- schema : {"1",[9]} , where 1 is advertiser id and 9 is campaign id, so map of advertiser id and campaign id array.
alter table site add column campaign_inc_exc_schema TEXT default NULL after campaign_json_array;

CREATE TABLE IF NOT EXISTS connection_type_metadata
(
    id SMALLINT PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL,
    created_on TIMESTAMP NULL DEFAULT NULL,
    last_modified TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
)ENGINE=INNODB CHARSET=latin1;

alter table targeting_profile add column connection_type_targeting_json VARCHAR(512) default NULL after exchange_supply_inc_exc;

alter table site add column nofill_backup_content TEXT default NULL after campaign_inc_exc_schema;
alter table site add column passback_type SMALLINT default -1 after nofill_backup_content;
alter table site add column is_richmedia_allowed BOOLEAN default false after passback_type;
alter table ad add column adv_domain TEXT default NULL after cpa_goal;

alter table targeting_profile add column tablet_targeting boolean default FALSE;

alter table logevents add column connectionTypeId int after buyerUid;
alter table logevents add column referer text after connectionTypeId;

alter table ext_supply_attr add column osId INT default 1 after ext_supply_url;

alter table creative_container add column ext_resource_url TEXT  after status_id;
alter table targeting_profile add column supply_inc_exc TEXT default NULL after tablet_targeting;

CREATE TABLE IF NOT EXISTS site_metadata
(
    id INTEGER UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    site_guid VARCHAR(100) UNIQUE NOT NULL,
    passback_url VARCHAR(1024) DEFAULT NULL,
    response_content_type VARCHAR(50) DEFAULT NULL,
    response_content BLOB DEFAULT NULL,
    last_modified TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
)ENGINE=INNODB CHARSET=utf8;

-- ecpm might be used for taking decisions which dpa to choose from,
-- this ecpm can come from offline jobs or by direct api triggered from 
-- outside by a campaign manager.

CREATE TABLE IF NOT EXISTS dpa_ad_metadata
(
    id INTEGER UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    account_guid VARCHAR(100) NOT NULL,
    ad_guid VARCHAR(100) UNIQUE NOT NULL,
    ecpm DOUBLE  DEFAULT NULL,
    last_modified TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
)ENGINE=INNODB CHARSET=utf8;

-- if site_guid null that means rule_json is default for all sites for this publisher id,otherwise if site_guid present
-- then use that particular rule_json, example -> {"1":"vserv_dpa_id","2":"-1","3":"adx_dpa_id"}, where 1 is max priority,
-- 2 is second and 3 is least priority, vserv_dpa_id is vserver demand partner account guid, -1 is kritter dsp and adx_dpa_id is adx.

CREATE TABLE IF NOT EXISTS dpa_site_metadata
(
    id INTEGER UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    pub_guid VARCHAR(100) NOT NULL,
    site_guid VARCHAR(100) NULL,
    rule_json TEXT DEFAULT NULL,
    last_modified TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
)ENGINE=INNODB CHARSET=utf8;

-- refers to com.kritter.constants.DemandType
alter table account add column  demandtype smallint default 0 after billing_rules_json; 
-- refers to com.kritter.constants.DemandPreference
alter table account add column  demandpreference smallint default 0 after demandtype; 

alter table ad add column is_frequency_capped BOOLEAN default FALSE after adv_domain;
alter table ad add column frequency_cap INTEGER default 0 after is_frequency_capped;
alter table ad add column time_window INTEGER default 0 after frequency_cap;

alter table account add column  qps int default 5 after demandpreference;
alter table account add column  timeout int default 200 after qps;


CREATE TABLE IF NOT EXISTS ssp_global_rules
(
    id INTEGER UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    rule_def TEXT ,
    last_modified TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
)ENGINE=INNODB CHARSET=utf8;

CREATE TABLE IF NOT EXISTS ssp_pub_rules
(
    id INTEGER UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    pub_id INTEGER NOT NULL,
    rule_def TEXT ,
    last_modified TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
)ENGINE=INNODB CHARSET=utf8;

CREATE TABLE IF NOT EXISTS ssp_site_rules
(
    id INTEGER UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    site_id INTEGER NOT NULL,
    rule_def TEXT ,
    last_modified TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
)ENGINE=INNODB CHARSET=utf8;

CREATE TABLE IF NOT EXISTS parent_account_type
(
    id SMALLINT PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL,
    description VARCHAR(200) NOT NULL,
    created_on TIMESTAMP NOT NULL DEFAULT now(),
    last_modified TIMESTAMP NOT NULL,
    is_deprecated boolean DEFAULT false
);

CREATE TABLE IF NOT EXISTS parent_account
(
    id INTEGER UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    guid VARCHAR(100) UNIQUE NOT NULL,
    status int DEFAULT 5,
    type_id SMALLINT NOT NULL DEFAULT 4, -- refers to id in parent_account_type table
    name VARCHAR(100) NOT NULL,
    created_on TIMESTAMP NOT NULL DEFAULT now(),
    last_modified TIMESTAMP NOT NULL,
    CONSTRAINT fk_parent_account_acctype FOREIGN KEY(type_id) REFERENCES parent_account_type(id)    
);

-- Below query ensures that all account entities refer to some parent account, by default kept as same.
alter table account add column parent_account_guid VARCHAR(100) NOT NULL DEFAULT 'default_parent_account_guid', ADD FOREIGN KEY 
fk_parent_account_guid(parent_account_guid) REFERENCES parent_account(guid);

CREATE TABLE IF NOT EXISTS account_access
(
    guid VARCHAR(100) UNIQUE NOT NULL,
    name VARCHAR(50) UNIQUE NOT NULL,
    access_rule_json TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS account_user_type
(
    id SMALLINT PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL,
    description VARCHAR(200) NOT NULL,
    created_on TIMESTAMP NOT NULL DEFAULT now(),
    last_modified TIMESTAMP NOT NULL,
    is_deprecated boolean DEFAULT false
);

CREATE TABLE IF NOT EXISTS account_user
(
    id INTEGER PRIMARY KEY,
    guid VARCHAR(100) UNIQUE NOT NULL,
    name VARCHAR(50) UNIQUE NOT NULL,
    status int DEFAULT 5,
    description VARCHAR(200) NOT NULL,
    type_id SMALLINT NOT NULL DEFAULT 1, -- refers to id in account_user_type table    
    account_access_ref TEXT NOT NULL, -- TODO, decide on schema
    account_ref TEXT NOT NULL,        -- TODO, decide on schema
    created_on TIMESTAMP NOT NULL DEFAULT now(),
    last_modified TIMESTAMP NOT NULL,
    is_deprecated boolean DEFAULT false,
    CONSTRAINT fk_account_user_type FOREIGN KEY(type_id) REFERENCES account_user_type(id)    
);


CREATE Table IF NOT EXISTS retargeting_segment
(
    id INTEGER UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100)  NOT NULL,
    tag text,
    is_deprecated boolean DEFAULT false,
    account_guid VARCHAR(100) NOT NULL,  -- refers to guid in account table
    created_on TIMESTAMP NOT NULL DEFAULT now(),
    last_modified TIMESTAMP NOT NULL,
    modified_by INTEGER UNSIGNED NOT NULL,
    CONSTRAINT fk_retargeting_segment_account_guid FOREIGN KEY(account_guid) REFERENCES account(guid)
)ENGINE=INNODB;

alter table targeting_profile add column retargeting text after supply_inc_exc;
-- example: {"exchange_guid":["deal_id1","deal_id2"]}
alter table targeting_profile add column pmp_deal_json text after retargeting;

-- put required charset if it fails
CREATE TABLE IF NOT EXISTS ad_budget
(
    ad_guid VARCHAR(100) UNIQUE NOT NULL, -- refrs to guid in ad table
    impression_cap INTEGER NOT NULL DEFAULT 0,
    time_window_hours INTEGER NOT NULL DEFAULT 0,
    impressions_accrued INTEGER NOT NULL DEFAULT 0,
    modified_by INTEGER UNSIGNED NOT NULL,
    created_on TIMESTAMP NOT NULL DEFAULT now(),
    last_modified TIMESTAMP NOT NULL,
    CONSTRAINT fk_ad_budget_guid FOREIGN KEY(ad_guid) REFERENCES ad(guid),
    CONSTRAINT fk_ad_budget_modified_by FOREIGN KEY(modified_by) REFERENCES account(id)
)ENGINE=INNODB;

-- example : [1,2] , where 1 is desktop,2 is mobile,refer to com.kritter.constants.DeviceType,in future will have gaming consoles and TV.
alter table targeting_profile add column device_type text after pmp_deal_json;

-- refers to currency defaulting to USD which has numeric code of 840 in ISO 4217 format
alter table account add column currency int default  840 after parent_account_guid;
-- refers to any demand property com.kritter.entity.demand_props.DemandProps
alter table account add column demand_props text  after currency;
-- refers to any demand property com.kritter.entity.demand_props.DemandProps
alter table account add column supply_props text  after demand_props;

-- keep two letter and three letter code mappings, in ui_targeting_country,two letter codes are available.
create table country_code_mappings
(
 country_code_two VARCHAR(2) NOT NULL,
 country_code_three VARCHAR(3) NOT NULL,
 last_modified TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS campaign_impressions_budget
(
    campaign_guid VARCHAR(100) UNIQUE NOT NULL, -- refrs to guid in campaign table
    impression_cap INTEGER NOT NULL DEFAULT 0,
    time_window_hours INTEGER NOT NULL DEFAULT 0,
    impressions_accrued INTEGER NOT NULL DEFAULT 0,
    modified_by INTEGER UNSIGNED NOT NULL,
    created_on TIMESTAMP NOT NULL DEFAULT now(),
    last_modified TIMESTAMP NOT NULL,
    CONSTRAINT fk_campaign_impressions_budget_guid FOREIGN KEY(campaign_guid) REFERENCES campaign(guid),
    CONSTRAINT fk_campaign_impressions_budget_modified_by FOREIGN KEY(modified_by) REFERENCES account(id)
)ENGINE=INNODB;

alter table site add column native_props text after is_richmedia_allowed;
alter table site add column is_native boolean default false after native_props;
alter table account add column billing_name text  after supply_props;
alter table account add column billing_email text  after billing_name;

alter table creative_container add column native_demand_props TEXT  after ext_resource_url;
-- set charset according to parent table charset
CREATE TABLE `native_screenshot` 
(   
    `id` int(10) unsigned NOT NULL AUTO_INCREMENT,   
    `guid` varchar(100) NOT NULL,   
    `account_guid` varchar(100) NOT NULL,   
    `ss_size` smallint(6) NOT NULL,   
    `resource_uri` varchar(100) NOT NULL,   
    `modified_by` int(10) unsigned NOT NULL,   
    `created_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,   
    `last_modified` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',   
    PRIMARY KEY (`id`),   
    UNIQUE KEY `guid` (`guid`),   
    KEY `native_screenshot_account` (`account_guid`),   
    KEY `fk_native_screenshot_modified_by` (`modified_by`),   
    CONSTRAINT `fk_native_screenshot_account` FOREIGN KEY (`account_guid`) REFERENCES `account` (`guid`),   
    CONSTRAINT `fk_native_screenshot_modified_by` FOREIGN KEY (`modified_by`) REFERENCES `account` (`id`) 
) ENGINE=InnoDB ;

-- set charset according to parent table charset
CREATE TABLE `native_icon` 
(   `id` int(10) unsigned NOT NULL AUTO_INCREMENT,   
    `guid` varchar(100) NOT NULL,   
    `account_guid` varchar(100) NOT NULL,   
    `icon_size` smallint(6) NOT NULL,   
    `resource_uri` varchar(100) NOT NULL,   
    `modified_by` int(10) unsigned NOT NULL,   
    `created_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,   
    `last_modified` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',   
    PRIMARY KEY (`id`),   
    UNIQUE KEY `guid` (`guid`),   
    KEY `fk_native_icon_account` (`account_guid`),   
    KEY `fk_native_icon_modified_by` (`modified_by`),   
    CONSTRAINT `fk_native_icon_account` FOREIGN KEY (`account_guid`) REFERENCES `account` (`guid`),   
    CONSTRAINT `fk_native_icon_modified_by` FOREIGN KEY (`modified_by`) REFERENCES `account` (`id`) 
) ENGINE=InnoDB ;

alter table ad add column bidtype smallint default 0 after time_window;

alter table payout add column daily_payout double default 0 after last_modified;
alter table payout add column campaign_id int default -1 after payout;

alter table creative_container add column creative_macro TEXT  after native_demand_props;

alter table creative_container add column video_props TEXT  after creative_macro;

create table IF NOT EXISTS device_type(
id int default 2 primary key not null,
name varchar(32) not null
)ENGINE=INNODB;

CREATE TABLE IF NOT EXISTS `req_logging` (
  `pubId` VARCHAR(100) DEFAULT NULL,
  `enable` boolean DEFAULT false,  
  `time_period` int(11) DEFAULT 5,  
  `created_on` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `last_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  KEY `req_logging_pubId` (`pubId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- external tracker refer com.kritter.entity.external_tracker.ExtTracker
alter table ad add column external_tracker TEXT after bidtype; 
alter table country modify column country_code VARCHAR(10) NOT NULL;

alter table logevents add column adv_inc_id int after referer;
alter table logevents add column pub_inc_id int after adv_inc_id;
alter table logevents add column tevent text after pub_inc_id;
alter table logevents add column teventtype text after tevent;
alter table logevents add column deviceType SMALLINT after teventtype;
alter table logevents add column bidFloor DOUBLE after deviceType;
alter table logevents add column exchangeUserId text after bidFloor;
alter table logevents add column kritterUserId text after exchangeUserId;
alter table logevents add column externalSiteAppId text after kritterUserId;

ALTER TABLE formats_attributes_mapping ADD CONSTRAINT uq_formats_attributes_mapping UNIQUE(creative_formats_id, creative_attributes_id);

alter table account add column ext text  after billing_email;

ALTER TABLE isp_mappings ADD CONSTRAINT uq_isp_mappings UNIQUE(country_name, isp_name);
alter table state add column state_code VARCHAR(50) after country_id;
alter table city add column city_code VARCHAR(50) after state_id;

drop table ui_targeting_city;
drop table ui_targeting_state;

-- This table stores statename along with set of ids generated by different geo datasources for a given statename.
CREATE TABLE IF NOT EXISTS ui_targeting_state
(
id INTEGER AUTO_INCREMENT PRIMARY KEY NOT NULL,
country_ui_id INTEGER NOT NULL,
state_name VARCHAR(200) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
entity_id_set TEXT NOT NULL, -- [1,2] where 1,2 refers to id column in state table
modified_on TIMESTAMP NOT NULL
)ENGINE=INNODB;

-- This table stores cityname along with set of ids generated by different geo datasources for a given cityname.
CREATE TABLE IF NOT EXISTS ui_targeting_city
(
id INTEGER AUTO_INCREMENT PRIMARY KEY NOT NULL,
state_ui_id INTEGER NOT NULL,
city_name VARCHAR(200) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
entity_id_set TEXT NOT NULL, -- [1,2] where 1,2 refers to id column in city table
modified_on TIMESTAMP NOT NULL
)ENGINE=INNODB;

drop table ui_targeting_city;
drop table ui_targeting_state;

-- This table stores statename along with set of ids generated by different geo datasources for a given statename.
CREATE TABLE IF NOT EXISTS ui_targeting_state
(
id INTEGER AUTO_INCREMENT PRIMARY KEY NOT NULL,
country_ui_id INTEGER NOT NULL,
state_name VARCHAR(200) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
entity_id_set TEXT NOT NULL, -- [1,2] where 1,2 refers to id column in state table
modified_on TIMESTAMP NOT NULL
)ENGINE=INNODB;

-- This table stores cityname along with set of ids generated by different geo datasources for a given cityname.
CREATE TABLE IF NOT EXISTS ui_targeting_city
(
id INTEGER AUTO_INCREMENT PRIMARY KEY NOT NULL,
state_ui_id INTEGER NOT NULL,
city_name VARCHAR(200) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
entity_id_set TEXT NOT NULL, -- [1,2] where 1,2 refers to id column in city table
modified_on TIMESTAMP NOT NULL
)ENGINE=INNODB;


-- set charset according to parent table charset
CREATE TABLE `video_info`
(   `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
    `guid` varchar(100) NOT NULL,
    `account_guid` varchar(100) NOT NULL,
    `video_size` smallint(6) NOT NULL,
    `resource_uri` varchar(100) NOT NULL,
    `modified_by` int(10) unsigned NOT NULL,
    `created_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `last_modified` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
    PRIMARY KEY (`id`),
    UNIQUE KEY `guid` (`guid`),
    KEY `fk_video_info_account` (`account_guid`),
    KEY `fk_video_info_modified_by` (`modified_by`),
    CONSTRAINT `fk_video_info_account` FOREIGN KEY (`account_guid`) REFERENCES `account` (`guid`),
    CONSTRAINT `fk_video_info_modified_by` FOREIGN KEY (`modified_by`) REFERENCES `account` (`id`)
) ENGINE=InnoDB ;

alter table video_info add column ext text after last_modified;

CREATE TABLE `mma_categories` (
  `code` varchar(128) NOT NULL,
  `name` varchar(512) NOT NULL,
  `parent_code` varchar(128) DEFAULT NULL,
  `tier` int(11) DEFAULT '1',
  `modified_by` int(10) unsigned NOT NULL DEFAULT 1,
  `created_on` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `last_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY `code` (`code`),
  KEY `fk_mma_categories_modified_by` (`modified_by`),
  CONSTRAINT `fk_mma_categories_modified_by` FOREIGN KEY (`modified_by`) REFERENCES `account` (`id`)
) ENGINE=InnoDB;

CREATE TABLE `supply_mma_mapping` (
    `exchangename` varchar(16) NOT NULL,
    `supplycode` varchar(128) NOT NULL,
    `mma_category_code` varchar(128) NOT NULL,
    `modified_by` int(10) unsigned NOT NULL DEFAULT 1,
    `created_on` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
    `last_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
     `id` int unsigned NOT NULL AUTO_INCREMENT,
     PRIMARY KEY (`id`),
     UNIQUE KEY `exchangename_supplycode` (`exchangename`,`supplycode`),
     KEY `fk_supply_mma_mapping_modified_by` (`modified_by`),
     CONSTRAINT `fk_supply_mma_mapping_modified_by` FOREIGN KEY (`modified_by`) REFERENCES `account` (`id`),
     KEY `fk_supply_mma_mapping_mma_category_code` (`mma_category_code`),
     CONSTRAINT `fk_supply_mma_mapping_mma_category_code` FOREIGN KEY (`mma_category_code`) REFERENCES `mma_categories` (`code`)
) ENGINE=InnoDB;

CREATE TABLE `mma_exchangename_id_mapping` (
  `exchangename` varchar(16) NOT NULL,
  `exchangeid` INTEGER NOT NULL,
  `exchangeguid` VARCHAR(100) NOT NULL,
   UNIQUE KEY `mma_exchangename_id` (`exchangename`,`exchangeid`),
   `created_on` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
   `last_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

create table ui_mma_category (
    `id` int unsigned NOT NULL AUTO_INCREMENT,
    `name` varchar(512) NOT NULL,
    `created_on` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
    `last_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB;

create table mma_code_mma_ui_mapping (
    `code` varchar(128) NOT NULL,
    `ui_id` int unsigned NOT NULL AUTO_INCREMENT,
    `created_on` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
    `last_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY `mma_code_mma_ui_mapping_name` (`code`,`ui_id`),
    KEY `fk_mma_code_mma_ui_mapping_code` (`code`),
    CONSTRAINT `fk_mma_code_mma_ui_mapping_code` FOREIGN KEY (`code`) REFERENCES `mma_categories` (`code`),
    KEY `fk_mma_code_mma_ui_mapping_ui_id` (`ui_id`),
    CONSTRAINT `fk_mma_code_mma_ui_mapping_ui_id` FOREIGN KEY (`ui_id`) REFERENCES `ui_mma_category` (`id`)

) ENGINE=InnoDB;

alter table ad add column ext TEXT after external_tracker;

alter table ui_mma_category add column tier int default 1 after name;

alter table targeting_profile add column ext TEXT after device_type;

alter table ui_mma_category add column mma_type int default 1 after tier;

alter table mma_categories add column mma_type int default 1 after last_modified;

-- This table contains mapping between a third party connection (demand channel modelled as advertiser in our system),
-- and its DSP that has a seat on it. In case of where third party connection is a standalone DSP the dsp_id is same
-- as third_party_conn_id. Example bidswitch is marketplace of DSPs, in which case it would have multiple entries.
create table third_party_conn_dsp_mapping
(
    id int unsigned NOT NULL AUTO_INCREMENT PRIMARY KEY,
    third_party_conn_id VARCHAR(100) NOT NULL,
    dsp_id VARCHAR(100) NOT NULL,
    description VARCHAR(100),
    last_modified timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_third_party_conn_id_adv_id FOREIGN KEY (third_party_conn_id) REFERENCES account(guid)
) ENGINE = InnoDB;


-- This table contains mapping between a stand alone DSP and advertiser/agency using it.
-- In case of standalone DSP this table can be used for sending whitelist of advertisers.
create table dsp_adv_agency_mapping
(
    id int unsigned NOT NULL AUTO_INCREMENT PRIMARY KEY,
    dsp_id VARCHAR(100) NOT NULL,
    adv_id VARCHAR(100) NOT NULL,
    description VARCHAR(100),
    last_modified timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE = InnoDB;

-- This table contains private marketplace deal with all of its attributes. The deal is created for a publisher.
create table pmp_deals
(
    deal_id VARCHAR(200) UNIQUE NOT NULL PRIMARY KEY, -- nomenclature: SSP-MMYY-Numeric_Value,e.g: nativead-0716-9871344567
    deal_name VARCHAR(200) UNIQUE NOT NULL,           -- nomenclature: ATD/Buyer-dsp-publisher-placement-MMDDYY,e.g: VIVAKI-MEDIAMATH-nativehome-sports_home-071116
    ad_guid VARCHAR(100) NOT NULL,                    -- ad.guid column,so that deal is aware where its applied via targeting,otherwise will be part of all requests.
    site_id_list TEXT NOT NULL,                       -- This has list of site ids for which deal is applicable,e.g: ["1","2"] or can be null
    bcat TEXT,                                        -- iab categories code array for list of iab categories to block.
    third_party_conn_list TEXT NOT NULL,              -- string array, with each value as an advertiser guid.
    dsp_id_list TEXT,                                 -- integer array, with each value as id from third_party_conn_dsp_mapping table.
    adv_id_list TEXT,                                 -- integer array, with each value as id from dsp_adv_agency_mapping table.
    wadomain TEXT,                                    -- whitelisted domains of advertisers,Map<String,String[]> syntax json.
    auction_type smallint NOT NULL,                   -- possible values 1,2,3. Refer to descriptions in open rtb documents for pmp auction type.
    request_cap int unsigned,                  
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP NOT NULL,
    deal_cpm double,
    last_modified TIMESTAMP NOT NULL
) ENGINE = InnoDB;

create table`ad_position` (
  `internalid` int unsigned NOT NULL AUTO_INCREMENT,
  `pubIncId` int NOT NULL,
  `adposid` varchar(512) NOT NULL,
  `name`  TEXT,
  `description` TEXT,
  `created_on` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `last_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`internalid`),
  UNIQUE KEY `ad_position_unique` (`pubIncId`,`adposid`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `channel` (
  `internalid` int unsigned NOT NULL AUTO_INCREMENT,
  `exchangename` varchar(16) NOT NULL,
  `channelcode` varchar(128) NOT NULL,
   `tier` int(11) DEFAULT '1',
  `modified_by` int(10) unsigned NOT NULL DEFAULT '1',
  `created_on` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `last_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`internalid`),
  UNIQUE KEY `channel_exchangename_channelcode` (`exchangename`,`channelcode`),
  KEY `fk_channel_modified_by` (`modified_by`),
  CONSTRAINT `fk_channel_modified_by` FOREIGN KEY (`modified_by`) REFERENCES `account` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

alter table account add column third_party_demand_channel_type int;
alter table account add column open_rtb_ver_required int;

alter table channel add column parentcode varchar(128) default NULL after last_modified;
alter table channel add column channelname varchar(128) default NULL after parentcode;

alter table site add column video_supply_props text after is_native;
alter table site add column is_video boolean default false after video_supply_props;

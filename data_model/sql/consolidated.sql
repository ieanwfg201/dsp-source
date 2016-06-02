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
insert into account_type (id, name,description,created_on,last_modified,is_deprecated) values 
(1,'root','universally super user',now(),now(),false),
(2,'directpublisher','direct publisher having our adcode',now(),now(),false),
(3,'directadvertiser','direct advertiser having demand',now(),now(),false),
(4,'exchange','adexchange who performs auction',now(),now(),false),
(5,'atd','agency trading desk,which buys inventory on behalf of advertisers',now(),now(),false),
(6,'adops','ad operations',now(),now(),false),
(7,'pubops','publisher operations',now(),now(),false),
(8,'pubbd','publisher business developer',now(),now(),false),
(9,'adsales','advertiser sales',now(),now(),false);

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

insert into status (id,name,description,created_on,last_modified,is_deprecated) values 
(1,'Active','Entity is in active status',now(),now(),false),
(2,'Paused','Entity has been paused by the user',now(),now(),false),
(3,'Expired','Entity completion date has passed',now(),now(),false),
(4,'Rejected','Entity has been rejected in the approval process',now(),now(),false),
(5,'Pending','Entity is pending approval',now(),now(),false),
(6,'Experimental','Entity is for experimentation',now(),now(),false),
(7,'Sandbox','Entity is for sandbox testing',now(),now(),false),
(8,'DEBUG','Entity is for debugging purposes',now(),now(),false),
(9,'Approved','Entity is approved',now(),now(),false);

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
insert into account (guid,status,type_id,name,userid,password,email,address,country,city,phone,modified_by,created_on,last_modified) values 
('root-guid',1,1,'root','admin','$2a$10$O5twVdGKoWvtabDCsivzi.EHJFKUqRQXhM41NMnxJmnFH/Lg4Vn8y','chaharv@gmail.com','private','india','bangalore','999999999',1,now(),now()),
('test_pub_guid',1,2,'test_pub','testpub','$2a$10$O5twVdGKoWvtabDCsivzi.EHJFKUqRQXhM41NMnxJmnFH/Lg4Vn8y','test','india','india','bang','999999999',1,now(),now()),
('test_adv_guid',1,3,'test_adv','testadv','$2a$10$O5twVdGKoWvtabDCsivzi.EHJFKUqRQXhM41NMnxJmnFH/Lg4Vn8y','test','india','india','bang','9999999',1,now(),now());

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
-- The ids entered here should not conflict with the sql under creative_slot folder
insert into creative_slots (id,width,height,description,modified_by,created_on,last_modified,is_deprecated) values 
(1,120,20,'Feature Phone Small Banner',1,now(),now(),false),
(2,168,28,'Feature Phone Medium Banner',1,now(),now(),false),
(3,216,36,'Feature Phone Large Banner',1,now(),now(),false),
(4,300,250,'Smartphone Static Interstitial',1,now(),now(),false),
(5,300,50,'Smartphone Static Banner',1,now(),now(),false),
(6,320,50,'Smartphone Static Wide Banner',1,now(),now(),false),
(7,300,250,'Smartphone Rich Interstitial',1,now(),now(),false),
(8,300,50,'Smartphone Rich Banner & Expandable',1,now(),now(),false),
(9,320,50,'Smartphone Rich Wide Banner & Expandable',1,now(),now(),false),
(10,970,250,'Billboard',1,now(),now(),false),
(11,300,600,'Filmstrip',1,now(),now(),false),
(12,300,1050,'Portrait',1,now(),now(),false),
(13,970,90,'Pushdown',1,now(),now(),false),
(14,300,250,'Sidekick',1,now(),now(),false),
(15,300,600,'Sidekick',1,now(),now(),false),
(16,970,250,'Sidekick',1,now(),now(),false),
(17,970,90,'Slider',1,now(),now(),false),
(18,300,250,'Medium Rectangle',1,now(),now(),false),
(19,180,150,'Rectangle',1,now(),now(),false),
(20,160,600,'Wide Skyscraper',1,now(),now(),false),
(21,728,90,'Leaderboard',1,now(),now(),false),(22,970,90,'Super Leaderboard',1,now(),now(),false),
(23,300,600,'Half Page',1,now(),now(),false),(24,120,60,'Button 2',1,now(),now(),false),(25,88,31,'Micro Bar',1,now(),now(),false),
(26,300,250,'In-Banner Video(File Loaded)',1,now(),now(),false),(27,180,150,'In-Banner Video(File Loaded)',1,now(),now(),false),
(28,160,600,'In-Banner Video(File Loaded)',1,now(),now(),false),(29,728,90,'In-Banner Video(File Loaded)',1,now(),now(),false),
(30,300,600,'In-Banner Video(File Loaded)',1,now(),now(),false),(31,300,250,'In-Banner Video(Streaming)',1,now(),now(),false),
(32,180,150,'In-Banner Video(Streaming)',1,now(),now(),false),(33,160,600,'In-Banner Video(Streaming)',1,now(),now(),false),
(34,728,90,'In-Banner Video(Streaming)',1,now(),now(),false),(35,300,600,'In-Banner Video(Streaming)',1,now(),now(),false),
(36,300,250,'Pop Ups',1,now(),now(),false),(37,550,480,'Pop Ups',1,now(),now(),false),(38,300,600,'',1,now(),now(),false),
(39,0,0,'Floating Variable Initial Dimensions',1,now(),now(),false),(40,0,0,'Variable Between the page aka Interstitial',1,now(),now(),false);

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
insert into creative_formats (id,name,description,modified_by,created_on,last_modified,is_deprecated) values 
(1,'Text','Text Creative',1,now(),now(),false),
(2,'Banner','Banner Creative',1,now(),now(),false),
(3,'Rich Media','Rich Media Creative',1,now(),now(),false),
(4,'Video','Video Creative',1,now(),now(),false);

CREATE TABLE IF NOT EXISTS creative_attributes
(
    id INTEGER UNIQUE NOT NULL,
    value VARCHAR(500) NOT NULL,
    modified_by INTEGER UNSIGNED NOT NULL,
    created_on TIMESTAMP NOT NULL DEFAULT now(),
    last_modified TIMESTAMP NOT NULL,
    CONSTRAINT fk_creative_attr_modified_by FOREIGN KEY(modified_by) REFERENCES account(id)
);

insert into creative_attributes (id,value,modified_by,created_on,last_modified) values
(1,"Audio Ad (Auto Play)",1,now(),now()),
(2,"Audio Ad (User Initiated)",1,now(),now()),
(3,"Expandable (Automatic)",1,now(),now()),
(4,"Expandable (User Initiated - Click)",1,now(),now()),
(5,"Expandable (User Initiated - Rollover)",1,now(),now()),
(6,"In-Banner Video Ad (Auto Play)",1,now(),now()),
(7,"In-Banner Video Ad (User Initiated)",1,now(),now()),
(8,"Pop (e.g., Over, Under, or upon Exit)",1,now(),now()),
(9,"Provocative or Suggestive Imagery",1,now(),now()),
(10,"Shaky, Flashing, Flickering, Extreme Animation, Smileys",1,now(),now()),
(11,"Surveys",1,now(),now()),
(12,"Text Only",1,now(),now()),
(13,"User Interactive (e.g., Embedded Games)",1,now(),now()),
(14,"Windows Dialog or Alert Style",1,now(),now()),
(15,"Has audio on/off button",1,now(),now()),
(16,"Ad can be skipped (e.g., skip button on preroll video)",1,now(),now()),
(17,"Banner Only",1,now(),now());
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

insert into creative_banner (guid,account_guid,slot_id,resource_uri,modified_by,last_modified) values ('test_banner_guid','test_adv_guid',6,"test_banner.png",1,now());

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

insert into creative_container (guid,account_guid,label,format_id,creative_attr,text,modified_by,created_on,last_modified,status_id) values 
('test_creative_text','test_adv_guid','text_ad_test',1,'[12]','This is test text ad,comes default with installation of the DSP, part of test advertiser account.',1,now(),now(),1);

insert into creative_container (guid,account_guid,label,format_id,creative_attr,text,resource_uri_ids,modified_by,created_on,last_modified,status_id) values
('test_creative_banner','test_adv_guid','banner_ad_test',2,'[10]','This is test banner ad,comes default with installation of the DSP, part of test advertiser account.','["test_banner_guid"]',1,now(),now(),1);

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

insert into marketplace (id,pricing,description,modified_by,created_on,last_modified,is_deprecated) values 
(1,'CPC','Cost per click in dollars',1,now(),now(),false),
(2,'CPM','Cost per thousand impressions in dollars',1,now(),now(),false),
(3,'CPD','Cost per download/Acquisition in dollars',1,now(),now(),false);

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

    state_json   TEXT DEFAULT NULL,
    city_json    TEXT DEFAULT NULL,
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

insert into targeting_profile (
    guid,
    name,
    account_guid,
    status_id,
    brand_list,
    model_list,
    os_json,
    browser_json,
    country_json,
    carrier_json,
    site_list,
    is_site_list_excluded,
    category_list,
    is_category_list_excluded,
    custom_ip_file_id_set,
    modified_by,
    created_on,
    last_modified) values 
    ('test_targeting_profile_guid','test_targeting_profile','test_adv_guid',true,'[32,1]','[283]','{"9":"3.2-4.2"}','{"3":"All-All"}',null,null,'[1]',false,'[1]',false,null,1,now(),now()),('test_targeting_profile_guid2','test_targeting_profile2','test_adv_guid',true,null,null,null,null,null,null,'[1]',false,'[1]',false,null,1,now(),now());

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
insert into campaign (guid,name,account_guid,status_id,start_date,end_date,created_on,modified_by,last_modified) values 
('test_campaign_guid','test_campaign','test_adv_guid',1,now(),'2025-05-25 15:16:49',now(),1,now());

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

insert into ad(guid,creative_id,creative_guid,landing_url,targeting_guid,campaign_id,campaign_guid,categories_list,hygiene_list,status_id,marketplace_id,internal_max_bid,advertiser_bid,created_on,last_modified,modified_by) values 
('test_ad_guid',2,'test_creative_banner','http://kritter.in','test_targeting_profile_guid2',1,'test_campaign_guid','[1]','[1]',1,2,0.1,0.2,now(),now(),1);

CREATE TABLE IF NOT EXISTS allocation_id
(
    id INT PRIMARY KEY,
    definition TEXT NULL,
    description TEXT NULL,
    fraction double NOT NULL DEFAULT 0.0,
    last_modified TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

insert into allocation_id (id,definition,description,fraction) values (1,'EXPERIMENT',null,0.5);

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

-- All of the IAB categories are included here however we might not show all of them
-- and just show the parent categories (whose code field do not have '-' in them),
-- or we might choose to show few of them,they can be set to be not visible on interface.

insert into content_categories (code,value,modified_by,created_on,last_modified) values 
("IAB1","Arts & Entertainment",1,now(),now()),
("IAB1-1","Books & Literature",1,now(),now()),
("IAB1-2","Celebrity Fan/Gossip",1,now(),now()),
("IAB1-3","Fine Art",1,now(),now()),
("IAB1-4","Humor",1,now(),now()),
("IAB1-5","Movies",1,now(),now()),
("IAB1-6","Music",1,now(),now()),
("IAB1-7","Television",1,now(),now()),
("IAB2","Automotive",1,now(),now()),
("IAB2-1","Auto Parts",1,now(),now()),
("IAB2-2","Auto Repair",1,now(),now()),
("IAB2-3","Buying/Selling Cars",1,now(),now()),
("IAB2-4","Car Culture",1,now(),now()),
("IAB2-5","Certified Pre-Owned",1,now(),now()),
("IAB2-6","Convertible",1,now(),now()),
("IAB2-7","Coupe",1,now(),now()),
("IAB2-8","Crossover",1,now(),now()),
("IAB2-9","Diesel",1,now(),now()),
("IAB2-10","Electric Vehicle",1,now(),now()),
("IAB2-11","Hatchback",1,now(),now()),
("IAB2-12","Hybrid",1,now(),now()),
("IAB2-13","Luxury",1,now(),now()),
("IAB2-14","MiniVan",1,now(),now()),
("IAB2-15","Motorcycles",1,now(),now()),
("IAB2-16","Off-Road Vehicles",1,now(),now()),
("IAB2-17","Performance Vehicles",1,now(),now()),
("IAB2-18","Pickup",1,now(),now()),
("IAB2-19","Road-Side Assistance",1,now(),now()),
("IAB2-20","Sedan",1,now(),now()),
("IAB2-21","Trucks & Accessories",1,now(),now()),
("IAB2-22","Vintage Cars",1,now(),now()),
("IAB2-23","Wagon",1,now(),now()),
("IAB3","Business",1,now(),now()),
("IAB3-1","Advertising",1,now(),now()),
("IAB3-2","Agriculture",1,now(),now()),
("IAB3-3","Biotech/Biomedical",1,now(),now()),
("IAB3-4","Business Software",1,now(),now()),
("IAB3-5","Construction",1,now(),now()),
("IAB3-6","Forestry",1,now(),now()),
("IAB3-7","Government",1,now(),now()),
("IAB3-8","Green Solutions",1,now(),now()),
("IAB3-9","Human Resources",1,now(),now()),
("IAB3-10","Logistics",1,now(),now()),
("IAB3-11","Marketing",1,now(),now()),
("IAB3-12","Metals",1,now(),now()),
("IAB4","Careers",1,now(),now()),
("IAB4-1","Career Planning",1,now(),now()),
("IAB4-2","College",1,now(),now()),
("IAB4-3","Financial Aid",1,now(),now()),
("IAB4-4","Job Fairs",1,now(),now()),
("IAB4-5","Job Search",1,now(),now()),
("IAB4-6","Resume Writing/Advice",1,now(),now()),
("IAB4-7","Nursing",1,now(),now()),
("IAB4-8","Scholarships",1,now(),now()),
("IAB4-9","Telecommuting",1,now(),now()),
("IAB4-10","U.S. Military",1,now(),now()),
("IAB4-11","Career Advice",1,now(),now()),
("IAB5","Education",1,now(),now()),
("IAB5-1","7-12 Education",1,now(),now()),
("IAB5-2","Adult Education",1,now(),now()),
("IAB5-3","Art History",1,now(),now()),
("IAB5-4","Colledge Administration",1,now(),now()),
("IAB5-5","College Life",1,now(),now()),
("IAB5-6","Distance Learning",1,now(),now()),
("IAB5-7","English as a 2nd Language",1,now(),now()),
("IAB5-8","Language Learning",1,now(),now()),
("IAB5-9","Graduate School",1,now(),now()),
("IAB5-10","Homeschooling",1,now(),now()),
("IAB5-11","Homework/Study Tips",1,now(),now()),
("IAB5-12","K-6 Educators",1,now(),now()),
("IAB5-13","Private School",1,now(),now()),
("IAB5-14","Special Education",1,now(),now()),
("IAB5-15","Studying Business",1,now(),now()),
("IAB6","Family & Parenting",1,now(),now()),
("IAB6-1","Adoption",1,now(),now()),
("IAB6-2","Babies & Toddlers",1,now(),now()),
("IAB6-3","Daycare/Pre School",1,now(),now()),
("IAB6-4","Family Internet",1,now(),now()),
("IAB6-5","Parenting - K-6 Kids",1,now(),now()),
("IAB6-6","Parenting teens",1,now(),now()),
("IAB6-7","Pregnancy",1,now(),now()),
("IAB6-8","Special Needs Kids",1,now(),now()),
("IAB6-9","Eldercare",1,now(),now()),
("IAB7","Health & Fitness",1,now(),now()),
("IAB7-1","Exercise",1,now(),now()),
("IAB7-2","A.D.D.",1,now(),now()),
("IAB7-3","AIDS/HIV",1,now(),now()),
("IAB7-4","Allergies",1,now(),now()),
("IAB7-5","Alternative Medicine",1,now(),now()),
("IAB7-6","Arthritis",1,now(),now()),
("IAB7-7","Asthma",1,now(),now()),
("IAB7-8","Autism/PDD",1,now(),now()),
("IAB7-9","Bipolar Disorder",1,now(),now()),
("IAB7-10","Brain Tumor",1,now(),now()),
("IAB7-11","Cancer",1,now(),now()),
("IAB7-12","Cholesterol",1,now(),now()),
("IAB7-13","Chronic Fatigue Syndrome",1,now(),now()),
("IAB7-14","Chronic Pain",1,now(),now()),
("IAB7-15","Cold & Flu",1,now(),now()),
("IAB7-16","Deafness",1,now(),now()),
("IAB7-17","Dental Care",1,now(),now()),
("IAB7-18","Depression",1,now(),now()),
("IAB7-19","Dermatology",1,now(),now()),
("IAB7-20","Diabetes",1,now(),now()),
("IAB7-21","Epilepsy",1,now(),now()),
("IAB7-22","GERD/Acid Reflux",1,now(),now()),
("IAB7-23","Headaches/Migraines",1,now(),now()),
("IAB7-24","Heart Disease",1,now(),now()),
("IAB7-25","Herbs for Health",1,now(),now()),
("IAB7-26","Holistic Healing",1,now(),now()),
("IAB7-27","IBS/Crohn's Disease",1,now(),now()),
("IAB7-28","Incest/Abuse Support",1,now(),now()),
("IAB7-29","Incontinence",1,now(),now()),
("IAB7-30","Infertility",1,now(),now()),
("IAB7-31","Men's Health",1,now(),now()),
("IAB7-32","Nutrition",1,now(),now()),
("IAB7-33","Orthopedics",1,now(),now()),
("IAB7-34","Panic/Anxiety Disorders",1,now(),now()),
("IAB7-35","Pediatrics",1,now(),now()),
("IAB7-36","Physical Therapy",1,now(),now()),
("IAB7-37","Psychology/Psychiatry",1,now(),now()),
("IAB7-38","Senor Health",1,now(),now()),
("IAB7-39","Sexuality",1,now(),now()),
("IAB7-40","Sleep Disorders",1,now(),now()),
("IAB7-41","Smoking Cessation",1,now(),now()),
("IAB7-42","Substance Abuse",1,now(),now()),
("IAB7-43","Thyroid Disease",1,now(),now()),
("IAB7-44","Weight Loss",1,now(),now()),
("IAB7-45","Women's Health",1,now(),now()),
("IAB8","Food & Drink",1,now(),now()),
("IAB8-1","American Cuisine",1,now(),now()),
("IAB8-2","Barbecues & Grilling",1,now(),now()),
("IAB8-3","Cajun/Creole",1,now(),now()),
("IAB8-4","Chinese Cuisine",1,now(),now()),
("IAB8-5","Cocktails/Beer",1,now(),now()),
("IAB8-6","Coffee/Tea",1,now(),now()),
("IAB8-7","Cuisine-Specific",1,now(),now()),
("IAB8-8","Desserts & Baking",1,now(),now()),
("IAB8-9","Dining Out",1,now(),now()),
("IAB8-10","Food Allergies",1,now(),now()),
("IAB8-11","French Cuisine",1,now(),now()),
("IAB8-12","Health/Lowfat Cooking",1,now(),now()),
("IAB8-13","Italian Cuisine",1,now(),now()),
("IAB8-14","Japanese Cuisine",1,now(),now()),
("IAB8-15","Mexican Cuisine",1,now(),now()),
("IAB8-16","Vegan",1,now(),now()),
("IAB8-17","Vegetarian",1,now(),now()),
("IAB8-18","Wine",1,now(),now()),
("IAB9","Hobbies & Interests",1,now(),now()),
("IAB9-1","Art/Technology",1,now(),now()),
("IAB9-2","Arts & Crafts",1,now(),now()),
("IAB9-3","Beadwork",1,now(),now()),
("IAB9-4","Birdwatching",1,now(),now()),
("IAB9-5","Board Games/Puzzles",1,now(),now()),
("IAB9-6","Candle & Soap Making",1,now(),now()),
("IAB9-7","Card Games",1,now(),now()),
("IAB9-8","Chess",1,now(),now()),
("IAB9-9","Cigars",1,now(),now()),
("IAB9-10","Collecting",1,now(),now()),
("IAB9-11","Comic Books",1,now(),now()),
("IAB9-12","Drawing/Sketching",1,now(),now()),
("IAB9-13","Freelance Writing",1,now(),now()),
("IAB9-14","Genealogy",1,now(),now()),
("IAB9-15","Getting Published",1,now(),now()),
("IAB9-16","Guitar",1,now(),now()),
("IAB9-17","Home Recording",1,now(),now()),
("IAB9-18","Investors & Patents",1,now(),now()),
("IAB9-19","Jewelry Making",1,now(),now()),
("IAB9-20","Magic & Illusion",1,now(),now()),
("IAB9-21","Needlework",1,now(),now()),
("IAB9-22","Painting",1,now(),now()),
("IAB9-23","Photography",1,now(),now()),
("IAB9-24","Radio",1,now(),now()),
("IAB9-25","Roleplaying Games",1,now(),now()),
("IAB9-26","Sci-Fi & Fantasy",1,now(),now()),
("IAB9-27","Scrapbooking",1,now(),now()),
("IAB9-28","Screenwriting",1,now(),now()),
("IAB9-29","Stamps & Coins",1,now(),now()),
("IAB9-30","Video & Computer Games",1,now(),now()),
("IAB9-31","Woodworking",1,now(),now()),
("IAB10","Home & Garden",1,now(),now()),
("IAB10-1","Appliances",1,now(),now()),
("IAB10-2","Entertaining",1,now(),now()),
("IAB10-3","Environmental Safety",1,now(),now()),
("IAB10-4","Gardening",1,now(),now()),
("IAB10-5","Home Repair",1,now(),now()),
("IAB10-6","Home Theater",1,now(),now()),
("IAB10-7","Interior Decorating",1,now(),now()),
("IAB10-8","Landscaping",1,now(),now()),
("IAB10-9","Remodeling & Construction",1,now(),now()),
("IAB11","Law, Gov't & Politics",1,now(),now()),
("IAB11-1","Immigration",1,now(),now()),
("IAB11-2","Legal Issues",1,now(),now()),
("IAB11-3","U.S. Government Resources",1,now(),now()),
("IAB11-4","Politics",1,now(),now()),
("IAB11-5","Commentary",1,now(),now()),
("IAB12","News",1,now(),now()),
("IAB12-1","International News",1,now(),now()),
("IAB12-2","National News",1,now(),now()),
("IAB12-3","Local News",1,now(),now()),
("IAB13","Personal Finance",1,now(),now()),
("IAB13-1","Beginning Investing",1,now(),now()),
("IAB13-2","Credit/Debt & Loans",1,now(),now()),
("IAB13-3","Financial News",1,now(),now()),
("IAB13-4","Financial Planning",1,now(),now()),
("IAB13-5","Hedge Fund",1,now(),now()),
("IAB13-6","Insurance",1,now(),now()),
("IAB13-7","Investing",1,now(),now()),
("IAB13-8","Mutual Funds",1,now(),now()),
("IAB13-9","Options",1,now(),now()),
("IAB13-10","Retirement Planning",1,now(),now()),
("IAB13-11","Stocks",1,now(),now()),
("IAB13-12","Tax Planning",1,now(),now()),
("IAB14","Society",1,now(),now()),
("IAB14-1","Dating",1,now(),now()),
("IAB14-2","Divorce Support",1,now(),now()),
("IAB14-3","Gay Life",1,now(),now()),
("IAB14-4","Marriage",1,now(),now()),
("IAB14-5","Senior Living",1,now(),now()),
("IAB14-6","Teens",1,now(),now()),
("IAB14-7","Weddings",1,now(),now()),
("IAB14-8","Ethnic Specific",1,now(),now()),
("IAB15","Science",1,now(),now()),
("IAB15-1","Astrology",1,now(),now()),
("IAB15-2","Biology",1,now(),now()),
("IAB15-3","Chemistry",1,now(),now()),
("IAB15-4","Geology",1,now(),now()),
("IAB15-5","Paranormal Phenomena",1,now(),now()),
("IAB15-6","Physics",1,now(),now()),
("IAB15-7","Space/Astronomy",1,now(),now()),
("IAB15-8","Geography",1,now(),now()),
("IAB15-9","Botany",1,now(),now()),
("IAB15-10","Weather",1,now(),now()),
("IAB16","Pets",1,now(),now()),
("IAB16-1","Aquariums",1,now(),now()),
("IAB16-2","Birds",1,now(),now()),
("IAB16-3","Cats",1,now(),now()),
("IAB16-4","Dogs",1,now(),now()),
("IAB16-5","Large Animals",1,now(),now()),
("IAB16-6","Reptiles",1,now(),now()),
("IAB16-7","Veterinary Medicine",1,now(),now()),
("IAB17","Sports",1,now(),now()),
("IAB17-1","Auto Racing",1,now(),now()),
("IAB17-2","Baseball",1,now(),now()),
("IAB17-3","Bicycling",1,now(),now()),
("IAB17-4","Bodybuilding",1,now(),now()),
("IAB17-5","Boxing",1,now(),now()),
("IAB17-6","Canoeing/Kayaking",1,now(),now()),
("IAB17-7","Cheerleading",1,now(),now()),
("IAB17-8","Climbing",1,now(),now()),
("IAB17-9","Cricket",1,now(),now()),
("IAB17-10","Figure Skating",1,now(),now()),
("IAB17-11","Fly Fishing",1,now(),now()),
("IAB17-12","Football",1,now(),now()),
("IAB17-13","Freshwater Fishing",1,now(),now()),
("IAB17-14","Game & Fish",1,now(),now()),
("IAB17-15","Golf",1,now(),now()),
("IAB17-16","Horse Racing",1,now(),now()),
("IAB17-17","Horses",1,now(),now()),
("IAB17-18","Hunting/Shooting",1,now(),now()),
("IAB17-19","Inline Skating",1,now(),now()),
("IAB17-20","Martial Arts",1,now(),now()),
("IAB17-21","Mountain Biking",1,now(),now()),
("IAB17-22","NASCAR Racing",1,now(),now()),
("IAB17-23","Olympics",1,now(),now()),
("IAB17-24","Paintball",1,now(),now()),
("IAB17-25","Power & Motorcycles",1,now(),now()),
("IAB17-26","Pro Basketball",1,now(),now()),
("IAB17-27","Pro Ice Hockey",1,now(),now()),
("IAB17-28","Rodeo",1,now(),now()),
("IAB17-29","Rugby",1,now(),now()),
("IAB17-30","Running/Jogging",1,now(),now()),
("IAB17-31","Sailing",1,now(),now()),
("IAB17-32","Saltwater Fishing",1,now(),now()),
("IAB17-33","Scuba Diving",1,now(),now()),
("IAB17-34","Skateboarding",1,now(),now()),
("IAB17-35","Skiing",1,now(),now()),
("IAB17-36","Snowboarding",1,now(),now()),
("IAB17-37","Surfing/Bodyboarding",1,now(),now()),
("IAB17-38","Swimming",1,now(),now()),
("IAB17-39","Table Tennis/Ping-Pong",1,now(),now()),
("IAB17-40","Tennis",1,now(),now()),
("IAB17-41","Volleyball",1,now(),now()),
("IAB17-42","Walking",1,now(),now()),
("IAB17-43","Waterski/Wakeboard",1,now(),now()),
("IAB17-44","World Soccer",1,now(),now()),
("IAB18","Style & Fashion",1,now(),now()),
("IAB18-1","Beauty",1,now(),now()),
("IAB18-2","Body Art",1,now(),now()),
("IAB18-3","Fashion",1,now(),now()),
("IAB18-4","Jewelry",1,now(),now()),
("IAB18-5","Clothing",1,now(),now()),
("IAB18-6","Accessories",1,now(),now()),
("IAB19","Technology & Computing",1,now(),now()),
("IAB19-1","3-D Graphics",1,now(),now()),
("IAB19-2","Animation",1,now(),now()),
("IAB19-3","Antivirus Software",1,now(),now()),
("IAB19-4","C/C++",1,now(),now()),
("IAB19-5","Cameras & Camcorders",1,now(),now()),
("IAB19-6","Cell Phones",1,now(),now()),
("IAB19-7","Computer Certification",1,now(),now()),
("IAB19-8","Computer Networking",1,now(),now()),
("IAB19-9","Computer Peripherals",1,now(),now()),
("IAB19-10","Computer Reviews",1,now(),now()),
("IAB19-11","Data Centers",1,now(),now()),
("IAB19-12","Databases",1,now(),now()),
("IAB19-13","Desktop Publishing",1,now(),now()),
("IAB19-14","Desktop Video",1,now(),now()),
("IAB19-15","Email",1,now(),now()),
("IAB19-16","Graphics Software",1,now(),now()),
("IAB19-17","Home Video/DVD",1,now(),now()),
("IAB19-18","Internet Technology",1,now(),now()),
("IAB19-19","Java",1,now(),now()),
("IAB19-20","JavaScript",1,now(),now()),
("IAB19-21","Mac Support",1,now(),now()),
("IAB19-22","MP3/MIDI",1,now(),now()),
("IAB19-23","Net Conferencing",1,now(),now()),
("IAB19-24","Net for Beginners",1,now(),now()),
("IAB19-25","Network Security",1,now(),now()),
("IAB19-26","Palmtops/PDAs",1,now(),now()),
("IAB19-27","PC Support",1,now(),now()),
("IAB19-28","Portable",1,now(),now()),
("IAB19-29","Entertainment",1,now(),now()),
("IAB19-30","Shareware/Freeware",1,now(),now()),
("IAB19-31","Unix",1,now(),now()),
("IAB19-32","Visual Basic",1,now(),now()),
("IAB19-33","Web Clip Art",1,now(),now()),
("IAB19-34","Web Design/HTML",1,now(),now()),
("IAB19-35","Web Search",1,now(),now()),
("IAB19-36","Windows",1,now(),now()),
("IAB20","Travel",1,now(),now()),
("IAB20-1","Adventure Travel",1,now(),now()),
("IAB20-2","Africa",1,now(),now()),
("IAB20-3","Air Travel",1,now(),now()),
("IAB20-4","Australia & New Zealand",1,now(),now()),
("IAB20-5","Bed & Breakfasts",1,now(),now()),
("IAB20-6","Budget Travel",1,now(),now()),
("IAB20-7","Business Travel",1,now(),now()),
("IAB20-8","By US Locale",1,now(),now()),
("IAB20-9","Camping",1,now(),now()),
("IAB20-10","Canada",1,now(),now()),
("IAB20-11","Caribbean",1,now(),now()),
("IAB20-12","Cruises",1,now(),now()),
("IAB20-13","Eastern Europe",1,now(),now()),
("IAB20-14","Europe",1,now(),now()),
("IAB20-15","France",1,now(),now()),
("IAB20-16","Greece",1,now(),now()),
("IAB20-17","Honeymoons/Getaways",1,now(),now()),
("IAB20-18","Hotels",1,now(),now()),
("IAB20-19","Italy",1,now(),now()),
("IAB20-20","Japan",1,now(),now()),
("IAB20-21","Mexico & Central America",1,now(),now()),
("IAB20-22","National Parks",1,now(),now()),
("IAB20-23","South America",1,now(),now()),
("IAB20-24","Spas",1,now(),now()),
("IAB20-25","Theme Parks",1,now(),now()),
("IAB20-26","Traveling with Kids",1,now(),now()),
("IAB20-27","United Kingdom",1,now(),now()),
("IAB21","Real Estate",1,now(),now()),
("IAB21-1","Apartments",1,now(),now()),
("IAB21-2","Architects",1,now(),now()),
("IAB21-3","Buying/Selling Homes",1,now(),now()),
("IAB22","Shopping",1,now(),now()),
("IAB22-1","Contests & Freebies",1,now(),now()),
("IAB22-2","Couponing",1,now(),now()),
("IAB22-3","Comparison",1,now(),now()),
("IAB22-4","Engines",1,now(),now()),
("IAB23","Religion & Spirituality",1,now(),now()),
("IAB23-1","Alternative Religions",1,now(),now()),
("IAB23-2","Atheism/Agnosticism",1,now(),now()),
("IAB23-3","Buddhism",1,now(),now()),
("IAB23-4","Catholicism",1,now(),now()),
("IAB23-5","Christianity",1,now(),now()),
("IAB23-6","Hinduism",1,now(),now()),
("IAB23-7","Islam",1,now(),now()),
("IAB23-8","Judaism",1,now(),now()),
("IAB23-9","Latter-Day Saints",1,now(),now()),
("IAB23-10","Pagan/Wiccan",1,now(),now()),
("IAB24","Uncategorized",1,now(),now()),
("IAB25","Non-Standard Content",1,now(),now()),
("IAB25-1","Unmoderated UGC",1,now(),now()),
("IAB25-2","Extreme Graphic/Explicit Violence",1,now(),now()),
("IAB25-3","Pornography",1,now(),now()),
("IAB25-4","Profane Content",1,now(),now()),
("IAB25-5","Hate Content",1,now(),now()),
("IAB25-6","Under Construction",1,now(),now()),
("IAB25-7","Incentivized",1,now(),now()),
("IAB26","Illegal Content",1,now(),now()),
("IAB26-1","Illegal Content",1,now(),now()),
("IAB26-2","Warez",1,now(),now()),
("IAB26-3","Spyware/Malware",1,now(),now()),
("IAB26-4","Copyright Infringement",1,now(),now());

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

insert into hygiene_categories(id,name,description,modified_by,created_on,last_modified) values 
(1,'Family Safe','Family safe content',1,now(),now()),
(2,'Mature','Mature content',1,now(),now()),
(3,'Performance','Performance/Subscription content',1,now(),now()),
(4,'Premium','Premium/Brand content',1,now(),now());

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

insert into site_platform (id,name,description,modified_by,created_on,last_modified) values
(1,'Web','Display PC',1,now(),now()),
(2,'Mobile Web','Mobile Web',1,now(),now()),
(3,'Mobile Application','Mobile Application',1,now(),now());

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

insert into app_store (id,name,description,modified_by,created_on,last_modified) values
(1,'App Store','Apple Store',1,now(),now()),
(2,'BlackBerry World','BlackBerry Store',1,now(),now()),
(3,'Google Play','Google Store',1,now(),now()),
(4,'Nokia Store','Nokia Store',1,now(),now()),
(5,'Windows Phone Store','Microsoft',1,now(),now()),
(6,'Windows Store','Microsoft',1,now(),now());

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

insert into site(guid,name,pub_id,pub_guid,site_url,categories_list,hygiene_list,site_platform_id,status_id,last_modified,modified_by) values 
('test_site_guid','xyz',2,'test_pub_guid','kritter.in','[1,2]','[1]',1,1,now(),1);

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

insert into campaign_budget (campaign_id,campaign_guid,internal_total_budget,adv_total_budget,internal_total_burn,adv_total_burn,internal_daily_budget,adv_daily_budget,internal_daily_burn,adv_daily_burn,modified_by,created_on,last_modified) values (1,'test_campaign_guid',1000,1000,0,0,100,100,0,0,1,now(),now());

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
country_code VARCHAR(5) UNIQUE NOT NULL,
country_name VARCHAR(200) UNIQUE NOT NULL,
entity_id_set TEXT NOT NULL, -- [1,2] where 1,2 refers to id column in country table which corresponds ids from multiple data source
modified_on TIMESTAMP NOT NULL
)ENGINE=INNODB;

insert into ui_targeting_country (id,country_code,country_name,entity_id_set,modified_on) values (-1,'None','None','[]',now());

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

insert into ui_targeting_isp (id,country_ui_id,isp_ui_name,entity_id_set,modified_on) values (-1,-1,'None','[]',now());

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


insert into formats_attributes_mapping(creative_formats_id, creative_attributes_id, modified_by) values
(2,1,1),(3,1,1),(4,1,1),
(2,2,1),(3,2,1),(4,2,1),
(3,3,1),
(3,4,1),
(3,5,1),
(3,6,1),
(3,7,1),
(1,8,1),(2,8,1),(3,8,1),
(2,9,1),(3,9,1),(4,9,1),
(2,10,1),(3,10,1),
(1,11,1),(2,11,1),(3,11,1),
(1,12,1),
(3,13,1),
(2,14,1),
(2,15,1),(3,15,1),(4,15,1),
(3,16,1),(4,16,1),
(2,17,1);
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

insert into supply_source_type(id,name) value(2,'WAP');
insert into supply_source_type(id,name) value(3,'APP');

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

insert into reporting_hygiene_categories(id,name,description,modified_by,created_on,last_modified) values
('[1]','Family Safe','Family safe content',1,now(),now()),
('[2]','Mature','Mature content',1,now(),now()),
('[3]','Performance','Performance/Subscription content',1,now(),now()),
('[4]','Premium','Premium/Brand content',1,now(),now());

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

insert into ext_supply_attr (id,site_inc_id,ext_supply_id) values (-1,-1,"-1");

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
state_name VARCHAR(200) NOT NULL,
entity_id_set TEXT NOT NULL, -- [1,2] where 1,2 refers to id column in state table
modified_on TIMESTAMP NOT NULL
)ENGINE=INNODB;

insert into ui_targeting_state (id,country_ui_id,state_name,entity_id_set) values (-1,-1,'None','[]');

-- This table stores cityname along with set of ids generated by different geo datasources for a given cityname.
CREATE TABLE IF NOT EXISTS ui_targeting_city
(
id INTEGER AUTO_INCREMENT PRIMARY KEY NOT NULL,
state_ui_id INTEGER NOT NULL,
city_name VARCHAR(200) NOT NULL,
entity_id_set TEXT NOT NULL, -- [1,2] where 1,2 refers to id column in city table
modified_on TIMESTAMP NOT NULL
)ENGINE=INNODB;

insert into ui_targeting_city (id,state_ui_id,city_name,entity_id_set) values (-1,-1,'None','[]');

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

insert into connection_type_metadata (id, name, created_on, last_modified) values (1, "WiFi", NOW(), NOW());
insert into connection_type_metadata (id, name, created_on, last_modified) values (2, "Carrier", NOW(), NOW());
insert into connection_type_metadata (id, name, created_on, last_modified) values (-1, "Unknown", NOW(), NOW());
insert into connection_type_metadata (id, name, created_on, last_modified) values (0, "All", NOW(), NOW());

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

insert into parent_account_type (id, name,description,created_on,last_modified,is_deprecated) values
(1,'AD-NETWORK','Ad-Network',now(),now(),false),
(2,'ATD','Agency Trading Desk',now(),now(),false),
(3,'PTD','Publisher Trading Desk',now(),now(),false),
(4,'DEFAULT','Default account type where account type not defined',now(),now(),false);

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

insert into parent_account (guid,status,type_id,name) values ('default_parent_account_guid',1,4,'default parent account');

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

insert into account_user_type (id,name,description,created_on,last_modified,is_deprecated) values (1,'Admin','Admin Super User having complete access to demand and supply',now(),now(),false);

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

insert into creative_formats(id,name,description,modified_by,created_on,last_modified,is_deprecated) values(51,"Native","Native Ad",1,now(),now(),false);
insert into creative_attributes(id,value,modified_by,created_on,last_modified) values(51,"Native",1,now(),now());

alter table creative_container add column native_demand_props TEXT  after ext_resource_url;
insert into formats_attributes_mapping(creative_formats_id,creative_attributes_id,last_modified,modified_by) values(51,51,now(),1);

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

insert into formats_attributes_mapping(creative_formats_id,creative_attributes_id,last_modified,modified_by) values(3,17,now(),1);

create table IF NOT EXISTS device_type(
id int default 2 primary key not null,
name varchar(32) not null
)ENGINE=INNODB;

insert into device_type(id,name) value(0,'UNKNOWN');
insert into device_type(id,name) value(1,'DESKTOP');
insert into device_type(id,name) value(2,'MOBILE');

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

insert into account_type (id, name,description,created_on,last_modified,is_deprecated) values
(1,'root','universally super user',now(),now(),false),
(2,'directpublisher','direct publisher having our adcode',now(),now(),false),
(3,'directadvertiser','direct advertiser having demand',now(),now(),false),
(4,'exchange','adexchange who performs auction',now(),now(),false),
(5,'atd','agency trading desk,which buys inventory on behalf of advertisers',now(),now(),false),
(6,'adops','ad operations',now(),now(),false),
(7,'pubops','publisher operations',now(),now(),false),
(8,'pubbd','publisher business developer',now(),now(),false),
(9,'adsales','advertiser sales',now(),now(),false)
ON DUPLICATE KEY UPDATE id = VALUES(id);


insert into status (id,name,description,created_on,last_modified,is_deprecated) values
(1,'Active','Entity is in active status',now(),now(),false),
(2,'Paused','Entity has been paused by the user',now(),now(),false),
(3,'Expired','Entity completion date has passed',now(),now(),false),
(4,'Rejected','Entity has been rejected in the approval process',now(),now(),false),
(5,'Pending','Entity is pending approval',now(),now(),false),
(6,'Experimental','Entity is for experimentation',now(),now(),false),
(7,'Sandbox','Entity is for sandbox testing',now(),now(),false),
(8,'DEBUG','Entity is for debugging purposes',now(),now(),false),
(9,'Approved','Entity is approved',now(),now(),false)
ON DUPLICATE KEY UPDATE id = VALUES(id);


insert into device_type(id,name) values
(0,'UNKNOWN'),
(1,'DESKTOP'),
(2,'MOBILE')
ON DUPLICATE KEY UPDATE id = VALUES(id);


insert into connection_type_metadata (id, name, created_on, last_modified) values
(1, "WiFi", NOW(), NOW()),
(2, "Carrier", NOW(), NOW()),
(-1, "Unknown", NOW(), NOW()),
(0, "All", NOW(), NOW())
ON DUPLICATE KEY UPDATE id = VALUES(id);


insert into account_user_type (id,name,description,created_on,last_modified,is_deprecated) values
(1,'Admin','Admin Super User having complete access to demand and supply',now(),now(),false)
ON DUPLICATE KEY UPDATE id = VALUES(id);


insert into parent_account_type (id, name,description,created_on,last_modified,is_deprecated) values
(1,'AD-NETWORK','Ad-Network',now(),now(),false),
(2,'ATD','Agency Trading Desk',now(),now(),false),
(3,'PTD','Publisher Trading Desk',now(),now(),false),
(4,'DEFAULT','Default account type where account type not defined',now(),now(),false)
ON DUPLICATE KEY UPDATE id = VALUES(id);


insert into parent_account (guid,status,type_id,name) values
('default_parent_account_guid',1,4,'default parent account')
ON DUPLICATE KEY UPDATE id = VALUES(id);


insert into account (guid,status,type_id,name,userid,password,email,address,country,city,phone,modified_by,created_on,last_modified,inventory_source, company_name) values
('root-guid',1,1,'root','admin','$2a$10$O5twVdGKoWvtabDCsivzi.EHJFKUqRQXhM41NMnxJmnFH/Lg4Vn8y','chaharv@gmail.com','private','india','bangalore','999999999',1,now(),now(),1,'root'),
('test_pub_guid',1,2,'test_pub','testpub','$2a$10$O5twVdGKoWvtabDCsivzi.EHJFKUqRQXhM41NMnxJmnFH/Lg4Vn8y','test','india','india','bang','999999999',1,now(),now(),1,'test_pub'),
('test_adv_guid',1,3,'test_adv','testadv','$2a$10$O5twVdGKoWvtabDCsivzi.EHJFKUqRQXhM41NMnxJmnFH/Lg4Vn8y','test','india','india','bang','9999999',1,now(),now(),0, 'test_adv'),
('kritterx',1,2,'kritterx','kritterx','$2a$10$NRxVckXVGU0gzk77jIBIZOgdgUwoNwylRqHERcjBAPKv4pAhFokZu','kritterx@kritter.in','','India','Bangalore','0123456',1,now(),now(),2,'kritterx'),
('adbuddiz',1,2,'adbuddiz','adbuddiz','$2a$10$NRxVckXVGU0gzk77jIBIZOgdgUwoNwylRqHERcjBAPKv4pAhFokZu','adbuddiz@adbuddiz.com','','United States','New York','0123456',1,now(),now(),2,'adbuddiz'),
('adx',1,2,'adx','adx','$2a$10$NRxVckXVGU0gzk77jIBIZOgdgUwoNwylRqHERcjBAPKv4pAhFokZu','adx@adx.com','','United States','New York','0123456',1,now(),now(),2,'adx'),
('amobee',1,2,'amobee','amobee','$2a$10$NRxVckXVGU0gzk77jIBIZOgdgUwoNwylRqHERcjBAPKv4pAhFokZu','amobee@amobee.com','','United States','New York','0123456',1,now(),now(),2,'amobee'),
('appodeal',1,2,'appodeal','appodeal','$2a$10$NRxVckXVGU0gzk77jIBIZOgdgUwoNwylRqHERcjBAPKv4pAhFokZu','appodeal@appodeal.com','','United States','New York','0123456',1,now(),now(),2,'appodeal'),
('axonix',1,2,'axonix','axonix','$2a$10$NRxVckXVGU0gzk77jIBIZOgdgUwoNwylRqHERcjBAPKv4pAhFokZu','axonix@axonix.com','','United States','New York','0123456',1,now(),now(),2,'axonix'),
('bidswitch',1,2,'bidswitch','bidswitch','$2a$10$NRxVckXVGU0gzk77jIBIZOgdgUwoNwylRqHERcjBAPKv4pAhFokZu','bidswitch@bidswitch.com','','United States','New York','0123456',1,now(),now(),2,'bidswitch'),
('mobfox',1,2,'mobfox','mobfox','$2a$10$NRxVckXVGU0gzk77jIBIZOgdgUwoNwylRqHERcjBAPKv4pAhFokZu','mobfox@mobfox.com','','United States','New York','0123456',1,now(),now(),2,'mobfox'),
('mopub',1,2,'mopub','mopub','$2a$10$NRxVckXVGU0gzk77jIBIZOgdgUwoNwylRqHERcjBAPKv4pAhFokZu','mopub@mopub.com','','United States','New York','0123456',1,now(),now(),2,'mopub'),
('nexage',1,2,'nexage','nexage','$2a$10$NRxVckXVGU0gzk77jIBIZOgdgUwoNwylRqHERcjBAPKv4pAhFokZu','nexage@nexage.com','','United States','New York','0123456',1,now(),now(),2,'nexage'),
('openx',1,2,'openx','openx','$2a$10$NRxVckXVGU0gzk77jIBIZOgdgUwoNwylRqHERcjBAPKv4pAhFokZu','openx@openx.com','','United States','New York','0123456',1,now(),now(),2,'openx'),
('opera',1,2,'opera','opera','$2a$10$NRxVckXVGU0gzk77jIBIZOgdgUwoNwylRqHERcjBAPKv4pAhFokZu','opera@opera.com','','United States','New York','0123456',1,now(),now(),2,'opera'),
('pinsight',1,2,'pinsight','pinsight','$2a$10$NRxVckXVGU0gzk77jIBIZOgdgUwoNwylRqHERcjBAPKv4pAhFokZu','pinsight@pinsight.com','','United States','New York','0123456',1,now(),now(),2,'pinsight'),
('pubnative',1,2,'pubnative','pubnative','$2a$10$NRxVckXVGU0gzk77jIBIZOgdgUwoNwylRqHERcjBAPKv4pAhFokZu','pubnative@pubnative.com','','United States','New York','0123456',1,now(),now(),2,'pubnative'),
('reporo',1,2,'reporo','reporo','$2a$10$NRxVckXVGU0gzk77jIBIZOgdgUwoNwylRqHERcjBAPKv4pAhFokZu','reporo@reporo.com','','United States','New York','0123456',1,now(),now(),2,'reporo'),
('rubicon',1,2,'rubicon','rubicon','$2a$10$NRxVckXVGU0gzk77jIBIZOgdgUwoNwylRqHERcjBAPKv4pAhFokZu','rubicon@rubicon.com','','United States','New York','0123456',1,now(),now(),2,'rubicon'),
('smaato',1,2,'smaato','smaato','$2a$10$NRxVckXVGU0gzk77jIBIZOgdgUwoNwylRqHERcjBAPKv4pAhFokZu','smaato@smaato.com','','United States','New York','0123456',1,now(),now(),2,'smaato'),
('smartyads',1,2,'smartyads','smartyads','$2a$10$NRxVckXVGU0gzk77jIBIZOgdgUwoNwylRqHERcjBAPKv4pAhFokZu','smartyads@smartyads.com','','United States','New York','0123456',1,now(),now(),2,'smartyads'),
('spotxchange',1,2,'spotxchange','spotxchange','$2a$10$NRxVckXVGU0gzk77jIBIZOgdgUwoNwylRqHERcjBAPKv4pAhFokZu','spotxchange@spotxchange.com','','United States','New York','0123456',1,now(),now(),2,'spotxchange'),
('startmeapp',1,2,'startmeapp','startmeapp','$2a$10$NRxVckXVGU0gzk77jIBIZOgdgUwoNwylRqHERcjBAPKv4pAhFokZu','startmeapp@startmeapp.com','','United States','New York','0123456',1,now(),now(),2,'startmeapp'),
('taptica',1,2,'taptica','taptica','$2a$10$NRxVckXVGU0gzk77jIBIZOgdgUwoNwylRqHERcjBAPKv4pAhFokZu','taptica@taptica.com','','United States','New York','0123456',1,now(),now(),2,'taptica'),
('tapsense',1,2,'tapsense','tapsense','$2a$10$NRxVckXVGU0gzk77jIBIZOgdgUwoNwylRqHERcjBAPKv4pAhFokZu','tapsense@tapsense.com','','United States','New York','0123456',1,now(),now(),2,'tapsense')
ON DUPLICATE KEY UPDATE guid = VALUES(guid);


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
(21,728,90,'Leaderboard',1,now(),now(),false),
(22,970,90,'Super Leaderboard',1,now(),now(),false),
(23,300,600,'Half Page',1,now(),now(),false),
(24,120,60,'Button 2',1,now(),now(),false),
(25,88,31,'Micro Bar',1,now(),now(),false),
(26,300,250,'In-Banner Video(File Loaded)',1,now(),now(),false),
(27,180,150,'In-Banner Video(File Loaded)',1,now(),now(),false),
(28,160,600,'In-Banner Video(File Loaded)',1,now(),now(),false),
(29,728,90,'In-Banner Video(File Loaded)',1,now(),now(),false),
(30,300,600,'In-Banner Video(File Loaded)',1,now(),now(),false),
(31,300,250,'In-Banner Video(Streaming)',1,now(),now(),false),
(32,180,150,'In-Banner Video(Streaming)',1,now(),now(),false),
(33,160,600,'In-Banner Video(Streaming)',1,now(),now(),false),
(34,728,90,'In-Banner Video(Streaming)',1,now(),now(),false),
(35,300,600,'In-Banner Video(Streaming)',1,now(),now(),false),
(36,300,250,'Pop Ups',1,now(),now(),false),
(37,550,480,'Pop Ups',1,now(),now(),false),
(38,300,600,'',1,now(),now(),false),
(39,0,0,'Floating Variable Initial Dimensions',1,now(),now(),false),
(40,0,0,'Variable Between the page aka Interstitial',1,now(),now(),false)
ON DUPLICATE KEY UPDATE id = VALUES(id);


insert into creative_formats (id,name,description,modified_by,created_on,last_modified,is_deprecated) values
(1,'Text','Text Creative',1,now(),now(),false),
(2,'Banner','Banner Creative',1,now(),now(),false),
(3,'Rich Media','Rich Media Creative',1,now(),now(),false),
(4,'Video','Video Creative',1,now(),now(),false),
(51,"Native","Native Ad",1,now(),now(),false)
ON DUPLICATE KEY UPDATE id = VALUES(id);


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
(17,"Banner Only",1,now(),now()),
(51,"Native",1,now(),now())
ON DUPLICATE KEY UPDATE id = VALUES(id);


insert into creative_banner (guid,account_guid,slot_id,resource_uri,modified_by,last_modified) values
('test_banner_guid','test_adv_guid',6,"test_banner.png",1,now())
ON DUPLICATE KEY UPDATE guid = VALUES(guid);


insert into creative_container (guid,account_guid,label,format_id,creative_attr,text,modified_by,created_on,last_modified,status_id) values
('test_creative_text','test_adv_guid','text_ad_test',1,'[12]','This is test text ad,comes default with installation of the DSP, part of test advertiser account.',1,now(),now(),1),
('test_creative_banner','test_adv_guid','banner_ad_test',2,'[10]','This is test banner ad,comes default with installation of the DSP, part of test advertiser account.',1,now(),now(),1)
ON DUPLICATE KEY update guid = VALUES(guid);


insert into marketplace (id,pricing,description,modified_by,created_on,last_modified,is_deprecated) values
(1,'CPC','Cost per click in dollars',1,now(),now(),false),
(2,'CPM','Cost per thousand impressions in dollars',1,now(),now(),false),
(3,'CPD','Cost per download/Acquisition in dollars',1,now(),now(),false)
ON DUPLICATE KEY UPDATE id = VALUES(id);


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
    ('test_targeting_profile_guid','test_targeting_profile','test_adv_guid',true,'[32,1]','[283]','{"9":"3.2-4.2"}','{"3":"All-All"}',null,null,'[1]',false,'{"TIER1":[],"TIER2":[]}',false,null,1,now(),now()),
    ('test_targeting_profile_guid2','test_targeting_profile2','test_adv_guid',true,null,null,null,null,null,null,'[1]',false,'{"TIER1":[],"TIER2":[]}',false,null,1,now(),now())
    ON DUPLICATE KEY UPDATE guid = VALUES(guid);


insert into campaign (guid,name,account_guid,status_id,start_date,end_date,created_on,modified_by,last_modified) values
('test_campaign_guid','test_campaign','test_adv_guid',1,now(),'2025-05-25 15:16:49',now(),1,now())
ON DUPLICATE KEY UPDATE guid = VALUES(guid);


insert into ad(guid,name,creative_id,creative_guid,landing_url,targeting_guid,campaign_id,campaign_guid,categories_list,hygiene_list,status_id,marketplace_id,internal_max_bid,advertiser_bid,created_on,last_modified,modified_by) values
('test_ad_guid','test_ad_name', 2,'test_creative_banner','http://kritter.in','test_targeting_profile_guid2',1,'test_campaign_guid','{"TIER1":[],"TIER2":[]}','[1]',1,2,0.1,0.2,now(),now(),1)
ON DUPLICATE KEY UPDATE guid = VALUES(guid);


insert into allocation_id (id,definition,description,fraction) values
(1,'EXPERIMENT',null,0.5)
ON DUPLICATE KEY UPDATE id = VALUES(id);


-- All of the IAB categories are included here however we might not show all of them
-- and just show the parent categories (whose code field do not have '-' in them),
-- or we might choose to show few of them,they can be set to be not visible on interface.

insert into content_categories (code,value,modified_by,created_on,last_modified, tier) values
("IAB1","Arts & Entertainment",1,now(),now(),1),
("IAB1-1","Books & Literature",1,now(),now(),2),
("IAB1-2","Celebrity Fan/Gossip",1,now(),now(),2),
("IAB1-3","Fine Art",1,now(),now(),2),
("IAB1-4","Humor",1,now(),now(),2),
("IAB1-5","Movies",1,now(),now(),2),
("IAB1-6","Music",1,now(),now(),2),
("IAB1-7","Television",1,now(),now(),2),
("IAB2","Automotive",1,now(),now(),1),
("IAB2-1","Auto Parts",1,now(),now(),2),
("IAB2-2","Auto Repair",1,now(),now(),2),
("IAB2-3","Buying/Selling Cars",1,now(),now(),2),
("IAB2-4","Car Culture",1,now(),now(),2),
("IAB2-5","Certified Pre-Owned",1,now(),now(),2),
("IAB2-6","Convertible",1,now(),now(),2),
("IAB2-7","Coupe",1,now(),now(),2),
("IAB2-8","Crossover",1,now(),now(),2),
("IAB2-9","Diesel",1,now(),now(),2),
("IAB2-10","Electric Vehicle",1,now(),now(),2),
("IAB2-11","Hatchback",1,now(),now(),2),
("IAB2-12","Hybrid",1,now(),now(),2),
("IAB2-13","Luxury",1,now(),now(),2),
("IAB2-14","MiniVan",1,now(),now(),2),
("IAB2-15","Motorcycles",1,now(),now(),2),
("IAB2-16","Off-Road Vehicles",1,now(),now(),2),
("IAB2-17","Performance Vehicles",1,now(),now(),2),
("IAB2-18","Pickup",1,now(),now(),2),
("IAB2-19","Road-Side Assistance",1,now(),now(),2),
("IAB2-20","Sedan",1,now(),now(),2),
("IAB2-21","Trucks & Accessories",1,now(),now(),2),
("IAB2-22","Vintage Cars",1,now(),now(),2),
("IAB2-23","Wagon",1,now(),now(),2),
("IAB3","Business",1,now(),now(),2),
("IAB3-1","Advertising",1,now(),now(),1),
("IAB3-2","Agriculture",1,now(),now(),2),
("IAB3-3","Biotech/Biomedical",1,now(),now(),2),
("IAB3-4","Business Software",1,now(),now(),2),
("IAB3-5","Construction",1,now(),now(),2),
("IAB3-6","Forestry",1,now(),now(),2),
("IAB3-7","Government",1,now(),now(),2),
("IAB3-8","Green Solutions",1,now(),now(),2),
("IAB3-9","Human Resources",1,now(),now(),2),
("IAB3-10","Logistics",1,now(),now(),2),
("IAB3-11","Marketing",1,now(),now(),2),
("IAB3-12","Metals",1,now(),now(),2),
("IAB4","Careers",1,now(),now(),1),
("IAB4-1","Career Planning",1,now(),now(),2),
("IAB4-2","College",1,now(),now(),2),
("IAB4-3","Financial Aid",1,now(),now(),2),
("IAB4-4","Job Fairs",1,now(),now(),2),
("IAB4-5","Job Search",1,now(),now(),2),
("IAB4-6","Resume Writing/Advice",1,now(),now(),2),
("IAB4-7","Nursing",1,now(),now(),2),
("IAB4-8","Scholarships",1,now(),now(),2),
("IAB4-9","Telecommuting",1,now(),now(),2),
("IAB4-10","U.S. Military",1,now(),now(),2),
("IAB4-11","Career Advice",1,now(),now(),2),
("IAB5","Education",1,now(),now(),1),
("IAB5-1","7-12 Education",1,now(),now(),2),
("IAB5-2","Adult Education",1,now(),now(),2),
("IAB5-3","Art History",1,now(),now(),2),
("IAB5-4","Colledge Administration",1,now(),now(),2),
("IAB5-5","College Life",1,now(),now(),2),
("IAB5-6","Distance Learning",1,now(),now(),2),
("IAB5-7","English as a 2nd Language",1,now(),now(),2),
("IAB5-8","Language Learning",1,now(),now(),2),
("IAB5-9","Graduate School",1,now(),now(),2),
("IAB5-10","Homeschooling",1,now(),now(),2),
("IAB5-11","Homework/Study Tips",1,now(),now(),2),
("IAB5-12","K-6 Educators",1,now(),now(),2),
("IAB5-13","Private School",1,now(),now(),2),
("IAB5-14","Special Education",1,now(),now(),2),
("IAB5-15","Studying Business",1,now(),now(),2),
("IAB6","Family & Parenting",1,now(),now(),1),
("IAB6-1","Adoption",1,now(),now(),2),
("IAB6-2","Babies & Toddlers",1,now(),now(),2),
("IAB6-3","Daycare/Pre School",1,now(),now(),2),
("IAB6-4","Family Internet",1,now(),now(),2),
("IAB6-5","Parenting - K-6 Kids",1,now(),now(),2),
("IAB6-6","Parenting teens",1,now(),now(),2),
("IAB6-7","Pregnancy",1,now(),now(),2),
("IAB6-8","Special Needs Kids",1,now(),now(),2),
("IAB6-9","Eldercare",1,now(),now(),2),
("IAB7","Health & Fitness",1,now(),now(),1),
("IAB7-1","Exercise",1,now(),now(),2),
("IAB7-2","A.D.D.",1,now(),now(),2),
("IAB7-3","AIDS/HIV",1,now(),now(),2),
("IAB7-4","Allergies",1,now(),now(),2),
("IAB7-5","Alternative Medicine",1,now(),now(),2),
("IAB7-6","Arthritis",1,now(),now(),2),
("IAB7-7","Asthma",1,now(),now(),2),
("IAB7-8","Autism/PDD",1,now(),now(),2),
("IAB7-9","Bipolar Disorder",1,now(),now(),2),
("IAB7-10","Brain Tumor",1,now(),now(),2),
("IAB7-11","Cancer",1,now(),now(),2),
("IAB7-12","Cholesterol",1,now(),now(),2),
("IAB7-13","Chronic Fatigue Syndrome",1,now(),now(),2),
("IAB7-14","Chronic Pain",1,now(),now(),2),
("IAB7-15","Cold & Flu",1,now(),now(),2),
("IAB7-16","Deafness",1,now(),now(),2),
("IAB7-17","Dental Care",1,now(),now(),2),
("IAB7-18","Depression",1,now(),now(),2),
("IAB7-19","Dermatology",1,now(),now(),2),
("IAB7-20","Diabetes",1,now(),now(),2),
("IAB7-21","Epilepsy",1,now(),now(),2),
("IAB7-22","GERD/Acid Reflux",1,now(),now(),2),
("IAB7-23","Headaches/Migraines",1,now(),now(),2),
("IAB7-24","Heart Disease",1,now(),now(),2),
("IAB7-25","Herbs for Health",1,now(),now(),2),
("IAB7-26","Holistic Healing",1,now(),now(),2),
("IAB7-27","IBS/Crohn's Disease",1,now(),now(),2),
("IAB7-28","Incest/Abuse Support",1,now(),now(),2),
("IAB7-29","Incontinence",1,now(),now(),2),
("IAB7-30","Infertility",1,now(),now(),2),
("IAB7-31","Men's Health",1,now(),now(),2),
("IAB7-32","Nutrition",1,now(),now(),2),
("IAB7-33","Orthopedics",1,now(),now(),2),
("IAB7-34","Panic/Anxiety Disorders",1,now(),now(),2),
("IAB7-35","Pediatrics",1,now(),now(),2),
("IAB7-36","Physical Therapy",1,now(),now(),2),
("IAB7-37","Psychology/Psychiatry",1,now(),now(),2),
("IAB7-38","Senor Health",1,now(),now(),2),
("IAB7-39","Sexuality",1,now(),now(),2),
("IAB7-40","Sleep Disorders",1,now(),now(),2),
("IAB7-41","Smoking Cessation",1,now(),now(),2),
("IAB7-42","Substance Abuse",1,now(),now(),2),
("IAB7-43","Thyroid Disease",1,now(),now(),2),
("IAB7-44","Weight Loss",1,now(),now(),2),
("IAB7-45","Women's Health",1,now(),now(),2),
("IAB8","Food & Drink",1,now(),now(),1),
("IAB8-1","American Cuisine",1,now(),now(),2),
("IAB8-2","Barbecues & Grilling",1,now(),now(),2),
("IAB8-3","Cajun/Creole",1,now(),now(),2),
("IAB8-4","Chinese Cuisine",1,now(),now(),2),
("IAB8-5","Cocktails/Beer",1,now(),now(),2),
("IAB8-6","Coffee/Tea",1,now(),now(),2),
("IAB8-7","Cuisine-Specific",1,now(),now(),2),
("IAB8-8","Desserts & Baking",1,now(),now(),2),
("IAB8-9","Dining Out",1,now(),now(),2),
("IAB8-10","Food Allergies",1,now(),now(),2),
("IAB8-11","French Cuisine",1,now(),now(),2),
("IAB8-12","Health/Lowfat Cooking",1,now(),now(),2),
("IAB8-13","Italian Cuisine",1,now(),now(),2),
("IAB8-14","Japanese Cuisine",1,now(),now(),2),
("IAB8-15","Mexican Cuisine",1,now(),now(),2),
("IAB8-16","Vegan",1,now(),now(),2),
("IAB8-17","Vegetarian",1,now(),now(),2),
("IAB8-18","Wine",1,now(),now(),2),
("IAB9","Hobbies & Interests",1,now(),now(),1),
("IAB9-1","Art/Technology",1,now(),now(),2),
("IAB9-2","Arts & Crafts",1,now(),now(),2),
("IAB9-3","Beadwork",1,now(),now(),2),
("IAB9-4","Birdwatching",1,now(),now(),2),
("IAB9-5","Board Games/Puzzles",1,now(),now(),2),
("IAB9-6","Candle & Soap Making",1,now(),now(),2),
("IAB9-7","Card Games",1,now(),now(),2),
("IAB9-8","Chess",1,now(),now(),2),
("IAB9-9","Cigars",1,now(),now(),2),
("IAB9-10","Collecting",1,now(),now(),2),
("IAB9-11","Comic Books",1,now(),now(),2),
("IAB9-12","Drawing/Sketching",1,now(),now(),2),
("IAB9-13","Freelance Writing",1,now(),now(),2),
("IAB9-14","Genealogy",1,now(),now(),2),
("IAB9-15","Getting Published",1,now(),now(),2),
("IAB9-16","Guitar",1,now(),now(),2),
("IAB9-17","Home Recording",1,now(),now(),2),
("IAB9-18","Investors & Patents",1,now(),now(),2),
("IAB9-19","Jewelry Making",1,now(),now(),2),
("IAB9-20","Magic & Illusion",1,now(),now(),2),
("IAB9-21","Needlework",1,now(),now(),2),
("IAB9-22","Painting",1,now(),now(),2),
("IAB9-23","Photography",1,now(),now(),2),
("IAB9-24","Radio",1,now(),now(),2),
("IAB9-25","Roleplaying Games",1,now(),now(),2),
("IAB9-26","Sci-Fi & Fantasy",1,now(),now(),2),
("IAB9-27","Scrapbooking",1,now(),now(),2),
("IAB9-28","Screenwriting",1,now(),now(),2),
("IAB9-29","Stamps & Coins",1,now(),now(),2),
("IAB9-30","Video & Computer Games",1,now(),now(),2),
("IAB9-31","Woodworking",1,now(),now(),2),
("IAB10","Home & Garden",1,now(),now(),1),
("IAB10-1","Appliances",1,now(),now(),2),
("IAB10-2","Entertaining",1,now(),now(),2),
("IAB10-3","Environmental Safety",1,now(),now(),2),
("IAB10-4","Gardening",1,now(),now(),2),
("IAB10-5","Home Repair",1,now(),now(),2),
("IAB10-6","Home Theater",1,now(),now(),2),
("IAB10-7","Interior Decorating",1,now(),now(),2),
("IAB10-8","Landscaping",1,now(),now(),2),
("IAB10-9","Remodeling & Construction",1,now(),now(),2),
("IAB11","Law, Gov't & Politics",1,now(),now(),1),
("IAB11-1","Immigration",1,now(),now(),2),
("IAB11-2","Legal Issues",1,now(),now(),2),
("IAB11-3","U.S. Government Resources",1,now(),now(),2),
("IAB11-4","Politics",1,now(),now(),2),
("IAB11-5","Commentary",1,now(),now(),2),
("IAB12","News",1,now(),now(),1),
("IAB12-1","International News",1,now(),now(),2),
("IAB12-2","National News",1,now(),now(),2),
("IAB12-3","Local News",1,now(),now(),2),
("IAB13","Personal Finance",1,now(),now(),1),
("IAB13-1","Beginning Investing",1,now(),now(),2),
("IAB13-2","Credit/Debt & Loans",1,now(),now(),2),
("IAB13-3","Financial News",1,now(),now(),2),
("IAB13-4","Financial Planning",1,now(),now(),2),
("IAB13-5","Hedge Fund",1,now(),now(),2),
("IAB13-6","Insurance",1,now(),now(),2),
("IAB13-7","Investing",1,now(),now(),2),
("IAB13-8","Mutual Funds",1,now(),now(),2),
("IAB13-9","Options",1,now(),now(),2),
("IAB13-10","Retirement Planning",1,now(),now(),2),
("IAB13-11","Stocks",1,now(),now(),2),
("IAB13-12","Tax Planning",1,now(),now(),2),
("IAB14","Society",1,now(),now(),1),
("IAB14-1","Dating",1,now(),now(),2),
("IAB14-2","Divorce Support",1,now(),now(),2),
("IAB14-3","Gay Life",1,now(),now(),2),
("IAB14-4","Marriage",1,now(),now(),2),
("IAB14-5","Senior Living",1,now(),now(),2),
("IAB14-6","Teens",1,now(),now(),2),
("IAB14-7","Weddings",1,now(),now(),2),
("IAB14-8","Ethnic Specific",1,now(),now(),2),
("IAB15","Science",1,now(),now(),1),
("IAB15-1","Astrology",1,now(),now(),2),
("IAB15-2","Biology",1,now(),now(),2),
("IAB15-3","Chemistry",1,now(),now(),2),
("IAB15-4","Geology",1,now(),now(),2),
("IAB15-5","Paranormal Phenomena",1,now(),now(),2),
("IAB15-6","Physics",1,now(),now(),2),
("IAB15-7","Space/Astronomy",1,now(),now(),2),
("IAB15-8","Geography",1,now(),now(),2),
("IAB15-9","Botany",1,now(),now(),2),
("IAB15-10","Weather",1,now(),now(),2),
("IAB16","Pets",1,now(),now(),1),
("IAB16-1","Aquariums",1,now(),now(),2),
("IAB16-2","Birds",1,now(),now(),2),
("IAB16-3","Cats",1,now(),now(),2),
("IAB16-4","Dogs",1,now(),now(),2),
("IAB16-5","Large Animals",1,now(),now(),2),
("IAB16-6","Reptiles",1,now(),now(),2),
("IAB16-7","Veterinary Medicine",1,now(),now(),2),
("IAB17","Sports",1,now(),now(),1),
("IAB17-1","Auto Racing",1,now(),now(),2),
("IAB17-2","Baseball",1,now(),now(),2),
("IAB17-3","Bicycling",1,now(),now(),2),
("IAB17-4","Bodybuilding",1,now(),now(),2),
("IAB17-5","Boxing",1,now(),now(),2),
("IAB17-6","Canoeing/Kayaking",1,now(),now(),2),
("IAB17-7","Cheerleading",1,now(),now(),2),
("IAB17-8","Climbing",1,now(),now(),2),
("IAB17-9","Cricket",1,now(),now(),2),
("IAB17-10","Figure Skating",1,now(),now(),2),
("IAB17-11","Fly Fishing",1,now(),now(),2),
("IAB17-12","Football",1,now(),now(),2),
("IAB17-13","Freshwater Fishing",1,now(),now(),2),
("IAB17-14","Game & Fish",1,now(),now(),2),
("IAB17-15","Golf",1,now(),now(),2),
("IAB17-16","Horse Racing",1,now(),now(),2),
("IAB17-17","Horses",1,now(),now(),2),
("IAB17-18","Hunting/Shooting",1,now(),now(),2),
("IAB17-19","Inline Skating",1,now(),now(),2),
("IAB17-20","Martial Arts",1,now(),now(),2),
("IAB17-21","Mountain Biking",1,now(),now(),2),
("IAB17-22","NASCAR Racing",1,now(),now(),2),
("IAB17-23","Olympics",1,now(),now(),2),
("IAB17-24","Paintball",1,now(),now(),2),
("IAB17-25","Power & Motorcycles",1,now(),now(),2),
("IAB17-26","Pro Basketball",1,now(),now(),2),
("IAB17-27","Pro Ice Hockey",1,now(),now(),2),
("IAB17-28","Rodeo",1,now(),now(),2),
("IAB17-29","Rugby",1,now(),now(),2),
("IAB17-30","Running/Jogging",1,now(),now(),2),
("IAB17-31","Sailing",1,now(),now(),2),
("IAB17-32","Saltwater Fishing",1,now(),now(),2),
("IAB17-33","Scuba Diving",1,now(),now(),2),
("IAB17-34","Skateboarding",1,now(),now(),2),
("IAB17-35","Skiing",1,now(),now(),2),
("IAB17-36","Snowboarding",1,now(),now(),2),
("IAB17-37","Surfing/Bodyboarding",1,now(),now(),2),
("IAB17-38","Swimming",1,now(),now(),2),
("IAB17-39","Table Tennis/Ping-Pong",1,now(),now(),2),
("IAB17-40","Tennis",1,now(),now(),2),
("IAB17-41","Volleyball",1,now(),now(),2),
("IAB17-42","Walking",1,now(),now(),2),
("IAB17-43","Waterski/Wakeboard",1,now(),now(),2),
("IAB17-44","World Soccer",1,now(),now(),2),
("IAB18","Style & Fashion",1,now(),now(),1),
("IAB18-1","Beauty",1,now(),now(),2),
("IAB18-2","Body Art",1,now(),now(),2),
("IAB18-3","Fashion",1,now(),now(),2),
("IAB18-4","Jewelry",1,now(),now(),2),
("IAB18-5","Clothing",1,now(),now(),2),
("IAB18-6","Accessories",1,now(),now(),2),
("IAB19","Technology & Computing",1,now(),now(),1),
("IAB19-1","3-D Graphics",1,now(),now(),2),
("IAB19-2","Animation",1,now(),now(),2),
("IAB19-3","Antivirus Software",1,now(),now(),2),
("IAB19-4","C/C++",1,now(),now(),2),
("IAB19-5","Cameras & Camcorders",1,now(),now(),2),
("IAB19-6","Cell Phones",1,now(),now(),2),
("IAB19-7","Computer Certification",1,now(),now(),2),
("IAB19-8","Computer Networking",1,now(),now(),2),
("IAB19-9","Computer Peripherals",1,now(),now(),2),
("IAB19-10","Computer Reviews",1,now(),now(),2),
("IAB19-11","Data Centers",1,now(),now(),2),
("IAB19-12","Databases",1,now(),now(),2),
("IAB19-13","Desktop Publishing",1,now(),now(),2),
("IAB19-14","Desktop Video",1,now(),now(),2),
("IAB19-15","Email",1,now(),now(),2),
("IAB19-16","Graphics Software",1,now(),now(),2),
("IAB19-17","Home Video/DVD",1,now(),now(),2),
("IAB19-18","Internet Technology",1,now(),now(),2),
("IAB19-19","Java",1,now(),now(),2),
("IAB19-20","JavaScript",1,now(),now(),2),
("IAB19-21","Mac Support",1,now(),now(),2),
("IAB19-22","MP3/MIDI",1,now(),now(),2),
("IAB19-23","Net Conferencing",1,now(),now(),2),
("IAB19-24","Net for Beginners",1,now(),now(),2),
("IAB19-25","Network Security",1,now(),now(),2),
("IAB19-26","Palmtops/PDAs",1,now(),now(),2),
("IAB19-27","PC Support",1,now(),now(),2),
("IAB19-28","Portable",1,now(),now(),2),
("IAB19-29","Entertainment",1,now(),now(),2),
("IAB19-30","Shareware/Freeware",1,now(),now(),2),
("IAB19-31","Unix",1,now(),now(),2),
("IAB19-32","Visual Basic",1,now(),now(),2),
("IAB19-33","Web Clip Art",1,now(),now(),2),
("IAB19-34","Web Design/HTML",1,now(),now(),2),
("IAB19-35","Web Search",1,now(),now(),2),
("IAB19-36","Windows",1,now(),now(),2),
("IAB20","Travel",1,now(),now(),1),
("IAB20-1","Adventure Travel",1,now(),now(),2),
("IAB20-2","Africa",1,now(),now(),2),
("IAB20-3","Air Travel",1,now(),now(),2),
("IAB20-4","Australia & New Zealand",1,now(),now(),2),
("IAB20-5","Bed & Breakfasts",1,now(),now(),2),
("IAB20-6","Budget Travel",1,now(),now(),2),
("IAB20-7","Business Travel",1,now(),now(),2),
("IAB20-8","By US Locale",1,now(),now(),2),
("IAB20-9","Camping",1,now(),now(),2),
("IAB20-10","Canada",1,now(),now(),2),
("IAB20-11","Caribbean",1,now(),now(),2),
("IAB20-12","Cruises",1,now(),now(),2),
("IAB20-13","Eastern Europe",1,now(),now(),2),
("IAB20-14","Europe",1,now(),now(),2),
("IAB20-15","France",1,now(),now(),2),
("IAB20-16","Greece",1,now(),now(),2),
("IAB20-17","Honeymoons/Getaways",1,now(),now(),2),
("IAB20-18","Hotels",1,now(),now(),2),
("IAB20-19","Italy",1,now(),now(),2),
("IAB20-20","Japan",1,now(),now(),2),
("IAB20-21","Mexico & Central America",1,now(),now(),2),
("IAB20-22","National Parks",1,now(),now(),2),
("IAB20-23","South America",1,now(),now(),2),
("IAB20-24","Spas",1,now(),now(),2),
("IAB20-25","Theme Parks",1,now(),now(),2),
("IAB20-26","Traveling with Kids",1,now(),now(),2),
("IAB20-27","United Kingdom",1,now(),now(),2),
("IAB21","Real Estate",1,now(),now(),1),
("IAB21-1","Apartments",1,now(),now(),2),
("IAB21-2","Architects",1,now(),now(),2),
("IAB21-3","Buying/Selling Homes",1,now(),now(),2),
("IAB22","Shopping",1,now(),now(),1),
("IAB22-1","Contests & Freebies",1,now(),now(),2),
("IAB22-2","Couponing",1,now(),now(),2),
("IAB22-3","Comparison",1,now(),now(),2),
("IAB22-4","Engines",1,now(),now(),2),
("IAB23","Religion & Spirituality",1,now(),now(),1),
("IAB23-1","Alternative Religions",1,now(),now(),2),
("IAB23-2","Atheism/Agnosticism",1,now(),now(),2),
("IAB23-3","Buddhism",1,now(),now(),2),
("IAB23-4","Catholicism",1,now(),now(),2),
("IAB23-5","Christianity",1,now(),now(),2),
("IAB23-6","Hinduism",1,now(),now(),2),
("IAB23-7","Islam",1,now(),now(),2),
("IAB23-8","Judaism",1,now(),now(),2),
("IAB23-9","Latter-Day Saints",1,now(),now(),2),
("IAB23-10","Pagan/Wiccan",1,now(),now(),2),
("IAB24","Uncategorized",1,now(),now(),1),
("IAB25","Non-Standard Content",1,now(),now(),1),
("IAB25-1","Unmoderated UGC",1,now(),now(),2),
("IAB25-2","Extreme Graphic/Explicit Violence",1,now(),now(),2),
("IAB25-3","Pornography",1,now(),now(),2),
("IAB25-4","Profane Content",1,now(),now(),2),
("IAB25-5","Hate Content",1,now(),now(),2),
("IAB25-6","Under Construction",1,now(),now(),2),
("IAB25-7","Incentivized",1,now(),now(),2),
("IAB26","Illegal Content",1,now(),now(),2),
("IAB26-1","Illegal Content",1,now(),now(),2),
("IAB26-2","Warez",1,now(),now(),2),
("IAB26-3","Spyware/Malware",1,now(),now(),2),
("IAB26-4","Copyright Infringement",1,now(),now(),2)
ON DUPLICATE KEY UPDATE id = VALUES(id);

insert into hygiene_categories(id,name,description,modified_by,created_on,last_modified) values
(1,'Family Safe','Family safe content',1,now(),now()),
(2,'Mature','Mature content',1,now(),now()),
(3,'Performance','Performance/Subscription content',1,now(),now()),
(4,'Premium','Premium/Brand content',1,now(),now())
ON DUPLICATE KEY UPDATE id = VALUES(id);


insert into site_platform (id,name,description,modified_by,created_on,last_modified) values
(1,'Web','Display PC',1,now(),now()),
(2,'Mobile Web','Mobile Web',1,now(),now()),
(3,'Mobile Application','Mobile Application',1,now(),now())
ON DUPLICATE KEY UPDATE id = VALUES(id);


insert into app_store (id,name,description,modified_by,created_on,last_modified) values
(1,'App Store','Apple Store',1,now(),now()),
(2,'BlackBerry World','BlackBerry Store',1,now(),now()),
(3,'Google Play','Google Store',1,now(),now()),
(4,'Nokia Store','Nokia Store',1,now(),now()),
(5,'Windows Phone Store','Microsoft',1,now(),now()),
(6,'Windows Store','Microsoft',1,now(),now())
ON DUPLICATE KEY UPDATE id = VALUES(id);


INSERT INTO site (guid,name,pub_id,pub_guid,site_url,categories_list,category_list_inc_exc,is_category_list_excluded,hygiene_list,site_platform_id,status_id,last_modified,modified_by,url_exclusion) values
('test_site_guid','xyz',(select id from account where guid='test_pub_guid'),'test_pub_guid','kritter.in','{\"TIER1\":[],\"TIER2\":[]}','{\"TIER1\":[],\"TIER2\":[]}',1,'[1]',1,1,now(),1,''),
('kritterx','kritterx',(select id from account where guid='kritterx'),'kritterx','','{\"TIER1\":[],\"TIER2\":[]}','{\"TIER1\":[],\"TIER2\":[]}',1,'[1]',1,1,now(),1,''),
('adbuddiz','adbuddiz',(select id from account where guid='adbuddiz'),'adbuddiz','','{\"TIER1\":[],\"TIER2\":[]}','{\"TIER1\":[],\"TIER2\":[]}',1,'[1]',1,1,now(),1,''),
('adx','adx',(select id from account where guid='adx'),'adx','','{\"TIER1\":[],\"TIER2\":[]}','{\"TIER1\":[],\"TIER2\":[]}',1,'[1]',1,1,now(),1,''),
('amobee','amobee',(select id from account where guid='amobee'),'amobee','','{\"TIER1\":[],\"TIER2\":[]}','{\"TIER1\":[],\"TIER2\":[]}',1,'[1]',1,1,now(),1,''),
('appodeal','appodeal',(select id from account where guid='appodeal'),'appodeal','','{\"TIER1\":[],\"TIER2\":[]}','{\"TIER1\":[],\"TIER2\":[]}',1,'[1]',1,1,now(),1,''),
('axonix','axonix',(select id from account where guid='axonix'),'axonix','','{\"TIER1\":[],\"TIER2\":[]}','{\"TIER1\":[],\"TIER2\":[]}',1,'[1]',1,1,now(),1,''),
('bidswitch','bidswitch',(select id from account where guid='bidswitch'),'bidswitch','','{\"TIER1\":[],\"TIER2\":[]}','{\"TIER1\":[],\"TIER2\":[]}',1,'[1]',1,1,now(),1,''),
('mobfox','mobfox',(select id from account where guid='mobfox'),'mobfox','','{\"TIER1\":[],\"TIER2\":[]}','{\"TIER1\":[],\"TIER2\":[]}',1,'[1]',1,1,now(),1,''),
('mopub','mopub',(select id from account where guid='mopub'),'mopub','','{\"TIER1\":[],\"TIER2\":[]}','{\"TIER1\":[],\"TIER2\":[]}',1,'[1]',1,1,now(),1,''),
('nexage','nexage',(select id from account where guid='nexage'),'nexage','','{\"TIER1\":[],\"TIER2\":[]}','{\"TIER1\":[],\"TIER2\":[]}',1,'[1]',1,1,now(),1,''),
('openx','openx',(select id from account where guid='openx'),'openx','','{\"TIER1\":[],\"TIER2\":[]}','{\"TIER1\":[],\"TIER2\":[]}',1,'[1]',1,1,now(),1,''),
('opera','opera',(select id from account where guid='opera'),'opera','','{\"TIER1\":[],\"TIER2\":[]}','{\"TIER1\":[],\"TIER2\":[]}',1,'[1]',1,1,now(),1,''),
('pinsight','pinsight',(select id from account where guid='pinsight'),'pinsight','','{\"TIER1\":[],\"TIER2\":[]}','{\"TIER1\":[],\"TIER2\":[]}',1,'[1]',1,1,now(),1,''),
('pubnative','pubnative',(select id from account where guid='pubnative'),'pubnative','','{\"TIER1\":[],\"TIER2\":[]}','{\"TIER1\":[],\"TIER2\":[]}',1,'[1]',1,1,now(),1,''),
('rubicon','rubicon',(select id from account where guid='rubicon'),'rubicon','','{\"TIER1\":[],\"TIER2\":[]}','{\"TIER1\":[],\"TIER2\":[]}',1,'[1]',1,1,now(),1,''),
('reporo','reporo',(select id from account where guid='reporo'),'reporo','','{\"TIER1\":[],\"TIER2\":[]}','{\"TIER1\":[],\"TIER2\":[]}',1,'[1]',1,1,now(),1,''),
('smaato','smaato',(select id from account where guid='smaato'),'smaato','','{\"TIER1\":[],\"TIER2\":[]}','{\"TIER1\":[],\"TIER2\":[]}',1,'[1]',1,1,now(),1,''),
('smartyads','smartyads',(select id from account where guid='smartyads'),'smartyads','','{\"TIER1\":[],\"TIER2\":[]}','{\"TIER1\":[],\"TIER2\":[]}',1, '[1]',1,1,now(),1,''),
('startmeapp','startmeapp',(select id from account where guid='startmeapp'),'startmeapp','','{\"TIER1\":[],\"TIER2\":[]}','{\"TIER1\":[],\"TIER2\":[]}',1,'[1]',1,1,now(),1,''),
('spotxchange','spotxchange',(select id from account where guid='spotxchange'),'spotxchange','','{\"TIER1\":[],\"TIER2\":[]}','{\"TIER1\":[],\"TIER2\":[]}',1,'[1]',1,1,now(),1,''),
('tapsense','tapsense',(select id from account where guid='tapsense'),'tapsense','','{\"TIER1\":[],\"TIER2\":[]}','{\"TIER1\":[],\"TIER2\":[]}',1,'[1]',1,1,now(),1,''),
('taptica','taptica',(select id from account where guid='taptica'),'taptica','','{\"TIER1\":[],\"TIER2\":[]}','{\"TIER1\":[],\"TIER2\":[]}',1,'[1]',1,1,now(),1,'')
ON DUPLICATE KEY UPDATE guid = VALUES(guid);


insert into campaign_budget (campaign_id,campaign_guid,internal_total_budget,adv_total_budget,internal_total_burn,adv_total_burn,internal_daily_budget,adv_daily_budget,internal_daily_burn,adv_daily_burn,modified_by,created_on,last_modified) values
(1,'test_campaign_guid',1000,1000,0,0,100,100,0,0,1,now(),now())
ON DUPLICATE KEY UPDATE campaign_id = VALUES(campaign_id);


insert into ui_targeting_country (id,country_code,country_name,entity_id_set,modified_on) values
(-1,'None','None','[]',now())
ON DUPLICATE KEY UPDATE country_code = VALUES(country_code);


insert into ui_targeting_isp (id,country_ui_id,isp_ui_name,entity_id_set,modified_on) values
(-1,-1,'None','[]',now())
ON DUPLICATE KEY UPDATE id = VALUES(id);


insert into ui_targeting_state (id,country_ui_id,state_name,entity_id_set) values
(-1,-1,'None','[]')
ON DUPLICATE KEY UPDATE id = VALUES(id);


insert into ui_targeting_city (id,state_ui_id,city_name,entity_id_set) values
(-1,-1,'None','[]')
ON DUPLICATE KEY UPDATE id = VALUES(id);


insert into formats_attributes_mapping(creative_formats_id, creative_attributes_id, modified_by) values
(2,1,1),
(3,1,1),
(4,1,1),
(2,2,1),
(3,2,1),
(4,2,1),
(3,3,1),
(3,4,1),
(3,5,1),
(3,6,1),
(3,7,1),
(1,8,1),
(2,8,1),
(3,8,1),
(2,9,1),
(3,9,1),
(4,9,1),
(2,10,1),
(3,10,1),
(1,11,1),
(2,11,1),
(3,11,1),
(1,12,1),
(3,13,1),
(2,14,1),
(2,15,1),
(3,15,1),
(4,15,1),
(3,16,1),
(4,16,1),
(2,17,1),
(3,17,1),
(51,51,1)
ON DUPLICATE KEY UPDATE creative_formats_id = VALUES(creative_formats_id);



insert into supply_source_type(id,name) values
(2,'WAP'),
(3,'APP')
ON DUPLICATE KEY UPDATE id = VALUES(id);


insert into reporting_hygiene_categories(id,name,description,modified_by,created_on,last_modified) values
('[1]','Family Safe','Family safe content',1,now(),now()),
('[2]','Mature','Mature content',1,now(),now()),
('[3]','Performance','Performance/Subscription content',1,now(),now()),
('[4]','Premium','Premium/Brand content',1,now(),now())
ON DUPLICATE KEY UPDATE id = VALUES(id);


insert into ext_supply_attr (id,site_inc_id,ext_supply_id) values
(-1,-1,"-1")
ON DUPLICATE KEY UPDATE id = VALUES(id);


insert into account (guid,status,type_id,name,userid,password,email,address,country,city,phone,modified_by,created_on,last_modified, inventory_source, company_name) values
('tencent',1,2,'tencent','tencent','$2a$10$NRxVckXVGU0gzk77jIBIZOgdgUwoNwylRqHERcjBAPKv4pAhFokZu','tencent@tencent.com','','China','Shanghai','0123456',1,now(),now(),2,'tencent')
ON DUPLICATE KEY UPDATE id = VALUES(id);

INSERT INTO site (guid,name,pub_id,pub_guid,site_url,categories_list,category_list_inc_exc,is_category_list_excluded,hygiene_list,site_platform_id,status_id,last_modified,modified_by,url_exclusion) values
('tencent','tencent',(select id from account where guid='tencent'),'tencent','','{\"TIER1\":[],\"TIER2\":[]}','{\"TIER1\":[],\"TIER2\":[]}',1,'[1]',1,1,now(),1,'')
ON DUPLICATE KEY UPDATE id = VALUES(id);

insert into account (guid,status,type_id,name,userid,password,email,address,country,city,phone,modified_by,created_on,last_modified, inventory_source, company_name) values
('youku',1,2,'youku','youku','$2a$10$NRxVckXVGU0gzk77jIBIZOgdgUwoNwylRqHERcjBAPKv4pAhFokZu','youku@youku.com','','China','Shanghai','0123456',1,now(),now(),2,'youku')
ON DUPLICATE KEY UPDATE id = VALUES(id);

INSERT INTO site (guid,name,pub_id,pub_guid,site_url,categories_list,category_list_inc_exc,is_category_list_excluded,hygiene_list,site_platform_id,status_id,last_modified,modified_by,url_exclusion) values
('youku','youku',(select id from account where guid='youku'),'youku','','{\"TIER1\":[],\"TIER2\":[]}','{\"TIER1\":[],\"TIER2\":[]}',1,'[1]',1,1,now(),1,'')
ON DUPLICATE KEY UPDATE id = VALUES(id);

insert into creative_attributes (id,value,modified_by,created_on,last_modified) values
(72,'Video Only',1,now(),now())
ON DUPLICATE KEY UPDATE id = VALUES(id);

insert into formats_attributes_mapping(creative_formats_id, creative_attributes_id, modified_by) values
(4,72, 1)
ON DUPLICATE KEY UPDATE creative_formats_id = VALUES(creative_formats_id);

insert into marketplace (id,pricing,description,modified_by,created_on,last_modified,is_deprecated) values
(-1,'NF','No Ad Found for Request',1,now(),now(),false)
ON DUPLICATE KEY UPDATE id = VALUES(id);

insert into inclusion_exclusion_type (id, name) values
(0, "None"),
(1, "Inclusion"),
(2, "Exclusion")
ON DUPLICATE KEY UPDATE id = VALUES(id);

INSERT INTO account (guid,STATUS,type_id,NAME,userid,PASSWORD,email,address,country,city,phone,modified_by,created_on,last_modified, inventory_source, company_name) VALUES
    ('cloudcross',1,2,'cloudcross','cloudcross','$2a$10$NRxVckXVGU0gzk77jIBIZOgdgUwoNwylRqHERcjBAPKv4pAhFokZu','cloudcross@cloudcross.com','','China','Shanghai','0123456',1,NOW(),NOW(),2,'cloudcross')
ON DUPLICATE KEY UPDATE id = VALUES(id);

INSERT INTO site (guid,NAME,pub_id,pub_guid,site_url,categories_list,category_list_inc_exc,is_category_list_excluded,hygiene_list,site_platform_id,status_id,last_modified,modified_by,url_exclusion) VALUES
    ('cloudcross','cloudcross',(SELECT id FROM account WHERE guid='cloudcross'),'cloudcross','','{\"TIER1\":[],\"TIER2\":[]}','{\"TIER1\":[],\"TIER2\":[]}',1,'[1]',1,1,NOW(),1,'')
ON DUPLICATE KEY UPDATE id = VALUES(id);

insert into payout_threshold_metadata (name, value) values
("campaign_absolute_payout_threshold", 2.5),
("campaign_percentage_payout_threshold", 0.10)
ON DUPLICATE KEY UPDATE name = VALUES(name);

update payout_threshold_metadata set value = 25 where name = "campaign_percentage_payout_threshold";
update campaign set last_modified = now();

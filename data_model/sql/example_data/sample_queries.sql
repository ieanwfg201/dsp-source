insert into creative_container (guid,account_id,label,format_id,creative_attr,text,resource_uri,slot_id,html_content,modified_by,created_on,last_modified) values ('test_creative_guid','test_adv_guid','testbannerad',1,'{1}','test creative test text',null,1,null,3,now(),now());

insert into targeting_profile (targeting_profile_guid,targeting_name,account_id,brand_list,model_list,os_json,browser_json,country_list,carrier_list,state_list,city_list,zipcode_list,site_list,is_site_list_excluded,category_list,is_category_list_excluded,profile_modified_by,profile_created_on,profile_last_modified) values ('targeting_profile_test_guid','test targeting profile','test_adv_guid','{}','{}',null,null,null,'{}','{}','{}','{}','{}',false,'{}',false,3,now(),now());

insert into campaign (guid,name,account_id,status_id,start_date,end_date,created_on,modified_by,last_modified) values ('test_campaign_guid','test campaign','test_adv_guid',1,now(),now() + INTERVAL 15 DAY,now(),3,now());

insert into ad (guid,name,creative_id,creative_guid,landing_url,targeting_guid,campaign_id,campaign_guid,status_id,marketplace_id,internal_max_bid,advertiser_bid,created_on,last_modified,modified_by) values ('test_ad_guid','test_text_ad',1,'test_creative_guid','http://kritter.in','targeting_profile_test_guid',1,'test_campaign_guid',1,1,0.03,0.03,now(),now(),3);

insert into site (guid,name,pub_id,site_url,app_id,app_store_id,categories_list,site_platform_id,status_id,created_on,last_modified,modified_by) values ('test_site_guid','test_site','test_pub_guid','http://kritter.in',null,null,'{1}',1,1,now(),now(),1);

insert into account_budget (guid,internal_balance,internal_burn,adv_balance,adv_burn,modified_by,created_on,last_modified) values ('test_adv_guid',3000,300,3000,2700,3,now(),now());

insert into campaign_budget (campaign_inc_id,campaign_id,internal_total_budget,adv_total_budget,internal_total_remaining_budget,adv_total_remaining_budget,internal_daily_budget,adv_daily_budget,internal_daily_remaining_budget,adv_daily_remaining_budget,modified_by,created_on,last_modified) values (1,'test_campaign_guid',3000,3000,2700,2700,3000,3000,2700,2700,3,now(),now());

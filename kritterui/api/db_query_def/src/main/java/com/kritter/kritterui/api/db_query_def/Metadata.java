package com.kritter.kritterui.api.db_query_def;

public class Metadata {
    public static final String get_status = "select id as id, name as name from status";
    public static final String get_tier_1_category = "select id as id, value as name, code as description from content_categories where tier=1";
    public static final String get_tier_2_category = "select id as id, value as name, code as description from content_categories where tier=2";
    public static final String get_app_store_id = "select id as id, name as name from app_store";
    public static final String get_ui_country = "select id as id, country_name as name,entity_id_set as entity_id_set from ui_targeting_country where id != -1 order by country_name";
    public static final String get_isp_country = "select id as id, isp_ui_name as name , entity_id_set as entity_id_set from ui_targeting_isp " +
            " where country_ui_id in (<id>)";
    public static final String get_isp_all_country = "select id as id, isp_ui_name as name , entity_id_set as entity_id_set from ui_targeting_isp where id != -1 order by isp_ui_name";


    public static final String get_creative_slots = "select id as id, concat_ws('*', width, height) as name,description as description from creative_slots";
    public static final String get_creative_formats = "select id as id, name as name from creative_formats";
    public static final String get_creative_attributes = "select id as id, value as name from creative_attributes";
    public static final String get_handset_manufacturer = "select manufacturer_id as id, manufacturer_name as name from handset_manufacturer";
    public static final String get_handset_manufacturer_by_os = "select manufacturer_id as id, manufacturer_name as name from handset_manufacturer where manufacturer_id in " +
            "(select distinct(manufacturer_id) from handset_detection_data where device_os_id in (<id>))";
    public static final String get_handset_manufacturer_by_all_os = "select manufacturer_id as id, manufacturer_name as name from handset_manufacturer where manufacturer_id in " +
            "(select distinct(manufacturer_id) from handset_detection_data)";

    public static final String get_handset_model_by_manufacturer = "select model_id as id, model_name as name from handset_model where manufacturer_id in (<id>)";
    public static final String get_handset_model_by_all_manufacturer = "select model_id as id, model_name as name from handset_model";
    public static final String get_handset_os = "select os_id as id, os_name as name,os_versions as version from handset_os";
    public static final String get_handset_browser = "select browser_id as id, browser_name as name,browser_versions as version from handset_browser";


    public static final String get_status_by_id = "select id as id, name as name from status where id in (<id>)";
    public static final String get_category_by_id = "select id as id, value as name, code as description from content_categories where id in (<id>)";
    public static final String get_app_store_id_by_id = "select id as id, name as name from app_store where id in (<id>)";
    public static final String get_ui_country_by_id = "select id as id, country_name as name,entity_id_set as entity_id_set from ui_targeting_country where id in (<id>)";
    public static final String get_all_isp_by_id = "select id as id, isp_ui_name as name,entity_id_set as entity_id_set from ui_targeting_isp where id in (<id>)";
    public static final String get_creative_slots_by_id = "select id as id, concat_ws('*', width, height) as name,description as description from creative_slots where id in (<id>)";
    public static final String get_creative_formats_by_id = "select id as id, name as name from creative_formats where id in (<id>)";
    public static final String get_creative_attributes_by_id = "select id as id, value as name from creative_attributes where id in (<id>)";
    public static final String get_creative_attributes_by_format_id = "select a.id as id, a.value as name from creative_attributes as a," +
            "formats_attributes_mapping as b where b.creative_formats_id in (<id>)  and a.id=b.creative_attributes_id";
    public static final String get_handset_manufacturer_by_id = "select manufacturer_id as id, manufacturer_name as name from handset_manufacturer where manufacturer_id in (<id>)";
    public static final String get_handset_model_by_id = "select model_id as id, model_name as name from handset_model where model_id in (<id>)";
    public static final String get_handset_os_by_id = "select os_id as id, os_name as name,os_versions as version from handset_os where os_id in (<id>)";
    public static final String get_handset_browser_by_id = "select browser_id as id, browser_name as name,browser_versions as version from handset_browser where browser_id in (<id>)";
    public static final String get_site_by_id = "select id as id, name from site where id in (<id>)";
    public static final String get_ext_site_by_id = "select id as id, ext_supply_name as name from ext_supply_attr where id in (<id>)";
    public static final String get_pub_by_id = "select id as id, name from account where id in (<id>)";
    public static final String get_pub_by_guid = "select id as id, name from account where guid in (<id>)";
    public static final String get_data_for_isp_mappings_insertion = "select C.country_name as country_name,I.isp_name as isp_name,I.data_source_name as data_source_name from isp as I,isp_mappings as M,country as C ,(select concat(C.country_name as country_name,'|',I.isp_name as isp_name) as country_ispname_val from isp as I,isp_mappings as M,country as C where I.country_id = C.id and I.isp_name = M.isp_name and C.country_name = M.country_name) as T where I.country_id = C.id and concat(C.country_name,'|',I.isp_name) != T.country_ispname_val limit ?,?";
    public static final String get_isp_mappings_data_for_edit = "select country_name as country_name,isp_ui_name as isp_ui_name,isp_name as isp_name from isp_mappings  limit ?,?";
    public static final String insert_into_isp_mappings = "insert into isp_mappings (country_name,isp_ui_name,isp_name) values (?,?,?)";
    public static final String update_isp_mappings_row = "update isp_mappings set isp_ui_name = ? where country_name = ? and isp_name = ?";

    public static final String get_active_advertiser_list = "select id as id, name as name from account where status=1 and type_id=3";
    public static final String get_account_as_meta_by_id = "select id as id, name as name from account where id in (<id>)";
    public static final String get_campaign_as_meta_by_id = "select id as id, name as name from campaign where id in (<id>)";
    public static final String get_state_by_country_ui_ids = "select id as id, state_name as name from ui_targeting_state where country_ui_id in (<id>)";
    public static final String get_city_by_state_ui_ids = "select id as id, city_name as name from ui_targeting_city where state_ui_id in (<id>)";
    public static final String get_state_by_ui_ids = "select id as id, state_name as name from ui_targeting_state where id in (<id>)";
    public static final String get_city_by_ui_ids = "select id as id, city_name as name from ui_targeting_city where id in (<id>)";
    public static final String mma_category_tier1_all = "select id as id, name as name from ui_mma_category where tier=1 and mma_type=1";
    public static final String mma_category_tier2_by_tier1 = "select b3.id as id, b3.name as name from mma_code_mma_ui_mapping as b1,"
            + "(select a1.code as code from mma_categories as a1 where a1.mma_type=1 and a1.parent_code in "
            + "(select  c.code as code from ui_mma_category as a, mma_code_mma_ui_mapping as b, "
            + "mma_categories as c where a.id=b.ui_id and b.code=c.code and a.mma_type=1 and c.mma_type=1 and a.id in (<id>))) b2, ui_mma_category b3 where "
            + "b1.code=b2.code and b3.id=b1.ui_id";
    public static final String mma_category_by_ids = "select id as id, name as name from ui_mma_category where id in (<id>) and mma_type=1";
    public static final String mma_industry_tier1_all = "select id as id, name as name from ui_mma_category where tier=1 and mma_type=2";
    public static final String mma_industry_tier2_all = "select id as id, name as name from ui_mma_category where tier=2 and mma_type=2";
    public static final String mma_industry_tier2_by_tier1 = "select b3.id as id, b3.name as name from mma_code_mma_ui_mapping as b1,"
            + "(select a1.code as code from mma_categories as a1 where a1.mma_type=2 and a1.parent_code in "
            + "(select  c.code as code from ui_mma_category as a, mma_code_mma_ui_mapping as b, "
            + "mma_categories as c where a.id=b.ui_id and b.code=c.code and a.mma_type=2 and c.mma_type=2 and a.id in (<id>))) b2, ui_mma_category b3 where "
            + "b1.code=b2.code and b3.id=b1.ui_id";
    public static final String mma_industry_by_ids = "select id as id, name as name from ui_mma_category where id in (<id>) and mma_type=2";
    public static final String adpos_all = "select a.internalid as id, CONCAT(b.name,'-',a.name) as name, a.description as description from ad_position as a, account as b where a.pubIncId=b.id";
    public static final String adpos_by_ids = "select a.internalid as id, CONCAT(b.name,'-',a.name) as name, a.description as description from ad_position as a, account as b where a.pubIncId=b.id and a.internalid in (<id>)";
    public static final String adpos_by_pubids = "select a.internalid as id, CONCAT(b.name,'-',a.name) as name, a.description as description from ad_position as a, account as b where a.pubIncId=b.id and a.pubIncId in (<id>)";
    public static final String channel_tier1_all = "select internalid as id, CONCAT(exchangename,'-',channelname) as name from channel where tier=1";
    public static final String channel_tier2_by_tier1 = "select internalid as id , CONCAT(exchangename,'-',channelname) as name from channel " +
            "where tier=2 and parentcode in (select channelcode from channel where internalid in (<id>))";
    public static final String channel_by_ids = "select internalid as id, CONCAT(exchangename,'-',channelname) as name from channel where internalid in (<id>)";
    public static final String channel_by_pubids = "select a.internalid as id, CONCAT(b.exchangename,'-',a.channelname) as name from channel as a, mma_exchangename_id_mapping as b where a.exchangename=b.exchangename and b.exchangeid in (<id>)";
    public static final String adxbasedexchanges_metadata = " select id as id,name as name from account where inventory_source=2 and adxbased=true";

    public static final String adpositionget_adxbasedexchanges = " select a.pubIncId as id, b.name as name from adxbasedexchanges_metadata as a, account as b where a.pubIncId=b.id and adposition_get=true";
    public static final String advertiserupload_adxbasedexchanges = "select a.pubIncId as id, b.name as name from adxbasedexchanges_metadata as a, account as b where a.pubIncId=b.id and advertiser_upload=true";
    public static final String bannerupload_adxbasedexchanges = "select a.pubIncId as id, b.name as name from adxbasedexchanges_metadata as a, account as b where a.pubIncId=b.id and banner_upload=true";
    public static final String videoupload_adxbasedexchanges = "select a.pubIncId as id, b.name as name from adxbasedexchanges_metadata as a, account as b where a.pubIncId=b.id and video_upload=true";
    public static final String aduience_by_deleted = "SELECT m1. CODE AS pCode , m2. CODE AS cCode , m1.`name` AS pName , m2.`name` AS cName , m3.`code` AS sCode , m3.`name` AS sName FROM audience_lov AS m1 , audience_lov AS m2 , audience_source AS m3 WHERE m1.`code` = m2.parentCode AND LOCATE(m3.`code` , m2.source_type) > 0";
    public static final String get_aduiences = "SELECT * FROM audience WHERE account_guid =? AND deleted = 0 AND type = 1";
    public static final String get_aduience_package = "SELECT * FROM audience WHERE account_guid =? AND type = 2 AND deleted = 0";
}
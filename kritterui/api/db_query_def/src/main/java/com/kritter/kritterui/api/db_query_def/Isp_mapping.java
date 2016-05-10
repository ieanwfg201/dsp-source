package com.kritter.kritterui.api.db_query_def;

public class Isp_mapping {

    public static final String insert_isp_mapping = "insert into isp_mappings(country_name,isp_ui_name," +
    		"isp_name,is_marked_for_deletion,modified_on) values (?,?,?,?,?)";

    public static final String get_mappings_by_country = "select d.id as id, d.country_name as country_name, d.data_source_name as data_source_name," +
    		"d.isp_name as isp_name,c.isp_ui_name as isp_ui_name,c.is_marked_for_deletion,c.id as isp_mapping_id,c.modified_on as modified_on " +
    		"from (select a.id as id, a.country_name as country_name, a.data_source_name as data_source_name,b.isp_name as isp_name  " +
    		"from country as a,isp as b where a.id=b.country_id and FIND_IN_SET(a.id, (select REPLACE(REPLACE(entity_id_set,']',''),'[','') as " +
    		"id from ui_targeting_country where id in (<id>))) != 0) as d LEFT JOIN isp_mappings as c on " +
    		"(d.country_name=c.country_name and d.isp_name=c.isp_name) order by isp_ui_name ";
    
    public static final String mark_delete = "update isp_mappings set is_marked_for_deletion=true,modified_on=?  where id =?";
    
    public static final String get_mapped_isp_by_country = "select d.id as id, d.country_name as country_name, d.data_source_name as " +
    		"data_source_name,d.isp_name as isp_name,c.isp_ui_name as isp_ui_name,c.is_marked_for_deletion,c.id as isp_mapping_id, c.modified_on as modified_on" +
    		" from " +
    		"(select a.id as id, a.country_name as country_name, a.data_source_name as data_source_name,b.isp_name as isp_name  " +
    		"from country as a,isp as b where a.id=b.country_id and FIND_IN_SET(a.id, (select REPLACE(REPLACE(entity_id_set,']',''),'[','') as " +
    		"id from ui_targeting_country where id in (<id>))) != 0) as d LEFT JOIN isp_mappings as c on " +
    		"(d.country_name=c.country_name and d.isp_name=c.isp_name) where c.is_marked_for_deletion= false order by isp_ui_name" ;

    public static final String get_rejected_isp_mapping_by_country = "select d.id as id, d.country_name as country_name, d.data_source_name as " +
            "data_source_name,d.isp_name as isp_name,c.isp_ui_name as isp_ui_name,c.is_marked_for_deletion,c.id as isp_mapping_id, c.modified_on as modified_on" +
            " from " +
            "(select a.id as id, a.country_name as country_name, a.data_source_name as data_source_name,b.isp_name as isp_name  " +
            "from country as a,isp as b where a.id=b.country_id and FIND_IN_SET(a.id, (select REPLACE(REPLACE(entity_id_set,']',''),'[','') as " +
            "id from ui_targeting_country where id in (<id>))) != 0) as d LEFT JOIN isp_mappings as c on " +
            "(d.country_name=c.country_name and d.isp_name=c.isp_name) where c.is_marked_for_deletion= true order by isp_ui_name" ;
    
}

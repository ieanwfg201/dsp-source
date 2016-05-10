package com.kritter.kritterui.api.db_query_def;

public class Ext_site {
    public static final String get_ext_site_by_pub  = "select a.id as pub_inc_id,c.id as id, c.site_inc_id as site_inc_id, " +
    		"c.ext_supply_id as ext_supply_id, c.ext_supply_name as ext_supply_name, c.ext_supply_domain as ext_supply_domain, " +
    		"c.last_modified as last_modified, c.req as req, c.approved as approved, c.unapproved as unapproved, " +
    		"c.supply_source_type as supply_source_type, c.ext_supply_url as ext_supply_url, c.osId as osId" +
    		" from account as a," +
    		"site as b, ext_supply_attr as c where a.id in (<id>) and " +
    		"a.id=b.pub_id and b.id=c.site_inc_id and c.approved=true and c.unapproved=false order by c.ext_supply_name limit ?,?";

    public static final String get_ext_site_by_site  = "select b.id as id, b.site_inc_id as site_inc_id, " +
    		"b.ext_supply_id as ext_supply_id, b.ext_supply_name as ext_supply_name, " +
    		"b.ext_supply_domain as ext_supply_domain, b.last_modified as last_modified, b.req as req," +
    		"b.approved as approved, b.unapproved as unapproved, b.supply_source_type as supply_source_type, b.ext_supply_url as ext_supply_url" +
    		", b.osId as osId from site as a, ext_supply_attr as b " +
    		"where a.id in (<id>) and a.id=b.site_inc_id and b.approved=true order by b.ext_supply_name limit ?,?;";

    public static final String get_unapproved_ext_site  = "select * from ext_supply_attr where approved=false and unapproved=false " +
    		"order by req desc limit ?,?";

    public static final String get_unapproved_ext_site_by_pub  = "select a.id as pub_inc_id,c.id as id, c.site_inc_id as site_inc_id, " +
            "c.ext_supply_id as ext_supply_id, c.ext_supply_name as ext_supply_name, c.ext_supply_domain as ext_supply_domain, " +
            "c.last_modified as last_modified, c.req as req, c.approved as approved, c.unapproved as unapproved, " +
            "c.supply_source_type as supply_source_type, c.ext_supply_url as ext_supply_url, c.osId as osId from account as a," +
            "site as b, ext_supply_attr as c where a.id in (<id>) and " +
            "a.id=b.pub_id and b.id=c.site_inc_id and c.approved=false and c.unapproved=false order by c.ext_supply_name limit ?,?";

    public static final String get_approved_ext_site_by_pub  = "select a.id as pub_inc_id,c.id as id, c.site_inc_id as site_inc_id, " +
            "c.ext_supply_id as ext_supply_id, c.ext_supply_name as ext_supply_name, c.ext_supply_domain as ext_supply_domain, " +
            "c.last_modified as last_modified, c.req as req, c.approved as approved, c.unapproved as unapproved, " +
            "c.supply_source_type as supply_source_type, c.ext_supply_url as ext_supply_url, c.osId as osId from account as a," +
            "site as b, ext_supply_attr as c where a.id in (<id>) and " +
            "a.id=b.pub_id and b.id=c.site_inc_id and c.approved=true and c.unapproved=false order by c.ext_supply_name limit ?,?";

    public static final String get_rejected_ext_site_by_pub  = "select a.id as pub_inc_id,c.id as id, c.site_inc_id as site_inc_id, " +
            "c.ext_supply_id as ext_supply_id, c.ext_supply_name as ext_supply_name, c.ext_supply_domain as ext_supply_domain, " +
            "c.last_modified as last_modified, c.req as req, c.approved as approved, c.unapproved as unapproved, " +
            "c.supply_source_type as supply_source_type, c.ext_supply_url as ext_supply_url, c.osId as osId from account as a," +
            "site as b, ext_supply_attr as c where a.id in (<id>) and " +
            "a.id=b.pub_id and b.id=c.site_inc_id and c.unapproved=true order by c.ext_supply_name limit ?,?";
    
    
    public static final String get_approved_ext_site  = "select * from ext_supply_attr where approved=true and unapproved=false " +
            "order by ext_supply_name limit ?,?";

    public static final String get_rejected_ext_site  = "select * from ext_supply_attr where unapproved=true " +
            "order by ext_supply_name limit ?,?";
    
    public static final String aprrove_ext_site_by_ids = "update ext_supply_attr set approved=true,unapproved=false,last_modified=? where id in (<id>)";
    
    public static final String unaprrove_ext_site_by_ids = "update ext_supply_attr set unapproved=true,last_modified=? where id in (<id>)";

    public static final String get_unapproved_ext_site_by_pub_os  = "select a.id as pub_inc_id,c.id as id, c.site_inc_id as site_inc_id, " +
            "c.ext_supply_id as ext_supply_id, c.ext_supply_name as ext_supply_name, c.ext_supply_domain as ext_supply_domain, " +
            "c.last_modified as last_modified, c.req as req, c.approved as approved, c.unapproved as unapproved, " +
            "c.supply_source_type as supply_source_type, c.ext_supply_url as ext_supply_url, c.osId as osId from account as a," +
            "site as b, ext_supply_attr as c where a.id in (<id>) and " +
            "a.id=b.pub_id and b.id=c.site_inc_id and c.approved=false and c.unapproved=false and c.osId=? order by c.ext_supply_name limit ?,?";

    public static final String get_approved_ext_site_by_pub_os  = "select a.id as pub_inc_id,c.id as id, c.site_inc_id as site_inc_id, " +
            "c.ext_supply_id as ext_supply_id, c.ext_supply_name as ext_supply_name, c.ext_supply_domain as ext_supply_domain, " +
            "c.last_modified as last_modified, c.req as req, c.approved as approved, c.unapproved as unapproved, " +
            "c.supply_source_type as supply_source_type, c.ext_supply_url as ext_supply_url, c.osId as osId from account as a," +
            "site as b, ext_supply_attr as c where a.id in (<id>) and " +
            "a.id=b.pub_id and b.id=c.site_inc_id and c.approved=true and c.unapproved=false and c.osId=? order by c.ext_supply_name limit ?,?";

    public static final String get_rejected_ext_site_by_pub_os  = "select a.id as pub_inc_id,c.id as id, c.site_inc_id as site_inc_id, " +
            "c.ext_supply_id as ext_supply_id, c.ext_supply_name as ext_supply_name, c.ext_supply_domain as ext_supply_domain, " +
            "c.last_modified as last_modified, c.req as req, c.approved as approved, c.unapproved as unapproved, " +
            "c.supply_source_type as supply_source_type, c.ext_supply_url as ext_supply_url, c.osId as osId from account as a," +
            "site as b, ext_supply_attr as c where a.id in (<id>) and " +
            "a.id=b.pub_id and b.id=c.site_inc_id and c.unapproved=true and c.osId=? order by c.ext_supply_name limit ?,?";

    
}

package com.kritter.kritterui.api.db_query_def;

public class AdpositionGetDef {
	public static final String insert_adposition_get = "insert into adposition_get(pubIncId,adxbasedexhangesstatus,last_modified) values(?,?,?)";
	public static final String update_insert_adposition_status = "update adposition_get set adxbasedexhangesstatus=?, last_modified=?  where internalid=?";
	public static final String list_adposition_get = "select ifnull(b.internalid,-1) as internalid,"
			+ "a.pubIncId as pubIncId,ifnull(b.adxbasedexhangesstatus,0) as adxbasedexhangesstatus, "
			+ "ifnull(b.message,'') as message, ifnull(b.last_modified,a.last_modified) as last_modified from "
			+ "adxbasedexchanges_metadata  as a left join adposition_get as b on a.pubIncId=b.pubIncId";
	public static final String update_status_by_pubincids = "update adposition_get set adxbasedexhangesstatus=?, last_modified=?  where pubIncId in (<id>)";
}


package com.kritter.kritterui.api.db_query_def;

public class MaterialAdvInfoDef {
	public static final String insert_material_advinfo = "insert into advinfo_upload(pubIncId,adxbasedexhangesstatus,advIncId,last_modified) values(?,?,?,?)";
	public static final String update_material_advinfo_status = "update advinfo_upload set adxbasedexhangesstatus=?, last_modified=?  where internalid in (<id>)";
	public static final String list_material_advinfo = "select a.internalid as internalid, a.pubIncId as pubIncId , "
			+ "a.adxbasedexhangesstatus as adxbasedexhangesstatus, a.advIncId as advIncId,b.name as advName,a.message as message, a.info as info, "
			+ "a.last_modified as last_modified "
			+ "from advinfo_upload as a,account as b "
			+ "where a.advIncId=b.id ";
	public static final String list_material_advinfo_by_pubincids = "select a.internalid as internalid, a.pubIncId as pubIncId , "
			+ "a.adxbasedexhangesstatus as adxbasedexhangesstatus, a.advIncId as advIncId,b.name as advName,a.message as message, a.info as info, "
			+ "a.last_modified as last_modified "
			+ "from advinfo_upload as a,account as b "
			+ "where a.advIncId=b.id "
			+ "and a.pubIncId in (<id>)";
}


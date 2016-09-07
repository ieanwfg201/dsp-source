package com.kritter.kritterui.api.db_query_def;

public class MaterialBannerDef {
	public static final String insert_material_banner = "insert into banner_upload(pubIncId,adxbasedexhangesstatus,advIncId,campaignId,"
			+ "adId,bannerId,last_modified) values(?,?,?,?,?,?,?)";
	public static final String update_material_banner_status = "update banner_upload set adxbasedexhangesstatus=?, last_modified=?  where internalid in (<id>)";
	public static final String list_material_banner = "";
}


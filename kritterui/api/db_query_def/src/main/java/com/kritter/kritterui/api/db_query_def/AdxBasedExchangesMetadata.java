package com.kritter.kritterui.api.db_query_def;

public class AdxBasedExchangesMetadata {
	public static final String insert_adbasedexchanges_metadata = "insert into adxbasedexchanges_metadata(pubIncId,advertiser_upload,adposition_get,"
			+ "banner_upload,video_upload,last_modified) values(?,?,?,?,?,?)";
	public static final String update_adbasedexchanges_metadata = "update adxbasedexchanges_metadata set pubIncId=?,advertiser_upload=?,adposition_get=?,"
			+ "banner_upload=?,video_upload=?,last_modified=? where internalid=?";
	public static final String get_all = "select * from adxbasedexchanges_metadata";
	public static final String get_by_internalids = "select * from adxbasedexchanges_metadata where internalid in (<id>)";
	public static final String get_by_pubincids = "select * from adxbasedexchanges_metadata where pubIncId in (<id>)";

}


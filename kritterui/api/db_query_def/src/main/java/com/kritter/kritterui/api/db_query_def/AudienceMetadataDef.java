package com.kritter.kritterui.api.db_query_def;

public class AudienceMetadataDef {

	public static final String update_audience_metadata = "update audience_metadata"
			+ " set enabled=?,last_modified=? where internalid in (<id>)";

	public static final String select__audience_metadata = "select * from audience_metadata";

}


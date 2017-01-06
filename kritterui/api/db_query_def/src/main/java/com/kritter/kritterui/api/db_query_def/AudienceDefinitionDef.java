package com.kritter.kritterui.api.db_query_def;

public class AudienceDefinitionDef {

	public static final String list_by_type = "select * from audience_definition where audience_type=? ";
	public static final String list_by_type_tier = "select * from audience_definition where audience_type=? and tier=?";
	public static final String list_by_ids = "select * from audience_definition where internalid in (<id>)";
	public static final String list_by_parentids = "select * from audience_definition where parent_internalid in (<id>)";
	public static final String list_cat_by_tier = "select * from audience_definition where audience_type=3 and tier=?";

}


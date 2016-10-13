package com.kritter.kritterui.api.db_query_def;

public class QualificationDef {
	public static final String insert_qualification = "insert into qualification"
			+ "(name,url,md5,state,advIncId,last_modified) "
			+ "values(?,?,?,?,?,?)";
	
	public static final String update_qualification = "update qualification"
			+ " set name=?,url=?,md5=?,state=?,last_modified=? "
			+ " where internalid=?";

	public static final String delete_multiple_qualification = "update qualification"
			+ " set state=?,last_modified=? "
			+ " where internalid in (<id>)";

	public static final String select_qualification_byadvertisers = "select * from qualification"
			+ " where advIncId in (<id>) and state != 3 ";

	public static final String select_qualification_byinternalids = "select * from qualification"
			+ " where internalid in (<id>) and state != 3 ";

	public static final String select_by_name_adv = "select * from qualification"
			+ " where advIncId =? and name=?";

}


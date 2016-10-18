package com.kritter.naterial_upload.youku.advInfo;

public class YoukuAdvInfoQuery {
	public static final String  selectQuery ="select "
			+ "a.id as advId,a.brand as brand,a.contactdetail as contactdetail,"
			+ "a.address as address,a.phone as phone,a.name as advName,"
			+ "c.supplycode as firstind,d.supplycode as secondind ,"
			+ "b.name as qname,b.url as qurl,b.md5 as qmd5, b.state as qstate "
			+ "from account as a left join qualification as b on a.id=b.advIncId "
			+ "left join "
			+ "(select p.supplycode as supplycode, q.ui_id as ui_id from "
			+ "	supply_mma_mapping as p, mma_code_mma_ui_mapping as q,mma_exchangename_id_mapping as r  "
			+ "	where p.mma_category_code = q.code and p.exchangename=r.exchangename and r.exchangeid=? group by q.ui_id)  as c "
			+ "on a.firstind=c.ui_id "
			+ "left join "
			+ "(select x.supplycode as supplycode, y.ui_id as ui_id "
			+ "	from supply_mma_mapping as x, mma_code_mma_ui_mapping as y,mma_exchangename_id_mapping as z  "
			+ "	where x.mma_category_code = y.code and x.exchangename=z.exchangename and z.exchangeid=? group by y.ui_id) as d "
			+ "on a.secondind=d.ui_id "
			+ "where a.type_id=3 and (a.last_modified>=? or b.last_modified>=?)";
	
	public static final String  getAdvInfoUpload = "select * from advinfo_upload where "
			+ "pubIncId=? and advIncId=?";
	public static final String  insertAdvInfoUpload = "insert into advinfo_upload"
			+ "(pubIncId,adxbasedexhangesstatus,advIncId,last_modified,info) "
			+ "values(?,?,?,?,?)";
	public static final String  updatetAdvInfoUpload = "update advinfo_upload"
			+ " set adxbasedexhangesstatus=?,info=?, last_modified=?"
			+ " where internalid=?";
	public static final String  selectforUpload = "select * from advinfo_upload where pubIncId=? and adxbasedexhangesstatus=2";
	
	public static final String  updatetAdvinfoStatus = "update advinfo_upload"
			+ " set adxbasedexhangesstatus=?,last_modified=?,message=? "
			+ " where internalid in (<id>)";
	public static final String  updatetAdvInfoStatusMessage = "update advinfo_upload"
			+ " set adxbasedexhangesstatus=?,message=?,last_modified=? "
			+ " where internalid in (<id>)";

	public static final String  selectforAudit = "select * from advinfo_upload where pubIncId=? and (adxbasedexhangesstatus=7 or adxbasedexhangesstatus=10) ";

	public static final String  insert_material_state= "insert into material_upload_state(pubIncId,materialtype,last_modified) values(?,?,?)";
	public static final String  update_material_state= "update material_upload_state set last_modified=? where materialtype=? and pubIncId=?";
	public static void main(String args[]){
		System.out.println(selectQuery);
	}
}

package com.kritter.naterial_upload.youku.banner;

public class YoukuBannerQuery {
	public static final String  selectQuery =
			"select j1.*,j2.id as bannerId,j2.resource_uri as resource_uri from "
			+ "(select a.id as advId, a.name as advName,"
			+ 	"b.id as campaignId,b.name as campaignName, b.start_date as campaignStartDate, "
			+ "b.end_date as campaignEndDate, b.status_id as campaignStatus, "
			+ 	"c.id as adId, c.name as adName, c.status_id as adStatus, c.landing_url as landing_url, "
			+ 	"d.id as creativeId, d.label as creativeName,d.resource_uri_ids  as resource_uri_ids,"
			+ "d.status_id as creativeStatus  "
			+ "from account as a,campaign as b, ad as c, creative_container as d  "
			+ 	"where  a.guid=b.account_guid and b.id=c.campaign_id and  c.creative_id=d.id "
			+ 	"and d.format_id=2 and d.resource_uri_ids is not null and d.resource_uri_ids<>'' "
			+ 	"and d.resource_uri_ids<>'[]' and "
			+ 	"GREATEST(a.last_modified,b.last_modified,c.last_modified,d.last_modified) >=? ) as j1 "
			+ "join creative_banner as j2 "
			+ 	"on FIND_IN_SET(j2.id,REPLACE(REPLACE(j1.resource_uri_ids,'[',''),']',''))";
	
	
	
	public static final String  getBannerUpload = "select * from banner_upload where "
			+ "pubIncId=? and advIncId=? and campaignId=? and adId=? and creativeId=? and bannerId=?";
	public static final String  insertBannerUpload = "insert into banner_upload"
			+ "(pubIncId,adxbasedexhangesstatus,advIncId,campaignId,campaignStatus"
			+ ",adId,adStatus,creativeId,creativeStatus,bannerId,last_modified,info) "
			+ "values(?,?,?,?,?,?,?,?,?,?,?,?)";
	public static final String  updatetBannerUpload = "update banner_upload"
			+ " set adxbasedexhangesstatus=?,campaignStatus=?"
			+ ",adStatus=?,creativeStatus=?,last_modified=?,info=? "
			+ " where internalid=?";
	public static final String  selectforUpload = "select * from banner_upload where pubIncId=? and adxbasedexhangesstatus=2";
	
	public static final String  updatetBannerStatus = "update banner_upload"
			+ " set adxbasedexhangesstatus=?,last_modified=?,message=? "
			+ " where internalid in (<id>)";
	public static final String  updatetBannerStatusMessage = "update banner_upload"
			+ " set adxbasedexhangesstatus=?,message=?,last_modified=? "
			+ " where internalid in (<id>)";
	public static final String  selectforAudit = "select * from banner_upload where pubIncId=? and (adxbasedexhangesstatus=7) ";

	public static final String  insert_material_state= "insert into material_upload_state(pubIncId,materialtype,last_modified) values(?,?,?)";
	public static final String  update_material_state= "update material_upload_state set last_modified=? where materialtype=? and pubIncId=?";
	public static void main(String args[]){
		System.out.println(selectQuery);
	}
}

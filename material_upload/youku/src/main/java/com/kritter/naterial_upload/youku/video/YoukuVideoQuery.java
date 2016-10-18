package com.kritter.naterial_upload.youku.video;

public class YoukuVideoQuery {
	public static final String  selectQuery =""
			+ "select a.id as advId, a.name as advName,"
			+ "b.id as campaignId,b.name as campaignName, "
			+ "b.start_date as campaignStartDate, b.end_date as campaignEndDate, b.status_id as campaignStatus, "
			+ "c.id as adId, c.name as adName, c.status_id as adStatus, c.landing_url as landing_url, "
			+ "d.id as creativeId, d.label as creativeName,d.video_props  as video_props,"
			+ "d.status_id as creativeStatus  "
			+ "from account as a,campaign as b, ad as c, creative_container as d  "
			+ "where  a.guid=b.account_guid and b.id=c.campaign_id and  c.creative_id=d.id and "
			+ "d.format_id=4 and d.video_props is not null and d.video_props<>'' and d.video_props<>'[]' "
			+ "and GREATEST(a.last_modified,b.last_modified,c.last_modified,d.last_modified) >=? ";
	
	public static final String  getVideoInfo = "select * from video_info where id=?";
	
	public static final String  getVideoUpload = "select * from video_upload where "
			+ "pubIncId=? and advIncId=? and campaignId=? and adId=? and creativeId=? and videoInfoId=? and adxbasedexhangesstatus!=14";
	public static final String  insertVideoUpload = "insert into video_upload"
			+ "(pubIncId,adxbasedexhangesstatus,advIncId,campaignId,campaignStatus"
			+ ",adId,adStatus,creativeId,creativeStatus,videoInfoId,last_modified,info) "
			+ "values(?,?,?,?,?,?,?,?,?,?,?,?)";
	public static final String  updatetVideoUpload = "update video_upload"
			+ " set adxbasedexhangesstatus=?,info=?,campaignStatus=?"
			+ ",adStatus=?,creativeStatus=?,last_modified=? "
			+ " where internalid=?";
	public static final String  selectforUpload = "select * from video_upload where pubIncId=? and adxbasedexhangesstatus=2";
	
	public static final String  updatetVideoStatus = "update video_upload"
			+ " set adxbasedexhangesstatus=?,last_modified=?,message=? "
			+ " where internalid in (<id>)";
	public static final String  updatetVideoStatusMessage = "update video_upload"
			+ " set adxbasedexhangesstatus=?,message=?,last_modified=? "
			+ " where internalid in (<id>)";
	public static final String  selectforAudit = "select * from video_upload where pubIncId=? and (adxbasedexhangesstatus=7 or adxbasedexhangesstatus=10) ";

	public static final String  insert_material_state= "insert into material_upload_state(pubIncId,materialtype,last_modified) values(?,?,?)";
	public static final String  update_material_state= "update material_upload_state set last_modified=? where materialtype=? and pubIncId=?";
	
	public static final String  update_video_info= "update video_info set last_modified=?,ext=? where id=?";
	
	public static final String  removedCreatives= "select internalid,creativeId,videoInfoId from video_upload where adxbasedexhangesstatus!=14";
	
	public static final String  getCreativeContainer= "select video_props from creative_container where id=?";
	
	public static final String  updateRemovedCreatives= "update video_upload set adxbasedexhangesstatus=14 where internalid=?";
	
	public static void main(String args[]){
		System.out.println(selectQuery);
	}
}

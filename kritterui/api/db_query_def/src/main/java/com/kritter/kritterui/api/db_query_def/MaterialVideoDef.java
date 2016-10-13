package com.kritter.kritterui.api.db_query_def;

public class MaterialVideoDef {
	public static final String insert_material_video = "insert into video_upload(pubIncId,adxbasedexhangesstatus,advIncId,campaignId,"
			+ "adId,videoInfoId,last_modified) values(?,?,?,?,?,?,?)";
	public static final String update_material_video_status = "update video_upload set adxbasedexhangesstatus=?, last_modified=?  where internalid in (<id>)";
	public static final String list_material_video = "select a.internalid as internalid, a.pubIncId as pubIncId , "
			+ "a.adxbasedexhangesstatus as adxbasedexhangesstatus, a.advIncId as advIncId,b.name as advName, a.campaignId as campaignId,"
			+ "c.name as campaignName, a.campaignStatus as campaignStatus, a.adId as adId,d.name as adName, a.adStatus as adStatus, a.creativeId as creativeId, "
			+ "a.creativeStatus as creativeStatus, e.label as creativeName, a.videoInfoId as videoInfoId, a.message as message, a.info as info, "
			+ "a.last_modified as last_modified,e.resource_uri_ids as resource_uri_ids "
			+ "from video_upload as a,account as b,campaign as c,ad as d,creative_container as e  "
			+ "where a.advIncId=b.id and a.campaignId=c.id and a.adId=d.id and a.creativeId=e.id and d.creative_id=e.id and c.id=d.campaign_id and "
			+ "c.account_guid=b.guid ";
	public static final String list_material_video_by_pubincids = "select a.internalid as internalid, a.pubIncId as pubIncId , "
			+ "a.adxbasedexhangesstatus as adxbasedexhangesstatus, a.advIncId as advIncId,b.name as advName, a.campaignId as campaignId,"
			+ "c.name as campaignName, a.campaignStatus as campaignStatus, a.adId as adId,d.name as adName, a.adStatus as adStatus, a.creativeId as creativeId, "
			+ "a.creativeStatus as creativeStatus, e.label as creativeName, a.videoInfoId as videoInfoId, a.message as message, a.info as info, "
			+ "a.last_modified as last_modified,e.resource_uri_ids as resource_uri_ids "
			+ "from video_upload as a,account as b,campaign as c,ad as d,creative_container as e  "
			+ "where a.advIncId=b.id and a.campaignId=c.id and a.adId=d.id and a.creativeId=e.id and d.creative_id=e.id and c.id=d.campaign_id and "
			+ "c.account_guid=b.guid and a.pubIncId in (<id>)";

	public static final String list_material_video_by_pubincids_state = "select a.internalid as internalid, a.pubIncId as pubIncId , "
			+ "a.adxbasedexhangesstatus as adxbasedexhangesstatus, a.advIncId as advIncId,b.name as advName, a.campaignId as campaignId,"
			+ "c.name as campaignName, a.campaignStatus as campaignStatus, a.adId as adId,d.name as adName, a.adStatus as adStatus, a.creativeId as creativeId, "
			+ "a.creativeStatus as creativeStatus, e.label as creativeName, a.videoInfoId as videoInfoId, a.message as message, a.info as info, "
			+ "a.last_modified as last_modified,e.resource_uri_ids as resource_uri_ids "
			+ "from video_upload as a,account as b,campaign as c,ad as d,creative_container as e  "
			+ "where a.advIncId=b.id and a.campaignId=c.id and a.adId=d.id and a.creativeId=e.id and d.creative_id=e.id and c.id=d.campaign_id and "
			+ "c.account_guid=b.guid and a.pubIncId in (<id>) and a.adxbasedexhangesstatus=?";
	
	public static void main(String args[]){
		System.out.println(list_material_video_by_pubincids_state);
	}

}


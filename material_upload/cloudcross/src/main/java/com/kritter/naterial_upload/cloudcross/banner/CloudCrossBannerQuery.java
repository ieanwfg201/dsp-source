package com.kritter.naterial_upload.cloudcross.banner;

public class CloudCrossBannerQuery {
	public static final String  selectQuery =
			"SELECT result.*,cs.`width`,cs.`height` FROM  " +
					"(SELECT  " +
					"  j1.*, " +
					"  j2.id AS bannerId, " +
					"  j2.resource_uri AS resource_uri  " +
					"FROM " +
					"  (SELECT  " +
					"    a.id AS advId, " +
					"    a.name AS advName, " +
					"    b.id AS campaignId, " +
					"    b.name AS campaignName, " +
					"    b.start_date AS campaignStartDate, " +
					"    b.end_date AS campaignEndDate, " +
					"    b.status_id AS campaignStatus, " +
					"    c.id AS adId, " +
					"    c.name AS adName, " +
					"    c.status_id AS adStatus, " +
					"    c.landing_url AS landing_url, " +
					"    d.id AS creativeId, " +
					"    d.label AS creativeName, " +
					"    d.resource_uri_ids AS resource_uri_ids, " +
					"    d.status_id AS creativeStatus  " +
					"  FROM " +
					"    account AS a, " +
					"    campaign AS b, " +
					"    ad AS c, " +
					"    creative_container AS d  " +
					"  WHERE a.guid = b.account_guid  " +
					"    AND b.id = c.campaign_id  " +
					"    AND c.creative_id = d.id  " +
					"    AND d.format_id = 2  " +
					"    AND d.resource_uri_ids IS NOT NULL  " +
					"    AND d.resource_uri_ids <> ''  " +
					"    AND d.resource_uri_ids <> '[]'  " +
					"    AND GREATEST( " +
					"      a.last_modified, " +
					"      b.last_modified, " +
					"      c.last_modified, " +
					"      d.last_modified " +
					"    ) > ?) AS j1  " +
					"  JOIN creative_banner AS j2  " +
					"    ON FIND_IN_SET( " +
					"      j2.id, " +
					"      REPLACE( " +
					"        REPLACE(j1.resource_uri_ids, '[', ''), " +
					"        ']', " +
					"        '' " +
					"      ) " +
					"    )) AS result  " +
					"  LEFT JOIN creative_banner AS cb ON cb.id = result.`bannerId` " +
					"  LEFT JOIN creative_slots AS cs ON cs.id = cb.slot_id";



	public static final String  getBannerUpload = "SELECT bu.*,cs.`width`,cs.`height`" +
			" FROM (SELECT * FROM banner_upload WHERE pubIncId=? AND advIncId=? AND campaignId=? AND adId=? AND creativeId=? AND bannerId=? ) AS bu" +
			"  LEFT JOIN creative_banner AS cb ON cb.id = bu.`bannerId`" +
			"  LEFT JOIN creative_slots AS cs ON cs.id = cb.slot_id";
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
			+ " set adxbasedexhangesstatus=?,last_modified=?,info=? "
			+ " where internalid in (<id>)";
	public static final String  updatetBannerStatusMessage = "update banner_upload"
			+ " set adxbasedexhangesstatus=?,message=?,last_modified=? "
			+ " where internalid in (<id>)";
	public static final String  selectforAudit = "select * from banner_upload where pubIncId=?  AND adxbasedexhangesstatus IN (7, 10, 12)";

	public static final String  insert_material_state= "insert into material_upload_state(pubIncId,materialtype,last_modified) values(?,?,?)";
	public static final String  update_material_state= "update material_upload_state set last_modified=? where materialtype=? and pubIncId=?";
	public static void main(String args[]){
		System.out.println(selectQuery);
	}
	public static final String removedCreativesQuery = "select c.internalid as internalid from creative_banner as a , "
			+ "creative_container as b,banner_upload as c "
			+ "where a.account_guid=b.account_guid and b.format_id=2 and "
			+ "not FIND_IN_SET(a.id,REPLACE(REPLACE(b.resource_uri_ids,'[',''),']','')) and "
			+ "GREATEST(a.last_modified,b.last_modified) >= ? and a.id=c.bannerId ";
	public static final String updateRemovedCreatives = "update banner_upload set adxbasedexhangesstatus=14 where internalid=?";

}

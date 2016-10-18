package com.kritter.naterial_upload.valuemaker.banner;

public class VamBannerQuery {
	public static final String  selectQuery ="\t\t\tSELECT result.*,cs.`width`,cs.`height` FROM  \n" +
            "(SELECT  \n" +
            "  j1.*, \n" +
            "  j2.id AS bannerId, \n" +
            "  j2.resource_uri AS resource_uri,\n" +
            "  m.supplycode category\n" +
            "FROM \n" +
            "  (SELECT  \n" +
            "    a.id AS advId, \n" +
            "    a.name AS advName,\n" +
            "    a.secondind AS secondind, \n" +
            "    b.id AS campaignId, \n" +
            "    b.name AS campaignName, \n" +
            "    b.start_date AS campaignStartDate, \n" +
            "    b.end_date AS campaignEndDate, \n" +
            "    b.status_id AS campaignStatus, \n" +
            "    c.id AS adId, \n" +
            "    c.name AS adName, \n" +
            "    c.status_id AS adStatus, \n" +
            "    c.landing_url AS landing_url, \n" +
            "    d.id AS creativeId, \n" +
            "    d.label AS creativeName, \n" +
            "    d.resource_uri_ids AS resource_uri_ids, \n" +
            "    d.status_id AS creativeStatus,  \n" +
            "    d.guid AS creativeGuid  \n" +
            "  FROM \n" +
            "    account AS a, \n" +
            "    campaign AS b, \n" +
            "    ad AS c, \n" +
            "    creative_container AS d  \n" +
            "  WHERE a.guid = b.account_guid  \n" +
            "    AND b.id = c.campaign_id  \n" +
            "    AND c.creative_id = d.id  \n" +
            "    AND d.format_id = 2  \n" +
            "    AND d.resource_uri_ids IS NOT NULL  \n" +
            "    AND d.resource_uri_ids <> ''  \n" +
            "    AND d.resource_uri_ids <> '[]'  \n" +
            "    AND GREATEST( \n" +
            "      a.last_modified, \n" +
            "      b.last_modified, \n" +
            "      c.last_modified, \n" +
            "      d.last_modified \n" +
            "    ) > ?) AS j1\n" +
            "  JOIN creative_banner AS j2   ON FIND_IN_SET( j2.id, REPLACE(REPLACE(j1.resource_uri_ids, '[', ''), ']', '' )  )\n" +
            "  left join mma_code_mma_ui_mapping um  on  um.ui_id=j1.secondind\n" +
            "  left join supply_mma_mapping m  on m.exchangename='valuemaker' and m.mma_category_code=um.code\n" +
            "  ) AS result  \n" +
            "  LEFT JOIN creative_banner AS cb ON cb.id = result.`bannerId` \n" +
            "  LEFT JOIN creative_slots AS cs ON cs.id = cb.slot_id";


	public static final String  getBannerUpload = "select * from banner_upload where "
			+ "pubIncId=? and advIncId=? and campaignId=? and adId=? and creativeId=? and bannerId=?";
	public static final String  insertBannerUpload = "insert into banner_upload"
			+ "(pubIncId,adxbasedexhangesstatus,advIncId,campaignId,campaignStatus"
			+ ",adId,adStatus,creativeId,creativeStatus,bannerId,last_modified,info) "
			+ "values(?,?,?,?,?,?,?,?,?,?,?,?)";
	public static final String  updatetBannerUpload =
			"update banner_upload" +
					" set adxbasedexhangesstatus=?,campaignStatus=?" +
					",adStatus=?,creativeStatus=?,last_modified=?,info=? " +
					" where internalid=?";
	public static final String  selectforUpload = "select * from banner_upload where pubIncId=? and adxbasedexhangesstatus=2";
	
	public static final String  updatetBannerStatus = "update banner_upload"
			+ " set adxbasedexhangesstatus=?,last_modified=? "
			+ " where internalid in (<id>)";
	public static final String  updatetBannerStatusMessage = "update banner_upload"
			+ " set adxbasedexhangesstatus=?,message=?,last_modified=? "
			+ " where internalid in (<id>)";
	public static final String  selectforAudit = "select * from banner_upload where pubIncId=? and (adxbasedexhangesstatus=7 or adxbasedexhangesstatus=12) ";

	public static final String  insert_material_state= "insert into material_upload_state(pubIncId,materialtype,last_modified) values(?,?,?)";
	public static final String  update_material_state= "update material_upload_state set last_modified=? where materialtype=? and pubIncId=?";

	public static final String removedCreativesQuery = "select c.internalid as internalid from creative_banner as a , "
			+ "creative_container as b,banner_upload as c "
			+ "where a.account_guid=b.account_guid and b.format_id=2 and "
			+ "not FIND_IN_SET(a.id,REPLACE(REPLACE(b.resource_uri_ids,'[',''),']','')) and "
			+ "GREATEST(a.last_modified,b.last_modified) >= ? and a.id=c.bannerId ";

	public static final String updateRemovedCreatives = "update banner_upload set adxbasedexhangesstatus=14 where internalid=?";


	public static void main(String args[]){
		System.out.println(selectQuery);
	}

	public static final String selectCreativeGuid = "SELECT b.guid FROM banner_upload AS a,creative_container AS b WHERE a.creativeId=b.id and a.pubIncId=? and (a.adxbasedexhangesstatus=7 or a.adxbasedexhangesstatus=12) ";
}

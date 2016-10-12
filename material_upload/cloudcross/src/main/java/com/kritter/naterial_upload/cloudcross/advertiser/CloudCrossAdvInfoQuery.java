package com.kritter.naterial_upload.cloudcross.advertiser;

/**
 * Created by hamlin on 16-9-28.
 */
public class CloudCrossAdvInfoQuery {
    public static final String selectQuery = "SELECT a.id AS advId, a.name AS advName, a.brand AS brand,a.contactdetail AS contactdetail,a.address AS address, a.phone AS phone,a.firstind AS firstind,a.secondind AS secondind FROM account AS a WHERE a.type_id=3 AND a.last_modified > ?";


    public static final String getAdvInfoUpload = "select * from advinfo_upload where "
            + "pubIncId=? and advIncId=?";
    public static final String insertAdvInfoUpload = "insert into advinfo_upload"
            + "(pubIncId,adxbasedexhangesstatus,advIncId,last_modified,info) "
            + "values(?,?,?,?,?)";
    public static final String updatetAdvInfoUpload = "update advinfo_upload"
            + " set adxbasedexhangesstatus=?,last_modified=?,info=? "
            + " where internalid=?";
    public static final String selectforUpload = "select * from advinfo_upload where pubIncId=? and adxbasedexhangesstatus=2 OR adxbasedexhangesstatus=15";

    public static final String updatetAdvinfoStatus = "update advinfo_upload"
            + " set adxbasedexhangesstatus=?,last_modified=?,message=? "
            + " where internalid in (<id>)";
    public static final String updatetAdvInfoStatusMessage = "update advinfo_upload"
            + " set adxbasedexhangesstatus=?,message=?,last_modified=? "
            + " where internalid in (<id>)";

    public static final String selectforAudit = "select * from advinfo_upload where pubIncId=? and (adxbasedexhangesstatus=7) ";

    public static final String insert_material_state = "insert into material_upload_state(pubIncId,materialtype,last_modified) values(?,?,?)";
    public static final String update_material_state = "update material_upload_state set last_modified=? where materialtype=? and pubIncId=?";
    public static final String selectSupplyIndustryIdByUIMMACategoriesId = "SELECT supplycode FROM supply_mma_mapping WHERE exchangename = 'cloudcross' AND mma_category_code = (SELECT `code` FROM mma_code_mma_ui_mapping WHERE ui_id = ?)";

    public static void main(String args[]) {
        System.out.println(selectQuery);
    }

}

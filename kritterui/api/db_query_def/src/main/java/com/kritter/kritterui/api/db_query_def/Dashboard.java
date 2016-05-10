package com.kritter.kritterui.api.db_query_def;

public class Dashboard {
    public static final String total_metric = "select impression_time as time," +
    		" SUM(total_request) as total_request_name, SUM(total_impression) as total_impression_name," +
    		" SUM(total_csc) as total_csc_name, SUM(total_win) as total_win_name, ROUND(SUM(demandCharges),2) as demandCharges_name," +
    		" ROUND(SUM(supplyCost),2) as supplyCost_name, SUM(total_click) as total_click_name, SUM(conversion) as conversion_name" +
    		" from dashboard_total_metric where impression_time>=? and impression_time <? group by " +
    		"impression_time";
    public static final String dashboard_campaign = "select ROUND(SUM(a.demandCharges),2) as demandCharges_name, b.name as campaign_name " +
    		"from dashboard_campaign as a, campaign as b where a.impression_time=? and a.campaignId=b.id group by a.campaignId " +
    		"order by demandCharges_name desc limit ?";

    public static final String dashboard_site = "select ROUND(SUM(a.supplyCost),2) as supplyCost_name, b.name as site_name from dashboard_site as a, site as b " +
    		"where a.impression_time=? and a.siteId=b.id group by a.siteId order by  supplyCost_name desc limit ?";

    public static final String dashboard_country = "select ROUND(100*(SUM(a.total_request)/c.total_count),2) as total_request_name, b.country_name as country_name from " +
    		"dashboard_country as a, ui_targeting_country as b," +
    		"(select sum(total_request) as total_count from dashboard_country where impression_time=? group by impression_time) as c " +
    		"where a.impression_time=? and a.countryId=b.id group by b.country_name " +
    		"order by total_request_name desc limit ?";
    
    public static final String dashboard_os = "select ROUND(100*(SUM(a.total_request)/c.total_count),2) as total_request_name, b.os_name as os_name from dashboard_os as a, " +
    		"handset_os as b," +
            "(select SUM(total_request) as total_count from dashboard_os where impression_time=? group by impression_time) as c" +
    		" where a.impression_time=? and a.deviceOsId=b.os_id group by b.os_name order by total_request_name desc limit ?";
    
    public static final String dashboard_manufacturer = "select ROUND(100*(SUM(a.total_request)/c.total_count),2) as total_request_name, b.manufacturer_name as manufacturer_name" +
    		" from dashboard_manufacturer as a, handset_manufacturer as b," +
    		"(select SUM(total_request) as total_count from dashboard_manufacturer where impression_time=? group by impression_time) as c" +
    		" where a.impression_time=? and " +
    		"a.deviceManufacturerId=b.manufacturer_id group by b.manufacturer_name order by total_request_name desc limit ?";
    
    public static final String dashboard_browser = "select ROUND(100*(SUM(a.total_request)/c.total_count),2) as total_request_name, b.browser_name as browser_name from " +
    		"dashboard_browser as a, handset_browser as b," +
    		"(select SUM(total_request) as total_count from dashboard_browser where impression_time=? group by impression_time) as c" +
    		" where a.impression_time=? and a.browserId=b.browser_id group by b.browser_name order by total_request_name desc" +
    		" limit ?";
    
    public static final String gain_campaign = "select ROUND(a.demandCharges-b.demandCharges,2) as gain,c.name as campaign_name from " +
    		"(select campaignId,sum(demandCharges) as demandCharges from dashboard_campaign where impression_time=? group by campaignId) " +
    		"as a LEFT JOIN (select campaignId,sum(demandCharges) as demandCharges from dashboard_campaign where impression_time=?" +
    		" group by campaignId) as b ON a.campaignId=b.campaignId JOIN campaign as c on a.campaignId=c.id having gain >= 0  order by gain desc limit ?";
    
    public static final String looser_campaign = "select ROUND(a.demandCharges-b.demandCharges,2) as loss,c.name as campaign_name from " +
            "(select campaignId,sum(demandCharges) as demandCharges from dashboard_campaign where impression_time=? group by campaignId) " +
            "as a LEFT JOIN (select campaignId,sum(demandCharges) as demandCharges from dashboard_campaign where impression_time=?" +
            " group by campaignId) as b ON a.campaignId=b.campaignId JOIN campaign as c on a.campaignId=c.id having loss < 0 order by loss asc limit ?";
    
    public static final String gain_site = "select ROUND(a.supplyCost-b.supplyCost,2) as gain,c.name as site_name from " +
    		"(select siteId,sum(supplyCost) as supplyCost from dashboard_site where impression_time=? group by siteId) as a LEFT JOIN " +
    		"(select siteId,sum(supplyCost) as supplyCost from dashboard_site where impression_time=? group by siteId) as b " +
    		"ON a.siteId=b.siteId JOIN site as c on a.siteId=c.id having gain >= 0 order by gain desc limit ?";

    public static final String looser_site = "select ROUND(a.supplyCost-b.supplyCost,2) as loss,c.name as site_name from " +
            "(select siteId,sum(supplyCost) as supplyCost from dashboard_site where impression_time=? group by siteId) as a LEFT JOIN " +
            "(select siteId,sum(supplyCost) as supplyCost from dashboard_site where impression_time=? group by siteId) as b " +
            "ON a.siteId=b.siteId JOIN site as c on a.siteId=c.id having loss < 0 order by loss asc limit ?";
    
    public static final String dashboard_advertisers = "select ROUND(SUM(a.demandCharges),2) as demandCharges_name, c.name as account_name " +
    		"from dashboard_campaign as a, campaign as b, account as c where a.impression_time=? and a.campaignId=b.id and " +
    		"b.account_guid=c.guid group by c.name order by demandCharges_name desc limit ?";

    public static final String dashboard_publisher = "select ROUND(SUM(a.supplyCost),2) as supplyCost_name, c.name as account_name from " +
    		"dashboard_site as a, site as b, account as c where a.impression_time=? and a.siteId=b.id and b.pub_id=c.id " +
    		"group by c.name order by supplyCost_name desc limit ?";
    
    public static final String dashboard_exchange = "select ROUND(SUM(a.exchangepayout),2) as exchangepayout_name, c.name as account_name from " +
    		"dashboard_site as a, site as b, account as c where a.impression_time=? and a.siteId=b.id and b.pub_id=c.id and " +
    		"exchangepayout > 0 group by c.name order by exchangepayout_name desc limit ?";
    
    public static final String gain_publisher = "select ROUND(a.supplyCost-b.supplyCost,2) as gain,a.name as pub_name from " +
    		"(select c1.name as name ,sum(a1.supplyCost) as supplyCost from dashboard_site as a1,site as b1, account as c1 " +
    		"where a1.impression_time=? and a1.siteId=b1.id and b1.pub_id=c1.id group by c1.name) as a LEFT JOIN " +
    		"(select c2.name as name ,sum(a2.supplyCost) as supplyCost from dashboard_site as a2,site as b2, account as c2 " +
    		"where a2.impression_time=? and a2.siteId=b2.id and b2.pub_id=c2.id group by c2.name) as b ON a.name=b.name " +
    		"having gain >= 0  order by gain desc  limit ?";
    
    public static final String looser_publisher = "select ROUND(a.supplyCost-b.supplyCost,2) as loss,a.name as pub_name from " +
            "(select c1.name as name ,sum(a1.supplyCost) as supplyCost from dashboard_site as a1,site as b1, account as c1 " +
            "where a1.impression_time=? and a1.siteId=b1.id and b1.pub_id=c1.id group by c1.name) as a LEFT JOIN " +
            "(select c2.name as name ,sum(a2.supplyCost) as supplyCost from dashboard_site as a2,site as b2, account as c2 " +
            "where a2.impression_time=? and a2.siteId=b2.id and b2.pub_id=c2.id group by c2.name) as b ON a.name=b.name " +
            "having loss < 0  order by loss asc limit ?";
    
    public static final String gain_advertisers = "select ROUND(a.demandCharges-b.demandCharges,2) as gain,a.name as advertiser_name from " +
    		"(select c1.name as name ,sum(a1.demandCharges) as demandCharges from dashboard_campaign as a1,campaign as b1, account as c1 " +
    		"where a1.impression_time=? and a1.campaignId=b1.id and b1.account_guid=c1.guid group by c1.name) as a LEFT JOIN " +
    		"(select c2.name as name ,sum(a2.demandCharges) as demandCharges from dashboard_campaign as a2,campaign as b2, account as c2 " +
    		"where a2.impression_time=? and a2.campaignId=b2.id and b2.account_guid=c2.guid group by c2.name) as b ON a.name=b.name   " +
    		"having gain >= 0  order by gain desc  limit ?";

    public static final String looser_advertisers = "select ROUND(a.demandCharges-b.demandCharges,2) as loss,a.name as advertiser_name from " +
            "(select c1.name as name ,sum(a1.demandCharges) as demandCharges from dashboard_campaign as a1,campaign as b1, account as c1 " +
            "where a1.impression_time=? and a1.campaignId=b1.id and b1.account_guid=c1.guid group by c1.name) as a LEFT JOIN " +
            "(select c2.name as name ,sum(a2.demandCharges) as demandCharges from dashboard_campaign as a2,campaign as b2, account as c2 " +
            "where a2.impression_time=? and a2.campaignId=b2.id and b2.account_guid=c2.guid group by c2.name) as b ON a.name=b.name   " +
            "having loss < 0  order by loss asc  limit ?";
    
    public static final String impression_win = "select a.impression_time as time, SUM(a.total_impression) as total_impression_name, " +
    		"SUM(a.total_win) as total_win_name  from dashboard_site as a,site as b,account as c " +
    		"where a.impression_time>=? and a.impression_time<? and a.siteId=b.id and b.pub_id=c.id and c.inventory_source=2 " +
    		"group by a.impression_time";

    public static final String dashboard_publisher_v1 = "select a.impression_time as time, c.name as account_name, " +
    		"ROUND(SUM(a.total_request),2) as total_request_name," +
    		" ROUND(SUM(a.total_impression),2) as total_impression_name,ROUND(SUM(a.total_click),2) as total_click_name, " +
    		"ROUND(SUM(a.total_csc),2) as total_csc_name, ROUND(SUM(a.total_win),2) as total_win_name," +
    		"ROUND(SUM(a.supplyCost),2) as supplyCost_name from dashboard_site_v1 as a, site as b, account as c where " +
    		"a.impression_time>=? and a.impression_time<=?  and a.siteId=b.id and b.pub_id=c.id  and c.guid=?" +
            " group by a.impression_time,c.name order by supplyCost_name desc";
    
    public static final String dashboard_advertisers_v1 = "select a.impression_time as time, c.name as account_name," +
    		" ROUND(SUM(a.total_impression),2) as total_impression_name,ROUND(SUM(a.total_click),2) as total_click_name, " +
    		"ROUND(SUM(a.total_win),2) as total_win_name,ROUND(SUM(a.conversion),2) as conversion_name," +
    		"ROUND(SUM(a.demandCharges),2) as demandCharges_name,ROUND(SUM(a.total_csc),2) as total_csc_name " +
            "from dashboard_campaign_v1 as a, campaign as b, account as c where a.impression_time>=? and a.impression_time<=?" +
            " and a.campaignId=b.id and c.guid=?" +
            " and b.account_guid=c.guid group by a.impression_time,c.name order by demandCharges_name desc";

}
 
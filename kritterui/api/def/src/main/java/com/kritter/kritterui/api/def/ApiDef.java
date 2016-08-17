package com.kritter.kritterui.api.def;


import java.sql.Connection;
import java.util.List;

import com.kritter.api.entity.deal.PrivateMarketPlaceApiEntity;
import com.kritter.api.entity.deal.ThirdPartyConnectionChildId;
import com.kritter.api.entity.deal.ThirdPartyConnectionChildIdList;
import com.kritter.api.entity.parent_account.ParentAccount;
import com.kritter.kritterui.api.deal.PrivateMarketPlaceDealCrud;
import com.kritter.kritterui.api.parent_account.ParentAccountCrud;
import org.codehaus.jackson.JsonNode;
import com.kritter.api.entity.account.Account;
import com.kritter.api.entity.account.AccountList;
import com.kritter.api.entity.account.AccountMsgPair;
import com.kritter.api.entity.account.ListEntity;
import com.kritter.api.entity.retargeting_segment.RetargetingSegmentInputEntity;
import com.kritter.api.entity.retargeting_segment.RetargetingSegmentList;
import com.kritter.api.entity.account_budget.Account_Budget_Msg;
import com.kritter.api.entity.account_budget.Account_budget;
import com.kritter.api.entity.ad.Ad;
import com.kritter.api.entity.ad.AdList;
import com.kritter.api.entity.ad.AdListEntity;
import com.kritter.api.entity.campaign.Campaign;
import com.kritter.api.entity.campaign.CampaignList;
import com.kritter.api.entity.campaign.CampaignListEntity;
import com.kritter.api.entity.campaign_budget.CampaignBudgetList;
import com.kritter.api.entity.campaign_budget.CampaignBudgetListEntity;
import com.kritter.api.entity.campaign_budget.Campaign_budget;
import com.kritter.api.entity.creative_banner.CreativeBannerList;
import com.kritter.api.entity.creative_banner.CreativeBannerListEntity;
import com.kritter.api.entity.creative_banner.Creative_banner;
import com.kritter.api.entity.creative_container.CreativeContainerList;
import com.kritter.api.entity.creative_container.CreativeContainerListEntity;
import com.kritter.api.entity.creative_container.Creative_container;
import com.kritter.api.entity.ext_site.Ext_site_input;
import com.kritter.api.entity.ext_site.Ext_site_list;
import com.kritter.api.entity.extsitereport.ExtSiteReportEntity;
import com.kritter.api.entity.fraud.FraudReportEntity;
import com.kritter.api.entity.iddefinition.IddefinitionInput;
import com.kritter.api.entity.iddefinition.IddefinitionList;
import com.kritter.api.entity.insertion_order.IOListEntity;
import com.kritter.api.entity.insertion_order.Insertion_Order;
import com.kritter.api.entity.insertion_order.Insertion_Order_List;
import com.kritter.api.entity.isp_mapping.Isp_mapping;
import com.kritter.api.entity.isp_mapping.Isp_mappingList;
import com.kritter.api.entity.isp_mapping.Isp_mappingListEntity;
import com.kritter.api.entity.log.LogEntity;
import com.kritter.api.entity.metadata.MetaInput;
import com.kritter.api.entity.metadata.MetaList;
import com.kritter.api.entity.native_icon.NativeIconList;
import com.kritter.api.entity.native_icon.NativeIconListEntity;
import com.kritter.api.entity.native_screenshot.NativeScreenshotList;
import com.kritter.api.entity.native_screenshot.NativeScreenshotListEntity;
import com.kritter.api.entity.reporting.ReportingEntity;
import com.kritter.api.entity.req_logging.ReqLoggingInput;
import com.kritter.api.entity.req_logging.ReqLoggingList;
import com.kritter.api.entity.response.msg.Message;
import com.kritter.api.entity.saved_query.SavedQueryEntity;
import com.kritter.api.entity.saved_query.SavedQueryList;
import com.kritter.api.entity.saved_query.SavedQueryListEntity;
import com.kritter.api.entity.site.Site;
import com.kritter.api.entity.site.SiteList;
import com.kritter.api.entity.site.SiteListEntity;
import com.kritter.api.entity.ssp.SSPEntity;
import com.kritter.api.entity.targeting_profile.TargetingProfileList;
import com.kritter.api.entity.targeting_profile.TargetingProfileListEntity;
import com.kritter.api.entity.targeting_profile.Targeting_profile;
import com.kritter.api.entity.tracking_event.TrackingEvent;
import com.kritter.api.entity.video_info.VideoInfoList;
import com.kritter.api.entity.video_info.VideoInfoListEntity;
import com.kritter.constants.MetadataType;
import com.kritter.entity.ad_stats.AdStats;
import com.kritter.entity.algomodel.AlgoModelEntity;
import com.kritter.entity.native_props.demand.NativeIcon;
import com.kritter.entity.native_props.demand.NativeScreenshot;
import com.kritter.entity.req_logging.ReqLoggingEntity;
import com.kritter.entity.retargeting_segment.RetargetingSegment;
import com.kritter.entity.userreports.UserReport;
import com.kritter.entity.video_props.VideoInfo;
import com.kritter.kritterui.api.account.AccountCrud;
import com.kritter.kritterui.api.account_budget.Account_Budget_Crud;
import com.kritter.kritterui.api.ad.AdCrud;
import com.kritter.kritterui.api.ad_stats.AdStatsCrud;
import com.kritter.kritterui.api.algo_models.AlgoModelCrud;
import com.kritter.kritterui.api.campaign.CampaignCrud;
import com.kritter.kritterui.api.campaign_budget.CampaignBudgetCrud;
import com.kritter.kritterui.api.creative_banner.CreativeBannerCrud;
import com.kritter.kritterui.api.creative_container.CreativeContainerCrud;
import com.kritter.kritterui.api.ext_site.Ext_siteCrud;
import com.kritter.kritterui.api.extsitereport.ExtSiteReportCrud;
import com.kritter.kritterui.api.fraud.FraudReportCrud;
import com.kritter.kritterui.api.iddefinition.IddefinitionCrud;
import com.kritter.kritterui.api.io.IOCrud;
import com.kritter.kritterui.api.isp_mapping.Isp_mappingCrud;
import com.kritter.kritterui.api.logevent.LogEventCrud;
import com.kritter.kritterui.api.metadata.MetadataCrud;
import com.kritter.kritterui.api.mixed.MixedCrud;
import com.kritter.kritterui.api.native_icon.NativeIconCrud;
import com.kritter.kritterui.api.native_screenshot.NativeScreenshotCrud;
import com.kritter.kritterui.api.reporting.DashBoardCrud;
import com.kritter.kritterui.api.reporting.ReportingCrud;
import com.kritter.kritterui.api.req_logging.ReqLoggingCrud;
import com.kritter.kritterui.api.retargeting_segment.RetargetingSegmentCrud;
import com.kritter.kritterui.api.saved_query.SavedQueryCrud;
import com.kritter.kritterui.api.site.SiteCrud;
import com.kritter.kritterui.api.ssp.SSPCrud;
import com.kritter.kritterui.api.targeting_profile.TargetingProfileCrud;
import com.kritter.kritterui.api.tracking_event.TrackingEventCrud;
import com.kritter.kritterui.api.userreport.UserReportCrud;
import com.kritter.kritterui.api.video_info.VideoInfoCrud;

public class ApiDef {
    
    /*ACCOUNT APIS*/
    
    public static JsonNode verifyAccount(Connection con, JsonNode jsonNode){
        return AccountCrud.verifyAccount(con, jsonNode);
    }
    public static Message verifyAccount(Connection con, Account account){
        return AccountCrud.verifyAccount(con, account);
    }
    public static JsonNode createAccount(Connection con, JsonNode jsonNode){
        return AccountCrud.createAccount(con, jsonNode);
    }
    public static Message createAccount(Connection con, Account account){
        return AccountCrud.createAccount(con, account,true);
    }
    public static Message createAccount(Connection con, Account account,boolean useProvidedGuid){
        return AccountCrud.createAccount(con, account,true,useProvidedGuid);
    }
    public static JsonNode listAccount(Connection con, JsonNode jsonNode){
        return AccountCrud.listAccount(con, jsonNode);
    }
    public static AccountList listAccount(Connection con, ListEntity listEntity){
        return AccountCrud.listAccount(con, listEntity);
    }
    public static JsonNode various_get_account(Connection con, JsonNode jsonNode){
        return AccountCrud.various_get_account(con, jsonNode);
    }
    public static AccountList various_get_account(Connection con, ListEntity listEntity){
        return AccountCrud.various_get_account(con, listEntity);
    }
    public static JsonNode listAccountByStatus(Connection con, JsonNode jsonNode){
        return AccountCrud.listAccountByStatus(con, jsonNode);
    }
    public static AccountList listAccountByStatus(Connection con, ListEntity listEntity){
        return AccountCrud.listAccountByStatus(con, listEntity);
    }
    public static JsonNode listExchangesByStatus(Connection con, JsonNode jsonNode){
        return AccountCrud.listExchangesByStatus(con, jsonNode);
    }
    public static AccountList listExchangesByStatus(Connection con, ListEntity listEntity){
        return AccountCrud.listExchangesByStatus(con, listEntity);
    }
    public static JsonNode listDirectPublisherByStatus(Connection con, JsonNode jsonNode){
        return AccountCrud.listDirectPublisherByStatus(con, jsonNode);
    }
    public static AccountList listDirectPublisherByStatus(Connection con, ListEntity listEntity){
        return AccountCrud.listDirectPublisherByStatus(con, listEntity);
    }
    public static ThirdPartyConnectionChildIdList listThirdPartyConnectionDSPAdvIdList(Connection con,ThirdPartyConnectionChildId thirdPartyConnectionChildId){
        return PrivateMarketPlaceDealCrud.getDSPAdvIdListForAdvertiserGuid(con,thirdPartyConnectionChildId);
    }
    public static JsonNode updateAccount(Connection con, JsonNode jsonNode){
        return AccountCrud.updateAccount(con, jsonNode);
    }
    public static Message updateAccount(Connection con, Account account){
        return MixedCrud.updateAccount(con, account,true);
    }
    public static Message updateAccountUsingAccountCrud(Connection con, Account account){
        return AccountCrud.updateAccount(con, account,true);
    }
    public static JsonNode updateAccountStatus(Connection con, JsonNode jsonNode){
        return AccountCrud.updateStatus(con, jsonNode);
    }
    public static Message updateAccountStatus(Connection con, Account account){
        return AccountCrud.updateStatus(con, account,true);
    }
    public static JsonNode get_Account(Connection con, JsonNode jsonNode){
        return AccountCrud.get_Account(con, jsonNode);
    }
    public static AccountMsgPair get_Account(Connection con, Account account){
        return AccountCrud.get_Account(con, account);
    }
    public static JsonNode get_Account_By_Guid(Connection con, JsonNode jsonNode){
        return AccountCrud.get_Account_By_Guid(con, jsonNode);
    }
    public static Account get_Account_By_Guid(Connection con, String guid){
        return AccountCrud.get_Account_By_Guid(con, guid);
    }
    public static AccountMsgPair get_Account_By_Guid(Connection con, Account account){
        return AccountCrud.get_Account_By_Guid(con, account);
    }
    public static JsonNode get_Account_By_Guid_Apikey(Connection con, JsonNode jsonNode){
        return AccountCrud.get_Account_By_Guid_Apikey(con, jsonNode);
    }
    public static AccountMsgPair get_Account_By_Guid_Apikey(Connection con, Account account){
        return AccountCrud.get_Account_By_Guid_Apikey(con, account);
    }
    public static JsonNode get_Account_By_UserId_Pwd(Connection con, JsonNode jsonNode){
        return AccountCrud.get_Account_By_UserId_Pwd(con, jsonNode);
    }
    public static AccountMsgPair get_Account_By_UserId_Pwd(Connection con, Account account){
        return AccountCrud.get_Account_By_UserId_Pwd(con, account);
    }
    
    /*ACCOUNT BUDGET APIs*/
    public static JsonNode insert_account_budget(Connection con, JsonNode jsonNode){
        return Account_Budget_Crud.insert_account_budget(con, jsonNode);
    }
    public static Message insert_account_budget(Connection con, Account_budget ab){
        return Account_Budget_Crud.insert_account_budget(con, ab, true);
    }
    public static JsonNode update_account_budget(Connection con, JsonNode jsonNode){
        return Account_Budget_Crud.update_account_budget(con, jsonNode);
    }
    public static Message update_account_budget(Connection con, Account_budget ab){
        return Account_Budget_Crud.update_account_budget(con, ab, true);
    }
    public static JsonNode get_Account_Budget(Connection con, JsonNode jsonNode){
        return Account_Budget_Crud.get_Account_Budget(con, jsonNode);
    }
    public static Account_Budget_Msg get_Account_Budget(Connection con, Account_budget ab){
        return Account_Budget_Crud.get_Account_Budget(con, ab);
    }
    public static Account_budget get_Account_Budget(String guid,Connection con){
        return Account_Budget_Crud.get_Account_Budget(guid,con);
    }

    
    /*IO APIS*/
    
    public static JsonNode insert_io(Connection con, JsonNode jsonNode){
        return IOCrud.insert_io(con, jsonNode);
    }
    public static Message insert_io(Connection con, Insertion_Order io){
        return IOCrud.insert_io(con, io,true);
    }
    public static JsonNode check_io(Connection con, JsonNode jsonNode){
        return IOCrud.check_io(con, jsonNode);
    }
    public static Message check_io(Connection con, Insertion_Order io){
        return IOCrud.check_io(con, io);
    }
    public static JsonNode get_io(Connection con, JsonNode jsonNode){
        return IOCrud.get_io(con, jsonNode);
    }
    public static Insertion_Order_List get_io(Connection con, Insertion_Order io){
        return IOCrud.get_io(con, io);
    }
    
    public static JsonNode list_io(Connection con, JsonNode jsonNode){
        return IOCrud.list_io(con, jsonNode);
    }
    public static Insertion_Order_List list_io(Connection con, IOListEntity ioListEntity){
        return IOCrud.list_io(con, ioListEntity);
    }
    
    public static JsonNode list_io_by_status(Connection con, JsonNode jsonNode){
        return IOCrud.list_io_by_status(con, jsonNode);
    }
    public static Insertion_Order_List list_io_by_status(Connection con, IOListEntity ioListEntity){
        return IOCrud.list_io_by_status(con, ioListEntity);
    }
    public static JsonNode list_io_by_account_guid(Connection con, JsonNode jsonNode){
        return IOCrud.list_io_by_account_guid(con, jsonNode);
    }
    public static Insertion_Order_List list_io_by_account_guid(Connection con, IOListEntity ioListEntity){
        return IOCrud.list_io_by_account_guid(con, ioListEntity);
    }
    public static JsonNode list_io_by_account_guid_by_status(Connection con, JsonNode jsonNode){
        return IOCrud.list_io_by_account_guid_by_status(con, jsonNode);
    }
    public static Insertion_Order_List list_io_by_account_guid_by_status(Connection con, IOListEntity ioListEntity){
        return IOCrud.list_io_by_account_guid_by_status(con, ioListEntity);
    }
    public static JsonNode update_io(Connection con, JsonNode jsonNode){
        return IOCrud.update_io(con, jsonNode);
    }
    public static Message update_io(Connection con, Insertion_Order io){
        return IOCrud.update_io(con, io,true);
    }
    public static JsonNode reject_io(Connection con, JsonNode jsonNode){
        return IOCrud.reject_io(con, jsonNode);
    }
    public static Message reject_io(Connection con, Insertion_Order io){
        return IOCrud.reject_io(con, io,true);
    }
    public static JsonNode approve_io(Connection con, JsonNode jsonNode){
        return MixedCrud.approve_io(con, jsonNode);
    }
    public static Message approve_io(Connection con, Insertion_Order io){
        return MixedCrud.approve_io(con, io, true);
    }
    
    
    /*SITE APIs*/
    
    public static JsonNode insert_site(Connection con, JsonNode jsonNode){
        return SiteCrud.insert_site(con, jsonNode);
    }
    public static Message insert_site(Connection con, Site site){
        return SiteCrud.insert_site(con, site, true);
    }
    public static Message insert_site(Connection con, Site site,boolean userSpecifiedGuid){
        return SiteCrud.insert_site(con, site, true,userSpecifiedGuid);
    }
    public static JsonNode update_site(Connection con, JsonNode jsonNode){
        return SiteCrud.update_site(con, jsonNode);
    }
    public static Message update_site(Connection con, Site site){
        return SiteCrud.update_site(con, site, true);
    }
    public static JsonNode change_site_status(Connection con, JsonNode jsonNode){
        return SiteCrud.change_site_status(con, jsonNode);
    }
    public static Message change_site_status(Connection con, SiteListEntity sitelistEntity ){
        return SiteCrud.change_site_status(con, sitelistEntity, true);
    }
    public static JsonNode list_site(Connection con, JsonNode jsonNode){
        return SiteCrud.list_site(con, jsonNode);
    }
    public static SiteList list_site(Connection con, SiteListEntity sitelistEntity){
        return SiteCrud.list_site(con, sitelistEntity);
    }
    public static JsonNode list_site_by_account_guid(Connection con, JsonNode jsonNode){
        return SiteCrud.list_site_by_account_guid(con, jsonNode);
    }
    public static SiteList list_site_by_account_guid(Connection con, SiteListEntity sitelistEntity){
        return SiteCrud.list_site_by_account_guid(con, sitelistEntity);
    }
    public static JsonNode list_site_by_account_id(Connection con, JsonNode jsonNode){
        return SiteCrud.list_site_by_account_id(con, jsonNode);
    }
    public static SiteList list_site_by_account_id(Connection con, SiteListEntity sitelistEntity){
        return SiteCrud.list_site_by_account_id(con, sitelistEntity);
    }
    public static JsonNode list_site_by_account_ids(Connection con, JsonNode jsonNode){
        return SiteCrud.list_site_by_account_ids(con, jsonNode);
    }
    public static SiteList list_site_by_account_ids(Connection con, SiteListEntity sitelistEntity){
        return SiteCrud.list_site_by_account_ids(con, sitelistEntity);
    }
    public static JsonNode get_site(Connection con, JsonNode jsonNode){
        return SiteCrud.get_site(con, jsonNode);
    }
    public static SiteList get_site(Connection con, SiteListEntity sitelistEntity){
        return SiteCrud.get_site(con, sitelistEntity);
    }
    public static Site get_site(Connection con, String siteGuid){
        return SiteCrud.get_site(con, siteGuid);
    }
    public static List<Site> get_site_list_by_pub_guid(Connection con, String pubGuid){
        return SiteCrud.get_site_by_pub_guid(con, pubGuid);
    }
    public static JsonNode approve_site(Connection con, JsonNode jsonNode){
        return SiteCrud.approve_site(con, jsonNode);
    }
    public static Message approve_site(Connection con, Site site){
        return SiteCrud.approve_site(con, site, true);
    }
    public static JsonNode reject_site(Connection con, JsonNode jsonNode){
        return SiteCrud.reject_site(con, jsonNode);
    }
    public static Message reject_site(Connection con, Site site){
        return SiteCrud.reject_site(con, site, true);
    }
    public static JsonNode pause_site(Connection con, JsonNode jsonNode){
        return SiteCrud.pause_site(con, jsonNode);
    }
    public static Message pause_site(Connection con, Site site){
        return SiteCrud.pause_site(con, site, true);
    }
    /* ReqLogging API's */
    public static JsonNode insert_req_logging(Connection con, JsonNode jsonNode){
        return ReqLoggingCrud.insert_req_logging(con, jsonNode);
    }
    public static Message insert_req_logging(Connection con, ReqLoggingEntity reqLoggingEntity){
        return ReqLoggingCrud.insert_req_logging(con, reqLoggingEntity, true);
    }
    public static JsonNode update_req_logging(Connection con, JsonNode jsonNode){
        return ReqLoggingCrud.update_req_logging(con, jsonNode);
    }
    public static Message update_req_logging(Connection con, ReqLoggingEntity reqLoggingEntity){
        return ReqLoggingCrud.update_req_logging(con, reqLoggingEntity, true);
    }
    public static JsonNode check_update_insert_req_logging(Connection con, JsonNode jsonNode){
        return ReqLoggingCrud.check_update_insert_req_logging(con, jsonNode);
    }
    public static Message check_update_insert_req_logging(Connection con, ReqLoggingEntity reqLoggingEntity){
        return ReqLoggingCrud.check_update_insert_req_logging(con, reqLoggingEntity, true);
    }
    public static JsonNode various_get_req_logging(Connection con, JsonNode jsonNode){
        return ReqLoggingCrud.various_get_req_logging(con, jsonNode);
    }
    public static ReqLoggingList various_get_req_logging(Connection con, ReqLoggingInput reqLoggingInput){
        return ReqLoggingCrud.various_get_req_logging(con, reqLoggingInput);
    }

    /* EXT SITE API */
    public static JsonNode various_get_ext_site(Connection con, JsonNode jsonNode){
        return Ext_siteCrud.various_get_ext_site(con, jsonNode);
    }
    public static Ext_site_list various_get_ext_site(Connection con, Ext_site_input ext_site_input){
        return Ext_siteCrud.various_get_ext_site(con, ext_site_input);
    }
    public static JsonNode update_ext_site(Connection con, JsonNode jsonNode){
        return Ext_siteCrud.update_ext_site(con, jsonNode);
    }
    public static Message update_ext_site(Connection con, Ext_site_input ext_site_input){
        return Ext_siteCrud.update_ext_site(con, ext_site_input, true);
    }
    
    /* CAMPAIGN API*/
    public static JsonNode insert_campaign(Connection con, JsonNode jsonNode){
        return CampaignCrud.insert_campaign(con, jsonNode);
    }
    public static Message insert_campaign(Connection con, Campaign campaign){
        return CampaignCrud.insert_campaign(con, campaign, true);
    }
    public static Message insert_campaign(Connection con, Campaign campaign,boolean useSpecifiedGuid){
        return CampaignCrud.insert_campaign(con, campaign, true,useSpecifiedGuid);
    }
    public static JsonNode update_campaign(Connection con, JsonNode jsonNode){
        return CampaignCrud.update_campaign(con, jsonNode);
    }
    public static Message update_campaign(Connection con, Campaign campaign){
        return CampaignCrud.update_campaign(con, campaign, true);
    }
    public static JsonNode list_campaign(Connection con, JsonNode jsonNode){
        return CampaignCrud.list_campaign(con, jsonNode);
    }
    public static CampaignList list_campaign(Connection con, CampaignListEntity campaignlistEntity){
        return CampaignCrud.list_campaign(con, campaignlistEntity);
    }
    public static JsonNode pause_campaign(Connection con, JsonNode jsonNode){
        return CampaignCrud.pause_campaign(con, jsonNode);
    }
    public static Message pause_campaign(Connection con, Campaign campaign){
        return CampaignCrud.pause_campaign(con, campaign, true);
    }
    public static JsonNode activate_campaign(Connection con, JsonNode jsonNode){
        return CampaignCrud.activate_campaign(con, jsonNode);
    }
    public static Message activate_campaign(Connection con, Campaign campaign){
        return CampaignCrud.activate_campaign(con, campaign, true);
    }
    public static Campaign get_campaign(Connection con, String campaignGuid){
        return CampaignCrud.get_campaign(con, campaignGuid);
    }
    
    /*Campaign Budget API*/
    public static JsonNode insert_campaign_budget(Connection con, JsonNode jsonNode){
        return CampaignBudgetCrud.insert_campaign_budget(con, jsonNode);
    }
    public static Message insert_campaign_budget(Connection con, Campaign_budget campaign_budget){
        return CampaignBudgetCrud.insert_campaign_budget(con, campaign_budget, true);
    }
    public static JsonNode update_campaign_budget(Connection con, JsonNode jsonNode){
        return CampaignBudgetCrud.update_campaign_budget(con, jsonNode);
    }
    public static Message update_campaign_budget(Connection con, Campaign_budget campaign_budget){
        return CampaignBudgetCrud.update_campaign_budget(con, campaign_budget, true);
    }
    public static JsonNode get_campaign_budget(Connection con, JsonNode jsonNode){
        return CampaignBudgetCrud.get_campaign_budget(con, jsonNode);
    }
    public static CampaignBudgetList get_campaign_budget(Connection con, CampaignBudgetListEntity campaignBudgetlistEntity){
        return CampaignBudgetCrud.get_campaign_budget(con, campaignBudgetlistEntity);
    }
    public static Campaign_budget get_campaign_budget(String guid,Connection con){
        return CampaignBudgetCrud.get_campaign_budget(guid, con);
    }
    
    /*Targeting Profile APIs*/
    public static JsonNode insert_targeting_profile(Connection con, JsonNode jsonNode){
        return TargetingProfileCrud.insert_targeting_profile(con, jsonNode);
    }    
    public static Message insert_targeting_profile(Connection con, Targeting_profile tp){
        return TargetingProfileCrud.insert_targeting_profile(con, tp, true);
    }
    public static Message insert_targeting_profile_limited_parameters(Connection con,Targeting_profile tp){
        return TargetingProfileCrud.insert_targeting_profile_limited_parameters(con,tp,true);
    }
    public static JsonNode update_targeting_profile(Connection con, JsonNode jsonNode){
        return TargetingProfileCrud.update_targeting_profile(con, jsonNode);
    }    
    public static Message update_targeting_profile(Connection con, Targeting_profile tp){
        return TargetingProfileCrud.update_targeting_profile(con, tp, true);
    }
    public static Message update_targeting_profile_using_provided_country_json(Connection con, Targeting_profile tp){
        return TargetingProfileCrud.update_targeting_profile_using_provided_country_json(con, tp, true,true);
    }
    public static JsonNode various_get_targeting_profile(Connection con, JsonNode jsonNode){
        return TargetingProfileCrud.various_get_targeting_profile(con, jsonNode);
    }
    public static TargetingProfileList various_get_targeting_profile(Connection con, TargetingProfileListEntity tplistEntity){
        return TargetingProfileCrud.various_get_targeting_profile(con, tplistEntity);
    }
    public static JsonNode deactivate_targeting_profile(Connection con, JsonNode jsonNode){
        return TargetingProfileCrud.deactivate_targeting_profile(con, jsonNode);
    }    
    public static Message deactivate_targeting_profile(Connection con, Targeting_profile tp){
        return TargetingProfileCrud.deactivate_targeting_profile(con, tp, true);
    }
    public static Targeting_profile getTargetingProfileUseRawCountryJson(Connection connection,String guid){
        return TargetingProfileCrud.getTargetingProfileUseRawCountryJson(guid,connection);
    }
    
    /*CREATIVE BANNER APIs*/
    public static JsonNode insert_creative_banner(Connection con, JsonNode jsonNode){
        return CreativeBannerCrud.insert_creative_banner(con, jsonNode);
    }    
    public static Message insert_creative_banner(Connection con, Creative_banner creative_banner){
        return CreativeBannerCrud.insert_creative_banner(con, creative_banner, true);
    }
    public static JsonNode update_creative_banner(Connection con, JsonNode jsonNode){
        return CreativeBannerCrud.update_creative_banner(con, jsonNode);
    }    
    public static Message update_creative_banner(Connection con, Creative_banner creative_banner){
        return CreativeBannerCrud.update_creative_banner(con, creative_banner, true);
    }
    public static JsonNode various_get_creative_banner(Connection con, JsonNode jsonNode){
        return CreativeBannerCrud.various_get_creative_banner(con, jsonNode);
    }
    public static CreativeBannerList various_get_creative_banner(Connection con, CreativeBannerListEntity cblistEntity){
        return CreativeBannerCrud.various_get_creative_banner(con, cblistEntity);
    }
    public static JsonNode get_creative_banner_from_container(Connection con, JsonNode jsonNode){
        return MixedCrud.get_creative_banner_from_container(con, jsonNode);
    }
    public static CreativeBannerList get_creative_banner_from_container(Connection con, Creative_container cc){
        return MixedCrud.get_creative_banner_from_container(con, cc);
    }
    
    
    /*CREATIVE CONTAINER APIs*/
    public static JsonNode insert_creative_container(Connection con, JsonNode jsonNode){
        return CreativeContainerCrud.insert_creative_container(con, jsonNode);
    }    
    public static Message insert_creative_container(Connection con, Creative_container cc){
        return CreativeContainerCrud.insert_creative_container(con, cc, true);
    }
    public static Message insert_creative_container(Connection con, Creative_container cc,boolean userProvidedGuid){
        return CreativeContainerCrud.insert_creative_container(con, cc, true,userProvidedGuid);
    }
    public static JsonNode update_creative_container(Connection con, JsonNode jsonNode){
        return CreativeContainerCrud.update_creative_container(con, jsonNode);
    }    
    public static Message update_creative_container(Connection con, Creative_container cc){
        return MixedCrud.update_creative_container(con, cc, true);
    }
    public static Message update_creative_container_using_creative_container_crud(Connection con, Creative_container cc){
        return CreativeContainerCrud.update_creative_container(con, cc, true);
    }
    public static JsonNode various_get_creative_container(Connection con, JsonNode jsonNode){
        return CreativeContainerCrud.various_get_creative_container(con, jsonNode);
    }
    public static CreativeContainerList various_get_creative_container(Connection con, CreativeContainerListEntity cclistEntity){
        return CreativeContainerCrud.various_get_creative_container(con, cclistEntity);
    }
    public static JsonNode update_creative_container_status(Connection con, JsonNode jsonNode){
        return CreativeContainerCrud.update_creative_container_status(con, jsonNode);
    }    
    public static Message update_creative_container_status(Connection con, CreativeContainerListEntity cclistEntity){
        return CreativeContainerCrud.update_creative_container_status(con, cclistEntity, true);
    }
    public static Creative_container getCreativeContainer(Connection connection,String guid){
        return CreativeContainerCrud.getCreative_container(guid,connection);
    }
    
    /*AD APIs*/
    public static JsonNode insert_ad(Connection con, JsonNode jsonNode){
        return AdCrud.insert_ad(con, jsonNode);
    }    
    public static Message insert_ad(Connection con, Ad ad){
        return AdCrud.insert_ad(con, ad, true);
    }
    public static Message insert_ad_using_provided_guid(Connection con, Ad ad){
        return AdCrud.insert_ad_using_provided_guid(con, ad, true,true);
    }
    public static JsonNode update_ad(Connection con, JsonNode jsonNode){
        return AdCrud.update_ad(con, jsonNode);
    }    
    public static Message update_ad(Connection con, Ad ad){
        return AdCrud.update_ad(con, ad, true);
    }
    public static JsonNode reject_ad(Connection con, JsonNode jsonNode){
        return AdCrud.reject_ad(con, jsonNode);
    }    
    public static Message reject_ad(Connection con, Ad ad){
        return AdCrud.reject_ad(con, ad, true);
    }
    public static JsonNode approve_ad(Connection con, JsonNode jsonNode){
        return AdCrud.approve_ad(con, jsonNode);
    }    
    public static Message approve_ad(Connection con, Ad ad){
        return AdCrud.approve_ad(con, ad, true);
    }
    public static JsonNode various_get_ad(Connection con, JsonNode jsonNode){
        return AdCrud.various_get_ad(con, jsonNode);
    }
    public static AdList various_get_ad(Connection con, AdListEntity adlistEntity){
        return AdCrud.various_get_ad(con, adlistEntity);
    }
    public static JsonNode activate_ad(Connection con, JsonNode jsonNode){
        return AdCrud.activate_ad(con, jsonNode);
    }    
    public static Message activate_ad(Connection con, Ad ad){
        return AdCrud.activate_ad(con, ad, true);
    }
    public static JsonNode pause_ad(Connection con, JsonNode jsonNode){
        return AdCrud.pause_ad(con, jsonNode);
    }    
    public static Message pause_ad(Connection con, Ad ad){
        return AdCrud.pause_ad(con, ad, true);
    }
    public static JsonNode change_status_ad(Connection con, JsonNode jsonNode){
        return AdCrud.change_status_ad(con, jsonNode);
    }    
    public static Message change_status_ad(Connection con, AdListEntity adListEntity){
        return AdCrud.change_status_ad(con, adListEntity, true);
    }
    public static Ad get_ad_by_guid(Connection con, String adGuid){
        return AdCrud.geAdByGuid(adGuid,con);
    }
    public static List<Ad> get_ad_by_campaign_guid(Connection con, String targetingGuid){
        return AdCrud.geAdByCampaignGuid(targetingGuid,con);
    }
    
    /*METADATA APIs*/
    
    public static JsonNode get_status(Connection con){
        return MetadataCrud.get_status(con);
    }
    public static JsonNode get_status_by_id(Connection con){
        return MetadataCrud.get_status_by_id(con);
    }
    public static JsonNode get_tier_1_category(Connection con){
        return MetadataCrud.get_tier_1_category(con);
    }
    public static JsonNode get_tier_2_category(Connection con){
        return MetadataCrud.get_tier_2_category(con);
    }
    public static JsonNode get_category_by_id(Connection con){
        return MetadataCrud.get_category_by_id(con);
    }
    public static JsonNode get_app_store_id(Connection con){
        return MetadataCrud.get_app_store_id(con);
    }
    public static JsonNode get_app_store_id_by_id(Connection con){
        return MetadataCrud.get_app_store_id_by_id(con);
    }
    public static JsonNode get_country(Connection con){
        return MetadataCrud.get_country(con);
    }
    public static JsonNode get_country_by_id(Connection con){
        return MetadataCrud.get_country_by_id(con);
    }
    public static JsonNode get_all_isp_by_id(Connection con){
        return MetadataCrud.get_all_isp_by_id(con);
    }
    public static JsonNode get_all_isp_by_country(Connection con, JsonNode jsonNode){
        return MetadataCrud.get_isp_by_country(con, jsonNode);
    }
    public static JsonNode get_creative_slots(Connection con, JsonNode jsonNode){
        return MetadataCrud.get_creative_slots(con);
    }
    public static JsonNode get_creative_slots_by_id(Connection con, JsonNode jsonNode){
        return MetadataCrud.get_creative_slots_by_id(con);
    }
    public static JsonNode get_creative_attributes(Connection con, JsonNode jsonNode){
        return MetadataCrud.get_creative_attributes(con);
    }
    public static JsonNode get_creative_attributes_by_id(Connection con, JsonNode jsonNode){
        return MetadataCrud.get_creative_attributes_by_id(con);
    }
    public static JsonNode get_creative_attributes_by_format_id(Connection con, JsonNode jsonNode){
        return MetadataCrud.get_creative_attributes_by_format_id(con);
    }
    public static JsonNode get_creative_formats(Connection con, JsonNode jsonNode){
        return MetadataCrud.get_creative_formats(con);
    }
    public static JsonNode get_creative_formats_by_id(Connection con, JsonNode jsonNode){
        return MetadataCrud.get_creative_formats_by_id(con);
    }
    public static JsonNode get_handset_manufacturer(Connection con, JsonNode jsonNode){
        return MetadataCrud.get_handset_manufacturer(con);
    }
    public static JsonNode get_handset_manufacturer_by_id(Connection con, JsonNode jsonNode){
        return MetadataCrud.get_handset_manufacturer_by_id(con);
    }
    public static JsonNode get_handset_manufacturer_by_os(Connection con, JsonNode jsonNode){
        return MetadataCrud.get_handset_manufacturer_by_os(con,jsonNode);
    }
    public static JsonNode get_handset_model_by_manufacturer(Connection con, JsonNode jsonNode){
        return MetadataCrud.get_handset_model_by_manufacturer(con, jsonNode);
    }
    public static JsonNode get_handset_os(Connection con, JsonNode jsonNode){
        return MetadataCrud.get_handset_os(con);
    }
    public static JsonNode get_handset_os_by_id(Connection con, JsonNode jsonNode){
        return MetadataCrud.get_handset_os_by_id(con);
    }
    public static JsonNode get_handset_browser(Connection con, JsonNode jsonNode){
        return MetadataCrud.get_handset_browser(con);
    }
    public static JsonNode get_handset_browser_by_id(Connection con, JsonNode jsonNode){
        return MetadataCrud.get_handset_browser_by_id(con);
    }
    public static MetaList get_metalist(Connection con, MetadataType metadataType, MetaInput metaInput){
        return MetadataCrud.get_metalist(con, metadataType, metaInput);
    }
    public static Message insert_update_meta(Connection con, MetadataType metadataType, MetaInput metaInput){
        return MetadataCrud.insert_update_meta(con, metaInput, metadataType, true);
    }
    public static JsonNode get_mma_category_tier1_all(Connection con){
        return get_metalist(con,MetadataType.MMA_CATEGORY_TIER1_ALL,null).toJson();
    }
    public static JsonNode get_mma_category_tier2_by_tier1(Connection con){
        return get_metalist(con,MetadataType.MMA_CATEGORY_TIER2_BY_TIER1,null).toJson();
    }
    public static JsonNode get_mma_category_by_ids(Connection con){
        return get_metalist(con,MetadataType.MMA_CATEGORY_BY_IDS,null).toJson();
    }
    public static JsonNode get_mma_industry_tier1_all(Connection con){
        return get_metalist(con,MetadataType.MMA_INDUSTRY_TIER1_ALL,null).toJson();
    }
    public static JsonNode get_mma_industry_tier2_by_tier1(Connection con){
        return get_metalist(con,MetadataType.MMA_INDUSTRY_TIER2_BY_TIER1,null).toJson();
    }
    public static JsonNode get_mma_industry_by_ids(Connection con){
        return get_metalist(con,MetadataType.MMA_INDUSTRY_BY_IDS,null).toJson();
    }
    public static JsonNode get_channel_tier1_all(Connection con){
        return get_metalist(con,MetadataType.MMA_CATEGORY_TIER1_ALL,null).toJson();
    }
    public static JsonNode get_channel_tier2_by_tier1(Connection con){
        return get_metalist(con,MetadataType.MMA_CATEGORY_TIER2_BY_TIER1,null).toJson();
    }
    public static JsonNode get_channel_by_ids(Connection con){
        return get_metalist(con,MetadataType.MMA_CATEGORY_BY_IDS,null).toJson();
    }
    
    /* LOG API */
    
    public static JsonNode get_log(Connection con,LogEntity logEntity, 
            boolean exportAsCSV, String absoluteFileName){
        return LogEventCrud.get_log(con, logEntity, exportAsCSV, absoluteFileName);
    }

    /*ExtSite API*/
    public static JsonNode get_data(Connection con, ExtSiteReportEntity extsiteReportEntity, 
            boolean returnWithId, boolean exportAsCsv, String absoluteFileName){
        return ExtSiteReportCrud.get_data(con, extsiteReportEntity, exportAsCsv, absoluteFileName);
    }
    
    /*Fraud API*/
    public static JsonNode get_data(Connection con, FraudReportEntity fraudReportEntity, 
            boolean returnWithId, boolean exportAsCsv, String absoluteFileName){
        return FraudReportCrud.get_data(con, fraudReportEntity, exportAsCsv, absoluteFileName);
    }
    /*Tracking Event API*/
    public static JsonNode get_data(Connection con, TrackingEvent trackingEvent, 
            boolean returnWithId, boolean exportAsCsv, String absoluteFileName){
        return TrackingEventCrud.get_data(con, trackingEvent, exportAsCsv, absoluteFileName);
    }
    /*AdStats API*/
    public static JsonNode get_data(Connection con, AdStats adStats, 
            boolean returnWithId, boolean exportAsCsv, String absoluteFileName){
        return AdStatsCrud.get_data(con, adStats, exportAsCsv, absoluteFileName);
    }
    /*UserReport API*/
    public static JsonNode get_data(Connection con, UserReport userReport, 
            boolean returnWithId, boolean exportAsCsv, String absoluteFileName){
        return UserReportCrud.get_data(con, userReport, exportAsCsv, absoluteFileName);
    }
    /*AlgoModel API*/
    public static JsonNode get_data(Connection con, AlgoModelEntity algoModelEntity){
        return AlgoModelCrud.get_data(con, algoModelEntity);
    }
    /*AlgoModel API*/
    public static JsonNode get_data(Connection con, ReqLoggingInput reqLoggingInput){
        return ReqLoggingCrud.get_data(con, reqLoggingInput);
    }
    /*REPORTING API*/
    public static JsonNode get_data(Connection con, ReportingEntity reportingEntity, 
            boolean returnWithId, boolean exportAsCsv, String absoluteFileName){
        return ReportingCrud.get_data(con, reportingEntity, returnWithId, exportAsCsv, absoluteFileName);
    }
    public static JsonNode get_top_n(Connection con, ReportingEntity reportingEntity){
        return ReportingCrud.get_top_n(con, reportingEntity);
    }
    public static JsonNode get_pie(Connection con, ReportingEntity reportingEntity){
        return ReportingCrud.get_pie(con, reportingEntity);
    }
    public static JsonNode get_bar(Connection con, ReportingEntity reportingEntity){
        return ReportingCrud.get_bar(con, reportingEntity);
    }
    public static JsonNode get_data(Connection con, String tz){
        return DashBoardCrud.get_data(con, tz);
    }
    public static JsonNode get_adv_dashboard(Connection con, String tz, String guid){
        return DashBoardCrud.get_adv_dashboard(con, tz, guid);
    }
    public static JsonNode get_pub_dashboard(Connection con, String tz, String guid){
        return DashBoardCrud.get_pub_dashboard(con, tz, guid);
    }
    
    /*REPORTING QUERY/SAVED QUERY APIs*/
    
    public static JsonNode insert_saved_query(Connection con, JsonNode jsonNode){
        return SavedQueryCrud.insert_saved_query(con, jsonNode);
    }
    public static Message insert_saved_query(Connection con, SavedQueryEntity savedQueryEntity){
        return SavedQueryCrud.insert_saved_query(con, savedQueryEntity, true);
    }
    public static JsonNode various_get_saved_query(Connection con, JsonNode jsonNode){
        return SavedQueryCrud.various_get_saved_query(con, jsonNode);
    }
    public static SavedQueryList various_get_saved_query(Connection con, SavedQueryListEntity savedQueryListEntity){
        return SavedQueryCrud.various_get_saved_query(con, savedQueryListEntity);
    }
    public static JsonNode change_status_saved_query(Connection con, JsonNode jsonNode){
        return SavedQueryCrud.change_status_saved_query(con, jsonNode);
    }    
    public static Message change_status_saved_query(Connection con, SavedQueryListEntity savedqueryListEntity){
        return SavedQueryCrud.change_status_saved_query(con, savedqueryListEntity, true);
    }
    public static JsonNode update_saved_query(Connection con, JsonNode jsonNode){
        return SavedQueryCrud.update_saved_query(con, jsonNode);
    }
    public static Message update_saved_query(Connection con, SavedQueryEntity savedQueryEntity){
        return SavedQueryCrud.update_saved_query(con, savedQueryEntity, true);
    }
    
    /*ISP Mapping*/
    public static JsonNode insert_isp_mapping(Connection con, JsonNode jsonNode){
        return Isp_mappingCrud.insert_isp_mapping(con, jsonNode);
    }
    public static Message insert_isp_mapping(Connection con, Isp_mapping isp_mapping , boolean createTransaction){
        return Isp_mappingCrud.insert_isp_mapping(con, isp_mapping, true);
    }
    public static JsonNode various_get_isp_mapping(Connection con, JsonNode jsonNode){
        return Isp_mappingCrud.various_get_isp_mapping(con, jsonNode);
    }
    public static Isp_mappingList various_get_isp_mapping(Connection con, Isp_mappingListEntity isp_mappingListEntity){
        return Isp_mappingCrud.various_get_isp_mapping(con, isp_mappingListEntity);
    }
    public static JsonNode update_isp_mapping(Connection con, JsonNode jsonNode){
        return Isp_mappingCrud.update_isp_mapping(con, jsonNode);
    }
    public static Message update_isp_mapping(Connection con, Isp_mappingListEntity isp_mappingListEntity){
        return Isp_mappingCrud.update_isp_mapping(con, isp_mappingListEntity, true);
    }
    /* Id definition */
    public static JsonNode get_iddefinition_list(Connection con, JsonNode jsonNode){
        return IddefinitionCrud.get_iddefinition_list(con, jsonNode);
    }
    public static IddefinitionList get_iddefinition_list(Connection con,IddefinitionInput iddefinitionInput){
        return IddefinitionCrud.get_iddefinition_list(con, iddefinitionInput);
    }
    
    /*SSP definition*/
    public static JsonNode ssp_global_data(Connection con){
        return SSPCrud.get_global_data(con);
    }
    public static JsonNode insert_update_global_rules(Connection con, JsonNode jsonNode){
        return SSPCrud.insert_update_global_rules(con, jsonNode);
    }
    public static Message insert_update_global_rules(Connection con, SSPEntity sspEntity){
        return SSPCrud.insert_update_global_rules(con, sspEntity, true);
    }

    /*Parent Account definitions*/
    public static JsonNode insertParentAccount(Connection con, JsonNode jsonNode)
    {
        return ParentAccountCrud.insertParentAccount(con, jsonNode);
    }

    public static Message insertParentAccount(Connection con, ParentAccount parentAccount)
    {
        return ParentAccountCrud.insertParentAccount(con, parentAccount, true);
    }

    public static Message insertParentAccount(Connection con, ParentAccount parentAccount,boolean userSpecifiedGuid)
    {
        return ParentAccountCrud.insertParentAccount(con, parentAccount, true,userSpecifiedGuid);
    }

    public static JsonNode updateParentAccount(Connection con, JsonNode jsonNode)
    {
        return ParentAccountCrud.updateParentAccount(con, jsonNode);
    }
    public static Message updateParentAccount(Connection con, ParentAccount parentAccount)
    {
        return ParentAccountCrud.updateParentAccount(con, parentAccount, true);
    }

    public static boolean doesParentAccountExist(Connection con, String parentAccountGuid)
    {
        return null != ParentAccountCrud.getParentAccount(con,parentAccountGuid);
    }

    public static ParentAccount getParentAccount(Connection con, String parentAccountGuid)
    {
        return ParentAccountCrud.getParentAccount(con,parentAccountGuid);
    }

    public static Account getAccount(Connection con, String accountGuid)
    {
        return AccountCrud.getAccount(con, accountGuid);
    }

    /*Parent account definitions end.*/
    
    /*Retargeting Segment definition*/
    public static JsonNode insert_retargeting_segment(Connection con, JsonNode jsonNode){
        return RetargetingSegmentCrud.insert_retargeting_segment(con, jsonNode);
    }
    public static Message insert_retargeting_segment(Connection con, RetargetingSegment retargeting_segment){
        return RetargetingSegmentCrud.insert_retargeting_segment(con, retargeting_segment , true);
    }
    public static JsonNode update_retargeting_segment(Connection con, JsonNode jsonNode){
        return RetargetingSegmentCrud.update_retargeting_segment(con, jsonNode);
    }
    public static Message update_retargeting_segment(Connection con, RetargetingSegment retargeting_segment){
        return RetargetingSegmentCrud.update_retargeting_segment(con, retargeting_segment , true);
    }

    public static JsonNode various_get_retargeting_segments(Connection con, JsonNode jsonNode){
        return RetargetingSegmentCrud.various_get_retargeting_segments(con, jsonNode);
    }
    public static RetargetingSegmentList various_get_retargeting_segments(Connection con, RetargetingSegmentInputEntity retargetingSegmentInputEntity){
        return RetargetingSegmentCrud.various_get_retargeting_segments(con, retargetingSegmentInputEntity);
    }

    public static Message insert_update_ad_budget(Connection connection,
                                                  String adGuid,
                                                  int impressionCap,
                                                  int timeWindowHours,
                                                  int modifiedBy)
    {
        return AdCrud.insertUpdateAdImpressionsBudget(connection,adGuid,impressionCap,timeWindowHours,modifiedBy);
    }

    public static Message insert_update_campaign_impression_budget( Connection connection,
                                                                    String campaignGuid,
                                                                    int impressionCap,
                                                                    int timeWindowHours,
                                                                    int modifiedBy)
    {
        return CampaignCrud.insertUpdateCampaignImpressionsBudget(connection,campaignGuid,impressionCap,timeWindowHours,modifiedBy);
    }
    
    /*NATIVE ICON APIs*/
    public static JsonNode insert_native_icon(Connection con, JsonNode jsonNode){
        return NativeIconCrud.insert_native_icon(con, jsonNode);
    }    
    public static Message insert_native_icon(Connection con, NativeIcon native_icon){
        return NativeIconCrud.insert_native_icon(con, native_icon, true);
    }
    public static JsonNode update_native_icon(Connection con, JsonNode jsonNode){
        return NativeIconCrud.update_native_icon(con, jsonNode);
    }    
    public static Message update_native_icon(Connection con, NativeIcon native_icon){
        return NativeIconCrud.update_native_icon(con, native_icon, true);
    }
    public static JsonNode various_get_native_icon(Connection con, JsonNode jsonNode){
        return NativeIconCrud.various_get_native_icon(con, jsonNode);
    }
    public static NativeIconList various_get_native_icon(Connection con, NativeIconListEntity cblistEntity){
        return NativeIconCrud.various_get_native_icon(con, cblistEntity);
    }
    public static JsonNode get_native_icon_from_container(Connection con, JsonNode jsonNode){
        return MixedCrud.get_native_icon_from_container(con, jsonNode);
    }
    public static NativeIconList get_native_icon_from_container(Connection con, Creative_container cc){
        return MixedCrud.get_native_icon_from_container(con, cc);
    }

    /*NATIVE SCREENSHOT APIs*/
    public static JsonNode insert_native_screenshot(Connection con, JsonNode jsonNode){
        return NativeScreenshotCrud.insert_native_screenshot(con, jsonNode);
    }    
    public static Message insert_native_screenshot(Connection con, NativeScreenshot native_scrrenshot){
        return NativeScreenshotCrud.insert_native_screenshot(con, native_scrrenshot, true);
    }
    public static JsonNode update_native_screenshot(Connection con, JsonNode jsonNode){
        return NativeScreenshotCrud.update_native_screenshot(con, jsonNode);
    }    
    public static Message update_native_screenshot(Connection con, NativeScreenshot native_scrrenshot){
        return NativeScreenshotCrud.update_native_screenshot(con, native_scrrenshot, true);
    }
    public static JsonNode various_get_native_screenshot(Connection con, JsonNode jsonNode){
        return NativeScreenshotCrud.various_get_native_screenshot(con, jsonNode);
    }
    public static NativeScreenshotList various_get_native_screenshot(Connection con, NativeScreenshotListEntity cblistEntity){
        return NativeScreenshotCrud.various_get_native_screenshot(con, cblistEntity);
    }
    public static JsonNode get_native_screenshot_from_container(Connection con, JsonNode jsonNode){
        return MixedCrud.get_native_screenshot_from_container(con, jsonNode);
    }
    public static NativeScreenshotList get_native_screenshot_from_container(Connection con, Creative_container cc){
        return MixedCrud.get_native_screenshot_from_container(con, cc);
    }
    /*VIDEO INFO ICON APIs*/
    public static JsonNode insert_video_info(Connection con, JsonNode jsonNode){
        return VideoInfoCrud.insert_video_info(con, jsonNode);
    }    
    public static Message insert_video_info(Connection con, VideoInfo video_info){
        return VideoInfoCrud.insert_video_info(con, video_info, true);
    }
    public static JsonNode update_video_info(Connection con, JsonNode jsonNode){
        return VideoInfoCrud.update_video_info(con, jsonNode);
    }    
    public static Message update_video_info(Connection con, VideoInfo video_info){
        return VideoInfoCrud.update_video_info(con, video_info, true);
    }
    public static JsonNode various_get_video_info(Connection con, JsonNode jsonNode){
        return VideoInfoCrud.various_get_video_info(con, jsonNode);
    }
    public static VideoInfoList various_get_video_info(Connection con, VideoInfoListEntity cblistEntity){
        return VideoInfoCrud.various_get_video_info(con, cblistEntity);
    }
    public static JsonNode get_video_info_from_container(Connection con, JsonNode jsonNode){
        return MixedCrud.get_video_info_from_container(con, jsonNode);
    }
    public static VideoInfoList get_video_info_from_container(Connection con, Creative_container cc){
        return MixedCrud.get_video_info_from_container(con, cc);
    }

    public static Message insert_pmp_deal(Connection con, PrivateMarketPlaceApiEntity pmp)
    {
        return PrivateMarketPlaceDealCrud.insertPrivateMarketPlaceDeal(pmp,con,true);
    }
}

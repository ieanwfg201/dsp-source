package com.kritter.kumbaya.libraries.query_planner.db;


import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.TreeMap;

import com.kritter.api.entity.reporting.ReportingEntity;
import com.kritter.constants.ChartType;
import com.kritter.constants.Frequency;
import com.kritter.constants.ReportingDIMTypeEnum;
import com.kritter.kumbaya.libraries.data_structs.common.AdId;
import com.kritter.kumbaya.libraries.data_structs.common.AdpositionId;
import com.kritter.kumbaya.libraries.data_structs.common.AdvIncId;
import com.kritter.kumbaya.libraries.data_structs.common.AdvertiserId;
import com.kritter.kumbaya.libraries.data_structs.common.Billedclicks;
import com.kritter.kumbaya.libraries.data_structs.common.Billedcsc;
import com.kritter.kumbaya.libraries.data_structs.common.BrowserId;
import com.kritter.kumbaya.libraries.data_structs.common.CPAGoal;
import com.kritter.kumbaya.libraries.data_structs.common.CampaignId;
import com.kritter.kumbaya.libraries.data_structs.common.ChannelId;
import com.kritter.kumbaya.libraries.data_structs.common.CityId;
import com.kritter.kumbaya.libraries.data_structs.common.ConnectionType;
import com.kritter.kumbaya.libraries.data_structs.common.Conversion;
import com.kritter.kumbaya.libraries.data_structs.common.CountryCarrierId;
import com.kritter.kumbaya.libraries.data_structs.common.CountryId;
import com.kritter.kumbaya.libraries.data_structs.common.CreativeFormatId;
import com.kritter.kumbaya.libraries.data_structs.common.DemandCharges;
import com.kritter.kumbaya.libraries.data_structs.common.DeviceManufacturerId;
import com.kritter.kumbaya.libraries.data_structs.common.DeviceModelId;
import com.kritter.kumbaya.libraries.data_structs.common.DeviceOsId;
import com.kritter.kumbaya.libraries.data_structs.common.Device_type;
import com.kritter.kumbaya.libraries.data_structs.common.DspNoFill;
import com.kritter.kumbaya.libraries.data_structs.common.Earning;
import com.kritter.kumbaya.libraries.data_structs.common.ExchangePayout;
import com.kritter.kumbaya.libraries.data_structs.common.ExchangeRevenue;
import com.kritter.kumbaya.libraries.data_structs.common.Ext_site;
import com.kritter.kumbaya.libraries.data_structs.common.KumbayaReportingConfiguration;
import com.kritter.kumbaya.libraries.data_structs.common.Marketplace;
import com.kritter.kumbaya.libraries.data_structs.common.NetworkPayout;
import com.kritter.kumbaya.libraries.data_structs.common.NetworkRevenue;
import com.kritter.kumbaya.libraries.data_structs.common.NofillReason;
import com.kritter.kumbaya.libraries.data_structs.common.PostimpEvent;
import com.kritter.kumbaya.libraries.data_structs.common.PubIncId;
import com.kritter.kumbaya.libraries.data_structs.common.PublisherId;
import com.kritter.kumbaya.libraries.data_structs.common.ReqState;
import com.kritter.kumbaya.libraries.data_structs.common.SiteId;
import com.kritter.kumbaya.libraries.data_structs.common.Site_hygiene;
import com.kritter.kumbaya.libraries.data_structs.common.StateId;
import com.kritter.kumbaya.libraries.data_structs.common.SupplyCost;
import com.kritter.kumbaya.libraries.data_structs.common.Supply_source_type;
import com.kritter.kumbaya.libraries.data_structs.common.TABLE;
import com.kritter.kumbaya.libraries.data_structs.common.TerminationReason;
import com.kritter.kumbaya.libraries.data_structs.common.Tevent;
import com.kritter.kumbaya.libraries.data_structs.common.Teventtype;
import com.kritter.kumbaya.libraries.data_structs.common.Total_Request_To_Dsp;
import com.kritter.kumbaya.libraries.data_structs.common.Total_bidValue;
import com.kritter.kumbaya.libraries.data_structs.common.Total_click;
import com.kritter.kumbaya.libraries.data_structs.common.Total_count;
import com.kritter.kumbaya.libraries.data_structs.common.Total_csc;
import com.kritter.kumbaya.libraries.data_structs.common.Total_event;
import com.kritter.kumbaya.libraries.data_structs.common.Total_event_type;
import com.kritter.kumbaya.libraries.data_structs.common.Total_floor;
import com.kritter.kumbaya.libraries.data_structs.common.Total_impression;
import com.kritter.kumbaya.libraries.data_structs.common.Total_request;
import com.kritter.kumbaya.libraries.data_structs.common.Total_win;
import com.kritter.kumbaya.libraries.data_structs.common.Total_win_bidValue;
import com.kritter.kumbaya.libraries.data_structs.query_planner.ColumnType;
import com.kritter.kumbaya.libraries.data_structs.query_planner.ErrorCode;
import com.kritter.kumbaya.libraries.data_structs.query_planner.Header;
import com.kritter.kumbaya.libraries.data_structs.query_planner.HeaderType;
import com.kritter.kumbaya.libraries.data_structs.query_planner.KFilterFields;
import com.kritter.kumbaya.libraries.data_structs.query_planner.KumbayaQueryPlanner;
import com.kritter.kumbaya.libraries.data_structs.query_planner.METRICTYPE;
import com.kritter.kumbaya.libraries.query_planner.common.IQueryPlanner;
import com.kritter.kumbaya.libraries.query_planner.helper.HelperKumbayaQueryPlanner;

public class DBQueryPlanner implements IQueryPlanner {

    private static final String aliasPrefix = "a";
    private Integer aliasCounter= new Integer(0);
    private HashMap<String, String> aliasMap = new LinkedHashMap<String, String>();
    private StringBuffer pieProjection = new StringBuffer("");
    
    public void resolve_none_frequency(ReportingEntity reportingEntity){
        if(reportingEntity != null && reportingEntity.getFrequency() == Frequency.ADMIN_INTERNAL_HOURLY){
            reportingEntity.setFrequency(Frequency.ADMIN_INTERNAL_HOURLY);
        }
    }
    @Override
    public void convert(KumbayaQueryPlanner kQueryPlanner,
            KumbayaReportingConfiguration kReportingConfiguration, ReportingEntity reportingEntity, boolean returnId) {
        
        if(reportingEntity == null || kQueryPlanner == null || kReportingConfiguration == null){
            return;
        }
        HashMap<String, String> kprojectionMap = new LinkedHashMap<String, String>();
        HashSet<KFilterFields> kFilterSet = new LinkedHashSet<KFilterFields>();
        HashSet<String> kgroupbyHashSet = new LinkedHashSet<String>();
        HashMap<String, String> kjoinMap = new LinkedHashMap<String, String>();
        HashSet<String> korderbyHashSet = new LinkedHashSet<String>();
        TreeMap<Integer, String> korderbyTreeMap = new TreeMap<Integer, String>();
        LinkedList<Header> headerList = new LinkedList<Header>();
    //    String date_format_string = null;
        Frequency frequency = Frequency.DATERANGE;
        TABLE table = TABLE.first_level;
        if(reportingEntity.getFrequency() != null){
            resolve_none_frequency(reportingEntity);
            frequency = reportingEntity.getFrequency();
        }
        table = HelperKumbayaQueryPlanner.getTABLE(frequency, reportingEntity.getReportingDIMTypeEnum(),
        		reportingEntity.getReportingTableType());
        if(table == null){
            HelperKumbayaQueryPlanner.populatePlanStatus(kQueryPlanner, ErrorCode.TABLE_NOT_FOUND, ErrorCode.TABLE_NOT_FOUND.toString());
            return;
        }
        String table_name=table.toString();
        aliasCounter = aliasCounter + HelperKumbayaQueryPlanner.setAlias(aliasPrefix, aliasMap, table_name, aliasCounter);
        //date_format_string = HelperKumbayaQueryPlanner.createDateFormat(frequency);
        //if(date_format_string == null){
        //    HelperKumbayaQueryPlanner.populatePlanStatus(kQueryPlanner, ErrorCode.DATE_FORMAT_INCORRECT, ErrorCode.DATE_FORMAT_INCORRECT.toString());
        //    return;
        //}
        if(reportingEntity.getStart_time_str() == null || "".equals(reportingEntity.getStart_time_str().trim())){;
            HelperKumbayaQueryPlanner.populatePlanStatus(kQueryPlanner, ErrorCode.START_TIME_ABSENT, ErrorCode.START_TIME_ABSENT.toString());
            return;
        }else{
            //start_time_string = TransformDate.convertDate(reportingEntity.getStart_time(), date_format_string, 
            //        kReportingConfiguration.getQuery_Destination().getTz());
            String table_alias  = aliasMap.get(table_name);
            HelperKumbayaQueryPlanner.populateMap(kjoinMap, table_name, table_alias);
            if(reportingEntity.isDate_as_dimension()){
                HelperKumbayaQueryPlanner.populateSet(kgroupbyHashSet, table_alias+"."+kReportingConfiguration.getStart_time().getFact_column());
                HelperKumbayaQueryPlanner.populateMap(kprojectionMap, table_alias+"."+kReportingConfiguration.getStart_time().getFact_column(),
                    kReportingConfiguration.getStart_time().getReturn_prefix());
                HelperKumbayaQueryPlanner.addToHeader(HeaderType.STRING, kReportingConfiguration.getStart_time().getReturn_prefix(), HeaderType.STRING, 
                        kReportingConfiguration.getStart_time().getReturn_prefix(),headerList,ColumnType.DIM,true, false);
            }
            HelperKumbayaQueryPlanner.populateTimeRangeStarttime(reportingEntity.getStart_time_str(), 
                    table_alias+"."+kReportingConfiguration.getStart_time().getFact_column(), kQueryPlanner);
        }
        if(reportingEntity.getEnd_time_str() == null || "".equals(reportingEntity.getEnd_time_str().trim())){;
            HelperKumbayaQueryPlanner.populatePlanStatus(kQueryPlanner, ErrorCode.END_TIME_ABSENT, ErrorCode.END_TIME_ABSENT.toString());
            return;
        }else{
            //end_time_string = TransformDate.convertDate(reportingEntity.getEnd_time(), date_format_string, 
            //        kReportingConfiguration.getQuery_Destination().getTz());
            String table_alias  = aliasMap.get(table_name);
            HelperKumbayaQueryPlanner.populateMap(kjoinMap, table_name, table_alias);
            HelperKumbayaQueryPlanner.populateTimeRangeEndtime(reportingEntity.getEnd_time_str(), 
                    table_alias+"."+kReportingConfiguration.getEnd_time().getFact_column(), kQueryPlanner);
        }
        if(reportingEntity.getPubId() != null){
            if(reportingEntity.getReportingDIMTypeEnum() == ReportingDIMTypeEnum.EXHAUSTIVE){
                PublisherId entity = kReportingConfiguration.getPublisherId();
                aliasCounter = aliasCounter + HelperKumbayaQueryPlanner.setAlias(aliasPrefix, aliasMap, entity.getFirst_table(), aliasCounter);
                aliasCounter = aliasCounter + HelperKumbayaQueryPlanner.setAlias(aliasPrefix, aliasMap, entity.getSecond_table(), aliasCounter);
                aliasCounter = aliasCounter + HelperKumbayaQueryPlanner.setAlias(aliasPrefix, aliasMap, table_name, aliasCounter);
                HelperKumbayaQueryPlanner.populateHierarchyFromList(reportingEntity.getPubId(), entity.getFirst_table(), entity.getFirst_column(), 
                        entity.getFirst_column_name(), entity.getSecond_table(), entity.getSecond_column_name(), entity.getUiname(), 
                        entity.getReturn_prefix(), entity.getFact_column(), entity.getSecond_fact_column(), table_name, 
                        returnId, kprojectionMap, kFilterSet, kgroupbyHashSet, kjoinMap, aliasMap, korderbyHashSet, headerList, 
                        reportingEntity.isPubId_clickable());
            }else{
                PubIncId entity = kReportingConfiguration.getPubincId();
                aliasCounter = aliasCounter + HelperKumbayaQueryPlanner.setAlias(aliasPrefix, aliasMap, entity.getDim_table(), aliasCounter);
                HelperKumbayaQueryPlanner.populateFromList(entity.return_prefix, reportingEntity.getPubId(), table_name, 
                        entity.getFact_column(), entity.getDim_table(), entity.getDim_column(), entity.getDim_column_name(),
                        returnId, kprojectionMap, kFilterSet, kgroupbyHashSet, kjoinMap, aliasMap, korderbyHashSet, headerList, entity.getUiname(),
                        reportingEntity.isReturnGuid(),null, reportingEntity.isPubId_clickable(), false);
            }
        }
        if(reportingEntity.getSite_hygiene() != null){
        }
        if(reportingEntity.getAdvertiserId() != null){
             AdvertiserId entity = kReportingConfiguration.getAdvertiserId();
             aliasCounter = aliasCounter + HelperKumbayaQueryPlanner.setAlias(aliasPrefix, aliasMap, entity.getFirst_table(), aliasCounter);
             aliasCounter = aliasCounter + HelperKumbayaQueryPlanner.setAlias(aliasPrefix, aliasMap, entity.getSecond_table(), aliasCounter);
             aliasCounter = aliasCounter + HelperKumbayaQueryPlanner.setAlias(aliasPrefix, aliasMap, table_name, aliasCounter);
             HelperKumbayaQueryPlanner.populateAdvertiserId(reportingEntity.getAdvertiserId(), entity.getFirst_table(), 
                     entity.getFirst_column_filter(),  entity.getFirst_column(), entity.getFirst_column_name(), 
                     entity.getSecond_table(),  entity.getSecond_column_name(), entity.getUiname(), 
                     entity.getReturn_prefix(), entity.getFact_column(), entity.getSecond_fact_column(), 
                     returnId, kprojectionMap, kFilterSet, kgroupbyHashSet, kjoinMap, aliasMap, korderbyHashSet, headerList, table_name,
                     reportingEntity.isAdvertiserId_clickable());
        }
        if(reportingEntity.getAdvId() != null){
            AdvIncId entity = kReportingConfiguration.getAdvincId();
            aliasCounter = aliasCounter + HelperKumbayaQueryPlanner.setAlias(aliasPrefix, aliasMap, entity.getDim_table(), aliasCounter);
            HelperKumbayaQueryPlanner.populateFromList(entity.return_prefix, reportingEntity.getAdvId(), table_name, 
                    entity.getFact_column(), entity.getDim_table(), entity.getDim_column(), entity.getDim_column_name(),
                    returnId, kprojectionMap, kFilterSet, kgroupbyHashSet, kjoinMap, aliasMap, korderbyHashSet, headerList, entity.getUiname(),
                    reportingEntity.isReturnGuid(),null, reportingEntity.isAdvId_clickable(), false);
        }
        
        if(reportingEntity.getSiteId() != null){
            SiteId entity = kReportingConfiguration.getSiteId();
            boolean siteGuid= reportingEntity.isReturnGuid() || reportingEntity.isReturnSiteGuid();
            aliasCounter = aliasCounter + HelperKumbayaQueryPlanner.setAlias(aliasPrefix, aliasMap, entity.getDim_table(), aliasCounter);
            HelperKumbayaQueryPlanner.populateFromList(entity.return_prefix, reportingEntity.getSiteId(), table_name, 
                    entity.getFact_column(), entity.getDim_table(), entity.getDim_column(), entity.getDim_column_name(),
                    returnId, kprojectionMap, kFilterSet, kgroupbyHashSet, kjoinMap, aliasMap, korderbyHashSet, headerList, entity.getUiname(),
                    siteGuid,entity.getDim_guid(), reportingEntity.isSiteId_clickable(), reportingEntity.isSiteId_just_filter());
        }
        if(reportingEntity.getChannelId() != null){
            ChannelId entity = kReportingConfiguration.getChannelId();
            aliasCounter = aliasCounter + HelperKumbayaQueryPlanner.setAlias(aliasPrefix, aliasMap, entity.getDim_table(), aliasCounter);
            HelperKumbayaQueryPlanner.populateFromList(entity.return_prefix, reportingEntity.getChannelId(), table_name, 
                    entity.getFact_column(), entity.getDim_table(), entity.getDim_column(), entity.getDim_column_name(),
                    returnId, kprojectionMap, kFilterSet, kgroupbyHashSet, kjoinMap, aliasMap, korderbyHashSet, headerList, entity.getUiname(),
                    false,null, reportingEntity.isChannelId_clickable(), false);
        }
        if(reportingEntity.getAdpositionId() != null){
            AdpositionId entity = kReportingConfiguration.getAdpositionId();
            aliasCounter = aliasCounter + HelperKumbayaQueryPlanner.setAlias(aliasPrefix, aliasMap, entity.getDim_table(), aliasCounter);
            HelperKumbayaQueryPlanner.populateFromList(entity.return_prefix, reportingEntity.getAdpositionId(), table_name, 
                    entity.getFact_column(), entity.getDim_table(), entity.getDim_column(), entity.getDim_column_name(),
                    returnId, kprojectionMap, kFilterSet, kgroupbyHashSet, kjoinMap, aliasMap, korderbyHashSet, headerList, entity.getUiname(),
                    false,null, reportingEntity.isAdpositionId_clickable(), false);
        }
        if(reportingEntity.getSupply_source_type() != null){
            Supply_source_type entity = kReportingConfiguration.getSupply_source_type();
            aliasCounter = aliasCounter + HelperKumbayaQueryPlanner.setAlias(aliasPrefix, aliasMap, entity.getDim_table(), aliasCounter);
            HelperKumbayaQueryPlanner.populateFromList(entity.return_prefix, reportingEntity.getSupply_source_type(), table_name, 
                    entity.getFact_column(), entity.getDim_table(), entity.getDim_column(), entity.getDim_column_name(),
                    returnId, kprojectionMap, kFilterSet, kgroupbyHashSet, kjoinMap, aliasMap, korderbyHashSet, headerList, entity.getUiname(),
                    false,null, reportingEntity.isSupply_source_type_clickable(), false);
        }
        if(reportingEntity.getExt_site() != null){
            Ext_site entity = kReportingConfiguration.getExt_site();
            aliasCounter = aliasCounter + HelperKumbayaQueryPlanner.setAlias(aliasPrefix, aliasMap, entity.getDim_table(), aliasCounter);
            HelperKumbayaQueryPlanner.populateFromList(entity.return_prefix, reportingEntity.getExt_site(), table_name, 
                    entity.getFact_column(), entity.getDim_table(), entity.getDim_column(), entity.getDim_column_name(),
                    returnId, kprojectionMap, kFilterSet, kgroupbyHashSet, kjoinMap, aliasMap, korderbyHashSet, headerList, entity.getUiname(),
                    false,null, reportingEntity.isExt_site_clickable(), false);
        }
        if(reportingEntity.getCampaignId() != null){
            CampaignId entity = kReportingConfiguration.getCampaignId();
            aliasCounter = aliasCounter + HelperKumbayaQueryPlanner.setAlias(aliasPrefix, aliasMap, entity.getDim_table(), aliasCounter);
            HelperKumbayaQueryPlanner.populateFromList(entity.return_prefix, reportingEntity.getCampaignId(), table_name, 
                    entity.getFact_column(), entity.getDim_table(), entity.getDim_column(), entity.getDim_column_name(),
                    returnId, kprojectionMap, kFilterSet, kgroupbyHashSet, kjoinMap, aliasMap, korderbyHashSet, headerList, entity.getUiname()
                    , false, null, reportingEntity.isCampaignId_clickable(), reportingEntity.isCampaignId_just_filter());
        }
        if(reportingEntity.getAdId() != null){
            AdId entity = kReportingConfiguration.getAdId();
            aliasCounter = aliasCounter + HelperKumbayaQueryPlanner.setAlias(aliasPrefix, aliasMap, entity.getDim_table(), aliasCounter);
            HelperKumbayaQueryPlanner.populateFromList(entity.return_prefix, reportingEntity.getAdId(), table_name, 
                    entity.getFact_column(), entity.getDim_table(), entity.getDim_column(), entity.getDim_column_name(),
                    returnId, kprojectionMap, kFilterSet, kgroupbyHashSet, kjoinMap, aliasMap, korderbyHashSet, headerList, entity.getUiname()
                    , reportingEntity.isReturnAdGuid(), "guid", reportingEntity.isAdId_clickable(), reportingEntity.isAdId_just_filter());
        }
        if(reportingEntity.getMarketplace() != null){
            Marketplace entity = kReportingConfiguration.getMarketplace();
            aliasCounter = aliasCounter + HelperKumbayaQueryPlanner.setAlias(aliasPrefix, aliasMap, entity.getDim_table(), aliasCounter);
            HelperKumbayaQueryPlanner.populateFromList(entity.return_prefix, reportingEntity.getMarketplace(), table_name, 
                    entity.getFact_column(), entity.getDim_table(), entity.getDim_column(), entity.getDim_column_name(),
                    returnId, kprojectionMap, kFilterSet, kgroupbyHashSet, kjoinMap, aliasMap, korderbyHashSet, headerList, entity.getUiname()
                    , false, null, reportingEntity.isMarketplace_clickable(), false);
        }
        if(reportingEntity.getDeviceId() != null){
            
        }
        if(reportingEntity.getDeviceManufacturerId() != null){
            DeviceManufacturerId entity = kReportingConfiguration.getDeviceManufacturerId();
            aliasCounter = aliasCounter + HelperKumbayaQueryPlanner.setAlias(aliasPrefix, aliasMap, entity.getDim_table(), aliasCounter);
            HelperKumbayaQueryPlanner.populateFromList(entity.return_prefix, reportingEntity.getDeviceManufacturerId(), table_name, 
                    entity.getFact_column(), entity.getDim_table(), entity.getDim_column(), 
                    entity.getDim_column_name(),returnId, kprojectionMap, kFilterSet, kgroupbyHashSet, kjoinMap, aliasMap,
                    korderbyHashSet, headerList, entity.getUiname(), false, null, reportingEntity.isDeviceManufacturerId_clickable(), false);
        }
        if(reportingEntity.getDeviceModelId() != null){
            DeviceModelId entity = kReportingConfiguration.getDeviceModelId();
            aliasCounter = aliasCounter + HelperKumbayaQueryPlanner.setAlias(aliasPrefix, aliasMap, entity.getDim_table(), aliasCounter);
            HelperKumbayaQueryPlanner.populateFromList(entity.return_prefix, reportingEntity.getDeviceModelId(), table_name, 
                    entity.getFact_column(), entity.getDim_table(), entity.getDim_column(), 
                    entity.getDim_column_name(),returnId, kprojectionMap, kFilterSet, kgroupbyHashSet, kjoinMap, aliasMap,
                    korderbyHashSet, headerList, entity.getUiname(), false, null, false, false);
        }
        if(reportingEntity.getDeviceOsId() != null){
            DeviceOsId entity = kReportingConfiguration.getDeviceOsId();
            aliasCounter = aliasCounter + HelperKumbayaQueryPlanner.setAlias(aliasPrefix, aliasMap, entity.getDim_table(), aliasCounter);
            HelperKumbayaQueryPlanner.populateFromList(entity.return_prefix, reportingEntity.getDeviceOsId(), table_name, 
                    entity.getFact_column(), entity.getDim_table(), entity.getDim_column(), 
                    entity.getDim_column_name(),returnId, kprojectionMap, kFilterSet, kgroupbyHashSet, kjoinMap, aliasMap,
                    korderbyHashSet, headerList, entity.getUiname(), false, null, reportingEntity.isDeviceOsId_clickable(), false);
        }
        if(reportingEntity.getDevice_type() != null){
            Device_type entity = kReportingConfiguration.getDevice_type();
            aliasCounter = aliasCounter + HelperKumbayaQueryPlanner.setAlias(aliasPrefix, aliasMap, entity.getDim_table(), aliasCounter);
            HelperKumbayaQueryPlanner.populateFromList(entity.return_prefix, reportingEntity.getDevice_type(), table_name, 
                    entity.getFact_column(), entity.getDim_table(), entity.getDim_column(), 
                    entity.getDim_column_name(),returnId, kprojectionMap, kFilterSet, kgroupbyHashSet, kjoinMap, aliasMap,
                    korderbyHashSet, headerList, entity.getUiname(), false, null, reportingEntity.isDevice_type_clickable(), false);
        }
        if(reportingEntity.getBrowserId() != null){
            BrowserId entity = kReportingConfiguration.getBrowserId();
            aliasCounter = aliasCounter + HelperKumbayaQueryPlanner.setAlias(aliasPrefix, aliasMap, entity.getDim_table(), aliasCounter);
            HelperKumbayaQueryPlanner.populateFromList(entity.return_prefix, reportingEntity.getBrowserId(), table_name, 
                    entity.getFact_column(), entity.getDim_table(), entity.getDim_column(), 
                    entity.getDim_column_name(),returnId, kprojectionMap, kFilterSet, kgroupbyHashSet, kjoinMap, aliasMap,
                    korderbyHashSet, headerList, entity.getUiname(), false, null, reportingEntity.isBrowserId_clickable(), false);
        }
        if(reportingEntity.getCountryId() != null){
            CountryId entity = kReportingConfiguration.getCountryId();
            aliasCounter = aliasCounter + HelperKumbayaQueryPlanner.setAlias(aliasPrefix, aliasMap, entity.getDim_table(), aliasCounter);
            HelperKumbayaQueryPlanner.populateFromList(entity.return_prefix, reportingEntity.getCountryId(), table_name, 
                    entity.getFact_column(), entity.getDim_table(), entity.getDim_column(), 
                    entity.getDim_column_name(),returnId, kprojectionMap, kFilterSet, kgroupbyHashSet, kjoinMap, aliasMap,
                    korderbyHashSet, headerList, entity.getUiname(),false, null, reportingEntity.isCountryId_clickable(), false);
        }
        if(reportingEntity.getCountryRegionId() != null){
        }
        if(reportingEntity.getCountryCarrierId() != null){
            CountryCarrierId entity = kReportingConfiguration.getCountryCarrierId() ;
            aliasCounter = aliasCounter + HelperKumbayaQueryPlanner.setAlias(aliasPrefix, aliasMap, entity.getDim_table(), aliasCounter);
            HelperKumbayaQueryPlanner.populateFromList(entity.return_prefix, reportingEntity.getCountryCarrierId(), table_name, 
                    entity.getFact_column(), entity.getDim_table(), entity.getDim_column(), 
                    entity.getDim_column_name(),returnId, kprojectionMap, kFilterSet, kgroupbyHashSet, kjoinMap, aliasMap,
                    korderbyHashSet, headerList, entity.getUiname(), false, null, reportingEntity.isCountryCarrierId_clickable(), false);
        }
        if(reportingEntity.getStateId() != null){
            StateId entity = kReportingConfiguration.getStateId();
            aliasCounter = aliasCounter + HelperKumbayaQueryPlanner.setAlias(aliasPrefix, aliasMap, entity.getDim_table(), aliasCounter);
            HelperKumbayaQueryPlanner.populateFromList(entity.return_prefix, reportingEntity.getStateId(), table_name, 
                    entity.getFact_column(), entity.getDim_table(), entity.getDim_column(), entity.getDim_column_name(),
                    returnId, kprojectionMap, kFilterSet, kgroupbyHashSet, kjoinMap, aliasMap, korderbyHashSet, headerList, entity.getUiname(),
                    false,null, reportingEntity.isStateId_clickable(), false);
        }
        if(reportingEntity.getCityeId() != null){
            CityId entity = kReportingConfiguration.getCityId();
            aliasCounter = aliasCounter + HelperKumbayaQueryPlanner.setAlias(aliasPrefix, aliasMap, entity.getDim_table(), aliasCounter);
            HelperKumbayaQueryPlanner.populateFromList(entity.return_prefix, reportingEntity.getCityeId(), table_name, 
                    entity.getFact_column(), entity.getDim_table(), entity.getDim_column(), entity.getDim_column_name(),
                    returnId, kprojectionMap, kFilterSet, kgroupbyHashSet, kjoinMap, aliasMap, korderbyHashSet, headerList, entity.getUiname(),
                    false,null, reportingEntity.isCityId_clickable(), false);
        }
        if(reportingEntity.getFormatId() != null){
            CreativeFormatId entity = kReportingConfiguration.getCreativeFormatId();
            aliasCounter = aliasCounter + HelperKumbayaQueryPlanner.setAlias(aliasPrefix, aliasMap, entity.getDim_table(), aliasCounter);
            HelperKumbayaQueryPlanner.populateFromList(entity.return_prefix, reportingEntity.getFormatId(), table_name, 
                    entity.getFact_column(), entity.getDim_table(), entity.getDim_column(), entity.getDim_column_name(),
                    returnId, kprojectionMap, kFilterSet, kgroupbyHashSet, kjoinMap, aliasMap, korderbyHashSet, headerList, entity.getUiname(),
                    false,null, reportingEntity.isFormatId_clickable(), false);
        }
        if(reportingEntity.getConnection_type() != null){
            ConnectionType entity = kReportingConfiguration.getConnectionType() ;
            aliasCounter = aliasCounter + HelperKumbayaQueryPlanner.setAlias(aliasPrefix, aliasMap, entity.getDim_table(), aliasCounter);
            HelperKumbayaQueryPlanner.populateFromList(entity.return_prefix, reportingEntity.getConnection_type(), table_name, 
                    entity.getFact_column(), entity.getDim_table(), entity.getDim_column(), 
                    entity.getDim_column_name(),returnId, kprojectionMap, kFilterSet, kgroupbyHashSet, kjoinMap, aliasMap,
                    korderbyHashSet, headerList, entity.getUiname(), false, null, reportingEntity.isConnection_type_clickable(), false);
        }
        if(reportingEntity.getExchangeId() != null){
        }
        if(reportingEntity.getCreativeId() != null){
        }
        if(reportingEntity.getBidderModelId() != null){
        }
        if(reportingEntity.getNofillReason() != null){
            NofillReason entity = kReportingConfiguration.getNofillReason();
            aliasCounter = aliasCounter + HelperKumbayaQueryPlanner.setAlias(aliasPrefix, aliasMap, table_name, aliasCounter);
            HelperKumbayaQueryPlanner.populateFromFactTable(entity.return_prefix, reportingEntity.getNofillReason(), table_name, 
                    entity.getFact_column(), kprojectionMap, kFilterSet, kgroupbyHashSet, kjoinMap, aliasMap,
                    korderbyHashSet, headerList, entity.getUiname(), false);
        }
        if(reportingEntity.getReqState() != null){
            ReqState entity = kReportingConfiguration.getReqState();
            aliasCounter = aliasCounter + HelperKumbayaQueryPlanner.setAlias(aliasPrefix, aliasMap, table_name, aliasCounter);
            HelperKumbayaQueryPlanner.populateFromFactTable(entity.return_prefix, reportingEntity.getReqState(), table_name, 
                    entity.getFact_column(), kprojectionMap, kFilterSet, kgroupbyHashSet, kjoinMap, aliasMap,
                    korderbyHashSet, headerList, entity.getUiname(), false);
        }
        if(reportingEntity.getDspNoFill() != null){
            DspNoFill entity = kReportingConfiguration.getDspNoFill();
            aliasCounter = aliasCounter + HelperKumbayaQueryPlanner.setAlias(aliasPrefix, aliasMap, table_name, aliasCounter);
            HelperKumbayaQueryPlanner.populateFromFactTable(entity.return_prefix, reportingEntity.getDspNoFill(), table_name, 
                    entity.getFact_column(), kprojectionMap, kFilterSet, kgroupbyHashSet, kjoinMap, aliasMap,
                    korderbyHashSet, headerList, entity.getUiname(), false);
        }
        if(reportingEntity.getTerminationReason() != null){
            TerminationReason entity = kReportingConfiguration.getTerminationReason();
            aliasCounter = aliasCounter + HelperKumbayaQueryPlanner.setAlias(aliasPrefix, aliasMap, table_name, aliasCounter);
            HelperKumbayaQueryPlanner.populateFromFactTable(entity.return_prefix, reportingEntity.getTerminationReason(), table_name, 
                    entity.getFact_column(), kprojectionMap, kFilterSet, kgroupbyHashSet, kjoinMap, aliasMap,
                    korderbyHashSet, headerList, entity.getUiname(), false);
        }
        if(reportingEntity.getPostimpevent() != null){
            PostimpEvent entity = kReportingConfiguration.getPostimpevent();
            aliasCounter = aliasCounter + HelperKumbayaQueryPlanner.setAlias(aliasPrefix, aliasMap, table_name, aliasCounter);
            HelperKumbayaQueryPlanner.populateFromFactTable(entity.return_prefix, reportingEntity.getPostimpevent(), table_name, 
                    entity.getFact_column(), kprojectionMap, kFilterSet, kgroupbyHashSet, kjoinMap, aliasMap,
                    korderbyHashSet, headerList, entity.getUiname(), false);
        }
        if(reportingEntity.getTevent() != null){
            Tevent entity = kReportingConfiguration.getTevent();
            aliasCounter = aliasCounter + HelperKumbayaQueryPlanner.setAlias(aliasPrefix, aliasMap, table_name, aliasCounter);
            HelperKumbayaQueryPlanner.populateFromFactTable(entity.return_prefix, reportingEntity.getTevent(), table_name, 
                    entity.getFact_column(), kprojectionMap, kFilterSet, kgroupbyHashSet, kjoinMap, aliasMap,
                    korderbyHashSet, headerList, entity.getUiname(), false);
        }
        if(reportingEntity.getTeventtype() != null){
            Teventtype entity = kReportingConfiguration.getTeventtype();
            aliasCounter = aliasCounter + HelperKumbayaQueryPlanner.setAlias(aliasPrefix, aliasMap, table_name, aliasCounter);
            HelperKumbayaQueryPlanner.populateFromFactTable(entity.return_prefix, reportingEntity.getTeventtype(), table_name, 
                    entity.getFact_column(), kprojectionMap, kFilterSet, kgroupbyHashSet, kjoinMap, aliasMap,
                    korderbyHashSet, headerList, entity.getUiname(), false);
        }
        if(reportingEntity.isTotal_event()){
            Total_event entity = kReportingConfiguration.getTotal_event();
            HelperKumbayaQueryPlanner.populateMetric(entity.getReturn_prefix(), true, table_name,  entity.getFact_column(), METRICTYPE.SUM,
                    kprojectionMap, kFilterSet, kgroupbyHashSet, kjoinMap, aliasMap, korderbyHashSet, headerList, HeaderType.LONG, 
                    entity.getUiname(), reportingEntity.getTotal_event_order_sequence(), korderbyTreeMap,reportingEntity.getChartType(),
                    pieProjection);
        }
        if(reportingEntity.isTotal_count()){
            Total_count entity = kReportingConfiguration.getTotal_count();
            HelperKumbayaQueryPlanner.populateMetric(entity.getReturn_prefix(), true, table_name,  entity.getFact_column(), METRICTYPE.SUM,
                    kprojectionMap, kFilterSet, kgroupbyHashSet, kjoinMap, aliasMap, korderbyHashSet, headerList, HeaderType.LONG, 
                    entity.getUiname(), reportingEntity.getTotal_count_order_sequence(), korderbyTreeMap,reportingEntity.getChartType(),
                    pieProjection);
        }
        if(reportingEntity.isTotal_request()){
            Total_request entity = kReportingConfiguration.getTotal_request();
            HelperKumbayaQueryPlanner.populateMetric(entity.getReturn_prefix(), true, table_name,  entity.getFact_column(), METRICTYPE.SUM,
                    kprojectionMap, kFilterSet, kgroupbyHashSet, kjoinMap, aliasMap, korderbyHashSet, headerList, HeaderType.LONG, 
                    entity.getUiname(), reportingEntity.getTotal_request_order_sequence(), korderbyTreeMap,reportingEntity.getChartType(),
                    pieProjection);
        }
        if(reportingEntity.isTotal_request_to_dsp()){
            Total_Request_To_Dsp entity = kReportingConfiguration.getTotal_request_to_dsp();
            HelperKumbayaQueryPlanner.populateMetric(entity.getReturn_prefix(), true, table_name,  entity.getFact_column(), METRICTYPE.SUM,
                    kprojectionMap, kFilterSet, kgroupbyHashSet, kjoinMap, aliasMap, korderbyHashSet, headerList, HeaderType.LONG, 
                    entity.getUiname(), reportingEntity.getTotal_request_to_dsp_order_sequence(), korderbyTreeMap,reportingEntity.getChartType(),
                    pieProjection);
        }
        if(reportingEntity.isTotal_impression()){
            Total_impression entity = kReportingConfiguration.getTotal_impression();
            HelperKumbayaQueryPlanner.populateMetric(entity.getReturn_prefix(), true, table_name,  entity.getFact_column(), METRICTYPE.SUM,
                    kprojectionMap, kFilterSet, kgroupbyHashSet, kjoinMap, aliasMap, korderbyHashSet, headerList, HeaderType.LONG, 
                    entity.getUiname(), reportingEntity.getTotal_impression_order_sequence(), korderbyTreeMap, reportingEntity.getChartType(),
                    pieProjection);
        }
        if(reportingEntity.isFr()){
            HelperKumbayaQueryPlanner.populateFR("fr", true, table_name, kReportingConfiguration.getTotal_impression().getFact_column(), 
                    table_name, kReportingConfiguration.getTotal_request().getFact_column(), METRICTYPE.SUM, 
                    kprojectionMap, kFilterSet, kgroupbyHashSet, kjoinMap, aliasMap, korderbyHashSet, headerList, "FR (%)", 
                    reportingEntity.getFr_order_sequence(), korderbyTreeMap);
        }
        if(reportingEntity.isTotal_win()){
            Total_win entity = kReportingConfiguration.getTotal_win();
            HelperKumbayaQueryPlanner.populateMetric(entity.getReturn_prefix(), true, table_name,  entity.getFact_column(), METRICTYPE.SUM,
                    kprojectionMap, kFilterSet, kgroupbyHashSet, kjoinMap, aliasMap, korderbyHashSet, headerList, HeaderType.INT, 
                    entity.getUiname(), reportingEntity.getTotal_win_order_sequence(), korderbyTreeMap, reportingEntity.getChartType(),
                    pieProjection);
        }
        if(reportingEntity.isWtr()){
            HelperKumbayaQueryPlanner.populateWTR("wtr", true, table_name, kReportingConfiguration.getTotal_win().getFact_column(), 
                    table_name, kReportingConfiguration.getTotal_impression().getFact_column(), 
                    METRICTYPE.SUM, kprojectionMap, kFilterSet, kgroupbyHashSet, kjoinMap, aliasMap, korderbyHashSet, headerList, "WTR (%)",
                    reportingEntity.getWtr_order_sequence(), korderbyTreeMap);
        }
        if(reportingEntity.isTotal_bidValue()){
            Total_bidValue entity = kReportingConfiguration.getTotal_bidValue();
            HelperKumbayaQueryPlanner.populateMetric(entity.getReturn_prefix(), true, table_name,  entity.getFact_column(), METRICTYPE.SUM,
                    kprojectionMap, kFilterSet, kgroupbyHashSet, kjoinMap, aliasMap, korderbyHashSet, headerList, HeaderType.DOUBLE, 
                    entity.getUiname(), reportingEntity.getTotal_bidValue_order_sequence(), korderbyTreeMap, reportingEntity.getChartType(),
                    pieProjection);
        }
        if(reportingEntity.isTotal_win_bidValue()){
            Total_win_bidValue entity = kReportingConfiguration.getTotal_win_bidValue();
            HelperKumbayaQueryPlanner.populateMetric(entity.getReturn_prefix(), true, table_name,  entity.getFact_column(), METRICTYPE.SUM,
                    kprojectionMap, kFilterSet, kgroupbyHashSet, kjoinMap, aliasMap, korderbyHashSet, headerList, HeaderType.DOUBLE, 
                    entity.getUiname(), reportingEntity.getTotal_win_bidValue_order_sequence(), korderbyTreeMap, reportingEntity.getChartType(),
                    pieProjection);
        }
        if(reportingEntity.isTotal_floor()){
            Total_floor entity = kReportingConfiguration.getTotal_floor();
            HelperKumbayaQueryPlanner.populateMetric(entity.getReturn_prefix(), true, table_name,  entity.getFact_column(), METRICTYPE.SUM,
                    kprojectionMap, kFilterSet, kgroupbyHashSet, kjoinMap, aliasMap, korderbyHashSet, headerList, HeaderType.DOUBLE, 
                    entity.getUiname(), reportingEntity.getTotal_floor_order_sequence(), korderbyTreeMap, reportingEntity.getChartType(),
                    pieProjection);
        }
        if(reportingEntity.iseCPW()){
            HelperKumbayaQueryPlanner.populateECPW("eCPW", true, table_name, kReportingConfiguration.getExchangerevenue().getFact_column(), 
                    table_name, kReportingConfiguration.getTotal_win().getFact_column(),  METRICTYPE.SUM, 
                    kprojectionMap, kFilterSet, kgroupbyHashSet, kjoinMap, aliasMap, korderbyHashSet, headerList, "ECPW",
                    reportingEntity.geteCPW_order_sequence(), korderbyTreeMap);
        }
        if(reportingEntity.iseIPW()){
            HelperKumbayaQueryPlanner.populateEIPW("eIPW", true, table_name, kReportingConfiguration.getExchangepayout().getFact_column(), 
                    table_name, kReportingConfiguration.getTotal_win().getFact_column(),  METRICTYPE.SUM, 
                    kprojectionMap, kFilterSet, kgroupbyHashSet, kjoinMap, aliasMap, korderbyHashSet, headerList, "EIPW",
                    reportingEntity.geteIPW_order_sequence(), korderbyTreeMap);
        }
        if(reportingEntity.isTotal_csc()){
            Total_csc entity = kReportingConfiguration.getTotal_csc();
            HelperKumbayaQueryPlanner.populateMetric(entity.getReturn_prefix(), true, table_name,  entity.getFact_column(), METRICTYPE.SUM,
                    kprojectionMap, kFilterSet, kgroupbyHashSet, kjoinMap, aliasMap, korderbyHashSet, headerList, HeaderType.INT, entity.getUiname(),
                    reportingEntity.getTotal_csc_order_sequence(), korderbyTreeMap, reportingEntity.getChartType(),
                    pieProjection);
        }
        if(reportingEntity.isBilledcsc()){
            Billedcsc entity = kReportingConfiguration.getBilledcsc();
            HelperKumbayaQueryPlanner.populateMetric(entity.getReturn_prefix(), true, table_name,  entity.getFact_column(), METRICTYPE.SUM,
                    kprojectionMap, kFilterSet, kgroupbyHashSet, kjoinMap, aliasMap, korderbyHashSet, headerList, HeaderType.INT, 
                    entity.getUiname(), reportingEntity.getBilledcsc_order_sequence(), korderbyTreeMap, reportingEntity.getChartType(),
                    pieProjection);
        }
        if(reportingEntity.isRtr()){
            HelperKumbayaQueryPlanner.populateRTR("rtr", true, table_name, kReportingConfiguration.getTotal_csc().getFact_column(), 
                    table_name, kReportingConfiguration.getTotal_impression().getFact_column(), 
                    METRICTYPE.SUM, kprojectionMap, kFilterSet, kgroupbyHashSet, kjoinMap, aliasMap, korderbyHashSet, headerList, "RTR (%)",
                    reportingEntity.getRtr_order_sequence(), korderbyTreeMap);
        }
        if(reportingEntity.isTotal_click()){
            Total_click entity = kReportingConfiguration.getTotal_click();
            HelperKumbayaQueryPlanner.populateMetric(entity.getReturn_prefix(), true, table_name,  entity.getFact_column(), METRICTYPE.SUM,
                    kprojectionMap, kFilterSet, kgroupbyHashSet, kjoinMap, aliasMap, korderbyHashSet, headerList, HeaderType.INT, 
                    entity.getUiname(), reportingEntity.getTotal_click_order_sequence(), korderbyTreeMap, reportingEntity.getChartType(),
                    pieProjection);
        }
        if(reportingEntity.isBilledclicks()){
            Billedclicks entity = kReportingConfiguration.getBilledclicks();
            HelperKumbayaQueryPlanner.populateMetric(entity.getReturn_prefix(), true, table_name,  entity.getFact_column(), METRICTYPE.SUM,
                    kprojectionMap, kFilterSet, kgroupbyHashSet, kjoinMap, aliasMap, korderbyHashSet, headerList, HeaderType.INT, 
                    entity.getUiname(), reportingEntity.getBilledclicks_order_sequence(), korderbyTreeMap, reportingEntity.getChartType(),
                    pieProjection);
        }
        if(reportingEntity.isCtr()){
            HelperKumbayaQueryPlanner.populateCTR("ctr", true, table_name, kReportingConfiguration.getTotal_click().getFact_column(), 
                    table_name, kReportingConfiguration.getTotal_impression().getFact_column(), 
                    METRICTYPE.SUM, kprojectionMap, kFilterSet, kgroupbyHashSet, kjoinMap, aliasMap, korderbyHashSet, headerList, "CTR (%)",
                    reportingEntity.getCtr_order_sequence(), korderbyTreeMap);
        }
        if(reportingEntity.isProfitmargin()){
            HelperKumbayaQueryPlanner.populateProfitMargin("profitmargin", true, table_name, kReportingConfiguration.getDemandCharges().getFact_column(), 
                    table_name, kReportingConfiguration.getSupplyCost().getFact_column(),  METRICTYPE.SUM, 
                    kprojectionMap, kFilterSet, kgroupbyHashSet, kjoinMap, aliasMap, korderbyHashSet, headerList, "Profit Margin (%)",
                    reportingEntity.getProfitmargin_order_sequence(), korderbyTreeMap);
        }
        if(reportingEntity.isConversion()){
            Conversion entity = kReportingConfiguration.getConversion();
            HelperKumbayaQueryPlanner.populateMetric(entity.getReturn_prefix(), true, table_name,  entity.getFact_column(), METRICTYPE.SUM,
                    kprojectionMap, kFilterSet, kgroupbyHashSet, kjoinMap, aliasMap, korderbyHashSet, headerList, HeaderType.INT, entity.getUiname(),
                    reportingEntity.getConversion_order_sequence(), korderbyTreeMap, reportingEntity.getChartType(),
                    pieProjection);
        }
        if(reportingEntity.isClicksr()){
            HelperKumbayaQueryPlanner.populateClicksr("clicksr", true, table_name, kReportingConfiguration.getConversion().getFact_column(), 
                    table_name, kReportingConfiguration.getTotal_click().getFact_column(),  METRICTYPE.SUM, 
                    kprojectionMap, kFilterSet, kgroupbyHashSet, kjoinMap, aliasMap, korderbyHashSet, headerList, "clicksr",
                    reportingEntity.getClicksr_order_sequence(), korderbyTreeMap);
        }
        if(reportingEntity.isDemandCharges()){
            DemandCharges entity = kReportingConfiguration.getDemandCharges();
            HelperKumbayaQueryPlanner.populateMetric(entity.getReturn_prefix(), true, table_name,  entity.getFact_column(), METRICTYPE.SUM,
                    kprojectionMap, kFilterSet, kgroupbyHashSet, kjoinMap, aliasMap, korderbyHashSet, headerList, HeaderType.DOUBLE, 
                    entity.getUiname(),reportingEntity.getDemandCharges_order_sequence(), korderbyTreeMap, reportingEntity.getChartType(),
                    pieProjection);
        }
        if(reportingEntity.isNetworkrevenue()){
            NetworkRevenue entity = kReportingConfiguration.getNetworkrevenue();
            HelperKumbayaQueryPlanner.populateMetric(entity.getReturn_prefix(), true, table_name,  entity.getFact_column(), METRICTYPE.SUM,
                    kprojectionMap, kFilterSet, kgroupbyHashSet, kjoinMap, aliasMap, korderbyHashSet, headerList, HeaderType.DOUBLE, 
                    entity.getUiname(),reportingEntity.getNetworkrevenue_order_sequence(), korderbyTreeMap, reportingEntity.getChartType(),
                    pieProjection);
        }
        if(reportingEntity.isExchangerevenue()){
            ExchangeRevenue entity = kReportingConfiguration.getExchangerevenue();
            HelperKumbayaQueryPlanner.populateMetric(entity.getReturn_prefix(), true, table_name,  entity.getFact_column(), METRICTYPE.SUM,
                    kprojectionMap, kFilterSet, kgroupbyHashSet, kjoinMap, aliasMap, korderbyHashSet, headerList, HeaderType.DOUBLE, 
                    entity.getUiname(),reportingEntity.getExchangerevenue_order_sequence(), korderbyTreeMap, reportingEntity.getChartType(),
                    pieProjection);
        }
        if(reportingEntity.isCpa_goal()){
            CPAGoal entity = kReportingConfiguration.getCpagoal();
            HelperKumbayaQueryPlanner.populateMetric(entity.getReturn_prefix(), true, table_name,  entity.getFact_column(), METRICTYPE.SUM,
                    kprojectionMap, kFilterSet, kgroupbyHashSet, kjoinMap, aliasMap, korderbyHashSet, headerList, HeaderType.DOUBLE, 
                    entity.getUiname(),reportingEntity.getCpa_goal_order_sequence(), korderbyTreeMap, reportingEntity.getChartType(),
                    pieProjection);
        }
        if(reportingEntity.iseCPC()){
            HelperKumbayaQueryPlanner.populateECPC("eCPC", true, table_name, kReportingConfiguration.getDemandCharges().getFact_column(), 
                    table_name, kReportingConfiguration.getTotal_click().getFact_column(),  METRICTYPE.SUM, 
                    kprojectionMap, kFilterSet, kgroupbyHashSet, kjoinMap, aliasMap, korderbyHashSet, headerList, "eCPC",
                    reportingEntity.geteCPC_order_sequence(), korderbyTreeMap);
        }
        if(reportingEntity.isBilledECPC()){
            HelperKumbayaQueryPlanner.populateECPC("billedECPC", true, table_name, kReportingConfiguration.getDemandCharges().getFact_column(), 
                    table_name, kReportingConfiguration.getBilledclicks().getFact_column(),  METRICTYPE.SUM, 
                    kprojectionMap, kFilterSet, kgroupbyHashSet, kjoinMap, aliasMap, korderbyHashSet, headerList, "billedECPC",
                    reportingEntity.getBilledECPC_order_sequence(), korderbyTreeMap);
        }
        if(reportingEntity.isEcpm()){
            HelperKumbayaQueryPlanner.populateECPM("ecpm", true, table_name, kReportingConfiguration.getDemandCharges().getFact_column(), 
                    table_name, kReportingConfiguration.getTotal_impression().getFact_column(),  METRICTYPE.SUM, 
                    kprojectionMap, kFilterSet, kgroupbyHashSet, kjoinMap, aliasMap, korderbyHashSet, headerList, "ECPM",
                    reportingEntity.getEcpm_order_sequence(), korderbyTreeMap);
        }
        if(reportingEntity.isBilledECPM()){
            HelperKumbayaQueryPlanner.populatebilledECPM("billedECPM", true, table_name, kReportingConfiguration.getDemandCharges().getFact_column(), 
                    table_name, kReportingConfiguration.getBilledcsc().getFact_column(),  METRICTYPE.SUM, 
                    kprojectionMap, kFilterSet, kgroupbyHashSet, kjoinMap, aliasMap, korderbyHashSet, headerList, "billedECPM",
                    reportingEntity.getBilledECPM_order_sequence(), korderbyTreeMap);
        }
        if(reportingEntity.iseCPA()){
            HelperKumbayaQueryPlanner.populateECPA("eCPA", true, table_name, kReportingConfiguration.getDemandCharges().getFact_column(), 
                    table_name, kReportingConfiguration.getConversion().getFact_column(),  METRICTYPE.SUM, 
                    kprojectionMap, kFilterSet, kgroupbyHashSet, kjoinMap, aliasMap, korderbyHashSet, headerList, "ECPA",
                    reportingEntity.geteCPA_order_sequence(), korderbyTreeMap);
        }
        if(reportingEntity.isSupplyCost()){
            SupplyCost entity = kReportingConfiguration.getSupplyCost();
            HelperKumbayaQueryPlanner.populateMetric(entity.getReturn_prefix(), true, table_name,  entity.getFact_column(), METRICTYPE.SUM,
                    kprojectionMap, kFilterSet, kgroupbyHashSet, kjoinMap, aliasMap, korderbyHashSet, headerList, HeaderType.DOUBLE, 
                    entity.getUiname(),reportingEntity.getSupplyCost_order_sequence(), korderbyTreeMap, reportingEntity.getChartType(),
                    pieProjection);
        }
        if(reportingEntity.isNetworkpayout()){
            NetworkPayout entity = kReportingConfiguration.getNetworkpayout();
            HelperKumbayaQueryPlanner.populateMetric(entity.getReturn_prefix(), true, table_name,  entity.getFact_column(), METRICTYPE.SUM,
                    kprojectionMap, kFilterSet, kgroupbyHashSet, kjoinMap, aliasMap, korderbyHashSet, headerList, HeaderType.DOUBLE, 
                    entity.getUiname(),reportingEntity.getNetworkpayout_order_sequence(), korderbyTreeMap, reportingEntity.getChartType(),
                    pieProjection);
        }
        if(reportingEntity.isExchangepayout()){
            ExchangePayout entity = kReportingConfiguration.getExchangepayout();
            HelperKumbayaQueryPlanner.populateMetric(entity.getReturn_prefix(), true, table_name,  entity.getFact_column(), METRICTYPE.SUM,
                    kprojectionMap, kFilterSet, kgroupbyHashSet, kjoinMap, aliasMap, korderbyHashSet, headerList, HeaderType.DOUBLE, 
                    entity.getUiname(),reportingEntity.getExchangepayout_order_sequence(), korderbyTreeMap, reportingEntity.getChartType(),
                    pieProjection);
        }
        if(reportingEntity.iseIPC()){
            HelperKumbayaQueryPlanner.populateEIPC("eIPC", true, table_name, kReportingConfiguration.getSupplyCost().getFact_column(), 
                    table_name, kReportingConfiguration.getTotal_click().getFact_column(),  METRICTYPE.SUM, 
                    kprojectionMap, kFilterSet, kgroupbyHashSet, kjoinMap, aliasMap, korderbyHashSet, headerList, "eIPC",
                    reportingEntity.geteIPC_order_sequence(), korderbyTreeMap);
        }
        if(reportingEntity.iseIPM()){
            HelperKumbayaQueryPlanner.populateEIPM("eIPM", true, table_name, kReportingConfiguration.getSupplyCost().getFact_column(), 
                    table_name, kReportingConfiguration.getTotal_impression().getFact_column(),  METRICTYPE.SUM, 
                    kprojectionMap, kFilterSet, kgroupbyHashSet, kjoinMap, aliasMap, korderbyHashSet, headerList, "EIPM",
                    reportingEntity.geteIPM_order_sequence(), korderbyTreeMap);
        }
        if(reportingEntity.isBilledEIPM()){
            HelperKumbayaQueryPlanner.populatebilledEIPM("billedEIPM", true, table_name, kReportingConfiguration.getSupplyCost().getFact_column(), 
                    table_name, kReportingConfiguration.getBilledcsc().getFact_column(),  METRICTYPE.SUM, 
                    kprojectionMap, kFilterSet, kgroupbyHashSet, kjoinMap, aliasMap, korderbyHashSet, headerList, "billedEIPM",
                    reportingEntity.getBilledEIPM_order_sequence(), korderbyTreeMap);
        }
        if(reportingEntity.iseIPA()){
            HelperKumbayaQueryPlanner.populateEIPA("eIPA", true, table_name, kReportingConfiguration.getSupplyCost().getFact_column(), 
                    table_name, kReportingConfiguration.getConversion().getFact_column(),  METRICTYPE.SUM, 
                    kprojectionMap, kFilterSet, kgroupbyHashSet, kjoinMap, aliasMap, korderbyHashSet, headerList, "EIPA",
                    reportingEntity.geteIPA_order_sequence(), korderbyTreeMap);
        }
        if(reportingEntity.isTotal_event_type()){
            Total_event_type entity = kReportingConfiguration.getTotal_event_type();
            HelperKumbayaQueryPlanner.populateMetric(entity.getReturn_prefix(), true, table_name,  entity.getFact_column(), METRICTYPE.SUM,
                    kprojectionMap, kFilterSet, kgroupbyHashSet, kjoinMap, aliasMap, korderbyHashSet, headerList, HeaderType.INT, 
                    entity.getUiname(), reportingEntity.getTotal_event_type_order_sequence(), korderbyTreeMap, reportingEntity.getChartType(),
                    pieProjection);
        }
        if(reportingEntity.isEarning()){
            Earning entity = kReportingConfiguration.getEarning();
            HelperKumbayaQueryPlanner.populateMetric(entity.getReturn_prefix(), true, table_name,  entity.getFact_column(), METRICTYPE.SUM,
                    kprojectionMap, kFilterSet, kgroupbyHashSet, kjoinMap, aliasMap, korderbyHashSet, headerList, HeaderType.DOUBLE, 
                    entity.getUiname(),reportingEntity.getEarning_order_sequence(), korderbyTreeMap, reportingEntity.getChartType(),
                    pieProjection);
        }
        /*PREPARE ORDER BY*/
        HelperKumbayaQueryPlanner.prepareKOrderByHashSet(korderbyTreeMap, korderbyHashSet);
        
        HelperKumbayaQueryPlanner.populateHeaderList(headerList, kQueryPlanner);
        HelperKumbayaQueryPlanner.populateKFilter(kFilterSet, kQueryPlanner);
        HelperKumbayaQueryPlanner.populateKProjection(kprojectionMap, kQueryPlanner);
        HelperKumbayaQueryPlanner.populateKJoin(kjoinMap, kQueryPlanner);
        HelperKumbayaQueryPlanner.populateKGroupby(kgroupbyHashSet, kQueryPlanner);
        HelperKumbayaQueryPlanner.populateKOrderby(korderbyHashSet, kQueryPlanner);
        HelperKumbayaQueryPlanner.populateKLimit(reportingEntity.getStartindex(), reportingEntity.getPagesize(), kQueryPlanner);
        HelperKumbayaQueryPlanner.populatePlanStatus(kQueryPlanner, ErrorCode.NO_ERROR, ErrorCode.NO_ERROR.toString());
        
    }

    @Override
    public void plan(KumbayaQueryPlanner kQueryPlanner,
            KumbayaReportingConfiguration kReportingConfiguration, ReportingEntity reportingEntity) {
        if(kQueryPlanner == null || kReportingConfiguration == null || reportingEntity==null){
            return;
        }
        if(kQueryPlanner.getKprojection()== null || kQueryPlanner.getKprojection().getKeyvalue() == null ||
                kQueryPlanner.getKprojection().getKeyvalue().size() < 1){
            kQueryPlanner.setQueryString(null);
            return;
        }
        StringBuffer sbuff = new StringBuffer("select ");
        if(ChartType.PIE != reportingEntity.getChartType()){
            sbuff.append("SQL_CALC_FOUND_ROWS ");
        }
        HelperKumbayaQueryPlanner.planKProjection(kQueryPlanner.getKprojection(), sbuff);
        HelperKumbayaQueryPlanner.planKJoin(kQueryPlanner.getKjoin(), sbuff, reportingEntity.getChartType());
        HelperKumbayaQueryPlanner.planKFilter(kQueryPlanner.getKfilter(), kQueryPlanner.getTimeRange(), sbuff);
        aliasCounter = aliasCounter+HelperKumbayaQueryPlanner.planPieChart(reportingEntity, sbuff, aliasCounter, aliasPrefix, aliasMap,pieProjection);
        HelperKumbayaQueryPlanner.planKGroupBy(kQueryPlanner.getKgroupby(), sbuff);
        HelperKumbayaQueryPlanner.planKOrderBy(kQueryPlanner.getKorderby(), sbuff,reportingEntity.isOrder_by_desc());
        HelperKumbayaQueryPlanner.planKLimit(kQueryPlanner.getKlimit(), sbuff);
        kQueryPlanner.setQueryString(sbuff.toString());
    }
    
/*    public static void main(String args[]){
        ReportingEntity reportingEntity = new ReportingEntity();
        reportingEntity.setStart_time(1406598327);
        reportingEntity.setEnd_time(1416663127000L);
        reportingEntity.setDate_as_dimension(true);
        reportingEntity.setChartType(ChartType.TABLE);
        reportingEntity.setFrequency(Frequency.ADMIN_HOURLY);
        reportingEntity.setTotal_request(true);
        DBQueryPlanner dbQueryPlanner = new DBQueryPlanner();
        KumbayaQueryPlanner kQueryPlanner = new KumbayaQueryPlanner();
        KumbayaReportingConfiguration kReportingConfiguration = new KumbayaReportingConfiguration();
        dbQueryPlanner.convert(kQueryPlanner, kReportingConfiguration, reportingEntity, false);
        dbQueryPlanner.plan(kQueryPlanner, kReportingConfiguration, reportingEntity);
        System.out.println(kQueryPlanner.getQueryString());

    }
*/

}

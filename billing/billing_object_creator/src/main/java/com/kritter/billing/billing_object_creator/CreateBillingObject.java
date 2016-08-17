package com.kritter.billing.billing_object_creator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kritter.postimpression.thrift.struct.Billing;
import com.kritter.postimpression.thrift.struct.PostImpressionRequestResponse;

public class CreateBillingObject {
    
    private static final Logger LOG = LoggerFactory.getLogger(CreateBillingObject.class);
    
    public static void create(PostImpressionRequestResponse pirr, Billing billing){
        if(pirr == null || billing == null){
            return;
        }
        if(pirr.isSetAdId()) { billing.setAdId(pirr.getAdId());}
        if(pirr.isSetCampaignId()) { billing.setCampaignId(pirr.getCampaignId());}
        if(pirr.isSetExchangeId()) { billing.setExchangeId(pirr.getExchangeId());}
        if(pirr.isSetSiteId()) { billing.setSiteId(pirr.getSiteId());}
        if(pirr.isSetImpressionId()) { billing.setImpressionId(pirr.getImpressionId());}
        if(pirr.isSetCountryCarrierId()) { billing.setCountryCarrierId(pirr.getCountryCarrierId());}
        if(pirr.isSetCountryRegionId()) { billing.setCountryRegionId(pirr.getCountryRegionId());}
        if(pirr.isSetCountryId()) { billing.setCountryId(pirr.getCountryId());}
        if(pirr.isSetDeviceId()) { billing.setDeviceId(pirr.getDeviceId());}
        if(pirr.isSetDeviceManufacturerId()) { billing.setDeviceManufacturerId(pirr.getDeviceManufacturerId());}
        if(pirr.isSetDeviceOsId()) { billing.setDeviceOsId(pirr.getDeviceOsId());}
        if(pirr.isSetTime()) { billing.setEventTime(pirr.getTime());}
        if(pirr.isSetSelectedSiteCategoryId()) { billing.setSelectedSiteCategoryId(pirr.getSelectedSiteCategoryId());}
        if(pirr.isSetEventTime()) { billing.setEventTime(pirr.getEventTime());};
        if(pirr.isSetBidderModelId()) {billing.setBidderModelId(pirr.getBidderModelId());};
        if(pirr.isSetDeviceBrowserId()) {billing.setBrowserId(pirr.getDeviceBrowserId());};
        if(pirr.isSetSupply_source_type()) {billing.setSupply_source_type(pirr.getSupply_source_type());};
        if(pirr.isSetExt_supply_attr_internal_id()) {billing.setExt_supply_attr_internal_id(pirr.getExt_supply_attr_internal_id());};
        if(pirr.isSetConnectionTypeId()) {billing.setConnectionTypeId(pirr.getConnectionTypeId());};
        if(pirr.isSetAdv_inc_id()) {billing.setAdv_inc_id(pirr.getAdv_inc_id());};
        if(pirr.isSetPub_inc_id()) {billing.setPub_inc_id(pirr.getPub_inc_id());};
        if(pirr.isSetDeviceType()) {billing.setDeviceType(pirr.getDeviceType());};
        if(pirr.isSetStateId()) {billing.setStateId(pirr.getStateId());};
        if(pirr.isSetCityId()) {billing.setCityId(pirr.getCityId());};
        if(pirr.isSetChannelId()) {billing.setChannelId(pirr.getChannelId());};
        if(pirr.isSetAdpositionId()) {billing.setAdpositionId(pirr.getAdpositionId());};
    }
}

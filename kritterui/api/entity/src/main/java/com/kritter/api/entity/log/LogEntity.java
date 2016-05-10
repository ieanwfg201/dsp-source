package com.kritter.api.entity.log;

import java.io.IOException;
import java.util.LinkedList;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.kritter.constants.PageConstants;


public class LogEntity {
    private String start_time_str = "";
    private String end_time_str = "";
    private boolean processing_time = false;
    private boolean time = true;
    private boolean eventTime = true;
    private LinkedList<String> status = new LinkedList<String>();
    private LinkedList<String> event = new LinkedList<String>();
    private LinkedList<Integer> campaignId = new LinkedList<Integer>();
    private LinkedList<Integer> adId = new LinkedList<Integer>();
    private LinkedList<Integer> marketplace_id = new LinkedList<Integer>();
    private LinkedList<Integer> exchangeId = new LinkedList<Integer>();
    private LinkedList<Integer> siteId = new LinkedList<Integer>();
    private LinkedList<Integer> ext_supply_attr_internal_id = new LinkedList<Integer>();
    private LinkedList<Integer> inventorySource = new LinkedList<Integer>();
    private LinkedList<Integer> supply_source_type = new LinkedList<Integer>();
    private boolean advertiser_bid = true;
    private boolean internal_max_bid = true;
    private boolean bidprice_to_exchange = true;
    private boolean cpa_goal = true;
    private LinkedList<Integer> countryId = new LinkedList<Integer>();
    private LinkedList<Integer> countryCarrierId = new LinkedList<Integer>();
    private boolean deviceId = true;
    private LinkedList<Integer> deviceManufacturerId = new LinkedList<Integer>();
    private LinkedList<Integer> deviceModelId = new LinkedList<Integer>();
    private LinkedList<Integer> deviceOsId = new LinkedList<Integer>();
    private LinkedList<Integer> deviceBrowserId = new LinkedList<Integer>();
    private boolean ipAddress = true;
    private boolean userAgent = true;
    private boolean xForwardedFor = true;
    private boolean urlExtraParameters = true;
    private boolean version = true;
    private boolean requestId = true;
    private boolean srcRequestId = true;
    private boolean impressionId = true;
    private boolean srcUrlHash = true;
    private boolean aliasUrlId = true;
    private boolean thirdPartyTrackingUrlEventId = true;
    private boolean thirdPartyId = true;
    private boolean thirdPartyUrlHash = true;
    private boolean auction_price = true;
    private boolean auction_currency = true;
    private boolean auction_seat_id = true;
    private boolean auction_bid_id = true;
    private boolean auction_id = true;
    private boolean auction_imp_id = true;
    private LinkedList<Integer> countryRegionId = new LinkedList<Integer>();
    private LinkedList<Integer> selectedSiteCategoryId = new LinkedList<Integer>();
    private boolean urlVersion = true;
    private boolean bidderModelId = true;
    private LinkedList<Integer> slotId = new LinkedList<Integer>();
    private boolean buyerUid = true;
    private boolean connectionTypeId = true;
    private boolean referer = true;
    private int startindex=PageConstants.start_index;
    private int pagesize=PageConstants.page_size;
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((adId == null) ? 0 : adId.hashCode());
        result = prime * result + (advertiser_bid ? 1231 : 1237);
        result = prime * result + (aliasUrlId ? 1231 : 1237);
        result = prime * result + (auction_bid_id ? 1231 : 1237);
        result = prime * result + (auction_currency ? 1231 : 1237);
        result = prime * result + (auction_id ? 1231 : 1237);
        result = prime * result + (auction_imp_id ? 1231 : 1237);
        result = prime * result + (auction_price ? 1231 : 1237);
        result = prime * result + (auction_seat_id ? 1231 : 1237);
        result = prime * result + (bidderModelId ? 1231 : 1237);
        result = prime * result + (bidprice_to_exchange ? 1231 : 1237);
        result = prime * result + (buyerUid ? 1231 : 1237);
        result = prime * result
                + ((campaignId == null) ? 0 : campaignId.hashCode());
        result = prime * result + (connectionTypeId ? 1231 : 1237);
        result = prime
                * result
                + ((countryCarrierId == null) ? 0 : countryCarrierId.hashCode());
        result = prime * result
                + ((countryId == null) ? 0 : countryId.hashCode());
        result = prime * result
                + ((countryRegionId == null) ? 0 : countryRegionId.hashCode());
        result = prime * result + (cpa_goal ? 1231 : 1237);
        result = prime * result
                + ((deviceBrowserId == null) ? 0 : deviceBrowserId.hashCode());
        result = prime * result + (deviceId ? 1231 : 1237);
        result = prime
                * result
                + ((deviceManufacturerId == null) ? 0 : deviceManufacturerId
                        .hashCode());
        result = prime * result
                + ((deviceModelId == null) ? 0 : deviceModelId.hashCode());
        result = prime * result
                + ((deviceOsId == null) ? 0 : deviceOsId.hashCode());
        result = prime * result
                + ((end_time_str == null) ? 0 : end_time_str.hashCode());
        result = prime * result + ((event == null) ? 0 : event.hashCode());
        result = prime * result + (eventTime ? 1231 : 1237);
        result = prime * result
                + ((exchangeId == null) ? 0 : exchangeId.hashCode());
        result = prime
                * result
                + ((ext_supply_attr_internal_id == null) ? 0
                        : ext_supply_attr_internal_id.hashCode());
        result = prime * result + (impressionId ? 1231 : 1237);
        result = prime * result + (internal_max_bid ? 1231 : 1237);
        result = prime * result
                + ((inventorySource == null) ? 0 : inventorySource.hashCode());
        result = prime * result + (ipAddress ? 1231 : 1237);
        result = prime * result
                + ((marketplace_id == null) ? 0 : marketplace_id.hashCode());
        result = prime * result + pagesize;
        result = prime * result + (processing_time ? 1231 : 1237);
        result = prime * result + (referer ? 1231 : 1237);
        result = prime * result + (requestId ? 1231 : 1237);
        result = prime
                * result
                + ((selectedSiteCategoryId == null) ? 0
                        : selectedSiteCategoryId.hashCode());
        result = prime * result + ((siteId == null) ? 0 : siteId.hashCode());
        result = prime * result + ((slotId == null) ? 0 : slotId.hashCode());
        result = prime * result + (srcRequestId ? 1231 : 1237);
        result = prime * result + (srcUrlHash ? 1231 : 1237);
        result = prime * result
                + ((start_time_str == null) ? 0 : start_time_str.hashCode());
        result = prime * result + startindex;
        result = prime * result + ((status == null) ? 0 : status.hashCode());
        result = prime
                * result
                + ((supply_source_type == null) ? 0 : supply_source_type
                        .hashCode());
        result = prime * result + (thirdPartyId ? 1231 : 1237);
        result = prime * result + (thirdPartyTrackingUrlEventId ? 1231 : 1237);
        result = prime * result + (thirdPartyUrlHash ? 1231 : 1237);
        result = prime * result + (time ? 1231 : 1237);
        result = prime * result + (urlExtraParameters ? 1231 : 1237);
        result = prime * result + (urlVersion ? 1231 : 1237);
        result = prime * result + (userAgent ? 1231 : 1237);
        result = prime * result + (version ? 1231 : 1237);
        result = prime * result + (xForwardedFor ? 1231 : 1237);
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        LogEntity other = (LogEntity) obj;
        if (adId == null) {
            if (other.adId != null)
                return false;
        } else if (!adId.equals(other.adId))
            return false;
        if (advertiser_bid != other.advertiser_bid)
            return false;
        if (aliasUrlId != other.aliasUrlId)
            return false;
        if (auction_bid_id != other.auction_bid_id)
            return false;
        if (auction_currency != other.auction_currency)
            return false;
        if (auction_id != other.auction_id)
            return false;
        if (auction_imp_id != other.auction_imp_id)
            return false;
        if (auction_price != other.auction_price)
            return false;
        if (auction_seat_id != other.auction_seat_id)
            return false;
        if (bidderModelId != other.bidderModelId)
            return false;
        if (bidprice_to_exchange != other.bidprice_to_exchange)
            return false;
        if (buyerUid != other.buyerUid)
            return false;
        if (campaignId == null) {
            if (other.campaignId != null)
                return false;
        } else if (!campaignId.equals(other.campaignId))
            return false;
        if (connectionTypeId != other.connectionTypeId)
            return false;
        if (countryCarrierId == null) {
            if (other.countryCarrierId != null)
                return false;
        } else if (!countryCarrierId.equals(other.countryCarrierId))
            return false;
        if (countryId == null) {
            if (other.countryId != null)
                return false;
        } else if (!countryId.equals(other.countryId))
            return false;
        if (countryRegionId == null) {
            if (other.countryRegionId != null)
                return false;
        } else if (!countryRegionId.equals(other.countryRegionId))
            return false;
        if (cpa_goal != other.cpa_goal)
            return false;
        if (deviceBrowserId == null) {
            if (other.deviceBrowserId != null)
                return false;
        } else if (!deviceBrowserId.equals(other.deviceBrowserId))
            return false;
        if (deviceId != other.deviceId)
            return false;
        if (deviceManufacturerId == null) {
            if (other.deviceManufacturerId != null)
                return false;
        } else if (!deviceManufacturerId.equals(other.deviceManufacturerId))
            return false;
        if (deviceModelId == null) {
            if (other.deviceModelId != null)
                return false;
        } else if (!deviceModelId.equals(other.deviceModelId))
            return false;
        if (deviceOsId == null) {
            if (other.deviceOsId != null)
                return false;
        } else if (!deviceOsId.equals(other.deviceOsId))
            return false;
        if (end_time_str == null) {
            if (other.end_time_str != null)
                return false;
        } else if (!end_time_str.equals(other.end_time_str))
            return false;
        if (event == null) {
            if (other.event != null)
                return false;
        } else if (!event.equals(other.event))
            return false;
        if (eventTime != other.eventTime)
            return false;
        if (exchangeId == null) {
            if (other.exchangeId != null)
                return false;
        } else if (!exchangeId.equals(other.exchangeId))
            return false;
        if (ext_supply_attr_internal_id == null) {
            if (other.ext_supply_attr_internal_id != null)
                return false;
        } else if (!ext_supply_attr_internal_id
                .equals(other.ext_supply_attr_internal_id))
            return false;
        if (impressionId != other.impressionId)
            return false;
        if (internal_max_bid != other.internal_max_bid)
            return false;
        if (inventorySource == null) {
            if (other.inventorySource != null)
                return false;
        } else if (!inventorySource.equals(other.inventorySource))
            return false;
        if (ipAddress != other.ipAddress)
            return false;
        if (marketplace_id == null) {
            if (other.marketplace_id != null)
                return false;
        } else if (!marketplace_id.equals(other.marketplace_id))
            return false;
        if (pagesize != other.pagesize)
            return false;
        if (processing_time != other.processing_time)
            return false;
        if (referer != other.referer)
            return false;
        if (requestId != other.requestId)
            return false;
        if (selectedSiteCategoryId == null) {
            if (other.selectedSiteCategoryId != null)
                return false;
        } else if (!selectedSiteCategoryId.equals(other.selectedSiteCategoryId))
            return false;
        if (siteId == null) {
            if (other.siteId != null)
                return false;
        } else if (!siteId.equals(other.siteId))
            return false;
        if (slotId == null) {
            if (other.slotId != null)
                return false;
        } else if (!slotId.equals(other.slotId))
            return false;
        if (srcRequestId != other.srcRequestId)
            return false;
        if (srcUrlHash != other.srcUrlHash)
            return false;
        if (start_time_str == null) {
            if (other.start_time_str != null)
                return false;
        } else if (!start_time_str.equals(other.start_time_str))
            return false;
        if (startindex != other.startindex)
            return false;
        if (status == null) {
            if (other.status != null)
                return false;
        } else if (!status.equals(other.status))
            return false;
        if (supply_source_type == null) {
            if (other.supply_source_type != null)
                return false;
        } else if (!supply_source_type.equals(other.supply_source_type))
            return false;
        if (thirdPartyId != other.thirdPartyId)
            return false;
        if (thirdPartyTrackingUrlEventId != other.thirdPartyTrackingUrlEventId)
            return false;
        if (thirdPartyUrlHash != other.thirdPartyUrlHash)
            return false;
        if (time != other.time)
            return false;
        if (urlExtraParameters != other.urlExtraParameters)
            return false;
        if (urlVersion != other.urlVersion)
            return false;
        if (userAgent != other.userAgent)
            return false;
        if (version != other.version)
            return false;
        if (xForwardedFor != other.xForwardedFor)
            return false;
        return true;
    }
    public boolean isProcessing_time() {
        return processing_time;
    }
    public void setProcessing_time(boolean processing_time) {
        this.processing_time = processing_time;
    }
    public boolean isTime() {
        return time;
    }
    public void setTime(boolean time) {
        this.time = time;
    }
    public boolean isEventTime() {
        return eventTime;
    }
    public void setEventTime(boolean eventTime) {
        this.eventTime = eventTime;
    }
    public LinkedList<String> getStatus() {
        return status;
    }
    public void setStatus(LinkedList<String> status) {
        this.status = status;
    }
    public LinkedList<String> getEvent() {
        return event;
    }
    public void setEvent(LinkedList<String> event) {
        this.event = event;
    }
    public LinkedList<Integer> getCampaignId() {
        return campaignId;
    }
    public void setCampaignId(LinkedList<Integer> campaignId) {
        this.campaignId = campaignId;
    }
    public LinkedList<Integer> getAdId() {
        return adId;
    }
    public void setAdId(LinkedList<Integer> adId) {
        this.adId = adId;
    }
    public LinkedList<Integer> getMarketplace_id() {
        return marketplace_id;
    }
    public void setMarketplace_id(LinkedList<Integer> marketplace_id) {
        this.marketplace_id = marketplace_id;
    }
    public LinkedList<Integer> getExchangeId() {
        return exchangeId;
    }
    public void setExchangeId(LinkedList<Integer> exchangeId) {
        this.exchangeId = exchangeId;
    }
    public LinkedList<Integer> getSiteId() {
        return siteId;
    }
    public void setSiteId(LinkedList<Integer> siteId) {
        this.siteId = siteId;
    }
    public LinkedList<Integer> getExt_supply_attr_internal_id() {
        return ext_supply_attr_internal_id;
    }
    public void setExt_supply_attr_internal_id(
            LinkedList<Integer> ext_supply_attr_internal_id) {
        this.ext_supply_attr_internal_id = ext_supply_attr_internal_id;
    }
    public LinkedList<Integer> getInventorySource() {
        return inventorySource;
    }
    public void setInventorySource(LinkedList<Integer> inventorySource) {
        this.inventorySource = inventorySource;
    }
    public LinkedList<Integer> getSupply_source_type() {
        return supply_source_type;
    }
    public void setSupply_source_type(LinkedList<Integer> supply_source_type) {
        this.supply_source_type = supply_source_type;
    }
    public boolean isAdvertiser_bid() {
        return advertiser_bid;
    }
    public void setAdvertiser_bid(boolean advertiser_bid) {
        this.advertiser_bid = advertiser_bid;
    }
    public boolean isInternal_max_bid() {
        return internal_max_bid;
    }
    public void setInternal_max_bid(boolean internal_max_bid) {
        this.internal_max_bid = internal_max_bid;
    }
    public boolean isBidprice_to_exchange() {
        return bidprice_to_exchange;
    }
    public void setBidprice_to_exchange(boolean bidprice_to_exchange) {
        this.bidprice_to_exchange = bidprice_to_exchange;
    }
    public boolean isCpa_goal() {
        return cpa_goal;
    }
    public void setCpa_goal(boolean cpa_goal) {
        this.cpa_goal = cpa_goal;
    }
    public LinkedList<Integer> getCountryId() {
        return countryId;
    }
    public void setCountryId(LinkedList<Integer> countryId) {
        this.countryId = countryId;
    }
    public LinkedList<Integer> getCountryCarrierId() {
        return countryCarrierId;
    }
    public void setCountryCarrierId(LinkedList<Integer> countryCarrierId) {
        this.countryCarrierId = countryCarrierId;
    }
    public boolean isDeviceId() {
        return deviceId;
    }
    public void setDeviceId(boolean deviceId) {
        this.deviceId = deviceId;
    }
    public LinkedList<Integer> getDeviceManufacturerId() {
        return deviceManufacturerId;
    }
    public void setDeviceManufacturerId(LinkedList<Integer> deviceManufacturerId) {
        this.deviceManufacturerId = deviceManufacturerId;
    }
    public LinkedList<Integer> getDeviceModelId() {
        return deviceModelId;
    }
    public void setDeviceModelId(LinkedList<Integer> deviceModelId) {
        this.deviceModelId = deviceModelId;
    }
    public LinkedList<Integer> getDeviceOsId() {
        return deviceOsId;
    }
    public void setDeviceOsId(LinkedList<Integer> deviceOsId) {
        this.deviceOsId = deviceOsId;
    }
    public LinkedList<Integer> getDeviceBrowserId() {
        return deviceBrowserId;
    }
    public void setDeviceBrowserId(LinkedList<Integer> deviceBrowserId) {
        this.deviceBrowserId = deviceBrowserId;
    }
    public boolean isIpAddress() {
        return ipAddress;
    }
    public void setIpAddress(boolean ipAddress) {
        this.ipAddress = ipAddress;
    }
    public boolean isUserAgent() {
        return userAgent;
    }
    public void setUserAgent(boolean userAgent) {
        this.userAgent = userAgent;
    }
    public boolean isxForwardedFor() {
        return xForwardedFor;
    }
    public void setxForwardedFor(boolean xForwardedFor) {
        this.xForwardedFor = xForwardedFor;
    }
    public boolean isUrlExtraParameters() {
        return urlExtraParameters;
    }
    public void setUrlExtraParameters(boolean urlExtraParameters) {
        this.urlExtraParameters = urlExtraParameters;
    }
    public boolean isVersion() {
        return version;
    }
    public void setVersion(boolean version) {
        this.version = version;
    }
    public boolean isRequestId() {
        return requestId;
    }
    public void setRequestId(boolean requestId) {
        this.requestId = requestId;
    }
    public boolean isSrcRequestId() {
        return srcRequestId;
    }
    public void setSrcRequestId(boolean srcRequestId) {
        this.srcRequestId = srcRequestId;
    }
    public boolean isImpressionId() {
        return impressionId;
    }
    public void setImpressionId(boolean impressionId) {
        this.impressionId = impressionId;
    }
    public boolean isSrcUrlHash() {
        return srcUrlHash;
    }
    public void setSrcUrlHash(boolean srcUrlHash) {
        this.srcUrlHash = srcUrlHash;
    }
    public boolean isAliasUrlId() {
        return aliasUrlId;
    }
    public void setAliasUrlId(boolean aliasUrlId) {
        this.aliasUrlId = aliasUrlId;
    }
    public boolean isThirdPartyTrackingUrlEventId() {
        return thirdPartyTrackingUrlEventId;
    }
    public void setThirdPartyTrackingUrlEventId(boolean thirdPartyTrackingUrlEventId) {
        this.thirdPartyTrackingUrlEventId = thirdPartyTrackingUrlEventId;
    }
    public boolean isThirdPartyId() {
        return thirdPartyId;
    }
    public void setThirdPartyId(boolean thirdPartyId) {
        this.thirdPartyId = thirdPartyId;
    }
    public boolean isThirdPartyUrlHash() {
        return thirdPartyUrlHash;
    }
    public void setThirdPartyUrlHash(boolean thirdPartyUrlHash) {
        this.thirdPartyUrlHash = thirdPartyUrlHash;
    }
    public boolean isAuction_price() {
        return auction_price;
    }
    public void setAuction_price(boolean auction_price) {
        this.auction_price = auction_price;
    }
    public boolean isAuction_currency() {
        return auction_currency;
    }
    public void setAuction_currency(boolean auction_currency) {
        this.auction_currency = auction_currency;
    }
    public boolean isAuction_seat_id() {
        return auction_seat_id;
    }
    public void setAuction_seat_id(boolean auction_seat_id) {
        this.auction_seat_id = auction_seat_id;
    }
    public boolean isAuction_bid_id() {
        return auction_bid_id;
    }
    public void setAuction_bid_id(boolean auction_bid_id) {
        this.auction_bid_id = auction_bid_id;
    }
    public boolean isAuction_id() {
        return auction_id;
    }
    public void setAuction_id(boolean auction_id) {
        this.auction_id = auction_id;
    }
    public boolean isAuction_imp_id() {
        return auction_imp_id;
    }
    public void setAuction_imp_id(boolean auction_imp_id) {
        this.auction_imp_id = auction_imp_id;
    }
    public LinkedList<Integer> getCountryRegionId() {
        return countryRegionId;
    }
    public void setCountryRegionId(LinkedList<Integer> countryRegionId) {
        this.countryRegionId = countryRegionId;
    }
    public LinkedList<Integer> getSelectedSiteCategoryId() {
        return selectedSiteCategoryId;
    }
    public void setSelectedSiteCategoryId(LinkedList<Integer> selectedSiteCategoryId) {
        this.selectedSiteCategoryId = selectedSiteCategoryId;
    }
    public boolean isUrlVersion() {
        return urlVersion;
    }
    public void setUrlVersion(boolean urlVersion) {
        this.urlVersion = urlVersion;
    }
    public boolean isBidderModelId() {
        return bidderModelId;
    }
    public void setBidderModelId(boolean bidderModelId) {
        this.bidderModelId = bidderModelId;
    }
    public LinkedList<Integer> getSlotId() {
        return slotId;
    }
    public void setSlotId(LinkedList<Integer> slotId) {
        this.slotId = slotId;
    }
    public boolean isBuyerUid() {
        return buyerUid;
    }
    public void setBuyerUid(boolean buyerUid) {
        this.buyerUid = buyerUid;
    }
    public String getStart_time_str() {
        return start_time_str;
    }
    public void setStart_time_str(String start_time_str) {
        this.start_time_str = start_time_str;
    }
    public String getEnd_time_str() {
        return end_time_str;
    }
    public void setEnd_time_str(String end_time_str) {
        this.end_time_str = end_time_str;
    }
    public int getStartindex() {
        return startindex;
    }
    public void setStartindex(int startindex) {
        this.startindex = startindex;
    }
    public int getPagesize() {
        return pagesize;
    }
    public void setPagesize(int pagesize) {
        this.pagesize = pagesize;
    }
    public boolean isConnectionTypeId() {
        return connectionTypeId;
    }
    public void setConnectionTypeId(boolean connectionTypeId) {
        this.connectionTypeId = connectionTypeId;
    }
    public boolean isReferer() {
        return referer;
    }
    public void setReferer(boolean referer) {
        this.referer = referer;
    }
    public JsonNode toJson(){
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.valueToTree(this);
        return jsonNode;
    }
    public static LogEntity getObject(String str) throws JsonParseException, JsonMappingException, IOException{
        ObjectMapper objectMapper = new ObjectMapper();
        LogEntity entity = objectMapper.readValue(str, LogEntity.class);
        return entity;

    }
}

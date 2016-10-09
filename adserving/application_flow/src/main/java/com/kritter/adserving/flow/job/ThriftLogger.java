package com.kritter.adserving.flow.job;

import com.kritter.common.caches.ad_stats.cache.AdStatsCache;
import com.kritter.entity.reqres.entity.Request;
import com.kritter.entity.reqres.entity.Response;
import com.kritter.entity.reqres.entity.ResponseAdInfo;
import com.kritter.adserving.thrift.struct.*;
import com.kritter.constants.ConnectionType;
import com.kritter.constants.SITE_PASSBACK_STATUS;
import com.kritter.core.workflow.Context;
import com.kritter.core.workflow.Job;
import com.kritter.core.workflow.Workflow;

import com.kritter.entity.user.userid.ExternalUserId;
import com.kritter.req_logging.ReqLoggingCache;
import com.kritter.req_logging.ReqLoggingCacheEntity;
import com.kritter.serving.demand.cache.AdEntityCache;
import com.kritter.serving.demand.cache.CampaignCache;
import com.kritter.serving.demand.entity.AdEntity;
import com.kritter.serving.demand.entity.Campaign;
import com.kritter.utils.common.AdNoFillStatsUtils;
import com.kritter.utils.common.ApplicationGeneralUtils;
import org.apache.commons.codec.binary.Base64;
import org.apache.thrift.TException;
import org.apache.thrift.TSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import com.kritter.constants.INVENTORY_SOURCE;

/**
 * This class logs adserving workflow resulting parameters in a thrift format.
 */

public class ThriftLogger implements Job {

    private String name;
    private Logger applicationLogger;
    private Logger thriftLogger;
    private String requestObjectKey;
    private String responseObjectKey;
    private AdEntityCache adEntityCache;
    private CampaignCache campaignCache;
    private String userAgentHeaderName;
    private String xForwardedForHeaderName;
    private String remoteAddressHeaderName;
    private Logger bidRequestLogger;
    private ReqLoggingCache reqLoggingCache;
    private static final String BID_REQ_DELIM_FOR_NOFILL = "|";
    private AdStatsCache adStatsCache;
    private String adNoFillReasonMapKey;

    public ThriftLogger(String name,
                        String applicationLoggerName,
                        String thriftLoggerName,
                        String requestObjectKey,
                        String responseObjectKey,
                        AdEntityCache adEntityCache,
                        CampaignCache campaignCache,
                        String userAgentHeaderName,
                        String xForwardedForHeaderName,
                        String remoteAddressHeaderName,
                        String bidRequestLoggerName,
                        ReqLoggingCache reqLoggingCache,
                        AdStatsCache adStatsCache,
                        String adNoFillReasonMapKey)
    {
        this.name = name;
        this.applicationLogger = LoggerFactory.getLogger(applicationLoggerName);
        this.thriftLogger = LoggerFactory.getLogger(thriftLoggerName);
        this.requestObjectKey = requestObjectKey;
        this.responseObjectKey = responseObjectKey;
        this.adEntityCache = adEntityCache;
        this.campaignCache = campaignCache;
        this.userAgentHeaderName = userAgentHeaderName;
        this.xForwardedForHeaderName = xForwardedForHeaderName;
        this.remoteAddressHeaderName = remoteAddressHeaderName;
        this.bidRequestLogger = LoggerFactory.getLogger(bidRequestLoggerName);
        this.reqLoggingCache = reqLoggingCache;
        this.adStatsCache = adStatsCache;
        this.adNoFillReasonMapKey = adNoFillReasonMapKey;
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    @Override
    public void execute(Context context)
    {
        this.applicationLogger.info("Inside ThriftLogger class of PreImpressionWorkflow.");

        byte[] serializedImpressionRequest = null;
        Base64 base64Encoder = new Base64(0);

        try
        {
            Request request = (Request)context.getValue(this.requestObjectKey);

            if(null == request || request.isRequestForSystemDebugging())
            {
                this.applicationLogger.error("Aborting!!!, Request object is null inside Thrift logger of adserving " +
                        "flow. Or is a Test Debug Request...");
                return;
            }

            HttpServletRequest httpServletRequest = (HttpServletRequest)context.getValue(Workflow.CONTEXT_REQUEST_KEY);

            AdservingRequestResponse adservingRequestResponse = new AdservingRequestResponse();
            adservingRequestResponse.setRequestId(request.getRequestId());

            //set time
            adservingRequestResponse.setTime(request.getTime()/1000);

            InventorySource inventorySource = fetchInventorySource(request);

            if(null != inventorySource)
                adservingRequestResponse.setInventorySource(inventorySource);

            if(null == request.getSite())
            {
                applicationLogger.error("Site object is null inside ThriftLogger, Site is invalid or inactive.");
                return;
            }

            //set site platform, user info and lat long
            adservingRequestResponse.setSitePlatformValue(request.getSite().getSitePlatform());

            if(null != request.getRequestingLatitudeValue())
                adservingRequestResponse.setEndUserlatitudeValue(request.getRequestingLatitudeValue());
            if(null != request.getRequestingLongitudeValue())
                adservingRequestResponse.setEndUserlongitudeValue(request.getRequestingLongitudeValue());

            // Set internal user id in the log object
            User user = fetchUserFromRequest(request);

            if(null != user)
                adservingRequestResponse.setEndUserIdentificationObject(user);

            // Set external user ids in the log object
            Set<ExternalUserId> externalUserIds = request.getExternalUserIds();
            List<String> externalUserIdStrings = new ArrayList<String>();
            if(externalUserIds != null) {
                for(ExternalUserId externalUserId : externalUserIds)
                    externalUserIdStrings.add(externalUserId.toString());
            }
            adservingRequestResponse.setExternalUserIds(externalUserIdStrings);
            TerminationReason terminationReason = fetchTerminationReason(request);

            if(null != terminationReason)
                adservingRequestResponse.setTerminationReason(terminationReason);

            if(null != request.getSite() && null != request.getSite().getSiteIncId())
                adservingRequestResponse.setSiteId(request.getSite().getSiteIncId());

            //set default
            adservingRequestResponse.setExchangeId(-1);
            adservingRequestResponse.setPub_inc_id(-1);
            //set exchange id if inventory source is adexchange.
            if(null != request.getSite() && null != request.getSite().getPublisherIncId())
            {
                adservingRequestResponse.setPub_inc_id(request.getSite().getPublisherIncId());

                if(request.getInventorySource() == INVENTORY_SOURCE.RTB_EXCHANGE.getCode())
                    adservingRequestResponse.setExchangeId(request.getSite().getPublisherIncId());
            }

            //set if passback done or not
            if(request.getInventorySource() != INVENTORY_SOURCE.RTB_EXCHANGE.getCode() &&
               request.isPassbackDoneForDirectPublisherSiteNoFill())
            {
                adservingRequestResponse.setDirect_nofill_passback(SITE_PASSBACK_STATUS.PASSBACK_DONE.getCode());
            }
            else
                adservingRequestResponse.setDirect_nofill_passback(SITE_PASSBACK_STATUS.PASSBACK_NOT_DONE.getCode());

            //set site categories
            if(null != request.getSite().getCategoriesArray())
                adservingRequestResponse.setSiteCategoryId(Arrays.asList(request.getSite().getCategoriesArray()));

            if(null!=request.getHandsetMasterData() && null!=request.getHandsetMasterData().getInternalId())
                adservingRequestResponse.setDeviceId(request.getHandsetMasterData().getInternalId());
            else
                adservingRequestResponse.setDeviceId(-1L);

            if(null!=request.getHandsetMasterData() && null!=request.getHandsetMasterData().getManufacturerId())
                adservingRequestResponse.setDeviceManufacturerId(request.getHandsetMasterData().getManufacturerId());
            else
                adservingRequestResponse.setDeviceManufacturerId(-1);

            if(null!=request.getHandsetMasterData() && null!=request.getHandsetMasterData().getModelId())
                adservingRequestResponse.setDeviceModelId(request.getHandsetMasterData().getModelId());
            else
                adservingRequestResponse.setDeviceModelId(-1);

            if(null!=request.getHandsetMasterData() &&
                    null!=request.getHandsetMasterData().getDeviceOperatingSystemId())
                adservingRequestResponse.setDeviceOsId(request.getHandsetMasterData().getDeviceOperatingSystemId());
            else
                adservingRequestResponse.setDeviceOsId(-1);

            if(null!=request.getHandsetMasterData() &&
                    null!=request.getHandsetMasterData().getDeviceBrowserId())
                adservingRequestResponse.setBrowserId(request.getHandsetMasterData().getDeviceBrowserId());
            else
                adservingRequestResponse.setBrowserId(-1);

            /*set device type*/
            if(null!=request.getHandsetMasterData() && null!=request.getHandsetMasterData().getDeviceType())
                adservingRequestResponse.setDeviceType(request.getHandsetMasterData().getDeviceType().getCode());
            else
                adservingRequestResponse.setDeviceType((short)-1);

            if(null!=request.getCountry())
                adservingRequestResponse.setDetectedCountryId(request.getCountry().getCountryInternalId());

            if(null!=request.getInternetServiceProvider())
                adservingRequestResponse.
                        setDetectedCountryCarrierId(
                                                    request.getInternetServiceProvider().getOperatorInternalId()
                                                   );

            /******************************************set location attributes****************************************/
            if(null != request.getCountry())
                adservingRequestResponse.setDs_country(request.getCountry().getDataSourceName());
            if(null != request.getInternetServiceProvider())
                adservingRequestResponse.setDs_isp(request.getInternetServiceProvider().getDataSourceName());
            if(null != request.getDataSourceNameUsedForStateDetection())
                adservingRequestResponse.setDs_state(request.getDataSourceNameUsedForStateDetection());
            if(null != request.getDataSourceNameUsedForCityDetection())
                adservingRequestResponse.setDs_city(request.getDataSourceNameUsedForCityDetection());
            if(null != request.getCountryUserInterfaceId())
                adservingRequestResponse.setCountryId(request.getCountryUserInterfaceId());
            if(null != request.getStateUserInterfaceId()){
                adservingRequestResponse.setStateId(request.getStateUserInterfaceId());
            }else{
            	adservingRequestResponse.setStateId(ApplicationGeneralUtils.DEFAULT_STATE_ID);
            }
            if(null != request.getCityUserInterfaceId()){
                adservingRequestResponse.setCityId(request.getCityUserInterfaceId());
            }else{
            	adservingRequestResponse.setCityId(ApplicationGeneralUtils.DEFAULT_CITY_ID);
            }
            if(null != request.getCarrierUserInterfaceId())
                adservingRequestResponse.setCountryCarrierId(request.getCarrierUserInterfaceId());
            /*********************************************************************************************************/

            Response response = (Response)context.getValue(this.responseObjectKey);

            if(null == response)
            {
                applicationLogger.debug("Response object is null inside thrift logger for adserving flow.");
                return;
            }

            //set bidder model id if available.
            if(null != response.getBidderModelId())
                adservingRequestResponse.setBidderModelId(response.getBidderModelId());

            //set selected site category id if available.
            if(null != response.getSelectedSiteCategoryId())
                adservingRequestResponse.setSelectedSiteCategoryId(response.getSelectedSiteCategoryId());

            //set supply source type code
            short supplySourceWapOrApp = -1;
            try
            {
                if(null != request.getSite().getSitePlatform())
                    supplySourceWapOrApp = request.getSite().getSitePlatform().shortValue();
            }
            catch (Exception e)
            {
                applicationLogger.error("Error in getting site platform code",e);
            }
            adservingRequestResponse.setSupply_source_type(supplySourceWapOrApp);

            //use response ad from exchange storage info for impression data, in rtb exchange inventory.
            Set<String> impressionIdSetExchange = response.fetchRTBExchangeImpressionIdToRespondFor();
            boolean isRequestAFill = false;

            if(
               null != impressionIdSetExchange &&
               (request.getInventorySource() == INVENTORY_SOURCE.RTB_EXCHANGE.getCode())
              )
            {
                if(request.getSite() != null){
                    if(request.getSite().getCategoriesArrayForInclusionExclusion() != null){
                        adservingRequestResponse.setBcat(Arrays.asList(request.getSite().getCategoriesArrayForInclusionExclusion()));
                    }
                    if(request.getSite().getCreativeAttributesForInclusionExclusion() != null){
                        adservingRequestResponse.setBattr(Arrays.asList(request.getSite().getCreativeAttributesForInclusionExclusion()));
                    }
                }

                Set<Integer> adIdsInExchangeResponse = new HashSet<Integer>();
                for(String impressionIdExchange : impressionIdSetExchange)
                {
                    ResponseAdInfo responseAdInfo =
                                        response.getFinalResponseAdInfoForExchangeImpressionId(impressionIdExchange);

                    if(null == responseAdInfo)
                        continue;

                    AdEntity adEntity = adEntityCache.query(responseAdInfo.getAdId());

                    Impression impression = new Impression();
                    impression.setAdId(adEntity.getId());
                    impression.setCampaignId(adEntity.getCampaignIncId());


                    if(null!=adEntity.getCreativeId())
                        impression.setCreativeId(adEntity.getCreativeId());

                    Campaign campaign = campaignCache.query(adEntity.getCampaignIncId());
                    impression.setAdvertiserId(campaign.getAccountId());
                    impression.setImpressionId(responseAdInfo.getImpressionId());
                    impression.setSlotId(responseAdInfo.getSlotId());
                    impression.setAdv_inc_id(adEntity.getAccountId());
                    impression.setPredictedCTR(responseAdInfo.getCtrValue());
                    impression.setMarketplace((short)adEntity.getMarketPlace().getCode());

                    //set bid value from bidder if available
                    if(null != responseAdInfo.getEcpmValue())
                        impression.setBidValue(responseAdInfo.getEcpmValue());

                    adservingRequestResponse.addToImpressions(impression);
                    isRequestAFill = true;

                    if(adStatsCache != null) {
                        this.applicationLogger.debug("Updating no fill reason to fill for ads that are sent in response");
                        adIdsInExchangeResponse.add(responseAdInfo.getAdId());
                        AdNoFillStatsUtils.updateContextForNoFillOfAd(responseAdInfo.getAdId(),
                                NoFillReason.FILL.getValue(), this.adNoFillReasonMapKey, context);
                    }
                }

                if(adStatsCache != null) {
                    Set<ResponseAdInfo> responseAdInfoSet = response.getResponseAdInfo();
                    this.applicationLogger.debug("Updating no fill reason to fill for ads not sent in response");
                    if(responseAdInfoSet != null) {
                        for (ResponseAdInfo responseAdInfo : responseAdInfoSet) {
                            if(!adIdsInExchangeResponse.contains(responseAdInfo.getAdId())) {
                                AdNoFillStatsUtils.updateContextForNoFillOfAd(responseAdInfo.getAdId(),
                                        NoFillReason.LOST_TO_COMPETITION.getValue(), this.adNoFillReasonMapKey,
                                        context);
                            }
                        }
                    }
                }
            }
            //case of non-exchange inventory
            else if(request.getInventorySource() != INVENTORY_SOURCE.RTB_EXCHANGE.getCode() &&
                    null != response.getResponseAdInfo())
            {
                for(ResponseAdInfo responseAdInfo : response.getResponseAdInfo())
                {
                    //still check that responseAdInfo is proper.
                    if(null == responseAdInfo)
                        continue;

                    AdEntity adEntity = adEntityCache.query(responseAdInfo.getAdId());

                    Impression impression = new Impression();
                    impression.setAdId(adEntity.getId());
                    impression.setCampaignId(adEntity.getCampaignIncId());

                    if(null!=adEntity.getCreativeId())
                        impression.setCreativeId(adEntity.getCreativeId());

                    Campaign campaign = campaignCache.query(adEntity.getCampaignIncId());
                    impression.setAdvertiserId(campaign.getAccountId());
                    impression.setImpressionId(responseAdInfo.getImpressionId());
                    impression.setSlotId(responseAdInfo.getSlotId());
                    impression.setAdv_inc_id(adEntity.getAccountId());
                    impression.setPredictedCTR(responseAdInfo.getCtrValue());
                    impression.setMarketplace((short)adEntity.getMarketPlace().getCode());
                    adservingRequestResponse.addToImpressions(impression);
                    isRequestAFill = true;

                    if(adStatsCache != null) {
                        this.applicationLogger.debug("Updating no fill reason to fill for ads that are sent in response");
                        AdNoFillStatsUtils.updateContextForNoFillOfAd(responseAdInfo.getAdId(),
                                NoFillReason.FILL.getValue(), this.adNoFillReasonMapKey, context);
                    }
                }
            }

            if(adStatsCache != null) {
                this.applicationLogger.debug("Updating stats in ad stats cache.");
                @SuppressWarnings("unchecked")
                Map<Integer, Integer> adToNofillReasonMap = (Map<Integer, Integer>) context.getValue(
                        this.adNoFillReasonMapKey);
                adStatsCache.updateAdNofillReasonMap(request.getTime(), adToNofillReasonMap);
            }

            //set no-fill reason if any, otherwise would be fill.
            NoFillReason noFillReason = request.getNoFillReason();
            String noFillReasonForBidLog = null;

            if(null != noFillReason)
            {
                adservingRequestResponse.setNofillReason(noFillReason);
                noFillReasonForBidLog = noFillReason.toString();
            }
            else if(!isRequestAFill)
            {
                adservingRequestResponse.setNofillReason(NoFillReason.INTERNAL_ERROR);
                noFillReasonForBidLog = NoFillReason.INTERNAL_ERROR.toString();
            }
            else
            {
                adservingRequestResponse.setNofillReason(NoFillReason.FILL);
                noFillReasonForBidLog = NoFillReason.FILL.toString();
            }

            ReqLoggingCacheEntity reqLoggingCacheEntity = null;

            if(null != request.getSite())
                reqLoggingCacheEntity = reqLoggingCache.query(request.getSite().getPublisherId());

            boolean logBidRequest = false;
            if(null != reqLoggingCacheEntity)
                logBidRequest = reqLoggingCacheEntity.isEnable();

            if(logBidRequest && INVENTORY_SOURCE.RTB_EXCHANGE.getCode() == request.getInventorySource())
            {
                StringBuffer bidRequestBuffer = new StringBuffer();
                bidRequestBuffer.append(request.getRequestId());
                bidRequestBuffer.append(BID_REQ_DELIM_FOR_NOFILL);
                bidRequestBuffer.append(noFillReasonForBidLog);
                this.bidRequestLogger.debug(bidRequestBuffer.toString());
            }

            //set requesting slot id array
            if(null != request.getRequestedSlotIdList())
                adservingRequestResponse.setReqSlotIds(request.getRequestedSlotIdList());

            if(null != request.getUserAgent())
                adservingRequestResponse.setUserAgent(request.getUserAgent());
            if(null != request.getIpAddressUsedForDetection())
                adservingRequestResponse.setIpAddress(request.getIpAddressUsedForDetection());


            String xFFHeaderValue = httpServletRequest.getHeader(this.xForwardedForHeaderName);
            if(null != xFFHeaderValue)
                adservingRequestResponse.setXForwardedFor(xFFHeaderValue);

            // set connection type
            if(request.getConnectionType() == null) {
                adservingRequestResponse.setConnectionTypeId(ConnectionType.UNKNOWN.getId());
            } else {
                adservingRequestResponse.setConnectionTypeId(request.getConnectionType().getId());
            }

            //set other exchange params if available.
            if(null != request.getMccMncCodeCouple())
                adservingRequestResponse.setMcc_mnc(request.getMccMncCodeCouple());

            /***********************Set external supply source attributes******************************************/
            if(null != request.getSite().getExternalSupplyDomain())
                adservingRequestResponse.setExt_supply_domain(request.getSite().getExternalSupplyDomain());
            if(null != request.getSite().getExternalSupplyId())
                adservingRequestResponse.setExt_supply_id(request.getSite().getExternalSupplyId());
            if(null != request.getSite().getExternalSupplyName())
                adservingRequestResponse.setExt_supply_name(request.getSite().getExternalSupplyName());
            if(null != request.getSite().getExternalSupplyUrl())
                adservingRequestResponse.setExt_supply_url(request.getSite().getExternalSupplyUrl());
            /******************************************************************************************************/

            /**********************set internal id for external supply attributes.*********************************/
            if(null != request.getExternalSupplyAttributesInternalId())
                adservingRequestResponse.
                        setExt_supply_attr_internal_id(request.getExternalSupplyAttributesInternalId());
            else
                adservingRequestResponse.setExt_supply_attr_internal_id
                                       (ApplicationGeneralUtils.DEFAULT_INTERNAL_ID_FOR_EXTERNAL_SUPPLY_ATTRIBUTES);
            /******************************************************************************************************/
            if(request.getSite() != null && request.getSite().getAdPositionUiId() != null){
            	adservingRequestResponse.setAdpositionId(request.getSite().getAdPositionUiId() );
            }else{
            	adservingRequestResponse.setAdpositionId(ApplicationGeneralUtils.DEFAULT_ADPOSITION_ID);
            }
            if(request.getSite() != null && request.getSite().getChannelInternalId() != null){
            	adservingRequestResponse.setChannelId(request.getSite().getChannelInternalId() );
            }else{
            	adservingRequestResponse.setChannelId(ApplicationGeneralUtils.DEFAULT_CHANNEL_ID);
            }

            /*********************************** Persist bid prices offered by DSPs**********************************/
            if(null != request.getSite())
            adservingRequestResponse.setBidFloor(request.getSite().getEcpmFloorValue());

            if(
               null != request.getDspBidPriceResponseForExchangeRequest() &&
               request.getDspBidPriceResponseForExchangeRequest().size() > 0
              )
            {
                adservingRequestResponse.setDsp_bid_price(request.getDspBidPriceResponseForExchangeRequest());
            }
            /********************************************************************************************************/

            try
            {
                TSerializer thriftSerializer = new TSerializer();
                serializedImpressionRequest = thriftSerializer.serialize(adservingRequestResponse);
                serializedImpressionRequest =  base64Encoder.encode(serializedImpressionRequest);
            }
            catch(TException t)
            {
                this.applicationLogger.error("TException inside ThriftLogger in adserving workflow.",t);
            }

        }
        catch (Exception e){

            this.applicationLogger.error("Exception inside ThriftLogger in adserving workflow.",e);
        }

        this.applicationLogger.info("Done prepairing thrift object, now writing it to thrift data file");
        this.thriftLogger.info(new String(serializedImpressionRequest));
    }

    private User fetchUserFromRequest(Request request)
    {
        User user = null;

        if(null != request.getUserId()) {
            user = new User();
            user.setUniqueUserId(request.getUserId());
        }

        return user;
    }

    private InventorySource fetchInventorySource(Request request)
    {
        if(INVENTORY_SOURCE.DIRECT_PUBLISHER.getCode() == request.getInventorySource())
        {
            return InventorySource.DIRECT_PUBLISHER;
        }
        else if(INVENTORY_SOURCE.AGGREGATOR.getCode() == request.getInventorySource())
        {
            return InventorySource.AGGREGATOR;
        }
        else if(INVENTORY_SOURCE.RTB_EXCHANGE.getCode() == request.getInventorySource())
        {
            return InventorySource.RTB_EXCHANGE;
        }else  if(INVENTORY_SOURCE.OPENRTB_AGGREGATOR.getCode() == request.getInventorySource())
        {
            return InventorySource.OPENRTB_AGGREGATOR;
        }


        return null;
    }

    private TerminationReason fetchTerminationReason(Request request){

        if(null==request.getRequestEnrichmentErrorCode() ||
           request.getRequestEnrichmentErrorCode().getErrorCode() ==
           Request.REQUEST_ENRICHMENT_ERROR_CODE.NO_ERROR.getErrorCode()){

            return TerminationReason.HEALTHY_REQUEST;
        }
        if(request.getRequestEnrichmentErrorCode().getErrorCode() ==
                Request.REQUEST_ENRICHMENT_ERROR_CODE.SITE_NOT_FIT.getErrorCode()){

            return TerminationReason.SITE_NOT_FIT;
        }
        if(request.getRequestEnrichmentErrorCode().getErrorCode() ==
                Request.REQUEST_ENRICHMENT_ERROR_CODE.REQUEST_MALFORMED.getErrorCode()){

            return TerminationReason.REQUEST_MALFORMED;
        }
        if(request.getRequestEnrichmentErrorCode().getErrorCode() ==
                Request.REQUEST_ENRICHMENT_ERROR_CODE.DEVICE_UNDETECTED.getErrorCode()){

            return TerminationReason.DEVICE_UNDETECTED;
        }
        if(request.getRequestEnrichmentErrorCode().getErrorCode() ==
                Request.REQUEST_ENRICHMENT_ERROR_CODE.DEVICE_BOT.getErrorCode()){

            return TerminationReason.DEVICE_BOT;
        }

        return null;
    }
}

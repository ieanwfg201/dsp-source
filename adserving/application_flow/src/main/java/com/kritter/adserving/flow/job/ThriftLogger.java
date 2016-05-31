package com.kritter.adserving.flow.job;

import com.kritter.entity.reqres.entity.Request;
import com.kritter.entity.reqres.entity.Response;
import com.kritter.entity.reqres.entity.ResponseAdInfo;
import com.kritter.adserving.thrift.struct.*;
import com.kritter.common.site.entity.Site;
import com.kritter.constants.ConnectionType;
import com.kritter.constants.SITE_PASSBACK_STATUS;
import com.kritter.core.workflow.Context;
import com.kritter.core.workflow.Job;
import com.kritter.core.workflow.Workflow;

import com.kritter.entity.user.userid.ExternalUserId;
import com.kritter.serving.demand.cache.AdEntityCache;
import com.kritter.serving.demand.cache.CampaignCache;
import com.kritter.serving.demand.entity.AdEntity;
import com.kritter.serving.demand.entity.Campaign;
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
    private static final String BID_REQ_DELIM_FOR_NOFILL = "|";

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
                        String bidRequestLoggerName)
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
                this.applicationLogger.error("Aborting!!!, Request object is null inside Thrift logger of adserving flow. Or is a Test Debug Request...");
                return;
            }

            HttpServletRequest httpServletRequest = (HttpServletRequest)context.getValue(Workflow.CONTEXT_REQUEST_KEY);

            AdservingRequestResponse adservingRequestResponse = new AdservingRequestResponse();
            adservingRequestResponse.setRequestId(request.getRequestId());

            //set time
            adservingRequestResponse.setTime(System.currentTimeMillis()/1000);

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

            if(null!=request.getHandsetMasterData() && null!=request.getHandsetMasterData().getManufacturerId())
                adservingRequestResponse.setDeviceManufacturerId(request.getHandsetMasterData().getManufacturerId());

            if(null!=request.getHandsetMasterData() && null!=request.getHandsetMasterData().getModelId())
                adservingRequestResponse.setDeviceModelId(request.getHandsetMasterData().getModelId());

            if(null!=request.getHandsetMasterData() &&
                    null!=request.getHandsetMasterData().getDeviceOperatingSystemId())
                adservingRequestResponse.setDeviceOsId(request.getHandsetMasterData().getDeviceOperatingSystemId());

            if(null!=request.getHandsetMasterData() &&
                    null!=request.getHandsetMasterData().getDeviceBrowserId())
                adservingRequestResponse.setBrowserId(request.getHandsetMasterData().getDeviceBrowserId());

            /*set device type*/
            if(null!=request.getHandsetMasterData() && null!=request.getHandsetMasterData().getDeviceType())
                adservingRequestResponse.setDeviceType(request.getHandsetMasterData().getDeviceType().getCode());

            if(null!=request.getCountry())
                adservingRequestResponse.setDetectedCountryId(request.getCountry().getCountryInternalId());

            if(null!=request.getInternetServiceProvider())
                adservingRequestResponse.
                        setDetectedCountryCarrierId(
                                                    request.getInternetServiceProvider().getOperatorInternalId()
                                                   );

            if(null!=request.getCountryUserInterfaceId())
                adservingRequestResponse.setCountryId(request.getCountryUserInterfaceId());

            if(null!=request.getCarrierUserInterfaceId())
                adservingRequestResponse.setCountryCarrierId(request.getCarrierUserInterfaceId());

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

                    //set bid value from bidder if available
                    if(null != responseAdInfo.getEcpmValue())
                        impression.setBidValue(responseAdInfo.getEcpmValue());

                    adservingRequestResponse.addToImpressions(impression);
                    isRequestAFill = true;
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

                    adservingRequestResponse.addToImpressions(impression);
                    isRequestAFill = true;
                }
            }

            //set no-fill reason if any, otherwise would be fill.
            NoFillReason noFillReason = fetchNoFillReason(request);
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


            if(INVENTORY_SOURCE.RTB_EXCHANGE.getCode() == request.getInventorySource())
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

    private NoFillReason fetchNoFillReason(Request request)
    {
        if(null == request.getNoFillReason())
            return null;

        if(request.getNoFillReason().getCode() == Request.NO_FILL_REASON.NO_ADS_COUNTRY_CARRIER.getCode())
            return NoFillReason.NO_ADS_COUNTRY_CARRIER;

        if(request.getNoFillReason().getCode() == Request.NO_FILL_REASON.NO_ADS_BRAND.getCode())
            return NoFillReason.NO_ADS_BRAND;

        if(request.getNoFillReason().getCode() == Request.NO_FILL_REASON.NO_ADS_MODEL.getCode())
            return NoFillReason.NO_ADS_MODEL;

        if(request.getNoFillReason().getCode() == Request.NO_FILL_REASON.SITE_INC_EXC_ADV_CMPGN.getCode())
            return NoFillReason.SITE_INC_EXC_ADV_CMPGN;

        if(request.getNoFillReason().getCode() == Request.NO_FILL_REASON.AD_INV_SRC.getCode())
            return NoFillReason.AD_INV_SRC;

        if(request.getNoFillReason().getCode() == Request.NO_FILL_REASON.AD_INV_SRC_TYPE.getCode())
            return NoFillReason.AD_INV_SRC_TYPE;

        if(request.getNoFillReason().getCode() == Request.NO_FILL_REASON.AD_SITE_CATEGORY_INC_EXC.getCode())
            return NoFillReason.AD_SITE_CATEGORY_INC_EXC;

        if(request.getNoFillReason().getCode() == Request.NO_FILL_REASON.SITE_AD_CATEGORY_INC_EXC.getCode())
            return NoFillReason.SITE_AD_CATEGORY_INC_EXC;

        if(request.getNoFillReason().getCode() == Request.NO_FILL_REASON.AD_DIRECT_SUPPLY_INC_EXC.getCode())
            return NoFillReason.AD_DIRECT_SUPPLY_INC_EXC;

        if(request.getNoFillReason().getCode() == Request.NO_FILL_REASON.AD_EXCHNG_SUPPLY_INC_EXC.getCode())
            return NoFillReason.AD_EXCHNG_SUPPLY_INC_EXC;

        if(request.getNoFillReason().getCode() == Request.NO_FILL_REASON.AD_SUPPLY_INC_EXC.getCode())
            return NoFillReason.AD_SUPPLY_INC_EXC;

        if(request.getNoFillReason().getCode() == Request.NO_FILL_REASON.AD_SITE_HYGIENE.getCode())
            return NoFillReason.AD_SITE_HYGIENE;

        if(request.getNoFillReason().getCode() == Request.NO_FILL_REASON.AD_OS_MIDP.getCode())
            return NoFillReason.AD_OS_MIDP;

        if(request.getNoFillReason().getCode() == Request.NO_FILL_REASON.AD_BROWSER.getCode())
            return NoFillReason.AD_BROWSER;

        if(request.getNoFillReason().getCode() == Request.NO_FILL_REASON.SITE_AD_DOMAIN.getCode())
            return NoFillReason.SITE_AD_DOMAIN;

        if(request.getNoFillReason().getCode() == Request.NO_FILL_REASON.AD_CUSTOM_IP.getCode())
            return NoFillReason.AD_CUSTOM_IP;

        if(request.getNoFillReason().getCode() == Request.NO_FILL_REASON.AD_HOUR_DAY.getCode())
            return NoFillReason.AD_HOUR_DAY;

        if(request.getNoFillReason().getCode() == Request.NO_FILL_REASON.CREATIVE_ATTR.getCode())
            return NoFillReason.CREATIVE_ATTR;

        if(request.getNoFillReason().getCode() == Request.NO_FILL_REASON.CREATIVE_SIZE.getCode())
            return NoFillReason.CREATIVE_SIZE;

        if(request.getNoFillReason().getCode() == Request.NO_FILL_REASON.ECPM_FLOOR_UNMET.getCode())
            return NoFillReason.ECPM_FLOOR_UNMET;

        if(request.getNoFillReason().getCode() == Request.NO_FILL_REASON.SITE_INC_EXC_ADV_CMPGN.getCode())
            return NoFillReason.SITE_INC_EXC_ADV_CMPGN;

        if(request.getNoFillReason().getCode() == Request.NO_FILL_REASON.CAMPAIGN_DATE_BUDGET.getCode())
            return NoFillReason.CAMPAIGN_DATE_BUDGET;

        if(request.getNoFillReason().getCode() == Request.NO_FILL_REASON.BIDDER_BID_NEGATIVE_ZERO.getCode())
            return NoFillReason.BIDDER_BID_NEGATIVE_ZERO;

        if(request.getNoFillReason().getCode() == Request.NO_FILL_REASON.AD_ZIPCODE.getCode())
            return NoFillReason.AD_ZIPCODE;

        if(request.getNoFillReason().getCode() == Request.NO_FILL_REASON.AD_LAT_LONG.getCode())
            return NoFillReason.AD_LAT_LONG;

        if(request.getNoFillReason().getCode() == Request.NO_FILL_REASON.NO_ADS_CONNECTION_TYPE.getCode())
            return NoFillReason.NO_ADS_CONNECTION_TYPE;

        if(request.getNoFillReason().getCode() == Request.NO_FILL_REASON.NO_ADS_TABLET_TARGETING.getCode())
            return NoFillReason.NO_ADS_TABLET_TARGETING;
        
        if(request.getNoFillReason().getCode() == Request.NO_FILL_REASON.ONLY_MEDIATION_DP.getCode())
            return NoFillReason.ONLY_MEDIATION_DP;
        
        if(request.getNoFillReason().getCode() == Request.NO_FILL_REASON.ONLY_DSP_DP.getCode())
            return NoFillReason.ONLY_DSP_DP;
        
        if(request.getNoFillReason().getCode() == Request.NO_FILL_REASON.EX_OD_REQ_CONVERT.getCode())
            return NoFillReason.EX_OD_REQ_CONVERT;
        
        if(request.getNoFillReason().getCode() == Request.NO_FILL_REASON.EX_OD_REQ_SER_NULL.getCode())
            return NoFillReason.EX_OD_REQ_SER_NULL;
        
        if(request.getNoFillReason().getCode() == Request.NO_FILL_REASON.EX_OD_URL_MAP_EMP.getCode())
            return NoFillReason.EX_OD_URL_MAP_EMP;
        
        if(request.getNoFillReason().getCode() == Request.NO_FILL_REASON.EX_OD_EXEC_NULL.getCode())
            return NoFillReason.EX_OD_EXEC_NULL;
        
        if(request.getNoFillReason().getCode() == Request.NO_FILL_REASON.EX_OD_API_INCORRECT.getCode())
            return NoFillReason.EX_OD_API_INCORRECT;
        
        if(request.getNoFillReason().getCode() == Request.NO_FILL_REASON.EX_OD_RESP_EMPTY.getCode())
            return NoFillReason.EX_OD_RESP_EMPTY;
        
        if(request.getNoFillReason().getCode() == Request.NO_FILL_REASON.EX_OD_BID_RESP_EMPTY.getCode())
            return NoFillReason.EX_OD_BID_RESP_EMPTY;
        
        if(request.getNoFillReason().getCode() == Request.NO_FILL_REASON.EX_OD_AP_INCORRECT.getCode())
            return NoFillReason.EX_OD_AP_INCORRECT;
        
        if(request.getNoFillReason().getCode() == Request.NO_FILL_REASON.EX_OD_WIN_NULL.getCode())
            return NoFillReason.EX_OD_WIN_NULL;
        
        if(request.getNoFillReason().getCode() == Request.NO_FILL_REASON.EX_OD_EXCEPTION.getCode())
            return NoFillReason.EX_OD_EXCEPTION;
        
        if(request.getNoFillReason().getCode() == Request.NO_FILL_REASON.ONLY_DIRECT_DP.getCode())
            return NoFillReason.ONLY_DIRECT_DP;
        
        if(request.getNoFillReason().getCode() == Request.NO_FILL_REASON.ONLY_DIRECTthenMed_DP.getCode())
            return NoFillReason.ONLY_DIRECTthenMed_DP;
        
        if(request.getNoFillReason().getCode() == Request.NO_FILL_REASON.ONLY_DIRECTthenDSP_DP.getCode())
            return NoFillReason.ONLY_DIRECTthenDSP_DP;
        
        if(request.getNoFillReason().getCode() == Request.NO_FILL_REASON.NATIVE_MISMATCH.getCode())
            return NoFillReason.NATIVE_MISMATCH;
        if(request.getNoFillReason().getCode() == Request.NO_FILL_REASON.VIDEO_MISMATCH.getCode())
            return NoFillReason.VIDEO_MISMATCH;
        if(request.getNoFillReason().getCode() == Request.NO_FILL_REASON.Video_ApiFramework.getCode())
            return NoFillReason.Video_ApiFramework;
        if(request.getNoFillReason().getCode() == Request.NO_FILL_REASON.Video_Mime.getCode())
            return NoFillReason.Video_Mime;
        if(request.getNoFillReason().getCode() == Request.NO_FILL_REASON.Video_Duration.getCode())
            return NoFillReason.Video_Duration;
        if(request.getNoFillReason().getCode() == Request.NO_FILL_REASON.Video_Protocol.getCode())
            return NoFillReason.Video_Protocol;
        if(request.getNoFillReason().getCode() == Request.NO_FILL_REASON.Video_StartDelay.getCode())
            return NoFillReason.Video_StartDelay;
        if(request.getNoFillReason().getCode() == Request.NO_FILL_REASON.Video_Width.getCode())
            return NoFillReason.Video_Width;
        if(request.getNoFillReason().getCode() == Request.NO_FILL_REASON.Video_Height.getCode())
            return NoFillReason.Video_Height;
        if(request.getNoFillReason().getCode() == Request.NO_FILL_REASON.Video_Linearity.getCode())
            return NoFillReason.Video_Linearity;
        if(request.getNoFillReason().getCode() == Request.NO_FILL_REASON.Video_MaxExtended.getCode())
            return NoFillReason.Video_MaxExtended;
        if(request.getNoFillReason().getCode() == Request.NO_FILL_REASON.Video_BitRate.getCode())
            return NoFillReason.Video_BitRate;
        if(request.getNoFillReason().getCode() == Request.NO_FILL_REASON.Video_Boxing.getCode())
            return NoFillReason.Video_Boxing;
        if(request.getNoFillReason().getCode() == Request.NO_FILL_REASON.Video_PlayBack.getCode())
            return NoFillReason.Video_PlayBack;
        if(request.getNoFillReason().getCode() == Request.NO_FILL_REASON.Video_Delivery.getCode())
            return NoFillReason.Video_Delivery;
        if(request.getNoFillReason().getCode() == Request.NO_FILL_REASON.Video_CompanionType.getCode())
            return NoFillReason.Video_CompanionType;
        if(request.getNoFillReason().getCode() == Request.NO_FILL_REASON.Video_Exception.getCode())
            return NoFillReason.Video_Exception;
        if(request.getNoFillReason().getCode() == Request.NO_FILL_REASON.CREATIVE_NOT_VIDEO.getCode())
            return NoFillReason.CREATIVE_NOT_VIDEO;
        if(request.getNoFillReason().getCode() == Request.NO_FILL_REASON.DEAL_ID_MISMATCH.getCode())
            return NoFillReason.DEAL_ID_MISMATCH;
        if(request.getNoFillReason().getCode() == Request.NO_FILL_REASON.VIDEO_PROPS_NULL.getCode())
            return NoFillReason.VIDEO_PROPS_NULL;
        if(request.getNoFillReason().getCode() == Request.NO_FILL_REASON.NATIVE_REQ_NULL.getCode())
            return NoFillReason.NATIVE_REQ_NULL;
        if(request.getNoFillReason().getCode() == Request.NO_FILL_REASON.NATIVE_PROPS_NULL.getCode())
            return NoFillReason.NATIVE_PROPS_NULL;
        if(request.getNoFillReason().getCode() == Request.NO_FILL_REASON.NATIVE_REQ_ASSET_NULL.getCode())
            return NoFillReason.NATIVE_REQ_ASSET_NULL;
        if(request.getNoFillReason().getCode() == Request.NO_FILL_REASON.CREATIVE_NOT_NATIVE.getCode())
            return NoFillReason.CREATIVE_NOT_NATIVE;
        if(request.getNoFillReason().getCode() == Request.NO_FILL_REASON.NATIVE_TITLE_LEN.getCode())
            return NoFillReason.NATIVE_TITLE_LEN;
        if(request.getNoFillReason().getCode() == Request.NO_FILL_REASON.NATIVE_IMGSIZE.getCode())
            return NoFillReason.NATIVE_IMGSIZE;
        if(request.getNoFillReason().getCode() == Request.NO_FILL_REASON.NATIVE_DESC_LEN.getCode())
            return NoFillReason.NATIVE_DESC_LEN;
        return null;
    }
}

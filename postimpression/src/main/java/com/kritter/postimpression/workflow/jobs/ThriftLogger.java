package com.kritter.postimpression.workflow.jobs;

import com.kritter.core.workflow.Context;
import com.kritter.core.workflow.Job;
import com.kritter.core.workflow.Workflow;
import com.kritter.postimpression.entity.Request;

import com.kritter.postimpression.enricher_fraud.checker.OnlineFraudUtils.ONLINE_FRAUD_REASON;
import com.kritter.postimpression.thrift.struct.PostImpressionEvent;
import com.kritter.postimpression.thrift.struct.PostImpressionRequestResponse;
import com.kritter.postimpression.thrift.struct.PostImpressionTerminationReason;
import com.kritter.postimpression.urlreader.PostImpressionEventUrlReader;
import com.kritter.constants.BEventType;
import com.kritter.constants.ConnectionType;
import com.kritter.constants.DefaultCurrency;
import com.kritter.utils.common.url.URLField;
import org.apache.commons.codec.binary.Base64;
import org.apache.thrift.TException;
import org.apache.thrift.TSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import com.kritter.constants.INVENTORY_SOURCE;
import com.kritter.constants.NoFraudPostImpEvents;

/**
 * This class logs postimpression parameters in a thrift format
 * using postimpression thrift structure.
 */

public class ThriftLogger implements Job
{
    private String name;
    private Logger applicationLogger;
    private Logger thriftLogger;
    private String postImpressionRequestObjectKey;
    private String userAgentHeaderName;
    private String xForwardedForHeaderName;
    private String remoteAddressHeaderName;
    private static final String AUCTION_CURRENCY = DefaultCurrency.defaultCurrency.getName();
    private static final String CONVERSION_INFO_KEY = "conv";
    private static final String REFERER_HEADER_NAME = "Referer";

    public ThriftLogger(String name,
                        String applicationLoggerName,
                        String thriftLoggerName,
                        String postImpressionRequestObjectKey,
                        String userAgentHeaderName,
                        String xForwardedForHeaderName,
                        String remoteAddressHeaderName)
    {
        this.name = name;
        this.applicationLogger = LoggerFactory.getLogger(applicationLoggerName);
        this.thriftLogger = LoggerFactory.getLogger(thriftLoggerName);
        this.postImpressionRequestObjectKey = postImpressionRequestObjectKey;
        this.userAgentHeaderName = userAgentHeaderName;
        this.xForwardedForHeaderName = xForwardedForHeaderName;
        this.remoteAddressHeaderName = remoteAddressHeaderName;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void execute(Context context)
    {

        this.applicationLogger.info("Inside ThriftLogger class of PostImpressionWorkflow.");

        byte[] serializedPostImpressionRequest = null;
        Base64 base64Encoder = new Base64(0);

        try
        {
            Request request = (Request)context.getValue(this.postImpressionRequestObjectKey);
            HttpServletRequest httpServletRequest = (HttpServletRequest)context.getValue(Workflow.CONTEXT_REQUEST_KEY);

            PostImpressionRequestResponse postImpressionRequestResponse = new PostImpressionRequestResponse();
            if(request.getPostImpressionEvent().getUrlIdentifierPrefix().equals
                    (PostImpressionEventUrlReader.POSTIMPRESSION_EVENT_URL_PREFIX.
                     CONVERSION_FEEDBACK.getUrlIdentifierPrefix()))
            {
                this.applicationLogger.debug("Setting requestId as conversion's click id.");
                postImpressionRequestResponse.setRequestId(request.getClickRequestIdReceivedFromConversion());
            }
            else
                postImpressionRequestResponse.setRequestId(request.getRequestId());

            PostImpressionEvent postImpressionEvent = fetchPostImpressionEvent(request);

            postImpressionRequestResponse.setEvent(postImpressionEvent);

            PostImpressionTerminationReason postImpressionTerminationReason =
                    fetchPostImpressionTerminationReason(request);

            postImpressionRequestResponse.setStatus(postImpressionTerminationReason);

            postImpressionRequestResponse.setSrcRequestId(request.getAdservingRequestId());

            postImpressionRequestResponse.setImpressionId(request.getImpressionId());

            if(null!=request.getAdId())
                postImpressionRequestResponse.setAdId(request.getAdId());

            postImpressionRequestResponse.setSrcUrlHash(request.getUrlHash());

            if(null!=request.getSiteId())
                postImpressionRequestResponse.setSiteId(request.getSiteId());

            if(null!=request.getUiCountryId())
                postImpressionRequestResponse.setCountryId(request.getUiCountryId());

            if(null!=request.getUiCountryCarrierId())
                postImpressionRequestResponse.setCountryCarrierId(request.getUiCountryCarrierId());

            if(request.getConnectionType() == null) {
                postImpressionRequestResponse.setConnectionTypeId(ConnectionType.UNKNOWN.getId());
            } else {
                postImpressionRequestResponse.setConnectionTypeId(request.getConnectionType().getId());
            }

            //set device type
            if(null != request.getDeviceTypeId())
                postImpressionRequestResponse.setDeviceType(request.getDeviceTypeId());

            //device id should be enough to find other handset attributes if required at all.
            if(null!=request.getDeviceId())
                postImpressionRequestResponse.setDeviceId(request.getDeviceId());
            if(null!=request.getDeviceManufacturerId())
                postImpressionRequestResponse.setDeviceManufacturerId(request.getDeviceManufacturerId());
            if(null!=request.getDeviceModelId())
                postImpressionRequestResponse.setDeviceModelId(request.getDeviceModelId());
            if(null!=request.getDeviceOsId())
                postImpressionRequestResponse.setDeviceOsId(request.getDeviceOsId());
            if(null!=request.getBrowserId())
                postImpressionRequestResponse.setDeviceBrowserId(request.getBrowserId());

            String userAgent = httpServletRequest.getHeader(this.userAgentHeaderName);
            String ipAddress = httpServletRequest.getHeader(this.remoteAddressHeaderName);
            String xForwardedFor = httpServletRequest.getHeader(this.xForwardedForHeaderName);
            String refererUrl = httpServletRequest.getHeader(REFERER_HEADER_NAME);

            if(null != userAgent)
                postImpressionRequestResponse.setUserAgent(userAgent);

            if(null != ipAddress)
                postImpressionRequestResponse.setIpAddress(ipAddress);

            if(null != xForwardedFor)
                postImpressionRequestResponse.setXForwardedFor(xForwardedFor);

            if(null != refererUrl)
                postImpressionRequestResponse.setReferer(refererUrl);

            //set slotid.
            if(null != request.getSlotId())
                postImpressionRequestResponse.setSlotId(request.getSlotId());

            Enumeration<String> urlParamEnumeration = httpServletRequest.getParameterNames();
            Map<String,String> urlExtraParameters = new HashMap<String, String>();

            //set conversion data into url extra parameters
            if(null != request.getConversionDataForClickEvent())
                urlExtraParameters.put(CONVERSION_INFO_KEY,request.getConversionDataForClickEvent());

            while(urlParamEnumeration.hasMoreElements())
            {
                String paramKey = urlParamEnumeration.nextElement();
                urlExtraParameters.put(paramKey,httpServletRequest.getParameter(paramKey));
            }

            if(urlExtraParameters.size() > 0)
            {
                postImpressionRequestResponse.setUrlExtraParameters(urlExtraParameters);
            }

            //set time and event time.
            postImpressionRequestResponse.setEventTime(request.getEventTime()/1000);
            postImpressionRequestResponse.setTime(System.currentTimeMillis()/1000);

            //set bidder model id
            postImpressionRequestResponse.setBidderModelId(request.getBidderModelId());
            //set marketplace value
            if(null != request.getAdEntity())
                postImpressionRequestResponse.setMarketplace_id
                            ((short)request.getAdEntity().getMarketPlace().getCode());

            /*set advertiser and publisher integer id from mysql if available*/
            postImpressionRequestResponse.setAdv_inc_id(-1);
            postImpressionRequestResponse.setPub_inc_id(-1);

            if(null != request.getAdEntity())
            {
                postImpressionRequestResponse.setAdv_inc_id(request.getAdEntity().getAccountId());
            }
            if(null != request.getDetectedSite())
            {
                postImpressionRequestResponse.setPub_inc_id(request.getDetectedSite().getPublisherIncId());
            }

            //set campaign id
            if(null != request.getAdEntity())
                postImpressionRequestResponse.setCampaignId(request.getAdEntity().getCampaignIncId());

            //set cpa goal if present
            if(null != request.getAdEntity() && null != request.getAdEntity().getCpaGoal())
                postImpressionRequestResponse.setCpa_goal(request.getAdEntity().getCpaGoal());

            //set internal bid and advertiser bid.
            if(null != request.getInternalMaxBid()){
                postImpressionRequestResponse.setInternal_max_bid(request.getInternalMaxBid());
                if(PostImpressionEvent.INT_EXCHANGE_WIN == postImpressionRequestResponse.getEvent() && 
                        null != request.getWinBidPriceFromExchange()){
                    postImpressionRequestResponse.setInternal_max_bid(request.getWinBidPriceFromExchange());
                }

            }
            if(null != request.getAdvertiserBid()){
                postImpressionRequestResponse.setAdvertiser_bid(request.getAdvertiserBid());
                if(PostImpressionEvent.INT_EXCHANGE_WIN == postImpressionRequestResponse.getEvent() && 
                        null != request.getWinBidPriceFromExchange()){
                    postImpressionRequestResponse.setAdvertiser_bid(request.getWinBidPriceFromExchange());
                }
            }

            //set default
            postImpressionRequestResponse.setExchangeId(-1);
            //set exchange id if traffic source is an adexchange.
            if(null != request.getInventorySource() && null != request.getDetectedSite())
            {
                if(INVENTORY_SOURCE.RTB_EXCHANGE.getCode() == request.getInventorySource().shortValue())
                    postImpressionRequestResponse.setExchangeId(request.getDetectedSite().getPublisherIncId());
            }

            //set selected site category id.
            postImpressionRequestResponse.setSelectedSiteCategoryId(request.getSelectedSiteCategoryId());

            //set supply source type code value as 2-wap, 3-app
            postImpressionRequestResponse.setSupply_source_type(request.getSupplySourceTypeCode().shortValue());

            //set external supply attributes internal id
            postImpressionRequestResponse.setExt_supply_attr_internal_id(request.getExternalSupplyAttributesInternalId().intValue());

            //set buyer uid (internal advertising id), received at the time of csc event directly from user's browser.
            postImpressionRequestResponse.setBuyerUid(request.getBuyerUid());

            //set url version and inventory source.
            if(null != request.getUrlVersion())
                postImpressionRequestResponse.setUrlVersion(request.getUrlVersion());

            postImpressionRequestResponse.setInventorySource(request.getInventorySource());

            //set win notification related parameters.
            postImpressionRequestResponse.setAuction_currency(AUCTION_CURRENCY);
            if(PostImpressionEventUrlReader.POSTIMPRESSION_EVENT_URL_PREFIX.NOFRDP.
                getUrlIdentifierPrefix().equals(request.getPostImpressionEvent().getUrlIdentifierPrefix())){
            	 postImpressionRequestResponse.setAuction_currency(null);
            }
            if(null != request.getWinBidPriceFromExchange())
                postImpressionRequestResponse.setAuction_price(request.getWinBidPriceFromExchange());
            if(null != request.getAuctionId())
                postImpressionRequestResponse.setAuction_id(request.getAuctionId());
            if(null != request.getAuctionBidId())
                postImpressionRequestResponse.setAuction_bid_id(request.getAuctionBidId());
            if(null != request.getAuctionImpressionId())
                postImpressionRequestResponse.setAuction_imp_id(request.getAuctionImpressionId());
            if(null != request.getBidPriceToExchange())
                postImpressionRequestResponse.setBidprice_to_exchange(request.getBidPriceToExchange());
            if(null != request.getTevent()){
                postImpressionRequestResponse.setTevent(request.getTevent());
            }
            if(null != request.getTtype()){
                postImpressionRequestResponse.setTeventtype(request.getTtype());
            }

            /**********************************Set url fields from adserving**************************************/
            Map<Short,URLField> urlFieldMap = request.getUrlFieldsFromAdservingMap();
            if(null != urlFieldMap)
            {
                URLField bidFloor = urlFieldMap.get(URLField.BID_FLOOR.getCode());
                if(null != bidFloor && null != bidFloor.getUrlFieldProperties().getFieldValue())
                    postImpressionRequestResponse.setBidFloor(((Float)bidFloor.getUrlFieldProperties().getFieldValue()).doubleValue());

                URLField kritterUserId = urlFieldMap.get(URLField.KRITTER_USER_ID.getCode());
                if(null != kritterUserId && null != kritterUserId.getUrlFieldProperties().getFieldValue())
                    postImpressionRequestResponse.setKritterUserId((String)kritterUserId.getUrlFieldProperties().getFieldValue());

                URLField exchangeUserId = urlFieldMap.get(URLField.EXCHANGE_USER_ID.getCode());
                if(null != exchangeUserId && null != exchangeUserId.getUrlFieldProperties().getFieldValue())
                    postImpressionRequestResponse.setExchangeUserId((String)exchangeUserId.getUrlFieldProperties().getFieldValue());

                URLField extSupplyIdFromExchange = urlFieldMap.get(URLField.EXTERNAL_SITE_ID.getCode());
                if(null != extSupplyIdFromExchange && null != extSupplyIdFromExchange.getUrlFieldProperties().getFieldValue())
                    postImpressionRequestResponse.setExternalSiteAppId((String)extSupplyIdFromExchange.getUrlFieldProperties().getFieldValue());

                URLField ifa = urlFieldMap.get(URLField.ID_FOR_ADVERTISER.getCode());
                if(null != ifa && null != ifa.getUrlFieldProperties().getFieldValue())
                    postImpressionRequestResponse.setIfa((String)ifa.getUrlFieldProperties().getFieldValue());

                URLField dpidmd5 = urlFieldMap.get(URLField.DEVICE_PLATFORM_ID_MD5.getCode());
                if(null != dpidmd5 && null != dpidmd5.getUrlFieldProperties().getFieldValue())
                    postImpressionRequestResponse.setDpidmd5((String)dpidmd5.getUrlFieldProperties().getFieldValue());

                URLField dpidsha1 = urlFieldMap.get(URLField.DEVICE_PLATFORM_ID_SHA1.getCode());
                if(null != dpidsha1 && null != dpidsha1.getUrlFieldProperties().getFieldValue())
                    postImpressionRequestResponse.setDpidsha1((String)dpidsha1.getUrlFieldProperties().getFieldValue());

                URLField macsha1 = urlFieldMap.get(URLField.MAC_ADDRESS_SHA1.getCode());
                if(null != macsha1 && null != macsha1.getUrlFieldProperties().getFieldValue())
                    postImpressionRequestResponse.setMacsha1((String)macsha1.getUrlFieldProperties().getFieldValue());

                URLField macmd5 = urlFieldMap.get(URLField.MAC_ADDRESS_MD5.getCode());
                if(null != macmd5 && null != macmd5.getUrlFieldProperties().getFieldValue())
                    postImpressionRequestResponse.setMacmd5((String)macmd5.getUrlFieldProperties().getFieldValue());
            }
            /**********************************Done setting url fields from adserving*****************************/

            try
            {
                //Since tserializer is not thread safe, so create a new instance everytime.
                TSerializer thriftSerializer = new TSerializer();
                serializedPostImpressionRequest = thriftSerializer.serialize(postImpressionRequestResponse);
                serializedPostImpressionRequest =  base64Encoder.encode(serializedPostImpressionRequest);
            }
            catch(TException t)
            {
                this.applicationLogger.error("TException inside ThriftLogger in postimpression workflow.",t);
            }

        }
        catch (Exception e)
        {
            this.applicationLogger.error("Exception inside ThriftLogger in postimpression workflow.",e);
        }

        this.applicationLogger.info("Done prepairing thrift object, now writing it to thrift data file");
        this.thriftLogger.info(new String(serializedPostImpressionRequest));
    }

    private PostImpressionEvent fetchPostImpressionEvent(Request request)
    {
        PostImpressionEvent postImpressionEvent = null;
        if(PostImpressionEventUrlReader.POSTIMPRESSION_EVENT_URL_PREFIX.NOFRDP.
                getUrlIdentifierPrefix().equals(request.getPostImpressionEvent().getUrlIdentifierPrefix())){
            if(NoFraudPostImpEvents.clk == request.getNfrdpType()){
                postImpressionEvent = PostImpressionEvent.CLICK;
            }else if(NoFraudPostImpEvents.csc == request.getNfrdpType()){
                postImpressionEvent = PostImpressionEvent.RENDER;
            }if(NoFraudPostImpEvents.win == request.getNfrdpType()){
                postImpressionEvent = PostImpressionEvent.WIN_NOTIFICATION;
            }
        }if(PostImpressionEventUrlReader.POSTIMPRESSION_EVENT_URL_PREFIX.TEVENT.
                getUrlIdentifierPrefix().equals(request.getPostImpressionEvent().getUrlIdentifierPrefix())){
            postImpressionEvent = PostImpressionEvent.TEVENT;
        }else if(PostImpressionEventUrlReader.POSTIMPRESSION_EVENT_URL_PREFIX.BEVENT.
                getUrlIdentifierPrefix().equals(request.getPostImpressionEvent().getUrlIdentifierPrefix())){
            if(BEventType.cscwin == request.getBEventType()){
                postImpressionEvent = PostImpressionEvent.BEVENT_CSCWIN;
            }
        }else if(PostImpressionEventUrlReader.POSTIMPRESSION_EVENT_URL_PREFIX.CLICK.
                getUrlIdentifierPrefix().equals(request.getPostImpressionEvent().getUrlIdentifierPrefix())){
            postImpressionEvent = PostImpressionEvent.CLICK;
        }else if(PostImpressionEventUrlReader.POSTIMPRESSION_EVENT_URL_PREFIX.MACRO_CLICK.
                getUrlIdentifierPrefix().equals(request.getPostImpressionEvent().getUrlIdentifierPrefix())){
            postImpressionEvent = PostImpressionEvent.CLICK;
        }else if(PostImpressionEventUrlReader.POSTIMPRESSION_EVENT_URL_PREFIX.CSC.
                getUrlIdentifierPrefix().equals(request.getPostImpressionEvent().getUrlIdentifierPrefix())){
            postImpressionEvent = PostImpressionEvent.RENDER;
        }else if(PostImpressionEventUrlReader.POSTIMPRESSION_EVENT_URL_PREFIX.INT_EXCHANGE_WIN.
                getUrlIdentifierPrefix().equals(request.getPostImpressionEvent().getUrlIdentifierPrefix())){
            postImpressionEvent = PostImpressionEvent.INT_EXCHANGE_WIN;
        }
        else if(PostImpressionEventUrlReader.POSTIMPRESSION_EVENT_URL_PREFIX.WIN_NOTIFICATION.
                getUrlIdentifierPrefix().equals(request.getPostImpressionEvent().getUrlIdentifierPrefix())){
            postImpressionEvent = PostImpressionEvent.WIN_NOTIFICATION;
        }
        else if(PostImpressionEventUrlReader.POSTIMPRESSION_EVENT_URL_PREFIX.WIN_API_NOTIFICATION.
                getUrlIdentifierPrefix().equals(request.getPostImpressionEvent().getUrlIdentifierPrefix())){
            postImpressionEvent = PostImpressionEvent.WIN_NOTIFICATION;
        }
        else if(PostImpressionEventUrlReader.POSTIMPRESSION_EVENT_URL_PREFIX.CONVERSION_FEEDBACK.
                getUrlIdentifierPrefix().equals(request.getPostImpressionEvent().getUrlIdentifierPrefix())){
            postImpressionEvent = PostImpressionEvent.CONVERSION;
        }
        else if(PostImpressionEventUrlReader.POSTIMPRESSION_EVENT_URL_PREFIX.COOKIE_BASED_CONV_JS.
                getUrlIdentifierPrefix().equals(request.getPostImpressionEvent().getUrlIdentifierPrefix())){
            postImpressionEvent = PostImpressionEvent.CONVERSION;
        }
        else if(PostImpressionEventUrlReader.POSTIMPRESSION_EVENT_URL_PREFIX.THIRD_PARTY_CLICK_ALIAS_URL.
                getUrlIdentifierPrefix().equals(request.getPostImpressionEvent().getUrlIdentifierPrefix())){
            postImpressionEvent = PostImpressionEvent.THIRD_PARTY_CLICK_EVENT;
        }
        else if(PostImpressionEventUrlReader.POSTIMPRESSION_EVENT_URL_PREFIX.TRACKING_URL_FROM_THIRD_PARTY.
                getUrlIdentifierPrefix().equals(request.getPostImpressionEvent().getUrlIdentifierPrefix())){
            postImpressionEvent = PostImpressionEvent.THIRD_PARTY_TRACKING_EVENT;
        }
        else if(PostImpressionEventUrlReader.POSTIMPRESSION_EVENT_URL_PREFIX.USR.
                getUrlIdentifierPrefix().equals(request.getPostImpressionEvent().getUrlIdentifierPrefix())){
            postImpressionEvent = PostImpressionEvent.USR;
        }

        return postImpressionEvent;
    }

    private PostImpressionTerminationReason fetchPostImpressionTerminationReason(Request request){

        PostImpressionTerminationReason postImpressionTerminationReason = null;

        if(request.getOnlineFraudReason().getFraudReasonValue().equals(ONLINE_FRAUD_REASON.HEALTHY_REQUEST.getFraudReasonValue())){
            postImpressionTerminationReason = PostImpressionTerminationReason.HEALTHY_REQUEST;
        }
        else if(request.getOnlineFraudReason().getFraudReasonValue().equals(ONLINE_FRAUD_REASON.URL_DATA_NOT_COMPLETE.getFraudReasonValue())){
            postImpressionTerminationReason = PostImpressionTerminationReason.URL_DATA_NOT_COMPLETE;
        }
        else if(request.getOnlineFraudReason().getFraudReasonValue().equals(ONLINE_FRAUD_REASON.LOCATION_MISMATCH.getFraudReasonValue())){
            postImpressionTerminationReason = PostImpressionTerminationReason.LOCATION_MISMATCH;
        }
        else if(request.getOnlineFraudReason().getFraudReasonValue().equals(ONLINE_FRAUD_REASON.SITE_ID_MISSING_FROM_REQUEST.getFraudReasonValue())){
            postImpressionTerminationReason = PostImpressionTerminationReason.SITE_ID_MISSING_FROM_REQUEST;
        }

        else if(request.getOnlineFraudReason().getFraudReasonValue().equals(ONLINE_FRAUD_REASON.AD_ID_MISSING.getFraudReasonValue())){
            postImpressionTerminationReason = PostImpressionTerminationReason.AD_ID_MISSING;
        }
        else if(request.getOnlineFraudReason().getFraudReasonValue().equals(ONLINE_FRAUD_REASON.AD_ID_NOT_FOUND.getFraudReasonValue())){

            postImpressionTerminationReason = PostImpressionTerminationReason.AD_ID_NOT_FOUND;
        }
        else if(request.getOnlineFraudReason().getFraudReasonValue().equals(ONLINE_FRAUD_REASON.BOT_HANDSET.getFraudReasonValue())){
            postImpressionTerminationReason = PostImpressionTerminationReason.BOT_HANDSET;
        }
        else if(request.getOnlineFraudReason().getFraudReasonValue().equals(ONLINE_FRAUD_REASON.HANDSET_ID_MISMATCH.getFraudReasonValue())){
            postImpressionTerminationReason = PostImpressionTerminationReason.HANDSET_ID_MISMATCH;
        }
        else if(request.getOnlineFraudReason().getFraudReasonValue().equals(ONLINE_FRAUD_REASON.HANDSET_ID_MISSING_FROM_REQUEST.getFraudReasonValue())){
            postImpressionTerminationReason = PostImpressionTerminationReason.HANDSET_ID_MISSING_FROM_REQUEST;
        }
        else if(request.getOnlineFraudReason().getFraudReasonValue().equals(ONLINE_FRAUD_REASON.HANDSET_UNDETECTED.getFraudReasonValue())){
            postImpressionTerminationReason = PostImpressionTerminationReason.HANDSET_UNDETECTED;
        }
        else if(request.getOnlineFraudReason().getFraudReasonValue().equals(ONLINE_FRAUD_REASON.IMPRESSION_EXPIRED.getFraudReasonValue())){
            postImpressionTerminationReason = PostImpressionTerminationReason.IMPRESSION_EXPIRED;
        }
        else if(request.getOnlineFraudReason().getFraudReasonValue().equals(ONLINE_FRAUD_REASON.CONVERSION_EXPIRED.getFraudReasonValue())){
            postImpressionTerminationReason = PostImpressionTerminationReason.CONVERSION_EXPIRED;
        }
        else if(request.getOnlineFraudReason().getFraudReasonValue().equals(ONLINE_FRAUD_REASON.IMPRESSION_ID_MISSING_FROM_REQUEST.getFraudReasonValue())){
            postImpressionTerminationReason = PostImpressionTerminationReason.IMPRESSION_ID_MISSING_FROM_REQUEST;
        }
        else if(request.getOnlineFraudReason().getFraudReasonValue().equals(ONLINE_FRAUD_REASON.INTERNAL_SERVER_ERROR.getFraudReasonValue())){
            postImpressionTerminationReason = PostImpressionTerminationReason.INTERNAL_SERVER_ERROR;
        }
        else if(request.getOnlineFraudReason().getFraudReasonValue().equals(ONLINE_FRAUD_REASON.LOCATION_ID_MISSING_FROM_REQUEST.getFraudReasonValue())){

            postImpressionTerminationReason = PostImpressionTerminationReason.LOCATION_ID_MISSING_FROM_REQUEST;
        }
        else if(request.getOnlineFraudReason().getFraudReasonValue().equals(ONLINE_FRAUD_REASON.LOCATION_UNDETECTED.getFraudReasonValue())){
            postImpressionTerminationReason = PostImpressionTerminationReason.LOCATION_UNDETECTED;
        }

        else if(request.getOnlineFraudReason().getFraudReasonValue().equals(ONLINE_FRAUD_REASON.LOCATION_MISMATCH.getFraudReasonValue())){
            postImpressionTerminationReason = PostImpressionTerminationReason.LOCATION_MISMATCH;
        }

        else if(request.getOnlineFraudReason().getFraudReasonValue().equals(ONLINE_FRAUD_REASON.SITE_ID_MISSING_FROM_REQUEST.getFraudReasonValue())){
            postImpressionTerminationReason = PostImpressionTerminationReason.SITE_ID_MISSING_FROM_REQUEST;
        }
        else if(request.getOnlineFraudReason().getFraudReasonValue().equals(ONLINE_FRAUD_REASON.SITE_ID_NOT_PRESENT_IN_CACHE.getFraudReasonValue())){
            postImpressionTerminationReason = PostImpressionTerminationReason.SITE_ID_NOT_PRESENT_IN_CACHE;
        }
        else if(request.getOnlineFraudReason().getFraudReasonValue().equals(ONLINE_FRAUD_REASON.SITE_UNFIT.getFraudReasonValue())){
            postImpressionTerminationReason = PostImpressionTerminationReason.SITE_UNFIT;
        }
        else if(request.getOnlineFraudReason().getFraudReasonValue().equals(ONLINE_FRAUD_REASON.EVENT_DUPLICATE.getFraudReasonValue())){
            postImpressionTerminationReason = PostImpressionTerminationReason.DUPLICATE;
        }
        else if(request.getOnlineFraudReason().getFraudReasonValue().equals(ONLINE_FRAUD_REASON.BILLABLE_EVENT.getFraudReasonValue())){
            postImpressionTerminationReason = PostImpressionTerminationReason.BILLABLE_EVENT;
        }
        else if(request.getOnlineFraudReason().getFraudReasonValue().equals(ONLINE_FRAUD_REASON.RETARGETING_SEGMENT_NF.getFraudReasonValue())){
            postImpressionTerminationReason = PostImpressionTerminationReason.RETARGETING_SEGMENT_NF;
        }

        return postImpressionTerminationReason;
    }
}

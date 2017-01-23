package com.kritter.entity.reqres.entity;

import com.kritter.adserving.thrift.struct.NoFillReason;
import com.kritter.bidrequest.entity.IBidRequest;
import com.kritter.common.site.entity.Site;
import com.kritter.constants.BidRequestImpressionType;
import com.kritter.constants.ConnectionType;
import com.kritter.geo.common.entity.Country;
import com.kritter.geo.common.entity.InternetServiceProvider;
import com.kritter.entity.exchangethrift.CreateExchangeThrift;
import com.kritter.entity.user.userid.ExternalUserId;
import com.kritter.utils.common.url.URLFieldFactory;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

import com.kritter.constants.INVENTORY_SOURCE;
import com.kritter.constants.OpenRTBVersion;
import com.kritter.device.common.entity.HandsetMasterData;

/**
 * This class represents an ad request, could represent some other request as well
 * ,i.e., where an ad is not necessary as response.
 * Contains all the information that request contains as well as the enriched objects.
 * The request could be from an exchange, publisher, ssp ,dcp,etc.
 *
 * There would be certain information that are modelled as java objects separately.
 * The uncommon information could be put into the data map.
 *
 * The idea is to capture universal request information in this object, in some cases
 * universe would be fully filled(like exchanges), in other cases it would be very
 * partially filled(like direct publisher).
 */

public class Request
{
    //get this from context.
    @Getter
    private String requestId;
    @Getter
    private short inventorySource;
    @Getter
    private boolean isRequestForSystemDebugging;
    @Getter
    private StringBuffer debugRequestBuffer;
    // time in milliseconds since epoch of the current request
    @Getter
    private final Long time;
    @Getter @Setter
    private int requestedNumberOfAds;
    @Getter @Setter
    private short sitePlatformValue;
    @Getter @Setter
    private Double requestingLatitudeValue;
    @Getter @Setter
    private Double requestingLongitudeValue;
    @Getter @Setter
    private Integer richMediaParameterValue;

    /*External user ids that are available from different inventory sources.*/
    @Getter @Setter
    private Set<ExternalUserId> externalUserIds;
    /**
     * UserId received from exchange bid request or from
     * direct traffic as a parameter in ad-request.
     */
    @Getter @Setter
    private String userId;
    @Getter @Setter
    private REQUEST_ENRICHMENT_ERROR_CODE requestEnrichmentErrorCode;
    @Getter @Setter
    private NoFillReason noFillReason;
    @Getter @Setter
    private String requestEnrichmentErrorDetail;
    @Getter @Setter
    private Site site;
    @Getter @Setter
    private String userAgent;
    @Getter @Setter
    private String ipAddressUsedForDetection;
    @Getter @Setter
    private HandsetMasterData handsetMasterData;
    @Getter @Setter
    private Country country;
    @Getter @Setter
    private InternetServiceProvider internetServiceProvider;
    @Getter @Setter
    private Integer countryUserInterfaceId;
    @Getter @Setter
    private Integer carrierUserInterfaceId;
    @Getter @Setter
    private Integer stateUserInterfaceId;
    @Getter @Setter
    private Integer cityUserInterfaceId;
    @Getter @Setter
    private String dataSourceNameUsedForStateDetection;
    @Getter @Setter
    private String dataSourceNameUsedForCityDetection;
    @Getter @Setter
    private ConnectionType connectionType;
    @Getter @Setter
    private boolean externalResouceURLRequired = false;

    private Map<String,AdExchangeInfo> adExchangeInfoMapPerImpression;

    @Getter @Setter
    private boolean isInterstitialBidRequest;

    /**
     * The requested widths and heights for a slot would be in order with one-one correspondence.
     */
    @Getter @Setter
    private int[] requestedSlotWidths;
    @Getter @Setter
    private int[] requestedSlotHeights;
    @Getter @Setter
    private boolean exactBannerSizeRequired = false;

    @Getter @Setter
    private List<Short> requestedSlotIdList;
    /*firstImpRequestedSlotIdList -for nfr cases and to populated closest slot id for reporting */
    @Getter @Setter
    private List<Short> firstImpClosestRequestedSlotIdList;
    @Getter @Setter
    private String invocationCodeVersion;
    @Getter @Setter
    private String responseFormat;
    @Getter @Setter
    private String buyerUserId;
    @Getter @Setter
    private String mccMncCodeCouple;

    /*This object contains open rtb bid request from ad-exchange.*/
    @Getter @Setter
    private IBidRequest bidRequest;

    /*Internal id for the exchange's supply attributes*/
    @Getter @Setter
    private Integer externalSupplyAttributesInternalId;

    @Getter @Setter
    private boolean isPassbackDoneForDirectPublisherSiteNoFill;

    @Getter @Setter
    private boolean writeResponseInsideExchangeAdaptor = false;

    @Getter @Setter
    private String language;

    @Getter @Setter
    private URLFieldFactory urlFieldFactory;
    @Getter @Setter
    private String generatedInformationForPostimpression;

    /*This field is populated when ad is found for rtb's impression element
    * ,since each impression has different floor value, this is average of
    * bidfloor of each impression for which ad is found.Reason for not
    * including non-ad impression's value is that it might corrupt the
    * value by large number which would have no sense in postimpression.*/
    @Getter @Setter
    private Float bidFloorForFilledExchangeImpressions;

    /*
    * This field is utilized to capture real time bid floor value sent by
    * the supply , this field is applicable only for direct supply
    * integrations and not valid for ad-exchange traffic.*/
    @Getter @Setter
    private Double bidFloorValueForNetworkSupply;

    /*AdId required in native ad request for external DSPs*/
    @Getter @Setter
    private Map<String,Integer> adUnitIdOfExternalDSPMapForNativeBidRequest;

    /*This field if available from direct publisher ad request ,
      is used for defining what type of impression request is to be
      constructed.*/
    @Getter @Setter
    private BidRequestImpressionType bidRequestImpressionType;
    @Getter @Setter
    private Boolean doNotTrack;

    /*This map would contain bid price values from DSPs*/
    @Getter @Setter
    private Map<String,Double> dspBidPriceResponseForExchangeRequest;

    @Getter @Setter
    private boolean passbackUsingFormatter = false;
    
    @Getter @Setter
    private boolean aggregatorOpenRTB = false;
    @Getter @Setter
    private OpenRTBVersion aggregatorversion;
    @Getter @Setter
    private com.kritter.bidrequest.entity.common.openrtbversion2_3.BidRequestParentNodeDTO openrtbObjTwoDotThree;
    //contains all request enrichment related error codes.
    @Getter @Setter
    private CreateExchangeThrift createExchangeThrift;

    private static final String DEALID_FLOOR_DELIM = ":";

    /**flag to mark request as test request.Any aggregator Network or Exchange might set this flag
     * to enable workflow to decide whether to perform time taking executions for this type of request
     * or simply allow next executions in line to decide on their workings.*/
    @Getter @Setter
    private Boolean isTestRequest;

    @Getter @Setter
    private Boolean secure=false;

    /**Adx specific adgroup id list against ad-slot-id.*/
    @Getter @Setter
    private Map<Integer,List<Long>> adxAdGroupIdListAgainstAdSlotId;
    @Getter @Setter
    private Boolean respDocWrite=false;

    public enum REQUEST_ENRICHMENT_ERROR_CODE
    {
        NO_ERROR(1,"ROK"),
        REQUEST_MALFORMED(2,"RMF"),
        SITE_NOT_FIT(3,"RSU"),
        DEVICE_UNDETECTED(4,"DUN"),
        DEVICE_BOT(5,"BOT");

        private int errorCode;
        private String errorName;

        private REQUEST_ENRICHMENT_ERROR_CODE(int errorCode,String errorName){
            this.errorCode = errorCode;
            this.errorName = errorName;
        }

        public int getErrorCode(){
            return this.errorCode;
        }

        public String getErrorName(){
            return this.errorName;
        }

    }

    private static final String NEW_LINE = "\n";

    public Request(String requestId,INVENTORY_SOURCE inventorySource) throws Exception {

        if(null==requestId || null==inventorySource)
            throw new Exception("RequestId or InventorySource is null.");

        this.requestId = requestId;
        this.inventorySource = inventorySource.getCode();
        this.debugRequestBuffer = new StringBuffer();
        this.adExchangeInfoMapPerImpression = new HashMap<String, AdExchangeInfo>();
        this.time = System.currentTimeMillis();
        this.urlFieldFactory = new URLFieldFactory();
    }

    public void setStrictBannerSizeForImpressionIdOfRTBExchange(String impressionId,boolean strictBannerSize)
    {
        AdExchangeInfo adExchangeInfo = this.adExchangeInfoMapPerImpression.get(impressionId);

        if(null == adExchangeInfo)
            adExchangeInfo = new AdExchangeInfo();

        adExchangeInfo.setStrictBannerSize(strictBannerSize);

        this.adExchangeInfoMapPerImpression.put(impressionId,adExchangeInfo);
    }

    public boolean fetchStrictBannerSizeForImpressionId(String impressionId)
    {
        if(null == impressionId)
            return false;

        AdExchangeInfo adExchangeInfo = this.adExchangeInfoMapPerImpression.get(impressionId);
        if(null == adExchangeInfo)
            return false;

        Boolean strictBannerSize = adExchangeInfo.getStrictBannerSize();

        if(null == strictBannerSize)
            return false;

        return strictBannerSize.booleanValue();
    }

    public void setRequiredSizeArrayForImpressionIdOfRTBExchange(String impressionId,int[] widths,int[] heights)
    {
        AdExchangeInfo adExchangeInfo = this.adExchangeInfoMapPerImpression.get(impressionId);

        if(null == adExchangeInfo)
            adExchangeInfo = new AdExchangeInfo();

        adExchangeInfo.setWidths(widths);
        adExchangeInfo.setHeights(heights);

        this.adExchangeInfoMapPerImpression.put(impressionId,adExchangeInfo);
    }

    public int[] fetchRequiredWidthArrayForImpressionId(String impressionId)
    {
        if(null == impressionId)
            return null;

        AdExchangeInfo adExchangeInfo = this.adExchangeInfoMapPerImpression.get(impressionId);

        if(null == adExchangeInfo)
            return null;

        return  adExchangeInfo.getWidths();
    }

    public int[] fetchRequiredHeightArrayForImpressionId(String impressionId)
    {
        if(null == impressionId)
            return null;

        AdExchangeInfo adExchangeInfo = this.adExchangeInfoMapPerImpression.get(impressionId);

        if(null == adExchangeInfo)
            return null;

        return  adExchangeInfo.getHeights();
    }

    public void setInterstitalMinimumSizeArrayForImpressionIdOfRTBExchange(String impressionId,int[] widths,int[] heights)
    {
        AdExchangeInfo adExchangeInfo = this.adExchangeInfoMapPerImpression.get(impressionId);

        if(null == adExchangeInfo)
            adExchangeInfo = new AdExchangeInfo();

        adExchangeInfo.setInterstitalMinimumWidths(widths);
        adExchangeInfo.setInterstitialMinimumheights(heights);

        this.adExchangeInfoMapPerImpression.put(impressionId,adExchangeInfo);
    }

    public void addToAdExchangeInfoPrivateDealInfo(String impressionId,AdExchangeInfo.PrivateDealInfo privateDealInfo)
    {
        AdExchangeInfo adExchangeInfo = this.adExchangeInfoMapPerImpression.get(impressionId);

        if(null == adExchangeInfo)
            adExchangeInfo = new AdExchangeInfo();

        adExchangeInfo.addPrivateDealInfo(privateDealInfo);
        this.adExchangeInfoMapPerImpression.put(impressionId,adExchangeInfo);
    }
    
    public Set<AdExchangeInfo.PrivateDealInfo> fetchPrivateDealInfoSetForImpressionId(String impressionId)
    {
        AdExchangeInfo adExchangeInfo = this.adExchangeInfoMapPerImpression.get(impressionId);

        if(null != adExchangeInfo)
            return adExchangeInfo.getPrivateDealInfoSet();

        return null;
    }

    public List<String> fetchDealIdSetFromBidRequest()
    {
        List<String> dealIdSet = new ArrayList<String>();

        if(null != adExchangeInfoMapPerImpression && adExchangeInfoMapPerImpression.size() > 0)
        {
            for(AdExchangeInfo adExchangeInfo : adExchangeInfoMapPerImpression.values())
            {
                Set<AdExchangeInfo.PrivateDealInfo> privateDealInfoSet = adExchangeInfo.getPrivateDealInfoSet();
                if(null == privateDealInfoSet || privateDealInfoSet.size() <= 0)
                    return dealIdSet;
                for(AdExchangeInfo.PrivateDealInfo privateDealInfo : privateDealInfoSet)
                {
                    dealIdSet.add(privateDealInfo.getDealId());
                }
            }
        }

        return dealIdSet;
    }

    public List<String> fetchDealIdWithFloorPriceSetFromBidRequest()
    {
        List<String> dealIdSet = new ArrayList<String>();

        if(null != adExchangeInfoMapPerImpression && adExchangeInfoMapPerImpression.size() > 0)
        {
            for(AdExchangeInfo adExchangeInfo : adExchangeInfoMapPerImpression.values())
            {
                Set<AdExchangeInfo.PrivateDealInfo> privateDealInfoSet = adExchangeInfo.getPrivateDealInfoSet();
                if(null == privateDealInfoSet || privateDealInfoSet.size() <= 0)
                    return dealIdSet;

                for(AdExchangeInfo.PrivateDealInfo privateDealInfo : privateDealInfoSet)
                {
                    StringBuffer sb = new StringBuffer(privateDealInfo.getDealId());

                    if(null != privateDealInfo.getBidFloor())
                    {
                        sb.append(DEALID_FLOOR_DELIM);
                        sb.append(privateDealInfo.getBidFloor());
                    }

                    dealIdSet.add(sb.toString());
                }
            }
        }

        return dealIdSet;
    }

    public int[] fetchMinimumInterstitialWidthArrayForImpressionId(String impressionId)
    {
        if(null == impressionId)
            return null;

        AdExchangeInfo adExchangeInfo = this.adExchangeInfoMapPerImpression.get(impressionId);

        if(null == adExchangeInfo)
            return null;

        return  adExchangeInfo.getInterstitalMinimumWidths();
    }

    public int[] fetchMinimumInterstitialHeightArrayForImpressionId(String impressionId)
    {
        if(null == impressionId)
            return null;

        AdExchangeInfo adExchangeInfo = this.adExchangeInfoMapPerImpression.get(impressionId);

        if(null == adExchangeInfo)
            return null;

        return  adExchangeInfo.getInterstitialMinimumheights();
    }

    public void setRequestAsDebugSystemForSupplyDemandMatching(boolean debugSystemForSupplyDemandMatching)
    {
        this.isRequestForSystemDebugging = debugSystemForSupplyDemandMatching;
    }

    public void addDebugMessageForTestRequest(String debugMessage)
    {
        this.debugRequestBuffer.append(debugMessage);
        this.debugRequestBuffer.append(NEW_LINE);
    }

    public void setRequestEnrichmentErrorCodeAndMessage(REQUEST_ENRICHMENT_ERROR_CODE code, String detail){
    	this.requestEnrichmentErrorCode=code;
    	this.requestEnrichmentErrorDetail=detail;
    }

    public void setSecureRequiredForImpressionIdOfRTBExchange(String impressionId,Boolean secureRequired)
    {
        AdExchangeInfo adExchangeInfo = this.adExchangeInfoMapPerImpression.get(impressionId);

        if(null == adExchangeInfo)
            adExchangeInfo = new AdExchangeInfo();

        adExchangeInfo.setRequiresSecureAssets(secureRequired);

        this.adExchangeInfoMapPerImpression.put(impressionId,adExchangeInfo);
    }

    public boolean fetchSecureRequiredForImpressionIdOfRTBExchange(String impressionId)
    {
        if(null == impressionId)
            return false;

        AdExchangeInfo adExchangeInfo = this.adExchangeInfoMapPerImpression.get(impressionId);
        if(null == adExchangeInfo)
            return false;

        Boolean secureRequired = adExchangeInfo.getRequiresSecureAssets();

        if(null == secureRequired)
            return false;

        return secureRequired.booleanValue();
    }

    public void addBidPriceOfferedByDSPForExchangeRequest(String dspGuid,Double bidPriceOfferedByDsp)
    {
        if(null == this.dspBidPriceResponseForExchangeRequest)
            this.dspBidPriceResponseForExchangeRequest = new HashMap<String, Double>();

        this.dspBidPriceResponseForExchangeRequest.put(dspGuid,bidPriceOfferedByDsp);
    }

    public Double fetchBidPriceOfferedByDSP(String dspGuid)
    {
        if(null == this.dspBidPriceResponseForExchangeRequest || null == dspGuid)
            return null;

        return this.dspBidPriceResponseForExchangeRequest.get(dspGuid);
    }

    /**
     * *******************Below fields would capture more information about this request, that might be****************
     * *******************extracted using primary parameters directly or via database lookups.*************************
     * *******************Can be then used in Ad-exchange(SSP) workflow where enrichment of bid request****************
     * *******************with more parameters is required or may be used by DSP workflow for better*******************
     * *******************targeting or decision making on bid values.**************************************************
     */
    @Getter @Setter
    private String bidRequestNativeVersion;

    @Getter @Setter
    private Integer bidRequestNativeRequestContext;

    @Getter @Setter
    private Integer bidRequestNativeRequestAdUnit;

    @Getter @Setter
    private Integer bidRequestNativeRequestContextsubtype;

    @Getter @Setter
    private Integer bidRequestNativeRequestPlcmttype;

    @Getter @Setter
    private String[] bidRequestSiteSectioncat;

    /**CSV of requesting content's keywords*/
    @Getter @Setter
    private String bidRequestSiteKeywords;

    @Getter @Setter
    private String bidRequestSiteContentId;

    @Getter @Setter
    private String bidRequestSiteContentKeywords;

    @Getter @Setter
    private String bidRequestSiteContentContext;

    @Getter @Setter
    private String bidRequestSiteContentLanguage;

    @Getter @Setter
    private String bidRequestDeviceCarrier;

    @Getter @Setter
    private String bidRequestDeviceLanguage;

    @Getter @Setter
    private Integer bidRequestDeviceGeoLocationType;

    @Getter @Setter
    private boolean bidRequestUseHMinWMinNativeRequest;

    @Getter @Setter
    private Integer bidRequestUserYOB;

    @Getter @Setter
    private String bidRequestUserGender;

    @Getter @Setter
    private String bidRequestUserKeywords;

    @Getter @Setter
    private String bidRequestUserCountry;
    /*******************Extra parameters might be required for bid request formation ends here*************************/
    /******************************************************************************************************************/
}
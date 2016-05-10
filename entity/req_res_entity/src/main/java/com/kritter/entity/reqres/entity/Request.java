package com.kritter.entity.reqres.entity;

import com.kritter.bidrequest.entity.IBidRequest;
import com.kritter.common.site.entity.Site;
import com.kritter.constants.ConnectionType;
import com.kritter.device.entity.HandsetMasterData;
import com.kritter.geo.common.entity.Country;
import com.kritter.geo.common.entity.InternetServiceProvider;
import com.kritter.entity.user.userid.ExternalUserId;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

import com.kritter.constants.INVENTORY_SOURCE;

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
    private NO_FILL_REASON noFillReason;
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
    private List<Short> requestedSlotIdList;
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

    //contains all request enrichment related error codes.
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

    public enum NO_FILL_REASON
    {
        FILL(1),
        BIDDER_FLOOR_UNMET(2),
        NO_ADS(3),
        ALPHA_EMPTY(4),
        AD_ABSENT_IN_ALPHA(5),
        BID_ZERO(6),
        PUB_FLOOR_ABSENT(7),
        CAMPAIGN_BID_FLOOR_UNMET(8),
        NO_ADS_COUNTRY_CARRIER(9),    /*No ads found for this country carrier combination*/
        NO_ADS_BRAND(10),             /*No ads found for this brand which also targets above country carrier combination*/
        NO_ADS_MODEL(11),             /*No ads found for this model which also targets above country carrier and brand combination*/
        SITE_INC_EXC_ADV(12),         /*Site excludes the advertisers*/
        AD_INV_SRC(13),               /*Ad targets specific inventory source, network or exchange*/
        AD_INV_SRC_TYPE(14),          /*Ad targets specific inventory source type , wap or app*/
        AD_SITE_CATEGORY_INC_EXC(15), /*Ad includes or excludes specific site's content category*/
        SITE_AD_CATEGORY_INC_EXC(16), /*Site includes or excludes specific ad's content categories*/
        AD_PUB_INC_EXC(17),           /*Ad includes or excludes specific publishers*/
        AD_SITE_INC_EXC(18),          /*Ad includes or excludes specific sites*/
        AD_SITE_HYGIENE(19),          /*Ad and site hygiene do not match up*/
        AD_OS_MIDP(20),               /*Ad targets some OS and its version or some midp version, not found in this request*/
        AD_BROWSER(21),               /*Ad targets some browser and its version not found in this request*/
        SITE_AD_DOMAIN(22),           /*Site excludes certain ad domains */
        AD_CUSTOM_IP(23),             /*Ad targets custom ip addresses ,request is not from the ip address set.*/
        AD_HOUR_DAY(24),              /*Ad targets certain hours of day, request has bad timing.*/
        CREATIVE_ATTR(25),            /*Selected Ad's creatives could not match the requested attributes*/
        CREATIVE_SIZE(26),            /*Selected Ad's creatives could not match the requested size slot*/
        ECPM_FLOOR_UNMET(27),         /*Selected Ads could not meet the ecpm floor required by site (network) or impression floor(exchange)*/
        SITE_INC_EXC_CAMPAIGNS(28),   /*Site excludes specific campaigns*/
        CAMPAIGN_DATE_BUDGET(29),     /*Ad's Campaign is expired or out of budget*/
        BIDDER_BID_NEGATIVE_ZERO(30), /*The online bidder calculated bid as negative or zero for final selected ads.*/
        INTERNAL_ERROR(31),           /*There was an internal error in the workflow or exception due to which fill could not be delivered.*/
        AD_ZIPCODE(32),               /*If ad is zipcode targeted and requesting lat-long doesnot fall into that zipcode range*/
        AD_LAT_LONG(33),              /*If ad is lat-long targeted with some radius given in kms,*/
        AD_EXT_SUPPLY_ATTR(34),       /*If ad targets external supply attributes and exchange request does not qualify*/
        AD_DIRECT_SUPPLY_INC_EXC(35), /*If ad includes or excludes direct supply attributes, siteid,publisherid*/
        AD_EXCHNG_SUPPLY_INC_EXC(36), /*If ad includes or excludes supply attributes, siteid,publisherid,external attributes available from exchange etc.*/
        SITE_INC_EXC_ADV_CMPGN(37),   /*If site includes or excludes demand attributes, advertiser id or campaign id etc.*/
        NO_ADS_CONNECTION_TYPE(38),   /*No ads found targeting this connection type*/
        NO_ADS_TABLET_TARGETING(39),  /*Failed in tablet targeting matching*/
        AD_SUPPLY_INC_EXC(40),        /*If ad includes or excludes supply attributes, siteid,publisherid,external attributes available.*/
        ONLY_MEDIATION_DP(41),        /*If supply specifies only mediation as demand preference and no ad is of demand type api.*/
        ONLY_DSP_DP(42),              /*If supply specifies only dsp as demand preference and no ad is of demand type dsp is found.*/
        EX_OD_REQ_CONVERT(43),          /* Only dsp demand when request cannot be converted to bid request object*/
        EX_OD_REQ_SER_NULL(44),         /* Only dsp demand when bid request object cannot be serialized*/
        EX_OD_URL_MAP_EMP(45),          /* Only dsp demand when dsp url mapping is null*/
        EX_OD_EXEC_NULL(46),            /* Only dsp demand when executor service is null*/
        EX_OD_API_INCORRECT(47),        /* Only dsp demand when api name is null*/
        EX_OD_RESP_EMPTY(48),           /* Only dsp demand when adv response is empty*/
        EX_OD_BID_RESP_EMPTY(49),       /* Only dsp demand when bid response converted from adv response is empty*/
        EX_OD_AP_INCORRECT(50),         /* Only dsp demand when auction price is incorrect*/
        EX_OD_WIN_NULL(51),             /* Only dsp demand when win is null*/
        EX_OD_EXCEPTION(52),            /* Only dsp demand when their is exception*/
        ONLY_DIRECT_DP(53),             /*If supply specifies only direct as demand preference and no ad is of demand type dsp is found.*/
        ONLY_DIRECTthenMed_DP(54),      /*If supply specifies only direct then mediation as demand preference and no ad is of demand type dsp is found.*/
        ONLY_DIRECTthenDSP_DP(55),      /*If supply specifies only direct then dsp as demand preference and no ad is of demand type dsp is found.*/
        NATIVE_MISMATCH(56),            /*Native supply and creative mismatch*/
        VIDEO_MISMATCH(57),             /*Video supply and creative mismatch*/
        Video_ApiFramework(58),         /*Video supply and ApiFramework mismatch*/
        Video_Mime(59),                 /*Video supply and Mime mismatch*/
        Video_Duration(60),             /*Video supply and Duration mismatch*/
        Video_Protocol(61),             /*Video supply and Protocol mismatch*/
        Video_StartDelay(62),           /*Video supply and StartDelay mismatch*/
        Video_Width(63),                /*Video supply and Width mismatch*/
        Video_Height(64),               /*Video supply and Height mismatch*/
        Video_Linearity(65),            /*Video supply and Linearity mismatch*/
        Video_MaxExtended(66),          /*Video supply and MaxExtended mismatch*/
        Video_BitRate(67),              /*Video supply and BitRate mismatch*/
        Video_Boxing(68),               /*Video supply and Boxing mismatch*/
        Video_PlayBack(69),             /*Video supply and Playback mismatch*/
        Video_Delivery(70),             /*Video supply and Delivery mismatch*/
        Video_CompanionType(71),        /*Video supply and CompanionType mismatch*/
        Video_Exception(72);            /*Video supply and Handled Exception*/

        @Getter
        private int code;

        private NO_FILL_REASON(int code)
        {
            this.code = code;
        }
    }


    private static final String NEW_LINE = "\n";

    public Request(String requestId,INVENTORY_SOURCE inventorySource) throws Exception{

        if(null==requestId || null==inventorySource)
            throw new Exception("RequestId or InventorySource is null.");

        this.requestId = requestId;
        this.inventorySource = inventorySource.getCode();
        this.debugRequestBuffer = new StringBuffer();
        this.adExchangeInfoMapPerImpression = new HashMap<String, AdExchangeInfo>();
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
}

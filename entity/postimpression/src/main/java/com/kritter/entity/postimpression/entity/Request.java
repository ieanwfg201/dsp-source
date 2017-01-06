package com.kritter.entity.postimpression.entity;

import com.kritter.common.site.entity.Site;
import com.kritter.constants.ONLINE_FRAUD_REASON;
import com.kritter.constants.POSTIMPRESSION_EVENT_URL_PREFIX;
import com.kritter.serving.demand.entity.AdEntity;
import com.kritter.constants.BEventType;
import com.kritter.constants.ConnectionType;
import com.kritter.constants.NoFraudPostImpEvents;
import com.kritter.constants.UserConstant;
import com.kritter.device.common.entity.HandsetMasterData;
import com.kritter.utils.common.url.URLField;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;


/**
 * This class consists of postimpression request related data.
 * Some fields may be populated in different cases, some may
 * be missing depending upon the event type of the requesting
 * url.
 */
public class Request
{
    /**
     * click event type and requestId.
     */
    @Getter
    private POSTIMPRESSION_EVENT_URL_PREFIX postImpressionEvent;
    @Getter
    private String requestId;

    @Getter @Setter
    private ONLINE_FRAUD_REASON onlineFraudReason;

    /**
     * request meta data map for data present in request parameters of the url.
     */
    private Map<String,Object> requestMetaData;

    /**
     * Click,CSC,Win notification related data.
     */
    @Getter @Setter
    private Integer urlVersion;

    @Getter @Setter
    private Integer inventorySource;

    @Getter @Setter
    private String adservingRequestId;

    @Getter @Setter
    private long eventTime;

    @Getter @Setter
    private String impressionId;

    @Getter @Setter
    private Integer adId;

    @Getter @Setter
    private Integer siteId;

    @Getter @Setter
    private Integer countryId;

    @Getter @Setter
    private Integer countryCarrierId;

    @Getter @Setter
    private ConnectionType connectionType;

    @Getter @Setter
    private Long deviceId;

    @Getter @Setter
    private Integer deviceManufacturerId;

    @Getter @Setter
    private Integer deviceModelId;

    @Getter @Setter
    private Integer deviceOsId;

    @Getter @Setter
    private Integer browserId;

    @Getter @Setter
    private Short deviceTypeId;

    @Getter @Setter
    private Short slotId;

    @Getter @Setter
    private Double internalMaxBid;

    @Getter @Setter
    private Double advertiserBid;

    @Getter @Setter
    private Short bidderModelId;

    @Getter @Setter
    private Short selectedSiteCategoryId;

    @Getter @Setter
    private Short supplySourceTypeCode;

    @Getter @Setter
    private String urlHash;

    /**
     * bid price that we sent to exchange and at what price we won.
     */
    @Getter @Setter
    private Double bidPriceToExchange;

    @Getter @Setter
    private String conversionDataForClickEvent;
    @Getter @Setter
    private String conversionDataFromConversionEvent;
    @Getter @Setter
    private String conversionDataFromCookieBasedConversionEvent;
    @Getter @Setter
    private String userAgent;
    @Getter @Setter
    private String ipAddress;
    @Getter @Setter
    private String xForwardedFor;

    /**
     * Alias url related data.
     */
    @Getter @Setter
    private String aliasUrlId;

    /**
     * Tracking url from third party advertisers or publishers,
     * where affiliate business is concerned like yeahmobi.
     * Third party mostly will be an advertiser.
     */
    @Getter @Setter
    private String thirdPartyTrackingUrlEventId;

    @Getter @Setter
    private String thirdPartyId;

    @Getter @Setter
    private String thirdPartyUrlHash;

    /**
     * common detected objects, handset,countryid,ccid,country-region,etc.
     */
    @Getter @Setter
    private HandsetMasterData handsetMasterData;

    //ui country and carrier ids.
    @Getter @Setter
    private Integer uiCountryId;
    @Getter @Setter
    private Integer uiCountryCarrierId;

    @Getter @Setter
    private Site detectedSite;
    @Getter @Setter
    private AdEntity adEntity;
    @Getter @Setter
    private String clickRequestIdReceivedFromConversion;

    /*********************** win notification parameters **************************************/
    @Getter @Setter
    private String auctionId;
    @Getter @Setter
    private String auctionBidId;
    @Getter @Setter
    private String auctionImpressionId;
    @Getter @Setter
    private Double winBidPriceFromExchange;
    /*********************** win notification parameters end here *****************************/

    /************************For User targeting/retargeting/other data*************************/
    @Getter @Setter
    private String buyerUid;
    /******************************************************************************************/

    /***************************Internal id for the exchange's supply attributes***************/
    @Getter @Setter
    private Integer externalSupplyAttributesInternalId;
    /******************************************************************************************/
    /***************************************Retargeting Segment *******************************/
    @Getter @Setter
    private Integer retargetingSegment = UserConstant.retargeting_segment_default;
    /******************************************************************************************/
    /***************************************Tracking events************************************/
    @Getter @Setter
    private String ttype;
    @Getter @Setter
    private String tevent;
    /******************************************************************************************/
    /***************************************Billable Events************************************/
    @Getter @Setter
    private BEventType bEventType = null;
    @Getter @Setter
    private Double mbr;
    /******************************************************************************************/
    /***************************************NoFRDP Events************************************/
    @Getter @Setter
    private NoFraudPostImpEvents nfrdpType = null;
    /******************************************************************************************/

    /******************************************************************************************/
    @Getter @Setter
    private Map<Short,URLField> urlFieldsFromAdservingMap;
    /******************************************************************************************/

    public Request(
                   String requestId,
                   POSTIMPRESSION_EVENT_URL_PREFIX postImpressionEvent
                  ) throws Exception
    {
        if(null==requestId || null==postImpressionEvent)
            throw new Exception("RequestId or ClickEventType is null inside postimpression request object.");

        this.requestId = requestId;
        this.postImpressionEvent = postImpressionEvent;
        this.requestMetaData = new HashMap<String, Object>();
        this.urlFieldsFromAdservingMap = new HashMap<Short, URLField>();
    }

    public void setRequestMetaData(String key,Object value) throws Exception{

        if(null==key || null==value)
            throw new Exception("Key or value provided is null inside setRequestMetaData() of Postimpression Request.");

        this.requestMetaData.put(key, value);
    }

    public Object getMetaData(String key) throws Exception
    {
        if(null==key)
            throw new Exception("Key provided is null inside getRequestUrlData() of Postimpression Request ");

        return this.requestMetaData.get(key);
    }

    public Map<String,Object> copyMetaData()
    {
        Map<String,Object> data = new HashMap<String, Object>();

        if(null!=this.requestMetaData)
        {
            for(Map.Entry entry : this.requestMetaData.entrySet()){
                    data.put((String) entry.getKey(),entry.getValue());
            }
            return data;
        }

        return null;
    }
}

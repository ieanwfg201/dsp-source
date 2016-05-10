package com.kritter.postimpression.enricher_fraud;

import com.kritter.abstraction.cache.utils.exceptions.UnSupportedOperationException;
import com.kritter.common.site.cache.SiteCache;
import com.kritter.common.site.entity.Site;
import com.kritter.common.site.entity.SiteIncIdSecondaryKey;
import com.kritter.constants.ConnectionType;
import com.kritter.constants.StatusIdEnum;
import com.kritter.core.workflow.Context;
import com.kritter.core.workflow.Workflow;
import com.kritter.postimpression.cache.ConversionEventIdStorageCache;
import com.kritter.postimpression.enricher_fraud.checker.OnlineFraudUtils;
import com.kritter.postimpression.entity.Request;
import com.kritter.serving.demand.cache.AdEntityCache;
import com.kritter.serving.demand.entity.AdEntity;
import com.kritter.tracking.common.ThirdPartyTrackingManager;
import com.kritter.tracking.common.entity.ThirdPartyTrackingData;
import com.kritter.utils.uuid.mac.UUIDGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.Set;

/**
 * This class reads conversion info value and logs different available parameters.
 */
public class ConversionEnricherAndFraudProcessor implements OnlineEnricherAndFraudCheckGroupProcessor
{
    private Logger logger;
    private ThirdPartyTrackingManager thirdPartyTrackingManagerParentObject;
    private String postImpressionRequestObjectKey;
    private AdEntityCache adEntityCache;
    private SiteCache siteCache;
    private boolean isUUIDBasedOnMacAddress;
    private long timeInMillisForClickExpiry;
    private ConversionEventIdStorageCache eventIdStorageCache;

    public ConversionEnricherAndFraudProcessor(String loggerName,
                                               ThirdPartyTrackingManager thirdPartyTrackingManagerParentObject,
                                               String postImpressionRequestObjectKey,
                                               AdEntityCache adEntityCache,
                                               SiteCache siteCache,
                                               boolean isUUIDBasedOnMacAddress,
                                               long timeInMillisForClickExpiry,
                                               ConversionEventIdStorageCache eventIdStorageCache)
    {
        this.logger = LoggerFactory.getLogger(loggerName);
        this.thirdPartyTrackingManagerParentObject = thirdPartyTrackingManagerParentObject;
        this.postImpressionRequestObjectKey = postImpressionRequestObjectKey;
        this.adEntityCache = adEntityCache;
        this.siteCache = siteCache;
        this.isUUIDBasedOnMacAddress = isUUIDBasedOnMacAddress;
        this.timeInMillisForClickExpiry = timeInMillisForClickExpiry;
        this.eventIdStorageCache = eventIdStorageCache;
    }

    //This function reads conversion info value from request object
    //and the populates postImpressionRequest with all available
    //fields.
    @Override
    public OnlineFraudUtils.ONLINE_FRAUD_REASON performOnlineFraudChecks(Context context)
    {
        this.logger.debug("Inside performOnlineFraudChecks of ConversionEnricherAndFraudProcessor");
        HttpServletRequest httpServletRequest = (HttpServletRequest)context.getValue(Workflow.CONTEXT_REQUEST_KEY);

        Request postImpressionRequest = (Request)context.getValue(this.postImpressionRequestObjectKey);

        ThirdPartyTrackingData thirdPartyTrackingData = thirdPartyTrackingManagerParentObject.
                decipherConversionDataFromTrackingPartner(httpServletRequest,null);

        postImpressionRequest.setInternalMaxBid(thirdPartyTrackingData.getInternalBid());
        postImpressionRequest.setAdvertiserBid(thirdPartyTrackingData.getAdvertiserBid());
        postImpressionRequest.setAdId(thirdPartyTrackingData.getAdId());
        postImpressionRequest.setSiteId(thirdPartyTrackingData.getSiteId());
        postImpressionRequest.setInventorySource(thirdPartyTrackingData.getInventorySource());
        postImpressionRequest.setClickRequestIdReceivedFromConversion(thirdPartyTrackingData.getClickRequestId());
        postImpressionRequest.setUiCountryCarrierId(thirdPartyTrackingData.getCarrierId());
        postImpressionRequest.setUiCountryId(thirdPartyTrackingData.getCountryId());
        postImpressionRequest.setDeviceModelId(thirdPartyTrackingData.getDeviceModelId());
        postImpressionRequest.setDeviceManufacturerId(thirdPartyTrackingData.getDeviceManufacturerId());
        postImpressionRequest.setDeviceOsId(thirdPartyTrackingData.getDeviceOsId());
        postImpressionRequest.setBrowserId(thirdPartyTrackingData.getDeviceBrowserId());
        postImpressionRequest.setSelectedSiteCategoryId(thirdPartyTrackingData.getSelectedSiteCategoryId());
        postImpressionRequest.setBidderModelId(thirdPartyTrackingData.getBidderModelId());
        postImpressionRequest.setSupplySourceTypeCode(thirdPartyTrackingData.getSupplySourceType().shortValue());
        postImpressionRequest.setExternalSupplyAttributesInternalId(thirdPartyTrackingData.getExternalSupplyAttributesInternalId().intValue());
        postImpressionRequest.setConnectionType(ConnectionType.getEnum(thirdPartyTrackingData.getConnectionTypeId()));
        postImpressionRequest.setDeviceTypeId(thirdPartyTrackingData.getDeviceTypeId());
        //set event time when the request
        long timeOfClickEvent;

        if(isUUIDBasedOnMacAddress)
            timeOfClickEvent = UUIDGenerator.extractTimeInLong(postImpressionRequest.getClickRequestIdReceivedFromConversion());
        else
            timeOfClickEvent = com.kritter.utils.uuid.rand.UUIDGenerator.extractTimeInLong(postImpressionRequest.getClickRequestIdReceivedFromConversion());

        if((System.currentTimeMillis() - timeOfClickEvent) > timeInMillisForClickExpiry)
            return OnlineFraudUtils.ONLINE_FRAUD_REASON.CONVERSION_EXPIRED;

        //if conversion not expired , check for repetition otherwise add to storage.
        if(this.eventIdStorageCache.
                doesEventIdExistInStorage(postImpressionRequest.getClickRequestIdReceivedFromConversion()))
            return OnlineFraudUtils.ONLINE_FRAUD_REASON.EVENT_DUPLICATE;
        else
            this.eventIdStorageCache.
                    addEventIdToStorage(postImpressionRequest.getClickRequestIdReceivedFromConversion());

        postImpressionRequest.setEventTime(timeOfClickEvent);

        //find ad entity and set into request.
        AdEntity adEntity = this.adEntityCache.query(postImpressionRequest.getAdId());
        if(null != adEntity)
            postImpressionRequest.setAdEntity(adEntity);
        else
            return OnlineFraudUtils.ONLINE_FRAUD_REASON.AD_ID_NOT_FOUND;

        //find site entity and set into request.
        Site site = null;
        try
        {
            Set<String> siteGuidSet = siteCache.query(new SiteIncIdSecondaryKey(postImpressionRequest.getSiteId()));

            for(String siteGuid : siteGuidSet)
            {
                site = siteCache.query(siteGuid);
            }
        }
        catch (UnSupportedOperationException unsope)
        {
            return OnlineFraudUtils.ONLINE_FRAUD_REASON.SITE_ID_NOT_PRESENT_IN_CACHE;
        }

        if(null==site)
            return OnlineFraudUtils.ONLINE_FRAUD_REASON.SITE_ID_NOT_PRESENT_IN_CACHE;
        if(site.getStatus().intValue() != StatusIdEnum.Active.getCode())
            return OnlineFraudUtils.ONLINE_FRAUD_REASON.SITE_UNFIT;

        postImpressionRequest.setDetectedSite(site);

        return OnlineFraudUtils.ONLINE_FRAUD_REASON.HEALTHY_REQUEST;
    }
}

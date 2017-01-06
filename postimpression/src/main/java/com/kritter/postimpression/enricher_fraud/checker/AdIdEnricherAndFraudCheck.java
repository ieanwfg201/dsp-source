package com.kritter.postimpression.enricher_fraud.checker;

import com.kritter.core.workflow.Context;
import com.kritter.entity.postimpression.entity.Request;
import com.kritter.serving.demand.cache.AdEntityCache;
import com.kritter.serving.demand.entity.AdEntity;
import com.kritter.constants.ONLINE_FRAUD_REASON;
import com.kritter.utils.common.ApplicationGeneralUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * This class finds ad id(from request) in adrepository and
 * checks if adid of request is same as the found one.
 */
public class AdIdEnricherAndFraudCheck implements OnlineEnricherAndFraudCheck
{
    private String signature;
    private Logger logger;
    private String postImpressionRequestObjectKey;
    private AdEntityCache adEntityCache;

    public AdIdEnricherAndFraudCheck(
                                        String signature,
                                        String loggerName,
                                        String postImpressionRequestObjectKey,
                                        AdEntityCache adEntityCache
                                    )
    {
        this.signature = signature;
        this.logger = LogManager.getLogger(loggerName);
        this.postImpressionRequestObjectKey = postImpressionRequestObjectKey;
        this.adEntityCache = adEntityCache;
    }

    @Override
    public String getIdentifier()
    {
        return this.signature;
    }

    @Override
    public ONLINE_FRAUD_REASON fetchFraudReason(Context context, boolean applyFraudCheck)
    {
        Request request = (Request)context.getValue(this.postImpressionRequestObjectKey);

        /*Check if the ad id is a dummy value from adserving application, in such a case use dummy ad.*/
        if(null != request.getAdId() && request.getAdId().intValue() == ApplicationGeneralUtils.DUMMY_AD_ID.intValue())
            return fetchOnlineFraudReasonForDummyAd(request,applyFraudCheck);

        logger.debug("Inside AdIdEnricherAndFraudCheck, adid from request: {}", request.getAdId());

        if(null==request.getAdId())
            return ONLINE_FRAUD_REASON.AD_ID_MISSING;

        AdEntity adEntity = adEntityCache.query(request.getAdId());

        if(null != adEntity)
            logger.debug("Ad found inside cache , all good till here...");

        request.setAdEntity(adEntity);

        if(applyFraudCheck)
        {
            if(null==adEntity)
            {
                logger.debug("Ad not found inside cache...the postimpression event is fraud...");

                return ONLINE_FRAUD_REASON.AD_ID_NOT_FOUND;
            }
        }

        return ONLINE_FRAUD_REASON.HEALTHY_REQUEST;
    }

    private ONLINE_FRAUD_REASON fetchOnlineFraudReasonForDummyAd(Request request, boolean applyFraudCheck)
    {
        logger.debug("Inside DummyAdIdEnricherAndFraudCheck, adid from request: {}", request.getAdId());

        if(null==request.getAdId())
            return ONLINE_FRAUD_REASON.AD_ID_MISSING;

        AdEntity adEntity = null;

        try
        {
            adEntity = prepareDummyAdEntity();
        }
        catch (Exception e)
        {
            logger.error("Exception in construction of dummy ad",e);
        }

        if(null != adEntity)
            logger.debug("Dummy Ad constructed, all good till here...");

        request.setAdEntity(adEntity);

        if(applyFraudCheck)
        {
            if(null==adEntity)
            {
                logger.debug("Dummy Ad not found...the postimpression event is fraud...");

                return ONLINE_FRAUD_REASON.AD_ID_NOT_FOUND;
            }
        }

        return ONLINE_FRAUD_REASON.HEALTHY_REQUEST;
    }

    private AdEntity prepareDummyAdEntity() throws Exception
    {
        AdEntity.AdEntityBuilder adEntityBuilder =
                new AdEntity.AdEntityBuilder(
                                                -1,"dummy_ad_guid",-1,"dummy_creative_guid",-1,
                                                "dummy_campaign_guid",null,null,null,null,null,
                                                null,false,null,false,-1,-1,-1,-1,null,-1,null,
                                                false,1
                                            );
        adEntityBuilder.setAccountId(-1);
        adEntityBuilder.setCpaGoal(0.000000000000001);
        AdEntity adEntity = new AdEntity(adEntityBuilder);
        return adEntity;
    }
}

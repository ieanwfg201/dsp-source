package com.kritter.postimpression.enricher_fraud.checker;

import com.kritter.core.workflow.Context;
import com.kritter.postimpression.entity.Request;
import com.kritter.serving.demand.cache.AdEntityCache;
import com.kritter.serving.demand.entity.AdEntity;
import com.kritter.postimpression.enricher_fraud.checker.OnlineFraudUtils.ONLINE_FRAUD_REASON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        this.logger = LoggerFactory.getLogger(loggerName);
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
}

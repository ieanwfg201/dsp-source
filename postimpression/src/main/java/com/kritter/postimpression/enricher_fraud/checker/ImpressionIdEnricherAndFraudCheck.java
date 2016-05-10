package com.kritter.postimpression.enricher_fraud.checker;

import com.kritter.core.workflow.Context;
import com.kritter.postimpression.cache.EventIdStorageCache;
import com.kritter.postimpression.entity.Request;
import com.kritter.postimpression.enricher_fraud.checker.OnlineFraudUtils.ONLINE_FRAUD_REASON;
import com.kritter.utils.uuid.mac.UUIDGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class detects whether the received post impression event has an expired
 * impression.
 */

public class ImpressionIdEnricherAndFraudCheck implements OnlineEnricherAndFraudCheck
{
    private String signature;
    private Logger logger;
    private String postImpressionRequestObjectKey;
	private long timeInMillisForImpressionExpiry;
    private boolean isUUIDBasedOnMacAddress;
    private EventIdStorageCache eventIdStorageCache;

	public ImpressionIdEnricherAndFraudCheck(String signature,
                                             String loggerName,
                                             String postImpressionRequestObjectKey,
                                             long timeInMillisForImpressionExpiry,
                                             boolean isUUIDBasedOnMacAddress,
                                             EventIdStorageCache eventIdStorageCache)
    {
		this.signature = signature;
        this.logger = LoggerFactory.getLogger(loggerName);
        this.postImpressionRequestObjectKey = postImpressionRequestObjectKey;
		this.timeInMillisForImpressionExpiry = timeInMillisForImpressionExpiry;
        this.isUUIDBasedOnMacAddress = isUUIDBasedOnMacAddress;
        this.eventIdStorageCache = eventIdStorageCache;
	}

	@Override
	public String getIdentifier()
    {
		return this.signature;
	}

	@Override
	public ONLINE_FRAUD_REASON fetchFraudReason(Context context,boolean applyFraudCheck)
    {
        Request request = (Request)context.getValue(this.postImpressionRequestObjectKey);

        if(null==request.getImpressionId())
            return ONLINE_FRAUD_REASON.IMPRESSION_ID_MISSING_FROM_REQUEST;

		long impressionServedTime = 0L;

        try
        {
            if(isUUIDBasedOnMacAddress)
                impressionServedTime = UUIDGenerator.extractTimeInLong(request.getAdservingRequestId());
            else
                impressionServedTime = com.kritter.utils.uuid.rand.UUIDGenerator.
                                                                   extractTimeInLong(request.getAdservingRequestId());
        }
        catch (RuntimeException rte)
        {
            logger.error("RuntimeException inside ImpressionIdEnricherAndFraudCheck ",rte);
            return ONLINE_FRAUD_REASON.IMPRESSION_ID_MISSING_FROM_REQUEST;
        }

        if(applyFraudCheck)
	    	if((System.currentTimeMillis() - impressionServedTime) > timeInMillisForImpressionExpiry)
                return ONLINE_FRAUD_REASON.IMPRESSION_EXPIRED;

        /********************************Check for duplication**********************************************/
        //check if impression id is duplicate or not.
        if(this.eventIdStorageCache.doesEventIdExistInStorage(request.getImpressionId()))
            return OnlineFraudUtils.ONLINE_FRAUD_REASON.EVENT_DUPLICATE;
        else
            this.eventIdStorageCache.addEventIdToStorage(request.getImpressionId());
        /***************************************************************************************************/

        return  ONLINE_FRAUD_REASON.HEALTHY_REQUEST;
	}
}
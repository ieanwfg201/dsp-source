package com.kritter.postimpression.enricher_fraud;

import com.kritter.core.workflow.Context;
import com.kritter.postimpression.enricher_fraud.checker.OnlineEnricherAndFraudCheck;
import com.kritter.postimpression.enricher_fraud.checker.OnlineFraudUtils;
import com.kritter.postimpression.enricher_fraud.checker.OnlineFraudUtils.ONLINE_FRAUD_REASON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.List;

/**
 * This class ensures if the win notification coming from rtb exchange's server
 * is valid or not.
 */
public class WinApiNotificationEnricherAndFraudProcessor implements OnlineEnricherAndFraudCheckGroupProcessor
{
    private Logger logger;
    private List<OnlineEnricherAndFraudCheck> onlineEnricherAndFraudChecks;

    public WinApiNotificationEnricherAndFraudProcessor(
                                                       String loggerName,
                                                       List<OnlineEnricherAndFraudCheck> onlineEnricherAndFraudChecks
                                                      )
    {
        this.logger = LoggerFactory.getLogger(loggerName);
        this.onlineEnricherAndFraudChecks = onlineEnricherAndFraudChecks;
    }

    @Override
    public OnlineFraudUtils.ONLINE_FRAUD_REASON performOnlineFraudChecks(Context context)
    {

        Iterator<OnlineEnricherAndFraudCheck> onlineEnricherAndFraudCheckIterator =
                this.onlineEnricherAndFraudChecks.iterator();

        //if some particular fraud check is not to be applied,check instanceof and put false there.
        ONLINE_FRAUD_REASON onlineFraudReason = null;

        while(onlineEnricherAndFraudCheckIterator.hasNext())
        {
            OnlineEnricherAndFraudCheck onlineEnricherAndFraudCheck = onlineEnricherAndFraudCheckIterator.next();

            logger.debug("Inside WinApiNotificationEnricherAndFraudProcessor, running enricher and fraud checker " +
                         "instance: {}", onlineEnricherAndFraudCheck.getIdentifier());

            ONLINE_FRAUD_REASON fraudReason = onlineEnricherAndFraudCheck.fetchFraudReason(context,true);

            if
            (
                !fraudReason.getFraudReasonValue().equals(ONLINE_FRAUD_REASON.HEALTHY_REQUEST.getFraudReasonValue()) &&
                null == onlineFraudReason
            )
            {
                onlineFraudReason = fraudReason;
            }
        }

        if(null != onlineFraudReason)
            return onlineFraudReason;

        return ONLINE_FRAUD_REASON.HEALTHY_REQUEST;
    }
}

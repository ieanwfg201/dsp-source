package com.kritter.postimpression.enricher_fraud;

import com.kritter.core.workflow.Context;
import com.kritter.postimpression.enricher_fraud.checker.OnlineEnricherAndFraudCheck;
import com.kritter.constants.ONLINE_FRAUD_REASON;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.Iterator;
import java.util.List;

/**
 * fraud checks for billable event
 */

public class BillableEventEnricherAndFraudProcessor implements OnlineEnricherAndFraudCheckGroupProcessor
{
    private Logger logger;
    private List<OnlineEnricherAndFraudCheck> onlineEnricherAndFraudChecks;

    public BillableEventEnricherAndFraudProcessor(String loggerName, List<OnlineEnricherAndFraudCheck> onlineEnricherAndFraudChecks)
    {
        this.logger = LogManager.getLogger(loggerName);
        this.onlineEnricherAndFraudChecks = onlineEnricherAndFraudChecks;
    }

    @Override
    public ONLINE_FRAUD_REASON performOnlineFraudChecks(Context context)
    {

        Iterator<OnlineEnricherAndFraudCheck> fraudCheckerClassIterator = this.onlineEnricherAndFraudChecks.iterator();

        //if some particular fraud check is not to be applied, check instanceof and put false there.
        ONLINE_FRAUD_REASON onlineFraudReason = null;

        while(fraudCheckerClassIterator.hasNext())
        {
            OnlineEnricherAndFraudCheck onlineEnricherAndFraudCheck = fraudCheckerClassIterator.next();

            logger.debug("Inside BillableEventEnricherAndFraudProcessor, running enricher and fraud checker instance: {}",
                         onlineEnricherAndFraudCheck.getIdentifier());

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

        //If healthy request, set cookie at the configured domain.

        return ONLINE_FRAUD_REASON.HEALTHY_REQUEST;
    }
}

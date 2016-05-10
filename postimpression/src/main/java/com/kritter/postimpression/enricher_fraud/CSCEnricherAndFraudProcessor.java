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
 * This class performs online fraud checks configured to validate
 * that the csc url is valid from a valid user and not simulated.
 * Fraud checks in order.
 * 1. URL hash fraud check.
 * 2. Site is fit or not.
 * 3. AdId found or not.
 * 4. Handset id matches or not.
 * 5. location id(country id,country-carrier id) matches or not.
 */

public class CSCEnricherAndFraudProcessor implements OnlineEnricherAndFraudCheckGroupProcessor
{
    private Logger logger;
    private List<OnlineEnricherAndFraudCheck> onlineEnricherAndFraudChecks;

    public CSCEnricherAndFraudProcessor(String loggerName, List<OnlineEnricherAndFraudCheck> onlineEnricherAndFraudChecks)
    {
        this.logger = LoggerFactory.getLogger(loggerName);
        this.onlineEnricherAndFraudChecks = onlineEnricherAndFraudChecks;
    }

    @Override
    public OnlineFraudUtils.ONLINE_FRAUD_REASON performOnlineFraudChecks(Context context)
    {

        Iterator<OnlineEnricherAndFraudCheck> fraudCheckerClassIterator = this.onlineEnricherAndFraudChecks.iterator();

        //if some particular fraud check is not to be applied, check instanceof and put false there.
        ONLINE_FRAUD_REASON onlineFraudReason = null;

        while(fraudCheckerClassIterator.hasNext())
        {
            OnlineEnricherAndFraudCheck onlineEnricherAndFraudCheck = fraudCheckerClassIterator.next();

            logger.debug("Inside CSCEnricherAndFraudProcessor, running enricher and fraud checker instance: {}",
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

package com.kritter.postimpression.enricher_fraud;

import com.kritter.constants.ONLINE_FRAUD_REASON;
import com.kritter.core.workflow.Context;

/**
 * This interface is to be implemented by any class grouping
 * various online fraud checks along with enriching the
 * post impression request object.
 *
 * Implementation may choose which [ enricher + fraud_checker]
 * to include/group and hence invoke , however for enriching
 * the request all implementations of OnlineEnricherAndFraudCheck.
 */

public interface OnlineEnricherAndFraudCheckGroupProcessor
{
    public ONLINE_FRAUD_REASON performOnlineFraudChecks(Context context);
}

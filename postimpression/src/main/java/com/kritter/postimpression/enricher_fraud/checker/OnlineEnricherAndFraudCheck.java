package com.kritter.postimpression.enricher_fraud.checker;

import com.kritter.core.workflow.Context;
import com.kritter.constants.ONLINE_FRAUD_REASON;

/**
 * This interface dictates methods to be implemented by any online fraud check.
 * The fraud check classes also enriches the request object and performs
 * fraud check if told to do so.
 * 
 */

public interface OnlineEnricherAndFraudCheck
{
	/**
	 * Returns an id or name for the fraud check.
	 * 
	 * @return online fraud class identifier
	 */
	public String getIdentifier();

	/**
	 * This method checks whether the post impression event is fraud, if yes
	 * then returns the reason , if not then no fraud reason.
	 * As an argument it takes input parameters map, on which it would perform
     * the fraud check.
	 * @return
	 */
	public ONLINE_FRAUD_REASON fetchFraudReason(Context context,boolean applyFraudCheck);

}

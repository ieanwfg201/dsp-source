package com.kritter.postimpression.enricher_fraud.checker;

import com.kritter.core.workflow.Context;
import com.kritter.postimpression.entity.Request;
import com.kritter.postimpression.enricher_fraud.checker.OnlineFraudUtils.ONLINE_FRAUD_REASON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is responsible to check whether the received url is tampered with
 * or not.
 * 
 */
public class BillableEventEnricherAndFraudCheck implements OnlineEnricherAndFraudCheck
{
	private String signature;
    private Logger logger;
    private String postImpressionRequestObjectKey;

	public BillableEventEnricherAndFraudCheck(String signature,String loggerName, 
	        String postImpressionRequestObjectKey){
        this.signature = signature;
        this.logger = LoggerFactory.getLogger(loggerName);
        this.postImpressionRequestObjectKey = postImpressionRequestObjectKey;
	}

	@Override
	public String getIdentifier(){
		return this.signature;
	}

	@Override
	public ONLINE_FRAUD_REASON fetchFraudReason(Context context,boolean applyFraudCheck)
    {
	    Request request = (Request)context.getValue(this.postImpressionRequestObjectKey);
	    if(request.getBEventType() == null || request.getMbr() == null){
	        return ONLINE_FRAUD_REASON.BILLABLE_EVENT;
	    }
	    return ONLINE_FRAUD_REASON.HEALTHY_REQUEST;
    }
}

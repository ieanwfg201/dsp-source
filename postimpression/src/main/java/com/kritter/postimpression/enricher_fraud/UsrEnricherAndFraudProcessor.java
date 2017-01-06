package com.kritter.postimpression.enricher_fraud;

import com.kritter.common.caches.retargeting_segment.RetargetingSegmentCache;
import com.kritter.common.caches.retargeting_segment.entity.RetargetingSegmentEntity;
import com.kritter.constants.ONLINE_FRAUD_REASON;
import com.kritter.constants.UserConstant;
import com.kritter.core.workflow.Context;
import com.kritter.entity.postimpression.entity.Request;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * This class reads user info value and logs different available parameters.
 */
public class UsrEnricherAndFraudProcessor implements OnlineEnricherAndFraudCheckGroupProcessor
{
    private Logger logger;
    private String postImpressionRequestObjectKey;
    private RetargetingSegmentCache retargetingSegmentCache;

    public UsrEnricherAndFraudProcessor(String loggerName,
                                               String postImpressionRequestObjectKey,
                                               RetargetingSegmentCache retargetingSegmentCache) {
        this.logger = LogManager.getLogger(loggerName);
        this.postImpressionRequestObjectKey = postImpressionRequestObjectKey;
        this.retargetingSegmentCache = retargetingSegmentCache;
    }

    //This function reads conversion info value from request object
    //and the populates postImpressionRequest with all available
    //fields.
    @Override
    public ONLINE_FRAUD_REASON performOnlineFraudChecks(Context context) {
        this.logger.debug("Inside performOnlineFraudChecks of UrlEnricherAndFraudProcessor, no fraud checks to be performed for Conversion_S2S_URL, marking Healthy Request");

        Request postImpressionRequest = (Request)context.getValue(this.postImpressionRequestObjectKey);
        if(UserConstant.retargeting_segment_default == postImpressionRequest.getRetargetingSegment()){
            return ONLINE_FRAUD_REASON.RETARGETING_SEGMENT_NF;
        }
        RetargetingSegmentEntity retargetingSegmentEntity = this.retargetingSegmentCache.query(postImpressionRequest.getRetargetingSegment());
        if(null == retargetingSegmentEntity){
            return ONLINE_FRAUD_REASON.RETARGETING_SEGMENT_NF;
        }

        return ONLINE_FRAUD_REASON.HEALTHY_REQUEST;
    }
}

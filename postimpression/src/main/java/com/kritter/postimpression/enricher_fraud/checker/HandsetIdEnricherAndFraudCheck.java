package com.kritter.postimpression.enricher_fraud.checker;

import com.kritter.core.workflow.Context;
import com.kritter.core.workflow.Workflow;
import com.kritter.device.common.HandsetDetectionProvider;
import com.kritter.device.common.entity.HandsetMasterData;
import com.kritter.entity.postimpression.entity.Request;
import com.kritter.constants.ONLINE_FRAUD_REASON;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import javax.servlet.http.HttpServletRequest;


//For fraud check logic in postimpression,check for deviceId first,
//if not equal then check for manufacturer and model to be equal.
//Reason being just in case new handset dataloading happens first
//in postimpression we will have a new deviceid detected for the
//same user agent however the manufacturer and model id would be
//the same, hence the postimpression can anytime in terms of new
//handset data loading, also in case of if adserving handset data
//loading takes place first then its not an issue as in that case
//also device id could be different(if postimpression has not
//loaded its handset data yet however the individual manufacturer
//and model ids would be same if handset detected at all in
//postimpression server.)
//In online system ,as we have right now,depending upon the
//frequency at which data loads takes place, we wait for two times
//that frequency after putting files in postimpression server(if
//postimpression server is different from adserving.)Otherwise
//in case of same server the synch issue couldbe very less or not
//at all.

public class HandsetIdEnricherAndFraudCheck implements OnlineEnricherAndFraudCheck
{
    private String signature;
    private Logger logger;
    private String postImpressionRequestObjectKey;
    private String userAgentHeaderName;
    private HandsetDetectionProvider handsetDetectionProvider;
    private String operaMiniUserAgentSubString;
    private String[] operaMiniUAHeaderNames;

    public HandsetIdEnricherAndFraudCheck(String signature,
                                          String loggerName,
                                          String postImpressionRequestObjectKey,
                                          String userAgentHeaderName,
                                          HandsetDetectionProvider handsetDetectionProvider,
                                          String operaMiniUserAgentSubString,
                                          String[] operaMiniUAHeaderNames)
    {
        this.signature = signature;
        this.logger = LogManager.getLogger(loggerName);
        this.postImpressionRequestObjectKey = postImpressionRequestObjectKey;
        this.userAgentHeaderName = userAgentHeaderName;
        this.handsetDetectionProvider = handsetDetectionProvider;
        this.operaMiniUserAgentSubString = operaMiniUserAgentSubString;
        this.operaMiniUAHeaderNames = operaMiniUAHeaderNames;
    }

    @Override
	public String getIdentifier()
    {
		return this.signature;
	}
	
	@Override
	public ONLINE_FRAUD_REASON fetchFraudReason(Context context,boolean applyFraudCheck)
    {
        HttpServletRequest httpServletRequest = (HttpServletRequest)context.getValue(Workflow.CONTEXT_REQUEST_KEY);
        Request request = (Request)context.getValue(this.postImpressionRequestObjectKey);
        String userAgent = findCorrectUserAgent(httpServletRequest);

        HandsetMasterData handsetMasterData = null;

        try
        {
            logger.debug("User-Agent received in postimpression server: {}", userAgent);

            if(null == userAgent)
            {
                logger.debug("Since user agent not found , the postimpression request is fraud...");
                return ONLINE_FRAUD_REASON.HANDSET_UNDETECTED;
            }

            handsetMasterData = this.handsetDetectionProvider.detectHandsetForUserAgent(userAgent);
        }
        catch (Exception e)
        {
            logger.error("Exception in handset detection inside HandsetIdEnricherAndFraudCheck",e);
        }

        logger.debug("Handset internal id detected in HandsetIdEnricherAndFraudCheck is: {}",
                      null == handsetMasterData ? - 1 : handsetMasterData.getInternalId());

        request.setHandsetMasterData(handsetMasterData);

        if(applyFraudCheck)
        {
            Long deviceId          = request.getDeviceId();
            Integer manufacturerId = request.getDeviceManufacturerId();
            Integer modelId        = request.getDeviceModelId();

            logger.debug("From request , Device id: {}, manufacturer id: {}, model id: {}",
                         deviceId, manufacturerId, modelId);

            if(null==deviceId)
                return ONLINE_FRAUD_REASON.HANDSET_ID_MISSING_FROM_REQUEST;

            /*If requesting device was null or default then just pass the fraud check.*/
            if(null != deviceId && deviceId == -1L)
                return ONLINE_FRAUD_REASON.HEALTHY_REQUEST;

            //If device id is equal or if both manufacturer id and model id are same
            //that means same device.
            else if(  null != handsetMasterData &&
                    !((!handsetMasterData.getInternalId().equals(-1L) && handsetMasterData.getInternalId().equals(deviceId))
                            ||
                      (
                       handsetMasterData.getManufacturerId().equals(manufacturerId) &&
                       handsetMasterData.getModelId().equals(modelId)
                      )
                     )
                   )
            {
                //set detected ids, as mismatch has happened.
                request.setDeviceId(handsetMasterData.getInternalId());
                request.setDeviceManufacturerId(handsetMasterData.getManufacturerId());
                request.setDeviceModelId(handsetMasterData.getModelId());
                request.setDeviceOsId(handsetMasterData.getDeviceOperatingSystemId());
                request.setBrowserId(handsetMasterData.getDeviceBrowserId());

                logger.error("HANDSET_ID_MISMATCH however relaxing this fraud check so as to allow more clicks into the system.");
                return ONLINE_FRAUD_REASON.HEALTHY_REQUEST;
            }

            else if(
                    null != handsetMasterData &&
                    handsetMasterData.isBot()
                   )
            {
                 return ONLINE_FRAUD_REASON.BOT_HANDSET;
            }
            else if(null == handsetMasterData)
            {
                return ONLINE_FRAUD_REASON.HANDSET_UNDETECTED;
            }
	    }

        return ONLINE_FRAUD_REASON.HEALTHY_REQUEST;
    }

    private String findCorrectUserAgent(HttpServletRequest httpServletRequest)
    {
        String userAgent = httpServletRequest.getHeader(this.userAgentHeaderName);
        if(null == userAgent)
            return null;

        // if opera mini is the browser use different header
        // names in sequence to find the correct user agent.
        if(userAgent.toLowerCase().contains(operaMiniUserAgentSubString))
        {
            logger.debug("The user agent is of opera mini, trying to find the correct user agent...");

            for(String operaMiniUAHeader : operaMiniUAHeaderNames)
            {
                userAgent = httpServletRequest.getHeader(operaMiniUAHeader);

                if(null != userAgent)
                    break;
            }
        }

        return userAgent;
    }
}
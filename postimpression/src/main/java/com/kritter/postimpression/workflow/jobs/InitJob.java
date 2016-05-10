package com.kritter.postimpression.workflow.jobs;

import com.kritter.core.workflow.Context;
import com.kritter.core.workflow.Job;
import com.kritter.core.workflow.Workflow;
import com.kritter.postimpression.entity.Request;
import com.kritter.postimpression.urlreader.PostImpressionEventUrlReader;
import com.kritter.postimpression.utils.PostImpressionUtils;
import com.kritter.utils.common.ApplicationGeneralUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

/**
 * This class represents initial job in post impression workflow.
 * This job checks whether the request uri starts with particular prefix
 * and sets in request context the value of the prefix.
 * If not found, then do not set anything.
 *
 * Also it removes application name from request uri,so that only parameters
 * to be used remain.
 */

public class InitJob implements Job{

    private String name;
    private Logger logger;
    private String uriKey;
    private String postImpressionRequestObjectKey;
    private PostImpressionUtils postImpressionUtils;


    public InitJob(String name,String loggerName,String uriKey,
                   String postImpressionRequestObjectKey,
                   PostImpressionUtils postImpressionUtils)
    {
        this.name = name;
        this.logger = LoggerFactory.getLogger(loggerName);
        this.uriKey = uriKey;
        this.postImpressionRequestObjectKey = postImpressionRequestObjectKey;
        this.postImpressionUtils = postImpressionUtils;
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    @Override
    public void execute(Context context)
    {
        logger.info("Inside execute() of InitJob");

        HttpServletRequest httpServletRequest = (HttpServletRequest)context.getValue(Workflow.CONTEXT_REQUEST_KEY);
        String requestURI = httpServletRequest.getRequestURI();

        PostImpressionUtils.logDebug(logger,"Request uri obtained inside InitJob is ",requestURI);

        context.setValue(this.uriKey,requestURI);

        ApplicationGeneralUtils.logDebug(logger,
                "Processed Request uri obtained and set to context inside InitJob is ", requestURI);

        Request request = null;

        try
        {
            /************************TRACKING_URL_FROM_THIRD_PARTY *******************************/
            if(requestURI.startsWith(PostImpressionEventUrlReader.POSTIMPRESSION_EVENT_URL_PREFIX.
                    TRACKING_URL_FROM_THIRD_PARTY.getUrlIdentifierPrefix()))
            {
                logger.debug("Inside InitJob, Event url is Third party Tracking url.");

                request = new Request(context.getUuid().toString(),PostImpressionEventUrlReader.
                        POSTIMPRESSION_EVENT_URL_PREFIX.TRACKING_URL_FROM_THIRD_PARTY);
            }
            /***********************************************************************************/

            /************************THIRD_PARTY_CLICK_ALIAS_URL *******************************/
            if(requestURI.startsWith(PostImpressionEventUrlReader.POSTIMPRESSION_EVENT_URL_PREFIX.
                    THIRD_PARTY_CLICK_ALIAS_URL.getUrlIdentifierPrefix()))
            {
                logger.debug("Inside InitJob, Event url is third party alias click url.");

                request = new Request(context.getUuid().toString(),PostImpressionEventUrlReader.
                        POSTIMPRESSION_EVENT_URL_PREFIX.THIRD_PARTY_CLICK_ALIAS_URL);
            }
            /************************************************************************************/

            /************************CLICK*******************************************************/
            if(requestURI.startsWith(PostImpressionEventUrlReader.POSTIMPRESSION_EVENT_URL_PREFIX.
                    CLICK.getUrlIdentifierPrefix()))
            {
                logger.debug("Inside InitJob, Event url is Inhouse click url.");

                request = new Request(context.getUuid().toString(),PostImpressionEventUrlReader.
                        POSTIMPRESSION_EVENT_URL_PREFIX.CLICK);
            }
            /************************MACRO_CLICK*******************************************************/
            if(requestURI.startsWith(PostImpressionEventUrlReader.POSTIMPRESSION_EVENT_URL_PREFIX.
                    MACRO_CLICK.getUrlIdentifierPrefix()))
            {
                logger.debug("Inside InitJob, Event url is Inhouse macro click url.");

                request = new Request(context.getUuid().toString(),PostImpressionEventUrlReader.
                        POSTIMPRESSION_EVENT_URL_PREFIX.MACRO_CLICK);
            }
            /***********************************************************************************/

            /************************CLICK*******************************************************/
            if(requestURI.startsWith(PostImpressionEventUrlReader.POSTIMPRESSION_EVENT_URL_PREFIX.
                    CONVERSION_FEEDBACK.getUrlIdentifierPrefix()))
            {
                logger.debug("Inside InitJob, Event url is Conversion S2S URL from third party!!!.");

                request = new Request(context.getUuid().toString(),PostImpressionEventUrlReader.
                        POSTIMPRESSION_EVENT_URL_PREFIX.CONVERSION_FEEDBACK);
            }
            /***********************************************************************************/

            /************************CSC********************************************************/
            if(requestURI.startsWith(PostImpressionEventUrlReader.POSTIMPRESSION_EVENT_URL_PREFIX.
                    CSC.getUrlIdentifierPrefix()))
            {
                logger.debug("Inside InitJob, Event url is Inhouse CSC url.");

                request = new Request(context.getUuid().toString(),PostImpressionEventUrlReader.
                        POSTIMPRESSION_EVENT_URL_PREFIX.CSC);
            }
            /***********************************************************************************/

            /************************WIN_NOTIFICATION*******************************************/
            if(requestURI.startsWith(PostImpressionEventUrlReader.POSTIMPRESSION_EVENT_URL_PREFIX.
                    WIN_NOTIFICATION.getUrlIdentifierPrefix()))
            {
                logger.debug("Inside InitJob, Event url is Win notification url.");

                request = new Request(context.getUuid().toString(),PostImpressionEventUrlReader.
                        POSTIMPRESSION_EVENT_URL_PREFIX.WIN_NOTIFICATION);
            }
            /***********************************************************************************/

            /************************WIN_API_NOTIFICATION*******************************************/
            if(requestURI.startsWith(PostImpressionEventUrlReader.POSTIMPRESSION_EVENT_URL_PREFIX.
                    WIN_API_NOTIFICATION.getUrlIdentifierPrefix()))
            {
                logger.debug("Inside InitJob, Event url is Win API notification url.");

                request = new Request(context.getUuid().toString(),PostImpressionEventUrlReader.
                        POSTIMPRESSION_EVENT_URL_PREFIX.WIN_API_NOTIFICATION);
            }
            /***********************************************************************************/
            /************************INT_EXCHANGE_WIN***********************************************/
            if(requestURI.startsWith(PostImpressionEventUrlReader.POSTIMPRESSION_EVENT_URL_PREFIX.
                    INT_EXCHANGE_WIN.getUrlIdentifierPrefix()))
            {
                logger.debug("Inside InitJob, Event url is exchange win url.");

                request = new Request(context.getUuid().toString(),PostImpressionEventUrlReader.
                        POSTIMPRESSION_EVENT_URL_PREFIX.INT_EXCHANGE_WIN);
            }
            /***********************************************************************************/

            /************************CLICK*******************************************************/
            if(requestURI.startsWith(PostImpressionEventUrlReader.POSTIMPRESSION_EVENT_URL_PREFIX.
                    COOKIE_BASED_CONV_JS.getUrlIdentifierPrefix()))
            {
                logger.debug("Inside InitJob, Event url is Cookie based Conversion js from advertiser.");

                request = new Request(context.getUuid().toString(),PostImpressionEventUrlReader.
                        POSTIMPRESSION_EVENT_URL_PREFIX.COOKIE_BASED_CONV_JS);
            }
            /***********************************************************************************/
        }
        catch (Exception e)
        {
            logger.error("Exception in preparing post impression request object" ,e);
            context.setValue(this.postImpressionUtils.getTerminationReasonKey(),
                    this.postImpressionUtils.getEventUrlNotSupportedMessage());
            context.setTerminated(true);

        }
        //finally set the request object to context.
        context.setValue(this.postImpressionRequestObjectKey,request);
    }
}

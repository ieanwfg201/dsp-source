package com.kritter.postimpression.workflow.jobs;

import com.kritter.core.workflow.Context;
import com.kritter.constants.POSTIMPRESSION_EVENT_URL_PREFIX;
import com.kritter.core.workflow.Job;
import com.kritter.core.workflow.Workflow;
import com.kritter.entity.postimpression.entity.Request;
import com.kritter.postimpression.urlreader.PostImpressionEventUrlReader;
import com.kritter.postimpression.utils.PostImpressionUtils;
import com.kritter.utils.common.ApplicationGeneralUtils;
import com.kritter.utils.common.url.URLField;
import com.kritter.utils.common.url.URLFieldFactory;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.Map;

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
    private String userSyncKey;

    public InitJob(String name,
                   String loggerName,
                   String uriKey,
                   String postImpressionRequestObjectKey,
                   PostImpressionUtils postImpressionUtils,
                   String userSyncKey)
    {
        this.name = name;
        this.logger = LogManager.getLogger(loggerName);
        this.uriKey = uriKey;
        this.postImpressionRequestObjectKey = postImpressionRequestObjectKey;
        this.postImpressionUtils = postImpressionUtils;
        this.userSyncKey = userSyncKey;
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
            if(requestURI.startsWith(POSTIMPRESSION_EVENT_URL_PREFIX.
                    TRACKING_URL_FROM_THIRD_PARTY.getUrlIdentifierPrefix()))
            {
                logger.debug("Inside InitJob, Event url is Third party Tracking url.");

                request = new Request(context.getUuid().toString(),
                        POSTIMPRESSION_EVENT_URL_PREFIX.TRACKING_URL_FROM_THIRD_PARTY);
            }
            /***********************************************************************************/

            /************************THIRD_PARTY_CLICK_ALIAS_URL *******************************/
            if(requestURI.startsWith(POSTIMPRESSION_EVENT_URL_PREFIX.
                    THIRD_PARTY_CLICK_ALIAS_URL.getUrlIdentifierPrefix()))
            {
                logger.debug("Inside InitJob, Event url is third party alias click url.");

                request = new Request(context.getUuid().toString(),
                        POSTIMPRESSION_EVENT_URL_PREFIX.THIRD_PARTY_CLICK_ALIAS_URL);
            }
            /************************************************************************************/

            /************************CLICK*******************************************************/
            if(requestURI.startsWith(POSTIMPRESSION_EVENT_URL_PREFIX.
                    CLICK.getUrlIdentifierPrefix()))
            {
                logger.debug("Inside InitJob, Event url is Inhouse click url.");

                request = new Request(context.getUuid().toString(),
                        POSTIMPRESSION_EVENT_URL_PREFIX.CLICK);
            }
            /************************MACRO_CLICK*******************************************************/
            if(requestURI.startsWith(POSTIMPRESSION_EVENT_URL_PREFIX.
                    MACRO_CLICK.getUrlIdentifierPrefix()))
            {
                logger.debug("Inside InitJob, Event url is Inhouse macro click url.");

                request = new Request(context.getUuid().toString(),
                        POSTIMPRESSION_EVENT_URL_PREFIX.MACRO_CLICK);
            }
            /***********************************************************************************/

            /************************CLICK*******************************************************/
            if(requestURI.startsWith(POSTIMPRESSION_EVENT_URL_PREFIX.
                    CONVERSION_FEEDBACK.getUrlIdentifierPrefix()))
            {
                logger.debug("Inside InitJob, Event url is Conversion S2S URL from third party!!!.");

                request = new Request(context.getUuid().toString(),
                        POSTIMPRESSION_EVENT_URL_PREFIX.CONVERSION_FEEDBACK);
            }
            /***********************************************************************************/

            /************************CSC********************************************************/
            if(requestURI.startsWith(POSTIMPRESSION_EVENT_URL_PREFIX.
                    CSC.getUrlIdentifierPrefix()))
            {
                logger.debug("Inside InitJob, Event url is Inhouse CSC url.");

                request = new Request(context.getUuid().toString(),
                        POSTIMPRESSION_EVENT_URL_PREFIX.CSC);
            }
            /***********************************************************************************/

            /************************WIN_NOTIFICATION*******************************************/
            if(requestURI.startsWith(POSTIMPRESSION_EVENT_URL_PREFIX.
                    WIN_NOTIFICATION.getUrlIdentifierPrefix()))
            {
                logger.debug("Inside InitJob, Event url is Win notification url.");

                request = new Request(context.getUuid().toString(),
                        POSTIMPRESSION_EVENT_URL_PREFIX.WIN_NOTIFICATION);
            }
            /***********************************************************************************/

            /************************WIN_API_NOTIFICATION*******************************************/
            if(requestURI.startsWith(POSTIMPRESSION_EVENT_URL_PREFIX.
                    WIN_API_NOTIFICATION.getUrlIdentifierPrefix()))
            {
                logger.debug("Inside InitJob, Event url is Win API notification url.");

                request = new Request(context.getUuid().toString(),
                        POSTIMPRESSION_EVENT_URL_PREFIX.WIN_API_NOTIFICATION);
            }
            /***********************************************************************************/
            /************************USR for Cookie Drop and Retargeting Segment*******************************************/
            if(requestURI.startsWith(POSTIMPRESSION_EVENT_URL_PREFIX.
                    USR.getUrlIdentifierPrefix()))
            {
                logger.debug("Inside InitJob, Event url is usr url for cookie drop and segment .");
                request = new Request(context.getUuid().toString(),
                        POSTIMPRESSION_EVENT_URL_PREFIX.USR);
            }
            /***********************************************************************************/

            /************************INT_EXCHANGE_WIN***********************************************/
            if(requestURI.startsWith(POSTIMPRESSION_EVENT_URL_PREFIX.
                    INT_EXCHANGE_WIN.getUrlIdentifierPrefix()))
            {
                logger.debug("Inside InitJob, Event url is exchange win url.");

                request = new Request(context.getUuid().toString(),
                        POSTIMPRESSION_EVENT_URL_PREFIX.INT_EXCHANGE_WIN);
            }
            /***********************************************************************************/

            /************************CLICK*******************************************************/
            if(requestURI.startsWith(POSTIMPRESSION_EVENT_URL_PREFIX.
                    COOKIE_BASED_CONV_JS.getUrlIdentifierPrefix()))
            {
                logger.debug("Inside InitJob, Event url is Cookie based Conversion js from advertiser.");

                request = new Request(context.getUuid().toString(),
                        POSTIMPRESSION_EVENT_URL_PREFIX.COOKIE_BASED_CONV_JS);
            }
            /***********************************************************************************/

            /************************Tracking event*********************************************/
            if(requestURI.startsWith(POSTIMPRESSION_EVENT_URL_PREFIX.
                    TEVENT.getUrlIdentifierPrefix()))
            {
                logger.debug("Inside InitJob, Event url is Tevent.");

                request = new Request(context.getUuid().toString(),
                        POSTIMPRESSION_EVENT_URL_PREFIX.TEVENT);
            }
            /***********************************************************************************/

            /************************BTracking event********************************************/
            if(requestURI.startsWith(POSTIMPRESSION_EVENT_URL_PREFIX.
                    BEVENT.getUrlIdentifierPrefix()))
            {
                logger.debug("Inside InitJob, Event url is Bevent.");

                request = new Request(context.getUuid().toString(),
                        POSTIMPRESSION_EVENT_URL_PREFIX.BEVENT);
            }
            /***********************************************************************************/
            /************************NOFRDP event********************************************/
            if(requestURI.startsWith(POSTIMPRESSION_EVENT_URL_PREFIX.
                    NOFRDP.getUrlIdentifierPrefix()))
            {
                logger.debug("Inside InitJob, Event url is NOFRDP.");

                request = new Request(context.getUuid().toString(),
                        POSTIMPRESSION_EVENT_URL_PREFIX.NOFRDP);
            }
            /***********************************************************************************/

            /************************USERSYNC event********************************************/
            if(requestURI.startsWith(POSTIMPRESSION_EVENT_URL_PREFIX.
                    USERSYNC.getUrlIdentifierPrefix()))
            {
                logger.debug("Inside InitJob, Event url is USERSYNC.");

                request = new Request(context.getUuid().toString(),
                        POSTIMPRESSION_EVENT_URL_PREFIX.USERSYNC);
                context.setValue(this.userSyncKey, true);
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

        /*Set to request object the information from adserving application.*/
        String adservingInformation =
                    httpServletRequest.getParameter(ApplicationGeneralUtils.ADSERVING_POSTIMPRESSION_INFO_PARAM_NAME);

        logger.debug("Adserving info received : {} ", adservingInformation);
        if(null != adservingInformation)
        {
            URLFieldFactory urlFieldFactory = new URLFieldFactory(adservingInformation);

            Map<Short,URLField> urlFieldFromAdservingMap = null;

            try
            {
                urlFieldFromAdservingMap = urlFieldFactory.decodeFields();
                if(null != urlFieldFromAdservingMap)
                {
                    logger.debug("URLField map set for adserving info");
                    request.setUrlFieldsFromAdservingMap(urlFieldFromAdservingMap);
                }
            }
            catch (UnsupportedEncodingException e)
            {
                logger.error("UnsupportedEncodingException inside InitJob ",e);
            }
        }
    }
}

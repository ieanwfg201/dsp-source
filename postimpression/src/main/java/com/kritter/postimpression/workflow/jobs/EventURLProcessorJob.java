package com.kritter.postimpression.workflow.jobs;

import com.kritter.constants.DeviceType;
import com.kritter.entity.user.recenthistory.RecentHistoryProvider;
import com.kritter.common.site.entity.Site;
import com.kritter.constants.ConnectionType;
import com.kritter.constants.INVENTORY_SOURCE;
import com.kritter.constants.tracking_partner.TrackingPartner;
import com.kritter.core.workflow.Context;
import com.kritter.core.workflow.Job;
import com.kritter.core.workflow.Workflow;
import com.kritter.postimpression.enricher_fraud.*;
import com.kritter.postimpression.entity.Request;
import com.kritter.postimpression.enricher_fraud.checker.OnlineFraudUtils.ONLINE_FRAUD_REASON;
import com.kritter.postimpression.urlreader.PostImpressionEventUrlReader;
import com.kritter.postimpression.urlreader.impl.*;
import com.kritter.postimpression.utils.PostImpressionUtils;
import com.kritter.serving.demand.entity.AdEntity;
import com.kritter.tracking.common.ThirdPartyTrackingManager;
import com.kritter.tracking.common.entity.ThirdPartyTrackingData;
import com.kritter.postimpression.utils.MacroUtils;
import com.kritter.user.thrift.struct.ImpressionEvent;
import com.kritter.utils.common.ApplicationGeneralUtils;
import com.kritter.utils.common.ConversionUrlData;
import com.kritter.utils.cookie_sync.common.CookieSyncManager;
import com.kritter.utils.uuid.mac.UUIDGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * This class processes different post impression events available such
 * as client side pixel, click, conversion and cookie based conversion
 * along with exchange related win notifications.
 *
 * This class also sets cookies wherever required.
 *
 **/

public class EventURLProcessorJob implements Job
{
    private String name;
    private Logger logger;
    private String uriKey;
    private String postImpressionRequestObjectKey;
    private ClickUrlReader clickUrlReader;
    private CSCUrlReader cscUrlReader;
    private ExchangeWinNoticeUrlReader exchangeWinNoticeUrlReader;
    private ConversionUrlReader conversionUrlReader;
    private PostImpressionUtils postImpressionUtils;
    private ClickEnricherAndFraudProcessor clickFraudProcessor;
    private CSCEnricherAndFraudProcessor cscFraudProcessor;
    private ConversionEnricherAndFraudProcessor conversionEnricherAndFraudProcessor;
    private WinNotificationEnricherAndFraudProcessor winNotificationEnricherAndFraudProcessor;
    private WinApiNotificationEnricherAndFraudProcessor winApiNotificationEnricherAndFraudProcessor;
    private Map<String,ThirdPartyTrackingManager> thirdPartyTrackingManagerMap;
    private String defaultLandingUrl;
    private String cookieKeyName;
    private int cookieExpireAgeInSeconds;
    private UUIDGenerator uuidGenerator;
    /*This map contains publisher id and their corresponding cookie sync implementation.*/
    private Map<String,CookieSyncManager> cookieSyncManagerMap;
    private String exchangeUserIdParameterName;
    private String userAgentHeaderName;
    private String xForwardedForHeaderName;
    private String remoteAddressHeaderName;
    //in case redirect url for clicks is present in the dsp click url.
    private static final String CLICK_URL_REDIRECT_PAGE_PARAMETER = "redirect";
    private InternalExcWinUrlReader internalExcWinNoticeUrlReader;
    private InternalExcWinEnricherAndFraudProcessor internalWinExcEnricherAndFraudProcessor;
    private CookieBasedConversionUrlReader cookieBasedConversionUrlReader;
    private CookieBasedConversionEnricherAndFraudProcessor cookieBasedConversionEnricherAndFraudProcessor;
    private String conversionCookieName;
    private int conversionCookieExpireAgeInSeconds;
    private MacroClickUrlReader macroClickUrlReader; 
    private MacroClickEnricherAndFraudProcessor macroClickFraudProcessor;
    private TrackingEventUrlReader trackingEventUrlReader;
    private TrackingEventEnricherAndFraudProcessor trackingEventFraudProcessor;
    private RecentHistoryProvider recentHistoryProvider;
    private BillableEventUrlReader billableEventUrlReader;
    private BillableEventEnricherAndFraudProcessor billableEventFraudProcessor;


    public EventURLProcessorJob(
            String name,
            String loggerName,
            String postImpressionRequestObjectKey,
            String uriKey,
            ClickUrlReader clickUrlReader,
            CSCUrlReader cscUrlReader,
            ExchangeWinNoticeUrlReader exchangeWinNoticeUrlReader,
            ConversionUrlReader conversionUrlReader,
            PostImpressionUtils postImpressionUtils,
            ClickEnricherAndFraudProcessor clickFraudProcessor,
            CSCEnricherAndFraudProcessor cscFraudProcessor,
            ConversionEnricherAndFraudProcessor conversionEnricherAndFraudProcessor,
            WinNotificationEnricherAndFraudProcessor winNotificationEnricherAndFraudProcessor,
            WinApiNotificationEnricherAndFraudProcessor winApiNotificationEnricherAndFraudProcessor,
            Map<String,ThirdPartyTrackingManager> thirdPartyTrackingManagerMap,
            String defaultLandingUrl,
            String cookieKeyName,
            int cookieExpireAgeInSeconds,
            Map<String,CookieSyncManager> cookieSyncManagerMap,
            String exchangeUserIdParameterName,
            String userAgentHeaderName,
            String xForwardedForHeaderName,
            String remoteAddressHeaderName,
            InternalExcWinUrlReader internalExcWinNoticeUrlReader,
            InternalExcWinEnricherAndFraudProcessor internalWinExcEnricherAndFraudProcessor,
            CookieBasedConversionUrlReader cookieBasedConversionUrlReader,
            CookieBasedConversionEnricherAndFraudProcessor cookieBasedConversionEnricherAndFraudProcessor,
            String conversionCookieName,
            int conversionCookieExpireAgeInSeconds,
            MacroClickUrlReader macroClickUrlReader,
            MacroClickEnricherAndFraudProcessor macroClickFraudProcessor,
            TrackingEventUrlReader trackingEventUrlReader,
            TrackingEventEnricherAndFraudProcessor trackingEventFraudProcessor,
            RecentHistoryProvider recentHistoryProvider,
            BillableEventUrlReader billableEventUrlReader,
            BillableEventEnricherAndFraudProcessor billableEventFraudProcessor
            )
    {
        this.name = name;
        this.logger = LoggerFactory.getLogger(loggerName);
        this.postImpressionRequestObjectKey = postImpressionRequestObjectKey;
        this.uriKey = uriKey;
        this.clickUrlReader = clickUrlReader;
        this.cscUrlReader = cscUrlReader;
        this.exchangeWinNoticeUrlReader = exchangeWinNoticeUrlReader;
        this.conversionUrlReader = conversionUrlReader;
        this.postImpressionUtils = postImpressionUtils;
        this.clickFraudProcessor = clickFraudProcessor;
        this.cscFraudProcessor = cscFraudProcessor;
        this.conversionEnricherAndFraudProcessor = conversionEnricherAndFraudProcessor;
        this.winNotificationEnricherAndFraudProcessor = winNotificationEnricherAndFraudProcessor;
        this.winApiNotificationEnricherAndFraudProcessor = winApiNotificationEnricherAndFraudProcessor;
        this.thirdPartyTrackingManagerMap = thirdPartyTrackingManagerMap;
        this.defaultLandingUrl = defaultLandingUrl;
        this.cookieKeyName = cookieKeyName;
        this.cookieExpireAgeInSeconds = cookieExpireAgeInSeconds;
        this.uuidGenerator = new UUIDGenerator();
        this.cookieSyncManagerMap = cookieSyncManagerMap;
        this.exchangeUserIdParameterName = exchangeUserIdParameterName;
        this.userAgentHeaderName = userAgentHeaderName;
        this.xForwardedForHeaderName = xForwardedForHeaderName;
        this.remoteAddressHeaderName = remoteAddressHeaderName;
        this.internalExcWinNoticeUrlReader = internalExcWinNoticeUrlReader;
        this.internalWinExcEnricherAndFraudProcessor = internalWinExcEnricherAndFraudProcessor;
        this.cookieBasedConversionUrlReader = cookieBasedConversionUrlReader;
        this.cookieBasedConversionEnricherAndFraudProcessor = cookieBasedConversionEnricherAndFraudProcessor;
        this.conversionCookieName = conversionCookieName;
        this.conversionCookieExpireAgeInSeconds = conversionCookieExpireAgeInSeconds;
        this.macroClickUrlReader = macroClickUrlReader;
        this.macroClickFraudProcessor = macroClickFraudProcessor;
        this.trackingEventUrlReader = trackingEventUrlReader;
        this.trackingEventFraudProcessor = trackingEventFraudProcessor;
        this.recentHistoryProvider = recentHistoryProvider;
        this.billableEventUrlReader = billableEventUrlReader;
        this.billableEventFraudProcessor = billableEventFraudProcessor;
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    @Override
    public void execute(Context context)
    {
        logger.info("Inside execute() of EventURLProcessorJob.");

        if(context.isTerminated()){
            logger.error("Error in PostImpression Workflow, request is terminated reason being : " +
                    context.getValue(this.postImpressionUtils.getTerminationReasonKey()));
            return;
        }

        HttpServletRequest httpServletRequest = (HttpServletRequest)context.getValue(Workflow.CONTEXT_REQUEST_KEY);
        HttpServletResponse httpServletResponse = (HttpServletResponse)context.getValue(Workflow.CONTEXT_RESPONSE_KEY);

        String requestURI = (String)context.getValue(this.uriKey);

        Request postImpressionRequest = (Request)context.getValue(this.postImpressionRequestObjectKey);

        String userAgent = httpServletRequest.getHeader(this.userAgentHeaderName);
        String ipAddress = httpServletRequest.getHeader(this.remoteAddressHeaderName);
        String xForwardedFor = httpServletRequest.getHeader(this.xForwardedForHeaderName);
        if(null != userAgent) {
            postImpressionRequest.setUserAgent(userAgent);
        }
        if(null != ipAddress) {
            postImpressionRequest.setIpAddress(ipAddress);
        }
        if(null != xForwardedFor){
            postImpressionRequest.setXForwardedFor(xForwardedFor);
        }

        PostImpressionUtils.logDebug(logger," PostImpressionURI-Identifier String = ",
                postImpressionRequest.getPostImpressionEvent().getUrlIdentifierPrefix());

        if(postImpressionRequest.getPostImpressionEvent().getUrlIdentifierPrefix().equals(
                PostImpressionEventUrlReader.POSTIMPRESSION_EVENT_URL_PREFIX.CLICK
                .getUrlIdentifierPrefix()))
        {
            String landingPageUrl = defaultLandingUrl;
            boolean isClickFraud = false;

            try
            {
                this.clickUrlReader.decipherPostImpressionUrl(postImpressionRequest, requestURI, context);

                ONLINE_FRAUD_REASON onlineFraudReason = this.clickFraudProcessor.performOnlineFraudChecks(context);

                postImpressionRequest.setOnlineFraudReason(onlineFraudReason);

                //if not healthy click request set click as fraud and send no conversion info attached.
                //anyways the user goes to default page.
                if(!onlineFraudReason.getFraudReasonValue().
                        equalsIgnoreCase(ONLINE_FRAUD_REASON.HEALTHY_REQUEST.getFraudReasonValue()))
                    isClickFraud = true;

                PostImpressionUtils.logDebug(this.logger,"The online fraud reason caught is " ,
                        onlineFraudReason.getFraudReasonValue());

                AdEntity adEntity = postImpressionRequest.getAdEntity();

                /****************in case landing page url is from click url*********************/
                boolean landingUrlFromClickUrl = false;
                landingPageUrl = fetchLandingUrlValueFromClickUrl(httpServletRequest);

                if(null != landingPageUrl)
                    landingUrlFromClickUrl = true;
                /*******************************************************************************/

                //even in case of a fraud, respond with actual landing url, might be testing scenario or
                //accidentally capturing it as fraud, no harm in redirecting to original url.
                //The click however is already attributed as fraud.
                if(null != adEntity && !landingUrlFromClickUrl)
                    landingPageUrl = adEntity.getLandingUrl();

                if(null == landingPageUrl)
                    landingPageUrl = defaultLandingUrl;

                this.logger.debug("Modifying landing url if the adEntity requires conversion information...");

                if(!isClickFraud && null != adEntity && !landingUrlFromClickUrl)
                {
                    /**
                     * replace macros in landing page
                     */
                    try
                    {
                        landingPageUrl = MacroUtils.replaceMacrosInLP(adEntity.getLpVelocityTemplate(), postImpressionRequest);

                        //check that url is wellformed and log the parameters present in the url for
                        //debugging purposes.
                        URL url = new URL(landingPageUrl);
                        postImpressionRequest.setConversionDataForClickEvent(url.getQuery());
                        logger.error("ModifiedLandingUrl after replacing macros : {}", landingPageUrl);
                    }
                    catch (RuntimeException re)
                    {
                        logger.error("Could not replace macros in landing page. {}", re.getMessage());
                    }
                    ThirdPartyTrackingData thirdPartyTrackingData = prepareThirdPartyTrackingData(postImpressionRequest);

                    if(null != adEntity.getTrackingPartnerId() &&
                            adEntity.getTrackingPartnerId().intValue() != TrackingPartner.NONE.getCode())
                    {
                        int trackingPartnerId = adEntity.getTrackingPartnerId();
                        logger.debug("AdEntity needs conversion data to be tracked with third party partner: {} ",
                                TrackingPartner.getEnum(trackingPartnerId).getName());

                        TrackingPartner trackingPartner = TrackingPartner.getEnum(trackingPartnerId);
                        String beanIdForTrackingManager = trackingPartner.getApplicationInstanceId();

                        logger.debug("Bean id to use for sending conversion information {} ", beanIdForTrackingManager);

                        ThirdPartyTrackingManager thirdPartyTrackingManager =
                                this.thirdPartyTrackingManagerMap.get(beanIdForTrackingManager);

                        if(null == thirdPartyTrackingManager)
                        {
                            logger.error("ThirdPartyTrackingManager not found for beanid: {} ,leaving landing url as it is..." , beanIdForTrackingManager);
                        }
                        else
                        {
                            landingPageUrl = thirdPartyTrackingManager.modifyLandingUrlWithConversionData
                                    (
                                            landingPageUrl,
                                            thirdPartyTrackingData
                                            );

                            //important info so keeping error mode, even in debug mode this will be logged in production.
                            logger.debug("Conversion info to be sent to thirdPartyTrackerName {} , " +
                                    "ModifiedLandingUrl {} ", trackingPartner.getName(), landingPageUrl);

                            //check that url is wellformed and log the parameters present in the url for
                            //debugging purposes.
                            URL url = new URL(landingPageUrl);
                            postImpressionRequest.setConversionDataForClickEvent(url.getQuery());
                        }
                    }

                }
            }
            catch(Exception e)
            {
                logger.error("Exception inside EventURLProcessorJob while processing inhouse click url",e);
            }

            try
            {
                /**
                 * Setting cookie irrespective of whether click is fraud or not (Changed on Apr 5-2016)
                 */
                try
                {
                    String cookieConversionInformation = prepareConversionInformationCookieValue(postImpressionRequest);
                    logger.debug("Persisting cookie value for conversion tracking as: {}", cookieConversionInformation);
                    setConversionCookieToEndUser(httpServletResponse, cookieConversionInformation);
                }
                catch (IOException ioe)
                {
                    logger.error("IOException inside EventURLProcessorJob while setting conversion cookie ",ioe);
                }

                logger.debug("The event is click, redirect user to adv landing url: {}", landingPageUrl);

                PostImpressionUtils.redirectUserToLandingPage(landingPageUrl,
                        (HttpServletResponse) context.getValue(Workflow.CONTEXT_RESPONSE_KEY));
            }
            catch (IOException e){

                logger.error("IOException inside EventURLProcessorJob ",e);
            }
        }else if(postImpressionRequest.getPostImpressionEvent().getUrlIdentifierPrefix().equals(
                PostImpressionEventUrlReader.POSTIMPRESSION_EVENT_URL_PREFIX.MACRO_CLICK
                .getUrlIdentifierPrefix()))
        {
            String landingPageUrl = defaultLandingUrl;
            boolean ismacroClickFraud = false;

            try
            {
                this.macroClickUrlReader.decipherPostImpressionUrl(postImpressionRequest, requestURI, context);

                ONLINE_FRAUD_REASON onlineFraudReason = this.macroClickFraudProcessor.performOnlineFraudChecks(context);

                postImpressionRequest.setOnlineFraudReason(onlineFraudReason);

                //if not healthy macro click request set macro click as fraud and send no conversion info attached.
                //anyways the user goes to default page.
                if(!onlineFraudReason.getFraudReasonValue().
                        equalsIgnoreCase(ONLINE_FRAUD_REASON.HEALTHY_REQUEST.getFraudReasonValue()))
                    ismacroClickFraud = true;

                PostImpressionUtils.logDebug(this.logger,"The online fraud reason caught is " ,
                        onlineFraudReason.getFraudReasonValue());

                AdEntity adEntity = postImpressionRequest.getAdEntity();

                /****************in case landing page url is from click url*********************/
                boolean landingUrlFromMacroClickUrl = false;
                landingPageUrl = fetchLandingUrlValueFromClickUrl(httpServletRequest);

                if(null != landingPageUrl)
                    landingUrlFromMacroClickUrl = true;
                /*******************************************************************************/

                //even in case of a fraud, respond with actual landing url, might be testing scenario or
                //accidentally capturing it as fraud, no harm in redirecting to original url.
                //The macro click however is already attributed as fraud.
                if(null != adEntity && !landingUrlFromMacroClickUrl)
                    landingPageUrl = adEntity.getLandingUrl();

                if(null == landingPageUrl)
                    landingPageUrl = defaultLandingUrl;

                this.logger.debug("Modifying landing url if the adEntity requires conversion information...");

                if(!ismacroClickFraud && null != adEntity && !landingUrlFromMacroClickUrl)
                {
                    /**
                     * replace macros in landing page
                     */
                    try
                    {
                        landingPageUrl = MacroUtils.replaceMacrosInLP(adEntity.getLpVelocityTemplate(), postImpressionRequest);

                        //check that url is wellformed and log the parameters present in the url for
                        //debugging purposes.
                        URL url = new URL(landingPageUrl);
                        postImpressionRequest.setConversionDataForClickEvent(url.getQuery());
                        logger.error("ModifiedLandingUrl after replacing macros : {}", landingPageUrl);
                    }
                    catch (RuntimeException re)
                    {
                        logger.error("Could not replace macros in landing page. {}", re.getMessage());
                    }
                    ThirdPartyTrackingData thirdPartyTrackingData = prepareThirdPartyTrackingData(postImpressionRequest);

                    if(null != adEntity.getTrackingPartnerId() &&
                            adEntity.getTrackingPartnerId().intValue() != TrackingPartner.NONE.getCode())
                    {
                        int trackingPartnerId = adEntity.getTrackingPartnerId();
                        logger.debug("AdEntity needs conversion data to be tracked with third party partner: {} ",
                                TrackingPartner.getEnum(trackingPartnerId).getName());

                        TrackingPartner trackingPartner = TrackingPartner.getEnum(trackingPartnerId);
                        String beanIdForTrackingManager = trackingPartner.getApplicationInstanceId();

                        logger.debug("Bean id to use for sending conversion information {} ", beanIdForTrackingManager);

                        ThirdPartyTrackingManager thirdPartyTrackingManager =
                                this.thirdPartyTrackingManagerMap.get(beanIdForTrackingManager);

                        if(null == thirdPartyTrackingManager)
                        {
                            logger.error("ThirdPartyTrackingManager not found for beanid: {} ,leaving landing url as it is..." , beanIdForTrackingManager);
                        }
                        else
                        {
                            landingPageUrl = thirdPartyTrackingManager.modifyLandingUrlWithConversionData
                                    (
                                            landingPageUrl,
                                            thirdPartyTrackingData
                                            );

                            //important info so keeping error mode, even in debug mode this will be logged in production.
                            logger.debug("Conversion info to be sent to thirdPartyTrackerName {} , " +
                                    "ModifiedLandingUrl {} ", trackingPartner.getName(), landingPageUrl);

                            //check that url is wellformed and log the parameters present in the url for
                            //debugging purposes.
                            URL url = new URL(landingPageUrl);
                            postImpressionRequest.setConversionDataForClickEvent(url.getQuery());
                        }
                    }

                }
            }
            catch(Exception e)
            {
                logger.error("Exception inside EventURLProcessorJob while processing inhouse macro click url",e);
            }

            try
            {
                /**
                 * Setting cookie irrespective of whether macro click is fraus or not (Changed on Apr 5-2016)
                 */
                try
                {
                    String cookieConversionInformation = prepareConversionInformationCookieValue(postImpressionRequest);
                    logger.debug("Persisting cookie value for conversion tracking as: {}", cookieConversionInformation);
                    setConversionCookieToEndUser(httpServletResponse, cookieConversionInformation);
                }
                catch (IOException ioe)
                {
                    logger.error("IOException inside EventURLProcessorJob while setting conversion cookie");
                }
                logger.debug("The event is macro click, redirect user to adv landing url: {}", landingPageUrl);

                PostImpressionUtils.redirectUserToLandingPage(landingPageUrl,
                        (HttpServletResponse) context.getValue(Workflow.CONTEXT_RESPONSE_KEY));
            }
            catch (IOException e){

                logger.error("IOException inside EventURLProcessorJob ",e);
            }
        }
        //in case of csc write one by one pixel gif image.
        else if(postImpressionRequest.getPostImpressionEvent().getUrlIdentifierPrefix().equals(
                PostImpressionEventUrlReader.POSTIMPRESSION_EVENT_URL_PREFIX.CSC
                .getUrlIdentifierPrefix()))
        {
            try
            {
                this.cscUrlReader.decipherPostImpressionUrl(postImpressionRequest, requestURI, context);

                ONLINE_FRAUD_REASON onlineFraudReason = this.cscFraudProcessor.performOnlineFraudChecks(context);

                postImpressionRequest.setOnlineFraudReason(onlineFraudReason);

                PostImpressionUtils.logDebug(this.logger,"The online fraud reason caught is " ,
                        onlineFraudReason.getFraudReasonValue());

                /*****************If no fraud, means impression shown successfully to end user***********************/
                //update impression history for user.
                String kritterUserId = httpServletRequest.getParameter(ApplicationGeneralUtils.KRITTER_USER_ID_PARAM_NAME);

                logger.debug("KritterUserId: {} in CSC url ", kritterUserId);

                if(null != kritterUserId && onlineFraudReason.getFraudReasonValue().
                                            equalsIgnoreCase(ONLINE_FRAUD_REASON.HEALTHY_REQUEST.getFraudReasonValue())
                        && null != recentHistoryProvider)
                {
                    SortedSet<ImpressionEvent> impressionEvents = new TreeSet<ImpressionEvent>();

                    ImpressionEvent impressionEvent = new ImpressionEvent();
                    impressionEvent.setAdId(postImpressionRequest.getAdId());
                    impressionEvent.setTimestamp(System.currentTimeMillis());
                    impressionEvents.add(impressionEvent);

                    recentHistoryProvider.updateRecentHistory(kritterUserId,impressionEvents);

                    logger.debug("RecentImpressionHistory updated for kritterUserId:{} for adId: {} ",
                                 kritterUserId,postImpressionRequest.getAdId());
                }

            }
            catch(Exception e)
            {
                logger.error("Exception inside EventURLProcessorJob while processing inhouse csc url",e);
            }

            try
            {
                logger.debug("Finding advertising cookie if set at the end user's browser...");

                String advertisingCookie = fetchAdvertisingCookieFromEndUser(httpServletRequest);
                boolean isAdvertisingCookieSetAtEndUser = false;

                if(null != advertisingCookie)
                {
                    logger.debug("Advertising cookie found as {} " , advertisingCookie);
                    postImpressionRequest.setBuyerUid(advertisingCookie);
                    isAdvertisingCookieSetAtEndUser = true;
                }
                //set the advertising cookie for the end user.
                else
                {
                    advertisingCookie = setAdvertisingCookieToEndUser(httpServletResponse);
                }

                logger.debug("Writing 1*1 pixel gif to response as event is CSC...");

                /*Till here the workflow has set the advertising cookie to the end user's browser
                 * if it was absent or just recorded the already present cookie . Now comes the workflow
                 * wherein if the requesting beacon came from an exchange user then we have to synchronize
                 * the cookie if match table does not have the entry , using exchange's methods */

                //check for inventory source and decide whether to cookie sync and write to user store
                //or to simply respond with 1by1 pixel.
                if(INVENTORY_SOURCE.RTB_EXCHANGE.getCode() == postImpressionRequest.getInventorySource().shortValue())
                {
                    logger.debug("The CSC beacon comes from an ad-exchange user.");

                    //fetch ad-exchange's cookie id from CSC URL.
                    String adExchangeUserIdValue = httpServletRequest.getParameter(this.exchangeUserIdParameterName);

                    logger.debug("AdExchangeUserId received from CSC url is : {} ", adExchangeUserIdValue);

                    Site site = postImpressionRequest.getDetectedSite();
                    String publisherId = (null != site) ? site.getPublisherId() : "-1" ;

                    logger.debug("Looking up cookie sync manager for publisher id: {} ", publisherId);

                    CookieSyncManager cookieSyncManager = null;

                    if(null != this.cookieSyncManagerMap)
                        cookieSyncManager = this.cookieSyncManagerMap.get(publisherId);

                    /*Lookup match table for mapping between exchange user id and internal user id.*/
                    String internalUserIdValue = null;
                    if(null != cookieSyncManager)
                        internalUserIdValue =
                        cookieSyncManager.fetchInternalUserIdForExchangeUserId(adExchangeUserIdValue);

                    /****************************************CASES for cookie synchronization ************************/
                    /*if cookie sync manager available for exchange, synchronize user ids*/
                    /*Assume ,if internal mapping not present , then match has not happened or has expired.*/
                    /*If advertising cookie is not set at the end user or it might have expired then also
                     * go for synchronization of the two user ids and hence make internal entry as well*/

                    if(
                            (null == internalUserIdValue      && null != cookieSyncManager)      ||
                            (!isAdvertisingCookieSetAtEndUser && null != cookieSyncManager)
                            )
                    {
                        logger.debug("The csc beacon comes from an ad-exchange end user," +
                                "CookieSyncManager found for publisher id :{} ",publisherId);

                        int expiryAgeInDays = ( cookieExpireAgeInSeconds / 86400 );

                        boolean syncResult = cookieSyncManager.
                                syncInternalUserIdWithExchangeUserId
                                (
                                        advertisingCookie,
                                        adExchangeUserIdValue,
                                        expiryAgeInDays,
                                        httpServletResponse
                                        );

                        logger.debug("Cookie synchronization result is: {} ", syncResult);

                        //if synch result is true, that means match has happened on remote side of ad-exchange.
                        if(syncResult)
                        {
                            logger.debug("Cookie Synchronization has completed, now making internal database " +
                                    "entry using exchangeUserId:{} ,and dspUserId:{} ",
                                    adExchangeUserIdValue,advertisingCookie);

                            boolean result = cookieSyncManager.
                                    updateMatchTableWithExchangeAndInternalUserId(adExchangeUserIdValue,
                                            advertisingCookie);

                            logger.debug("Internal updation in the match table has happened : {} ", result);
                        }
                    }
                    /**********************************************Cookie synch over*******************************/

                    //if cookie sync manager not available for exchange or if the match is already present then
                    //simply write 1by1 pixel image to the end user.
                    else
                    {
                        logger.debug("Cookie is already matched with exchange and internal database contains the " +
                                " user ids internal and external, or may be cookie synch manager not available" +
                                " for the publisher id: {} ", publisherId);
                        PostImpressionUtils.writeOneByOnePixelToResponse(
                                (HttpServletResponse)context.getValue(Workflow.CONTEXT_RESPONSE_KEY));
                    }
                }
                else
                {
                    PostImpressionUtils.writeOneByOnePixelToResponse(
                            (HttpServletResponse)context.getValue(Workflow.CONTEXT_RESPONSE_KEY));
                }
            }
            catch (IOException e){

                logger.error("IOException inside EventURLProcessorJob ",e);
            }
        }
        /*
         * CONVERSION FEEDBACK EVENT BASED ON S2S API CALL.
         **/
        else if(postImpressionRequest.getPostImpressionEvent().getUrlIdentifierPrefix().equals(
                PostImpressionEventUrlReader.POSTIMPRESSION_EVENT_URL_PREFIX.CONVERSION_FEEDBACK
                .getUrlIdentifierPrefix()))
        {
            try
            {
                this.conversionUrlReader.decipherPostImpressionUrl(postImpressionRequest, requestURI, context);

                logger.debug("ConversionInfoReceived From third Party tracker {}",
                        postImpressionRequest.getConversionDataFromConversionEvent());

                ONLINE_FRAUD_REASON onlineFraudReason = this.conversionEnricherAndFraudProcessor.performOnlineFraudChecks(context);

                postImpressionRequest.setOnlineFraudReason(onlineFraudReason);

                if(onlineFraudReason.getFraudReasonValue().equals(ONLINE_FRAUD_REASON.HEALTHY_REQUEST.getFraudReasonValue()))
                    PostImpressionUtils.writeOKAsResponseForConversionFeedback((HttpServletResponse) context.getValue(Workflow.CONTEXT_RESPONSE_KEY));
                else
                    PostImpressionUtils.writeFAILAsResponseForConversionFeedback((HttpServletResponse) context.getValue(Workflow.CONTEXT_RESPONSE_KEY));

                this.logger.debug("The online fraud reason caught is {} " ,onlineFraudReason.getFraudReasonValue());
            }
            catch(Exception e)
            {
                logger.error("Exception inside EventURLProcessorJob while processing conversion S2S url ",e);

                try
                {
                    PostImpressionUtils.writeFAILAsResponseForConversionFeedback((HttpServletResponse) context.getValue(Workflow.CONTEXT_RESPONSE_KEY));
                }
                catch (IOException ioe)
                {
                    logger.error("IOException inside EventURLProcessorJob while writing response to third party tracker ", ioe);
                }
            }
        }
        /*
         * CONVERSION FEEDBACK BASED ON COOKIE.
         *
         * */
        else if(postImpressionRequest.getPostImpressionEvent().getUrlIdentifierPrefix().equals(
                PostImpressionEventUrlReader.POSTIMPRESSION_EVENT_URL_PREFIX.COOKIE_BASED_CONV_JS
                .getUrlIdentifierPrefix()))
        {
            try
            {
                this.cookieBasedConversionUrlReader.decipherPostImpressionUrl(postImpressionRequest, requestURI, context);

                logger.debug("ConversionInfoReceived From conversion cookie via javascript on thank you page {}",
                        postImpressionRequest.getConversionDataFromCookieBasedConversionEvent());

                ONLINE_FRAUD_REASON onlineFraudReason = this.cookieBasedConversionEnricherAndFraudProcessor.performOnlineFraudChecks(context);

                postImpressionRequest.setOnlineFraudReason(onlineFraudReason);

                PostImpressionUtils.writeOneByOnePixelToResponse(
                        (HttpServletResponse)context.getValue(Workflow.CONTEXT_RESPONSE_KEY));

                this.logger.debug("The online fraud reason caught is {} " ,onlineFraudReason.getFraudReasonValue());
            }
            catch(Exception e)
            {
                logger.error("Exception inside EventURLProcessorJob while processing cookie based conversion url ",e);
            }
        }
        else if(postImpressionRequest.getPostImpressionEvent().getUrlIdentifierPrefix().equals(
                PostImpressionEventUrlReader.POSTIMPRESSION_EVENT_URL_PREFIX.WIN_NOTIFICATION
                .getUrlIdentifierPrefix()))
        {
            try
            {
                logger.debug("Event received is win notification...calling win notification url reader...");

                this.exchangeWinNoticeUrlReader.decipherPostImpressionUrl(postImpressionRequest, requestURI, context);

                logger.debug("Calling win notification enricher and fraud processor....");

                ONLINE_FRAUD_REASON onlineFraudReason = this.winNotificationEnricherAndFraudProcessor.performOnlineFraudChecks(context);

                this.logger.debug("The online fraud reason caught is {} " ,onlineFraudReason.getFraudReasonValue());

                postImpressionRequest.setOnlineFraudReason(onlineFraudReason);
            }
            catch(Exception e)
            {
                logger.error("Exception inside EventURLProcessorJob while processing win notification url",e);
            }
        }
        else if(postImpressionRequest.getPostImpressionEvent().getUrlIdentifierPrefix().equals(
                PostImpressionEventUrlReader.POSTIMPRESSION_EVENT_URL_PREFIX.WIN_API_NOTIFICATION
                .getUrlIdentifierPrefix()))
        {
            try
            {
                logger.debug("Event received is win api notification...calling win notification url reader...");

                this.exchangeWinNoticeUrlReader.decipherPostImpressionUrl(postImpressionRequest, requestURI, context);

                logger.debug("Calling win api notification enricher and fraud processor....");

                ONLINE_FRAUD_REASON onlineFraudReason = this.winApiNotificationEnricherAndFraudProcessor.performOnlineFraudChecks(context);

                this.logger.debug("The online fraud reason caught is {} " ,onlineFraudReason.getFraudReasonValue());

                postImpressionRequest.setOnlineFraudReason(onlineFraudReason);
            }
            catch(Exception e)
            {
                logger.error("Exception inside EventURLProcessorJob while processing win api notification url",e);
            }
        }
        else if(postImpressionRequest.getPostImpressionEvent().getUrlIdentifierPrefix().equals(
                PostImpressionEventUrlReader.POSTIMPRESSION_EVENT_URL_PREFIX.INT_EXCHANGE_WIN
                .getUrlIdentifierPrefix()))
        {
            try
            {
                logger.debug("Event received is internal exc win ..calling internal exc win url reader...");

                this.internalExcWinNoticeUrlReader.decipherPostImpressionUrl(postImpressionRequest, requestURI, context);

                logger.debug("Calling internal exc win enricher and fraud processor....");

                ONLINE_FRAUD_REASON onlineFraudReason = this.internalWinExcEnricherAndFraudProcessor.performOnlineFraudChecks(context);

                this.logger.debug("The online fraud reason caught is {} " ,onlineFraudReason.getFraudReasonValue());

                postImpressionRequest.setOnlineFraudReason(onlineFraudReason);
            }
            catch(Exception e)
            {
                logger.error("Exception inside EventURLProcessorJob while processing win notification url",e);
            }
        }
        else if(postImpressionRequest.getPostImpressionEvent().getUrlIdentifierPrefix().equals(
                PostImpressionEventUrlReader.POSTIMPRESSION_EVENT_URL_PREFIX.TEVENT
                .getUrlIdentifierPrefix()))
        {
            try
            {
                logger.debug("Event received is tracking event ..calling tracking event url reader...");

                this.trackingEventUrlReader.decipherPostImpressionUrl(postImpressionRequest, requestURI, context);

                logger.debug("Calling tracking event enricher and fraud processor....");

                ONLINE_FRAUD_REASON onlineFraudReason = this.trackingEventFraudProcessor.performOnlineFraudChecks(context);

                this.logger.debug("The online fraud reason caught is {} " ,onlineFraudReason.getFraudReasonValue());

                postImpressionRequest.setOnlineFraudReason(onlineFraudReason);
                PostImpressionUtils.writeOneByOnePixelToResponse(
                        (HttpServletResponse)context.getValue(Workflow.CONTEXT_RESPONSE_KEY));

            }
            catch(Exception e)
            {
                logger.error("Exception inside EventURLProcessorJob while processing win notification url",e);
            }
        }
        else if(postImpressionRequest.getPostImpressionEvent().getUrlIdentifierPrefix().equals(
                PostImpressionEventUrlReader.POSTIMPRESSION_EVENT_URL_PREFIX.BEVENT
                .getUrlIdentifierPrefix()))
        {
            try
            {
                logger.debug("Event received is billable event ..calling billable event url reader...");

                this.billableEventUrlReader.decipherPostImpressionUrl(postImpressionRequest, requestURI, context);

                logger.debug("Calling billable event enricher and fraud processor....");

                ONLINE_FRAUD_REASON onlineFraudReason = this.billableEventFraudProcessor.performOnlineFraudChecks(context);

                this.logger.debug("The online fraud reason caught is {} " ,onlineFraudReason.getFraudReasonValue());

                postImpressionRequest.setOnlineFraudReason(onlineFraudReason);
                PostImpressionUtils.writeOneByOnePixelToResponse(
                        (HttpServletResponse)context.getValue(Workflow.CONTEXT_RESPONSE_KEY));

            }
            catch(Exception e)
            {
                logger.error("Exception inside EventURLProcessorJob while processing billable event",e);
            }
        }
    }

    private ThirdPartyTrackingData prepareThirdPartyTrackingData(Request request)
    {
        ThirdPartyTrackingData thirdPartyTrackingData = new ThirdPartyTrackingData();
        thirdPartyTrackingData.setAdvertiserBid(request.getAdvertiserBid());
        thirdPartyTrackingData.setBidderModelId(request.getBidderModelId());
        thirdPartyTrackingData.setAdId(request.getAdId());
        thirdPartyTrackingData.setSelectedSiteCategoryId(request.getSelectedSiteCategoryId());
        thirdPartyTrackingData.setDeviceOsId(request.getDeviceOsId());
        thirdPartyTrackingData.setCarrierId(request.getUiCountryCarrierId());
        thirdPartyTrackingData.setCountryId(request.getUiCountryId());
        thirdPartyTrackingData.setDeviceManufacturerId(request.getDeviceManufacturerId());
        thirdPartyTrackingData.setDeviceModelId(request.getDeviceModelId());
        thirdPartyTrackingData.setDeviceBrowserId(request.getBrowserId());
        thirdPartyTrackingData.setClickRequestId(request.getRequestId());
        thirdPartyTrackingData.setInternalBid(request.getInternalMaxBid());
        thirdPartyTrackingData.setInventorySource(request.getInventorySource());
        thirdPartyTrackingData.setSiteId(request.getSiteId());
        thirdPartyTrackingData.setSupplySourceType(request.getSupplySourceTypeCode().shortValue());
        thirdPartyTrackingData.setExternalSupplyAttributesInternalId(request.getExternalSupplyAttributesInternalId());
        thirdPartyTrackingData.setSiteGuid(request.getDetectedSite().getSiteGuid());
        Short connectionTypeId = request.getConnectionType() == null ? ConnectionType.UNKNOWN.getId() :
            request.getConnectionType().getId();
        thirdPartyTrackingData.setConnectionTypeId(connectionTypeId);
        Short deviceTypeId = (null == request.getDeviceTypeId() ) ? DeviceType.UNKNOWN.getCode() :
                request.getDeviceTypeId();
        thirdPartyTrackingData.setDeviceTypeId(deviceTypeId);

        return thirdPartyTrackingData;
    }

    private String setAdvertisingCookieToEndUser(HttpServletResponse httpServletResponse)
    {

        String advertisingCookie = this.uuidGenerator.generateUniversallyUniqueIdentifier().toString();
        logger.debug("Setting advertising cookie as : {} ", advertisingCookie);
        Cookie cookie = new Cookie(cookieKeyName,advertisingCookie);
        cookie.setPath("/");
        cookie.setMaxAge(cookieExpireAgeInSeconds);
        httpServletResponse.addCookie(cookie);
        return advertisingCookie;
    }

    private String fetchAdvertisingCookieFromEndUser(HttpServletRequest httpServletRequest)
    {
        Cookie[] endUserCookies = httpServletRequest.getCookies();

        if(null != endUserCookies)
        {
            for(Cookie cookie : endUserCookies)
            {
                if(cookie.getName().equalsIgnoreCase(cookieKeyName))
                {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private void setConversionCookieToEndUser(HttpServletResponse httpServletResponse,String conversionInformation)
    {
        logger.debug("Setting conversion cookie as : {} ", conversionInformation);
        Cookie cookie = new Cookie(conversionCookieName,conversionInformation);
        cookie.setPath("/");
        cookie.setMaxAge(conversionCookieExpireAgeInSeconds);
        httpServletResponse.addCookie(cookie);
    }

    private String fetchConversionCookieFromEndUser(HttpServletRequest httpServletRequest)
    {
        Cookie[] endUserCookies = httpServletRequest.getCookies();

        if(null != endUserCookies)
        {
            for(Cookie cookie : endUserCookies)
            {
                if(cookie.getName().equalsIgnoreCase(conversionCookieName))
                {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private static String fetchLandingUrlValueFromClickUrl(HttpServletRequest httpServletRequest)
    {
        String value = httpServletRequest.getParameter(CLICK_URL_REDIRECT_PAGE_PARAMETER);

        if(null != value)
            return value;

        return null;
    }

    private String prepareConversionInformationCookieValue(Request request) throws IOException
    {
        ConversionUrlData conversionUrlData = new ConversionUrlData();
        conversionUrlData.setAdvertiserBid(request.getAdvertiserBid());
        conversionUrlData.setAdId(request.getAdId());
        conversionUrlData.setSelectedSiteCategoryId(request.getSelectedSiteCategoryId());
        conversionUrlData.setDeviceOsId(request.getDeviceOsId());
        conversionUrlData.setCarrierId(request.getUiCountryCarrierId());
        conversionUrlData.setCountryId(request.getUiCountryId());
        conversionUrlData.setDeviceManufacturerId(request.getDeviceManufacturerId());
        conversionUrlData.setDeviceModelId(request.getDeviceModelId());
        conversionUrlData.setDeviceBrowserId(request.getBrowserId());
        conversionUrlData.setBidderModelId(request.getBidderModelId());
        conversionUrlData.setClickRequestId(request.getRequestId());
        conversionUrlData.setInternalBid(request.getInternalMaxBid());
        conversionUrlData.setInventorySource(request.getInventorySource());
        conversionUrlData.setSiteId(request.getSiteId());
        conversionUrlData.setSupplySourceType(request.getSupplySourceTypeCode().shortValue());
        conversionUrlData.setExternalSupplyAttributesInternalId(request.getExternalSupplyAttributesInternalId());

        Short connectionTypeId = (request.getConnectionType() == null) ? ConnectionType.UNKNOWN.getId() :
            request.getConnectionType().getId();
        conversionUrlData.setConnectionTypeId(connectionTypeId);
        Short deviceTypeId = (null == request.getDeviceTypeId()) ? DeviceType.UNKNOWN.getCode() :
                request.getDeviceTypeId();
        conversionUrlData.setDeviceTypeId(deviceTypeId);
        return ApplicationGeneralUtils.fetchCommonEncodedDataForConversionFeedback(conversionUrlData);
    }
}

package com.kritter.adserving.request.enricher;

import com.kritter.entity.reqres.entity.Request;
import com.kritter.common.site.cache.SiteCache;
import com.kritter.common.site.entity.Site;
import com.kritter.constants.*;
import com.kritter.device.common.HandsetDetectionProvider;
import com.kritter.device.common.entity.HandsetMasterData;
import com.kritter.entity.user.userid.ExternalUserId;
import com.kritter.geo.common.entity.Country;
import com.kritter.geo.common.entity.InternetServiceProvider;
import com.kritter.geo.common.entity.reader.IConnectionTypeDetectionCache;
import com.kritter.geo.common.entity.reader.CountryDetectionCache;
import com.kritter.geo.common.entity.reader.ISPDetectionCache;
import com.kritter.utils.common.ApplicationGeneralUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Neil Attewell on 2014/07/30.
 */
public class ParameterBasedAggregatorRequestEnricher implements RequestEnricher {
    private Logger logger;
    private String parameterNameRemoteAddress;
    private String parameterNameXForwardedFor;
    private String parameterNameUserAgent;
    private String parameterNameAdvertCount;
    private String parameterNameLatitude;
    private String parameterNameLongitude;
    private String widthParameterName;
    private String heightParameterName;
    private String parameterNameInvocationCodeVersion;
    private String parameterNameUserId;
    private String parameterNameResponseFormat;
    private String parameterNameAppWapForSiteCode;
    private String parameterNameSiteId;
    private boolean extractSiteIdAsParameterElseUri;

    private String operaMiniUserAgentSubString;
    private String[] operaMiniUAHeaderNames;
    private List<String> privateAddressPrefixList;
    private SiteCache siteCache;
    private HandsetDetectionProvider handsetDetectionProvider;
    private CountryDetectionCache countryDetectionCache;
    private ISPDetectionCache ispDetectionCache;
    private IConnectionTypeDetectionCache connectionTypeDetectionCache;

    public ParameterBasedAggregatorRequestEnricher(String loggerName){
        this.logger = LoggerFactory.getLogger(loggerName);
    }
    @PostConstruct
    public void init(){
        if(this.extractSiteIdAsParameterElseUri && StringUtils.isBlank(this.parameterNameSiteId)){
            throw new RuntimeException("When extracting the Site Id as a parameter, the \"parameterNameSiteId\" is required");
        }
    }
    @Override
    public Request validateAndEnrichRequest(String requestId, HttpServletRequest httpServletRequest, Logger logger) throws Exception {
        this.logger.debug("Inside validateAndEnrichRequest() of ParameterBasedAggregatorRequestEnricher");

        Request request = new Request(requestId, INVENTORY_SOURCE.AGGREGATOR);

        String requestSiteId = extractSiteId(httpServletRequest);
        String requestIpAddress = extractRemoteAddress(httpServletRequest);
        String requestXffHeader = extractXForwardedFor(httpServletRequest);
        String requestUserAgent = extractUserAgent(httpServletRequest);
        String requestResponseFormat = extractResponseFormat(httpServletRequest);
        String requestInvocationCodeVersion = extractInvocationCodeVersion(httpServletRequest);
        String requestUserId = extractUserId(httpServletRequest);

        int requestNumberOfAds   = extractAdvertCount(httpServletRequest);
        Short requestAppWapForSiteCode = extractAppWapForSiteCode(httpServletRequest);
        Double requestLatitude = extractLatitude(httpServletRequest);
        Double requestLongitude = extractLongitude(httpServletRequest);
        Integer width = extractWidth(httpServletRequest);
        Integer height = extractHeight(httpServletRequest);

        if(StringUtils.isBlank(requestInvocationCodeVersion)){
                requestInvocationCodeVersion = "s2s_1";
        }

        if(StringUtils.isBlank(requestUserAgent) || StringUtils.isBlank(requestIpAddress) || StringUtils.isBlank(requestSiteId)) {
            request.setRequestEnrichmentErrorCode(Request.REQUEST_ENRICHMENT_ERROR_CODE.REQUEST_MALFORMED);
            return request;
        }

        String ip = ApplicationGeneralUtils.findCorrectIpAddressForDetection(requestIpAddress,
                                                                             requestXffHeader,
                                                                             privateAddressPrefixList);

        String userAgent = findCorrectUserAgent(httpServletRequest,requestUserAgent);

        ApplicationGeneralUtils.logDebug(
                this.logger,
                "AggregatorRequestEnricher, requestId: ", requestId,
                ", user-agent: ", userAgent,
                ", Final IPAddress : ", ip,
                ", xffHeader: ", requestXffHeader,
                ", siteId:", requestSiteId,
                ", ip: ", ip,
                ", invocation_code_version: ", requestInvocationCodeVersion,
                ", response_format: ", requestResponseFormat,
                ", requested ads: ",  ""+requestNumberOfAds,
                ", appWapForSiteCode: ", ""+requestAppWapForSiteCode,
                ", requestingUserId: ", requestUserId,
                ", requestingLatitude: ", ""+requestLatitude,
                ", requestingLongitude: ", ""+requestLongitude
        );

        request.setInvocationCodeVersion(requestInvocationCodeVersion);
        request.setResponseFormat(requestResponseFormat);

        /*other extra parameters being.*/
        SITE_PLATFORM sitePlatform = getSitePlatform(requestAppWapForSiteCode);
        request.setRequestedNumberOfAds(requestNumberOfAds);
        request.setSitePlatformValue(sitePlatform.getPlatform());
        Set<ExternalUserId> externalUserIds = request.getExternalUserIds();
        if(externalUserIds == null) {
            externalUserIds = new HashSet<ExternalUserId>();
            request.setExternalUserIds(externalUserIds);
        }

        if(requestUserId != null) {
            externalUserIds.add(new ExternalUserId(ExternalUserIdType.AGGREGATOR_USER_ID, request.getInventorySource(),
                    requestUserId));
        }

        request.setRequestingLongitudeValue(requestLongitude);
        request.setRequestingLatitudeValue(requestLatitude);
        /*other extra parameters end.*/

        if( StringUtils.isBlank(ip) || StringUtils.isBlank(requestInvocationCodeVersion) ) {
            request.setRequestEnrichmentErrorCode(Request.REQUEST_ENRICHMENT_ERROR_CODE.REQUEST_MALFORMED);
            this.logger.error("For requestId: {} Request is malformed inside ParameterBasedAggregatorRequestEnricher", requestId);
            return request;
        }
        //get the site object and decide to set error code or put site object.
        Site site = fetchSiteEntity(requestSiteId);
        logger.debug("Site is null? {}",(null == site));
        logger.debug("Site status is : {}", site.getStatus());

        if(null==site || site.getStatus() != StatusIdEnum.Active.getCode()){
            request.setRequestEnrichmentErrorCode(Request.REQUEST_ENRICHMENT_ERROR_CODE.SITE_NOT_FIT);
            this.logger.error("Requesting site is not fit or is not found in cache . siteid: {}", requestSiteId );
            return request;
        }

        ApplicationGeneralUtils.logDebug(this.logger,"The site detection complete, internal site id is : ", String.valueOf(site.getId()));
        //set the site platform value if received from aggregator.
        if(sitePlatform != SITE_PLATFORM.NO_VALID_VALUE) {
            site.setSitePlatform(sitePlatform.getPlatform());
        }
        request.setSite(site);
        request.setUserAgent(userAgent);

        HandsetMasterData handsetMasterData = this.handsetDetectionProvider.detectHandsetForUserAgent(userAgent);
        if(null == handsetMasterData) {
            this.logger.error("Device detection failed inside ParameterBasedAggregatorRequestEnricher, can not proceed further");
            request.setRequestEnrichmentErrorCode(Request.REQUEST_ENRICHMENT_ERROR_CODE.DEVICE_UNDETECTED);
            return request;
        }
        if(handsetMasterData.isBot()){
            this.logger.error("Device detected is BOT inside ParameterBasedAggregatorRequestEnricher, can not proceed further");
            request.setRequestEnrichmentErrorCode(Request.REQUEST_ENRICHMENT_ERROR_CODE.DEVICE_BOT);
            return request;
        }
        this.logger.debug("The internal id for handset detection is : {}", handsetMasterData.getInternalId());
        request.setHandsetMasterData(handsetMasterData);
        //also set requested slot size.
        int[] requiredWidths = new int[1];
        int[] requiredHeights = new int[1];

        if(null != width && null != height)
        {
            requiredWidths[0] = width;
            requiredHeights[0] = height;
        }
        else
        {
            requiredWidths[0] = handsetMasterData.getHandsetCapabilityObject().getResolutionWidth();
            requiredHeights[0] = handsetMasterData.getHandsetCapabilityObject().getResolutionHeight();
        }

        request.setRequestedSlotWidths(requiredWidths);
        request.setRequestedSlotHeights(requiredHeights);
            /***************Done setting handset attributes ******************/

        if(StringUtils.isBlank(ip)) {
            return request;
        }
        //set final ip address into request object, used for detection.
        request.setIpAddressUsedForDetection(ip);
        //get country id from country file.
        Country country =  findCountry(ip);
        if(null != country){
            ApplicationGeneralUtils.logDebug(
                this.logger,
                " Country detected successfully inside AggregatorRequestEnricher, id being: ",
                String.valueOf(country.getCountryInternalId())
            );
        }
        request.setCountry(country);
        //get country carrier id from country carrier file
        InternetServiceProvider internetServiceProvider = findCarrierEntity(ip);

        //check also if detected isp datasource is same as country datasource.
        //if not then only country is detected and isp remains undetected.
        if(internetServiceProvider == null) {
            return request;
        }
        if(country == null) {
            return request;
        }
        if(!StringUtils.equalsIgnoreCase(internetServiceProvider.getDataSourceName(),country.getDataSourceName())) {
            return request;
        }
        ApplicationGeneralUtils.logDebug(
            this.logger,
            " CountryCarrier detected successfully inside AggregatorRequestEnricher, id being: ",
            String.valueOf(internetServiceProvider.getOperatorInternalId())
        );
        request.setInternetServiceProvider(internetServiceProvider);

        if(connectionTypeDetectionCache != null) {
            // Get the connection type for this ip
            request.setConnectionType(this.connectionTypeDetectionCache.getConnectionType(ip));
        } else {
            // Default to unknown if the cache is not present
            request.setConnectionType(ConnectionType.UNKNOWN);
        }

        return request;
    }
    /** Convenience methods **/
    private Country findCountry(String ip){
        try{
            return this.countryDetectionCache.findCountryForIpAddress(ip);
        }catch (Exception e){
            logger.error("Exception inside AggregatorRequestEnricher in fetching country " , e);
        }
        return null;
    }
    private InternetServiceProvider findCarrierEntity(String ip){
        try{
            return this.ispDetectionCache.fetchISPForIpAddress(ip);
        }catch (Exception e){
            logger.error("Exception inside AggregatorRequestEnricher in fetching isp ",e);
        }
        return null;
    }
    private Site fetchSiteEntity(String siteId){
        return this.siteCache.query(siteId);
    }
    private SITE_PLATFORM getSitePlatform(Short appWapForSiteCodeValue){
        if(appWapForSiteCodeValue == SITE_PLATFORM.APP.getPlatform()){
            return SITE_PLATFORM.APP;
        }
        if(appWapForSiteCodeValue == SITE_PLATFORM.APP.getPlatform()){
            return SITE_PLATFORM.WAP;
        }
        return SITE_PLATFORM.NO_VALID_VALUE;
    }

    private String findCorrectUserAgent(HttpServletRequest request, String userAgent){
        if(StringUtils.isBlank(userAgent)) {
            return userAgent;
        }
        if(!StringUtils.containsIgnoreCase(userAgent,this.operaMiniUserAgentSubString)) {
            return userAgent;
        }
        for(String operaMiniUAHeader : this.operaMiniUAHeaderNames){
            String alternativeUserAgent = request.getParameter("header_" + operaMiniUAHeader);
            if(StringUtils.isNotBlank(alternativeUserAgent)) {
                return alternativeUserAgent;
            }
        }
        return userAgent;
    }
    /** Extractor methods **/
    private String extractRemoteAddress(HttpServletRequest request) {
        return extractParameterValueFromRequest(request,this.parameterNameRemoteAddress);
    }
    private String extractXForwardedFor(HttpServletRequest request) {
        return extractParameterValueFromRequest(request,this.parameterNameXForwardedFor);
    }
    private String extractSiteId(HttpServletRequest request) {
        if(this.extractSiteIdAsParameterElseUri){
            return extractParameterValueFromRequest(request,this.parameterNameSiteId);
        }

        String path = request.getRequestURI();
        return StringUtils.substringAfterLast(path,"/");
//        if (path == null) {
//            return null;
//        }
//        if (path.startsWith("/") && path.length() > 1) {
//            path = path.substring(1);
//        }
//        return StringUtils.substringBefore(path, "/");
    }
    private String extractUserAgent(HttpServletRequest request) {
        return extractParameterValueFromRequest(request,this.parameterNameUserAgent);
    }
    private Short extractAdvertCount(HttpServletRequest request) {
        String value = extractParameterValueFromRequest(request,this.parameterNameAdvertCount);
        if(StringUtils.isBlank(value)){
            return 0;
        }
        if(StringUtils.isNumeric(value)){
            return Short.valueOf(value);
        }
        return 0;
    }
    private Double extractLatitude(HttpServletRequest request) {
        String value = extractParameterValueFromRequest(request,this.parameterNameLatitude);
        if(StringUtils.isBlank(value)){
            return null;
        }
        try{
            return Double.valueOf(value);
        }catch (NumberFormatException e){
            return null;
        }
    }
    private Double extractLongitude(HttpServletRequest request) {
        String value = extractParameterValueFromRequest(request,this.parameterNameLongitude);
        if(StringUtils.isBlank(value)){
            return null;
        }
        try{
            return Double.valueOf(value);
        }catch (NumberFormatException e){
            return null;
        }
    }

    private Integer extractWidth(HttpServletRequest request){
        String value = extractParameterValueFromRequest(request,this.widthParameterName);
        if(StringUtils.isBlank(value)){
            return null;
        }
        try{
            return Integer.valueOf(value);
        }catch (NumberFormatException e){
            return null;
        }
    }

    private Integer extractHeight(HttpServletRequest request){
        String value = extractParameterValueFromRequest(request,this.heightParameterName);
        if(StringUtils.isBlank(value)){
            return null;
        }
        try{
            return Integer.valueOf(value);
        }catch (NumberFormatException e){
            return null;
        }
    }

    private String extractInvocationCodeVersion(HttpServletRequest request) {
        return extractParameterValueFromRequest(request,this.parameterNameInvocationCodeVersion);
    }
    private String extractUserId(HttpServletRequest request) {
        return extractParameterValueFromRequest(request,this.parameterNameUserId);
    }
    private String extractResponseFormat(HttpServletRequest request) {

        String outputFormat = extractParameterValueFromRequest(request,this.parameterNameResponseFormat);

        //assign default to xhtml if not found
        if(!(outputFormat.equalsIgnoreCase(FormatterIds.XHTML_FORMATTER_ID) ||
             outputFormat.equalsIgnoreCase(FormatterIds.XML_FORMATTER_ID))
          )
        {
            outputFormat = FormatterIds.XHTML_FORMATTER_ID;
        }

        return outputFormat;
    }
    private Short extractAppWapForSiteCode(HttpServletRequest request) {
        String value = extractParameterValueFromRequest(request,this.parameterNameAppWapForSiteCode);
        if(StringUtils.isBlank(value)){
            return 0;
        }
        if(StringUtils.isNumeric(value)){
            return Short.valueOf(value);
        }
        return 0;
    }

    private static String extractParameterValueFromRequest(HttpServletRequest request, String name){
        return trimAndClean(request.getParameter(name));
    }
    private static String trimAndClean(String input){
        input = StringUtils.trim(input);
        input = StringUtils.trimToNull(input);
        input = StringUtils.removeStart(input, "[");
        input = StringUtils.removeEnd(input, "]");
        return StringUtils.trim(input);
    }

    /** Setter methods **/
    @Required
    public void setParameterNameRemoteAddress(String parameterNameRemoteAddress) {
        this.parameterNameRemoteAddress = parameterNameRemoteAddress;
    }
    @Required
    public void setParameterNameXForwardedFor(String parameterNameXForwardedFor) {
        this.parameterNameXForwardedFor = parameterNameXForwardedFor;
    }
    @Required
    public void setParameterNameUserAgent(String parameterNameUserAgent) {
        this.parameterNameUserAgent = parameterNameUserAgent;
    }
    @Required
    public void setParameterNameAdvertCount(String parameterNameAdvertCount) {
        this.parameterNameAdvertCount = parameterNameAdvertCount;
    }
    @Required
    public void setParameterNameLatitude(String parameterNameLatitude) {
        this.parameterNameLatitude = parameterNameLatitude;
    }
    @Required
    public void setParameterNameLongitude(String parameterNameLongitude) {
        this.parameterNameLongitude = parameterNameLongitude;
    }
    @Required
    public void setWidthParameterName(String widthParameterName) {
        this.widthParameterName = widthParameterName;
    }
    @Required
    public void setHeightParameterName(String heightParameterName) {
        this.heightParameterName = heightParameterName;
    }
    @Required
    public void setParameterNameInvocationCodeVersion(String parameterNameInvocationCodeVersion) {
        this.parameterNameInvocationCodeVersion = parameterNameInvocationCodeVersion;
    }
    @Required
    public void setParameterNameUserId(String parameterNameUserId) {
        this.parameterNameUserId = parameterNameUserId;
    }
    @Required
    public void setParameterNameResponseFormat(String parameterNameResponseFormat) {
        this.parameterNameResponseFormat = parameterNameResponseFormat;
    }
    @Required
    public void setParameterNameAppWapForSiteCode(String parameterNameAppWapForSiteCode) {
        this.parameterNameAppWapForSiteCode = parameterNameAppWapForSiteCode;
    }
    @Required
    public void setOperaMiniUserAgentSubString(String operaMiniUserAgentSubString) {
        this.operaMiniUserAgentSubString = operaMiniUserAgentSubString;
    }
    @Required
    public void setOperaMiniUAHeaderNames(String[] operaMiniUAHeaderNames) {
        this.operaMiniUAHeaderNames = operaMiniUAHeaderNames;
    }

    @Required
    public void setPrivateAddressPrefixList(List<String> privateAddressPrefixList) {
        this.privateAddressPrefixList = privateAddressPrefixList;
    }
    @Required
    public void setSiteCache(SiteCache siteCache) {
        this.siteCache = siteCache;
    }
    @Required
    public void setHandsetDetectionProvider(HandsetDetectionProvider handsetDetectionProvider) {
        this.handsetDetectionProvider = handsetDetectionProvider;
    }
    @Required
    public void setCountryDetectionCache(CountryDetectionCache countryDetectionCache) {
        this.countryDetectionCache = countryDetectionCache;
    }
    @Required
    public void setIspDetectionCache(ISPDetectionCache ispDetectionCache) {
        this.ispDetectionCache = ispDetectionCache;
    }
    @Required
    public void setConnectionTypeDetectionCache(IConnectionTypeDetectionCache connectionTypeDetectionCache) {
        this.connectionTypeDetectionCache = connectionTypeDetectionCache;
    }
    @Required
    public void setExtractSiteIdAsParameterElseUri(boolean extractSiteIdAsParameterElseUri) {
        this.extractSiteIdAsParameterElseUri = extractSiteIdAsParameterElseUri;
    }
    //Optional
    public void setParameterNameSiteId(String parameterNameSiteId) {
        this.parameterNameSiteId = parameterNameSiteId;
    }
}

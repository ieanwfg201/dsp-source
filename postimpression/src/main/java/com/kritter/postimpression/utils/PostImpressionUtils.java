package com.kritter.postimpression.utils;


import com.kritter.constants.ConnectionType;
import com.kritter.constants.error.ErrorEnum;
import com.kritter.postimpression.entity.Request;
import com.kritter.utils.common.ApplicationGeneralUtils;
import com.kritter.utils.uuid.mac.UUIDGenerator;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.apache.commons.codec.binary.Base64;

/**
 *  Utility class for post impression.
 */

public class PostImpressionUtils
{
    @Getter
    private String  uriFieldsDelimiter;
    @Getter
    private String requestIdAdIdDelimiter = ":";
    @Getter
    private String thirdPartyClickUrlSuffix;
    @Getter
    private String eventUrlNotSupportedMessage;
    @Getter
    private String terminationReasonKey;
    @Getter
    private String jspAccessUserName;
    @Getter
    private String jspAccessPassword;

    private Logger applicationLogger;
    private boolean isUUIDBasedOnMacAddress;

    //one by one pixel image content
    private static final String PIXEL_B64  = "R0lGODlhAQABAPAAAAAAAAAAACH5BAEAAAAALAAAAAABAAEAAAICRAEAOw==";
    private static final byte[] PIXEL_BYTES = Base64.decodeBase64(PIXEL_B64.getBytes());

    public PostImpressionUtils(String loggerName,String uriFieldsDelimiter, String requestIdAdIdDelimiter,
                               String thirdPartyClickUrlSuffix,String eventUrlNotSupportedMessage,
                               String terminationReasonKey,String jspAccessUserName,String jspAccessPassword,
                               boolean isUUIDBasedOnMacAddress)
    {
        this.applicationLogger = LoggerFactory.getLogger(loggerName);
        this.uriFieldsDelimiter = uriFieldsDelimiter;
        this.requestIdAdIdDelimiter = requestIdAdIdDelimiter;
        this.thirdPartyClickUrlSuffix = thirdPartyClickUrlSuffix;
        this.eventUrlNotSupportedMessage = eventUrlNotSupportedMessage;
        this.terminationReasonKey = terminationReasonKey;
        this.jspAccessUserName = jspAccessUserName;
        this.jspAccessPassword = jspAccessPassword;
        this.isUUIDBasedOnMacAddress = isUUIDBasedOnMacAddress;
    }

    public String removeApplicationNameFromRequestUri(String requestURI){

        String parts[] = requestURI.split(this.uriFieldsDelimiter);
        StringBuffer result = new StringBuffer();

        for(int i=2 ; i<parts.length ; i++){
            result.append(this.uriFieldsDelimiter);
            result.append(parts[i]);
        }

        return result.toString();
    }

    public static void populateMapWithRequestParams(Request request, HttpServletRequest httpServletRequest,
                                                    Logger applicationLogger){

        Map<String,String[]> parameterMap = (Map<String,String[]>)httpServletRequest.getParameterMap();

        Set<Map.Entry<String,String[]>> entrySet = parameterMap.entrySet();

        Iterator<Map.Entry<String,String[]>> iterator = entrySet.iterator();

        while(iterator.hasNext()){
            Map.Entry<String,String[]> entry = iterator.next();
            String key = entry.getKey();
            String value[] = entry.getValue();

            try{

                request.setRequestMetaData(key,value);

            }catch(Exception e){

                applicationLogger.error("Exception inside populateMapWithRequestParams of PostImpressionUtils", e);

            }
        }
    }

    public static void logDebug(Logger logger, String... messages){

        if(logger.isDebugEnabled()){
            StringBuffer sb = new StringBuffer();
            for(int i=0;i<messages.length;i++){
                sb.append(messages[i]);
            }
            logger.debug(sb.toString());
        }

    }

    public static void redirectUserToLandingPage(String landingPageUrl,
                                                 HttpServletResponse httpServletResponse) throws IOException
    {
        httpServletResponse.sendRedirect(landingPageUrl);
    }

    public static void writeNoContentHeaderAsResponse(HttpServletResponse httpServletResponse) throws IOException
    {
        httpServletResponse.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    public static void writeOKAsResponseForConversionFeedback(HttpServletResponse httpServletResponse) throws IOException
    {
        OutputStream os = httpServletResponse.getOutputStream();
        os.write(ErrorEnum.OK_STATUS_FOR_CONVERSION_FEEDBACK.getName().getBytes());
        os.flush();
        os.close();
    }

    public static void writeFAILAsResponseForConversionFeedback(HttpServletResponse httpServletResponse) throws IOException
    {
        OutputStream os = httpServletResponse.getOutputStream();
        os.write(ErrorEnum.FRAUD_STATUS_FOR_CONVERSION_FEEDBACK.getName().getBytes());
        os.flush();
        os.close();
    }

    public static void writeOneByOnePixelToResponse(HttpServletResponse httpServletResponse) throws IOException
    {
        httpServletResponse.setContentType("image/gif");
        httpServletResponse.getOutputStream().write(PIXEL_BYTES);
    }

    /**
     * This function prepares postimpression request with parameters from uri.
     * In doing so it takes care of the version received from the uri according
     * to which the uri parsing happens.
     * If uri version is changed as a result of adserving push of change in uri
     * then we can implement the new uri parsing here and release postimpression
     * application.After which adserving push can happen so that uri parsing
     * does not break.
     * @param postImpressionRequest
     * @param requestURI
     * @throws Exception
     */
    public void populatePostImpressionRequestObject( Request postImpressionRequest,String requestURI) throws Exception{
    	populatePostImpressionRequestObject(postImpressionRequest, requestURI, true);
    }
    
    public void populatePostImpressionRequestObject(
                                                   Request postImpressionRequest,
                                                   String requestURI, boolean hashPresent
                                                  ) throws Exception
    {
        String requestURIParts[] = requestURI.split(this.uriFieldsDelimiter);

        //the request uri parts have to be atleast three length so as the version
        //could be checked.
        if(requestURIParts.length < 3)
            throw new Exception("The postimpression uri is malformed , not of appropriate length," +
                                "even unable to check the version of the uri inside " +
                                "populatePostImpressionRequestObject() of PostImpressionUtils");

        try
        {
            Integer urlVersion = Integer.parseInt(requestURIParts[2]);

            //prepare request for version 1
            if(urlVersion.intValue() == 1)
            {
                preparePostImpressionRequestWithRequestParametersForVersion1(postImpressionRequest,requestURIParts, hashPresent);
            }
        }
        catch (RuntimeException e)
        {
            throw new Exception("RuntimeException inside populatePostImpressionRequestObjet() of " +
                                "PostImpressionUtils", e);
        }
    }
    public void populatePostImpressionRequestObjectForNoFRDP(
            Request postImpressionRequest,
            String requestURI, boolean hashPresent
           ) throws Exception
    {
    	
    	String requestURIParts[] = requestURI.split(this.uriFieldsDelimiter);

    	//the request uri parts have to be atleast three length so as the version
    	//could be checked.
    	if(requestURIParts.length < 3)
    		throw new Exception("The postimpression uri is malformed , not of appropriate length," +
    				"even unable to check the version of the uri inside " +
    				"populatePostImpressionRequestObject() of PostImpressionUtils");

    	try
    	{
    		Integer urlVersion = Integer.parseInt(requestURIParts[2]);

    		//prepare request for version 1
    		if(urlVersion.intValue() == 1)
    		{
    			preparePostImpressionRequestWithRequestParametersForVersion1(postImpressionRequest,requestURIParts, hashPresent);
    		}
    	}
    	catch (RuntimeException e)
    	{
    		throw new Exception("RuntimeException inside populatePostImpressionRequestObjet() of " +
    				"PostImpressionUtils", e);
    	}
}

    /**
     * This function populates postimpression request object with request time parameters for version 1.
     * @param postImpressionRequest
     * @param requestURIParts
     * @throws Exception
     */
    private void preparePostImpressionRequestWithRequestParametersForVersion1(
                                                                              Request postImpressionRequest,
                                                                              String requestURIParts[], boolean hashPresent
                                                                             ) throws Exception
    {
        if(hashPresent && (null == requestURIParts || requestURIParts.length != 22))
            throw new Exception("Inside preparePostImpressionRequestWithRequestParametersForVersion1() of " +
                                "PostImpressionUtils, URI is malformed");
        if(!hashPresent && (null == requestURIParts || requestURIParts.length != 21))
            throw new Exception("Inside preparePostImpressionRequestWithRequestParametersForVersion1() of " +
                                "PostImpressionUtils, URI is malformed");

        try
        {
            Integer urlVersion = Integer.parseInt(requestURIParts[2]);
            Integer inventorySource = Integer.parseInt(requestURIParts[3]);
            String impressionId = requestURIParts[4];
            Long deviceId = Long.parseLong(requestURIParts[5]);
            Integer manufacturerId = Integer.parseInt(requestURIParts[6]);
            Integer modelId = Integer.parseInt(requestURIParts[7]);
            Integer osId = Integer.parseInt(requestURIParts[8]);
            Integer browserId = Integer.parseInt(requestURIParts[9]);
            Short slotId = Short.parseShort(requestURIParts[10]);
            Integer siteId = Integer.parseInt(requestURIParts[11]);
            Integer countryId = Integer.parseInt(requestURIParts[12]);
            Integer countryCarrierId = Integer.parseInt(requestURIParts[13]);
            String encodedBidInfo = requestURIParts[14];
            Map<ApplicationGeneralUtils.AD_BIDS_TYPE,Double> bidInfoDecoded =
                    ApplicationGeneralUtils.fetchBidTypesWithValues(encodedBidInfo);
            Short bidderModelId = Short.valueOf(requestURIParts[15]);
            Short selectedSiteCategoryId = Short.valueOf(requestURIParts[16]);
            Short supplySourceTypeCode = Short.valueOf(requestURIParts[17]);
            Integer externalSupplyAttributesInternalId = Integer.valueOf(requestURIParts[18]);
            Short connectionTypeId = Short.valueOf(requestURIParts[19]);
            Short deviceTypeId = Short.valueOf(requestURIParts[20]);
            String hashValue = null;
            if(hashPresent){
            	hashValue = requestURIParts[21];
            }

            String impressionIdSplitForAdId[] = impressionId.split(this.requestIdAdIdDelimiter);

            if(impressionIdSplitForAdId.length != 2)
                throw new RuntimeException("Impression id received in ClickUrl is malformed.Has adid missing.");

            String adservingRequestId = impressionIdSplitForAdId[0];
            Integer adId = Integer.valueOf(impressionIdSplitForAdId[1],16);

            postImpressionRequest.setUrlVersion(urlVersion);
            postImpressionRequest.setInventorySource(inventorySource);
            postImpressionRequest.setAdservingRequestId(adservingRequestId);
            postImpressionRequest.setImpressionId(impressionId);
            postImpressionRequest.setAdId(adId);
            postImpressionRequest.setDeviceId(deviceId);
            postImpressionRequest.setDeviceManufacturerId(manufacturerId);
            postImpressionRequest.setDeviceModelId(modelId);
            postImpressionRequest.setDeviceOsId(osId);
            postImpressionRequest.setBrowserId(browserId);
            postImpressionRequest.setSlotId(slotId);
            postImpressionRequest.setSiteId(siteId);
            postImpressionRequest.setUiCountryId(countryId);
            postImpressionRequest.setUiCountryCarrierId(countryCarrierId);

            postImpressionRequest.setInternalMaxBid(bidInfoDecoded.
                    get(ApplicationGeneralUtils.AD_BIDS_TYPE.INTERNAL_BID));
            postImpressionRequest.setAdvertiserBid(bidInfoDecoded.
                    get(ApplicationGeneralUtils.AD_BIDS_TYPE.ADVERTISER_BID));
            postImpressionRequest.setBidderModelId(bidderModelId);
            postImpressionRequest.setSelectedSiteCategoryId(selectedSiteCategoryId);
            postImpressionRequest.setSupplySourceTypeCode(supplySourceTypeCode);
            postImpressionRequest.setExternalSupplyAttributesInternalId(externalSupplyAttributesInternalId);
            postImpressionRequest.setConnectionType(ConnectionType.getEnum(connectionTypeId));
            postImpressionRequest.setDeviceTypeId(deviceTypeId);
            postImpressionRequest.setUrlHash(hashValue);

            //set event time when the request
            if(isUUIDBasedOnMacAddress)
                postImpressionRequest.setEventTime(UUIDGenerator.extractTimeInLong(postImpressionRequest.getAdservingRequestId()));
            else
                postImpressionRequest.setEventTime(com.kritter.utils.uuid.rand.UUIDGenerator.
                        extractTimeInLong(postImpressionRequest.getAdservingRequestId()));
        }
        catch (RuntimeException e)
        {
            throw new Exception("RuntimeException inside populatePostImpressionRequestObjet() of " +
                                "PostImpressionUtils", e);
        }
    }
}

package com.kritter.valuemaker.reader_v20160817.response_creator;


import com.kritter.bidrequest.entity.IBidResponse;
import com.kritter.bidrequest.entity.common.openrtbversion2_3.BidRequestImpressionDTO;
import com.kritter.bidrequest.exception.BidResponseException;
import com.kritter.bidrequest.response_creator.IBidResponseCreator;
import com.kritter.constants.CreativeFormat;
import com.kritter.constants.ExternalUserIdType;
import com.kritter.core.workflow.Context;
import com.kritter.core.workflow.Workflow;
import com.kritter.entity.reqres.entity.Request;
import com.kritter.entity.reqres.entity.Response;
import com.kritter.entity.reqres.entity.ResponseAdInfo;
import com.kritter.entity.user.userid.ExternalUserId;
import com.kritter.ex_int.utils.comparator.EcpmValueComparator;
import com.kritter.ex_int.utils.picker.RandomPicker;
import com.kritter.formatterutil.CreativeFormatterUtils;
import com.kritter.serving.demand.cache.AdEntityCache;
import com.kritter.serving.demand.entity.Creative;
import com.kritter.utils.common.ApplicationGeneralUtils;
import com.kritter.utils.common.ServerConfig;
import com.kritter.valuemaker.reader_v20160817.converter.response.ConvertBid;
import com.kritter.valuemaker.reader_v20160817.entity.BidRequestVam;
import com.kritter.valuemaker.reader_v20160817.entity.VamBidRequestParentNodeDTO;
import org.apache.commons.codec.binary.Base64;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

public class VamBidResponseCreator implements IBidResponseCreator
{
    private Logger logger;
    private ObjectMapper objectMapper;
    private static final Random randomPicker = new Random();
    private String secretKey;
    private int urlVersion;
    public VamBidResponseCreator(
                                 String loggerName,
                                 ServerConfig serverConfig,
                                 String secretKey,
                                 int urlVersion,
                                 AdEntityCache adEntityCache
                                 )
    {
        this.logger = LoggerFactory.getLogger(loggerName);
        this.objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
        this.secretKey = secretKey;
        this.urlVersion = urlVersion;
    }

    private void writeEmptyResponse(OutputStream outputStream,int totalProcessingTime) throws IOException {
        RTB.VamRealtimeBidding.VamResponse.Builder vamResponseBuilder = RTB.VamRealtimeBidding.VamResponse.newBuilder();
        RTB.VamRealtimeBidding.VamResponse response = vamResponseBuilder.build();
        response.writeTo(outputStream);
        outputStream.close();
    }

    private void populateVamBidResponseUsingValidPayloadAndWriteToExchange(
                                                                           Request request,
                                                                           Response response,
                                                                           OutputStream outputStream,
                                                                           int totalProcessingTime
                                                                          ) throws IOException,BidResponseException
    {
        /*********************Get openrtb bidrequest object for reading different request attributes.*****************/
        RTB.VamRealtimeBidding.VamResponse.Builder vamResponseBuilder = RTB.VamRealtimeBidding.VamResponse.newBuilder();
        vamResponseBuilder.setId(request.getRequestId());
        Set<String> impressionIdsToRespondFor = response.fetchRTBExchangeImpressionIdToRespondFor();
        if(null == impressionIdsToRespondFor)
            logger.debug("There is no impression ids to respond for inside VamBidResponseCreator");
        Comparator<ResponseAdInfo> comparator = new EcpmValueComparator();
        BidRequestVam bidRequestVam = (BidRequestVam)request.getBidRequest();

        VamBidRequestParentNodeDTO vamBidRequestParentNodeDTO = bidRequestVam.getVamBidRequestParentNodeDTO();
        Map<String,BidRequestImpressionDTO> bidRequestImpressionDTOMap = new HashMap<String, BidRequestImpressionDTO>();
        for (BidRequestImpressionDTO bidRequestImpressionDTO:vamBidRequestParentNodeDTO.getBidRequestImpressionArray())
        {
            bidRequestImpressionDTOMap.put(bidRequestImpressionDTO.getBidRequestImpressionId(),bidRequestImpressionDTO);
        }
        /*****************The impression ids received above are the integer ids sent by valuemaker exchange.*****************/
        for(String impressionId : impressionIdsToRespondFor)
        {
            Set<ResponseAdInfo> responseAdInfos = response.getResponseAdInfoSetForBidRequestImpressionId(impressionId);
            if(null == responseAdInfos || responseAdInfos.size() <= 0){
                logger.debug("ResponseAdInfo Size is 0 inside VamBidResponseCreator , writing nofill...");
                continue;
            }
            //sort and pick the one with highest ecpm value.
            List<ResponseAdInfo> list = new ArrayList<ResponseAdInfo>();
            for(ResponseAdInfo responseAdInfoTemp : responseAdInfos){
                if(responseAdInfoTemp.getCreative().getCreativeFormat().equals(CreativeFormat.BANNER)){
                    list.add(responseAdInfoTemp);
                }else if(responseAdInfoTemp.getCreative().getCreativeFormat().equals(CreativeFormat.VIDEO)){
                        list.add(responseAdInfoTemp);
                }
            }

            if(null == list || list.size() <= 0){
                logger.debug("There is no banner to be served inside VamBidResponseCreator , writing nofill...");
                continue;
            }

            Collections.sort(list,comparator);

            ResponseAdInfo responseAdInfoToUse = list.get(0);
            responseAdInfoToUse = RandomPicker.pickRandomlyOneOfTheResponseAdInfoWithHighestSameEcpmValues
                    (responseAdInfoToUse,list, randomPicker);
            Creative creative = responseAdInfoToUse.getCreative();

            double maxPrice = responseAdInfoToUse.getEcpmValue().doubleValue();
            response.addResponseAdInfoAsFinalForImpressionId(impressionId,responseAdInfoToUse);
            String creativeId = creative.getCreativeGuid();
            if(null != responseAdInfoToUse.getCreativeBanner())
                creativeId = responseAdInfoToUse.getCreativeBanner().getGuid();
//            return convert(openrtbBid.getCreativeId(), openrtbBid.getPrice(),
//                    openrtbBid.getWinNotificationUrl(),openrtbBid.getSampleImageUrl(),
//                    null,openrtbBid.getDealId());
//        }
            RTB.VamRealtimeBidding.VamResponse.Bid bid = ConvertBid.convert(creativeId, (float)maxPrice, null,null, null, null);
            vamResponseBuilder.addBid(bid);
        }

        RTB.VamRealtimeBidding.VamResponse vamBidResponse = vamResponseBuilder.build();
        vamBidResponse.writeTo(outputStream);
        outputStream.close();
    }


    @Override
    public IBidResponse constructBidResponseForExchange(Object... dspObjects) throws BidResponseException
    {
        if(null == dspObjects || dspObjects.length != 3)
            throw new BidResponseException("Inside VamBidResponseCreator, arguments passed " +
                                           "for formatting are null or not Request and Response objects...");

        Object requestObject = dspObjects[0];
        Object responseObject = dspObjects[1];
        Object contextObject = dspObjects[2];

        Request request = (Request)requestObject;
        Response response = (Response)responseObject;
        Context context = (Context)contextObject;

        HttpServletResponse httpServletResponse = (HttpServletResponse)context.getValue(Workflow.CONTEXT_RESPONSE_KEY);

        if(
           !(requestObject instanceof Request) ||
           !(responseObject instanceof Response) ||
           !(contextObject instanceof Context)
          )
        {
            logger.error("Request/Response objects are not passed inside VamBidResponseCreator,writing no-fill");

            try
            {
                writeEmptyResponse(httpServletResponse.getOutputStream(),0);
            }
            catch (IOException ioe)
            {
                logger.error("IOException inside VamBidResponseCreator ",ioe);
            }

            return null;
        }
        Set<String> impressionIdsToRespondFor = response.fetchRTBExchangeImpressionIdToRespondFor();

        if(null == impressionIdsToRespondFor)
        {
            logger.debug("There is no impression ids to respond for inside BidRequestResponseCreatorVam");

            try
            {
                writeEmptyResponse(httpServletResponse.getOutputStream(),0);
            }
            catch (IOException ioe)
            {
                logger.error("IOException inside VamBidResponseCreator ",ioe);
            }

            return null;
        }

        try
        {
            populateVamBidResponseUsingValidPayloadAndWriteToExchange(request,
                                                                      response,
                                                                      httpServletResponse.getOutputStream(),
                                                                      2);
        }
        catch (IOException ioe)
        {
            logger.error("IOException inside VamBidResponseCreator ", ioe);
        }

        return null;
    }

    @Override
    public void validateBidResponseEntity(IBidResponse bidResponseEntity) throws BidResponseException
    {
    }

    private String preparExt(
                                             Request request,
                                             ResponseAdInfo responseAdInfo,
                                             Response response
                                            ) throws BidResponseException,IOException
    {
        String clickUri = CreativeFormatterUtils.prepareClickUri
                                                                (
                                                                 this.logger,
                                                                 request,
                                                                 responseAdInfo,
                                                                 response.getBidderModelId(),
                                                                 urlVersion,
                                                                 request.getInventorySource(),
                                                                 response.getSelectedSiteCategoryId(),
                                                                 this.secretKey, true
                                                                );

        if(null == clickUri)
            throw new BidResponseException("Click URI could not be formed using different attributes like " +
                                           "handset,location,bids,version,etc. inside VamBidResponseCreator");



        Set<ExternalUserId> externalUserIdSet = request.getExternalUserIds();
        String exchangeUserId = null;
        if(null != externalUserIdSet)
        {
            for(ExternalUserId externalUserId : externalUserIdSet)
            {
                if(externalUserId.getIdType().equals(ExternalUserIdType.EXCHANGE_CONSUMER_ID))
                    exchangeUserId = externalUserId.toString();
            }
        }
        /**
         * Reusing modify cscurl
         */
        clickUri = new StringBuffer(ApplicationGeneralUtils.modifyCSCURLForUserIds(
                                                                                       exchangeUserId,
                                                                                       request.getUserId(),
                                                                                       clickUri
                                                                                      )).toString();

        logger.debug("click url is modified to contain exchange and kritter UserId, after modification url:{} ",
                clickUri.toString());

        Base64 base64 = new Base64(0);
        logger.debug("LENGTH OF EXT ############## {} #########",clickUri.getBytes().length);
        return base64.encodeToString(clickUri.getBytes());
    }

}

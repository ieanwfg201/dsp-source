package com.kritter.tencent.reader_v20150313.response_creator;


import com.kritter.ex_int.utils.picker.AdPicker;
import com.kritter.formatterutil.CreativeFormatterUtils;
import com.kritter.entity.reqres.entity.Request;
import com.kritter.entity.reqres.entity.Response;
import com.kritter.entity.reqres.entity.ResponseAdInfo;
import com.kritter.entity.user.userid.ExternalUserId;
import com.kritter.bidrequest.entity.IBidResponse;
import com.kritter.bidrequest.entity.common.openrtbversion2_3.BidRequestImpressionDTO;
import com.kritter.bidrequest.exception.BidResponseException;
import com.kritter.bidrequest.response_creator.IBidResponseCreator;
import com.kritter.constants.CreativeFormat;
import com.kritter.constants.ExternalUserIdType;
import com.kritter.core.workflow.Context;
import com.kritter.core.workflow.Workflow;
import com.kritter.serving.demand.cache.AdEntityCache;
import com.kritter.serving.demand.entity.Creative;
import com.kritter.tencent.reader_v20150313.converter.response.ConvertBid;
import com.kritter.tencent.reader_v20150313.entity.BidRequestTencent;
import com.kritter.tencent.reader_v20150313.entity.TencentBidRequestParentNodeDTO;
import com.kritter.utils.common.ApplicationGeneralUtils;
import com.kritter.utils.common.ServerConfig;

import org.apache.commons.codec.binary.Base64;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

/**
 * This class builds tencent bid response using open rtb response.
 *
 * 1. The win notification response has a parameter d, which tells to decrypt
 * winning price on receipt using google api.
 */
public class TencentBidResponseCreator implements IBidResponseCreator
{
    private Logger logger;
    private static final Random randomPicker = new Random();
    private String secretKey;
    private int urlVersion;
    private AdPicker adPicker;
    public TencentBidResponseCreator(
                                 String loggerName,
                                 ServerConfig serverConfig,
                                 String secretKey,
                                 int urlVersion,
                                 AdEntityCache adEntityCache,
                                 AdPicker adPicker
                                 )
    {
        this.logger = LogManager.getLogger(loggerName);
        this.secretKey = secretKey;
        this.urlVersion = urlVersion;
    }

    private void writeEmptyResponse(OutputStream outputStream,int totalProcessingTime) throws IOException {
        RTB.Tencent.Response.Builder tencentResponseBuilder = RTB.Tencent.Response.newBuilder();
        RTB.Tencent.Response response = tencentResponseBuilder.build();
        response.writeTo(outputStream);
        outputStream.close();
    }

    private void populateTencentBidResponseUsingValidPayloadAndWriteToExchange(
                                                                           Request request,
                                                                           Response response,
                                                                           OutputStream outputStream,
                                                                           int totalProcessingTime
                                                                          ) throws IOException,BidResponseException
    {
        /*********************Get openrtb bidrequest object for reading different request attributes.*****************/
        RTB.Tencent.Response.Builder tencentResponseBuilder = RTB.Tencent.Response.newBuilder();
        tencentResponseBuilder.setId(request.getRequestId());
        tencentResponseBuilder.setBidid(request.getRequestId());
        RTB.Tencent.Response.SeatBid.Builder seatbidBuider = RTB.Tencent.Response.SeatBid.newBuilder(); 
        Set<String> impressionIdsToRespondFor = response.fetchRTBExchangeImpressionIdToRespondFor();
        if(null == impressionIdsToRespondFor)
            logger.debug("There is no impression ids to respond for inside TencentBidResponseCreator");
        BidRequestTencent bidRequestTencent = (BidRequestTencent)request.getBidRequest();

        TencentBidRequestParentNodeDTO tencentBidRequestParentNodeDTO = bidRequestTencent.getTencentBidRequestParentNodeDTO();
        Map<String,BidRequestImpressionDTO> bidRequestImpressionDTOMap = new HashMap<String, BidRequestImpressionDTO>();
        for (BidRequestImpressionDTO bidRequestImpressionDTO:tencentBidRequestParentNodeDTO.getBidRequestImpressionArray())
        {
            bidRequestImpressionDTOMap.put(bidRequestImpressionDTO.getBidRequestImpressionId(),bidRequestImpressionDTO);
        }
        /*****************The impression ids received above are the integer ids sent by tencent exchange.*****************/
        for(String impressionId : impressionIdsToRespondFor)
        {
            Set<ResponseAdInfo> responseAdInfos = response.getResponseAdInfoSetForBidRequestImpressionId(impressionId);
            if(null == responseAdInfos || responseAdInfos.size() <= 0){
                logger.debug("ResponseAdInfo Size is 0 inside TencentBidResponseCreator , writing nofill...");
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
                logger.debug("There is no banner to be served inside TencentBidResponseCreator , writing nofill...");
                continue;
            }

            ResponseAdInfo responseAdInfoToUse = adPicker.pick(responseAdInfos);
            Creative creative = responseAdInfoToUse.getCreative();

            String ext = preparExt(request, responseAdInfoToUse, response);
            double maxPrice = responseAdInfoToUse.getEcpmValue().doubleValue();
            response.addResponseAdInfoAsFinalForImpressionId(impressionId,responseAdInfoToUse);
            String creativeId = creative.getCreativeGuid();
            if(null != responseAdInfoToUse.getCreativeBanner())
                creativeId = responseAdInfoToUse.getCreativeBanner().getGuid();

            RTB.Tencent.Response.Bid bid = ConvertBid.convert(responseAdInfoToUse.getImpressionId(), responseAdInfoToUse.getImpressionId(), (float)maxPrice, responseAdInfoToUse.getGuid(), creativeId, ext, null);
            seatbidBuider.addBid(bid);
        }
        tencentResponseBuilder.addSeatbid(seatbidBuider.build());
        RTB.Tencent.Response tencentBidResponse = tencentResponseBuilder.build();
        tencentBidResponse.writeTo(outputStream);
        outputStream.close();
    }


    @Override
    public IBidResponse constructBidResponseForExchange(Object... dspObjects) throws BidResponseException
    {
        if(null == dspObjects || dspObjects.length != 3)
            throw new BidResponseException("Inside TencentBidResponseCreator, arguments passed " +
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
            logger.error("Request/Response objects are not passed inside TencentBidResponseCreator,writing no-fill");

            try
            {
                writeEmptyResponse(httpServletResponse.getOutputStream(),0);
            }
            catch (IOException ioe)
            {
                logger.error("IOException inside TencentBidResponseCreator ",ioe);
            }

            return null;
        }
        Set<String> impressionIdsToRespondFor = response.fetchRTBExchangeImpressionIdToRespondFor();

        if(null == impressionIdsToRespondFor)
        {
            logger.debug("There is no impression ids to respond for inside BidRequestResponseCreatorTencent");

            try
            {
                writeEmptyResponse(httpServletResponse.getOutputStream(),0);
            }
            catch (IOException ioe)
            {
                logger.error("IOException inside TencentBidResponseCreator ",ioe);
            }

            return null;
        }

        try
        {
            populateTencentBidResponseUsingValidPayloadAndWriteToExchange(request,
                                                                      response,
                                                                      httpServletResponse.getOutputStream(),
                                                                      2);
        }
        catch (IOException ioe)
        {
            logger.error("IOException inside TencentBidResponseCreator ", ioe);
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
                                           "handset,location,bids,version,etc. inside TencentBidResponseCreator");



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

package com.kritter.adserving.flow.job;

import com.kritter.adserving.formatting.JSONFormatter;
import com.kritter.adserving.formatting.VASTFormatter;
import com.kritter.adserving.formatting.XHTMLFormatter;
import com.kritter.adserving.formatting.XMLFormatter;
import com.kritter.entity.reqres.entity.Request;
import com.kritter.entity.reqres.entity.Response;
import com.kritter.entity.reqres.entity.ResponseAdInfo;
import com.kritter.bidrequest.entity.IBidRequest;
import com.kritter.bidrequest.entity.IBidResponse;
import com.kritter.bidrequest.exception.BidResponseException;
import com.kritter.bidrequest.response_creator.IBidResponseCreator;
import com.kritter.common.site.cache.SiteMetaDataCache;
import com.kritter.common.site.entity.NoFillPassbackContent;
import com.kritter.common.site.entity.Site;
import com.kritter.common.site.entity.SiteMetaDataEntity;
import com.kritter.constants.FormatterIds;
import com.kritter.constants.INVENTORY_SOURCE;
import com.kritter.constants.SITE_PASSBACK_CONTENT_TYPE;
import com.kritter.constants.SITE_PASSBACK_TYPE;
import com.kritter.core.workflow.Context;
import com.kritter.core.workflow.Job;
import com.kritter.core.workflow.Workflow;
import com.kritter.utils.common.ApplicationGeneralUtils;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * This class takes ad ids to format and after formatting writes to
 * the response, the kind of formatter to be invoked may depend upon various
 * parameters.
 */

public class ResponseFormattingJob implements Job{

    private Logger logger;
    private String name;
    private String requestObjectKey;
    private String errorOrEmptyResponse;
    private String responseObjectKey;
    private XHTMLFormatter creativesXHTMLFormatter;
    private XMLFormatter creativesXMLFormatter;
    private JSONFormatter creativesJSONFormatter;
    private Map<String,IBidResponseCreator> exchangeResponseCreatorMap;
    private String inventorySourceHeader;
    private SiteMetaDataCache siteMetaDataCache;
    private VASTFormatter vastFormatter;

    public ResponseFormattingJob(
                                 String loggerName,
                                 String jobName,
                                 String requestObjectKey,
                                 String errorOrEmptyResponse,
                                 XHTMLFormatter creativesXHTMLFormatter,
                                 XMLFormatter creativesXMLFormatter ,
                                 JSONFormatter creativesJSONFormatter,
                                 String responseObjectKey,
                                 Map<String,IBidResponseCreator> exchangeResponseCreatorMap,
                                 String inventorySourceHeader,
                                 SiteMetaDataCache siteMetaDataCache,
                                 VASTFormatter vastFormatter
                                )
    {
        this.logger = LoggerFactory.getLogger(loggerName);
        this.name = jobName;
        this.requestObjectKey = requestObjectKey;
        this.errorOrEmptyResponse = errorOrEmptyResponse;
        this.responseObjectKey = responseObjectKey;
        this.creativesXHTMLFormatter = creativesXHTMLFormatter;
        this.creativesXMLFormatter = creativesXMLFormatter;
        this.creativesJSONFormatter = creativesJSONFormatter;
        this.exchangeResponseCreatorMap = exchangeResponseCreatorMap;
        this.inventorySourceHeader = inventorySourceHeader;
        this.siteMetaDataCache = siteMetaDataCache;
        this.vastFormatter = vastFormatter;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void execute(Context context) {

        this.logger.info("Inside execute() of ResponseFormattingJob going to fetch request object and format adids");

        Request request = (Request)context.getValue(this.requestObjectKey);

        HttpServletResponse httpServletResponse = (HttpServletResponse)context.
                getValue(Workflow.CONTEXT_RESPONSE_KEY);

        String responseContent = null;
        int responseCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
        Object inventorySourceObject = context.getValue(this.inventorySourceHeader);
        short inventorySource = -1;

        if(null != inventorySourceObject)
            inventorySource = (Short)inventorySourceObject;

        //in case request is null which means request is bad or malformed or could not be enriched
        //look at the inventory source header value and write appropriate response.
        String errorName = (null != request && null != request.getRequestEnrichmentErrorCode()) ?
                           request.getRequestEnrichmentErrorCode().getErrorName() : null ;

        if(null != errorName && null != request && !request.isWriteResponseInsideExchangeAdaptor())
        {
            this.logger.error("There is error in request enrichment so skipping ResponseFormattingJob, cannot continue.Skipping further workflow in this job.Writing error/empty response. Error: {} ", errorName);

            responseCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
            responseContent = this.errorOrEmptyResponse;

            logger.debug("Inventory source code is : {} and error name: {} ",
                         inventorySource, errorName);

            //in case of exchange request write no content header, rest of cases handled at the end.
            if(inventorySource == INVENTORY_SOURCE.RTB_EXCHANGE.getCode())
            {
                logger.debug("Inventory source is RTB Exchange, writing no bid to exchange, the enrichment error is : {} ",errorName);

                writeNoBidResponseToExchange(httpServletResponse);
            }
            //may be possible that error name is not null and request is not null.
            else
            {
                if( null != request && StringUtils.isNotBlank(request.getRequestEnrichmentErrorDetail()))
                {
                    httpServletResponse.addHeader("error_reason", request.getRequestEnrichmentErrorDetail());
                }
            }
        }
        else if(null != request && request.isRequestForSystemDebugging())
        {
            responseCode = HttpServletResponse.SC_OK;
            responseContent = request.getDebugRequestBuffer().toString();
        }
        //valid request and valid response if available
        else if(null != request &&
                request.getInventorySource() == INVENTORY_SOURCE.RTB_EXCHANGE.getCode() &&
                !request.isWriteResponseInsideExchangeAdaptor())
        {
            try
            {
                logger.debug("Inventory source is RTB Exchange inside ResponseFormattingJob...");

                Response response = (Response)context.getValue(this.responseObjectKey);

                IBidRequest iBidRequest = request.getBidRequest();

                if(null == iBidRequest)
                {
                    logger.error("BidRequest object is null inside ResponseFormattingJob");
                    writeNoBidResponseToExchange(httpServletResponse);
                    return;
                }

                IBidResponseCreator bidResponseCreator =
                                            exchangeResponseCreatorMap.get(request.getBidRequest().getAuctioneerId());

                IBidResponse bidResponse = null;

                try
                {
                    bidResponse = bidResponseCreator.constructBidResponseForExchange(request,response);
                }
                catch (BidResponseException bre)
                {
                    logger.error("BidResponseException inside ResponseFormattingJob ", bre);
                }

                if(null != bidResponse)
                {
                    try
                    {
                        writeResponseToUser(httpServletResponse,
                                            bidResponse.getPayloadToBeServedToExchange(),
                                            HttpServletResponse.SC_OK);
                    }
                    catch (IOException ioe)
                    {
                        logger.error("IOException inside ResponseFormattingJob", ioe);
                        writeNoBidResponseToExchange(httpServletResponse);
                    }
                }
                else
                    writeNoBidResponseToExchange(httpServletResponse);
            }
            catch (Exception e)
            {
                logger.error("Exception inside ResponseFormattingJob ", e);
                writeNoBidResponseToExchange(httpServletResponse);
            }
        }
        else if(null != request &&
                request.getInventorySource() == INVENTORY_SOURCE.RTB_EXCHANGE.getCode() &&
                request.isWriteResponseInsideExchangeAdaptor())
        {
            try
            {
                logger.debug("Inventory source is RTB Exchange inside ResponseFormattingJob...");

                Response response = (Response)context.getValue(this.responseObjectKey);

                IBidResponseCreator bidResponseCreator =
                        exchangeResponseCreatorMap.get(request.getBidRequest().getAuctioneerId());

                try
                {
                    bidResponseCreator.constructBidResponseForExchange(request,response,context);
                }
                catch (BidResponseException bre)
                {
                    logger.error("BidResponseException inside ResponseFormattingJob ", bre);
                }
            }
            catch (Exception e)
            {
                logger.error("Exception inside ResponseFormattingJob ", e);
            }
        }
        else
        {
            Response response = (Response)context.getValue(this.responseObjectKey);
            Set<ResponseAdInfo> responseAdInfos = response.getResponseAdInfo();

            if(null == responseAdInfos)
                responseAdInfos = new HashSet<ResponseAdInfo>();

            logger.debug("AdId info size inside ResponseFormattingJob: {} ", responseAdInfos.size());

            try
            {
                if(null == request.getResponseFormat())
                    request.setResponseFormat(FormatterIds.XHTML_FORMATTER_ID);

                Site site = request.getSite();

                NoFillPassbackContent[] noFillPassbackContentArray = site.getNofillBackupContentArray();

                //consider no-fill case for network inventory where passback type
                //is SITE_PASSBACK_TYPE.DIRECT_PASSBACK,where expect single element in passback content array.
                if(
                   request.getInventorySource() != INVENTORY_SOURCE.RTB_EXCHANGE.getCode() &&
                   responseAdInfos.size() == 0 &&
                   null != noFillPassbackContentArray &&
                   noFillPassbackContentArray.length > 0 &&
                   site.getSitePassbackType().getCode() == SITE_PASSBACK_TYPE.DIRECT_PASSBACK.getCode() &&
                   !request.isPassbackUsingFormatter()
                  )
                {
                    logger.debug("NoFill event and site has specified direct passback, performing it.");
                    request.setPassbackDoneForDirectPublisherSiteNoFill(true);

                    NoFillPassbackContent noFillPassbackContent = noFillPassbackContentArray[0];

                    if(noFillPassbackContent.getPassBackContentType() == SITE_PASSBACK_CONTENT_TYPE.PASSBACK_URL.getCode())
                    {
                        logger.debug("Passback content is a URL: {} , fetch content from mysql ",
                                      noFillPassbackContent.getPassBackContent());

                        responseCode = HttpServletResponse.SC_OK;

                        try
                        {
                            SiteMetaDataEntity siteMetaDataEntity = siteMetaDataCache.query(site.getSiteGuid());
                            if(null != siteMetaDataEntity)
                                responseContent = siteMetaDataEntity.getResponseContent();
                        }
                        catch (Exception e)
                        {
                            this.logger.error("Exception inside ResponseFormattingJob while performing URL passback",e);
                            responseCode = HttpServletResponse.SC_OK;
                            responseContent = this.errorOrEmptyResponse;
                            request.setPassbackDoneForDirectPublisherSiteNoFill(false);
                        }
                    }
                    else
                    {
                        responseCode = HttpServletResponse.SC_OK;
                        responseContent = noFillPassbackContent.getPassBackContent();
                    }
                }
                else if(null != responseAdInfos && responseAdInfos.size() > 0)
                {
                    if(request.getResponseFormat().equalsIgnoreCase(FormatterIds.XHTML_FORMATTER_ID))
                        responseContent = this.creativesXHTMLFormatter.formatCreatives(request,response);
                    else if(request.getResponseFormat().equalsIgnoreCase(FormatterIds.XML_FORMATTER_ID))
                        responseContent = this.creativesXMLFormatter.formatCreatives(request,response);
                    else if(request.getResponseFormat().equalsIgnoreCase(FormatterIds.JSON_FORMATTER_ID))
                        responseContent = this.creativesJSONFormatter.formatCreatives(request,response);
                    else if(request.getResponseFormat().equalsIgnoreCase(FormatterIds.VAST_FORMATTER_ID))
                        responseContent = this.vastFormatter.formatCreatives(request,response);
                    else
                        logger.error("Unrecognized formatting option for ad units: {}" , request.getResponseFormat());

                    responseCode = HttpServletResponse.SC_OK;
                }
                else if(responseAdInfos.size() == 0)
                {
                    responseContent = this.errorOrEmptyResponse;
                    responseCode = HttpServletResponse.SC_OK;
                }
            }
            catch(Exception e)
            {
                this.logger.error("Exception inside execute() of ResponseFormattingJob ",e);
            }
        }

        if(
               null != request &&
               (
                request.getInventorySource() != INVENTORY_SOURCE.RTB_EXCHANGE.getCode() ||
                request.isRequestForSystemDebugging()
               )
          )
        {
            try
            {
                if(responseCode != HttpServletResponse.SC_FOUND)
                    httpServletResponse.setStatus(responseCode);

                writeResponseToUser(httpServletResponse,responseContent,responseCode);
            }
            catch(IOException e)
            {
                this.logger.error("IOException inside execute() of ResponseFormattingJob ",e);
            }
        }
    }

    private void writeResponseToUser(HttpServletResponse httpServletResponse,
                                     String content,
                                     int responseCode) throws IOException
    {
        if(responseCode == HttpServletResponse.SC_FOUND)
        {
            logger.debug("Response being sent to user is redirect to the url {} ",
                         content);
            httpServletResponse.sendRedirect(content);
        }
        else
        {
            OutputStream os = httpServletResponse.getOutputStream();

            if(null == content)
                content = errorOrEmptyResponse;

            os.write(content.getBytes());
            os.flush();
            os.close();
        }
    }

    private void writeNoBidResponseToExchange(HttpServletResponse httpServletResponse)
    {
        httpServletResponse.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }
}

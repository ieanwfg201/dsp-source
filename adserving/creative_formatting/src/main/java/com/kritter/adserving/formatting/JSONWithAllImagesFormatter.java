package com.kritter.adserving.formatting;

import com.kritter.entity.user.userid.ExternalUserId;
import com.kritter.constants.ExternalUserIdType;
import com.kritter.serving.demand.entity.CreativeBanner;
import com.kritter.serving.demand.entity.CreativeSlot;
import com.kritter.utils.common.ApplicationGeneralUtils;
import org.json.JSONException;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import com.kritter.entity.formatter_entity.creative.FormatCreative;
import com.kritter.entity.reqres.entity.Request;
import com.kritter.entity.reqres.entity.Response;
import com.kritter.entity.reqres.entity.ResponseAdInfo;
import com.kritter.adserving.formatting.macro.AdTagMacroReplace;
import com.kritter.constants.CreativeFormat;
import com.kritter.serving.demand.cache.AdEntityCache;
import com.kritter.serving.demand.cache.CreativeCache;
import com.kritter.serving.demand.cache.CreativeSlotCache;
import com.kritter.serving.demand.entity.AdEntity;
import com.kritter.serving.demand.entity.Creative;
import com.kritter.utils.common.ServerConfig;
import com.kritter.formatterutil.CreativeFormatterUtils;

import java.util.*;

/**
 * This class formats creatives in json format, this formatter allows to send out all images
 * available for the ad unit.
 */
public class JSONWithAllImagesFormatter extends JSONFormatter implements CreativesFormatter{
    private Logger logger;
    private String postImpressionBaseClickUrl;
    private String postImpressionBaseCSCUrl;
    private String cdnBaseImageUrl;
    private String secretKey;
    private int urlVersion;
    private CreativeCache creativeCache;
    private AdEntityCache adEntityCache;
    private CreativeSlotCache creativeSlotCache;
    private String loggerName;
	private String macroPostImpressionBaseClickUrl;

    public JSONWithAllImagesFormatter(
                                        String loggerName,
                                        String secretKey,
                                        int urlVersion,
                                        CreativeCache creativeCache,
                                        AdEntityCache adEntityCache,
                                        CreativeSlotCache creativeSlotCache,
                                        ServerConfig serverConfig
                                     )
    {
        super(loggerName,secretKey,urlVersion,creativeCache,adEntityCache,creativeSlotCache,serverConfig);
        this.logger = LogManager.getLogger(loggerName);
        this.loggerName = loggerName;
        this.postImpressionBaseClickUrl = serverConfig.getValueForKey(ServerConfig.CLICK_URL_PREFIX);
        this.postImpressionBaseCSCUrl = serverConfig.getValueForKey(ServerConfig.CSC_URL_PREFIX);
        this.cdnBaseImageUrl = serverConfig.getValueForKey(ServerConfig.CDN_URL_PREFIX);
        this.secretKey = secretKey;
        this.urlVersion = urlVersion;
        this.creativeCache = creativeCache;
        this.adEntityCache = adEntityCache;
        this.creativeSlotCache = creativeSlotCache;
        this.macroPostImpressionBaseClickUrl = serverConfig.getValueForKey(ServerConfig.MACRO_CLICK_URL_PREFIX);
    }

    @Override
    public String formatCreatives(Request request, Response response) throws Exception
    {
        if(null == response || null == response.getResponseAdInfo())
        {
            logger.error("No Ads shortlisted for this request,nothing to format in formatCreatives of JSONFormatter");
            return null;
        }

        logger.debug("Bidder model id inside formatCreatives of JSONFormatter is : {}", response.getBidderModelId());
        FormatCreative formatCreative = new FormatCreative(loggerName);

        for(ResponseAdInfo responseAdInfo : response.getResponseAdInfo())
        {
            if(null == responseAdInfo)
            {
                continue;
            }
            AdEntity adEntity = this.adEntityCache.query(responseAdInfo.getAdId());

            if(null== adEntity)
            {
                this.logger.error("The ad entity not found in cache inside formatCreatives() of JSONFormatter,id being: {}", adEntity.getId());
                continue;
            }

            Creative creative = this.creativeCache.query(adEntity.getCreativeId());

            if(null == creative)
            {
                this.logger.error("The creative entity not found inside formatCreatives() JSONFormatter,id being: {}", adEntity.getCreativeId());
                continue;
            }

            String clickUri = CreativeFormatterUtils.prepareClickUri(
                    this.logger,
                    request,
                    responseAdInfo,
                    response.getBidderModelId(),
                    this.urlVersion,
                    request.getInventorySource(),
                    response.getSelectedSiteCategoryId(),
                    this.secretKey
            );
            if(null == clickUri){
                throw new Exception("Click URI could not be formed using different attributes like handset,location,bids,version,etc. inside formatCreatives of JSONFormatter");
            }
            StringBuffer clickUrl = new StringBuffer();
            clickUrl.append(this.postImpressionBaseClickUrl);
            clickUrl.append(clickUri);
            StringBuffer macroClickUrl = new StringBuffer(this.macroPostImpressionBaseClickUrl);
            macroClickUrl.append(clickUri);


            StringBuffer cscBeaconUrl = new StringBuffer();
            cscBeaconUrl.append(this.postImpressionBaseCSCUrl);
            cscBeaconUrl.append(clickUri);

            /**modify csc url to have bid-switch exchangeId as a parameter for usage in post-impression server************/
            logger.debug("Going to modify CSC URL if request has user ids available, for kritterUserId:{} ",
                    request.getUserId());

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

            cscBeaconUrl = new StringBuffer(ApplicationGeneralUtils.modifyCSCURLForUserIds(
                    exchangeUserId,
                    request.getUserId(),
                    cscBeaconUrl.toString())
            );

            logger.debug("CSC url is modified to contain exchange and kritter UserId, after modification url:{} ",
                    cscBeaconUrl.toString());

            /**************************modifying csc url completed********************************************************/

            //set common post impression uri to be used in any type of post impression url.
            responseAdInfo.setCommonURIForPostImpression(clickUri);

            if(creative.getCreativeFormat().equals(CreativeFormat.BANNER))
            {
                if(null == responseAdInfo.getCreativeBannerSetForNetworkTraffic())
                {
                    logger.error("CreativeBanner List is null inside JSONWithAllImagesFormatter,cannot format ad, for adid: {}", responseAdInfo.getAdId());
                    continue;
                }
                List<String> extImpTracker = null;
                if(adEntity.getExtTracker() != null && adEntity.getExtTracker().getImpTracker() != null){
                    extImpTracker = AdTagMacroReplace.adTagMacroReplace(adEntity.getExtTracker().getImpTracker(), request, 
                    		responseAdInfo, response, macroClickUrl.toString(), adEntity.getExtTracker().getImpMacro(), adEntity.getExtTracker().getImpMacroQuote());
                }

                Map<String,String> allImages = new HashMap<String, String>();
                for(CreativeBanner creativeBanner :  responseAdInfo.getCreativeBannerSetForNetworkTraffic())
                {
                    CreativeSlot creativeSlot = creativeSlotCache.query(creativeBanner.getSlotId());
                    String sizeFormatted = formatSize(creativeSlot.getCreativeSlotWidth(),creativeSlot.getCreativeSlotHeight());
                    if(null == sizeFormatted)
                        sizeFormatted = "";

                    allImages.put(creativeBanner.getResourceURI(),sizeFormatted);
                }

                formatBanner(formatCreative,allImages,creative, clickUrl.toString(),cscBeaconUrl.toString(), extImpTracker);
            }
            else if(creative.getCreativeFormat().equals(CreativeFormat.TEXT))
            {
                formatText(formatCreative, creative, clickUrl.toString(), cscBeaconUrl.toString());
            }
        }

        return formatCreative.writeJson();
    }
    private void formatText(FormatCreative formatCreative, Creative creative, String clickUrl, String cscBeaconUrl) throws JSONException
    {
        formatCreative.addTextEntity(creative.getText(), clickUrl.toString(), cscBeaconUrl.toString());
    }

    private void formatBanner(FormatCreative formatCreative, Map<String,String> images, Creative creative,
                              String clickUrl, String cscBeaconUrl, List<String> extImpTracker) throws JSONException
    {
        Map<String,String> fullPathImageSet = new HashMap<String,String>();
        for(String image : images.keySet())
        {
            fullPathImageSet.put(this.cdnBaseImageUrl + image,images.get(image));
        }

        formatCreative.addBannerEntity(fullPathImageSet,creative.getText(), clickUrl, cscBeaconUrl, extImpTracker);
    }

    public String formatSize(Short width,Short height)
    {
        if(null == width || null == height)
            return null;

        StringBuffer sb = new StringBuffer();
        sb.append(width);
        sb.append("*");
        sb.append(height);
        return sb.toString();
    }
}
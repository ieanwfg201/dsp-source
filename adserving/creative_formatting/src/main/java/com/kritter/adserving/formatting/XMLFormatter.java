package com.kritter.adserving.formatting;

import com.kritter.entity.formatter_entity.creative.FormatCreative;
import com.kritter.entity.reqres.entity.Request;
import com.kritter.entity.reqres.entity.Response;
import com.kritter.entity.reqres.entity.ResponseAdInfo;
import com.kritter.entity.user.userid.ExternalUserId;
import com.kritter.adserving.formatting.macro.AdTagMacroReplace;
import com.kritter.constants.CreativeFormat;
import com.kritter.constants.ExternalUserIdType;
import com.kritter.serving.demand.cache.AdEntityCache;
import com.kritter.serving.demand.cache.CreativeCache;
import com.kritter.serving.demand.cache.CreativeSlotCache;
import com.kritter.serving.demand.entity.AdEntity;
import com.kritter.serving.demand.entity.Creative;
import com.kritter.serving.demand.entity.CreativeSlot;
import com.kritter.utils.common.ApplicationGeneralUtils;
import com.kritter.utils.common.ServerConfig;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import com.kritter.formatterutil.CreativeFormatterUtils;

import java.util.List;
import java.util.Set;

/**
 * This class formats creatives in xml format.
 */
public class XMLFormatter implements CreativesFormatter
{


    private Logger logger;
    private String loggerName;
    private String postImpressionBaseClickUrl;
    private String postImpressionBaseCSCUrl;
    private String cdnBaseImageUrl;
    private String secretKey;
    private int urlVersion;
    private CreativeCache creativeCache;
    private AdEntityCache adEntityCache;
    private CreativeSlotCache creativeSlotCache;
    private String macroPostImpressionBaseClickUrl;

    public XMLFormatter(String loggerName,
                        String secretKey,
                        int urlVersion,
                        CreativeCache creativeCache,
                        AdEntityCache adEntityCache,
                        CreativeSlotCache creativeSlotCache,
                        ServerConfig serverConfig)
    {
        this.loggerName = loggerName;
        this.logger = LogManager.getLogger(loggerName);
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

        FormatCreative fCreative = new FormatCreative(loggerName);

        if(null == response || null == response.getResponseAdInfo())
        {
            logger.error("No Ads shortlisted for this request,nothing to format in formatCreatives of XMLFormatter");
            return null;
        }

        logger.debug("Bidder model id inside formatCreatives of XMLFormatter is : {}", response.getBidderModelId());

        for(ResponseAdInfo responseAdInfo : response.getResponseAdInfo())
        {
            if(null == responseAdInfo)
                continue;

            AdEntity adEntity = adEntityCache.query(responseAdInfo.getAdId());

            if(null== adEntity)
            {
                this.logger.error("The ad entity not found in cache inside formatCreatives() of XMLFormatter,id being: {}", adEntity.getId());
                continue;
            }

            Creative creative = creativeCache.query(adEntity.getCreativeId());
            if(null == creative)
            {
                this.logger.error("The creative entity not found inside formatCreatives() XMLFormatter,id being: {}", adEntity.getCreativeId());
                continue;
            }

            String clickUri = CreativeFormatterUtils.prepareClickUri(this.logger,
                                                                     request,
                                                                     responseAdInfo,
                                                                     response.getBidderModelId(),
                                                                     urlVersion,
                                                                     request.getInventorySource(),
                                                                     response.getSelectedSiteCategoryId(),
                                                                     this.secretKey);

            if(null == clickUri)
                throw new Exception("Click URI could not be formed using different attributes like " +
                                    "handset,location,bids,version,etc. inside formatCreatives of XMLFormatter");

            StringBuffer clickUrl = new StringBuffer(this.postImpressionBaseClickUrl);
            clickUrl.append(clickUri);
            StringBuffer macroClickUrl = new StringBuffer(this.macroPostImpressionBaseClickUrl);
            macroClickUrl.append(clickUri);

            //set common post impression uri to be used in any type of post impression url.
            responseAdInfo.setCommonURIForPostImpression(clickUri);

            StringBuffer cscBeaconUrl = new StringBuffer(this.postImpressionBaseCSCUrl);
            cscBeaconUrl.append(clickUri);

            /**modify csc url to have exchangeId as a parameter for usage in post-impression server************/
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
                                                                                            cscBeaconUrl.toString()
                                                                                          )
                                           );

            logger.debug("CSC url is modified to contain exchange and kritter UserId, after modification url:{} ",
                         cscBeaconUrl.toString());

            /**************************modifying csc url completed********************************************************/

            if(creative.getCreativeFormat().equals(CreativeFormat.BANNER))
            {
                //find height and width of the banner image.
                if(null == responseAdInfo.getCreativeBanner())
                {
                    logger.error("CreativeBanner is null inside XMLFormatter,cannot format ad, for adid: {}", responseAdInfo.getAdId());
                }

                CreativeSlot creativeSlot = creativeSlotCache.query(responseAdInfo.getCreativeBanner().getSlotId());

              
                StringBuffer creativeImageUrl = new StringBuffer(this.cdnBaseImageUrl);
                creativeImageUrl.append(responseAdInfo.getCreativeBanner().getResourceURI());
                List<String> extImpTracker = null;
                if(adEntity.getExtTracker() != null && adEntity.getExtTracker().getImpTracker() != null){
                    extImpTracker = AdTagMacroReplace.adTagMacroReplace(adEntity.getExtTracker().getImpTracker(), request, 
                    		responseAdInfo, response, macroClickUrl.toString(), adEntity.getExtTracker().getImpMacro(), adEntity.getExtTracker().getImpMacroQuote());
                }
                fCreative.addBannerEntity(creativeImageUrl.toString(), creativeSlot.getCreativeSlotWidth(), 
                        creativeSlot.getCreativeSlotHeight(),creative.getText(), 
                        clickUrl.toString(), cscBeaconUrl.toString(), extImpTracker);
            }
            else if(creative.getCreativeFormat().equals(CreativeFormat.TEXT))
            {
                fCreative.addTextEntity(creative.getText(), clickUrl.toString(), cscBeaconUrl.toString());
            }
            if(creative.getCreativeFormat().equals(CreativeFormat.RICHMEDIA) &&
               responseAdInfo.isRichMediaAdIsCompatibleForAdserving())
            {
                logger.debug("Richmedia Ad is compatible for this request, serving adId : {}",responseAdInfo.getAdId());
                fCreative.addRichMediaEntity(
                        AdTagMacroReplace.adTagMacroReplace(creative.getHtmlContent(), 
                                creative.getCreative_macro(), request, responseAdInfo, response, macroClickUrl.toString()), 
                        cscBeaconUrl.toString());
            }
        }

        return fCreative.writeXml();
    }
}

package com.kritter.adserving.formatting;

import com.kritter.entity.user.userid.ExternalUserId;
import com.kritter.constants.ExternalUserIdType;
import com.kritter.utils.common.ApplicationGeneralUtils;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kritter.entity.formatter_entity.creative.FormatCreative;
import com.kritter.entity.reqres.entity.Request;
import com.kritter.entity.reqres.entity.Response;
import com.kritter.entity.reqres.entity.ResponseAdInfo;
import com.kritter.constants.CreativeFormat;
import com.kritter.serving.demand.cache.AdEntityCache;
import com.kritter.serving.demand.cache.CreativeCache;
import com.kritter.serving.demand.cache.CreativeSlotCache;
import com.kritter.serving.demand.entity.AdEntity;
import com.kritter.serving.demand.entity.Creative;
import com.kritter.serving.demand.entity.CreativeSlot;
import com.kritter.utils.common.ServerConfig;
import com.kritter.formatterutil.CreativeFormatterUtils;

import java.util.List;
import java.util.Set;

/**
 * This class formats creatives in json format.
 */
public class JSONFormatter implements CreativesFormatter{
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
	public JSONFormatter(
                         String loggerName,
                         String secretKey,
                         int urlVersion,
                         CreativeCache creativeCache,
                         AdEntityCache adEntityCache,
                         CreativeSlotCache creativeSlotCache,
                         ServerConfig serverConfig)
    {
		this.logger = LoggerFactory.getLogger(loggerName);
		this.loggerName = loggerName;
		this.postImpressionBaseClickUrl = serverConfig.getValueForKey(ServerConfig.CLICK_URL_PREFIX);
		this.postImpressionBaseCSCUrl = serverConfig.getValueForKey(ServerConfig.CSC_URL_PREFIX);
		this.cdnBaseImageUrl = serverConfig.getValueForKey(ServerConfig.CDN_URL_PREFIX);
		this.secretKey = secretKey;
		this.urlVersion = urlVersion;
		this.creativeCache = creativeCache;
		this.adEntityCache = adEntityCache;
		this.creativeSlotCache = creativeSlotCache;
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
                if(null == responseAdInfo.getCreativeBanner())
                {
                    logger.error("CreativeBanner is null inside JSONFormatter,cannot format ad, for adid: {}", responseAdInfo.getAdId());
                    continue;
                }

                CreativeSlot creativeSlot = this.creativeSlotCache.query(responseAdInfo.getCreativeBanner().getSlotId());
                List<String> extImpTracker = null;
                if(adEntity.getExtTracker() != null && adEntity.getExtTracker().getImpTracker() != null){
                    extImpTracker = adEntity.getExtTracker().getImpTracker();
                }
                formatBanner(formatCreative, responseAdInfo, creative, creativeSlot, clickUrl.toString(), 
                        cscBeaconUrl.toString(), extImpTracker);

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

    private void formatBanner(FormatCreative formatCreative, ResponseAdInfo responseAdInfo, Creative creative, 
            CreativeSlot creativeSlot, String clickUrl, String cscBeaconUrl, List<String> extImpTracker) throws JSONException
    {
        formatCreative.addBannerEntity(this.cdnBaseImageUrl + responseAdInfo.getCreativeBanner().getResourceURI(), 
                creativeSlot.getCreativeSlotWidth(), creativeSlot.getCreativeSlotHeight(), 
                creative.getText(), clickUrl, cscBeaconUrl, extImpTracker);
	}
}

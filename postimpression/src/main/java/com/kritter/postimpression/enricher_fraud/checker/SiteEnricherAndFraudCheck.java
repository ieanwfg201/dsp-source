package com.kritter.postimpression.enricher_fraud.checker;

import com.kritter.abstraction.cache.utils.exceptions.UnSupportedOperationException;
import com.kritter.common.site.entity.Site;
import com.kritter.common.site.cache.SiteCache;
import com.kritter.common.site.entity.SiteIncIdSecondaryKey;
import com.kritter.core.workflow.Context;
import com.kritter.postimpression.entity.Request;
import com.kritter.postimpression.enricher_fraud.checker.OnlineFraudUtils.ONLINE_FRAUD_REASON;
import com.kritter.constants.StatusIdEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * This class checks whether the site is fit for the system to consider the post
 * impression event.It could happen that at the time of serving the event from
 * adserver the site was fit but in the meantime post impression events came the
 * site was marked as unfit.
 * 
 */
public class SiteEnricherAndFraudCheck implements OnlineEnricherAndFraudCheck
{
	private String signature;
    private Logger logger;
    private String postImpressionRequestObjectKey;
	private SiteCache siteCache;

	public SiteEnricherAndFraudCheck(String signature,
                                     String loggerName,
                                     String postImpressionRequestObjectKey,
                                     SiteCache siteCache)
    {
		this.signature = signature;
        this.logger = LoggerFactory.getLogger(loggerName);
        this.postImpressionRequestObjectKey = postImpressionRequestObjectKey;
		this.siteCache = siteCache;
	}

	@Override
	public String getIdentifier()
    {
		return this.signature;
	}

	@Override
	public ONLINE_FRAUD_REASON fetchFraudReason(Context context,boolean applyFraudCheck)
    {
        Request request = (Request)context.getValue(this.postImpressionRequestObjectKey);
        if(null==request.getSiteId())
            return ONLINE_FRAUD_REASON.SITE_ID_MISSING_FROM_REQUEST;

        Site site = null;
        try
        {
            Set<String> siteGuidSet = siteCache.query(new SiteIncIdSecondaryKey(request.getSiteId()));

            for(String siteGuid : siteGuidSet)
            {
                site = siteCache.query(siteGuid);
            }
        }
        catch (UnSupportedOperationException unsope)
        {
            return ONLINE_FRAUD_REASON.SITE_ID_NOT_PRESENT_IN_CACHE;
        }

        request.setDetectedSite(site);

        if(applyFraudCheck)
        {
            if(null==site)
                return ONLINE_FRAUD_REASON.SITE_ID_NOT_PRESENT_IN_CACHE;

            if(!(site.getStatus().intValue() == StatusIdEnum.Active.getCode()))
                return ONLINE_FRAUD_REASON.SITE_UNFIT;
        }

		return ONLINE_FRAUD_REASON.HEALTHY_REQUEST;
	}
}
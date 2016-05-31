package com.kritter.adserving.flow.job;

import com.kritter.entity.reqres.entity.Request;
import com.kritter.entity.reqres.entity.Response;
import com.kritter.entity.reqres.entity.ResponseAdInfo;
import com.kritter.entity.reqres.log.ReqLog;
import com.kritter.core.workflow.Context;
import com.kritter.core.workflow.Job;
import com.kritter.serving.demand.cache.AdEntityCache;
import com.kritter.serving.demand.cache.CampaignCache;
import com.kritter.serving.demand.cache.CreativeCache;
import com.kritter.serving.demand.entity.AdEntity;
import com.kritter.serving.demand.entity.Campaign;
import com.kritter.serving.demand.entity.Creative;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * This class matches supply and demand.
 * By matching means :
 * 1. Targeting match on basis of handset,location,content,etc.
 * 2. Supply side filters.
 * 3. Demand side constraints validation(like budget or publisher/site exclusion
 *    as well as creative level filtering/matching like on ad text or banner type).
 *
 * This job should not have any internal logic or workflow wherein ranking ,ctr,
 * or any other relevance is performed, this job should only perform intersection
 * between supply and demand and not judge the performance between supply and demand.
 *
 * The output of this job is adids on which relevance/ranking could be performed and
 * given out for formatting.
 */
public class SupplyDemandMatcherJob implements Job
{
    private Logger logger;
    private String name;
    private String requestObjectKey;
    private String responseObjectKey;
    private String shortlistedAdKey;
    private String selectedSiteCategoryIdKey;
    private CreativeCache creativeCache;
    private AdEntityCache adEntityCache;
    private CampaignCache campaignCache;

    public SupplyDemandMatcherJob(String loggerName,
                                  String jobName,
                                  String requestObjectKey,
                                  String responseObjectKey,
                                  String shortlistedAdKey,
                                  String selectedSiteCategoryIdKey,
                                  CreativeCache creativeCache,
                                  AdEntityCache adEntityCache,
                                  CampaignCache campaignCache)
    {
        this.logger = LoggerFactory.getLogger(loggerName);
        this.name = jobName;
        this.requestObjectKey = requestObjectKey;
        this.responseObjectKey = responseObjectKey;
        this.shortlistedAdKey = shortlistedAdKey;
        this.selectedSiteCategoryIdKey = selectedSiteCategoryIdKey;
        this.creativeCache = creativeCache;
        this.adEntityCache = adEntityCache;
        this.campaignCache = campaignCache;
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    @Override
    public void execute(Context context)
    {
        Request request = (Request)context.getValue(this.requestObjectKey);

        try
        {
            this.logger.info("Inside execute() of SupplyDemandMatcherJob.Going to shortlist campaigns for request.");

            StringBuilder errorMessage = new StringBuilder();

            if(null == request)
            {
                errorMessage.append("Request object is null inside SupplyDemandMatcherJob, cannot continue. Skipping further workflow in this job.");

                this.logger.error(errorMessage.toString());
            }
            else if(null != request.getRequestEnrichmentErrorCode())
            {
                ReqLog.errorWithDebug(logger, request, "Request object is null or the request parameters are not enough to proceed inside SupplyDemandMatcherJob, cannot continue. Skipping further workflow in this job.");
                logger.debug("Reason for request being invalid is errorCode: {}",request.getRequestEnrichmentErrorCode());
            }
            else
            {
                Response response = new Response();

                /**
                 * Fetch shortlisted ad ids from ad targeting matcher then set
                 * response ad infos to be used in later jobs for creative
                 * filtering and floor matching, finally to be used for
                 * formatting.
                 */
                Set<Integer> shortlistedAdIds = (Set<Integer>) context.getValue(this.shortlistedAdKey);

                /* shortlistedAdGuidSet for usage later in workflow.
                 * Set this to response object.*/
                Set<String> shortlistedAdGuidSet = new HashSet<String>();

                Set<ResponseAdInfo> responseAdInfos = new HashSet<ResponseAdInfo>();
                Map<String,ResponseAdInfo> responseAdInfoMap = new HashMap<String,ResponseAdInfo>();

                for(Integer adId: shortlistedAdIds)
                {
                    AdEntity adEntity = adEntityCache.query(adId);
                    Campaign campaign = campaignCache.query(adEntity.getCampaignIncId());

                    if(null == adEntity)
                    {
                        logger.error("AdEntity not found in cache,FATAL error!!! for ad id: {}" , adId);
                        continue;
                    }

                    shortlistedAdGuidSet.add(adEntity.getAdGuid());

                    if(null == campaign)
                    {
                        errorMessage.setLength(0);
                        errorMessage.append("FATAL!!! campaign not found for adid: ");
                        errorMessage.append(adEntity.getId());
                        ReqLog.errorWithDebug(logger, request, errorMessage.toString());
                        continue;
                    }

                    Creative creative = creativeCache.query(adEntity.getCreativeId());

                    if(null == creative)
                    {
                        logger.error("Creative null in cache,FATAL error!!! for creative id: {}" , adEntity.getCreativeId());
                        continue;
                    }

                    ResponseAdInfo responseAdInfo = new ResponseAdInfo(
                                                                        adId,adEntity.getBid(),
                                                                        adEntity.getAdvertiserBid(),
                                                                        campaign.getDailyBudgetRemaining()
                                                                      );

                    responseAdInfo.setMarketPlace(adEntity.getMarketPlace());
                    responseAdInfo.setCampaignId(adEntity.getCampaignIncId());
                    responseAdInfo.setDemandtype(adEntity.getDemandtype());
                    responseAdInfo.setGuid(adEntity.getAdGuid());
                    responseAdInfo.setAdvertiserGuid(adEntity.getAccountGuid());
                    responseAdInfo.setCreative(creative);
                    responseAdInfos.add(responseAdInfo);
                    responseAdInfoMap.put(adEntity.getAdGuid(),responseAdInfo);
                }

                //set response ad info set to response object.
                response.setResponseAdInfo(responseAdInfos);
                response.setResponseAdInfoAgainstAdGuidMap(responseAdInfoMap);

                //set shortlisted ad id set to response object.
                response.setShortlistedAdIdSet(shortlistedAdGuidSet);

                //set selected site category id.
                response.setSelectedSiteCategoryId((Short) context.getValue(this.selectedSiteCategoryIdKey));

                context.setValue(this.responseObjectKey,response);
            }
        }
        catch (Exception e)
        {
            ReqLog.errorWithDebug(logger, request, "Exception inside SupplyDemandMatcherJob while processing the request." ,e );
        }
    }
}

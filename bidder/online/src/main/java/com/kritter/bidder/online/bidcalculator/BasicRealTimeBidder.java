package com.kritter.bidder.online.bidcalculator;

import com.kritter.bidder.online.cache.ServedEntityBidderDataDBCache;
import com.kritter.bidder.online.entity.ExchangeBidWithEntityId;
import com.kritter.bidder.online.entity.BidderDataDBEntity;
import com.kritter.bidder.online.entity.ServedEntityInfo;
import com.kritter.constants.Budget;
import com.kritter.constants.MarketPlace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * This class calculates bid values for all ad units by
 * looking at its marketplace, the final generated value
 * could be seen as ecpm value for all types of ad units
 * be it cpm,cpc,cpd.
 *
 * This bid value should be used for auctioning on the
 * exchanges as an exchange requires us to bid on the
 * value which is per 1000 impressions.(ecpm).
 *
 * Also these values can be used for ranking as such.
 *
 * Algorithm is in resources/docs/basic_real_time_bidder.doc
 */

public class BasicRealTimeBidder implements RealTimeBidder
{
    private Logger logger;
    private ServedEntityBidderDataDBCache servedEntityBidderDataDBCache;
    private int bidderModelId; // Bidder model id to be used
    // Arbitrarily high alpha value.
    public static final double MAX_ALPHA = 10000000.0;
    // default alpha value.
    public static final double DEFAULT_ALPHA = 0.95;

    public BasicRealTimeBidder(
                               String loggerName,
                               ServedEntityBidderDataDBCache servedEntityBidderDataDBCache,
                               int bidderModelId
                              )
    {
        this.logger = LoggerFactory.getLogger(loggerName);
        this.servedEntityBidderDataDBCache = servedEntityBidderDataDBCache;
        this.bidderModelId = bidderModelId;
    }

    /**
     * This function finds out the best serving entities to be given out to exchange for bidding.
     * The result is map with key as entity inc id along with optimized calculated bid value object.
     */
    @Override
    public Map<Integer,ExchangeBidWithEntityId> calculateBidForEntities(Set<ServedEntityInfo> servedEntityInfos)
    {

        Map<Integer,ExchangeBidWithEntityId> bidsToRespond = new HashMap<Integer, ExchangeBidWithEntityId>();

        for(ServedEntityInfo servedEntityInfo : servedEntityInfos)
        {
            double clickProbabilityOfEntityForThisImpression = servedEntityInfo.getCtrProbabilityForEntity();
            double cpaProbabilityForEntityForThisImpression  = servedEntityInfo.getCpaProbabilityForEntity();


            BidderDataDBEntity bidderDBEntity = this.servedEntityBidderDataDBCache.query(this.bidderModelId);

            Double alphaValueForThisAdFromOfflineLPSolver = null;

            if(null != bidderDBEntity)
                alphaValueForThisAdFromOfflineLPSolver = bidderDBEntity.getAlphaValueForDemandEntity(servedEntityInfo.getEntityId());

            logger.debug("Inside BasicRealTimeBidder, alpha value for adId:{} is: {}",
                         servedEntityInfo.getEntityId(),alphaValueForThisAdFromOfflineLPSolver);

            double optimizedBidValueOfAdForThisImpression = servedEntityInfo.getMaxBid();

            if(servedEntityInfo.getMarketPlace().equals(MarketPlace.CPC))
            {
                if(null == alphaValueForThisAdFromOfflineLPSolver)
                    alphaValueForThisAdFromOfflineLPSolver = DEFAULT_ALPHA;

                optimizedBidValueOfAdForThisImpression = (
                                                          optimizedBidValueOfAdForThisImpression    *
                                                          clickProbabilityOfEntityForThisImpression *
                                                          1000
                                                         );
            }
            else if(servedEntityInfo.getMarketPlace().equals(MarketPlace.CPM))
            {
                if(null == alphaValueForThisAdFromOfflineLPSolver)
                    alphaValueForThisAdFromOfflineLPSolver = DEFAULT_ALPHA;
            }
            else if(servedEntityInfo.getMarketPlace().equals(MarketPlace.CPD))
            {
                if(null == alphaValueForThisAdFromOfflineLPSolver)
                    alphaValueForThisAdFromOfflineLPSolver = MAX_ALPHA;

                optimizedBidValueOfAdForThisImpression = (
                                                          optimizedBidValueOfAdForThisImpression    *
                                                          clickProbabilityOfEntityForThisImpression *
                                                          cpaProbabilityForEntityForThisImpression  *
                                                          1000
                                                         );
            }

            double alphaFactor = 1 - alphaValueForThisAdFromOfflineLPSolver;

            optimizedBidValueOfAdForThisImpression = optimizedBidValueOfAdForThisImpression * alphaFactor;

            if(optimizedBidValueOfAdForThisImpression <= 0.0)
            {
                logger.error("The optimized bid calculated inside BasicRealTimeBidder is negative: {} ,for entity id:{} ", optimizedBidValueOfAdForThisImpression, servedEntityInfo.getEntityId());
                continue;
            }


            if(logger.isDebugEnabled())
                logger.debug("Optimized bid value calculated inside BasicRealTimeBidder as : {}", optimizedBidValueOfAdForThisImpression);

            //check for budget remaining of the entity, if less , then ad cant participate in auction.
            if(
               (servedEntityInfo.getDailyRemainingBudget() - optimizedBidValueOfAdForThisImpression) <
               (Budget.min_budget)
              )
            {
                logger.error("ServedEntityInfo's budget is less than minimum value inside BasicRealTimeBidder for adid : {} ", servedEntityInfo.getEntityId());
                continue;
            }

            ExchangeBidWithEntityId exchangeBidWithAdId =
                    new ExchangeBidWithEntityId(
                                                servedEntityInfo.getEntityId(),
                                                optimizedBidValueOfAdForThisImpression
                                               );

            bidsToRespond.put(exchangeBidWithAdId.getEntityId(), exchangeBidWithAdId);

        }

        return bidsToRespond;
    }
}

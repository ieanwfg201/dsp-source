package com.kritter.adserving.ad_rank_select.top_ecpm;

import com.kritter.adserving.adrankselect.common.AdRankingSelection;
import com.kritter.entity.reqres.entity.Request;
import com.kritter.entity.reqres.entity.Response;
import com.kritter.entity.reqres.entity.ResponseAdInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * This class picks adunits based on their ecpm values in decreasing
 * order.
 */
public class TopEcpmSelection implements AdRankingSelection
{
    private Logger logger;
    private int numberOfAdsToSelect;

    public TopEcpmSelection(String loggerName,int numberOfAdsToSelect)
    {
        this.logger = LoggerFactory.getLogger(loggerName);
        this.numberOfAdsToSelect = numberOfAdsToSelect;
    }

    /**
     * This function picks up adunits by sorting them on their ecpm values.
     * @param request
     * @param response
     * @return
     */
    @Override
    public ResponseAdInfo[] rankAdUnitsInDecreasingOrder(Request request, Response response)
    {
        logger.info("Inside rankAdUnitsInDecreasingOrder of TopEcpmSelection");
        Set<ResponseAdInfo> responseAdInfos = response.getResponseAdInfo();

        List<ResponseAdInfo> responseAdInfoList = new ArrayList<ResponseAdInfo>(responseAdInfos);

        Collections.sort(responseAdInfoList,new EcpmValueComparator());

        int numberOfAdsRequired = numberOfAdsToSelect;

        if(responseAdInfoList.size() < numberOfAdsRequired)
            numberOfAdsRequired = responseAdInfoList.size();

        //check if requested number of ads are non zero and have less value than configured value.
        if(request.getRequestedNumberOfAds() != 0 && request.getRequestedNumberOfAds() < numberOfAdsRequired)
            numberOfAdsRequired = request.getRequestedNumberOfAds();

        ResponseAdInfo[] responseAdInfoResult = new ResponseAdInfo[numberOfAdsRequired];

        for(int i = 0; i < numberOfAdsRequired; i ++)
        {
            ResponseAdInfo responseAdInfo = responseAdInfoList.get(i);

            this.logger.debug("Ad unit selected inside TopEcpmSelection with id : {}", responseAdInfo.getAdId());

            responseAdInfoResult[i] = responseAdInfo;
        }

        return responseAdInfoResult;
    }

    private static class EcpmValueComparator implements Comparator<ResponseAdInfo>
    {
        @Override
        public int compare(
                           ResponseAdInfo responseAdInfoFirst,
                           ResponseAdInfo responseAdInfoSecond
                          )
        {

            if(responseAdInfoFirst.getEcpmValue() > responseAdInfoSecond.getEcpmValue())
                return -1;
            else if(responseAdInfoFirst.getEcpmValue() < responseAdInfoSecond.getEcpmValue())
                return 1;
            else return 0;
        }
    }
}

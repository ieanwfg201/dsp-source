package com.kritter.billing.ad_budget.event_counter;

import com.kritter.postimpression.thrift.struct.PostImpressionEvent;
import com.kritter.postimpression.thrift.struct.PostImpressionRequestResponse;
import com.kritter.postimpression.thrift.struct.PostImpressionTerminationReason;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdImpressionCounter implements EventCounter {
    private static final Logger logger = LoggerFactory.getLogger(EventCounter.class);

    /***
     * Take a list of post impression request response objects and the event type to count.
     * Returns a map containing mapping from ad inc id to number of events of the aforementioned type present in the
     * list.
     * @param postImpressionRequestResponses list of post impression request response objects corresponding to post
     *                                       impression log lines.
     * @param eventType Type of event whose occurrences to count
     * @return map containing ad inc id to count mapping.
     */
    public Map<Integer, Integer> countEvents(List<PostImpressionRequestResponse> postImpressionRequestResponses,
                                                    PostImpressionEvent eventType) {
        if(postImpressionRequestResponses == null)
            return null;

        Map<Integer, Integer> adIdEventCountMap = new HashMap<>();
        for(PostImpressionRequestResponse postImpressionRequestResponse : postImpressionRequestResponses) {
            if(postImpressionRequestResponse.getStatus() == PostImpressionTerminationReason.HEALTHY_REQUEST) {
                if(postImpressionRequestResponse.getEvent() == eventType) {
                    // Increment the count for this ad in the map by 1. If not exists in the map, insert entry
                    int adId = postImpressionRequestResponse.getAdId();
                    int value = 0;
                    if(adIdEventCountMap.containsKey(adId)) {
                        value = adIdEventCountMap.get(adId);
                    }
                    adIdEventCountMap.put(adId, value + 1);
                    logger.debug("Event count for ad id : {} is {}", adId, value + 1);
                }
            }
        }

        return adIdEventCountMap;
    }
}

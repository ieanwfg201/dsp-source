package com.kritter.billing.ad_budget.event_counter;

import com.kritter.postimpression.thrift.struct.PostImpressionEvent;
import com.kritter.postimpression.thrift.struct.PostImpressionRequestResponse;
import com.kritter.postimpression.thrift.struct.PostImpressionTerminationReason;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface EventCounter {
    /***
     * Take a list of post impression request response objects and the event type to count.
     * Returns a map containing mapping from entity id to number of events of the aforementioned type present in the
     * list.
     * @param postImpressionRequestResponses list of post impression request response objects corresponding to post
     *                                       impression log lines.
     * @param eventType Type of event whose occurrences to count
     * @return map containing entity id to count mapping.
     */
    public Map<Integer, Integer> countEvents(List<PostImpressionRequestResponse> postImpressionRequestResponses,
                                            PostImpressionEvent eventType);
}

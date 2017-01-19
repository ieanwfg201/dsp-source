package com.kritter.postimpression.urlreader;

import com.kritter.core.workflow.Context;
import com.kritter.entity.postimpression.entity.Request;

/**
 * This interface represents implementations for different kinds of post impression event
 * url readers.
 *
 * Basic url formats are :
 *
 * tracking url -> http://techhinge.com/trk/event-id/third-party-id/md5Hash?params.
 *
 * third-party-click-url -> http://techhinge.com/tclk/uuid-value.clk
 *
 *
 */

public interface PostImpressionEventUrlReader {

    /**
     * Gets the name of the url reader.
     */
    public String getName();

    /**
     * This method reads requesting url's uri and request parameters.
     * Extracts it into request object to be used everywhere in workflow.
     */
    public void decipherPostImpressionUrl(Request postImpressionRequest,String requestURI, Context context) throws Exception;

}

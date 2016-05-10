package com.kritter.postimpression.urlreader;

import com.kritter.core.workflow.Context;
import com.kritter.postimpression.entity.Request;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

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

    //constants which define the types of url supported in application.
    public enum POSTIMPRESSION_EVENT_URL_PREFIX {

        TRACKING_URL_FROM_THIRD_PARTY("/trk/"),
        THIRD_PARTY_CLICK_ALIAS_URL("/tclk/"),
        CLICK("/clk/"),
        CSC("/csc/"),
        WIN_NOTIFICATION("/win/"),
        WIN_API_NOTIFICATION("/winapi/"),
        CONVERSION_FEEDBACK("/conv/"),
        INT_EXCHANGE_WIN("/excwin/"),
        COOKIE_BASED_CONV_JS("/cnv_cky"),
        MACRO_CLICK("/macroclk"),
        TEVENT("/tevent"),
        BEVENT("/bevent");

        private String urlIdentifierPrefix;

        private POSTIMPRESSION_EVENT_URL_PREFIX(String urlIdentifierPrefix){
            this.urlIdentifierPrefix = urlIdentifierPrefix;
        }

        public String getUrlIdentifierPrefix(){
            return this.urlIdentifierPrefix;
        }

    }

}

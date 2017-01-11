package com.kritter.dpa.vserv;

import lombok.Getter;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Map;

/**
 * This class holds response from Vserv as demand partner.
 */
public class VserveResponseEntity
{
    @Getter
    private String statusCode;
    @Getter
    private String adMarkup;
    @Getter
    private boolean isNoFill;

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final TypeReference<Map<String,String>>
                                                TYPE_REFERENCE = new TypeReference<Map<String, String>>() {};
    private static final String PARAM_STATUS = "status";
    private static final String PARAM_AD_MARKUP = "ad";

    private static final String SUCCESS = "000";
    private static final String NO_FILL = "001";
    private static final String ADSPOT_KEY_MISSING = "M001";
    private static final String USER_AGENT_MISSING = "M002";
    private static final String SOURCE_MISSING = "M003";


    public VserveResponseEntity(String httpResponsePayload,Logger logger)
    {

        Map<String,String> responseMap = null;

        try
        {
            responseMap = objectMapper.readValue(httpResponsePayload,TYPE_REFERENCE);
            if(null != responseMap)
            {
                this.statusCode = responseMap.get(PARAM_STATUS);
                this.adMarkup = responseMap.get(PARAM_AD_MARKUP);
                if(this.statusCode.equals(SUCCESS))
                    isNoFill = false;
                else if(this.statusCode.equals(NO_FILL))
                    isNoFill = true;

            }
        }
        catch (IOException ioe)
        {
            logger.error("IOException inside VserveResponseEntity ", ioe);
            isNoFill = true;
        }


        if(null == statusCode || null == adMarkup)
        {
            logger.error("Status or Admarkup from Vserv are null...");
            isNoFill = true;
        }
    }

    public int getHttpStatusCode(Logger logger)
    {
        if(this.statusCode.equals(SUCCESS) || this.statusCode.equals(NO_FILL))
        {
            logger.debug("Response is successful from Vserv, code being: {} ", this.statusCode);
            return 200;
        }

        if(
           this.statusCode.equals(ADSPOT_KEY_MISSING) ||
           this.statusCode.equals(USER_AGENT_MISSING) ||
           this.statusCode.equals(SOURCE_MISSING)
          )
        {
            logger.error("Response is error from Vserv, code being: {} ", this.statusCode);
            return 404;
        }

        return 404;
    }
}
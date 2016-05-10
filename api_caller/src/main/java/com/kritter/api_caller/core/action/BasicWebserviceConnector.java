package com.kritter.api_caller.core.action;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;


/**
 * This class has basic use of calling any thrid party webserver.
 * This uses connection pooling at TCP level.
 */

public class BasicWebserviceConnector {

    private Logger logger;
    private static final String POST_METHOD = "POST";
    private static final Boolean DO_OUTPUT = true;
    private int connectTimeOut;
    private int readTimeOut;

    public BasicWebserviceConnector(String loggerName, int maxConnectionsPerRoute,
                                    int connectTimeOut, int readTimeOut) {

        this.logger = LoggerFactory.getLogger(loggerName);
        System.setProperty("http.keepAlive","true");
        System.setProperty("http.maxConnections", String.valueOf(maxConnectionsPerRoute));
        this.connectTimeOut = connectTimeOut;
        this.readTimeOut = readTimeOut;
    }

    public String fetchResponseFromThirdPartyServer(String bidderURL, Map<String,String> headerValuesToSet,
                                                    String postBodyPayload, String contentMimeType){

        String apiResponse = null;

        if(null == bidderURL)
        {
            logger.error("The requested bidder url is null, cannot proceed inside fetchResponseFromThirdPartyServer()" +
                    " of BasicWebserviceConnector class");

            return apiResponse;
        }

        if(logger.isDebugEnabled())
            logger.debug("Bidder Url used as: " + bidderURL);

        HttpURLConnection httpURLConnection = null;
        InputStream instream = null;

        try
        {
            URL url = new URL(bidderURL);
            //Since keep alive is true, get connection from tcp connection pool.
            httpURLConnection = (HttpURLConnection)url.openConnection();

            httpURLConnection.setRequestMethod(POST_METHOD);

            //set readtimeout and connect timeout.
            httpURLConnection.setConnectTimeout(this.connectTimeOut);
            httpURLConnection.setReadTimeout(this.readTimeOut);

            if(null != headerValuesToSet)
            {
                for(Map.Entry<String,String> entry : headerValuesToSet.entrySet())
                {
                    String key = entry.getKey();
                    String value = entry.getValue();

                    if(logger.isDebugEnabled())
                        logger.debug("Setting header inside fetchResponseFromThirdPartyServer() of " +
                                "BasicWebserviceConnector, key and value: " + key + " : " + value);

                    httpURLConnection.setRequestProperty(key, value);
                }
            }

            //send post body data
            httpURLConnection.setDoOutput(DO_OUTPUT);

            DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());
            wr.writeBytes(postBodyPayload);
            wr.flush();
            wr.close();

            //get response
            StringWriter stringWriter = new StringWriter();

            instream = httpURLConnection.getInputStream();

            //going for the standard encoding used.
            IOUtils.copy(instream, stringWriter, contentMimeType);

            if(!stringWriter.toString().isEmpty())
                apiResponse = stringWriter.toString();

        }
        catch (Exception e)
        {
            logger.error("Exception in http connector ",e);
        }
        finally
        {
            if(null != instream)
            {
                //This will trigger system resources cleanup, connection may/maynot get closed.
                try
                {
                    instream.close();
                }
                catch(IOException ioe)
                {
                    logger.error("IOException in Http connector in closing input stream",ioe);
                }
            }    
            //Return connection to idle pool, jdk internally cleans up idle connections
            //This is must so that there are no CLOSE_WAIT scenarios.
            if(null != httpURLConnection)
                httpURLConnection.disconnect();
        }

        return apiResponse;
    }


    /**
     * This function is called when client is no longer needed at all.
     * Most probably at the time of application shutdown.
     * @throws Exception
     */
    public void releaseResources()
    {

    }
}

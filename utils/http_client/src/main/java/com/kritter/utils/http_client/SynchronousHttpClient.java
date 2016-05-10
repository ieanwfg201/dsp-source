package com.kritter.utils.http_client;

import com.kritter.utils.http_client.entity.HttpRequest;
import com.kritter.utils.http_client.entity.HttpResponse;
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
 * This class is used for making synchronous http calls to a third party
 * server. At the construction or initialization time of this class
 * maxConnectionsPerRoute , connectTimeOut, readTimeOut have to be
 * specified.
 * Default or example values could be:
 * a. maxConnectionsPerRoute : 20.
 * b. connectTimeOut : 100 (time in milliseconds, if used in applications like SSP)
 * c. readTimeOut : 50 (time in milliseconds, if used in applications like SSP)
 */
public class SynchronousHttpClient
{
    private Logger logger;

    private static final String UTF_ENCODING = "UTF-8";
    private static final Boolean DO_OUTPUT = true;
    private static final String KEEPALIVE_HTTP_SYSTEM_PROPERTY = "http.keepAlive";
    private static final String MAXCONNECTIONS_HTTP_SYSTEM_PROPERTY = "http.maxConnections";

    public SynchronousHttpClient(
                                 String loggerName,
                                 int maxConnectionsPerRoute
                                )
    {
        /*Set system properties to keep alive connections and enable pooling,define maxConnectionsPerRoute as well.*/
        System.setProperty(KEEPALIVE_HTTP_SYSTEM_PROPERTY     ,"true");
        System.setProperty(MAXCONNECTIONS_HTTP_SYSTEM_PROPERTY,String.valueOf(maxConnectionsPerRoute));

        this.logger = LoggerFactory.getLogger(loggerName);
    }

    public HttpResponse fetchResponseFromThirdPartyServer(HttpRequest httpRequest)
    {
        HttpResponse apiResponse = null;

        if(null == httpRequest.getServerURL())
        {
            logger.error("The requested server url is null inside SynchronousHttpClient, cannot proceed...");
            return apiResponse;
        }

        logger.debug("Server URL to be used for http call is: {} ", httpRequest.getServerURL());

        HttpURLConnection httpURLConnection = null;
        InputStream inputStream = null;
        String responseFromThirdPartyserver = null;
        int responseCode = 404;
        String responseContentType = null;

        try
        {
            URL url = new URL(httpRequest.getServerURL());
            //Since keep alive is true, get connection from tcp connection pool.
            httpURLConnection = (HttpURLConnection)url.openConnection();

            httpURLConnection.setRequestMethod(httpRequest.getRequestMethod().getMethod());

            //set readtimeout and connect timeout.
            httpURLConnection.setConnectTimeout(httpRequest.getConnectTimeOut());
            httpURLConnection.setReadTimeout(httpRequest.getReadTimeOut());

            if(null != httpRequest.getHeaderValuesToSet())
            {
                for(Map.Entry<String,String> entry : httpRequest.getHeaderValuesToSet().entrySet())
                {
                    String key = entry.getKey();
                    String value = entry.getValue();

                    logger.debug("Setting header inside fetchResponseFromBidder() of ApacheCommonsHttpConnector class key and value: {} : {}", key, value);

                    httpURLConnection.setRequestProperty(key, value);
                }
            }

            //send post body data
            httpURLConnection.setDoOutput(DO_OUTPUT);

            if(null != httpRequest.getPostBodyPayload())
            {
                DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());
                wr.writeBytes(httpRequest.getPostBodyPayload());
                wr.flush();
                wr.close();
            }

            //get response
            StringWriter stringWriter = new StringWriter();

            inputStream = httpURLConnection.getInputStream();

            //going for the standard encoding used.
            IOUtils.copy(inputStream, stringWriter, UTF_ENCODING);


            if(!stringWriter.toString().isEmpty())
                responseFromThirdPartyserver = stringWriter.toString();

            responseCode = httpURLConnection.getResponseCode();
            responseContentType = httpURLConnection.getContentType();
        }
        catch (Exception e)
        {
            if(null != httpURLConnection)
            {
                try
                {
                    responseCode = httpURLConnection.getResponseCode();
                    responseFromThirdPartyserver = null;
                }
                catch (IOException ioe)
                {
                    logger.debug("IOException inside SynchronousHttpClient , while fetching response code", ioe);
                }
            }

            logger.error("Exception inside SynchronousHttpClient ",e);
        }
        finally
        {
            if(null != inputStream)
            {
                //This will trigger system resources cleanup, connection may/maynot get closed.
                try
                {
                    inputStream.close();
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

        apiResponse = new HttpResponse(responseCode,responseFromThirdPartyserver,responseContentType);
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

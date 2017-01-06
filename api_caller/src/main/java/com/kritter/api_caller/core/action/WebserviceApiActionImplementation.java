package com.kritter.api_caller.core.action;

import com.kritter.api_caller.core.model.WebServiceRequestEntity;
import com.kritter.api_caller.core.model.WebServiceResponseEntity;
import com.kritter.utils.http_client.SynchronousHttpClient;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * This class implements WebserviceApiAction interface, so as to provide ability
 * to execute a remote call to some webserver and fetch contents in response.
 * This is a simple implementation wherein it uses a synchronous http client
 * to make call over http protocol (https would require oauth additionally).
 */
public class WebserviceApiActionImplementation implements WebserviceApiAction
{
    private SynchronousHttpClient synchronousHttpClient;
    private Logger logger;

    public WebserviceApiActionImplementation(String loggerName,
                                             int maxConnectionsPerRoute)
    {
        this.synchronousHttpClient = new SynchronousHttpClient(loggerName,maxConnectionsPerRoute);
        this.logger = LogManager.getLogger(loggerName);
    }

    @Override
    public WebServiceResponseEntity fetchApiResponseFromRemoteWebServer(WebServiceRequestEntity webServiceRequestEntity)
    {
        return (WebServiceResponseEntity)this.synchronousHttpClient.fetchResponseFromThirdPartyServer(webServiceRequestEntity);
    }
}

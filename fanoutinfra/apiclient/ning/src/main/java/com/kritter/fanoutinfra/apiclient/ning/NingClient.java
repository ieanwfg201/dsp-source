package com.kritter.fanoutinfra.apiclient.ning;

import java.net.URI;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.kritter.fanoutinfra.apiclient.common.KHttpResponse;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import com.kritter.fanoutinfra.apiclient.common.KHttpClient;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClientConfig;
import com.ning.http.client.Request;

public class NingClient implements KHttpClient {
    private static AsyncHttpClient client;
    private static Logger logger;
    private static String loggerName;
    public NingClient(String loggerName){
        this.logger = LogManager.getLogger(loggerName);
        this.loggerName = loggerName;
    }
    private synchronized AsyncHttpClient getAsyncClient(int requestTimeoutMillis,
            int maxConnectionPerHost, int maxConnection){
    	if(client==null){
            AsyncHttpClientConfig config = new AsyncHttpClientConfig.Builder()
                    .setAllowPoolingConnections(true)
                    .setCompressionEnforced(false)
                    .setMaxConnections(maxConnection)
                    .setMaxConnectionsPerHost(maxConnectionPerHost)
                    .setRequestTimeout(requestTimeoutMillis)
                    .setConnectTimeout(requestTimeoutMillis)
                    .setReadTimeout(requestTimeoutMillis)
                    .build();
            client = new AsyncHttpClient(config);
    	}
        return client;
    }

    private AsyncHttpClient getAsyncClientNonBlocking(int requestTimeoutMillis,
                                                      int maxConnectionPerHost,
                                                      int maxConnection)
    {
        if(client==null)
        {
            synchronized (this)
            {
                AsyncHttpClientConfig config = new AsyncHttpClientConfig.Builder()
                        .setAllowPoolingConnections(true)
                        .setCompressionEnforced(false)
                        .setMaxConnections(maxConnection)
                        .setMaxConnectionsPerHost(maxConnectionPerHost)
                        .setRequestTimeout(requestTimeoutMillis)
                        .setConnectTimeout(requestTimeoutMillis)
                        .setReadTimeout(requestTimeoutMillis)
                        .build();
                client = new AsyncHttpClient(config);
            }
        }

        return client;
    }

    @Override
    public void init() throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    public void shutdown() throws Exception {
        if (client != null) {
            client.close();
        }
    }

    @Override
    public String post(URI target, byte[] content, int requestTimeoutMillis,
            int maxConnectionPerHost, int maxConnection) throws Exception {
        String returnStr = null;
        getAsyncClient(requestTimeoutMillis, maxConnectionPerHost, maxConnection);
        Request request;
        request = this.client.preparePost(target.toASCIIString())
                .setBody(content).setRequestTimeout(requestTimeoutMillis)
                .build();
        try {
            Future<String> f = this.client.executeRequest(request, new NingAsyncHandler(this.loggerName));
            returnStr = f.get(requestTimeoutMillis,TimeUnit.MILLISECONDS);
            return returnStr;
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            return returnStr;
        }finally{
            this.client.close();
        }        
    }
    
	@Override
	public String get(URI target, int requestTimeoutMillis, int maxConnectionPerHost, int maxConnection)
			throws Exception {
        String returnStr = null;
        getAsyncClient(requestTimeoutMillis, maxConnectionPerHost, maxConnection);
        Request request;
        request = this.client.prepareGet(target.toASCIIString())
                .build();
        try {
            Future<String> f = this.client.executeRequest(request, new NingAsyncHandler(this.loggerName));
            returnStr = f.get();
            return returnStr;
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            return returnStr;
        }finally{
            this.client.close();
        }        
	}

    @Override
    public void postAsync(URI target, byte[] content, int requestTimeoutMillis,
                       int maxConnectionPerHost, int maxConnection) throws Exception {
    	getAsyncClient(requestTimeoutMillis, maxConnectionPerHost, maxConnection);
    	Request request;
        request = this.client.preparePost(target.toASCIIString())
                .setBody(content)
                .build();
        this.client.executeRequest(request, new NingAsyncHandler(this.loggerName));
    }

    @Override
    public void getAsync(URI target, int requestTimeoutMillis, int maxConnectionPerHost, int maxConnection)
            throws Exception {
    	getAsyncClient(requestTimeoutMillis, maxConnectionPerHost, maxConnection);
    	Request request;
        request = this.client.prepareGet(target.toASCIIString())
                .build();
        this.client.executeRequest(request, new NingAsyncHandler(this.loggerName));
    }

    @Override
    public KHttpResponse queryNonBlocking(URI target, byte[] content, int requestTimeoutMillis,
                                         int maxConnectionPerHost, int maxConnection,
                                         REQUEST_METHOD requestMethod) throws Exception
    {
        int statusCode = 200;
        String returnStr = null;
        getAsyncClientNonBlocking(requestTimeoutMillis, maxConnectionPerHost, maxConnection);

        Request request = null;
        if(null == requestMethod || requestMethod.getMethod().equalsIgnoreCase(REQUEST_METHOD.POST_METHOD.getMethod()))
        {
            request = this.client.preparePost(target.toASCIIString())
                    .setBody(content).setRequestTimeout(requestTimeoutMillis)
                    .build();
        }
        else if(requestMethod.getMethod().equalsIgnoreCase(REQUEST_METHOD.GET_METHOD.getMethod()))
        {
            request = this.client.prepareGet(target.toASCIIString())
                    .setRequestTimeout(requestTimeoutMillis)
                    .build();
        }

        try
        {
            if(null == request)
            {
                KHttpResponse kHttpResponse = new KHttpResponse();
                kHttpResponse.setResponseStatusCode(504);
                kHttpResponse.setResponsePayload(null);
                return kHttpResponse;
            }

            NingAsyncHandler ningAsyncHandler = new NingAsyncHandler(loggerName);
            Future<String> f = this.client.executeRequest(request, ningAsyncHandler);
            returnStr = f.get(requestTimeoutMillis,TimeUnit.MILLISECONDS);
            statusCode = ningAsyncHandler.getStatus();
            KHttpResponse kHttpResponse = new KHttpResponse();
            kHttpResponse.setResponseStatusCode(statusCode);
            kHttpResponse.setResponsePayload(returnStr);
            return kHttpResponse;
        }
        catch (InterruptedException ine)
        {
            logger.error(ine.getMessage(),ine);
            KHttpResponse kHttpResponse = new KHttpResponse();
            kHttpResponse.setResponseStatusCode(504);
            kHttpResponse.setResponsePayload(null);
            return kHttpResponse;
        }
        catch (TimeoutException te)
        {
            logger.error(te.getMessage(),te);
            KHttpResponse kHttpResponse = new KHttpResponse();
            kHttpResponse.setResponseStatusCode(504);
            kHttpResponse.setResponsePayload(null);
            return kHttpResponse;
        }
        catch (ExecutionException exe)
        {
            logger.error(exe.getMessage(),exe);
            KHttpResponse kHttpResponse = new KHttpResponse();
            kHttpResponse.setResponseStatusCode(504);
            kHttpResponse.setResponsePayload(null);
            return kHttpResponse;
        }
        finally
        {
        }
    }

    public static void main(String args[]) throws Exception{
    /*    KHttpClient n = new NingClient("dwkj");
        long c =  new Date().getTime();
        System.out.println(c);
        //for(int i=1;i<100;i++){
        //System.out.println(n.post(new URI("http://ads.kritter.com/impag/ads?"
          //      + "site-id=010a4137-6a5c-6201-521b-7c910200018d&site_guid="
            //    + "010a4137-6a5c-6201-521b-7c910200018d&ua="+
              //  URLEncoder.encode("Mozilla/5.0 (SymbianOS/9.4; Series60/5.0 Nokia5230/21.0.004; Profile/MIDP-2.1 Configuration/CLDC-1.1 ) AppleWebKit/525 (KHTML, like Gecko) Version/3.0 BrowserNG/7.2.5.2 3gpp-gba")+"&ip=122.172.28.22&&fmt=json&ver=s2s_1"), 
                //"dufqw".getBytes(), 10000, 1, 1));
         n.post(new URI("http://localhost/test/50x.html"), "dufqw".getBytes(), 50, 1, 1);
        //}
        long d =  new Date().getTime();
        System.out.println(d);
        System.out.println(d-c);
        
                KHttpClient n = new NingClient("sdqw");
        long c =  new Date().getTime();
        System.out.println(c);
        //for(int i=1;i<100;i++){
         n.get(new URI("http://localhost/50x.html"), 10000, 1, 1);
        //
        long d =  new Date().getTime();
        System.out.println(d);
        System.out.println(d-c);

        
        */
    }
}

package com.kritter.fanoutinfra.apiclient.ning;

import java.net.URI;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kritter.fanoutinfra.apiclient.common.KHttpClient;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClientConfig;
import com.ning.http.client.Request;

public class NingClient implements KHttpClient {
    private AsyncHttpClient client;
    private Logger logger;
    private String loggerName;
    public NingClient(String loggerName){
        this.logger = LoggerFactory.getLogger(loggerName);
        this.loggerName = loggerName;
    }
    @Override
    public void init() throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    public void shutdown() throws Exception {
        if (this.client != null) {
            this.client.close();
        }
    }

    @Override
    public String post(URI target, byte[] content, int requestTimeoutMillis,
            int maxConnectionPerHost, int maxConnection) throws Exception {
        String returnStr = null;
        if (this.client != null) {
            this.client.close();
        }
        AsyncHttpClientConfig config = new AsyncHttpClientConfig.Builder()
                .setAllowPoolingConnections(true)
                .setCompressionEnforced(false)
                .setMaxConnections(maxConnection)
                .setMaxConnectionsPerHost(maxConnectionPerHost)
                .setRequestTimeout(requestTimeoutMillis)
                .build();
        this.client = new AsyncHttpClient(config);
        Request request;
        request = this.client.preparePost(target.toASCIIString())
                .setBody(content)
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
	public String get(URI target, int requestTimeoutMillis, int maxConnectionPerHost, int maxConnection)
			throws Exception {
        String returnStr = null;
        if (this.client != null) {
            this.client.close();
        }
        AsyncHttpClientConfig config = new AsyncHttpClientConfig.Builder()
                .setAllowPoolingConnections(true)
                .setCompressionEnforced(false)
                .setMaxConnections(maxConnection)
                .setMaxConnectionsPerHost(maxConnectionPerHost)
                .setRequestTimeout(requestTimeoutMillis)
                .build();
        this.client = new AsyncHttpClient(config);
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

    public static void main(String args[]) throws Exception{
        /*KHttpClient n = new NingClient();
        long c =  new Date().getTime();
        System.out.println(c);
        //for(int i=1;i<100;i++){
        System.out.println(n.post(new URI("http://ads.kritter.com/impag/ads?"
                + "site-id=010a4137-6a5c-6201-521b-7c910200018d&site_guid="
                + "010a4137-6a5c-6201-521b-7c910200018d&ua="+
                URLEncoder.encode("Mozilla/5.0 (SymbianOS/9.4; Series60/5.0 Nokia5230/21.0.004; Profile/MIDP-2.1 Configuration/CLDC-1.1 ) AppleWebKit/525 (KHTML, like Gecko) Version/3.0 BrowserNG/7.2.5.2 3gpp-gba")+"&ip=122.172.28.22&&fmt=json&ver=s2s_1"), 
                "dufqw".getBytes(), 10000, 1, 1));
         //n.post(new URI("http://localhost/50x.html"), "dufqw".getBytes(), 10000, 1, 1);
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

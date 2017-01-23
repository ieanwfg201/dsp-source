package com.kritter.fanoutinfra.executorservice.common;

import java.net.URI;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import com.kritter.fanoutinfra.apiclient.common.KHttpResponse;
import lombok.Getter;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import com.kritter.constants.ExchangeConstants;
import com.kritter.fanoutinfra.apiclient.common.KHttpClient;
import com.kritter.fanoutinfra.apiclient.ning.NingClient;

public class KExecutor {
    private Logger logger;
    private static ExecutorService executorService =  null;
    private static KExecutor kExecutor = null;


    private KExecutor(String loggerName){
        this.logger = LogManager.getLogger(loggerName);
        if(executorService == null){
            //executorService = Executors.newSingleThreadExecutor();
            executorService = Executors.newFixedThreadPool(50);
        }
    }
    public static synchronized KExecutor getKExecutor(String loggerName){
        if(kExecutor == null){
            kExecutor = new KExecutor(loggerName); 
        }
        return kExecutor;
    }

    public static KExecutor getSingletonInstanceNonBlocking(String loggerName) {
        if (null == kExecutor)
        {
            synchronized (KExecutor.class)
            {
                kExecutor = new KExecutor(loggerName);
            }
        }
        return kExecutor;
    }

    public Map<String, String> call(Map<String,URI> dspGuidUrlMap,KHttpClient httpClient,
                                    String content, int requestTimeoutMillis,
                                    int maxConnectionPerHost, int maxConnection) {

        logger.debug("Inside call of KExecutor , going to call dsp URLs");
        try{
            if( dspGuidUrlMap==null || dspGuidUrlMap.size()< 1){
                logger.debug("DSP guid URL map is null or of 0 size.");
                return null;
            }
            
            Set<Callable<String>> callables = new HashSet<Callable<String>>();
            for(String key:dspGuidUrlMap.keySet()){
                logger.debug("URL being called: {} , content sent is :{} ", dspGuidUrlMap.get(key),content);
                callables.add(new NingCallable(dspGuidUrlMap.get(key), content, 
                        requestTimeoutMillis, maxConnectionPerHost, maxConnection, 
                        httpClient,key));
            }
            List<Future<String>> futures = executorService.invokeAll(callables,requestTimeoutMillis,TimeUnit.MILLISECONDS);
            Map<String, String> advResponseMap = new HashMap<String, String>();
            
            for(Future<String> future : futures){
                String futureGetStr = future.get(requestTimeoutMillis,TimeUnit.MILLISECONDS);

                logger.error("Response from DSP is: {} ", futureGetStr);

                if(futureGetStr != null){
                    String strSplit[] = futureGetStr.split(ExchangeConstants.callDelimiter);
                    if(strSplit.length == 1)
                        advResponseMap.put(strSplit[0],"");
                    else if(strSplit.length == 2)
                        advResponseMap.put(strSplit[0], strSplit[1]);
                }
            }
            return advResponseMap;
        }catch(Exception e){
            logger.error(e.getMessage(), e);
            return null;
        }finally{
            
        }
    }

    public Map<String, String> call(Map<String,URI> dspGuidUrlMap,KHttpClient httpClient,
                                    Map<String,String> contentMapForDSP, int requestTimeoutMillis,
                                    int maxConnectionPerHost, int maxConnection) {

        logger.debug("Inside call of KExecutor , going to call dsp URLs");
        try{
            if( dspGuidUrlMap==null || dspGuidUrlMap.size()< 1){
                logger.debug("DSP guid URL map is null or of 0 size.");
                return null;
            }

            Set<Callable<String>> callables = new HashSet<Callable<String>>();
            for(String key:dspGuidUrlMap.keySet()){
                logger.debug("URL being called: {} , content sent is :{} ", dspGuidUrlMap.get(key),contentMapForDSP.get(key));
                callables.add(new NingCallable(dspGuidUrlMap.get(key), contentMapForDSP.get(key),
                        requestTimeoutMillis, maxConnectionPerHost, maxConnection,
                        httpClient,key));
            }
            List<Future<String>> futures = executorService.invokeAll(callables,requestTimeoutMillis,TimeUnit.MILLISECONDS);
            Map<String, String> advResponseMap = new HashMap<String, String>();

            for(Future<String> future : futures){
                String futureGetStr = future.get(requestTimeoutMillis,TimeUnit.MILLISECONDS);

                logger.debug("Response from DSP is: {} ", futureGetStr);

                if(futureGetStr != null){
                    String strSplit[] = futureGetStr.split(ExchangeConstants.callDelimiter);
                    if(strSplit.length == 1)
                        advResponseMap.put(strSplit[0],"");
                    else if(strSplit.length == 2)
                        advResponseMap.put(strSplit[0], strSplit[1]);
                }
            }
            return advResponseMap;
        }catch(Exception e){
            logger.error(e.getMessage(), e);
            return null;
        }finally{

        }
    }

    public Map<String, KHttpResponse> callNonBlocking(Map<String,URI> dspGuidUrlMap,KHttpClient httpClient,
                                                      Map<String,String> contentMapForDSP, int requestTimeoutMillis,
                                                      int maxConnectionPerHost, int maxConnection,
                                                      Map<String,KHttpClient.REQUEST_METHOD> requestMethodPerDsp)
    {
        logger.debug("Inside call of KExecutor , going to call dsp URLs");
        try
        {
            if( dspGuidUrlMap==null || dspGuidUrlMap.size()< 1)
            {
                logger.debug("DSP guid URL map is null or of 0 size.");
                return null;
            }

            /**Use list so that order can be preserved of the callables*/
            List<Callable<KHttpResponse>> callables = new ArrayList<Callable<KHttpResponse>>();

            /**Keep an array of dsp advertiser guids, since the future list is returned exactly in the order as the
             * callables are submitted.*/
            String[] orderedDspGuidArray = null;
            int counter = 0;
            if(null != dspGuidUrlMap && dspGuidUrlMap.keySet().size() > 0)
                orderedDspGuidArray = new String[dspGuidUrlMap.keySet().size()];

            for(String key:dspGuidUrlMap.keySet())
            {
                logger.debug("URL being called: {} , content sent is (if required) :{} ",
                              dspGuidUrlMap.get(key),contentMapForDSP.get(key));

                callables.add(new NingCallableNonBlocking(dspGuidUrlMap.get(key), contentMapForDSP.get(key),
                              requestTimeoutMillis, maxConnectionPerHost, maxConnection,
                              httpClient,key,requestMethodPerDsp.get(key)));
                orderedDspGuidArray[counter ++] = key;
            }

            List<Future<KHttpResponse>> futures = executorService.invokeAll(callables,requestTimeoutMillis,TimeUnit.MILLISECONDS);
            Map<String, KHttpResponse> advResponseMap = new HashMap<String, KHttpResponse>();

            counter = 0;
            for(Future<KHttpResponse> future : futures)
            {
                KHttpResponse kHttpResponse = null;
                String dspGuid = orderedDspGuidArray[counter ++];

                /**In case future crashes with interrupted exception due to timeout, let other DSPs work*/
                try
                {
                    logger.debug("Current dsp guid: {} going to be called for response with max timeout value: {}" ,
                                  dspGuid,requestTimeoutMillis);

                    kHttpResponse = future.get(requestTimeoutMillis, TimeUnit.MILLISECONDS);
                }
                catch (Exception e)
                {
                    logger.error("Interrupted Exception/ Timeout met for dsp: {} ",dspGuid,e);
                }

                String futureGetStr = null;
                if(null != kHttpResponse)
                    futureGetStr = kHttpResponse.getResponsePayload();

                logger.debug("Response from DSP is: {} ", futureGetStr);

                if(futureGetStr != null && null != kHttpResponse && kHttpResponse.getResponseStatusCode() == 200)
                {
                    String strSplit[] = futureGetStr.split(ExchangeConstants.callDelimiter);
                    if(strSplit.length == 1)
                    {
                        kHttpResponse.setResponsePayload("");
                        advResponseMap.put(strSplit[0],kHttpResponse);
                    }
                    else if(strSplit.length == 2)
                    {
                        kHttpResponse.setResponsePayload(strSplit[1]);
                        advResponseMap.put(strSplit[0], kHttpResponse);
                    }
                }
                //ERROR from DSP, as 504 is already noted as timeout, 200/204 as ok, any other means ERROR from DSP.
                else if(null != kHttpResponse                        &&
                        kHttpResponse.getResponseStatusCode() != 200 &&
                        kHttpResponse.getResponseStatusCode() != 204 &&
                        kHttpResponse.getResponseStatusCode() != 504)
                {
                    kHttpResponse.setResponsePayload(null);
                    advResponseMap.put(dspGuid,kHttpResponse);
                }
            }

            /**If no entry made in response map that would mean request simply timed out or future
             * cancelled waiting on the maximum timeout specified.*/
            for(String key : dspGuidUrlMap.keySet())
            {
                if(!advResponseMap.containsKey(key))
                {
                    KHttpResponse kHttpResponse = new KHttpResponse();
                    kHttpResponse.setResponsePayload("");
                    kHttpResponse.setResponseStatusCode(504);
                    advResponseMap.put(key,kHttpResponse);
                }
            }

            return advResponseMap;
        }
        catch(Exception e)
        {
            logger.error(e.getMessage(), e);
            return null;
        }
        finally
        {
        }
    }

    public void call(List<String> urlsTofire,KHttpClient httpClient,
    		int requestTimeoutMillis,
            int maxConnectionPerHost, int maxConnection) {

        logger.debug("Inside call of KExecutor , going to call URLs");
        try{
            if( urlsTofire==null || urlsTofire.size()< 1){
                logger.debug("urlsTofire is null or of 0 size.");
                return ;
            }
            
            Set<Callable<String>> callables = new HashSet<Callable<String>>();
            for(String str:urlsTofire){
            	URI uri = new URI(str);
                logger.debug("URL being called: {} ", uri);
                callables.add(new NingGetUrlCallable(uri,
                        requestTimeoutMillis, maxConnectionPerHost, maxConnection, 
                        httpClient));
            }
            executorService.invokeAll(callables);
        }catch(Exception e){
            logger.error(e.getMessage(), e);
        }finally{
            
        }
    }

    public void callAsync(List<String> urlsTofire,KHttpClient httpClient, int requestTimeoutMillis,
                          int maxConnectionPerHost, int maxConnection) {
        logger.debug("Inside call of KExecutor , going to call URLs");
        try {
            if(urlsTofire==null || urlsTofire.size()< 1) {
                logger.debug("urlsTofire is null or of 0 size.");
                return ;
            }

            Set<Callable<Void>> callables = new HashSet<Callable<Void>>();
            for(String str:urlsTofire) {
                URI uri = new URI(str);
                logger.debug("URL being called: {} ", uri);
                callables.add(new NingGetUrlCallable.AsyncCallable(uri, requestTimeoutMillis, maxConnectionPerHost,
                        maxConnection, httpClient));
            }
            executorService.invokeAll(callables);
        } catch(Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void shutdown(){
        if(executorService != null){
            executorService.shutdown();
        }
    }
    /*public static void main(String args[]) throws Exception{
    long c =  new Date().getTime();
        System.out.println(c);
        KExecutor k = KExecutor.getKExecutor("blah");
        Map<String,URI> dspGuidUrlMap = new HashMap<String, URI>();
        dspGuidUrlMap.put("a",new URI("http://ads.tapsomnia.com/impag?"
                + "site-id=010a4137-6a5c-6201-521b-7c910200018d&site_guid="
                + "010a4137-6a5c-6201-521b-7c910200018d&ua="+
                URLEncoder.encode("Mozilla/5.0 (SymbianOS/9.4; Series60/5.0 Nokia5230/21.0.004; Profile/MIDP-2.1 Configuration/CLDC-1.1 ) AppleWebKit/525 (KHTML, like Gecko) Version/3.0 BrowserNG/7.2.5.2 3gpp-gba")+"&ip=106.51.242.133&&fmt=xhtml&ver=s2s_1"));
        k.call(dspGuidUrlMap, new NingClient("blah"), "kjdqwj", 10000, 10000, 10000);
        k.shutdown();
        long d =  new Date().getTime();
        System.out.println(d);
        System.out.println(d-c);
        
    }
    */
    /*public static void main(String args[]) throws Exception{
    long d = System.currentTimeMillis();
        KExecutor k = KExecutor.getKExecutor("blah");
        List<String> ll = new LinkedList<String>();
        ll.add("http://localhost/test/50x.html");
        k.call(ll, new NingClient("blah"), 10000, 10000, 10000);
        System.out.println(System.currentTimeMillis()-d);
        k.shutdown();
        
    }*/
	
}

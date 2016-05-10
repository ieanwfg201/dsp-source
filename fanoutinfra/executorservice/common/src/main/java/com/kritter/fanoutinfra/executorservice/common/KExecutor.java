package com.kritter.fanoutinfra.executorservice.common;

import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kritter.constants.ExchangeConstants;
import com.kritter.fanoutinfra.apiclient.common.KHttpClient;

public class KExecutor {
    private Logger logger;
    private static ExecutorService executorService =  null;
    private static KExecutor kExecutor = null;
    private KExecutor(String loggerName){
        this.logger = LoggerFactory.getLogger(loggerName);
        if(executorService == null){
            executorService = Executors.newSingleThreadExecutor();
        }
    }
    public static synchronized KExecutor getKExecutor(String loggerName){
        if(kExecutor == null){
            kExecutor = new KExecutor(loggerName); 
        }
        return kExecutor;
    }
    public Map<String, String> call(Map<String,URI> dspGuidUrlMap,KHttpClient httpClient,
            String content, int requestTimeoutMillis,
            int maxConnectionPerHost, int maxConnection) {
        
        try{
            if( dspGuidUrlMap==null || dspGuidUrlMap.size()< 1){
                return null;
            }
            
            Set<Callable<String>> callables = new HashSet<Callable<String>>();
            for(String key:dspGuidUrlMap.keySet()){
                callables.add(new NingCallable(dspGuidUrlMap.get(key), content, 
                        requestTimeoutMillis, maxConnectionPerHost, maxConnection, 
                        httpClient,key));
            }
            List<Future<String>> futures = executorService.invokeAll(callables);
            Map<String, String> advResponseMap = new HashMap<String, String>();
            
            for(Future<String> future : futures){
                String futureGetStr = future.get();
                if(futureGetStr != null){
                    String strSplit[] = futureGetStr.split(ExchangeConstants.callDelimiter);
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
        
    }*/
    

}

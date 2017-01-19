package com.kritter.exchange.job;

import com.kritter.core.workflow.Context;
import com.kritter.core.workflow.Job;
import com.kritter.entity.reqres.entity.Request;
import com.kritter.entity.reqres.log.ReqLog;
import com.kritter.entity.user.userid.DSPUserIdProvider;
import com.kritter.fanoutinfra.apiclient.common.KHttpClient;
import com.kritter.fanoutinfra.apiclient.ning.NingClient;
import com.kritter.fanoutinfra.executorservice.common.KExecutor;
import lombok.Getter;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DSPUserFetchJob implements Job {
    @Getter
    private String name;
    private String loggerName;
    private Logger logger;
    private String requestObjectKey;
    private String dspUserIdsMapKey;
    private DSPUserIdProvider dspUserIdProvider;
    private int requestTimeoutMillis;
    private int maxConnectionsPerHost;
    private int maxConnections;

    public DSPUserFetchJob(String jobName,
                           String loggerName,
                           String requestObjectKey,
                           String dspUserIdsMapKey,
                           DSPUserIdProvider dspUserIdProvider) {
        this.loggerName = loggerName;
        this.logger = LogManager.getLogger(loggerName);
        this.name = jobName;
        this.requestObjectKey = requestObjectKey;
        this.dspUserIdsMapKey = dspUserIdsMapKey;
        this.dspUserIdProvider = dspUserIdProvider;
        this.requestTimeoutMillis = 100;
        this.maxConnectionsPerHost = 20;
        this.maxConnections = 20;
    }

    @Override
    public void execute(Context context) {
        Request request = (Request)context.getValue(this.requestObjectKey);
        ReqLog.debugWithDebugNew(this.logger, request, "Inside {}", getName());

        // Fetch the user ids corresponding to the exchange user id.
        // Get the list of DSP's eligible for this request.
        // TODO
        Set<Integer> dspIds = null;
        if(dspIds == null || dspIds.size() == 0) {
            ReqLog.debugWithDebugNew(this.logger, request, "No DSP's eligible for this request. Returning.");
            return;
        }

        ReqLog.debugWithDebugNew(this.logger, request, "DSP's eligible for this request :");
        if(request.isRequestForSystemDebugging() || this.logger.isDebugEnabled()) {
            for(int dspId : dspIds) {
                ReqLog.debugWithDebugNew(this.logger, request, "\t{}", dspId);
            }
        }

        // Get the exchange user id for the user of this request.
        // TODO
        String exchangeUserId = null;
        ReqLog.debugWithDebugNew(this.logger, request, "Exchange user id for which to fetch dsp user ids : {}",
                exchangeUserId);

        Map<Integer, String> dspIdToUserIdMap = dspUserIdProvider.getDSPUserIdForExchangeId(exchangeUserId, dspIds);
        if(request.isRequestForSystemDebugging() || this.logger.isDebugEnabled()) {
            if(dspIdToUserIdMap == null || dspIdToUserIdMap.isEmpty()) {
                ReqLog.debugWithDebugNew(this.logger, request, "DSP inc id to User id map is empty.");
            } else {
                ReqLog.debugWithDebugNew(this.logger, request, "DSP inc id to User id map :");
                for (Map.Entry<Integer, String> entry : dspIdToUserIdMap.entrySet()) {
                    int dspId = entry.getKey();
                    String userId = entry.getValue();
                    ReqLog.debugWithDebugNew(this.logger, request, "\t{}:{}", dspId, userId);
                }
            }
        }

        if(dspIdToUserIdMap != null) {
            context.setValue(this.dspUserIdsMapKey, dspIdToUserIdMap);
        }

        // Additionally, if a DSP provides a callback URL to sync exchange's user id with the DSP's user id, call it
        List<String> urlList = new ArrayList<String>();
        for(int dspId : dspIds) {
            // Get the url for the DSP.
            // TODO
            String url = null;
            if(url == null) {
                ReqLog.debugWithDebugNew(this.logger, request, "DSP id : {} does not specify a callback URL.", url);
                continue;
            }

            // If no user id mapping is present for the DSP, append it's callback to the list of url's to be called
            if(dspIdToUserIdMap == null || !dspIdToUserIdMap.containsKey(dspId)) {
                ReqLog.debugWithDebugNew(this.logger, request, "User mapping for DSP id : {} doesn't exist.", dspId);
            } else {
                ReqLog.debugWithDebugNew(this.logger, request, "User mapping for DSP id : {} already exists.", dspId);
                continue;
            }

            ReqLog.debugWithDebugNew(this.logger, request, "DSP id : {} specifies callback URL : {}. Replacing the " +
                    "value for exchange user id in the URL and adding it to the list of urls to fire.", dspId);
            urlList.add(url);
        }

        if(!urlList.isEmpty()) {
            KExecutor kexecutor = KExecutor.getKExecutor(loggerName);
            if (kexecutor == null) {
                ReqLog.debugWithDebugNew(this.logger, request, "Kexecutor not initialized.");
            } else {
                ReqLog.debugWithDebugNew(this.logger, request, "Calling the urls asynchonously.");
                KHttpClient kHttpClient = new NingClient(loggerName);
                kexecutor.callAsync(urlList, kHttpClient, requestTimeoutMillis, maxConnectionsPerHost, maxConnections);
            }
        }
    }
}
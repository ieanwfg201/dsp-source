package com.kritter.adserving.shortlisting.targetingmatcher;

import com.kritter.adserving.shortlisting.TargetingMatcher;
import com.kritter.constants.Protocol;
import com.kritter.core.workflow.Context;
import com.kritter.entity.reqres.entity.Request;
import com.kritter.entity.reqres.log.ReqLog;
import com.kritter.serving.demand.cache.AdEntityCache;
import com.kritter.serving.demand.entity.AdEntity;
import lombok.Getter;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.HashSet;
import java.util.Set;

public class SSLTargetingMatcher implements TargetingMatcher {
    @Getter
    private String name;
    private Logger logger;

    private AdEntityCache adEntityCache;
    private String sslRequestKey;

    public SSLTargetingMatcher(String name,
                               String loggerName,
                               AdEntityCache adEntityCache,
                               String sslRequestKey) {
        this.name = name;
        this.logger = LogManager.getLogger(loggerName);
        this.adEntityCache = adEntityCache;
        this.sslRequestKey = sslRequestKey;
    }

    @Override
    public Set<Integer> shortlistAds(Set<Integer> adIdSet, Request request, Context context) {
        if(adIdSet == null || adIdSet.isEmpty()) {
            ReqLog.debugWithDebugNew(this.logger, request, "Candidate ad id set is null or empty.");
            return adIdSet;
        }

        boolean isSslRequest = (Boolean) context.getValue(this.sslRequestKey) || request.getSecure();
        if(!isSslRequest) {
            ReqLog.debugWithDebugNew(this.logger, request, "Request is not ssl enabled. passing all the ads.");
            return adIdSet;
        }

        Set<Integer> shortlistedAdIds = new HashSet<Integer>();
        ReqLog.debugWithDebugNew(this.logger, request, "Request is ssl enabled. Selecting ads with secure landing " +
                "url.");
        for(int adId : adIdSet) {
            ReqLog.debugWithDebugNew(this.logger, request, "Processing ad id : {}", adId);
            AdEntity adEntity = adEntityCache.query(adId);
            if(adEntity == null) {
                ReqLog.debugWithDebugNew(this.logger, request, "Ad entity for id : {} is null in ad entity cache",
                        adId);
                continue;
            }
            Protocol protocol = Protocol.getEnum(adEntity.getProtocol());
            ReqLog.debugWithDebugNew(this.logger, request, "Landing url Protocol : {}", protocol.getName());
            
            if(Protocol.HTTPS == protocol || Protocol.HTTP_HTTPS == protocol){
            	ReqLog.debugWithDebugNew(this.logger, request, "Landing url supports https. Passing ad id : {}",
                        adId);
                shortlistedAdIds.add(adId);
            }else {
                ReqLog.debugWithDebugNew(this.logger, request, "Landing url does not support https. Dropping ad id : {}",
                        adId);
            }
            /*String landingUrl = adEntity.getLandingUrl();
            ReqLog.debugWithDebugNew(this.logger, request, "Landing url : {}", landingUrl);
            if(landingUrl.startsWith("https://")) {
                ReqLog.debugWithDebugNew(this.logger, request, "Landing url starts with https. Passing ad id : {}",
                        adId);
                shortlistedAdIds.add(adId);
            } else {
                ReqLog.debugWithDebugNew(this.logger, request, "Landing url starts with http. Dropping ad id : {}",
                        adId);
            }*/
        }
        return shortlistedAdIds;
    }
}

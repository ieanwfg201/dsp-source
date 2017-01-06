package com.kritter.exchange.job;

import com.kritter.common.caches.account.AccountCache;
import com.kritter.common.caches.account.entity.AccountEntity;
import com.kritter.constants.ONLINE_FRAUD_REASON;
import com.kritter.core.workflow.Context;
import com.kritter.core.workflow.Job;
import com.kritter.core.workflow.Workflow;
import com.kritter.entity.postimpression.entity.Request;
import com.kritter.entity.user.userid.DSPUserIdProvider;
import com.kritter.utils.common.CookieUtils;
import lombok.Getter;
import org.apache.commons.codec.binary.Base64;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DSPUserSyncJob implements Job {
    //one by one pixel image content
    private static final String PIXEL_B64  = "R0lGODlhAQABAPAAAAAAAAAAACH5BAEAAAAALAAAAAABAAEAAAICRAEAOw==";
    private static final byte[] PIXEL_BYTES = Base64.decodeBase64(PIXEL_B64.getBytes());

    @Getter
    private String name;
    private Logger logger;
    private String kritterCookieIdName;
    private String dspIdKey;
    private String dspUserIdKey;
    private String exchangeUserIdKey;
    private DSPUserIdProvider dspUserIdProvider;
    private boolean dropCookies;
    private String dspUserSyncPrefix;
    private int cookieExpirySeconds;
    private String postImpressionRequestObjectKey;
    private AccountCache accountCache;

    public DSPUserSyncJob(String name,
                          String loggerName,
                          String kritterCookieIdName,
                          String dspIdKey,
                          String dspUserIdKey,
                          String exchangeUserIdKey,
                          DSPUserIdProvider dspUserIdProvider,
                          boolean dropCookies,
                          String dspUserSyncPrefix,
                          int cookieExpirySeconds,
                          String postImpressionRequestObjectKey,
                          AccountCache accountCache) {
        this.name = name;
        this.logger = LogManager.getLogger(loggerName);
        this.kritterCookieIdName = kritterCookieIdName;
        this.dspIdKey = dspIdKey;
        this.dspUserIdKey = dspUserIdKey;
        this.exchangeUserIdKey = exchangeUserIdKey;
        this.dspUserIdProvider = dspUserIdProvider;
        this.dropCookies = dropCookies;
        this.dspUserSyncPrefix = dspUserSyncPrefix;
        this.cookieExpirySeconds = cookieExpirySeconds;
        this.postImpressionRequestObjectKey = postImpressionRequestObjectKey;
        this.accountCache = accountCache;
    }

    @Override
    public void execute(Context context) {
        logger.debug("Inside execute of {}", getName());
        HttpServletRequest httpServletRequest = (HttpServletRequest)context.getValue(Workflow.CONTEXT_REQUEST_KEY);
        HttpServletResponse httpServletResponse = (HttpServletResponse) context.getValue(Workflow.CONTEXT_RESPONSE_KEY);
        Request postImpressionRequest = (Request)context.getValue(this.postImpressionRequestObjectKey);

        // The exchange user id could either come in the header or would come as a request parameter. Check both.
        // If both present then the request parameter takes precedence.
        String cookieUserId = CookieUtils.fetchCookieFromRequest(httpServletRequest, kritterCookieIdName, logger);
        String exchangeUserId = httpServletRequest.getParameter(this.exchangeUserIdKey);

        if(cookieUserId == null || cookieUserId.trim().isEmpty()) {
            logger.debug("Cookie user id is empty.");
        } else if(exchangeUserId == null || exchangeUserId.trim().isEmpty()) {
            logger.debug("Found cookie user id : {}. Exchange id is null, so setting it to exchange id.", cookieUserId);
            exchangeUserId = cookieUserId;
        }

        if(exchangeUserId == null || exchangeUserId.trim().isEmpty()) {
            logger.debug("Exchange user id not present or empty in the query params. Returning.");
            postImpressionRequest.setOnlineFraudReason(ONLINE_FRAUD_REASON.USER_SYNC_EXCH_ID_MISSING);
            return;
        }

        String dspUserId = httpServletRequest.getParameter(this.dspUserIdKey);
        if(dspUserId == null || dspUserId.trim().isEmpty()) {
            postImpressionRequest.setOnlineFraudReason(ONLINE_FRAUD_REASON.USER_SYNC_DSP_USER_ID_MISSING);
            logger.debug("DSP user id not present or empty in the query params. Returning.");
            return;
        }

        String dspGuid = httpServletRequest.getParameter(this.dspIdKey);
        if(dspGuid == null || dspGuid.trim().isEmpty()) {
            postImpressionRequest.setOnlineFraudReason(ONLINE_FRAUD_REASON.USER_SYNC_DSP_ID_MISSING);
            logger.debug("DSP id not present or empty in the query params. Returning.");
            return;
        }
        AccountEntity accountEntity = accountCache.query(dspGuid);
        if(accountEntity == null) {
            postImpressionRequest.setOnlineFraudReason(ONLINE_FRAUD_REASON.AD_ID_NOT_FOUND);
            logger.debug("Account with guid : {} not present in account cache.", dspGuid);
            return;
        }
        int dspId = accountEntity.getAccountId();

        if(dspUserIdProvider != null) {
            Map<Integer, String> dspExchangeUserIdMap = new HashMap<Integer, String>();
            dspExchangeUserIdMap.put(dspId, dspUserId);
            logger.debug("Updating the dsp user id : {} corresponding to exchange user id : {} for dsp id : {}", dspUserId,
                    exchangeUserId, dspId);
            dspUserIdProvider.updateDSPUserIdForExchangeId(exchangeUserId, dspExchangeUserIdMap);
        }

        if(dropCookies) {
            // Drop cookie for dsp id. Set value as <dspUserSyncPrefix><dspId> and value as <dspUserId>
            String cookieName = dspUserSyncPrefix + Integer.toString(dspId);
            CookieUtils.setCookie(httpServletResponse, this.logger, cookieName, dspUserId, cookieExpirySeconds);

            try {
                // Also send a 1x1 pixel
                httpServletResponse.setContentType("image/gif");
                httpServletResponse.getOutputStream().write(PIXEL_BYTES);
            } catch (IOException ioe) {
                this.logger.error("Caught exception while getting output stream of httpServletResponse. {}", ioe);
            }
        }

        if(postImpressionRequest.getOnlineFraudReason() == null) {
            logger.debug("Online fraud reason null. Setting.");
            postImpressionRequest.setOnlineFraudReason(ONLINE_FRAUD_REASON.HEALTHY_REQUEST);
        } else {
            logger.debug("Online fraud reason is already set : {}.", postImpressionRequest.getOnlineFraudReason());
        }
    }
}

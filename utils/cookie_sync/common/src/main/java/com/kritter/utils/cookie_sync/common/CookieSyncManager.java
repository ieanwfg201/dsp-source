package com.kritter.utils.cookie_sync.common;

import javax.servlet.http.HttpServletResponse;

/**
 * This interface defines methods to be used by for cookie synchronization
 * with an ad-exchange. Cookie is a feature feasible on web users who are
 * browsing internet on mobile web browsers or PC web browsers or any other
 * web browsers by use of some device.
 *
 * For storage of cookies in match table or any other information regarding
 * user its on the implementations to decide the store.It would mostly
 * be a nosql store.
 *
 */
public interface CookieSyncManager
{

    /**
     * This method has to synchronize the internal user id with the
     * exchange user id by use of http call.
     * @param internalCookieId
     * @param exchangeCookieId
     * @param expiryDays
     * @param httpServletResponse
     * @return if sync is successful(no exception) then true
     * otherwise false.
     */
    public boolean syncInternalUserIdWithExchangeUserId(String internalCookieId,
                                                        String exchangeCookieId,
                                                        int expiryDays,
                                                        HttpServletResponse httpServletResponse);


    /**
     * This function fetches DSP cookie/user id from store using the exchange user id.
     * @param exchangeCookieId
     * @return
     */
    public String fetchInternalUserIdForExchangeUserId(String exchangeCookieId);

    /**
     * This method is responsible to insert into or update the match table with exchange
     * user id and internal user id.
     * Returns true if insertion/updation successful otherwise false.
     * @param exchangeCookieId
     * @param internalCookieId
     * @return
     */
    public boolean updateMatchTableWithExchangeAndInternalUserId(String exchangeCookieId,String internalCookieId);
}

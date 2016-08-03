package com.kritter.postimpression.enricher_fraud.checker;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * This class is used for any utilities that online fraud checks may need.
 * 
 */

public class OnlineFraudUtils {

	private static final String WWW = "www.";

	public enum ONLINE_FRAUD_REASON {

		// if requesting handset is bot, done before actual handset
		// id matching.
		BOT_HANDSET("BOT"),

		// handset id mismatch.
		HANDSET_ID_MISMATCH("HI"),

        //handset id missing from request,
        HANDSET_ID_MISSING_FROM_REQUEST("HIM"),

        //handset id missing from request,
        HANDSET_UNDETECTED("HUD"),

        // impression served is expired.
		IMPRESSION_EXPIRED("EXP"),

        //impression id missing from request
        IMPRESSION_ID_MISSING_FROM_REQUEST("IMP_ID"),

		// url tampered with, hash check failure.
		URL_TAMPERED("URL"),

        // url's data not complete
        URL_DATA_NOT_COMPLETE("URL_MAL"),

        // location id mismatch, only at country level for now.
		LOCATION_MISMATCH("GEO"),

        // location id missing from request
        LOCATION_ID_MISSING_FROM_REQUEST("LI"),

        // location id could not be detected
        LOCATION_UNDETECTED("LU"),

        // site marked as not fit, red marked or suspended.
		SITE_UNFIT("SU"),

        // site id missing from request.
        SITE_ID_MISSING_FROM_REQUEST("SI"),

        // site id not present in cache.
        SITE_ID_NOT_PRESENT_IN_CACHE("SINP"),

        // for analytics related or in general cases.This reason is for
		// post impression event wherein the url received is not
		// from correct source.We do that by checking domain of referer
		// url against the one present in our system.
		REFERER_DOMAIN_UNKNOWN("DOMAIN"),

		// if there is any server error.
		INTERNAL_SERVER_ERROR("SERVER"),

        //inhouse click event,ad id not found.
        AD_ID_NOT_FOUND("ADID"),

        //inhouse click event,ad id missing from request.
        AD_ID_MISSING("ADIDM"),

        // special reason for request not being a fraud.
		HEALTHY_REQUEST("OK"),

        //if conversion expired.
        CONVERSION_EXPIRED("CONV_EXP"),

        //if event repeats.
        EVENT_DUPLICATE("DUPLICATE"),
	    
	    BILLABLE_EVENT("BILLABLE_EVENT"),
        //if RETARGETING SEGMENT NOT FOUND.
        RETARGETING_SEGMENT_NF("RETARGETING_SEGMENT_NF");

        private String value;

        private ONLINE_FRAUD_REASON(String value){
            this.value = value;
        }

        public String getFraudReasonValue(){
            return this.value;
        }
	};

	public static String getDomainName(String url) throws MalformedURLException {
		URL netUrl = new URL(url);
		String host = netUrl.getHost();
		if (host.startsWith(WWW)) {
			host = host.substring(4);
		}
		return host;
	}

	public static String fetchIpAddressFromLongValue(long ipAddrValue) {

		long a = (ipAddrValue & (0xff << 24)) >> 24;
		long b = (ipAddrValue & (0xff << 16)) >> 16;
		long c = (ipAddrValue & (0xff << 8)) >> 8;
		long d = ipAddrValue & 0xff;
		StringBuffer sb = new StringBuffer(String.valueOf(a));
		sb.append(".");
		sb.append(String.valueOf(b));
		sb.append(".");
		sb.append(String.valueOf(c));
		sb.append(".");
		sb.append(String.valueOf(d));
		return sb.toString();

	}

	public static void main(String[] args)
    {
		System.out.println(fetchIpAddressFromLongValue(1041498111));
	}

}

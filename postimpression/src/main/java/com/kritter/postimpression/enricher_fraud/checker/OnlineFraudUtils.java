package com.kritter.postimpression.enricher_fraud.checker;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * This class is used for any utilities that online fraud checks may need.
 * 
 */

public class OnlineFraudUtils {

	private static final String WWW = "www.";

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

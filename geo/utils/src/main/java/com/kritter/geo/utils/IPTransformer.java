package com.kritter.geo.utils;


import java.math.BigInteger;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class IPTransformer
{
    private static final long MAX_VALUE_IPV4_ONEPLACE = 256;
    private static final BigInteger MAX_VALUE_IPV6_ONEPLACE = BigInteger
            .valueOf(MAX_VALUE_IPV4_ONEPLACE);

    private static final long ipV4FourthPlaceValue = MAX_VALUE_IPV4_ONEPLACE
            * MAX_VALUE_IPV4_ONEPLACE * MAX_VALUE_IPV4_ONEPLACE;
    private static final long ipV4ThirdPlaceValue = MAX_VALUE_IPV4_ONEPLACE
            * MAX_VALUE_IPV4_ONEPLACE;
    private static final long ipV4SecondPlaceValue = MAX_VALUE_IPV4_ONEPLACE;

    private static final String DOT = "\\.";
    private static final String IP_DOT = ".";
    private static final String OPENING_BRACE = "[";
    private static final String CLOSING_BRACE = "]";
    private static final String EMPTY_STRING = "";


    public static long fetchLongValueForIPV4(String ipAddress) throws Exception
    {
        String partsIpAddress[] = ipAddress.split(DOT);

        if (partsIpAddress.length != 4)
            throw new Exception("ip address received is not well formed "
                    + ipAddress);

        long conversion = 0;

        conversion = conversion + Long.parseLong(partsIpAddress[3].trim());

        conversion = conversion
                + (Long.parseLong(partsIpAddress[2].trim()) * ipV4SecondPlaceValue);

        conversion = conversion
                + (Long.parseLong(partsIpAddress[1].trim()) * ipV4ThirdPlaceValue);

        conversion = conversion
                + (Long.parseLong(partsIpAddress[0].trim()) * ipV4FourthPlaceValue);

        return conversion;

    }

    public static BigInteger fetchBigIntValueForIPV6(String ipAddress) throws Exception
    {
        InetAddress ia = InetAddress.getByName(ipAddress);
        byte[] ba = ia.getAddress();
        BigInteger conversion = BigInteger.ZERO;
        BigInteger m = MAX_VALUE_IPV6_ONEPLACE;
        String arr = Arrays.toString(ba);

        arr = arr.replace(OPENING_BRACE, EMPTY_STRING);
        arr = arr.replace(CLOSING_BRACE, EMPTY_STRING);

        String parts[] = arr.split(",");

        int j = 15;
        for (String p : parts)
        {
            conversion = conversion.add(BigInteger.valueOf(
                    Integer.valueOf(p.trim())).multiply(m.pow(j)));
            j--;
        }

        return conversion;
    }

}

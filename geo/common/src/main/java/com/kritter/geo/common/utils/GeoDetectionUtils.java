package com.kritter.geo.common.utils;

import com.kritter.geo.common.entity.IpRangeKeyValue;

import java.math.BigInteger;
import java.net.InetAddress;
import java.util.*;

import com.kritter.geo.utils.IPTransformer;

/**
 * This class keeps all the utility methods required for geo detection.
 */
public class GeoDetectionUtils
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

    public static void sortIpRangeKeyValueSet(List<IpRangeKeyValue> inputData)
    {
        Collections.sort(inputData,new ComparatorIpRangeKeyValue());
    }

    private static class ComparatorIpRangeKeyValue implements Comparator<IpRangeKeyValue>
    {
        @Override
        public int compare(IpRangeKeyValue first, IpRangeKeyValue second)
        {
            if (first.getStartIpValue().compareTo(second.getStartIpValue()) <= 0)
                return -1;

            if (first.getStartIpValue().compareTo(second.getStartIpValue()) >= 0)
                return 1;

            return 0;
        }
    }

    /**
     * If the input range key values have intersecting IP ranges, then merge the intersecting ranges.
     * The input must be sorted
     *
     * @param rangeKeyValues Sorted list of IP ranges
     * @return Merged and cleaned up IP ranges
     */
    public static List<IpRangeKeyValue> mergeAndCleanupIpRanges(List<IpRangeKeyValue> rangeKeyValues) {
        if(rangeKeyValues == null || rangeKeyValues.size() == 0)
            return new ArrayList<IpRangeKeyValue>();

        List<IpRangeKeyValue> outputRangeKeyValues = new ArrayList<IpRangeKeyValue>();
        IpRangeKeyValue currentIpRangeKeyValue = rangeKeyValues.get(0);
        BigInteger currentStart = currentIpRangeKeyValue.getStartIpValue();
        BigInteger currentEnd = currentIpRangeKeyValue.getEndIpValue();
        for(int i = 1; i < rangeKeyValues.size(); ++i) {
            IpRangeKeyValue rangeKeyValue = rangeKeyValues.get(i);
            BigInteger start = rangeKeyValue.getStartIpValue();
            BigInteger end = rangeKeyValue.getEndIpValue();
            if(start.compareTo(currentEnd) > 0) {
                // Ranges not intersecting, add the currently created range to the output
                outputRangeKeyValues.add(new IpRangeKeyValue(currentStart, currentEnd, -1, null));
                currentStart = start;
                currentEnd = end;
            } else {
                // Ranges intersecting. If the end of this range is greater than the existing, update the end. Else
                // continue
                if(end.compareTo(currentEnd) >= 0) {
                    currentEnd = end;
                }
            }
        }
        outputRangeKeyValues.add(new IpRangeKeyValue(currentStart, currentEnd, -1, null));
        return outputRangeKeyValues;
    }

    public static int fetchIndexByBinarySearchForIPRange(IpRangeKeyValue[] arrayToSearch,
                                                         int startIndex, int endIndex,
                                                         BigInteger valueToFind)
    {
        int searchIndex = (endIndex - startIndex) / 2;

        if (searchIndex == -1
                || arrayToSearch[0].getStartIpValue().compareTo(
                valueToFind) > 0
                || arrayToSearch[arrayToSearch.length - 1]
                .getEndIpValue().compareTo(valueToFind) < 0)
            return -1;

        if (searchIndex > -1)
            searchIndex = startIndex + searchIndex;

        if (arrayToSearch[searchIndex].getStartIpValue().compareTo(
                valueToFind) <= 0
                && arrayToSearch[searchIndex].getEndIpValue().compareTo(
                valueToFind) >= 0)
            return searchIndex;

        if (searchIndex > 0
                && arrayToSearch[searchIndex].getStartIpValue()
                .compareTo(valueToFind) > 0
                && arrayToSearch[searchIndex - 1].getEndIpValue()
                .compareTo(valueToFind) < 0)
            return -1;

        if (arrayToSearch[searchIndex].getStartIpValue().compareTo(
                valueToFind) < 0) {

            startIndex = searchIndex;
            return fetchIndexByBinarySearchForIPRange(arrayToSearch,
                    startIndex, endIndex, valueToFind);
        }

        if (arrayToSearch[searchIndex].getStartIpValue().compareTo(
                valueToFind) > 0) {

            endIndex = searchIndex;
            return fetchIndexByBinarySearchForIPRange(arrayToSearch,
                    startIndex, endIndex, valueToFind);
        }
        return -1;
    }

    public static int fetchIndexByBinarySearchForIPRange(BigInteger[] arrayToSearch,
                                                         int startIndex, int endIndex,
                                                         BigInteger valueToFind)
    {
        if (
            arrayToSearch[0].compareTo(valueToFind) > 0 ||
            arrayToSearch[arrayToSearch.length - 1].compareTo(valueToFind) < 0
           )
            return -1;

        while(startIndex <= endIndex)
        {
            int middle = (startIndex + endIndex) / 2;
            if( valueToFind.compareTo(arrayToSearch[middle]) > 0)
            {
                startIndex = middle + 1;
            }
            else if(valueToFind.compareTo(arrayToSearch[middle]) < 0)
            {
                endIndex = middle - 1;
            }
            else
            {
                return middle;
            }
        }

        return -1;
    }


    public static long fetchLongValueForIPV4(String ipAddress) throws Exception
    {
        return IPTransformer.fetchLongValueForIPV4(ipAddress);
    }

    public static BigInteger fetchBigIntValueForIPV6(String ipAddress) throws Exception
    {
        return IPTransformer.fetchBigIntValueForIPV6(ipAddress);
    }

    public static String fetchIpAddressFromLongValue(long ipAddrValue) {

        long a = (ipAddrValue & (0xff << 24)) >> 24;
        long b = (ipAddrValue & (0xff << 16)) >> 16;
        long c = (ipAddrValue & (0xff << 8)) >> 8;
        long d = ipAddrValue & 0xff;
        StringBuffer sb = new StringBuffer(String.valueOf(a));
        sb.append(IP_DOT);
        sb.append(String.valueOf(b));
        sb.append(IP_DOT);
        sb.append(String.valueOf(c));
        sb.append(IP_DOT);
        sb.append(String.valueOf(d));
        return sb.toString();
    }

    public static void main(String s[])
    {
        System.out.println(fetchIpAddressFromLongValue(3389423616L));
    }
}

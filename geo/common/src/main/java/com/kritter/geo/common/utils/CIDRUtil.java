package com.kritter.geo.common.utils;

import lombok.Getter;

public class CIDRUtil
{
    private int baseIPnumeric;
    private int netmaskNumeric;
    //used for host range
    @Getter
    private String firstIP;
    @Getter
    private String lastIP;

    /**
     * Constructor
     *
     *@param IPinCIDRFormat IP in CIDR format e.g. 192.168.1.0/24
     */
    public CIDRUtil(String IPinCIDRFormat) throws NumberFormatException
    {
        String[] st = IPinCIDRFormat.split("\\/");
        if (st.length != 2)
        {
            throw new NumberFormatException("Invalid CIDR format '"
                    + IPinCIDRFormat + "', should be: xx.xx.xx.xx/xx");
        }
        String symbolicIP = st[0];
        String symbolicCIDR = st[1];

        Integer numericCIDR = new Integer(symbolicCIDR);
        if (numericCIDR > 32)
        {
            throw new NumberFormatException("CIDR can not be greater than 32");
        }

        //Get IP
        st = symbolicIP.split("\\.");
        if (st.length != 4)
        {
            throw new NumberFormatException("Invalid IP address: " + symbolicIP);
        }

        int i = 24;
        baseIPnumeric = 0;

        for (int n = 0; n < st.length; n++)
        {
            int value = Integer.parseInt(st[n]);
            if (value != (value & 0xff))
            {
                throw new NumberFormatException("Invalid IP address: " + symbolicIP);
            }
            baseIPnumeric += value << i;
            i -= 8;
        }

        //Get netmask
        if (numericCIDR < 8)
            throw new NumberFormatException("Netmask CIDR can not be less than 8");
        netmaskNumeric = 0xffffffff;
        netmaskNumeric = netmaskNumeric << (32 - numericCIDR);
    }

    /**
     * Converts Numeric version of IP to Symbolic, i.e. xxx.xxx.xxx.xxx
     *
     *@param ip IP Address in numeric form
     *@return the result of sb.toString(), the symbolic IP as a String
     */
    private String convertNumericIpToSymbolic(Integer ip)
    {
        StringBuffer sb = new StringBuffer(15);
        for (int shift = 24; shift > 0; shift -= 8)
        {
            // process 3 bytes, from high order byte down.
            sb.append(Integer.toString((ip >>> shift) & 0xff));
            sb.append('.');
        }
        sb.append(Integer.toString(ip & 0xff));
        return sb.toString();
    }

    /**
     * Calculates the range of hosts and assigns min and max to firstIP and lastIP
     *
     */
    public void getHostAddressRange()
    {
        int numberOfBits;
        for (numberOfBits = 0; numberOfBits < 32; numberOfBits++)
        {
            if ((netmaskNumeric << numberOfBits) == 0)
                break;
        }

        Integer numberOfIPs = 0;

        for (int n = 0; n < (32 - numberOfBits); n++)
        {
            numberOfIPs = numberOfIPs << 1;
            numberOfIPs = numberOfIPs | 0x01;
        }

        Integer baseIP = baseIPnumeric & netmaskNumeric;
        firstIP = convertNumericIpToSymbolic(baseIP);
        lastIP = convertNumericIpToSymbolic(baseIP + numberOfIPs);
    }
}

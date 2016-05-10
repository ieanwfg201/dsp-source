package com.kritter.utils.common;

/**
 * This class has utility methods to be defined by an ad exchange
 * if required by the ad exchange.
 */
public class AdExchangeUtils
{
    /*Default implementation is to expect price as double.*/
    public Double decodeWinBidPrice(String price)
    {
        return Double.valueOf(price);
    }
}

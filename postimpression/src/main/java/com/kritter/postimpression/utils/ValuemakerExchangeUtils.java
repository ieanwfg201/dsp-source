package com.kritter.postimpression.utils;

import com.kritter.utils.common.AdExchangeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by moon on 16/11/4.
 */
public class ValuemakerExchangeUtils extends AdExchangeUtils {

    Logger logger = LoggerFactory.getLogger(ValuemakerExchangeUtils.class);

    public Double decodeWinBidPrice(String price) {
        try {
            if (price != null) {
                return Double.valueOf(price) / 100;
            }
            return 0.0;
        } catch (Exception e) {
            logger.error(e.toString());
            return 0.0;
        }
    }
}

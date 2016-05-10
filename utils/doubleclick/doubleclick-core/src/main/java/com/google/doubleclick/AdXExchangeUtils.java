package com.google.doubleclick;

import com.google.doubleclick.crypto.DoubleClickCrypto;
import com.kritter.utils.common.AdExchangeUtils;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.SignatureException;

/**
 * This class implements utility methods required by google adx exchange.
 */
public class AdXExchangeUtils extends AdExchangeUtils
{
    private Logger logger;
    private Base64 base64Encoder;
    private byte[] encryptionKeyBytes;
    private byte[] integrityKeyBytes;
    private SecretKeySpec encryptionSecretKey;
    private SecretKeySpec integritySecretKey;
    private static final String HMAC_ALGORITHM_NAME = "HMAC";

    /*Instance to decrypt win bid price information.*/
    private DoubleClickCrypto.Price priceCryptoManager;


    public AdXExchangeUtils(String loggerName,
                            String encryptionKey,
                            String integrityKey) throws InvalidKeyException
    {
        logger = LoggerFactory.getLogger(loggerName);
        base64Encoder = new Base64(0);
        encryptionKeyBytes = base64Encoder.decode(encryptionKey);
        integrityKeyBytes = base64Encoder.decode(integrityKey);

        encryptionSecretKey = new SecretKeySpec(encryptionKeyBytes,HMAC_ALGORITHM_NAME);
        integritySecretKey = new SecretKeySpec(integrityKeyBytes,HMAC_ALGORITHM_NAME);

        DoubleClickCrypto.Keys keys = new DoubleClickCrypto.Keys(encryptionSecretKey,integritySecretKey);

        priceCryptoManager = new DoubleClickCrypto.Price(keys);
    }


    @Override
    public Double decodeWinBidPrice(String price)
    {
        /**The price value in micros (1/1.000.000th of the currency unit)
         * convert to currency unit before returning.**/

        Double priceToReturn = 0.0;

        long microValue;

        try
        {
            microValue = priceCryptoManager.decodePriceMicros(price);
            logger.debug("Price micro value decoded from win notification : {} ", microValue);
            priceToReturn = new Double(microValue);
        }
        catch (SignatureException se)
        {
            logger.error("SignatureException inside AdXExchangeUtils ", se);
        }

        return priceToReturn/1000;
    }
}

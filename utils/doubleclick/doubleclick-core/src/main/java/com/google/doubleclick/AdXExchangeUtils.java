package com.google.doubleclick;

import com.google.doubleclick.crypto.DoubleClickCrypto;
import com.kritter.utils.common.AdExchangeUtils;
import lombok.Getter;
import org.apache.commons.codec.binary.Base64;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

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
    @Getter
    private DoubleClickCrypto.Idfa idfa;

    public AdXExchangeUtils(String loggerName,
                            String encryptionKey,
                            String integrityKey) throws InvalidKeyException
    {
        logger = LogManager.getLogger(loggerName);
        base64Encoder = new Base64(0);
        encryptionKeyBytes = base64Encoder.decode(encryptionKey);
        integrityKeyBytes = base64Encoder.decode(integrityKey);

        encryptionSecretKey = new SecretKeySpec(encryptionKeyBytes,HMAC_ALGORITHM_NAME);
        integritySecretKey = new SecretKeySpec(integrityKeyBytes,HMAC_ALGORITHM_NAME);

        DoubleClickCrypto.Keys keys = new DoubleClickCrypto.Keys(encryptionSecretKey,integritySecretKey);

        priceCryptoManager = new DoubleClickCrypto.Price(keys);
        idfa = new DoubleClickCrypto.Idfa(keys);
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

    /*public static void main(String[] args) throws Exception
    {
        byte[] encryptionKey = {
                (byte) 0x26, (byte) 0x80, (byte) 0x43, (byte) 0x47, (byte) 0xf4,
                (byte) 0x11, (byte) 0x28, (byte) 0x4d, (byte) 0x55, (byte) 0x48,
                (byte) 0xd6, (byte) 0x97, (byte) 0x58, (byte) 0x94, (byte) 0xbf,
                (byte) 0x8b, (byte) 0xc3, (byte) 0xe7, (byte) 0x65, (byte) 0x78,
                (byte) 0x51, (byte) 0x32, (byte) 0xc5, (byte) 0x67, (byte) 0x85,
                (byte) 0x38, (byte) 0x69, (byte) 0x00, (byte) 0xf4, (byte) 0x08,
                (byte) 0xca, (byte) 0x5d
        };

        byte[] integrityKey = {
                (byte) 0xb5, (byte) 0xba, (byte) 0x5b, (byte) 0x30, (byte) 0x85,
                (byte) 0xa9, (byte) 0x21, (byte) 0xd9, (byte) 0xba, (byte) 0x76,
                (byte) 0xe6, (byte) 0xe4, (byte) 0xbb, (byte) 0xc0, (byte) 0x57,
                (byte) 0x4d, (byte) 0x0b, (byte) 0xf4, (byte) 0x10, (byte) 0x05,
                (byte) 0x73, (byte) 0xa2, (byte) 0x63, (byte) 0x55, (byte) 0xb0,
                (byte) 0x27, (byte) 0x56, (byte) 0x53, (byte) 0xcd, (byte) 0x58,
                (byte) 0xae, (byte) 0x00
        };

        Base64 base64Encoder = new Base64(0);

        String ek = base64Encoder.encodeAsString(encryptionKey);
        base64Encoder = new Base64(0);
        String ik = base64Encoder.encodeAsString(integrityKey);
        System.out.println(ek);
        System.out.println(ik);

        AdXExchangeUtils utils = new AdXExchangeUtils("","JoBDR/QRKE1VSNaXWJS/i8PnZXhRMsVnhThpAPQIyl0=","tbpbMIWpIdm6dubku8BXTQv0EAVzomNVsCdWU81YrgA=");

        System.out.println(utils.decodeWinBidPrice("WBHygQABzqYKfrFNAA0mhqaCbF-4JmOMRT8MKw"));
        System.out.println(utils.decodeWinBidPrice("WBMTKwANK-IKfsfDAA5fhYotSu_ec-KM4jIQbA"));
    }*/
}

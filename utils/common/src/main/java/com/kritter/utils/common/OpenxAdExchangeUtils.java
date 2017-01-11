package com.kritter.utils.common;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.openx.market.ssrtb.crypter.SsRtbCrypter;
import org.openx.market.ssrtb.crypter.SsRtbDecryptingException;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class OpenxAdExchangeUtils extends AdExchangeUtils {
    private static final String HMAC_SHA1 = "HmacSHA1";

    private Logger logger;
    private SecretKey encryptKey;
    private SecretKey identityKey;

    public OpenxAdExchangeUtils(String loggerName, String encyptKeyStr, String identityKeyStr) throws DecoderException {
        this.logger = LogManager.getLogger(loggerName);

        byte[] encyptKeyStrBytes = Hex.decodeHex(encyptKeyStr.toCharArray());
        byte[] identityKeyStrBytes = Hex.decodeHex(identityKeyStr.toCharArray());

        encryptKey = new SecretKeySpec(encyptKeyStrBytes, HMAC_SHA1);
        identityKey = new SecretKeySpec(identityKeyStrBytes, HMAC_SHA1);
    }

    @Override
    public Double decodeWinBidPrice(String price) {
        try {
            logger.debug("Encrypted price : {}", price);
            long decryptedValue = new SsRtbCrypter().decodeDecrypt(price, encryptKey, identityKey);
            double winPrice = decryptedValue * 0.001;
            logger.debug("Decrypted value in micros : {}. Win price : {}.", decryptedValue, winPrice);
            return winPrice;
        } catch (SsRtbDecryptingException srtbe) {
            logger.error("Error decrypting the encrypted openx price : {}, {}", price, srtbe);
        }
        return 0.0;
    }

    /*
    public static void main(String[] args) throws DecoderException {
        String price = "AAABWQHaN246Vukxi8-URvvr_lrKpMVuXcBn_A";

        String encryptKey = "A8A123E79E484239941BAB02550469B47D35E891CF9347C48894B017C1881E8D";
        String identityKey = "C9A1C4D389A44BF9B761C39AB978C0A6A1EE939C7A804AEFA31A3D19B504950F";

        OpenxAdExchangeUtils utils = new OpenxAdExchangeUtils("none", encryptKey, identityKey);

        double finalPrice = utils.decodeWinBidPrice(price);
        System.out.println("Final price = " + finalPrice);
    }
    */
}

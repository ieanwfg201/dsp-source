package com.kritter.postimpression.utils;

import com.kritter.utils.common.AdExchangeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * This class is used for price decryption of rubicon win notifications.
 */
public class RubiconExchangeUtils extends AdExchangeUtils
{
    private static char[] password;
    private static final String HEXES = "0123456789ABCDEF";
    private Logger logger;

    public RubiconExchangeUtils(String secretKeyPassword,String loggerName)
    {
        password = secretKeyPassword.toCharArray();
        this.logger = LoggerFactory.getLogger(loggerName);
    }

    public Double decodeWinBidPrice(String price)
    {
        String priceDecryptedString = null;

        try
        {
            logger.debug("Price to decrypt inside RubiconExchangeUtils is: {} with password: {} ",
                          price,password);

            priceDecryptedString = decrypt(password, dehex(price));
            Double priceValue = Double.valueOf(priceDecryptedString);
            logger.debug("Price decrypted inside RubiconExchangeUtils is: {} ",priceValue);
            return priceValue;
        }
        catch (Exception e)
        {
            logger.error("Exception inside RubiconExchangeUtils is ",e);
            return null;
        }
    }

    public static void main(String[] args) throws Exception {


        password = "X1ADSYYXHWTHR38Q".toCharArray();
        String[] samplePrices = new String[] {
                "06.93308",
                "01.34821",
                "12.31345",
                "00.23913",
                "102.9000",
                "149.1341" };
        for (String price : samplePrices) {
            String encryptedHexValue = getHex(encrypt(password, price));
            String decrypted = decrypt(password, dehex(encryptedHexValue));
            System.out.println(decrypted + " => 0x" + encryptedHexValue);
        }

    }

    private static byte[] encrypt(char[] password, String plaintext) throws Exception {
        byte[] bytes = new byte[password.length];
        for (int i = 0; i < password.length; ++i) {
            bytes[i] = (byte) password[i];
        }
        SecretKeySpec skeySpec = new SecretKeySpec(bytes, "Blowfish");
        Cipher cipher = Cipher.getInstance("Blowfish/ECB/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        byte[] encrypted = cipher.doFinal(plaintext.getBytes());
        return encrypted;
    }

    private static String decrypt(char[] password, byte[] ciphertext) throws Exception {
        byte[] bytes = new byte[password.length];
        for (int i = 0; i < password.length; ++i) {
            bytes[i] = (byte) password[i];
        }
        SecretKeySpec skeySpec = new SecretKeySpec(bytes, "Blowfish");
        Cipher cipher = Cipher.getInstance("Blowfish/ECB/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);
        byte[] encrypted = cipher.doFinal(ciphertext);
        return new String(encrypted);
    }

    private static byte[] dehex(String hex) {
        byte[] bits = new byte[hex.length() / 2];
        for (int i = 0; i < bits.length; i++) {
            bits[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2),
                    16);
        }
        return bits;
    }

    private static String getHex(byte[] raw) {
        if (raw == null) {
            return null;
        }
        final StringBuilder hex = new StringBuilder(2 * raw.length);
        for (final byte b : raw) {
            hex.append(HEXES.charAt((b & 0xF0) >> 4)).append(
                    HEXES.charAt((b & 0x0F)));
        }
        return hex.toString();
    }
}
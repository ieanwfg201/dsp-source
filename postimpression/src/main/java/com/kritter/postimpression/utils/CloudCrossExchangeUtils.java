package com.kritter.postimpression.utils;

import com.kritter.utils.common.AdExchangeUtils;
import com.kritter.utils.common.ServerConfig;
import org.apache.commons.lang.StringUtils;

/**
 * Created by hamlin on 16-9-9.
 */
public class CloudCrossExchangeUtils extends AdExchangeUtils {
    private String decryKey;

    public CloudCrossExchangeUtils(ServerConfig serverConfig, String cloudCrossToken) {
        this.decryKey = serverConfig.getValueForKey(cloudCrossToken);
    }

    public static String decry_RC4(byte[] data, String key) {
        if (data == null || key == null) {
            return null;
        }
        return asString(RC4Base(data, key));
    }

    /**
     * 对加密的字符串用秘钥进行解密
     *
     * @param data
     * @param key
     * @return
     */
    public static String decry_RC4(String data, String key) {
        if (data == null || key == null) {
            return null;
        }
        return new String(RC4Base(HexString2Bytes(data), key));
    }


    /**
     * 对明文字符串用秘钥进行加密
     *
     * @param data
     * @param key
     * @return
     */
    public static byte[] encry_RC4_byte(String data, String key) {
        if (data == null || key == null) {
            return null;
        }
        byte b_data[] = data.getBytes();
        return RC4Base(b_data, key);
    }

    /**
     * 对明文用秘钥进行加密
     *
     * @param data 明文字符串
     * @param key  加密秘钥
     * @return
     */
    public static String encry_RC4_string(String data, String key) {
        if (data == null || key == null) {
            return null;
        }
        return toHexString(asString(encry_RC4_byte(data, key)));
    }

    /**
     * 将传入的字节数组的每一个字节转换成字符，并将这些字符组成一个字符串
     *
     * @param buf
     * @return
     */
    private static String asString(byte[] buf) {
        StringBuffer strbuf = new StringBuffer(buf.length);
        for (int i = 0; i < buf.length; i++) {
            strbuf.append((char) buf[i]);
        }
        return strbuf.toString();
    }

    /**
     * 产生秘钥流
     *
     * @param aKey
     * @return
     */
    private static byte[] initKey(String aKey) {
        byte[] b_key = aKey.getBytes();
        byte state[] = new byte[256];

        for (int i = 0; i < 256; i++) {
            state[i] = (byte) i;
        }

        int index1 = 0;
        int index2 = 0;

        if (b_key == null || b_key.length == 0) {
            return null;
        }

        for (int i = 0; i < 256; i++) {
            index2 = ((b_key[index1] & 0xff) + (state[i] & 0xff) + index2) & 0xff;
            byte tmp = state[i];
            state[i] = state[index2];
            state[index2] = tmp;
            index1 = (index1 + 1) % b_key.length;
        }
        return state;
    }

    /**
     * 将一个字符串的每一个字符的值转换十六进制成无符号整数值的字符串表示形式的字符串
     *
     * @param s
     * @return
     */
    private static String toHexString(String s) {
        String str = "";
        for (int i = 0; i < s.length(); i++) {
            int ch = (int) s.charAt(i);
            String s4 = Integer.toHexString(ch & 0xFF);
            if (s4.length() == 1) {
                s4 = '0' + s4;
            }
            str = str + s4;
        }
        return str;// 0x表示十六进制
    }


    /**
     * 十六进制成无符号整数值的字符串表示形式的字符串转换成一个字节数组，每个字节不再是16进制表示
     *
     * @param src
     * @return
     */
    private static byte[] HexString2Bytes(String src) {
        int size = src.length();
        byte[] ret = new byte[size / 2];
        byte[] tmp = src.getBytes();
        for (int i = 0; i < size / 2; i++) {
            ret[i] = uniteBytes(tmp[i * 2], tmp[i * 2 + 1]);
        }
        return ret;
    }

    /**
     * 将一个有十六进制表示的无符号整数值转换成一个字符
     *
     * @param src0
     * @param src1
     * @return
     */
    private static byte uniteBytes(byte src0, byte src1) {
        char _b0 = (char) Byte.decode("0x" + new String(new byte[]{src0}))
                .byteValue();
        _b0 = (char) (_b0 << 4);
        char _b1 = (char) Byte.decode("0x" + new String(new byte[]{src1}))
                .byteValue();
        byte ret = (byte) (_b0 ^ _b1);
        return ret;
    }

    /**
     * RC4加密算法的实现
     *
     * @param input
     * @param mKkey
     * @return
     */
    private static byte[] RC4Base(byte[] input, String mKkey) {
        int x = 0;
        int y = 0;
        byte key[] = initKey(mKkey);
        int xorIndex;
        byte[] result = new byte[input.length];

        for (int i = 0; i < input.length; i++) {
            x = (x + 1) & 0xff;
            y = ((key[x] & 0xff) + y) & 0xff;
            byte tmp = key[x];
            key[x] = key[y];
            key[y] = tmp;
            xorIndex = ((key[x] & 0xff) + (key[y] & 0xff)) & 0xff;
            result[i] = (byte) (input[i] ^ key[xorIndex]);
        }
        return result;
    }

    public static void main(String[] args) {
        String inputStr = "99";
        String str = encry_RC4_string(inputStr, "lalalalalal");
        System.out.println(str);
        System.out.println(decry_RC4(str, "lalalalalal"));
    }

    @Override
    public Double decodeWinBidPrice(String price) {
        price = decry_RC4(price, this.decryKey);
        return Double.parseDouble(StringUtils.isEmpty(price) ? "0" : price) / 100;
    }
}

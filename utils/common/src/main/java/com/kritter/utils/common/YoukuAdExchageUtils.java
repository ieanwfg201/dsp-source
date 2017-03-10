package com.kritter.utils.common;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import com.google.common.io.BaseEncoding;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class YoukuAdExchageUtils extends AdExchangeUtils{
	private Logger logger;
	private String token;

	public YoukuAdExchageUtils(String loggerName,
			String token){
		logger = LogManager.getLogger(loggerName);
		this.token=token;
	}
	public Double decodeWinBidPrice(String price)
	{
		try{
			if(price != null){
				SecretKey skc = new SecretKeySpec(strToBytes(this.token), "AES");
				Cipher aesCipher = Cipher.getInstance("AES");
				aesCipher.init(Cipher.DECRYPT_MODE, skc);
				BaseEncoding baseEncoding = BaseEncoding.base64Url().omitPadding();
				byte[] bytePlainText = aesCipher.doFinal(baseEncoding.decode(price));
				String s = new String(bytePlainText);
				String split[] = s.split("_");
				Double d = Double.parseDouble(split[0]);
				return d/100;
			}
			return 0.0;
		}catch(Exception e){
			logger.error(e.getMessage(),e);
			return 0.0;
		}
	}
	public String encryptText(String plainText) throws Exception{
		SecretKey skc = new SecretKeySpec(strToBytes(this.token), "AES");
		Cipher aesCipher = Cipher.getInstance("AES");
		aesCipher.init(Cipher.ENCRYPT_MODE, skc);
		BaseEncoding baseEncoding = BaseEncoding.base64Url().omitPadding();
		byte[] byteCipherText = aesCipher.doFinal(plainText.getBytes());
		return baseEncoding.encode(byteCipherText);
	}

	private byte[] strToBytes(String s) {
		int len = s.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
					+ Character.digit(s.charAt(i + 1), 16));
		}
		return data;
	}

	/*	public static void main(String arg[]) throws Exception{

		YoukuAdExchageUtils y = new YoukuAdExchageUtils("a", "1234567890123456");
		String s = y.encryptText("345_1564652");
		System.out.println(y.decodeWinBidPrice(s));
	}*/
}

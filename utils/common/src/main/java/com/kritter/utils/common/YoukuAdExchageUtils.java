package com.kritter.utils.common;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class YoukuAdExchageUtils extends AdExchangeUtils{
	private Logger logger;
	private String token;

	public YoukuAdExchageUtils(String loggerName,
			String token){
		logger = LoggerFactory.getLogger(loggerName);
		this.token=token;
	}
	public Double decodeWinBidPrice(String price)
	{
		try{
			if(price != null){
				SecretKey skc = new SecretKeySpec(this.token.getBytes(), "AES");
				Cipher aesCipher = Cipher.getInstance("AES");
				aesCipher.init(Cipher.DECRYPT_MODE, skc);
				byte[] bytePlainText = aesCipher.doFinal(Base64.decodeBase64(price));
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
		SecretKey skc = new SecretKeySpec(this.token.getBytes(), "AES");
		Cipher aesCipher = Cipher.getInstance("AES");
		aesCipher.init(Cipher.ENCRYPT_MODE, skc);
		byte[] byteCipherText = aesCipher.doFinal(plainText.getBytes());
		return Base64.encodeBase64String(byteCipherText);
	}

/*	public static void main(String arg[]) throws Exception{

		YoukuAdExchageUtils y = new YoukuAdExchageUtils("a", "1234567890123456");
		String s = y.encryptText("345_1564652");
		System.out.println(y.decodeWinBidPrice(s));
	}*/
}

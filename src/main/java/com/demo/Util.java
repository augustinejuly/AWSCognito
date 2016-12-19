package com.demo;

import java.io.InputStream;
import java.util.Properties;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.amazonaws.util.Base64;
import com.amazonaws.util.StringUtils;


public class Util
{

	private static Properties properties;

	private final static String HMAC_SHA_256 = "HmacSHA256";

	static String getProperty(String key)
	{
		if(properties == null)
		{
			properties = new Properties();
			try(InputStream ins = Util.class.getClassLoader().getResourceAsStream("config.properties"))
			{
				properties.load(ins);
			} catch (Exception e) 
			{
				throw new RuntimeException(e);
			}
		}
		return properties.getProperty(key);
	}

	public static String getSecretHash(String userId, String clientId, String clientSecret) 
	{

		if (userId == null)
		{
			throw new IllegalArgumentException("user ID cannot be null");
		}

		if (clientId == null) 
		{
			throw new IllegalArgumentException("client ID cannot be null");
		}

		if (clientSecret == null) 
		{
			return null;
		}

		SecretKeySpec signingKey = new SecretKeySpec(clientSecret.getBytes(StringUtils.UTF8), HMAC_SHA_256);

		try {
			Mac mac = Mac.getInstance(HMAC_SHA_256);
			mac.init(signingKey);
			mac.update(userId.getBytes(StringUtils.UTF8));
			byte[] rawHmac = mac.doFinal(clientId.getBytes(StringUtils.UTF8));
			return  new String(Base64.encode(rawHmac));
		} catch (Exception e) 
		{
			throw new RuntimeException("errors in HMAC calculation");
		}
	}

}

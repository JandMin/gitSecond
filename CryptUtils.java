package com.aisino.projects.common.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.log4j.Logger;

public class CryptUtils{
	
	private static Logger logger = Logger.getLogger(CryptUtils.class);
	
  public static byte[] encrypt(String key, byte[] src){
    try{
	      KeyGenerator kgen = KeyGenerator.getInstance("AES");
	      SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
	      secureRandom.setSeed(key.getBytes("UTF-8"));
	      kgen.init(128, secureRandom);
	      SecretKey secretKey = kgen.generateKey();
	      byte[] enCodeFormat = secretKey.getEncoded();
	      SecretKey skey = new SecretKeySpec(enCodeFormat, "AES");
	
	      Cipher cipher = Cipher.getInstance("AES");
	      cipher.init(1, skey);
	      return cipher.doFinal(src); 
      } catch (Exception e) {
    	  logger.info("{key:"+key+"}",e);
      }
    return null;
  }

  public static byte[] decrypt(String key, byte[] src){
	  try{
	      KeyGenerator kgen = KeyGenerator.getInstance("AES");
	      SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
	      secureRandom.setSeed(key.getBytes("UTF-8"));
	      kgen.init(128, secureRandom);
	      SecretKey secretKey = kgen.generateKey();
	      byte[] deCodeFormat = secretKey.getEncoded();
	      SecretKey secretkey = new SecretKeySpec(deCodeFormat, "AES");
	      Cipher cipher = Cipher.getInstance("AES");
	      cipher.init(2, secretkey);
	      return cipher.doFinal(src); 
      }catch(Exception e){
    	  logger.info("{key:"+key+"}",e);
      }
	  return null;
  }

  public static String byte2hex(byte[] b){
    String hs = "";
    String stmp = "";
    for (int n = 0; n < b.length; n++) {
      stmp = Integer.toHexString(b[n] & 0xFF);
      if (stmp.length() == 1)
        hs = hs + "0" + stmp;
      else
        hs = hs + stmp;
    }
    return hs.toUpperCase();
  }

  public static byte[] hex2byte(byte[] b){
    if (b.length % 2 != 0){
    	return null;
    }
    byte[] b2 = new byte[b.length / 2];
    for (int n = 0; n < b.length; n += 2) {
      String item = new String(b, n, 2);
      b2[(n / 2)] = (byte)Integer.parseInt(item, 16);
    }
    return b2;
  }

  public static long byte2long(byte[] bytes, int offset){
    long value = 0L;
    for (int i = 7; i > -1; i--) {
      value |= (bytes[(offset++)] & 0xFF) << 8 * i;
    }
    return value;
  }

  public static void long2byte(long value, byte[] bytes, int offset)
  {
    for (int i = 7; i > -1; i--)
      bytes[(offset++)] = (byte)(int)(value >> 8 * i & 0xFF);
  }

  public static String getMac(InetAddress addr)
  {
    try
    {
      byte[] mac = NetworkInterface.getByInetAddress(addr).getHardwareAddress();
      StringBuffer sb = new StringBuffer();
      for (int i = 0; i < mac.length; i++) {
        if (i != 0) {
          sb.append("-");
        }

        String s = Integer.toHexString(mac[i] & 0xFF);
        sb.append(s.length() == 1 ? 0 + s : s);
      }

      return sb.toString().toUpperCase();
    } catch (Exception e) {
    	 logger.info("{addr:"+addr+"}",e);
    }
    return "";
  }
}
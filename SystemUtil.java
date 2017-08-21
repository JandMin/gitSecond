package com.asiainfo.imcd.mcp.util;

//import com.asiainfo.common.demo.MD5;
//import com.asiainfo.common.demo.SHA1;
import org.apache.log4j.Logger;
import org.bouncycastle.jcajce.provider.digest.SHA1;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by YUHB on 2016/11/11.
 */
public class SystemUtil {
    private static Logger logger = Logger.getLogger(SystemUtil.class);

    private static Properties SYSTEM_PROPERTIES = new Properties();

    private static final String SYSTEM_PROPERTIES_FILE = "/system.properties";

    static {
        try {

            logger.info("#static block. SYSTEM_PROPERTIES_FILE: "+SYSTEM_PROPERTIES_FILE);
            SYSTEM_PROPERTIES.load(SystemUtil.class.getResourceAsStream(SYSTEM_PROPERTIES_FILE));

        } catch (Exception e) {
            logger.error("#static block. error. ", e);
        }
    }

    /************************************
     * 返回系统配置变量值
     * @param key
     * @return
     */
    public static String getProperty(String key) {
//		logger.info("#getProperty. key: "+key+", value: "+SYSTEM_PROPERTIES.getProperty(key));
        return SYSTEM_PROPERTIES.getProperty(key);
    }

    public static String getDefaultCharset() {
        return getProperty(ApplicationConstant.SYSTEM_CHARSET);
    }

    /************************************
     * 判断字符串是否为空
     * @param source
     * @return boolean
     * @author YUHB
     */
    public static boolean isEmpty(String source) {
        return source == null ||
                source.length() == 0 ||
                "".equals(source) ||
                "null".equals(source);
    }

    /************************************
     * 合并字符数组
     * @param strs
     * @return String
     * @author YUHB
     */
    public static String join(String[] strs){
        return join(strs, ",");
    }

    /************************************
     * 合并字符数组
     * @param strs
     * @return String
     * @author YUHB
     */
    public static String join(String[] strs, String splitChar){
        if (strs.length == 1) {
            return strs[0];
        }
        String str = "";
        for (int i = 0; i < strs.length; i++) {
            str = str + splitChar + strs[i];
        }

        return str.substring(1);
    }

   /* *//************************************
     * 返回MD5加密
     * @param source
     * @return
     *//*
    public static String MD5(String source){

        return new MD5().encode(source);
    }

    *//************************************
     * 返回SHA1加密
     * @param source
     * @return
     */
    public static String SHA1(String source){
        String outStr="";
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(source.getBytes());
           // byte[] digest = md.digest();
            outStr = byteToString(md.digest());
            //logger.info("SHA-256加密后字符串"+outStr);
        }catch (Exception e){
            logger.error(e);
        }

        return outStr;
    }
    private static String byteToString(byte[] digest) {
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < digest.length; i++) {
            String tempStr = Integer.toHexString(digest[i] & 0xff);
            if (tempStr.length() == 1) {
                buf.append("0");
            } else {
                buf.append(tempStr);
            }
        }
        return buf.toString();
    }

    /**
     *  利用java原生的摘要实现SHA256加密
     * @param str 加密后的报文
     * @return
     */
    public static String getSHA256StrJava(String str){
        MessageDigest messageDigest;
        String encodeStr = "";
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(str.getBytes("UTF-8"));
            encodeStr = byte2Hex(messageDigest.digest());
        } catch (Exception e) {
            logger.error(e);
        }
        return encodeStr;
    }

    /**
     * 将byte转为16进制
     * @param bytes
     * @return
     */
    private static String byte2Hex(byte[] bytes){
        StringBuffer stringBuffer = new StringBuffer();
        String temp = null;
        for (int i=0;i<bytes.length;i++){
            temp = Integer.toHexString(bytes[i] & 0xFF);
            if (temp.length()==1){
                //1得到一位的进行补0操作
                stringBuffer.append("0");
            }
            stringBuffer.append(temp);
        }
        return stringBuffer.toString();
    }
    public static List<String> getDifferentFromList( List<String> list){
        List<String> list1 = new ArrayList<String>();
        list1.add(ApplicationConstant.CHANGE_NUM_01);
        list1.add(ApplicationConstant.CHANGE_NUM_02);
        list1.add(ApplicationConstant.CHANGE_NUM_03);
        list1.add(ApplicationConstant.CHANGE_NUM_04);
        list1.add(ApplicationConstant.CHANGE_NUM_05);
        list1.removeAll(list);
        return list1;
    }

}

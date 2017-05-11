package com.aisino.projects.crmapp.smdispatch.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 手机号码判断工具类
 * @author JandMin
 *
 */
public class PhoneUtil {
	
	/**
	 * 判断号码是否为可用的手机号码：11位数
	 * 	匹配格式：前三位固定格式+后8位任意数 
     * 	此方法中前三位格式有： 13+任意数 、15+除4的任意数 、18+任意数 、17+除9的任意数 、145、147
	 * @param mobile 需要判断的号码
	 * @return boolean
	 */
	public static boolean matcherPhone(String mobile){
		String regExp = "^((13[0-9])|(15[^4])|(18[0-9])|(17[0-8])|(14[5|7]))\\d{8}$";  
        Pattern p = Pattern.compile(regExp);  
        Matcher m = p.matcher(mobile);  
        return m.matches();  
	}
	
	/**
	 * 从字符串中获取手机号码
	 * @param str 原始字符串
	 * @return String 字符串中的手机号码
	 */
	public static String getPhoneNum(String str){
		if(str.length() < 11){		
			return "";
		}
		Pattern pattern = Pattern.compile("((13[0-9])|(15[^4])|(18[0-9])|(17[0-8])|(14[5|7]))\\d{8}$*");
		Matcher matcher = pattern.matcher(str);
		StringBuffer sb = new StringBuffer();
		while (matcher.find()) {
			sb.append(matcher.group()).append(",");
		}
		int len = sb.length();
		if (len > 0) {
			sb.deleteCharAt(len - 1);
		}
		return sb.toString();
	}
	public static void main(String[] args) {
		System.out.println(matcherPhone("18679892092"));
		String str = "aaaa1787876680000bbin18678767876yxyafa87687lisadji13187878787";
		System.out.println(getPhoneNum(str));
	}
}

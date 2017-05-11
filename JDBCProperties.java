package com.aisino.jdbc.helper;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import org.springframework.util.DefaultPropertiesPersister;
import org.springframework.util.PropertiesPersister;

import com.aisino.global.context.common.utils.CryptUtils;

public class JDBCProperties {
	public static void main(String[] stra) throws Exception{
		jiami();
	}
	public static void  jiami() throws Exception{
		String src = "c:/jdbc.properties";
		Properties properties = new Properties();  
		InputStream input = new FileInputStream(new File(src)); 

		byte[] b = new byte[input.available()];
		input.read(b);
		input.close();
		PropertiesPersister propertiesPersister = new DefaultPropertiesPersister();
		propertiesPersister.load(properties, new ByteArrayInputStream( b));

		for(String s:properties.stringPropertyNames()){
			// System.out.println(s+"："+properties.getProperty(s));
		}
		byte[] a = CryptUtils.decrypt("0D497DC094C3B34ED99F9425EA2A", b);
		//byte[] a = CryptUtils.encrypt("0D497DC094C3B34ED99F9425EA2A", b);
		String srcTmp = "c:/jdbcTmp.properties";
		OutputStream os = new FileOutputStream(new File(srcTmp)); 
		os.write(a);
		os.close();
		System.out.println("文件"+srcTmp+"生成成功");//UTF-8,iso8859-1,gbk,gb2312
	}
}

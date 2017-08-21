package com.asiainfo.biframe.task.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;

import javax.swing.filechooser.FileFilter;

public class FileUtils {
	public static void generateFile(String path, String fileName,
			CharSequence content, String encode) {
		File dirFile = new File(path);
		if (!dirFile.exists()) {
			dirFile.mkdirs();
		}

		File file = new File(dirFile, fileName);

		PrintWriter printWriter = null;
		// printWriter = new PrintWriter(jsp);
		try {
			printWriter = new PrintWriter(file, encode);
			printWriter.println(content);
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Can not find the file " + file);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} finally {
			if (printWriter != null)
				printWriter.close();
		}
	}
	
	/**
	 * 删除{path}路径下一天之前的文件
	 * @param path
	 */
	public static void deleteFilesOneDayAgo(String path) {
		File folder = new File(path);
		Calendar cal = Calendar.getInstance();
		Calendar aDayBefore = Calendar.getInstance();
		aDayBefore.add(Calendar.DAY_OF_MONTH, -1);
		try {
			if(folder.isDirectory()){
				for(File file : folder.listFiles()){
					long time = file.lastModified();  
			        cal.setTimeInMillis(time);    
					if(file.isFile() && cal.before(aDayBefore))
						file.delete();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

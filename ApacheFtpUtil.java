package com.asiainfo.biframe.task.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import com.asiainfo.biframe.utils.string.StringUtil;
import common.Logger;

/**
 * FTP客户端工具类apache版
 *
 */
public class ApacheFtpUtil {
	//private static final Logger log = LogManager.getLogger();
	 private static final Logger log = Logger.getLogger(ApacheFtpUtil.class);
	
	private FTPClient ftpClient;
	private String defaultEncoding = "UTF-8";

	//private static final 

	public String getDefaultEncoding() {
		return defaultEncoding;
	}

	public void setDefaultEncoding(String defaultEncoding) {
		this.defaultEncoding = defaultEncoding;
	}

	private ApacheFtpUtil() {

	}

	/**
	 * 实例获取方法
	 * @param server
	 * @param port
	 * @param loginName
	 * @param loginPwd
	 * @param encoding
	 * @return
	 */
	public static ApacheFtpUtil getInstance(String server, int port, String loginName, String loginPwd, String encoding) {
		ApacheFtpUtil ftpInstance = new ApacheFtpUtil();
		ftpInstance.init(server, port, loginName, loginPwd, encoding);
		return ftpInstance;
	}

	public static ApacheFtpUtil getInstance() {
		return new ApacheFtpUtil();
	}

	/**
	 * @param server
	 * @param port
	 * @param loginName
	 * @param loginPwd
	 */
	public void init(String server, int port, String loginName, String loginPwd) {
		init(server, port, loginName, loginPwd, getDefaultEncoding());
	}

	/**
	 * @param server
	 * @param port
	 * @param loginName
	 * @param loginPwd
	 * @param isPrintCommand
	 */
	public void init(String server, int port, String loginName, String loginPwd, String encoding) {
		try {
			ftpClient = new FTPClient();
			//			ftpClient.configure(getFTPClientConfig());
			String encode = StringUtil.isEmpty(encoding) ? getDefaultEncoding() : encoding;
			log.debug("设置FTP客户端当前编码为:" + encoding);
			setDefaultEncoding(encode);
			ftpClient.setControlEncoding(encode);
			ftpClient.setConnectTimeout(2000);
			ftpClient.setBufferSize(1024);

			ftpClient.connect(server, port);
			if (!ftpClient.login(loginName, loginPwd)) {
				ftpClient.logout();
				throw new Exception("登录FTP服务器失败,请检查![Server:" + server + "、" + "User:" + loginName + "、"
						+ "Password:" + loginPwd);
			}
			// 文件类型,默认是ASCII
			ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
			// 设置被动模式
			ftpClient.enterLocalPassiveMode();
			// 响应信息
			if (!isPositive()) {
				throw new Exception("登录FTP服务器失败,请检查![Server:" + server + "、" + "User:" + loginName + "、"
						+ "Password:" + loginPwd);
			}
		} catch (Exception e) {
			log.error("", e);
			closeFtpConnection();
			ftpClient = null;
			try {
				throw new Exception(e);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

	/**
	 * 配置FTP连接参数
	 *
	 * @return
	 * @throws Exception
	 */
	private FTPClientConfig getFTPClientConfig() throws Exception {
		String systemKey = FTPClientConfig.SYST_NT;
		String serverLanguageCode = "zh";
		FTPClientConfig conf = new FTPClientConfig(systemKey);
		conf.setServerLanguageCode(serverLanguageCode);
		conf.setDefaultDateFormatStr("yyyy-MM-dd");
		return conf;
	}

	/***************************************************************************
	 * 连接是否有效的
	 *
	 * @return
	 */
	public Boolean isPositive() {
		return FTPReply.isPositiveCompletion(ftpClient.getReplyCode());
	}

	/**
	 * 执行FTP操作前的检查，连接无效直接关闭
	 */
	private void isPositiveable() {
		if (!isPositive()) {
			closeFtpConnection();
		}
	}

	/***
	 * 切换工作目录
	 *
	 * @param pathname
	 * @return
	 * @throws EMVException
	 */
	public boolean changeWorkingDirectory(String pathname) {
		boolean flag = true;
		try {
			return ftpClient.changeWorkingDirectory(pathname);
		} catch (IOException e) {
			log.error("", e);
			flag = false;
		}
		return flag;
	}

	/***************************************************************************
	 * 下载文件
	 *
	 * @param outputStream
	 * @param remoteFileName
	 * @param fileType
	 * @throws EMVException
	 * @throws IOException
	 */
	public boolean download(OutputStream outputStream, String remoteFileName, int fileType) throws IOException {
		//		isPositiveable();

		ftpClient.setControlEncoding(defaultEncoding);
		ftpClient.enterLocalPassiveMode();
		boolean flag = true;
		try {
			ftpClient.setFileType(fileType);
			flag = ftpClient.retrieveFile(new String(remoteFileName.getBytes(getDefaultEncoding()), "ISO-8859-1"),
					outputStream);
		} catch (FileNotFoundException e) {
			log.error("", e);
			log.error(e);
			flag = false;
		} catch (IOException e) {
			log.error("", e);
			log.error(e);
			flag = false;
		} finally {
			if (outputStream != null) {
				outputStream.close();
			}
			closeFtpConnection();
		}
		return flag;
	}

	/***************************************************************************
	 * 下载文件
	 *
	 * @param outputStream
	 * @param remoteFileName
	 * @param fileType
	 * @throws EMVException
	 * @throws IOException
	 */
	public boolean download(OutputStream outputStream, String remoteFileName) throws IOException {
		return download(outputStream, remoteFileName, FTP.BINARY_FILE_TYPE);
	}

	/***************************************************************************
	 *
	 * @param file
	 *            下载下来的文件
	 * @param remoteFileName
	 * @throws EMVException
	 * @throws IOException
	 */
	public boolean download(File file, String remoteFileName) throws IOException {
		OutputStream outputStream = new FileOutputStream(file);
		return download(outputStream, remoteFileName, FTP.BINARY_FILE_TYPE);
	}

	/**
	 * 下载文件
	 * @param fileName 本地文件路径
	 * @param remoteFileName 远程文件路径
	 * @return
	 * @throws IOException
	 */
	public boolean download(String fileName, String remoteFileName) throws IOException {
		File file = new File(fileName);
		OutputStream outputStream = new FileOutputStream(file);
		return download(outputStream, remoteFileName, FTP.BINARY_FILE_TYPE);
	}

	/***************************************************************************
	 * 上传文件
	 *
	 * @param InputStream
	 * @param remoteFileName
	 * @throws EMVException
	 * @throws IOException
	 */
	public boolean upload(InputStream stream, String remoteFileName, Integer fileType) throws IOException {
		//		isPositiveable();
		boolean flag = true;
		try {
			ftpClient.setFileType(fileType);
			return ftpClient.storeFile(new String(remoteFileName.getBytes("GBK"), "ISO-8859-1"), stream);
		} catch (IOException e) {
			log.error("", e);
			log.error(e);
			flag = false;
		} finally {
			if (stream != null) {
				stream.close();
			}
			closeFtpConnection();
		}
		return flag;
	}

	/***************************************************************************
	 * 上传文件,默认以二进制传输
	 *
	 * @param InputStream
	 * @param remoteFileName
	 * @throws EMVException
	 * @throws IOException
	 */
	public boolean upload(InputStream stream, String remoteFileName) throws IOException {
		return upload(stream, remoteFileName, FTP.BINARY_FILE_TYPE);
	}

	/***************************************************************************
	 * 上传文件,默认以二进制传输
	 *
	 * @param InputStream
	 * @param remoteFileName
	 * @throws EMVException
	 * @throws IOException
	 */
	public boolean upload(File file) throws IOException {
		InputStream inputStream = new FileInputStream(file);
		return upload(inputStream, file.getName(), FTP.BINARY_FILE_TYPE);
	}

	/***************************************************************************
	 * 上传文件
	 *
	 * @param file
	 * @param fileType
	 * @throws EMVException
	 * @throws IOException
	 */
	public boolean upload(File file, int fileType) throws IOException {
		InputStream inputStream = new FileInputStream(file);
		return upload(inputStream, file.getName(), fileType);
	}

	/**
	 * 上传文件
	 * @param fileName
	 * @param remoteFileName
	 * @return
	 * @throws IOException
	 */
	public boolean upload(String fileName, String remoteFileName) throws IOException {
		File file = new File(fileName);
		InputStream inputStream = new FileInputStream(file);
		return upload(inputStream, remoteFileName, FTP.BINARY_FILE_TYPE);
	}

	/***************************************************************************
	 * 列出某目录下的所有文件和文件夹
	 *
	 * @param pathName
	 * @return
	 * @throws EMVException
	 */
	public FTPFile[] arrayFiles(String pathName) {
		//		isPositiveable();
		FTPFile[] files = null;
		try {
			files = ftpClient.listFiles(pathName);
		} catch (IOException e) {
			log.error("", e);
		} finally {
			closeFtpConnection();
		}
		return files;
	}

	/***************************************************************************
	 * 删除文件夹或文件,删除文件夹时，必须没有子目录或子文件
	 *
	 * @param pathName
	 * @throws EMVException
	 */
	public boolean del(String pathName) {
		//		isPositiveable();
		boolean flag = true;
		try {
			return FTPReply.isPositiveCompletion(ftpClient.dele(pathName));
		} catch (IOException e) {
			log.error("", e);
			flag = false;
		} finally {
			closeFtpConnection();
		}
		return flag;
	}

	/**
	 * 获取单个远程文件大小
	 * @param remoteFileName
	 * @return
	 * @throws IOException
	 * @throws UnsupportedEncodingException
	 */
	public long getFileSize(String remoteFileName) {
		//		isPositiveable();
		long fileSize = 0;
		try {
			FTPFile[] files = ftpClient.listFiles(new String(remoteFileName.getBytes(getDefaultEncoding()),
					"iso-8859-1"));
			if (files.length == 1) {
				fileSize = files[0].getSize();
			}
		} catch (IOException e) {
			log.error("", e);
		} finally {
			closeFtpConnection();
		}
		return fileSize;
	}

	/***************************************************************************
	 * 删除单个文件
	 *
	 * @param pathName
	 * @throws EMVException
	 */
	public boolean delete(String pathName) {
		//		isPositiveable();
		boolean flag = true;
		try {
			return ftpClient.deleteFile(pathName);
		} catch (IOException e) {
			log.error("", e);
			flag = false;
		} finally {
			closeFtpConnection();
		}
		return flag;
	}
	/**
	 * 删除以指定名称开头的文件
	 * @param headName
	 * @return
	 */
	public boolean deleteByHead(String headName)  {
		boolean flag = false;
		try {
			String[] fileArray = ftpClient.listNames();
			for(String file : fileArray){
				if(file.startsWith(headName)){
					return ftpClient.deleteFile(file);					
				}
			}
		} catch (IOException e) {
			log.error("", e);
		} finally {
			closeFtpConnection();
		}
		return flag;
	}

	/****
	 * 删除文件夹里的文件，不是会像 rm -rf 一样的
	 *
	 * @param path
	 * @return
	 * @throws EMVException
	 */
	public boolean removeDirectory(String path) {
		//		isPositiveable();
		boolean flag = true;
		try {
			return ftpClient.removeDirectory(path);
		} catch (IOException e) {
			log.error("", e);
			flag = false;
		} finally {
			closeFtpConnection();
		}
		return flag;
	}

	/***************************************************************************
	 * 递归删除某个文件夹下的文件,但不会删除当前文件夹，需要自己关闭FTP连接
	 *
	 * @param path
	 * @return
	 * @throws EMVException
	 * @throws IOException
	 */
	public void recursionDelele(String path) throws IOException {
		//		isPositiveable();
		FTPFile files[] = ftpClient.listFiles(path);
		if (ArrayUtils.isEmpty(files)) {
			ftpClient.deleteFile(path);
			return;
		}
		for (FTPFile file : files) {
			String p = path
					+ (file.isDirectory() ? path.endsWith("/") ? file.getName() : "/" + file.getName() + "/" : path
							.endsWith("/") ? file.getName() : "/" + file.getName());
			if (!file.isDirectory()) {
				ftpClient.deleteFile(p);
				continue;
			}
			if (file.isDirectory()) {
				recursionDelele(p);
			}
		}
	}

	/***
	 * 递归删除某个文件夹下的文件并删除当前文件夹
	 *
	 * @param path
	 * @throws IOException
	 * @throws EMVException
	 */
	public void recursionDele(String path) {
		//		isPositiveable();
		FTPFile files[];
		try {
			files = ftpClient.listFiles(path);
			for (FTPFile file : files) {
				if (!file.isDirectory()) {
					ftpClient.deleteFile(path + "/" + file.getName());
					continue;
				}
				if (file.isDirectory()) {
					recursionDelele(path);
				}
			}
			ftpClient.deleteFile(path);
		} catch (IOException e) {
			log.error("", e);
		} finally {
			closeFtpConnection();
		}
	}

	/***
	 * 递归下载某个目录下的所有文件和文件夹，文件的命名和FTP目录的命名结构一样，不关闭FTP连接
	 *
	 * @param localDir
	 *            本地接收的目录
	 * @param remotePath
	 * @throws EMVException
	 * @throws IOException
	 */
	private void recursionDownLoad(final String localDir, String remotePath, final String rootPath) throws IOException {
		//		isPositiveable();
		FTPFile[] files = ftpClient.listFiles(remotePath);
		File ff = new File(localDir + "/"
				+ (rootPath == null ? remotePath : remotePath.substring(rootPath.length() + 1, remotePath.length())));
		if (!ff.exists()) {
			ff.mkdir();
		}
		for (FTPFile file : files) {
			String p = remotePath
					+ (file.isDirectory() ? remotePath.endsWith("/") ? file.getName() : "/" + file.getName() + "/"
							: remotePath.endsWith("/") ? file.getName() : "/" + file.getName());
			String local = rootPath == null ? localDir + "/" + p : localDir + "/"
					+ p.substring(rootPath.length() + 1, p.length());
			if (!file.isDirectory()) {
				OutputStream outputStream = new FileOutputStream(new File(local));
				boolean b = ftpClient.retrieveFile(p, outputStream);
				log.debug( file.getName()+"{} 下载 " + (b ? "成功！" : "失败！"));
				outputStream.close();
			}
			if (file.isDirectory()) {
				File f = new File(local);
				if (!f.exists()) {
					f.mkdir();
				}
				recursionDownLoad(localDir, p, rootPath);
			}
		}
	}

	/***
	 * 下载远程目录下的所有的文件和文件夹，连远程目录会一起下载
	 *
	 * @param localDir
	 * @param remotePath
	 * @throws IOException
	 * @throws EMVException
	 */
	public void recursionDownLoad(String localDir, String remotePath) throws IOException {
		try {
			recursionDownLoad(localDir, remotePath, null);
		} catch (IOException e) {
			log.error("", e);
			throw e;
		} finally {
			closeFtpConnection();
		}
	}

	/***
	 * 递归下载某个目录下的所有文件和文件夹，文件的命名和FTP目录的命名结构一样不包括当前的目录，且会关闭FTP连接
	 *
	 * @param localDir
	 *            本地接收的目录
	 * @param remotePath
	 * @throws EMVException
	 * @throws IOException
	 */
	public void recursionDownLoadAndClose(final String localDir, String remotePath) {
		//		isPositiveable();
		FTPFile[] files;
		try {
			files = ftpClient.listFiles(remotePath);
			for (FTPFile file : files) {
				String p = remotePath
						+ (file.isDirectory() ? remotePath.endsWith("/") ? file.getName() : "/" + file.getName() + "/"
								: remotePath.endsWith("/") ? file.getName() : "/" + file.getName());
				String local = remotePath == null ? localDir + "/" + p : localDir + "/"
						+ p.substring(remotePath.length() + 1, p.length());
				if (!file.isDirectory()) {
					OutputStream outputStream = new FileOutputStream(new File(local));
					boolean b = ftpClient.retrieveFile(p, outputStream);
					log.debug(file.getName()+" 下载 " + (b ? "成功！" : "失败！"));
					outputStream.close();
				}
				if (file.isDirectory()) {
					recursionDownLoad(localDir, p, remotePath);
				}
			}
		} catch (IOException e) {
			log.error("", e);
		} finally {
			closeFtpConnection();
		}
	}

	/***
	 * 上传文件夹下的所有文件和文件夹,需要自己关闭连接
	 *
	 * @param local
	 * @param remotePath
	 * @throws EMVException
	 * @throws IOException
	 */
	private void recursionUpload(String local, final String remotePath) throws IOException {
		//		isPositiveable();
		File file = new File(local);
		if (!file.exists()) {
			return;
		}
		try {
			InputStream stream = null;
			if (file.isFile()) {
				stream = new FileInputStream(file);
				boolean b = ftpClient.storeFile(new String((remotePath + "/" + file.getName()).getBytes(getDefaultEncoding()), "ISO-8859-1"),stream);
				log.debug( file.getName()+" 上传 " + (b ? "成功！" : "失败！"));
				stream.close();
				return;
			}
			if (file.isDirectory()) {
				File[] files = file.listFiles();
				for (int i = 0; i < files.length; i++) {
					if (files[i].isFile()) {
						stream = new FileInputStream(files[i]);
						ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
						boolean b = ftpClient.storeFile(
								new String((remotePath + "/" + files[i].getName()).getBytes(getDefaultEncoding()),
										"ISO-8859-1"), stream);
						log.debug(files[i].getName()+" 上传 " + (b ? "成功！" : "失败！") );
						stream.close();
						continue;
					}
					if (files[i].isDirectory()) {
						ftpClient.makeDirectory(remotePath + "/" + files[i].getName());
						recursionUpload(files[i].getAbsolutePath(), remotePath + "/" + files[i].getName());
					}

				}
			}
		} catch (FileNotFoundException e) {
			log.error("", e);
		} catch (IOException e) {
			log.error("", e);
		}
	}

	/***
	 * 上传文件夹下的所有文件和文件夹
	 *
	 * @param local
	 * @param remotePath
	 * @throws EMVException
	 * @throws IOException
	 */
	public void recursionUploadFiles(String local, final String remotePath) throws IOException {
		try {
			recursionUpload(local, remotePath);
		} catch (IOException e) {
			log.error("", e);
			throw e;
		} finally {
			closeFtpConnection();
		}
	}

	/***************************************************************************
	 * 列出pathName目录下的所有文件,排除文件夹,如果不传入路径，就会显示当前的工作空间的路径
	 *
	 * @param pathName
	 * @return
	 * @throws EMVException
	 */
	public List<String> listFile(String pathName) {
		return listFiles(pathName, FTPFile.FILE_TYPE);
	}

	/***************************************************************************
	 * @param pathName
	 * @return
	 * @throws EMVException
	 */
	public List<String> listFiles(String pathName, int fileType) {
		FTPFile[] files = arrayFiles(pathName);
		List<String> fileList = new ArrayList<String>();
		for (FTPFile file : files) {
			if (file.getType() == fileType) {
				fileList.add(file.getName());
			}
		}
		return fileList;
	}

	/***************************************************************************
	 * 列出pathName目录下的所有文件,排除文件夹
	 *
	 * @param pathName
	 * @return
	 * @throws EMVException
	 */
	public List<String> listDirectory(String pathName) {
		return listFiles(pathName, FTPFile.DIRECTORY_TYPE);
	}

	/***************************************************************************
	 * 移动文件
	 *
	 * @param remoteFileFrom
	 *            目标文件
	 * @param remoteFileTo
	 *            移动至
	 * @throws EMVException
	 */
	public boolean move(String remoteFileFrom, String remoteFileTo) {
		//		isPositiveable();
		boolean flag = true;
		try {
			return ftpClient.rename(remoteFileFrom, remoteFileTo);
		} catch (IOException e) {
			log.error("", e);
			flag = false;
		} finally {
			closeFtpConnection();
		}
		return flag;
	}

	/***************************************************************************
	 * 创建文件夹
	 *
	 * @param remoteDirectory
	 * @throws EMVException
	 */
	public boolean makeDirectory(String remoteDirectory) {
		//		isPositiveable();
		boolean flag = true;
		try {
			flag = ftpClient.makeDirectory(new String(remoteDirectory.getBytes(getDefaultEncoding()), "iso-8859-1"));
		} catch (IOException e) {
			log.error("", e);
			flag = false;
		}
		//		finally {
		//			closeFtpConnection();
		//		}
		return flag;
	}
	/**
	 * 判断当前目录下是否存在某个文件
	 * @param ftp
	 * @param fileName
	 * @return
	 */
	public Boolean exists(String fileName){
		Boolean flag=false;
		List<String> list = this.listFile(null);
		if(list!=null && list.contains(fileName)){
			flag=true;
		}
		return flag;		
	}
	/**
	 * 读取当前目录下文件的内容
	 * @return
	 * 
	 */
	public String readFile(String fileName) {
		  InputStream ins = null;
		  StringBuilder builder = null;
		  try {
		   // 从服务器上读取指定的文件
		   ins = ftpClient.retrieveFileStream(fileName);
		   BufferedReader reader = new BufferedReader(new InputStreamReader(ins, "UTF-8"));
		   String line;
		   builder = new StringBuilder(150);
		   while ((line = reader.readLine()) != null) {
		    System.out.println(line);
		    builder.append(line);
		   }
		   reader.close();
		   if (ins != null) {
		    ins.close();
		   }
		   // 主动调用一次getReply()把接下来的226消费掉. 这样做是可以解决这个返回null问题
		   ftpClient.getReply();
		  } catch (IOException e) {
		   e.printStackTrace();
		  }
		  return builder.toString();
		} 

	/**
	 * 变更工作目录
	 *
	 * @param remoteDir--目录路径
	 */
	public boolean changeDir(String remoteDirectory) {
		//		isPositiveable();
		boolean flag = true;
		try {
			flag = ftpClient.changeWorkingDirectory(new String(remoteDirectory.getBytes(getDefaultEncoding()),
					"iso-8859-1"));
		} catch (IOException e) {
			log.error("", e);
			flag = false;
		}
		//		finally {
		//			closeFtpConnection();
		//		}
		return flag;
	}

	/**
	 * 变更工作目录(不存在并闯将)
	 *
	 * @param remoteDir--目录路径
	 */
	public void changeAndMakeDir(String remoteDirectory) {
		if (!changeDir(remoteDirectory)) {
			makeDirectory(remoteDirectory);
			changeDir(remoteDirectory);
		}
	}

	/**
	 * 返回上一级目录(父目录)
	 */
	public boolean toParentDir() throws IOException {
		//		isPositiveable();
		boolean flag = true;
		try {
			flag = ftpClient.changeToParentDirectory();
		} catch (IOException e) {
			log.error("", e);
			flag = false;
		}
		return flag;
	}

	/***************************************************************************
	 * 关闭FTP连接
	 *
	 * @throws EMVException
	 */
	public void closeFtpConnection() {
		try {
			if (ftpClient != null && !isPositive()) {
				ftpClient.logout();
				ftpClient.disconnect();
			}
		} catch (IOException e) {
			log.error("", e);
		}
	}

	/**
	 * 关闭连接
	 */
	public void forceCloseConnection() {
		try {
			if (ftpClient != null && ftpClient.isConnected()) {
				ftpClient.logout();
				ftpClient.disconnect();
			}
		} catch (IOException e) {
			log.error("", e);
		}
	} 
	 

	public FTPClient getFtpClient() {
		return ftpClient;
	}

	public static void main(String[] args) {
		ApacheFtpUtil ftp = ApacheFtpUtil.getInstance("10.1.252.197", 21, "crmtest", "crmtest", "gb2312");
		try {

			//ftp.changeDir("/ifdata1/data/F-IOP2.1-0005/20121117/23/");
			//ftp.download("D:/F_000IOP_F-IOP2.1-0005_000000_20121117230000.CHK", "F_000IOP_F-IOP2.1-0005_000000_20121117230000.CHK");
		} catch (Exception e) {
		}
		/*BufferedReader reader = null;
		try {
			File file = new File("D:/F_000IOP_F-IOP2.1-0005_000000_20121117230000.CHK");
			//		           log.debug("以行为单位读取文件内容，一次读一整行：");
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
			String tempString = null;
			int line = 1;
			// 一次读入一行，直到读入null为文件结束
			while ((tempString = reader.readLine()) != null) {
				// 显示行号
				log.debug("line " + line + ": " + tempString);
				//"€^"分隔符解析
				log.debug(Arrays.toString(tempString.split("€", -1)));

				//		            	log.debug(Arrays.toString(tempString.split(new String(new byte[]{(byte)0x80}, -1))));
				line++;
			}
			reader.close();
		} catch (IOException e) {
		log.error("",e);
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}
		}
		//		} catch (IOException e) {
		log.error("",e);
		//		}finally{
		//			ftp.forceCloseConnection();
		//		}
		*/}
}

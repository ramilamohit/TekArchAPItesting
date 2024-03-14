package com.tekArch.utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PropertiesUtility {
	
	protected static Logger PropertiesUtilitylog = LogManager.getLogger();


	public static String readDataFromPropertyFile(String path,String key) {
		File file=new File(path);
		FileInputStream fi=null;
		Properties propFile=new Properties();
		String data=null;
		
		try {
			fi=new FileInputStream(file);
			propFile.load(fi);
			data=propFile.getProperty(key,"b.gmail.com");
			fi.close();
			
		} catch (FileNotFoundException e) {
			PropertiesUtilitylog.error(".............error in file path....................");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			PropertiesUtilitylog.error("..............error while loading property file..........");
			e.printStackTrace();
		}
		
		return data;
	}
	
	
	public static void writeDataToPropertyFile(String path,String key,String value) {
		Properties propFile=new Properties();
		propFile.setProperty(key, value);
		FileOutputStream fo=null;
		File file=new File(path);
		try {
			fo=new FileOutputStream(file);
			propFile.store(fo,"adding new property with value");
			fo.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static int getSize(String path) {
		File file=new File(path);
		FileInputStream fi=null;
		Properties propFile=new Properties();
		int size=0;
		
		try {
			fi=new FileInputStream(file);
			propFile.load(fi);
			size=propFile.size();
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return size;
	}

	

}

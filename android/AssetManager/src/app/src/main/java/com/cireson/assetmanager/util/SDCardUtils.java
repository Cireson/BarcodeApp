
//////////////////////////////////////////////////////////////////////////////
//This file is part of Cireson Barcode Scanner. 
//
//Cireson Barcode Scanner is free software: you can redistribute it and/or modify
//it under the terms of the GNU General Public License as published by
//the Free Software Foundation, either version 3 of the License, or
//(at your option) any later version.
//
//Cireson Barcode Scanner is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.See the
//GNU General Public License for more details.
//
//You should have received a copy of the GNU General Public License
//along with Cireson Barcode Scanner.  If not, see<https://www.gnu.org/licenses/>.
/////////////////////////////////////////////////////////////////////////////


package com.cireson.assetmanager.util;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import org.apache.http.HttpEntity;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.FileChannel;

public class SDCardUtils {

	private static File externalDir = null;

	public static File getExternalDirectory(Context cntx) {
		if (externalDir != null) {
			return externalDir;
		} else {
			boolean mExternalStorageAvailable = false;
			boolean mExternalStorageWriteable = false;
			String state = Environment.getExternalStorageState();
			if (Environment.MEDIA_MOUNTED.equals(state)) {
				// We can read and write the media
				mExternalStorageAvailable = mExternalStorageWriteable = true;
			} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
				// We can only read the media
				mExternalStorageAvailable = true;
				mExternalStorageWriteable = false;
			} else {
				// Something else is wrong. It may be one of many other states,
				// but all we need
				// to know is we can neither read nor write
				mExternalStorageAvailable = mExternalStorageWriteable = false;
			}

			if (mExternalStorageAvailable && mExternalStorageWriteable) {
				externalDir = cntx.getExternalFilesDir(null);
			}
			return externalDir;
		}
	}

	public static boolean saveToSdcard(Context cntx, String content,
			String relativePath, String fileName) {

		try {

			String dir = getExternalDirectory(cntx) + File.separator
					+ relativePath;
			File dirF = new File(dir);
			if (!dirF.exists()) {
				dirF.mkdirs();
			}
			String path = dir + File.separator + fileName;
			Writer output = null;
			File file = new File(path);
			output = new BufferedWriter(new FileWriter(file));
			output.write(content);
			output.close();

		} catch (FileNotFoundException e) {
			return false;
		} catch (IOException e) {
			return false;
		}
		return true;
	}
	
	public static boolean appendToSdcard(Context cntx, String content,
			String relativePath, String fileName) {

		try {

			String dir = getExternalDirectory(cntx) + File.separator
					+ relativePath;
			File dirF = new File(dir);
			if (!dirF.exists()) {
				dirF.mkdirs();
			}
			String path = dir + File.separator + fileName;
			Writer output = null;
			File file = new File(path);
			output = new BufferedWriter(new FileWriter(file,true));
			output.write(content);
			output.close();

		} catch (FileNotFoundException e) {
			return false;
		} catch (IOException e) {
			return false;
		}
		return true;
	}
	
	public static boolean  saveEntityToSdcard(Context cntx, HttpEntity entity,String relativePath, String fileName){
		Log.d("Datta", "saveEntityToSdcard");
		 try {
			String dir = getExternalDirectory(cntx) + File.separator + relativePath;
			File dirF = new File(dir);
			if(!dirF.exists()){
				dirF.mkdirs();
			}
			String path = dir + File.separator + fileName;				
			FileOutputStream fos = new FileOutputStream(path, false);
			entity.writeTo(fos);
			fos.close();					
		 } catch (FileNotFoundException e) {
			return false;
		 } catch (IOException e) {
			return false;
		 }		
		return true;
	}

	/**
	 * Check if the database exist
	 * 
	 * @return true if it exists, false if it doesn't
	 */
	public static boolean isDatabaseExist(Context cntx,String dbfileName)
	{
		String dir = getExternalDirectory(cntx) + File.separator + dbfileName;
		File database=new File(dir);
		if (!database.exists()) {
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * This function is used to download the database file and store in local folder.
	 * also delete database if exist. 
	 * @param string
	 */
	public static void downloadDBFileTask(Context cntx,String urlStr,String dbfileName) 
	{
		String dir = getExternalDirectory(cntx) + File.separator + dbfileName;
		try {
			File file=new File(dir);
			if(file.exists())
				file.delete();
			
	            URL url = new URL(urlStr);
	            URLConnection connection = url.openConnection();
	            connection.connect();

	            // download the file
	            InputStream input = new BufferedInputStream(url.openStream());
	            OutputStream output = new FileOutputStream(dir);

	            byte data[] = new byte[1024];
	            int count;
	            while ((count = input.read(data)) != -1) {
	                output.write(data, 0, count);
	            }

	            output.flush();
	            output.close();
	            input.close();
		} catch (MalformedURLException e) {e.printStackTrace();
		} catch (IOException e) {e.printStackTrace();
		} catch (Exception e) {e.printStackTrace();}
	}
	
	public static String readAsText(Context cntx, String relativePath,
			String fileName) throws IOException {
		StringBuffer fileData = new StringBuffer(1000);
		BufferedReader reader = new BufferedReader(new FileReader(getFile(cntx,
				relativePath, fileName)));
		char[] buf = new char[1024];
		int numRead = 0;
		while ((numRead = reader.read(buf)) != -1) {
			String readData = String.valueOf(buf, 0, numRead);
			fileData.append(readData);
			buf = new char[1024];
		}
		reader.close();
		return fileData.toString();
	}

	public static InputStream getInputStream(Context cntx, String relativePath,
			String fileName) {
		FileInputStream in = null;
		try {
			String dir = getExternalDirectory(cntx) + File.separator
					+ relativePath;
			File dirF = new File(dir);
			if (!dirF.exists()) {
				return null;
			}
			String path = dir + File.separator + fileName;
			in = new FileInputStream(path);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return in;
	}

	public static File getFile(Context cntx, String relativePath,
			String fileName) {
		String dir = getExternalDirectory(cntx) + File.separator + relativePath;
		File dirF = new File(dir);
		if (!dirF.exists()) {
			dirF.mkdirs();
		}
		String path = dir + File.separator + fileName;
		return new File(path);
	}

	public static void copyFile(File sourceFile, File destFile)
			throws IOException {
		if (!sourceFile.exists()) {
			return;
		}
		if (!destFile.exists()) {
			destFile.createNewFile();
		}
		FileChannel source = null;
		FileChannel destination = null;
		source = new FileInputStream(sourceFile).getChannel();
		destination = new FileOutputStream(destFile).getChannel();
		if (destination != null && source != null) {
			destination.transferFrom(source, 0, source.size());
		}
		if (source != null) {
			source.close();
		}
		if (destination != null) {
			destination.close();
		}

	}

}

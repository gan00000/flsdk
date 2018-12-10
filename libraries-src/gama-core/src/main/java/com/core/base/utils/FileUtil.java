package com.core.base.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URI;
import java.util.Properties;

public class FileUtil {
	
	public static String readFile(String fileName) throws IOException{
		return readFile(null, fileName);
	}
	
	public static String readFile(Context context, String fileName) throws IOException{
//		FileInputStream inputStream = context.openFileInput(fileName);
		if (TextUtils.isEmpty(fileName)) {
			return "";
		}
		File file = new File(fileName);
		if (file.isFile() && file.exists()) {
			FileInputStream inputStream = new FileInputStream(file);
			return readFile(context, inputStream);
		}
		return "";
	}
	public static String readFileByByte(Context context, String fileName) throws IOException{
//		FileInputStream inputStream = context.openFileInput(fileName);
		if (TextUtils.isEmpty(fileName)) {
			return "";
		}
		File file = new File(fileName);
		if (file.isFile() && file.exists()) {
			FileInputStream inputStream = new FileInputStream(file);
			return readFileByByte(context, inputStream);
		}
		return "";
	}
	
	public static String readFile(Context context, FileInputStream inputStream) throws IOException{
		BufferedReader is = new BufferedReader(new InputStreamReader(inputStream));
		String data = "";
		StringBuffer content = new StringBuffer();
		while ((data = is.readLine()) != null) {
			content.append(data);
		}
		is.close();//先关闭BufferedReader
		inputStream.close();
		return content.toString();
	}
	
	public static boolean writeFileData(String filePath,String data) throws IOException{
		File f = new File(filePath);
		return writeFileData(null, f, data);
	}
	
	public static boolean writeFileData(Context context, String filePath,String data) throws IOException{
		File f = new File(filePath);
		return writeFileData(context, f, data);
	}
	
	public static boolean writeFileData(Context context, File f,String data) throws IOException{
		if (!f.exists()) {
    		try {
    			f.createNewFile();
    		} catch (IOException e) {
    			PL.d("createNewFile error:" + e.getMessage());
    			if (e.getMessage().contains("Permission denied")) {
					PL.d("没有添加android.permission.WRITE_EXTERNAL_STORAGE权限");
				}
    			e.printStackTrace();
    			return false;
    		}
    	}
		
		OutputStream os = new FileOutputStream(f);
		os.write(data.getBytes());
		os.flush();
		os.close();
		return true;
	}
	
	public static String readFileByByte(Context context, FileInputStream inputStream) throws IOException{
		if (inputStream == null) {
			return "";
		}
		
		StringBuilder data = new StringBuilder();
		int offset = 0;
		byte[] buffer = new byte[2048];
		while ((offset = inputStream.read(buffer, 0, buffer.length)) != -1) {
			String string = new String(buffer,0,offset);
			data.append(string);
			offset = 0;
		}
		inputStream.close();
		return data.toString();
	}
	
	
	
	/**
	 * 获取文件名
	 */
	public static String getFileName(String filePath) {
		String filename = filePath.substring(filePath.lastIndexOf('/') + 1);
		return filename.trim();
	}
	
	/**
	* <p>Title: getFileNameTmp</p>
	* <p>Description:  构造临时文件名</p>
	* @param filePath
	* @return
	*/
	public static String getFileNameTmp(String filePath){
		String mFileName = getFileName(filePath);
		String tmp = mFileName.substring(0, mFileName.lastIndexOf(".") + 1) + "tmp";
		PL.d("tmp file name:" + tmp);
		return tmp;
	}
	
	public static void deleteFile(File file) {
        if (null != file && file.exists()) { // 判断文件是否存在
            if (file.isFile()) { // 判断是否是文件
				PL.d("删除文件");
                file.delete(); // 删除文件;
            } else if (file.isDirectory()) { // 否则如果它是一个目录
                File files[] = file.listFiles(); // 声明目录下所有的文件 files[];
				for (File file1 : files) { // 遍历目录下所有的文件
					deleteFile(file1); // 把每个文件 用这个方法进行迭代
				}
            }
            file.delete();
        }
    }
	
	public static InputStream readFileStream(String filePath) throws FileNotFoundException{
		if (TextUtils.isEmpty(filePath)) {
			return null;
		}
		File file = new File(filePath);
		if (file.isFile() && file.exists()) {

			return new FileInputStream(file);
		}
		return null;
	}
	
	
	public static Properties readProterties(String filePath) throws IOException {
		InputStream inputStream = readFileStream(filePath);
		if (inputStream != null) {
			Properties props = new Properties();
			props.load(inputStream);
			inputStream.close();
			return props;
		}
		return null;

	}
	
	public static File createFile(String fileName) throws IOException{
		File f = new File(fileName);
		if (!f.getParentFile().exists()) {
			f.getParentFile().mkdirs();
		}
		if (!f.exists()) {
			f.createNewFile();
		}
		return f;
	}
	
	
	public static boolean copyFile(String oldFilePath,String newFilePath) {

		try {
			//如果原文件不存在
			File oldFile = new File(oldFilePath);
			if (oldFile.exists()) {
				PL.d("oldFile.exists true");
				//获得原文件流
				FileInputStream inputStream = new FileInputStream(oldFile);
				byte[] data = new byte[2048 * 2];
				//输出流
				File newFile = new File(newFilePath);
				if (newFile.exists()) {
					newFile.delete();
				}
				
				File newTmpFile = new File(newFilePath + ".tmp");
				if (!newTmpFile.getParentFile().exists()) {
					newTmpFile.getParentFile().mkdirs();
				}
				
				FileOutputStream outputStream = new FileOutputStream(newTmpFile);
				int offset = 0;
				//开始处理流
				while ((offset = inputStream.read(data, 0, data.length)) != -1) {
					outputStream.write(data,0,offset);
					offset = 0;
					Log.d("cpoy", "copy ... ");
				}
				
				inputStream.close();
				outputStream.close();
				newTmpFile.renameTo(newFile);
				return true;
			}
			PL.d( "oldFile.exists false");
		} catch (IOException e) {
			PL.d("e:" + e.getMessage());
			e.printStackTrace();
		}
		return false;
		
	}

	/**
	 * 写入文件
	 *
	 * @param fileName 文件名
	 * @param content 输出到文件的内容
	 * @param append 是否追加
	 */
	public static void writeFile(String fileName, String content, boolean append) {
		FileWriter writer = null;
		try
		{
			writer = new FileWriter(fileName, append);
			writer.write(content);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(writer != null) {
				try {
					writer.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	/**
	 * 读取assets中的文本
	 */
	public static String readAssetsTxtFile(Context context,String fileName){
		try {
			InputStream is = context.getAssets().open(fileName);
			if (is != null) {
				int lenght = is.available();

				byte[] buffer = new byte[lenght];
				is.read(buffer);

				is.close();
				return new String(buffer, "utf8");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 读取assets中的properties配置文件
	 */
	public static Properties readAssetsPropertiestFile(Context context,String fileName){
		try {
			InputStream is = context.getAssets().open(fileName);
			if (is != null) {
//				int lenght = is.available();
//
//				byte[] buffer = new byte[lenght];
//				is.read(buffer);

				Properties properties = new Properties();
				properties.load(is);
				is.close();
				return properties;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Uri getContentUri(Activity context, String picPath) {
		Uri picUri = null;
		if (TextUtils.isEmpty(picPath)) {
			PL.e("picPath is null, return");
			return null;
		}
		if (Build.VERSION.SDK_INT < 24) {
			File temp = new File(picPath);
			picUri = Uri.fromFile(temp);
		} else {
			String providerName = context.getPackageName() + ".GamaProvider";
			picUri = FileProvider.getUriForFile(context, providerName, new File(picPath));
		}
		if (picUri != null) {
			PL.d("uriForFile --> " + picUri);
		} else {
			PL.d("uriForFile --> null");
		}
		return picUri;
	}

	/**
	 * 将uri
	 * @param context
	 * @param uri
	 * @return
	 */
	public static String getFilePathByUri(Context context, Uri uri) {
		String path = null;
		// 以 file:// 开头的
		if (ContentResolver.SCHEME_FILE.equals(uri.getScheme())) {
			path = uri.getPath();
			return path;
		}
		// 以 content:// 开头的，比如 content://media/extenral/images/media/17766
		if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme()) && Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
			Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.Media.DATA}, null, null, null);
			if (cursor != null) {
				if (cursor.moveToFirst()) {
					int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
					if (columnIndex > -1) {
						path = cursor.getString(columnIndex);
					}
				}
				cursor.close();
			}
			return path;
		}
		// 4.4及之后的 是以 content:// 开头的，比如 content://com.android.providers.media.documents/document/image%3A235700
		if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme()) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			if (DocumentsContract.isDocumentUri(context, uri)) {
				if (isExternalStorageDocument(uri)) {
					// ExternalStorageProvider
					final String docId = DocumentsContract.getDocumentId(uri);
					final String[] split = docId.split(":");
					final String type = split[0];
					if ("primary".equalsIgnoreCase(type)) {
						path = Environment.getExternalStorageDirectory() + "/" + split[1];
						return path;
					}
				} else if (isDownloadsDocument(uri)) {
					// DownloadsProvider
					final String id = DocumentsContract.getDocumentId(uri);
					final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),
							Long.valueOf(id));
					path = getDataColumn(context, contentUri, null, null);
					return path;
				} else if (isMediaDocument(uri)) {
					// MediaProvider
					final String docId = DocumentsContract.getDocumentId(uri);
					final String[] split = docId.split(":");
					final String type = split[0];
					Uri contentUri = null;
					if ("image".equals(type)) {
						contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
					} else if ("video".equals(type)) {
						contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
					} else if ("audio".equals(type)) {
						contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
					}
					final String selection = "_id=?";
					final String[] selectionArgs = new String[]{split[1]};
					path = getDataColumn(context, contentUri, selection, selectionArgs);
					return path;
				}
			}
		}
		return null;
	}

	private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
		Cursor cursor = null;
		final String column = "_data";
		final String[] projection = {column};
		try {
			cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
			if (cursor != null && cursor.moveToFirst()) {
				final int column_index = cursor.getColumnIndexOrThrow(column);
				return cursor.getString(column_index);
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return null;
	}

	private static boolean isExternalStorageDocument(Uri uri) {
		return "com.android.externalstorage.documents".equals(uri.getAuthority());
	}

	private static boolean isDownloadsDocument(Uri uri) {
		return "com.android.providers.downloads.documents".equals(uri.getAuthority());
	}

	private static boolean isMediaDocument(Uri uri) {
		return "com.android.providers.media.documents".equals(uri.getAuthority());
	}
}

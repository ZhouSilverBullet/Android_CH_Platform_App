package com.chsdk.utils;

import static android.os.Environment.MEDIA_MOUNTED;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;

/**
 * @author ZengLei
 *         <p>
 * @version 2016年8月15日
 *          <p>
 */
public class FileUtil {
	private static final String TAG = FileUtil.class.getSimpleName();
	private static final String CACHE_FILE_TAG = "/CaoHuaSDK/";
	private static final String DEVICE_DIR_TAG = ".com.chsdk.device";
	private static final String DEIVCE_FIEL_TAG = "device.dat";
	private static final String HTML_ZIP_TAG = "Html/";
	
 	public static void createParentFolder(String filePath) {
		if (TextUtils.isEmpty(filePath)) {
			return;
		}
		File file = new File(filePath);
		if (file.exists()) {
			return;
		}
		File dir = file.getParentFile();
		if (dir != null) {
			dir.mkdirs();
		}
	}

	public static String readTxtFile(String filePath) {
		StringBuilder txt = null;
		try {
			File file = new File(filePath);
			if (file.isFile() && file.exists()) {
				InputStreamReader read = new InputStreamReader(new FileInputStream(file), "UTF-8");
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTxt = null;
				txt = new StringBuilder();
				while ((lineTxt = bufferedReader.readLine()) != null) {
					txt.append(lineTxt);
				}
				bufferedReader.close();
				read.close();
			} 
		} catch (Exception e) {
		} 
		
		return txt == null ? null : txt.toString().trim();
	}
	
	public static boolean writeFile(String localPath, byte[] data) {
		if (TextUtils.isEmpty(localPath) || data == null) {
			return false;
		}
		File localFile = new File(localPath);
		FileOutputStream out = null;
		try {
			createParentFolder(localPath);
			if (!localFile.exists() && !localFile.createNewFile()) {
				return false;
			}
			out = new FileOutputStream(localFile);
			out.write(data);
			out.flush();
			return true;
		} catch (IOException e) {
			localFile.delete();
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (IOException e) {
			}
		}
		return false;
	}

	/**
	 * 获取截图存放路径
	 */
	public static String getCropPicSavePath(Context context) {
		return getCHSdkCacheDirectory(context);
	}
	
	public static String getHtmlSavePath(Context context) {
		return getCHSdkCacheDirectory(context) + HTML_ZIP_TAG;
	}
	
	public static void unZip(Context context, String assetName, String outputDirectory)
			throws IOException {
		File file = new File(outputDirectory);
		if (!file.exists()) {
			file.mkdirs();
		}
		
		InputStream inputStream = context.getAssets().open(assetName);
		ZipInputStream zipInputStream = new ZipInputStream(inputStream);
		ZipEntry zipEntry = zipInputStream.getNextEntry();
		byte[] buffer = new byte[1024 * 4];
		int count = 0;
		while (zipEntry != null) {
			if (zipEntry.isDirectory()) {
				file = new File(outputDirectory + File.separator + zipEntry.getName());
				if (!file.exists()) {
					file.mkdir();
				}
			} else {
				file = new File(outputDirectory + File.separator + zipEntry.getName());
				if (!file.exists() || file.length() == 0) {
					file.createNewFile();
					FileOutputStream fileOutputStream = new FileOutputStream(file);
					while ((count = zipInputStream.read(buffer)) > 0) {
						fileOutputStream.write(buffer, 0, count);
					}
					fileOutputStream.close();
				}
			}
			zipEntry = zipInputStream.getNextEntry();
		}
		zipInputStream.close();
		inputStream.close();
	}
	
	public static String readDeviceFile(Context context, int appId) {
		try {
			String externalStorageState = Environment.getExternalStorageState();
			if (MEDIA_MOUNTED.equals(externalStorageState) && hasExternalStoragePermission(context)) {
				File dir = new File(Environment.getExternalStorageDirectory(), DEVICE_DIR_TAG);
				String[] list = dir.list();
				if (list != null && list.length > 0) {
					for (String fileName : list) {
						if (fileName.contains(appId + "_device")) {
							String filePath = dir.getAbsolutePath() + File.separator + appId + "_" + DEIVCE_FIEL_TAG;
						    return readTxtFile(filePath);
						}
					}
				}
			}
		} catch (Exception e) {
			LogUtil.errorLog("readDeviceFile: " + e.getMessage());
		}
		return null;
	}
	
	public static void saveDeviceFilePath(Context context, String deviceNo, int appId) {
		if (TextUtils.isEmpty(deviceNo))
			return;
		
		try {
			String externalStorageState = Environment.getExternalStorageState();
			if (MEDIA_MOUNTED.equals(externalStorageState) && hasExternalStoragePermission(context)) {
				File dir = new File(Environment.getExternalStorageDirectory(), DEVICE_DIR_TAG);
			    String filePath = dir.getAbsolutePath() + File.separator + appId + "_" + DEIVCE_FIEL_TAG;
				if (!dir.exists()) {
					if (dir.mkdirs()) {
						writeFile(filePath, deviceNo.getBytes());
					}
				} else {
					if (!new File(filePath).exists()) {
						writeFile(filePath, deviceNo.getBytes());
					}
				}
			}
		} catch (Exception e) { // (sh)it happens (Issue #660)
		}
	}
	
	/**
	 * 获取缓存目录
	 */
	public static String getCHSdkCacheDirectory(Context context) {
		File dataFileDir = getCacheDirectory(context, true);
		String cacheDir = dataFileDir.getAbsolutePath() + CACHE_FILE_TAG;
		return cacheDir;
	}

	public static File getCacheDirectory(Context context, boolean preferExternal) {
		File appCacheDir = null;
		String externalStorageState;
		try {
			externalStorageState = Environment.getExternalStorageState();
		} catch (NullPointerException e) { // (sh)it happens (Issue #660)
			externalStorageState = "";
		}

		if (preferExternal && MEDIA_MOUNTED.equals(externalStorageState) && hasExternalStoragePermission(context)) {
			appCacheDir = getExternalCacheDir(context);
		}
		
		if (appCacheDir == null) {
			appCacheDir = context.getCacheDir();
		}

		if (appCacheDir == null) {
			String cacheDirPath = "/data/data/" + context.getPackageName() + "/files/cache/";
			LogUtil.errorLog(TAG, "Can't define system cache directory! '%s' will be used." + cacheDirPath);
			appCacheDir = new File(cacheDirPath);
		}
		LogUtil.debugLog(TAG, "getCacheDirectory:" + appCacheDir);
		return appCacheDir;
	}

	private static File getExternalCacheDir(Context context) {
		File dataDir = context.getExternalCacheDir();
		if (dataDir == null) {
			String path = Environment.getExternalStorageDirectory() + File.separator + "Android" + File.separator + "data" + 
					File.separator + context.getPackageName() + File.separator + "cache";
			dataDir = new File(path);
		}
		
		if (!dataDir.exists()) {
			if (!dataDir.mkdirs()) {
				LogUtil.errorLog(TAG, "Unable to create external files directory");
				return null;
			}
		}
		
		try {
			new File(dataDir, ".nomedia").createNewFile();
			new File(dataDir, ".nomedia").delete();
		} catch (Exception e) {
			LogUtil.errorLog(TAG, "Unable to create external file:" + e.getMessage());
			return null;
		}
		
//		if (!appCacheDir.exists()) {
//			if (!appCacheDir.mkdirs()) {
//				LogUtil.errorLog(TAG, "Unable to create external cache directory");
//				return null;
//			}
//			try {
//				new File(appCacheDir, ".nomedia").createNewFile();
//			} catch (IOException e) {
//				LogUtil.errorLog(TAG, "Can't create \".nomedia\" file in application external cache directory");
//			}
//		}
		return dataDir;
	}

	private static boolean hasExternalStoragePermission(Context context) {
		int perm = context.checkCallingOrSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE");
		return perm == PackageManager.PERMISSION_GRANTED;
	}

	public static String getFileExtensionFromUrl(String url) {
		if (!TextUtils.isEmpty(url)) {
			int fragment = url.lastIndexOf('#');
			if (fragment > 0) {
				url = url.substring(0, fragment);
			}

			int query = url.lastIndexOf('?');
			if (query > 0) {
				url = url.substring(0, query);
			}

			int filenamePos = url.lastIndexOf('/');
			String filename = 0 <= filenamePos ? url.substring(filenamePos + 1) : url;

			// if the filename contains special characters, we don't
			// consider it valid for our matching purposes:
			if (!filename.isEmpty() && Pattern.matches("[a-zA-Z_0-9\\.\\-\\(\\)\\%]+", filename)) {
				int dotPos = filename.lastIndexOf('.');
				if (0 <= dotPos) {
					return filename.substring(dotPos + 1);
				}
			}
		}

		return "";
	}

	public static String pathFindExtension(String path) {
		if (path == null)
			return null;
		int index = path.lastIndexOf(".");
		if (index >= 0) {
			return path.substring(index + 1, path.length());
		}
		return null;
	}

	public static String getFileName(String path) {
		try {
			int start = path.lastIndexOf("/");
			int end = path.lastIndexOf(".");
			if (start != -1 && end != -1) {
				return path.substring(start + 1, end);
			} else {
				return null;
			}
		} catch (Exception e) {
			return null;
		}
	}  
	
	public static void deleteFile(File file) {
		if (file.isFile()) {
			file.delete();
			return;
		}
		if (file.isDirectory()) {
			File[] childFile = file.listFiles();
			if (childFile == null || childFile.length == 0) {
				file.delete();
				return;
			}
			for (File f : childFile) {
				deleteFile(f);
			}
			file.delete();
		}
	}

	public static void deleteDir(File file) {
		if (file.isFile()) {
			file.delete();
			return;
		}
		if (file.isDirectory()) {
			File[] childFile = file.listFiles();
			if (childFile == null || childFile.length == 0) {
				return;
			}
			for (File f : childFile) {
				deleteFile(f);
			}
		}
	}

	public static boolean getStorageStatus() {
		String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator +
				"Download/";
		File file = new File(path);
		if (!file.exists()) {
			boolean mkdir = file.mkdir();
			if (!mkdir) {
				return false;
			}
		}
		String testPath = path + ".test";
		File testFile = new File(testPath);
		if (!testFile.exists()) {
			try {
				boolean newFile = testFile.createNewFile();
				if (!newFile) {
					return false;
				}
				testFile.delete();
			} catch (IOException e) {
				return false;
			}
		}
		return true;
	}
}

package com.chsdk.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.caohua.games.app.AppContext;
import com.chsdk.biz.BaseLogic.LogicListener;
import com.chsdk.configure.DataStorage;
import com.chsdk.http.RequestExe;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author ZengLei
 *         <p>
 * @version 2016年8月19日
 *          <p>
 */
public class PicUtil {
    private static final String CROP_PIC_FILE = "CaoHua_Crop_";
    private static final String CROP_PIC_FORMAT = ".png";
    private static final int DIST_CACHE_SIZE = 100 * 1024 * 1024;

	public static String getPushPicPath(Context context, String url) {
		String path = context.getCacheDir().getAbsolutePath() + "/CaoHuaSDK/ad/push/" + "push" + url.hashCode()+ ".jpeg";
		LogUtil.errorLog("getPushPicPath:" + path + ", url:" + url);
		return path;
	}

    private static BitmapFactory.Options getOptions(Context context, String path) {
        int density = (int) context.getResources().getDisplayMetrics().density;
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, o);
        o.outHeight = o.outHeight * density;
        o.outWidth = o.outWidth * density;
        return o;
    }

    public static void downloadPushPic(final Context context, final String url, final LogicListener listener) {
        LogUtil.errorLog("PicUtil downloadPushPic url:" + url);

        final String path = getPushPicPath(context, url);
        File file = new File(path);
        if (file.exists()) {
            BitmapFactory.Options o = getOptions(context, path);
            listener.success(o.outWidth + "", o.outHeight + "");
            return;
        } else {
            FileUtil.deleteDir(new File(context.getCacheDir().getAbsolutePath() + "/CaoHuaSDK/ad/push/"));
        }

        Request request = new Request.Builder().url(url).get().build();
        RequestExe.getClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (listener != null) {
                    RequestExe.getOkMainThreadHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            listener.failed(null);
                        }
                    });
                }
            }

            @Override
            public void onResponse(Call call, Response response) {
                LogUtil.errorLog("PicUtil downloadPushPic onSuccess:" + url);
                if (listener == null)
                    return;
                InputStream inputStream = null;
                FileOutputStream fos = null;
                try {
                    inputStream = response.body().byteStream();
                    if (inputStream != null) {
                        File file = new File(context.getCacheDir().getAbsolutePath() + "/CaoHuaSDK/ad/push/");
                        if (!file.exists()) {
                            file.mkdirs();
                        }
                        LogUtil.errorLog("PicUtil downloadPushPic onSuccess: " + context.getCacheDir().getAbsolutePath() + "/CaoHuaSDK/ad/push/" + url.hashCode());
                        fos = new FileOutputStream(path);
                        int len = -1;
                        byte[] b = new byte[1024 * 4];
                        while ((len = inputStream.read(b)) != -1) {
                            fos.write(b, 0, len);
                            fos.flush();
                        }
                        RequestExe.getOkMainThreadHandler().post(new Runnable() {
                            @Override
                            public void run() {
                                BitmapFactory.Options o = getOptions(context, path);
                                LogUtil.errorLog("PicUtil downloadPushPic onSuccess: o.outWidth :" + o.outWidth + "   o.outHeight :" + o.outHeight);
                                listener.success(o.outWidth + "", o.outHeight + "");
                            }
                        });

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    listener.failed("PicUtil downloadPushPic fail : 图片下载出错 ！");
                } finally {
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (fos != null) {
                        try {
                            fos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    public static void downloadSplashPic(boolean isSome, final Context context, final String url, final LogicListener listener) {
        LogUtil.errorLog("PicUtil downloadSplashPic url:" + url);
        DataStorage.saveSplashPicStartTime(context, System.currentTimeMillis());
        final String path = context.getCacheDir().getAbsolutePath() + "/CaoHuaSDK/ad/splash/" + "splash.jpeg";
        File file = new File(path);
        if (file.exists() && isSome) {
            BitmapFactory.Options o = getOptions(context, path);
            listener.success(o.outWidth + "", o.outHeight + "");
            return;
        } else {
            FileUtil.deleteDir(new File(context.getCacheDir().getAbsolutePath() + "/CaoHuaSDK/ad/splash/"));
        }

        Request request = new Request.Builder().url(url).get().build();
        RequestExe.getClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (listener != null) {
                    RequestExe.getOkMainThreadHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            listener.failed(null);
                        }
                    });
                }
            }

            @Override
            public void onResponse(Call call, Response response) {
                LogUtil.errorLog("PicUtil downloadSplashPic onSuccess:" + url);
                if (listener == null)
                    return;
                InputStream inputStream = null;
                FileOutputStream fos = null;
                try {
                    inputStream = response.body().byteStream();
                    if (inputStream != null) {
                        File file = new File(context.getCacheDir().getAbsolutePath() + "/CaoHuaSDK/ad/splash/");
                        if (!file.exists()) {
                            file.mkdirs();
                        }
                        LogUtil.errorLog("PicUtil downloadSplashPic onSuccess: " + context.getCacheDir().getAbsolutePath() + "/CaoHuaSDK/ad/push/" + url.hashCode());
                        fos = new FileOutputStream(path);
                        int len = -1;
                        byte[] b = new byte[1024 * 4];
                        while ((len = inputStream.read(b)) != -1) {
                            fos.write(b, 0, len);
                            fos.flush();
                        }
                        RequestExe.getOkMainThreadHandler().post(new Runnable() {
                            @Override
                            public void run() {
                                BitmapFactory.Options o = getOptions(context, path);
                                LogUtil.errorLog("PicUtil downloadSplashPic onSuccess: o.outWidth :" + o.outWidth + "   o.outHeight :" + o.outHeight);
                                listener.success(o.outWidth + "", o.outHeight + "");
                            }
                        });

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    listener.failed("PicUtil downloadSplashPic fail : 图片下载出错 ！");
                } finally {
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (fos != null) {
                        try {
                            fos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    public static void downloadPic(final Context context, final String url, final LogicListener listener) {
        LogUtil.errorLog("PicUtil downloadPic start:" + url);
        try {
            if (context == null) {
                Glide.with(AppContext.getAppContext()).load(url);
            } else {
                Glide.with(context).load(url);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void displayImg(Context context, View img, String url, int defaultDrawable) {
        if (!TextUtils.isEmpty(url)) {
            if (context == null) {
                Glide.with(AppContext.getAppContext()).load(url).placeholder(defaultDrawable).dontAnimate().into((ImageView) img);
            } else {
                if (context instanceof Activity) {
                    if (!((Activity) context).isFinishing()) {
                        Glide.with(context).load(url).placeholder(defaultDrawable).dontAnimate().into((ImageView) img);
                    }
                } else {
                    Glide.with(context).load(url).placeholder(defaultDrawable).dontAnimate().into((ImageView) img);
                }
            }
        } else {
            ((ImageView) img).setImageResource(defaultDrawable);
        }
    }

    public static String cropPic(Context context, View decorView, String userName) {
        String savePicPath = null;
        String cacheDir = FileUtil.getCropPicSavePath(context);
        String fileName = CROP_PIC_FILE + userName + CROP_PIC_FORMAT;
        File file = new File(cacheDir, fileName);
        try {
            if (!file.exists()) {
                file.mkdirs();
            }
            if (!file.exists()) {
                file.createNewFile();
            } else {
                file.delete();
            }

            FileOutputStream fos = null;
            fos = new FileOutputStream(file);
            if (null != fos) {
                decorView.setDrawingCacheEnabled(true);
                // Measure(decorview);
                Bitmap bt = decorView.getDrawingCache();
                bt.compress(Bitmap.CompressFormat.PNG, 90, fos);// 保存进sd卡
                decorView.setDrawingCacheEnabled(false);

                fos.flush();
                fos.close();
            }

            // 其次把文件插入到系统图库
            savePicPath = MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(),
                    fileName, userName);

            savePicPath = checkIsSameUserCrop(context, savePicPath);
            if (!TextUtils.isEmpty(savePicPath)) {
                context.sendBroadcast(
                        new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(savePicPath))));
                file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return savePicPath;
    }

    public static String checkIsSameUserCrop(Context context, String lastSavePath) {
        if (TextUtils.isEmpty(lastSavePath)) {
            return null;
        }

        String[] arr = PicUtil.getRealPathFromUri(context, Uri.parse(lastSavePath));
        if (arr == null) {
            return null;
        }

        String path = arr[0];
        String title = arr[1];

        if (!TextUtils.isEmpty(path)) {
            return path;
        }

        return null;
    }

    public static String[] getRealPathFromUri(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA, MediaStore.Images.Media.TITLE};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            if (cursor.moveToFirst()) {
                String path = cursor.getString(0);
                String title = cursor.getString(1);
                return new String[]{path, title};
            }
        } catch (Exception e) {
            LogUtil.errorLog("" + e.getMessage());
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    public static int computeSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);
        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }
        return roundedSize;
    }

    private static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;
        int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(Math.floor(w / minSideLength), Math.floor(h / minSideLength));
        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }
        if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }
}

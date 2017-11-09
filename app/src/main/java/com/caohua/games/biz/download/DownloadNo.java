package com.caohua.games.biz.download;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.widget.RemoteViews;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.caohua.games.R;
import com.caohua.games.ui.HomePagerActivity;
import com.caohua.games.ui.download.DownloadListActivity;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by zhouzhou on 2017/6/13.
 */

public class DownloadNo {
    private Context context;
    private Notification notification;
    private NotificationManager manager;
    private static DownloadNo downloadNo;
    private String imageUrl;
    private Bitmap bitmap;
    private Notification.Builder builder;

    public static DownloadNo getDownloadNo(Context context) {
        if (downloadNo == null) {
            synchronized (DownloadNo.class) {
                downloadNo = new DownloadNo(context);
            }
        }
        return downloadNo;
    }

    private DownloadNo(Context context) {
        this.context = context;
        manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public void start() {
        builder = new Notification.Builder(context);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.ch_download_notifiction);
        Intent intent1 = new Intent(context, HomePagerActivity.class);
        Intent intent2 = new Intent(context, DownloadListActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivities(context, 200, new Intent[]{intent1, intent2}, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.ch_download_downloading_layout, pendingIntent);
        builder.setContent(remoteViews);
        remoteViews.setProgressBar(R.id.ch_download_downloading_progress, 100, 0, false);
        remoteViews.setTextViewText(R.id.ch_download_downloading_percent, "开始下载");
        this.notification = builder.getNotification();
        manager.notify(100, this.notification);
    }

    public void downloading(int progress, String loadPath, String gameName) {
        if (notification != null) {
            builder = new Notification.Builder(context);
            builder.setSmallIcon(R.mipmap.ic_launcher);
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.ch_download_notifiction);
            Intent intent1 = new Intent(context, HomePagerActivity.class);
            Intent intent2 = new Intent(context, DownloadListActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivities(context, 200, new Intent[]{intent1, intent2}, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.ch_download_downloading_layout, pendingIntent);
            builder.setContent(remoteViews);
            remoteViews.setProgressBar(R.id.ch_download_downloading_progress, 100, 0, false);
            remoteViews.setTextViewText(R.id.ch_download_downloading_percent, "开始下载");
            notification.contentView.setProgressBar(R.id.ch_download_downloading_progress, 100, progress, false);
            notification.contentView.setTextViewText(R.id.ch_download_downloading_percent, progress + "%");
            notification.contentView.setTextViewText(R.id.ch_download_downloading_name, "正在下载" + gameName);
            if (!TextUtils.isEmpty(loadPath) && !loadPath.equals(imageUrl)) {
                notification.contentView.setImageViewBitmap(R.id.ch_download_downloading_image, null);
                bitmap = null;
                imageUrl = loadPath;
                loadImage(imageUrl);
            }

            if (notification != null && bitmap != null) {
                try {
                    notification.contentView.setImageViewBitmap(R.id.ch_download_downloading_image, bitmap);
                } catch (Exception e) {
                }
            }

            if (manager != null) {
                manager.notify(100, this.notification);
            }
        }
    }

    public void completed() {
        if (notification != null) {
            notification.contentView.setTextViewText(R.id.ch_download_downloading_name, "下载完成");
            bitmap = null;
        }
    }

    public void loadImage(String urlStr) {
        new AsyncTask<String, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(String... params) {
                try {
                    URL url = new URL(params[0]);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(5000);//设置超时
                    conn.setDoInput(true);
                    conn.setUseCaches(false);//不缓存
                    conn.connect();
                    int code = conn.getResponseCode();
                    Bitmap bitmap = null;
                    if (code == 200) {
                        InputStream is = conn.getInputStream();//获得图片的数据流
                        bitmap = BitmapFactory.decodeStream(is);
                    }
                    return bitmap;
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    return null;
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Bitmap result) {
                super.onPostExecute(result);
                if (result != null) {
                    bitmap = result;
                }
            }
        }.execute(urlStr);
    }
}

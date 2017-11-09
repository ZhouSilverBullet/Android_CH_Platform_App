package com.caohua.games.biz.download;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;

import com.caohua.games.app.AppContext;
import com.caohua.games.biz.task.DoneTaskLogic;
import com.caohua.games.ui.HomePagerActivity;
import com.caohua.games.ui.download.DownloadListActivity;
import com.caohua.games.ui.download.IDownloadView;
import com.chsdk.biz.app.AnalyticsHome;
import com.chsdk.biz.download.DownloadEntry;
import com.chsdk.configure.DataStorage;
import com.chsdk.ui.widget.CHAlertDialog;
import com.chsdk.ui.widget.CHToast;
import com.chsdk.utils.ApkUtil;
import com.chsdk.utils.LogUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.wlf.filedownloader.DownloadFileChangeConfiguration;
import org.wlf.filedownloader.DownloadFileInfo;
import org.wlf.filedownloader.DownloadStatusConfiguration;
import org.wlf.filedownloader.FileDownloader;
import org.wlf.filedownloader.base.DownloadParams;
import org.wlf.filedownloader.listener.OnDownloadFileChangeListener;
import org.wlf.filedownloader.listener.OnFileDownloadStatusListener;

import java.io.File;

import pub.devrel.easypermissions.EasyPermissions;

import static org.wlf.filedownloader.base.Status.DOWNLOAD_STATUS_COMPLETED;
import static org.wlf.filedownloader.base.Status.DOWNLOAD_STATUS_DOWNLOADING;
import static org.wlf.filedownloader.base.Status.DOWNLOAD_STATUS_ERROR;
import static org.wlf.filedownloader.base.Status.DOWNLOAD_STATUS_FILE_NOT_EXIST;
import static org.wlf.filedownloader.base.Status.DOWNLOAD_STATUS_PAUSED;
import static org.wlf.filedownloader.base.Status.DOWNLOAD_STATUS_PREPARED;
import static org.wlf.filedownloader.base.Status.DOWNLOAD_STATUS_PREPARING;
import static org.wlf.filedownloader.base.Status.DOWNLOAD_STATUS_RETRYING;
import static org.wlf.filedownloader.base.Status.DOWNLOAD_STATUS_UNKNOWN;
import static org.wlf.filedownloader.base.Status.DOWNLOAD_STATUS_WAITING;

/**
 * Created by ZengLei on 2016/10/19.
 */

public class ViewDownloadMgr implements View.OnClickListener {
    private IDownloadView downloadView;
    private DownloadListener downloadListener;
    private FileStatusListener fileStatusListener;
    private DownloadEntry entry;
    private String lastRegisterUrl;
    private boolean isUpdate;

    public ViewDownloadMgr(IDownloadView downloadView) {
        this.downloadView = downloadView;
        ((View) downloadView).setOnClickListener(this);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Subscribe
    public void changeOpen(DownloadIsInsert insert) {
        String pkg = insert.getPkg();
        if (entry != null && entry.getPkg().equals(pkg)) {
            downloadView.setDownloadingText("打开");
        }
    }

    public void setData(@NonNull DownloadEntry entry) {
        if (entry == null || TextUtils.isEmpty(entry.downloadUrl) || !entry.downloadUrl.contains(".apk")) {
            this.entry = entry;
            unRegister("");
            downloadView.setDownloadingText("下载");
            return;
        }
        this.entry = entry;
        DownloadFileInfo info = FileDownloader.getDownloadFile(entry.downloadUrl);
        registerDownloadListener(entry.downloadUrl);
        setStatus(info);
    }

    /**
     * @param entry
     * @param isUpdate 更新
     */
    public void setData(@NonNull DownloadEntry entry, boolean isUpdate) {
        this.isUpdate = isUpdate;
        if (entry == null || TextUtils.isEmpty(entry.downloadUrl) || !entry.downloadUrl.contains(".apk")) {
            this.entry = entry;
            unRegister("");
            downloadView.setDownloadingText("下载");
            return;
        }
        this.entry = entry;
        DownloadFileInfo info = FileDownloader.getDownloadFile(entry.downloadUrl);
        registerDownloadListener(entry.downloadUrl);
        setStatus(info);
    }

    private boolean isAppInstalled() {
        // String packageName = ApkUtil.getUnInstallApkPackageName(context, info.getFilePath());
        return ApkUtil.checkAppInstalled(AppContext.getAppContext(), entry.pkg);
    }

    private void setStatus(DownloadFileInfo info) {
        if (isAppInstalled() && !isUpdate) {
            downloadView.setDownloadingText("打开");
            return;
        }

        if (isAppInstalled() && isUpdate) {
            downloadView.setDownloadingText("更新");
            return;
        }

        if (info != null) {
            int status = info.getStatus();
            int progress = getDownloadPercent(info);
            if (progress > 100) {
                FileDownloader.pause(entry.downloadUrl);
                downloadView.setProgress(0);
                downloadView.setError();
                deleteFile(info);
                return;
            }

            downloadView.setProgress(progress);
            if (status == DOWNLOAD_STATUS_PAUSED) {
                downloadView.setPause();
            } else if (status == DOWNLOAD_STATUS_ERROR) {
                if (progress > 100) {
                    downloadView.setProgress(0);
                    deleteFile(info);
                }
                downloadView.setError();
            } else if (status == DOWNLOAD_STATUS_DOWNLOADING ||
                    status == DOWNLOAD_STATUS_RETRYING ||
                    status == DOWNLOAD_STATUS_PREPARED ||
                    status == DOWNLOAD_STATUS_PREPARING ||
                    status == DOWNLOAD_STATUS_WAITING) {
                downloadView.setDownloading();
            } else if (status == DOWNLOAD_STATUS_COMPLETED) {
                downloadView.setDownloadingText("安装");
                LogUtil.errorLog("ViewDownloadMgr  TASK_DOWNLOAD 安装");
            } else if (status == DOWNLOAD_STATUS_UNKNOWN || status == DOWNLOAD_STATUS_FILE_NOT_EXIST) {
//                FileDownloader.delete(entry.downloadUrl, true, null);
                downloadView.setDownloadingText("下载");
            } else {
                downloadView.setDownloadingText("下载");
            }
        } else {
            if (isAppInstalled() && isUpdate) {
                downloadView.setDownloadingText("更新");
            } else {
                downloadView.setDownloadingText("下载");
            }
        }
    }

    private DownloadParams getParams() {
        DownloadParams params = new DownloadParams();
        params.url = entry.downloadUrl;
        params.title = entry.title;
        params.pkg = entry.pkg;
        params.iconUrl = entry.iconUrl;
        return params;
    }

    @Override
    public void onClick(View v) {
        final Context context = v.getContext();
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (!EasyPermissions.hasPermissions(context, perms)) {
            EasyPermissions.requestPermissions((Activity) context, "请授予手机读写权限", HomePagerActivity.PERMISSION_FOR_WRITE_STORAGE, perms);
            return;
        }

        if (isAppInstalled() && !isUpdate) {
            downloadView.setDownloadingText("打开");
            ApkUtil.startAPP(context, entry.pkg);
            return;
        }

        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            CHToast.show(context, "sdcard无法写入文件,请检查sdcard状态");
            return;
        }

        if (entry == null || TextUtils.isEmpty(entry.downloadUrl)) {
            CHToast.show(context, "无效的下载地址");
            downloadView.setDownloadingText("无效地址");
            return;
        }


        if (isAppInstalled() && isUpdate) {
            downloadView.setDownloadingText("更新");
            ApkUtil.startAPP(context, entry.pkg);
            return;
        }

        if (TextUtils.isEmpty(entry.pkg)) {
            CHToast.show(context, "无效包名");
            return;
        }

        if (!entry.downloadUrl.contains(".apk")) {
            CHToast.show(context, "无效的下载地址");
            downloadView.setDownloadingText("无效地址");
            unRegister(entry.downloadUrl);
            return;
        }

        final DownloadFileInfo info = FileDownloader.getDownloadFile(entry.downloadUrl);
        int status = -1;
        if (info != null) {
            status = info.getStatus();
        }
        if (info == null || status == DOWNLOAD_STATUS_PAUSED ||
                status == DOWNLOAD_STATUS_ERROR ||
                status == DOWNLOAD_STATUS_FILE_NOT_EXIST ||
                status == DOWNLOAD_STATUS_UNKNOWN
                ) {
            boolean wifiDownload = DataStorage.getWifiDownload(context);

            if (wifiDownload && getNetworkType() != ConnectivityManager.TYPE_WIFI) {
                final CHAlertDialog chAlertDialog = new CHAlertDialog((Activity) context);
                chAlertDialog.show();
                chAlertDialog.setContent("你确定在非wifi状态下载吗?");
                chAlertDialog.setCancelButton("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        chAlertDialog.dismiss();
                    }
                });
                chAlertDialog.setOkButton("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CHToast.show(context, "建议您在wifi条件下载");
                        downloadMethod(info);
                        chAlertDialog.dismiss();
                    }
                });
            } else {
                downloadMethod(info);
            }
        } else {
            downloadMethod(info);
        }
    }

    private void downloadMethod(DownloadFileInfo info) {
        if (info == null) {

            AnalyticsHome.umOnEvent(AnalyticsHome.HOME_DOWNLOAD_CLICK_ANALYTICS, "开始下载" + entry.getPkg());

            FileDownloader.start(getParams());
            downloadView.setProgress(0);
            downloadView.setDownloading();
        } else {
            int status = info.getStatus();
            int progress = getDownloadPercent(info);
            if (status == DOWNLOAD_STATUS_PAUSED) {
                downloadView.setProgress(progress);
                downloadView.setDownloading();
                FileDownloader.start(getParams());
            } else if (status == DOWNLOAD_STATUS_ERROR) {
                downloadView.setProgress(progress);
                downloadView.setDownloading();
                if ("无法下载".equals(entry.downloadUrl)) {
                    downloadView.setDownloadingText("无法下载");
                    return;
                }
                FileDownloader.start(getParams());
//                FileDownloader.delete(entry.downloadUrl, true, null);
//                FileDownloader.reStart(getParams());
            } else if (status == DOWNLOAD_STATUS_DOWNLOADING ||
                    status == DOWNLOAD_STATUS_RETRYING ||
                    status == DOWNLOAD_STATUS_PREPARED ||
                    status == DOWNLOAD_STATUS_PREPARING) {
                downloadView.setPause();
                FileDownloader.pause(entry.downloadUrl);
            } else if (status == DOWNLOAD_STATUS_COMPLETED) {
                downloadView.setDownloadingText("安装");
                ApkUtil.installApk(AppContext.getAppContext(), info.getFilePath());
            } else if (status == DOWNLOAD_STATUS_FILE_NOT_EXIST || status == DOWNLOAD_STATUS_UNKNOWN) {
                downloadView.setDownloading();
                if ("无法下载".equals(entry.downloadUrl)) {
                    downloadView.setDownloadingText("无法下载");
                    return;
                }
                downloadView.setProgress(0);
//                FileDownloader.delete(entry.downloadUrl, true, null);
                FileDownloader.reStart(getParams());
            } else if (status == DOWNLOAD_STATUS_WAITING) {
                // 跳转到下载管理
                downloadView.setDownloading();
                downloadView.setProgress(progress);
                View downloadView = (View) this.downloadView;
                downloadView.getContext().startActivity(new Intent(downloadView.getContext(), DownloadListActivity.class));
            } else {
                downloadView.setDownloading();
                downloadView.setProgress(progress);
                FileDownloader.start(getParams());
            }
        }
    }

    public Context getDownloadContext() {
        return AppContext.getAppContext();
    }

    public void startDownload() {
        if (entry.downloadUrl.equals("无法下载")) {
            return;
        }
        if (!entry.downloadUrl.contains(".apk")) {
            CHToast.show(getDownloadContext(), "地址无效");
            downloadView.setDownloadingText("地址无效");
            return;
        }
        DownloadFileInfo info = FileDownloader.getDownloadFile(entry.downloadUrl);
        if (info == null) {
            FileDownloader.start(getParams());
            downloadView.setProgress(0);
            downloadView.setDownloading();
        } else {
            int status = info.getStatus();
            switch (status) {
                case DOWNLOAD_STATUS_COMPLETED:
                    downloadView.setDownloadingText("安装");
                    break;
                case DOWNLOAD_STATUS_PAUSED:
                    downloadView.setDownloading();
                    FileDownloader.start(getParams());
                    break;
            }
        }
    }

    private void registerDownloadListener(String url) {
        unRegister(url);
        if (downloadListener == null) {
            lastRegisterUrl = url;
            downloadListener = new DownloadListener();
            DownloadFileChangeConfiguration.Builder builder = new DownloadFileChangeConfiguration.Builder();
            builder.addListenUrl(url);
            FileDownloader.registerDownloadFileChangeListener(downloadListener, builder.build());
        }

        if (fileStatusListener == null) {
            fileStatusListener = new FileStatusListener();
            DownloadStatusConfiguration.Builder builder1 = new DownloadStatusConfiguration.Builder();
            builder1.addListenUrl(url);
            FileDownloader.registerDownloadStatusListener(fileStatusListener, builder1.build());
        }
    }

    private void unRegister(String url) {
        if (url.equals(lastRegisterUrl)) {
            return;
        }
        if (downloadListener != null) {
            FileDownloader.unregisterDownloadFileChangeListener(downloadListener);
            downloadListener = null;
        }
        if (fileStatusListener != null) {
            FileDownloader.unregisterDownloadStatusListener(fileStatusListener);
            fileStatusListener = null;
        }
    }

    public void release() {
        if (downloadListener != null) {
            FileDownloader.unregisterDownloadFileChangeListener(downloadListener);
        }

        if (fileStatusListener != null) {
            FileDownloader.unregisterDownloadStatusListener(fileStatusListener);
        }
        EventBus.getDefault().unregister(this);
    }

    class FileStatusListener implements OnFileDownloadStatusListener {

        private DownloadNo downloadNo;

        public FileStatusListener() {
            downloadNo = DownloadNo.getDownloadNo(getDownloadContext());
        }

        @Override
        public void onFileDownloadStatusWaiting(DownloadFileInfo downloadFileInfo) {
            LogUtil.errorLog("FileStatusListener onFileDownloadStatusWaiting");
            if (downloadNo != null) {
                downloadNo.start();
            }
        }

        @Override
        public void onFileDownloadStatusPreparing(DownloadFileInfo downloadFileInfo) {
            LogUtil.errorLog("FileStatusListener onFileDownloadStatusPreparing");
        }

        @Override
        public void onFileDownloadStatusPrepared(DownloadFileInfo downloadFileInfo) {
            LogUtil.errorLog("FileStatusListener onFileDownloadStatusPrepared");
        }

        @Override
        public void onFileDownloadStatusDownloading(DownloadFileInfo downloadFileInfo, float downloadSpeed, long remainingTime) {
            LogUtil.errorLog("FileStatusListener onFileDownloadStatusDownloading：" + getDownloadPercent(downloadFileInfo));
            if (downloadNo != null) {
                int percent = getDownloadPercent(downloadFileInfo);
                if (percent % 5 == 0 || percent % 3 == 0) {
                    downloadNo.downloading(percent, entry.getIconUrl(), entry.getTitle());
                }
            }
        }

        @Override
        public void onFileDownloadStatusPaused(DownloadFileInfo downloadFileInfo) {
            LogUtil.errorLog("FileStatusListener onFileDownloadStatusPaused");
        }

        @Override
        public void onFileDownloadStatusCompleted(DownloadFileInfo downloadFileInfo) {
            if (downloadNo != null) {
                downloadNo.completed();
            }
            LogUtil.errorLog("FileStatusListener onFileDownloadStatusCompleted");
            new DoneTaskLogic(DoneTaskLogic.TASK_DOWNLOAD).getDoneTask();
            ApkUtil.installApk(getDownloadContext(), downloadFileInfo.getFilePath());
        }

        @Override
        public void onFileDownloadStatusFailed(String url, DownloadFileInfo downloadFileInfo, FileDownloadStatusFailReason failReason) {
            handleStorageError(failReason);
            if (downloadFileInfo != null) {
                int progress = getDownloadPercent(downloadFileInfo);
                if (progress > 100) {
                    downloadView.setProgress(0);
                    deleteFile(downloadFileInfo);
                }
            }
            downloadView.setError();
        }
    }

    class DownloadListener implements OnDownloadFileChangeListener {

        @Override
        public void onDownloadFileCreated(DownloadFileInfo downloadFileInfo) {
            LogUtil.errorLog("ViewDownloadMgr onDownloadFileCreated" + downloadFileInfo.getPkg());
        }

        @Override
        public void onDownloadFileUpdated(DownloadFileInfo downloadFileInfo, Type type) {
            if (!TextUtils.isEmpty(entry.downloadUrl) && entry.downloadUrl.equals(downloadFileInfo.getUrl())) {
                LogUtil.errorLog("ViewDownloadMgr onDownloadFileUpdated" + downloadFileInfo.getPkg());
                setStatus(downloadFileInfo);
            }
        }

        @Override
        public void onDownloadFileDeleted(DownloadFileInfo downloadFileInfo) {
            if (downloadFileInfo == null)
                return;

            if (isAppInstalled() && !isUpdate) {
                downloadView.setDownloadingText("打开");
            } else if (isAppInstalled() && isUpdate) {
                downloadView.setDownloadingText("更新");
            } else {
                downloadView.setDownloadingText("下载");
            }
        }
    }

    private int getDownloadPercent(DownloadFileInfo info) {
        double percent = ((double) info.getDownloadedSizeLong()) / ((double) info
                .getFileSizeLong());
        return (int) (percent * 100);
    }

    private int getNetworkType() {
        ConnectivityManager connectMgr = (ConnectivityManager) AppContext.getAppContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectMgr != null) {
            NetworkInfo info = connectMgr.getActiveNetworkInfo();
            if (info != null) {
                return info.getType();
            } else {
                return -1;
            }
        }
        return -1;
    }

    private void deleteFile(DownloadFileInfo downloadFile) {
        String downloadDir = FileDownloader.getDownloadDir();
        File file = new File(downloadDir + "/" + downloadFile.getFileName());
        if (file.exists()) {
            file.delete();
        }
    }

    private static long lastToastTime;

    public static void handleStorageError(OnFileDownloadStatusListener.FileDownloadStatusFailReason failReason) {
        if (System.currentTimeMillis() - lastToastTime > 5000 ||
                lastToastTime - System.currentTimeMillis() > 0) {
            if (failReason != null && OnFileDownloadStatusListener.OnFileDownloadStatusFailReason.TYPE_STORAGE_SPACE_IS_FULL.equals(failReason.getType())) {
                lastToastTime = System.currentTimeMillis();
                CHToast.show(AppContext.getAppContext(), "下载失败,当前的存储空间不足");
            }
        }
    }
}

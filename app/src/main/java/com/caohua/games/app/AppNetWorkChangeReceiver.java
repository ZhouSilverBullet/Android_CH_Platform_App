package com.caohua.games.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;

import com.caohua.games.biz.download.DownloadIsInsert;
import com.caohua.games.biz.gift.InitGiftLogic;
import com.caohua.games.biz.task.DoneTaskLogic;
import com.chsdk.api.AppOperator;
import com.chsdk.biz.game.TokenRefreshLogic;
import com.chsdk.configure.DataStorage;
import com.chsdk.utils.LocationHelper;
import com.chsdk.utils.LogUtil;

import org.greenrobot.eventbus.EventBus;
import org.wlf.filedownloader.DownloadFileInfo;
import org.wlf.filedownloader.FileDownloader;

import java.util.List;

import static org.wlf.filedownloader.base.Status.DOWNLOAD_STATUS_DOWNLOADING;
import static org.wlf.filedownloader.base.Status.DOWNLOAD_STATUS_PREPARED;
import static org.wlf.filedownloader.base.Status.DOWNLOAD_STATUS_PREPARING;
import static org.wlf.filedownloader.base.Status.DOWNLOAD_STATUS_RETRYING;
import static org.wlf.filedownloader.base.Status.DOWNLOAD_STATUS_WAITING;

/**
 * Created by admin on 2016/11/16.
 */

public class AppNetWorkChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, final Intent intent) {
        if (intent == null)
            return;

        if (!AppContext.getAppContext().isCaoHuaProcess(AppContext.getAppContext().getCurProcessName())) {
            return;
        }

        String action = intent.getAction();
        if (TextUtils.isEmpty(action))
            return;

        if (action.equals(Intent.ACTION_PACKAGE_ADDED)) {
            handleAppInstalled(context, intent);
        } else if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            handleNetwork(context);
        }
    }

    private void handleAppInstalled(final Context context, Intent intent) {
        if (intent.getData() == null)
            return;

        final String insertPkg = intent.getData().getSchemeSpecificPart();
        if (TextUtils.isEmpty(insertPkg))
            return;
        new DoneTaskLogic(DoneTaskLogic.TASK_INSTALLATION, insertPkg).getDoneTask();
        EventBus.getDefault().post(new DownloadIsInsert(true, insertPkg));
        LogUtil.errorLog("handleAppInstalled:" + insertPkg);

        AppOperator.runOnThread(new Runnable() {
            @Override
            public void run() {
                boolean pkgDelete = DataStorage.getPkgDelete(context);
                if (!pkgDelete) {
                    return;
                }

                final List<DownloadFileInfo> downloadFiles = FileDownloader.getDownloadFiles();
                if (downloadFiles == null || downloadFiles.isEmpty()) {
                    return;
                }

                for (DownloadFileInfo downloadFile : downloadFiles) {
                    if (downloadFile.getPkg().endsWith(insertPkg)) {
                        FileDownloader.delete(downloadFile.getUrl(), true, null);
                    }
                }
            }
        });
    }

    private void handleNetwork(final Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetworkInfo = manager.getActiveNetworkInfo();

        if (activeNetworkInfo != null && (activeNetworkInfo.isAvailable() || activeNetworkInfo.isConnected())) {
            String giftMoreUrl = DataStorage.getGiftMoreUrl(context);
            String giftInterceptUrl = DataStorage.getGiftInterceptUrl(context);
            if (TextUtils.isEmpty(giftMoreUrl) || TextUtils.isEmpty(giftInterceptUrl)) {
                new InitGiftLogic().getInitGiftLogic();
            }
            LocationHelper.initLocation(context.getApplicationContext());
            LogUtil.errorLog("handleNetwork TokenRefreshLogic");
            TokenRefreshLogic.refresh(null);
        }

        AppOperator.runOnThread(new Runnable() {
            @Override
            public void run() {
                boolean wifiDownload = DataStorage.getWifiDownload(context);
                if (!wifiDownload) {
                    return;
                }

                if (activeNetworkInfo == null || activeNetworkInfo.getType() != ConnectivityManager.TYPE_WIFI) {
                    List<DownloadFileInfo> downloadFiles = FileDownloader.getDownloadFiles();
                    if (downloadFiles == null)
                        return;

                    for (DownloadFileInfo downloadFile : downloadFiles) {
                        switch (downloadFile.getStatus()) {
                            case DOWNLOAD_STATUS_DOWNLOADING:
                            case DOWNLOAD_STATUS_RETRYING:
                            case DOWNLOAD_STATUS_PREPARED:
                            case DOWNLOAD_STATUS_PREPARING:
                            case DOWNLOAD_STATUS_WAITING:
                                FileDownloader.pauseAll();
                                break;
                        }
                    }
                }
            }
        });
    }
}

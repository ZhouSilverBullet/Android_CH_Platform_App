package com.caohua.games.ui.download;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.caohua.games.R;
import com.caohua.games.app.AppContext;
import com.caohua.games.biz.download.DownloadIsInsert;
import com.caohua.games.biz.download.DownloadNo;
import com.caohua.games.biz.download.ViewDownloadMgr;
import com.caohua.games.biz.task.DoneTaskLogic;
import com.caohua.games.ui.HomePagerActivity;
import com.chsdk.configure.DataStorage;
import com.chsdk.ui.widget.CHAlertDialog;
import com.chsdk.ui.widget.CHToast;
import com.chsdk.ui.widget.RiffEffectLinearLayout;
import com.chsdk.ui.widget.RippleEffectButton;
import com.chsdk.utils.ApkUtil;
import com.chsdk.utils.LogUtil;
import com.chsdk.utils.PicUtil;
import com.chsdk.utils.ViewUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.wlf.filedownloader.DownloadFileChangeConfiguration;
import org.wlf.filedownloader.DownloadFileInfo;
import org.wlf.filedownloader.DownloadStatusConfiguration;
import org.wlf.filedownloader.FileDownloader;
import org.wlf.filedownloader.base.DownloadParams;
import org.wlf.filedownloader.base.Status;
import org.wlf.filedownloader.listener.OnDownloadFileChangeListener;
import org.wlf.filedownloader.listener.OnFileDownloadStatusListener;

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
 * Created by ZengLei on 2016/10/27.
 */

public class DownloadItemView extends RiffEffectLinearLayout implements View.OnClickListener {

    private ImageView imgIcon;
    private TextView tvTitle, tvStatus, tvPercent;
    private ProgressBar progressBar;
    private RippleEffectButton btnDownStatus;

    private String lastRegisterUrl;
    private DownloadFileInfo info;
    private OnDownloadListener listener;
    private DownloadListener downloadListener;
    private FileStatusListener fileStatusListener;

    private boolean isClickDown;

    public DownloadItemView(Context context) {
        super(context);
        loadXml();
        setOrientation(HORIZONTAL);
        setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT));
        setMinimumHeight(ViewUtil.dp2px(getContext(), 100));
    }

    public DownloadItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        loadXml();
    }

    private void loadXml() {
        LayoutInflater.from(getContext()).inflate(R.layout.ch_download_item, this, true);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        initView();
    }

    public void setData(DownloadFileInfo info) {
        if (info == null || TextUtils.isEmpty(info.getUrl()))
            return;

        if (this.info == null || !this.info.getIconUrl().equals(info.getIconUrl())) {
            PicUtil.displayImg(getContext(), imgIcon, info.getIconUrl(), R.drawable.ch_default_apk_icon);
        }

        this.info = info;
        setView(info, true);
        registerDownloadListener(info.getUrl());
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        isClickDown = true;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        isClickDown = false;
                        break;
                }
                return false;
            }
        });
    }

    private void setView(DownloadFileInfo info, boolean refresh) {
        if (info == null)
            return;

        if (isClickDown && !refresh)
            return;

        tvTitle.setText(info.getTitle());
        setStatus(info);
    }

    private void setStatus(DownloadFileInfo downloadFileInfo) {
        String sizePercent = null;
        String pkg = downloadFileInfo.getPkg();
        if (!TextUtils.isEmpty(pkg) && isAppInstalled(pkg)) {
            hideProgress();
            tvStatus.setText("安装完成");
            btnDownStatus.setText("打开");
            btnDownStatus.setBackgroundResource(R.drawable.download_button_shape_open);
            FileDownloader.pause(info.getUrl());   //正在下载，就停止它
            return;
        }

        switch (downloadFileInfo.getStatus()) {
            case DOWNLOAD_STATUS_FILE_NOT_EXIST:
                hideProgress();
                tvStatus.setText("文件不存在");
                btnDownStatus.setText("重试");
                btnDownStatus.setBackgroundResource(R.drawable.download_button_shape_downloading);
                break;
            case DOWNLOAD_STATUS_ERROR:
                hideProgress();
                tvStatus.setText("下载错误");
                btnDownStatus.setText("重试");
                btnDownStatus.setBackgroundResource(R.drawable.download_button_shape_downloading);
                break;
            case DOWNLOAD_STATUS_UNKNOWN:
                hideProgress();
                tvStatus.setText("下载错误");
                btnDownStatus.setText("重试");
                btnDownStatus.setBackgroundResource(R.drawable.download_button_shape_downloading);
                break;
            case DOWNLOAD_STATUS_PAUSED:
                showProgress(getPercent(info));
                btnDownStatus.setText("继续");
                btnDownStatus.setBackgroundResource(R.drawable.download_button_shape_pause);
                break;
            case DOWNLOAD_STATUS_WAITING:
                hideProgress();
                tvStatus.setText("正等待下载");
                btnDownStatus.setText("暂停");
                btnDownStatus.setBackgroundResource(R.drawable.download_button_shape_stop);
                break;
            case DOWNLOAD_STATUS_DOWNLOADING:
            case DOWNLOAD_STATUS_RETRYING:
//                tvText.setStatus("重试：重连资源");
            case DOWNLOAD_STATUS_PREPARING:
//                tvText.setStatus("正在获取资源");
            case DOWNLOAD_STATUS_PREPARED:
//                tvText.setStatus("已连接资源");
                showProgress(getPercent(info));
                btnDownStatus.setText("暂停");
                btnDownStatus.setBackgroundResource(R.drawable.download_button_shape_stop);
                break;
            case DOWNLOAD_STATUS_COMPLETED:
                hideProgress();
                String packageName = ApkUtil.getUnInstallApkPackageName(getContext(), downloadFileInfo.getFilePath());
                boolean isInstall = ApkUtil.checkAppInstalled(getContext(), packageName);
                if (isInstall) {
                    btnDownStatus.setText("打开");
                    btnDownStatus.setBackgroundResource(R.drawable.download_button_shape_normal);
                    tvStatus.setText("安装完成");
                } else {
                    tvStatus.setText("下载完成");
                    btnDownStatus.setText("安装");
                    btnDownStatus.setBackgroundResource(R.drawable.download_button_shape_pause);
                }
                break;
        }
    }

    @Subscribe
    public void changeOpen(DownloadIsInsert insert) {
        String pkg = insert.getPkg();
        if (info.getPkg().equals(pkg)) {
            btnDownStatus.setText("打开");
            btnDownStatus.setBackgroundResource(R.drawable.download_button_shape_open);
            tvStatus.setText("安装完成");
        }
    }

    private String getSizePercent(DownloadFileInfo info) {
        return getSize(info.getDownloadedSizeLong()) + "/"
                + getSize(info.getFileSizeLong());
    }

    private int getPercent(DownloadFileInfo info) {
        double doublePercent = ((double) info.getDownloadedSizeLong()) / ((double) info.getFileSizeLong());
        int percent = (int) (doublePercent * 100);
        return percent;
    }

    private void showProgress(int percent) {
        progressBar.setVisibility(VISIBLE);
        tvPercent.setVisibility(VISIBLE);
        progressBar.setProgress(percent);
        tvPercent.setText(percent + "%");
        tvStatus.setText(getSizePercent(info));
    }

    private void hideProgress() {
        progressBar.setVisibility(INVISIBLE);
        tvPercent.setVisibility(INVISIBLE);
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
            DownloadStatusConfiguration.Builder builder = new DownloadStatusConfiguration.Builder();
            builder.addListenUrl(url);
            FileDownloader.registerDownloadStatusListener(fileStatusListener, builder.build());
        }
    }

    public void unRegister(String url) {
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

    private String getSize(long size) {
        return Formatter.formatFileSize(getContext(), size);
    }

    private void initView() {
        btnDownStatus = (RippleEffectButton) findViewById(R.id.ch_download_item_btn);
        imgIcon = (ImageView) findViewById(R.id.ch_download_item_icon);
        tvTitle = (TextView) findViewById(R.id.ch_download_item_title);
        tvStatus = (TextView) findViewById(R.id.ch_download_item_size);
        tvPercent = (TextView) findViewById(R.id.ch_download_item_percent);
        progressBar = (ProgressBar) findViewById(R.id.ch_download_item_progress);
        btnDownStatus.setOnClickListener(this);
        setClickable(true);
        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showDialog(info);
                return true;
            }
        });
    }

    private boolean isAppInstalled(String pkg) {
        return ApkUtil.checkAppInstalled(AppContext.getAppContext(), pkg);
    }

    private void showDialog(final DownloadFileInfo downloadFileInfo) {
        if (downloadFileInfo == null)
            return;

        int code = 0;
        String[] items = null;
        if (isAppInstalled(downloadFileInfo.getPkg())) {
            code = 1;
            items = new String[]{"打开", "删除"};
        } else {
            int status = downloadFileInfo.getStatus();
            if (status == Status.DOWNLOAD_STATUS_FILE_NOT_EXIST ||
                    status == Status.DOWNLOAD_STATUS_UNKNOWN) {
                code = 2;
                items = new String[]{"重试", "删除"};
            } else if (status == Status.DOWNLOAD_STATUS_COMPLETED) {
                code = 3;
                items = new String[]{"安装", "删除"};
            } else if (status == Status.DOWNLOAD_STATUS_DOWNLOADING ||
                    status == Status.DOWNLOAD_STATUS_WAITING ||
                    status == Status.DOWNLOAD_STATUS_PREPARING ||
                    status == Status.DOWNLOAD_STATUS_PREPARED ||
                    status == Status.DOWNLOAD_STATUS_RETRYING) {
                code = 4;
                items = new String[]{"暂停", "删除"};
            } else if (status == Status.DOWNLOAD_STATUS_PAUSED) {
                code = 5;
                items = new String[]{"继续", "删除"};
            } else if (status == Status.DOWNLOAD_STATUS_ERROR) {
                code = 6;
                items = new String[]{"重试", "删除"};
            }
        }

        items = new String[]{"删除"};
        code = 7;

        final int statusCode = code;
        new AlertDialog.Builder(getContext()).setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    if (statusCode == 1) {
                        ApkUtil.startAPP(getContext(), downloadFileInfo.getPkg());
                    } else if (statusCode == 2) {
                        FileDownloader.reStart(getDownloadParams(downloadFileInfo));
//                        FileDownloader.delete(downloadFileInfo.getUrl(), true, null);
                    } else if (statusCode == 3) {
                        ApkUtil.installApk(getContext(), info.getFilePath());
                    } else if (statusCode == 4) {
                        FileDownloader.pause(downloadFileInfo.getUrl());
                    } else if (statusCode == 5) {
                        FileDownloader.start(getDownloadParams(downloadFileInfo));
                    } else if (statusCode == 6) {
                        FileDownloader.start(getDownloadParams(downloadFileInfo));
                    } else {
                        FileDownloader.delete(downloadFileInfo.getUrl(), true, null);
                        if (listener != null) {
                            listener.deleteItem(downloadFileInfo);
                        }
                    }
                } else if (i == 1) {
                    FileDownloader.delete(downloadFileInfo.getUrl(), true, null);
                    if (listener != null) {
                        listener.deleteItem(downloadFileInfo);
                    }
                }
            }
        }).create().show();
    }

    private DownloadParams getDownloadParams(DownloadFileInfo downloadFileInfo) {
        DownloadParams params = new DownloadParams();
        params.url = downloadFileInfo.getUrl();
        params.title = downloadFileInfo.getTitle();
        params.pkg = downloadFileInfo.getPkg();
        params.iconUrl = downloadFileInfo.getIconUrl();
        return params;
    }

    public void setDeleteListener(OnDownloadListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        if (v == this) {
            showDialog(info);
        } else if (btnDownStatus == v) {
            handleBtnClick();
        }
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

    private void handleBtnClick() {
        if (info == null)
            return;

        if (isAppInstalled(info.getPkg())) {
            ApkUtil.startAPP(getContext(), info.getPkg());
            return;
        }

        if (TextUtils.isEmpty(info.getUrl())) {
            CHToast.show(getContext(), "无效的下载地址");
            return;
        }

        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (!EasyPermissions.hasPermissions(getContext(), perms)) {
            EasyPermissions.requestPermissions((Activity) getContext(), "请授予手机读写权限", HomePagerActivity.PERMISSION_FOR_WRITE_STORAGE, perms);
            return;
        }

        final DownloadFileInfo downloadFileInfo = FileDownloader.getDownloadFile(info.getUrl());
        if (downloadFileInfo == null) {
            FileDownloader.start(getParams(info));
        } else {
            int status = downloadFileInfo.getStatus();
            if (status == DOWNLOAD_STATUS_PAUSED) {
                boolean wifiDownload = DataStorage.getWifiDownload(getContext());
                if (wifiDownload && getNetworkType() != ConnectivityManager.TYPE_WIFI) {
                    final CHAlertDialog chAlertDialog = new CHAlertDialog((Activity) getContext());
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
                            CHToast.show(getContext(), "建议您在wifi条件下载");
                            FileDownloader.start(getParams(downloadFileInfo));
                            chAlertDialog.dismiss();
                        }
                    });
                } else {
                    FileDownloader.start(getParams(downloadFileInfo));
                }
            } else if (status == DOWNLOAD_STATUS_ERROR) {
                FileDownloader.start(getParams(downloadFileInfo));
            } else if (status == DOWNLOAD_STATUS_DOWNLOADING ||
                    status == DOWNLOAD_STATUS_RETRYING ||
                    status == DOWNLOAD_STATUS_PREPARED ||
                    status == DOWNLOAD_STATUS_PREPARING) {
                FileDownloader.pause(downloadFileInfo.getUrl());
            } else if (status == DOWNLOAD_STATUS_COMPLETED) {
                ApkUtil.installApk(AppContext.getAppContext(), info.getFilePath());
            } else if (status == DOWNLOAD_STATUS_FILE_NOT_EXIST || status == DOWNLOAD_STATUS_UNKNOWN) {
                FileDownloader.reStart(getParams(downloadFileInfo));
            } else if (status == DOWNLOAD_STATUS_WAITING) {
                FileDownloader.pause(downloadFileInfo.getUrl());
            } else {
                FileDownloader.start(getParams(downloadFileInfo));
            }
        }
    }

    private DownloadParams getParams(DownloadFileInfo downloadFileInfo) {
        DownloadParams params = new DownloadParams();
        params.url = downloadFileInfo.getUrl();
        params.title = downloadFileInfo.getTitle();
        params.pkg = downloadFileInfo.getPkg();
        params.iconUrl = downloadFileInfo.getIconUrl();
        return params;
    }

    public static interface OnDownloadListener {
        void deleteItem(DownloadFileInfo info);

        void completed(DownloadFileInfo info);
    }

    class FileStatusListener implements OnFileDownloadStatusListener {

        private DownloadNo downloadNo;

        public FileStatusListener() {
            downloadNo = DownloadNo.getDownloadNo(getContext());
        }

        @Override
        public void onFileDownloadStatusWaiting(DownloadFileInfo downloadFileInfo) {
            if (downloadNo != null) {
                LogUtil.errorLog("DownloadItemView onFileDownloadStatusWaiting");
                downloadNo.start();
            }
        }

        @Override
        public void onFileDownloadStatusPreparing(DownloadFileInfo downloadFileInfo) {

        }

        @Override
        public void onFileDownloadStatusPrepared(DownloadFileInfo downloadFileInfo) {

        }

        @Override
        public void onFileDownloadStatusDownloading(DownloadFileInfo downloadFileInfo, float downloadSpeed, long remainingTime) {
            if (downloadNo != null) {
                LogUtil.errorLog("DownloadItemView onFileDownloadStatusDownloading");
                int percent = getDownloadPercent(downloadFileInfo);
                if (percent % 5 == 0 || percent % 3 == 0) {
                    downloadNo.downloading(percent, getParams(downloadFileInfo).iconUrl, getParams(downloadFileInfo).title);
                }
            }
        }

        @Override
        public void onFileDownloadStatusPaused(DownloadFileInfo downloadFileInfo) {

        }

        @Override
        public void onFileDownloadStatusCompleted(DownloadFileInfo downloadFileInfo) {
            if (downloadNo != null) {
                LogUtil.errorLog("DownloadItemView onFileDownloadStatusCompleted");
                downloadNo.completed();
            }
            if (listener != null) {
                listener.completed(downloadFileInfo);
                new DoneTaskLogic(DoneTaskLogic.TASK_DOWNLOAD).getDoneTask();
                ApkUtil.installApk(getContext(), info.getFilePath());
            }
        }

        @Override
        public void onFileDownloadStatusFailed(String url, DownloadFileInfo downloadFileInfo, FileDownloadStatusFailReason failReason) {
            ViewDownloadMgr.handleStorageError(failReason);
            hideProgress();
            tvStatus.setText("下载错误");
            btnDownStatus.setText("重试");
            btnDownStatus.setBackgroundResource(R.drawable.download_button_shape_pause);
        }
    }

    private int getDownloadPercent(DownloadFileInfo info) {
        double percent = ((double) info.getDownloadedSizeLong()) / ((double) info
                .getFileSizeLong());
        return (int) (percent * 100);
    }

    class DownloadListener implements OnDownloadFileChangeListener {

        @Override
        public void onDownloadFileCreated(DownloadFileInfo downloadFileInfo) {
        }

        @Override
        public void onDownloadFileUpdated(DownloadFileInfo downloadFileInfo, Type type) {
            setView(downloadFileInfo, false);
        }

        @Override
        public void onDownloadFileDeleted(DownloadFileInfo downloadFileInfo) {
        }
    }
}

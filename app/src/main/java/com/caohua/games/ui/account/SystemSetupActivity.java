package com.caohua.games.ui.account;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;
import com.caohua.games.R;
import com.caohua.games.app.AppUpdateLogic;
import com.caohua.games.ui.BaseActivity;
import com.chsdk.api.AppOperator;
import com.chsdk.api.CacheManager;
import com.chsdk.biz.BaseLogic;
import com.chsdk.configure.DataStorage;
import com.chsdk.model.game.UpdateEntry;
import com.chsdk.ui.UpdateGameDialog;
import com.chsdk.ui.widget.CHAlertDialog;
import com.chsdk.ui.widget.CHToast;
import com.chsdk.ui.widget.LoadingDialog;

import java.io.File;

/**
 * Created by CXK on 2016/11/3.
 */

public class SystemSetupActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener {
    private ToggleButton icon, iconTwo;
    private RelativeLayout about, updata, cache;
    private TextView clearCache;
    private String fileCacheDir;
    private String picCacheDir;
    private String glidCacheDir;
    private String externalPicCacheDir;
    private TextView versionNameText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ch_activity_system_setup);

        fileCacheDir = getFilesDir().getAbsolutePath() + File.separator;
        glidCacheDir = getCacheDir().getAbsolutePath() + File.separator + InternalCacheDiskCacheFactory.DEFAULT_DISK_CACHE_DIR;
        picCacheDir = getCacheDir().getAbsolutePath() + "/CaoHuaSDK/ad/";
        try {
            externalPicCacheDir = getExternalCacheDir().getAbsolutePath() + "/CaoHuaSDK/ad/";
        } catch (Exception e) {
            externalPicCacheDir = "";
        }

        initView();
        setCacheSize();
    }

    private void setCacheSize() {
        AppOperator.runOnThread(new Runnable() {
            @Override
            public void run() {
                final String size = getTotalCacheSize();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        clearCache.setText(size);
                    }
                });
            }
        });
    }

    private void initView() {
        clearCache = (TextView) findViewById(R.id.ch_activity_system_setup_cache_clear);
        icon = (ToggleButton) findViewById(R.id.ch_switch_one);
        iconTwo = (ToggleButton) findViewById(R.id.ch_switch_two);

        boolean wifiDownload = DataStorage.getWifiDownload(getApplicationContext());
        icon.setChecked(wifiDownload);
        boolean pkgDelete = DataStorage.getPkgDelete(getApplicationContext());
        iconTwo.setChecked(pkgDelete);

        about = (RelativeLayout) findViewById(R.id.ch_activity_system_setup_about);
        updata = (RelativeLayout) findViewById(R.id.ch_activity_system_setup_update);
        cache = (RelativeLayout) findViewById(R.id.ch_activity_system_setup_cache);
        versionNameText = getView(R.id.ch_activity_system_setup_version_name);
        versionNameText.setText(getVersionName());
        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SystemSetupActivity.this, AboutActivity.class));
            }
        });
        updata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkVersion();
            }
        });
        cache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteDialog();
            }
        });
        icon.setOnCheckedChangeListener(this);
        iconTwo.setOnCheckedChangeListener(this);
    }

    private void checkVersion() {
        final LoadingDialog loadingDialog = new LoadingDialog(this, "");
        loadingDialog.show();

        new AppUpdateLogic().initSdk(new BaseLogic.AppLogicListner() {
            @Override
            public void failed(String errorMsg) {
                loadingDialog.dismiss();
                CHToast.show(getApplicationContext(), "检查失败:" + errorMsg);
            }

            @Override
            public void success(Object entryResult) {
                loadingDialog.dismiss();
                if (entryResult != null) {
                    try {
                        if (entryResult instanceof UpdateEntry) {
                            UpdateEntry entry = (UpdateEntry) entryResult;
                            UpdateGameDialog upgradeUtil = new UpdateGameDialog(
                                    SystemSetupActivity.this, entry);
                            upgradeUtil.showDialog();
                        }
                        return;
                    } catch (Exception e) {
                    }
                }
                CHToast.show(getApplicationContext(), "已经是最新版本");
            }
        });
    }

    public String getVersionName() {
        try {
            PackageManager manager = this.getPackageManager();
            PackageInfo info = manager.getPackageInfo(getPackageName(), 0);
            String version = info.versionName;
            return "版本号为 : V" + version;
        } catch (Exception e) {
            return "版本号为 : V2.1.1";
        }
    }

    private void showDeleteDialog() {
        final CHAlertDialog chAlertDialog = new CHAlertDialog(this);
        chAlertDialog.show();
        chAlertDialog.setContent("确定删除缓存吗?");
        chAlertDialog.setTitle("警告！");
        Dialog dialog = chAlertDialog.getDialog();
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        chAlertDialog.setOkButton("返回", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chAlertDialog.dismiss();
            }
        });
        chAlertDialog.setCancelButton("删除", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chAlertDialog.dismiss();
                clearAllCache();
                clearCache.setText("0KB");
            }
        });
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.ch_switch_one:
                DataStorage.setWifiDownload(getApplicationContext(), isChecked);
                break;
            case R.id.ch_switch_two:
                DataStorage.setPkgDelete(getApplicationContext(), isChecked);
                break;
        }
    }

    private String getTotalCacheSize() {
        long cacheSize = 0;
        cacheSize += getPicCacheSize();
        cacheSize += getFileCacheSize();
        return Formatter.formatFileSize(this, cacheSize);
    }

    private long getPicCacheSize() {
        long cacheSize = 0;
        cacheSize += getFolderSize(new File(picCacheDir));
        cacheSize += getFolderSize(new File(externalPicCacheDir));
        cacheSize += getFolderSize(new File(glidCacheDir));
        return cacheSize;
    }

    private long getFileCacheSize() {
        long cacheSize = 0;
        File file = new File(fileCacheDir);
        if (file.exists() && file.isDirectory()) {
            File[] children = file.listFiles();
            if (children == null)
                return cacheSize;

            final String cachePrefix = CacheManager.CACHE_PREFIX;
            for (File child : children) {
                if (child.getName().startsWith(cachePrefix)) {
                    cacheSize += child.length();
                }
            }
        }
        return cacheSize;
    }

    public void clearAllCache() {
        Glide.get(SystemSetupActivity.this).clearMemory();
        AppOperator.runOnThread(new Runnable() {
            @Override
            public void run() {
                Glide.get(SystemSetupActivity.this).clearDiskCache();
                File outAdDir = new File(externalPicCacheDir);
                checkFileExists(outAdDir);
                File cacheAd = new File(picCacheDir);
                checkFileExists(cacheAd);
                File file = new File(fileCacheDir);
                checkFileExists(file);
            }
        });
    }

    private void checkFileExists(final File file) {
        if (file.exists() && file.length() > 0) {
            deleteDir(file);
        }
    }

    private boolean deleteDir(File dir) {
        if (dir == null) {
            return false;
        }
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }

    private long getFolderSize(File file) {
        if (!file.exists())
            return 0;

        long size = 0;
        try {
            File[] fileList = file.listFiles();
            for (int i = 0; i < fileList.length; i++) {
                if (fileList[i].isDirectory()) {
                    size = size + getFolderSize(fileList[i]);
                } else {
                    size = size + fileList[i].length();
                }
            }
        } catch (Exception e) {
        }
        return size;
    }
}


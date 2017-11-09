package com.caohua.games.ui.dataopen;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.caohua.games.R;
import com.caohua.games.app.AppContext;
import com.caohua.games.biz.dataopen.DataOpenActLogic;
import com.caohua.games.biz.dataopen.DataOpenGameBindLogic;
import com.caohua.games.biz.dataopen.DataOpenGiftInfoLogic;
import com.caohua.games.biz.dataopen.DataOpenHintLogic;
import com.caohua.games.biz.dataopen.DataOpenStartGameLogic;
import com.caohua.games.biz.dataopen.entry.DataOpenActEntry;
import com.caohua.games.biz.dataopen.entry.DataOpenGameBindEntry;
import com.caohua.games.biz.dataopen.entry.DataOpenGiftInfoEntry;
import com.caohua.games.biz.dataopen.entry.DataOpenHintEntry;
import com.caohua.games.biz.download.ViewDownloadMgr;
import com.caohua.games.ui.HomePagerActivity;
import com.caohua.games.ui.dataopen.widget.DataOpenActListView;
import com.caohua.games.ui.dataopen.widget.DataOpenGameBindLayout;
import com.caohua.games.ui.dataopen.widget.DataOpenGiftInfoLayout;
import com.caohua.games.ui.dataopen.widget.DataOpenHintView;
import com.caohua.games.ui.download.DownloadButton;
import com.caohua.games.ui.vip.CommonActivity;
import com.chsdk.biz.BaseLogic;
import com.chsdk.biz.download.DownloadEntry;
import com.chsdk.ui.widget.CHAlertDialog;
import com.chsdk.utils.ApkUtil;

import org.wlf.filedownloader.FileDownloader;
import org.wlf.filedownloader.base.DownloadParams;

import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by admin on 2017/9/19.
 */

public class DataOpenActivity extends CommonActivity {

    public static final String MANAGER_GAME_ID = "manager_game_id";

    private DataOpenActListView actView;
    private DataOpenHintView hintView;
    private String managerGameId;
    private DataOpenGameBindLayout bindView;
    private Button openGameBtn;
    private TextView openGametext;
    private ViewDownloadMgr downloadMgr;
    private DataOpenGiftInfoLayout giftView;
    private String titleName;
    private View openRl;

    @Override
    protected String subTitle() {
        return titleName;
    }

    @Override
    protected void initVariables() {
        Intent intent = getIntent();
        if (intent != null) {
            managerGameId = intent.getStringExtra(MANAGER_GAME_ID);
            titleName = intent.getStringExtra("titleName");
        }
    }

    @Override
    protected int childViewId() {
        return R.layout.ch_activity_data_open;
    }

    @Override
    protected void initView() {
        titleView.getSkipTitle().setVisibility(View.VISIBLE);
        titleView.getSkipTitle().setText("提醒管理");
        titleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataOpenNotifyActivity.startOpen(activity, managerGameId, titleName, DataOpenNotifyActivity.class);
            }
        });

        actView = getView(R.id.ch_activity_data_open_act);
        hintView = getView(R.id.ch_activity_data_open_hint);

        bindView = getView(R.id.ch_activity_data_open_bind);
        bindView.setGameBindNotifyListener(new DataOpenGameBindLayout.GameBindNotifyListener() {
            @Override
            public void onGameBindNotify() {
                loadData();
            }
        });

        openRl = getView(R.id.ch_data_open_rl);
        openRl.setVisibility(View.GONE);
        openGameBtn = getView(R.id.ch_data_open_rl_open_game);
        openGametext = getView(R.id.ch_data_open_rl_text);
        downloadMgr = new ViewDownloadMgr((DownloadButton) findViewById(R.id.ch_activity_data_open_gone_button));

        giftView = getView(R.id.ch_activity_data_open_gift);

    }

    @Override
    protected void loadData() {
        gameBindLogic();
        giftLogic();
        hintLogic();
        gameLogic();
        actLogic();
    }

    private void giftLogic() {
        showLoadProgress(true);
        new DataOpenGiftInfoLogic().openGiftInfo(managerGameId, new BaseLogic.DataLogicListner<DataOpenGiftInfoEntry>() {
            @Override
            public void failed(String errorMsg, int errorCode) {
                showLoadProgress(false);
                handleAllHaveData();
            }

            @Override
            public void success(DataOpenGiftInfoEntry entryResult) {
                showLoadProgress(false);
                hintLogic();
                if (entryResult != null) {
                    giftView.setData(entryResult, managerGameId);
                }
                handleAllHaveData();
            }
        });
    }

    boolean gameData;

    private void gameLogic() {
        showLoadProgress(true);
        new DataOpenStartGameLogic().openStartGame(managerGameId, new DataOpenStartGameLogic.DataLogicListener<DownloadEntry>() {
            @Override
            public void failed(String errorMsg, int errorCode) {
                showLoadProgress(false);
                gameData = false;
                openBtnHandler(false, null);
                openRl.setVisibility(gameData ? View.VISIBLE : View.GONE);
            }

            @Override
            public void success(DownloadEntry entryResult, String notice) {
                showLoadProgress(false);
                openGametext.setText(notice);
                if (entryResult != null) {
                    gameData = true;
                    openBtnHandler(true, entryResult);
                } else {
                    gameData = false;
                }
                openRl.setVisibility(gameData ? View.VISIBLE : View.GONE);
            }
        });
    }

    private void openBtnHandler(final boolean isSuccess, final DownloadEntry entry) {
        openGameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSuccess && entry != null) {
                    if (ApkUtil.checkAppInstalled(activity, entry.getPkg())) {
                        ApkUtil.startAPP(activity, entry.getPkg());
                    } else {
                        final CHAlertDialog dialog = new CHAlertDialog(activity);
                        dialog.show();
                        dialog.setTitle("提示");
                        dialog.setContent("您的手机当前可能未安装" + entry.getTitle() + "游戏");
                        dialog.setCancelButton("取消", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        dialog.setOkButton("下载", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                                if (!EasyPermissions.hasPermissions(activity, perms)) {
                                    EasyPermissions.requestPermissions(activity, "请授予手机读写权限", HomePagerActivity.PERMISSION_FOR_WRITE_STORAGE, perms);
                                    return;
                                }
                                DownloadParams params = new DownloadParams();
                                params.url = entry.downloadUrl;
                                params.title = entry.getTitle();
                                params.pkg = entry.getPkg();
                                params.iconUrl = entry.getIconUrl();
                                downloadMgr.setData(entry);

                                FileDownloader.start(params);
                                dialog.dismiss();
                            }
                        });
                    }
                }
            }
        });
    }

    private void hintLogic() {
        showLoadProgress(true);
        new DataOpenHintLogic().getOpenHint(managerGameId, new BaseLogic.DataLogicListner<List<DataOpenHintEntry>>() {
            @Override
            public void failed(String errorMsg, int errorCode) {
                showLoadProgress(false);
                handleAllHaveData();
            }

            @Override
            public void success(List<DataOpenHintEntry> entryResult) {
                showLoadProgress(false);
                if (entryResult != null) {
                    hintView.setData(entryResult, managerGameId, titleName);
                }
                handleAllHaveData();
            }
        });
    }

    private void gameBindLogic() {
        showLoadProgress(true);
        new DataOpenGameBindLogic().getDataOpenGame(managerGameId, new BaseLogic.DataLogicListner<DataOpenGameBindEntry>() {
            @Override
            public void failed(String errorMsg, int errorCode) {
                showLoadProgress(false);
                handleAllHaveData();
            }

            @Override
            public void success(DataOpenGameBindEntry entryResult) {
                showLoadProgress(false);
                if (entryResult != null) {
                    bindView.setData(entryResult, managerGameId);
                }
                handleAllHaveData();
            }
        });
    }

    private void actLogic() {
        showLoadProgress(true);
        new DataOpenActLogic().dataOpenAct(managerGameId, null, new BaseLogic.DataLogicListner<List<DataOpenActEntry>>() {
            @Override
            public void failed(String errorMsg, int errorCode) {
                showLoadProgress(false);
                handleAllHaveData();
            }

            @Override
            public void success(List<DataOpenActEntry> entryResult) {
                showLoadProgress(false);
                if (entryResult != null) {
                    actView.setData(managerGameId, entryResult);
                }
                handleAllHaveData();
            }
        });
    }

    @Override
    public void handleAllHaveData() {
        super.handleAllHaveData();
        if (actView.existBack() || bindView.existBack() || giftView.existBack() || hintView.existBack()) {
            showNoNetworkView(false);
            showEmptyView(false);
        } else {
            if (AppContext.getAppContext().isNetworkConnected()) {
                showNoNetworkView(true);
                return;
            }
            showEmptyView(true);
        }
    }

    public static void startOpen(Context context, String managerGameId, String titleName, Class clazz) {
        Intent intent = new Intent(context, clazz);
        intent.putExtra(MANAGER_GAME_ID, managerGameId);
        intent.putExtra("titleName", titleName);
        context.startActivity(intent);
    }
}

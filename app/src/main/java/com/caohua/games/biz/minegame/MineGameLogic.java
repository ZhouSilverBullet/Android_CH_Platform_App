package com.caohua.games.biz.minegame;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.caohua.games.app.AppContext;
import com.caohua.games.ui.minegame.MineGameActivity;
import com.chsdk.api.AppOperator;
import com.chsdk.api.CacheManager;
import com.chsdk.biz.BaseLogic;
import com.chsdk.biz.game.TokenRefreshLogic;
import com.chsdk.http.HttpConsts;
import com.chsdk.http.IRequestListener;
import com.chsdk.http.RequestExe;
import com.chsdk.model.BaseModel;
import com.chsdk.model.ResultBean;
import com.chsdk.utils.LogUtil;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by zhouzhou on 2017/2/22.
 */

public class MineGameLogic extends BaseLogic {
    private static final String MINE_GAME_PATH = "ucenter/myGame";
    private final MineGameActivity activity;
    private List<MineGameEntry> packageList;
    private AppLogicListner logicListner;

    public MineGameLogic(MineGameActivity activity) {
        this.activity = activity;
    }

    public void getMineGame(final AppLogicListner logicListner) {
        this.logicListner = logicListner;
        new Thread(){
            @Override
            public void run() {
                PackageManager packageManager = activity.getPackageManager();
                List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
                if (packageList == null) {
                    packageList = new ArrayList<>();
                }
                MineGameEntry mineGameEntry = null;
                for (PackageInfo info : packageInfos) {
                    String name = info.packageName;
                    String versionCode = info.versionName;
                    String appName = packageManager.getApplicationLabel(info.applicationInfo).toString();
                    if (!TextUtils.isEmpty(name) && !name.contains("com.android")
                            && !name.contains("com.google")
                            && !name.contains("com.example")
                            && !name.contains("android")
                            && !name.contains("com.miui") // 小米
                            && !name.contains("com.samsung") // 三星
                            && !name.contains("com.meizu") // 魅族
                            && !name.contains("com.huawei") // 华为
                            && !name.contains("com.oppo") // oppo
                            && !name.contains("com.vivo") // vivo
                            ) {
                        mineGameEntry = new MineGameEntry();
                        mineGameEntry.setPackageName(name);
                        mineGameEntry.setVersionName(versionCode);
                        mineGameEntry.setAppName(appName);
                        packageList.add(mineGameEntry);
                    }
                }
                MineGameDataEntry mineGameDataEntry = new MineGameDataEntry();
                mineGameDataEntry.setData(packageList);
                final String json = new Gson().toJson(packageList);
                LogUtil.errorLog("MineGameLogic json: " + json);

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        BaseModel baseModel = new BaseModel() {
                            @Override
                            public void putDataInMap() {
                                AppContext appContext = AppContext.getAppContext();
                                if (appContext.isLogin() && appContext.getUser() != null) {
                                    put(BaseModel.PARAMS_USER_ID, appContext.getUser().userId);
                                    put(BaseModel.PARAMS_TOKEN, appContext.getUser().token);
                                } else {
                                    put(BaseModel.PARAMS_USER_ID, "0");
                                }
                                put(BaseModel.PARAMS_PACKAGE_LIST, json);
                            }
                        };

                        RequestExe.post(HOST_APP + MINE_GAME_PATH, baseModel, new IRequestListener() {
                            @Override
                            public void success(HashMap<String, String> map) {
                                if (logicListner != null && map != null) {
                                    String json = map.get(HttpConsts.POST_PARAMS_DATA);
                                    final List<MineGameListEntry> entryList = fromJsonArray(json, MineGameListEntry.class);
                                    logicListner.success(entryList);
                                    AppOperator.runOnThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            CacheManager.saveObject(activity, new ResultBean(entryList), "mineGame");
                                        }
                                    });
                                } else if (logicListner != null) {
                                    logicListner.failed("您没有玩过草花游戏！");
                                    CacheManager.saveObject(activity, new ResultBean(new ArrayList()), "mineGame");
                                }
                            }

                            @Override
                            public void failed(String error, int errorCode) {
                                if (logicListner != null) {
                                    if (errorCode == 206) {
                                        handleTokenError(error);
                                        return;
                                    }
                                    logicListner.failed(error);
                                }
                                CacheManager.saveObject(activity, new ResultBean(new ArrayList()), "mineGame");
                            }
                        });

                    }
                });
            }
        }.start();
    }

    private void handleTokenError(final String error) {
        TokenRefreshLogic.refresh(new LogicListener() {
            @Override
            public void failed(String errorMsg) {
                if (logicListner != null) {
                    logicListner.failed(error);
                }
            }

            @Override
            public void success(String... result) {
                getMineGame(logicListner);
            }
        });
    }
}

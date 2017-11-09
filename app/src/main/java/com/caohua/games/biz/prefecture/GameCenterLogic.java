package com.caohua.games.biz.prefecture;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.caohua.games.app.AppContext;
import com.caohua.games.biz.minegame.MineGameDataEntry;
import com.caohua.games.biz.minegame.MineGameEntry;
import com.caohua.games.ui.prefecture.GameCenterActivity;
import com.chsdk.api.AppOperator;
import com.chsdk.api.CacheManager;
import com.chsdk.biz.app.AppLogic;
import com.chsdk.http.HttpConsts;
import com.chsdk.http.IRequest2Listener;
import com.chsdk.http.RequestExe;
import com.chsdk.model.BaseModel;
import com.chsdk.model.ResultBean;
import com.chsdk.utils.JsonUtil;
import com.chsdk.utils.NetworkUtils;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhouzhou on 2017/5/23.
 */

public class GameCenterLogic extends AppLogic<GameCenterEntry> {
    private static final String GAME_CENTER_URL = "subject/gameCenter";
    private int page;
    private int pageSize;


    public GameCenterLogic(Context context, Params params, int page, int pageSize) {
        super(context, params);
        this.page = page;
        this.pageSize = pageSize;
    }

    public GameCenterLogic(Context context, Params params, int page) {
        super(context, params);
        this.page = page;
    }

    public void getGameCenter(final AppGameCenterListener listener) {

        if (!NetworkUtils.isNetworkConnected(context)) {
            if (listener != null) {
                listener.failed(HttpConsts.ERROR_NO_NETWORK);
            }
            return;
        }

        final BaseModel baseModel = new BaseModel() {
            @Override
            public void putDataInMap() {
                put(PARAMS_NUMBER, page + "");
                put(PARAMS_PAGE_SIZE, pageSize + "");
                if (page == 0) {
                    put(PARAMS_PACKAGE_LIST, getLocalGameJson((GameCenterActivity) context));
                }
            }
        };

        RequestExe.post(HOST_APP + GAME_CENTER_URL, baseModel, new IRequest2Listener() {
            @Override
            public void success(String result) {
                if (listener != null) {
                    if (!TextUtils.isEmpty(result)) {
                        try {
                            final GameCenterEntry gameCenterEntry = new Gson().fromJson(result, GameCenterEntry.class);
                            listener.success(gameCenterEntry, page);
                            if (page == 0) {
                                AppOperator.runOnThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        CacheManager.saveObject(AppContext.getAppContext(), gameCenterEntry, "gameCenterEntry");
                                    }
                                });
                            }
                        } catch (Exception e) {
                            resolveError(result, listener);
                        }
                    } else {
                        listener.failed("");
                    }
                }
            }

            @Override
            public void failed(String error, int errorCode) {
                if (listener != null) {
                    listener.failed(error);
                }
            }
        });
    }

    private void resolveError(String result, AppGameCenterListener listener) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(result);
        } catch (JSONException e1) {
        }
        int code = JsonUtil.getStatusCode(jsonObject);
        String msg = JsonUtil.getServerMsg(jsonObject);
        if (code == 0 || code == 200) {
            listener.success(null, -1);
            return;
        }
        listener.failed(msg + "(" + code + ")");
    }

    private String getLocalGameJson(Activity activity) {
        if (activity == null) {
            return null;
        }
        PackageManager packageManager = activity.getPackageManager();
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
        List<MineGameEntry> packageList = new ArrayList<>();
        MineGameEntry mineGameEntry = null;
        for (PackageInfo info : packageInfos) {
            String name = info.packageName;
            String versionCode = info.versionName;
            if (!TextUtils.isEmpty(name) && !name.contains("com.android")
                    && !name.contains("com.google")
                    && !name.contains("com.example")
                    && !name.contains("android") && !name.contains("com.miui")) {
                mineGameEntry = new MineGameEntry();
                mineGameEntry.setPackageName(name);
                mineGameEntry.setVersionName(versionCode);
                packageList.add(mineGameEntry);
            }
        }
        MineGameDataEntry mineGameDataEntry = new MineGameDataEntry();
        mineGameDataEntry.setData(packageList);
        return new Gson().toJson(packageList);
    }


    public static interface AppGameCenterListener {
        void failed(String errorMsg);

        void success(Object entryResult, int currentPage);
    }
}

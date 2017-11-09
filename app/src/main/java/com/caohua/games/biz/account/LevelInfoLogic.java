package com.caohua.games.biz.account;

import android.content.Context;

import com.chsdk.biz.BaseLogic;
import com.chsdk.configure.SdkSession;
import com.chsdk.http.IRequestListener;
import com.chsdk.http.RequestExe;
import com.chsdk.model.BaseModel;
import com.chsdk.utils.LogUtil;
import com.chsdk.utils.NetworkUtils;

import java.util.HashMap;

/**
 * Created by zhouzhou on 2017/6/13.
 */

public class LevelInfoLogic extends BaseLogic {
    private static final long UPDATE_INTERVAL = 30 * 1000;
    private final boolean isRefresh;
    private Context context;
    private static long resumeTime;

    public LevelInfoLogic(Context context, boolean isRefresh) {
        this.context = context;
        this.isRefresh = isRefresh;
    }

    public void getLevelInfo(final AppLogicListner listener) {
        if (!NetworkUtils.isNetworkConnected(context)) {
            if (listener != null) {
                listener.failed("网络连接失败，请重试！");
            }
            return;
        }
        if (System.currentTimeMillis() - resumeTime > 0 &&
                System.currentTimeMillis() - resumeTime < UPDATE_INTERVAL && !isRefresh) {
            LogUtil.errorLog("TaskFragment onResume  updateTime 2: " + resumeTime);
            if (listener != null) {
                listener.success("delay");
            }
            return;
        }
        BaseModel baseModel = new BaseModel() {
            @Override
            public void putDataInMap() {
                put(PARAMS_USER_ID, SdkSession.getInstance().getUserId());
                put(PARAMS_TOKEN, SdkSession.getInstance().getToken());
            }
        };
        RequestExe.post(HOST_PASSPORT + "grow/levelInfo", baseModel, new IRequestListener() {
            @Override
            public void success(HashMap<String, String> map) {
                resumeTime = System.currentTimeMillis();
                if (listener != null) {
                    if (map != null) {
                        String grow_level = map.get("grow_level");
                        String img_mask = map.get("img_mask");
                        String grow_name = map.get("grow_name");
                        LevelInfoEntry levelInfoEntry = new LevelInfoEntry();
                        levelInfoEntry.setGrow_level(grow_level);
                        levelInfoEntry.setImg_mask(img_mask);
                        levelInfoEntry.setGrow_name(grow_name);
                        listener.success(levelInfoEntry);
                    } else {
                        listener.success(null);
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
}

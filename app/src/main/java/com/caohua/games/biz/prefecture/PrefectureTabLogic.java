package com.caohua.games.biz.prefecture;

import android.text.TextUtils;

import com.caohua.games.app.AppContext;
import com.chsdk.api.AppOperator;
import com.chsdk.api.CacheManager;
import com.chsdk.biz.BaseLogic;
import com.chsdk.configure.DataStorage;
import com.chsdk.configure.SdkSession;
import com.chsdk.http.IRequest2Listener;
import com.chsdk.http.RequestExe;
import com.chsdk.model.BaseModel;
import com.chsdk.utils.DeviceUtil;
import com.chsdk.utils.FileUtil;
import com.chsdk.utils.JsonUtil;
import com.chsdk.utils.LogUtil;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by zhouzhou on 2017/5/31.
 */

public class PrefectureTabLogic extends BaseLogic {
    private int position;
    private int page;
    private String subjectId;
    private String tabId;

    public PrefectureTabLogic(String subjectId, String tabId, int page, int position) {
        this.subjectId = subjectId;
        this.tabId = tabId;
        this.page = page;
        this.position = position;
    }

    protected String getDeviceNo() {
        int appId = SdkSession.getInstance().getAppId();
        AppContext context = AppContext.getAppContext();
        String deviceNo = FileUtil.readDeviceFile(context, appId);
        if (TextUtils.isEmpty(deviceNo)) {
            String deviceForSp = DataStorage.getDeviceNo(context);
            if (TextUtils.isEmpty(deviceForSp)) {
                deviceNo = DeviceUtil.getDeviceNo(context);
                if (!TextUtils.isEmpty(deviceNo)) {
                    DataStorage.saveDeviceNo(context, deviceNo);
                }
            } else {
                deviceNo = deviceForSp;
            }
        }
        SdkSession.getInstance().setDeviceNo(deviceNo);
        return deviceNo;
    }

    public void tabLogic(final GameCenterLogic.AppGameCenterListener listener) {
        BaseModel baseModel = new BaseModel() {
            @Override
            public void putDataInMap() {
                put(BaseModel.PARAMS_NUMBER, page + "");
                put(BaseModel.PARAMS_PAGE_SIZE, 20 + "");
                put(BaseModel.PARAMS_SUBJECT_GAME_ID, subjectId);
                put(BaseModel.PARAMS_TAB_ID, tabId);
                put(BaseModel.PARAMS_DEVICE_NO, getDeviceNo());
                String userId = SdkSession.getInstance().getUserId();
                put(BaseModel.PARAMS_USER_ID, userId == null ? "0" : userId);
                put(BaseModel.PARAMS_TOKEN, SdkSession.getInstance().getToken());
            }
        };
        RequestExe.post(HOST_APP + "subject/tab", baseModel, new IRequest2Listener() {
            @Override
            public void success(String result) {
                if (listener != null) {
                    if (!TextUtils.isEmpty(result)) {
                        try {
                            final ContentEntry contentEntry = new Gson().fromJson(result, ContentEntry.class);
                            listener.success(contentEntry, 0);
                            if (position == 0 && page == 0) {
                                AppOperator.runOnThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        CacheManager.saveObject(AppContext.getAppContext(), contentEntry, "contentEntry");
                                    }
                                });
                            }
                        } catch (Exception e) {
                            resolveError(result, listener);
                        }
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

    private void resolveError(String result, GameCenterLogic.AppGameCenterListener listener) {
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
}

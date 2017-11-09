package com.chsdk;

import android.content.Context;
import android.text.TextUtils;

import com.caohua.games.app.AppContext;
import com.chsdk.biz.BaseLogic;
import com.chsdk.configure.DataStorage;
import com.chsdk.configure.SdkSession;
import com.chsdk.http.IRequestListener;
import com.chsdk.http.RequestExe;
import com.chsdk.model.BaseModel;
import com.chsdk.utils.LogUtil;

import java.util.HashMap;

/**
 * Created by zhouzhou on 2017/2/24.
 */

public class SplashLogic extends BaseLogic {
    private static final String SPLASH_PATH = "app/initScreen";

    public void getSplashSave() {

        BaseModel model = new BaseModel() {

        };
        RequestExe.post(HOST_APP + SPLASH_PATH, model, new IRequestListener() {
            @Override
            public void success(HashMap<String, String> map) {
                final Context context = SdkSession.getInstance().getAppContext();
                if (map != null) {
                    String ad_img = map.get("ad_img");
                    String ad_url = map.get("ad_url");
                    String splashPic = DataStorage.getSplashPic(context);
                    String splashUrl = DataStorage.getSplashUrl(context);
                    long startTime = DataStorage.getSplashPicStartTime(context);
                    if (!TextUtils.isEmpty(splashUrl) && !splashUrl.equals(ad_url)) {
                        DataStorage.saveSplashUrl(context, ad_url);
                    } else {
                        DataStorage.saveSplashUrl(context, "");
                    }
                    if (System.currentTimeMillis() - startTime <= 24 * 60 * 60 * 1000
                            && !TextUtils.isEmpty(ad_img) && splashPic.equals(ad_img)
                            ) {
                        return;  //当图片相同时，就直接返回
                    }
                    String left_time = map.get("left_time");
                    if (!TextUtils.isEmpty(ad_img) && !TextUtils.isEmpty(left_time)) {
                        DataStorage.saveSplashPic(context, ad_img);
//                        PicUtil.downloadSplashPic(isSome, context, ad_img, new LogicListener() {
//                            @Override
//                            public void failed(String errorMsg) {
//                                LogUtil.errorLog("SplashLogic getSplashSave: 图片下载失败！");
//                            }
//
//                            @Override
//                            public void success(String... result) {
//                                LogUtil.errorLog("SplashLogic getSplashSave: 图片下载成功！");
//                            }
//                        });
                        if (!TextUtils.isEmpty(ad_url)) {
                            DataStorage.saveSplashUrl(context, ad_url);
                        } else {
                            DataStorage.saveSplashUrl(context, "");
                        }
                        DataStorage.saveSplashLeftTime(context, left_time);
                        LogUtil.errorLog("SplashLogic getSplashSave left_time: " + left_time);
                    }
                } else {
                    loadDefaultFailed(context);
                }
            }

            @Override
            public void failed(String error, int errorCode) {
                loadDefaultFailed(AppContext.getAppContext());
            }
        });
    }

    private void loadDefaultFailed(Context context) { //如果是个空的就不暂存了
//        long startTime = DataStorage.getSplashPicStartTime(context);
//        if (System.currentTimeMillis() - startTime >= 24 * 60 * 60 * 1000) {
            //时间超时
            DataStorage.saveSplashUrl(context, "");
            DataStorage.saveSplashPic(context, "");
//        }
    }
}

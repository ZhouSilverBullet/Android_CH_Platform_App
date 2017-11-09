package com.caohua.games.biz.send;

import android.app.Activity;
import android.content.Context;

import com.chsdk.api.AppOperator;
import com.chsdk.biz.BaseLogic;
import com.chsdk.http.HttpConsts;
import com.chsdk.http.IRequestListener;
import com.chsdk.http.RequestExe;
import com.chsdk.model.BaseModel;
import com.chsdk.utils.FileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ZengLei on 2017/5/22.
 */

public class PublishPicsHelper extends BaseLogic {

    private List<String> successPicUrl;
    private BaseLogic.DataLogicListner listener;
    private List<String> picPaths;
    private Activity activity;
    private String cacheDir;

    public PublishPicsHelper(Activity activity, BaseLogic.DataLogicListner listener) {
        this.activity = activity;
        this.listener = listener;
        cacheDir = String.format("%s/PublishPictures/%s", activity.getCacheDir().getAbsolutePath(), "cache");
        FileUtil.deleteDir(new File(cacheDir));
    }

    public void post(List<String> picPaths) {
        this.picPaths = picPaths;
        AppOperator.runOnThread(new Runnable() {
            @Override
            public void run() {
                handleCompressPic();
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        post(0);
                    }
                });
            }
        });
    }

    public void clearCache() {
        if (picPaths != null) {
            for (String path : picPaths) {
                File file = new File(path);
                if (file.exists()) {
                    boolean a = file.delete();
                    a = !a;
                }
            }
        }
    }

    private void handleCompressPic() {
        String[] cachePics = PicUtils.saveImageToCache(cacheDir, picPaths);
        picPaths = Arrays.asList(cachePics);
    }

    private void post(int index) {
        if (index < 0 || index >= picPaths.size()) {
            if (listener != null) {
                listener.success(successPicUrl);
            }
            return;
        }

        final String path = picPaths.get(index);
        post(index, path);
    }

    private void post(final int index, String picPath) {
        post(picPath, new BaseLogic.DataLogicListner<String>() {
            @Override
            public void failed(String errorMsg, int errorCode) {
                if (listener != null) {
                    listener.failed(errorMsg, 0);
                }
            }

            @Override
            public void success(String entryResult) {
                addSuccessCache(entryResult);
                post(index + 1);
            }
        });
    }

    private void addSuccessCache(String url) {
        if (successPicUrl == null) {
            successPicUrl = new ArrayList<>();
        }
        successPicUrl.add(url);
    }

    private static void post(final String picPath, final BaseLogic.DataLogicListner listner) {
        BaseModel baseModel = new BaseModel() {
            @Override
            public void putDataInMap() {
                put(PARAMS_TOKEN, session.getToken());
                put(PARAMS_USER_ID, session.getUserId());
            }
        };

        RequestExe.upload(HOSTP_APP_API + "app/articleImg", new File(picPath), baseModel, "file", new IRequestListener() {
            @Override
            public void success(HashMap<String, String> map) {
                if (map != null) {
                    String postUrl = map.get("img");
                    if (listner != null) {
                        listner.success(postUrl);
                    }
                    return;
                }
                if (listner != null) {
                    listner.failed(HttpConsts.ERROR_CODE_PARAMS_VALID, 0);
                }
            }

            @Override
            public void failed(String error, int errorCode) {
                if (listner != null) {
                    listner.failed(error, errorCode);
                }
            }
        });
    }
}

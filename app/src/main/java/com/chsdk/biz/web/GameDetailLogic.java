package com.chsdk.biz.web;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.caohua.games.app.AppContext;
import com.chsdk.biz.BaseLogic;
import com.chsdk.biz.download.DownloadEntry;
import com.chsdk.http.HttpConsts;
import com.chsdk.http.IRequestListener;
import com.chsdk.http.RequestExe;
import com.chsdk.model.BaseModel;
import com.chsdk.ui.widget.CHToast;
import com.chsdk.utils.JsonUtil;

import java.util.HashMap;
import java.util.List;

/**
 * Created by ZengLei on 2017/1/6.
 */

public class GameDetailLogic extends BaseLogic {

    private static final String PATH = "game/getGameById";
    private String id;
    private ActivityStatusListener listener;

    public GameDetailLogic(String id, @NonNull ActivityStatusListener listener) {
        this.id = id;
        this.listener = listener;
    }

    public void getEntry() {
        BaseModel baseModel = new BaseModel() {
            @Override
            public void putDataInMap() {
                put("igd", id);
            }
        };
        RequestExe.post(HOST_APP + PATH, baseModel, new GetGameEntryListener());
    }

    class GetGameEntryListener implements IRequestListener {
        @Override
        public void success(final HashMap<String, String> map) {
            listener.postAction(1000, new Runnable() {
                @Override
                public void run() {
                    listener.stopProgress();
                    boolean handled = false;
                    if (map != null) {
                        String data = map.get(HttpConsts.POST_PARAMS_DATA);
                        handled = handleData(fromJsonArray(data, DownloadEntry.class));
                    }
                    if (!handled) {
                        CHToast.show(AppContext.getAppContext(), "当前游戏无法下载");
                        listener.closeActivity();
                    }
                }
            });
        }

        private boolean handleData(List<DownloadEntry> list) {
            if (list != null && list.size() > 0) {
                DownloadEntry downloadEntry = list.get(0);
                if (downloadEntry != null) {
                    if (TextUtils.isEmpty(downloadEntry.getDetail_url())) {
                        CHToast.show(AppContext.getAppContext(), "当前游戏下载失败，未获取到游戏详情");
                        listener.closeActivity();
                        return true;
                    }

                    if (TextUtils.isEmpty(downloadEntry.getDownloadUrl())) {
                        CHToast.show(AppContext.getAppContext(), "当前游戏下载失败，未获取到游戏下载地址");
                        listener.closeActivity();
                        return true;
                    }

                    listener.setEntry(downloadEntry);
                    return true;
                }
            }
            return false;
        }

        @Override
        public void failed(final String error, int errorCode) {
            listener.postAction(1000, new Runnable() {
                @Override
                public void run() {
                    CHToast.show(AppContext.getAppContext(), "当前游戏无法下载:" + error);
                    listener.stopProgress();
                    listener.closeActivity();
                }
            });
        }
    }

    public static interface ActivityStatusListener {
        void closeActivity();

        void stopProgress();

        void setEntry(DownloadEntry entry);

        void postAction(long time, Runnable action);
    }
}

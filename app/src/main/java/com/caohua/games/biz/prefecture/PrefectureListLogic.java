package com.caohua.games.biz.prefecture;

import com.caohua.games.app.AppContext;
import com.chsdk.biz.BaseLogic;
import com.chsdk.configure.DataStorage;
import com.chsdk.http.IRequestListener;
import com.chsdk.http.RequestExe;
import com.chsdk.model.BaseModel;
import com.chsdk.utils.JsonUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

/**
 * Created by zhouzhou on 2017/6/2.
 */

public class PrefectureListLogic extends BaseLogic {
    private int number;
    private int listId;

    public PrefectureListLogic(int number, int listId) {
        this.number = number;
        this.listId = listId;
    }

    public void prefectureList(final String classifyId, final GameCenterLogic.AppGameCenterListener listener) {
        BaseModel baseModel = new BaseModel() {
            @Override
            public void putDataInMap() {
                put(BaseModel.PARAMS_NUMBER, number + "");
                put(BaseModel.PARAMS_PAGE_SIZE, 20 + "");
                put(BaseModel.PARAMS_SUBJECT_GAME_ID, DataStorage.getSubjectGameId(AppContext.getAppContext()) + "");
                put(BaseModel.PARAMS_LIST_ID, listId + "");
                put(BaseModel.PARAMS_DEVICE_NO, session.getDeviceNo());
                put(BaseModel.PARAMS_USER_ID, session.getUserId() == null ? "0" : session.getUserId());
                put(BaseModel.PARAMS_TOKEN, session.getToken() == null ? "0" : session.getToken());
                put("cli", classifyId);
            }
        };

        RequestExe.post(HOST_APP + "subject/list", baseModel, new IRequestListener() {
            @Override
            public void success(HashMap<String, String> map) {
                if (listener != null) {
                    if (map != null) {
                        PrefectureListEntry entry = new PrefectureListEntry();
                        String content = map.get("content");
                        String classify = map.get("classify");
                        List<PrefectureListEntry.ContentBean> beanList = fromJsonArray(content, PrefectureListEntry.ContentBean.class);
                        List<PrefectureListEntry.ClassifyBean> classifyList = fromJsonArray(classify, PrefectureListEntry.ClassifyBean.class);
                        entry.setContent(beanList);
                        entry.setClassify(classifyList);
                        listener.success(entry, number);
                        return;
                    }
                    listener.failed("");
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

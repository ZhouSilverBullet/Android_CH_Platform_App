package com.caohua.games.biz.send;

import com.caohua.games.ui.send.ChooseTagActivity;
import com.chsdk.biz.BaseLogic;
import com.chsdk.http.HttpConsts;
import com.chsdk.http.IRequestListener;
import com.chsdk.http.RequestExe;
import com.chsdk.model.BaseModel;

import java.util.HashMap;
import java.util.List;

/**
 * Created by zhouzhou on 2017/5/12.
 */

public class GetTagLogic extends BaseLogic {

    public void getTag(final String forumId, final DataLogicListner listner) {
        BaseModel baseModel = new BaseModel() {
            @Override
            public void putDataInMap() {
                put(PARAMS_FORUM_ID, forumId);
                put(PARAMS_USER_ID, session.getUserId() == null ? "0" : session.getUserId());
                put(PARAMS_TOKEN, session.getToken());
            }
        };

        RequestExe.post(HOST_APP + "forum/tagList", baseModel, new IRequestListener() {
            @Override
            public void success(HashMap<String, String> map) {
                if (map != null) {
                    String responseString = map.get(HttpConsts.RESULT_PARAMS_DATA);
                    List<ChooseTagActivity.TagItem> list = fromJsonArray(responseString, ChooseTagActivity.TagItem.class);
                    if (listner != null) {
                        listner.success(list);
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

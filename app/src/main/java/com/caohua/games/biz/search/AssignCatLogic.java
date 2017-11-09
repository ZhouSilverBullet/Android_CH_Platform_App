package com.caohua.games.biz.search;

import android.content.Context;

import com.chsdk.biz.app.AppLogic;
import com.chsdk.http.HttpConsts;
import com.chsdk.http.IRequestListener;
import com.chsdk.http.RequestExe;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/**
 * Created by CXK on 2016/11/10.
 */

public class AssignCatLogic<T extends Serializable> extends AppLogic {

    private static final String ASSIGCAT_INFO_PATH = "game/searchAssociation";
    private AppLogicListner listner;

    public AssignCatLogic(Context context) {
        super(context, null);
    }

    public void getAssigCatGame(AssigCatModel model, final AppLogicListner listner) {
        RequestExe.post(HOST_APP + ASSIGCAT_INFO_PATH, model, new IRequestListener() {
            @Override
            public void success(HashMap<String, String> map) {
                if (map != null) {
                    String responseString = map.get(HttpConsts.RESULT_PARAMS_DATA);
                    List<T> bean = fromJsonArray(responseString, AssigCatEntry.class);
                    if (listner != null) {
                        listner.success(bean);
                    }
                    return;
                }
                if (listner != null) {
                    listner.failed(HttpConsts.ERROR_CODE_PARAMS_VALID);
                }
            }

            @Override
            public void failed(String error, int errorCode) {
                if (listner != null) {
                    listner.failed(error);
                }
            }
        });
    }
}

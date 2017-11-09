package com.caohua.games.biz.gift;

import com.caohua.games.app.AppContext;
import com.chsdk.biz.BaseLogic;
import com.chsdk.http.HttpConsts;
import com.chsdk.http.IRequestListener;
import com.chsdk.http.RequestExe;
import com.chsdk.ui.widget.CHToast;

import java.util.HashMap;
import java.util.List;

/**
 * Created by admin on 2016/11/12.
 */

public class GetGiftDetailsLogic extends BaseLogic {
    private static final String GET_GIFT_INFO_PATH = "main/giftDetail";

    public void getGetGiftDetails(int type, String data, GetGiftDetailsModel model, final AppLogicListner listener) {
        if (type == 99) {
            RequestExe.postToData(HOST_APP + GET_GIFT_INFO_PATH, data, new IRequestListener() {
                @Override
                public void success(HashMap<String, String> map) {
                    if (map != null) {
                        String data = map.get(HttpConsts.POST_PARAMS_DATA);
                        List<GetGiftDetailsEntry> list = fromJsonArray(data, GetGiftDetailsEntry.class);
                        if (null != listener) {
                            listener.success(list.get(0));
                        }
                    } else {
                        listener.success(null);
                        CHToast.show(AppContext.getAppContext(), "访问出错");
                    }
                }

                @Override
                public void failed(String error, int errorCode) {
                    if (null != listener) {
                        listener.failed(error);
                    }
                }
            });
        } else {
            RequestExe.post(HOST_APP + GET_GIFT_INFO_PATH, model, new IRequestListener() {
                @Override
                public void success(HashMap<String, String> map) {
                    if (map != null) {
                        String data = map.get(HttpConsts.POST_PARAMS_DATA);
                        List<GetGiftDetailsEntry> list = fromJsonArray(data, GetGiftDetailsEntry.class);
                        if (null != listener) {
                            listener.success(list.get(0));
                        }
                    } else {
                        listener.success(null);
                        CHToast.show(AppContext.getAppContext(), "访问出错");
                    }

                }

                @Override
                public void failed(String error, int errorCode) {
                    if (null != listener) {
                        listener.failed(error);
                    }
                }
            });
        }
    }
}

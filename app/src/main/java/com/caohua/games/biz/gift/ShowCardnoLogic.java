package com.caohua.games.biz.gift;

import com.chsdk.biz.BaseLogic;
import com.chsdk.biz.game.TokenRefreshLogic;
import com.chsdk.http.HttpConsts;
import com.chsdk.http.IRequestListener;
import com.chsdk.http.RequestExe;

import java.util.HashMap;

/**
 * Created by admin on 2016/11/12.
 */

public class ShowCardnoLogic extends BaseLogic {
    private static final String GIFT_INFO_PATH = "gift/showCardno";
    private ShowCardnoModel model;
    private AppLogicListner listner;

    public ShowCardnoLogic() {

    }

    public void getShowCardno(final int type, final String data, ShowCardnoModel showCardnoModel, final AppLogicListner listener) {
        this.model = showCardnoModel;
        this.listner = listener;
        if (type == 99) {
            RequestExe.postToData(HOST_APP + GIFT_INFO_PATH, data, new IRequestListener() {
                @Override
                public void success(HashMap<String, String> map) {
                    String cardno = map.get(HttpConsts.RESULT_PARAMS_GIFT_CORDNO);
                    if (listener != null) {
                        listener.success(cardno);
                    }
                }

                @Override
                public void failed(String error, int errorCode) {
                    if (BaseLogic.isTokenInvialid(error)) {
                        handleTokenError(type, data, error);
                        return;
                    }
                    if (listener != null) {
                        listener.failed(error);
                    }
                }
            });
        } else {
            RequestExe.post(HOST_APP + GIFT_INFO_PATH, showCardnoModel, new IRequestListener() {
                @Override
                public void success(HashMap<String, String> map) {
                    String cardno = map.get(HttpConsts.RESULT_PARAMS_GIFT_CORDNO);
                    if (listener != null) {
                        listener.success(cardno);
                    }
                }

                @Override
                public void failed(String error, int errorCode) {
                    if (BaseLogic.isTokenInvialid(error)) {
                        handleTokenError(type, data, error);
                        return;
                    }
                    if (listener != null) {
                        listener.failed(error);
                    }
                }
            });
        }
    }

    private void handleTokenError(final int type, final String data, final String error) {
        TokenRefreshLogic.refresh(new LogicListener() {
            @Override
            public void failed(String errorMsg) {
                if (listner != null) {
                    listner.failed(error);
                }
            }

            @Override
            public void success(String... result) {
                getShowCardno(type, data, model, listner);
            }
        });
    }
}

package com.caohua.games.biz.pay;

import android.text.TextUtils;

import com.caohua.games.ui.account.PayActionActivity;
import com.chsdk.biz.BaseLogic;
import com.chsdk.biz.game.TokenRefreshLogic;
import com.chsdk.http.HttpConsts;
import com.chsdk.http.IRequestListener;
import com.chsdk.http.RequestExe;

import java.util.HashMap;

/**
 * Created by CXK on 2016/11/16.
 */

public class PayOrderLogic extends BaseLogic {
    private static final String PAY_ORDER_PATH = "appchb/orderInfo";
    private PayOrderModel model;
    private LogicListener listner;
    private int type;

    public void getOrder(final PayOrderModel model, final int type, final LogicListener listner) {
        this.model = model;
        this.listner = listner;
        this.type = type;
        RequestExe.post(HOST_PAY + PAY_ORDER_PATH, model, new IRequestListener() {
                    @Override
                    public void success(HashMap<String, String> map) {
                        if (map != null) {
                            String sdkOrder = map.get(HttpConsts.RESULT_PARAMS_SDK_ORDER_NO);
                            if (!TextUtils.isEmpty(sdkOrder)) {
                                if (type == PayActionActivity.PAY_TYPE_ALIPAY) {
                                    String partner = map.get(HttpConsts.RESULT_PARAMS_PARTNER);
                                    String seller = map.get(HttpConsts.RESULT_PARAMS_SELLER);
                                    String rsaprivate = map.get(HttpConsts.RESULT_PARAMS_RSA_PRIVATE);
                                    String notifyurl = map.get(HttpConsts.RESULT_PARAMS_NOTIFY_URL);

                                    if (listner != null) {
                                        listner.success(sdkOrder, partner, seller, rsaprivate, notifyurl);
                                    }
                                } else if (type == PayActionActivity.PAY_TYPE_UNION) {
                                    String tn = map.get(HttpConsts.RESULT_PARAMS_TN);
                                    if (listner != null) {
                                        listner.success(sdkOrder, tn);
                                    }
                                } else if (type == PayActionActivity.PAY_TYPE_WETCHAT || type == PayActionActivity.PAY_TYPE_IPAY) {
                                    String tokenId = map.get(HttpConsts.RESULT_PARAMS_TOKEN_ID);
                                    if (listner != null) {
                                        listner.success(sdkOrder, tokenId);
                                    }
                                } else {
                                    if (listner != null) {
                                        listner.success(sdkOrder);
                                    }
                                }
                                return;
                            }
                        }
                        if (listner != null) {
                            listner.failed(HttpConsts.ERROR_CODE_PARAMS_VALID);
                        }
                    }

                    @Override
                    public void failed(String error, int errorCode) {
                        if (BaseLogic.isTokenInvialid(error)) {
                            handleTokenError(error);
                            return;
                        }

                        if (listner != null) {
                            listner.failed(error);
                        }
                    }
                }
        );
    }

    private void handleTokenError(final String error) {
        TokenRefreshLogic.refresh(new LogicListener() {
            @Override
            public void failed(String errorMsg) {
                if (listner != null) {
                    listner.failed(error);
                }
            }

            @Override
            public void success(String... result) {
                getOrder(model, type, listner);
            }
        });
    }
}

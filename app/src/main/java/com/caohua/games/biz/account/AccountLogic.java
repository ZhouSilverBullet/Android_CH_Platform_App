package com.caohua.games.biz.account;

import android.content.Context;

import com.chsdk.biz.BaseLogic;
import com.chsdk.http.IRequestListener;
import com.chsdk.http.RequestExe;
import com.chsdk.ui.widget.LoadingDialog;

import java.util.HashMap;

/**
 * Created by CXK on 2016/11/8.
 */

public class AccountLogic extends BaseLogic {
    private static final String CHANGE_INFO_PATH = "account/changeInfo";
    private Context context;

    public AccountLogic(Context context) {
        this.context = context;
    }

    public void updataInfo(AccountModel model, final IRequestListener listener) {
        final LoadingDialog dialog = new LoadingDialog(context, "");
        dialog.show();
        String url = HOST_APP + CHANGE_INFO_PATH;
        RequestExe.post(url, model, new IRequestListener() {
            @Override
            public void success(HashMap<String, String> map) {
                dialog.dismiss();
                if (listener != null) {
                    listener.success(null);
                }
            }

            @Override
            public void failed(String error, int errorCode) {
                dialog.dismiss();
                if (listener != null) {
                    listener.failed(error, errorCode);
                }
            }
        });
    }
}

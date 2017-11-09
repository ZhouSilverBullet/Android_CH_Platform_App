package com.caohua.games.biz.article;

import com.chsdk.biz.BaseLogic;
import com.chsdk.http.IRequestListener;
import com.chsdk.http.RequestExe;
import com.chsdk.model.BaseModel;

import java.util.HashMap;

/**
 * Created by ZengLei on 2017/5/24.
 */

public class OperateLogic extends BaseLogic {
    public static final String OPERATE_COLLECT = "oc"; // 收藏
    public static final String OPERATE_TOP = "ot"; // 置顶
    public static final String OPERATE_GOOD = "og"; // 加精
    public static final String OPERATE_LOCK = "ol"; // 锁定
    public static final String OPERATE_HIDE = "oh"; // 隐藏

    public static final String DONE = "1";
    public static final String CANCEL = "2";

    public void doOperate(final String articleId, final String operateType, final String operateResult, final LogicListener listener) {
        BaseModel model = new BaseModel(){
            @Override
            public void putDataInMap() {
                put(PARAMS_USER_ID, session.getUserId());
                put(PARAMS_TOKEN, session.getToken());
                put("ai", articleId);
                put(operateType, operateResult);
            }
        };

        RequestExe.post(HOST_APP + "forum/articleOperate", model, new IRequestListener() {
            @Override
            public void success(HashMap<String, String> map) {
                if (listener != null) {
                    listener.success();
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
}

package com.caohua.games.ui.emoji;

import android.content.Context;
import android.text.TextUtils;
import android.widget.TextView;

import com.caohua.games.biz.comment.CommentAddCommentLogic;
import com.caohua.games.biz.comment.CommentEntry;
import com.caohua.games.biz.comment.TimesEntry;
import com.chsdk.biz.BaseLogic;
import com.chsdk.ui.widget.CHToast;
import com.chsdk.ui.widget.LoadingDialog;

/**
 * Created by admin on 2017/1/17.
 */

public class CommentResponse {
    private Context mContext;
    private TextView et_sendmessage;
    private String mCommentID;
    private CommentResponseCallback mCallback;

    public CommentResponse(Context context, TextView textView, String commentID) {
        mContext = context;
        et_sendmessage = textView;
        mCommentID = commentID;
    }

    public void setCallback(CommentResponseCallback callback) {
        mCallback = callback;
    }

    public interface CommentResponseCallback {
        void onSuccess(String... entryResult);

        void onFailure(String errorMsg, int errorCode);
    }

    public void commentResponseLogic(final String entryResult, String isVerify, CommentEntry commentEntry
            , TimesEntry timesEntry) {
        if (commentEntry == null) {
            commentEntry = new CommentEntry();
            if (timesEntry == null) {
//                LogUtil.errorLog(TAG, "timesEntry = 为空 ");
                et_sendmessage.setText("");
                return;
            }
            if (TextUtils.isEmpty(mCommentID)) {
                mCommentID = "0";
            }
            commentEntry.setCommentType(timesEntry.getCommentType());
            commentEntry.setCommentGameType(timesEntry.getType());
            commentEntry.setCommentTypeId(timesEntry.getId());
            commentEntry.setCommentID(mCommentID);
        }
        commentEntry.setCommentContent(et_sendmessage.getText().toString());
        commentEntry.setCommentIsVerify(isVerify);
        final LoadingDialog loadingDialog = new LoadingDialog(mContext, "");
        loadingDialog.show();
        CommentAddCommentLogic commentAddCommentLogic = new CommentAddCommentLogic(entryResult, commentEntry);
        commentAddCommentLogic.getAddComment(new BaseLogic.CommentLogicListener() {
            @Override
            public void failed(String errorMsg, int errorCode) {
                loadingDialog.dismiss();

                if (!TextUtils.isEmpty(errorMsg) && errorCode != 0) {
                    if (504 == errorCode) {
                        //弹出验证码
                        if (mCallback != null) {
                            mCallback.onFailure(errorMsg, errorCode);
                        }
                    } else if (256 == errorCode) {
                        //什么也不干
                    } else {
                        CHToast.show(mContext, errorMsg);
                    }
                } else {
                    CHToast.show(mContext, errorMsg);
                }
            }

            @Override
            public void success(String... entryResult) {
                loadingDialog.dismiss();
                if (mCallback != null) {
                    et_sendmessage.setText("");  //发送请求发出后，则置为空
                    CHToast.show(mContext, "评论成功！");
                    mCallback.onSuccess(entryResult);
                }
            }
        });
    }
}

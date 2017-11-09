package com.caohua.games.app;

import android.annotation.SuppressLint;
import android.content.Context;

import com.chsdk.utils.LogUtil;
import com.xiaomi.mipush.sdk.MiPushCommandMessage;
import com.xiaomi.mipush.sdk.MiPushMessage;
import com.xiaomi.mipush.sdk.PushMessageReceiver;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MiMessageReceiver extends PushMessageReceiver {
    public static final String TAG = "MiMessageReceiver";

    /**
     * 用来接收服务器发送的透传消息
     *
     * @param context
     * @param message
     */
    @Override
    public void onReceivePassThroughMessage(Context context, MiPushMessage message) {
    }

    @Override
    public void onNotificationMessageClicked(final Context context, final MiPushMessage message) {
        LogUtil.debugLog(TAG,"onNotificationMessageClicked is called. " + Thread.currentThread().getName());
    }

    @Override //message content值
    public void onNotificationMessageArrived(Context context, MiPushMessage message) {
        LogUtil.debugLog(TAG,"onNotificationMessageArrived is called. " + message.toString());
    }

    /**
     * 当客户端向服务器发送注册push、设置alias、取消注册alias、订阅topic、取消订阅topic等等命令后，从服务器返回结果。
     *
     * @param context
     * @param message
     */
    @Override
    public void onCommandResult(Context context, MiPushCommandMessage message) {
        LogUtil.debugLog(TAG,"onCommandResult is called. " + message.toString());
    }

    /**
     * 用来接受客户端向服务器发送注册命令消息后返回的响应
     *
     * @param context
     * @param message
     */
    @Override
    public void onReceiveRegisterResult(Context context, MiPushCommandMessage message) {
        LogUtil.debugLog(TAG, "onReceiveRegisterResult is called. " + message.toString());
    }

    @SuppressLint("SimpleDateFormat")
    private static String getSimpleDate() {
        return new SimpleDateFormat("MM-dd hh:mm:ss").format(new Date());
    }

}

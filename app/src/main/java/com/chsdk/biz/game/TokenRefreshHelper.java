package com.chsdk.biz.game;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Log;

import com.caohua.games.app.AppContext;
import com.chsdk.biz.BaseLogic;
import com.chsdk.utils.LogUtil;

/**
 * @author ZengLei
 *         <p>
 * @version 2016年9月8日
 *          <p>
 */
public class TokenRefreshHelper extends BaseLogic {
    private static final String TAG = TokenRefreshHelper.class.getSimpleName();
    private static final String GAME_HEART_ACTION = "ch_app_game_heart";
    private static final int REQUEST_CODE = 22;

    private static final int INTERVAL = 10;

    public static void registerAlarm(Context context) {
        unregister(context);
        IntentFilter filter = new IntentFilter(GAME_HEART_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        context.registerReceiver(AlarmListener.getInstance(true), filter);
        setAlarm(context);
        Log.e("TokenRefreshHelper", "registerAlarm");
    }

    public static void unregister(Context context) {
        AlarmListener listener = AlarmListener.getInstance(false);
        Log.e("TokenRefreshHelper", "unregister");
        if (listener != null) {
            try {
                context.unregisterReceiver(listener);
                LogUtil.errorLog(TAG, "unregisterReceiver");
            } catch (IllegalArgumentException e) {
                LogUtil.errorLog(TAG, "unregisterReceiver_err_" + e.getMessage());
            }
            AlarmListener.destroy();
        }
    }

    private synchronized static void setAlarm(Context context) {
        if (!AppContext.getAppContext().isLogin()) {
            unregister(context);
            return;
        }

        AlarmManager mAlarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(GAME_HEART_ACTION);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, REQUEST_CODE, intent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        mAlarm.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + INTERVAL * 60 * 1000L, pendingIntent);
    }

    static class AlarmListener extends BroadcastReceiver {

        private static AlarmListener listener;

        public static synchronized AlarmListener getInstance(boolean register) {
            if (listener == null && register) {
                listener = new AlarmListener();
            }
            return listener;
        }

        public static synchronized void destroy() {
            if (listener != null) {
                listener = null;
            }
        }

        private AlarmListener() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null)
                return;

            String action = intent.getAction();
            if (TextUtils.isEmpty(action))
                return;

            if (action.equals(GAME_HEART_ACTION)) {
                Log.e("TokenRefreshHelper", "onReceive GAME_HEART_ACTION");
                LogUtil.debugLog(TAG, "onReceive");
                doAction();
                setAlarm(context);
            } else {
                Log.e("TokenRefreshHelper", "onReceive CONNECTIVITY_SERVICE");
                ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo netInfo = mConnectivityManager.getActiveNetworkInfo();
                if (netInfo != null && netInfo.isAvailable()) {
                    setAlarm(context);
                }
            }
        }

        private void doAction() {
            TokenRefreshLogic.refresh(null);
        }
    }
}

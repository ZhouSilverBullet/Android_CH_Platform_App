package com.chsdk.configure;

import android.content.Context;

import com.chsdk.model.game.PushEntry;
import com.chsdk.model.login.LoginUserInfo;
import com.chsdk.utils.DeviceUtil;
import com.chsdk.utils.LogUtil;
import com.chsdk.utils.SpUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author ZengLei
 *         <p>
 * @version 2016年8月18日
 *          <p>
 */
public class DataStorage {
    private static final String KEY_LOGIN_COUNT = "lc_";

    private static final String KEY_HTML_UPDATE = "html_";
    private static final String KEY_AD_PIC_TIME = "ad_time_";
    private static final String KEY_AD_PIC_URL = "ad_url_";
    private static final String KEY_AD_PIC_LINK = "ad_link_";

    private static final String KEY_IS_LOGIN = "login";
    private static final String KEY_APP_START_LAST_TIME = "app_last_start_";

    private static final String KEY_GAME_NAME = "game_name_2";

    private static final String KEY_STRING_GIFT_URL = "gift_url";
    private static final String KEY_STRING_GIFT_INTERCEPT_URL = "gift_intercept_url";

    //wifi条件下才能下载 true  false 任何条件下载
    private static final String WIFI_IS_USE = "WIFI_IS_USE";

    private static final String PACKAGE_IS_DELETE = "package_id";

    private static final String KEY_APP_PAY_SORT = "app_pay_sort";

    private static final String GUIDE_IS_SHOW = "guide_is_show";
    private static final String GUIDE_SHOW_CODE = "guide_show_code";

    private static final String HOT_GIFT_ONCE = "hot_gift_once";

    private static final String LOCATION_TIME = "location_time";

    private static final String LOCATION_CITY = "location_city";

    private static final String SPLASH_PIC = "splash_pic";

    private static final String SPLASH_URL = "splash_url";

    private static final String LEFT_TIME = "left_time";

    private static final String START_TIME = "start_time";

    private static final String TIPS_TIME = "tips";

    private static final String GAME_REWARD = "game_reward";

    private static final String SUBJECT_GAME_ID = "subject_game_id";
    private static final String DEVICE_NO = "device_no";

    private static final String COUPON_ACTIVE = "coupon_active";
    private static final String GIFT_SAVE_DIALOG = "gift_save_dialog";

    public static void setAppLogin(Context context, boolean login) {
        SpUtil.putBoolean(context, KEY_IS_LOGIN, login);
    }

    public static boolean isAppLogin(Context context) {
        return SpUtil.getBoolean(context, KEY_IS_LOGIN, false);
    }

    public static void setAppStartTime(Context context) {
        SpUtil.putLong(context, KEY_APP_START_LAST_TIME, System.currentTimeMillis());
    }

    public static long getLastAppStartTime(Context context) {
        return SpUtil.getLong(context, KEY_APP_START_LAST_TIME, 0L);
    }

    public static void saveAdPicUrl(Context context, String url) {
        SpUtil.putString(context, KEY_AD_PIC_URL, url);
    }

    public static String getAdPicUrl(Context context) {
        return SpUtil.getString(context, KEY_AD_PIC_URL, null);
    }

    public static void saveAdPicLink(Context context, String link) {
        SpUtil.putString(context, KEY_AD_PIC_LINK, link);
    }

    public static String getAdPicLink(Context context) {
        return SpUtil.getString(context, KEY_AD_PIC_LINK, null);
    }

    public static void saveAdPicUpdateTime(Context context) {
        SpUtil.putLong(context, KEY_AD_PIC_TIME, System.currentTimeMillis());
    }

    public static long getAdPicLastUpdateTime(Context context) {
        return SpUtil.getLong(context, KEY_AD_PIC_TIME, 0);
    }

    public static void saveHtmlUpdateTime(Context context, String fileName) {
        SpUtil.putLong(context, KEY_HTML_UPDATE + fileName, System.currentTimeMillis());
    }

    public static long getHtmlLastUpdateTime(Context context, String fileName) {
        return SpUtil.getLong(context, KEY_HTML_UPDATE + fileName, 0);
    }

    public static void deleteLoginCountInfo(Context context, String userName) {
        SpUtil.remove(context, KEY_LOGIN_COUNT + userName);
    }
    /* login count end */

    public static Map<String, List<String>> getPushList(Context context, long currentTime) {
        Map<String, ?> map = SpUtil.getPushContent(context);
        Set<String> set = map == null ? null : map.keySet();
        if (set == null || set.size() == 0)
            return null;

        Map<String, List<String>> mapId = null;
        try {
            List<String> userList = getUserList(context);
            Object[] keyArr = set.toArray();
            for (int index = keyArr.length - 1; index >= 0; index--) {
                String key = (String) keyArr[index];
                String[] arr = key.split("_", 2);
                String id = arr[0];
                String user = arr[1];

                String[] valueArr = ((String) map.get(key)).split("_", 3);
                long expireTime = Long.valueOf(valueArr[0]);
                String type = valueArr[1];
                String lastClickTime = valueArr[2];

                if (currentTime > expireTime) {
                    SpUtil.deletePush(context, key);
                    continue;
                }

                if (userList == null || userList.size() == 0) {
                    SpUtil.deletePush(context, key);
                    continue;
                } else {
                    if (!userList.contains(user)) {
                        SpUtil.deletePush(context, key);
                        continue;
                    }
                }

                // 如果msg类型是2，则判断上次点击时间是否是当天日期，是则需要添加id，否则不添加
                if (PushEntry.SHOW_TYPE_CLICK_ONCE_DAY.equals(type)) {
                    if (!DeviceUtil.isSameDay(Long.valueOf(lastClickTime)))
                        continue;
                }

                // 如果msg类型是1，则每次都添加
                if (mapId == null) {
                    mapId = new HashMap<String, List<String>>();
                }

                List<String> listId = mapId.get(user);
                if (listId == null) {
                    listId = new ArrayList<String>();
                }

                listId.add(id);
                mapId.put(user, listId);
            }
        } catch (Exception e) {
            LogUtil.errorLog(e.getMessage());
        }

        return mapId;
    }

    public static List<String> getNotUserPushList(Context context, long currentTime) {
        Map<String, ?> map = SpUtil.getPushContent(context);
        Set<String> set = map == null ? null : map.keySet();
        if (set == null || set.size() == 0)
            return null;

        List<String> mapId = null;
        try {
            Object[] keyArr = set.toArray();
            for (int index = keyArr.length - 1; index >= 0; index--) {
                String key = (String) keyArr[index];
                String[] arr = key.split("_", 2);
                String id = arr[0];

                String[] valueArr = ((String) map.get(key)).split("_", 3);
                long expireTime = Long.valueOf(valueArr[0]);
                String type = valueArr[1];
                String lastClickTime = valueArr[2];

                if (currentTime > expireTime) {
                    SpUtil.deletePush(context, key);
                    continue;
                }
                // 如果msg类型是2，则判断上次点击时间是否是当天日期，是则需要添加id，否则不添加
                if (PushEntry.SHOW_TYPE_CLICK_ONCE_DAY.equals(type)) {
                    if (!DeviceUtil.isSameDay(Long.valueOf(lastClickTime)))
                        continue;
                }

                // 如果msg类型是1，则每次都添加
                if (mapId == null) {
                    mapId = new ArrayList<String>();
                }
                mapId.add(id);
            }
        } catch (Exception e) {
            LogUtil.errorLog(e.getMessage());
        }

        return mapId;
    }

    public static void savePush(Context context, String id, String user, String time, String type) {
        SpUtil.savePushMsg(context, id, user, time, type);
    }

    public static void saveNotUserPush(Context context, String id, String time, String type) {
        SpUtil.saveNotUserPushMsg(context, id, time, type);
    }

    private static List<String> getUserList(Context context) {
        List<LoginUserInfo> userList = UserDBHelper.getUserList(context);
        if (userList != null && userList.size() > 0) {
            List<String> list = new ArrayList<String>();
            for (LoginUserInfo info : userList) {
                list.add(info.userName);
            }
            return list;
        }
        return null;
    }

    public static void saveGameName(Context context, String name) {
        SpUtil.putString(context, KEY_GAME_NAME, name);

    }

    public static String getGameName(Context context) {
        return SpUtil.getString(context, KEY_GAME_NAME, null);
    }

    /**
     * 礼物详情
     *
     * @param context
     * @return
     */
    public static String getGiftMoreUrl(Context context) {
        return SpUtil.getString(context, KEY_STRING_GIFT_URL, "");
    }

    public static String getGiftInterceptUrl(Context context) {
        return SpUtil.getString(context, KEY_STRING_GIFT_INTERCEPT_URL, "");
    }

    public static void saveGiftMoreUrl(Context context, String url) {
        SpUtil.putString(context, KEY_STRING_GIFT_URL, url);
    }

    public static void saveGiftInterceptUrl(Context context, String url) {
        SpUtil.putString(context, KEY_STRING_GIFT_INTERCEPT_URL, url);
    }

    public static void saveAppPaySort(Context context, String paySort) {
        SpUtil.putString(context, KEY_APP_PAY_SORT, paySort);
    }

    public static String getAppPaySort(Context context) {
        return SpUtil.getString(context, KEY_APP_PAY_SORT, null);
    }

    public static boolean getWifiDownload(Context context) {
        return SpUtil.getBoolean(context, WIFI_IS_USE, true);
    }

    public static void setWifiDownload(Context context, boolean b) {
        SpUtil.putBoolean(context, WIFI_IS_USE, b);
    }

    public static boolean getPkgDelete(Context context) {
        return SpUtil.getBoolean(context, PACKAGE_IS_DELETE, false);
    }

    public static void setPkgDelete(Context context, boolean b) {
        SpUtil.putBoolean(context, PACKAGE_IS_DELETE, b);
    }

    public static boolean getGuideShow(Context context) {
        return SpUtil.getBoolean(context, GUIDE_IS_SHOW, false);
    }

    public static void setGuideShow(Context context, boolean b) {
        SpUtil.putBoolean(context, GUIDE_IS_SHOW, b);
    }

    public static int getGuideShowCode(Context context) {
        return SpUtil.getInt(context, GUIDE_SHOW_CODE, 0);
    }

    public static void setGuideShowCode(Context context, int code) {
        SpUtil.putInt(context, GUIDE_SHOW_CODE, code);
    }

    public static boolean getHotGiftOnce(Context context) {
        return SpUtil.getBoolean(context, HOT_GIFT_ONCE, false);
    }

    public static void setHotGiftOnce(Context context, boolean b) {
        SpUtil.putBoolean(context, HOT_GIFT_ONCE, b);
    }


    public static void saveLocationTime(Context context) {
        SpUtil.putLong(context, LOCATION_TIME, System.currentTimeMillis());
    }

    public static long getLocationTime(Context context) {
        return SpUtil.getLong(context, LOCATION_TIME, 0);
    }

    public static void saveLocationCity(Context context, String city) {
        SpUtil.putString(context, LOCATION_CITY, city);
    }

    public static String getLocationCity(Context context) {
        return SpUtil.getString(context, LOCATION_CITY, null);
    }

    public static void saveSplashPic(Context context, String picUrl) {
        SpUtil.putString(context, SPLASH_PIC, picUrl);
    }

    public static String getSplashPic(Context context) {
        return SpUtil.getString(context, SPLASH_PIC, null);
    }

    public static void saveSplashUrl(Context context, String linkUrl) {
        SpUtil.putString(context, SPLASH_URL, linkUrl);
    }

    public static String getSplashUrl(Context context) {
        return SpUtil.getString(context, SPLASH_URL, null);
    }

    public static void saveSplashLeftTime(Context context, String leftTime) {
        SpUtil.putString(context, LEFT_TIME, leftTime);
    }

    public static String getSplashLeftTime(Context context) {
        return SpUtil.getString(context, LEFT_TIME, null);
    }

    public static void saveSplashPicStartTime(Context context, long startTime) {
        SpUtil.putLong(context, START_TIME, startTime);
    }

    public static long getSplashPicStartTime(Context context) {
        return SpUtil.getLong(context, START_TIME, -1);
    }

    public static void saveTipsUpdateTime(Context context) {
        SpUtil.putLong(context, TIPS_TIME, System.currentTimeMillis());
    }

    public static long getTipsUpdateTime(Context context) {
        return SpUtil.getLong(context, TIPS_TIME, 0);
    }

    public static void saveTaskNotGave(Context context, String game_reward) {
        SpUtil.putString(context, GAME_REWARD, game_reward);
    }

    public static String getTaskNotGave(Context context) {
        return SpUtil.getString(context, GAME_REWARD, "");
    }

    public static boolean getPayCheck(Context context, String userId) {
        return SpUtil.getBoolean(context, userId, false);
    }

    public static void setPayCheck(Context context, String userId, boolean b) {
        SpUtil.putBoolean(context, userId, b);
    }

    public static int getSubjectGameId(Context context) {
        return SpUtil.getInt(context, SUBJECT_GAME_ID, 133);
    }

    public static void saveSubjectGameId(Context context, int subjectId) {
        SpUtil.putInt(context, SUBJECT_GAME_ID, subjectId);
    }

    public static String getDeviceNo(Context context) {
        return SpUtil.getString(context, DEVICE_NO, "");
    }

    public static void saveDeviceNo(Context context, String deviceNo) {
        SpUtil.putString(context, DEVICE_NO, deviceNo);
    }

    public static String getCouponActive(Context context) {
        return SpUtil.getString(context, COUPON_ACTIVE, "");
    }

    public static void saveCouponActive(Context context, String couponActive) {
        SpUtil.putString(context, COUPON_ACTIVE, couponActive);
    }

    public static boolean getGiftSaveDialog(Context context) {
        return SpUtil.getBoolean(context, GIFT_SAVE_DIALOG, false);
    }

    public static void saveGiftSaveDialog(Context context, boolean couponActive) {
        SpUtil.putBoolean(context, GIFT_SAVE_DIALOG, couponActive);
    }
}

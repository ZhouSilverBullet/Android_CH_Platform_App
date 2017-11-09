package com.chsdk.utils;

import android.content.Context;
import android.text.TextUtils;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.chsdk.configure.DataStorage;

import org.wlf.filedownloader.util.NetworkUtil;

/**
 * Created by ZengLei on 2017/1/19.
 */

public class LocationHelper {
    private Context context;
    private AMapLocationClient locationClient;
    private boolean isLocationing;
    private static LocationHelper locationHelper;

    public static LocationHelper getInstance(Context context) {
        if (locationHelper == null) {
            synchronized (LocationHelper.class) {
                if (locationHelper == null) {
                    locationHelper = new LocationHelper(context);
                }
            }
        }
        return locationHelper;
    }

    private LocationHelper(Context context) {
        this.context = context;
    }

    public boolean isLocationing() {
        return isLocationing;
    }

    public String getLastLocation() {
        try {
            if (locationClient == null) {
                locationClient = new AMapLocationClient(context);
            }
            AMapLocation location = locationClient.getLastKnownLocation();
            LogUtil.errorLog("LocationHelper getLastKnownLocation:" + location);
            if (location != null) {
                return location.getCity();
            }
        } catch (Exception e) {
            LogUtil.errorLog("LocationHelper getLastKnownLocation error:" + e.getMessage());
        }
        return null;
    }

    public synchronized void onDestroy() {
        LogUtil.errorLog("LocationHelper onDestroy ");
        isLocationing = false;
        try {
            if (locationClient != null) {
                locationClient.stopLocation();
                locationClient.onDestroy();
                locationClient = null;
            }
        } catch (Exception e) {
            LogUtil.errorLog("LocationHelper onDestroy error:" + e.getMessage());
        }
        locationHelper = null;
    }

    private AMapLocationClientOption getDefaultOption(){
        AMapLocationClientOption mOption = new AMapLocationClientOption();
        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        mOption.setGpsFirst(false);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        mOption.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        mOption.setInterval(2000);//可选，设置定位间隔。默认为2秒
        mOption.setNeedAddress(true);//可选，设置是否返回逆地理地址信息。默认是true
        mOption.setOnceLocation(true);//可选，设置是否单次定位。默认是false
        mOption.setOnceLocationLatest(true);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
        AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP);//可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
        mOption.setSensorEnable(false);//可选，设置是否使用传感器。默认是false
        mOption.setWifiScan(false); //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
        mOption.setLocationCacheEnable(true); //可选，设置是否使用缓存定位，默认为true
        return mOption;
    }

    public void startLocation() {
        if (isLocationing) {
            return;
        }
        isLocationing = true;
        try {
            if (locationClient == null) {
                locationClient = new AMapLocationClient(context);
            }
            locationClient.setLocationOption(getDefaultOption());

            LogUtil.errorLog("LocationHelper startLocation");
            locationClient.setLocationListener(new AMapLocationListener() {
                @Override
                public void onLocationChanged(AMapLocation loc) {
                    isLocationing = false;
                    LogUtil.errorLog("LocationHelper onLocationChanged:" + loc);
                    if (null != loc) {
                        String city = loc.getCity();
                        if (!TextUtils.isEmpty(city)) {
                            saveCity(context, city);
                        } else {
                            city = getLastLocation();
                            if (!TextUtils.isEmpty(city)) {
                                saveCity(context, city);
                            }
                        }
                    }
                    onDestroy();
                }
            });
            locationClient.startLocation();
        } catch (Exception e) {
            LogUtil.errorLog("LocationHelper startLocation error:" + e.getMessage());
        }
    }

    public static void initLocation(Context context) {
        long time = DataStorage.getLocationTime(context);
        String city = getCity(context);
        if (TextUtils.isEmpty(city) || System.currentTimeMillis() - time > 2 * 60 * 60 * 1000) {
            LogUtil.errorLog("LocationHelper initLocation");
            if (NetworkUtil.isNetworkAvailable(context)) {
                LocationHelper helper = LocationHelper.getInstance(context);
                helper.startLocation();
            } else {
                if (TextUtils.isEmpty(city)) {
                    LocationHelper helper = LocationHelper.getInstance(context);
                    city = helper.getLastLocation();
                    helper.onDestroy();
                    DataStorage.saveLocationCity(context, city);
                }
            }
        }
    }

    public static String getCity(Context context) {
        return DataStorage.getLocationCity(context);
    }

    private static void saveCity(Context context, String city) {
        DataStorage.saveLocationCity(context, city);
        DataStorage.saveLocationTime(context);
    }
}

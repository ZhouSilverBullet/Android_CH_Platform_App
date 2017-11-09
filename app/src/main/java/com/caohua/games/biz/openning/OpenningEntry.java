package com.caohua.games.biz.openning;

import android.text.TextUtils;

import com.chsdk.biz.download.DownloadEntry;
import com.chsdk.model.BaseEntry;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by CXK on 2016/10/22.
 */

public class OpenningEntry extends BaseEntry {
    private String game_name;
    private String game_icon;
    private String service_name;
    private String game_url;
    private String open_time;
    private String classify_name;
    private String game_size;
    private String package_name;
    private String detail_url;
    private String hour;

    public String getDetail_url() {
        return detail_url;
    }

    public void setDetail_url(String detail_url) {
        this.detail_url = detail_url;
    }

    public String getDay() {
        if (!TextUtils.isEmpty(open_time)) {
            try {
                Calendar cal = new GregorianCalendar();
                Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(open_time);
                cal.setTime(date);
                return (cal.get(Calendar.MONTH)) + 1 + "月" + cal.get(Calendar.DAY_OF_MONTH) + "日";
            } catch (ParseException e) {
            }
            String[] arr = open_time.split(" ");
            if (arr.length > 0) {
                return open_time.split(" ")[0];
            }
        }
        return "";
    }

    public String getHour() {
        if (!TextUtils.isEmpty(hour)) {
            return hour;
        }

        if (!TextUtils.isEmpty(open_time)) {
            try {
                Calendar cal = new GregorianCalendar();
                Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(open_time);
                cal.setTime(date);
                hour = (cal.get(Calendar.HOUR_OF_DAY) < 10 ? "0" : "") + cal.get(Calendar.HOUR_OF_DAY)  + ":"
                        + (cal.get(Calendar.MINUTE) < 10 ? "0" : "") + cal.get(Calendar.MINUTE);
                return hour;
            } catch (ParseException e) {
            }

            String[] arr = open_time.split(" ");
            if (arr.length > 1) {
                hour = open_time.split(" ")[1];
                return hour;
            }
        }
        return "";
    }

    public String getService_name() {
        return service_name;
    }

    public void setService_name(String service_name) {
        this.service_name = service_name;
    }

    public String getClassify_name() {
        return classify_name;
    }

    public void setClassify_name(String classify_name) {
        this.classify_name = classify_name;
    }

    public String getGame_icon() {
        return game_icon;
    }

    public void setGame_icon(String game_icon) {
        this.game_icon = game_icon;
    }

    public String getGame_name() {
        return game_name;
    }

    public void setGame_name(String game_name) {
        this.game_name = game_name;
    }

    public String getGame_size() {
        return game_size;
    }

    public void setGame_size(String game_size) {
        this.game_size = game_size;
    }

    public String getGame_url() {
        return game_url;
    }

    public void setGame_url(String game_url) {
        this.game_url = game_url;
    }

    public String getOpen_time() {
        return open_time;
    }

    public void setOpen_time(String open_time) {
        this.open_time = open_time;
    }

    public String getPackage_name() {
        return package_name;
    }

    public void setPackage_name(String package_name) {
        this.package_name = package_name;
    }

    public DownloadEntry getDownloadEntry() {
        DownloadEntry entry = new DownloadEntry();
        entry.downloadUrl = game_url;
        entry.iconUrl = game_icon;
        entry.pkg = package_name;
        entry.title = game_name;
        entry.detail_url =detail_url;
        return entry;
    }

    public boolean sameIcon(OpenningEntry entry) {
        return entry != null && !TextUtils.isEmpty(game_icon) && game_icon.equals(entry.game_icon);
    }
}

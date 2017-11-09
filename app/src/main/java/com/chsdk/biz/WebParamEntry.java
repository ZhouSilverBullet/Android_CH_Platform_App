package com.chsdk.biz;

import com.chsdk.model.BaseEntry;

import java.util.Map;

/**
 * Created by admin on 2017/11/8.
 */

public class WebParamEntry extends BaseEntry {
    private Map<String, String> map;

    public Map<String, String> getMap() {
        return map;
    }

    public void setMap(Map<String, String> map) {
        this.map = map;
    }
}

package com.chsdk.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ZengLei on 2016/10/15.
 */

public class ResultBean<T> implements Serializable {
    public ResultBean(List<T> data) {
        this.data = data;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    private List<T> data;

}

package com.caohua.games.biz.home;

/**
 * Created by ZengLei on 2016/11/10.
 */

public class TopicDetailModel extends RequestPageModel {

    private String id;

    public TopicDetailModel(String requestCount, String loadedCount) {
        super(requestCount, loadedCount);
    }

    public void putDataInMap() {
        put(PARAMS_SUBJECT_ID, id);
        super.putDataInMap();
    }

    public void setId(String id) {
        this.id = id;
    }
}

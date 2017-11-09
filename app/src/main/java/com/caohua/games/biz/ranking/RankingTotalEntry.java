package com.caohua.games.biz.ranking;

import com.chsdk.model.BaseEntry;

import java.util.List;

/**
 * Created by CXK on 2016/10/22.
 */

public class RankingTotalEntry extends BaseEntry {

    public List<RankingTotalSubEntry> multiple;
    public List<RankingTotalSubEntry> recommend;
    public List<RankingTotalSubEntry> online;

    public List<RankingTotalSubEntry> getMultiple() {
        return multiple;
    }

    public void setMultiple(List<RankingTotalSubEntry> multiple) {
        this.multiple = multiple;
    }

    public List<RankingTotalSubEntry> getNewly() {
        return newly;
    }

    public void setNewly(List<RankingTotalSubEntry> newly) {
        this.newly = newly;
    }

    public List<RankingTotalSubEntry> getOnline() {
        return online;
    }

    public void setOnline(List<RankingTotalSubEntry> online) {
        this.online = online;
    }

    public List<RankingTotalSubEntry> getRecommend() {
        return recommend;
    }

    public void setRecommend(List<RankingTotalSubEntry> recommend) {
        this.recommend = recommend;
    }

    public List<RankingTotalSubEntry> newly;
}

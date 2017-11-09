package com.caohua.games.biz.send;

import com.chsdk.model.BaseEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZengLei on 2017/5/22.
 */

public class ArticleEntry extends BaseEntry {
    public String title;
    public String tag;
    public String tagName;
    public String content;
    public String forumId;
    public ArrayList<String> savePics;
    public List<String> pics;

    public String getPics() {
        if (pics == null || pics.size() == 0)
            return null;

        StringBuilder sb = new StringBuilder();
        for (String path : pics) {
            if (sb.length() > 0) {
                sb.append(",");
            }
            sb.append(path);
        }
        return sb.toString();
    }
}

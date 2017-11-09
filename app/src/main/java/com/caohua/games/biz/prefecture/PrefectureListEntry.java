package com.caohua.games.biz.prefecture;

import com.chsdk.model.BaseEntry;

import java.util.List;

/**
 * Created by zhouzhou on 2017/6/2.
 */

public class PrefectureListEntry extends BaseEntry {

    private List<ContentBean> content;
    private List<ClassifyBean> classify;

    public List<ContentBean> getContent() {
        return content;
    }

    public void setContent(List<ContentBean> content) {
        this.content = content;
    }

    public List<ClassifyBean> getClassify() {
        return classify;
    }

    public void setClassify(List<ClassifyBean> classify) {
        this.classify = classify;
    }

    public static class ContentBean extends BaseEntry {

        private String title;
        private String image;
        private String classify_name;
        private String time;
        private String url;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getClassify_name() {
            return classify_name;
        }

        public void setClassify_name(String classify_name) {
            this.classify_name = classify_name;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    public static class ClassifyBean extends BaseEntry {

        private String classify_id;
        private String classify_name;

        public String getClassify_id() {
            return classify_id;
        }

        public void setClassify_id(String classify_id) {
            this.classify_id = classify_id;
        }

        public String getClassify_name() {
            return classify_name;
        }

        public void setClassify_name(String classify_name) {
            this.classify_name = classify_name;
        }
    }
}

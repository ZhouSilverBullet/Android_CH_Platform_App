package com.caohua.games.biz.prefecture;

import com.chsdk.model.BaseEntry;

import java.util.List;

/**
 * Created by zhouzhou on 2017/5/24.
 */

public class ContentEntry extends BaseEntry {

    private int code;
    private String msg;
    private DataBean data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean extends BaseEntry {
        private List<ContentBean> content;

        public List<ContentBean> getContent() {
            return content;
        }

        public void setContent(List<ContentBean> content) {
            this.content = content;
        }

        public static class ContentBean extends BaseEntry {
            /**
             * title : 周末送福利 充值领绑银
             * image : http://www.caohua.com/UpLoadFiles/Article/2016/12/09/636168970366354507.jpg
             * type : 1
             * url : http://m.caohua.com/article/detail?id=10903&fr=app
             */

            private String title;
            private String image;
            private int type;
            private String url;
            private String time;
            private String classify_name;

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

            public int getType() {
                return type;
            }

            public void setType(int type) {
                this.type = type;
            }

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }

            public String getTime() {
                return time;
            }

            public void setTime(String time) {
                this.time = time;
            }

            public String getClassify_name() {
                return classify_name;
            }

            public void setClassify_name(String classify_name) {
                this.classify_name = classify_name;
            }
        }
    }
}

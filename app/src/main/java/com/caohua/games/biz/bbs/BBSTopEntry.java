package com.caohua.games.biz.bbs;

import com.chsdk.model.BaseEntry;

import java.util.List;

/**
 * Created by zhouzhou on 2017/5/25.
 */

public class BBSTopEntry extends BaseEntry {
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
        private String forum_name;
        private String game_icon;
        private String back_url;
        private String forum_memo;
        private String forum_count;
        private String forum_day_count;
        private List<ForumTagBean> forum_tag;
        private List<TopArticleBean> top_article;

        public String getForum_memo() {
            return forum_memo;
        }

        public void setForum_memo(String forum_memo) {
            this.forum_memo = forum_memo;
        }

        public String getForum_count() {
            return forum_count;
        }

        public void setForum_count(String forum_count) {
            this.forum_count = forum_count;
        }

        public String getForum_day_count() {
            return forum_day_count;
        }

        public void setForum_day_count(String forum_day_count) {
            this.forum_day_count = forum_day_count;
        }

        public String getForum_name() {
            return forum_name;
        }

        public void setForum_name(String forum_name) {
            this.forum_name = forum_name;
        }

        public String getGame_icon() {
            return game_icon;
        }

        public void setGame_icon(String game_icon) {
            this.game_icon = game_icon;
        }

        public String getBack_url() {
            return back_url;
        }

        public void setBack_url(String back_url) {
            this.back_url = back_url;
        }

        public List<ForumTagBean> getForum_tag() {
            return forum_tag;
        }

        public void setForum_tag(List<ForumTagBean> forum_tag) {
            this.forum_tag = forum_tag;
        }

        public List<TopArticleBean> getTop_article() {
            return top_article;
        }

        public void setTop_article(List<TopArticleBean> top_article) {
            this.top_article = top_article;
        }

        public static class ForumTagBean extends BaseEntry {
            private String tag_id;
            private String tag_name;

            public String getTag_id() {
                return tag_id;
            }

            public void setTag_id(String tag_id) {
                this.tag_id = tag_id;
            }

            public String getTag_name() {
                return tag_name;
            }

            public void setTag_name(String tag_name) {
                this.tag_name = tag_name;
            }
        }

        public static class TopArticleBean extends BaseEntry {
            private String article_id;
            private String tag_id;
            private String tag_name;
            private String title;
            private boolean is_titlebold;
            private boolean is_titlext;
            private boolean is_underline;
            private String title_color;
            private String detail_url;

            public String getArticle_id() {
                return article_id;
            }

            public void setArticle_id(String article_id) {
                this.article_id = article_id;
            }

            public String getTag_id() {
                return tag_id;
            }

            public void setTag_id(String tag_id) {
                this.tag_id = tag_id;
            }

            public String getTag_name() {
                return tag_name;
            }

            public void setTag_name(String tag_name) {
                this.tag_name = tag_name;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public boolean isIs_titlebold() {
                return is_titlebold;
            }

            public void setIs_titlebold(boolean is_titlebold) {
                this.is_titlebold = is_titlebold;
            }

            public boolean isIs_titlext() {
                return is_titlext;
            }

            public void setIs_titlext(boolean is_titlext) {
                this.is_titlext = is_titlext;
            }

            public boolean isIs_underline() {
                return is_underline;
            }

            public void setIs_underline(boolean is_underline) {
                this.is_underline = is_underline;
            }

            public String getTitle_color() {
                return title_color;
            }

            public void setTitle_color(String title_color) {
                this.title_color = title_color;
            }

            public String getDetail_url() {
                return detail_url;
            }

            public void setDetail_url(String detail_url) {
                this.detail_url = detail_url;
            }
        }
    }
}

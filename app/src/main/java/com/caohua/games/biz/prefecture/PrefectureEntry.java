package com.caohua.games.biz.prefecture;

import com.chsdk.model.BaseEntry;

import java.util.List;

/**
 * Created by zhouzhou on 2017/5/9.
 */

public class PrefectureEntry extends BaseEntry {

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

        private ModuleBean module;
        private List<MainBannerBean> main_banner;
        private List<TabBannerBean> tab_banner;

        public ModuleBean getModule() {
            return module;
        }

        public void setModule(ModuleBean module) {
            this.module = module;
        }

        public List<MainBannerBean> getMain_banner() {
            return main_banner;
        }

        public void setMain_banner(List<MainBannerBean> main_banner) {
            this.main_banner = main_banner;
        }

        public List<TabBannerBean> getTab_banner() {
            return tab_banner;
        }

        public void setTab_banner(List<TabBannerBean> tab_banner) {
            this.tab_banner = tab_banner;
        }

        public static class ModuleBean extends BaseEntry {

            private String game_icon;
            private String game_name;
            private String cover;

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

            public String getCover() {
                return cover;
            }

            public void setCover(String cover) {
                this.cover = cover;
            }
        }

        public static class MainBannerBean extends BaseEntry {
            /**
             * type : url
             * name : 测试1
             * url : http://m.caohua.com/gift/gamelist?id=212&fr=app
             * target : forum
             * id : 133
             */

            private String type;
            private String name;
            private String url;
            private String target;
            private int id;
            private int banner_type;

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }

            public String getTarget() {
                return target;
            }

            public void setTarget(String target) {
                this.target = target;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public int getBanner_type() {
                return banner_type;
            }

            public void setBanner_type(int banner_type) {
                this.banner_type = banner_type;
            }
        }

        public static class TabBannerBean extends BaseEntry {
            /**
             * name : 测试tab
             * banner_id : 98
             */

            private String name;
            private String banner_id;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getBanner_id() {
                return banner_id;
            }

            public void setBanner_id(String banner_id) {
                this.banner_id = banner_id;
            }
        }
    }
}

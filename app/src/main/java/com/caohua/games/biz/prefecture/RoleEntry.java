package com.caohua.games.biz.prefecture;

import com.chsdk.model.BaseEntry;

/**
 * Created by zhouzhou on 2017/5/31.
 */

public class RoleEntry extends BaseEntry {

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

        private UserRoleBean user_role;

        public UserRoleBean getUser_role() {
            return user_role;
        }

        public void setUser_role(UserRoleBean user_role) {
            this.user_role = user_role;
        }

        public static class UserRoleBean extends BaseEntry {
            /**
             * role_name : 摩西安格斯
             * server_name : C164白杨导弹
             */

            private String role_name;
            private String server_name;

            public String getRole_name() {
                return role_name;
            }

            public void setRole_name(String role_name) {
                this.role_name = role_name;
            }

            public String getServer_name() {
                return server_name;
            }

            public void setServer_name(String server_name) {
                this.server_name = server_name;
            }
        }
    }
}

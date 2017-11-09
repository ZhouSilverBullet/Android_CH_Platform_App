package com.caohua.games.biz.account;

import com.chsdk.configure.SdkSession;
import com.chsdk.model.BaseModel;

/**
 * Created by CXK on 2016/11/7.
 */

public class AccountModel extends BaseModel {
    String nickName;
    String userSex;
    String userBirthday;
    String userQQ;
    String userAderess;
    String userId;

    public AccountModel() {
        userId = SdkSession.getInstance().getUserId();;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public void setUserAderess(String userAderess) {
        this.userAderess = userAderess;
    }

    public void setUserBirthday(String userBirthday) {
        this.userBirthday = userBirthday;
    }

    public void setUserQQ(String userQQ) {
        this.userQQ = userQQ;
    }

    public void setUserSex(String userSex) {
        this.userSex = userSex;
    }

    @Override
    public void putDataInMap() {
        put(PARAMS_USER_ID, userId);
        put(PARAMS_NIKE_NAME, nickName);
        put(PARAMS_USER_SEX, userSex);
        put(PARAMS_USER_BIRTHDAY, userBirthday);
        put(PARAMS_USER_QQ, userQQ);
        put(PARAMS_USER_ADRESS, userAderess);
    }

    public boolean equalsValue(String tempValue, String lastAddressValue, String time, String lastQqValue, String lastNameValue) {
        if (tempValue.equals(userSex) && lastAddressValue.equals(userAderess)
                && time.equals(userBirthday) && lastQqValue.equals(userQQ) && lastNameValue.equals(nickName)) {
            return true;
        }
        return false;
    }
}

package com.chsdk.http;

/**
 * Created by zhouzhou on 2017/5/9.
 */

public interface IRequest2Listener {
    void success(String result);

    void failed(String error, int errorCode);
}

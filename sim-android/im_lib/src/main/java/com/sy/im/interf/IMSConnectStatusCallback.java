package com.sy.im.interf;

/**
 * ims 连接状态回调
 */
public interface IMSConnectStatusCallback {

    /**
     * ims连接中
     */
    void onConnecting();

    /**
     * ims连接成功
     */
    void onConnected();

    /**
     * ims连接失败
     */
    void onConnectFailed();

    /**
     * 签名过期
     */
    void onUserSigExpired();

    /**
     * 被踢下线
     */
    void onKickedOffline();
}
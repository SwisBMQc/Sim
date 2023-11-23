package com.sy.im.interf;

import com.sy.im.protobuf.MessageProtobuf;

/**
 * 发送消息回调
 */
public interface IMSendCallback {

    void onSuccess(MessageProtobuf.Msg msg);

    void onError(String msg);

}

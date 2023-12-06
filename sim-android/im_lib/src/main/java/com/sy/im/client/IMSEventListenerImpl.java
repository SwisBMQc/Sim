package com.sy.im.client;

import com.sy.im.interf.OnEventListener;
import com.sy.im.protobuf.MessageProtobuf;

/**
 * @Author：sy
 * @Date：2023/12/4
 */
public class IMSEventListenerImpl implements OnEventListener {
    @Override
    public void dispatchMsg(MessageProtobuf.Msg msg) {

    }

    @Override
    public boolean isNetworkAvailable() {
        return true;
    }

    @Override
    public int getReconnectInterval() {
        return 0;
    }

    @Override
    public int getConnectTimeout() {
        return 0;
    }

    @Override
    public int getForegroundHeartbeatInterval() {
        return 0;
    }

    @Override
    public int getBackgroundHeartbeatInterval() {
        return 0;
    }

    @Override
    public MessageProtobuf.Msg getHandshakeMsg() {
        return null;
    }

    @Override
    public MessageProtobuf.Msg getHeartbeatMsg() {
        return null;
    }

    @Override
    public int getResendCount() {
        return 0;
    }

    @Override
    public int getResendInterval() {
        return 0;
    }
}

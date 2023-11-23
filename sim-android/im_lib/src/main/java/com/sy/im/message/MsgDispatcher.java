package com.sy.im.message;

import com.sy.im.interf.OnEventListener;
import com.sy.im.protobuf.MessageProtobuf;

/**
 * 消息转发器
 *
 */
public class MsgDispatcher {

    private OnEventListener listener;

    public MsgDispatcher() {

    }

    public void setOnEventListener(OnEventListener listener) {
        this.listener = listener;
    }

    /**
     * 接收消息，并通过OnEventListener转发消息到应用层
     * @param msg
     */
    public void receivedMsg(MessageProtobuf.Msg msg) {
        if(listener == null) {
            return;
        }

        listener.dispatchMsg(msg);
    }
}

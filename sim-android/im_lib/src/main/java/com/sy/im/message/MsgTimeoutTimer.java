package com.sy.im.message;

import com.alibaba.fastjson.JSONObject;
import com.sy.im.interf.IMSClient;
import com.sy.im.protobuf.MessageProtobuf;
import com.sy.im.util.IMSConfig;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 消息发送超时定时器，每一条消息对应一个定时器
 */
public class MsgTimeoutTimer extends Timer {

    private IMSClient imsClient;// ims客户端
    private MessageProtobuf.Msg msg;// 发送的消息
    private int currentResendCount;// 当前重发次数
    private MsgTimeoutTask task;// 消息发送超时任务

    public MsgTimeoutTimer(IMSClient imsClient, MessageProtobuf.Msg msg) {
        this.imsClient = imsClient;
        this.msg = msg;
        task = new MsgTimeoutTask();
        this.schedule(task, imsClient.getResendInterval(), imsClient.getResendInterval()); // 延迟执行时间，上一个成功任务的时间间隔
    }

    /**
     * 消息发送超时任务
     */
    private class MsgTimeoutTask extends TimerTask {

        @Override
        public void run() {
            if (imsClient.isClosed()) { // 客户端关闭，移除消息，直接退出
                if (imsClient.getMsgTimeoutTimerManager() != null) {
                    imsClient.getMsgTimeoutTimerManager().remove(msg.getHead().getMsgId());
                }

                return;
            }

            currentResendCount++; // 当前重发次数
            if (currentResendCount > imsClient.getResendCount()) {
                // 重发次数大于可重发次数，直接标识为发送失败，并通过消息转发器通知应用层
                try {

                    // 构建一个失败消息
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("status",IMSConfig.DEFAULT_REPORT_SERVER_SEND_MSG_FAILURE);

                    MessageProtobuf.Msg.Builder builder = MessageProtobuf.Msg.newBuilder();
                    MessageProtobuf.Head.Builder headBuilder = MessageProtobuf.Head
                                    .newBuilder()
                                    .setMsgId(msg.getHead().getMsgId())
                                    .setMsgType(MessageType.SERVER_MSG_SENT_STATUS_REPORT.getMsgType()) // 服务端消息状态报告
                                    .setTimestamp(System.currentTimeMillis())
                                    .setExtend(jsonObject.toString());

                    builder.setHead(headBuilder.build());

                    // 通知应用层消息发送失败
                    imsClient.getMsgDispatcher().receivedMsg(builder.build());
                } finally {
                    // 从消息发送超时管理器移除该消息
                    imsClient.getMsgTimeoutTimerManager().remove(msg.getHead().getMsgId());

                    // 执行到这里，认为连接已断开或不稳定，触发重连
                    imsClient.resetConnect();
                    currentResendCount = 0;
                }
            } else {
                // 发送消息，但不再加入超时管理器，达到最大发送失败次数就算了
                sendMsg();
            }
        }
    }

    public void sendMsg() {
        System.out.println("正在重发消息，message=" + msg);
        imsClient.sendMsg(msg, false);
    }

    public MessageProtobuf.Msg getMsg() {
        return msg;
    }

    @Override
    public void cancel() {
        if (task != null) {
            task.cancel();
            task = null;
        }

        super.cancel();
    }
}

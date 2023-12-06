package com.sy.im.client;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sy.im.interf.IMSClient;
import com.sy.im.interf.IMSConnectStatusCallback;
import com.sy.im.interf.IMSendCallback;
import com.sy.im.interf.OnEventListener;
import com.sy.im.message.MessageManager;
import com.sy.im.protobuf.MessageProtobuf;
import com.sy.im.netty.IMSClientFactory;

import java.util.concurrent.CopyOnWriteArrayList;

public class IMSClientBootstrap {

    private static final IMSClientBootstrap INSTANCE = new IMSClientBootstrap();
    private IMSClient imsClient;
    private boolean isActive;

    private IMSClientBootstrap() {}

    public static IMSClientBootstrap getInstance() {
        return INSTANCE;
    }

    public static void main(String[] args) {
        String hosts = "[{\"host\":\"127.0.0.1\", \"port\":9090}]";
        IMSClientBootstrap bootstrap = IMSClientBootstrap.getInstance();
        bootstrap.init(
                hosts,
                new IMSEventListenerImpl(),
                new IMSConnectStatusImpl(),
                0);
    }

    public synchronized void init(String hosts, OnEventListener iMSEventListener,
                                  IMSConnectStatusCallback iMSConnectStatusListener, int appStatus) {
        if (!isActive) {
            CopyOnWriteArrayList<String> serverUrlList = convertHosts(hosts);
            if (serverUrlList == null || serverUrlList.size() == 0){
                System.err.println("sim-init IMLibClientBootstrap error,ims hosts is null");
                return;
            }

            isActive = true;
            System.out.println("sim-init IMLibClientBootstrap, servers=" + hosts);
            if (null != imsClient) {
                imsClient.close();
            }
            imsClient = IMSClientFactory.getIMSClient();
            imsClient.init(serverUrlList, iMSEventListener, iMSConnectStatusListener);
            updateAppStatus(appStatus); // 0为前台
        }
    }

    /**
     * 发送消息
     *
     * @param msg
     */
    public void sendMessage(MessageProtobuf.Msg msg) {
        if (isActive) {
            imsClient.sendMsg(msg);
        }
    }

    /**
     * 发送消息
     * 同时加入消息管理器
     * @param msg
     * @param callback
     */
    public void sendMessage(MessageProtobuf.Msg msg,IMSendCallback callback) {
        if (isActive) {
            MessageManager.put(msg.getHead().getMsgId(),callback);
            sendMessage(msg);
        }
    }

    /**
     * 根据前台和后台来调整心跳间隔
     * @param appStatus
     */
    public void updateAppStatus(int appStatus) {
        if (imsClient == null) {
            return;
        }

        imsClient.setAppStatus(appStatus);
    }
    public boolean isActive() {
        return isActive;
    }

    private CopyOnWriteArrayList<String> convertHosts(String hosts) {
        if (hosts != null && hosts.length()>0){
            // 拆解成json数组
            JSONArray hostArray = JSONArray.parseArray(hosts);
            if (null != hostArray && hostArray.size() > 0) {
                CopyOnWriteArrayList<String> serverUrlList = new CopyOnWriteArrayList<>();
                JSONObject host;
                for (int i = 0; i < hostArray.size(); i++) {
                    host = JSON.parseObject(hostArray.get(i).toString());
                    serverUrlList.add(host.getString("host") + " "
                            + host.getInteger("port"));
                }
                return serverUrlList;
            }
        }
        return null;
    }


}

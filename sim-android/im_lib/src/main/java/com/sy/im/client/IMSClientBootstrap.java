package com.sy.im.client;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sy.im.interf.IMSClient;
import com.sy.im.ulit.IMSClientFactory;

import java.util.concurrent.CopyOnWriteArrayList;

import io.netty.util.internal.StringUtil;

public class IMSClientBootstrap {

    private static final IMSClientBootstrap INSTANCE = new IMSClientBootstrap();
    private IMSClient imsClient;
    private boolean isActive;

    private IMSClientBootstrap() {}

    public static IMSClientBootstrap getInstance() {
        return INSTANCE;
    }

    public static void main(String[] args) {
        String userId = "100001";
        String token = "token_" + userId;
        String hosts = "[{\"host\":\"127.0.0.1\", \"port\":9000}]";
        IMSClientBootstrap bootstrap = IMSClientBootstrap.getInstance();
        bootstrap.init(userId, token, hosts, 0);
    }

    public synchronized void init(String userId, String token, String hosts, int appStatus) {
        if (!isActive) {
            CopyOnWriteArrayList<String> serverUrlList = convertHosts(hosts);
            if (serverUrlList == null || serverUrlList.size() == 0){
                System.out.println("init IMLibClientBootstrap error,ims hosts is null");
                return;
            }

            isActive = true;
            System.out.println("init IMLibClientBootstrap, servers=" + hosts);
            // 关闭重启
            if (null != imsClient) {
                imsClient.close();
            }
            imsClient = IMSClientFactory.getIMSClient();
            imsClient.init(serverUrlList, new IMSEventListener(userId, token), new IMSConnectStatusListener());
        }
    }

    private void updateAppStatus(int appStatus) {
        if (imsClient == null) {
            return;
        }

        imsClient.setAppStatus(appStatus);
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

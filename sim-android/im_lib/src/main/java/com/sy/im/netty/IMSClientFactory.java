package com.sy.im.netty;

import com.sy.im.interf.IMSClient;
import com.sy.im.netty.NettyTcpClient;

public class IMSClientFactory {
    public static IMSClient getIMSClient(){
        return NettyTcpClient.getInstance();
    }
}

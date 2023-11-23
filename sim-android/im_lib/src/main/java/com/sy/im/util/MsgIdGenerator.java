package com.sy.im.util;

import java.util.UUID;

public class MsgIdGenerator {
    public static String generateMsgId() {
        // 使用UUID生成唯一的msgId
        return UUID.randomUUID().toString();
    }
}
package com.sy.im.netty.util;

import io.netty.util.AttributeKey;

//接口中声明变量默认的是static final
public interface State {
    AttributeKey<String> USERNAME = AttributeKey.newInstance("username");
    AttributeKey<Boolean> LOGIN = AttributeKey.newInstance("login");
}

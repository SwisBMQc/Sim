package com.sy.im.common.result;

import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;

/**
 * 返回 一个map对象
 * k: status    v: 1 或 -1
 * k: reason    v: 消息
 * k: data      v: 也是一个map对象
 */
public class ResultJson extends HashMap<String, Object> {
    private static final long serialVersionUID = 1L;

    private static final Integer SUCCESS_STATUS = 1;
    private static final Integer ERROR_STATUS = -1;

    private static final String SUCCESS_MSG = "操作成功";


    public ResultJson() {
        super();
    }

    /**
     * 有参构造
     * @param status 状态： -1 失败，1 成功
     * @param reason 成功或失败消息
     */
    public ResultJson(int status, String reason){
        super();
        put("status", status);
        put("reason", reason);
    }

    public int getStatus(){
        return (int) get("status");
    }


    public static ResultJson success(){
        return new  ResultJson(SUCCESS_STATUS,SUCCESS_MSG);
    }

    public static ResultJson success(String reason){
        return new  ResultJson(SUCCESS_STATUS,reason);
    }

    public static ResultJson error(String reason){
        return new  ResultJson(ERROR_STATUS,reason);
    }

    public ResultJson setData(String key, Object obj) {
        @SuppressWarnings("unchecked")
        HashMap<String, Object> data = (HashMap<String, Object>) get("data");
        if (data == null) {
            data = new HashMap<String, Object>();
            put("data", data);
        }
        data.put(key, obj);
        return this;
    }

    /**
     * 返回JSON字符串
     */
    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}

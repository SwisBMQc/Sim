package com.sy.im.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sy.im.common.constant.APITag;
import com.sy.im.common.constant.MessageType;
import com.sy.im.common.model.vo.Person;
import com.sy.im.common.protobuf.MessageProtobuf;
import com.sy.im.common.result.ResultJson;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * @Author��sy
 * @Date��2023/11/14
 */
public class ServiceTest {

    @Test
    public void jsonTest() throws UnsupportedEncodingException {

        Person person = new Person();
        person.setUserId("admin");
        person.setNickname("�������ϵĹ���Ա");
        person.setSignature("�����и��Եĸ���ǩ��");

    }

    @Test
    public void test(){

        Person person = new Person();
        person.setUserId("userId");

        ResultJson resultJson = ResultJson.success("��¼�ɹ�")
                .setData("token", "token")
                .setData("person",person);


        // ������Ϣ
        MessageProtobuf.Head head = MessageProtobuf.Head.newBuilder()
                .setMsgType(MessageType.HANDSHAKE.getMsgType())
                .setFromId("userId")
                .setExtend(resultJson.toString())
                .build();

        MessageProtobuf.Msg msg = MessageProtobuf.Msg.newBuilder().setHead(head).build();

        String extend = msg.getHead().getExtend();
        JSONObject jsonObj = JSON.parseObject(extend);

        Map data = (Map) jsonObj.get("data");
        System.out.println(data.get("token").getClass());

        System.out.println(data.get("person")); // jsonObj
        System.out.println(data.get("person").toString());
    }
}

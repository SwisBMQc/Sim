package com.sy.im.image;

import com.alibaba.fastjson.JSONObject;
import com.google.protobuf.ByteString;
import com.sy.im.common.constant.APITag;
import com.sy.im.common.constant.MessageType;
import com.sy.im.protobuf.MessageProtobuf;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @Author：sy
 * @Date：2023/11/30
 */
public class MsgTest {
    String imagePath = "C:\\Users\\soyo1\\Pictures\\Saved Pictures\\avatar1.jpg";

    @Test
    public void test(){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("api", APITag.UPLOAD_AVATAR);

        MessageProtobuf.Head head = MessageProtobuf.Head.newBuilder()
                .setMsgId("123")
                .setToken("eyJhbGciOiJIUzUxMiIsInppcCI6IkdaSVAifQ.H4sIAAAAAAAAAKtWKi5NUrJScgwN8dANDXYNUtJRSq0oULIyNDcwMjAyNjMx0FEqLU4tykvMTQWqK0ktLlGqBQDWTmVANgAAAA.CBwJ4yeAANNUc2InzKznzSFfQHuRVzwfz16oOCqg0BEoCTp02njc_Xn8EZcuhej9BCS-ndWfKI4SdnSLHRnogQ")
                .setMsgType(MessageType.REQUEST.getMsgType())
                .setFromId("1")
                .setExtend(jsonObject.toString())
                .build();

        try {
            // 读取图片文件为字节数组
            byte[] imageBytes = Files.readAllBytes(Paths.get(imagePath));

            MessageProtobuf.Msg message = MessageProtobuf.Msg.newBuilder()
                    .setHead(head)
                    .setBody(ByteString.copyFrom(imageBytes))
                    .build();

            System.out.println(message.getSerializedSize());

            System.out.println(head.getSerializedSize());

            System.out.println(65532-head.getSerializedSize());

        } catch (Exception e) {
        e.printStackTrace();
        }
    }

    @Test
    public void test1(){
        String a = "https://sim-oss.oss-cn-guangzhou.aliyuncs.com/test/avatar/sim_201421842.jpeg";
        System.out.println(a.substring("https://sim-oss.oss-cn-guangzhou.aliyuncs.com".length(),a.length()));
    }

}

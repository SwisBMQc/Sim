package com.sy.im.image;

import org.json.JSONObject;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

public class ImageToJsonObject {

    public static void main(String[] args) {
        // 本地图片路径
        String imagePath = "path/to/your/image.jpg";

        try {
            // 读取图片文件为字节数组
            byte[] imageBytes = Files.readAllBytes(Paths.get(imagePath));

            // 将字节数组转换为Base64编码的字符串
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);

            // 创建JSON对象并将Base64字符串放入其中
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("image", base64Image);

            // 输出JSON对象
            System.out.println(jsonObject.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

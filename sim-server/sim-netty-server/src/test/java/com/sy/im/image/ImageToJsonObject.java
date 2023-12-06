package com.sy.im.image;

import org.json.JSONObject;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

public class ImageToJsonObject {

    public static void main(String[] args) {
        // ����ͼƬ·��
        String imagePath = "path/to/your/image.jpg";

        try {
            // ��ȡͼƬ�ļ�Ϊ�ֽ�����
            byte[] imageBytes = Files.readAllBytes(Paths.get(imagePath));

            // ���ֽ�����ת��ΪBase64������ַ���
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);

            // ����JSON���󲢽�Base64�ַ�����������
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("image", base64Image);

            // ���JSON����
            System.out.println(jsonObject.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

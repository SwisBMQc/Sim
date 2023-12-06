package com.sy.im.file.service;

import java.io.InputStream;

/**
 * 文件存储逻辑 接口
 * @Author：sy
 * @Date：2023/12/1
 */
public interface FileStorageService {

    /**
     * 上传头像
     * @param prefix 前缀
     * @param filename 文件名
     * @param imgData 图片的字节数组
     * @return 可读路径
     */
    String uploadAvatar(String prefix, String filename,byte[] imgData);
    String uploadFile(String prefix, String filename,byte[] imgData);
    String uploadFile(String objectName, InputStream inputStream);
    byte[] download(String filePath);
    boolean delete(String filePath);
}

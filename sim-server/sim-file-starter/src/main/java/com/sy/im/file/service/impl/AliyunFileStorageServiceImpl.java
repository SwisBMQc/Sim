package com.sy.im.file.service.impl;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.VoidResult;
import com.sy.im.file.config.OSSConfig;
import com.sy.im.file.service.FileStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Author：sy
 * @Date：2023/12/1
 */
@Slf4j
@Import(OSSConfig.class)
public class AliyunFileStorageServiceImpl implements FileStorageService {

    @Autowired
    OSSConfig properties;

    @Autowired
    OSS ossClient;

    private final static String separator = "/";

    /**
     * 上传头像
     *
     * @param prefix   前缀（用户名）
     * @param filename 文件名
     * @param imgData  图片的字节数组
     * @return 可读路径
     */
    @Override
    public String uploadAvatar(String prefix, String filename, byte[] imgData) {
        String objectName = prefix+separator+"avatar"+separator+filename;
        ByteArrayInputStream inputStream = new ByteArrayInputStream(imgData);
        return uploadFile(objectName,inputStream);
    }

    @Override
    public String uploadFile(String prefix, String filename, byte[] imgData) {
        String objectName = builderFilePath(prefix,filename);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(imgData);
        return uploadFile(objectName,inputStream);
    }

    @Override
    public String uploadFile(String objectName, InputStream inputStream) {

        try {
            ossClient.putObject(properties.getBucket(), objectName, inputStream);
            return builderReadPath(objectName);

        } catch (ClientException ce) {
            log.error("Error Message:" + ce.getMessage());
            throw new RuntimeException("上传文件失败");
        }
    }

    @Override
    public byte[] download(String filePath) {

        String objectName = filePath.substring(properties.getReadPath().length(),filePath.length());

        try {
            OSSObject ossObject = ossClient.getObject(properties.getBucket(), objectName);
            // 读取输出流
            InputStream content = ossObject.getObjectContent();

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            byte[] buffer = new byte[1024];
            int length;
            while ((length = content.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, length);
            }
            byteArrayOutputStream.flush();

            return byteArrayOutputStream.toByteArray();
        } catch (IOException ce) {
            log.error("Error Message:" + ce.getMessage());
            throw new RuntimeException("文件下载失败");
        }
    }

    @Override
    public boolean delete(String filePath) {
        String objectName = filePath.substring(properties.getReadPath().length(),filePath.length());
        try {
            // 删除文件
            ossClient.deleteObject(properties.getBucket(), objectName);

        } catch (OSSException oe) {
            throw new RuntimeException("文件删除出错");
        }
        return true;
    }

    /**
     * @param objectName
     * @return 读文件路径
     */
    private String builderReadPath(String objectName) {
        StringBuilder urlPath = new StringBuilder(properties.getReadPath())
                .append(separator)
                .append(objectName);
        return urlPath.toString();
    }


    /**
     * @param dirPath
     * @param filename  yyyy/mm/dd/file.jpg
     * @return 添加目录后的文件名
     */
    private String builderFilePath(String dirPath,String filename) {
        StringBuilder stringBuilder = new StringBuilder(50);
        if(!StringUtils.isEmpty(dirPath)){
            stringBuilder.append(dirPath).append(separator);
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        String todayStr = sdf.format(new Date());
        stringBuilder.append(todayStr)
                .append(separator)
                .append(filename);
        return stringBuilder.toString();
    }

}

package com.sy.im.file.config;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.sy.im.file.service.FileStorageService;
import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.Serializable;

/**
 * @Author：sy
 * @Date：2023/12/1
 */
@Data
@Configuration()
@ConfigurationProperties(prefix = "oss.aliyun")
@ConditionalOnClass(FileStorageService.class) // 当引入FileStorageService接口时
public class OSSConfig implements Serializable {
    private String accessKey;
    private String secretKey;
    private String bucket;
    private String endpoint;
    private String readPath;

    @Bean
    public OSS ossClient() {
        OSS ossClient = new OSSClientBuilder().build(getEndpoint(), getAccessKey(), getSecretKey());
        Runtime.getRuntime().addShutdownHook(new Thread(() -> ossClient.shutdown())); // 注册一个JVM关闭钩子
        return ossClient;
    }
}

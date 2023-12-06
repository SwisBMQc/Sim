package com.sy.im.oos;

import com.sy.im.ServerApplication;
import com.sy.im.file.config.OSSConfig;
import com.sy.im.file.service.FileStorageService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * @Author£ºsy
 * @Date£º2023/12/1
 */
@SpringBootTest(classes = ServerApplication.class)
@RunWith(SpringRunner.class)
public class OOSTest {
    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    OSSConfig properties;

    @Test
    public void testProperties() {
        System.out.println(properties.getEndpoint());
    }

    @Test
    public void testUpdateImgFile() {
        try {
            InputStream inputStream = new FileInputStream("C:\\Users\\soyo1\\Pictures\\Saved Pictures\\admin.png");
            String filePath = fileStorageService.uploadFile("system/avatar/admin.png", inputStream);
            System.out.println(filePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}

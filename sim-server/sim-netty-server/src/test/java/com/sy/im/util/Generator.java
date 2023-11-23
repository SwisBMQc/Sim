package com.sy.im.util;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

public class Generator {
    public static void main(String[] args) {

        FastAutoGenerator.create("jdbc:mysql://localhost:3306/sim?serverTimezone=GMT%2B8&useSSL=false", "root", "root")
                // 全局配置
                .globalConfig(builder -> {
                    builder.author("sy") // 设置作者
//                            .enableSwagger() // 开启 swagger 模式

                            .fileOverride() // 覆盖已生成文件
                            .outputDir("D:\\Java\\gd-project\\sim-server\\sim-netty-server\\src\\main\\java"); // 指定输出目录
                })
                // 包配置
                .packageConfig(builder -> {
                    builder.parent("com.sy") // 设置父包名
                            .moduleName("im"); // 设置父包模块名
                })
                // 策略配置
                .strategyConfig(builder -> {
                    builder.addInclude("user_info")
//                            .addTablePrefix("user")   // 去掉表格前缀
                            .controllerBuilder()
                            .enableRestStyle()  //开启生成@RestController 控制器
                            .serviceBuilder()
                            .formatServiceFileName("%sService") // 去掉I
                            .formatServiceImplFileName("%sServiceImp")
                            .build();

                })
                .templateEngine(new FreemarkerTemplateEngine()) // 使用Freemarker引擎模板，默认的是Velocity引擎模板
                .execute();

    }
}
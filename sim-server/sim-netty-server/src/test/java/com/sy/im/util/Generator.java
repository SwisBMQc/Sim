package com.sy.im.util;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

public class Generator {
    public static void main(String[] args) {

        FastAutoGenerator.create("jdbc:mysql://localhost:3306/sim?serverTimezone=GMT%2B8&useSSL=false", "root", "root")
                // ȫ������
                .globalConfig(builder -> {
                    builder.author("sy") // ��������
//                            .enableSwagger() // ���� swagger ģʽ

                            .fileOverride() // �����������ļ�
                            .outputDir("D:\\Java\\gd-project\\sim-server\\sim-netty-server\\src\\main\\java"); // ָ�����Ŀ¼
                })
                // ������
                .packageConfig(builder -> {
                    builder.parent("com.sy") // ���ø�����
                            .moduleName("im"); // ���ø���ģ����
                })
                // ��������
                .strategyConfig(builder -> {
                    builder.addInclude("friend")
//                            .addTablePrefix("user")   // ȥ�����ǰ׺
                            .controllerBuilder()
                            .enableRestStyle()  //��������@RestController ������
                            .serviceBuilder()
                            .formatServiceFileName("%sService") // ȥ��I
                            .formatServiceImplFileName("%sServiceImp")
                            .entityBuilder().enableLombok()
                            .build();

                })
                .templateEngine(new FreemarkerTemplateEngine()) // ʹ��Freemarker����ģ�壬Ĭ�ϵ���Velocity����ģ��
                .execute();

    }
}
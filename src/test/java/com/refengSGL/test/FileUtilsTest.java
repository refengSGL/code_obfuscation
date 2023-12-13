package com.refengSGL.test;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

/**
 * @author: MightCell
 * @description: 学习FileUtils工具类
 * @date: Created in 11:41 2023-03-01
 */
public class FileUtilsTest {

    final String basePath = "/home/test/upload/";

    public void testFileUpload(MultipartFile file) {

        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));

        // 使用 UUID 重新生成文件名，防止文件名称重复，造成文件覆盖
        String fileName = UUID.randomUUID().toString() + suffix;

        // 创建一个目录对象
        File dir = new File(basePath);

        // 判断当前目录是否存在
        if (!dir.exists()) {
            // 目录不存在，创建目录结构
            dir.mkdir();
        }

        try {
            // 将临时文件转存到其他位置
            file.transferTo(new File(basePath + fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        ArrayList<String> list = new ArrayList<>();
        String url = "/home/project/upload/";
        String fileName = "970ade1c-7411-4ea8-a488-ba8b9215ced1.c";
        String shellPath = "/home/project/";
        System.out.println(url + fileName);
        list.add("1");
        list.add("2");
        list.add("30");
        list.add(0, url + fileName);
        list.add(0, "clang");
        list.add(0, "/" + shellPath);
        for (String item : list) {
            System.out.print(item);
        }
    }
}

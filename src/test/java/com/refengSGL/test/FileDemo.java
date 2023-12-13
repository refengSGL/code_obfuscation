package com.refengSGL.test;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

/**
 * @author: MightCell
 * @description:
 * @date: Created in 14:06 2023-03-03
 */
public class FileDemo {
    public static void main(String[] args) {
        ArrayList<String> list = new ArrayList<>();
        list.add("1");
        list.add("2");
        list.add("3");
        System.out.println(list.get(0));
        System.out.println(list.get(1));
        System.out.println(list.get(2));

    }

    /**
     * 获取当前目录下所有的文件夹和文件的名称
     */
    public static void getFileList() {
        File file = new File(" /home/protect/upload/");
        File[] files = file.listFiles();
        for (File item : files) {
            if (item.getName().equals("970ade1c-7411-4ea8-a488-ba8b9215ced1.c_bcf")) {
                System.out.println(item.length());
            }
        }
    }

    /**
     * 获取指定目录下指定文件类型的所有文件
     */
    public static void getSpecifiedFileList() {
        File file = new File("/home/project/upload");
        String[] list = file.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".c");
            }
        });
        for (String item : list) {
            System.out.println(item);
        }
    }

    /**
     * 获取指定目录下所有文件，返回文件对象数组
     */
    public static void getFileObjectList() {
        File file = new File("/home/protect/upload/");
        File[] files = file.listFiles();
        for (File item : files) {
            System.out.println(item);
        }
    }

    @Test
    public void testFileUploadResult() {
        String protectionType = "_bcf";

    }



}

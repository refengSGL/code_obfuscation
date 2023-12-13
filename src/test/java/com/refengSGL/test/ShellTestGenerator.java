package com.refengSGL.test;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * @author: MightCell
 * @description: 测试shell脚本调用
 * @date: Created in 13:35 2023-02-26
 */
public class ShellTestGenerator {

    public static void main(String[] args) {
        callShellByExec("docker info");
    }

    /**
     * 使用exec调用shell脚本
     *
     * @param shellString
     */
    private static void callShellByExec(String shellString) {
        BufferedReader reader = null;
        try {
            Process process = Runtime.getRuntime().exec(shellString);
            int exitValue = process.waitFor();
            if (exitValue != 0) {
                System.out.println("call shell failed. error code is : " + exitValue);
            }
            // 获取返回值
            reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = null;
            while ((line = reader.readLine()) != null) {
                System.out.println("[root@iZ0jldi7i29wyumf0objxhZ demo]# " + line);
            }
        } catch (Throwable e) {
            System.out.println("call shell failed. " + e);
        }

    }

    private void callScript() {

    }
}

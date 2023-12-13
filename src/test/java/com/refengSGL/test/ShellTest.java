package com.refengSGL.test;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * @author: MightCell
 * @description:
 * @date: Created in 15:15 2023-02-26
 */
public class ShellTest {

    public static void main(String[] args) {
        // 输出echo hello
        callShellByExec("echo hello");
    }

    /**
     * 使用exec调用shell命令
     *
     * @param stringShell
     */
    private static void callShellByExec(String stringShell) {
        BufferedReader reader = null;
        try {
            // 执行shell命令
            Process process = Runtime.getRuntime().exec(stringShell);

            // 获取命令执行结果：成功与否
            int exitValue = process.waitFor();

            if (exitValue != 0) {
                System.out.println("call shell failed. error code is : " + exitValue);
            }
            // 获取命令执行返回值
            reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = null;
            while ((line = reader.readLine()) != null) {
                System.out.println("[root@mightcell test]# " + line);
            }
        } catch (Throwable e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void testShellScript() {
        final String FILE_DIR = "/home/test";
        final String SHELL_FILE = "test_shell.sh";

        ArrayList<String> paramList = new ArrayList<>();
        paramList.add("bash");
        paramList.add("param1");
        paramList.add("param2");
        paramList.add("param3");

        callShellByExec("cd" + FILE_DIR);
        callShellByExec("chmod a+x" + SHELL_FILE);

        ProcessBuilder processBuilder = new ProcessBuilder(paramList);
        processBuilder.directory(new File(FILE_DIR));

        try {
            Process process = processBuilder.start();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }


}

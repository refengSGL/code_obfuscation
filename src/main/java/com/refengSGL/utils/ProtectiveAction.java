package com.refengSGL.utils;

import com.refengSGL.exception.CodeException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import static com.refengSGL.constant.FileConstant.*;

@Data
@Slf4j
public class ProtectiveAction {

    public final static String SHELL_FILE_DIR = "/home/project/";
    public final static String SHELL_FILE_NAME = "ollvm_obfuscate.sh";
    public final static String FILE_STORE = "/home/project/upload/";

    public final static String OLLVM_BCF = "_bcf";
    public final static String OLLVM_FLA = "_fla";
    public final static String OLLVM_SUB = "_sub";
    public final static int OLLVM_BCF_CODE = 1;
    public final static int OLLVM_FLA_CODE = 2;
    public final static int OLLVM_SUB_CODE = 3;

    public static String getProtectType(Integer protectCode) {
        switch (protectCode) {
            case OLLVM_BCF_CODE:
                return OLLVM_BCF;
            case OLLVM_FLA_CODE:
                return OLLVM_FLA;
            default:
                return OLLVM_SUB;
        }
    }

    public static void callShellByExec(String stringShell) {
        log.info("Start invoking the shell command");
        try {
            // 执行shell命令
            Process process = Runtime.getRuntime().exec(stringShell);
            // 获取命令执行结果：成功与否
            int exitValue = process.waitFor();
            if (exitValue != SUCCESS_EXIT) {
                log.error("Failed to invoke the shell command");
                throw new CodeException("调用shell命令失败");
            }
            log.info("The shell command was successfully invoked");
        } catch (Throwable e) {
            log.error("Failed to invoke the shell command: {}", e.getMessage());
            throw new CodeException("调用shell命令失败");
        }
    }

    public static void performShellScript(String fileName, ArrayList<String> paramList) {
        log.info("Start invoking the shell command");
        BufferedReader reader = null;
        initParamList(fileName, paramList);
        // 增加可操作性权限
        grantPermission();
        ProcessBuilder processBuilder = initProcessBuilder(paramList);
        try {
            Process process = processBuilder.start();
            Thread.sleep(500);
        } catch (IOException | InterruptedException e) {
            log.error("Failed to invoke the shell script: {}", ExceptionUtils.getMessage(e));
            throw new CodeException("执行脚本文件失败");
        }
    }

    private static ProcessBuilder initProcessBuilder(ArrayList<String> paramList) {
        ProcessBuilder processBuilder = new ProcessBuilder(paramList);
        processBuilder.directory(new File(SHELL_FILE_DIR));
        return processBuilder;
    }

    private static void grantPermission() {
        callShellByExec("chmod a+x " + SHELL_FILE_NAME);
    }


    public static void initParamList(String fileName, ArrayList<String> paramList) {
        log.info("Starts the initial parameter list");
        paramList.add(LIST_HEADER, FILE_STORE + fileName);
        paramList.add(LIST_HEADER, CLANG);
        paramList.add(LIST_HEADER, RUN_SHELL + SHELL_FILE_NAME);
    }

    public static String getFileMemory(String fileName) {
        log.info("Fetching the file memory");
        File file = new File(FILE_STORE);
        File[] files = file.listFiles();
        if (Objects.isNull(files)) {
            log.info("There are no files in the storage directory");
            throw new CodeException("存储目录中没有文件");
        }
        for (File item : files) {
            if (item.getName().equals(fileName)) {
                log.info("Get the file memory successfully");
                return String.valueOf(item.length());
            }
        }
        log.info("There are no matching file in the storage directory");
        throw new CodeException("存储目录中没有匹配的文件");
    }

    public static void getCurrentFileList() {
        log.info("开始获取当前目录列表");
        File file = new File(FILE_STORE);
        File[] files = file.listFiles();
        log.info("目录文件如下：");
        for (File item : files) {
            log.info(item.getName());
        }
    }

    public static File getResultFileObject(String originalFileName, String newFileName) {
        // 获取原始文件对象
        log.info("开始获取保护后的文件对象");
        File file = new File(FILE_STORE + originalFileName);
        if (file.renameTo(new File(FILE_STORE + newFileName))) {
            return file;
        }
        return null;
    }
}

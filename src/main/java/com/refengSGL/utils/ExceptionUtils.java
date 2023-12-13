package com.refengSGL.utils;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionUtils {
    public static String getMessage(Exception e) {
        StringWriter stringWriter = null;
        PrintWriter printWriter = null;
        try {
            stringWriter = new StringWriter();
            printWriter = new PrintWriter(stringWriter);
//            将出错的站信息，输出到printWriter中
            e.printStackTrace(printWriter);
            printWriter.flush();
            stringWriter.flush();
        } finally {
            if (stringWriter != null) {
                try {
                    stringWriter.close();
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }
            if (printWriter != null) {
                printWriter.close();
            }
        }
        return stringWriter.toString();
    }
}

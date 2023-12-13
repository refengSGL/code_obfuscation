package com.refengSGL.constant;

public interface FileConstant {
    String FILE_UPLOAD_PATH = "/home/project/upload/"; // 文件路径

    String WORK_DIRECTORY = "/home/project/"; //工作目录
    Integer FILE_EXIST = 1; // 判断是否存在文件
    Integer SUCCESS_EXIT = 0; // 成功退出
    Integer LIST_HEADER = 0;
    String CLANG = "clang";

    // case 1
    String OURS_OBFUSCATE = "ours_stop.sh";
    // case 2
    String TIGRESS_OBFUSCATE = "tigress_obfuscate.sh";

    String SCAN_BUILD = "scan-build";
    String OUTPUT_CONTAIN_WARNING = "warning:";

    String RUN_SHELL = "./";
    String GET_BY_ID = "id";
    String GET_BY_NAME = "name";
    String C_MIME = "text/x-c";

    String C_SUFFIX = ".c";
    String EMPTY_STRING = "";

    String TIGRESS_ELF = "ELF 64-bit LSB executable";

    // TIGRESS_OBFUSCATE 的 三种保护方式
    String TIGRESS_FLATTEN = "_flatten";
    String TIGRESS_ENCODE_ARITHMETIC = "_encodeArithmetic";
    String TIGRESS_ADD_OPAQUE = "_addOpaque";

    String TIGRESS_AFTER_FILE_TYPE = "ASCII text";

    // 先测试一下 ours的混淆方式
    String OBF = "_obf";
    String OURS_STOP_I ="_I";
    String OURS_STOP_II ="_II";
    String OURS_STOP_BOTH ="_both";

    String OURS_AFTER_FILE_TYPE = "ELF 64-bit LSB";

    int TIGRESS_TYPE = 1;
    int OURS_STOP = 0;

    int END_OF_FILE = -1;

}

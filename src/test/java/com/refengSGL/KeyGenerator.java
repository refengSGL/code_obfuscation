package com.refengSGL;

import cn.dev33.satoken.secure.SaSecureUtil;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

public class KeyGenerator {

    @Test
    public void keyGenerator() throws Exception {
        HashMap<String, String> map = SaSecureUtil.rsaGenerateKeyPair();
        System.out.println(map.get("public"));
        System.out.println(map.get("private"));
    }
}
